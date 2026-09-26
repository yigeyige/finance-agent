param(
    [string]$MavenCommand = "mvn",
    [string]$NpmCommand = "npm",
    [switch]$BuildDocker,
    [string]$DockerCommand = "docker",
    [string]$BackendImageRepository = "finance-agent-backend",
    [string]$FrontendImageRepository = "finance-agent-frontend"
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$pomPath = Join-Path $repoRoot "backend\pom.xml"
$pomText = Get-Content -LiteralPath $pomPath -Raw
$versionMatch = [regex]::Match($pomText, '<artifactId>finance-agent</artifactId>\s*<version>(?<version>[^<]+)</version>')
if (-not $versionMatch.Success) {
    throw "无法从 backend/pom.xml 读取 finance-agent project.version"
}
$version = $versionMatch.Groups["version"].Value

function Get-DirectorySha256 {
    param([Parameter(Mandatory = $true)][string]$Directory)

    $resolvedDirectory = (Resolve-Path -LiteralPath $Directory).Path.TrimEnd('\', '/')
    $manifest = New-Object System.Text.StringBuilder
    $files = Get-ChildItem -LiteralPath $resolvedDirectory -File -Recurse | Sort-Object FullName
    foreach ($file in $files) {
        $relativePath = $file.FullName.Substring($resolvedDirectory.Length).TrimStart('\', '/')
        $fileHash = (Get-FileHash -LiteralPath $file.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
        [void]$manifest.AppendLine("$relativePath`t$fileHash")
    }
    $sha256 = [System.Security.Cryptography.SHA256]::Create()
    try {
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($manifest.ToString())
        return ([System.BitConverter]::ToString($sha256.ComputeHash($bytes))).Replace('-', '').ToLowerInvariant()
    }
    finally {
        $sha256.Dispose()
    }
}

Push-Location (Join-Path $repoRoot "frontend")
try {
    & $NpmCommand ci
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    & $NpmCommand run build
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
finally {
    Pop-Location
}

$distPath = Join-Path $repoRoot "frontend\dist"
if (-not (Test-Path -LiteralPath (Join-Path $distPath "index.html"))) {
    throw "未找到前端独立交付物: $distPath\index.html"
}
$frontendHash = Get-DirectorySha256 -Directory $distPath

$mavenArgs = @("-f", $pomPath, "-q", "-DskipTests", "clean", "package")
& $MavenCommand @mavenArgs
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$jarPath = Join-Path $repoRoot "backend\target\finance-agent-$version.jar"
if (-not (Test-Path -LiteralPath $jarPath)) {
    throw "未找到预期 JAR: $jarPath"
}
$jarHash = (Get-FileHash -LiteralPath $jarPath -Algorithm SHA256).Hash.ToLowerInvariant()
Write-Output "version=$version"
Write-Output "frontendDist=$distPath"
Write-Output "frontendDistSha256=$frontendHash"
Write-Output "jar=$jarPath"
Write-Output "jarSha256=$jarHash"

if ($BuildDocker) {
    $backendImageTag = "$BackendImageRepository`:$version"
    $frontendImageTag = "$FrontendImageRepository`:$version"
    & $DockerCommand build (Join-Path $repoRoot ".") -f (Join-Path $repoRoot "deploy\backend.Dockerfile") "--build-arg" "APP_VERSION=$version" "-t" $backendImageTag
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    & $DockerCommand build (Join-Path $repoRoot ".") -f (Join-Path $repoRoot "deploy\frontend.Dockerfile") "--build-arg" "APP_VERSION=$version" "-t" $frontendImageTag
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    Write-Output "backendImage=$backendImageTag"
    Write-Output "frontendImage=$frontendImageTag"
}

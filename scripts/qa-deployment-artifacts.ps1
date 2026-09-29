[CmdletBinding()]
param(
    [switch]$BuildArtifacts
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

$pom = Get-Content -LiteralPath (Join-Path $repoRoot 'backend/pom.xml') -Raw
$builder = Get-Content -LiteralPath (Join-Path $repoRoot 'scripts/build-artifacts.ps1') -Raw
$backendDocker = Get-Content -LiteralPath (Join-Path $repoRoot 'deploy/backend.Dockerfile') -Raw
$frontendDocker = Get-Content -LiteralPath (Join-Path $repoRoot 'deploy/frontend.Dockerfile') -Raw
$properties = Get-Content -LiteralPath (Join-Path $repoRoot 'backend/src/main/resources/application.properties') -Raw
$prod = Get-Content -LiteralPath (Join-Path $repoRoot 'backend/src/main/resources/application-prod.yml') -Raw
$nginx = Get-Content -LiteralPath (Join-Path $repoRoot 'deploy/nginx.conf') -Raw

Assert-Condition ($pom -match '<artifactId>finance-agent</artifactId>\s*<version>(?<version>[^<]+)</version>') 'backend version is missing'
$version = $Matches.version
Assert-Condition ($builder.Contains('backend\target\finance-agent-$version.jar') -and $builder.Contains('$NpmCommand run build')) 'artifact builder contract drifted'
Assert-Condition ($backendDocker.Contains('/opt/finance-agent/app.jar') -and $backendDocker.Contains('EXPOSE 8080') -and $backendDocker.Contains('/actuator/health')) 'backend image contract drifted'
Assert-Condition ($frontendDocker.Contains('/usr/share/nginx/html/') -and $frontendDocker.Contains('EXPOSE 8080') -and $frontendDocker.Contains('wget')) 'frontend image contract drifted'
Assert-Condition ($backendDocker.Contains('USER 10001:10001') -and $frontendDocker.Contains('USER nginx')) 'images must run non-root'
Assert-Condition ($backendDocker -notmatch 'docker\.sock' -and $frontendDocker -notmatch 'docker\.sock') 'Docker socket is forbidden'
Assert-Condition ($properties.Contains('management.endpoint.health.probes.enabled=true') -and $properties.Contains('management.endpoint.health.group.readiness')) 'health/readiness probes are not configured'
foreach ($name in @('FINANCE_AGENT_DB_URL', 'FINANCE_AGENT_DB_USERNAME', 'FINANCE_AGENT_DB_PASSWORD')) {
    Assert-Condition ($prod.Contains($name)) "external database variable missing: $name"
}
Assert-Condition ($nginx.Contains('listen 8080') -and $nginx.Contains('try_files')) 'frontend static server contract drifted'
$composeFiles = @(Get-ChildItem -LiteralPath (Join-Path $repoRoot 'deploy') -File -Recurse | Where-Object { $_.Name -match 'compose|docker-compose' })
Assert-Condition ($composeFiles.Count -eq 0) 'Compose files are not allowed'

if ($BuildArtifacts) {
    & (Join-Path $repoRoot 'scripts/build-artifacts.ps1')
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Write-Output 'status=OK'
Write-Output 'mode=STATIC'
Write-Output "version=$version"
Write-Output 'backend=JAR_OR_INDEPENDENT_IMAGE'
Write-Output 'frontend=INDEPENDENT_IMAGE'
Write-Output 'externalDatabase=FINANCE_AGENT_DB_URL,FINANCE_AGENT_DB_USERNAME,FINANCE_AGENT_DB_PASSWORD'
Write-Output 'health=/actuator/health,/actuator/health/readiness,/actuator/health/liveness'
Write-Output 'compose=0'
Write-Output 'dockerSocket=0'

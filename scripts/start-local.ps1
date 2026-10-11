[CmdletBinding()]
param(
    [ValidateRange(1, 65535)]
    [int]$BackendPort = 8080,
    [ValidateRange(1, 65535)]
    [int]$FrontendPort = 5173,
    [string]$MavenCommand = "mvn.cmd",
    [string]$NpmCommand = "npm.cmd"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$dotenvPath = Join-Path $repoRoot ".env"
$backendDirectory = Join-Path $repoRoot "backend"
$frontendDirectory = Join-Path $repoRoot "frontend"
$migrationScript = Join-Path $PSScriptRoot "flyway-migrate.ps1"
$logDirectory = Join-Path $repoRoot ".runtime\local-logs"

function Test-ListeningPort {
    param([Parameter(Mandatory = $true)][int]$Port)

    return $null -ne (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -First 1)
}

function Import-DotEnv {
    param([Parameter(Mandatory = $true)][string]$Path)

    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "未找到本地启动配置文件: $Path"
    }

    $lineNumber = 0
    foreach ($line in Get-Content -LiteralPath $Path) {
        $lineNumber++
        $trimmedLine = $line.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmedLine) -or $trimmedLine.StartsWith("#")) {
            continue
        }

        $match = [regex]::Match($line, '^\s*(?<key>[A-Za-z_][A-Za-z0-9_]*)=(?<value>.*)$')
        if (-not $match.Success) {
            throw "本地启动配置第 $lineNumber 行格式无效，请使用 KEY=VALUE。"
        }

        $value = $match.Groups["value"].Value.Trim()
        $doubleQuote = [string][char]34
        $singleQuote = [string][char]39
        if ($value.Length -ge 2 -and (($value.StartsWith($doubleQuote) -and $value.EndsWith($doubleQuote)) -or
                    ($value.StartsWith($singleQuote) -and $value.EndsWith($singleQuote)))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        [Environment]::SetEnvironmentVariable($match.Groups["key"].Value, $value, "Process")
    }
}

function Resolve-Executable {
    param([Parameter(Mandatory = $true)][string]$CommandName)

    $command = Get-Command -Name $CommandName -CommandType Application -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($null -eq $command) {
        throw "未找到启动命令: $CommandName"
    }
    return $command.Source
}

function Start-LocalProcess {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$WorkingDirectory,
        [Parameter(Mandatory = $true)][string]$Executable,
        [Parameter(Mandatory = $true)][string[]]$Arguments,
        [Parameter(Mandatory = $true)][string]$LogPrefix
    )

    $escapedExecutable = $Executable.Replace("'", "''")
    $escapedArguments = $Arguments | ForEach-Object { "'" + $_.Replace("'", "''") + "'" }
    $commandText = "& '$escapedExecutable' @(" + ($escapedArguments -join ", ") + ")"
    $hostExecutable = (Get-Process -Id $PID).Path
    $standardOutputPath = Join-Path $logDirectory "$LogPrefix.out.log"
    $standardErrorPath = Join-Path $logDirectory "$LogPrefix.err.log"

    $process = Start-Process -FilePath $hostExecutable `
        -ArgumentList @("-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-Command", $commandText) `
        -WorkingDirectory $WorkingDirectory `
        -WindowStyle Hidden `
        -RedirectStandardOutput $standardOutputPath `
        -RedirectStandardError $standardErrorPath `
        -PassThru
    Write-Host "$Name 已在后台启动（PID $($process.Id)）。"
    return @($standardOutputPath, $standardErrorPath)
}

Import-DotEnv -Path $dotenvPath
New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$createdLogPaths = @()

if (Test-ListeningPort -Port $BackendPort) {
    Write-Output "后端已监听端口 $BackendPort，保持复用。"
}
else {
    if (-not (Test-Path -LiteralPath $migrationScript -PathType Leaf)) {
        throw "未找到数据库迁移脚本: $migrationScript"
    }
    Write-Output "正在执行本地数据库迁移。"
    & $migrationScript -MavenCommand $MavenCommand *> $null
    $migrationExitCode = $LASTEXITCODE
    if ($migrationExitCode -ne 0) {
        throw "本地数据库迁移失败，未启动后端和前端。"
    }
    Write-Output "本地数据库迁移完成。"
    $mavenExecutable = Resolve-Executable -CommandName $MavenCommand
    $createdLogPaths += Start-LocalProcess -Name "后端" -WorkingDirectory $backendDirectory `
        -Executable $mavenExecutable -Arguments @("spring-boot:run") -LogPrefix "backend-$timestamp"
}

if (Test-ListeningPort -Port $FrontendPort) {
    Write-Output "前端已监听端口 $FrontendPort，保持复用。"
}
else {
    $vitePath = Join-Path $frontendDirectory "node_modules\.bin\vite.cmd"
    if (-not (Test-Path -LiteralPath $vitePath -PathType Leaf)) {
        throw "未找到前端依赖，请先在 frontend 目录执行 npm install。"
    }
    $npmExecutable = Resolve-Executable -CommandName $NpmCommand
    $createdLogPaths += Start-LocalProcess -Name "前端" -WorkingDirectory $frontendDirectory `
        -Executable $npmExecutable -Arguments @("run", "dev", "--", "--host", "127.0.0.1") `
        -LogPrefix "frontend-$timestamp"
}

Write-Output "前端地址: http://127.0.0.1:$FrontendPort/"
Write-Output "后端地址: http://127.0.0.1:$BackendPort/"
if ($createdLogPaths.Count -gt 0) {
    Write-Output "本次启动日志: $logDirectory"
}
else {
    Write-Output "本次未创建后台进程；现有服务保持运行。"
}

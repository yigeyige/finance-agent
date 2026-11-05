[CmdletBinding()]
param(
    [string]$Executable,
    [string[]]$ChildArguments = @(),
    [string]$LogDirectory,
    [string]$LogPrefix,
    [long]$MaxBytes = 10485760
)

$ErrorActionPreference = 'Stop'
$script:currentDay = ''
$script:currentPath = ''
$script:currentBytes = 0L
$script:segment = 0

function Write-RotatingLine {
    param(
        [Parameter(Mandatory = $true)][string]$Line,
        [Parameter(Mandatory = $true)][string]$LogDirectory,
        [Parameter(Mandatory = $true)][string]$LogPrefix,
        [Parameter(Mandatory = $true)][long]$MaxBytes
    )
    if ($LogPrefix -notmatch '^(backend|frontend)-\d{8}-\d{6}$' -or $MaxBytes -le 0) {
        throw '本地日志参数无效'
    }
    $day = Get-Date -Format 'yyyyMMdd'
    if ($script:currentDay -ne $day) {
        $script:currentDay = $day
        $script:segment = 0
        $script:currentBytes = 0
        $script:currentPath = ''
    }
    $content = $Line + [Environment]::NewLine
    $encoding = [System.Text.UTF8Encoding]::new($false)
    $length = $encoding.GetByteCount($content)
    if ($script:currentPath -and $script:currentBytes + $length -gt $MaxBytes) {
        $script:segment++
        $script:currentPath = ''
        $script:currentBytes = 0
    }
    if (-not $script:currentPath) {
        [System.IO.Directory]::CreateDirectory($LogDirectory) | Out-Null
        $name = '{0}.{1}.{2:D3}.log' -f $LogPrefix, $day, $script:segment
        $script:currentPath = Join-Path $LogDirectory $name
    }
    [System.IO.File]::AppendAllText($script:currentPath, $content, $encoding)
    $script:currentBytes += $length
}

if ($MyInvocation.InvocationName -ne '.') {
    if (-not $Executable -or -not $LogDirectory -or -not $LogPrefix) {
        throw '启动程序、日志目录和日志前缀不能为空'
    }
    & $Executable @ChildArguments 2>&1 | ForEach-Object {
        Write-RotatingLine -Line ([string]$_) -LogDirectory $LogDirectory `
            -LogPrefix $LogPrefix -MaxBytes $MaxBytes
    }
    exit $LASTEXITCODE
}

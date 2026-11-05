$ErrorActionPreference = 'Stop'
$root = Join-Path ([System.IO.Path]::GetTempPath()) ("finance-agent-log-test-" + [guid]::NewGuid().ToString('N'))
$tempRoot = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath())
$resolvedRoot = [System.IO.Path]::GetFullPath($root)
if (-not $resolvedRoot.StartsWith($tempRoot, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw '测试目录不在临时目录内'
}
try {
    . (Join-Path $PSScriptRoot 'rolling-local-process.ps1')
    Write-RotatingLine -Line '1234567890' -LogDirectory $root -LogPrefix 'backend-20261104-000000' -MaxBytes 12
    Write-RotatingLine -Line 'abcdefghij' -LogDirectory $root -LogPrefix 'backend-20261104-000000' -MaxBytes 12
    $files = @(Get-ChildItem -LiteralPath $root -Filter '*.log' -File)
    if ($files.Count -ne 2) { throw "预期生成两个轮转文件，实际为 $($files.Count)" }
    $shell = (Get-Process -Id $PID).Path
    & (Join-Path $PSScriptRoot 'rolling-local-process.ps1') -Executable $shell `
        -ChildArguments @('-NoProfile', '-Command', 'Write-Output child-ok') `
        -LogDirectory $root -LogPrefix 'frontend-20261104-000000' -MaxBytes 1024
    $childOutput = @(Get-ChildItem -LiteralPath $root -Filter 'frontend-*.log' -File |
        ForEach-Object { Get-Content -LiteralPath $_.FullName }) -join "`n"
    if ($childOutput -notmatch 'child-ok') { throw '子进程输出没有写入轮转日志' }
    $helper = (Join-Path $PSScriptRoot 'rolling-local-process.ps1').Replace("'", "''")
    $escapedRoot = $root.Replace("'", "''")
    $escapedShell = $shell.Replace("'", "''")
    $commandText = "& '$helper' -Executable '$escapedShell' -ChildArguments @('-NoProfile', '-Command', 'Write-Output quoted-ok') -LogDirectory '$escapedRoot' -LogPrefix 'backend-20261104-000001'"
    & $shell -NoProfile -NonInteractive -ExecutionPolicy Bypass -Command $commandText
    $quotedOutput = @(Get-ChildItem -LiteralPath $root -Filter 'backend-20261104-000001.*.log' -File |
        ForEach-Object { Get-Content -LiteralPath $_.FullName }) -join "`n"
    if ($quotedOutput -notmatch 'quoted-ok') { throw '启动脚本的命令字符串没有正确转发输出' }
    Write-Output '本地日志按大小轮转：通过'
} finally {
    if (Test-Path -LiteralPath $resolvedRoot) { Remove-Item -LiteralPath $resolvedRoot -Recurse -Force }
}

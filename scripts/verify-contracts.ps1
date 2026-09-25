param(
    [string]$Python = "python"
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
& $Python (Join-Path $PSScriptRoot "verify-contracts.py") --root $repoRoot
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

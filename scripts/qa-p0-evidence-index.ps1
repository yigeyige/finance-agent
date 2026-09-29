[CmdletBinding()]
param(
    [string]$FixturePath = (Join-Path $PSScriptRoot 'fixtures/p0-evidence-index-v1.json'),
    [string]$EvidenceRoot = ''
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

$fixture = Get-Content -LiteralPath $FixturePath -Raw | ConvertFrom-Json
Assert-Condition ($fixture.fixtureVersion -eq 'p0-evidence-index-v1') 'fixture version is invalid'
Assert-Condition ($fixture.tasks.Count -eq 10) 'P0 evidence index must contain ten tasks'
$allowedStatuses = @('VERIFIED_STATIC', 'PENDING_USER')
foreach ($task in $fixture.tasks) {
    Assert-Condition ($task.status -in $allowedStatuses) "unsupported task status: $($task.id)"
    foreach ($artifact in $task.artifacts) {
        Assert-Condition (Test-Path -LiteralPath (Join-Path $repoRoot $artifact)) "missing evidence artifact: $artifact"
    }
}
Assert-Condition ($fixture.zeroTolerance.Count -eq 3) 'three zero-tolerance metrics are required'
foreach ($metric in $fixture.zeroTolerance) {
    Assert-Condition ([int]$metric.value -eq 0) "zero-tolerance metric is non-zero: $($metric.id)"
    Assert-Condition ($metric.status -eq 'PENDING_USER') "zero-tolerance metric must remain pending: $($metric.id)"
}
Assert-Condition ($fixture.signOff.status -eq 'PENDING_USER' -and $fixture.signOff.conclusion -eq 'CONDITIONAL') 'unsigned P0 conclusion must remain conditional'
Assert-Condition ([string]::IsNullOrWhiteSpace($fixture.signOff.signedBy)) 'fixture must not contain a pre-filled signer'
if ($EvidenceRoot) { Assert-Condition (Test-Path -LiteralPath $EvidenceRoot) "evidence root does not exist: $EvidenceRoot" }

$pending = @($fixture.tasks | Where-Object { $_.status -eq 'PENDING_USER' }).Count
Write-Output 'status=OK'
Write-Output 'mode=STATIC'
Write-Output "taskCount=$($fixture.tasks.Count)"
Write-Output "zeroToleranceCount=$($fixture.zeroTolerance.Count)"
Write-Output "pendingUserCount=$pending"
Write-Output 'networkCalls=0'
Write-Output 'databaseCalls=0'
Write-Output 'conclusion=CONDITIONAL'

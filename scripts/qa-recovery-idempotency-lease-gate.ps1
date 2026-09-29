[CmdletBinding()]
param(
    [switch]$Execute,
    [string]$FixturePath = (Join-Path $PSScriptRoot 'fixtures/recovery-idempotency-lease-v1.json'),
    [string]$ResultDirectory,
    [string]$OutputDirectory = (Join-Path $env:TEMP 'finance-agent-recovery-gate')
)

$ErrorActionPreference = 'Stop'

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

Assert-Condition (Test-Path -LiteralPath $FixturePath -PathType Leaf) "fixture not found: $FixturePath"
$fixture = Get-Content -LiteralPath $FixturePath -Raw | ConvertFrom-Json
Assert-Condition ($fixture.fixtureVersion -eq 'recovery-idempotency-lease-v1') 'unexpected fixture version'
Assert-Condition ($fixture.faultPoints.Count -eq 6) 'six interruption points are required'
Assert-Condition ($fixture.negativeCases.Count -eq 6) 'six recovery negative cases are required'
Assert-Condition (($fixture.invariants.doubleExecution -eq 0) -and ($fixture.invariants.duplicateRevision -eq 0) -and ($fixture.invariants.approvalBypass -eq 0) -and ($fixture.invariants.unknownAutomaticReplay -eq 0)) 'recovery invariants must be zero'
$evidenceRequirements = (($fixture.evidenceRequirements | Sort-Object) -join ',')
Assert-Condition ($evidenceRequirements -eq 'database-counts,recovery-decision,worker-log-summary') 'evidence requirements drifted'
Assert-Condition (-not ($fixture.PSObject.Properties.Name -contains 'payload')) 'fixture must not contain payload'

$requiredPoints = @('AFTER_APPROVAL', 'AFTER_INVOCATION_INTENT', 'AFTER_RESULT_SUBMISSION', 'AFTER_COMPUTATION', 'AFTER_DRAFT_ASSEMBLY', 'DURING_PUBLICATION_TRANSACTION')
$actualPoints = (($fixture.faultPoints.id | Sort-Object) -join ',')
$expectedPoints = (($requiredPoints | Sort-Object) -join ',')
Assert-Condition ($actualPoints -eq $expectedPoints) 'fault point coverage is incomplete'
foreach ($point in $fixture.faultPoints) {
    Assert-Condition ($point.doubleExecution -eq 0 -and $point.duplicateRevision -eq 0 -and $point.approvalBypass -eq 0) "fault point invariant drifted: $($point.id)"
}

if (-not $Execute) {
    Write-Output 'status=OK'
    Write-Output 'mode=STATIC'
    Write-Output "fixtureVersion=$($fixture.fixtureVersion)"
    Write-Output "faultPointCount=$($fixture.faultPoints.Count)"
    Write-Output "negativeCaseCount=$($fixture.negativeCases.Count)"
    Write-Output 'networkCalls=0'
    Write-Output 'databaseCalls=0'
    exit 0
}

Assert-Condition (-not [string]::IsNullOrWhiteSpace($ResultDirectory)) 'ResultDirectory is required with -Execute'
Assert-Condition (Test-Path -LiteralPath $ResultDirectory -PathType Container) "result directory not found: $ResultDirectory"
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
$results = @{}
Get-ChildItem -LiteralPath $ResultDirectory -File -Filter '*.json' -Recurse | ForEach-Object {
    $entry = Get-Content -LiteralPath $_.FullName -Raw | ConvertFrom-Json
    if ($entry.caseId) { $results[$entry.caseId] = $entry }
}
$expectedCases = @($fixture.faultPoints.id + $fixture.negativeCases.id)
$missing = @($expectedCases | Where-Object { -not $results.ContainsKey($_) })
$unsafe = @($results.Values | Where-Object { $_.doubleExecution -ne 0 -or $_.duplicateRevision -ne 0 -or $_.approvalBypass -ne 0 -or $_.automaticReplay -ne 0 })
$report = [pscustomobject]@{
    fixtureVersion = $fixture.fixtureVersion
    expectedCaseCount = $expectedCases.Count
    resultCount = $results.Count
    missingCases = $missing
    unsafeCases = @($unsafe | ForEach-Object caseId)
    status = if ($missing.Count -eq 0 -and $unsafe.Count -eq 0) { 'OK' } else { 'FAILED' }
}
$reportPath = Join-Path $OutputDirectory 'recovery-idempotency-lease-report.json'
$report | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $reportPath -Encoding utf8
Write-Output "status=$($report.status)"
Write-Output "report=$reportPath"
Write-Output "missingCases=$($missing.Count)"
Write-Output "unsafeCases=$($unsafe.Count)"
if ($report.status -ne 'OK') { exit 2 }

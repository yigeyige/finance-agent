[CmdletBinding()]
param(
    [switch]$Execute,
    [string]$FixturePath = (Join-Path $PSScriptRoot 'fixtures/coverage-wording-gate-v1.json'),
    [string]$ResultDirectory,
    [string]$OutputDirectory = (Join-Path $env:TEMP 'finance-agent-coverage-wording')
)

$ErrorActionPreference = 'Stop'

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

Assert-Condition (Test-Path -LiteralPath $FixturePath -PathType Leaf) "fixture not found: $FixturePath"
$fixture = Get-Content -LiteralPath $FixturePath -Raw | ConvertFrom-Json
Assert-Condition ($fixture.fixtureVersion -eq 'coverage-wording-gate-v1') 'unexpected fixture version'
Assert-Condition ($fixture.scenarios.Count -eq 10) 'coverage wording gate must contain ten scenarios'
Assert-Condition (($fixture.scenarios.id | Sort-Object -Unique).Count -eq 10) 'scenario ids must be unique'
$reviewEvidence = (($fixture.reviewEvidence | Sort-Object) -join ',')
Assert-Condition ($reviewEvidence -eq 'packet-draft-or-rejection,reviewer-note,scenario-result') 'review evidence contract drifted'
Assert-Condition (-not ($fixture.PSObject.Properties.Name -contains 'realPayload')) 'fixture must not contain real payload'

$requiredIds = @('COMPLETE', 'PARTIAL_PAGINATION_FAILURE', 'UNKNOWN_TOTAL_CHANGED', 'PAGINATION_CHANGED',
    'CROSS_SOURCE_CONFLICT', 'FUTURE_TO_CURRENT', 'RAG_PLACEHOLDER', 'MEMORY_PLACEHOLDER', 'MODEL_PLACEHOLDER',
    'BUSINESS_IMPACT_UNCONFIRMED')
$actualIds = (($fixture.scenarios.id | Sort-Object) -join ',')
$expectedIds = (($requiredIds | Sort-Object) -join ',')
Assert-Condition ($actualIds -eq $expectedIds) 'coverage wording scenario coverage is incomplete'
foreach ($scenario in $fixture.scenarios) {
    Assert-Condition ($scenario.coverage -in @('COMPLETE', 'PARTIAL', 'UNKNOWN')) "invalid coverage: $($scenario.id)"
    Assert-Condition ($scenario.expected -in @('ALLOW', 'ALLOW_WITH_LIMITATIONS', 'ALLOW_WITH_UNDETERMINED', 'REJECT_FULL_SCOPE_CLAIM', 'ALLOW_CLASSIFIED_CURRENT', 'REJECT_CURRENT_FACT', 'REQUIRE_PENDING_ITEMS')) "invalid expected result: $($scenario.id)"
}

if (-not $Execute) {
    Write-Output 'status=OK'
    Write-Output 'mode=STATIC'
    Write-Output "fixtureVersion=$($fixture.fixtureVersion)"
    Write-Output "scenarioCount=$($fixture.scenarios.Count)"
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
    if ($entry.scenarioId) { $results[$entry.scenarioId] = $entry }
}
$missing = @($fixture.scenarios | Where-Object { -not $results.ContainsKey($_.id) } | ForEach-Object id)
$report = [pscustomobject]@{
    fixtureVersion = $fixture.fixtureVersion
    scenarioCount = $fixture.scenarios.Count
    resultCount = $results.Count
    missingScenarios = $missing
    status = if ($missing.Count -eq 0) { 'OK' } else { 'INCOMPLETE' }
}
$reportPath = Join-Path $OutputDirectory 'coverage-wording-report.json'
$report | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $reportPath -Encoding utf8
Write-Output "status=$($report.status)"
Write-Output "report=$reportPath"
Write-Output "missingScenarios=$($missing.Count)"
if ($report.status -ne 'OK') { exit 2 }

[CmdletBinding()]
param(
    [switch]$Execute,
    [string]$FixturePath = (Join-Path $PSScriptRoot 'fixtures/approval-permission-publication-negative-v1.json'),
    [string]$EvidenceRoot,
    [string]$OutputDirectory = (Join-Path $env:TEMP 'finance-agent-approval-negative')
)

$ErrorActionPreference = 'Stop'

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

Assert-Condition (Test-Path -LiteralPath $FixturePath -PathType Leaf) "fixture not found: $FixturePath"
$fixture = Get-Content -LiteralPath $FixturePath -Raw | ConvertFrom-Json
Assert-Condition ($fixture.fixtureVersion -eq 'approval-permission-publication-negative-v1') 'unexpected fixture version'
Assert-Condition ($fixture.cases.Count -eq 10) 'negative gate must contain ten cases'
Assert-Condition (($fixture.cases.id | Sort-Object -Unique).Count -eq 10) 'case ids must be unique'
$evidenceRequirements = (($fixture.evidenceRequirements | Sort-Object) -join ',')
Assert-Condition ($evidenceRequirements -eq 'audit,response,status') 'evidence requirements drifted'
Assert-Condition (-not ($fixture.PSObject.Properties.Name -contains 'credentials')) 'fixture must not contain credentials'

$requiredIds = @('SCOPE_NOT_CONFIRMED', 'APPROVAL_EXPIRED', 'FIELD_POLICY_OR_TEMPLATE_DRIFT', 'CROSS_TASK_PERMIT',
    'PROFESSIONAL_CONFIRMATION_MISSING', 'STALE_REVIEW', 'CONCURRENT_PUBLICATION', 'VIEWER_PUBLISH_FORBIDDEN',
    'VIEWER_REVIEW_FORBIDDEN', 'VIEWER_HANDOFF_FORBIDDEN')
$actualCaseIds = (($fixture.cases.id | Sort-Object) -join ',')
$expectedCaseIds = (($requiredIds | Sort-Object) -join ',')
Assert-Condition ($actualCaseIds -eq $expectedCaseIds) 'negative case coverage is incomplete'
foreach ($case in $fixture.cases) {
    Assert-Condition ($case.request.method -in @('POST', 'DELETE')) "unsupported request method: $($case.id)"
    Assert-Condition (-not [string]::IsNullOrWhiteSpace($case.request.path)) "request path missing: $($case.id)"
    Assert-Condition ($case.expectedHttpStatus -in @(201, 403, 409)) "unexpected expected status: $($case.id)"
    Assert-Condition ($case.expectedDecision -in @('REJECTED', 'FORBIDDEN', 'STALE_DRAFT', 'IDEMPOTENT_REPLAY')) "unexpected expected decision: $($case.id)"
    Assert-Condition (-not [string]::IsNullOrWhiteSpace($case.auditEvent)) "audit event missing: $($case.id)"
    Assert-Condition ($case.request.idempotencyKey.Length -ge 16) "idempotency key too short: $($case.id)"
}

if (-not $Execute) {
    Write-Output 'status=OK'
    Write-Output 'mode=STATIC'
    Write-Output "fixtureVersion=$($fixture.fixtureVersion)"
    Write-Output "caseCount=$($fixture.cases.Count)"
    Write-Output 'networkCalls=0'
    Write-Output 'databaseCalls=0'
    exit 0
}

Assert-Condition (-not [string]::IsNullOrWhiteSpace($EvidenceRoot)) 'EvidenceRoot is required with -Execute'
Assert-Condition (Test-Path -LiteralPath $EvidenceRoot -PathType Container) "evidence root not found: $EvidenceRoot"
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
$evidence = @{}
Get-ChildItem -LiteralPath $EvidenceRoot -File -Filter '*.json' -Recurse | ForEach-Object {
    $entry = Get-Content -LiteralPath $_.FullName -Raw | ConvertFrom-Json
    if ($entry.caseId) { $evidence[$entry.caseId] = $entry }
}
$missing = @($fixture.cases | Where-Object { -not $evidence.ContainsKey($_.id) } | ForEach-Object id)
$mismatched = @($fixture.cases | Where-Object {
    $actual = $evidence[$_.id]
    $actual -and ($actual.status -ne $_.expectedHttpStatus -or $actual.decision -ne $_.expectedDecision -or $actual.auditEvent -ne $_.auditEvent)
} | ForEach-Object id)
$report = [pscustomobject]@{
    fixtureVersion = $fixture.fixtureVersion
    caseCount = $fixture.cases.Count
    evidenceCount = $evidence.Count
    missingCases = $missing
    mismatchedCases = $mismatched
    status = if ($missing.Count -eq 0 -and $mismatched.Count -eq 0) { 'OK' } else { 'FAILED' }
}
$reportPath = Join-Path $OutputDirectory 'approval-permission-negative-report.json'
$report | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $reportPath -Encoding utf8
Write-Output "status=$($report.status)"
Write-Output "report=$reportPath"
Write-Output "missingCases=$($missing.Count)"
Write-Output "mismatchedCases=$($mismatched.Count)"
if ($report.status -ne 'OK') { exit 2 }

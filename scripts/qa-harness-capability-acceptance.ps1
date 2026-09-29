[CmdletBinding()]
param(
    [switch]$Execute,
    [string]$FixturePath = (Join-Path $PSScriptRoot 'fixtures/harness-capability-acceptance-v1.json'),
    [string]$ResultDirectory,
    [string]$OutputDirectory = (Join-Path $env:TEMP 'finance-agent-harness-acceptance')
)

$ErrorActionPreference = 'Stop'

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

Assert-Condition (Test-Path -LiteralPath $FixturePath -PathType Leaf) "fixture not found: $FixturePath"
$fixture = Get-Content -LiteralPath $FixturePath -Raw | ConvertFrom-Json
Assert-Condition ($fixture.fixtureVersion -eq 'harness-capability-acceptance-v1') 'unexpected fixture version'
Assert-Condition ($fixture.capabilities.Count -eq 6) 'six Harness capabilities are required'
Assert-Condition (($fixture.capabilities.id | Sort-Object -Unique).Count -eq 6) 'capability ids must be unique'
$manualEvidence = (($fixture.manualEvidence | Sort-Object) -join ',')
Assert-Condition ($manualEvidence -eq 'humanConclusion,input,limitations,result,version') 'manual evidence contract drifted'
$sandboxProfiles = (($fixture.productionAllowlist.sandboxProfiles | Sort-Object) -join ',')
Assert-Condition ($sandboxProfiles -eq 'JAR_LOCAL') 'sandbox allowlist drifted'
Assert-Condition (-not ($fixture.PSObject.Properties.Name -contains 'credentials')) 'fixture must not contain credentials'

$required = @('CONTEXT_COMPRESSION', 'CONTROLLED_WORKSPACE', 'CONTROLLED_SKILLS', 'CONTROLLED_SUBAGENTS', 'LONG_TERM_MEMORY', 'DOCKER_SANDBOX')
$actual = (($fixture.capabilities.id | Sort-Object) -join ',')
$expected = (($required | Sort-Object) -join ',')
Assert-Condition ($actual -eq $expected) 'Harness capability coverage is incomplete'
foreach ($capability in $fixture.capabilities) {
    Assert-Condition (-not [string]::IsNullOrWhiteSpace($capability.version)) "version missing: $($capability.id)"
    Assert-Condition ($capability.functionalChecks.Count -gt 0 -and $capability.securityChecks.Count -gt 0 -and $capability.failureChecks.Count -gt 0) "check categories incomplete: $($capability.id)"
    Assert-Condition ($capability.manualRequired -eq $true) "manual verification flag missing: $($capability.id)"
}

$backendDockerfile = Get-Content -LiteralPath (Join-Path $PSScriptRoot '../deploy/backend.Dockerfile') -Raw
$frontendDockerfile = Get-Content -LiteralPath (Join-Path $PSScriptRoot '../deploy/frontend.Dockerfile') -Raw
Assert-Condition ($backendDockerfile -notmatch 'docker\.sock' -and $frontendDockerfile -notmatch 'docker\.sock') 'Docker socket mount is not allowed'
Assert-Condition ($backendDockerfile -match 'USER 10001:10001' -and $frontendDockerfile -match 'USER nginx') 'deployment must be non-root'

if (-not $Execute) {
    Write-Output 'status=OK'
    Write-Output 'mode=STATIC'
    Write-Output "fixtureVersion=$($fixture.fixtureVersion)"
    Write-Output "capabilityCount=$($fixture.capabilities.Count)"
    Write-Output 'networkCalls=0'
    Write-Output 'containerCalls=0'
    exit 0
}

Assert-Condition (-not [string]::IsNullOrWhiteSpace($ResultDirectory)) 'ResultDirectory is required with -Execute'
Assert-Condition (Test-Path -LiteralPath $ResultDirectory -PathType Container) "result directory not found: $ResultDirectory"
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
$resultIds = @(Get-ChildItem -LiteralPath $ResultDirectory -File -Filter '*.json' -Recurse | ForEach-Object {
    (Get-Content -LiteralPath $_.FullName -Raw | ConvertFrom-Json).capabilityId
} | Where-Object { $_ })
$missing = @($required | Where-Object { $_ -notin $resultIds })
$report = [pscustomobject]@{
    fixtureVersion = $fixture.fixtureVersion
    capabilityCount = $fixture.capabilities.Count
    resultCount = $resultIds.Count
    missingCapabilities = $missing
    status = if ($missing.Count -eq 0) { 'OK' } else { 'INCOMPLETE' }
}
$reportPath = Join-Path $OutputDirectory 'harness-capability-acceptance-report.json'
$report | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $reportPath -Encoding utf8
Write-Output "status=$($report.status)"
Write-Output "report=$reportPath"
Write-Output "missingCapabilities=$($missing.Count)"
if ($report.status -ne 'OK') { exit 2 }

[CmdletBinding()]
param(
    [switch]$Execute,
    [string]$FixturePath = (Join-Path $PSScriptRoot 'fixtures/data-boundary-negative-v1.json'),
    [string]$ScanRoot,
    [string]$OutputDirectory = (Join-Path $env:TEMP 'finance-agent-data-boundary-negative')
)

$ErrorActionPreference = 'Stop'

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

Assert-Condition (Test-Path -LiteralPath $FixturePath -PathType Leaf) "fixture not found: $FixturePath"
$fixture = Get-Content -LiteralPath $FixturePath -Raw | ConvertFrom-Json
Assert-Condition ($fixture.fixtureVersion -eq 'data-boundary-negative-v1') 'unexpected fixture version'
Assert-Condition ($fixture.syntheticMarker -match '^NEGATIVE_BOUNDARY_MARKER_[A-Z0-9]+$') 'invalid synthetic marker'
Assert-Condition ($fixture.scanTargets.Count -eq 7) 'scan target list must cover seven boundary types'
Assert-Condition (($fixture.scanTargets | Sort-Object) -join ',' -eq 'checkpoint,database,export,logs,sse,trace,workspace') 'scan targets are incomplete'
Assert-Condition ($fixture.expected.forbiddenPersistence -eq 0 -and $fixture.expected.forbiddenModelTransmission -eq 0 -and $fixture.expected.sourceWrites -eq 0 -and $fixture.expected.markerHits -eq 0) 'negative gate expectations must be zero'
Assert-Condition ($fixture.sourceWriteProbe.expected -eq 'rejected') 'source write probe must be rejected'
Assert-Condition (($fixture.sourceWriteProbe.methodNames | Sort-Object) -join ',' -eq 'catalog,query') 'source adapter contract drifted'
Assert-Condition (-not ($fixture.PSObject.Properties.Name -contains 'payload')) 'fixture must not contain payload'

if (-not $Execute) {
    Write-Output 'status=OK'
    Write-Output 'mode=STATIC'
    Write-Output "fixtureVersion=$($fixture.fixtureVersion)"
    Write-Output 'networkCalls=0'
    Write-Output 'databaseCalls=0'
    exit 0
}

Assert-Condition (-not [string]::IsNullOrWhiteSpace($ScanRoot)) 'ScanRoot is required with -Execute'
Assert-Condition (Test-Path -LiteralPath $ScanRoot -PathType Container) "scan root not found: $ScanRoot"
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$forbiddenKeys = @($fixture.forbiddenKeys | ForEach-Object { '"' + [regex]::Escape($_) + '"\s*:' })
$patterns = @($forbiddenKeys + [regex]::Escape($fixture.syntheticMarker))
$hits = @()
Get-ChildItem -LiteralPath $ScanRoot -File -Recurse | ForEach-Object {
    $content = Get-Content -LiteralPath $_.FullName -Raw -ErrorAction SilentlyContinue
    foreach ($pattern in $patterns) {
        if ($content -match $pattern) {
            $hits += [pscustomobject]@{ target = $_.FullName; pattern = $pattern }
        }
    }
}

$report = [pscustomobject]@{
    fixtureVersion = $fixture.fixtureVersion
    scanRoot = (Resolve-Path -LiteralPath $ScanRoot).Path
    markerHits = @($hits | Where-Object { $_.pattern -eq [regex]::Escape($fixture.syntheticMarker) }).Count
    forbiddenHits = @($hits | Where-Object { $_.pattern -ne [regex]::Escape($fixture.syntheticMarker) }).Count
    sourceWrites = 0
    status = if ($hits.Count -eq 0) { 'OK' } else { 'FAILED' }
}
$reportPath = Join-Path $OutputDirectory 'data-boundary-negative-report.json'
$report | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $reportPath -Encoding utf8
Write-Output "status=$($report.status)"
Write-Output "report=$reportPath"
Write-Output "markerHits=$($report.markerHits)"
Write-Output "forbiddenHits=$($report.forbiddenHits)"
Write-Output 'sourceWrites=0'
if ($hits.Count -gt 0) { exit 2 }

[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$FixturePath = (Join-Path $PSScriptRoot "fixtures/core-synthetic-vertical-slice-v1.json"),
    [string]$OutputDirectory = (Join-Path (Split-Path -Parent $PSScriptRoot) "qa-output/core-synthetic-vertical-slice"),
    [switch]$Execute
)

$ErrorActionPreference = "Stop"

function Read-Fixture {
    if (-not (Test-Path -LiteralPath $FixturePath -PathType Leaf)) {
        throw "fixture not found: $FixturePath"
    }
    $fixture = Get-Content -Raw -LiteralPath $FixturePath | ConvertFrom-Json
    $required = @("fixtureVersion", "seed", "operatorIdentity", "viewerIdentity", "runtimeBundleId", "task",
        "premise", "scope", "steps", "source", "recheck", "packet", "requiredArtifacts")
    foreach ($name in $required) {
        if ($null -eq $fixture.$name) { throw "fixture field is missing: $name" }
    }
    if ($fixture.fixtureVersion -ne "core-synthetic-vertical-slice-v1" -or $fixture.seed -ne 17) {
        throw "unexpected fixture version or seed"
    }
    if (@($fixture.steps).Count -ne 3 -or @($fixture.steps | Where-Object recommended).Count -ne 1) {
        throw "fixture must contain three steps and one recommendation"
    }
    $requiredArtifacts = @($fixture.requiredArtifacts)
    foreach ($artifact in @("task", "step_approval", "fact_snapshot", "coverage_record", "finding",
            "computation_witness", "professional_confirmation", "packet_review", "scope_recheck",
            "packet_revision", "audit_event", "handoff")) {
        if ($requiredArtifacts -notcontains $artifact) { throw "required artifact missing: $artifact" }
    }
    $serialized = $fixture | ConvertTo-Json -Depth 30 -Compress
    if ($serialized -match '(?i)password|api[_-]?key|prompt|reasoning|real[_-]?data') {
        throw "fixture contains a forbidden sensitive value marker"
    }
    return $fixture
}

function New-ResultDirectory {
    $root = [IO.Path]::GetFullPath($OutputDirectory)
    $run = Join-Path $root (Get-Date -Format "yyyyMMdd-HHmmss")
    New-Item -ItemType Directory -Path $run -Force | Out-Null
    return $run
}

function Get-Sha256Hex([string]$Value) {
    $bytes = [Text.Encoding]::UTF8.GetBytes($Value)
    $hash = [Security.Cryptography.SHA256]::Create().ComputeHash($bytes)
    return ([BitConverter]::ToString($hash) -replace '-', '').ToLowerInvariant()
}

function Invoke-Api {
    param(
        [string]$Name,
        [ValidateSet("GET", "POST")][string]$Method,
        [string]$Path,
        [string]$Identity,
        [object]$Body,
        [int[]]$ExpectedStatus = @(200, 201)
    )
    $headers = @{ "X-Test-Identity" = $Identity; "Idempotency-Key" = ("qa-" + [Guid]::NewGuid().ToString()) }
    $uri = ($BaseUrl.TrimEnd('/') + $Path)
    $jsonBody = if ($null -eq $Body) { $null } else { $Body | ConvertTo-Json -Depth 30 -Compress }
    $response = $null
    $status = 0
    $content = ""
    try {
        if ($null -eq $jsonBody) {
            $response = Invoke-WebRequest -UseBasicParsing -Uri $uri -Method $Method -Headers $headers
        } else {
            $response = Invoke-WebRequest -UseBasicParsing -Uri $uri -Method $Method -Headers $headers -ContentType "application/json" -Body $jsonBody
        }
        $status = [int]$response.StatusCode
        $content = [string]$response.Content
    } catch {
        if ($null -eq $_.Exception.Response) { throw }
        $status = [int]$_.Exception.Response.StatusCode
        $reader = New-Object IO.StreamReader($_.Exception.Response.GetResponseStream())
        $content = $reader.ReadToEnd()
        $reader.Dispose()
    }
    $parsed = if ([string]::IsNullOrWhiteSpace($content)) { $null } else { $content | ConvertFrom-Json }
    $record = [ordered]@{ step = $Name; status = $status; body = $parsed }
    $record | ConvertTo-Json -Depth 30 | Set-Content -LiteralPath (Join-Path $script:RunDirectory ($Name + ".json")) -Encoding UTF8
    if ($ExpectedStatus -notcontains $status) {
        throw "$Name failed with HTTP $status; response saved under $script:RunDirectory"
    }
    return $parsed
}

$fixture = Read-Fixture
$script:RunDirectory = New-ResultDirectory
$fixture | ConvertTo-Json -Depth 30 | Set-Content -LiteralPath (Join-Path $script:RunDirectory "fixture.json") -Encoding UTF8
Write-Output "fixture=OK"
Write-Output "fixtureVersion=$($fixture.fixtureVersion)"
Write-Output "outputDirectory=$script:RunDirectory"

if (-not $Execute) {
    Write-Output "mode=static"
    Write-Output "静态校验通过；需要用户启动 local 环境后显式传入 -Execute 执行 API 纵向切片。"
    exit 0
}

$health = Invoke-Api -Name "00-health" -Method GET -Path "/actuator/health" -Identity $fixture.operatorIdentity
$task = Invoke-Api -Name "01-create-task" -Method POST -Path "/api/v1/tasks" -Identity $fixture.operatorIdentity -Body @{
    title = $fixture.task.title; originalQuestion = $fixture.task.originalQuestion
    initiatorUserId = $fixture.task.initiatorUserId; runtimeBundleId = $fixture.runtimeBundleId
}
$taskId = $task.taskId
$premise = Invoke-Api -Name "02-add-premise" -Method POST -Path "/api/v1/tasks/$taskId/premises" -Identity $fixture.operatorIdentity -Body $fixture.premise
$scope = Invoke-Api -Name "03-confirm-scope" -Method POST -Path "/api/v1/tasks/$taskId/scope-confirmations" -Identity $fixture.operatorIdentity -Body $fixture.scope
$proposal = Invoke-Api -Name "04-propose-steps" -Method POST -Path "/api/v1/tasks/$taskId/step-proposals" -Identity $fixture.operatorIdentity -Body @{ steps = @($fixture.steps) }
$recommended = @($proposal.steps | Where-Object recommended | Select-Object -First 1)[0]
$goalHash = Get-Sha256Hex $recommended.goal
$sourceBindingId = [Guid]::NewGuid().ToString()
$approval = Invoke-Api -Name "05-approve-step" -Method POST -Path "/api/v1/tasks/$taskId/step-approvals" -Identity $fixture.operatorIdentity -Body @{
    stepId = $recommended.stepId; goalHash = $goalHash; sourceBindingId = $sourceBindingId
    scopeRevisionId = $scope.currentScopeRevisionId; fieldPolicyVersion = $fixture.source.fieldPolicyVersion
    queryTemplateVersion = $fixture.source.queryTemplateVersion; maxCalls = 3; expiresAt = $null
    expectedRowVersion = $proposal.rowVersion; decision = "APPROVED"
}

$evidence = Invoke-Api -Name "06-evidence-overview" -Method GET -Path "/api/v1/tasks/$taskId/evidence" -Identity $fixture.operatorIdentity
if ((@($evidence.snapshots).Count -eq 0) -and (@($evidence.coverages).Count -eq 0) -and (@($evidence.findings).Count -eq 0)) {
    throw "evidence is empty; run the approved synthetic step/worker and rerun from this checkpoint"
}

$draftBody = @{
    scopeRevisionId = $scope.currentScopeRevisionId; runtimeBundleId = $fixture.runtimeBundleId
    problem = $fixture.packet.problem; premises = $fixture.packet.premises; scope = $fixture.packet.scope
    sources = $fixture.packet.sources; facts = $fixture.packet.facts; computations = $fixture.packet.computations
    professionalJudgments = $fixture.packet.professionalJudgments; recommendations = $fixture.packet.recommendations
    businessConfirmations = $fixture.packet.businessConfirmations; validationPreparation = $fixture.packet.validationPreparation
    handoff = $fixture.packet.handoff
}
$draft = Invoke-Api -Name "07-assemble-draft" -Method POST -Path "/api/v1/tasks/$taskId/packet-drafts" -Identity $fixture.operatorIdentity -Body $draftBody
$review = Invoke-Api -Name "08-review-draft" -Method POST -Path "/api/v1/tasks/$taskId/packet-drafts/$($draft.draftId)/reviews" -Identity $fixture.operatorIdentity -Body @{
    contentDigest = $draft.contentDigest; scopeRevisionId = $scope.currentScopeRevisionId
    runtimeBundleId = $fixture.runtimeBundleId; decision = "APPROVE"; comment = "合成纵向切片复核"
}
$publicationBody = @{
    draftId = $draft.draftId; publicationId = [Guid]::NewGuid().ToString(); contentDigest = $draft.contentDigest
    scopeRevisionId = $scope.currentScopeRevisionId; runtimeBundleId = $fixture.runtimeBundleId
    canonicalJson = $draft.canonicalJson; htmlSnapshot = $draft.htmlSnapshot
    sourceVersions = $fixture.sourceVersionsJson; coverageSummary = $fixture.coverageSummaryJson
    baseline = $fixture.recheck.baseline; current = $fixture.recheck.changedCurrent
}
$stale = Invoke-Api -Name "09-publication-stale-recheck" -Method POST -Path "/api/v1/tasks/$taskId/packet-publications" -Identity $fixture.operatorIdentity -Body $publicationBody -ExpectedStatus @(409)
if ($stale.published -ne $false -or $stale.recheckDecision -ne "STALE_DRAFT") { throw "stale recheck did not invalidate the old review" }

$draft2 = Invoke-Api -Name "10-assemble-refreshed-draft" -Method POST -Path "/api/v1/tasks/$taskId/packet-drafts" -Identity $fixture.operatorIdentity -Body $draftBody
$review2 = Invoke-Api -Name "11-review-refreshed-draft" -Method POST -Path "/api/v1/tasks/$taskId/packet-drafts/$($draft2.draftId)/reviews" -Identity $fixture.operatorIdentity -Body @{
    contentDigest = $draft2.contentDigest; scopeRevisionId = $scope.currentScopeRevisionId
    runtimeBundleId = $fixture.runtimeBundleId; decision = "APPROVE"; comment = "重查后重新复核"
}
$publicationBody.draftId = $draft2.draftId
$publicationBody.publicationId = [Guid]::NewGuid().ToString()
$publicationBody.contentDigest = $draft2.contentDigest
$publicationBody.canonicalJson = $draft2.canonicalJson
$publicationBody.htmlSnapshot = $draft2.htmlSnapshot
$publicationBody.current = $fixture.recheck.stableCurrent
$published = Invoke-Api -Name "12-publication-clean-recheck" -Method POST -Path "/api/v1/tasks/$taskId/packet-publications" -Identity $fixture.operatorIdentity -Body $publicationBody -ExpectedStatus @(201)
$operatorView = Invoke-Api -Name "13-operator-published-view" -Method GET -Path "/api/v1/tasks/$taskId/published-packet" -Identity $fixture.operatorIdentity
$viewerView = Invoke-Api -Name "14-viewer-published-view" -Method GET -Path "/api/v1/tasks/$taskId/published-packet" -Identity $fixture.viewerIdentity
if (($operatorView | ConvertTo-Json -Depth 30 -Compress) -ne ($viewerView | ConvertTo-Json -Depth 30 -Compress)) {
    throw "operator and viewer published views differ; responses saved under $script:RunDirectory"
}
$revisionId = $published.packetRevisionId
$handoff = Invoke-Api -Name "15-handoff" -Method POST -Path "/api/v1/tasks/$taskId/packet-revisions/$revisionId/handoff" -Identity $fixture.operatorIdentity -Body $fixture.packet.handoff
$closed = Invoke-Api -Name "16-close-task" -Method POST -Path "/api/v1/tasks/$taskId/close" -Identity $fixture.operatorIdentity -Body @{ closureReason = "合成纵向切片已交接" }
Write-Output "mode=execute"
Write-Output "taskId=$taskId"
Write-Output "packetRevisionId=$revisionId"
Write-Output "result=PASS"

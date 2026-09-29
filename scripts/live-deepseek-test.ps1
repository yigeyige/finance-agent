[CmdletBinding()]
param(
    [switch]$Execute,
    [string]$BaseUrl = 'https://api.deepseek.com',
    [string]$Model = 'deepseek-flash',
    [string]$OutputFile = (Join-Path $env:TEMP 'finance-agent-deepseek-live-result.json')
)

$ErrorActionPreference = 'Stop'

function Assert-Condition([bool]$Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}

Assert-Condition ($Model -eq 'deepseek-flash') 'live test must use the approved synthetic model'
Assert-Condition (-not [string]::IsNullOrWhiteSpace($BaseUrl)) 'BaseUrl is required'

if (-not $Execute) {
    Write-Output 'status=READY'
    Write-Output 'mode=STATIC'
    Write-Output 'networkCalls=0'
    Write-Output 'apiKeyRead=0'
    Write-Output "model=$Model"
    exit 0
}

$apiKey = $env:DEEPSEEK_API_KEY
Assert-Condition (-not [string]::IsNullOrWhiteSpace($apiKey)) 'DEEPSEEK_API_KEY must be set only in the current process'
$request = @{
    model = $Model
    messages = @(@{ role = 'user'; content = 'Return a JSON object with exactly one field named status and value ok.' })
    stream = $false
    response_format = @{ type = 'json_object' }
    max_tokens = 128
    thinking = @{ type = 'enabled' }
    include_reasoning = $true
} | ConvertTo-Json -Depth 6

$response = Invoke-RestMethod -Method Post -Uri (($BaseUrl.TrimEnd('/')) + '/chat/completions') `
    -Headers @{ Authorization = "Bearer $apiKey"; 'Content-Type' = 'application/json' } `
    -Body $request -TimeoutSec 60
$choice = @($response.choices)[0]
$content = if ($choice.message.content) { $choice.message.content | ConvertFrom-Json } else { $null }
$schemaStatus = if ($content -and $content.status -eq 'ok' -and @($content.PSObject.Properties.Name).Count -eq 1) { 'OK' } else { 'FAILED' }
$result = [pscustomobject]@{
    status = if ($schemaStatus -eq 'OK') { 'OK' } else { 'FAILED' }
    model = $response.model
    responseId = $response.id
    finishReason = $choice.finish_reason
    promptTokens = $response.usage.prompt_tokens
    completionTokens = $response.usage.completion_tokens
    totalTokens = $response.usage.total_tokens
    schemaStatus = $schemaStatus
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputFile) | Out-Null
$result | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $OutputFile -Encoding utf8
Write-Output "status=$($result.status)"
Write-Output "model=$($result.model)"
Write-Output "responseId=$($result.responseId)"
Write-Output "finishReason=$($result.finishReason)"
Write-Output "totalTokens=$($result.totalTokens)"
Write-Output "schemaStatus=$($result.schemaStatus)"
Write-Output "result=$OutputFile"
if ($result.status -ne 'OK') { exit 2 }

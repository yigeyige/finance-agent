param(
    [string]$MigrationDirectory = ""
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($MigrationDirectory)) {
    $MigrationDirectory = Join-Path $repoRoot "backend\src\main\resources\db\migration"
}

$expected = 1..14 | ForEach-Object { "V{0}__" -f $_ }
$files = Get-ChildItem -LiteralPath $MigrationDirectory -Filter "V*.sql" -File |
    Where-Object { $_.Name -match '^V[1-9][0-9]*__.*\.sql$' } |
    Sort-Object { [int]([regex]::Match($_.Name, '^V(\d+)__').Groups[1].Value) }
if ($files.Count -ne 14) {
    throw "Expected exactly 14 Flyway migration files (V1-V14)."
}
for ($index = 0; $index -lt $expected.Count; $index++) {
    if (-not $files[$index].Name.StartsWith($expected[$index])) {
        throw "Unexpected migration order or name: $($files[$index].Name)"
    }
}

$sql = ($files | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"
$requiredSchemas = @("iam", "task", "evidence", "packet", "harness", "knowledge", "governance")
foreach ($schema in $requiredSchemas) {
    if ($sql -notmatch "(?im)\b$schema\.") {
        throw "Missing schema reference: $schema"
    }
}
foreach ($forbidden in @("CREATE\s+ROLE", "CREATE\s+DATABASE", "CREATE\s+EXTENSION", "DROP\s+SCHEMA", "DROP\s+DATABASE", "PASSWORD\s*=")) {
    if ($sql -match "(?i)$forbidden") {
        throw "Forbidden statement found: $forbidden"
    }
}
if ($sql -notmatch "(?i)vector\s*\(\s*3\s*\)") {
    throw "V7 must pin the verified vector dimension to 3."
}
if ($sql -notmatch "(?i)ux_one_active_lease_per_task") {
    throw "Expected active lease uniqueness index is missing."
}
if ($sql -notmatch "(?is)created_by.*created_at.*updated_by.*updated_at") {
    throw "Uniform audit columns are missing from the migration set."
}

Write-Output "status=OK"
Write-Output "migrationFiles=$($files.Count)"
Write-Output "vectorDimensions=3"

param(
    [string]$MigrationDirectory = ""
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($MigrationDirectory)) {
    $MigrationDirectory = Join-Path $repoRoot "backend\src\main\resources\db\migration"
}

$expected = 1..16 | ForEach-Object { "V{0}__" -f $_ }
$files = Get-ChildItem -LiteralPath $MigrationDirectory -Filter "V*.sql" -File |
    Where-Object { $_.Name -match '^V[1-9][0-9]*__.*\.sql$' } |
    Sort-Object { [int]([regex]::Match($_.Name, '^V(\d+)__').Groups[1].Value) }
if ($files.Count -ne 16) {
    throw "Expected exactly 16 Flyway migration files (V1-V16)."
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

$commentMigrationPath = Join-Path $MigrationDirectory "V15__add_schema_comments.sql"
if (-not (Test-Path -LiteralPath $commentMigrationPath -PathType Leaf)) {
    throw "Missing schema comments migration: V15__add_schema_comments.sql"
}
$commentSql = Get-Content -LiteralPath $commentMigrationPath -Raw -Encoding UTF8
foreach ($requiredToken in @("COMMENT ON TABLE", "COMMENT ON COLUMN", "缺少数据库表注释", "缺少数据库字段注释")) {
    if ($commentSql -notmatch [regex]::Escape($requiredToken)) {
        throw "Schema comments migration is missing required token: $requiredToken"
    }
}
$commentedTables = [regex]::Matches($commentSql, "WHEN '(?<table>[a-z_]+\.[a-z_]+)' THEN") |
    ForEach-Object { $_.Groups["table"].Value }
$expectedCommentedTables = @(
    "iam.agent_user", "iam.role_binding",
    "task.investigation_task", "task.scope_revision", "task.task_premise", "task.investigation_step",
    "task.step_approval", "task.task_lease", "task.tool_invocation", "task.business_scene_skill",
    "task.task_business_scene_skill",
    "evidence.fact_snapshot", "evidence.coverage_record", "evidence.finding", "evidence.finding_evidence",
    "evidence.computation_witness", "evidence.professional_confirmation",
    "packet.packet_draft", "packet.packet_review", "packet.packet_revision", "packet.handoff", "packet.scope_recheck",
    "harness.session_state", "harness.checkpoint", "harness.plan_revision", "harness.workspace_ref",
    "knowledge.document", "knowledge.embedding_set", "knowledge.chunk",
    "governance.runtime_bundle", "governance.audit_event", "governance.outbox_event", "governance.model_connection_config"
)
$missingTableComments = $expectedCommentedTables | Where-Object { $_ -notin $commentedTables }
$unknownTableComments = $commentedTables | Where-Object { $_ -notin $expectedCommentedTables }
if ($missingTableComments.Count -gt 0 -or $unknownTableComments.Count -gt 0 -or
        ($commentedTables | Select-Object -Unique).Count -ne $expectedCommentedTables.Count) {
    throw "Schema comments table mapping is incomplete or contains unknown tables."
}
$columnSection = [regex]::Match($commentSql, '(?s)column_description := CASE column_record\.column_name(?<body>.*?)\s+ELSE NULL\s+END;')
if (-not $columnSection.Success) {
    throw "Schema comments column mapping is missing."
}
$commentedColumns = [regex]::Matches($columnSection.Groups["body"].Value, "WHEN '(?<column>[a-z_]+)' THEN") |
    ForEach-Object { $_.Groups["column"].Value }
if (($commentedColumns | Select-Object -Unique).Count -ne 201 -or $commentedColumns.Count -ne 201) {
    throw "Schema comments column mapping must contain exactly 201 unique business column names."
}
$passwordMigrationPath = Join-Path $MigrationDirectory "V16__password_login.sql"
if (-not (Test-Path -LiteralPath $passwordMigrationPath -PathType Leaf)) { throw "Missing password login migration: V16__password_login.sql" }
$passwordSql = Get-Content -LiteralPath $passwordMigrationPath -Raw -Encoding UTF8
foreach ($requiredToken in @("iam.user_password", "COMMENT ON TABLE iam.user_password", "COMMENT ON COLUMN iam.user_password")) {
    if ($passwordSql -notmatch [regex]::Escape($requiredToken)) { throw "Password login migration is missing required token: $requiredToken" }
}

Write-Output "status=OK"
Write-Output "migrationFiles=$($files.Count)"
Write-Output "vectorDimensions=3"
Write-Output "commentedTables=$($expectedCommentedTables.Count)"
Write-Output "commentedColumns=$($commentedColumns.Count)"

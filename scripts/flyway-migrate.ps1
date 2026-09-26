param(
    [string]$MavenCommand = "mvn",
    [switch]$ValidateOnly
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$execArguments = @(
    "-f", (Join-Path $repoRoot "backend\pom.xml"),
    "-q", "exec:java",
    "-Dexec.mainClass=cn.com.crc.ems.finance.agent.migration.FlywayMigrationRunner",
    "-Dexec.classpathScope=runtime"
)
if ($ValidateOnly) {
    $execArguments += "-Dexec.args=--validate"
}

& $MavenCommand @execArguments
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

param(
    [string]$MavenCommand = "mvn",
    [switch]$CheckConfig
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$mavenArgs = @(
    "-f", (Join-Path $repoRoot "backend\pom.xml"),
    "-q",
    "exec:java",
    "-Dexec.mainClass=cn.com.crc.ems.finance.agent.diagnostics.PostgresConnectionProbe",
    "-Dexec.classpathScope=runtime"
)
if ($CheckConfig) {
    $mavenArgs += "-Dexec.args=--check-config"
}

& $MavenCommand @mavenArgs
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

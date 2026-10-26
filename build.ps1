$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot
$sourceRoot = Join-Path $projectRoot "src\main\java"
$outputRoot = Join-Path $projectRoot "out"
$sources = @(Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter *.java |
    Select-Object -ExpandProperty FullName)
if ($sources.Count -ne 17) {
    throw "Expected exactly the 17 Stage 1 types from the design; found $($sources.Count)."
}
New-Item -ItemType Directory -Force -Path $outputRoot | Out-Null
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d $outputRoot $sources
if ($LASTEXITCODE -ne 0) { throw "Compilation failed." }
Write-Host "Compiled all 17 Stage 1 prototypes. Game-rule TODOs remain unimplemented."

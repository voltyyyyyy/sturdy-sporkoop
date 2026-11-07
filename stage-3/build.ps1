$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot
$sourceRoot = Join-Path $projectRoot "src\main\java"
$outputRoot = Join-Path $projectRoot "out"
$sources = @(Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter *.java |
    Select-Object -ExpandProperty FullName)
if ($sources.Count -eq 0) {
    throw "No Stage 3 Java source files found."
}
New-Item -ItemType Directory -Force -Path $outputRoot | Out-Null
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d $outputRoot $sources
if ($LASTEXITCODE -ne 0) { throw "Compilation failed." }
Write-Host "Compiled $($sources.Count) Stage 3 source files."

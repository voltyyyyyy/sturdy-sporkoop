$ErrorActionPreference = "Stop"

$root = $PSScriptRoot
$sourceRoot = Join-Path $root "src\main\java"
$output = Join-Path $root "out"

$sources = @(Get-ChildItem -Path $sourceRoot -Recurse -Filter *.java |
    Select-Object -ExpandProperty FullName)

if ($sources.Count -eq 0) {
    throw "No Java sources found under $sourceRoot."
}

New-Item -ItemType Directory -Force -Path $output | Out-Null
javac -encoding UTF-8 --release 17 -d $output $sources

if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed with exit code $LASTEXITCODE."
}

Write-Host "Compiled $($sources.Count) source files into $output."

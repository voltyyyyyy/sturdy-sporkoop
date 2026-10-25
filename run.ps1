$ErrorActionPreference = "Stop"

$root = $PSScriptRoot
& (Join-Path $root "build.ps1")

java -cp (Join-Path $root "out") restaurant.app.Main @args
exit $LASTEXITCODE

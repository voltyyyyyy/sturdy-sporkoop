$ErrorActionPreference = "Stop"
& (Join-Path $PSScriptRoot "build.ps1")
java -cp (Join-Path $PSScriptRoot "out") restaurant.control.GameMgr @args
exit $LASTEXITCODE

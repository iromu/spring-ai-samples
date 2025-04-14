$sw = [Diagnostics.Stopwatch]::StartNew()

# To activate custom dockerignore
$env:DOCKER_BUILDKIT = 1

Set-Location (get-item $PSScriptRoot).parent.FullName
Import-Module ./docker/docker_lib.psm1

dockerBuildNodeWorkspace 'mini-chat-ui' 'mini-chat-ui'

$sw.Stop()

echo "NODE FINISHED"
$sw.Elapsed


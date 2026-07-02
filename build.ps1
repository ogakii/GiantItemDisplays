param(
    [switch]$Clean
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

function Get-MavenCommand {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) {
        return $cmd.Source
    }

    $toolsDir = Join-Path $projectRoot ".build-tools"
    $mavenVersion = "3.9.9"
    $mavenRoot = Join-Path $toolsDir "apache-maven-$mavenVersion"
    $mavenCmd = Join-Path $mavenRoot "bin\mvn.cmd"
    if (Test-Path $mavenCmd) {
        return $mavenCmd
    }

    New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
    $zipPath = Join-Path $toolsDir "apache-maven-$mavenVersion-bin.zip"
    $url = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"

    Write-Host "Maven nao esta no PATH. Baixando Apache Maven $mavenVersion..."
    Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile $zipPath
    Expand-Archive -LiteralPath $zipPath -DestinationPath $toolsDir -Force

    return $mavenCmd
}

$mvn = Get-MavenCommand
$argsList = @()
if ($Clean) {
    $argsList += "clean"
}
$argsList += "package"

& $mvn @argsList

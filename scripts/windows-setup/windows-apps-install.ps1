######################################################################################################
# Install Chocolatey
######################################################################################################
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Removes confirmation dialogs for each install
choco feature enable -n allowGlobalConfirmation

######################################################################################################
# Install apps
######################################################################################################
# choco install eclipse
choco install vscode
choco install github-desktop
choco install git
choco install ffmpeg
choco install teamviewer
choco install vlc
choco install googlechrome
choco install nodejs-lts
choco install rustdesk
choco install quicklook
choco install nvidia-display-driver
choco install python
choco install 7zip.install

######################################################################################################
# Download apps for manual install
######################################################################################################

$WebClient = New-Object System.Net.WebClient
# Realsense
$WebClient.DownloadFile("https://github.com/IntelRealSense/librealsense/releases/download/v2.54.1/Intel.RealSense.SDK-WIN10-2.54.1.5216.exe", "$HOME\Downloads\Intel.RealSense.SDK-WIN10-2.54.1.5216.exe")
# Wub.exe
$WebClient.DownloadFile("https://www.sordum.org/files/download/windows-update-blocker/Wub_v1.8.zip", "$HOME\Downloads\Wub_v1.8.zip")
Expand-Archive -Path "$HOME\Downloads\Wub_v1.8.zip" -Force -DestinationPath "$HOME\Downloads\Wub_v1.8"
Remove-Item -Path "$HOME\Downloads\Wub_v1.8.zip"

######################################################################################################
# Clean up links created on desktop
######################################################################################################
Write-Host "Remove all shortcuts"
Remove-Item C:\Users\*\Desktop\*lnk -Force


######################################################################################################
######################################################################################################
######################################################################################################
# Disable apps
######################################################################################################
######################################################################################################
######################################################################################################

Write-Host "Disabling OneDrive..."
If (!(Test-Path "HKLM:\SOFTWARE\Policies\Microsoft\Windows\OneDrive")) {
    New-Item -Path "HKLM:\SOFTWARE\Policies\Microsoft\Windows\OneDrive" | Out-Null
}
Set-ItemProperty -Path "HKLM:\SOFTWARE\Policies\Microsoft\Windows\OneDrive" -Name "DisableFileSyncNGSC" -Type DWord -Value 1
Write-Host "Uninstalling OneDrive..."
Stop-Process -Name OneDrive -ErrorAction SilentlyContinue
Start-Sleep -s 3
$onedrive = "$env:SYSTEMROOT\SysWOW64\OneDriveSetup.exe"
If (!(Test-Path $onedrive)) {
    $onedrive = "$env:SYSTEMROOT\System32\OneDriveSetup.exe"
}
Start-Process $onedrive "/uninstall" -NoNewWindow -Wait
Start-Sleep -s 3
Stop-Process -Name explorer -ErrorAction SilentlyContinue
Start-Sleep -s 3
Remove-Item -Path "$env:USERPROFILE\OneDrive" -Force -Recurse -ErrorAction SilentlyContinue
Remove-Item -Path "$env:LOCALAPPDATA\Microsoft\OneDrive" -Force -Recurse -ErrorAction SilentlyContinue
Remove-Item -Path "$env:PROGRAMDATA\Microsoft OneDrive" -Force -Recurse -ErrorAction SilentlyContinue
Remove-Item -Path "$env:SYSTEMDRIVE\OneDriveTemp" -Force -Recurse -ErrorAction SilentlyContinue
If (!(Test-Path "HKCR:")) {
    New-PSDrive -Name HKCR -PSProvider Registry -Root HKEY_CLASSES_ROOT | Out-Null
}
Remove-Item -Path "HKCR:\CLSID\{018D5C66-4533-4307-9B53-224DE2ED1FE6}" -Recurse -ErrorAction SilentlyContinue
Remove-Item -Path "HKCR:\Wow6432Node\CLSID\{018D5C66-4533-4307-9B53-224DE2ED1FE6}" -Recurse -ErrorAction SilentlyContinue


######################################################################################################
Write-Host "Uninstall some applications that come with Windows out of the box" -ForegroundColor "Yellow"

# Referenced to build script
# https://docs.microsoft.com/en-us/windows/application-management/remove-provisioned-apps-during-update
# https://github.com/jayharris/dotfiles-windows/blob/master/windows.ps1#L157
# https://gist.github.com/jessfraz/7c319b046daa101a4aaef937a20ff41f
# https://gist.github.com/alirobe/7f3b34ad89a159e6daa1
# https://github.com/W4RH4WK/Debloat-Windows-10/blob/master/scripts/remove-default-apps.ps1
# 
# How to find more app IDs for uninstall:
# Get-AppxPackage -AllUsers | Select Name, PackageFullName
# More info here: https://techcult.com/how-to-uninstall-mcafee-livesafe-in-windows-10/
# 
# But then other apps needs to be found like so:
# https://redmondmag.com/articles/2019/08/27/powershell-to-uninstall-an-application.aspx

function removeApp {
	Param ([string]$appName)
	Write-Host "Trying to remove $appName"
	Get-AppxPackage $appName -AllUsers | Remove-AppxPackage
	Get-AppXProvisionedPackage -Online | Where DisplayName -like $appName | Remove-AppxProvisionedPackage -Online
}

$applicationList = @(
	# Microsoft
	"Microsoft.3DBuilder"
	"Microsoft.AppConnector"
	"Microsoft.BingFinance"
	"Microsoft.BingNews"
	"Microsoft.BingSports"
	"Microsoft.BingWeather"
	"Microsoft.CBSPreview"
	"Microsoft.CommsPhone"
	"Microsoft.ConnectivityStore"
	"Microsoft.FreshPaint"
	"Microsoft.GetHelp"
	"Microsoft.Getstarted"
	"Microsoft.Messaging"
	"Microsoft.Microsoft3DViewer"
	"Microsoft.MicrosoftOfficeHub"
	"Microsoft.MicrosoftPowerBIForWindows"
	"Microsoft.MicrosoftSolitaireCollection"
	"Microsoft.MicrosoftStickyNotes"
	"Microsoft.MinecraftUWP"
	"Microsoft.MixedReality.Portal*"
	"Microsoft.NetworkSpeedTest"
	"Microsoft.Office.OneNote"
	"Microsoft.Office.Sway"
	"Microsoft.OneConnect"
	"Microsoft.OneDriveSync"
	"Microsoft.People"
	"Microsoft.Print3D"
	"Microsoft.RemoteDesktop"
	"Microsoft.SkypeApp"
	"Microsoft.Todos"
	"Microsoft.WindowsAlarms"
	"Microsoft.WindowsCamera"
	"microsoft.windowscommunicationsapps"
	"Microsoft.WindowsFeedbackHub"
	"Microsoft.WindowsMaps"
	"Microsoft.WindowsPhone"
	"Microsoft.WindowsSoundRecorder"
	"Microsoft.XboxApp"
	"Microsoft.XboxGameOverlay"
	"Microsoft.XboxGamingOverlay"
	"Microsoft.XboxIdentityProvider"
	"Microsoft.YourPhone"
	"Microsoft.ZuneMusic"
	"Microsoft.ZuneVideo"
	"MicrosoftTeams"
	# "Microsoft.MSPaint"
	# "Microsoft.Windows.Photos"
	# 3rd party
	"*MarchofEmpires*"
	"*Minecraft*"
	"*Solitaire*"
	"*Skype*"
	"*Autodesk*"
	"*BubbleWitch*"
	"king.com*"
	"G5*"
	"*Dell*"
	"*Facebook*"
	"*Keeper*"
	"*Netflix*"
	"*McAfee*"
	"*Twitter*"
	"*Plex*"
	"*.Duolingo-LearnLanguagesforFree"
	"*.EclipseManager"
	"ActiproSoftwareLLC.562882FEEB491" 
	"*.AdobePhotoshopExpress"
	"9E2F88E3.Twitter"
	"king.com.CandyCrushSodaSaga"
	"4DF9E0F8.Netflix"
	"Drawboard.DrawboardPDF"
	"D52A8D61.FarmVille2CountryEscape"
	"GAMELOFTSA.Asphalt8Airborne"
	"flaregamesGmbH.RoyalRevolt2"
	"AdobeSystemsIncorporated.AdobePhotoshopExpress"
	"ActiproSoftwareLLC.562882FEEB491"
	"D5EA27B7.Duolingo-LearnLanguagesforFree"
	"Facebook.Facebook"
	"46928bounde.EclipseManager"
	"A278AB0D.MarchofEmpires"
	"KeeperSecurityInc.Keeper"
	"king.com.BubbleWitch3Saga"
	"89006A2E.AutodeskSketchBook"
	"CAF9E577.Plex"
	"A278AB0D.DisneyMagicKingdoms"
	"828B5831.HiddenCityMysteryofShadows"
	"WinZipComputing.WinZipUniversal"
	"SpotifyAB.SpotifyMusic"
	"PandoraMediaInc.29680B314EFC2"
	"2414FC7A.Viber"
	"64885BlueEdge.OneCalendar"
	"41038Axilesoft.ACGMediaPlayer"
	# ASUS
	"Clipchamp.Clipchamp"
	"B9ECED6F.ArmouryCrate"
	"Microsoft.Todos"
);

foreach ($app in $applicationList) {
  removeApp $app
}

Write-Host "Collecting new build from server..."
$srcZipURL = "https://example.com/files.zip"
$destZipPath = "$HOME\Downloads\files.zip"
$destDir = "$HOME\Downloads\files"

Write-Host "Downloading files. Please be patient..."
$WebClient = New-Object System.Net.WebClient
$WebClient.DownloadFile($srcZipURL, $destZipPath)

Write-Host "Extracting files..."
Expand-Archive -Path $destZipPath -Force -DestinationPath $destDir

Write-Host "Cleaning up..."
Remove-Item -Path $destZipPath

Write-Host "Done!" -ForegroundColor Green
Start-Sleep -s 10
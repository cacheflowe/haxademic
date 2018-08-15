<?php
// listing!
echo "<h1>Check-ins</h1>\n";
$projectId = $_GET['project'];
$path = "./projects/" . $projectId . "/checkins";
if ($handle = opendir($path)) {
  while (false !== ($dayPath = readdir($handle))) {
    if ('.' === $dayPath) continue;
    if ('..' === $dayPath) continue;
    if ('.DS_Store' === $dayPath) continue;

    // show project info!
    echo "<a href='./?project=$projectId&date=$dayPath'>$dayPath</a><br> \n";
    // showProjectInfo($dayPath);

  
  }
  closedir($handle);
}
?>
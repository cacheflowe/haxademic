<?php
// show project listing
echo '<h1>Projects</h1>';
$path = "./projects";
if ($handle = opendir($path)) {
  while (false !== ($projectId = readdir($handle))) {
    if ('.' === $projectId) continue;
    if ('..' === $projectId) continue;
    if ('.DS_Store' === $projectId) continue;

    // show project info!
    echo "<a href='./?project=$projectId'>$projectId</a><br> \n";
  }
  closedir($handle);
}
?>
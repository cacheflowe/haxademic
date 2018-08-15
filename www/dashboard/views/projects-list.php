<div class="mdl-grid portfolio-max-width">
  <h2 class='mdl-cell mdl-cell-full-width'>Projects</h2>
</div>
<div class="mdl-grid portfolio-max-width">
  <?php
  // project list links
  $projectDirs = get_files_chrono("./projects", false);
  foreach($projectDirs as $projectId) {
    $projectName = ucwords($projectId);
    $mostRecentJsonFile = last_checkin_data_for_project($projectId);
    $json_data = file_get_contents($mostRecentJsonFile);
    $checkinJSON = json_decode($json_data, true); 
    echo html_checkin_detail($checkinJSON, $projectId, true);
  }
  ?>
</div>
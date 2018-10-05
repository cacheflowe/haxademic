<div class="mdl-grid portfolio-max-width">
  <h2 class='mdl-cell mdl-cell-full-width'>Projects</h2>
</div>
<div class="mdl-grid portfolio-max-width">
  <?php
  // project list links
  $projectDirs = get_files_chrono("./projects", false);
  foreach($projectDirs as $projectId) {
    $mostRecentJsonFileName = last_checkin_data_for_project($projectId);
    echo html_checkin_detail($mostRecentJsonFileName, $projectId, true, true);
  }
  ?>
</div>

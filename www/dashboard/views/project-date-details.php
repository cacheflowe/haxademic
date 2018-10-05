<div class="mdl-grid portfolio-max-width">
  <h2 class='mdl-cell mdl-cell-full-width'><?php echo $_projectName ?> Checkins for <?php echo $_detailDate ?></h2>
</div>
<div class="mdl-grid portfolio-max-width">
  <?php
  // show project listing
  if(isset($_detailDate)) {
    // get day data dir
    $path = "./projects/$_projectId/checkins/$_detailDate/data";

    // get date's json files and draw cards
    $files = get_files_chrono($path);
    foreach($files as $dayJSONPath) {
      // show day info!
      $jsonCheckinFile = $path . '/' . $dayJSONPath;
      echo html_checkin_detail($jsonCheckinFile, $_projectId, false);
    }
  }
?>
</div>

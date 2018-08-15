<?php

// show project listing
$day = $_GET['date'];
if(isset($day)) {
  // day data!
  echo "<h2>Check-ins for $day: </h2>\n";
  $path = './projects/' . $_GET['project'] . "/checkins/$day/data";

  // get files and sort list
  $files = array();
  $dir = opendir($path); // open the cwd..also do an err check.
  while(false != ($file = readdir($dir))) {
    if(($file != ".") and ($file != "..") and ($file != ".DS_Store")) {
      $files[] = $file; // put in array.
    }   
  }
  natsort($files); // sort.
  $files = array_reverse($files, true);
  // closedir($path);

  // process files
  foreach($files as $dayJSONPath) {
    // show day info!
    $json_data = file_get_contents($path . '/' . $dayJSONPath);
    $dayJSONObj = json_decode($json_data, true); 
    // print_r($dayJSONObj);
    ?>

    <div class="demo-card-square mdl-card mdl-shadow--2dp">
      <div class="mdl-card__title mdl-card--expand">
        <?php if(isset($dayJSONObj['image'])) echo "<img src='" . $dayJSONObj['image'] . "'>"; ?>
        <?php if(isset($dayJSONObj['screenshot'])) echo "<img src='" . $dayJSONObj['screenshot'] . "'>"; ?>
        <!-- <h2 class="mdl-card__title-text">Update</h2> -->
      </div>
      <div class="mdl-card__supporting-text">
        <ul>
          <?php
          if(isset($dayJSONObj['time'])) echo "<li>Time: " . $dayJSONObj['time'] . "</li>";
          if(isset($dayJSONObj['uptime'])) echo "<li>Uptime: " . $dayJSONObj['uptime'] . "</li>";
          if(isset($dayJSONObj['frameRate'])) echo "<li>Framerate: " . $dayJSONObj['frameRate'] . "</li>";
          if(isset($dayJSONObj['frameCount'])) echo "<li>Frame count: " . $dayJSONObj['frameCount'] . "</li>";
          if(isset($dayJSONObj['resolution'])) echo "<li>Resolution: " . $dayJSONObj['resolution'] . "</li>";
          if(isset($dayJSONObj['relaunch'])) echo "<li>Relaunch: " . $dayJSONObj['relaunch'] . "</li>";
          ?>
        </ul>
      </div>
      <!--<div class="mdl-card__actions mdl-card--border">
        <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">
          View Updates
        </a>
      </div>-->
    </div>
    <?php
    // echo "<a href='./?date=$dayJSONPath'>$dayJSONPath</a><br> \n";
    // showProjectInfo($dayJSONPath);
  }
}

?>

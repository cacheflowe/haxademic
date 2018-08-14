<?php

// show project listing
echo '<html>';
echo '<head>';
echo '<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">';
echo '<link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">';
echo '<script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>';
echo '</head>';

$day = $_GET['date'];
if(isset($day)) {
  // day data!
  echo "<h2>Check-ins for $day: </h2>\n";
  $path = "./checkins/$day/data";

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
        <?php if(isset($dayJSONObj['image'])) echo "<img src='../." . $dayJSONObj['image'] . "'>"; ?>
        <?php if(isset($dayJSONObj['screenshot'])) echo "<img src='../." . $dayJSONObj['screenshot'] . "'>"; ?>
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


  if ($handle = opendir($path)) {
    while (false !== ($dayJSONPath = readdir($handle))) {
      if ('.' === $dayJSONPath) continue;
      if ('..' === $dayJSONPath) continue;
      if ('.DS_Store' === $dayJSONPath) continue;
  

    
    }
  }

} else {
  // listing!
  echo "<h1>Check-ins</h1>\n";
  $path = "./checkins";
  if ($handle = opendir($path)) {
    while (false !== ($dayPath = readdir($handle))) {
      if ('.' === $dayPath) continue;
      if ('..' === $dayPath) continue;
      if ('.DS_Store' === $dayPath) continue;
  
      // show project info!
      echo "<a href='./?date=$dayPath'>$dayPath</a><br> \n";
      // showProjectInfo($dayPath);
  
    
    }
    closedir($handle);
  }
}
echo '</html>';

?>

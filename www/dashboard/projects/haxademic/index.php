<?php

// show project listing
echo '<html>';
$day = $_GET['date'];
if(isset($day)) {
  // day data!
  echo "<h1>Check-ins for $day: </h1>\n";
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
    echo '<div>';
    echo '<ul>';
    if(isset($dayJSONObj['time'])) echo "<li>Time: " . $dayJSONObj['time'] . "</li>";
    if(isset($dayJSONObj['uptime'])) echo "<li>Uptime: " . $dayJSONObj['uptime'] . "</li>";
    if(isset($dayJSONObj['frameRate'])) echo "<li>Framerate: " . $dayJSONObj['frameRate'] . "</li>";
    if(isset($dayJSONObj['image'])) echo "<li><img src='../." . $dayJSONObj['image'] . "'></li>";
    echo '</ul>';
    echo '</div>';
    
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

<?php

// TODO LATER:
// Cache locally on app machine, then upload JSON files and delete on success
// Organize data:
// - Check-ins (Store as array per-day: performance, uptime, screenshot of the app running)
// - Interactions (Store as array per-day so we can parse/sort/chart later)
// - Crash alert (Also sends an email?)

// Notes:
// Make sure to add write permissions to save directory

// incoming json -> php object -> file
function isValidJSON($str) {
   json_decode($str);
   return json_last_error() == JSON_ERROR_NONE;
}

// create dir
function makeDirs($dirpath, $mode=0777) {
  return is_dir($dirpath) || mkdir($dirpath, $mode, true);
}

// convert base64 image to file
function base64_to_png($base64_string, $output_file) {
  // open the output file for writing
  $ifp = fopen( $output_file, 'wb' );
  fwrite( $ifp, base64_decode( $base64_string ) );
  // clean up the file resource
  fclose( $ifp );
  // return $output_file;
}

// Make sure URL doesn't redirect (even just adding a '/') - this kills php://input
$json_params = file_get_contents("php://input");

// save file 
if (strlen($json_params) > 0 && isValidJSON($json_params)) {
  // true option to return as array instead of json object
  $json_obj = json_decode($json_params, true); 

  // if project property exists, we're saving a project data file
  if(isset($json_obj['project'])) {
    date_default_timezone_set('America/Denver');

    // generate timestamp
    $date = date('Y-m-d');
    $time = date('H:i:s');
    $timestamp = date('Y-m-d-H-i-s');

    // get project dir
    $projectsDir = "./projects/";
    $projectDir = $projectsDir . $json_obj['project'] . '/';
    $checkinsDir = $projectDir . 'checkins/';
    $dateDir = $checkinsDir . $date . '/';
    $dateDataDir = $dateDir . 'data/';
    $dataFile = $dateDataDir . $timestamp . '.json';
    $dateImagesDir = $dateDir . 'images/';
    $screenshotFile = $dateImagesDir . $timestamp . '.png';

    // create project dir
    makeDirs($projectDir);
    makeDirs($checkinsDir);
    makeDirs($dateDir);
    makeDirs($dateDataDir);
    makeDirs($dateImagesDir);

    // set extra data on json
    $json_obj['time'] = $time;

    // write image to file and replace base64 image with new file path
    if(isset($json_obj['imageBase64'])) {
      base64_to_png($json_obj['imageBase64'], $screenshotFile);
      $json_obj['image'] = $screenshotFile;
      unset($json_obj['imageBase64']);
      echo "Image saved to: \n" . $screenshotFile . "\n";
      print_r($json_obj);
    }

    // write json data to file
    file_put_contents($dataFile, json_encode($json_obj, JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT) );
    chmod($dataFile, 0755);
  }

  // print new file contents
  echo file_get_contents($dataFile);
} else {
  // show project listing
  echo '<html>';
  echo '<h1>Projects</h1>';
  $path = "./projects";
  if ($handle = opendir($path)) {
    while (false !== ($projectPath = readdir($handle))) {
      if ('.' === $projectPath) continue;
      if ('..' === $projectPath) continue;
      if ('.DS_Store' === $projectPath) continue;

      // show project info!
      echo "<a href='./projects/$projectPath'>$projectPath</a><br> \n";
    }
    closedir($handle);
  }
  echo '</html>';
}

?>

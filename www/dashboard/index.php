<?php

// TODO:
// Cache locally on app machine, then upload JSON files and delete on success
// Organize data:
// - Check-ins (Store as array per-day: performance, uptime, screenshot of the app running)
// - Interactions (Store as array per-day so we can parse/sort/chart later)
// - Crash alert (Also sends an email?)

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

    // generate timestamp
    $timestamp = date('Y-m-d-H-i-s');

    // get project dir
    $projectsDir = "./projects/";
    $projectDir = $projectsDir . $json_obj['project'] . '/';
    $dataFile = $projectDir . $timestamp . '-data.json';
    $screenshotFile = $projectDir . $timestamp . '-screenshot.png';

    // create project dir
    makeDirs($projectsDir);
    makeDirs($projectDir);

    // write image to file and replace base64 image with new file path
    if(isset($json_obj['imageBase64'])) {
      base64_to_png($json_obj['imageBase64'], $screenshotFile);
      $json_obj['imageBase64'] = $screenshotFile;
      echo $screenshotFile . "\n";
      print_r($json_obj);
    }

    // write json data to file
    file_put_contents($dataFile, json_encode($json_obj, JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT) );
    chmod($dataFile, 0755);
  }

  // print new file contents
  echo file_get_contents($dataFile);
} else {
  echo '{error: true}';
}

?>

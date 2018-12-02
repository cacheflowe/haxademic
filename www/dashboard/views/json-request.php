<?php

// true option to return as array instead of json object
$json_obj = json_decode($jsonPosted, true);

// if project property exists, we're saving a project data file
if(isset($json_obj['project'])) {
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
  $appImage = $dateImagesDir . $timestamp . '.png';
  $screenshotImage = $dateImagesDir . $timestamp . '-screenshot.png';

  // create project dir
  chmod($projectDir, 0755);
  makeDirs($projectDir);
  makeDirs($checkinsDir);
  makeDirs($dateDir);
  makeDirs($dateDataDir);
  makeDirs($dateImagesDir);

  // set extra data on json
  $json_obj['time'] = $time;

  // write image to file and replace base64 image with new file path
  if(isset($json_obj['imageBase64'])) {
    base64_to_png($json_obj['imageBase64'], $appImage);
    $json_obj['image'] = $appImage;
    unset($json_obj['imageBase64']);
    echo "Image saved to: \n" . $appImage . "\n";
  }

  // write image to file and replace base64 image with new file path
  if(isset($json_obj['screenshotBase64'])) {
    base64_to_png($json_obj['screenshotBase64'], $screenshotImage);
    $json_obj['screenshot'] = $screenshotImage;
    unset($json_obj['screenshotBase64']);
    echo "Screenshot saved to: \n" . $screenshotImage . "\n";
  }

  // debug print newly-written json file
  print_r($json_obj);

  // write json data to file
  file_put_contents($dataFile, json_encode($json_obj, JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT) );
  chmod($dataFile, 0755);

  // remove older than 5 days
  remove_old_checkins($checkinsDir, 5);
}

// print new file contents
echo file_get_contents($dataFile);

?>

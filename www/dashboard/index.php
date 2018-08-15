<?php

// TODO LATER:
// Cache locally on app machine, then upload JSON files and delete on success
// Organize data:
// - Interactions (Store as array per-day so we can parse/sort/chart later)
// - Crash alert (Also sends an email?)

// Notes:
// Make sure to add write permissions to save directory

// set timezone
date_default_timezone_set('America/Denver');

// includes
include './views/functions.php';

// Make sure URL doesn't redirect (even just adding a '/') - this kills php://input
// check for JSON post
$jsonPosted = file_get_contents("php://input");

// get props
$projectPath = dirName($_SERVER['SCRIPT_FILENAME']);
$detailDate = $_GET['date'];
$projectId = $_GET['project'];
$projectName = ucwords($projectId);

if (isValidJSON($jsonPosted)) {
  // save file 
  include './views/json-request.php'; 
} else {
  // list projects
  include './views/html.php'; 
}
?>

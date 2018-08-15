<?php

// TODO LATER:
// Show current server time in dashboard UI
// Auto-refresh page every couple of minutes
// On date list, show number of checkins for each mini date card
// Cache JSON locally on app machine, then upload JSON files and delete on success
// User interaction tracking (Store as array per-day so we can parse/sort/chart later?)
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
$_projectPath = dirName($_SERVER['SCRIPT_FILENAME']);
$_detailDate = $_GET['date'];
$_projectId = $_GET['project'];
$_projectName = ucwords($_projectId);

if (isValidJSON($jsonPosted)) {
  // save file 
  include './views/json-request.php'; 
} else {
  // list projects
  include './views/html.php'; 
}
?>

<?php

// TODO LATER:
// Add a purge button (or auto-purge when > x days accumulate)
// On date list, show number of checkins for each mini date card
// [BUG] Why isn't the first image getting encoded properly?
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
$_projectName = ucwords(str_replace("-", " ", $_projectId)); //ucwords($_projectId);

if (isValidJSON($jsonPosted)) {
  // save file
  include './views/json-request.php';
} else {
  // list projects
  include './views/html.php';
}
?>

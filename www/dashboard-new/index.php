<?php

// date_default_timezone_set('America/Denver');

// includes
include './views/functions.php';
include './php/request/login.php';
include './php/request/request.php';
include './php/util/date-util.php';
include './php/util/file-util.php';
include './php/util/json-util.php';
include './php/util/string-util.php';
include './php/response/response.php';
include './views/dashboard.php';

$constants = [
  Login::PASS_KEY => "PASSWORD",
];

// Make sure URL doesn't redirect (even just adding a '/') - this kills php://input
// check for JSON post
$request = new Request();
$response = new Response($request);

// check for login submit - only if we've submitted the form
if(isset($_POST[Login::PASS_KEY])) {
  Login::checkPostedLoginPassword($_POST[Login::PASS_KEY], $constants[Login::PASS_KEY]);
} else if(isset($_GET['logout'])) {
  Login::clearAllCookies();
}

// get dashboard data from file
$dataPath = "data/dashboard.json";
$dashboard = new Dashboard($request, $dataPath);

// receive post or list existing data
$jsonPostStr = $request->postBody();
if (JsonUtil::isValidJSON($jsonPostStr)) {
  $dashboard->storePostedCheckIn();
} else {
  include './views/html.php';
}
?>

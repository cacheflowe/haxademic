<?php

// Simplesite & Dashboard includes
include './php/request/login.php';
include './php/request/request.php';
include './php/util/date-util.php';
include './php/util/file-util.php';
include './php/util/json-util.php';
include './php/util/string-util.php';
include './php/response/response.php';
include './views/dashboard.php';

// set up password & other constants
$constants = [
  Login::PASS_KEY => "PASSWORD",
];

// Get the SimpleSite request/response objects
// (Make sure URL doesn't redirect (even just adding a '/') - this kills php://input)
$request = new Request();
$response = new Response($request);

// check for login submit - only if we've submitted the form
if(isset($_POST[Login::PASS_KEY])) {
  Login::checkPostedLoginPassword($_POST[Login::PASS_KEY], $constants[Login::PASS_KEY]);
} else if(isset($_GET['logout'])) {
  Login::clearAllCookies();
}

// get dashboard data from file & init Dashboard
$dashboard = new Dashboard($request, "data/", 12, true);

// Receive posted checkin via JSON post & return success/fail message in JSON response
// OR: List existing data via Dashboard UI if we're not submitting a checkin
$jsonPostStr = $request->postBody();
if (JsonUtil::isValidJSON($jsonPostStr)) {
  $dashboard->storePostedCheckIn();
} else {
  include './views/html.php';
}
?>

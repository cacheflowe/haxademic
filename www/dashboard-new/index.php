<?php

date_default_timezone_set('America/Denver');

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
  "password" => "PASSWORD",
];

// Make sure URL doesn't redirect (even just adding a '/') - this kills php://input
// check for JSON post
$request = new Request();
$response = new Response($request);
$jsonPostStr = $request->postBody();

// get dashboard data
$dataPath = "dashboard.json";
$dashboard = new Dashboard($request, $dataPath);

// receive post or list existing data
if (JsonUtil::isValidJSON($jsonPostStr)) {
  $dashboard->storePostedCheckIn();
} else {
  include './views/html.php';
}
?>

<?php
  // return json
  header('Content-Type: application/json');
  header('Access-Control-Allow-Origin: *');

  // random message
  $outputJSON = '{"success": "It worked!"}';
  if(rand(0, 1) > 0.5) {
  	$outputJSON = '{"error": "Fail!"}';
  }
  echo $outputJSON;
 ?>

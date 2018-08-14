<?php

// Make sure to add write permissions to save directory

// incoming json -> php object -> file
function isValidJSON($str) {
   json_decode($str);
   return json_last_error() == JSON_ERROR_NONE;
}

// convert base64 image to file
function base64_to_png($base64_string, $output_file) {
  // open the output file for writing
  $ifp = fopen( $output_file, 'wb' );
  // split the string on commas
  // $data[ 0 ] == "data:image/png;base64"
  // $data[ 1 ] == <actual base64 string>
  // $data = explode( ',', $base64_string );
  // we could add validation here with ensuring count( $data ) > 1
  // fwrite( $ifp, base64_decode( $data[ 1 ] ) );
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
  $decoded_params = json_decode($json_params, true); 

  // write json data to file
  $file = 'data.json';
  file_put_contents($file, $json_params);

  // write image to file
  if(isset($decoded_params['imageBase64'])) {
    base64_to_png($decoded_params['imageBase64'], "incoming.png"); // ".uniqid()."
  }

  // print new file contents
  echo file_get_contents($file);
}

?>

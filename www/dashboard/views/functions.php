<?php

//////////////////////////////////////
// Helper functions
//////////////////////////////////////

// incoming json -> php object -> file
function isValidJSON($str) {
  if(strlen($str) == 0) return false;
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
?>
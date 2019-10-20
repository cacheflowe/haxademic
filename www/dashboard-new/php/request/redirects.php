<?php

// redirections if urls aren't well-formed for our purposes -------------------------------------------------
// redirect & strip if path has a trailing slash
if( substr( $_SERVER["REQUEST_URI"], -1 ) == "/" && strlen( $_SERVER["REQUEST_URI"] ) > 1 ) {
  header( "Location: " . substr( $_SERVER["REQUEST_URI"], 0, strlen( $_SERVER["REQUEST_URI"] ) - 1 ) );
  exit();
}

// redirect & strip if path has a www
if( substr( $_SERVER["REQUEST_URI"], 0, 9 ) == "http://www" ) {
  header( "Location: " . str_replace( "http://www.", "http://", $_SERVER["REQUEST_URI"] )  );
  exit();
}

// redirect if old page with ?page=blah format
if( isset( $_REQUEST['page'] ) ) {
  header("Location: /".$_REQUEST['page'],TRUE,301);
  exit();
}

?>

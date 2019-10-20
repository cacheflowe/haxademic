<?php

class Response {
  function __construct( $request ) {
    $this->request = $request;
    $this->initEnvironment();
    $this->compressHtmlOutput();
  }

  function view() { return $this->view; }

  function initEnvironment() {
    date_default_timezone_set('America/Denver');

    ini_set('display_errors', 1);
    error_reporting(E_ALL);
  }

  function compressHtmlOutput() {
    // compress output
    if( !@ini_set('zlib.output_compression',TRUE) && !@ini_set('zlib.output_compression_level',2) ) {
      ob_start('ob_gzhandler');
    }
    header ("content-type: text/html; charset: UTF-8");
    header ("cache-control: must-revalidate");
    $offset = 1 * -1;
    $expire = "expires: " . gmdate ("D, d M Y H:i:s", time() + $offset) . " GMT";
    header ($expire);
  }

  function renderPageRequest() {
    $this->view = new View();

    // determine if output is an ajax snippet or fully-rendered page
    if( $this->request->isAjax() == true ) {
      echo $this->view->html();
    } else {
      include './simplesite/php/response/html.php';
    }
  }

  function __destruct() {
    $this->request = null;
  }
}

?>

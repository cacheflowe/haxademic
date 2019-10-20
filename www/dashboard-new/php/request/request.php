<?php

class Request {
  function __construct() {
    $this->_path = '';
    $this->_postBody = '';
    $this->_isAjax = false;
    $this->isDev = false;
    $this->_isAPI = false;
    $this->_needsAuth = false;

    $this->getPath();
    $this->getPostData();
    $this->getDevMode();
    $this->setOutputType();
  }

  function routes() { return $this->routes; }
  function host() { return $this->_host; }
  function path() { return $this->_path; }
  function pathComponents() { return $this->_pathComponents; }
  function lastPathComponent() { return basename($this->_path); }
  function pathComponentsSize() { return count($this->_pathComponents); }
  function postBody() { return $this->_postBody; }
  function postedJson() { return json_decode($this->postBody(), true); }
  function isAjax() { return $this->_isAjax; }
  function isAPI() { return $this->_isAPI; }
  function isDev() { return $this->isDev; }
  function needsAuth() { return $this->_needsAuth; }

  function setAPI($isAPI) {
    $this->_isAPI = $isAPI;
    if($isAPI == true) $this->_isAjax = true;
  }

  function setAuthRequired($needsAuth) {
    $this->_needsAuth = $needsAuth;
  }

  function getPath() {
    // get page/mode and set to empty string if none
    $this->_host = "http://$_SERVER[HTTP_HOST]"; // "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]"
    $pathWithoutQuery = explode( "?", $_SERVER["REQUEST_URI"] );
    $this->_path = StringUtil::protectYaText( $pathWithoutQuery[0] ); // protectYaText( $_SERVER['QUERY_STRING'] ); //substr(, 1); // $_REQUEST['path'];
    if($this->_path == '' || $this->_path == '/') $this->_path = '/home';
    if(!Login::isLoggedIn() && $this->_needsAuth) $this->_path = '/login';
    $this->_pathComponents = explode( '/', substr( $this->_path, 1 ) );	// strip first slash and get array of path components
  }

  function getSectionName() {
    if(count($this->_pathComponents) > 0) return $this->_pathComponents[0];
    return null;
  }

  function getPostData() {
    $this->_postBody = file_get_contents('php://input');
  }

  function getDevMode() {
    global $serverConfig;
    if(strpos($_SERVER["SERVER_NAME"], "localhost") === 0) $this->isDev = true;
    if(isset( $_REQUEST['notDev'] )) $this->isDev = false;
    if(isset( $_REQUEST['isDev'] )) $this->isDev = true;
    if($serverConfig['alwaysDev'] == true) $this->isDev = true;
  }

  function setOutputType() {
    // check whether it's an ajax request
    // if( !empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) == 'xmlhttprequest' ) {
    if(strtolower($_SERVER['REQUEST_METHOD']) == 'post') {
      $this->_isAjax = true;
    }
    if(isset( $_REQUEST['ajax'] )) {
      $this->_isAjax = true;
    }
    if(isset( $_REQUEST['api'] )) {
      $this->_isAPI = true;
    }
  }
}

?>

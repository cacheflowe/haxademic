<?php

class Login {

  public static function isLoggedIn() {
    global $constants;
    global $request;

    // Simple password protection
    $loggedIn = false;
    if (isset($_COOKIE['password']) && $_COOKIE['password'] === $constants['password']) {
      $loggedIn = true;
    }
    return $loggedIn;
  }

  public static function setAuthRequired() {
    global $request;
    $request->setAuthRequired(true);
  }

  public static function resetPasswordCookie() {
    if (isset($_COOKIE['password'])) {
      unset($_COOKIE['password']);
      setcookie('password', '', time() - 3600, '/');
    }
  }

  public static function checkPostedLoginPassword() {
    global $constants;
    global $request;

    $submittedData = $request->postBody();   // expects entire POST body to be password text
    setcookie('password', $submittedData, strtotime('+30 days'), "/");
    if(isset($submittedData) && $submittedData == $constants['password']) {
      return true;
    } else {
      return false;
    }
  }

  public static function redirectIfNotLoggedIn() {
    global $request;
    if(!Login::isLoggedIn() && $request->pathComponents()[0] != 'login' && $request->path() != '/api') {
      header( "Location: /login"  );
      die();
    }
  }

}

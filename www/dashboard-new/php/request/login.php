<?php

class Login {

  const PASS_KEY = "password";

  public static function isLoggedIn() {
    global $constants;

    // Simple password protection
    $loggedIn = false;
    if(isset($_COOKIE[Login::PASS_KEY]) && $_COOKIE[Login::PASS_KEY] == $constants[Login::PASS_KEY]) {
      $loggedIn = true;
    }
    return $loggedIn;
  }

  public static function setAuthRequired($isRequired=true) {
    global $request;
    $request->setAuthRequired($isRequired);
  }

  public static function resetPasswordCookie() {
    if (isset($_COOKIE[Login::PASS_KEY])) {
      unset($_COOKIE[Login::PASS_KEY]);
      setcookie(Login::PASS_KEY, '', 1, '/');
    }
  }

  public static function checkPostedLoginPassword($password, $realPassword) {
    setcookie(Login::PASS_KEY, $password, strtotime('+30 days'), '/');
    $_COOKIE[Login::PASS_KEY] = $password;  // make it available to $_COOKIE on this page load (before the default of the next refresh): https://stackoverflow.com/a/3230167/352456
    if($password == $realPassword) {
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

  public static function clearAllCookies() {
    foreach($_COOKIE as $key => $value) {
      setcookie($key, '', 1);     // time of 1 is 1/1/1970
      setcookie($key, '', 1, '/');
      $_COOKIE[$key] = '';
    }
  }

}

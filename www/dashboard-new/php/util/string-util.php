<?php

class StringUtil {

  public static function protectYaText( $text ) {
    // security against naughty foreign or local includes. limit to alphanumeric + '/'?
    $text = str_replace( "http", " ", $text );
    $text = str_replace( "../", " ", $text );
    $text = str_replace( "./", " ", $text );
    $text = preg_replace("/[^a-zA-Z0-9-+@.\/\s]/", "", $text );
    return $text;
  }

  public static function makeFriendlyText( $text ) {
    $text = strtolower( $text );
    // $text = substr( $text, 0, FRIENDLY_TITLE_CUTOFF ); // shorten it?
    $text = StringUtil::stripAllExceptAlphanumeric( $text );
    $text = str_replace( " ", '-', $text );
    while( strpos( $text, '--' ) !== false ) { $text = str_replace( "--", '-', $text ); } // make sure dashes in title don't mess up our pretty url
    return $text;
  }

  public static function stripAllExceptAlphanumeric( $str ) {
    return preg_replace("/[^a-zA-Z0-9-+\s]/", "", $str);
  }

  public static function isHexColor( $str ) {
    return(preg_match('/^#[a-f0-9]{6}$/i', $str));
  }

}

?>

<?php

class DateUtil {

  public static function getYear() {
    return date("Y");
  }

  public static function getSimpleDateFromJsDate($dateStr) {
    return substr($dateStr, 0, 10);
  }

  public static function getReadableDateFromJsDate($jsDate) {
    $date = DateUtil::getDateTimeFromYearMonthDay(DateUtil::getSimpleDateFromJsDate($jsDate));
    return $date->format('M j, Y');
  }

  public static function getDateTimeFromYearMonthDay($dateTimeStamp) {
    // $format = 'Y-m-d-H-i-s';
    $format = 'Y-m-d';
    return DateTime::createFromFormat($format, $dateTimeStamp); // 2009-02-15 15:16:17
  }

  public static function createTimestamp() {
    return date('Y-m-d-H-i-s');
  }

  public static function msSince1970() {
    return time();
  }

  public static function getDateTimeFromMS($msSince1970) {
    return new DateTime("@$msSince1970");
  }

  public static function getCurrentYearMonthDay() {
    $now = new DateTime();
    return $now->format('Y-m-d');
  }

  public static function dateStampOffsetFromNow($dateStamp) {
    $otherDate = DateUtil::getDateTimeFromYearMonthDay($dateStamp);
    $now = DateUtil::getDateTimeFromYearMonthDay(DateUtil::getCurrentYearMonthDay());
    $dateDiff = $otherDate->diff($now);
    return intval( $dateDiff->format('%R%a') );
  }

  public static function jsDateDaysAgo($jsDate) {
    return DateUtil::dateStampOffsetFromNow(DateUtil::getSimpleDateFromJsDate($jsDate));
  }

  public static function sortByDate($a, $b) {
    return strcmp($b["date"], $a["date"]);
  }

  public static function timeElapsedString($dateTimeOld, $full=false) {
      $now = new DateTime;
      $ago = $dateTimeOld; // new DateTime($dateTime);
      $diff = $now->diff($ago);

      $diff->w = floor($diff->d / 7);
      $diff->d -= $diff->w * 7;

      $string = array(
          'y' => 'year',
          'm' => 'month',
          'w' => 'week',
          'd' => 'day',
          'h' => 'hour',
          'i' => 'minute',
          's' => 'second',
      );
      foreach ($string as $k => &$v) {
          if ($diff->$k) {
              $v = $diff->$k . ' ' . $v . ($diff->$k > 1 ? 's' : '');
          } else {
              unset($string[$k]);
          }
      }

      if (!$full) $string = array_slice($string, 0, 1);
      return $string ? implode(', ', $string) . ' ago' : 'just now';
  }

  public static function secondsToClockTime($seconds) {
    return gmdate('H:i:s', $seconds);
  }
}

?>

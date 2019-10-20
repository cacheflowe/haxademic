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

// rm -rf
function rrmdir($dir) {
  if (is_dir($dir)) {
    $objects = scandir($dir);
    foreach ($objects as $object) {
      if ($object != "." && $object != "..") {
        if (is_dir($dir."/".$object))
          rrmdir($dir."/".$object);
        else
          unlink($dir."/".$object);
      }
    }
    rmdir($dir);
  }
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

// get files/dirs from dir and sort reverse chronologially
function get_files_chrono($path, $reverse=true) {
  $files = array();
  $dir = opendir($path); // open the cwd..also do an err check.
  while(false != ($file = readdir($dir))) {
    if(($file != ".") and ($file != "..") and ($file != ".DS_Store")) {
      $files[] = $file; // put in array.
    }
  }
  natsort($files); // sort.
  if($reverse == true) $files = array_reverse($files, true);
  closedir($dir);
  return $files;
}

// get most recent project day timestamp (i.e., `2018-10-05`)
function last_checkin_day_for_project($project_id) {
  $dateDirs = get_files_chrono("./projects/$project_id/checkins", false);
  return end($dateDirs); // return last element in non-reversed array
}

// get date for last checkin
function last_checkin_date_for_checkin_data_and_day($checkinJSON, $jsonDayStamp) {
  return end($dateDirs); // return last element in non-reversed array
}

// get most recent project data
function last_checkin_data_for_project($project_id) {
  $recentDate = last_checkin_day_for_project($project_id);
  $projectDatePath = "./projects/$project_id/checkins/$recentDate/data/";
  $jsonFiles = get_files_chrono($projectDatePath, false);
  return $projectDatePath . end($jsonFiles);
}

function html_checkin_detail($jsonFile, $project_id, $showTitle, $isMostRecent=false) {
  // load json file to data object
  $json_data = file_get_contents($jsonFile);
  $checkinJSON = json_decode($json_data, true);

  // get datetime object from filename timestamp
  $jsonFileWithoutPath = basename($jsonFile);
  $dateTimeStamp = str_replace(".json", "", $jsonFileWithoutPath);
  // format into DateTime object
  $format = 'Y-m-d-H-i-s';
  $date = DateTime::createFromFormat($format, $dateTimeStamp); // 2009-02-15 15:16:17
  // get date difference
  $now = new DateTime();
  $dateDiff = $date->diff($now);
  $monthsAgo = intval( $dateDiff->format('%m') );
  $daysAgo = intval( $dateDiff->format('%d') );
  $hoursAgo = intval( $dateDiff->format('%H') );
  $minutesAgo = intval( $dateDiff->format('%i') );
  $strAgo = "";
  // build string to describe time ago
  // echo date_format($date,"Y/m/d H:i:s") . '<br>';
  // echo date_format(new DateTime(),"Y/m/d H:i:s");
  if($monthsAgo > 0) $strAgo .= $monthsAgo . ' months, ';
  if($daysAgo > 0) $strAgo .= $daysAgo . ' days, ';
  if($hoursAgo > 0) $strAgo .= $hoursAgo . ' hours, ';
  $strAgo .= $minutesAgo . ' minutes ago';
  // echo $dateDiff->format('%Y-%m-%d %H:%i:%s') . '<br>';
  // echo $dateDiff->format('%d') . '<br>';


  // add classes for relaunch and offline
  $relaunchClass = (isset($checkinJSON['relaunch'])) ? ' relaunched' : '';
  $offlineClass = (($hoursAgo > 3 || $daysAgo > 0) && $isMostRecent == true) ? ' offline' : '';

  // build html string with json data
  $html = "" .
          '<div class="mdl-cell mdl-card mdl-shadow--2dp portfolio-card ' . $relaunchClass . $offlineClass . '">';
          if($showTitle == true) {
  $html .='  <div class="mdl-card__title">' .
          '    <h2 class="mdl-card__title-text">' . ucwords(str_replace("-", " ", $project_id)) . '</h2>' .
          '  </div>';
          } else {
  $html .='  <div class="mdl-card__title">' .
          '    <h2 class="mdl-card__title-text">' . $checkinJSON['time'] . '</h2>' .
          '  </div>';
          }
  $html .='  <div>';
          if(isset($checkinJSON['image'])) {
  $html .='  <div class="mdl-card__media">' .
          '    <img class="article-image imagexpander" src="' . $checkinJSON['image'] . '" alt="" border="0">' .
          '  </div>';
          }
          if(isset($checkinJSON['screenshot'])) {
  $html .='  <div class="mdl-card__media">' .
          '    <img class="article-image imagexpander" src="' . $checkinJSON['screenshot'] . '" alt="" border="0">' .
          '  </div>';
          }
  $html .='  </div>';
  $html .='  <div class="mdl-card__supporting-text">';
      if(isset($checkinJSON['time'])) $html .= "<strong>Updated</strong>: " . $strAgo . "<br>";
      if(isset($checkinJSON['time'])) $html .= "<strong>Update time</strong>: " . date_format($date,"Y/m/d H:i:s") . "<br>";
      if(isset($checkinJSON['uptime'])) $html .= "<strong>Uptime</strong>: " . $checkinJSON['uptime'] . "<br>";
      if(isset($checkinJSON['frameRate'])) $html .= "<strong>Framerate</strong>: " . $checkinJSON['frameRate'] . "<br>";
      if(isset($checkinJSON['frameCount'])) $html .= "<strong>Frame count</strong>: " . $checkinJSON['frameCount'] . "<br>";
      if(isset($checkinJSON['resolution'])) $html .= "<strong>Resolution</strong>: " . $checkinJSON['resolution'] . "<br>";
      if(isset($checkinJSON['relaunch'])) $html .= "<strong>App rebooted!</strong><br>";
      if(isset($checkinJSON['custom'])) {
        if(count($checkinJSON) > 0) $html .= "<strong>Custom Props:</strong><br>";
        foreach ($checkinJSON['custom'] as $key => $value) {
          $html .= "<strong>".$key."</strong>: ".$value."<br>";
        }
      }
      $html .='  </div>';
          if($showTitle) {
            $html .='<div class="mdl-card__actions mdl-card--border">';
            $html .='  <a class="mdl-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" href="./?project=' . $project_id . '">Go<span class="mdl-button__ripple-container"><span class="mdl-ripple"></span></span></a>';
            $html .='</div>';
          }
  $html .='</div>';
  return $html;
}

function remove_old_checkins($checkinsDir, $maxCheckinDays) {
  $checkinDirs = get_files_chrono($checkinsDir);
  $dirIndex = 0;
  foreach($checkinDirs as $checkinDir) {
    echo $checkinsDir . $checkinDir . "\n";
    if($dirIndex >= $maxCheckinDays) {
      rrmdir($checkinsDir . $checkinDir);
    }
    $dirIndex++;
  }
}

?>

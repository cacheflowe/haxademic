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

// get most recent project date
function last_checkin_date_for_project($projectId) {
  $dateDirs = get_files_chrono("./projects/$projectId/checkins", false);
  return end($dateDirs); // return last element in non-reversed array
}

// get most recent project data
function last_checkin_data_for_project($projectId) {
  $recentDate = last_checkin_date_for_project($projectId);
  $projectDatePath = "./projects/$projectId/checkins/$recentDate/data/";
  $jsonFiles = get_files_chrono($projectDatePath, false);
  return $projectDatePath . end($jsonFiles);
}

function html_checkin_detail($checkinJSON, $projectId, $showTitle) {
  $relaunchClass = (isset($checkinJSON['relaunch'])) ? 'relaunched' : '';
  $html = "" .
          '<div class="mdl-cell mdl-card mdl-shadow--2dp portfolio-card ' . $relaunchClass . '">';
          if($showTitle == true) {
    $html .='  <div class="mdl-card__title">' .
            '    <h2 class="mdl-card__title-text">' . ucwords($projectId) . '</h2>' .
            '  </div>';
          } else {
    $html .='  <div class="mdl-card__title">' .
            '    <h2 class="mdl-card__title-text">' . $checkinJSON['time'] . '</h2>' .
            '  </div>';
          }
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
  $html .='  <div class="mdl-card__supporting-text">';
      // if(isset($checkinJSON['time'])) echo "<li>Time: " . $checkinJSON['time'] . "</li>";
  if(isset($checkinJSON['time'])) $html .= "<strong>Last update</strong>: " . $checkinJSON['time'] . "<br>";
  if(isset($checkinJSON['uptime'])) $html .= "<strong>Uptime</strong>: " . $checkinJSON['uptime'] . "<br>";
  if(isset($checkinJSON['frameRate'])) $html .= "<strong>Framerate</strong>: " . $checkinJSON['frameRate'] . "<br>";
  if(isset($checkinJSON['frameCount'])) $html .= "<strong>Frame count</strong>: " . $checkinJSON['frameCount'] . "<br>";
  if(isset($checkinJSON['resolution'])) $html .= "<strong>Resolution</strong>: " . $checkinJSON['resolution'] . "<br>";
  if(isset($checkinJSON['relaunch'])) $html .= "<strong>App rebooted!</strong><br>";
  $html .='  </div>';
          if($showTitle) {
            $html .='<div class="mdl-card__actions mdl-card--border">';
            $html .='  <a class="mdl-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" href="./?project=' . $projectId . '">Go<span class="mdl-button__ripple-container"><span class="mdl-ripple"></span></span></a>';
            $html .='</div>';
          }
  $html .='</div>';
  return $html;
}

?>
<?php

class Dashboard {

  const HALF_HOUR = 30 * 60;        // 60 seconds * 30 minutes
  const OFFLINE_TIMEOUT = 30 * 60;  // 60 seconds * 20 minutes
  const MAX_CHECKINS = 100;

  function __construct($request, $dataPath, $maxCheckIns=Dashboard::MAX_CHECKINS, $standalone=false) {
    $this->offlineTimeout = Dashboard::OFFLINE_TIMEOUT;
    $this->restartWindow = Dashboard::HALF_HOUR;
    $this->maxCheckIns = $maxCheckIns;
    $this->standalone = $standalone;  // should we show links in cards to view details?
    $this->imageExt = "jpg";

    $this->request = $request;
    $this->dataPath = $dataPath;
    $this->projectsJsonPath = $dataPath."projects.json";
    $this->dbFileExists = file_exists($this->projectsJsonPath);
    if($this->dbFileExists == false) $this->createDb();  // if no db file exists, create it
    $this->dbFileAvailable = $this->dbFileExists && is_readable($this->projectsJsonPath) && is_writable($this->projectsJsonPath);
    // potential concurrency problem! file_exists, but JsonUtil::getJsonFromFile() returns null.
    // likely info: https://stackoverflow.com/questions/26619359/file-get-contents-fails-with-multiple-concurrent-requests
    $this->dashboardDB = JsonUtil::getJsonFromFile($this->projectsJsonPath);
  }

  function createDb() {
    $emptyDb = new stdClass();
    $emptyDb->checkins = array();
    JsonUtil::writeJsonToFile($this->projectsJsonPath, $emptyDb);
    $this->dbFileExists = true;
  }

  function storePostedCheckIn() {
    // get data from form submit & set response type as json
    $jsonPostObj = $this->request->postedJson();
    JsonUtil::setJsonOutput();

    // check for old-style data
    $this->transformDataFromOldStyle($jsonPostObj);

    if($this->dbFileAvailable == false || $this->dashboardDB == null) {
      // make sure we can read/write the database file. if we can't bail. hopefully solves an issue where the file is locked by another process and triggered a clearing of the database in `storeCheckIn()`
      $dbLoadError = "Couldn't read JSON databse, probably due to a concurrency issue. Checkin failed. Try again later.";
      error_log($dbLoadError, 0);
      JsonUtil::printFailMessage($dbLoadError);
    } else if(isset($jsonPostObj['appId'])) {
      // validate form data
      // get props to store submitted checkin to dashboard database
      $this->submittedAppId = $jsonPostObj['appId'];
      $this->projectDataPath = $this->dataPath . "projects/" . $this->submittedAppId . '/';
      $this->imageSavePath = $this->projectDataPath . "images/";
      $this->projectHistoryJsonPath = $this->projectDataPath . "history.json";
      FileUtil::makeDirs($this->imageSavePath);
      $this->storeCheckIn($jsonPostObj);

      // output success json message
      $jsonObj = new stdClass();
      $jsonObj->success = "Received checkin from ".$this->submittedAppId;
      $jsonObj->appData = $this->dashboardDB['checkins'][$this->submittedAppId];
      JSONUtil::printJsonObj($jsonObj);
    } else {
      // output fail if there wasn't a valid app id property (could be that the json was busted)
      JsonUtil::printFailMessage("No valid checkin JSON posted");
    }
  }

  function storeCheckIn($jsonPostObj) {
    // create json database properties if they dont exist
    if(isset($this->dashboardDB['checkins']) == false) {
      $this->dashboardDB['checkins'] = array();    // create outer object
    }
    if(isset($this->dashboardDB['checkins'][$this->submittedAppId]) == false) $this->dashboardDB['checkins'][$this->submittedAppId] = array();  // app-specific object

    // add custom backend props to incoming json, which gets copied to local database json
    if(isset($jsonPostObj['appUptime']))  $jsonPostObj['lastSeenApp'] = DateUtil::msSince1970();    // special secondary lastSeen if it's a web app and not the java app
    if(isset($jsonPostObj['uptime']))     $jsonPostObj['lastSeen'] =    DateUtil::msSince1970();

    // store any props from the json post to the current database node
    // but first, convert any base64 images to files & paths in json
    $this->convertImagesToFiles($jsonPostObj);
    foreach ($jsonPostObj as $key => $value) {
      $this->dashboardDB['checkins'][$this->submittedAppId][$key] = $jsonPostObj[$key];
    }

    // store database back to disk
    JsonUtil::writeJsonToFile($this->projectsJsonPath, $this->dashboardDB);

    // store individual checkin to project's json array
    // first try to get file, but use empty array if there's no valid file to load
    $projectHistoryData = array();
    if(file_exists($this->projectHistoryJsonPath) && JsonUtil::isValidJSON(file_get_contents($this->projectHistoryJsonPath))) {
      $projectHistoryData = JsonUtil::getJsonFromFile($this->projectHistoryJsonPath);
    }
    // store checkin post into history & write to disk
    // also remove old checkins & related images
    $projectHistoryData[] = $jsonPostObj;
    while(count($projectHistoryData) > $this->maxCheckIns) {
      $removedCheckIn = array_shift($projectHistoryData); // shift the oldest checkins
      // remove images
      if(isset($removedCheckIn['imageScreenshot'])) FileUtil::deleteFile($removedCheckIn['imageScreenshot']);
      if(isset($removedCheckIn['imageExtra'])) FileUtil::deleteFile($removedCheckIn['imageExtra']);
    }
    JsonUtil::writeJsonToFile($this->projectHistoryJsonPath, $projectHistoryData);
  }

  function convertImagesToFiles(&$jsonPostObj) {
    // save images & rewrite base64 image to image file path
    if(isset($jsonPostObj['imageScreenshot'])) {
      $imagePath = $this->imageSavePath . DateUtil::createTimestamp() . "-screenshot." . $this->imageExt;
      FileUtil::base64ToFile($jsonPostObj['imageScreenshot'], $imagePath);
      $jsonPostObj['imageScreenshot'] = $imagePath;
      $jsonPostObj['lastSeenScreenshot'] = DateUtil::msSince1970(); // add timestamp for image upload
    }
    if(isset($jsonPostObj['imageExtra'])) {
      $imagePath = $this->imageSavePath . DateUtil::createTimestamp() . "-extra." . $this->imageExt;
      FileUtil::base64ToFile($jsonPostObj['imageExtra'], $imagePath);
      $jsonPostObj['imageExtra'] = $imagePath;
      $jsonPostObj['lastSeenExtra'] = DateUtil::msSince1970(); // add timestamp for image upload
    }
  }

  function transformDataFromOldStyle(&$jsonPostObj) {
    // make sure we're looking at the old dashboard json
    if(isset($jsonPostObj['project']) && isset($jsonPostObj['screenshotBase64'])) {
      // migrate old 'project' key to 'appId' & 'appTitle'
      $jsonPostObj['appId'] = $jsonPostObj['project'];
      $jsonPostObj['appTitle'] = ucwords(str_replace("-", " ", $jsonPostObj['project']));
      unset($jsonPostObj['project']);

      // turn time strings into numbers - why the heck were these strings anyway?
      if(isset($jsonPostObj['frameCount'])) $jsonPostObj['frameCount'] = intval($jsonPostObj['frameCount']);
      if(isset($jsonPostObj['uptime'])) $jsonPostObj['uptime'] = intval($jsonPostObj['frameCount']) / 60; // old style was formatted clock time 00:00:00 - so, let's just divide by 60fps to get seconds uptime
      if(isset($jsonPostObj['frameRate'])) $jsonPostObj['frameRate'] = intval($jsonPostObj['frameRate']);

      // migrate images (were png - need to be saved as such)
      if(isset($jsonPostObj['screenshotBase64'])) {
        $jsonPostObj['imageScreenshot'] = $jsonPostObj['screenshotBase64'];
        unset($jsonPostObj['screenshotBase64']);
      }
      if(isset($jsonPostObj['imageBase64'])) {
        $jsonPostObj['imageExtra'] = $jsonPostObj['imageBase64'];
        unset($jsonPostObj['imageBase64']);
      }
      $this->imageExt = "png";

      // migrate custom props in own 'custom' object node
      if(isset($jsonPostObj['custom'])) {
        $customObj = $jsonPostObj['custom'];
        foreach ($customObj as $key => $val) {
          $jsonPostObj[$key] = $val;
        }
        unset($jsonPostObj['custom']);
      }

      // finally, only allow 50 checkins, since every checkin has images, which take up a lot of space on the server
      $this->maxCheckIns = 50;
    }
  }

  function checkActions() {
    if(isset($_GET['action'])) {
      $action = $_GET['action'];
      if($action == 'delete') {
        $appId = $_GET['app'];

        // remove directory with images & history
        $projectDataPath = $this->dataPath . "projects/" . $appId . '/';
        FileUtil::deleteDir($projectDataPath);

        // remove from current database & write back out
        unset($this->dashboardDB['checkins'][$appId]);
        JsonUtil::writeJsonToFile($this->projectsJsonPath, $this->dashboardDB);
      }
    }
  }

  function noResults() {
    return '<div class="dashboard-card"><span>No results.</span></div>';
  }

  function listProjects() {
    // bail if no projects.json data
    if($this->dashboardDB == null || count($this->dashboardDB['checkins']) == 0) {
      return $this->noResults();
    }

    // sort checkins
    function cmp($a, $b) {
      return strcmp($a['appId'], $b['appId']);
    }
    usort($this->dashboardDB['checkins'], "cmp");

    // display
    $html = "";
    foreach ($this->dashboardDB['checkins'] as $appKey => $checkIn) {
      $html .= $this->renderCheckInCard($checkIn);
    }
    return $html;
  }

  function listProjectCheckins($appId) {
    // override restart window
    // $this->restartWindow = 30 * 60;

    // get paths to read a project's history data
    $projectDataPath = $this->dataPath . "projects/" . $appId . '/';
    $projectHistoryJsonPath = $projectDataPath . "history.json";

    // get history json
    $html = "";
    if(JsonUtil::isValidJSON(file_get_contents($projectHistoryJsonPath))) {
      $projectHistory = JsonUtil::getJsonFromFile($projectHistoryJsonPath);

      // loop through checkin history
      $historyReverse = array_reverse($projectHistory);
      foreach ($historyReverse as $key => $checkIn) {
        $html .= $this->renderCheckInCard($checkIn, true);
      }
    }
    // if no history...
    if(strlen($html) < 2) {
      // $html = '<div class="dashboard-card"><span>No History for: '.$appId.'</span></div>';
    }
    return $html;
  }

  function renderCheckInCard($checkIn, $isDetails=false) {
    // sort the keys for custom values below
    ksort($checkIn);

    // get time since seen
    $lastSeenTimeMS = isset($checkIn['lastSeen']) ? $checkIn['lastSeen'] : 60 * 60 * 24 * 365;
    $lastSeenDateTime = DateUtil::getDateTimeFromMS($lastSeenTimeMS);
    $timeSinceLastSeen = DateUtil::timeElapsedString($lastSeenDateTime);

    // show alert if we're past the threshold of being "offline"
    $msSinceSeen = time() - $lastSeenTimeMS;
    $offlineAlert = ($msSinceSeen > $this->offlineTimeout && !$isDetails) ? " dashboard-offline" : "";                // 20 minute window to show offline indication

    // show uptime stats
    $uptimeSeconds = isset($checkIn['uptime']) ? $checkIn['uptime'] : 0;
    $uptimeClock = ($uptimeSeconds) ? DateUtil::daysAndSecondsToClockTime($uptimeSeconds) : "n/a";
    $restartedAlert = (abs($uptimeSeconds) < $this->restartWindow && $offlineAlert == "") ? " dashboard-restarted" : "";    // 30 minute window to show restarted color

    $appTitle = isset($checkIn['appTitle']) ? $checkIn['appTitle'] : $checkIn['appId'];

    // display card for app
    $html = "";
    $html .= '<div class="dashboard-card'.$offlineAlert.$restartedAlert.'">';
    // title
    if($this->standalone && $isDetails == false) {
      $html .= '  <div class="dashboard-title" title="' . $appTitle . '"><a href="?detail='.$checkIn['appId'].'">' . $appTitle . '</a></div>';
      $html .= '  <a href="?action=delete&app='.$checkIn['appId'].'" class="dashboard-card-delete">✖️</a>';
    } else {
      $html .= '  <div class="dashboard-title" title="' . $appTitle . '">' . $appTitle . '</div>';
    }
    // time info
    if($uptimeSeconds) {
      $html .= '  <div class="dashboard-info-time">';
      $html .= '    <b>Uptime</b>: '. $uptimeClock .'<br>';
      $html .= '    <b>Updated</b>: ' . $timeSinceLastSeen;
      $html .= '  </div>';
    }
    // images
    if(isset($checkIn['imageScreenshot']))  {
      $html .= '  <div class="dashboard-img-outer">';
      $html .= 'Screenshot: ' . DateUtil::timeElapsedString(DateUtil::getDateTimeFromMS($checkIn['lastSeenScreenshot'])) . '<span class="dashboard-img-container"><img data-zoomable class="imagexpander" src="'.$checkIn['imageScreenshot'].'"></span>';
      $html .= '  </div>';
    }
    if(isset($checkIn['imageExtra'])) {
      $html .= '  <div class="dashboard-img-outer">';
      $html .= 'Custom Img: ' . DateUtil::timeElapsedString(DateUtil::getDateTimeFromMS($checkIn['lastSeenExtra'])) . '<span class="dashboard-img-container"><img data-zoomable class="imagexpander" src="'.$checkIn['imageExtra'].'"></span>';
      $html .= '  </div>';
    }
    // custom values
    $html .= '    <div class="dashboard-info-custom"><b>Properties</b></div>';
    $html .= '    <div class="dashboard-custom-props">';
    foreach ($checkIn as $key => $value) {
      $val = $value;
      // special html output for custom properties
      if($key == 'lastSeenApp') {
        $val = DateUtil::timeElapsedString(DateUtil::getDateTimeFromMS($value));
      }
      if($key == 'appUptime') $val = DateUtil::secondsToClockTime($value);
      // make sure we're not showing system props, since we've already shown them
      if($key != 'appId' && $key != 'uptime' && $key != 'lastSeen' && $key != 'appTitle' && $key != 'lastSeenScreenshot' && $key != 'lastSeenExtra' && $key != 'imageScreenshot' && $key != 'imageExtra') {
        $html .= '<div class="dashboard-key" title="' . $key . '">' . $key . '</div><div class="dashboard-val" title="' . $val . '">' . $val . '</div>';
      }
    }
    $html .= '  </div>';
    $html .= '</div>';
    return $html;
  }

}

?>

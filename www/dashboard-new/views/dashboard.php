<?php

class Dashboard {

  function __construct($request, $jsonDataPath) {
    $this->request = $request;
    $this->jsonDataPath = $jsonDataPath;
    $this->dashboardDB = JsonUtil::getJsonFromFile($jsonDataPath);
  }

  function storePostedCheckIn() {
    // get data from form submit & set response type as json
    $jsonPostObj = $this->request->postedJson();
    JsonUtil::setJsonOutput();

    // validate form data
    if(isset($jsonPostObj['appId'])) {
      // get app id key & add to dashboard data if it doesn't exist yet
      $appId = $jsonPostObj['appId'];
      if(isset($this->dashboardDB['checkins']) == false) $this->dashboardDB['checkins'] = array();
      if(isset($this->dashboardDB['checkins'][$appId]) == false) $this->dashboardDB['checkins'][$appId] = array();

      // store updated props if they're in the json post
      // $this->dashboardDB['checkins'][$appId]['appId'] = $appId;
      foreach ($jsonPostObj as $key => $value) {
        $this->dashboardDB['checkins'][$appId][$key] = $jsonPostObj[$key];
      }

      // add custom backend props
      if(isset($jsonPostObj['appUptime'])) $this->dashboardDB['checkins'][$appId]['lastSeenApp'] = DateUtil::msSince1970(); // only update lastSeen if it's the web app and not the server
      if(isset($jsonPostObj['uptime'])) $this->dashboardDB['checkins'][$appId]['lastSeen'] = DateUtil::msSince1970();
      if(isset($jsonPostObj['imageScreenshot'])) $this->dashboardDB['checkins'][$appId]['lastSeenScreenshot'] = DateUtil::msSince1970();
      if(isset($jsonPostObj['imageExtra'])) $this->dashboardDB['checkins'][$appId]['lastSeenExtra'] = DateUtil::msSince1970();

      // store back to disk
      JsonUtil::writeJsonToFile($this->jsonDataPath, $this->dashboardDB);

      // output success
      $jsonObj = new stdClass();
      $jsonObj->success = "Received checkin from ".$appId;
      $jsonObj->appData = $this->dashboardDB['checkins'][$appId];
      JSONUtil::printJsonObj($jsonObj);
    } else {
      // output fail
      JsonUtil::printFailMessage("No valid checkin JSON posted");
    }
  }

  function listCards() {
    // loop through checkin objects
    $html = "";
    // sort
    function cmp($a, $b) {
      return strcmp($a['appId'], $b['appId']);
    }
    usort($this->dashboardDB['checkins'], "cmp");
    // display
    foreach ($this->dashboardDB['checkins'] as $appKey => $appData) {
      // get time since seen
      $lastSeenTimeMS = isset($appData['lastSeen']) ? $appData['lastSeen'] : 60 * 60 * 24 * 365;
      $lastSeenDateTime = DateUtil::getDateTimeFromMS($lastSeenTimeMS);
      $timeSinceLastSeen = DateUtil::timeElapsedString($lastSeenDateTime);

      // show alert if we're past the threshold of being "offline"
      $msSinceSeen = time() - $lastSeenTimeMS;
      $offlineAlert = ($msSinceSeen > 60 * 20) ? " dashboard-offline" : "";                                 // 20 minute window to show offline indication

      // show uptime stats
      $uptimeSeconds = isset($appData['uptime']) ? $appData['uptime'] : 0;
      $uptimeClock = ($uptimeSeconds) ? DateUtil::daysAndSecondsToClockTime($uptimeSeconds) : "n/a";
      $restartedAlert = ($uptimeSeconds < 60 * 30 && $offlineAlert == "") ? " dashboard-restarted" : "";    // 30 minute window to show restarted color

      $appTitle = isset($appData['appTitle']) ? $appData['appTitle'] : $appKey;

      // display card for app
      $html .= '<div class="dashboard-card'.$offlineAlert.$restartedAlert.'">';
      $html .= '  <div class="dashboard-title">' . $appTitle . '</div>';
      $html .= '  <div>';
      $html .= '    <p><b>Uptime</b>: '. $uptimeClock .'<br>';
      $html .= '      Last seen: ' . $timeSinceLastSeen .'<br>'; //  . ' ('.$msSinceSeen.'s)</p>';
      if(isset($appData['imageScreenshot']))  $html .= 'Screenshot: ' . DateUtil::timeElapsedString(DateUtil::getDateTimeFromMS($appData['lastSeenScreenshot'])) . '<span class="dashboard-img-container"><img data-zoomable class="imagexpander" src="data:image/jpeg;base64,'.$appData['imageScreenshot'].'"></span>';
      if(isset($appData['imageExtra']))       $html .= 'Custom Img: ' . DateUtil::timeElapsedString(DateUtil::getDateTimeFromMS($appData['lastSeenExtra'])) . '<span class="dashboard-img-container"><img data-zoomable class="imagexpander" src="data:image/jpeg;base64,'.$appData['imageExtra'].'"></span>';
      $html .= '    </p>';
      $html .= '    <p><b>Custom Values</b><br>';
      foreach ($appData as $key => $value) {
        $val = $value;
        // special html output for custom properties
        if($key == 'lastSeenApp') {
          $val = DateUtil::timeElapsedString(DateUtil::getDateTimeFromMS($value));
        }
        if($key == 'appUptime') $val = DateUtil::secondsToClockTime($value);
        // make sure we're not showing system props, since we've already shown them
        if($key != 'appId' && $key != 'uptime' && $key != 'lastSeen' && $key != 'appTitle' && $key != 'lastSeenScreenshot' && $key != 'lastSeenExtra' && $key != 'imageScreenshot' && $key != 'imageExtra') {
          $html .= $key . ': ' . $val . '<br>';
        }
      }
      // if(isset($appData['uptime'])) $html .= '    Server Uptime: ' . DateUtil::secondsToClockTime($appData['uptime']) . '<br>';
      $html .= '    </p>';
      $html .= '  </div>';
      $html .= '</div>';
    }
    return $html;
  }

}

?>

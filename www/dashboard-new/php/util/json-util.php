<?php

class JsonUtil {

    public static function isValidJSON($str) {
      if(strlen($str) == 0) return false;
      json_decode($str);
      return json_last_error() == JSON_ERROR_NONE;
    }

    public static function getJsonFromFile($path) {
      $jsonStr = file_get_contents($path);
      return json_decode($jsonStr, true);
    }

    public static function writeJsonToFile($path, $data) {
      file_put_contents($path, json_encode($data, JSON_PRETTY_PRINT | JSON_NUMERIC_CHECK));
    }

    public static function jsonDataObjToString($data) {
      return json_encode($data, JSON_PRETTY_PRINT | JSON_NUMERIC_CHECK);
    }

    public static function setJsonOutput() {
      // don't return html fragment (requires Simplesite)
      global $request;
      $request->setAPI(true);

      // set headers to return json data type
      header('Content-Type: application/json');
      header('Access-Control-Allow-Origin: *');
    }

    public static function printJsonObj($jsonObj) {
      print(JSONUtil::jsonDataObjToString($jsonObj));
    }

    public static function printSuccessMessage($msg) {
      $jsonObj = new stdClass();
      $jsonObj->success = $msg;
      JSONUtil::printJsonObj($jsonObj);
    }

    public static function printFailMessage($msg) {
      $jsonObj = new stdClass();
      $jsonObj->fail = $msg;
      JSONUtil::printJsonObj($jsonObj);
    }

    public static function backupJsonFile($filePath) {
      // result: moves /data/json/boulder-1.json to /data/json/backup/boulder-1-2019-09-05.json
      $path = FileUtil::dirFromPath($filePath);
      $fileName = FileUtil::fileNameFromPath($filePath);
      $fileNameBackup = $path . "/backup/" . str_replace(".json", '-' . DateUtil::createTimestamp() . ".json", $fileName);
      FileUtil::makeDirs(dirname($fileNameBackup));
      return copy($filePath, $fileNameBackup);
    }
}

?>

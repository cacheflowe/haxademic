<?php

class FileUtil {

    // create dir
    public static function makeDirs($dirpath, $mode=0777) {
      return is_dir($dirpath) || mkdir($dirpath, $mode, true);
    }

    // rm -rf
    public static function deleteDir($dir) {
      if (is_dir($dir)) {
        $objects = scandir($dir);
        foreach ($objects as $object) {
          if ($object != "." && $object != "..") {
            if (is_dir($dir."/".$object))
              FileUtil::deleteDir($dir."/".$object);
            else
              unlink($dir."/".$object);
          }
        }
        rmdir($dir);
      }
    }

    public static function deleteFile($filePath) {
      if(file_exists($filePath)) {
        unlink($filePath);
      }
    }

    // convert base64 image to file
    public static function base64ToFile($base64_string, $output_file) {
      // open the output file for writing
      $ifp = fopen( $output_file, 'wb' );
      fwrite( $ifp, base64_decode( $base64_string ) );
      // clean up the file resource
      fclose( $ifp );
      // return $output_file;
    }

    public static function getFilesSorted($path, $reverse=true) {
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

    public static function getDirectoryTree($outerDir, $level, &$files){
        $dirs = array_diff( scandir( $outerDir ), Array( ".", ".." ) );
        $dir_array = Array();
        foreach ($dirs as $dir) {
            if (is_dir($outerDir . "/"  . $dir)) {
                $dir_array[$dir] = FileUtil::getDirectoryTree( $outerDir . "/" . $dir, $level+1, $files);
            } else {
                $files[] = $outerDir . "/" . $dir;  // add to single files array
                $dir_array[$dir] = $dir;
            }
        }
        return $dir_array;
    }

    public static function fileSizeFromBytes($bytes) {
      // primarily would come from file upload info
      $kb = $bytes / 1024;
      if($kb < 1000) {
        return round($kb) . "kb";
      } else {
        return round($kb / 1024, 1) . "mb";
      }
    }

    public static function fileSizeFromPath($filePath) {
      return FileUtil::fileSizeFromBytes(filesize($filePath));
    }

    public static function fileCreatedTime($filePath) {
      return date("F d Y H:i:s", filemtime($filePath));
    }

    public static function fileExtension($filePath) {
      return pathinfo($filePath, PATHINFO_EXTENSION);
    }

    public static function fileNameFromPath($filePath) {
      return basename($filePath);
    }

    public static function dirFromPath($filePath) {
      return dirname($filePath);
    }

    public static function filesAreIdentical($filePath1, $filePath2) {
      if( !file_exists($filePath1)) return false;
      if( !file_exists($filePath2)) return false;
      if (filetype($filePath1) !== filetype($filePath2)) return false;
      if (filesize($filePath1) !== filesize($filePath2)) return false;
      if (! $fp1 = fopen($filePath1, 'rb')) return false;
      if (! $fp2 = fopen($filePath2, 'rb')) {
          fclose($fp1);
          return false;
      }
      $same = true;
      while (! feof($fp1) and ! feof($fp2))
          if (fread($fp1, 4096) !== fread($fp2, 4096))
          {
              $same = false;
              break;
          }
      if (feof($fp1) !== feof($fp2)) $same = false;
      fclose($fp1);
      fclose($fp2);
      return $same;
    }

}

?>

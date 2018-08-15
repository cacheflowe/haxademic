<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible" />
    <meta content="no" name="imagetoolbar" />
    <!-- 
    <meta name="viewport" content="<?php // echo $metadata->get_viewport(); ?>" />
    <link rel="apple-touch-icon-precomposed" href="<?php // echo $metadata->get_favicon(); ?>">
    <link rel="apple-touch-startup-image" href="<?php // echo $metadata->get_favicon(); ?>">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <meta name="apple-mobile-web-app-title" content="<?php // echo $metadata->get_appTitle(); ?>">
    <meta name="format-detection" content="telephone=no">
    <link rel="shortcut icon" type="image/x-icon" href="<?php // echo $metadata->get_favicon(); ?>">
    -->
    <title>Big Mother</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
  </head>
  <body>
    <?php
      if(isset($_GET['project']) && isset($_GET['date'])) {
        include './views/project-date-details.php'; 
      } else if(isset($_GET['project'])) {
        include './views/list-project-dates.php'; 
      } else {
        include './views/projects-list.php'; 
      }
    ?>
    <!-- <script src="./js/vendor/embetter.js"></script> -->
  </body>
</html>
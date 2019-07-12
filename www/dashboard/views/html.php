<?php
  // Simple password protection
  $loggedIn = false;
  $pass = 'YOUR_PASSWORD';
  if (isset($_COOKIE['password']) && $_COOKIE['password'] === $pass) {
    $loggedIn = true;
  }
  if (isset($_POST['password']) && $_POST['password'] == $pass) {
    setcookie("password", $pass, strtotime('+30 days'));
    $loggedIn = true;
  }
?>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible" />
    <meta content="no" name="imagetoolbar" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Project Dashboard</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-green.min.css">
    <!-- https://getmdl.io/components/index.html -->
    <style>
      body {
        background: #222;
        color: #eee;
      }
      .mdl-card {
        min-height: 0;
      }
      .mdl-card__media {
        background-color: #000;
        height: 17vh;
        overflow: hidden;
        width: 100%;
        text-align: center;
        padding: 1rem;
        box-sizing: border-box;
        background: #555;
      }
      .mdl-card__media:first-child {
        padding-bottom: 0.5rem;
      }
      .mdl-card__media:last-child {
        padding-top: 0.5rem;
      }
      .mdl-card__media img {
        width: 100%;
        max-height: 100%;
        object-fit: contain;
      }
      @media (min-width: 1000px) {
        .mdl-cell {
          width: calc(20% - 16px);
        }
      }
      .mdl-cell-full-width {
        width: 100%;
      }
      .relaunched {
        background-color: #99ff99;
      }
      .offline {
        background-color: #ff9999;
      }
      /* lightbox */
      .lightbox,.lightbox-image-holder{width:100%;height:100%;box-sizing:border-box}.lightbox{position:fixed;top:0;left:0;background:rgba(255,255,255,.9);z-index:9999;opacity:0;transition:opacity .2s linear}.lightbox.showing{opacity:1}.lightbox-image-holder{position:relative;background-position:center;background-size:contain;background-repeat:no-repeat}.lightbox-image-holder.lightbox-image-contained{background-size:auto}.imagexpander,.lightbox,.lightbox-image-holder,img[rel]{cursor:pointer}
    </style>
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
  </head>
  <body>
    <div class="mdl-layout__container">
      <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header has-drawer is-upgraded">
        <!-- HEADER -->
        <header class="mdl-layout__header mdl-layout__header--transparent">
          <div class="mdl-layout__header-row">
            <!-- Title -->
            <span class="mdl-layout-title">Project Dashboard</span>
            <!-- Add spacer, to align navigation to the right -->
            <div class="mdl-layout-spacer"></div>
            <!-- Navigation -->
            <nav class="mdl-navigation">
              <span class="mdl-navigation__link"><?php echo date('H:i:s') ?></span>
              <a class="mdl-navigation__link" href="./">Home</a>
            </nav>
          </div>
        </header>
        <div class="mdl-layout__drawer">
          <span class="mdl-layout-title">Project Dashboard</span>
          <nav class="mdl-navigation">
            <a class="mdl-navigation__link" href="./">Home</a>
            <hr>
            <?php
              if($loggedIn == true) {
                $projectDirs = get_files_chrono("./projects", false);
                foreach($projectDirs as $projectId) {
                  echo '<a class="mdl-navigation__link" href="./?project=' . $projectId . '">' . ucwords(str_replace("-", " ", $projectId)) . '</a>';
                }
              }
            ?>
          </nav>
        </div>

        <!-- CONTENT -->
        <main class="mdl-layout__content">
          <?php
            if($loggedIn == true) {
              if(isset($_GET['project']) && isset($_GET['date'])) {
                include './views/project-date-details.php';
              } else if(isset($_GET['project'])) {
                include './views/project-dates-list.php';
              } else {
                include './views/projects-list.php';
              }
            } else {
          ?>
          <div class="mdl-grid portfolio-max-width">
            <div class="mdl-cell mdl-card mdl-shadow--2dp portfolio-card">
            <div class="mdl-card__title">
              <form action="#" method="POST">
                <div class="mdl-textfield mdl-js-textfield">
                  <input class="mdl-textfield__input" type="text" name="password" id="password">
                  <label class="mdl-textfield__label" for="password">Password</label>
                </div>
              </form>
            </div>
            </div>
          </div>
          <?php
            }
          ?>
        </main>
      </div>
    </div>
    <script>
      // lightbox min
      var _createClass=function(){function t(t,e){for(var i=0;i<e.length;i++){var n=e[i];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(t,n.key,n)}}return function(e,i,n){return i&&t(e.prototype,i),n&&t(e,n),e}}();function _classCallCheck(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}var Lightbox=function(){function t(){var e=this;_classCallCheck(this,t),this.lightboxDiv=null,this.lightboxImgUrl=null,this.lightboxImageLoader=null,this.active=!1,document.addEventListener("click",function(t){return e.hideLightbox(t)}),document.addEventListener("keyup",function(t){return e.checkEscClose(t)}),window.addEventListener("scroll",function(t){return e.hideLightbox(t)}),document.addEventListener("click",function(t){return e.checkDocumentClick(t)})}return _createClass(t,[{key:"closest",value:function(t,e){for(e=e.toLowerCase();;){if(t.nodeName.toLowerCase()===e)return t;if(!(t=t.parentNode))break}return null}},{key:"checkDocumentClick",value:function(t){var e=this.closest(t.target,"a");e&&"lightbox"==e.getAttribute("rel")&&(t.preventDefault(),this.handleLightboxLink(e.href));var i=this.closest(t.target,"img");i&&i.classList.contains("imagexpander")&&(t.preventDefault(),this.handleLightboxLink(i.getAttribute("src")))}},{key:"handleLightboxLink",value:function(t){var e=this;this.lightboxImgUrl=t,this.lightboxImageLoader=new Image,this.lightboxImageLoader.addEventListener("load",function(t){return e.lightboxImageLoaded(t)}),this.lightboxImageLoader.src=this.lightboxImgUrl}},{key:"lightboxImageLoaded",value:function(){var t=this,e=this.lightboxImageLoader.height<window.innerHeight-40&&this.lightboxImageLoader.width<window.innerWidth-40?"lightbox-image-contained":"";this.lightboxDiv=document.createElement("div"),this.lightboxDiv.className="lightbox",this.lightboxDiv.innerHTML='<div class="lightbox-image-holder '+e+'" style="background-image:url('+this.lightboxImgUrl+')"></div>',document.body.appendChild(this.lightboxDiv),this.active=!0,requestAnimationFrame(function(){t.lightboxDiv.className="lightbox",requestAnimationFrame(function(){t.lightboxDiv.className="lightbox showing"})})}},{key:"checkEscClose",value:function(t){27==t.keyCode&&this.hideLightbox()}},{key:"hideLightbox",value:function(t){var e=this;this.active&&this.lightboxDiv&&(this.active=!1,this.lightboxDiv.className="lightbox",setTimeout(function(){document.body.removeChild(e.lightboxDiv)},300))}}]),t}();
      var lightbox = new Lightbox();
      // auto-refresh
      window.addEventListener('offline', function() { console.log('offline'); });
      window.addEventListener('online', function() { console.log('online'); });
      setTimeout(function() {
        if(window.navigator.onLine) {
          window.location.reload()
        }
      }, 60 * 1000);
    </script>
    <!-- <script src="./js/vendor/embetter.js"></script> -->
  </body>
</html>

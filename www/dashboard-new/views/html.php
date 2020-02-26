<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible" />
    <meta content="no" name="imagetoolbar" />
    <link rel="shortcut icon" type="image/x-icon" href="icon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title><?php echo $constants['dashboardTitle']; ?></title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/dashboard-local.css">
    <link rel="stylesheet" href="css/lightbox.css">
  </head>
  <body>
    <!-- HEADER -->
    <header>
      <span class="main-title"><a href="./"><?php echo $constants['dashboardTitle']; ?></a><?php
        if(isset($_GET['detail'])) {
          print('<b>'.$_GET['detail'].'</b>');
        }
      ?></span>
      <nav class="main-navigation">
        <span><?php echo date('h:i A') ?></span>
        <?php if(Login::isLoggedIn() == true) { ?>
        <a href="./?logout=true">Logout</a>
        <?php } ?>
      </nav>
    </header>

    <!-- DASHBOARD CONTENT -->
    <div class="dashboard-items">
      <?php
        if(Login::isLoggedIn() == true) {
          $dashboard->checkActions();
          if(isset($_GET['detail'])) {
            print($dashboard->listProjectCheckins($_GET['detail']));
          } else {
            print($dashboard->listProjects());
          }
        } else {
      ?>
      <div class="dashboard-card">
        <form action="#" method="POST">
          <label for="password">Log In</label>
          <input type="password" name="password" id="password" placeholder="Password">
        </form>
      </div>
      <?php
        }
      ?>
    </div>

    <!-- Javascripts -->
    <script>
      // lightbox min
      var _createClass=function(){function t(t,e){for(var i=0;i<e.length;i++){var n=e[i];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(t,n.key,n)}}return function(e,i,n){return i&&t(e.prototype,i),n&&t(e,n),e}}();function _classCallCheck(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}var Lightbox=function(){function t(){var e=this;_classCallCheck(this,t),this.lightboxDiv=null,this.lightboxImgUrl=null,this.lightboxImageLoader=null,this.active=!1,document.addEventListener("click",function(t){return e.hideLightbox(t)}),document.addEventListener("keyup",function(t){return e.checkEscClose(t)}),window.addEventListener("scroll",function(t){return e.hideLightbox(t)}),document.addEventListener("click",function(t){return e.checkDocumentClick(t)})}return _createClass(t,[{key:"closest",value:function(t,e){for(e=e.toLowerCase();;){if(t.nodeName.toLowerCase()===e)return t;if(!(t=t.parentNode))break}return null}},{key:"checkDocumentClick",value:function(t){var e=this.closest(t.target,"a");e&&"lightbox"==e.getAttribute("rel")&&(t.preventDefault(),this.handleLightboxLink(e.href));var i=this.closest(t.target,"img");i&&i.classList.contains("imagexpander")&&(t.preventDefault(),this.handleLightboxLink(i.getAttribute("src")))}},{key:"handleLightboxLink",value:function(t){var e=this;this.lightboxImgUrl=t,this.lightboxImageLoader=new Image,this.lightboxImageLoader.addEventListener("load",function(t){return e.lightboxImageLoaded(t)}),this.lightboxImageLoader.src=this.lightboxImgUrl}},{key:"lightboxImageLoaded",value:function(){var t=this,e=this.lightboxImageLoader.height<window.innerHeight-40&&this.lightboxImageLoader.width<window.innerWidth-40?"lightbox-image-contained":"";this.lightboxDiv=document.createElement("div"),this.lightboxDiv.className="lightbox",this.lightboxDiv.innerHTML='<div class="lightbox-image-holder '+e+'" style="background-image:url('+this.lightboxImgUrl+')"></div>',document.body.appendChild(this.lightboxDiv),this.active=!0,requestAnimationFrame(function(){t.lightboxDiv.className="lightbox",requestAnimationFrame(function(){t.lightboxDiv.className="lightbox showing"})})}},{key:"checkEscClose",value:function(t){27==t.keyCode&&this.hideLightbox()}},{key:"hideLightbox",value:function(t){var e=this;this.active&&this.lightboxDiv&&(this.active=!1,this.lightboxDiv.className="lightbox",setTimeout(function(){document.body.removeChild(e.lightboxDiv)},300))}}]),t}();
      var lightbox = new Lightbox();

      // auto-refresh every minute
      window.addEventListener('offline', function() { console.log('offline'); });
      window.addEventListener('online', function() { console.log('online'); });
      setTimeout(function() {
        if(window.navigator.onLine) {
          window.location.href = window.location.origin + window.location.pathname;
        }
      }, 60 * 1000);

      // if there was an action in the url params, refresh immediately to strip them away
      if(!!document.location.href.match(/\?(logout|action)/)) {
        window.location.href = window.location.origin + window.location.pathname;
      }

      // confirm delete clicks
      document.body.addEventListener('click', function(e) {
        if(e.target && e.target.classList.contains('dashboard-card-delete')) {
          var result = confirm("Delete this project?!\nNo worries - the next check-in will recreate it.");
          if(result == false) e.preventDefault();
        }
      });
    </script>
  </body>
</html>

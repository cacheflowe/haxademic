class GATracking {

  constructor(gaID=null) {
    if(gaID != null) {
      if(gaId.indexOf('UA-') != -1) console.warn('Please only use the numeric GA tracking id');
      // https://developers.google.com/analytics/devguides/collection/analyticsjs/
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
      ga('create', `UA-${gaID}`, 'auto');
      ga('send', 'pageview');
    }
  }

  event(category='test', action='click') {
    // More info: https://developers.google.com/analytics/devguides/collection/analyticsjs/events
    window.ga('send', 'event', category, action);
  }

  page(path=document.location.pathname) {
    // More info: https://developers.google.com/analytics/devguides/collection/analyticsjs/pages
    // More info: https://developers.google.com/analytics/devguides/collection/analyticsjs/single-page-applications
    window.ga('set', 'page', path); // sets the page for a single-page app, so subsequent events are tracked to this page
    window.ga('send', 'pageview');
  }

}

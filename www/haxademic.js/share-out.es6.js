class ShareOut {

  constructor() {

  }

  closest(element, tagname) {
    tagname = tagname.toLowerCase();
    while (true) {
      if (element.nodeName.toLowerCase() === tagname) return element;
      if (!(element = element.parentNode)) break;
    }
    return null;
  }

  clickedShareContainer(e) {
    e.preventDefault();
    let clickedEl = this.closest(e.target, 'a') || this.closest(e.target, 'button');
    if(clickedEl) {
      this.sharePostToService(clickedEl.getAttribute('data-network'), clickedEl.getAttribute('href'));
    }
  }

  sharePostToService(network, url) {
    let height, width;
    if (network === 'email') {
      return this.sharePostToEmail(url);
    }
    width = ShareOut.networkShareDimensions[network].width || 700;
    height = ShareOut.networkShareDimensions[network].height || 450;
    // document.getElementById('share-out-frame').src = url;
    return window.open(url, "_blank", "width=" + width + ", height=" + height + ", left=" + (window.innerWidth / 2 - width / 2) + ", top=" + (window.innerHeight / 2 - height / 2) + ", toolbar=0, location=0, menubar=0, directories=0, scrollbars=0");
  }

  sharePostToEmail(url) {
    document.location.href = url;
  }

  setShareLinks(container, url, summary, img) {
    var emailBody, emailSummary, emailSummarySafe, encodedLinebreak, summaryArr, summarySafe, tweetSummary, tweetSummarySafe, urlSafe;
    encodedLinebreak = "%0D%0A";
    summarySafe = window.encodeURIComponent(summary.trim());
    urlSafe = (url && url.length > 0) ? window.encodeURIComponent(url) : "";

    emailSummary = summary;
    emailSummarySafe = window.encodeURIComponent(emailSummary);
    emailBody = "" + summarySafe + encodedLinebreak + encodedLinebreak + urlSafe;
    tweetSummary = summary;
    if (tweetSummary.length + urlSafe.length > 139) {
      tweetSummary = tweetSummary.substr(0, 139 - 24);
      summaryArr = tweetSummary.split(' ');
      summaryArr.pop();
      tweetSummary = summaryArr.join(' ');
    }
    tweetSummarySafe = window.encodeURIComponent(tweetSummary);

    if(container.querySelector('[data-network="email"]'))
      container.querySelector('[data-network="email"]').setAttribute('href', "mailto:?subject=" + emailSummarySafe + "&body=" + emailBody);
    if(container.querySelector('[data-network="facebook"]')) {
      var fbEl = container.querySelector('[data-network="facebook"]');
      if(fbEl.getAttribute('data-facebook-quote') != null) {
        fbEl.setAttribute('href', "https://www.facebook.com/sharer/sharer.php?quote="+summarySafe+"&u=" + urlSafe);
      } else {
        fbEl.setAttribute('href', "https://www.facebook.com/sharer/sharer.php?u=" + urlSafe);
      }
    }
    // container.querySelector('[data-network="facebook"]').setAttribute('href', "http://www.facebook.com/sharer.php?s=100&p[title]" + summarySafe + "&p[url]=" + urlSafe + "&p[images][0]=" + img);
    // container.querySelector('[data-network="facebook"]').setAttribute('href', "https://www.facebook.com/dialog/feed?display=popup&caption="+summarySafe+"&link="+urlSafe);
    if(container.querySelector('[data-network="twitter"]'))
      container.querySelector('[data-network="twitter"]').setAttribute('href', "https://twitter.com/intent/tweet?url=" + urlSafe + "&text=" + tweetSummarySafe);
    if(container.querySelector('[data-network="pinterest"]'))
      container.querySelector('[data-network="pinterest"]').setAttribute('href', "http://pinterest.com/pin/create/button/?url=" + urlSafe + "&description=" + summarySafe + "&media=" + img);
    if(container.querySelector('[data-network="googleplus"]'))
      container.querySelector('[data-network="googleplus"]').setAttribute('href', "https://plus.google.com/share?url=" + urlSafe);
    if(container.querySelector('[data-network="tumblr"]'))
      container.querySelector('[data-network="tumblr"]').setAttribute('href', "http://www.tumblr.com/share/link?posttype=link&url=" + urlSafe + "&title=" + summarySafe + "&content=" + summarySafe + "&caption=" + summarySafe);
       // container.querySelector('[data-network="reddit"]').setAttribute('href', "http://reddit.com/submit?url=" + urlSafe + "&title=" + summarySafe);
    if(container.querySelector('[data-network="linkedin"]'))
      container.querySelector('[data-network="linkedin"]').setAttribute('href', "http://www.linkedin.com/shareArticle?mini=true&url=" + urlSafe + "&title=" + summarySafe);
    if(container.querySelector('[data-network="yammer"]'))
      container.querySelector('[data-network="yammer"]').setAttribute('href', "https://www.yammer.com/messages/new?login=true&status=" + summarySafe + ": " + urlSafe);

    // bind for listener removal
    this.clickHandler = this.clickedShareContainer.bind(this);
    container.addEventListener('click', this.clickHandler);
  }

  disposeShareLinks(container) {
    container.removeEventListener('click', this.clickHandler);
    this.clickHandler = null;
  }
}

ShareOut.networkShareDimensions = {
  facebook:   { width: 480, height: 210 },
  twitter:    { width: 550, height: 420 },
  pinterest:  { width: 750, height: 320 },
  googleplus: { width: 500, height: 385 },
  tumblr:     { width: 450, height: 430 },
  reddit:     { width: 540, height: 420 },
  linkedin:   { width: 550, height: 460 },
  yammer:     { width: 600, height: 350 }
};

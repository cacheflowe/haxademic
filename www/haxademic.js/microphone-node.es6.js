class MicrophoneNode {

  constructor(context, callback) {
    navigator.mediaDevices.getUserMedia({audio: true})
    .then(function(stream) {
      let source = context.createMediaStreamSource(stream);
      window.source = source; // fix for FF bug: https://stackoverflow.com/questions/22860468/html5-microphone-capture-stops-after-5-seconds-in-firefox
      callback(source);
    })
    .catch(function(err) {
      console.log('The following gUM error occured: ' + err);
    });
  }
}

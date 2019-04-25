class Sounds {

  constructor() {
    _store.addListener(this);
    this.sounds = {};
    // this.buildAudioToggleButton();
    this.loadSounds();
    this.initMute();
  }

  // buildAudioToggleButton() {
  //   this.toggleButton = document.getElementById('audio-toggle');
  //   this.toggleButton.addEventListener('click', (e) => this.toggleMute(e));
  // }

  loadSounds() {
    for(var key in Sounds.audioFiles) {
      let fileName = Sounds.audioFiles[key];
      this.sounds[fileName] = new Howl({src:[fileName]});
    }
  }

  initMute() {
    this.muted = false;
    if(window.localStorage.getItem("audio-mute") == "true") {
      this.toggleMute();
    }
  }

  toggleMute(e) {
    this.muted = !this.muted;
    Howler.mute(this.muted);

    if(this.muted == false) {
      window.localStorage.setItem("audio-mute", "false");
      document.body.classList.remove('audio-muted');
    } else {
      window.localStorage.setItem("audio-mute", "true");
      document.body.classList.add('audio-muted');
    }
  }

  getSound(soundId) {
    return this.sounds[soundId];
  }

  playSound(soundId) {
    this.sounds[soundId].stop().play();
  }

  storeUpdated(key, value) {
    // request specific sound
    if(key == Sounds.PLAY_SOUND) this.playSound(value);
  }

  playSoundtrack() {
    if(this.menuSoundtrackAdded) return;
    this.menuSoundtrackAdded = true;

    if(MobileUtil.isMobileBrowser()) {
      document.body.addEventListener('touchstart', this.startIntroLoop());
    } else {
      this.startIntroLoop();
    }
  }

  startIntroLoop() {
    if(this.introLoopStarted) return;
    this.introLoopStarted = true;

    let fileName = Sounds.audioFiles.MENU_LOOP;
    this.sounds[fileName] = new Howl({src:[fileName], volume:0, loop:true});
    this.playSound(fileName);
    this.getSound(fileName).fade(0, 0.75, 750);
  }

}

Sounds.PLAY_SOUND = 'PLAY_SOUND';
Sounds.audioFiles = {
  MENU_LOOP:        './audio/loop.mp3',
}

// dependencies:
// - ArrayUtil.crossfadeEnds()
// - FloatBuffer

class SoundFFT {

  constructor(context, audioNode) { // Howler.ctx, sound._sounds[0]._node
    this.context = context;
    this.audioNode = audioNode;
    this.debug = false;
    // send sound node to analyser. the main destination output remains intact
    this.analyser = this.context.createAnalyser();
    this.analyser.fftSize = 512;
    this.analyser.smoothingTimeConstant = 0.1;
    this.audioNode.connect(this.analyser);

    // build audio data array
    this.binCount = this.analyser.frequencyBinCount;
    this.spectrumData = new Uint8Array(this.binCount);
    this.spectrum = new Array(this.binCount);
    this.spectrumCrossfaded = new Array(this.binCount);
    for(var i=0; i < this.binCount; i++) this.spectrum[i] = 0; // reset to zero

    this.waveformData = new Uint8Array(this.binCount);
    this.waveform = new Array(this.binCount);
    for(var i=0; i < this.binCount; i++) this.waveform[i] = 0; // reset to zero

    // spectrum decay
    this.freqDecay = 0.98;

    // beat detect
    this.detectedBeat = false;
    this.beatCutOff = 0;
    this.beatLastTime = 0;
    this.beatHoldTime = 300;     // num frames to hold a beat
    this.beatDecayRate = 0.97;
    this.beatMin = 0.15; //level less than this is no beat
    this.avgAmp = 0;
    this.ampDir = new FloatBuffer(5);
  }

  setDebug(isDebug) {
    this.debug = isDebug;
  }

  getSpectrum() {
    return this.spectrum;
  }

  getWaveform() {
    return this.waveform;
  }

  getDetectedBeat() {
    return this.detectedBeat;
  }

  update() {
    if(this.analyser) {
      // get raw data
      this.analyser.getByteFrequencyData(this.spectrumData);
      this.analyser.getByteTimeDomainData(this.waveformData);
      // turn it into usable floats: 0-1 for spectrum and -1 to 1 for waveform data
      this.normalizeSpectrumFloats(false);
      this.normalizeWaveformFloats();
      this.calcAverageLevel();
      this.detectBeats();
      ArrayUtil.crossfadeEnds(this.waveform, 0.05);
      if(this.debug) this.drawDebug();
    }
  }

  normalizeSpectrumFloats(crossfadeEnds) {
    if(crossfadeEnds == false) {
      for(var i = 0; i < this.spectrumData.length; i++) {
        let curFloat = this.spectrumData[i] / 255;
        this.spectrum[i] = Math.max(curFloat, this.spectrum[i] * this.freqDecay); // lerp decay
      }
    } else {
      // create temp crossfaded array without decay
      for(var i = 0; i < this.spectrumData.length; i++) {
        let curFloat = this.spectrumData[i] / 255;
        this.spectrumCrossfaded[i] = curFloat;
      }
      ArrayUtil.crossfadeEnds(this.spectrumCrossfaded, 0.5);
      // use crossfaded array to decay
      for(var i = 0; i < this.spectrumCrossfaded.length; i++) {
        let curFloat = this.spectrumCrossfaded[i];
        this.spectrum[i] = Math.max(curFloat, this.spectrum[i] * this.freqDecay); // lerp decay
      }
    }
  }

  normalizeWaveformFloats() {
    for(var i = 0; i < this.waveformData.length; i++) {
      this.waveform[i] = 2 * (this.waveformData[i] / 255 - 0.5);
    }
  }

  calcAverageLevel() {
    let lastAmp = this.avgAmp;
    let ampSum = 0;
    for(var i = 0; i < this.spectrum.length; i++) {
      ampSum += this.spectrum[i];
    }
    this.avgAmp = ampSum / this.spectrum.length;
    this.ampDir.update(this.avgAmp - lastAmp);
  }

  detectBeats() {
    if(this.avgAmp > this.beatCutOff && this.avgAmp > this.beatMin && this.beatDetectAvailable() && this.ampDir.average() > 0.01) {
      this.detectedBeat = true;
      this.beatCutOff = this.avgAmp * 1.1;
      this.beatLastTime = Date.now();
    } else {
      this.detectedBeat = false;
      if(this.beatDetectAvailable()){
        this.beatCutOff *= this.beatDecayRate;
        this.beatCutOff = Math.max(this.beatCutOff, this.beatMin);
      }
    }
  }

  beatDetectAvailable() {
    return Date.now() > this.beatLastTime + this.beatHoldTime;
  }

  resetSpectrum() {
    // bring spectrum back down to zero after song ends
    if(this.spectrum) {
      for (var i = 0; i < this.spectrum.length; i++) {
        if(this.spectrum[i] > 0) {
          this.spectrum[i] = this.spectrum[i] - 1;
        }
      }
    }
  }

  buildDebugCanvas() {
    // debug params
    this.debugW = 200;
    this.debugH = 140;
    this.fftH = this.debugH * 2/5;
    this.debugWhite = '#fff';
    this.debugGreen = 'rgba(0, 255, 0, 1)';
    this.debugBlack = '#000';
    this.clearColor = 'rgba(0, 0, 0, 0)';

    // build canvas
    this.canvas = document.createElement('canvas');
    this.canvas.width = this.debugW;
    this.canvas.height = this.debugH;
    this.canvas.setAttribute('style', 'position:absolute;bottom:0;right:0;z-index:9999');
    document.body.appendChild(this.canvas);

    // setup
    this.ctx = this.canvas.getContext('2d');
    this.ctx.fillStyle = this.debugBlack;
    this.ctx.strokeStyle = this.debugWhite;
    this.ctx.lineWidth = 2;
  }

  drawDebug() {
    if(this.ctx == null) this.buildDebugCanvas();

    // background
    this.ctx.fillStyle = this.debugBlack;
    this.ctx.strokeStyle = this.debugWhite;
    this.ctx.fillRect(0, 0, this.debugW, this.debugH);
    this.ctx.strokeRect(0, 0, this.debugW, this.fftH);
    this.ctx.strokeRect(0, this.fftH, this.debugW, this.fftH);
    this.ctx.strokeRect(0, this.fftH * 2, this.debugW, this.fftH/2);

    // draw spectrum bars
    var barWidth = this.debugW / this.binCount;
    this.ctx.fillStyle = this.debugWhite;
    this.ctx.lineWidth = barWidth;
    for(var i = 0; i < this.binCount; i++) {
      this.ctx.fillRect(i * barWidth, this.fftH, barWidth, -this.spectrum[i] * this.fftH);
    }

    // draw waveform
    this.ctx.strokeStyle = this.debugWhite;
    this.ctx.beginPath();
    for(var i = 0; i < this.binCount; i++) {
      this.ctx.lineTo(i * barWidth, this.fftH * 1.5 + this.waveform[i] * this.fftH / 2);
    }
    this.ctx.stroke();

    // beat detection
    this.ctx.fillStyle = this.debugWhite;
    if (this.beatDetectAvailable() == false && Date.now() < this.beatLastTime + 200){
      this.ctx.fillStyle = this.debugGreen;
    }
    this.ctx.fillRect(0, this.fftH * 2, this.debugW * this.avgAmp, this.fftH / 2);

    // beat detect cutoff
    this.ctx.fillRect(this.debugW * this.beatCutOff, this.fftH * 2, 2, this.fftH / 2);
  }

}

SoundFFT.FREQUENCIES = 'SoundFFT.FREQUENCIES';
SoundFFT.WAVEFORM = 'SoundFFT.WAVEFORM';
SoundFFT.BEAT = 'SoundFFT.BEAT';

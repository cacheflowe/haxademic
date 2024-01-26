package com.haxademic.core.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.EasingFloat;

import beads.Sample;
import beads.SamplePlayer;
import processing.core.PGraphics;


public class WaveformScroller {

  // sound and buffer to draw. can be switched in real-time
  protected PGraphics pg;
  protected SamplePlayer curSound;

  protected Sample curSample;
  protected int numChannels;
  protected long numFrames;
  protected int samplesPerUpdate;
  protected int numFramesToDraw;

  // Config for subdividing the current frame of info, given the sticky playhead issue.
  // the eased playhead progress helps fill in the gap, and we can further subdivide the 
  // data to match up better with the current frame of data
  protected float playheadEaseFactor = 0.1f;
  protected int scrollSpeed = 10;

  // audio playback data
  protected float[] audioFrames;
  protected float[][] sampledFrames;
  protected EasingFloat progress = new EasingFloat(0, playheadEaseFactor);

  public WaveformScroller(int w, int h) {
    pg = PG.newPG(w, h);
  }

  public PGraphics texture() {
    return pg;
  }

  public void scrollSpeed(int scrollSpeed) {
    this.scrollSpeed = scrollSpeed;
  }

  public int scrollSpeed() {
    return scrollSpeed;
  }

  public float progress() {
    return progress.value();
  }

  public void update(SamplePlayer curSound) {
    boolean didChange = (this.curSound != curSound);
    this.curSound = curSound;
    if(didChange) recalculateAudio();
    updatePlayheadProgress();
    updateAudioData();
    drawWaveform();
  }

  protected void updatePlayheadProgress() {
    // NOTE: had to use an easing float because the playhead progress will be stuck on the same number for multiple frames, leading to weird echoed waveform readings
    float progressLocal = (float) curSound.getPosition() / (float) curSound.getSample().getLength(); // borrowed from WavPlayer.progress();
    progress.setEaseFactor(playheadEaseFactor);
    if(progressLocal < progress.value()) {
      progress.setTarget(progressLocal).setCurrent(progressLocal);
    } else {
      progress.setTarget(progressLocal).update();
    }
  }

  protected void recalculateAudio() {
    // get audio data from Sample
    // SamplePlayer curSound = player.getPlayer(soundbed);
    curSample = curSound.getSample();
    numChannels = curSample.getNumChannels();
    numFrames = curSample.getNumFrames();
    samplesPerUpdate = P.round((int)numFrames / (float)curSample.getLength() * 60); // samples per second at 60fps
    numFramesToDraw = pg.width;

    // rebuild audio data arrays
    audioFrames = new float[numFramesToDraw];
    for (int i = 0; i < audioFrames.length; i++) audioFrames[i] = 0;
    // cache this array instead of recreating every frame. this is populated by Beads' Sample.getFrames()
    sampledFrames = new float[numChannels][samplesPerUpdate]; 
  }

  protected void updateAudioData() {	
    // only update audio data if we have good Sample data and are actually playing the sound
    if(numFrames > 0 && curSample.getLength() > 0 && curSound.isPaused() == false) {
      // get start frame but don't select a window beyond the end of the sample
      int startFrame = P.round(progress.value() * numFrames);
      if(startFrame + samplesPerUpdate > numFrames) startFrame = (int)(numFrames - samplesPerUpdate);
      
      // get samples in relation to buffer size
      curSample.getFrames(startFrame, sampledFrames);

      // move/scroll data samples in our persistent storage array
      for (int i = 0; i < audioFrames.length - scrollSpeed; i++) {
        audioFrames[i] = audioFrames[i + scrollSpeed];
      }

      // add new samples to end of scrolling storage, skipping over data to keep only what we need.
      // this takes into account the playhead issue in Beads
      int iCopy = 0;
      int iCopySkip = P.round((samplesPerUpdate / scrollSpeed) * playheadEaseFactor);
      for (int i = audioFrames.length - scrollSpeed; i < audioFrames.length; i++) {
        audioFrames[i] = sampledFrames[0][iCopy];
        iCopy += iCopySkip;
      }
    }
  }

  protected void drawWaveform() {
    // calc data visual width
    int x = 0;
    int h = pg.height;
    
    // draw to buffer
    pg.beginDraw();
    pg.background(0);
    pg.fill(255);
    PG.setDrawCenter(pg);
    pg.translate(0, pg.height / 2);
    for (int i = 0; i < pg.width; i++) {
      pg.fill(255, 255, 255);
      pg.rect(x, 0, 1, h * audioFrames[i]);
      x++;
    }
    pg.endDraw();
  }

}

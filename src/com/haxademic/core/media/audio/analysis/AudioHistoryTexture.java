package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;

public class AudioHistoryTexture {

  protected int bands = 256;
  protected int length = 256;
  protected int waveW = 512;
  protected PGraphics historyFFT; 
  protected PGraphics historyWaveform; 

  public AudioHistoryTexture() {
    historyFFT = PG.newPG(bands, length);
    historyWaveform = PG.newPG(waveW, length);
  }

  public PGraphics textureFFT() {
    return historyFFT;
  }
  
  public void updateFFT() {
    updateFFT(true);
  }
  
  public void updateFFT(boolean useAudioInTexture) {
    // build audio map w/scrolling history
    historyFFT.beginDraw();
    historyFFT.noStroke();

    // scroll down
    historyFFT.copy(0, 0, historyFFT.width, historyFFT.height, 0, 1, historyFFT.width, historyFFT.height);

    // draw top line of current eq, either with pixels, or by 
    // using the AudioIn global texture. Requires `AudioIn.drawBufferFFT();`
    // see Demo_WavPlayer
    if(useAudioInTexture) {
      historyFFT.image(AudioIn.bufferFFT(), 0, 0, historyFFT.width, 1);
    } else {
      for (int i = 0; i < historyFFT.width; i++) {
        historyFFT.fill(255f * AudioIn.audioFreq(i));
        historyFFT.rect(i, 0, 1, 1);
      }
    }

    // close context
    historyFFT.endDraw();
  }
  
  public PGraphics textureWaveform() {
    return historyWaveform;
  }
  
  public void updateWaveform() {
    updateWaveform(true);
  }
  
  public void updateWaveform(boolean useAudioInTexture) {
    // build audio map w/scrolling history
    historyWaveform.beginDraw();
    historyWaveform.noStroke();
    
    // scroll down
    historyWaveform.copy(0, 0, historyWaveform.width, historyWaveform.height, 0, 1, historyWaveform.width, historyWaveform.height);
    
    // draw top line of current eq, either with pixels, or by 
    // using the AudioIn global texture. Requires `AudioIn.drawBufferFFT();`
    // see Demo_WavPlayer
    if(useAudioInTexture) {
      historyWaveform.image(AudioIn.bufferWaveform(), 0, 0, historyWaveform.width, 1);
    } else {
      for (int i = 0; i < historyWaveform.width; i++) {
        historyWaveform.fill(255f * AudioIn.audioWave(i));
        historyWaveform.rect(i, 0, 1, 1);
      }
    }
    
    // close context
    historyWaveform.endDraw();
  }

}

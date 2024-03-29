package com.haxademic.demo.media.audio.vst;

/*
 *  Copyright 2007 - 2009 Martin Roth (mhroth@gmail.com)
 *                        Matthew Yee-King
 * 
 *  This file is part of JVstHost.
 *
 *  JVstHost is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JVstHost is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JVstHost.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.synthbot.audioplugin.vst.vst2.JVstHost2;

/**
 * JVstAudioThread implements a continuously running audio stream, calling
 * processReplacing on a single vst and sending the result to the sound output.
 */
public class JVstAudioThreadCustom implements Runnable {

  private JVstHost2 vst;
  private JVstHost2 vstFX;
  private final float[][] fInputs;
  private final float[][] fOutputs;
  private final float[][] fOutputs2;
  private final byte[] bOutput;
  private int blockSize;
  private int numOutputs;
  private int numAudioOutputs;
  private AudioFormat audioFormat;
  private SourceDataLine sourceDataLine;

  private static final float ShortMaxValueAsFloat = (float) Short.MAX_VALUE;

  public JVstAudioThreadCustom(JVstHost2 vst, JVstHost2 vstFX, float[][] fInputs) {
    this.vst = vst;
    this.vstFX = vstFX;

    numOutputs = vst.numOutputs();
    numAudioOutputs = Math.min(2, numOutputs); // because most machines do not offer more than 2 output channels
    blockSize = vst.getBlockSize();
    this.fInputs = fInputs;
//    fInputs = new float[vst.numInputs()][blockSize];

    fOutputs = new float[numOutputs][blockSize];
    fOutputs2 = new float[numOutputs][blockSize];
    bOutput = new byte[numAudioOutputs * blockSize * 2];

    audioFormat = new AudioFormat((int) vst.getSampleRate(), 16, numAudioOutputs, true, false);
    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

    sourceDataLine = null;
    try {
      sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      sourceDataLine.open(audioFormat, bOutput.length);
      sourceDataLine.start();
    } catch (LineUnavailableException lue) {
      lue.printStackTrace(System.err);
//      System.exit(1);
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    try {
      // close the sourceDataLine properly when this object is garbage collected
      sourceDataLine.drain();
      sourceDataLine.close();      
    } finally {
      super.finalize();
    }
  }

  /**
   * Converts a float audio array [-1,1] to an interleaved array of 16-bit samples
   * in little-endian (low-byte, high-byte) format.
   */
  private byte[] floatsToBytes(float[][] fData, byte[] bData) {
    int index = 0;
    for (int i = 0; i < blockSize; i++) {
      for (int j = 0; j < numAudioOutputs; j++) {
        short sval = (short) (fData[j][i] * ShortMaxValueAsFloat);
        bData[index++] = (byte) (sval & 0x00FF);
        bData[index++] = (byte) ((sval & 0xFF00) >> 8);
      }
    }
    return bData;
  }
  
  public void run() {
    while (true) {
//    	P.out("Hello", fInputs[0][0]);
//    	P.out("Hello2", fOutputs[0][0]);
//    	P.out("Hello2", vst.turnOn());
//    	VstPinProperties props = vst.getInputProperties(0);
//    	P.out("Hello2", props.isActive());
      vst.processReplacing(fInputs, fOutputs, blockSize);
      vstFX.processReplacing(fOutputs, fOutputs2, blockSize);
//      vst.process(fInputs, fOutputs, blockSize);
//      vst.isSynth();
//      P.out("nope");
      sourceDataLine.write(floatsToBytes(fOutputs2, bOutput), 0, bOutput.length);
    }
  }
}

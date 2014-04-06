package com.haxademic.core.audio;

import krister.Ess.AudioChannel;
import processing.core.PApplet;

import com.haxademic.core.app.P;
import com.haxademic.core.render.Renderer;

public class WaveformData {
	
	protected PApplet p;
	public float[] _waveform;
	
	public WaveformData( PApplet p5, int bufferSize ) {
		p = p5;
		_waveform = new float[bufferSize];
		for( int i = 0; i < bufferSize; i++ ) _waveform[i] = 0;
	}
	
	public void updateWaveformData ( krister.Ess.AudioInput audioInput, int bufferSize ) {
		// store waveform data from live input
		int interp = (int) P.max( 0, (( ( p.millis() - audioInput.bufferStartTime)/(float)audioInput.duration) * audioInput.size));
		for (int i=0;i<bufferSize;i++) {
			int segmentLength = i;
			if( segmentLength+interp < audioInput.buffer2.length ) {
				_waveform[i] = audioInput.buffer2[segmentLength+interp];
			}
		}
	}
	
	public void updateWaveformDataMinim ( ddf.minim.AudioInput audioInput ) {
		// store waveform data from live input
		for (int i=0; i < _waveform.length; i++ ) {
			_waveform[i] = audioInput.mix.get(i);
		}
	}
	
	public void updateWaveformDataForRender ( Renderer renderer, krister.Ess.AudioInput audioInput, int bufferSize ) {
		// store waveform data from Channel that's playing the audio file
		int buffer2Length = renderer.getChannel().buffer2.length;
		AudioChannel channel = renderer.getChannel();
		int interp = (int) P.max( 0, (( ( p.millis() - channel.bufferStartTime) / (float)channel.ms(channel.buffer.length) ) * bufferSize));
		for (int i=0;i<512;i++) {
			if( i+interp < buffer2Length ) {
				_waveform[i] = channel.buffer2[i+interp];
			}
		}
	}
	
	public void copyFromOtherWaveformData( WaveformData other, int bufferSize ) {
		_waveform = new float[bufferSize];
		for( int i = 0; i < bufferSize; i++ ) _waveform[i] = other._waveform[i];
	}
}

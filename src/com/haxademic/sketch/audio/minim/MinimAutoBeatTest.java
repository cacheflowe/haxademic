package com.haxademic.sketch.audio;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import ddf.minim.AudioPlayer;

public class MinimAutoBeatTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AudioPlayer _kick;
	protected AudioPlayer _snare;
	protected AudioPlayer _stab;
	protected AudioPlayer _bass;
	
	protected float _bpm = 170;
	protected float _interval = (60 * 1000) / _bpm;
	protected int _lastTick = 0;
	protected int _step = 0;

	protected void overridePropsFile() {
	}

	public void setup() {
		super.setup();
		
		_kick = p.audioIn.minim().loadFile( FileUtil.getHaxademicDataPath() + "audio/kit808/kick.wav", 1024 );
		_snare = p.audioIn.minim().loadFile( FileUtil.getHaxademicDataPath() + "audio/kit808/snare.wav", 1024 );
		_stab = p.audioIn.minim().loadFile( FileUtil.getHaxademicDataPath() + "audio/drums/janet-stab.wav", 1024 );
		_bass = p.audioIn.minim().loadFile( FileUtil.getHaxademicDataPath() + "audio/kit808/bass.wav", 1024 );

//		_beats.add( new BeatSquare(0 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,0), ) );
//		_beats.add( new BeatSquare(1 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,1), "data/audio/kit808/snare.wav") );
//		_beats.add( new BeatSquare(2 * drumPadW, 1 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(1,2), "data/audio/kit808/tom.wav") );
//		
//		_beats.add( new BeatSquare(0 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,0), "data/audio/drums/snare-x10.wav") );
//		_beats.add( new BeatSquare(1 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,1), "data/audio/drums/chirp-11.wav") );
//		_beats.add( new BeatSquare(2 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,2), "data/audio/drums/chirp-18.wav") );
//		_beats.add( new BeatSquare(3 * drumPadW, 2 * drumPadH, drumPadW, drumPadH, _colors.getColorFromGroup(2,3), "data/audio/drums/janet-stab.wav") );

	}

	public void drawApp() {
		background(0);
		
		_bpm = 40 + 160 * MathUtil.getPercentWithinRange(0, p.width, p.mouseX);
		_interval = (60 * 1000) / _bpm;

		
		if( p.millis() > _lastTick + _interval ) {
			if( _step % 2 == 0 ) {
				_kick.play(0);
			}

			if( (_step + 1) % 2 == 0 ) {
				_snare.play(0);
			}
			
			if( _step % 8 == 0 ) {
				_stab.play(0);
			}
			
			if( (_step - 2) % 8 == 0 ) {
				_bass.play(0);
			}
			
			_lastTick = p.millis();
			_step++;
			_step = _step % 16;
		}
	}

}
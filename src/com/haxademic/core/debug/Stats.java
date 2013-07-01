package com.haxademic.core.debug;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class Stats {
	
	protected PApplet p;
	
	protected int _frames = 0;
	protected int _time, _timeLastFrame, _timeLastSecond;
	protected int _fps = 0;
	protected int _fpsMin = 1000;
	protected int _fpsMax = 0;
	protected int _ms = 0;
	protected int _msMin = 1000;
	protected int _msMax = 0;
	
	/**
	 * Ported from https://github.com/mrdoob/stats.js
	 * @param p
	 */
	public Stats( PApplet p ) {
		this.p = p;
		_time = p.millis();
		_timeLastFrame = _time;
		_timeLastSecond = _time;
	}

	public int getFps() {
		return _fps;
	}

	public int getFpsMin() {
		return _fpsMin;
	}

	public int getFpsMax() {
		return _fpsMax;
	}

	public int getMs() {
		return _ms;
	}

	public int getMsMin() {
		return _msMin;
	}

	public int getMsMax() {
		return _msMax;
	}

	public void update() {
		_time = p.millis();
		_ms = _time - _timeLastFrame;
		_msMin = Math.min( _msMin, _ms );
		_msMax = Math.max( _msMax, _ms );


		_timeLastFrame = _time;
		_frames++;
		
		if ( _time > _timeLastSecond + 1000 ) {
			_fps = Math.round( ( _frames * 1000 ) / ( _time - _timeLastSecond ) );
			_fpsMin = Math.min( _fpsMin, _fps );
			_fpsMax = Math.max( _fpsMax, _fps );
						
			_timeLastSecond = _time;
			_frames = 0;
		}
	}
	
	public void printStats() {
		P.println( _ms + " MS (" + _msMin + "-" + _msMax + ")" );
		P.println( _fps + " FPS (" + _fpsMin + "-" + _fpsMax + ")" );
	}
}

package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import processing.video.Movie;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.system.FileUtil;

public class VideoScrubberLayer
extends ElementBase 
implements IVizElement {
	
	protected Movie _movie;
	protected float _scrubTime = 0;
	protected boolean _hasLoaded = false;
	protected int _timingFrame = 0;
	protected boolean _active = false;

	public VideoScrubberLayer( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		_movie = new Movie( p, FileUtil.getHaxademicDataPath() + "video/da-dip.mov" );
		_movie.play();
		_movie.loop();
		_movie.volume(0);
		_movie.pause();
	}
	
	public void setDrawProps(float width, float height) {

	}

	public void updateColorSet( ColorGroup colors ) {
//		_baseColor = colors.getRandomColor().copy();
//		_fillColor = _baseColor.copy();
//		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		if( _active == false ) {
			_active = true;
			_movie.play();
			updateSection();
		}
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setCenterScreen(p);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setPImageAlpha(p, 1);
		p.resetMatrix();
		
		p.pushMatrix();
		p.translate(0, 0, -2000);
		p.scale(7.0f);

		float sliderVal = (float)((PAppletHax)p)._midi.sliderValue;
		if( sliderVal >= 1 ) {
			DrawUtil.setPImageAlpha(p, sliderVal / 127f );
			p.image(_movie, 0, 0);
			DrawUtil.resetPImageAlpha(p);
		}
		
		p.popMatrix();
	}

	public void nextImage() {

	}
	
	public void reset() {
		
	}

	public void pause() {
		_movie.pause();
		_active = false;
	}

	public void dispose() {
		_audioData = null;
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_movie.jump(_scrubTime);
		}
		_timingFrame++;
	}

	public void updateSection() {
		_scrubTime = p.random( 0, _movie.duration() );
		_movie.jump(_scrubTime);
	}

}

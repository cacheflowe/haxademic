package com.haxademic.core.render;


import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class VideoFrameGrabber {

	protected String _videoFile;
	protected Movie _video = null;
	protected PImage _curFrame = null;
	protected int _fps = 30;
	protected int _startFrame = 0;
	
	public VideoFrameGrabber( PApplet p, String videoFile, int frameRate, int startFrame ) {
		_videoFile = videoFile;
		_fps = frameRate;
		_startFrame = startFrame;
		
		_video = new Movie( p, _videoFile );
		_video.play();
		_video.frameRate( _fps );
		_video.pause();
		
		seekAndUpdateFrame(0);
	}
	
	public void readVideo() {
		if( _video != null ) _video.read();
	}
	
	public PImage curFrame() {
		return _curFrame;
	}
	
	public boolean isVideoDataGood() {
		if( _video.pixels.length > 100 ) return true;
		else return false;
	}
	
	public void seekAndUpdateFrame( int frameIndex ) {
		frameIndex += _startFrame;
		seekTo( (float) frameIndex / _fps );
		_video.read();
		_video.pause();
		P.p.image(_video, 0, 0);
		if( _curFrame == null )	_curFrame = new PImage( _video.width, _video.height );	// need to wait till first frame has been read from video
		_curFrame.copy( _video, 0, 0, _video.width, _video.height, 0, 0, _curFrame.width, _curFrame.height );
	}
	
	public void randomMovieTime() {
		_video.jump( MathUtil.randRangeDecimel( 0, _video.duration() - 1 ) );
	}
	
	public void seekTo( float time ) {
		_video.jump( time );
		_video.pause();
	}
	
	// Called every time a new frame is available to read
	public void movieEvent(Movie m) {
		// m.read();
	}
	

}

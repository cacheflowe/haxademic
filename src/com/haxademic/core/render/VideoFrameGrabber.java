package com.haxademic.core.render;


import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class VideoFrameGrabber {

	protected String _videoFile;
	protected Movie _video = null;
	protected PGraphics _curFrameBuffer = null;
	protected PImage _curFrameImage= null;
	protected int _fps = 30;
	protected int _startFrame = 0;
	
	public VideoFrameGrabber( PApplet p, String videoFile, int frameRate, int startFrame ) {
		_videoFile = videoFile;
		_fps = frameRate;
		_startFrame = startFrame;
		
		_video = new Movie( p, _videoFile );
		_video.play();
		_video.frameRate( _fps );
//		_video.pause();
		
		_curFrameBuffer = p.createGraphics( _video.width, _video.height, P.P3D );
		_curFrameImage = p.createImage( _video.width, _video.height, P.ARGB );
		
		seekAndUpdateFrame(0);
	}
	
	public int width() {
		return _video.width;
	}
	
	public int height() {
		return _video.height;
	}
	
	public void readVideo() {
		if( _video != null ) _video.read();
	}
	
	public PImage curFrame() {
		return _curFrameImage;
	}
	
	public boolean isVideoDataGood() {
		if( _video.pixels.length > 100 ) return true;
		else return false;
	}
	
	public void seekAndUpdateFrame( int frameIndex ) {
		frameIndex += _startFrame;
		seekTo( (float) frameIndex / _fps );
//		if( _video.available() ) {
			P.println("attempt to read "+frameIndex + "  - "+_video.available());
//			P.p.image(_video, 0, 0);
			
			_curFrameBuffer.beginDraw();
			_curFrameBuffer.image(_video, 0, 0, _video.width, _video.height);
			_curFrameBuffer.endDraw();
//
			_curFrameImage.copy( _curFrameBuffer, 0, 0, _curFrameBuffer.width, _curFrameBuffer.height, 0, 0, _curFrameImage.width, _curFrameImage.height );
//			if( _curFrame == null )	_curFrame = new PImage( _video.width, _video.height );	// need to wait till first frame has been read from video
//			_curFrame.copy( _video, 0, 0, _video.width, _video.height, 0, 0, _curFrame.width, _curFrame.height );
//		}
	}
	
	public void randomMovieTime() {
		_video.jump( MathUtil.randRangeDecimel( 0, _video.duration() - 1 ) );
	}
	
	public void seekTo( float time ) {
		_video.jump( time );
		_video.play();
//		_video.pause();
	}
	
	

}

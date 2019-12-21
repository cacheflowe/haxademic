package com.haxademic.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.PixelTriFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.BlobOuterMeshFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.ImageHistogramFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.PixelFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.ReflectionFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.VideoFrameGrabber;

import processing.core.PConstants;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

public class MultiInputImageFilters
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int inputType;
	protected final int WEBCAM = 0;
	protected final int VIDEO = 1;
	protected final int IMAGE = 2;
	
	protected PImage _loadedImg;
	protected PImage _curFrame;
	protected VideoFrameGrabber _frameGrabber;
	protected BlobOuterMeshFilter _blobFilter;
	protected ReflectionFilter _reflectionFilter;
	protected PixelTriFilter _pixelTriFilter;
	protected PixelFilter _pixelFilter;
//	protected Cluster8BitRow _clusterRowFilter;
	protected ImageHistogramFilter _histogramFilter;
	
	protected PShader blur;

		
	public void firstFrame() {
		initRender();
	}
	
	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
	}

	// INITIALIZE OBJECTS ===================================================================================
	public void initRender() {
		inputType = IMAGE;
		int w = 680;
		int h = 680;
		
//		blur = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/blur.glsl" ); 
		
		switch( inputType ) {
			case VIDEO :
				_frameGrabber = new VideoFrameGrabber( p, "/Users/cacheflowe/Documents/workspace/haxademic/assets/media/video/Janet Jackson - Control - trimmed.mov", 30, 100 );
				break;
			case IMAGE :
//				_loadedImg = p.loadImage("http://blogs.smithsonianmag.com/artscience/files/2012/09/caffeine-crystals-big.jpg");
				_loadedImg = DemoAssets.squareTexture();
				break;
		}
		
		_blobFilter = new BlobOuterMeshFilter( w, h );
		_reflectionFilter = new ReflectionFilter( w, h );
		_pixelFilter = new PixelFilter( w, h, 2 );
//		_clusterRowFilter = new Cluster8BitRow( w, h, 8, false );
		_histogramFilter = new ImageHistogramFilter( w, h, 6 );
		_pixelTriFilter = new PixelTriFilter( w, h, 6 );
	}
		
	// FRAME LOOP RENDERING ===================================================================================
	public void drawApp() {
//		filter(blur); 
		
		p.background(0);
		p.fill( 255 );
		p.noStroke();
		p.rectMode( PConstants.CENTER );
		PG.setBasicLights( p );
		
		// draw current frame and image filter
		PG.setColorForPImage(this);
		PG.setPImageAlpha(this, 1.0f);
		
		p.translate(0, 0, -400);
		
		// capture source image
		switch( inputType ) {
			case WEBCAM :
				_curFrame = WebCam.instance().image();
				_curFrame = ImageUtil.getReversePImageFast( _curFrame );	// mirror mode
				break;
			case VIDEO :
				_frameGrabber.setFrameIndex( p.frameCount );
				_curFrame = _frameGrabber.frameImageCopy();
				break;
			case IMAGE :
				_curFrame = _loadedImg;
				break;
		}
		
		
		// draw source and processed/filtered images
//		applyPostFilters();
		applyImageFilters();
		p.image( _curFrame, 0, 0, _curFrame.width, _curFrame.height );
	}
	
	protected void applyImageFilters() {
//		_curFrame = _histogramFilter.updateWithPImage( _curFrame );
//		if( frameCount % 2 == 1 ) _curFrame = _clusterRowFilter.updateWithPImage( _curFrame );
//		_curFrame = _pixelTriFilter.updateWithPImage( _curFrame );	// _clusterRowFilter.updateWithPImage( 
//		_curFrame = _pixelTriFilter.updateWithPImage( _histogramFilter.updateWithPImage( _curFrame ) );
//		_curFrame = _blobFilter.updateWithPImage( _curFrame );	// _pixelFilter.updateWithPImage( 
//		_curFrame = _pixelTriFilter.updateWithPImage( _histogramFilter.updateWithPImage( _reflectionFilter.updateWithPImage( _curFrame ) ) );
		_curFrame = _blobFilter.updateWithPImage( _pixelFilter.updateWithPImage( _curFrame ) );
//		_curFrame = _pixelFilter.updateWithPImage( _curFrame );
	}
	
	protected void drawSourceFrame() {
		p.pushMatrix();
		p.translate(0,0,-5);
		p.image(_curFrame,0,0,_curFrame.width,_curFrame.height);
		p.popMatrix();
	}
	
	
	// Called every time a new frame is available to read
	public void movieEvent(Movie m) {
		m.read();
	}

}

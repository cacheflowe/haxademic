package com.haxademic.sketch.render;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pgraphics.PixelTriFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.BlobOuterMeshFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.ImageHistogramFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.PixelFilter;
import com.haxademic.core.draw.filters.pgraphics.archive.ReflectionFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.VideoFrameGrabber;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.HSBAdjustFilter;

import processing.core.PConstants;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

public class MultiInputImageFilters
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
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

		
	public void setup() {
		super.setup();
		initRender();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
		p.appConfig.setProperty( AppSettings.WEBCAM_INDEX, 6 );
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
				_loadedImg = p.loadImage(FileUtil.getHaxademicDataPath()+"images/bobby-broadway.jpg");
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
		DrawUtil.setBasicLights( p );
		
		// draw current frame and image filter
		DrawUtil.setColorForPImage(this);
		DrawUtil.setPImageAlpha(this, 1.0f);
		
		p.translate(0, 0, -400);
		
		// capture source image
		switch( inputType ) {
			case WEBCAM :
				_curFrame = p.webCamWrapper.getImage();
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
		applyPostFilters();
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
	
	protected void applyPostFilters() {
		// create native java image
		BufferedImage buff = ImageUtil.pImageToBuffered( _curFrame );
		
		// contrast
		ContrastFilter filt = new ContrastFilter();
		filt.setBrightness(1.2f);
		filt.setContrast(1.5f);
		filt.filter(buff, buff);
		
		// hsb adjust
		HSBAdjustFilter hsb = new HSBAdjustFilter();
		hsb.setHFactor(P.sin(p.frameCount/400f));
		hsb.setSFactor(0.2f);
		hsb.setBFactor(0.2f);
		hsb.filter(buff, buff);
		
		// glow
//		GlowFilter glow = new GlowFilter();
//		glow.setRadius(20f);
//		glow.filter(buff, buff);
		
		// bump
//		BumpFilter bump = new BumpFilter();
//		bump.filter(buff, buff);
		
		// edge
//		EdgeFilter edge = new EdgeFilter();
//		edge.filter(buff, buff);
		
		// motion blur
//		MotionBlurFilter blur = new MotionBlurFilter();
//		blur.setAngle(P.TWO_PI/16f);
//		blur.setDistance(30f);
//		blur.filter(buff, buff);
		
		// ray
//		RaysFilter ray = new RaysFilter();
//		ray.setAngle(P.TWO_PI/8f);
//		ray.setDistance(60f);
//		ray.filter(buff, buff);
		
		// kaleidoscope
//		KaleidoscopeFilter kaleida = new KaleidoscopeFilter();
//		kaleida.setSides(8);
//		kaleida.filter(buff, buff);
		
		// contrast again
		filt.filter(buff, buff);

		
		// save processed image back to _curFrame
		_curFrame = ImageUtil.bufferedToPImage( buff );
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

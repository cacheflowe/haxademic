package com.haxademic.sketch.test;

import java.awt.image.BufferedImage;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.image.filters.ImageHistogramFilter;
import com.haxademic.core.image.filters.PixelTriFilter;
import com.haxademic.core.math.MathUtil;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.HSBAdjustFilter;

@SuppressWarnings("serial")
public class VideoReaderTest
extends PAppletHax  
{
	protected Movie movie;
	protected PImage _curFrame;
	protected PGraphics _curMov;
	protected int curBase = 0;


	PixelTriFilter _triPixelFilter;
	ImageHistogramFilter _histogramFilter;
	// PixelTriFilter, Cluster8BitRow, ImageHistogramFilter

	public void setup() {
		super.setup();		

		_curMov = p.createGraphics(width, height, P.P3D);
		_curFrame = p.createImage(width, height, P.ARGB);
		movie = new Movie(this, "/Users/cacheflowe/Documents/workspace/haxademic/assets/media/video/Janet Jackson - Control - trimmed.mov");
		curBase = (int) (movie.duration() * (float) Math.random());

		_triPixelFilter = new PixelTriFilter( _curFrame.width, _curFrame.height, 10 );
		_histogramFilter = new ImageHistogramFilter( _curFrame.width, _curFrame.height, 10 );
		
		reSeek();
	}

	public void drawApp() {
		
		// size pixels
		int newPixelSize = 2 + (int)((mouseY + 2) / 20f);
		if( newPixelSize % 2 == 1 ) newPixelSize++;
		_triPixelFilter.setPixelSize( newPixelSize );
		
		// seek video
//		movie.jump(curBase + (float)Math.random()*0.2f);
		movie.jump( ( movie.duration() * ((float)mouseX / (float)width) )  + (float)( p.frameCount % 6 ) / 40f );
		movie.play();
		movie.pause();

		// copy video to off-screen graphics
		_curMov.beginDraw();
		_curMov.image(movie, 0, 0, width, height);
		_curMov.endDraw();
		
		// copy pGraphics to PImage for post-processing
		_curFrame.copy( _curMov, 0, 0, _curMov.width, _curMov.height, 0, 0, _curFrame.width, _curFrame.height );


		// Filters!!
		BufferedImage buff = ImageUtil.pImageToBuffered( _curFrame );

		ContrastFilter filt = new ContrastFilter();
		filt.setBrightness(1.2f);
		filt.setContrast(1.5f);
		filt.filter(buff, buff);

		HSBAdjustFilter hsb = new HSBAdjustFilter();
		hsb.setHFactor(P.sin(p.frameCount/400f));
		hsb.setSFactor(0.2f);
		hsb.setBFactor(0.0f);
//		hsb.filter(buff, buff);

		_curFrame = ImageUtil.bufferedToPImage( buff );


		// draw filtered image
		if( _curMov != null ) image( _triPixelFilter.updateWithPImage( _histogramFilter.updateWithPImage( _curFrame ) ), 0, 0, width, height);

	}
	
	protected void reSeek() {
		// generate different video offset
		if(p.frameCount % 100 == 0) {
			curBase = (int) (movie.duration() * (float) Math.random());
			int pixels = MathUtil.randRange( 6, 20 );
			if( pixels % 2 == 1 ) pixels++;
			_triPixelFilter.setPixelSize( pixels );
		}
	}

	public void movieEvent(Movie m) {
		m.read();
//		reSeek();
	}

}

package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PImage;

public class Chaircuterie
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected ArrayList<PImage> _images;
	protected int _numImages;
	protected int _imageIndex = 0;
	protected float _framesPerImage = 40;
	protected boolean _pixelated = false;
	
	protected void overridePropsFile() {
		loadImages();
		p.appConfig.setProperty( AppSettings.WIDTH, "360" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "360" );
				
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "true" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "30" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_framesPerImage * _images.size() - 1) );
	}
	
	public void setupFirstFrame() {

		p.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	protected void loadImages() {
		// String imgBase = "images/chairs/06-canvas/";
		String imgBase = "images/cb-logos/";
		
		ArrayList<String> files = FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "jpg" );
		files.addAll( FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "png" ) );
		FileUtil.shuffleFileList( files );
		
		_images = new ArrayList<PImage>();
		for( int i=0; i < files.size(); i++ ) {
			_images.add( P.p.loadImage( FileUtil.getHaxademicDataPath() + imgBase + files.get(i) ) );
		}
		
		_numImages = Math.round(_framesPerImage * _images.size());
	}

	
	public void drawApp() {	
		p.background(0);
		p.noStroke();
		
		int curChairFrame = Math.round(p.frameCount % _framesPerImage);
		int curChairFrameShrink = Math.round(p.frameCount % (_framesPerImage/4f));
		
		float percentComplete = ((float)curChairFrame/_framesPerImage);
		float percentGrowComplete = ((float)curChairFrame*2f/_framesPerImage);
		float percentShrinkComplete = ((float)curChairFrameShrink/(_framesPerImage/4f));
		if(percentGrowComplete > 1.5f) 
			percentGrowComplete = 1f - percentShrinkComplete;
		else if(percentGrowComplete > 1f) 
			percentGrowComplete = 1;

		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		float easedPercentGrow = Penner.easeInOutQuart(percentGrowComplete, 0, 1, 1);
		if(percentComplete == 0 && _imageIndex < _numImages - 3) {
			_imageIndex++;
			_imageIndex = _imageIndex % _numImages;
		} else {
			
		}
		
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
		p.scale(easedPercentGrow);
		p.image(_images.get(_imageIndex), 0, 0, p.width, p.height);
		p.popMatrix();
		
		if(_pixelated == true) {
			// redraw stage pixelated 
			PImage stage = p.get();
			
			float cellsize = p.width / 36;
			int columns = 36;//img.width / cellsize;  // Calculate # of columns
			int rows = 36;//img.height / cellsize;  // Calculate # of rows
	
			
			// columns
			for ( int i = 0; i < columns; i++) {
				// rows
				for ( int j = 0; j < rows; j++) {
					int x = P.round(i*cellsize + cellsize/2f);
					int y = P.round(j*cellsize + cellsize/2f);
					int loc = x + y*stage.width;  //  p.PIxel array location
					int c = stage.pixels[loc];  // Grab the color
	
					// Translate to the location, set fill and stroke, and draw the rect
					p.fill(c);
					p.rectMode(PConstants.CORNER);
					p.rect(i * cellsize, j * cellsize, cellsize, cellsize);
				}
			}
		}
		
	}

}




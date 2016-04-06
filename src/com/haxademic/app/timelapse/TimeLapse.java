package com.haxademic.app.timelapse;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.system.FileUtil;

public class TimeLapse
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Image sequence
	 */
	protected ArrayList<String> _images;
	
	/**
	 * Image path
	 */
	protected String _imageDir;
	
	/**
	 * Image type
	 */
	protected String _imageType;
	
	/**
	 * Image sequence index
	 */
	protected int _imageIndex;
	
	/**
	 * Frames per image
	 */
	protected int _fpi;
	
	public void settings() {
		customPropsFile = FileUtil.getHaxademicDataPath() + "properties/timelapse.properties";
		super.settings();
	}

	public void setup() {
		super.setup();
		initRender();
	}

	public void initRender() {
		_imageDir = p.appConfig.getString( "image_dir", "" );
		_imageType = p.appConfig.getString( "image_type", ".png" );
		_images = FileUtil.getFilesInDirOfType( _imageDir, _imageType ); // FileUtil.getHaxademicDataPath() + 
		_imageIndex = 0;
		_fpi = p.appConfig.getInt( "frames_per_image", 2 );
	}
		
	public void drawApp() {
		p.background(0);
		DrawUtil.setColorForPImage(p);
		DrawUtil.setPImageAlpha(p, (p.frameCount % 2 == 1) ? 0.999f : 1 );	// stupid hack b/c UMovieMaker doesn't save the exact same frame twice in a row.
		
		// load and display current image
		if( _imageIndex < _images.size() ) {
			PImage img = p.loadImage( _imageDir + _images.get( _imageIndex ) );
			p.image( img, 0, 0 );
		}
		
		// step to next image
		if( p.frameCount > 0 && p.frameCount % _fpi == 0 ) _imageIndex++;
		
		// stop when done
		if( _imageIndex == _images.size() ) {
			_renderer.stop();
		} else if( _imageIndex == _images.size() + 1 ) {
			p.exit();
		}
	}
	
}

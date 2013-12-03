package com.haxademic.app.timelapse;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.system.FileUtil;

public class TimeLapse
extends PAppletHax  
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.timelapse.TimeLapse" });
	}
	
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
	
	public void setup() {
		_customPropsFile = FileUtil.getHaxademicDataPath() + "properties/timelapse.properties";
		super.setup();
		initRender();
	}

	public void initRender() {
		_imageDir = _appConfig.getString( "image_dir", "" );
		_imageType = _appConfig.getString( "image_type", ".jpg" );
		_images = FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + _imageDir, _imageType );
		_imageIndex = 0;
		_fpi = _appConfig.getInt( "frames_per_image", 2 );
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

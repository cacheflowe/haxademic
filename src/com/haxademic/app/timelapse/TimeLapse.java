package com.haxademic.app.timelapse;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class TimeLapse
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<String> _images;
	protected String _imageDir;
	protected String _imageType;
	protected int _imageIndex;
	protected int _framesPerImage;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1080 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( "image_type", "jpg" );
		p.appConfig.setProperty( "image_dir", "/Users/cacheflowe/Documents/workspace/mode_set/client_docs/legwork/nike-all-star/final_snkrs_xpress_game_faces/" );
	}

	public void setup() {
		super.setup();
		initRender();
	}

	public void initRender() {
		_imageDir = p.appConfig.getString( "image_dir", "" );
		_imageType = p.appConfig.getString( "image_type", "png" );
		_images = FileUtil.getFilesInDirOfType( _imageDir, _imageType ); 
		P.println(_images.size());
		_imageIndex = 0;
		_framesPerImage = p.appConfig.getInt( "frames_per_image", 1 );
	}
		
	public void drawApp() {
		p.background(0);
		DrawUtil.setColorForPImage(p);
		DrawUtil.setPImageAlpha(p, (p.frameCount % 2 == 1) ? 0.999f : 1 );	// stupid hack b/c UMovieMaker doesn't save the exact same frame twice in a row.
		
		// load and display current image
		if( _imageIndex < _images.size() ) {
			PImage img = p.loadImage( _imageDir + _images.get( _imageIndex ) );
			p.image( img, 0, 0, p.width, p.height );
		}
		
		// step to next image
		if( p.frameCount > 0 && p.frameCount % _framesPerImage == 0 ) _imageIndex++;
		
		// stop when done
		if( _imageIndex == _images.size() ) {
			movieRenderer.stop();
		} else if( _imageIndex == _images.size() + 1 ) {
			p.exit();
		}
	}
	
}

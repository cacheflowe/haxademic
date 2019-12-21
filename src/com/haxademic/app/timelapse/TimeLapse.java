package com.haxademic.app.timelapse;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class TimeLapse
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<String> _images;
	protected String _imageDir;
	protected String _imageType;
	protected int _imageIndex;
	protected int _framesPerImage;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1080 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( "image_type", "jpg" );
		Config.setProperty( "image_dir", "/Users/cacheflowe/Documents/workspace/mode_set/client_docs/legwork/nike-all-star/final_snkrs_xpress_game_faces/" );
	}

	public void firstFrame() {

		initRender();
	}

	public void initRender() {
		_imageDir = Config.getString( "image_dir", "" );
		_imageType = Config.getString( "image_type", "png" );
		_images = FileUtil.getFilesInDirOfType( _imageDir, _imageType ); 
		P.println(_images.size());
		_imageIndex = 0;
		_framesPerImage = Config.getInt( "frames_per_image", 1 );
	}
		
	public void drawApp() {
		p.background(0);
		PG.setColorForPImage(p);
		PG.setPImageAlpha(p, (p.frameCount % 2 == 1) ? 0.999f : 1 );	// stupid hack b/c UMovieMaker doesn't save the exact same frame twice in a row.
		
		// load and display current image
		if( _imageIndex < _images.size() ) {
			PImage img = p.loadImage( _imageDir + _images.get( _imageIndex ) );
			p.image( img, 0, 0, p.width, p.height );
		}
		
		// step to next image
		if( p.frameCount > 0 && p.frameCount % _framesPerImage == 0 ) _imageIndex++;
		
		// stop when done
		if( _imageIndex == _images.size() ) {
			videoRenderer.stop();
		} else if( _imageIndex == _images.size() + 1 ) {
			p.exit();
		}
	}
	
}

package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class TextureImageTimeStepper
extends BaseTexture {

	protected PShader _saturation;
	protected int _mode = 0;
	
	
	protected PGraphics _image;
	protected ArrayList<PImage> _images;
	protected int _imageIndex = 0;
	protected int _imageIndexTime = 0;
	protected boolean _needsReload = false;
	protected PShader _pixelate;
	protected PShader _blurH;
	protected PShader _blurV;
	protected PShader _vignette;
	protected PShader _kaleido;
	protected PShader _brightness;
	protected int _timingFrame = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);
	protected EasingFloat _brightEaser = new EasingFloat(0, 10);


	public TextureImageTimeStepper( int width, int height ) {
		super(width, height);
		
		
		loadShaders();
		loadImages();
	}
	
	protected void loadShaders() {
		_vignette = P.p.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness = P.p.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/brightness.glsl" );
		_brightness.set("brightness", _brightEaser.value() );

		_saturation = P.p.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
		_saturation.set("saturation", 0.25f );

	
		_blurH = P.p.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/blur-horizontal.glsl" ); 
		_blurH.set( "h", 1f/width );
		_blurV = P.p.loadShader( FileUtil.haxademicDataPath()+"haxademic/shaders/filters/blur-vertical.glsl" ); 
		_blurV.set( "v", 1f/height );
		
	}
	
	protected void loadImages() {
		String imgBase = "haxademic/images/space/";
		
		ArrayList<String> files = FileUtil.getFilesInDirOfType( FileUtil.haxademicDataPath() + imgBase, "jpg" );
		files.addAll( FileUtil.getFilesInDirOfType( FileUtil.haxademicDataPath() + imgBase, "png" ) );
		FileUtil.shuffleFileList( files );
		
		_images = new ArrayList<PImage>();
		for( int i=0; i < files.size(); i++ ) {
			_images.add( P.p.loadImage( FileUtil.haxademicDataPath() + imgBase + files.get(i) ) );
		}
		_image = P.p.createGraphics( width, height, P.P2D);// new PImage( p.width, p.height );
		nextImage();

	}

	public void draw() {
		if( _needsReload == true ) {
			reloadImage();
			_needsReload = false;
		}
		
		updateShaders();
		
//		_texture.clear();
		pg.background(0);
		pg.image( _image, 0, 0, _image.width, _image.height );
		pg.filter( _saturation );
		pg.filter( _brightness );
//		_texture.filter( _vignette );
	}
	
	public void nextImage() {
		_imageIndex++;
		if( _imageIndex >= _images.size() ) _imageIndex = 0;
		_needsReload = true;
	}

	protected void updateShaders() {
		_timeEaser.update();
		_brightEaser.update();

//		_vignette.set("darkness", 0.7f);
//		_vignette.set("spread", 0.15f);

		_brightness.set("brightness", _brightEaser.value() );
	}
	
	public void reloadImage() {
		ImageUtil.cropFillCopyImage( _images.get( _imageIndex ), _image, true );
//		_image.filter(_blurH);
//		_image.filter(_blurV);
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_brightEaser.setCurrent(2.0f);
			_timeEaser.setTarget( _timeEaser.value() + 3 );
			_needsReload = true;
		} else {
			_brightEaser.setCurrent(1.0f);
			_timeEaser.setTarget( _timeEaser.value() + 1 );
			_needsReload = true;
		}
		_brightEaser.setTarget(0.25f);
		_timingFrame++;
	}
	
	public void updateTimingSection() {
		_timingFrame = 0;
		_mode++;
		if(_mode >= 3) _mode = 0;
		nextImage();
	}

}

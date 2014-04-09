package com.haxademic.app.haxmapper.textures;

import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

public class TextureImageTimeStepper
extends BaseTexture {

//	protected PGraphics _image;
//	protected PShader _vignette;
//	protected PShader _brightness;
	protected PShader _saturation;
//	protected int _timingFrame = 0;
//	protected EasingFloat _timeEaser = new EasingFloat(0, 15);
//	protected EasingFloat _brightEaser = new EasingFloat(0, 10);
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
		super();
		
		buildGraphics( width, height );
		loadShaders();
		loadImages();
	}
	
	protected void loadShaders() {
		_vignette = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

		_brightness = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", _brightEaser.value() );

		_saturation = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_saturation.set("saturation", 0.25f );

	
		_blurH = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" ); 
		_blurH.set( "h", 1f/_texture.width );
		_blurV = _texture.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" ); 
		_blurV.set( "v", 1f/_texture.height );
		
	}
	
	protected void loadImages() {
		String imgBase = "images/cacheflowe-art/";
		
		ArrayList<String> files = FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "jpg" );
		files.addAll( FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "png" ) );
		FileUtil.shuffleFileList( files );
		
		_images = new ArrayList<PImage>();
		for( int i=0; i < files.size(); i++ ) {
			_images.add( P.p.loadImage( FileUtil.getHaxademicDataPath() + imgBase + files.get(i) ) );
		}
		_image = P.p.createGraphics( _texture.width, _texture.height, P.P2D);// new PImage( p.width, p.height );
		nextImage();

	}

	public void update() {
		super.update();

		if( _needsReload == true ) {
			reloadImage();
			_needsReload = false;
		}
		
		updateShaders();
		
		_texture.beginDraw();
		_texture.clear();
		_texture.image( _image, 0, 0, _image.width, _image.height );
		_texture.filter( _saturation );
		_texture.filter( _brightness );
		_texture.filter( _vignette );
		
		_texture.endDraw();
	}
	
	public void nextImage() {
		_imageIndex++;
		if( _imageIndex >= _images.size() ) _imageIndex = 0;
		_needsReload = true;
	}

	protected void updateShaders() {
		_timeEaser.update();
		_brightEaser.update();

		_vignette.set("darkness", 0.7f);
		_vignette.set("spread", 0.15f);

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

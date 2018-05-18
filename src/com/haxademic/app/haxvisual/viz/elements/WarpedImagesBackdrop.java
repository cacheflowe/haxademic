package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class WarpedImagesBackdrop
extends ElementBase 
implements IVizElement  {
	
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

	public WarpedImagesBackdrop( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		String imgBase = "images/cacheflowe-art/";
		
		ArrayList<String> files = FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "jpg" );
		files.addAll( FileUtil.getFilesInDirOfType( FileUtil.getHaxademicDataPath() + imgBase, "png" ) );
		FileUtil.shuffleFileList( files );
		
		_images = new ArrayList<PImage>();
		for( int i=0; i < files.size(); i++ ) {
			_images.add( p.loadImage( FileUtil.getHaxademicDataPath() + imgBase + files.get(i) ) );
		}
		_image = p.createGraphics(p.width, p.height, P.P2D);// new PImage( p.width, p.height );
		nextImage();
		
		_pixelate = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/pixelate.glsl" ); 
		_pixelate.set("divider", _image.width/10f, _image.height/10f);
		
		_blurH = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/blur-horizontal.glsl" ); 
		_blurH.set( "h", 1f/p.width );
		_blurV = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/blur-vertical.glsl" ); 
		_blurV.set( "v", 1f/p.height );
		
		_vignette = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.75f);
		_vignette.set("spread", 0.3f);

		_kaleido = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/kaleido.glsl" ); 
		_kaleido.set("sides", 6.0f);
		_kaleido.set("angle", P.PI/2f);

		_brightness = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/brightness.glsl" );
		_brightness.set("brightness", 0.98f );

	}

	public void setDrawProps(float strokeWeight, float width, float amp) {

	}
	
	public void nextImage() {
		_imageIndex++;
		if( _imageIndex >= _images.size() ) _imageIndex = 0;
		_needsReload = true;
	}
	
	public void reloadImage() {
		ImageUtil.cropFillCopyImage( _images.get( _imageIndex ), _image, true );
		_image.filter(_blurH);
		_image.filter(_blurV);
	}

	public void update() {
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setCenterScreen(p);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setPImageAlpha(p, 1);
		p.resetMatrix();
		
		if( _needsReload == true ) {
			reloadImage();
			_needsReload = false;
		}
		
		p.pushMatrix();
		p.translate(0, 0, -2000);
		p.scale(3.0f);
		p.rotateX(P.PI);
		
		_timeEaser.update();
//		_warping.set("time", _timeEaser.value() );
//		_image.filter(_warping);
//		_image.filter(_pixelate);
		_image.filter(_vignette);
		_image.filter(_brightness);

		p.image(_image, 0, 0);
		p.popMatrix();
		
	}
		
	public void reset() {
		
	}

	public void dispose() {
	}

	public void updateLineMode() {
//		_kaleido.set("sides", MathUtil.randRange(1, 2));
	}

	public void updateCamera() {
		// TODO Auto-generated method stub
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 && p.millis() > _imageIndexTime + 100 ) {
//			cropFillCopyImage( _images.get( _imageIndex ), _image, true );
//			_image.filter(_pixelate);
			_needsReload = true;
//			_kaleido.set("sides", (float)MathUtil.randRange(1, 2));
			//_image.filter(_kaleido);
//			_timeEaser.setTarget(_timeEaser.value() + 10);
		}
		_timingFrame++;
	}
		
	public void updateSection() {
		nextImage();
		_timingFrame = 0;
	}

}

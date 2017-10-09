package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class ImageMaskTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _mask;
	protected PGraphics _maskInverse;
	protected PGraphics _image, _image2;
	protected PShape _svgMask;
	protected float _sizeRatio;
	protected PImage img, img2;

	protected void overridePropsFile() {
		appConfig.setProperty( AppSettings.WIDTH, "1000" );
		appConfig.setProperty( AppSettings.HEIGHT, "1000" );
		appConfig.setProperty( AppSettings.FPS, "60" );
	}

	public void setup() {
		super.setup();
		
		_mask = p.createGraphics( p.width, p.height, P.P3D );
		_mask.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		_maskInverse = p.createGraphics( p.width, p.height, P.P3D );
		_maskInverse.smooth(OpenGLUtil.SMOOTH_MEDIUM);

		_svgMask = p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cacheflowe-logo.svg" );
		_sizeRatio = (float) p.height / (float) _svgMask.height;
		
		img = p.loadImage( FileUtil.getHaxademicDataPath() + "images/sphere-map-test.jpg" );
		img2 = p.loadImage( FileUtil.getHaxademicDataPath() + "images/sphere-map-test-2.jpg" );
		_image = p.createGraphics( p.width, p.height, P.P3D );
		_image2 = p.createGraphics( p.width, p.height, P.P3D );
	}

	public void drawApp() {
		p.background(0);
		
		_mask.beginDraw();
//		_mask.clear();
		_mask.background(0);
		DrawUtil.setDrawCenter(_mask);
		_svgMask.disableStyle();
		_mask.fill(255, 127 + 127 * P.sin(p.frameCount*0.01f));
		_mask.pushMatrix();
		_mask.translate( _mask.width / 2f, _mask.height / 2f );
		_mask.rotate(p.frameCount * 0.01f);
		_mask.shape(_svgMask, 0, 0, _svgMask.width * _sizeRatio, _svgMask.height * _sizeRatio );
		_mask.popMatrix();
		_mask.endDraw();
		
		_image.beginDraw();
//		_image.clear();
		DrawUtil.setDrawCenter(_image);
		_image.translate(_image.width/2, _image.height/2);
		_image.rotate(p.millis()/800f);
		_image.image(img, 0, 0, _image.width, _image.height );
		_image.endDraw();
		
		_image.mask( _mask );
		p.image(_image, 0, 0);
		
		
		_maskInverse.beginDraw();
//		_maskInverse.clear();
		_maskInverse.background(255);
		DrawUtil.setDrawCenter(_maskInverse);
		_svgMask.disableStyle();
		_maskInverse.fill(0);
		_maskInverse.pushMatrix();
		_maskInverse.translate( _maskInverse.width / 2f, _maskInverse.height / 2f );
		_maskInverse.rotate(p.frameCount * 0.01f);
		_maskInverse.shape(_svgMask, 0, 0, _svgMask.width * _sizeRatio, _svgMask.height * _sizeRatio );
		_maskInverse.popMatrix();
		_maskInverse.endDraw();

		_image2.beginDraw();
//		_image2.clear();
		DrawUtil.setDrawCenter(_image2);
		_image2.translate(_image2.width/2, _image2.height/2);
		_image2.rotate(p.millis()/1000f);
		_image2.image(img2, 0, 0, _image2.width*2, _image2.height*2 );
		_image2.endDraw();
		
		_image2.mask( _maskInverse );
		p.image(_image2, 0, 0);
	}

}
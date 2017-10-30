package com.haxademic.sketch.render.ello;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo005RollAcross
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	float _frames = 60;
	float _elloSize = 40;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		if(p.appConfig.getBoolean("rendering_gif", false) == true) startGifRender();
	}
	
	public void startGifRender() {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp(p) + "-export.gif" );
		encoder.setFrameRate( 45 );
		encoder.setQuality( 15 );
		encoder.setRepeat( 0 );
	}
		
	public void renderGifFrame() {
		PImage screenshot = get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);
	}

	public void drawApp() {
		p.background(255);
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		if(percentComplete == 0)
			_elloSize *= 4;
		
		DrawUtil.setDrawCorner(p);
				
		float dist = percentComplete * (p.width + _elloSize*2);
		
		float x = -_elloSize + dist;
		float circumference = _elloSize * P.PI;
		float rotationRads = (x / circumference) * P.TWO_PI;
		
		p.pushMatrix();
		p.translate(x, p.height - _elloSize/2f);
		p.rotate(rotationRads);
		p.shape(_logo, 0, 0, _elloSize, _elloSize);
		p.popMatrix();

//		filter(INVERT);

		if(p.appConfig.getBoolean("rendering_gif", false) == true) renderGifFrame();
		if( p.frameCount == _frames * 4 + 5 ) {
			if(p.appConfig.getBoolean("rendering_gif", false) ==  true) encoder.finish();
			if(_renderer != null) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}
}




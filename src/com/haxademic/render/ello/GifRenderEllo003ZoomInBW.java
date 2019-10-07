package com.haxademic.render.ello;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.SystemUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo003ZoomInBW
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	float _frames = 50;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "500" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "500" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		if(p.appConfig.getBoolean("rendering_gif", false) ==  true) startGifRender();
	}
	
	public void startGifRender() {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp() + "-export.gif" );
		encoder.setFrameRate( 40 );
		encoder.setRepeat( 0 );
	}
		
	public void renderGifFrame() {
		PImage screenshot = get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);

		if( p.frameCount == 55 ) {
			if(p.appConfig.getBoolean("rendering_gif", false) ==  true) encoder.finish();
			P.println("gif render done!");
		}
	}

	public void drawApp() {
		p.background(255);
//		p.fill(255, 40);
//		p.rect(0, 0, p.width, p.height);
		p.noStroke();
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float percentWhiteComplete = P.constrain(((float)(p.frameCount - (_frames/2))/_frames), 0, 1);
		float percentBlackComplete = P.constrain(((float)(p.frameCount)/_frames), 0, 1);
		float easedScale = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		float easedWhiteScale = Penner.easeInOutQuart(percentWhiteComplete, 0, 1, 1);
		float easedWhiteRot = 0.6f - Penner.easeInOutSine(percentWhiteComplete, 0, 1, 1);
		float easedBlackScale = Penner.easeInOutQuart(percentBlackComplete, 0, 1, 1);
		float easedBlackRot = -0.6f + Penner.easeInOutSine(percentBlackComplete, 0, 1f, 1);

		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
//		float elloSize = (float)(p.width/1.5f + 7f * frameOsc);
		float elloSize = (float)(p.width/1.5f);
		
		PG.setDrawCorner(p);
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
		
//		P.println("frame",frameCount);
		
		p.pushMatrix();
		p.scale(easedBlackScale * 5f);
		p.rotate(easedBlackRot);
		p.shape(_logo, 0, 0, elloSize, elloSize);
		p.popMatrix();
		
		p.pushMatrix();
		p.scale(easedWhiteScale * 5f);
		p.rotate(easedWhiteRot);
		p.shape(_logoInverse, 0, 0, elloSize, elloSize);
		p.popMatrix();


		if(p.appConfig.getBoolean("rendering_gif", false) ==  true) renderGifFrame();
	}
}




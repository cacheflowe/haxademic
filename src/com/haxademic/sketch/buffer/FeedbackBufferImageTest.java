package com.haxademic.sketch.buffer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;

public class FeedbackBufferImageTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _texture;
	protected PImage img;
	protected float _frames = 90;
	
	public void setup() {
		super.setup();
		
		img = p.loadImage(FileUtil.getFile("images/penis-witch.png"));
		
		_texture = p.createGraphics( p.width, p.height, P.P3D );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "420" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, ""+ Math.round(_frames*2) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, ""+Math.round(_frames + _frames*2) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames*2) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames*2) );
	}
		
	public void drawApp() {
		p.background(255);

		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutSine(percentComplete, 0, 1, 1);
		// float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		// texture feedback
		float feedback = 1f;// * P.sin(percentComplete * P.TWO_PI);
		_texture.copy(
				_texture, 
				0, 
				0, 
				_texture.width, 
				_texture.height, 
				P.round(0), 
				P.round(0), 
				P.round(_texture.width + feedback), 
				P.round(_texture.height + feedback)
		);

		// start texture drawing
		_texture.beginDraw();
		DrawUtil.setDrawCenter(_texture);
		_texture.noStroke();
		
		// fade out
		_texture.fill( 255, 160f );
		_texture.translate(p.width/2f, p.height * 0.45f);
		_texture.rect(0, 0, p.width, p.height);

		
		// draw image
		_texture.rotate(0.06f * P.sin(P.QUARTER_PI + P.TWO_PI * percentComplete));
		_texture.image(img, 2f + 10f * P.sin(P.TWO_PI * percentComplete), 16f * P.sin(P.TWO_PI * percentComplete), img.width * 0.3f, img.height * 0.3f);
		
		// finish drawing
		_texture.endDraw();
		
		// draw texture to stage
		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}
}

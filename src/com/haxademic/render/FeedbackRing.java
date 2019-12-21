package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;

public class FeedbackRing
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _texture;
	protected float _frames = 60;
	
	public void firstFrame() {

		
		_texture = p.createGraphics( p.width, p.height, P.P3D );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
	}
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );
		
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames*4) );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames*4) );
	}
		
	public void drawApp() {
		p.background(255);

		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutSine(percentComplete, 0, 1, 1);
		// float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		// texture feedback
		float feedback = 10f;// * P.sin(percentComplete * P.TWO_PI);
		PG.feedback(_texture, 2);

		// start texture drawing
		_texture.beginDraw();
		PG.setDrawCenter(_texture);
		_texture.noStroke();
		
		// fade out
		_texture.fill( 255, 15f );
		_texture.translate(p.width/2f, p.height/2f);
		_texture.rect(0, 0, p.width, p.height);

		
		// rotating circles
		float arms = 260;
		float circleInc = P.TWO_PI / arms;
		float radius = width/6f;
		
		for (int i = 0; i < arms; i++) {
			float curRads = circleInc * i;
			float moreRads = curRads + easedPercent * P.TWO_PI;
			float radiuz = radius + radius/2f * P.sin(moreRads + percentComplete * 1f * P.TWO_PI);
			_texture.pushMatrix();
			_texture.translate(P.sin(moreRads) * radiuz, P.cos(moreRads) * radiuz);
			_texture.rotate(moreRads);
			_texture.fill(60 + 50 * P.sin(moreRads), 127 + 80 * P.sin(curRads), 100 + 80 * P.sin(curRads), 200);
			_texture.ellipse(0, 0, 5, 5);
			_texture.popMatrix();
		}
		
		
		// finish drawing
		_texture.endDraw();
		
		// draw texture to stage
		PG.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}
}

package com.haxademic.sketch.test;

import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.Penner;

@SuppressWarnings("serial")
public class FeedbackBufferTest
extends PAppletHax  
{	
	protected PGraphics _texture;
	protected float _frames = 60;
	
	public void setup() {
		super.setup();
		
		_texture = p.createGraphics( p.width, p.height, P.OPENGL );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
		
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "1" );
		_appConfig.setProperty( "rendering_gif_startframe", ""+ Math.round(_frames*4) );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames + _frames*4) );
	}
		
	public void drawApp() {
		p.background(255);

		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutSine(percentComplete, 0, 1, 1);
		// float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		// texture feedback
		float feedback = 20f;// * P.sin(percentComplete * P.TWO_PI);
		_texture.copy(
				_texture, 
				0, 
				0, 
				_texture.width, 
				_texture.height, 
				P.round(-feedback/2f), 
				P.round(-feedback/2f), 
				P.round(_texture.width + feedback), 
				P.round(_texture.height + feedback)
		);

		// start texture drawing
		_texture.beginDraw();
		DrawUtil.setDrawCenter(_texture);
		_texture.noStroke();
		
		// fade out
		_texture.fill( 255, 15f );
		_texture.translate(p.width/2f, p.height/2f);
		_texture.rect(0, 0, p.width, p.height);

		
		// rotating circles
		float arms = 260;
		float circleInc = P.TWO_PI / arms;
		float radius = 100;
		
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
		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}
}

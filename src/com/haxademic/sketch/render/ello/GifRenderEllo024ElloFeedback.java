package com.haxademic.sketch.render.ello;

import processing.core.PGraphics;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.shapes.Gradients;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo024ElloFeedback
extends PAppletHax {

	protected PGraphics _texture;
	protected PShape _logo;
	protected float _frames = 40f;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
		
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "1" );
		_appConfig.setProperty( "rendering_gif_startframe", ""+ Math.round(_frames*3) );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames + _frames*3) );
	}
	
	public void setup() {
		super.setup();
		
		_texture = p.createGraphics( p.width, p.height, P.OPENGL );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
		
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-filled.svg");
	}
	
	public void drawApp() {
		p.background(255);

		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutSine(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		// texture feedback
		float feedback = 12f;// * P.sin(percentComplete * P.TWO_PI);
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
		_texture.fill( 255, 9f );
		_texture.translate(p.width/2f, p.height/2f);
		_texture.rect(0, 0, p.width, p.height);
		
		// rotating circles
		float arms = 12f;
		float circleInc = P.TWO_PI / arms;
		float radius = 100f;
		
		DrawUtil.setDrawCorner(_texture);
		for (float i = 0; i < arms; i++) {
			float curRads = circleInc * i;
			float moreRads = curRads + percentComplete * circleInc;
			float radiuz = radius + (radius/8f) * P.sin(moreRads + easedPercent * P.TWO_PI);

			_texture.pushMatrix();
			_texture.translate(P.sin(moreRads) * radiuz, P.cos(moreRads) * radiuz);
			_texture.rotate(-moreRads);
			_texture.scale(0.4f + 0.1f * P.sin(P.PI + moreRads + percentComplete * P.TWO_PI));
			_texture.shape(_logo);
			_texture.popMatrix();
		}		
		
		// finish drawing
		_texture.endDraw();
		
		// draw texture to stage
		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}}

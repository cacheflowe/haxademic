package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PShape;

public class GifRenderEllo024ElloFeedback
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _texture;
	protected PShape _logo;
	protected float _frames = 40f;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );
		
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames*3) );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames*3) );
	}
	
	public void firstFrame() {

		
		_texture = p.createGraphics( p.width, p.height, P.P3D );
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
		PG.setDrawCenter(_texture);
		_texture.noStroke();
		
		// fade out
		_texture.fill( 255, 9f );
		_texture.translate(p.width/2f, p.height/2f);
		_texture.rect(0, 0, p.width, p.height);
		
		// rotating circles
		float arms = 12f;
		float circleInc = P.TWO_PI / arms;
		float radius = 100f;
		
		PG.setDrawCorner(_texture);
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
		PG.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}}

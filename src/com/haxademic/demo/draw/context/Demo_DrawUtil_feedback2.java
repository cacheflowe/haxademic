package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DrawUtil_feedback2
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _texture;
	protected PImage img;
	protected float _frames = 90;
	
	public void setupFirstFrame() {
		img = DemoAssets.smallTexture();
		
		_texture = p.createGraphics( p.width, p.height, P.P3D );
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, ""+ Math.round(_frames*2) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, ""+Math.round(_frames + _frames*2) );
	}
		
	public void drawApp() {
		p.background(255);
		
		// texture feedback
		DrawUtil.feedback(_texture, 2);

		// start texture drawing
		_texture.beginDraw();
		DrawUtil.setDrawCenter(_texture);
		_texture.noStroke();
		_texture.translate(p.width/2, p.height/2);
		
		// draw image
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		_texture.rotate(0.06f * P.sin(P.QUARTER_PI + P.TWO_PI * percentComplete));
		_texture.image(img, 2f + 10f * P.sin(P.TWO_PI * percentComplete), 16f * P.sin(P.TWO_PI * percentComplete), img.width * 3f, img.height * 3f);
		
		// finish drawing
		_texture.endDraw();
		
		// draw texture to stage
		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}
}

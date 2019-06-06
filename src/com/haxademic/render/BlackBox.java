package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;

public class BlackBox 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 95;
	PGraphics texture;
	PImage logo;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();
		noStroke();
		texture = p.createGraphics(p.width, p.height, P.P3D);
		logo = p.loadImage(FileUtil.getFile("images/the-black-box-white.png"));
	}

	public void drawApp() {
		p.background(0);
		p.translate(width/2, height/2, 0);
		DrawUtil.setBetterLights(p);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setPImageAlpha(p, 1);

		updateBoxTexture();
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete % 1, 0, 1, 1);
		float radsComplete = percentComplete * P.TWO_PI;


		// draw outer box
		p.pushMatrix();
		p.translate(0, height * -0.2f, 0);
		p.rotateX(-P.PI/8f);
		p.rotateY(P.PI/4f);
		Shapes.drawTexturedCube(p.g, p.width * 1.2f + (p.width * 0.1f) * P.sin(radsComplete), texture);
		p.popMatrix();

		// draw black box
		p.pushMatrix();
		p.fill(1);
		p.translate(0, 0, -p.height * 0.9f);
		p.rotateX(-P.PI/8f);
		p.rotateY(-P.PI/4f);
		float boxSize = p.width * 0.8f + (p.width * 0.01f) * P.sin(radsComplete);
		p.box(boxSize);
		
		// draw logo based on inner box translation
		translate(boxSize * 0.29f, boxSize * 0.3f, boxSize * 0.52f);
		float logoScale = 1.3f * ((float) logo.width / (float) p.width);
		DrawUtil.setPImageAlpha(p, 0.9f + 0.1f * P.sin(P.PI + radsComplete));
		p.image(logo, 0, 0, logoScale * logo.width, logoScale * logo.height);
		
		p.popMatrix();
		
		VignetteFilter.instance(p).applyTo(p);
	}
	
	protected void updateBoxTexture() {
		texture.beginDraw();
		texture.background(150);
		VignetteFilter.instance(p).applyTo(texture);
		texture.endDraw();
	}
	
}

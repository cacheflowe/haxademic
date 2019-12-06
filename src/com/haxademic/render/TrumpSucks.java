package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class TrumpSucks
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img1;
	protected PImage img2;
	protected PShader transitionShader;
	protected PGraphics buffer;
	protected float frames = 200;
	protected TiledTexture tiledImg;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 372/2 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 484/2 );
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 400 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) frames );
	}

	public void setupFirstFrame() {

		
		img2 = ImageUtil.imageToGraphics(p.loadImage(FileUtil.getFile("images/trmp-fuck.png")));
		img1 = ImageUtil.imageToGraphics(p.loadImage(FileUtil.getFile("images/trmp-trmp.png")));

		buffer = ImageUtil.imageToGraphics(img1);
		buffer.smooth(8);
		
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/circle-open.glsl"));
		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/directional-wipe.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/warp-fade.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/morph.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/swap.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/dissolve.glsl"));
//		transitionShader.set("blocksize", 6f);
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/cross-zoom.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/hsv-blend.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/wind.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/cube.glsl"));
//		transitionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/glitch-displace.glsl"));

		
		transitionShader.set("from", img1);
		transitionShader.set("to", img2);
		
		tiledImg = new TiledTexture(buffer);

	}

	public void drawApp() {
//		p.background(0);
		float progress = (p.frameCount % frames*2) / frames; 
		if(progress < 1.0f) {
			transitionShader.set("from", img1);
			transitionShader.set("to", img2);
		} else {
			transitionShader.set("from", img2);
			transitionShader.set("to", img1);
		}
		float loopProgress = progress % 1f;
		float easedProgress = Penner.easeInOutCubic(loopProgress, 0, 1, 1);

		transitionShader.set("progress", easedProgress);
		buffer.filter(transitionShader);		
		
		
		
		
//		float easedPercent = Penner.easeInOutQuart(progress % 1, 0, 1, 1);
		float progressRads = (progress * P.TWO_PI)/2f;

		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		float rot = P.sin(progressRads) * 0.15f;
		float size = 10f + 8f * P.sin(progressRads);
//		rot = p.frameCount * 0.03f;
//		tiledImg.setRotation(P.sin(progressRads * 2f) * 0.1f);
		tiledImg.setRotation(rot);
		tiledImg.setOffset(-0.2f + progress * 4f, 0);
		tiledImg.setSize(size, size);
		tiledImg.update();
		tiledImg.drawCentered(p.g, p.width, p.height);
		p.popMatrix();

	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
		}
	}


}

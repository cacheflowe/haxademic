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

public class ThriveText
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage textImg;
	protected PGraphics tiledBuffer;
	protected PGraphics stripesBuffer1;
	protected PGraphics stripesBuffer2;
	protected float frames = 120;
	protected TiledTexture tiledImg;
	
	protected PShader stripes;
	protected PShader maskShader;

	// colors 
	/*
		#CBD727 / 203, 215, 39
		#95B2BB / 149, 178, 187
		#2D2D2B / 45, 45, 43
	 */

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 460/2 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) frames );
	}

	public void setupFirstFrame() {

		
		textImg = ImageUtil.imageToGraphics(p.loadImage(FileUtil.getFile("images/thrive-text.png")));

		stripesBuffer1 = p.createGraphics(p.width, p.height, P.P3D);
		stripesBuffer1.smooth(8);
		stripesBuffer2 = p.createGraphics(p.width, p.height, P.P3D);
		stripesBuffer2.smooth(8);
		
		tiledBuffer = p.createGraphics(p.width, p.height, P.P3D);
		tiledBuffer.smooth(8);
		
		tiledImg = new TiledTexture(textImg);
		
		maskShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/three-texture-opposite-mask.glsl"));
		
		stripes = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-rotating-stripes.glsl"));
	}

	public void drawApp() {
//		p.background(0);
		float progress = (p.frameCount % frames) / frames; 
		float easedProgress = Penner.easeInOutCubic(progress, 0, 1, 1);
		float progressRads = progress * P.TWO_PI;
		
		// draw stripes
		stripes.set("time", progress);
//		stripes.set("amp", 100.0f + 20f * P.sin(progressRads));
		stripes.set("amp", 100.0f);
		stripes.set("rot", 0.2f * P.sin(progressRads));
		stripes.set("rot", P.QUARTER_PI - 0.05f * P.sin(progressRads));
		stripes.set("rot", P.QUARTER_PI);
		stripes.set("color1", val255to1(203), val255to1(215), val255to1(39));
		stripes.set("color2", val255to1(45), val255to1(45), val255to1(43));
		stripesBuffer1.filter(stripes);
		
		// 2nd stripes
//		stripes.set("amp", 100.0f + 20f * P.sin(progressRads + P.PI));
		stripes.set("time", -1f * progress);
		stripes.set("rot", -1f * P.QUARTER_PI + 0.05f * P.sin(progressRads));
		stripes.set("rot", -1f * P.QUARTER_PI);
		stripes.set("color1", val255to1(149), val255to1(178), val255to1(187));
		stripesBuffer2.filter(stripes);
		
		// Tiled THRIVE
		tiledBuffer.beginDraw();
		tiledBuffer.pushMatrix();
		tiledBuffer.translate(tiledBuffer.width/2, tiledBuffer.height/2);
		float rot = P.sin(progressRads) * 0.02f;
		float size = 2.03f * 0.5f;// 2f + 1f * P.sin(progressRads);
//		rot = p.frameCount * 0.03f;
//		tiledImg.setRotation(P.sin(progressRads * 2f) * 0.1f);
		tiledImg.setRotation(rot);
		tiledImg.setOffset(0, progress);// easedProgress);
		tiledImg.setSize(size, size);
		tiledImg.update();
		tiledImg.drawCentered(tiledBuffer, tiledBuffer.width, tiledBuffer.height);
		tiledBuffer.popMatrix();
		tiledBuffer.endDraw();

		// apply 3-texture mask shader
//		maskShader.set("mask", textImg );
		maskShader.set("mask", tiledBuffer );
		maskShader.set("tex1", stripesBuffer1 );
		maskShader.set("tex2", stripesBuffer2 );
		p.filter(maskShader);

		
//		p.image(tiledBuffer, 0, 0);
	}
	
	protected float val255to1(float val) {
		return val / 255f;
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
		}
	}


}

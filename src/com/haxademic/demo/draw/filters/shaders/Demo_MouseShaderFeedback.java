package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.video.Movie;

public class MouseShader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShader mouseShader;
	protected PGraphics mouseBuffer;
	protected PShader feedbackShader;
	protected PGraphics feedbackBuffer;
	protected PImage img;
	protected EasingFloat mouseSpeed = new EasingFloat(0, 8f);

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280);
		p.appConfig.setProperty(AppSettings.HEIGHT, 984);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	public void setupFirstFrame() {
		img = DemoAssets.textureNebula();
		img = p.loadImage(FileUtil.getFile("images/textures/space/eagle-nebula-pillars-of-creation.jpg"));
		mouseBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		mouseBuffer.smooth(8);
		mouseBuffer.beginDraw(); mouseBuffer.background(0); mouseBuffer.endDraw();
		mouseShader = loadShader(FileUtil.getFile("shaders/interactive/mouse-speed.glsl"));
		feedbackBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		feedbackBuffer.smooth(8);
		feedbackShader = loadShader(FileUtil.getFile("shaders/interactive/mouse-speed-feedback-map.glsl"));
	}
	
	protected void blurMap(PGraphics buffer) {
		BlurProcessingFilter.instance(p).setBlurSize(10);
//		for(int i=0; i < 5; i++) 
		BlurProcessingFilter.instance(p).applyTo(buffer);
	}

	public void drawApp() {
		background(0);
		
		// update mouse shader
		mouseShader.set("mouse", P.map(p.mouseX, 0, p.width, 0f, 1f), P.map(p.mouseY, 0, p.height, 1f, 0f));
		mouseSpeed.setTarget((P.abs(p.mouseX - p.pmouseX) + P.abs(p.mouseY - p.pmouseY)) / ((float) p.width / 20f));
		mouseSpeed.update();
		float mouseDir = MathUtil.getRadiansToTarget(p.mouseX, p.mouseY, p.pmouseX, p.pmouseY);
		p.debugView.setValue("mouseSpeed", mouseSpeed.value());
		mouseShader.set("mouseSpeed", mouseSpeed.value());
		if(p.mouseX != p.pmouseX && p.mouseY != p.pmouseY) mouseShader.set("mouseDir", mouseDir);
		mouseShader.set("time", p.frameCount);
		mouseBuffer.filter(mouseShader);
		
		// blurMap(mouseBuffer);
		
		// copy video
//		if(video.width > 10) ImageUtil.cropFillCopyImage(video, img, true);
		
		// update feeback display
		feedbackBuffer.beginDraw();
		DrawUtil.setDrawCenter(feedbackBuffer);
		DrawUtil.setPImageAlpha(feedbackBuffer, 0.05f);
//		feedbackBuffer.tint(
//				300 + 155 * P.sin(p.frameCount/50f),
//				300 + 155 * P.sin(p.frameCount/80f),
//				300 + 155 * P.sin(p.frameCount/90f),
//				10);
		float scaleHeight = MathUtil.scaleToTarget(img.height, feedbackBuffer.height);
		feedbackBuffer.image(img, feedbackBuffer.width/2, feedbackBuffer.height/2, img.width * scaleHeight, img.height * scaleHeight);
		feedbackBuffer.endDraw();
		
		// set mouse map on displacement shader, and apply
//		feedbackShader.set("mouse", P.map(p.mouseX, 0, p.width, 0f, 1f), P.map(p.mouseY, 0, p.height, 1f, 0f));
		feedbackShader.set("map", mouseBuffer);
		feedbackBuffer.filter(feedbackShader); 
//		feedbackBuffer.filter(feedbackShader); 
//		feedbackBuffer.filter(feedbackShader); 

		// apply feedback to map??
		// mouseBuffer.filter(feedbackShader); 
		
		// draw to screen
		p.image(feedbackBuffer, 0, 0);
		
		// draw debug direction grid
		if(p.showDebug) {
			mouseBuffer.loadPixels();
			p.fill(255, 127);
			for (int x = 0; x < p.width; x += 30) {
				for (int y = 0; y < p.height; y += 30) {
					int pixelColor = ImageUtil.getPixelColor(mouseBuffer, x, y);
					p.pushMatrix();
					p.translate(x, y);
					p.rotate(P.PI + P.TWO_PI * -ColorUtil.redFromColorInt(pixelColor) / 255f);
					p.rect(0, -1, 20 * ColorUtil.greenFromColorInt(pixelColor) / 255f, 2);
					p.popMatrix();
				}
			}
		}
		
		// debug draw
		p.debugView.setTexture(mouseBuffer);
		p.debugView.setTexture(img);
		p.debugView.setTexture(feedbackBuffer);

	}
}


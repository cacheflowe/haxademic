package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_MouseShaderFeedback
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShader mouseShader;
	protected PGraphics mouseBuffer;
	protected PShader feedbackShader;
	protected PGraphics feedbackBuffer;
	protected PImage img;
	protected EasingFloat mouseSpeed = new EasingFloat(0, 8f);

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 984);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		img = DemoAssets.textureNebula();
//		img = p.loadImage(FileUtil.getFile("images/textures/space/eagle-nebula-pillars-of-creation.jpg"));
		mouseBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		mouseBuffer.smooth(8);
		mouseBuffer.beginDraw(); mouseBuffer.background(0); mouseBuffer.endDraw();
		mouseShader = loadShader(FileUtil.getPath("haxademic/shaders/interactive/mouse-speed.glsl"));
		feedbackBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		feedbackBuffer.smooth(8);
		feedbackShader = loadShader(FileUtil.getPath("haxademic/shaders/interactive/mouse-speed-feedback-map.glsl"));
	}
	
	protected void blurMap(PGraphics buffer) {
		BlurProcessingFilter.instance(p).setBlurSize(10);
//		for(int i=0; i < 5; i++) 
		BlurProcessingFilter.instance(p).applyTo(buffer);
	}

	protected void drawApp() {
		background(0);
		
		// update mouse shader
		mouseShader.set("mouse", P.map(p.mouseX, 0, p.width, 0f, 1f), P.map(p.mouseY, 0, p.height, 1f, 0f));
		mouseSpeed.setTarget((P.abs(p.mouseX - p.pmouseX) + P.abs(p.mouseY - p.pmouseY)) / ((float) p.width / 20f));
		mouseSpeed.update();
		float mouseDir = MathUtil.getRadiansToTarget(p.mouseX, p.mouseY, p.pmouseX, p.pmouseY);
		DebugView.setValue("mouseSpeed", mouseSpeed.value());
		mouseShader.set("mouseSpeed", mouseSpeed.value());
		if(p.mouseX != p.pmouseX && p.mouseY != p.pmouseY) mouseShader.set("mouseDir", mouseDir);
		mouseShader.set("time", p.frameCount);
		mouseBuffer.filter(mouseShader);
		
		// blurMap(mouseBuffer);
		
		// copy video
//		if(video.width > 10) ImageUtil.cropFillCopyImage(video, img, true);
		
		// update feeback display
		feedbackBuffer.beginDraw();
		PG.setDrawCenter(feedbackBuffer);
		PG.setPImageAlpha(feedbackBuffer, 0.05f);
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
		if(DebugView.active()) {
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
		DebugView.setTexture("mouseBuffer", mouseBuffer);
		DebugView.setTexture("img", img);
		DebugView.setTexture("feedbackBuffer", feedbackBuffer);

	}
}


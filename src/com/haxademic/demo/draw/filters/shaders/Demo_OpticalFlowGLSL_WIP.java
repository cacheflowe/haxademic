package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.text.StringFormatter;

import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.video.Movie;

public class Demo_OpticalFlowGLSL_WIP
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie testMovie;
	protected PGraphics lastFrame;
	protected PGraphics curFrame;
	protected PGraphics opFlowResult;
	protected PGraphics opFlowResultLerped;
	protected PShader opFlowShader;
	protected PShader textureLerpShader;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
	}

	public void setupFirstFrame() {
		// load movie
		testMovie = DemoAssets.movieKinectSilhouette();
		testMovie.jump(0);
		testMovie.loop();
		testMovie.speed(0.8f);
		
		// create buffers
		curFrame = p.createGraphics(p.width, p.height, PRenderers.P3D);
		lastFrame = p.createGraphics(p.width, p.height, PRenderers.P3D);
		opFlowResult = p.createGraphics(p.width, p.height, PRenderers.P3D);
		opFlowResultLerped = p.createGraphics(p.width, p.height, PRenderers.P3D);
		p.debugView.setTexture(curFrame);
		p.debugView.setTexture(lastFrame);
		p.debugView.setTexture(opFlowResult);
		p.debugView.setTexture(opFlowResultLerped);
		
		// load shader
		opFlowShader = p.loadShader(FileUtil.getFile("shaders/filters/optical-flow.glsl"));
		textureLerpShader = p.loadShader(FileUtil.getFile("shaders/filters/texture-blend-towards-texture.glsl"));
	}

	public void drawApp() {
		p.background(0);
		
		if(testMovie.width > 10) {
			// copy movie frames
			ImageUtil.cropFillCopyImage(curFrame, lastFrame, true);
			ImageUtil.cropFillCopyImage(testMovie.get(), curFrame, true);
			
			// update/draw shader
			opFlowShader.set("tex0", curFrame);
			opFlowShader.set("tex1", lastFrame);
			opFlowShader.set("lambda", 1f);
			opFlowShader.set("offset", 0.05f, 0.05f);
			opFlowShader.set("scale", 1.5f);
			opFlowResult.filter(opFlowShader);
			
			// fade it
			// run target blend shader
			textureLerpShader.set("blendLerp", 0.5f);
			textureLerpShader.set("targetTexture", opFlowResult);
			opFlowResultLerped.filter(textureLerpShader);
		}
		
		// draw lerped op flow result
		p.image(opFlowResultLerped, 0, 0);
		
		// debug
		// r,g,b,a = -x,+x,-y,+y
		p.loadPixels();
		p.fill(255);
		p.noStroke();
		for (int x = 0; x < p.width; x += 5) {
			for (int y = 0; y < p.height; y += 5) {
				int pixelColor = ImageUtil.getPixelColor(p, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
				float g = ColorUtil.greenFromColorInt(pixelColor) / 255f;
				float b = ColorUtil.blueFromColorInt(pixelColor) / 255f;
				float a = ColorUtil.alphaFromColorInt(pixelColor) / 255f;
				float xDir = (r + g) - 0.5f;
				float yDir = (b + a) - 0.5f;
				float rotation = -1f * (r * -P.TWO_PI); // MathUtil.getRadiansToTarget(0, 0, xDir, yDir);
				if(xDir + yDir > 0.01f) { 
					p.pushMatrix();
					p.translate(x, y);
					p.rotate(rotation);
					if(g > 0.03f) p.rect(0, -1, 100f * g, 1);
					p.popMatrix();
				}
			}
		}

	}

}









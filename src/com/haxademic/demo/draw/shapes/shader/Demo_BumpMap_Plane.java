package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_BumpMap_Plane
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// Ported into haxademic from: 
	// https://github.com/codeanticode/pshader-experiments
	
	protected PImage texture;
	protected PGraphics bumpMap;
	protected PGraphics specMap;
	protected PShape plane;
	protected PShader bumpMapShader;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, 900);
	}
	
	protected void firstFrame() {
		// load textures
		texture = P.getImage("images/textures/space/moon-layers/color.jpg");
		bumpMap = ImageUtil.imageToGraphics(P.getImage("images/textures/space/moon-layers/displacement.jpg"));
		ImageUtil.flipV(bumpMap);
		specMap = ImageUtil.imageToGraphics(bumpMap);
		
		// reuse bump map for specular map
		ImageUtil.copyImage(bumpMap, specMap);
		BrightnessFilter.instance(p).setBrightness(0.25f);
		BrightnessFilter.instance(p).applyTo(specMap);
		for(int i=0; i < 2; i++) BlurProcessingFilter.instance(p).applyTo(specMap);

		// show in debug
		DebugView.setTexture("bumpMap", bumpMap);
		DebugView.setTexture("specMap", specMap);

		// load shader
		bumpMapShader = loadShader(
				FileUtil.getPath("haxademic/shaders/vertex/bump-mapping-frag.glsl"), 
				FileUtil.getPath("haxademic/shaders/vertex/bump-mapping-vert.glsl")
				);
		bumpMapShader.set("texMap", texture);
		bumpMapShader.set("bumpMap", bumpMap);
		bumpMapShader.set("specularMap", specMap);
		bumpMapShader.set("bumpScale", 0.05f);
		
		// create sphere
		float rectSize = p.height * 0.6f;
		plane = createShape(P.RECT, -rectSize/2, -rectSize/2, rectSize, rectSize);
		plane.setStroke(false);
		plane.setSpecular(color(125));
		plane.setShininess(10);
	}
	
	protected void drawApp() {
		// set context
		background(0);
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g, 0.1f);
		
		// light source emitting from the right of the camera
		p.pointLight(255, 255, 255, P.map(Mouse.xNorm, 0, 1, 1500, -1500), 0, 500);
		
		// update shader
		bumpMapShader.set("bumpScale", Mouse.yNorm * 0.01f);
		
		// draw from center
		p.shader(bumpMapShader);
		p.pushMatrix();
//		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
//		p.rotateY(FrameLoop.progressRads());
		p.shape(plane);
		p.popMatrix();	  
	}

}

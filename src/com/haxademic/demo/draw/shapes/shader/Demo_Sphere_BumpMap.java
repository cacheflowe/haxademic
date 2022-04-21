package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_Sphere_BumpMap
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// Ported into haxademic from: 
	// https://github.com/codeanticode/pshader-experiments
	
	protected PImage texture;
	protected PGraphics bumpMap;
	protected PGraphics specMap;
	protected PShape sphere;
	protected PShader bumpMapShader;

	protected void firstFrame() {
		// load textures
		texture = DemoAssets.textureJupiter();
		bumpMap = ImageUtil.imageToGraphics(DemoAssets.textureJupiter());
		specMap = ImageUtil.imageToGraphics(DemoAssets.textureJupiter());
		
		// adjust texture, since we're reusing the texture map for alternate purposes
		BlurProcessingFilter.instance(p).setBlurSize(2);
		BlurProcessingFilter.instance(p).setSigma(1);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance(p).applyTo(bumpMap);
		DebugView.setTexture("bumpMap", bumpMap);
		
		ImageUtil.copyImage(bumpMap, specMap);
		
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
		p.sphereDetail(150);
		sphere = createShape(P.SPHERE, 200);
		sphere.setStroke(false);
		sphere.setSpecular(color(125));
		sphere.setShininess(10);
	}
	
	protected void drawApp() {
		// set context
		background(0);
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);
		
		// light source emitting from the right of the camera
		p.pointLight(255, 255, 255, P.map(Mouse.xNorm, 0, 1, 1500, -1500), 0, 500);
		
		// update shader
		bumpMapShader.set("bumpScale", Mouse.yNorm * 0.1f);
		
		// draw from center
		p.shader(bumpMapShader);
		p.pushMatrix();
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
		p.shape(sphere);
		p.popMatrix();	  
	}

}

package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_LightsTest_WIP 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PImage texture;
	protected PShader shader;
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();

		// normalize shape
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.50f);
		
		// load shader
		shader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/light-test-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/light-test-vert.glsl")
		);
		
		// Set UV coords & set texture on obj.
		// why is this necessary if it's not used??
		PShapeUtil.addTextureUVSpherical(obj, null);
//		obj.setTexture(ImageUtil.imageToGraphics(DemoAssets.textureNebula()));
	}

	public void drawApp() {
		background(0);
		DrawUtil.setCenterScreen(p);
		p.rotateY(0.5f * P.sin(p.frameCount * 0.01f));

		// use shader w/pointlight
		p.shader(shader);  
		p.pointLight(255, 0, 255, width/2, height/2 + 400 * P.sin(p.frameCount * 0.01f), 500);
		p.shape(obj);
		p.resetShader();
	}
		
}
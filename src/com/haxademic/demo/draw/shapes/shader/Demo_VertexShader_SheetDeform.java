package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_SheetDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;
	protected PShader displacementShader;
	protected float _frames = 360;

	protected void overridePropsFile() {
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// load texture
		perlin = new PerlinTexture(p, 256, 256);
		texture = perlin.texture();
		
		// build sheet mesh
		shape = Shapes.createSheet(100, texture);
		shape.setTexture(texture);
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		background(0);
		
		// update displacement texture
		perlin.update(0.05f, 0.05f, p.frameCount * 0.01f, 0);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, 0);
		p.rotateY(0.4f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +

		// draw mesh with texture or without
		displacementShader.set("displacementMap", perlin.texture());
		displacementShader.set("displaceStrength", 400f);
		p.shader(displacementShader);  
		p.shape(shape);
		p.resetShader();
	}
		
}
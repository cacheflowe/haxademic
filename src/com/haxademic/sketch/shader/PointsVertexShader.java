package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class PointsVertexShader 
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
		float size = 128;
		// load texture
		perlin = new PerlinTexture(p, (int) size, (int) size);
		int vertices = perlin.texture().width * perlin.texture().height; 
		texture = perlin.texture();
		
		// replace with a points version
		shape = P.p.createShape(); // PShape.GEOMETRY
		shape.beginShape(PConstants.POINTS);
//		shape.beginShape();
		shape.stroke(255);
		shape.strokeWeight(1);
		shape.noFill();
		for (int i = 0; i < vertices; i++) {
			float x = i % size;
			float y = P.floor(i / size);
			if(y % 2 == 1) x = size - x - 1;
			shape.vertex(x * 5f, y * 5f, 0, x/size, y/size);
		}
		shape.endShape();
		shape.setTexture(texture);
		
		// load shader
		displacementShader = loadShader(
//			FileUtil.getFile("shaders/vertex/brightness-displace-frag-color.glsl"), 
//			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
			FileUtil.getFile("shaders/point/point-frag.glsl"), 
			FileUtil.getFile("shaders/point/point-vert.glsl")
		);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		background(0);
		
		// update displacement texture
		perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
		// p.image(perlin.texture(), 0, 0);
		
		// rotate
//		p.translate(p.width/2f, p.height/2f, -width*1.5f);
		p.rotateY(0.4f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +

		// draw mesh with texture or without
//		shape.disableStyle();
//		p.noFill();
//		p.stroke(0, 255, 0);
//		p.strokeWeight(0.25f);
		if(percentComplete < 0.5) {
//			displacementShader.set("displacementMap", perlin.texture());
//			displacementShader.set("displaceStrength", 400f);
			p.shader(displacementShader);  
		}
		p.shape(shape);
		if(percentComplete < 0.5) p.resetShader();
	}
		
}
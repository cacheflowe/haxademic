package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_PointsPShapeAndTextureMapDeform 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PerlinTexture perlin;
	protected PShader pointsDisplacementShader;
	protected float _frames = 360;
	float size = 256;

	protected void overridePropsFile() {
//		p.appConfig.setProperty(AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE);
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// load texture
		perlin = new PerlinTexture(p, (int) size, (int) size);
		int vertices = P.round( size * size); 
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
			shape.vertex(x/size, y/size, 0); // x/y coords are used as UV coords, and multplied by `spread` uniform
		}
		shape.endShape();
		shape.setTexture(texture);
		
		// load shader
		pointsDisplacementShader = loadShader(
			FileUtil.getFile("shaders/point/point-frag.glsl"), 
			FileUtil.getFile("shaders/point/point-vert.glsl")
		);

//		background(0); // THIS MESSES UP BLENDING
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		p.blendMode(PBlendModes.BLEND);
		p.fill(0, 20);
		p.rect(0, 0, p.width, p.height);
		
		
		// update displacement texture
		perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, 100);
//		p.rotateX(0.1f * P.sin(percentComplete * P.TWO_PI)); // -P.HALF_PI +
		p.translate(-128, -128, 0);

//		p.blendMode(PBlendModes.ADD);
		
		// draw mesh with texture or without
//		shape.disableStyle();
//		p.noFill();
//		p.stroke(0, 255, 0);
//		p.strokeWeight(0.25f);
		pointsDisplacementShader.set("displacementMap2", perlin.texture());
		pointsDisplacementShader.set("displacementMap", DemoAssets.textureNebula());
		pointsDisplacementShader.set("pointSize", 2f + 1f * P.sin(P.TWO_PI * percentComplete));
		pointsDisplacementShader.set("spread", size * 1.f);
		pointsDisplacementShader.set("mixVal",  0.5f + 0.5f * P.sin(P.TWO_PI * percentComplete));
		pointsDisplacementShader.set("displaceStrength", 200f + 200f * P.sin(P.TWO_PI * percentComplete));
		p.shader(pointsDisplacementShader);  
		p.shape(shape);
		p.resetShader();
	}
		
}
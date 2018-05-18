package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class PointShaderTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// original code from: https://processing.org/tutorials/pshader/
	
	protected PShader pointShader;
	protected PShape pointsShape;
	protected PImage img;
	protected PerlinTexture perlinTexture;

	protected float _frames = 10;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	public void setup() {
		super.setup();
		
		perlinTexture = new PerlinTexture(p, 200, 200);
		perlinTexture.update(0.1f, 0.1f, P.sin(p.frameCount * 0.03f), P.cos(p.frameCount * 0.03f));

		img = p.loadImage(FileUtil.getFile("images/ello-tiny-edit.png"));
		
		pointsShape = p.createShape();
		pointsShape.beginShape();
		pointsShape.stroke(255);
		for (int x = 0; x < perlinTexture.texture().width; x++) {
			for (int y = 0; y < perlinTexture.texture().height; y++) {
				pointsShape.stroke(ImageUtil.getPixelColor(perlinTexture.texture(), x, y));
				pointsShape.fill(ImageUtil.getPixelColor(perlinTexture.texture(), x, y));
				pointsShape.vertex(x, y);
			}
		}
		pointsShape.endShape();

		pointsShape = Shapes.createSheet(10, perlinTexture.texture());

//		((PGraphics)g).textureWrap(Texture.REPEAT);

		pointShader = p.loadShader(
				FileUtil.getFile("haxademic/shaders/point/default-point-frag.glsl"), 
				FileUtil.getFile("haxademic/shaders/point/default-point-vert.glsl")
		);
		pointShader.set("textureInput", perlinTexture.texture());

		p.stroke(255);
		p.strokeWeight(1);
		p.background(0);
	}

	public void drawApp() {
		p.background(127);
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
		p.rotateX(0.2f * P.sin(P.p.millis() * 0.003f));
		
		perlinTexture.update(0.1f, 0.1f, P.sin(p.frameCount * 0.03f), P.cos(p.frameCount * 0.03f));

		pointShader.set("textureInput", img);
		p.shader(pointShader, PConstants.POINTS); // 
		
		
//		if (mousePressed) {   
//			p.point(mouseX, mouseY);
//		}
		
		p.shape(pointsShape, 0, 0);

//		for (int x = 0; x < img.width; x++) {
//			for (int y = 0; y < img.height; y++) {
//				p.stroke(ImageUtil.getPixelColor(img, x, y));
//				p.point(x, y);
//			}
//		}

		p.resetShader();
		p.popMatrix();
		
		p.image(perlinTexture.texture(), 0, 0);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {}
	}

}

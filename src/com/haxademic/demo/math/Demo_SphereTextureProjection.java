package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OrientationUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.SphericalCoord;
import com.haxademic.core.media.DemoAssets;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_SphereTextureProjection
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected float sphereRadius;
	protected PVector[] spherePointsFib;
	
	protected PImage texture;
	protected PShape icosa;
	protected PVector center = new PVector();


	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 800);
		Config.setProperty(AppSettings.HEIGHT, 800);
	}

	protected void firstFrame() {
		sphereRadius = p.width * 0.3f;
		spherePointsFib = SphericalCoord.buildFibonacciSpherePoints(200);
		
		// build icosahedron
		int detail = 5;
		texture = DemoAssets.textureJupiter();
		texture.loadPixels();
		icosa = Icosahedron.createIcosahedron(p.g, detail, texture);
		PShapeUtil.scaleShapeToHeight(icosa, sphereRadius * 2);
	}
	
	protected void drawApp() {
		p.background(200);
//		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// draw mesh sphere to show positions
		p.noStroke();
		p.strokeWeight(2);
		p.noFill();
		p.shape(icosa);
		
		// draw icosa triangles
		int vertexCount = icosa.getVertexCount();
		DebugView.setValue("vertexCount", vertexCount);
		p.g.beginShape(PConstants.TRIANGLES);
		for (int i = 0; i < vertexCount; i+=3) {
			PVector vertex1 = icosa.getVertex(i);
			PVector vertex2 = icosa.getVertex(i+1);
			PVector vertex3 = icosa.getVertex(i+2);
			p.g.vertex(vertex1.x, vertex1.y, vertex1.z, icosa.getTextureU(i),   icosa.getTextureV(i));
			p.g.vertex(vertex2.x, vertex2.y, vertex2.z, icosa.getTextureU(i+1), icosa.getTextureV(i+1));
			p.g.vertex(vertex3.x, vertex3.y, vertex3.z, icosa.getTextureU(i+2), icosa.getTextureV(i+2));
		}
		p.g.endShape();
		
		// sample UV coords & draw bars sampled to texture color
		p.noStroke();
		for (int i = 0; i < vertexCount; i+=3) {
			float u = (1 + icosa.getTextureU(i+1)) % 1;		// add one and mod to make up for negative UV coords
			float v = (1 + icosa.getTextureV(i+1)) % 1;
			int textureColor = ImageUtil.getPixelColor(texture, P.round(u * texture.width), P.round(v * texture.height));
			PVector vertex = icosa.getVertex(i);
			p.pushMatrix();
			float distScale = 1.0f;
			p.translate(vertex.x * distScale, vertex.y * distScale, vertex.z * distScale);
			OrientationUtil.setRotationTowards(p.g, vertex, center);
			p.rotateX(-P.HALF_PI);
			p.g.fill(textureColor);
			Shapes.drawPyramid(p.g, 20, 10, false);
//			p.g.box(3, 50 * p.red(textureColor)/255f, 3);
			p.popMatrix();
		}

	}

	public void mousePressed() {
		super.mousePressed();
	}
	
}

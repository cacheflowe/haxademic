package com.haxademic.sketch.pshape;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.PShapeUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class PShapeExportTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shapeTessellated;
	protected PShape shapeIcos;
	protected PImage img;
	protected float _frames = 240;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "800" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();	

		p.sphereDetail(10);
		shape = p.createShape(P.SPHERE, p.width/5f);		
		float extent = PShapeUtil.getSvgMaxExtent(shape);
		shapeIcos = Icosahedron.createIcosahedron(p, 4, img);
		PShapeUtil.scaleSvgToExtent(shapeIcos, extent);
		
		
		PShape newIcos = p.createShape();
		newIcos.beginShape(PConstants.TRIANGLES);
		PVector v = new PVector();
		for (int i = 0; i < shapeIcos.getVertexCount(); i+=3) {
			// get current vertex
			shapeIcos.getVertex(i, v);
			v.mult(4f); // scale up for noise since scaleSvgToExtent doesn't change the actual vertices
			float noiseExclude = p.noise(v.x, v.y, v.z);
			// selectively add faces depending on noise
			if(noiseExclude > 0.45f) {
				shapeIcos.getVertex(i, v);
				v.mult(extent * (1f + p.noise(v.x, v.y, v.z)));
				newIcos.vertex(v.x, v.y, v.z);
				shapeIcos.getVertex(i+1, v);
				v.mult(extent * (1f + p.noise(v.x, v.y, v.z)));
				newIcos.vertex(v.x, v.y, v.z);
				shapeIcos.getVertex(i+2, v);
				v.mult(extent * (1f + p.noise(v.x, v.y, v.z)));
				newIcos.vertex(v.x, v.y, v.z);
			}
		}
		newIcos.endShape();
		shapeIcos = newIcos;
	}

	public void drawApp() {
		background(255);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);

		// setup lights
		DrawUtil.setBetterLights(p);
		
		// icosahedron
		p.pushMatrix();
		p.translate(p.width/2f, p.height/2f);
		p.rotateY(percentComplete * P.TWO_PI);
		shapeIcos.disableStyle();
		p.fill(20);
		p.noStroke();
//		p.stroke(0);
		p.shape(shapeIcos);
		p.popMatrix();
		
		if(p.frameCount == 100) {
			PShapeUtil.exportMesh(shapeIcos);
		}
	}
	
}
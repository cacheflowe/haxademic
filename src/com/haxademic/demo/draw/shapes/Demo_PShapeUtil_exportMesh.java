package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_exportMesh 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shapeIcos;
	protected PImage img;

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, 240);
		Config.setProperty(AppSettings.WIDTH, 800);
		Config.setProperty(AppSettings.HEIGHT, 800);
	}

	protected void firstFrame() {
		p.sphereDetail(10);
		float extent = p.width / 5f;
		shapeIcos = Icosahedron.createIcosahedron(p.g, 4, img);
		PShapeUtil.scaleShapeToExtent(shapeIcos, extent);

		PShape newIcos = p.createShape();
		newIcos.beginShape(PConstants.TRIANGLES);
		newIcos.fill(255);
		PVector v = new PVector();
		for (int i = 0; i < shapeIcos.getVertexCount(); i += 3) {
			// get current vertex
			shapeIcos.getVertex(i, v);
			v.mult(4f); // scale up for noise since scaleSvgToExtent doesn't change the actual vertices
			float noiseExclude = p.noise(v.x, v.y, v.z);
			// selectively add faces depending on noise
			if (noiseExclude > 0.45f) {
				shapeIcos.getVertex(i, v);
				v.mult(1f + p.noise(v.x, v.y, v.z));
				newIcos.vertex(v.x, v.y, v.z);
				shapeIcos.getVertex(i + 1, v);
				v.mult(1f + p.noise(v.x, v.y, v.z));
				newIcos.vertex(v.x, v.y, v.z);
				shapeIcos.getVertex(i + 2, v);
				v.mult(1f + p.noise(v.x, v.y, v.z));
				newIcos.vertex(v.x, v.y, v.z);
			}
		}
		newIcos.endShape();
		shapeIcos = newIcos;
	}

	protected void drawApp() {
		background(0);

		// setup lights
		PG.setBetterLights(p);

		// icosahedron
		p.pushMatrix();
		p.translate(p.width / 2f, p.height / 2f);
		p.rotateY(FrameLoop.progressRads());
		shapeIcos.disableStyle();
		p.fill(255);
		p.noStroke();
		p.stroke(255, 0, 0);
		p.shape(shapeIcos);
		p.popMatrix();

		if (p.frameCount == 100) {
			PShapeUtil.exportMesh(shapeIcos);
		}
	}

}
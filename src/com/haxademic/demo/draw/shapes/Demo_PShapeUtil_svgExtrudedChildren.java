package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_svgExtrudedChildren 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape9;
	protected PShape shape8;
	protected PShape[] shapeClones;
	protected PShape allClones;
	protected PVector modelSize;
	protected boolean overrideColor = false;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// ORDER OF OPERATIONS REALLY MATTERS HERE...
		// * Color can be unavailable before tessellation(?!)
		// * The original pre-tessellated shape might not even draw right, with super jacked triangles (probably polygons that PShape thinks are tris?!)
		// * _Some_ shapes need the repair called to add the missing face, some don't
		// TODO: 
		// * Add as children to a PShape GROUP [DONE... but]
		// * How to properly center the shape?
		// * Figure out how to clone many instances for a particle system
		// * Use attributes to animate with a vertex shader?
		// * If animating with CPU, is it more efficient to translate the PShape or can we call p.translate on-the-fly?
		
		// build shape and assign texture
		shape9 = numberStyle1("svg/numbers/nine.svg");
		shape9 = shape9.getTessellation();
		PShapeUtil.centerShape(shape9);
		shape8 = numberStyle1("svg/numbers/eight.svg");
		shape8 = shape8.getTessellation();
		PShapeUtil.centerShape(shape8);
		
		// create clone
//		shapeClone = PShapeCopy.copyShape(shape);
		shapeClones = new PShape[1500];
		for (int i = 0; i < shapeClones.length; i++) {
			// create new clones
			shapeClones[i] = (i % 2 == 0) ?
					PShapeUtil.clonePShape(P.p, shape8) :
					PShapeUtil.clonePShape(P.p, shape9);
			
			// position clones as they're created
			PShapeUtil.offsetShapeVertices(
					shapeClones[i],
				P.sin(i/3f) * p.width * 0.5f,  
				-p.height/2 + (float) i * ((float) p.height / shapeClones.length), 
				P.cos(i/3f) * p.width * 0.5f
			);
		}
		
		// add to big group
		allClones = PShapeUtil.addShapesToGroup(shapeClones);
		P.out("vertexCount allClones", PShapeUtil.vertexCount(allClones));
		// normalize shape (scaling centers)
//		P.out("vertexCount", PShapeUtil.vertexCount(shapeClone));
	}
	
	protected PShape numberStyle1(String file) {
		PShape newShape = p.loadShape(FileUtil.getPath(file));
		ArrayList<PShape> shapeChildren = new ArrayList<PShape>();
//		PShapeUtil.centerShape(shape);
		for (int j = 0; j < newShape.getChildCount(); j++) {
			// transform to triangles and grab original color
			PShape subShape = newShape.getChild(j);
			PShape subShapeCopy = subShape.getTessellation();
			int shapeColor = subShapeCopy.getFill(0);
			// repair missing vertex from tessellation
			PShapeUtil.repairMissingSVGVertex(subShapeCopy);
			// extrude and store new shape
			PShape childShape = PShapeUtil.createExtrudedShape(subShapeCopy, 2 + 2 * j);
			shapeChildren.add(childShape);
			// copy original material color to new shape
			PShapeUtil.setMaterialColor(childShape, shapeColor);
			// scale up. only do this for this particular style 
//			PShapeUtil.scaleShapeToHeight(childShape, p.height * 0.4f);
//			shapeClone = PShapeCopy.copyGeo(childShape);
		}
		
		// put children into a shape group
		PShape shapeGrouped = PShapeUtil.addShapesToGroup(shapeChildren);
		PShapeUtil.scaleShapeToHeight(shapeGrouped, p.height * 0.05f);
		PShapeUtil.centerShape(shapeGrouped);
		P.out("vertexCount", PShapeUtil.vertexCount(shapeGrouped));
		return shapeGrouped;
	}
		
	protected void drawApp() {
		// clear the screen
		background(20);
		p.noStroke();
		PG.setCenterScreen(p);
		PG.setDrawCorner(p.g);
		PG.setBetterLights(p.g);
		PG.basicCameraFromMouse(p.g, 0.9f);

		// draw original shape
		p.shape(shape9);
		
		// draw clones
		p.shape(allClones);
		
		// draw clones
//		for (int i = 0; i < shapeClones.length; i++) {
//			float radius = 300 + 100f * P.sin(FrameLoop.count(0.03f) + i/200f);
//			PShape clone = shapeClones[i];
//			p.push();
//			p.translate(
//				P.sin(i/3f) * radius,  
//				-p.height / 2f + (float) i * ((float)p.height / shapeClones.length), 
//				P.cos(i/3f) * radius
//			);
//			p.rotateY(FrameLoop.count(0.03f) + i/200f);
//			p.shape(clone);
//			p.pop();
//		}
	}
	
}
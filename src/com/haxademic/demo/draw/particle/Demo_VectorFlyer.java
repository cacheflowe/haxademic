package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.VectorFlyer;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_VectorFlyer 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected int attractorsCount = 0;
	
	protected PVector center = new PVector();
	protected ArrayList<PVector> attractors;
	protected ArrayList<VectorFlyer> flyers;


	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 160 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		shape = Icosahedron.createIcosahedron(p.g, 4, null);
//		shape = DemoAssets.objHumanoid();
		PShapeUtil.scaleShapeToExtent(shape, p.displayHeight * 0.3f);
		
		initFlyers();
	}
	
	protected void initFlyers() {
		attractors = new ArrayList<PVector>();
		addPointsToAttractors(shape);
		
		flyers = new ArrayList<VectorFlyer>();
		for( int i=0; i < 300; i++ ) {
			flyers.add( new VectorFlyer( p.random(0.15f, 0.7f), p.random(12f, 15f) ) );
		}
	}

	protected boolean attractorExists(PVector p) {
		for (int i = 0; i < attractors.size(); i++) {
			if(p.dist(attractors.get(i)) == 0) return true;
		}
		return false;
	}
	
	// recurse through mesh points
	public void addPointsToAttractors(PShape shape) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector point = shape.getVertex(i);
			if(attractorExists(point) == false) {
				attractors.add( point ); 
				attractorsCount++;
				DebugView.setValue("attractorsCount", attractorsCount);
			}
		}
			
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			addPointsToAttractors(subShape);
		}
	}

	protected void drawApp() {
		background(0);
		
		// setup lights
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		p.translate(0, 0, -p.width/2);
		PG.basicCameraFromMouse(p.g);

		// update attributes
		float speedScale = 1;
		for( int i=0; i < flyers.size(); i++ ) {
			flyers.get(i).setSpeed((9 + 0.01f * i) * speedScale);
			flyers.get(i).setAccel((0.8f + 0.0001f * i) * speedScale);
		}

		
		// set target of particle to closest attractor
		for( int i=0; i < flyers.size(); i++ ) {
			flyers.get(i).setTarget( flyers.get(i).findClosestPoint( attractors ) );
			if(p.keyPressed && p.key == ' ') flyers.get(i).setTarget(center);
			flyers.get(i).update(p.g, true, true);
		}
		
		// icosahedron
		shape.disableStyle();
		p.noFill();
		p.stroke(255, 60);
//		p.shape(shapeIcos);
		PShapeUtil.drawTrianglesAudio(p.g, shape, 1, p.color(255));

		// draw attract points
		p.fill(100,200,100);
		p.noStroke();
		for (int i = 0; i < attractors.size(); i++) {
			p.pushMatrix();
			p.translate(attractors.get(i).x, attractors.get(i).y, attractors.get(i).z);
			p.box(10);
			p.popMatrix();
		}
	}
		
}
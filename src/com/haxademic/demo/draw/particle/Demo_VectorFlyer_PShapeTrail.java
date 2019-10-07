package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.VectorFlyer;
import com.haxademic.core.draw.shapes.LineTrail;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_VectorFlyer_PShapeTrail 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected int attractorsCount = 0;
	
	protected PVector center = new PVector();
	protected ArrayList<PVector> attractors;
	protected ArrayList<VectorFlyer> flyers;
	protected ArrayList<LineTrail> trails;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 160 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		shape = DemoAssets.objHumanoid();
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.displayHeight * 0.8f);
		
		initFlyers();
	}
	
	protected void initFlyers() {
		attractors = PShapeUtil.getUniqueVertices(shape);
		flyers = new ArrayList<VectorFlyer>();
		trails = new ArrayList<LineTrail>();
		for( int i=0; i < 500; i++ ) {
			flyers.add( new VectorFlyer( p.random(0.15f, 0.7f), p.random(12f, 15f) ) );
			trails.add(new LineTrail(20));
		}
	}

	public void drawApp() {
		background(0);
		
		// setup lights
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		p.translate(0, 0, -p.width/2);
		PG.basicCameraFromMouse(p.g);

		// update attributes
		float speedScale = 1;
		for( int i=0; i < flyers.size(); i++ ) {
			flyers.get(i).setSpeed((4 + 0.01f * i) * speedScale);
			flyers.get(i).setAccel((0.99f + 0.0001f * i) * speedScale);
		}

		
		// set target of particle to closest attractor
		for( int i=0; i < flyers.size(); i++ ) {
			// update flyers
			flyers.get(i).setTarget( flyers.get(i).findClosestPoint( attractors ) );
			if(p.keyPressed && p.key == ' ') flyers.get(i).setTarget(center);
			flyers.get(i).update(p.g, false, false);
			
			// draw trail
			p.stroke(255);
			p.strokeWeight(1.3f);
			trails.get(i).update(p.g, flyers.get(i).position());
		}
		
		// fraw object
		shape.disableStyle();
		p.fill(0, 200, 0, 100 + 100 * P.sin(p.frameCount * 0.1f));
//		p.stroke(0, 100 + 100 * P.sin(p.frameCount * 0.1f), 0);
		p.noStroke();
		p.strokeWeight(1);
//		p.shape(shapeIcos);
		PShapeUtil.drawTriangles(p.g, shape, null, 1);
//		PShapeUtil.drawTrianglesAudio(p.g, shape, 1, p.color(255));

//		// draw attract points
//		p.fill(100,200,100);
//		p.noStroke();
//		for (int i = 0; i < attractors.size(); i++) {
//			p.pushMatrix();
//			p.translate(attractors.get(i).x, attractors.get(i).y, attractors.get(i).z);
//			p.box(10);
//			p.popMatrix();
//		}
	}
		
}
package com.haxademic.sketch.particle;

import java.util.ArrayList;
import java.util.List;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.particle.VectorFlyer;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PVector;
import wblut.external.ProGAL.AlphaComplex;
import wblut.external.ProGAL.CTriangle;
import wblut.external.ProGAL.Point;

public class ParticleAlphaShape
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public ArrayList<VectorFlyer> boxes;
	public ArrayList<Attractor> attractors;
	public ArrayList<PVector> attractorsPositions;

	protected float _numAttractors = 4;
	protected float _numParticles = 50;
	
	List<Point> points;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, false );

		
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
//		p.appConfig.setProperty( AppSettings.FPS, 30 );
	}

	public void setupFirstFrame() {


		initFlyers();
	}

	protected void initFlyers() {
		attractors = new ArrayList<Attractor>();
		attractorsPositions = new ArrayList<PVector>();
		for( int i=0; i < _numAttractors; i++ ) {
			attractors.add( new Attractor( new PVector() ) );
			attractorsPositions.add( attractors.get(i).position() );
		}

		boxes = new ArrayList<VectorFlyer>();
		for( int i=0; i < _numParticles; i++ ) {
			PVector pos = new PVector( MathUtil.randRange(-600, 600), MathUtil.randRange(-600, 600), -2500 );
			boxes.add( new VectorFlyer( p.random(3.5f, 5.5f), p.random(10f, 50f), pos ) );
			boxes.get(i).setVector( new PVector( MathUtil.randRange(-10, 10), MathUtil.randRange(-10, 10), MathUtil.randRange(-10, 10) ) );
		}
		
		points = new ArrayList<Point>();
		for( int i=0; i < _numParticles; i++ ) {
			points.add( new Point(0,0,0) );
		}
	}


	public void drawApp() {
		if(joons == null) p.background(0);
		p.lights();

//		_jw.jr.background(25, 25, 25);
		joons.jr.background(JoonsWrapper.BACKGROUND_GI);

		setUpRoom();
		
		translate(0, 0, -400);

		// set target of particle to closest attractor
		VectorFlyer box = null;
		for( int i=0; i < boxes.size(); i++ ) {
			box = boxes.get(i);
			box.setTarget( box.findClosestPoint( attractorsPositions ) );
			box.update( p.g, false, false );
		}
		for( int i=0; i < attractors.size(); i++ ) attractors.get(i).update( false );
		
//		drawVoxels();
//		drawProximitySticks();
		drawAlphaShape( true );
	}

	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, -2000);
		float radiance = 20;
		int samples = 16;
		joons.jr.background("cornell_box", 
				4000, 3000, 3000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, // left rgb
				40, 40, 40, // right rgb
				60, 60, 60, // back rgb
				60, 60, 60, // top rgb
				60, 60, 60  // bottom rgb
		); 
		popMatrix();		
	}
	
	protected void drawVoxels() {
		
		ArrayList<PVector> voxelPositions = new ArrayList<PVector>();
		ArrayList<Integer> voxelDecay = new ArrayList<Integer>();
		float resolution = 50;
		
		PVector checkVector;
		PVector roundedPos = new PVector();
		for( int i=0; i < _numParticles; i++ ) {
			checkVector = boxes.get(i).position();
			roundedPos.set(
					Math.round(checkVector.x / resolution) * resolution, 
					Math.round(checkVector.y / resolution) * resolution, 
					Math.round(checkVector.z / resolution) * resolution
					);
			
			boolean foundMatch = false;
			for( int j=0; j < voxelPositions.size(); j++ ) {
				if( roundedPos.equals(voxelPositions.get(j))) {
					foundMatch = true;
				}
			}
			if( foundMatch == false ) {
				voxelPositions.add( new PVector( roundedPos.x, roundedPos.y, roundedPos.z ) );
				voxelDecay.add(1);
			}
				
		}
		
		for( int i=0; i < voxelPositions.size(); i++ ) {
			// PVector worldCenter = new PVector();
			PVector voxel = voxelPositions.get(i);
			// P.println(i+"  "+voxel.x+"  "+ Math.abs(Math.round(voxel.x / resolution)));
//			if( Math.abs(Math.round(voxel.x / resolution)) == 0 &&  Math.abs(Math.round(voxel.y / resolution)) == 0 ) { 
//				p.fill(255, 255, 255);
//				_jw.jr.fill(JoonsWrapper.MATERIAL_LIGHT, 255, 255, 255, 4);
//			} else 
			if( Math.abs(Math.round(voxel.x / resolution)) % 2 == 0 ||  Math.abs(Math.round(voxel.y / resolution)) % 2 == 0 ) {
				p.fill( Math.abs(voxel.x)/4, Math.abs(voxel.y)/3, Math.abs(voxel.z)/2);
				joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, Math.abs(voxel.x)/3, Math.abs(voxel.y)/3, Math.abs(voxel.z)/2);
			} else { 
				p.fill(20, 20, 20);
				joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 20, 20, 20, 1);
			}
			
			p.pushMatrix();
//			p.rotateZ(p.frameCount / 130f);
			p.translate(voxelPositions.get(i).x, voxelPositions.get(i).y, voxelPositions.get(i).z);
//			p.sphere(resolution/2f);
			p.box(resolution);
			p.popMatrix();
		}
	}
	
	protected void drawProximitySticks() {
		DebugUtil.printErr("This probably wont work anymore with Joons since Shapes.boxBetween() uses p.g instead of p"); 
		// set target of particle to closest attractor
		VectorFlyer box = null;
		VectorFlyer boxCheck = null;
		for( int i=0; i < boxes.size(); i++ ) {
			for( int j=0; j < boxes.size(); j++ ) {
				box = boxes.get(i);
				boxCheck = boxes.get(j);
				if( box != boxCheck ) {
					if( box.position().dist( boxCheck.position() ) < 200 ) {
						joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 190, 190, 190, 0.55f);
						Shapes.boxBetween(p.g, box.position(), boxCheck.position(), 20 );
						joons.jr.fill(JoonsWrapper.MATERIAL_DIFFUSE, 90, 90, 90);
						p.pushMatrix();
						p.translate(box.position().x, box.position().y, box.position().z);
						p.sphere(15);
						p.popMatrix();
						p.pushMatrix();
						p.translate(boxCheck.position().x, boxCheck.position().y, boxCheck.position().z);
						p.sphere(15);
						p.popMatrix();
					}
				}
			}
		}
	}

	protected void drawAlphaShape( boolean complex ) {
		for( int i=0; i < _numParticles; i++ ) {
			points.get(i).setX( boxes.get(i).position().x );
			points.get(i).setY( boxes.get(i).position().y );
			points.get(i).setZ( boxes.get(i).position().z );
		}

		if( complex == false ) {
			AlphaComplex af = new AlphaComplex(points);
			List<CTriangle> triangles = af.getAlphaShape(200.8);
			for(CTriangle tri: triangles) {
				p.fill( 50, 200, 50 );
				joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 50, 200, 50, 0.75f);
				beginShape(TRIANGLES);

				vertex( (float) tri.getP1().x(), (float) tri.getP1().y(), (float) tri.getP1().z() );
				vertex( (float) tri.getP2().x(), (float) tri.getP2().y(), (float) tri.getP2().z() );
				vertex( (float) tri.getP3().x(), (float) tri.getP3().y(), (float) tri.getP3().z() );

				endShape();
			}
		} else {
			// draw alpha complex
			AlphaComplex ac = new AlphaComplex(points, 200.8);
			for(CTriangle tri: ac.getTriangles()){		
				p.fill( 50, 200, 50 );
//				if(MathUtil.randBoolean() == true) {
					joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 190, 210, 190, 0.25f);
//				} else {
//					_jw.jr.fill(JoonsWrapper.MATERIAL_GLASS, 255, 255, 255);
//				}
				beginShape(TRIANGLES);

				vertex( (float) tri.getP1().x(), (float) tri.getP1().y(), (float) tri.getP1().z() );
				vertex( (float) tri.getP2().x(), (float) tri.getP2().y(), (float) tri.getP2().z() );
				vertex( (float) tri.getP3().x(), (float) tri.getP3().y(), (float) tri.getP3().z() );

				endShape();
			}
		}
	}


	public class Attractor {
		protected PVector position = new PVector();
		protected float randDivisor;

		public Attractor( PVector newPosition ) {
			position.set( newPosition );
			randDivisor = MathUtil.randRangeDecimal(600, 1000);
			resetPos();
		}

		public PVector position() {
			return position;
		}

		public void resetPos() {
			position.x = MathUtil.randRange(-600, 600);
			position.y = MathUtil.randRange(-600, 600);
			position.z = MathUtil.randRange(-2500, -3000);
		}
		
		public void update( boolean draws ) {
//			position.x = P.sin(p.millis() / randDivisor) * 500;
//			position.y = P.sin(p.millis() / (randDivisor*2)) * 500;
//			position.z = -2000 + P.sin(p.millis() / (randDivisor*2.5f)) * 500;
			if(p.frameCount % 100 == 0) {
				resetPos();
			}
			
			if( draws == true ) {
				p.fill( 255, 255, 255 );
				p.noStroke();
				p.pushMatrix();
				p.translate(position.x, position.y, position.z);
				p.box(30);
				p.popMatrix();
			}
		}
	}


}
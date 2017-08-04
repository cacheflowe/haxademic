package com.haxademic.app.haxvisual.viz.modules;

import java.util.Iterator;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.AABB;
import toxi.geom.AxisAlignedCylinder;
import toxi.geom.Cone;
import toxi.geom.Matrix4x4;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.SphericalHarmonics;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.camera.CameraOscillate;
import com.haxademic.core.camera.common.ICamera;
import com.haxademic.core.hardware.midi.MidiWrapper;


public class Toxi
	extends ModuleBase
	implements IVizModule
	{
		protected ToxiclibsSupport gfx;
		// class props
		protected Plus[] _blocks;
		protected int _cols = 10;
		protected int _rows = 10;
		protected ICamera camera;
		protected int NUM_BLOCKS = 20;  
		protected float rotInc = 0;
		protected float baseRadius = 400f;
		protected float radiusAmpAmount = 0f;
		protected float baseSize = 100f;
		protected float boxBaseLength = 10000f;
		protected float _rRot = .1f;
		protected float _gRot = .1f;
		protected float _bRot = .1f;
		protected Shape shape;

		public Toxi( )
		{
			super();
			// store and init audio engine
			initAudio();

			// init viz
			init();
		}

		public void init() {
			p.colorMode( PConstants.RGB, 1, 1, 1, 1 );

			p.noStroke();
			p.shininess(1000); 
			p.lights();
			
			gfx = new ToxiclibsSupport(p);	
			
			// create background cell objects
			_blocks = new Plus[ NUM_BLOCKS ];
			float cellW = p.width/_cols;
			float cellH = p.height/_rows;
			int tmpIndex = 0;
			
			// Initialize each object
			for ( int i = 0; i < NUM_BLOCKS; i++ ) 
			{
				_blocks[i] = new Plus( cellW, cellH, tmpIndex );
				tmpIndex++;
			}
			
			shape = new Shape();
			
			// set up camera
			camera = new CameraOscillate( p, 0, 0, -500, 400 );

		}

		public void initAudio()
		{
			_audioData.setNumAverages( NUM_BLOCKS );
			_audioData.setDampening( .13f );
		}

		public void focus() {
			p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
			p.camera();
			p.noStroke();
			initAudio();
			pickMode();
		}

		public void update() {
			//p.rectMode(PConstants.CENTER);
			p.noStroke();
			//p.background(0,0,0,1);
			
			//rotInc += .01;
			//p.rotateX( rotInc );
			

			
			AABB cube;
			AxisAlignedCylinder cyl;
			Cone cone,cone2;
			Sphere ball, ball1;
			TriangleMesh mesh;
			TriangleMesh meshTri;
			 
			p.background(0);
			p.shininess(500); 
			p.lights();
			p.ambientLight(0.2f,0.2f,0.2f, 0, 0, 6000);
			p.ambientLight(0.2f,0.2f,0.2f, 0, 0, -6000);

			p.translate(p.width/2,p.height/2,0);
			p.rotateX(p.mouseY*0.01f);
			p.rotateY(p.mouseX*0.01f);
			p.noStroke();
			 
			float test = -_audioData.getFFT().averages[10]*20*baseSize;
			
			cone=new Cone(new Vec3D(0,test + 200,0), new Vec3D(0,-test,0), 50, 100, 50);
			gfx.cone(cone,10,false);
			cone2=new Cone(new Vec3D(0,-test - 200,0), new Vec3D(0,test,0), 50, 100, 50);
			gfx.cone(cone2,10,true);
			 
			/*
			SurfaceFunction f = new SuperEllipsoid(0.5f,0.3f);
			mesh = new SurfaceMeshBuilder(f).createMesh((int)40);
			meshTri.transform(new Matrix4x4().translate(0,0,200));
			gfx.mesh(mesh,true,10);
			*/
			 
			 /*
			p.stroke(255,255,0);
			Ray3D ray=new Ray3D(new Vec3D(),Vec3D.Y_AXIS);
			gfx.ray(ray,200);
			*/
			/*
			p.stroke(0,255,255);
			Spline3D spline=new Spline3D();
			spline.add(cube).add(ball).add(cone).add(cyl.getPosition()).add(mesh.getCentroid());
			gfx.lineStrip3D(spline.computeVertices(16));
			*/
		  	p.pushMatrix();
			// Oscillate and display each object
			p.noStroke();
			for (int i = 0; i < NUM_BLOCKS; i++) {
				_blocks[i].update( i, NUM_BLOCKS, _audioData.getFFT().averages[i] * 100 );
			}
		  	p.popMatrix();
			// center ball
			ball1=new Sphere(new Vec3D(0,0,0),test);
			gfx.sphere(ball1,10);

			//shape.draw();
			
			// outer shell
		  	p.fill( 0.1f, 1 );
			ball=new Sphere(new Vec3D(0,0,0),10000);
			gfx.sphere(ball,10);
			
			// outer shell
			p.fill( 0.9f, 0 );
			p.stroke( 0.9f, 0.1f );
			ball=new Sphere(new Vec3D(0,0,0),8000);
			gfx.sphere(ball,10);
			p.noStroke();
			  
			camera.update();
			  
			  
			// lets us use the keyboard to funk it up
			if( p.keyPressed ) handleKeyboardInput();
		}

		public void handleKeyboardInput()
		{
			if ( p.key == 'm' || p.key == 'M' || p.midi.midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
				pickMode();
			}
			if ( p.key == 'v' || p.key == 'V' || p.midi.midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
				newCamera();
			}
			if ( p.key == 'c' || p.key == 'C' || p.midi.midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.midi.midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
				newColor();
//				for (int i = 0; i < NUM_BLOCKS; i++) {
//					_blocks[i].update( i, NUM_BLOCKS, _audioData.getFFT().averages[i] * 100 );
//				}
			}
		}
		
		void pickMode()
		{
			//shape.randomizeMesh();
			newColor();
			// new radius
			baseRadius = p.random(400, 1000);
			radiusAmpAmount = p.random(0, 3000);
			baseSize = p.random(100, 600);
			boxBaseLength = p.random(3000, 30000);
			// set up camera
			newCamera();
		}
		
		void newCamera()
		{
			// set up camera
			camera = new CameraOscillate( p, 0, (int)p.random(-300f, 300f), (int)p.random(-600f, 600f), (int)p.random(-2000f, 2000f) );
		}

		void newColor()
		{
			// color speeds
			_rRot = p.random(.05f, .2f);
			_gRot = p.random(.05f, .2f);
			_bRot = p.random(.05f, .2f);
			for (int i = 0; i < NUM_BLOCKS; i++) {
				_blocks[i].newColor();
			}

		}

		public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
		{

		}

		// A Cell object
		class Plus 
		{
			// A cell object knows about its location in the _blocks as well as its size with the variables x,y,w,h.
			float w,h;   // p.width and p.height
			float rotX, rotY, rotZ; // current rotation 
			int index; // angle for oscillating brightness
			
			// Cell Constructor
			Plus( float tempW, float tempH, int tempIndex ) {
				w = tempW;
				h = tempH;
				index = tempIndex;
				rotX = rotY = rotZ = 0;
			} 

			/**
			 * Place and draw each block
			 * @param index
			 * @param total
			 */
			void update( int index, int total, float audioAmp ) 
			{				
				// define size & position based on amplitude
				float radiusAmp = baseRadius;// + audioAmp * radiusAmpAmount;
				
				// place in ring
				float angle = ( 2.0f * (float) Math.PI ) * ( (float) index / (float) total );
				float x = (radiusAmp * p.sin( angle ));
				float y = (radiusAmp * p.cos( angle ));
				
				// rotate color
				rotX += .01 * index * _rRot;
				rotY += .01 * index * _gRot;
				rotZ += .01 * index * _bRot;
				
				// draw square
				p.fill(  .9f + p.sin( rotX ) * .3f, 1f + p.cos( rotY ) * .3f, 1f + p.sin( rotZ ) * .2f, 1 );
				
				AABB cube;
				//Sphere ball;
				for(int i=1; i<15; i++) {
					float size = audioAmp*(120 - i*10);
					cube = new AABB(new Vec3D(x*i,y*i,0),new Vec3D(50+i*20,50+i*20,size));
					cube.rotateY(angle);
					gfx.box(cube);
					
//					ball=new Sphere(new Vec3D(x*i,y*i,0),size);
//					gfx.sphere(ball,8);

				}
			}
			
			public void newColor() 
			{
				float colorJump = p.random(0.5f, 2);
				rotX += colorJump;
				rotY += colorJump;
				rotZ += colorJump;
			}
		}
		
		// A Cell object
		class Shape 
		{
			TriangleMesh mesh = new toxi.geom.mesh.TriangleMesh();
			 
			boolean isWireFrame = true;
			boolean showNormals = true;
			 
			Matrix4x4 normalMap = new Matrix4x4().translateSelf(128,128,128).scaleSelf(127);
			
			// Cell Constructor
			Shape() {
				randomizeMesh();
			} 
			 
			void draw() {
			  p.shininess(16);
			  p.directionalLight(255,255,255,0,-1,1);
			  p.specular(255);
			  if (isWireFrame) {
				  p.noFill();
				  p.stroke(255);
			  }
			  else {
				  p.fill(255);
				  p.noStroke();
			  }
			  drawMesh(p.g, mesh, isWireFrame, showNormals);
			}
			 
			void randomizeMesh() {
			  float[] m=new float[8];
			  for(int i=0; i<8; i++) {
			    m[i]=(int)p.random(9);
			  }
			  SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
			  mesh = (TriangleMesh) b.createMesh(mesh,80,4);
			}
			 			 
			void drawMesh(PGraphics gfx, TriangleMesh mesh, boolean vertexNormals, boolean showNormals) {
			  gfx.beginShape(PConstants.TRIANGLES);
			  AABB bounds=mesh.getBoundingBox();
			  Vec3D min=bounds.getMin();
			  Vec3D max=bounds.getMax();
			    for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
			    	Face f=(Face)i.next();
			      Vec3D n = normalMap.applyTo(f.a.normal);
			      gfx.fill(n.x, n.y, n.z);
			      gfx.normal(f.a.normal.x, f.a.normal.y, f.a.normal.z);
			      gfx.vertex(f.a.x, f.a.y, f.a.z);
			      n = normalMap.applyTo(f.b.normal);
			      gfx.fill(n.x, n.y, n.z);
			      gfx.normal(f.b.normal.x, f.b.normal.y, f.b.normal.z);
			      gfx.vertex(f.b.x, f.b.y, f.b.z);
			      n = normalMap.applyTo(f.c.normal);
			      gfx.fill(n.x, n.y, n.z);
			      gfx.normal(f.c.normal.x, f.c.normal.y, f.c.normal.z);
			      gfx.vertex(f.c.x, f.c.y, f.c.z);
			    }
//			    for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
//			    	Face f=(Face)i.next();
//			      gfx.normal(f.normal.x, f.normal.y, f.normal.z);
//			      gfx.vertex(f.a.x, f.a.y, f.a.z);
//			      gfx.vertex(f.b.x, f.b.y, f.b.z);
//			      gfx.vertex(f.c.x, f.c.y, f.c.z);
//			    }
//		      for (Iterator i=mesh.faces.iterator(); i.hasNext();) {
//		    	  Face f=(Face)i.next();
//		        Vec3D c = f.a.add(f.b).addSelf(f.c).scaleSelf(1f / 3);
//		        Vec3D d = c.add(f.normal.scale(5));
//		        Vec3D n = f.normal.scale(127);
//		        gfx.stroke(n.x + 128, n.y + 128, n.z + 128);
//		        gfx.line(c.x, c.y, c.z, d.x, d.y, d.z);
//		      }
			  gfx.endShape();
			}
		}
	}
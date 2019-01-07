package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.draw.camera.CameraOscillate;
import com.haxademic.core.draw.camera.common.ICamera;

import processing.core.PConstants;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

public class PlusRing
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

		public PlusRing( )
		{
			super();
			// store and init audio engine
			initAudio();

			// init viz
			init();
		}

		public void init() {
			gfx = new ToxiclibsSupport(p);	
			p.colorMode( PConstants.RGB, 1, 1, 1, 1 );

			p.noStroke();
			p.shininess(1000); 
			p.lights();
			
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
			
			// set up camera
			camera = new CameraOscillate( p, 0, 0, -500, 400 );

		}

		public void initAudio()
		{
//			audioData.setNumAverages( NUM_BLOCKS );
//			audioData.setDampening( .13f );
		}

		public void focus() {
			p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
			p.camera();
			p.noStroke();
			initAudio();
			pickMode();
		}

		public void update() {
			p.rectMode(PConstants.CENTER);
			p.noStroke();
			p.background(0,0,0,1);
			
			p.shininess(500); 
			p.lights();
			float lightAmp = 0.3f;
			p.ambientLight(lightAmp,lightAmp,lightAmp, 0, 0, 6000);
			p.ambientLight(lightAmp,lightAmp,lightAmp, 0, 0, -6000);

			
			p.pushMatrix();
			
			rotInc += .01;
			p.rotateX( rotInc );
			
			// put it all in a huge cube
		  	p.fill( 0.1f, 1 );
		  	p.stroke( 1, 0.2f );
			p.strokeWeight(2);
			//gfx.sphere(new Sphere(new Vec3D(0,0,0),20000),10);
			gfx.box( new AABB(new Vec3D(0,0,0),new Vec3D(20000,20000,20000)) );
			p.noStroke();

			
			// Oscillate and display each object
			for (int i = 0; i < NUM_BLOCKS; i++) {
				_blocks[i].update( i, NUM_BLOCKS, p.audioFreq(i) );
			}
			
			camera.update();
			
			p.popMatrix();
			
			// lets us use the keyboard to funk it up
			if( p.keyPressed ) handleKeyboardInput();
		}

		public void handleKeyboardInput()
		{
			if ( p.key == 'm' || p.key == 'M') {
				pickMode();
			}
		}
		
		void  pickMode()
		{
			// color speeds
			_rRot = p.random(.05f, .2f);
			_gRot = p.random(.05f, .2f);
			_bRot = p.random(.05f, .2f);
			// new radius
			baseRadius = p.random(400, 3000);
			radiusAmpAmount = p.random(0, 3000);
			baseSize = p.random(100, 600);
			boxBaseLength = p.random(3000, 30000);
			// set up camera
			camera = new CameraOscillate( p, 0, 0, (int)p.random(-600f, 600f), (int)p.random(-1000f, 1000f) );
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
				
				p.pushMatrix();
				
				// define size & position based on amplitude
				float radiusAmp = baseRadius + audioAmp * radiusAmpAmount;
				float boxLength = audioAmp * boxBaseLength;
				float boxDepth = baseSize + (50 * audioAmp);
				float boxHeight = baseSize/2 + (50 * audioAmp);
				
				// place in ring
				float angle = ( 2.0f * (float) Math.PI ) * ( (float) index / (float) total );
				float x = (p.width/2) + (radiusAmp * p.sin( angle ));
				float y = (p.height/2) + (radiusAmp * p.cos( angle ));
				p.translate( x, y );
				
				// rotate color
				rotX += .01 * index * _rRot;
				rotY += .01 * index * _gRot;
				rotZ += .01 * index * _bRot;
				
				// draw square
				p.fill(  .4f + p.sin( rotX ) * .5f, .8f + p.cos( rotY ) * .5f, .9f + p.sin( rotZ ) * .5f, 1 );
				p.box( boxHeight, boxDepth, boxLength );
				p.box( boxDepth, boxHeight, boxLength );
				
				p.popMatrix();
			}
		}
		
	}
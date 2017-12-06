package com.haxademic.app.musicvideos;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.EasingTColor;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PConstants;
import processing.core.PImage;
import processing.video.Movie;
import toxi.color.TColor;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;
import toxi.math.noise.PerlinNoise;

public class BrimLiskiRepetitions
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
	 * Image sequence
	 */
	protected ArrayList<String> _images;
	
	/**
	 * Image path
	 */
	protected String _videoFile;
	
	/**
	 * Video object
	 */
	protected Movie _myMovie = null;
	
	/**
	 * Image sequence index
	 */
	protected int _frameIndex;
	
	/**
	 * Radians of the wind
	 */
	protected EasingFloat _windRadians;
	
	/**
	 * Array of Particles
	 */
	protected ArrayList<Particle> _particles;
	
	/**
	 * Array of frame/radians
	 */
	protected ArrayList<FrameRadians> _frameRadians;

	/**
	 * Current radians array index
	 */
	protected int _frameRadiansIndex;

	/**
	 * Last Frame
	 */
	protected FrameRadians _finalFrame;

	protected int NUM_PARTICLES = 50000;
	
	// Mesh version =====================================
	protected WETriangleMesh _mesh;
	protected WETriangleMesh _meshDeform;
	
	public void settings() {
		customPropsFile = FileUtil.getHaxademicDataPath() + "properties/brimliskirepetitions.properties";
		super.settings();
	}

	// INITIALIZE OBJECTS ===================================================================================
	public void initRender() {
		_videoFile = p.appConfig.getString( "video_file", "" );
		_frameIndex = 0;
		_myMovie = new Movie( this, _videoFile );
		_myMovie.play();
		_myMovie.frameRate( _fps );
		_myMovie.pause();
		
		buildRadiansForFrames();
		
//		_windRadians = MathUtil.randRange( 0, 2 * (float) Math.PI );
		
		_particles = new ArrayList<Particle>();
		for( int i = 0; i < NUM_PARTICLES; i++ ) {
			_particles.add( new Particle(i) );
		}
		
		_myMovie.read();
		for( int i = 0; i < NUM_PARTICLES; i++ ) {
			_particles.get(i).reset();
		}
		
		createMesh();
	}
	
	protected void buildRadiansForFrames() {
		_windRadians = new EasingFloat( 0, 10 );
		_frameRadians = new ArrayList<FrameRadians>();
		_frameRadiansIndex = 0;
		
		_frameRadians.add( new FrameRadians( 0, 0, 0.00f, 45 ) );
		_frameRadians.add( new FrameRadians( 0, 6, 0.90f, 135 ) );
		_frameRadians.add( new FrameRadians( 0, 11, 0.70f, 350 ) );
		_frameRadians.add( new FrameRadians( 0, 17, 0.23f, 250 ) );
		_frameRadians.add( new FrameRadians( 0, 22, 0.86f, 0 ) );
		_frameRadians.add( new FrameRadians( 0, 28, 0.36f, 300 ) );
		_frameRadians.add( new FrameRadians( 0, 33, 0.93f, 270 ) );
		_frameRadians.add( new FrameRadians( 0, 39, 0.36f, 202 ) );
		_frameRadians.add( new FrameRadians( 0, 44, 0.80f, 275 ) );
		_frameRadians.add( new FrameRadians( 0, 50, 0.40f, 12 ) );
		_frameRadians.add( new FrameRadians( 0, 55, 0.00f, 0 ) );
		_frameRadians.add( new FrameRadians( 1, 29, 0.93f, 90 ) );
		_frameRadians.add( new FrameRadians( 1, 41, 0.13f, 0 ) );
		_frameRadians.add( new FrameRadians( 1, 48, 0.03f, 295 ) );
		_frameRadians.add( new FrameRadians( 1, 52, 0.06f, 183 ) );
		_frameRadians.add( new FrameRadians( 1, 58, 0.40f, 315 ) );
		_frameRadians.add( new FrameRadians( 2, 03, 0.06f, 280 ) );
		_frameRadians.add( new FrameRadians( 2, 07, 0.03f, 195 ) );
		_frameRadians.add( new FrameRadians( 2, 12, 0.13f, 300 ) );
		_frameRadians.add( new FrameRadians( 2, 17, 0.96f, 240 ) );
		_frameRadians.add( new FrameRadians( 2, 22, 0.10f, 30 ) );
		_frameRadians.add( new FrameRadians( 2, 26, 0.90f, 10 ) );
		_frameRadians.add( new FrameRadians( 2, 35, 0.26f, 180 ) );
		_frameRadians.add( new FrameRadians( 2, 40, 0.80f, 290 ) );
		_frameRadians.add( new FrameRadians( 2, 51, 0.60f, 254 ) );
		_frameRadians.add( new FrameRadians( 2, 56, 0.70f, 270 ) );
		_frameRadians.add( new FrameRadians( 3, 01, 0.83f, 90 ) );
		
		
		_finalFrame = new FrameRadians( 3, 13, 0.66f, -1 );
	}
	
	protected void createMesh() {
		float quadSize = 20;
		_mesh = new WETriangleMesh();
		
		int cols = Math.round( p.width / quadSize );
		int rows = Math.round( p.height / quadSize );
		
		for ( int i = 0; i < cols - 1; i++) {
			for ( int j = 0; j < rows - 1; j++) {
				// position mesh out from center
				float x = i * quadSize;
				float y = j * quadSize;
				// create 2 faces and their UV texture coordinates
				_mesh.addFace( new Vec3D( x, y, 0 ), new Vec3D( x+quadSize, y, 0 ), new Vec3D( x+quadSize, y+quadSize, 0 ), new Vec2D( x, y ), new Vec2D( x+quadSize, y ), new Vec2D( x+quadSize, y+quadSize ) );
				_mesh.addFace( new Vec3D( x, y, 0 ), new Vec3D( x+quadSize, y+quadSize, 0 ), new Vec3D( x, y+quadSize, 0 ), new Vec2D( x, y ), new Vec2D( x+quadSize, y+quadSize ), new Vec2D( x, y+quadSize ) );
			}
		}

		_meshDeform = _mesh.copy();
	}

		
	// FRAME LOOP RENDERING ===================================================================================
	public void drawApp() {
		if( p.frameCount == 1 ) initRender();
		p.background(0);
		p.fill( 255 );
		p.noStroke();
		p.rectMode( PConstants.CENTER );
		
		//DrawUtil.resetGlobalProps( p );
		DrawUtil.setBasicLights( p );
		
		if( _myMovie != null ) _myMovie.read();
		if( _myMovie.pixels.length > 100 ) {
			setWindRadiansPerClip();		
			seekAndDrawMovieFrame();
			
			// DRAW PARTICLES
			for( int i = 0; i < NUM_PARTICLES; i++ ) {
				_particles.get(i).update();
			}
			// DRAW texture MESH
//			drawTexturedMesh();
			// DRAW Mosaic MESH
//			drawColorMesh();
		}
		
		// step to next image
		_frameIndex++;
		
		// stop when done
		if( _frameIndex == _finalFrame.frame + 1 ) {
			p.exit();
		}
	}
	
	protected void setWindRadiansPerClip() {
		_windRadians.update();
		if( _frameRadiansIndex < _frameRadians.size() && _frameRadians.get( _frameRadiansIndex ).frame == _frameIndex ) {
			_windRadians.setTarget( _frameRadians.get( _frameRadiansIndex ).radians );
			_windRadians.setCurrent( _frameRadians.get( _frameRadiansIndex ).radians );
			_frameRadiansIndex++;
			
			// immediately set color
			for( int i = 0; i < NUM_PARTICLES; i++ ) {
				_particles.get(i).setColorFromPosition();
			}

		}
	}
		
	// MOVIE PARSING METHODS ===================================================================================
	protected void seekAndDrawMovieFrame() {
		seekTo( (float) p.frameCount / 30f );
		_myMovie.read();
		_myMovie.pause();

//		p.image( _myMovie, 0, 0 );
	}
	
	public void randomMovieTime() {
		_myMovie.jump(random(_myMovie.duration()));
	}
	
	public void seekTo( float time ) {
		_myMovie.jump( time );
		_myMovie.pause();
	}
	
	// Called every time a new frame is available to read
	public void movieEvent(Movie m) {
	  // m.read();
	}
	
	// PARTICLE CLASS ===========================================================================================	
	public class Particle {
		protected int _index;
		protected float _x = 0;
		protected float _y = 0;
		protected float _speed = 0;
		protected float _size = 0;
		protected EasingTColor _color = new EasingTColor( new TColor( TColor.WHITE ), 0.2f );
		protected PerlinNoise _perlin = new PerlinNoise();
		protected float _windOffset = 0;
		
		public Particle( int index ) {
			_index = index;
//			reset();
		}
		
		public void update() {
			_windOffset = _perlin.noise( p.frameCount / 1000f );
			
			_x += Math.sin( _windRadians.value() + _windOffset ) * _speed;
			_y += Math.cos( _windRadians.value() + _windOffset ) * _speed;
			checkBoundaries();
			
			// ease color towards current pixel
			_color.setTargetColor( TColor.newARGB( ImageUtil.getPixelColor( _myMovie, Math.round( _x ), Math.round( _y ) ) ) );
			_color.update();
			
			float amp = 0.2f + p._audioInput.getFFT().spectrum[_index % 512] * 5;
			
			p.fill( _color.color().toARGB() );
			// draw 2d circle
			p.ellipse( _x, _y, _size * amp, _size * amp );
//			p.rect( _x, _y, _size * amp, _size * amp );
			// draw spheres
//			p.pushMatrix();
//			p.translate( _x, _y );
//			p.sphere( _size * amp );
//			p.popMatrix();
		}
		
		public void checkBoundaries() {
			if( _x > p.width - 1 ) _x = 0;
			if( _x < 0 ) _x = p.width - 1;
			if( _y > p.height - 1 ) _y = 0;
			if( _y < 0 ) _y = p.height - 1;
			// set color to pixel if recycled to other side of stage
			if( _x == 0 || _x == p.width - 1 || _y == 0 || _y == p.height - 1 ) setColorFromPosition();
		}
		
		public void reset() {
			_x = MathUtil.randRange( 0, p.width - 1 );
			_y = MathUtil.randRange( 0, p.height - 1 );
			
//			_size = MathUtil.randRangeDecimel( 10, 50 );
//			_speed = _size/4;
			_size = MathUtil.randRangeDecimal( 1, 4 );
			_speed = MathUtil.randRangeDecimal( 2, 5 );
			
			setColorFromPosition();
		}
		
		public void setColorFromPosition() {
			TColor curColor = TColor.newARGB( ImageUtil.getPixelColor( _myMovie, (int) _x, (int) _y ) );
			_color.setCurAndTargetColors( curColor, curColor );
		}
	}
	
	// FRAME / RADIANS ===========================================================================================	
	public class FrameRadians {
		public int frame;
		public float radians;
		
		public FrameRadians( int minutes, int seconds, float hundredths, float angle ) {
			
			angle += 50; // hack to make angle correct - originals were entered as a standard cartesian angle
			
			frame = ( minutes * _fps * 60 ) + ( seconds * _fps ) + Math.round( hundredths * _fps );
			radians = angleToRadians( angle );
			
		}
		
		protected float angleToRadians( float angle ) {
			return  angle * (float) Math.PI / 180f; 
		}
	}
	
	// MESH DRAWING
	protected void drawTexturedMesh() {
		if( _mesh == null ) createMesh();
		PImage img = _myMovie;

		// set draw props to draw texture mesh properly
		p.fill( 0 );
		p.noStroke();
//		p.stroke(255);
		
		p.translate( 12, 12, 12 );
		
		// iterate over all mesh triangles
		// and add their vertices
		p.beginShape(P.TRIANGLES);
		p.texture(img);
		float brightA, brightB, brightC = 0;
		for( Face f : _mesh.getFaces() ) {
			p.fill( ImageUtil.getPixelColor( _myMovie, (int)f.a.x, (int)f.a.y ) );
			//P.println(ImageUtil.getPixelColor( img, (int)f.a.x, (int)f.a.y ));

			// get z-depth
			brightA = getBrightnessForTextureLoc( img, f.uvA.x, f.uvA.y ) * 3;
			brightB = getBrightnessForTextureLoc( img, f.uvB.x, f.uvB.y ) * 3;
			brightC = getBrightnessForTextureLoc( img, f.uvC.x, f.uvC.y ) * 3;
			// draw vertices
			p.vertex(f.a.x,f.a.y,f.a.z+brightA,f.uvA.x,f.uvA.y);
			p.vertex(f.b.x,f.b.y,f.b.z+brightB,f.uvB.x,f.uvB.y);
			p.vertex(f.c.x,f.c.y,f.c.z+brightC,f.uvC.x,f.uvC.y);
	   	}
		p.endShape();
	}

	protected void drawColorMesh() {
		if( _mesh == null ) createMesh();
		PImage img = _myMovie;

		// set draw props to draw texture mesh properly
		p.fill( 0 );
		p.noStroke();
		
		p.translate( 12, 12, 12 );
		
		deformWithAudio();
		
		// iterate over all mesh triangles
		// and add their vertices
		p.beginShape(P.TRIANGLES);
		int index = 0;	// use to traverse eq spectrum
		if( _myMovie.pixels.length > 100 ) {

			for( Face f : _meshDeform.getFaces() ) {
//				P.println((int)f.a.x+","+(int)f.a.y);
				p.fill( ImageUtil.getPixelColor( img, (int)f.a.x, (int)f.a.y ) );
//				P.println(ImageUtil.getPixelColor( img, (int)f.a.x, (int)f.a.y ));
				
				// get z-depth
	//			brightA = getBrightnessForTextureLoc( img, f.uvA.x, f.uvA.y ) * 3;
	//			brightB = getBrightnessForTextureLoc( img, f.uvB.x, f.uvB.y ) * 3;
	//			brightC = getBrightnessForTextureLoc( img, f.uvC.x, f.uvC.y ) * 3;
				// draw vertices
	//			float amp = 0.5f + p.getAudio().getFFT().spectrum[index % 512] * 10;
				
				p.vertex(f.a.x,f.a.y,f.a.z);
				p.vertex(f.b.x,f.b.y,f.b.z);
				p.vertex(f.c.x,f.c.y,f.c.z);
				
				index++;
		   	}
		}
		p.endShape();
	}
	
	protected void deformWithAudio() {
		int numVertices = _mesh.getNumVertices();
		int eqStep = Math.round( (float) numVertices / 512f );
		for( int i = 0; i < numVertices; i++ ) {
			float eq = p._audioInput.getFFT().spectrum[Math.round(i/eqStep) % 64];
			eq *= 2f;
			
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x;
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y;
				_meshDeform.getVertexForID( i ).z = 100 * eq;
			}
		}

	}


//	void drawNativeMesh() {
//		PImage img = _myMovie;
//		int x, y, color;
//		p.beginShape(P.TRIANGLES);
//		for ( int i = 0; i < _camW - 1; i++) {
//			for ( int j = 0; j < _camH - 1; j++) {
//				x = i;  // x position
//				y = j;  // y position
//				color = ImageUtil.getPixelColor( img, x, y );
//
//				float z = p.brightness(color) / 10f;
//				
//				p.fill(color);
//				p.stroke(0);
//				p.strokeWeight(1);
//
//				// draw grid out from center
//				x = -img.width/2 + x;
//				y = -img.height/2 + y;
//				
//				// draw trianges 
//				p.vertex( x, y, z );
//				p.vertex( x+1, y, z );
//				p.vertex( x+1, y+1, z );
//				
//				p.vertex( x, y, z );
//				p.vertex( x, y+1, z );
//				p.vertex( x+1, y+1, z );
//
//			}
//		}
//		p.endShape();
//	}

	
	protected float getBrightnessForTextureLoc( PImage img, float x, float y ) {
		float loc = x + y * img.width;  //  p.Pixel array location
		int c = img.pixels[(int)loc];  // Grab the color
		return p.brightness(c) * 0.1f;
	}

}

package com.haxademic.core.hardware.kinect;


import java.util.Vector;

import org.supercsv.cellprocessor.constraint.ForbidSubStr;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;
import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

public class KinectSilhouetteBasic {
	
	protected int PIXEL_SIZE = 20;
	protected int KINECT_CLOSE = 500;
	protected int KINECT_FAR = 30000;
	protected int KINECT_TOP_PIXEL = 0;
	protected int KINECT_BOTTOM_PIXEL = 480;
	protected int KINECT_LEFT_PIXEL = 0;
	protected int KINECT_RIGHT_PIXEL = 640;
	protected int KINECT_BUFFER_FRAMES = 3;
	protected int DEPTH_KEY_DIST = 400;

	public PGraphics _kinectPixelated;
	
	protected KinectBufferedData _kinectBuffer;
	protected KinectBufferedData _kinectBufferRoomScan;
	protected int _framesToScan = 300;
	
	BlobDetection theBlobDetection;
	public PGraphics blobBufferGraphics;
	protected PShader _blurH;
	protected PShader _blurV;

	protected float _canvasW = 640;
	protected float _canvasH = 480;
	public PGraphics _canvas;
	
	protected float _scaleDownForBlobDetection = 0.4f;
	protected boolean _hasParticles = false;
	protected int _framesScannedCount = 0;
	
	protected boolean DEBUG_OUTPUT = false;

	
	public KinectSilhouetteBasic(float scaleDownForBlobDetection, boolean depthKeying, boolean hasParticles) {
		if(P.p.appConfig.getInt("kinect_pixel_skip", -1) != -1) PIXEL_SIZE = P.p.appConfig.getInt("kinect_pixel_skip", -1);
		if(P.p.appConfig.getInt("kinect_left_pixel", -1) != -1) KINECT_LEFT_PIXEL = P.p.appConfig.getInt("kinect_left_pixel", -1);
		if(P.p.appConfig.getInt("kinect_right_pixel", -1) != -1) KINECT_RIGHT_PIXEL = P.p.appConfig.getInt("kinect_right_pixel", -1);
		if(P.p.appConfig.getInt("kinect_top_pixel", -1) != -1) KINECT_TOP_PIXEL = P.p.appConfig.getInt("kinect_top_pixel", -1);
		if(P.p.appConfig.getInt("kinect_bottom_pixel", -1) != -1) KINECT_BOTTOM_PIXEL = P.p.appConfig.getInt("kinect_bottom_pixel", -1);
		if(P.p.appConfig.getInt("kinect_scan_frames", -1) != -1) _framesToScan = P.p.appConfig.getInt("kinect_scan_frames", -1);
		if(P.p.appConfig.getInt("kinect_depth_key_dist", -1) != -1) DEPTH_KEY_DIST = P.p.appConfig.getInt("kinect_depth_key_dist", -1);

		_scaleDownForBlobDetection = scaleDownForBlobDetection;
		_hasParticles = hasParticles;
		
		initBlobDetection();
		_kinectBuffer = new KinectBufferedData(PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR, KINECT_BUFFER_FRAMES, true);
		_kinectBuffer.setKinectRect(KINECT_LEFT_PIXEL, KINECT_RIGHT_PIXEL, KINECT_TOP_PIXEL, KINECT_BOTTOM_PIXEL);
		if(depthKeying == true) {
			_kinectBufferRoomScan = new KinectBufferedData(PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR, KINECT_BUFFER_FRAMES, false);
			_kinectBufferRoomScan.setKinectRect(KINECT_LEFT_PIXEL, KINECT_RIGHT_PIXEL, KINECT_TOP_PIXEL, KINECT_BOTTOM_PIXEL);
		}
		_kinectPixelated = P.p.createGraphics( KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL, KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL, P.OPENGL );
		_kinectPixelated.noSmooth();
		_canvas = P.p.createGraphics( KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL, KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL, P.OPENGL );
		_canvas.noSmooth();
		_canvasW = _canvas.width;
		_canvasH = _canvas.height;
	}
	
	protected void initBlobDetection() {
		blobBufferGraphics = P.p.createGraphics( (int)(_canvasW * _scaleDownForBlobDetection), (int)(_canvasH * _scaleDownForBlobDetection), P.OPENGL);
		blobBufferGraphics.noSmooth();
		_blurV = P.p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set( "v", 2f/_canvasH );
		_blurH = P.p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set( "h", 2f/_canvasW );

		theBlobDetection = new BlobDetection( blobBufferGraphics.width, blobBufferGraphics.height );
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
	}

	protected void runBlobDetection( PImage source ) {
		blobBufferGraphics.beginDraw();
		blobBufferGraphics.clear();
		blobBufferGraphics.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferGraphics.width, blobBufferGraphics.height);
		blobBufferGraphics.filter(_blurH);
		blobBufferGraphics.filter(_blurV);
		theBlobDetection.computeBlobs(blobBufferGraphics.get().pixels);
		blobBufferGraphics.endDraw();
	}


	public void update() {
		drawKinectForBlob();
		runBlobDetection( _kinectPixelated );
		drawEdges();
	}
	
	public PImage debugKinectScanBuffer() {
		return _kinectBufferRoomScan.drawDebug();
	}
	
	public PImage debugKinectBuffer() {
		return _kinectBuffer.drawDebug();
	}
	
	protected void drawKinectForBlob() {
		_kinectBuffer.update(P.p.kinectWrapper);
		if(_kinectBufferRoomScan != null && _framesScannedCount <= _framesToScan) {
			_kinectBufferRoomScan.update(P.p.kinectWrapper);
			if(_framesScannedCount == _framesToScan) {
				P.println("=== Finished Scanning Room ===");
			}
		}
		
		// loop through kinect data within player's control range
		int pixelsDrawn = 0;
		_kinectPixelated.beginDraw();
		_kinectPixelated.clear();
		_kinectPixelated.noStroke();
		_kinectPixelated.noSmooth();
		float pixelDepth;
		float pixelDepthRoom = -99999;
		_kinectPixelated.fill(255f);
		int kinectBlobPadding = 10;
		// leave edges blank to get solid blobs that don't go off-screen!
		for ( int x = KINECT_LEFT_PIXEL + kinectBlobPadding; x < KINECT_RIGHT_PIXEL - kinectBlobPadding; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP_PIXEL + kinectBlobPadding; y < KINECT_BOTTOM_PIXEL - kinectBlobPadding; y += PIXEL_SIZE ) {
				// pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				pixelDepth = _kinectBuffer.getBufferedDepthForKinectPixel( x, y );
				pixelDepthRoom = -99999;
				if(_kinectBufferRoomScan != null) {
					pixelDepthRoom = _kinectBufferRoomScan.getBufferedDepthForKinectPixel( x, y );
					float confidence = _kinectBuffer.getConfidenceForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
						if(Math.abs(pixelDepth - pixelDepthRoom) > DEPTH_KEY_DIST) {
							_kinectPixelated.fill(255f * confidence);
							_kinectPixelated.rect(x - KINECT_LEFT_PIXEL, y - KINECT_TOP_PIXEL, PIXEL_SIZE, PIXEL_SIZE);
//							_kinectPixelated.fill(0,205f,0);
//							_kinectPixelated.text(MathUtil.roundToPrecision(pixelDepth/1000f, 1),x - KINECT_LEFT_PIXEL,y - KINECT_TOP_PIXEL);
//							_kinectPixelated.text(MathUtil.roundToPrecision(pixelDepthRoom/1000f, 1),x - KINECT_LEFT_PIXEL,y - KINECT_TOP_PIXEL+14);
							pixelsDrawn++;
						}
					}
				} else if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					_kinectPixelated.rect(x - KINECT_LEFT_PIXEL, y - KINECT_TOP_PIXEL, PIXEL_SIZE, PIXEL_SIZE);
					pixelsDrawn++;
				}
			}
		}

		_kinectPixelated.endDraw();
		_framesScannedCount++;
		if(DEBUG_OUTPUT) P.println("pixelsDrawn",pixelsDrawn);
	}
	
	protected void drawEdges() {
		
		_canvas.beginDraw();
		_canvas.clear();
		_canvas.noSmooth();
		_canvas.background(0);
		
		_canvas.stroke(255);
		_canvas.strokeWeight(3f);
		_canvas.fill(255);
		int numEdges = 0;
		// do edge detection
		Blob b;
		EdgeVertex eA,eB;
		for (int n=0 ; n < theBlobDetection.getBlobNb() ; n++) {
			b = theBlobDetection.getBlob(n);
			if ( b != null ) {
				_canvas.beginShape();
				for (int m = 0; m < b.getEdgeNb(); m++) {
					if(m % 3 == 0) { // only draw every other segment
						eA = b.getEdgeVertexA(m);
						eB = b.getEdgeVertexB(m);	// should store these too?
						if (eA !=null && eB !=null) {
							drawEdgeVertex( _canvas, eA.x * _canvasW, eA.y * _canvasH );
							drawEdgeVertex( _canvas, eB.x * _canvasW, eB.y * _canvasH );
	//						_canvas.line(eA.x * _canvasW, eA.y * _canvasH, eB.x * _canvasW, eB.y * _canvasH);
							numEdges++;
						}
					}
				}
				// connect last vertex to first
				eA = b.getEdgeVertexA(0);
				eB = b.getEdgeVertexB(0);
				drawEdgeVertex( _canvas, eA.x * _canvasW, eA.y * _canvasH );
				drawEdgeVertex( _canvas, eB.x * _canvasW, eB.y * _canvasH );

				// complete shape
				_canvas.endShape();
			}
		}
		
		if(_hasParticles == true) updateParticles();
		_canvas.endDraw();
		if(DEBUG_OUTPUT) P.println("drawEdges",numEdges);
	}
	
	
	protected void drawEdgeVertex(PGraphics canvas, float vertexX, float vertexY) {
		canvas.vertex( vertexX, vertexY );
		if(_hasParticles == true) {
			if(MathUtil.randRangeDecimal(0, 1) < 0.05f) {
				newParticle(vertexX, vertexY, P.p.random(-2f,2f), P.p.random(-3f,1f), 255);
			}
		}
	}
	
	
	
	
	
	
	
	
	protected Vector<FloatParticle> _particles;
	protected Vector<FloatParticle> _inactiveParticles;

	
	protected void updateParticles() {
		if(_particles == null) {
			_particles = new Vector<FloatParticle>();
			_inactiveParticles = new Vector<FloatParticle>();
		}
		// update particles
		FloatParticle particle;
		int particlesLen = _particles.size() - 1;
		for( int i = particlesLen; i > 0; i-- ) {
			particle = _particles.get(i);
			if( particle.active == true ) {
				particle.update();
			} else {
				_particles.remove(i);
				_inactiveParticles.add(particle);
			}
		}
		if(DEBUG_OUTPUT) P.println("_particles.size()",_particles.size());
	}
	
	
	protected void newParticle( float x, float y, float speedX, float speedY, int color ) {
		FloatParticle particle;
		if( _inactiveParticles.size() > 0 ) {
			particle = _inactiveParticles.remove( _inactiveParticles.size() - 1 );
		} else {
			particle = new FloatParticle(_canvas);
		}
		particle.startAt( x, y, speedX, speedY, color );
		_particles.add( particle );
	}

	
	public class FloatParticle {
		
		PGraphics p;
		PVector _position = new PVector();
		PVector _speed = new PVector();
		PVector _gravity = new PVector(0,-0.31f);
		int _color = 0;
		float _size = 4;
		public boolean active = false;
		protected int _audioIndex;
		
		public FloatParticle(PGraphics p) {
			this.p = p;
		}
		
		public void startAt( float x, float y, float speedX, float speedY, int color ) {
			_position.set( x, y );
			_speed.set( speedX, speedY );	// add a little extra x variance
//			_speed.mult( 1 + p._audioInput.getFFT().spectrum[_audioIndex] ); // speed multiplied by audio
			_color = color;
			active = true;
			_size = 3; //P.max(6,(P.abs(_speed.x) + P.abs(_speed.y))*0.3f);
			_audioIndex = MathUtil.randRange(0, 511);
		}
		
		public void update() {
			_position.add( _speed );
			_speed.add( _gravity );
			_speed.set( _speed.x * 0.98f, _speed.y );
			_size *= 0.97f;
			
			_size -= 0.1f;
			if( _size <= 0.1f ) {
				active = false;
			} else if( _position.y < -5 ) {
				active = false;
			} else {
				p.fill( _color );
				p.noStroke();
				// float size = _size; // 1 + p._audioInput.getFFT().spectrum[_audioIndex] * 10; // was 3
				p.rect( _position.x, _position.y, _size, _size );
			}
		}
	}
	
	
	
}
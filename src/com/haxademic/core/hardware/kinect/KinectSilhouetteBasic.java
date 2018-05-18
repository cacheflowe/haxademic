package com.haxademic.core.hardware.kinect;


import java.util.Vector;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;
import processing.video.Movie;

public class KinectSilhouetteBasic {
	
	protected int PIXEL_SIZE = 20;
	protected int KINECT_NEAR = 500;
	protected int KINECT_FAR = 2000;
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
//	public PGraphics blobBufferGraphics;
	protected PShader _blurH;
	protected PShader _blurV;

	protected float _canvasW = 640;
	protected float _canvasH = 480;
	public PGraphics _canvas;
	
//	protected float _scaleDownForBlobDetection = 0.4f;
	protected boolean _hasParticles = false;
	protected int _framesScannedCount = 0;
	protected int _backgroundColor = 0;
	
	protected boolean DEBUG_OUTPUT = false;
	
	Movie _movieFallback;

	
	public KinectSilhouetteBasic(boolean depthKeying, boolean hasParticles) {
		// set properties if they're been defined in appConfig
		if(P.p.appConfig.getInt("kinect_pixel_skip", -1) != -1) PIXEL_SIZE = P.p.appConfig.getInt("kinect_pixel_skip", -1);
		if(P.p.appConfig.getInt("kinect_left_pixel", -1) != -1) KINECT_LEFT_PIXEL = P.p.appConfig.getInt("kinect_left_pixel", -1);
		if(P.p.appConfig.getInt("kinect_right_pixel", -1) != -1) KINECT_RIGHT_PIXEL = P.p.appConfig.getInt("kinect_right_pixel", -1);
		if(P.p.appConfig.getInt("kinect_top_pixel", -1) != -1) KINECT_TOP_PIXEL = P.p.appConfig.getInt("kinect_top_pixel", -1);
		if(P.p.appConfig.getInt("kinect_bottom_pixel", -1) != -1) KINECT_BOTTOM_PIXEL = P.p.appConfig.getInt("kinect_bottom_pixel", -1);
		if(P.p.appConfig.getInt("kinect_near", -1) != -1) KINECT_NEAR = P.p.appConfig.getInt("kinect_near", -1);
		if(P.p.appConfig.getInt("kinect_far", -1) != -1) KINECT_FAR = P.p.appConfig.getInt("kinect_far", -1);
		if(P.p.appConfig.getInt("kinect_scan_frames", -1) != -1) _framesToScan = P.p.appConfig.getInt("kinect_scan_frames", -1);
		if(P.p.appConfig.getInt("kinect_depth_key_dist", -1) != -1) DEPTH_KEY_DIST = P.p.appConfig.getInt("kinect_depth_key_dist", -1);
		_backgroundColor = P.p.appConfig.getInt("kinect_blob_bg_int", 0);

//		_scaleDownForBlobDetection = scaleDownForBlobDetection;
		_hasParticles = hasParticles;
		
		_kinectBuffer = new KinectBufferedData(PIXEL_SIZE, KINECT_NEAR, KINECT_FAR, KINECT_BUFFER_FRAMES, true);
		setCustomKinectRect();
		if(depthKeying == true) {
			_kinectBufferRoomScan = new KinectBufferedData(PIXEL_SIZE, KINECT_NEAR, KINECT_FAR, KINECT_BUFFER_FRAMES, false);
			_kinectBufferRoomScan.setKinectRect(KINECT_LEFT_PIXEL, KINECT_RIGHT_PIXEL, KINECT_TOP_PIXEL, KINECT_BOTTOM_PIXEL);
		}
//		_kinectPixelated = P.p.createGraphics( KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL, KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL, P.P3D );
		_kinectPixelated = P.p.createGraphics( Math.round((KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL)/PIXEL_SIZE), Math.round((KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL)/PIXEL_SIZE), P.P3D );
		_kinectPixelated.noSmooth();
		
		initBlobDetection();
		
		_canvas = P.p.createGraphics( KINECT_RIGHT_PIXEL - KINECT_LEFT_PIXEL, KINECT_BOTTOM_PIXEL - KINECT_TOP_PIXEL, P.P3D );
//		_canvas.noSmooth();
//		_canvas.smooth(OpenGLUtil.SMOOTH_LOW);
		_canvas.smooth();
		_canvasW = _canvas.width;
		_canvasH = _canvas.height;
		
		if(P.p.appConfig.getBoolean("kinect_active", false) == false) {
			initBackupMovie();
		}
	}
	
	protected void initBackupMovie() {
		_movieFallback = new Movie(P.p, FileUtil.getFile("video/loops/bubbles-2.m4v"));
		_movieFallback.play();
		_movieFallback.loop();
		_movieFallback.jump(0);
		_movieFallback.speed(0.5f);
	}
	
	protected void setCustomKinectRect() {
		if(KINECT_TOP_PIXEL != 0 || KINECT_LEFT_PIXEL != 0 || KINECT_RIGHT_PIXEL != KinectSize.WIDTH || KINECT_BOTTOM_PIXEL != KinectSize.HEIGHT) {
			_kinectBuffer.setKinectRect(KINECT_LEFT_PIXEL, KINECT_RIGHT_PIXEL, KINECT_TOP_PIXEL, KINECT_BOTTOM_PIXEL);
		}
	}
	
	protected void initBlobDetection() {
		_blurV = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/blur-vertical.glsl"));
		_blurV.set( "v", 3f/_canvasH );
		_blurH = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/blur-horizontal.glsl"));
		_blurH.set( "h", 3f/_canvasW );

		theBlobDetection = new BlobDetection( _kinectPixelated.width, _kinectPixelated.height );
		theBlobDetection.setPosDiscrimination(true);	// true if looking for bright areas
		theBlobDetection.setThreshold(0.3f); // will detect bright areas whose luminosity > threshold
	}

	protected void runBlobDetection( PGraphics source ) {
		source.filter(_blurH);
		source.filter(_blurV);
		theBlobDetection.computeBlobs(source.get().pixels);
	}


	public void update(boolean clearsCanvas) {
		if(_movieFallback == null) {
//			updateThread();
			drawKinectForBlob();
			runBlobDetection( _kinectPixelated );
		} else {
//			if(_movieFallback.width > 10) runBlobDetection( _movieFallback );	// needs PImage, not PGraphics, which was switched when not copying KinectPixelated to another offscreen buffer
		}
		drawBlobs(clearsCanvas);
	}
	
	
//	protected UpdateAsync _updater;
//	protected boolean _updateComplete = true;
//	protected Thread _updateThread;
//
//	class UpdateAsync implements Runnable {
//		public UpdateAsync() {}    
//
//		public void run() {
//			drawKinectForBlob();
//			runBlobDetection( _kinectPixelated );
//			
//			_updateComplete = true;
//		} 
//	}
//
//	public void updateThread() {
//		if(_updateComplete == true) {
//			_updateComplete = false;
//			if(_updater == null) _updater = new UpdateAsync();
//			_updateThread = new Thread( _updater );
//			_updateThread.start();
//		}
//	}

	
	public PImage debugKinectScanBuffer() {
		return _kinectBufferRoomScan.drawDebug();
	}
	
	public PImage debugKinectBuffer() {
		return _kinectBuffer.drawDebug();
	}
		
	public float getRoomScanProgress() {
		return (float) _framesScannedCount / (float) _framesToScan;
	}
	
	protected void drawKinectForBlob() {
		_kinectBuffer.update(P.p.kinectWrapper);
		if(_kinectBufferRoomScan != null && _framesScannedCount <= _framesToScan) {
			_kinectBufferRoomScan.update(P.p.kinectWrapper);
			if(_framesScannedCount == _framesToScan) {
				_kinectBufferRoomScan.extraSpread();
				P.println("=== Finished Scanning Room ===");
			}
		}
		
		// loop through kinect data within player's control range
		int pixelsDrawn = 0;
		_kinectPixelated.beginDraw();
		_kinectPixelated.clear();
		_kinectPixelated.noStroke();
//		_kinectPixelated.noSmooth();
		_kinectPixelated.fill(255f);
		float pixelDepth;
		float pixelDepthRoom = -99999;
		int kinectBlobPadding = PIXEL_SIZE * 3;
//		float kinectPixelScaleUp = 1.0f;
//		float kinectPixelSize = PIXEL_SIZE;
		// leave edges blank to get solid blobs that don't go off-screen!
		for ( int x = KINECT_LEFT_PIXEL + kinectBlobPadding; x < KINECT_RIGHT_PIXEL - kinectBlobPadding; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP_PIXEL + kinectBlobPadding; y < KINECT_BOTTOM_PIXEL - kinectBlobPadding; y += PIXEL_SIZE ) {
				// pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				pixelDepth = _kinectBuffer.getBufferedDepthForKinectPixel( x, y );
				pixelDepthRoom = -99999;
				if(_kinectBufferRoomScan != null) {
					pixelDepthRoom = _kinectBufferRoomScan.getBufferedDepthForKinectPixel( x, y );
					float confidence = _kinectBuffer.getConfidenceForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > KINECT_NEAR && pixelDepth < KINECT_FAR ) {
						if(Math.abs(pixelDepth - pixelDepthRoom) > DEPTH_KEY_DIST) {
							_kinectPixelated.fill(255f * confidence);
//							_kinectPixelated.rect(x - KINECT_LEFT_PIXEL, y - KINECT_TOP_PIXEL, kinectPixelSize, kinectPixelSize);
							_kinectPixelated.rect(Math.round((x - KINECT_LEFT_PIXEL)/PIXEL_SIZE), Math.round((y - KINECT_TOP_PIXEL)/PIXEL_SIZE), 1, 1);
//							_kinectPixelated.fill(0,205f,0);
//							_kinectPixelated.text(MathUtil.roundToPrecision(pixelDepth/1000f, 1),x - KINECT_LEFT_PIXEL,y - KINECT_TOP_PIXEL);
//							_kinectPixelated.text(MathUtil.roundToPrecision(pixelDepthRoom/1000f, 1),x - KINECT_LEFT_PIXEL,y - KINECT_TOP_PIXEL+14);
							pixelsDrawn++;
						}
					}
				} else if( pixelDepth != 0 && pixelDepth > KINECT_NEAR && pixelDepth < KINECT_FAR ) {
//					_kinectPixelated.rect(x - KINECT_LEFT_PIXEL, y - KINECT_TOP_PIXEL, PIXEL_SIZE, PIXEL_SIZE);
					_kinectPixelated.rect(Math.round((x - KINECT_LEFT_PIXEL)/PIXEL_SIZE), Math.round((y - KINECT_TOP_PIXEL)/PIXEL_SIZE), 1, 1);
					pixelsDrawn++;
				}
			}
		}

		_kinectPixelated.endDraw();
		_framesScannedCount++;
		if(DEBUG_OUTPUT) P.println("pixelsDrawn",pixelsDrawn);
	}
	
	protected void drawBlobs(boolean clearsCanvas) {
		
		_canvas.beginDraw();
		
		if(clearsCanvas) {
			_canvas.clear();		
			_canvas.background(_backgroundColor);
		} else {
			// texture feedback
			float feedback = 2;// * P.sin(percentComplete * P.TWO_PI);
			_canvas.copy(
					_canvas, 
					0, 
					0, 
					_canvas.width, 
					_canvas.height, 
					P.round(-feedback/2f), 
					P.round(-feedback), 
					P.round(_canvas.width + feedback), 
					P.round(_canvas.height + feedback)
			);

			
			// fade out
			DrawUtil.setDrawCorner(_canvas);
			_canvas.fill(_backgroundColor,8);
			_canvas.rect(0, 0, _canvasW, _canvasH);
		}
		
//		_canvas.stroke(255);
//		_canvas.strokeWeight(2f);
		_canvas.noStroke();
		_canvas.fill(255);
		// do edge detection
		Blob b;
		for (int n=0 ; n < theBlobDetection.getBlobNb() ; n++) {
			b = theBlobDetection.getBlob(n);
			if ( b != null ) processBlob(b);
		}
		
		if(_hasParticles == true) updateParticles();
		_canvas.endDraw();
	}
	
	protected void processBlob(Blob b) {
		int numEdges = 0;
		_canvas.beginShape();
		for (int m = 0; m < b.getEdgeNb(); m++) {
			if(m % 3 == 0) { // only draw every other segment
				drawEdgeSegmentAtIndex(b, m);
				numEdges++;
			}
		}
		// connect last vertex to first
		drawEdgeSegmentAtIndex(b, 0);
		
		// complete shape
		_canvas.endShape();
		if(DEBUG_OUTPUT) P.println("drawEdges",numEdges);
	}
	
	protected void drawEdgeSegmentAtIndex(Blob b, int i) {
		EdgeVertex eA,eB;
		eA = b.getEdgeVertexA(i);
		eB = b.getEdgeVertexB(i);
		if (eA !=null && eB !=null) {
			drawEdgeVertex( _canvas, eA.x * _canvasW, eA.y * _canvasH, b.x * _canvasW, b.y * _canvasH);
			drawEdgeVertex( _canvas, eB.x * _canvasW, eB.y * _canvasH, b.x * _canvasW, b.y * _canvasH);
		}
	}
	
	
	protected void drawEdgeVertex(PGraphics canvas, float vertexX, float vertexY, float blobX, float blobY) {
		canvas.vertex( vertexX, vertexY );
		if(_hasParticles == true) {
			if(MathUtil.randRangeDecimal(0, 1) < 0.05f) {
				newParticle(vertexX, vertexY, blobX, blobY);
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
		_canvas.fill( 255 );
		_canvas.noStroke();
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
	
	
	protected void newParticle( float x, float y, float blobX, float blobY ) {
		float speedX = P.p.random(-2f,2f);
		float speedY = P.p.random(-1f,1f);
		if(_particles == null) return;
		FloatParticle particle;
		if( _inactiveParticles.size() > 0 ) {
			particle = _inactiveParticles.remove( _inactiveParticles.size() - 1 );
		} else {
			particle = new FloatParticle(_canvas);
		}
		particle.startAt( x, y, speedX, speedY );
		_particles.add( particle );
	}

	
	public class FloatParticle {
		
		PGraphics p;
		PVector _position = new PVector();
		PVector _speed = new PVector();
		PVector _gravity = new PVector(0,-0.11f);
		int _color = 0;
		float _size = 4;
		public boolean active = false;
		protected int _audioIndex;
		
		public FloatParticle(PGraphics p) {
			this.p = p;
		}
		
		public void startAt( float x, float y, float speedX, float speedY ) {
			_position.set( x, y );
			_speed.set( speedX, speedY );	// add a little extra x variance
//			_speed.mult( 1 + p._audioInput.getFFT().spectrum[_audioIndex] ); // speed multiplied by audio
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
				// float size = _size; // 1 + p._audioInput.getFFT().spectrum[_audioIndex] * 10; // was 3
				p.rect( _position.x, _position.y, _size, _size );
			}
		}
	}
	
}
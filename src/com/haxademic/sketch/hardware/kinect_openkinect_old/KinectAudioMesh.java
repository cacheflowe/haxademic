//package com.haxademic.sketch.hardware.kinect_openkinect_old;
//
//import krister.Ess.AudioInput;
//
//import org.openkinect.processing.Kinect;
//
//import processing.core.PApplet;
//import processing.core.PConstants;
//import processing.core.PImage;
//import processing.core.PVector;
//import toxi.geom.Sphere;
//import toxi.geom.Vec3D;
//import toxi.processing.ToxiclibsSupport;
//
//import com.haxademic.core.audio.AudioInputWrapper;
//import com.haxademic.core.cameras.CameraOscillate;
//import com.haxademic.core.cameras.common.ICamera;
//import com.haxademic.core.render.Renderer;
//
//public class KinectAudioMesh extends PApplet {
//
//	protected Kinect _kinect;
//	protected KinectTracker _tracker;
//	protected float[] _depthLookUp = new float[2048];
//	protected ToxiclibsSupport _gfx;
//	protected int _hardwareTilt = 0;
//	protected int _fps = 40;
//		
//	protected Renderer _render;
//	protected AudioInputWrapper _audioInput;
//	protected int _numEQ = 100; 
//	protected float _audioBoost = 1;
//	
//	protected ICamera _camera;
//	float _zOffset = -2000;
//	float _zFactor = 500;
//	int _pixelsSkip = 5;
//	float _curR = 0;
//	float _curG = 0;
//	float _curB = 0;
//
//	public void setup() {
//		size(1200, 860, PConstants.P3D); // size(screen.width,screen.height,P3D);
////		size( 1200, 860, "hipstersinc.P5Sunflow" );				//size(screen.width,screen.height,P3D);
//		
//		textureMode(this.NORMALIZED);
//		
//		frameRate(_fps);
//		colorMode(PConstants.RGB, 255, 255, 255, 255);
//		background(0);
//		
//		shininess(1000);
//		lights();
//		
//		_gfx = new ToxiclibsSupport(this);
//		_kinect = new Kinect(this);
//		_tracker = new KinectTracker();
//
//		// for true depth values
//		_kinect.enableDepth(true);
//		_kinect.processDepthImage(false);
//		_tracker.setThreshold(0);
//		// Lookup table for all possible depth values (0 - 2047)
//		for (int i = 0; i < _depthLookUp.length; i++) {
//			_depthLookUp[i] = rawDepthToMeters(i);
//		}
//		
//		// set up audio input
//		_audioInput = new AudioInputWrapper( this, false );
//		_audioInput.setNumAverages( _numEQ );
//		
//		// set up camera
//		_camera = new CameraOscillate( this, 0, 0, -0, 100 );
//		
//		// set up renderer
//		_render = null;
////		_render = new Renderer( this, _fps, Renderer.OUTPUT_TYPE_MOVIE );
////		_render.startRenderer();
//		//_render.startRendererForAudio( "wav/cacheflowe-intro-id.wav", _audioInput );
//
//	}
//
//	public void draw() {
////		_render.analyzeAudio();
////		rotateY(frameCount / 100f);
//		background(0);
//
//		// Run the tracking analysis
//		_tracker.track();
//		// Show the image
//		_tracker.display();
//		
//		// draw rings
//		stroke(255, 100*_audioInput.getFFT().spectrum[127]*_audioBoost);
//		noStroke();
//		pushMatrix();
//		translate(width / 2, (height / 5) * 2 - _audioInput.getFFT().spectrum[50]*_audioBoost*1000, 0);
//		rotateX(PI/2);
//		drawDisc3D( 2000 + _audioInput.getFFT().spectrum[50]*_audioBoost*10000, 1900, 50 + _audioInput.getFFT().spectrum[50]*_audioBoost*50, 40, color(_curR, _curG, _curB, _audioInput.getFFT().spectrum[50]*_audioBoost * 100), color(_curR, _curG, _curB, _audioInput.getFFT().spectrum[50]*_audioBoost * 100) );
//		popMatrix();
//		pushMatrix();
//		translate(width / 2, (height / 5) * 3, 0);
//		rotateX(PI/2);
//		drawDisc3D( 2000 + _audioInput.getFFT().spectrum[150]*_audioBoost*10000, 1900, 50 + _audioInput.getFFT().spectrum[150]*_audioBoost*50, 40, color(_curR, _curG, _curB, _audioInput.getFFT().spectrum[150]*_audioBoost * 100), color(_curR, _curG, _curB, _audioInput.getFFT().spectrum[150]*_audioBoost * 100) );
//		popMatrix();
//		pushMatrix();
//		translate(width / 2, (height / 5) * 4 + _audioInput.getFFT().spectrum[200]*_audioBoost*1000, 0);
//		rotateX(PI/2);
//		drawDisc3D( 2000 + _audioInput.getFFT().spectrum[200]*_audioBoost*10000, 1900, 50 + _audioInput.getFFT().spectrum[200]*_audioBoost*50, 40, color(_curR, _curG, _curB, _audioInput.getFFT().spectrum[200]*_audioBoost * 100), color(_curR, _curG, _curB, _audioInput.getFFT().spectrum[200]*_audioBoost * 100) );
//		popMatrix();
//		
//		// draw outer spheres
//		noFill();
//		stroke(255, 30*_audioInput.getFFT().spectrum[127]*_audioBoost);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),10000 + (_audioInput.getFFT().spectrum[127]*_audioBoost)*2000 ), 30);
//		pushMatrix();
//		rotateY(frameCount/1000f);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),8000 + (_audioInput.getFFT().spectrum[200]*_audioBoost)*2000 ), 30);
//		pushMatrix();
//		rotateY(frameCount/1000f);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),6000 + (_audioInput.getFFT().spectrum[220]*_audioBoost)*2000 ), 30);
//		pushMatrix();
//		rotateY(frameCount/1000f);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),4000 + (_audioInput.getFFT().spectrum[100]*_audioBoost)*2000 ), 30);
//		popMatrix();
//		popMatrix();
//		popMatrix();
//		
//		
//		
//		_camera.update();
//		
//		// Display some info
//		int t = _tracker.getThreshold();
//		
//		if( _render != null ) _render.renderFrame();
//	}
//
//	public void keyPressed() {
//		if (key == ' ') {
//			String _timestamp = "" + String.valueOf(year()) + "-"
//					+ String.valueOf(month()) + "-" + String.valueOf(day())
//					+ "-" + String.valueOf(hour()) + "-"
//					+ String.valueOf(minute()) + "-" + String.valueOf(second());
//			saveFrame("output/img_" + _timestamp + nf(frameCount, 8) + ".jpg");
//		} else if (key == '/') {
//			//_render.renderFrame();
//		} else if (key == ',') {
//			_audioBoost -= 0.5;
//			_audioBoost = constrain( _audioBoost, 0, 10 );
//		} else if (key == '.') {
//			_audioBoost += 0.5;
//			_audioBoost = constrain( _audioBoost, 0, 10 );
//		} else if(key == 'm') {
//			_tracker.swapMapping();
//		} else if(key == 'r') {
//			_tracker.swapRowBased();
//		} else if(key == 'c') {
//			_tracker.newBaseColors();
//		} else if (key == CODED) {
//			if (keyCode == UP) {
//				_hardwareTilt+=5;
//				_hardwareTilt = constrain(_hardwareTilt, 0, 30);
//				_kinect.tilt(_hardwareTilt);
//			} else if (keyCode == DOWN) {
//				_hardwareTilt-=5;
//				_hardwareTilt = constrain(_hardwareTilt, 0, 30);
//				_kinect.tilt(_hardwareTilt);
//			} else if (keyCode == LEFT) {
//				_pixelsSkip -= 1;
//			} else if (keyCode == RIGHT) {
//				_pixelsSkip += 1;
//			}
//			_pixelsSkip = constrain( _pixelsSkip, 3, 12 );
//		}
//	}
//
//	public void stop() {
//		_kinect.quit();
//		if(_render != null) { 
//			_render.stop(); 
//		}
//		super.stop();
//	}
//
//	class KinectTracker {
//
//		// Size of kinect image
//		int _kinectWidth = 640;
//		int _kinectHeight = 480;
//		int _centerWidth = 640;
//		int _centerHeight = 480;
//		int _thresholdLow = 0;
//		float _thresholdHigh = 1.2f;
//
//		
//		PVector _loc; 		// Raw location
//		PVector _lerpedLoc;	// Interpolated location
//
//		// Depth data
//		int[] _depthArray;
//
//		// image data from 2 cameras
//		PImage _display;
//		PImage _img;
//		
//		// UV coordinates for mapping images to mesh
//		float _U;
//		float _V;
//		
//		// used in grid loop for raw depth
//		int rawDepth;
//		int rawDepthR;
//		int rawDepthD;
//		int rawDepthRD;
//		PVector _curPixelDepth;
//		PVector _rightPixelDepth;
//		PVector _rightDownPixelDepth;
//		PVector _downPixelDepth;
//		int _offsetC;
//		int _offsetR;
//		int _offsetD;
//		int _offsetRD;
//
//		
//		// images from the kinect cameras
//		PImage _depthImage;
//		PImage _videoCamImg;
//
//		
//		// visual drawing vars
//		float _drawGridSizeFactor = 7000;
//		boolean _isMapping = false;
//		boolean _isRowBased = false;
//		
//
//		KinectTracker() {
//			_kinect.start();
//			_kinect.enableDepth(true);
//			_kinect.enableRGB(true);
//
//			// We could skip processing the grayscale image for efficiency
//			// but this example is just demonstrating everything
//			_kinect.processDepthImage(false);
//			
//			newBaseColors();
//
//			_display = createImage(_kinectWidth, _kinectHeight, PConstants.RGB);
//
//			_loc = new PVector(0, 0);
//			_lerpedLoc = new PVector(0, 0);
//		}
//		
//		public void swapMapping() {
//			_isMapping = !_isMapping;
//		}
//		
//		public void swapRowBased() {
//			_isRowBased = !_isRowBased;
//		}
//
//		public void newBaseColors() {
//			_curR = random(0,100);
//			_curG = random(0,100);
//			_curB = random(0,100);
//		}
//
//		void track() {
//
//			// Get the raw depth as array of integers
//			_depthArray = _kinect.getRawDepth();
//
//			// Being overly cautious here
//			if (_depthArray == null)
//				return;
//
//			float sumX = 0;
//			float sumY = 0;
//			float count = 0;
//
//			for (int x = 0; x < _kinectWidth; x++) {
//				for (int y = 0; y < _kinectHeight; y++) {
//					// Mirroring the image
//					int offset = _kinectWidth - x - 1 + y * _kinectWidth;
//					// Grabbing the raw depth
//					int rawDepth = _depthArray[offset];
//
//					// Testing against threshold
//					if (rawDepth < _thresholdLow) {
//						sumX += x;
//						sumY += y;
//						count++;
//					}
//				}
//			}
//			// As long as we found something
//			if (count != 0) {
//				_loc = new PVector(sumX / count, sumY / count);
//			}
//
//			// Interpolating the location, doing it arbitrarily for now
//			_lerpedLoc.x = PApplet.lerp(_lerpedLoc.x, _loc.x, 0.3f);
//			_lerpedLoc.y = PApplet.lerp(_lerpedLoc.y, _loc.y, 0.3f);
//		}
//
//		PVector getLerpedPos() {
//			return _lerpedLoc;
//		}
//
//		PVector getPos() {
//			return _loc;
//		}
//
//		void display() {
//			pushMatrix();
//			
//			translate(width / 2, height / 2, 0);
//			//rotateY(frameCount/1000);
////			fill(255, 100);
////			rect(0,0,100,100);
//			
//			int numPixelsDrawn = 0;
//
//			
//			// get camera image and create UV coord floats
//			_depthImage = _kinect.getDepthImage();
//			if( _isMapping ) { 
//				_videoCamImg = _kinect.getVideoImage(); 
//			}
//			
//			for (int x = 0; x < _kinectWidth; x+=_pixelsSkip) {
//				for (int y = 0; y < _kinectHeight; y+=_pixelsSkip) {
//			
////			for (int x = startX; x < startX + cw; x++) {
////				for (int y = startY; y < startY + ch; y++) {
//					int nextX = x + _pixelsSkip;
//					int nextY = y + _pixelsSkip;
//
//					// stay 1 row/col in from edge, and skip cells for lower resolution
//					if (nextX < _kinectWidth && nextY < _kinectHeight && x >= 1 && y >= 1
//							&& (x % _pixelsSkip == 0 && y % _pixelsSkip == 0)) {
//						
//						beginShape(TRIANGLES);
//						if( _isMapping ) { 
//							texture(_videoCamImg);
//						}
//						
//						// mirroring image, get raw depth
//						_offsetC = _kinectWidth - x - 1 + y * _kinectWidth;
//						_offsetR = _kinectWidth - nextX - 1 + y * _kinectWidth;
//						_offsetD = _kinectWidth - x - 1 + nextY * _kinectWidth;
//						_offsetRD = _kinectWidth - nextX - 1 + nextY * _kinectWidth;
//						
//						// get depth for neighbor cells
//						rawDepth = _depthArray[_offsetC];
//						rawDepthR = _depthArray[_offsetR];
//						rawDepthD = _depthArray[_offsetD];
//						rawDepthRD = _depthArray[_offsetRD];
//						
//						_curPixelDepth = depthToWorld(x, y, rawDepth);
//						_rightPixelDepth = depthToWorld(nextX, y, rawDepthR);
//						_rightDownPixelDepth = depthToWorld(nextX, nextY, rawDepthRD);
//						_downPixelDepth = depthToWorld(x, nextY, rawDepthD);
//						
//						// draw if within threshold
//						if (_curPixelDepth.z < _thresholdHigh && _rightPixelDepth.z < _thresholdHigh && _curPixelDepth.z < _thresholdHigh && _downPixelDepth.z < _thresholdHigh) {
//							
//							// calculate image mapping, with a little fudging for kinect camera offset
//							_U = (1.0f - x/(float)_kinectWidth) - 0.01f;
//							_V = (y/(float)_kinectHeight) + 0.05f;
//	
//							// if z depth delta is over a threshold, don't draw it
//							if (Math.abs(_curPixelDepth.z - _rightPixelDepth.z) > 0.2 || Math.abs(_curPixelDepth.z - _rightDownPixelDepth.z) > 0.2 || Math.abs(_curPixelDepth.z - _downPixelDepth.z) > 0.2) {
//							} else {
//								if( _isMapping ) { 
//									// map image to mesh by adding UV
//									vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset - _curPixelDepth.z * _zFactor, _U, _V);
//									vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightDownPixelDepth.z * _zFactor, _U, _V);
//									vertex(_downPixelDepth.x * _drawGridSizeFactor, _downPixelDepth.y * _drawGridSizeFactor, _zOffset - _downPixelDepth.z * _zFactor, _U, _V);
//									
//									vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset - _curPixelDepth.z * _zFactor, _U, _V);
//									vertex(_rightPixelDepth.x * _drawGridSizeFactor, _rightPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightPixelDepth.z * _zFactor, _U, _V);
//									vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightDownPixelDepth.z * _zFactor, _U, _V);
//								} else {
//									// set color
//									if( _isRowBased ) {
//										fill(_curR + 255*_audioInput.getFFT().spectrum[y%512]*_audioBoost,_curG + 255*_audioInput.getFFT().spectrum[y%512]*_audioBoost,_curB + 255*_audioInput.getFFT().spectrum[y%512]*_audioBoost,255*_audioInput.getFFT().spectrum[x%512]*_audioBoost);
//									} else {
//										fill(_curR + 255*_audioInput.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost,_curG + 255*_audioInput.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost,_curB + 255*_audioInput.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost,255*_audioInput.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost);
//									}
//									stroke(255*_audioInput.getFFT().spectrum[x%512]);
//									//noFill();
//									//noStroke();
//									
////									if(x > 105 && x < 200 && y > 105 && y < 200) {
////										println( _zOffset - _curPixelDepth.z * _zFactor );
////									}
//									vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset + _curPixelDepth.z * _zFactor );
//									vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset + _rightDownPixelDepth.z * _zFactor );
//									vertex(_downPixelDepth.x * _drawGridSizeFactor, _downPixelDepth.y * _drawGridSizeFactor, _zOffset + _downPixelDepth.z * _zFactor );
//									
//									vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset + _curPixelDepth.z * _zFactor );
//									vertex(_rightPixelDepth.x * _drawGridSizeFactor, _rightPixelDepth.y * _drawGridSizeFactor, _zOffset + _rightPixelDepth.z * _zFactor );
//									vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset + _rightDownPixelDepth.z * _zFactor );
//								}
//								
//								numPixelsDrawn++;
//							}
//	
//						}
//
//						endShape();
//					}
//				}
//			}
//
//			// display.updatePixels();
//
//			// if(numPixelsDrawn > 2000 && numPixelsDrawn < 10000) {
//			// Draw the image
//			// image(display, 0, 0);
//			
//			popMatrix();
//		}
//
//		void quit() {
//			_kinect.quit();
//		}
//
//		int getThreshold() {
//			return _thresholdLow;
//		}
//
//		void setThreshold(int t) {
//			_thresholdLow = t;
//		}
//
//	}
//	
//	
//	/* Custom drawing methods */
//	protected void drawDisc3D( float radius, float innerRadius, float cylinderHeight, int numSegments, int color, int wallcolor )
//	{
//		// draw triangles
//		beginShape(TRIANGLES);
//		
//		float segmentCircumference = (2f*PI) / numSegments;
//		float halfHeight = cylinderHeight / 2;
//
//		for( int i = 0; i < numSegments; i++ )
//		{
//			fill( color );
//
//			// top disc
//			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
//			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
//			
//			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
//			
//			// bottom disc
//			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
//			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, -halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
//			
//			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
//			
//			fill( wallcolor );
//			// outer wall
//			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, halfHeight );
//			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, -halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
//			
//			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, -halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
//			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
//			
//			// only draw inner radius if needed
//			if( innerRadius > 0 )
//			{
//				fill(wallcolor);
//				// inner wall
//				vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
//				vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
//				vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
//				
//				vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
//				vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
//				vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
//			}
//		}
//		
//		endShape();
//	}
//
//
//	// ---- FOR DEPTH MEASUREMENT
//	// These functions come from:
//	// http://graphics.stanford.edu/~mdfisher/Kinect.html
//	float rawDepthToMeters(int depthValue) {
//		if (depthValue < 2047) {
//			return (float) (1.0 / ((double) (depthValue) * -0.0030711016 + 3.3309495161));
//		}
//		return 0.0f;
//	}
//
//	PVector depthToWorld(int x, int y, int depthValue) {
//
//		final double fx_d = 1.0 / 5.9421434211923247e+02;
//		final double fy_d = 1.0 / 5.9104053696870778e+02;
//		final double cx_d = 3.3930780975300314e+02;
//		final double cy_d = 2.4273913761751615e+02;
//
//		PVector result = new PVector();
//		double depth = _depthLookUp[depthValue];// rawDepthToMeters(depthValue);
//		result.x = (float) ((x - cx_d) * depth * fx_d);
//		result.y = (float) ((y - cy_d) * depth * fy_d);
//		result.z = (float) (depth);
//		return result;
//	}
//
//	// PApp-level listener for audio input data ------------------------ 
//	public void audioInputData( AudioInput theInput ) {
//		_audioInput.getFFT().getSpectrum(theInput);
//		_audioInput.detector.detect(theInput);
//	}
//
//}

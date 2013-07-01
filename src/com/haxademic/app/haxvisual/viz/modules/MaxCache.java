package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.app.P;
import com.haxademic.core.cameras.CameraBasic;
import com.haxademic.core.cameras.CameraOscillate;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.hardware.midi.MidiWrapper;

public class MaxCache extends ModuleBase implements IVizModule {
	
	protected int _numAverages = 12;

	protected KinectWrapper _kinectinterface;
	protected ToxiclibsSupport _gfx;
		
	protected float _audioBoost = 6;
	
	float _zOffset = 3000;
	float _zFactor = -2000;
	int _pixelsSkip = 7;
	int _thresholdLow = 0;
	float _thresholdHigh = 1.7f;
	
	float _curR = 0;
	float _curG = 0;
	float _curB = 0;
	
	int _mode;
	final int MAPPING = 0;
	final int BOXES = 1;
	final int MESH = 2;

	PImage _depthImage;
	PImage _videoCamImg;
	
	// Size of kinect image
	int _kinectWidth = 640;
	int _kinectHeight = 480;
	int _centerWidth = 640;
	int _centerHeight = 480;
	
	// visual drawing vars
	float _drawGridSizeFactor = 7000;
	boolean _isRowBased = false;

	// UV coordinates for mapping images to mesh
	float _U;
	float _V;
	
	// used in grid loop for raw depth
	int rawDepth;
	int rawDepthR;
	int rawDepthD;
	int rawDepthRD;
	PVector _curPixelDepth;
	PVector _rightPixelDepth;
	PVector _rightDownPixelDepth;
	PVector _downPixelDepth;
	int _offsetC;
	int _offsetR;
	int _offsetD;
	int _offsetRD;

	
	public MaxCache()
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode(PConstants.RGB, 255, 255, 255, 255);
		p.noStroke();
		p.shininess(1000);
		p.lights();
		
		newCamera();
		
		
		
		// Kinect stuff
		p.textureMode(P.NORMAL);
		
		p.background(0);
		
		
		_gfx = new ToxiclibsSupport(p);
		_kinectinterface = p.kinectWrapper;

		
		newBaseColors();

		// set up camera
		_curCamera = new CameraOscillate( p, 0, 0, -0, 100 );
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 255, 255, 255, 255 );
		p.noStroke();
		newCamera();
	}

	public void update() {
		//p.rotateY(p.frameCount / 100f);
		p.background(0);
		
		// Run the tracking analysis
		_kinectinterface.update();
		
		// draw rings
		p.stroke(255, 200*_audioData.getFFT().spectrum[127]*_audioBoost);
		p.noStroke();
		p.pushMatrix();
		p.translate(p.width / 2, (p.height / 5) * 2 - _audioData.getFFT().spectrum[50]*_audioBoost*1000, 0);
		p.rotateX(P.PI/2);
		Shapes.drawDisc3D( p, 2000 + _audioData.getFFT().spectrum[50]*_audioBoost*20000, 10000, 50 + _audioData.getFFT().spectrum[50]*_audioBoost*50, 50, p.color(_curR, _curG, _curB, _audioData.getFFT().spectrum[50]*_audioBoost * 100), p.color(_curR, _curG, _curB, _audioData.getFFT().spectrum[50]*_audioBoost * 100) );
		p.popMatrix();
		p.pushMatrix();
		p.translate(p.width / 2, (p.height / 5) * 3, 0);
		p.rotateX(P.PI/2);
		Shapes.drawDisc3D( p, 2000 + _audioData.getFFT().spectrum[150]*_audioBoost*20000, 10000, 50 + _audioData.getFFT().spectrum[150]*_audioBoost*50, 50, p.color(_curR, _curG, _curB, _audioData.getFFT().spectrum[150]*_audioBoost * 100), p.color(_curR, _curG, _curB, _audioData.getFFT().spectrum[150]*_audioBoost * 100) );
		p.popMatrix();
		p.pushMatrix();
		p.translate(p.width / 2, (p.height / 5) * 4 + _audioData.getFFT().spectrum[200]*_audioBoost*1000, 0);
		p.rotateX(P.PI/2);
		Shapes.drawDisc3D( p, 2000 + _audioData.getFFT().spectrum[200]*_audioBoost*20000, 10000, 50 + _audioData.getFFT().spectrum[200]*_audioBoost*50, 50, p.color(_curR, _curG, _curB, _audioData.getFFT().spectrum[200]*_audioBoost * 100), p.color(_curR, _curG, _curB, _audioData.getFFT().spectrum[200]*_audioBoost * 100) );
		p.popMatrix();
		
		// draw outer spheres
		p.noFill();
		p.stroke(255, 100*_audioData.getFFT().spectrum[127]*_audioBoost);
		_gfx.sphere(new Sphere(new Vec3D(0,0,0),10000 + (_audioData.getFFT().spectrum[127]*_audioBoost)*2000 ), 30);
//		p.pushMatrix();
//		p.rotateY(p.frameCount/1000f);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),8000 + (_audioData.getFFT().spectrum[200]*_audioBoost)*2000 ), 30);
//		p.pushMatrix();
//		p.rotateY(p.frameCount/1000f);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),6000 + (_audioData.getFFT().spectrum[220]*_audioBoost)*2000 ), 30);
//		p.pushMatrix();
//		p.rotateY(p.frameCount/1000f);
//		_gfx.sphere(new Sphere(new Vec3D(0,0,0),4000 + (_audioData.getFFT().spectrum[100]*_audioBoost)*2000 ), 30);
//		p.popMatrix();
//		p.popMatrix();
//		p.popMatrix();
		
		
		drawKinect();
		
		_curCamera.update();
		
	}
	
	void drawKinect() {
		p.pushMatrix();
		
		p.translate(p.width / 2, p.height / 2, 0);
		//rotateY(frameCount/1000);
//		fill(255, 100);
//		rect(0,0,100,100);
		
		int numPixelsDrawn = 0;
		int[] _depthArray = _kinectinterface.getDepthData();
		
		// get camera image and create UV coord floats
		_depthImage = _kinectinterface.getDepthImage();
		if( _mode == MAPPING ) { 
			//_videoCamImg = _kinectinterface.getVideoImage(); 
		}
		
		for (int x = 0; x < _kinectWidth; x+=_pixelsSkip) {
			for (int y = 0; y < _kinectHeight; y+=_pixelsSkip) {
				//p.println(x+" = "+_depthArray[_kinectWidth - x - 1 + y * _kinectWidth]);
//		for (int x = startX; x < startX + cw; x++) {
//			for (int y = startY; y < startY + ch; y++) {
				int nextX = x + _pixelsSkip;
				int nextY = y + _pixelsSkip;

				// stay 1 row/col in from edge, and skip cells for lower resolution
				if (nextX < _kinectWidth && nextY < _kinectHeight && x >= 1 && y >= 1
						&& (x % _pixelsSkip == 0 && y % _pixelsSkip == 0)) {
					
					p.beginShape(P.TRIANGLES);
					if( _mode == MAPPING ) { 
						//p.texture(_videoCamImg);
					}
					
					// mirroring image, get raw depth
					_offsetC = _kinectWidth - x - 1 + y * _kinectWidth;
					_offsetR = _kinectWidth - nextX - 1 + y * _kinectWidth;
					_offsetD = _kinectWidth - x - 1 + nextY * _kinectWidth;
					_offsetRD = _kinectWidth - nextX - 1 + nextY * _kinectWidth;
					
					// get depth for neighbor cells
					rawDepth = _depthArray[_offsetC];
					rawDepthR = _depthArray[_offsetR];
					rawDepthD = _depthArray[_offsetD];
					rawDepthRD = _depthArray[_offsetRD];
					
					_rightPixelDepth = new PVector( x, y, p.kinectWrapper.getMillimetersDepthForKinectPixel(nextX, y) );
					_curPixelDepth = new PVector( x, y, p.kinectWrapper.getMillimetersDepthForKinectPixel(x, y) );
					_rightDownPixelDepth = new PVector( x, y, p.kinectWrapper.getMillimetersDepthForKinectPixel(nextX, nextY) );
					_downPixelDepth = new PVector( x, y, p.kinectWrapper.getMillimetersDepthForKinectPixel(x, nextY) );
					
					// draw if within threshold
					if (_curPixelDepth.z < _thresholdHigh && _rightPixelDepth.z < _thresholdHigh && _curPixelDepth.z < _thresholdHigh && _downPixelDepth.z < _thresholdHigh) {
						
						// calculate image mapping, with a little fudging for kinect camera offset
						_U = (1.0f - x/(float)_kinectWidth) - 0.01f;
						_V = (y/(float)_kinectHeight) + 0.05f;

						// if z depth delta is over a threshold, don't draw it
						if (Math.abs(_curPixelDepth.z - _rightPixelDepth.z) > 0.2 || Math.abs(_curPixelDepth.z - _rightDownPixelDepth.z) > 0.2 || Math.abs(_curPixelDepth.z - _downPixelDepth.z) > 0.2) {
						} else {
							switch( _mode ) {
								case MAPPING :
									// map image to mesh by adding UV
//									p.vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset - _curPixelDepth.z * _zFactor, _U, _V);
//									p.vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightDownPixelDepth.z * _zFactor, _U, _V);
//									p.vertex(_downPixelDepth.x * _drawGridSizeFactor, _downPixelDepth.y * _drawGridSizeFactor, _zOffset - _downPixelDepth.z * _zFactor, _U, _V);
//									
//									p.vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset - _curPixelDepth.z * _zFactor, _U, _V);
//									p.vertex(_rightPixelDepth.x * _drawGridSizeFactor, _rightPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightPixelDepth.z * _zFactor, _U, _V);
//									p.vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightDownPixelDepth.z * _zFactor, _U, _V);
									
									
									p.fill(305*_audioData.getFFT().spectrum[x%512]);
									
									p.vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset - _curPixelDepth.z * _zFactor - 2550*_audioData.getFFT().spectrum[x%512] );
									p.vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightDownPixelDepth.z * _zFactor - 2550*_audioData.getFFT().spectrum[x%512] );
									p.vertex(_downPixelDepth.x * _drawGridSizeFactor, _downPixelDepth.y * _drawGridSizeFactor, _zOffset - _downPixelDepth.z * _zFactor - 2550*_audioData.getFFT().spectrum[x%512] );
									
									p.vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset - _curPixelDepth.z * _zFactor - 2550*_audioData.getFFT().spectrum[x%512] );
									p.vertex(_rightPixelDepth.x * _drawGridSizeFactor, _rightPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightPixelDepth.z * _zFactor - 2550*_audioData.getFFT().spectrum[x%512] );
									p.vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset - _rightDownPixelDepth.z * _zFactor - 2550*_audioData.getFFT().spectrum[x%512] );

									break;
								case MESH :
									// set color
									if( _isRowBased ) {
										p.fill(_curR + 255*_audioData.getFFT().spectrum[y%512]*_audioBoost,_curG + 255*_audioData.getFFT().spectrum[y%512]*_audioBoost,_curB + 255*_audioData.getFFT().spectrum[y%512]*_audioBoost,255*_audioData.getFFT().spectrum[x%512]*_audioBoost);
									} else {
										p.fill(_curR + 255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost,_curG + 255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost,_curB + 255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost,255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost);
									}
									p.stroke(255*_audioData.getFFT().spectrum[x%512]);
									//noFill();
									p.noStroke();
									
//									if(x > 105 && x < 200 && y > 105 && y < 200) {
//										println( _zOffset - _curPixelDepth.z * _zFactor );
//									}
									p.vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset + _curPixelDepth.z * -_zFactor );
									p.vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset + _rightDownPixelDepth.z * -_zFactor );
									p.vertex(_downPixelDepth.x * _drawGridSizeFactor, _downPixelDepth.y * _drawGridSizeFactor, _zOffset + _downPixelDepth.z * -_zFactor );
									
									p.vertex(_curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset + _curPixelDepth.z * -_zFactor );
									p.vertex(_rightPixelDepth.x * _drawGridSizeFactor, _rightPixelDepth.y * _drawGridSizeFactor, _zOffset + _rightPixelDepth.z * -_zFactor );
									p.vertex(_rightDownPixelDepth.x * _drawGridSizeFactor, _rightDownPixelDepth.y * _drawGridSizeFactor, _zOffset + _rightDownPixelDepth.z * -_zFactor );
									
									break;
								case BOXES :
									// set color
									if( _isRowBased ) {
										p.fill(_curR + 255*_audioData.getFFT().spectrum[y%512]*_audioBoost*1.6f,_curG + 255*_audioData.getFFT().spectrum[y%512]*_audioBoost*1.6f,_curB + 255*_audioData.getFFT().spectrum[y%512]*_audioBoost*1.6f,255*_audioData.getFFT().spectrum[x%512]*_audioBoost*1.6f);
									} else {
										p.fill(_curR + 255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost*1.6f,_curG + 255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost*1.6f,_curB + 255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost*1.6f,255*_audioData.getFFT().spectrum[numPixelsDrawn%512]*_audioBoost*1.6f);
									}
									p.noStroke();
									p.pushMatrix();
									p.translate( _curPixelDepth.x * _drawGridSizeFactor, _curPixelDepth.y * _drawGridSizeFactor, _zOffset + _curPixelDepth.z * -_zFactor );
									p.box( _audioData.getFFT().spectrum[y%512]*_audioBoost*1.6f * 70 );
									p.popMatrix();
									
									break;
							}
							
							numPixelsDrawn++;
						}

					}

					p.endShape();
				}
			}
		}

		// display.updatePixels();

		// if(numPixelsDrawn > 2000 && numPixelsDrawn < 10000) {
		// Draw the image
		// image(display, 0, 0);
		
		p.popMatrix();
	}
	
	public void swapRowBased() {
		_isRowBased = !_isRowBased;
	}

	public void newBaseColors() {
		_curR = p.random(100,255);
		_curG = p.random(100,255);
		_curB = p.random(100,255);
	}


	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
			newBaseColors();
		}
		if ( p.key == 'v' || p.key == 'V' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_03 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_03 ) == 1 ) {
			swapRowBased();
		}
		if ( p.key == 'f' || p.key == 'F' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_05 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_05 ) == 1 ) {
//			newFollowObject();
		}
		if ( p.key == 'm' || p.key == 'M' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
			newBaseColors();
			newCamera();
			_mode = P.round( p.random( 0, 2 ) );
			updateMode();
		}
		if ( p.key == '1' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_09 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_09 ) == 1 ) {
			_mode = MAPPING;
			updateMode();
		} else if ( p.key == '2' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_10 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_10 ) == 1 ) {
			_mode = BOXES;
			updateMode();
		} else if ( p.key == '3' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_11 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_11 ) == 1 ) {
			_mode = MESH;
			updateMode();
		} else if ( p.getMidi().midiPadIsOn( MidiWrapper.PAD_05 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_05 ) == 1 ) {
			_pixelsSkip--;
			_pixelsSkip = P.constrain(_pixelsSkip,5,15);
		} else if ( p.getMidi().midiPadIsOn( MidiWrapper.PAD_06 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_06 ) == 1 ) {
			_pixelsSkip++;
			_pixelsSkip = P.constrain(_pixelsSkip,5,15);
		} else if ( p.getMidi().midiPadIsOn( MidiWrapper.PAD_07 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_07 ) == 1 ) {
			_kinectinterface.tiltDown();
		} else if ( p.getMidi().midiPadIsOn( MidiWrapper.PAD_08 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_08 ) == 1 ) {
			_kinectinterface.tiltUp();
		}

		if (p.key == ' ') {
//			Renderer.renderScreenToJPG(p);
		} else if (p.key == P.CODED) {
			if (p.keyCode == P.UP) {
				_kinectinterface.tiltUp();
			} else if (p.keyCode == P.DOWN) {
				_kinectinterface.tiltDown();
			}
		}
		
		
		// make sure proper skip value is used
		

	}
	
	void updateMode() {
		switch( _mode ) {
		case MAPPING :
			_pixelsSkip = 10;
			break;
		case MESH :
			_pixelsSkip = 6;
			break;
		case BOXES :
			_pixelsSkip = 9;
			break;
	}

	}

	void newCamera()
	{
//		int randCamera = p.round( p.random( 0, 2 ) );
//		int newZ = p.round( p.random( -500, -300 ) );
//		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 0, 0, newZ );
//		else if( randCamera == 1 ) _curCamera = new CameraSpotter( p, 0, 0, newZ );
//		else if( randCamera == 2 ) _curCamera = new CameraOscillate( p, 0, 0, newZ/2, 100 );
		_curCamera = new CameraBasic( p, 0, 0, -1500 );
		_curCamera.reset();
		
		_curCamera.setTarget( p.width/2, p.height/2, 0 );

	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}

}

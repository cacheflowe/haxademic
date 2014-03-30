package com.haxademic.app.haxmapper;

import java.awt.Point;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Capture;
import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class HaxMapper
extends PAppletHax {
	
	PImage img;
	
	PGraphics _barsTexture;
	PGraphics _eqTexture;
	PGraphics _webcamTexture;
	Capture _webCam;
	PGraphics _movieTexture;
	Movie _movie;
	ArrayList<IMappedPolygon> _mappedPolygons;
	
	protected String _inputFileLines[];
	
	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.haxmapper.HaxMapper" });
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fullscreen", "true" );
		_appConfig.setProperty( "fills_screen", "true" );
		_appConfig.setProperty( "mapping_file", FileUtil.getHaxademicDataPath() + "text/mapping/mapping-2014-03-24-09-29-28.txt" );
	}

	public void setup() {
		super.setup();
		img = loadImage(FileUtil.getHaxademicDataPath() + "images/justin-tiny-color1.png");
		// img = loadImage(FileUtil.getHaxademicDataPath() + "images/sphere-map-test-2.jpg");
		noStroke();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_mappedPolygons = new ArrayList<IMappedPolygon>();
		if( _appConfig.getString("mapping_file", "") == "" ) {
			for(int i=0; i < 100; i++ ) {
				float startX = p.random(0,p.width);
				float startY = p.random(0,p.height);
				_mappedPolygons.add( new MappedTriangle( startX, startY, startX + p.random(-300,300), startY + p.random(-300,300), startX + p.random(-300,300), startY + p.random(-300,300) ) );
			}
			_mappedPolygons.add( new MappedTriangle( 100, 200, 400, 700, 650, 300 ) );
		} else {
			_inputFileLines = loadStrings(_appConfig.getString("mapping_file", ""));
			for( int i=0; i < _inputFileLines.length; i++ ) {
				String inputLine = _inputFileLines[i]; 
				// count lines that contain characters
				if( inputLine.indexOf("#group#") != -1 ) {
					// group!
				} else if( inputLine.indexOf("#poly#") != -1 ) {
					// poly!
					inputLine = inputLine.replace("#poly#", "");
					String polyPoints[] = inputLine.split(",");
					if(polyPoints.length == 6) {
						_mappedPolygons.add( new MappedTriangle( 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] )
						) );
					} else if(polyPoints.length == 8) {
						_mappedPolygons.add( new MappedQuad( 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ),
								ConvertUtil.stringToFloat( polyPoints[6] ), 
								ConvertUtil.stringToFloat( polyPoints[7] )
						) );
					}
				}  
			}
			
		}
		
//		initWebCam();
	}
	
	void initWebCam() {
		String[] cameras = Capture.list();
		if (cameras.length == 0) {
			exit();
		} else {
			_webCam = new Capture(this, cameras[0]);
			_webCam.start();
		}      
	}
		
	public void drawApp() {
		updateTextures();
		
		background(0);
		
		
		// basic quad
//		beginShape(QUADS);
//		texture(_barsTexture);
//		vertex(50+ 90f*sin(f/10f), 70+ 50f*sin(f/9f),  150f*sin(f/6f), 	0, 0);
//		vertex(250+ 90f*sin(f/7f), 100+ 50f*sin(f/7f), 150f*sin(f/7f), 	_barsTexture.width, 0);
//		vertex(300+ 90f*sin(f/3f), 350+ 50f*sin(f/4f), 150f*sin(f/3f), 	_barsTexture.width, _barsTexture.height);
//		vertex(90+ 90f*sin(f/9f),  300+ 60f*sin(f/6f), 150f*sin(f/8f), 	0, _barsTexture.height);
//		endShape();
		
		// update triangles
		for(int i=0; i < _mappedPolygons.size(); i++ ) {
			IMappedPolygon triangle = _mappedPolygons.get(i);
			triangle.draw(p.g);
		}	
		// called after polygon draw() to be sure that polygon's texture has initialized
		checkBeat();
	}
	
	protected void checkBeat() {
		int[] beatDetectArr = _audioInput.getBeatDetection();
		boolean isKickCount = (beatDetectArr[0] > 0);
		boolean isSnareCount = (beatDetectArr[1] > 0);
		boolean isHatCount = (beatDetectArr[2] > 0);
		boolean isOnsetCount = (beatDetectArr[3] > 0);
		// if(isKickCount == true || isSnareCount == true || isHatCount == true || isOnsetCount == true) {
		if( isKickCount == true || isSnareCount == true ) {
			randomizeNextPolygon();
		}
	}
	
	protected void randomizeNextPolygon() {
		for(int i=0; i < _mappedPolygons.size(); i++ ) {
			IMappedPolygon triangle = _mappedPolygons.get(i);
			if(p.random(0,100) > 99) {
				triangle.rotateTexture();
				
				float rand = p.random(0,100);
				if(rand > 66) {
					triangle.setTexture(_barsTexture);
				} else if(rand > 33) {
					triangle.setTexture(_eqTexture);					
				} else {
					triangle.setTexture(_movieTexture);
				}
				
				triangle.setTextureStyle( MathUtil.randBoolean(p) );
			}
		}		
	}
	
	public void updateTextures() {
		// webcam ---------------------------------------------------
		if( _webCam != null && _webCam.available() ) { 
			if( _webcamTexture == null && _webCam.width > 1 ) {
				_webcamTexture = p.createGraphics( _webCam.width, _webCam.height, PConstants.OPENGL );
			}
			_webCam.read(); 

			if( _webcamTexture != null ) {
				_webcamTexture.beginDraw();
				_webcamTexture.image( _webCam.get(), 0, 0, _webCam.width, _webCam.height );
				_webcamTexture.endDraw();
			}
		}
		
		// video ----------------------------------------------------
		if( _movie == null ) {
			_movie = new Movie( p, FileUtil.getHaxademicDataPath() + "video/smoke-loop.mov" );
			_movie.play();
			_movie.loop();
			_movie.volume(0);
			_movie.speed(1f);
			_movieTexture = p.createGraphics( _movie.width, _movie.height, PConstants.OPENGL );
		}
		_movieTexture.beginDraw();
		_movieTexture.image(_movie, 0, 0);
		_movieTexture.endDraw();
		
		// repeating columns ----------------------------------------
		if( _barsTexture == null ) {
			_barsTexture = p.createGraphics(100, 100, PConstants.OPENGL);
			_barsTexture.smooth(OpenGLUtil.SMOOTH_HIGH);
		}
		int barW = 20;
		int x = p.frameCount % (barW * 2);
		
		_barsTexture.beginDraw();
		_barsTexture.clear();
		
		for( int i=x - barW*2; i < _barsTexture.width; i+=barW*2 ) {
			_barsTexture.fill( 0 );
			_barsTexture.rect(i, 0, barW, _barsTexture.height );
			_barsTexture.fill( 255 );
			_barsTexture.rect(i+barW, 0, barW, _barsTexture.height );
		}
		
		_barsTexture.endDraw();

		// eq columns ------------------------------------------------
		if( _eqTexture == null ) {
			_eqTexture = p.createGraphics(200, 100, PConstants.OPENGL);
		}
		int numBands = 32;
		float eqW = P.ceil( _eqTexture.width / numBands );
		float spectrumInterval = (int) ( 256 / numBands );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		
		_eqTexture.beginDraw();
		_eqTexture.clear();
		
		for( int i=0; i < numBands; i++ ) {
			_eqTexture.fill( 255 * p._audioInput.getFFT().spectrum[P.floor(i*spectrumInterval)] * 2, 255 );
			_eqTexture.rect(i * eqW, 0, eqW, _eqTexture.height );
		}
		
		_eqTexture.endDraw();
	}
	
}
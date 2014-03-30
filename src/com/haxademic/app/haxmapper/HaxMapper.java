package com.haxademic.app.haxmapper;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderBwEyeJacker;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWebCam;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class HaxMapper
extends PAppletHax {
	
	PImage img;
	
	ArrayList<IMappedPolygon> _mappedPolygons;
	ArrayList<BaseTexture> _curTextures;
	
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
		
		buildTextures();
	}
	
	protected void buildTextures() {
		_curTextures = new ArrayList<BaseTexture>();
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-in-water.mp4" ));
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/smoke-loop.mov" ));
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/clouds-timelapse.mov" ));
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/water.mp4" ));
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/tree-loop.mp4" ));
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-grow-shrink.mp4" ));
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/fire.mp4" ));
		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/bubbles.mp4" ));		
		_curTextures.add( new TextureScrollingColumns( 100, 100 ));
		_curTextures.add( new TextureEQColumns( 200, 100 ));
		_curTextures.add( new TextureShaderBwEyeJacker( 200, 200 ));
//		_curTextures.add( new TextureWebCam());
		
	}
			
	public void drawApp() {
		updateTextures();
		
		background(0);
		
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
				int randTexture = MathUtil.randRange( 0, _curTextures.size() - 1 );
				triangle.setTexture(_curTextures.get(randTexture).texture());
				triangle.setTextureStyle( MathUtil.randBoolean(p) );
				triangle.rotateTexture();
			}
		}		
	}
	
	public void updateTextures() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).update();
		}
	}
	
}
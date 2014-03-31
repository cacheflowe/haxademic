package com.haxademic.app.haxmapper;

import java.util.ArrayList;

import processing.core.PApplet;

import com.haxademic.app.haxmapper.overlays.MeshLines;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.polygons.MappedQuad;
import com.haxademic.app.haxmapper.polygons.MappedTriangle;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderBwEyeJacker;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.midi.MidiWrapper;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class HaxMapper
extends PAppletHax {
		
	protected String _inputFileLines[];
	protected ArrayList<IMappedPolygon> _mappedPolygons;
	protected ArrayList<BaseTexture> _curTextures;
	protected MeshLines _meshLines;
	
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
		noStroke();
		p.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		
		_meshLines = new MeshLines( p.width, p.height );
		
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
						// add polygons
						_mappedPolygons.add( new MappedTriangle( 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] )
						) );
						// add to mesh
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ) 
						);
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ) 
						);
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ), 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ) 
						);
					} else if(polyPoints.length == 8) {
						// add polygons
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
						// add to mesh
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ) 
						);
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ) 
						);
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ),
								ConvertUtil.stringToFloat( polyPoints[6] ), 
								ConvertUtil.stringToFloat( polyPoints[7] )
						);
						_meshLines.addSegment(
								ConvertUtil.stringToFloat( polyPoints[6] ), 
								ConvertUtil.stringToFloat( polyPoints[7] ), 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ) 
						);
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
//		_curTextures.add( new TextureVideoPlayer( 640, 360, "video/loops/bubbles.mp4" ));		
		_curTextures.add( new TextureScrollingColumns( 100, 100 ));
		_curTextures.add( new TextureEQColumns( 200, 100 ));
		_curTextures.add( new TextureEQGrid( 320, 160 ));
		_curTextures.add( new TextureShaderBwEyeJacker( 200, 200 ));
//		_curTextures.add( new TextureShaderGlowWave( 200, 200 ));
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
		
		// draw mesh on top
		_meshLines.update();
		p.image( _meshLines.texture(), 0, 0, _meshLines.texture().width, _meshLines.texture().height );
		
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
			// randomizeNextPolygon();
		}
	}
	
	protected void randomizeNextPolygon() {
//		for(int i=0; i < _mappedPolygons.size(); i++ ) {
			IMappedPolygon triangle = _mappedPolygons.get( MathUtil.randRange( 0, _mappedPolygons.size() - 1 ) );
			int randTexture = MathUtil.randRange( 0, _curTextures.size() - 1 );
			triangle.setTexture(_curTextures.get(randTexture).texture());
			triangle.setTextureStyle( MathUtil.randBoolean(p) );
			triangle.rotateTexture();
//		}		
	}
	
	public void updateTextures() {
		for( int i=0; i < _curTextures.size(); i++ ) {
			_curTextures.get(i).update();
		}
	}
	
	
	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		
//		if( p.key == 'a' || p.key == 'A' ){
//			_isAutoPilot = !_isAutoPilot;
//			P.println("_isAutoPilot = "+_isAutoPilot);
//		}
//		if( p.key == 'S' ){
//			_isStressTesting = !_isStressTesting;
//			P.println("_isStressTesting = "+_isStressTesting);
//		}
		if ( p.key == 'c' || p.key == 'C' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
//			pickNewColors();
		}
		if ( p.key == 'm' || p.key == 'M' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
			for( int i=0; i < _curTextures.size(); i++ ) {
				_curTextures.get(i).newMode();
			}
		}
		if ( p.key == 'l' || p.key == 'L' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_08 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_08 ) == 1 ) {
			for( int i=0; i < _curTextures.size(); i++ ) {
				_curTextures.get(i).newLineMode();
			}
			_meshLines.updateLineMode();
		}
		if ( p.key == 'v' || p.key == 'V' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
			for( int i=0; i < _curTextures.size(); i++ ) {
				_curTextures.get(i).newRotation();
			}
		}
		if ( p.key == 'n' || p.key == 'N' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_03 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_03 ) == 1 ) {
			for( int i=0; i < _curTextures.size(); i++ ) {
				_curTextures.get(i).updateTiming();
			}
		}
		if ( p.key == 'f' || p.key == 'F' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_05 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_05 ) == 1 ) {
			for( int i=0; i < _curTextures.size(); i++ ) {
				_curTextures.get(i).updateTimingSection();
			}
		}
		if ( p.key == ' ' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_07 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_07 ) == 1 ) {
			randomizeNextPolygon();
//			pickNewColors();
		}
	}
	

}
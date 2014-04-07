package com.haxademic.app.haxmapper;

import java.util.ArrayList;

import oscP5.OscMessage;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.app.haxmapper.polygons.MappedQuad;
import com.haxademic.app.haxmapper.polygons.MappedTriangle;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.midi.AkaiMpdPads;
import com.haxademic.core.hardware.osc.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class HaxMapper
extends PAppletHax {
		
	protected String _inputFileLines[];
	protected PGraphics _overlayPG;
	protected ArrayList<MappingGroup> _mappingGroups;
	protected ArrayList<BaseTexture> _texturePool;
	protected ArrayList<BaseTexture> _activeTextures;
	
	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07});
	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{});

	protected PShader _brightness;
	protected float _brightnessVal = 1f;

	
	public void oscEvent(OscMessage theOscMessage) {  
		super.oscEvent(theOscMessage);
		String oscMsg = theOscMessage.addrPattern();
		if( oscMsg.indexOf("/7/fader0") != -1) {
			_brightnessVal = theOscMessage.get(0).floatValue() * 2.0f;
		}
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fullscreen", "true" );
		_appConfig.setProperty( "fills_screen", "true" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		noStroke();
		importPolygons();
		buildTextures();
		buildPostProcessingChain();
	}
	
	protected void importPolygons() {
		_overlayPG = P.p.createGraphics( p.width, p.height, PConstants.OPENGL );
		_mappingGroups = new ArrayList<MappingGroup>();
		
		if( _appConfig.getString("mapping_file", "") == "" ) {
			_mappingGroups.add( new MappingGroup( this, _overlayPG ) );
			for(int i=0; i < 200; i++ ) {
				// create triangle
				float startX = p.random(0,p.width);
				float startY = p.random(0,p.height);
				float x2 = startX + p.random(-300,300);
				float y2 = startY + p.random(-300,300);
				float x3 = startX + p.random(-300,300);
				float y3 = startY + p.random(-300,300);
				// add polygon
				_mappingGroups.get(0).addPolygon( new MappedTriangle( startX, startY, x2, y2, y3, y3 ) );
				// add to mesh
				_mappingGroups.get(0).addMeshSegment( startX, startY, x2, y2 );
				_mappingGroups.get(0).addMeshSegment( x2, y2, x3, y3 );
				_mappingGroups.get(0).addMeshSegment( x3, y3, startX, startY );

			}
			_mappingGroups.get(0).addPolygon( new MappedTriangle( 100, 200, 400, 700, 650, 300 ) );
		} else {
			_inputFileLines = loadStrings(_appConfig.getString("mapping_file", ""));
			for( int i=0; i < _inputFileLines.length; i++ ) {
				String inputLine = _inputFileLines[i]; 
				// count lines that contain characters
				if( inputLine.indexOf("#group#") != -1 ) {
					_mappingGroups.add( new MappingGroup( this, _overlayPG ) );
				} else if( inputLine.indexOf("#poly#") != -1 ) {
					// poly!
					inputLine = inputLine.replace("#poly#", "");
					String polyPoints[] = inputLine.split(",");
					if(polyPoints.length == 6) {
						// add polygons
						_mappingGroups.get(_mappingGroups.size()-1).addPolygon( new MappedTriangle( 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] )
						) );
						// add to mesh
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ) 
						);
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ) 
						);
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ), 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ) 
						);
					} else if(polyPoints.length == 8) {
						// add polygons
						_mappingGroups.get(_mappingGroups.size()-1).addPolygon( new MappedQuad( 
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
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ), 
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ) 
						);
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[2] ), 
								ConvertUtil.stringToFloat( polyPoints[3] ), 
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ) 
						);
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[4] ), 
								ConvertUtil.stringToFloat( polyPoints[5] ),
								ConvertUtil.stringToFloat( polyPoints[6] ), 
								ConvertUtil.stringToFloat( polyPoints[7] )
						);
						_mappingGroups.get(_mappingGroups.size()-1).addMeshSegment(
								ConvertUtil.stringToFloat( polyPoints[6] ), 
								ConvertUtil.stringToFloat( polyPoints[7] ), 
								ConvertUtil.stringToFloat( polyPoints[0] ), 
								ConvertUtil.stringToFloat( polyPoints[1] ) 
						);
					}
				}  
			}
		}
	}
	
	protected void buildTextures() {
		_texturePool = new ArrayList<BaseTexture>();
		_activeTextures = new ArrayList<BaseTexture>();
		addTexturesToPool();
		buildPolygonGroups();
	}
			
	protected void buildPolygonGroups() {
		// override this!
	}
	
	protected void addTexturesToPool() {
		// override this!
	}

	protected void buildPostProcessingChain() {
		_brightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", 1.0f );
		
	}
	
	public void drawApp() {
		
		background(0);
		
		updateActiveTextures();
		drawPolygonGroups();
		drawOverlays();
		checkBeat();
		postProcessFilters();
	}
	
	protected void updateActiveTextures() {
		// reset active texture pool array
		while( _activeTextures.size() > 0 ) {
			_activeTextures.remove( _activeTextures.size() - 1 ).resetUseCount();
		}
		// figure out which textures are being used and rebuild array, telling 
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			ArrayList<BaseTexture> textures = _mappingGroups.get(i).textures();
			for( int j=0; j < textures.size(); j++ ) {
				if( _activeTextures.indexOf( textures.get(j) ) == -1 ) {
					textures.get(j).setActive(true);
					_activeTextures.add( textures.get(j) );
				}
			}
		}
		// set inactive pool textures' _active state to false (mostly for turning off video players)
		for( int i=0; i < _texturePool.size(); i++ ) {
			if( _texturePool.get(i).useCount() == 0 ) {
				// P.println("setting to false",i);
				_texturePool.get(i).setActive(false);
			}
		}
		// update active textures, once each
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).update();
		}
	}
	
	protected void drawPolygonGroups() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).draw();
		}
	}
	
	protected void drawOverlays() {
		DrawUtil.setColorForPImage(p);
		DrawUtil.resetPImageAlpha(p);
		// draw mesh on top
		_overlayPG.beginDraw();
		_overlayPG.clear();
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).drawOverlay();
		}
		_overlayPG.endDraw();
		p.image( _overlayPG, 0, 0, _overlayPG.width, _overlayPG.height );
	}
	
	protected void postProcessFilters() {
		_brightness.set("brightness", _brightnessVal );
		p.filter( _brightness );	
	}
	
	protected void checkBeat() {
//		int[] beatDetectArr = _audioInput.getBeatDetection();
//		boolean isKickCount = (beatDetectArr[0] > 0);
//		boolean isSnareCount = (beatDetectArr[1] > 0);
//		boolean isHatCount = (beatDetectArr[2] > 0);
//		boolean isOnsetCount = (beatDetectArr[3] > 0);
//		// if(isKickCount == true || isSnareCount == true || isHatCount == true || isOnsetCount == true) {
//		if( isKickCount == true || isSnareCount == true ) {
//			// randomizeNextPolygon();
//		}
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
		if ( _colorTrigger.active() == true ) {
			for( int i=0; i < _mappingGroups.size(); i++ ) {
				_mappingGroups.get(i).newColor();
			}
		}
		if ( _modeTrigger.active() == true ) {
			for( int i=0; i < _mappingGroups.size(); i++ ) {
				_mappingGroups.get(i).newMode();
			}
		}
		if ( _lineModeTrigger.active() == true ) {
			for( int i=0; i < _mappingGroups.size(); i++ ) {
				_mappingGroups.get(i).newLineMode();
			}
		}
		if ( _rotationTrigger.active() == true ) {
			for( int i=0; i < _mappingGroups.size(); i++ ) {
				_mappingGroups.get(i).newRotation();
			}
		}
		
		if ( _timingTrigger.active() == true ) {
			for( int i=0; i < _activeTextures.size(); i++ ) {
				_activeTextures.get(i).updateTiming();
			}
		}
		if ( _timingSectionTrigger.active() == true ) {
			for( int i=0; i < _activeTextures.size(); i++ ) {
				_activeTextures.get(i).updateTimingSection();
			}
		}
		if ( _bigChangeTrigger.active() == true ) bigChangeTrigger();
		if ( _audioInputUpTrigger.active() == true ) _audioInput.gainUp();
		if ( _audioInputDownTrigger.active() == true ) _audioInput.gainDown();
	}
	
	protected void bigChangeTrigger() {
		for( int i=0; i < _mappingGroups.size(); i++ ) _mappingGroups.get(i).randomizeNextPolygon();
//		pickNewColors();
	}

}










////_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-in-water.mp4" ));
////_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/smoke-loop.mov" ));
////_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/clouds-timelapse.mov" ));
////_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/water.mp4" ));
//_texturePool.add( new TextureVideoPlayer( 640, 360, "video/horrorhouse/genessier/Genessier_Emlyn_1080P_h264/Genessier_Emlyn_1080P_h264-desktop.m4v" ));
////_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/tree-loop.mp4" ));
//_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-grow-shrink.mp4" ));
//_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/fire.mp4" ));
//_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/bubbles.mp4" ));		
//_texturePool.add( new TextureScrollingColumns( 100, 100 ));
//_texturePool.add( new TextureEQColumns( 200, 100 ));
//_texturePool.add( new TextureEQGrid( 320, 160 ));
////_texturePool.add( new TextureShaderBwEyeJacker( 200, 200 ));
////_texturePool.add( new TextureShaderGlowWave( 200, 200 ));
//_texturePool.add( new TextureShaderWavyCheckerPlanes( 300, 300 ));
//_texturePool.add( new TextureColorAudioFade( 100, 100 ));
//_texturePool.add( new TextureColorAudioSlide( 100, 100 ));
//_texturePool.add( new TextureSphereRotate( 400, 400 ));
////_texturePool.add( new TextureShaderGlowWave( 200, 200 ));
////_texturePool.add( new TextureWebCam());


//// temp: give each group a texture to start ---------------
//MappingGroup centerGroup = _mappingGroups.get(1);
//centerGroup.pushTexture( _texturePool.get(0) );
//centerGroup.pushTexture( _texturePool.get(1) );
//centerGroup.pushTexture( _texturePool.get(2) );
//centerGroup.pushTexture( _texturePool.get(3) );
//centerGroup.pushTexture( _texturePool.get(4) );
//centerGroup.pushTexture( _texturePool.get(5) );
//centerGroup.pushTexture( _texturePool.get(6) );
//centerGroup.pushTexture( _texturePool.get(7) );
//centerGroup.pushTexture( _texturePool.get(10) );
//
//MappingGroup leftGroup = _mappingGroups.get(0);
//leftGroup.pushTexture( _texturePool.get(5) );
//leftGroup.pushTexture( _texturePool.get(6) );
//leftGroup.pushTexture( _texturePool.get(8) );
//leftGroup.pushTexture( _texturePool.get(9) );
//
//MappingGroup rightGroup = _mappingGroups.get(2);
//rightGroup.pushTexture( _texturePool.get(5) );
//rightGroup.pushTexture( _texturePool.get(6) );
//rightGroup.pushTexture( _texturePool.get(8) );
//rightGroup.pushTexture( _texturePool.get(9) );

//MappingGroup bottomGroup = _mappingGroups.get(3);
//bottomGroup.pushTexture( _texturePool.get(7) );
//bottomGroup.pushTexture( _texturePool.get(8) );
//bottomGroup.pushTexture( _texturePool.get(9) );

// -- loop through all
//for( int i=0; i < _mappingGroups.size(); i++ ) {
//_mappingGroups.get(i).pushTexture( _texturePool.get(0) );
//}


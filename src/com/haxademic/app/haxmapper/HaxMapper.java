package com.haxademic.app.haxmapper;

import java.util.ArrayList;

import oscP5.OscMessage;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PShader;

import com.haxademic.app.haxmapper.distribution.MappingGroup;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.polygons.MappedQuad;
import com.haxademic.app.haxmapper.polygons.MappedTriangle;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.midi.AbletonNotes;
import com.haxademic.core.hardware.midi.AkaiMpdPads;
import com.haxademic.core.hardware.osc.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class HaxMapper
extends PAppletHax {
		
	public static int MAX_ACTIVE_TEXTURES = 4;
	public static int MAX_ACTIVE_TEXTURES_PER_GROUP = 2;
	public static int MAX_ACTIVE_MOVIE_TEXTURES = 2;
	
	protected String _inputFileLines[];
	protected PGraphics _overlayPG;
	protected ArrayList<MappingGroup> _mappingGroups;
	protected ArrayList<BaseTexture> _texturePool;
	protected ArrayList<BaseTexture> _curTexturePool;
	protected ArrayList<BaseTexture> _movieTexturePool;
	protected ArrayList<BaseTexture> _activeTextures;
	
	protected boolean _debugTextures = false;

	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, AbletonNotes.NOTE_02});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, AbletonNotes.NOTE_03});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, AbletonNotes.NOTE_04});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, AbletonNotes.NOTE_05});
	protected InputTrigger _allSameTextureTrigger = new InputTrigger(new char[]{'a'},new String[]{TouchOscPads.PAD_06},new Integer[]{AkaiMpdPads.PAD_06, AbletonNotes.NOTE_06});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, AbletonNotes.NOTE_07});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, AbletonNotes.NOTE_08});
	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{});
	protected InputTrigger _brightnessUpTrigger = new InputTrigger(new char[]{']'},new String[]{},new Integer[]{});
	protected InputTrigger _brightnessDownTrigger = new InputTrigger(new char[]{'['},new String[]{},new Integer[]{});
	protected InputTrigger _debugTexturesTrigger = new InputTrigger(new char[]{'d'},new String[]{},new Integer[]{});
	protected int _lastInputMillis = 0;
	protected int numBeatsDetected = 0;

	protected PShader _brightness;
	protected float _brightnessVal = 1f;
	protected PShader _blurH;
	protected PShader _blurV;

	
	public void oscEvent(OscMessage theOscMessage) {  
		super.oscEvent(theOscMessage);
		String oscMsg = theOscMessage.addrPattern();
		// handle brightness slider
		if( oscMsg.indexOf("/7/fader0") != -1) {
			_brightnessVal = theOscMessage.get(0).floatValue() * 3.0f;
		}		
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "fullscreen", "true" );
		_appConfig.setProperty( "fills_screen", "true" );
		_appConfig.setProperty( "osc_active", "true" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		noStroke();
		importPolygons();
		for( int i=0; i < _mappingGroups.size(); i++ ) _mappingGroups.get(i).completePolygonImport();
		buildTextures();
		buildPostProcessingChain();
	}
	
	protected void importPolygons() {
		_overlayPG = P.p.createGraphics( p.width, p.height, PConstants.OPENGL );
		_overlayPG.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		_mappingGroups = new ArrayList<MappingGroup>();
		
		if( _appConfig.getString("mapping_file", "") == "" ) {
			_mappingGroups.add( new MappingGroup( this, _overlayPG ) );
			for(int i=0; i < 200; i++ ) {
				// create triangle
				float startX = p.random(0,p.width);
				float startY = p.random(0,p.height);
				float x2 = startX + p.random(-300,300);
				float y2 = startY + p.random(-300,300);
				// float x3 = startX + p.random(-300,300);
				float y3 = startY + p.random(-300,300);
				// add polygon
				_mappingGroups.get(0).addPolygon( new MappedTriangle( startX, startY, x2, y2, y3, y3 ) );

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
					}
				}  
			}
		}
	}
	
	protected void buildTextures() {
		_texturePool = new ArrayList<BaseTexture>();
		_curTexturePool = new ArrayList<BaseTexture>();
		_movieTexturePool = new ArrayList<BaseTexture>();
		_activeTextures = new ArrayList<BaseTexture>();
		addTexturesToPool();
		buildMappingGroups();
	}
			
	protected void buildMappingGroups() {
		// override this!
	}
	
	protected void addTexturesToPool() {
		// override this!
	}

	protected void buildPostProcessingChain() {
		_brightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", 1.0f );

		_blurH = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set("h", 1.0f );
		_blurV = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set("v", 1.0f );
		
	}
	
	public void drawApp() {
		
		background(0);
		
		checkBeat();
		updateActiveTextures();
		traverseGroups();
		drawPolygonGroups();
		drawOverlays();
		postProcessFilters();
		if(_debugTextures == true) debugTextures();
	}
	
	protected void updateActiveTextures() {
		// reset active texture pool array
		while( _activeTextures.size() > 0 ) {
			_activeTextures.remove( _activeTextures.size() - 1 ).resetUseCount();
		}
		// figure out which textures are being used and rebuild array, telling active textures that they're active
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
				_texturePool.get(i).setActive(false);
			}
		}
		// update active textures, once each
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).update();
		}
//		P.println(_activeTextures.size());
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
//		_overlayPG.filter(_blurH);
//		_overlayPG.filter(_blurV);
		p.image( _overlayPG, 0, 0, _overlayPG.width, _overlayPG.height );
	}
	
	protected void postProcessFilters() {
		_brightness.set("brightness", _brightnessVal );
		p.filter( _brightness );	
	}
	
	protected void debugTextures() {
		// debug current textures
		for( int i=0; i < _activeTextures.size(); i++ ) {
			p.image(_activeTextures.get(i).texture(), i * 100, 0, 100, 100);
		}
	}
	
	protected void checkBeat() {
	}
	
	public void resetBeatDetectMode() {
		_lastInputMillis = p.millis();
		numBeatsDetected = 1;
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
			resetBeatDetectMode();
			updateColor();
		}
		if ( _modeTrigger.active() == true ) {
			newMode();
			traverseTrigger();
		}
		if ( _lineModeTrigger.active() == true ) {
			resetBeatDetectMode();
			updateLineMode();
		}
		if ( _rotationTrigger.active() == true ) {
			resetBeatDetectMode();
			updateRotation();
		}
		if ( _timingTrigger.active() == true ) {
			resetBeatDetectMode();
			updateTiming();
		}
		if ( _timingSectionTrigger.active() == true ) {
			updateTimingSection();
		}
		if ( _bigChangeTrigger.active() == true ) {
			resetBeatDetectMode();
			bigChangeTrigger();
		}
		if ( _allSameTextureTrigger.active() == true ) {
			resetBeatDetectMode();
			setAllSameTexture();
		}
		if ( _audioInputUpTrigger.active() == true ) audioIn.gainUp();
		if ( _audioInputDownTrigger.active() == true ) audioIn.gainDown();
		if ( _brightnessUpTrigger.active() == true ) _brightnessVal += 0.1f;
		if ( _brightnessDownTrigger.active() == true ) _brightnessVal -= 0.1f;
		if ( _debugTexturesTrigger.active() == true ) _debugTextures = !_debugTextures;
	}
	
	protected void newMode() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).newMode();
		}
	}
	
	protected void updateColor() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).newColor();
			_mappingGroups.get(i).pulseColor();
		}
	}
	
	protected void updateLineMode() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).newLineMode();
		}
	}
	
	protected void updateRotation() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
//			_mappingGroups.get(i).newRotation();
			_mappingGroups.get(i).newRandomRotation();
		}
	}
	
	protected void updateTiming() {
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTiming();
		}
	}
	
	protected void updateTimingSection() {
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTimingSection();
		}
	}
	
	protected void bigChangeTrigger() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).randomTextureToRandomPolygon();
		}
//		pickNewColors();
	}

	protected void setAllSameTexture() {
		boolean mode = MathUtil.randBoolean(p);
		BaseTexture newTexture = _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 ) );
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).clearAllTextures();
			_mappingGroups.get(i).pushTexture( newTexture );
			_mappingGroups.get(i).setAllPolygonsToTexture(0);
			if( mode == true ) {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_CONTAIN_RANDOM_TEX_AREA );
			} else {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_MASK );
			}
		}
	}
	
	protected void traverseTrigger() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).traverseStart();
		}
	}

	protected void traverseGroups() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).traverseUpdate();
		}
	}
}

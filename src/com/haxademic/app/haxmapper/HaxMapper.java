package com.haxademic.app.haxmapper;

import java.awt.Rectangle;
import java.util.ArrayList;

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
import com.haxademic.core.image.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.image.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.image.filters.shaders.DeformBloomFilter;
import com.haxademic.core.image.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.image.filters.shaders.EdgesFilter;
import com.haxademic.core.image.filters.shaders.HalftoneFilter;
import com.haxademic.core.image.filters.shaders.InvertFilter;
import com.haxademic.core.image.filters.shaders.KaleidoFilter;
import com.haxademic.core.image.filters.shaders.MirrorFilter;
import com.haxademic.core.image.filters.shaders.PixelateFilter;
import com.haxademic.core.image.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.image.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.image.filters.shaders.WobbleFilter;
import com.haxademic.core.math.MathUtil;

import oscP5.OscMessage;
import processing.core.PConstants;
import processing.core.PGraphics;


@SuppressWarnings("serial")
public class HaxMapper
extends PAppletHax {
		
	public static int MAX_ACTIVE_TEXTURES = 4;
	public static int MAX_ACTIVE_TEXTURES_PER_GROUP = 2;
	public static int MAX_ACTIVE_MOVIE_TEXTURES = 2;
	
	protected String _inputFileLines[];
	protected PGraphics _overlayPG;
	protected Rectangle _boundingBox;
	protected float[] extentsX = {-1,-1};
	protected float[] extentsY = {-1,-1};
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

//	protected PShader _brightness;
//	protected float _brightnessVal = 1f;
//	protected PShader _blurH;
//	protected PShader _blurV;

	protected int[] _textureEffectsIndices = {0,0,0,0,0,0,0};	// store a effects number for each texture position after the first
	protected int _numTextureEffects = 15 + 8; // +8 to give a good chance at removing the filter from the texture slot
	
	public void oscEvent(OscMessage theOscMessage) {  
		super.oscEvent(theOscMessage);
		String oscMsg = theOscMessage.addrPattern();
		// handle brightness slider
		if( oscMsg.indexOf("/7/fader0") != -1) {
//			_brightnessVal = theOscMessage.get(0).floatValue() * 3.0f;
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
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		noStroke();
		importPolygons();
		P.println("_boundingBox", _boundingBox);
		for( int i=0; i < _mappingGroups.size(); i++ ) _mappingGroups.get(i).completePolygonImport();
		buildTextures();
	}
	
	protected void importPolygons() {
		_boundingBox = new Rectangle(-1, -1, 0, 0);
		
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
				float x3 = startY + p.random(-300,300);
				float y3 = startY + p.random(-300,300);
				// add polygon
				_mappingGroups.get(0).addPolygon( new MappedTriangle( startX, startY, x2, y2, x3, y3 ) );
				// update bounding box as we build
				updateBoundingBox(startX, startY);
				updateBoundingBox(x2, y2);
				updateBoundingBox(x3, y3);
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
						// update bounding box as we build
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[0] ), ConvertUtil.stringToFloat( polyPoints[1] ));
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[2] ), ConvertUtil.stringToFloat( polyPoints[3] ));
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[4] ), ConvertUtil.stringToFloat( polyPoints[5] ));

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
						// update bounding box as we build
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[0] ), ConvertUtil.stringToFloat( polyPoints[1] ));
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[2] ), ConvertUtil.stringToFloat( polyPoints[3] ));
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[4] ), ConvertUtil.stringToFloat( polyPoints[5] ));
						updateBoundingBox(ConvertUtil.stringToFloat( polyPoints[6] ), ConvertUtil.stringToFloat( polyPoints[7] ));
					}
				}  
			}
		}
	}
	
	protected void updateBoundingBox(float x, float y) {
		if(x < extentsX[0] || extentsX[0] == -1) extentsX[0] = x;
		if(x > extentsX[1] || extentsX[1] == -1) extentsX[1] = x;
		if(y < extentsY[0] || extentsY[0] == -1) extentsY[0] = y;
		if(y > extentsY[1] || extentsY[1] == -1) extentsY[1] = y;
		_boundingBox.x = (int) Math.floor(extentsX[0]);
		_boundingBox.width = (int) Math.ceil(extentsX[1] - extentsX[0]);
		_boundingBox.y = (int) Math.floor(extentsY[0]);
		_boundingBox.height= (int) Math.ceil(extentsY[1] - extentsY[0]);
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
	
	public void drawApp() {
		
		background(0);
		
		checkBeat();
		updateActiveTextures();
		filterActiveTextures();
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
			if( _texturePool.get(i).useCount() == 0 && _texturePool.get(i).isActive() == true ) {
				_texturePool.get(i).setActive(false);
				// P.println("Deactivated: ", _texturePool.get(i).getClass().getName());
			}
		}
		// update active textures, once each
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).update();
		}
//		P.println(_activeTextures.size());
	}
	
	protected void selectNewActiveTextureFilters() {
		for(int i=1; i < _textureEffectsIndices.length; i++) {
			if(MathUtil.randRange(0, 10) > 8) {
				_textureEffectsIndices[i] = MathUtil.randRange(0, _numTextureEffects);
			}
		}
	}
	
	protected void filterActiveTextures() {
		for( int i=0; i < _activeTextures.size(); i++ ) {
			PGraphics pg = _activeTextures.get(i).texture();
			float filterTime = p.frameCount / 40f;
			
			if(_textureEffectsIndices[i] == 1) {
				KaleidoFilter.instance(p).setSides(4);
				KaleidoFilter.instance(p).setAngle(filterTime / 10f);
				KaleidoFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 2) {
				DeformTunnelFanFilter.instance(p).setTime(filterTime);
				DeformTunnelFanFilter.instance(p).applyTo(p);
			} else if(_textureEffectsIndices[i] == 3) {
				EdgesFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 4) {
				MirrorFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 5) {
				WobbleFilter.instance(p).setTime(filterTime);
				WobbleFilter.instance(p).setSpeed(0.5f);
				WobbleFilter.instance(p).setStrength(0.0004f);
				WobbleFilter.instance(p).setSize( 200f);
				WobbleFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 6) {
				InvertFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 7) {
				RadialRipplesFilter.instance(p).setTime(filterTime);
				RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(filterTime));
				RadialRipplesFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 8) {
				BadTVLinesFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 9) {
				EdgesFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 10) {
				CubicLensDistortionFilter.instance(p).setTime(filterTime);
				CubicLensDistortionFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 11) {
				SphereDistortionFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 12) {
				HalftoneFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 13) {
				PixelateFilter.instance(p).setDivider(15f, 15f * pg.height/pg.width);
				PixelateFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 14) {
				DeformBloomFilter.instance(p).setTime(filterTime);
				DeformBloomFilter.instance(p).applyTo(pg);
			} else if(_textureEffectsIndices[i] == 15) {
				DeformTunnelFanFilter.instance(p).setTime(filterTime);
				DeformTunnelFanFilter.instance(p).applyTo(pg);
			}
//			WarperFilter.instance(p).setTime( _timeEaseInc / 5f);
//			WarperFilter.instance(p).applyTo(pg);
//			ColorDistortionFilter.instance(p).setTime( _timeEaseInc / 5f);
//			ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(radsComplete));
//			ColorDistortionFilter.instance(p).applyTo(pg);
//			OpenGLUtil.setTextureRepeat(_buffer);

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
//		_overlayPG.filter(_blurH);
//		_overlayPG.filter(_blurV);
		p.image( _overlayPG, 0, 0, _overlayPG.width, _overlayPG.height );
	}
	
	protected void postProcessFilters() {
		// brightness
		float brightMult = 6f;
		if(p.frameCount < 3) p.midi.controllerChange(3, 41, P.round(127f/brightMult));
		float brightnessVal = p.midi.midiCCPercent(3, 41) * brightMult;
		BrightnessFilter.instance(p).setBrightness(brightnessVal);
		BrightnessFilter.instance(p).applyTo(p);
		
		// color distortion auto
		int distAutoFrame = p.frameCount % 6000;
		float distFrames = 100f;
		if(distAutoFrame <= distFrames) {
			float distAmpAuto = P.sin(distAutoFrame/distFrames * P.PI);
			p.midi.controllerChange(3, 42, P.round(127 * distAmpAuto));
			p.midi.controllerChange(3, 43, P.round(127 * distAmpAuto));
		}
		
		// color distortion
		float colorDistortionAmp = p.midi.midiCCPercent(3, 42) * 0.5f;
		float colorDistortionTimeMult = p.midi.midiCCPercent(3, 43);
		if(colorDistortionAmp > 0) {
			float prevTime = ColorDistortionFilter.instance(p).getTime();
			ColorDistortionFilter.instance(p).setTime(prevTime + 1/100f * colorDistortionTimeMult);
			ColorDistortionFilter.instance(p).setAmplitude(colorDistortionAmp);
			ColorDistortionFilter.instance(p).applyTo(p);
		}
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
//		if ( _brightnessUpTrigger.active() == true ) _brightnessVal += 0.1f;
//		if ( _brightnessDownTrigger.active() == true ) _brightnessVal -= 0.1f;
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
		selectNewActiveTextureFilters();
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

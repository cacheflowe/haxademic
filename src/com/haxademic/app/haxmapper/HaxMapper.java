package com.haxademic.app.haxmapper;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;

import com.haxademic.app.haxmapper.distribution.MappingGroup;
import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxmapper.overlays.FullMaskTextureOverlay;
import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.polygons.MappedQuad;
import com.haxademic.app.haxmapper.polygons.MappedTriangle;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.OpenGLUtil.Blend;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.pshader.DeformBloomFilter;
import com.haxademic.core.draw.filters.pshader.DeformTunnelFanFilter;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneFilter;
import com.haxademic.core.draw.filters.pshader.HueFilter;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.PixelateFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.filters.pshader.SphereDistortionFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.textures.pgraphics.TextureKinectFacePlayback;
import com.haxademic.core.draw.textures.pgraphics.TextureKinectFaceRecording;
import com.haxademic.core.draw.textures.pgraphics.TextureVideoPlayer;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.midi.devices.AbletonNotes;
import com.haxademic.core.hardware.midi.devices.AkaiMpdPads;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.devices.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.sketch.hardware.kinect_openni.KinectFaceRecorder;

import processing.core.PGraphics;


public class HaxMapper
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	// mesh polygons & graphics layers
	protected String _inputFileLines[];
	protected Rectangle _boundingBox;
	protected float[] extentsX = {-1,-1};
	protected float[] extentsY = {-1,-1};
	protected ArrayList<MappingGroup> _mappingGroups;
	protected PGraphics _overlayPG;
	protected PGraphics _fullMask;
	protected PGraphics _fullMaskOverlay;
	protected FullMaskTextureOverlay _fullMaskTexture;
	
	// texture pool
	public static int MAX_ACTIVE_TEXTURES = 4;
	public static int MAX_ACTIVE_TEXTURES_PER_GROUP = 2;
	public static int MAX_ACTIVE_MOVIE_TEXTURES = 2;
	protected ArrayList<BaseTexture> _texturePool;
	protected ArrayList<BaseTexture> _movieTexturePool;
	protected ArrayList<BaseTexture> _activeTextures;
	protected ArrayList<BaseTexture> _overlayTexturePool;
	protected int _texturePoolNextIndex = 0;
	protected boolean _debugTextures = false;

	// user input triggers
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, AbletonNotes.NOTE_03, LaunchControl.PAD_01});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, AbletonNotes.NOTE_05, LaunchControl.PAD_02});
	
	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, AbletonNotes.NOTE_02, LaunchControl.PAD_03});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, AbletonNotes.NOTE_04, LaunchControl.PAD_04});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, AbletonNotes.NOTE_08, LaunchControl.PAD_05});

	protected InputTrigger _newTextureTrigger = new InputTrigger(new char[]{'b'},new String[]{TouchOscPads.PAD_09},new Integer[]{AkaiMpdPads.PAD_09, AbletonNotes.NOTE_09, LaunchControl.PAD_06});
	protected InputTrigger _allSameTextureTrigger = new InputTrigger(new char[]{'a'},new String[]{TouchOscPads.PAD_06},new Integer[]{AkaiMpdPads.PAD_06, AbletonNotes.NOTE_06, LaunchControl.PAD_07});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, AbletonNotes.NOTE_07, LaunchControl.PAD_08});
	
	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{});
	protected InputTrigger _brightnessUpTrigger = new InputTrigger(new char[]{']'},new String[]{},new Integer[]{});
	protected InputTrigger _brightnessDownTrigger = new InputTrigger(new char[]{'['},new String[]{},new Integer[]{});
	protected InputTrigger _debugTexturesTrigger = new InputTrigger(new char[]{'d'},new String[]{},new Integer[]{});
	protected int _lastInputMillis = 0;
	protected int USER_INPUT_BEAT_TIMEOUT = 5000;
	
	// beat-detection trigger intervals
	protected float BEAT_DIVISOR = 1f; // 10 to test
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_MAP_STYLE_CHANGE = (int) Math.ceil(4f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(16f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ALL_SAME = (int) Math.ceil(88f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_LINE_MODE = (int) Math.ceil(32f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING_SECTION = (int) Math.ceil(40f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_MODE = (int) Math.ceil(44f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TEXTURE = (int) Math.ceil(80f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(250f / BEAT_DIVISOR);
	protected boolean _timingDebug = false;
	protected int numBeatsDetected = 0;
	
	// face recorder insanity mode
	protected KinectFaceRecorder _faceRecorder;
	protected BaseTexture _faceRecordingTexture;
	protected BaseTexture _facesPlaybackTexture;

	// dmx physical lighting
	protected RandomLightTiming _dmxLights;

	// global effects processing
	protected int[] _textureEffectsIndices = {0,0,0,0,0,0,0};	// store a effects number for each texture position after the first
	protected int _numTextureEffects = 16 + 8; // +8 to give a good chance at removing the filter from the texture slot
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, "true" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "true" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "false" );
		p.appConfig.setProperty( "osc_active", "true" );
	}
	
	/////////////////////////////////////////////////////////////////
	// Setup: build mapped polygon groups & init texture pools
	/////////////////////////////////////////////////////////////////

	public void setup() {
		super.setup();
//		p.hint(P.DISABLE_DEPTH_SORT);
//		p.hint(P.DISABLE_DEPTH_TEST);
//		p.hint(P.DISABLE_DEPTH_MASK);
		noStroke();
		importPolygons();
		for( int i=0; i < _mappingGroups.size(); i++ ) _mappingGroups.get(i).completePolygonImport();
		buildTextures();
		if(p.appConfig.getInt("dmx_lights_count", 0) > 0) _dmxLights = new RandomLightTiming(p.appConfig.getInt("dmx_lights_count", 0));
	}
	
	protected void importPolygons() {
		_boundingBox = new Rectangle(-1, -1, 0, 0);
		
		_overlayPG = P.p.createGraphics( p.width, p.height, P.P3D );
		_overlayPG.smooth(OpenGLUtil.SMOOTH_MEDIUM);
//		_overlayPG.hint(P.DISABLE_DEPTH_SORT);

		_mappingGroups = new ArrayList<MappingGroup>();
		
		if( p.appConfig.getString("mapping_file", "") == "" ) {
			_mappingGroups.add( new MappingGroup( this, _overlayPG, _boundingBox) );
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
			_inputFileLines = loadStrings(p.appConfig.getString("mapping_file", ""));
			for( int i=0; i < _inputFileLines.length; i++ ) {
				String inputLine = _inputFileLines[i]; 
				// count lines that contain characters
				if( inputLine.indexOf("#group#") != -1 ) {
					_mappingGroups.add( new MappingGroup( this, _overlayPG, _boundingBox ) );
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
	
	protected void buildOverlayMask() {
		// draw black on white
		_fullMask = p.createGraphics(p.width, p.height, P.P3D);
		_fullMask.smooth(OpenGLUtil.SMOOTH_HIGH);
		_fullMask.beginDraw();
		_fullMask.background(255);
		_fullMask.fill(0);
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).drawShapeForMask(_fullMask);
		}
		_fullMask.endDraw();
		
		// crate simple black overlay with polygons excluded via masking 
		_fullMaskOverlay = p.createGraphics(p.width, p.height, P.P3D);
		_fullMaskOverlay.smooth(OpenGLUtil.SMOOTH_HIGH);
		_fullMaskOverlay.beginDraw();
		_fullMaskOverlay.background(0);
		_fullMaskOverlay.endDraw();
		_fullMaskOverlay.mask(_fullMask);
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
		if(p.appConfig.getBoolean("kinect_active", false ) == true) {
			_faceRecorder = new KinectFaceRecorder(this);
			_faceRecordingTexture = new TextureKinectFaceRecording(320, 240);
			_facesPlaybackTexture = new TextureKinectFacePlayback(320, 240);
		}
		
		_texturePool = new ArrayList<BaseTexture>();
		_movieTexturePool = new ArrayList<BaseTexture>();
		_activeTextures = new ArrayList<BaseTexture>();
		_overlayTexturePool = new ArrayList<BaseTexture>();
		_fullMaskTexture = new FullMaskTextureOverlay(_overlayPG, _boundingBox);
		addTexturesToPool();
		storeVideoTextures();
		cycleANewTexture(null);
	}
			
	protected void addTexturesToPool() {
		// override this!
	}
	
	protected void storeVideoTextures() {
		// store just movies to restrain the number of concurrent movies
		for( int i=0; i < _texturePool.size(); i++ ) {
			if( _texturePool.get(i) instanceof TextureVideoPlayer ) {
				_movieTexturePool.add( _texturePool.get(i) );
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// Main draw loop
	/////////////////////////////////////////////////////////////////

	public void drawApp() {
		handleInputTriggers();
		background(0);
		if(_faceRecorder != null) updateFaceRecorder();
		checkBeat();
		updateActiveTextures();
		filterActiveTextures();
		drawMappingGroups();
		drawOverlays();
		postProcessFilters();
		drawOverlayMask();
		runDmxLights();
		if(_debugTextures == true) debugTextures();
	}
	
	protected void drawMappingGroups() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).draw();
		}
	}
	
	protected void drawOverlays() {
		// let current overlay texture update before overlay pgraphics buffer gets composited
		if(_overlayTexturePool.size() > 0) _overlayTexturePool.get(0).update();
		
		// draw mesh & overlay on top
		_overlayPG.beginDraw();
		_overlayPG.clear();
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).drawOverlay();
		}
		// draw semi-transparent current texture on top
		if(_overlayTexturePool.size() > 0) _fullMaskTexture.drawOverlay();
		_overlayPG.endDraw();
		
		// draw composited overlay buffer
		PG.setColorForPImage(p);
		PG.resetPImageAlpha(p);
		OpenGLUtil.setBlendMode(p.g, Blend.ADDITIVE);
//		p.blendMode(P.ADD);
//		p.blendMode(P.SCREEN);
		p.image( _overlayPG, 0, 0, _overlayPG.width, _overlayPG.height );
		OpenGLUtil.setBlendMode(p.g, Blend.DEFAULT);
//		p.blendMode(P.BLEND);
	}
	
	protected void drawOverlayMask() {
		if(p.frameCount == 1) buildOverlayMask();
		p.image( _fullMaskOverlay, 0, 0, _fullMaskOverlay.width, _fullMaskOverlay.height );
	}

	protected void debugTextures() {
		PG.setPImageAlpha(p, 1);
		// debug current textures
		for( int i=0; i < _activeTextures.size(); i++ ) {
			p.image(_activeTextures.get(i).texture(), i * 100, p.height - 100, 100, 100);
		}
		// debug overlay texture
		if(_overlayTexturePool.size() > 0) {
			p.image(_fullMaskTexture.texture(), p.width - 100, p.height - 100, 100, 100);

		}
	}
	
	protected void runDmxLights() {
		if(_dmxLights != null) {
			_dmxLights.update();
			if(_debugTextures == true) _dmxLights.drawDebug(p.g);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// Texture-cycling methods
	/////////////////////////////////////////////////////////////////
	
	protected void shuffleTexturePool() {
		Collections.shuffle(_texturePool);
	}
	
	protected BaseTexture randomActiveTexture() {
		return _activeTextures.get( MathUtil.randRange(0, _activeTextures.size()-1) );
	}
	
	protected BaseTexture randomCurTexture() {
		return _activeTextures.get( MathUtil.randRange(0, _activeTextures.size()-1) );
	}
	
	protected MappingGroup getRandomGroup() {
		return _mappingGroups.get( MathUtil.randRange( 0, _mappingGroups.size()-1) );
	}
	
	protected int nextTexturePoolIndex() {
		_texturePoolNextIndex++;
		if(_texturePoolNextIndex >= _texturePool.size()) {
			_texturePoolNextIndex = 0;
			shuffleTexturePool(); // shuffle texture pool array again to prevent the same combination over and over
		}
		return _texturePoolNextIndex;
	}

	protected void nextOverlayTexture() {
		if(_overlayTexturePool.size() == 0)  return;
		// cycle the first to the last element & set on fullMaskTexture 
		_overlayTexturePool.add(_overlayTexturePool.remove(0));
		_fullMaskTexture.setTexture(_overlayTexturePool.get(0));
	}

	protected void updateActiveTextures() {
		// update active textures, once each if used in a group
		for( int i=0; i < _activeTextures.size(); i++ ) {
			BaseTexture texture = _activeTextures.get(i);
			for (MappingGroup group : _mappingGroups) {
				if(group.isUsingTexture(texture) == true) {					
					texture.update();
					break;
				}
			}
		}
	}
	
	protected int numMovieTextures() {
		int numMovieTextures = 0;
		for( int i=0; i < _activeTextures.size(); i++ ) {
			if( _activeTextures.get(i) instanceof TextureVideoPlayer ) numMovieTextures++;
		}
		return numMovieTextures;
	}
	
	protected void removeOldestMovieTexture() {
		for( int i=0; i < _activeTextures.size(); i++ ) {
			if( _activeTextures.get(i) instanceof TextureVideoPlayer ) {
				_activeTextures.remove(i).setActive(false);
				return;
			}
		}
	}
	
	protected void cycleANewTexture(BaseTexture specificTexture) {
		// check number of movie textures, and make sure we never have more than 2
		if(specificTexture != null) {
			_activeTextures.add( specificTexture.setActive(true) );
		} else {
			_activeTextures.add( _texturePool.get( nextTexturePoolIndex() ).setActive(true) );
		}
		while( numMovieTextures() >= MAX_ACTIVE_MOVIE_TEXTURES ) {
			removeOldestMovieTexture();
			_activeTextures.add( _texturePool.get( nextTexturePoolIndex() ).setActive(true) );
		}

		// remove oldest texture if more than max 
		if( _activeTextures.size() > MAX_ACTIVE_TEXTURES ) {
			_activeTextures.remove(0).setActive(false);
		}
		
		sendRandomTextureToGroups();
		
		// swap filters 
		selectNewActiveTextureFilters();
		// debugLogActiveTextures();
	}
	
	protected void sendRandomTextureToGroups() {
		// make sure mapping groups update their textures - this will do nothing if the group already has the random active texture
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).pushTexture( randomCurTexture().setActive(true), _activeTextures );
		}
	}
	
	public void debugLogActiveTextures() {
		P.println("%%% _activeTextures ===============");
		for(int j = 0; j < _activeTextures.size(); j++) {
			P.println(""+_activeTextures.get(j).toString());
		}
		P.println("%%% end AFTER ===============");
	}
	
	/////////////////////////////////////////////////////////////////
	// Texture-level post-processing effects
	/////////////////////////////////////////////////////////////////

	protected void selectNewActiveTextureFilters() {
		for(int i=1; i < _textureEffectsIndices.length; i++) {
			if(MathUtil.randRange(0, 10) > 8) {
				_textureEffectsIndices[i] = MathUtil.randRange(0, _numTextureEffects);
			}
		}
	}
	
	protected void filterActiveTextures() {
		for( int i=0; i < _activeTextures.size(); i++ ) {
			if(_activeTextures.get(i).isActive() == true) {
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
					ReflectFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 5) {
					WobbleFilter.instance(p).setTime(filterTime);
					WobbleFilter.instance(p).setSpeed(0.5f);
					WobbleFilter.instance(p).setStrength(0.0004f);
					WobbleFilter.instance(p).setSize( 200f);
					WobbleFilter.instance(p).applyTo(pg);
	//			} else if(_textureEffectsIndices[i] == 6) {
	//				InvertFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 7) {
					RadialRipplesFilter.instance(p).setTime(filterTime);
					RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(filterTime));
					RadialRipplesFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 8) {
					BadTVLinesFilter.instance(p).applyTo(pg);
	//			} else if(_textureEffectsIndices[i] == 9) {
	//				EdgesFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 10) {
					CubicLensDistortionFilterOscillate.instance(p).setTime(filterTime);
					CubicLensDistortionFilterOscillate.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 11) {
					SphereDistortionFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 12) {
					HalftoneFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 13) {
					PixelateFilter.instance(p).setDivider(15f, pg.width, pg.height);
					PixelateFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 14) {
					DeformBloomFilter.instance(p).setTime(filterTime);
					DeformBloomFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 15) {
					DeformTunnelFanFilter.instance(p).setTime(filterTime);
					DeformTunnelFanFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 16) {
					HueFilter.instance(p).setTime(filterTime);
					HueFilter.instance(p).applyTo(pg);
				}
	//			WarperFilter.instance(p).setTime( _timeEaseInc / 5f);
	//			WarperFilter.instance(p).applyTo(pg);
	//			ColorDistortionFilter.instance(p).setTime( _timeEaseInc / 5f);
	//			ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(radsComplete));
	//			ColorDistortionFilter.instance(p).applyTo(pg);
	//			OpenGLUtil.setTextureRepeat(_buffer);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// Global (PApplet-level) special effects
	/////////////////////////////////////////////////////////////////

	protected void postProcessFilters() {
		// brightness
		float brightMult = 2.8f;
		if(p.frameCount < 3) p.midiState.controllerChange(3, 41, P.round(127f/brightMult));	// default to 1.0, essentially, with room to get up to 2.8f
		float brightnessVal = p.midiState.midiCCPercent(3, 41) * brightMult;
		BrightnessFilter.instance(p).setBrightness(brightnessVal);
		BrightnessFilter.instance(p).applyTo(p);
		
//		SaturationFilter.instance(p).setSaturation(1.2f);
//		SaturationFilter.instance(p).applyTo(p);
		
		float contrastMult = 2.5f;
		if(p.frameCount < 3) p.midiState.controllerChange(3, 44, P.round(127f/contrastMult));	// default to 1.0, essentially, with room to get up to 2.8f
		float contrastVal = p.midiState.midiCCPercent(3, 44) * contrastMult;
		ContrastFilter.instance(p).setContrast(contrastVal);
		ContrastFilter.instance(p).applyTo(p);

//		FXAAFilter.instance(p).applyTo(p);
		
//		ColorCorrectionFilter.instance(p).setBrightness(0.1f * P.sin(p.frameCount/10f));
//		ColorCorrectionFilter.instance(p).setContrast(1f + 0.1f * P.sin(p.frameCount/10f));
//		ColorCorrectionFilter.instance(p).setGamma(1f + 0.2f * P.cos(p.frameCount/10f));
//		ColorCorrectionFilter.instance(p).setBrightness(-0.1f);
//		ColorCorrectionFilter.instance(p).setContrast(1.2f);
//		ColorCorrectionFilter.instance(p).setGamma(1.4f);
//		ColorCorrectionFilter.instance(p).applyTo(p);

		
		// color distortion auto
		int distAutoFrame = p.frameCount % 6000;
		float distFrames = 100f;
		if(distAutoFrame <= distFrames) {
			float distAmpAuto = P.sin(distAutoFrame/distFrames * P.PI);
			p.midiState.controllerChange(3, 42, P.round(127 * distAmpAuto));
			p.midiState.controllerChange(3, 43, P.round(127 * distAmpAuto));
		}
		
		// color distortion
		float colorDistortionAmp = p.midiState.midiCCPercent(3, 42) * 0.5f;
		float colorDistortionTimeMult = p.midiState.midiCCPercent(3, 43);
		if(colorDistortionAmp > 0) {
			float prevTime = ColorDistortionFilter.instance(p).getTime();
			ColorDistortionFilter.instance(p).setTime(prevTime + 1/100f * colorDistortionTimeMult);
			ColorDistortionFilter.instance(p).setAmplitude(colorDistortionAmp);
			ColorDistortionFilter.instance(p).applyTo(p);
		}
	}
	
	protected void traverseTrigger() {
		getRandomGroup().traverseStart();
		
		// now also send a random texture to a group
//		sendRandomTextureToGroups();
	}
	
	/////////////////////////////////////////////////////////////////
	// Beat detection & user override
	/////////////////////////////////////////////////////////////////
	
	protected void checkBeat() {
		if( p.audioData.isBeat() == true && isBeatDetectMode() == true ) {
			updateTiming();
		}
	}
	
	protected boolean isBeatDetectMode() {
		return ( p.millis() - USER_INPUT_BEAT_TIMEOUT > _lastInputMillis );
	}
	
	public void resetBeatDetectMode() {
		_lastInputMillis = p.millis();
//		numBeatsDetected = 1;
	}
	
	/////////////////////////////////////////////////////////////////
	// User input
	/////////////////////////////////////////////////////////////////
	
	public void handleInputTriggers() {
		
//		if( p.key == 'a' || p.key == 'A' ){
//			_isAutoPilot = !_isAutoPilot;
//			P.println("_isAutoPilot = "+_isAutoPilot);
//		}
//		if( p.key == 'S' ){
//			_isStressTesting = !_isStressTesting;
//			P.println("_isStressTesting = "+_isStressTesting);
//		}
		if ( _colorTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateColor();
		}
		if ( _modeTrigger.triggered() == true ) {
			newMode();
			traverseTrigger();
		}
		if ( _lineModeTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateLineMode();
		}
		if ( _rotationTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateRotation();
		}
		if ( _timingTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateTiming();
		}
		if ( _timingSectionTrigger.triggered() == true ) {
			updateTimingSection();
		}
		if ( _newTextureTrigger.triggered() == true ) {
			cycleANewTexture(null);
		}
		if ( _bigChangeTrigger.triggered() == true ) {
			resetBeatDetectMode();
			bigChangeTrigger();
		}
		if ( _allSameTextureTrigger.triggered() == true ) {
			resetBeatDetectMode();
			if(MathUtil.randBoolean() == true) setGroupsMappingStylesToTheSame(true);
			if(MathUtil.randBoolean() == true) setGroupsTextureToTheSameMaybe();
			if(MathUtil.randBoolean() == true) setAllSameTexture();

		}
		if ( _audioInputUpTrigger.triggered() == true ) p.audioData.setGain(p.audioData.gain() + 0.05f);
		if ( _audioInputDownTrigger.triggered() == true ) p.audioData.setGain(p.audioData.gain() - 0.05f);
		if ( _brightnessUpTrigger.triggered() == true ) p.midiState.controllerChange(3, 41, Math.round(127f * p.midiState.midiCCPercent(3, 41) + 1));
		if ( _brightnessDownTrigger.triggered() == true ) p.midiState.controllerChange(3, 41, Math.round(127f * p.midiState.midiCCPercent(3, 41) - 1));
		if ( _debugTexturesTrigger.triggered() == true ) _debugTextures = !_debugTextures;
	}
	
	protected void updateTiming() {
		// pass beat detection on to textures and lighting
		if(_dmxLights != null) _dmxLights.updateDmxLightsOnBeat();
		if(_overlayTexturePool.size() > 0) _overlayTexturePool.get(0).updateTiming();
		for( int i=0; i < _activeTextures.size(); i++ ) _activeTextures.get(i).updateTiming();
		
		// make beat detection sequencing decisions
		numBeatsDetected++;
		
		if( numBeatsDetected % BEAT_INTERVAL_MAP_STYLE_CHANGE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_MAP_STYLE_CHANGE");
			changeGroupsRandomPolygonMapStyle();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_COLOR == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_COLOR");
			updateColor();
		}
		if( numBeatsDetected % BEAT_INTERVAL_ROTATION == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_ROTATION");
			updateRotation();
		}
		if( numBeatsDetected % BEAT_INTERVAL_TRAVERSE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_TRAVERSE");
			traverseTrigger();
		}
		if( numBeatsDetected % BEAT_INTERVAL_ALL_SAME == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_ALL_SAME");
			if(MathUtil.randBoolean() == true) setGroupsMappingStylesToTheSame(true);
			if(MathUtil.randBoolean() == true) setGroupsTextureToTheSameMaybe();
			if(MathUtil.randBoolean() == true) setAllSameTexture();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_LINE_MODE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_LINE_MODE");
			updateLineMode();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_NEW_TIMING_SECTION == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_NEW_TIMING_SECTION");
			updateTimingSection();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_NEW_MODE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_NEW_MODE");
			newMode();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_NEW_TEXTURE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_NEW_TEXTURE");
			cycleANewTexture(null);
		}
		if( numBeatsDetected % BEAT_INTERVAL_BIG_CHANGE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_BIG_CHANGE");
			bigChangeTrigger();
		}
	}
	
	
	/////////////////////////////////////////////////////////////////
	// Group/polygon mode updates
	/////////////////////////////////////////////////////////////////

	protected void newMode() {
		for( int i=0; i < _mappingGroups.size(); i++ ) 
			_mappingGroups.get(i).newMode();
	}
	
	protected void updateColor() {
		// sometimes do all groups, but mostly pick a random group to change
		if( MathUtil.randRange(0, 100) > 80 ) {
			for( int i=0; i < _mappingGroups.size(); i++ )
				_mappingGroups.get(i).newColor();
		} else {
			getRandomGroup().newColor();
		}
	}
	
	protected void updateLineMode() {
		// sometimes do all groups, but mostly pick a random one to change
		if( MathUtil.randRange(0, 100) > 80 ) {
			for( int i=0; i < _mappingGroups.size(); i++ )
				_mappingGroups.get(i).newLineMode();
		} else {
			getRandomGroup().newLineMode();
		}
	}
	
	protected void updateRotation() {
		randomActiveTexture().newRotation();
		for( int i=0; i < _mappingGroups.size(); i++ )
			_mappingGroups.get(i).newRotation();
	}
	

	protected void updateTimingSection() {
		if(_overlayTexturePool.size() > 0) _overlayTexturePool.get(0).updateTimingSection();

		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTimingSection();
		}
		
		newLineModeForRandomGroup();
		selectNewActiveTextureFilters();
	}
	
	protected void bigChangeTrigger() {
		// bail if the face recording texture is active
		if(_faceRecorder != null && _faceRecordingTexture != null) {
			if(_faceRecordingTexture.isActive() == true) return;
		}
		
		// swap in a new overlay texture and normal pool texture
		cycleANewTexture(null);
		nextOverlayTexture();
		
		// randomize all textures to polygons & global effects
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).randomTextureToRandomPolygon();
		}
		
		// do a couple of normal triggers
		newLineModesForAllGroups();
		updateTimingSection();
		
		// reset rotations
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).resetRotation();
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// cool rules (should just be across groups or forward timing/triggers to all groups)
	/////////////////////////////////////////////////////////////////

	protected void setGroupsTextureToTheSameMaybe() {
		// maybe also set a group to all to be the same texture
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			if( MathUtil.randRange(0, 100) < 25 ) {
				_mappingGroups.get(i).setAllPolygonsToSameRandomTexture();
			}
		}
	}	
	
	protected void changeGroupsRandomPolygonMapStyle() {
		// every beat, change a polygon mapping style or texture
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			if( MathUtil.randBoolean() == true ) {
				_mappingGroups.get(i).randomTextureToRandomPolygon();
			} else {
				_mappingGroups.get(i).randomPolygonRandomMappingStyle();
			}
		}
	}
	
	protected void setGroupsMappingStylesToTheSame(boolean allowsFullEQ) {
		// every once in a while, set all polygons' styles to be the same per group
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			if( MathUtil.randRange(0, 100) < 90 || allowsFullEQ == false ) {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( MathUtil.randRange(0, 2) );
			} else {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_EQ );	// less likely to go to EQ fill
			}
			_mappingGroups.get(i).newColor();
		}
	}

	
	protected void newLineModeForRandomGroup() {
		getRandomGroup().newLineMode();
	}
	
	protected void newLineModesForAllGroups() {
		// set new line mode
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).newLineMode();
		}
		// once in a while, reset all mesh lines to the same random mode
		if( MathUtil.randRange(0, 100) < 10 ) {
			int newLineMode = MathUtil.randRange(0, MODE.values().length - 1);
			for(int i=0; i < _mappingGroups.size(); i++ ) {
				_mappingGroups.get(i).resetLineModeToIndex( newLineMode );
			}
		}
	}
		


	protected void setAllSameTexture() {
		while(_activeTextures.size() > 1) _activeTextures.remove(0);
		boolean mode = MathUtil.randBoolean();
		BaseTexture newTexture = _texturePool.get(nextTexturePoolIndex());
		cycleANewTexture(newTexture);
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).clearAllTextures();
			_mappingGroups.get(i).pushTexture( newTexture, _activeTextures );
			_mappingGroups.get(i).setAllPolygonsToTexture(0);
			if( mode == true ) {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_CONTAIN_RANDOM_TEX_AREA );
			} else {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_MASK );
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// Face recorded insanity
	/////////////////////////////////////////////////////////////////
	
	public void updateFaceRecorder() {
		_faceRecorder.update(_faceRecordingTexture.isActive(), _facesPlaybackTexture.isActive());
	}
	
	public void startFaceRecording() {
		setAllFaceRecorder();
		_faceRecordingTexture.setActive(true);
	}
	
	public void stopFaceRecording() {
		_faceRecordingTexture.setActive(false);
		removeFaceRecorderTexture();
		cycleANewTexture(_facesPlaybackTexture);
		bigChangeTrigger();
		updateTiming();
		updateTimingSection();
	}
	
	protected void setAllFaceRecorder() {
		// this is fucked because we're just adding to mapping groups without adding to the _curTexturePool. removal below is funky
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).clearAllTextures();
			_mappingGroups.get(i).pushTextureFront(_faceRecordingTexture);
			_mappingGroups.get(i).setAllPolygonsToTexture(0);
			_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_MASK );
		}
	}	

	protected void removeFaceRecorderTexture() {
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).clearAllTextures();
		}

//		for( int i=0; i < _curTexturePool.size(); i++ ) {
//			P.println("remove attempt!@! ", _curTexturePool.get(i));
//			if( _curTexturePool.get(i) instanceof TextureKinectFaceRecording ) {
//				_curTexturePool.remove(i);
//				P.println("removed TextureKinectFaceRecording!!!");
//				return;
//			}
//		}
	}
	
}

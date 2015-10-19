package com.haxademic.app.haxmapper.mappers;

import java.util.ArrayList;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxmapper.overlays.FullMaskTextureOverlay;
import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQFloatParticles;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

import oscP5.OscMessage;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class McaMapper
extends HaxMapper{
	
//	protected AudioPixelInterface _audioPixel;
//	protected int[] _audioPixelColors;
		
	/*
	 * TODO:
	 * - When switching to all one texture, clear out other textures in the current pool?
	 * - Add another floating audio-reactive particle overlay texture - swithc the overloay textures out on an interval
	 * - Mapped UV coordinates should never bee out of texture's frame - this results in lines - more prominent in triangle polygons
	 */
	
	protected float BEAT_DIVISOR = 1f; // 10 to test
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_MAP_STYLE_CHANGE = (int) Math.ceil(4f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(16f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ALL_SAME = (int) Math.ceil(140f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_LINE_MODE = (int) Math.ceil(32f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(80f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(250f / BEAT_DIVISOR);
	
	protected RandomLightTiming _dmxLights;
	
	protected boolean _timingDebug = false;

	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", McaMapper.class.getName() });
	}

	protected void overridePropsFile() {
		super.overridePropsFile();
		p.appConfig.setProperty( "mapping_file", FileUtil.getFile("text/mapping/mapping-2015-09-18-17-55-50.txt") );
		p.appConfig.setProperty( "rendering", "false" );
		p.appConfig.setProperty( "fullscreen", "true" );
		p.appConfig.setProperty( "fills_screen", "true" );
		p.appConfig.setProperty( "osc_active", "false" );
		p.appConfig.setProperty( "audio_debug", "true" );
		p.appConfig.setProperty( "width", "1200" );
		p.appConfig.setProperty( "height", "1000" );
		p.appConfig.setProperty( "dmx_lights_count", "4" );
		p.appConfig.setProperty( "hide_cursor", "false" );
		p.appConfig.setProperty( "force_foreground", "true" );
	}

	public void oscEvent(OscMessage theOscMessage) {  
		super.oscEvent(theOscMessage);
	}
	
	public void setup() {
		super.setup();
		if(p.appConfig.getInt("dmx_lights_count", 0) > 0) _dmxLights = new RandomLightTiming(p.appConfig.getInt("dmx_lights_count", 0));
		
//		_audioPixel = new AudioPixelInterface();
//		_audioPixelColors = new int[ _mappingGroups.size() ];
	}
	
	protected void buildMappingGroups() {
		// give each group a texture to start with
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).pushTexture( _texturePool.get(0) );
			_mappingGroups.get(i).pushTexture( _texturePool.get(1) );
		}
		
		// set initial mapping properties - make all fully contain their textures
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			ArrayList<IMappedPolygon> polygons = _mappingGroups.get(i).polygons();
			for(int j=0; j < polygons.size(); j++ ) {
				IMappedPolygon polygon = polygons.get(j);
				polygon.setTextureStyle( IMappedPolygon.MAP_STYLE_MASK );
			}
		}
	}

	protected void addTexturesToPool() {

		int videoW = 592;
		int videoH = 334;
		
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));	
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));
		
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Blue Defocused Particles HD1080.flv.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Close up eye and pupil dilation.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/EYE IRIS.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Fun with Bokeh in Renderman.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/glitter effect.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/glitter1-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/glitter2.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/glitter3-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/glitter4.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/glitter5.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Macro Video of Human Eye & Iris.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Steam on Glass #2.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Water drop running down glass.mp4" ));
		
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/ICE_LIPS_2.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/ink-in-water.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Macro shot of lips on Vimeo_2.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/Smoke_02.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/minter/STEEL_WOLL_2.mov" ));
		
		int shaderW = 300;
		int shaderH = 300;
		
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-clouds.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "gradient-line.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "light-leak.glsl" ));
//		_texturePool.add( new TextureEQFloatParticles( 800, 600 ) );

//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "star-field.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "stars-fractal-field.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "water-smoke.glsl" ));
		
		// shuffle one time!
		shuffleTexturePool();
		
		// store just movies to restrain the number of concurrent movies
		for( int i=0; i < _texturePool.size(); i++ ) {
			if( _texturePool.get(i) instanceof TextureVideoPlayer ) {
				_movieTexturePool.add( _texturePool.get(i) );
			}
		}
		
		// add 1 inital texture to current array
		_curTexturePool.add( _texturePool.get(nextTexturePoolIndex() ) );

		// add full screen overlay texture
		_fullMaskTexture = new FullMaskTextureOverlay(_overlayPG, _boundingBox);
		_overlayTexturePool.add(new TextureEQFloatParticles( 600, 420 ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( 420, 360, "light-leak.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( 420, 360, "star-field.glsl" ));
	}

	
	public void drawApp() {
		// update & prepare overlay graphics
		_overlayTexturePool.get(0).update();
		_fullMaskTexture.setTexture(_overlayTexturePool.get(0));
		
		// draw the main mapping app
		super.drawApp();
		
		// deal with physical lighting
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).getAudioPixelColor();
			// _audioPixelColors[i] = _mappingGroups.get(i).colorEaseInt();
		}
		
		if(_dmxLights != null) {
			_dmxLights.update();
			if(_debugTextures == true) _dmxLights.drawDebug(p.g);
		}
		
	}
	
	protected void checkBeat() {
		if( audioIn.isBeat() == true && isBeatDetectMode() == true ) {
			updateTiming();
		}
	}
	
	protected boolean isBeatDetectMode() {
		return ( p.millis() - 10000 > _lastInputMillis );
	}
	
	protected void updateColor() {
		// sometimes do all groups, but mostly pick a random one to change
		if( MathUtil.randRange(0, 100) > 80 ) {
			super.updateColor();
		} else {
			int randGroup = MathUtil.randRange( 0, _mappingGroups.size() - 1 );
			_mappingGroups.get(randGroup).newColor();
			_mappingGroups.get(randGroup).pulseColor();
		}
	}
	
	protected void updateLineMode() {
		// sometimes do all groups, but mostly pick a random one to change
		if( MathUtil.randRange(0, 100) > 80 ) {
			super.updateLineMode();
		} else {
			int randGroup = MathUtil.randRange( 0, _mappingGroups.size() - 1 );
			_mappingGroups.get(randGroup).newLineMode();
		}
	}
	
	protected void updateTiming() {
		super.updateTiming();
		
//		if( isBeatDetectMode() == true ) 
		numBeatsDetected++;
		
		if(_dmxLights != null) _dmxLights.updateDmxLightsOnBeat();
		if(_overlayTexturePool.size() > 0) _overlayTexturePool.get(0).updateTiming();
		
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
//		updateColor();
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).newAudioPixelColor();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_ALL_SAME == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_ALL_SAME");
			setGroupsMappingStylesToTheSame();
			setGroupsTextureToTheSameMaybe();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_LINE_MODE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_LINE_MODE");
			updateLineMode();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_NEW_TIMING == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_NEW_TIMING");
			updateTimingSection();
		}
		
		// every 40 beats, do something bigger
		if( numBeatsDetected % BEAT_INTERVAL_BIG_CHANGE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_BIG_CHANGE");
			bigChangeTrigger();
		}
	}
	
	protected void updateTimingSection() {
		super.updateTimingSection();
		
		newLineModeForRandomGroup();
		cycleANewTexture(null);
		if(_overlayTexturePool.size() > 0) _overlayTexturePool.get(0).updateTimingSection();
	}
	
	protected void bigChangeTrigger() {
		if(_faceRecordingTexture != null) {
			if(_faceRecordingTexture.isActive() == true) return;
		}
		super.bigChangeTrigger();
		
		cycleANewTexture(null);
		newLineModesForAllGroups();
		nextOverlayTexture();

		// set longer timing updates
		updateTimingSection();
		updateColor();
		
		// reset rotations
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).resetRotation();
		}
	}
	
	protected void nextOverlayTexture() {
		// cycle the first to the last element
		_overlayTexturePool.add(_overlayTexturePool.remove(0));
	}
	
	// cool rules =========================================================
	
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
			if( MathUtil.randBoolean(p) == true ) {
				_mappingGroups.get(i).randomTextureToRandomPolygon();
			} else {
				_mappingGroups.get(i).randomPolygonRandomMappingStyle();
			}
		}
	}
	
	protected void newLineModeForRandomGroup() {
		int randGroup = MathUtil.randRange( 0, _mappingGroups.size() - 1 );
		_mappingGroups.get(randGroup).newLineMode();
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
		
	
	// MCA ONLY =============================
	
	// never go to EQ mapping mode
	protected void setGroupsMappingStylesToTheSame() {
		// every once in a while, set all polygons' styles to be the same per group
		for(int i=0; i < _mappingGroups.size(); i++ ) {
//			if( MathUtil.randRange(0, 100) < 90 ) {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( MathUtil.randRange(0, 2) );
//			} else {
//				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_EQ );	// less likely to go to EQ fill
//			}
			_mappingGroups.get(i).newColor();
		}
	}

	// override to allow all-movie textures
	@Override
	protected void cycleANewTexture(BaseTexture specificTexture) {
		// rebuild the array of currently-available textures
		// check number of movie textures, and make sure we never have more than 2
		if(specificTexture != null) {
			_curTexturePool.add(specificTexture);
		} else {
			_curTexturePool.add( _texturePool.get( nextTexturePoolIndex() ) );
		}
//		while( numMovieTextures() >= MAX_ACTIVE_MOVIE_TEXTURES ) {
//			removeOldestMovieTexture();
//			_curTexturePool.add( _texturePool.get( nextTexturePoolIndex() ) );
//		}
		// remove oldest texture if more than max 
		if( _curTexturePool.size() >= MAX_ACTIVE_TEXTURES ) {
			// P.println(_curTexturePool.size());
			_curTexturePool.remove(0);
		}
		
		refreshGroupsTextures();
	}

}



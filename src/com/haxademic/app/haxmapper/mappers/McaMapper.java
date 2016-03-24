package com.haxademic.app.haxmapper.mappers;

import java.util.ArrayList;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxmapper.overlays.FullMaskTextureOverlay;
import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureAudioTube;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureEQFloatParticles;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureImageTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureLinesEQ;
import com.haxademic.app.haxmapper.textures.TextureOuterSphere;
import com.haxademic.app.haxmapper.textures.TextureRotatingRings;
import com.haxademic.app.haxmapper.textures.TextureRotatorShape;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
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
	 * - Refactor & organize
	 * - triangle rotation is causing warped polygons
	 * - use new triangle random coordinates for mapped quads
	 * - Add a vertex shader to manipulate the z of all mesh vertices? since we're not using pshapes, maybe just use noise() to deform the z, then apply lighting
	 * - Add more ambient overlay shaders
	 * - Make sure nextTexturePoolIndex() is working properly 
	 * - Add new SDF shaders in B&W
	 * - Fix image cycling texture - it does weird flashy things
	 * - Test with multiple groups
	 * - Make sure there are no texture effects that are slowing things down
	 * - Find the video Wally wanted to import: https://www.youtube.com/watch?v=gUilOCTqPC4
	 * - is newMode() getting called on textures?
	 * - is rotate() getting called on textures?
	 * 
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
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(40f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TEXTURE = (int) Math.ceil(80f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(250f / BEAT_DIVISOR);
	
	protected RandomLightTiming _dmxLights;
	
	protected boolean _timingDebug = false;

	public static void main(String args[]) {
		_isFullScreen = false;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", McaMapper.class.getName() });
	}

	protected void overridePropsFile() {
		super.overridePropsFile();
		p.appConfig.setProperty( "mapping_file", FileUtil.getFile("text/mapping/mapping-2016-03-23-22-53-52.txt") );
		p.appConfig.setProperty( "rendering", "false" );
		p.appConfig.setProperty( "fullscreen", "false" );
		p.appConfig.setProperty( "fills_screen", "false" );
		p.appConfig.setProperty( "osc_active", "false" );
		p.appConfig.setProperty( "audio_debug", "true" );
		p.appConfig.setProperty( "width", "1800" );
		p.appConfig.setProperty( "height", "1024" );
		p.appConfig.setProperty( "dmx_lights_count", "0" );
		p.appConfig.setProperty( "hide_cursor", "false" );
		p.appConfig.setProperty( "force_foreground", "false" );
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
		

		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));	

		
		int shaderW = 600;
		int shaderH = 500;
		int shaderWsm = shaderW/2;
		int shaderHsm = shaderH/2;
		
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "basic-checker.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "basic-diagonal-stripes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bubbles-iq.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-circles.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-clouds.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-expand-loop.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-eye-jacker-01.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-eye-jacker-02.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-kaleido.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-motion-illusion.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-simple-sin.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "circle-parts-rotate.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "cog-tunnel.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "dots-orbit.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "fade-dots.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "firey-spiral.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "flame-wisps.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "flexi-spiral.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "glowwave.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "gradient-line.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "hex-alphanumerics.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "inversion-iq.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "iq-iterations-shiny.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "light-leak.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "lines-scroll-diag.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "matrix-rain.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "radial-burst.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "radial-waves.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "shiny-circle-wave.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "sin-grey.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "sin-waves.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "space-swirl.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "spinning-iq.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "square-fade.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "square-twist.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "star-field.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "stars-fractal-field.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "stars-nice.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "stars-screensaver.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "stars-scroll.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "supershape-2d.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "swirl.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "triangle-perlin.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "warped-tunnel.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "water-smoke.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "wavy-3d-tubes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "wavy-checker-planes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "wobble-sin.glsl" ));
		// bad performance:
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "dot-grid-dof.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "docking-tunnel.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "hughsk-metaballs.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "hughsk-tunnel.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "morphing-bokeh-shape.glsl" ));


		
		_texturePool.add( new TextureTwistingSquares( shaderWsm, shaderHsm ));
		_texturePool.add( new TextureEQConcentricCircles( shaderW, shaderH ) );
		_texturePool.add( new TextureScrollingColumns( shaderW, shaderH ));
		_texturePool.add( new TextureImageTimeStepper( shaderW, shaderH ));
		_texturePool.add( new TextureEQColumns( shaderW, shaderH ));
		_texturePool.add( new TextureEQGrid( shaderW, shaderH ));
		_texturePool.add( new TextureLinesEQ( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformSimple( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformCircle( shaderW, shaderH ));
//		_texturePool.add( new TextureSphereRotate( shaderW, shaderH ));
		_texturePool.add( new TextureOuterSphere( shaderW, shaderH ) );
		_texturePool.add( new TextureRotatorShape( shaderW, shaderH ) );
		_texturePool.add( new TextureRotatingRings( shaderW, shaderH ) );
		_texturePool.add( new TextureAudioTube( shaderW, shaderH ) );
//		_texturePool.add( new TextureColorAudioSlide( shaderW, shaderH ));

		
		
		
		
		// shuffle one time!
		shuffleTexturePool();
		
		// store just movies to restrain the number of concurrent movies
		for( int i=0; i < _texturePool.size(); i++ ) {
			if( _texturePool.get(i) instanceof TextureVideoPlayer ) {
				_movieTexturePool.add( _texturePool.get(i) );
			}
		}
		
		// add 1 inital texture to current array
		_curTexturePool.add( _texturePool.get(nextTexturePoolIndex() ));

		// add full screen overlay texture
		_fullMaskTexture = new FullMaskTextureOverlay(_overlayPG, _boundingBox);
		_overlayTexturePool.add(new TextureEQFloatParticles( shaderW, shaderH ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderW, shaderH, "light-leak.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderW, shaderH, "star-field.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderW, shaderH, "square-fade.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderW, shaderH, "bw-clouds.glsl" ));

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
		
		if( numBeatsDetected % BEAT_INTERVAL_NEW_TEXTURE == 0 ) {
			if(_timingDebug == true) P.println("BEAT_INTERVAL_NEW_TEXTURE");
			cycleANewTexture(null);
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
		selectNewActiveTextureFilters();
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

//	// override to allow all-movie textures
//	@Override
//	protected void cycleANewTexture(BaseTexture specificTexture) {
//		// rebuild the array of currently-available textures
//		// check number of movie textures, and make sure we never have more than 2
//		if(specificTexture != null) {
//			_curTexturePool.add(specificTexture);
//		} else {
//			_curTexturePool.add( _texturePool.get( nextTexturePoolIndex() ) );
//		}
//		// remove oldest texture if more than max 
//		if( _curTexturePool.size() >= MAX_ACTIVE_TEXTURES ) {
//			// P.println(_curTexturePool.size());
//			_curTexturePool.remove(0);
//		}
//		
//		refreshGroupsTextures();
//	}

}



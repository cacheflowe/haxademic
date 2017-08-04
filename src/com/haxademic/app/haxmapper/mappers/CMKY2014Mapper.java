package com.haxademic.app.haxmapper.mappers;

import java.util.ArrayList;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.overlays.MeshLines.MODE;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureImageTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PApplet;

public class CMKY2014Mapper
extends HaxMapper{
	
//	protected AudioPixelInterface _audioPixel;
//	protected int[] _audioPixelColors;
		
	protected float BEAT_DIVISOR = 1; // 10 to test
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(8f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ALL_SAME = (int) Math.ceil(150f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(40f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(400f / BEAT_DIVISOR);
	

	public static void main(String args[]) {
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", CMKY2014Mapper.class.getName() });
	}

	protected void overridePropsFile() {
		super.overridePropsFile();
		p.appConfig.setProperty( "mapping_file", FileUtil.getHaxademicDataPath() + "text/mapping/mapping-2015-08-31-20-09-27.txt" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, "true" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "true" );
		p.appConfig.setProperty( "osc_active", "false" );
		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1200" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1000" );
	}

//	public void oscEvent(OscMessage theOscMessage) {  
//		super.oscEvent(theOscMessage);
//	}

	protected void buildMappingGroups() {
		// give each group a texture to start with
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).pushTexture( _texturePool.get(0), _activeTextures );
			_mappingGroups.get(i).pushTexture( _texturePool.get(1), _activeTextures );
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

		int videoW = 420;
		int videoH = 236;
		

//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));	
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));
		
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_01.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_02.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_03.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_04.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_05.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_06.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/horrorhouse/MotelLondon_08.mov" ));

				
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-00-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-01-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-02-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-03-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-04-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-08-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-09-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-10-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-11-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-12-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/LL-13-desktop.m4v" ));
//		
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/crystal-growth-2.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/crystal-growth-3-desktop.m4v" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/crystal-growth-4.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( videoW, 236, "video/lunar-lodge/crystal-growth-desktop.m4v" ));

		int shaderW = 300;
		int shaderH = 300;
		
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
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "docking-tunnel.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "dot-grid-dof.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "dots-orbit.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "fade-dots.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "firey-spiral.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "flame-wisps.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "flexi-spiral.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "glowwave.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "gradient-line.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "hex-alphanumerics.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "hughsk-metaballs.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "hughsk-tunnel.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "inversion-iq.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "iq-iterations-shiny.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "light-leak.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "lines-scroll-diag.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "matrix-rain.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "morphing-bokeh-shape.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "radial-burst.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "radial-waves.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "shiny-circle-wave.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "sin-grey.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "sin-waves.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "space-swirl.glsl" ));
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
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "wavy-3d-tubes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "wavy-checker-planes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "wobble-sin.glsl" ));


		_texturePool.add( new TextureScrollingColumns( 100, 100 ));
		_texturePool.add( new TextureTwistingSquares( shaderW, shaderH ));
		_texturePool.add( new TextureImageTimeStepper( 600, 600 ));
		_texturePool.add( new TextureEQColumns( shaderW, shaderH ));
		_texturePool.add( new TextureEQColumns( shaderW, shaderH ));
		_texturePool.add( new TextureEQGrid( shaderW, shaderH ));
		_texturePool.add( new TextureEQGrid( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformSimple( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformSimple( shaderW, shaderH ));
//		_texturePool.add( new TextureColorAudioFade( 200, 200 ));
//		_texturePool.add( new TextureColorAudioFade( 200, 200 ));
		_texturePool.add( new TextureColorAudioSlide( 200, 200 ));
//		_texturePool.add( new TextureColorAudioSlide( 200, 200 ));
		_texturePool.add( new TextureSphereRotate( shaderW, shaderH ));
		_texturePool.add( new TextureEQConcentricCircles( shaderW, shaderH ) );
//		_texturePool.add( new TextureWebCam() );		

		// shuffle one time!
		shuffleTexturePool();
		
		// store just movies to restrain the number of concurrent movies
		for( int i=0; i < _texturePool.size(); i++ ) {
			if( _texturePool.get(i) instanceof TextureVideoPlayer ) {
				_movieTexturePool.add( _texturePool.get(i) );
			}
		}
		
		// add 1 inital texture to current array
		_activeTextures.add( _texturePool.get(nextTexturePoolIndex() ) );

	}
	
	public void setup() {
		super.setup();
//		_audioPixel = new AudioPixelInterface();
//		_audioPixelColors = new int[ _mappingGroups.size() ];
	}
	
	public void drawApp() {
		super.drawApp();
		
		for(int i=0; i < _mappingGroups.size(); i++ ) {
//			_mappingGroups.get(i).getAudioPixelColor();
//			_audioPixelColors[i] = _mappingGroups.get(i).colorEaseInt();
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
//			_mappingGroups.get(randGroup).pulseColor();
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
		
		if( isBeatDetectMode() == true ) numBeatsDetected++;
		
		changeGroupsRandomPolygonMapStyle();
		
		// make sure textures are timed to the beat
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTiming();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_COLOR == 0 ) {
//			P.println("BEAT_INTERVAL_COLOR");
			updateColor();
		}
		if( numBeatsDetected % BEAT_INTERVAL_ROTATION == 0 ) {
//			P.println("BEAT_INTERVAL_ROTATION");
			updateRotation();
		}
		if( numBeatsDetected % BEAT_INTERVAL_TRAVERSE == 0 ) {
//			P.println("BEAT_INTERVAL_TRAVERSE");
			traverseTrigger();
		}
//		updateColor();
//		for(int i=0; i < _mappingGroups.size(); i++ ) {
////			_mappingGroups.get(i).newAudioPixelColor();
//		}
		
		if( numBeatsDetected % BEAT_INTERVAL_ALL_SAME == 0 ) {
//			P.println("BEAT_INTERVAL_ALL_SAME");
			setGroupsMappingStylesToTheSame();
			setGroupsTextureToTheSameMaybe();
			updateLineMode();
		}
		
		if( numBeatsDetected % BEAT_INTERVAL_NEW_TIMING == 0 ) {
//			P.println("BEAT_INTERVAL_NEW_TIMING");
			updateTimingSection();
		}
		
		// every 40 beats, do something bigger
		if( numBeatsDetected % BEAT_INTERVAL_BIG_CHANGE == 0 ) {
//			P.println("BEAT_INTERVAL_BIG_CHANGE");
			bigChangeTrigger();
		}
	}
	
	protected void updateTimingSection() {
		super.updateTimingSection();
		
		newLineModeForRandomGroup();
		// cycleANewTexture();
	}
	
	protected void bigChangeTrigger() {
		if(_faceRecordingTexture != null) {
			if(_faceRecordingTexture.isActive() == true) return;
		}
		super.bigChangeTrigger();
		
		cycleANewTexture(null);
		newLineModesForAllGroups();

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
	
	protected void setGroupsMappingStylesToTheSame() {
		// every once in a while, set all polygons' styles to be the same per group
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			if( MathUtil.randRange(0, 100) < 90 ) {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( MathUtil.randRange(0, 2) );
			} else {
				_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_EQ );	// less likely to go to EQ fill
			}
			_mappingGroups.get(i).newColor();
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
		
}



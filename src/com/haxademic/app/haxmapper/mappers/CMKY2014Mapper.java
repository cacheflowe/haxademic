package com.haxademic.app.haxmapper.mappers;

import java.util.ArrayList;

import oscP5.OscMessage;
import processing.core.PApplet;

import com.haxademic.app.haxmapper.AudioPixelInterface;
import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.overlays.MeshLines;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.TextureColorAudioFade;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureImageTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class CMKY2014Mapper
extends HaxMapper{
	
	protected int MAX_ACTIVE_TEXTURES = 8;
	protected int MAX_ACTIVE_MOVIE_TEXTURES = 2;
	
	protected AudioPixelInterface _audioPixel;
	protected int[] _audioPixelColors;

	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.haxmapper.mappers.CMKY2014Mapper" });
	}

	protected void overridePropsFile() {
		super.overridePropsFile();
		_appConfig.setProperty( "mapping_file", FileUtil.getHaxademicDataPath() + "text/mapping/mapping-2014-04-09-23-16-58.txt" );
	}

	public void oscEvent(OscMessage theOscMessage) {  
		super.oscEvent(theOscMessage);
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

//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/smoke-loop.mov" ));
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/tree-loop.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-in-water.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-grow-shrink.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/fire.mp4" ));
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/bubbles.mp4" ));	
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/clouds-timelapse.mov" ));
//		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/water.mp4" ));
		
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/airlift_01-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Aspen Sunset Timelapse-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Blood Tide-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Ferrofluid-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/GUNDAM_01-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Intermolecular_02-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Macro Timelapse_02-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Macro Timelapse_03-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Macro Timelapse_04-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Ride of the Valkyries_01_720p-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Sunset in Colorado Springs-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/Sunset Timelapse in Colorado-desktop.m4v" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/deadbeat/The Aurora_01-desktop.m4v" ));
		
		_texturePool.add( new TextureScrollingColumns( 100, 100 ));
		_texturePool.add( new TextureTwistingSquares( 300, 300 ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "wavy-checker-planes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "bw-eye-jacker-01.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "dots-orbit.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "glowwave.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "bw-simple-sin.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "supershape-2d.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "star-field.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "sin-grey.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 600, 600, "swirl.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "bw-motion-illusion.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "sin-waves.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "lines-scroll-diag.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "iq-iterations-shiny.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( 400, 400, "bw-kaleido.glsl" ));
		_texturePool.add( new TextureImageTimeStepper( 600, 600 ));
		_texturePool.add( new TextureEQColumns( 200, 100 ));
		_texturePool.add( new TextureEQColumns( 200, 100 ));
		_texturePool.add( new TextureEQGrid( 320, 160 ));
		_texturePool.add( new TextureEQGrid( 320, 160 ));
		_texturePool.add( new TextureWaveformSimple( 500, 500 ));
		_texturePool.add( new TextureColorAudioFade( 200, 200 ));
		_texturePool.add( new TextureColorAudioFade( 200, 200 ));
		_texturePool.add( new TextureColorAudioSlide( 200, 200 ));
		_texturePool.add( new TextureColorAudioSlide( 200, 200 ));
		_texturePool.add( new TextureSphereRotate( 500, 500 ));
//		_texturePool.add( new TextureWebCam() );
		
		// store just movies to restrain the number of concurrent movies
		for( int i=0; i < _texturePool.size(); i++ ) {
			if( _texturePool.get(i) instanceof TextureVideoPlayer ) {
				_movieTexturePool.add( _texturePool.get(i) );
			}
		}
		
		// add 1 inital texture to current array
		_curTexturePool.add( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 ) ) );

	}

	protected int numMovieTextures() {
		int numMovieTextures = 0;
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			if( _curTexturePool.get(i) instanceof TextureVideoPlayer ) numMovieTextures++;
		}
		return numMovieTextures;
	}
	
	public void setup() {
		super.setup();
		_audioPixel = new AudioPixelInterface();
		_audioPixelColors = new int[ _mappingGroups.size() ];
	}
	
	public void drawApp() {
		super.drawApp();
		
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_audioPixelColors[i] = _mappingGroups.get(i).colorEaseInt();
			// debug draw!
			//			int size = 100;
			//			p.fill( _audioPixelColors[i] );
			//			p.rect(size*i, p.height-size, size, size);
		}
		_audioPixel.sendColorData( _audioPixelColors );
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
		
		if( isBeatDetectMode() == true ) numBeatsDetected++;
		
		changeGroupsRandomPolygonMapStyle();
		
		// make sure textures are timed to the beat
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTiming();
		}
		
		if( numBeatsDetected % 1 == 0 ) {
			updateColor();
		}
		updateColor();
		
		if( numBeatsDetected % 100 == 0 ) {
			setGroupsMappingStylesToTheSame();
			setGroupsTextureToTheSameMaybe();
		}
		
		if( numBeatsDetected % 200 == 0 ) {
			updateTimingSection();
		}
		
		// every 40 beats, do something bigger
		if( numBeatsDetected % 400 == 0 ) {
			bigChangeTrigger();
		}
	}
	
	protected void updateTimingSection() {
		super.updateTimingSection();
		
		newLineModeForRandomGroup();
		cycleANewTexture();
	}
	
	protected void bigChangeTrigger() {
		
		cycleANewTexture();
		newTexturesForAllGroups();
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
			int newLineMode = MathUtil.randRange(0, MeshLines.NUM_MODES - 1);
			for(int i=0; i < _mappingGroups.size(); i++ ) {
				_mappingGroups.get(i).resetLineModeToIndex( newLineMode );
			}
		}
	}
	
	protected void newTexturesForAllGroups() {
		// give each group a new texture
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).shiftTexture();
			_mappingGroups.get(i).pushTexture( _curTexturePool.get( MathUtil.randRange(0, _curTexturePool.size()-1 )) );
			_mappingGroups.get(i).setAllPolygonsToSameRandomTexture();				
		}
	}

	protected void cycleANewTexture() {
		// rebuild the array of currently-available textures
		// check number of movie textures, and make sure we never have more than 2
		_curTexturePool.add( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 ) ) );
		while( numMovieTextures() > MAX_ACTIVE_MOVIE_TEXTURES ) {
			_curTexturePool.remove( _curTexturePool.size() - 1 );
			_curTexturePool.add( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 ) ) );
		}
		// remove oldest texture if more than max 
		if( _curTexturePool.size() >= MAX_ACTIVE_TEXTURES ) {
			_curTexturePool.remove(0);
		}
		
		// make sure polygons update their textures
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).shiftTexture();
			_mappingGroups.get(i).pushTexture( _curTexturePool.get( MathUtil.randRange(0, _curTexturePool.size()-1 )) );
			_mappingGroups.get(i).reloadTextureAtIndex();				
		}
	}
}



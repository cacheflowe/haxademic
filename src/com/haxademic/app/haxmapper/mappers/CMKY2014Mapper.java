package com.haxademic.app.haxmapper.mappers;

import java.util.ArrayList;

import oscP5.OscMessage;
import processing.core.PApplet;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.polygons.IMappedPolygon;
import com.haxademic.app.haxmapper.textures.TextureColorAudioFade;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureImageTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class CMKY2014Mapper
extends HaxMapper{
	
	protected int MAX_ACTIVE_TEXTURES = 8;
	protected int MAX_ACTIVE_MOVIE_TEXTURES = 2;
	
	protected int numBeatsDetected = 0;

	public static void main(String args[]) {
		_isFullScreen = true;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.haxmapper.mappers.CMKY2014Mapper" });
	}

	protected void overridePropsFile() {
		super.overridePropsFile();
		_appConfig.setProperty( "mapping_file", FileUtil.getHaxademicDataPath() + "text/mapping/mapping-2014-04-09-01-13-48.txt" );
	}

	public void oscEvent(OscMessage theOscMessage) {  
		super.oscEvent(theOscMessage);
	}

	protected void buildPolygonGroups() {
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
//		bigChangeTrigger();
//		bigChangeTrigger();
	}

	protected void addTexturesToPool() {

		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/smoke-loop.mov" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/tree-loop.mp4" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-in-water.mp4" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/ink-grow-shrink.mp4" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/fire.mp4" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/bubbles.mp4" ));	
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/clouds-timelapse.mov" ));
		_texturePool.add( new TextureVideoPlayer( 640, 360, "video/loops/water.mp4" ));
		
		_texturePool.add( new TextureScrollingColumns( 100, 100 ));
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
		_texturePool.add( new TextureEQGrid( 320, 160 ));
		_texturePool.add( new TextureWaveformSimple( 500, 500 ));
		_texturePool.add( new TextureColorAudioFade( 200, 200 ));
		_texturePool.add( new TextureColorAudioFade( 200, 200 ));
		_texturePool.add( new TextureColorAudioSlide( 200, 200 ));
		_texturePool.add( new TextureColorAudioSlide( 200, 200 ));
		_texturePool.add( new TextureSphereRotate( 500, 500 ));
//		_texturePool.add( new TextureWebCam() );
		
		// store just movies
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
	
	protected void bigChangeTrigger() {
		
		// rebuild the array of currently-available textures
		// check number of movie textures, and make sure we never have more than 2
		_curTexturePool.add( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 ) ) );
		while( numMovieTextures() > MAX_ACTIVE_MOVIE_TEXTURES ) {
			_curTexturePool.remove( _curTexturePool.size() - 1 );
			_curTexturePool.add( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 ) ) );
		}
		// remove last texture if more than max 
		if( _curTexturePool.size() >= MAX_ACTIVE_TEXTURES ) {
			_curTexturePool.remove(0);
		}
		
		// give each group a new texture
		for( int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).shiftTexture();
			_mappingGroups.get(i).pushTexture( _curTexturePool.get( MathUtil.randRange(0, _curTexturePool.size()-1 )) );
			_mappingGroups.get(i).setAllPolygonsToNewTexture();				
		}
				
		// set new line mode
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).newLineMode();
		}

		// set longer timing updates
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTimingSection();
		}
		
		// reset rotations
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).resetRotation();
		}
		

	}

	public void drawApp() {
		super.drawApp();
	}

	protected void updateTiming() {
		numBeatsDetected++;
		
		// every beat, change a polygon or 2
		for(int i=0; i < _mappingGroups.size(); i++ ) {
			_mappingGroups.get(i).randomTextureToRandomPolygon();
			_mappingGroups.get(i).randomPolygonRandomMappingStyle();
		}
		
		// make sure everything's timed to the beat
		for( int i=0; i < _activeTextures.size(); i++ ) {
			_activeTextures.get(i).updateTiming();
		}
		
		if( numBeatsDetected % 100 == 0 ) {
			// every 20 beats, set all polygons' styles to be the same per group
			for(int i=0; i < _mappingGroups.size(); i++ ) {
				if( MathUtil.randRange(0, 100) < 90 ) {
					_mappingGroups.get(i).setAllPolygonsTextureStyle( MathUtil.randRange(0, 2) );
				} else {
					_mappingGroups.get(i).setAllPolygonsTextureStyle( IMappedPolygon.MAP_STYLE_EQ );
				}
				_mappingGroups.get(i).newColor();
			}
			// maybe also set a group to all to be the same texture
			for(int i=0; i < _mappingGroups.size(); i++ ) {
				if( MathUtil.randRange(0, 100) < 50 ) {
					_mappingGroups.get(i).setAllPolygonsToNewTexture();
				}
			}
		}
		
		// every 40 beats, do something bigger
		if( numBeatsDetected % 400 == 0 ) {
			bigChangeTrigger();
		}
	}
	
	protected void checkBeat() {
		if( audioIn.isBeat() == true && p.millis() - 4000 > _lastInputMillis ) {
			updateTiming();
		}
	}
}


//MappingGroup centerGroup = _mappingGroups.get(0);
//centerGroup.clearAllTextures();
//centerGroup.pushTexture( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 )) );
//centerGroup.pushTexture( _texturePool.get( MathUtil.randRange(0, _texturePool.size()-1 )) );
//centerGroup.setAllPolygonsToTexture(0);

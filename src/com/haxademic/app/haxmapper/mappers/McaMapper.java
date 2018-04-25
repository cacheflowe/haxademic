package com.haxademic.app.haxmapper.mappers;

import com.haxademic.app.haxmapper.HaxMapper;
import com.haxademic.app.haxmapper.textures.TextureAudioTube;
import com.haxademic.app.haxmapper.textures.TextureBlobSheet;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureEQFloatParticles;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureImageTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureLinesEQ;
import com.haxademic.app.haxmapper.textures.TextureRotatingRings;
import com.haxademic.app.haxmapper.textures.TextureRotatorShape;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureSvgPattern;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;

public class McaMapper
extends HaxMapper{
		
	/*
	 * TODO:
	 * - Catch MappedQuads up to Mapped Triangles: rotation lerping, uv lerping, etc 
	 * - use new triangle random coordinates for mapped quads
	 * - Refactor & organize
	 * 		- Merge user input triggers & beat detection decisions and then start to organize automated decision making 
	 * - Fix image cycling texture - it does weird flashy things
	 * - interpolate polygon coordinates for animated UV maps
	 * - is newMode() getting called on textures?
	 * - is rotate() getting called on textures?
	 * - Add a vertex shader to manipulate the z of all mesh vertices? since we're not using pshapes, maybe just use noise() to deform the z, then apply lighting
	 * - triangle rotation is causing warped polygons
	 * 		- it's also non-interpolating right now, which it should be
	 * - Fix up switching to all one texture. clear out other textures in the current pool?
	 * When switching textures, go dark and fade back in quickly
	 * - test with 1-6 mapping groups
	 * - Update LED lights to have more modes. do some oscillation/cycling across the lights
	 * - Fill in beat detection by tracking previous beats and averaging out the recent intervals to keep the beat going - fade off if the beat stops 

	 * - Add more ambient overlay shaders
	 * - Fix TextureSphereAudioTextures

	 * - Add another floating audio-reactive particle overlay texture - switch the overloay textures out on an interval
	 */
	

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		super.overridePropsFile();
		p.appConfig.setProperty( "mapping_file", FileUtil.getFile("text/mapping/mapping-2016-04-09-20-23-29.txt") );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.OSC_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, true );
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1024 );
		p.appConfig.setProperty( AppSettings.DMX_LIGHTS_COUNT, 0 );
		p.appConfig.setProperty( AppSettings.HIDE_CURSOR, false );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	/////////////////////////////////////////////////////////////////
	// required overrides to init mapping groups and texture pools 
	/////////////////////////////////////////////////////////////////

	protected void addTexturesToPool() {

		int videoW = 512;// 592;
		int videoH = 288;// 334;
		

		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));
		_texturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));	

		
		int shaderW = 512;
		int shaderH = 512;
		int shaderWsm = shaderW/2;
		int shaderHsm = shaderH/2;
		shaderWsm = shaderW;
		shaderHsm = shaderH;
		
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "sdf-01-auto.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "sdf-02-auto.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "basic-checker.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "basic-diagonal-stripes.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bubbles-iq.glsl" ));
//		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-circles.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-clouds.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-expand-loop.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-eye-jacker-01.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-eye-jacker-02.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-kaleido.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-motion-illusion.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-scroll-rows.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-simple-sin.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderW, shaderH, "bw-tiled-moire.glsl" ));
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
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "star-field.glsl" ));
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
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "dot-grid-dof.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm/2, shaderHsm/2, "docking-tunnel.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm, shaderHsm, "hughsk-metaballs.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm/2, shaderHsm/2, "hughsk-tunnel.glsl" ));
		_texturePool.add( new TextureShaderTimeStepper( shaderWsm/2, shaderHsm/2, "morphing-bokeh-shape.glsl" ));


		
		_texturePool.add( new TextureTwistingSquares( shaderWsm, shaderHsm ));
		_texturePool.add( new TextureTwistingSquares( shaderWsm, shaderHsm ));
		_texturePool.add( new TextureEQConcentricCircles( shaderW, shaderH ) );
		_texturePool.add( new TextureEQConcentricCircles( shaderW, shaderH ) );
		_texturePool.add( new TextureScrollingColumns( shaderW, shaderH ));
		_texturePool.add( new TextureImageTimeStepper( shaderW, shaderH ));
		_texturePool.add( new TextureEQColumns( shaderW, shaderH ));
		_texturePool.add( new TextureEQGrid( shaderW, shaderH ));
		_texturePool.add( new TextureLinesEQ( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformSimple( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformSimple( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformCircle( shaderW, shaderH ));
		_texturePool.add( new TextureWaveformCircle( shaderW, shaderH ));
		_texturePool.add( new TextureSphereRotate( shaderW, shaderH ));
//		_texturePool.add( new TextureOuterSphere( shaderW, shaderH ) );
//		_texturePool.add( new TextureOuterSphere( shaderW, shaderH ) );
		_texturePool.add( new TextureRotatorShape( shaderW, shaderH ) );
		_texturePool.add( new TextureRotatorShape( shaderW, shaderH ) );
		_texturePool.add( new TextureRotatingRings( shaderW, shaderH ) );
		_texturePool.add( new TextureAudioTube( shaderW, shaderH ) );
		_texturePool.add( new TextureBlobSheet( shaderW, shaderH ) );
		_texturePool.add( new TextureColorAudioSlide( shaderW, shaderH ));
		_texturePool.add( new TextureColorAudioSlide( shaderW, shaderH ));
		_texturePool.add( new TextureEQFloatParticles( shaderW, shaderH ));
		_texturePool.add( new TextureEQFloatParticles( shaderW, shaderH ));
		_texturePool.add( new TextureSvgPattern( shaderW, shaderH ));
		
		
		// shuffle one time and add 1 inital texture to current array
		shuffleTexturePool();
		_activeTextures.add( _texturePool.get(nextTexturePoolIndex() ));

		// add full screen overlay texture
		_overlayTexturePool.add(new TextureEQFloatParticles( shaderWsm, shaderHsm ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderWsm, shaderHsm, "light-leak.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderWsm, shaderHsm, "star-field.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderWsm, shaderHsm, "square-fade.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderWsm, shaderHsm, "bw-clouds.glsl" ));
		_overlayTexturePool.add(new TextureShaderTimeStepper( shaderWsm, shaderHsm, "water-smoke.glsl" ));
	}
		
	
	// MCA ONLY =============================
	

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



package com.haxademic.app.musicvideos;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Superformula;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.filters.BlobParticles;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.render.VideoFrameGrabber;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class CachePatterVideo
extends PAppletHax  
{
	/**
	 * TODO:
	 * - contrast filter on post-processing?
	 * - "CACHEFLOWE - PATTER" text
	 * - more noticable particle audioreactivity
	 * - fade particles up before fading out - they're too strong at the outset..
	 * - automate animation of vignette filter
	 * - Pick final order of clips - tell a story damnit...
	 * - Supershape should do interesting things every snare hit... - set up a test app and audio object for this
	 * - Triangualted parts of humans as larger particles?
	 * - fix frame scrubbing - it's shaky on some clips
	 * - Make sure clips are playing at right speed
	 * - Larger effects per person - reflections, positioning, etc?
	 * - Fix squished video at 1920x1080
	 */

	
	// movie scrubbing
	PGraphics _movieBuffer;
	VideoFrameGrabber _videoFrames;
	PShader _chromaKeyFilter;
	float _curMovieFrame = 1800;
	float _playbackFrameCountInc = 1f/3f;
	protected LinearFloat _movieOpacityEaser;
	
	BlobParticles _blobFilter;
	
	// clouds
	PGraphics _cloudsGraphics;
	PShader _clouds;
	int _timingFrames = -1;
	protected float _timeConstantInc = 0.01f;
	protected EasingFloat _cloudTimeEaser = new EasingFloat(0, 13);
	float _songLengthFrames = 4520; // really 4519, but we're letting Renderer shut this down at the end of the audio file
	
	// post-processing
	PShader _fxaa;
	PShader _desaturate;
	PShader _resaturate;
	PShader _vignette;
	protected EasingFloat _vignetteEaser = new EasingFloat(0.15f, 13);
	PShader _brightness;
	protected LinearFloat _overallBrightnessEaser = new LinearFloat(2, 0.004f);	// (1/0.004)/30 = 8.3 seconds
	PShader _contrast;

	// superformula
	protected Superformula _superForm;
	protected float[] _camPos = { 0f, 0f, 2500f};
	protected PGraphics _superFormGfx;
	PShader _opacity;
	protected EasingFloat _superShapeOpacityEaser = new EasingFloat(0, 17);
	
	
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1920" );
		_appConfig.setProperty( "height", "1080" );
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
		_appConfig.setProperty( "rendering", "true" );
		_appConfig.setProperty( "render_midi", "true" );
		_appConfig.setProperty( "render_midi_file", FileUtil.getHaxademicDataPath() + "midi/patter-kick-snare-bass-synth-timing.mid" );
		_appConfig.setProperty( "render_midi_bpm", "132" );
		_appConfig.setProperty( "render_midi_offset", "0" );
		_appConfig.setProperty( "render_audio", "true" );
		_appConfig.setProperty( "render_audio_file", "/Users/cacheflowe/Documents/workspace/plasticsoundsupply/resources/_releases/PSS020 - ambient compilation/patter-video/13. CacheFlowe - Patter - Master.wav" );
	}

	/**
	 * SETUP ===================================================================================================
	 */
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		setupClouds();
		setupMovieScrubbing();
		setupSuperformula();
		setupPostEffects();
		
		// snare();
	}
	
	protected void setupMovieScrubbing() {
		// video frame buffer graphics
		_movieBuffer = p.createGraphics(p.width, p.height, P.P3D);
		_movieBuffer.smooth(OpenGLUtil.SMOOTH_HIGH);

		// shaders
		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-gpu.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
		_chromaKeyFilter.set("smoothing", 0.19f);
		_chromaKeyFilter.set("colorToReplace", 0.48f,0.8f,0.2f);
		
		_movieOpacityEaser = new LinearFloat(0, 0.02f);
		
		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_desaturate.set("saturation", 0.3f);
	
		_resaturate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/saturation.glsl" );
		_resaturate.set("saturation", 1.4f);
		
		// video scrubber
		_videoFrames = new VideoFrameGrabber(p, "/Users/cacheflowe/Documents/workspace/plasticsoundsupply/resources/_releases/PSS020 - ambient compilation/patter-video/selectes-reference-all-export-264.mov", 30, 0);

		// particles
		_blobFilter = new BlobParticles( p.width, p.height );
	}
	
	protected void setupClouds() {
		_cloudsGraphics = p.createGraphics(p.width, p.height, P.OPENGL);
		_cloudsGraphics.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/clouds-iq.glsl" ); 
	}
	
	protected void setupSuperformula() {
		_opacity = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/opacity.glsl" );
		_opacity.set("opacity", 0.3f);

		_superForm = new Superformula(100,100, 1, 1,   6, 20,  7, 18);
		_superFormGfx = p.createGraphics(p.width, p.height, P.P3D);
		_superFormGfx.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	protected void setupPostEffects() {
		_fxaa = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/fxaa.glsl" ); 
		_fxaa.set("resolution", 1f, (float)(p.width/p.height));
		
		_brightness = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", 0.5f);
		_overallBrightnessEaser.setCurrent(2.5f);
		_overallBrightnessEaser.setTarget(1.1f);
		
		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.5f);
		_vignette.set("spread", 0.15f);
		
		_contrast = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/contrast.glsl" );
		_contrast.set("contrast", 1.2f);
	}

	/**
	 * FRAME PROCESSING ===================================================================================================
	 */
	
	public void drawApp() {
		p.background(0,0,0);
		drawClouds();
		drawSuperformula();
		drawMovie();
		applyPostProcessing();
		checkRenderComplete();		
	}
	
	protected void drawClouds() {
		// move clouds control in a big half-circle
		float percentComplete = (float) p.frameCount / _songLengthFrames;
		float cloudControlRadians = percentComplete * P.PI;
		float cloudControlX = (float)p.width/2f + 		   P.sin(cloudControlRadians - P.HALF_PI) * (float)p.width/2f;
		float cloudControlY = ((float)-p.height * 0.3f) + P.cos(cloudControlRadians - P.HALF_PI) * (float)p.width/2f * 1.f;

		_cloudTimeEaser.update();
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/clouds-iq.glsl" ); 
		_clouds.set("resolution", 1f, (float)(p.width/p.height));
		_clouds.set("time", p.frameCount * _timeConstantInc + _cloudTimeEaser.value() );
//		_clouds.set("mouse", 0.5f + p.frameCount/4000f, 0.9f - p.frameCount/4000f);		
//		_clouds.set("mouse", (float)mouseX/p.width, (float)mouseY/p.height);		
		_clouds.set("mouse", cloudControlX /p.width, cloudControlY/p.height);		

		_cloudsGraphics.beginDraw();
		_cloudsGraphics.filter(_clouds);		
		_cloudsGraphics.endDraw();
//		_destClouds.copy(_bg, 0, 0, _bg.width, _bg.height, 0, 0, _bg.width, _bg.height);
		p.image( _cloudsGraphics, 0, 0);
		
		// debug cloud controls
//		p.fill(0);
//		p.ellipse(cloudControlX, cloudControlY, 10, 10);
		
//		_cloudsGraphics.filter(_contrast);
	}
	
	protected void drawSuperformula() {
		_superFormGfx.beginDraw();
		_superFormGfx.pushMatrix();
		_superFormGfx.clear();
		
		_superFormGfx.translate( p.width/2, p.height/2, 0 );

//			p.rotateX(p.frameCount/20f);
//			p.rotateY(P.PI/2f);
		_superFormGfx.rotateZ(p.frameCount/50f);
		
		float audioRange = 0.1f;
		_superForm.a( 6 + (audioRange * 100f * p._audioInput.getFFT().averages[0]));
		_superForm.b( 8 + (audioRange * 10f * p._audioInput.getFFT().averages[1]));
		_superForm.m( 15 + (audioRange * 10f * p._audioInput.getFFT().averages[2]));
		_superForm.n1( 15 + (audioRange * 20f * p._audioInput.getFFT().averages[3]));
		_superForm.n2( 15 + (audioRange * 50f * p._audioInput.getFFT().averages[4]));
		_superForm.n3( 6 + (audioRange * 40f * p._audioInput.getFFT().averages[5]));

		_superForm.update();
		_superForm.drawMesh(_superFormGfx, true, true, true, false, _camPos );
		
		_superFormGfx.popMatrix();
		_superFormGfx.endDraw();

		_superShapeOpacityEaser.update();
//		_opacity = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/opacity.glsl" );
//		_opacity.set("opacity", _opacityEaser.value());
//		_superFormGfx.filter(_opacity);
		DrawUtil.setPImageAlpha(p, _superShapeOpacityEaser.value());
		p.image( _superFormGfx, 0, 0);
		DrawUtil.setPImageAlpha(p, 1);
	}
	
	protected void drawMovie() {
		_curMovieFrame += _playbackFrameCountInc;
		_videoFrames.setFrameIndex((int)Math.floor(_curMovieFrame));
		_movieBuffer.beginDraw();
		_movieBuffer.image(_videoFrames.movie(), 0, 0, p.width, p.height);
		_movieBuffer.endDraw();

		// update & apply chroma shader
		_movieBuffer.filter(_chromaKeyFilter);
		
		// apply blob/particle filter draw movie
		// and alpha fade-in 
		_movieOpacityEaser.update();
		DrawUtil.setPImageAlpha(p, _movieOpacityEaser.value());
		PImage filteredVideo = _blobFilter.updateWithPImage( _movieBuffer );
		p.image(filteredVideo, 0, 0);
		DrawUtil.setPImageAlpha(p, 1);

	}
	
	protected void applyPostProcessing() {
//		p.filter(_fxaa);

		_overallBrightnessEaser.update();
		_brightness.set("brightness", _overallBrightnessEaser.value());
//		p.filter(_brightness);
		
		_vignetteEaser.update();
		_vignette.set("spread", _vignetteEaser.value() );
		p.filter(_vignette);
		
	}
	
	protected void checkRenderComplete() {
		// shut down after rendering length of song ======================================================
		if( p.frameCount >= _songLengthFrames ) {
			if( _isRendering ) {
				_renderShutdown = p.frameCount;
				_renderer.stop();
				P.println("shutting down");
			}
		}
	}
	
	/**
	 * TIMING & SECTION CHANGES ======================================================================================
	 */
	
	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		// P.println(_midi._notesOn);
		// handle midi file input
		if( isMidi && _midi != null ) {
			if( _midi.midiNoteIsOn( 64 ) == 1 ) newTiming();
			else if( _midi.midiNoteIsOn( 60 ) == 1 ) kick();
			else if( _midi.midiNoteIsOn( 61 ) == 1 ) snare();
		} 
	}
	
	protected void newTiming() {
		_timingFrames++;
		P.println("==========================");
		P.println("========= newTiming: "+_timingFrames);
		P.println("==========================");
		int timingSectionChange = 16;
		// we need 22 sections for 16 timing ticks at 7 seconds each
		if( _timingFrames == timingSectionChange * 0 ) {
			// joey dreamy
			setNewClipProps( 1800, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 1 ) {
			// jeff hands/shoulders
			setNewClipProps( 3939, 1f/1.5f );
		}
		if( _timingFrames == timingSectionChange * 2 ) {
			// marcellus hand
			setNewClipProps( 7080, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 3 ) {	// beat kicks in
			// dawn headphones dreamy
			setNewClipProps( 5549, 1f/2.25f );
		}
		if( _timingFrames == timingSectionChange * 4 ) {
			// mike hairblow
			setNewClipProps( 3600, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 5 ) {
			// justin closeup and hands
			setNewClipProps( 7590, 1f/2.5f );
		}
		if( _timingFrames == timingSectionChange * 6 ) {
			// joey hands in face;
			setNewClipProps( 1935, 1f/2f );
		}
		if( _timingFrames == timingSectionChange * 7 ) {
			// justin hands;
			setNewClipProps( 700, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 8 ) {
			// mike hair blow shake;
			setNewClipProps( 3375, 1f/2.5f );
		}
		if( _timingFrames == timingSectionChange * 9 ) {	// bassline
			// jeff headphones sway
			setNewClipProps( 4365, 1f/2f );
		}
		if( _timingFrames == timingSectionChange * 10 ) {
			// joey stand up headphones
			setNewClipProps( 4938, 1f/1.2f );
		}
		if( _timingFrames == timingSectionChange * 11 ) {
			// dawn hand wiggle
			setNewClipProps( 6407, 1f/2.75f );
		}
		if( _timingFrames == timingSectionChange * 12 ) {
			// joey mind blown
			setNewClipProps( 6775, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 13 ) {	// heavy beat
			// joey falls on pillow
			setNewClipProps( 2280, 1f/1.25f );
		}
		if( _timingFrames == timingSectionChange * 14 ) {
			// dawn headphones fan
			setNewClipProps( 5253, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 15 ) {
			// marcellus glasses off headphones
			setNewClipProps( 5970, 1f/2f );
		}
		if( _timingFrames == timingSectionChange * 16 ) {	// outro
			// mike mind blown
			setNewClipProps( 7320, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 17 ) {
			// mike and dawn laughing
			setNewClipProps( 9180, 1f/2f );
		}
		if( _timingFrames == timingSectionChange * 18 ) {
			// mike and dawn hair staggered // 9690 // 
			setNewClipProps( 9570, 1f/2f );
		}
		if( _timingFrames == timingSectionChange * 19 ) {	// ambient outro, 12 seconds left
			// mike hand wiggle
			setNewClipProps( 2975, 1f/4f );
			
			// fade out to white
			_overallBrightnessEaser.setTarget(4f);
		}
//		if( _timingFrames == timingSectionChange * 20 ) {
//			// jeffrey laughing 
//			setNewClipProps( 3750, 1f/2f );
//		}
		
		_videoFrames.setFrameIndex((int)Math.floor(_curMovieFrame));
	}
	
	protected void setNewClipProps( int curFrame, float playbackSpeed ) {
		if( p.frameCount > 10 ) _blobFilter.runParticlesFullImage();	// skip first person since there wasn't a previous person to dissipate
		
		_curMovieFrame = curFrame; 
		_playbackFrameCountInc = playbackSpeed;

		// fade new clip in with shader or native PImage opacity
		_movieOpacityEaser.setCurrent(0f);
		_movieOpacityEaser.setTarget(0.9f);
	}

	protected void kick() {
		P.println("= kick ==================");
		// push clouds forward
		_cloudTimeEaser.setTarget(_cloudTimeEaser.value() + 0.68f);
		
		// animate vignette
		_vignetteEaser.setTarget( 0.15f );
		_vignetteEaser.setCurrent( 0.3f );
	}
	
	protected void snare() {
		P.println("= snare ==================");
		// show and fade out supershape
		_superShapeOpacityEaser.setCurrent(0.2f);
		_superShapeOpacityEaser.setTarget(0);
	}
}

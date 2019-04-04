package com.haxademic.app.musicvideos;

import java.util.Vector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pgraphics.archive.FastBlurFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.draw.shapes.Superformula;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.render.VideoFrameGrabber;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;


public class CachePatterVideo
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
	 * TODO:
	 * - adjust chroma threshold per clip (especially mike's)
	 * - all clips should be centered on actors to line up better with supershape (imovie editing)
	 * - MIDI needs snares throughout - fix in ableton and add last section start midi note
	 * - make clouds go at different speeds (into & outro should speed up and up until stop)
	 * - particles should fly in the direction of the clouds to create a more holistic environment - use rotation from clouds test
	 * - PSS logo fade-out (with chroma threshold control)
	 * - Finish clip order
	 * - Finish clip chroma threshold selection
	 * - Finish polishing up of superformula selections 
	 * - export source video to mov with all keyframes before rendering final
	 * - Fix squished video at 1920x1080
	 */


	// movie scrubbing
	PGraphics _movieBuffer;
	PGraphics _movieMotionBlurBuffer;
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
	float _songLengthFrames = 4530; // really 4519, but we're letting Renderer shut this down at the end of the audio file

	// post-processing
	PShader _fxaa;
	PShader _desaturate;
	PShader _resaturate;
	PShader _vignette;
	protected EasingFloat _vignetteSpreadEase = new EasingFloat(0.15f, 13);
	protected LinearFloat _vignetteDarknessEaser = new LinearFloat(0, 0.0025f);
	PShader _brightness;
	protected LinearFloat _overallBrightnessEaser = new LinearFloat(2, 0.004f);	// (1/0.004)/30 = 8.3 seconds
	PShader _contrast;

	MotionBlurPGraphics _movieMotionBlur;
	MotionBlurPGraphics _superformMotionBlur;

	// superformula
	protected Superformula _superForm;
	protected float[] _camPos = { 0f, 0f, 2500f};
	protected PGraphics _superFormGfx;
	protected float[] _superFormSettings = {6f, 8f, 15f, 15f, 15f, 6f};
	PShader _opacity;
	protected EasingFloat _superShapeOpacityEaser = new EasingFloat(0, 17);



	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "30" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1920" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1080" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "720" );
//		p.appConfig.setProperty( AppSettings.WIDTH, "960" );
//		p.appConfig.setProperty( AppSettings.HEIGHT, "540" );
//		p.appConfig.setProperty( AppSettings.WIDTH, "480" );
//		p.appConfig.setProperty( AppSettings.HEIGHT, "270" );
//		p.appConfig.setProperty( AppSettings.WIDTH, "240" );
//		p.appConfig.setProperty( AppSettings.HEIGHT, "135" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "true" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) _songLengthFrames );
		p.appConfig.setProperty( AppSettings.RENDER_MIDI, "true" );
		p.appConfig.setProperty( AppSettings.RENDER_MIDI_FILE, FileUtil.getHaxademicDataPath() + "midi/patter-kick-snare-bass-synth-timing-more-snares.mid" );
		p.appConfig.setProperty( AppSettings.RENDER_MIDI_BPM, "132" );
		p.appConfig.setProperty( AppSettings.RENDER_MIDI_OFFSET, "0" );
		p.appConfig.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getHaxademicDataPath() + "audio/13. CacheFlowe - Patter - Master.wav" );
	}

	/**
	 * SETUP ===================================================================================================
	 */

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_movieMotionBlur = new MotionBlurPGraphics(8);
		_superformMotionBlur = new MotionBlurPGraphics(6);

		setupClouds();
		setupMovieScrubbing();
		setupSuperformula();
		setupPostEffects();

		// snare();
	}

	protected void setupMovieScrubbing() {
		// video frame buffer graphics
		_movieBuffer = p.createGraphics(p.width, p.height, P.P2D);
		_movieBuffer.smooth(OpenGLUtil.SMOOTH_HIGH);

		_movieMotionBlurBuffer = p.createGraphics(p.width, p.height, P.P2D);
		_movieMotionBlurBuffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		// shaders
		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/chroma-gpu.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
		_chromaKeyFilter.set("smoothing", 0.26f);
		_chromaKeyFilter.set("colorToReplace", 0.29f,0.93f,0.14f);

		_movieOpacityEaser = new LinearFloat(0, 0.02f);

		_desaturate = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
		_desaturate.set("saturation", 0.0f);

		_resaturate = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
		_resaturate.set("saturation", 1.4f);

		// video scrubber
		_videoFrames = new VideoFrameGrabber(p, FileUtil.getHaxademicDataPath() + "video/patter/ultrasoft-selects-1080.mp4", 30, 0);

		// particles
		_blobFilter = new BlobParticles( p.width, p.height );
	}

	protected void setupClouds() {
		_cloudsGraphics = p.createGraphics(p.width, p.height, P.P3D);
		_cloudsGraphics.smooth(OpenGLUtil.SMOOTH_HIGH);

		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/clouds-iq.glsl" ); 
	}

	protected void setupSuperformula() {
		_opacity = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/opacity.glsl" );
		_opacity.set("opacity", 0.3f);

		_superForm = new Superformula(100,100, 1, 1,   6, 20,  7, 18);
		_superFormGfx = p.createGraphics(p.width, p.height, P.P3D);
		_superFormGfx.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		
	}

	protected void setupPostEffects() {
		_fxaa = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/fxaa.glsl" ); 
		_fxaa.set("resolution", 1f, (float)(p.width/p.height));

		_brightness = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/brightness.glsl" );
		_brightness.set("brightness", 1.5f);
		_overallBrightnessEaser.setCurrent(1.5f);
		_overallBrightnessEaser.setTarget(1.0f);

		_vignette = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/vignette.glsl" );
		_vignette.set("darkness", 0.0f);
		_vignetteDarknessEaser.setTarget(0.5f);
		_vignette.set("spread", 0.15f);

		_contrast = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/contrast.glsl" );
		_contrast.set("contrast", 1.2f);
	}

	/**
	 * FRAME PROCESSING ===================================================================================================
	 */

	public void drawApp() {
		handleInputTriggers();
		p.background(0,0,0);
		drawClouds();
		drawSuperformula();
		drawMovie();
		applyPostProcessing();
	}

	protected void drawClouds() {
		// move clouds control in a big half-circle
		float percentComplete = (float) p.frameCount / _songLengthFrames;
		float cloudControlRadians = percentComplete * P.PI;
		float cloudControlX = (float)p.width/2f + 		  P.sin(cloudControlRadians - P.HALF_PI) * (float)p.width/2f;
		float cloudControlY = ((float)-p.height * 0.3f) + P.cos(cloudControlRadians - P.HALF_PI) * (float)p.width/2f * 1.f;

		_cloudTimeEaser.update();
		//		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/textures/clouds-iq.glsl" ); 
		_clouds.set("resolution", 1f, (float)(p.width/p.height));
		_clouds.set("time", p.frameCount * _timeConstantInc + _cloudTimeEaser.value() );
		//		_clouds.set("mouse", 0.5f + p.frameCount/4000f, 0.9f - p.frameCount/4000f);		
		//		_clouds.set("mouse", (float)mouseX/p.width, (float)mouseY/p.height);		
		_clouds.set("mouse", cloudControlX / p.width, cloudControlY / p.height);		

		_cloudsGraphics.beginDraw();
		_cloudsGraphics.filter(_clouds);		
		_cloudsGraphics.endDraw();
//		_cloudsGraphics.filter(_contrast);
		//		_destClouds.copy(_bg, 0, 0, _bg.width, _bg.height, 0, 0, _bg.width, _bg.height);
		p.image( _cloudsGraphics, 0, 0);

		// debug cloud controls
		//		p.fill(0);
		//		p.ellipse(cloudControlX, cloudControlY, 10, 10);

	}

	protected void drawSuperformula() {
		_superFormGfx.beginDraw();
		_superFormGfx.pushMatrix();
		_superFormGfx.clear();

		_superFormGfx.translate( p.width/2, p.height/2, 0 );

		//			p.rotateX(p.frameCount/20f);
		//			p.rotateY(P.PI/2f);
//		_superFormGfx.rotateZ(p.frameCount/50f);

		float audioRange = 0.1f;
		_superForm.a( _superFormSettings[0] + (audioRange * 100f * p.audioFreq(0)));
		_superForm.b( _superFormSettings[1] + (audioRange * 10f * p.audioFreq(1)));
		_superForm.m( _superFormSettings[2]);// + (audioRange * 10f * p.audioFreq(2)));
		_superForm.n1( _superFormSettings[3] + (audioRange * 20f * p.audioFreq(3)));
		_superForm.n2( _superFormSettings[4] + (audioRange * 50f * p.audioFreq(4)));
		_superForm.n3( _superFormSettings[5] + (audioRange * 40f * p.audioFreq(5)));

		_superForm.update();
		_superForm.drawMesh(_superFormGfx, true, true, false, true, _camPos );

		_superFormGfx.popMatrix();
		_superFormGfx.endDraw();

		_superShapeOpacityEaser.update();
		//		_opacity = loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/opacity.glsl" );
		//		_opacity.set("opacity", _opacityEaser.value());
		//		_superFormGfx.filter(_opacity);
		DrawUtil.setPImageAlpha(p, _superShapeOpacityEaser.value());
//		p.image( _superFormGfx, 0, 0);
		
		_superformMotionBlur.updateToCanvas(_superFormGfx, p.g, _superShapeOpacityEaser.value());
	}

	protected void drawMovie() {
		// set movie frame
		_curMovieFrame += _playbackFrameCountInc;
		_videoFrames.setFrameIndex((int)Math.floor(_curMovieFrame));
		
		// draw movie to canvas
		_movieBuffer.beginDraw();
		_movieBuffer.image(_videoFrames.movie(), 0, 0, p.width, p.height);
		_movieBuffer.endDraw();

		// update & apply chroma shader
		_movieBuffer.filter(_chromaKeyFilter);

		PImage particlesLayer = _blobFilter.updateWithPImage( _movieBuffer );

		_movieBuffer.filter(_desaturate);
		
		// draw to yet another buffer so we can fade in with motion blur
		_movieMotionBlurBuffer.beginDraw();
		_movieMotionBlurBuffer.clear();
		_movieMotionBlur.updateToCanvas(_movieBuffer, _movieMotionBlurBuffer, 0.9f);
		_movieMotionBlurBuffer.endDraw();
		
		
		// draw movie motion blur to screen with alpha
		_movieOpacityEaser.update();
		DrawUtil.setPImageAlpha(p, (p.frameCount % 2 == 1) ? _movieOpacityEaser.value() * 0.999f : _movieOpacityEaser.value() * 1f );	// stupid hack b/c UMovieMaker doesn't save the exact same frame twice in a row.
//		DrawUtil.setPImageAlpha(p, _movieOpacityEaser.value());
		p.image(_movieMotionBlurBuffer, 0, 0);

		// draw particles at full alpha
		DrawUtil.setPImageAlpha(p, 1.0f);
		p.image(particlesLayer, 0, 0);

	}

	protected void applyPostProcessing() {
		_overallBrightnessEaser.update();
		_brightness.set("brightness", _overallBrightnessEaser.value());
		p.filter(_brightness);

//		p.filter(_fxaa);
		
		_vignetteDarknessEaser.update();
		_vignette.set("darkness", _vignetteDarknessEaser.value() );
		_vignetteSpreadEase.update();
		_vignette.set("spread", _vignetteSpreadEase.value() );
		p.filter(_vignette);
	}

	/**
	 * TIMING & SECTION CHANGES ======================================================================================
	 */

	public void handleInputTriggers() {
		// P.println(_midi._notesOn);
		// handle midi file input
		if( midiState != null ) {
			if( midiState.isMidiButtonOn( 64 ) ) newTiming();
			else if( midiState.isMidiButtonOn( 60 ) ) kick();
			else if( midiState.isMidiButtonOn( 61 ) ) snare();
		} 
	}

	protected void newTiming() {
		_timingFrames++;
		P.println("==========================");
		P.println("========= newTiming: "+_timingFrames);
		P.println("==========================");
		// 30*((60*2)+39.7)
		int timingSectionChange = 16;
		// we need 22 sections for 16 timing ticks at 7 seconds each
		if( _timingFrames == timingSectionChange * 0 ) {
//			_movieOpacityEaser.setInc(0.02f);
			_movieOpacityEaser.setInc(0.002f);
			_chromaKeyFilter.set("thresholdSensitivity", 0.4f);

			// title
			setNewClipProps( 6510, 1f/1f );
			
			// test clips 
//			setNewClipProps( 5859, 1f/2.1f );
//			_chromaKeyFilter.set("thresholdSensitivity", 0.65f);

		}
		if( _timingFrames == timingSectionChange * 1 ) {
			_movieOpacityEaser.setInc(0.02f);
			_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
			
			// jeff hands/shoulders
			setNewClipProps( 3240, 1f/2.5f );
//			setNewClipProps( 3078, 1f/3f );
		}
		if( _timingFrames == timingSectionChange * 2 ) {
			// joey dreamy
			setNewClipProps( 888, 1f/2.9f );
//			setNewClipSuperformula(4f, 9f, 26f, 15f, 4.5f, 15f);
		}
		if( _timingFrames == timingSectionChange * 3 ) {
			// beat kicks in
			// mike hairblow
			setNewClipProps( 2529, 1f/2.65f );
		}
		if( _timingFrames == timingSectionChange * 4 ) {
			// FIRST SNARES - superformula!
			// dawn headphones dreamy
			setNewClipProps( 4781, 1f/2.7f );
			setNewClipSuperformula(5.6f, 26f, 30f, 8.6f, 4.5f, 2.25f);
		}
		if( _timingFrames == timingSectionChange * 5 ) {
			// marcellus hand
			setNewClipProps( 5694, 1f/2.8f );
		}
		if( _timingFrames == timingSectionChange * 6 ) {
			// joey hands in face;
			setNewClipProps( 1395, 1f/2f );
			setNewClipSuperformula(12.75f, 20f, 8f, 30f, 11.81f, 9f);
		}
		if( _timingFrames == timingSectionChange * 7 ) {
			// justin hand;
			setNewClipProps( 147, 1f/4.4f );
			setNewClipSuperformula(20.4f, 18f, 30f, 11.6f, 3.5f, 3.4f);
		}
		if( _timingFrames == timingSectionChange * 8 ) {
			// jeff headphones sway
			setNewClipProps( 3375, 1f/2.0f );
			setNewClipSuperformula(20.4f, 14f, 0.0f, 6f, 1.1f, 5.2f);
		}
		if( _timingFrames == timingSectionChange * 9 ) {	// bassline
			// dawn headphones sway
			setNewClipProps( 4938, 1f/2f );
			setNewClipSuperformula(7.3f, 6.93f, 8f, 9.56f, 4.87f, 6.37f);
		}
		if( _timingFrames == timingSectionChange * 10 ) {
			// joey stand up headphones
			setNewClipProps( 4305, 1f/1.3f );
			setNewClipSuperformula(8.6f, 10.5f, 16f, 7.3f, 3.5f, 3.4f);
		}
		if( _timingFrames == timingSectionChange * 11 ) {
			// marceullus head back
			setNewClipProps( 5619, 1f/3.1f );
			setNewClipSuperformula(10.68f, 5.43f, 7.87f, 15f, 6.56f, 6.93f);
		}
		if( _timingFrames == timingSectionChange * 12 ) {
			// mike laugh
			setNewClipProps( 2130, 1f/4f );
			setNewClipSuperformula(10.68f, 5.43f, 4.12f, 26.81f, 15f, 12.93f);
		}
		if( _timingFrames == timingSectionChange * 13 ) {	// heavy beat
			// joey falls on pillow
			setNewClipProps( 1521, 1f/1.1f );
			setNewClipSuperformula(10.68f, 11.62f, 12f, 19.12f, 5.81f, 8.25f);
		}
		if( _timingFrames == timingSectionChange * 14 ) {
			// marcellus
			setNewClipProps( 5253, 1f/3f );
			setNewClipSuperformula(25.68f, 4.5f, 3.93f, 19.12f, 5.81f, 8.25f);
		}
		if( _timingFrames == timingSectionChange * 15 ) {
			// marcellus glasses off headphones
			setNewClipProps( 5166, 1f/2f );
			setNewClipSuperformula(25.68f, 4.5f, 3.93f, 21.56f, 6f, 18.37f);
		}
		if( _timingFrames == timingSectionChange * 16 ) {	// outro
			// mike mind blown
			setNewClipProps( 5859, 1f/2.1f );
			setNewClipSuperformula(25.68f, 2.25f, 4.01f, 21.56f, 12.0f, 18.37f);
		}
		if( _timingFrames == timingSectionChange * 17 ) {
			// justin sunglasses
			setNewClipProps( 1960, 1f/2.1f );
			setNewClipSuperformula(18.18f, 5.06f, 12.0f, 19.68f, 5.25f, 18.37f);
		}
		if( _timingFrames == timingSectionChange * 18 ) {
			// mike and dawn hair  
			setNewClipProps( 2724, 1f/0.9f );
			setNewClipSuperformula(7.31f, 1f, 4.0f, 16.68f, 13.12f, 30f);
		}
		if( _timingFrames == timingSectionChange * 19 ) {	// ambient outro, 12 seconds left
			// joey hands forward
			setNewClipProps( 5512, 1f/3.4f );
			setNewClipSuperformula(7.31f, 1f, 4.0f, 10.12f, 8.06f, 5.62f);

			// fade out to white
			_overallBrightnessEaser.setTarget(1.3f);
			_vignetteDarknessEaser.setTarget(0.0f);
		}
		if( _timingFrames == timingSectionChange * 20 ) {
			// justing falling asleep 
//			setNewClipProps( 6375, 1f/3f );
			// pss logo 
			setNewClipProps( 6840, 1f/1f );
			_overallBrightnessEaser.setTarget(5f);
		}

		_videoFrames.setFrameIndex((int)Math.floor(_curMovieFrame));
	}

	protected void setNewClipSuperformula( float a, float b, float m, float n1, float n2, float n3 ) {
		_superFormSettings[0] = a;
		_superFormSettings[1] = b;
		_superFormSettings[2] = m;
		_superFormSettings[3] = n1;
		_superFormSettings[4] = n2;
		_superFormSettings[5] = n3;

	}
	protected void setNewClipProps( int curFrame, float playbackSpeed ) {
		if( p.frameCount > 10 ) _blobFilter.runParticlesFullImage();	// skip first person since there wasn't a previous person to dissipate

		_curMovieFrame = curFrame; 
		_playbackFrameCountInc = playbackSpeed;

		// fade new clip in with shader or native PImage opacity
		_movieOpacityEaser.setCurrent(0f);
		_movieOpacityEaser.setTarget(1f);
	}

	protected void kick() {
		P.println("= kick ==================");
		// push clouds forward
		_cloudTimeEaser.setTarget(_cloudTimeEaser.value() + 0.68f);

		// animate vignette
		_vignetteSpreadEase.setTarget( 0.15f );
		_vignetteSpreadEase.setCurrent( 0.3f );
	}

	protected void snare() {
		P.println("= snare ==================");
		// show and fade out supershape
		_superShapeOpacityEaser.setCurrent(0.05f);
		_superShapeOpacityEaser.setTarget(0);
	}























	// modified copy of original
	public class BlobParticles {

		protected PAppletHax p;
		protected int _width;
		protected int _height;
		protected PGraphics _pg;
		protected PImage _image;
		protected PImage _source;
		BlobDetection theBlobDetection;
		PImage blobBufferImg;
		PShader _desaturate;

		protected Vector<BlobParticle> _particles;
		protected Vector<BlobParticle> _inactiveParticles;

		public BlobParticles( int width, int height ) {
			p = (PAppletHax) P.p;
			_width = width;
			_height = height;
			initBlobDetection();
			_particles = new Vector<BlobParticle>();
			_inactiveParticles = new Vector<BlobParticle>();

			_desaturate = p.loadShader( FileUtil.getHaxademicDataPath()+"haxademic/shaders/filters/saturation.glsl" );
			_desaturate.set("saturation", 0.75f);

		}

		public PImage pg() {
			return _pg;
		}

		protected void initBlobDetection() {
			_pg = p.createGraphics( _width, _height, P.P3D );
			_pg.smooth(OpenGLUtil.SMOOTH_HIGH); 
			_image = p.createImage( _width, _height, P.ARGB );

			// BlobDetection
			// img which will be sent to detection (a smaller copy of the image frame);
			blobBufferImg = new PImage( (int)(_width * 0.15f), (int)(_height * 0.15f) ); 
			theBlobDetection = new BlobDetection( blobBufferImg.width, blobBufferImg.height );
			theBlobDetection.setPosDiscrimination(false);	// true if looking for bright areas
			theBlobDetection.setThreshold(0.32f); // will detect bright areas whose luminosity > 0.2f;
		}

		public PImage updateWithPImage( PImage source ) {
			_source = source;
			runBlobDetection( source );

			_pg.beginDraw();
			ImageUtil.clearPGraphics( _pg );
			_pg.noStroke();
			_pg.fill(0,0);

			drawEdges( source );
			drawParticles();

			_pg.endDraw();
//			_pg.filter(_desaturate);

			return _pg;
		}

		public void runParticlesFullImage() {
			for( int x=0; x < _source.width; x += 1 ) {
				for( int y=0; y < _source.height; y += 1 ) {
					int color = ImageUtil.getPixelColor( _source, x, y );
					if( color != ImageUtil.BLACK_INT && color != ImageUtil.CLEAR_INT && color != ImageUtil.EMPTY_INT ) {
						newParticle( x, y, p.random(-0.01f,0.01f), p.random(0,-0.5f), color );
					}
				}				
			}
		}

		// IMAGE PROCESSING METHODS ===================================================================================
		protected void runBlobDetection( PImage source ) {
			blobBufferImg.copy(source, 0, 0, source.width, source.height, 0, 0, blobBufferImg.width, blobBufferImg.height);
			FastBlurFilter.blur(blobBufferImg, 2);
			theBlobDetection.computeBlobs(blobBufferImg.pixels);
		}

		// test 2 - mesh from outer
		protected void drawEdges(PImage source)
		{
			//		_pg.image( source, 0, 0 );

			// do edge detection
			Blob b;
			EdgeVertex eA,eB;
			for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++) {
				b=theBlobDetection.getBlob(n);
				if (b!=null) {
					for (int m=0;m<b.getEdgeNb();m++) {
						eA = b.getEdgeVertexA(m);
						eB = b.getEdgeVertexB(m);

						if (eA !=null && eB !=null) {

							float angle = -MathUtil.getAngleToTarget(eA.x, eA.y, b.x, b.y);
							float angleB = -MathUtil.getAngleToTarget(eB.x, eB.y, b.x, b.y);
							float distance = MathUtil.getDistance(b.x, b.y, eA.x, eA.y) * 1f;
							float distanceB = MathUtil.getDistance(b.x, b.y, eB.x, eB.y) * 1f;

							float outerX = eA.x + P.sin( MathUtil.degreesToRadians(angle) )*distance;
							float outerY = eA.y + P.cos( MathUtil.degreesToRadians(angle) )*distance;
							float outerXB = eB.x + P.sin( MathUtil.degreesToRadians(angleB) )*distanceB;
							float outerYB = eB.y + P.cos( MathUtil.degreesToRadians(angleB) )*distanceB;

							// draw inner lines for debugging
							//						_pg.beginDraw();
							//						_pg.stroke(0,127);
							//						_pg.line(eA.x*_width, eA.y*_height, eB.x*_width, eB.y*_height);
							//						_pg.noStroke();
							//						_pg.endDraw();


							int color = ImageUtil.getPixelColor( source, P.round(eA.x*source.width-1), P.round(eA.y*source.height-1) );
							_pg.fill( color, 127 );
							float bright = p.brightness(color);

							newParticle( 
									eA.x*_width + p.random(-3f,2f), 
									eA.y*_height + p.random(-1f,1f), 
									outerX - eA.x, 
									outerY - eA.y, 
									color 
									);

						}
					}
				}
			}
			//		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
		}
		
		protected void drawParticles() {
			// update particles
			BlobParticle particle;
			int particlesLen = _particles.size() - 1;
			for( int i = particlesLen; i > 0; i-- ) {
				particle = _particles.get(i);
				if( particle.active == true ) {
					particle.update();
				} else {
					_particles.remove(i);
					_inactiveParticles.add(particle);
				}
			}
			//		P.println("Active particles: "+_particles.size()+"  available: "+_inactiveParticles.size());
		}

		protected void newParticle( float x, float y, float speedX, float speedY, int color ) {
			BlobParticle particle;
			if( _inactiveParticles.size() > 0 ) {
				particle = _inactiveParticles.remove( _inactiveParticles.size() - 1 );
			} else {
				particle = new BlobParticle();
			}
			particle.startAt( x, y, speedX, speedY, color );
			_particles.add( particle );
		}






		public class BlobParticle {

			PVector _position = new PVector();
			PVector _speed = new PVector();
			PVector _gravity = new PVector(0,-0.07f);
			int _color = 0;
			float _opacity = 1;
			float _opacityFadeSpeed = -0.01f;
			public boolean active = false;
			protected int _audioIndex;
			float _baseSize = ((float)p.height/720f) * 2.1f;

			public BlobParticle() {

			}

			public void startAt( float x, float y, float speedX, float speedY, int color ) {
				_position.set( x, y );
				_speed.set( speedX * 15 * p.random(2f) + p.random(-0.2f,0.2f), speedY * 5 * p.random(3f) );	// add a little extra x variance
				_speed.mult( 1 + p.audioFreq(_audioIndex) ); // speed multiplied by audio
				_color = color;
				_opacity = 0.8f;
				
				// special fade-out on bottom 1/4 of the screen
				if(y > p.height * 0.75f) {
					float opacitySub = MathUtil.getPercentWithinRange(p.height * 0.75f, p.height, y);//(p.mouseY, p.height * 0.75f, p.height, 0, 1f);
					_opacity -= opacitySub * 0.8f;
				}
				
				_opacityFadeSpeed = p.random(50f, 500f) / 10000f; // 0.005 - 0.05
				active = true;
				_audioIndex = MathUtil.randRange(0, 511);
			}

			public void update() {
				_position.add( _speed );
				_speed.add( _gravity );
				_speed.set( _speed.x * 0.98f, P.constrain( _speed.y, -5f, 5f )  );

				_opacity -= _opacityFadeSpeed;
				if( _opacity <= 0 ) {
					active = false;
				} else {
					_pg.fill( _color, 127f * _opacity );
					_pg.noStroke();
					float size = _baseSize + (p.audioFreq(_audioIndex) * 5f); // was 3
//					_pg.rect(_position.x - size/2f, _position.y - size/2f, size, size);
					_pg.ellipse(_position.x - size/2f, _position.y - size/2f, size, size);
				}
			}
		}

	}


}

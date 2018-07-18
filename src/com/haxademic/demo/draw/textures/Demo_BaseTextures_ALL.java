package com.haxademic.demo.draw.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.TextureShaderTimeStepper;
import com.haxademic.core.draw.textures.pgraphics.TextureSvg3dExtruded;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.midi.devices.AbletonNotes;
import com.haxademic.core.hardware.midi.devices.AkaiMpdPads;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.devices.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;

public class Demo_BaseTextures_ALL 
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	protected BaseTexture[] _textures;
	int w = 500;
	int h = 300;
	float frames = 500;

	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, LaunchControl.PAD_03, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, LaunchControl.PAD_04, AbletonNotes.NOTE_02});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, LaunchControl.PAD_01, AbletonNotes.NOTE_03});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, LaunchControl.PAD_05, AbletonNotes.NOTE_04});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, LaunchControl.PAD_02, AbletonNotes.NOTE_05});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, LaunchControl.PAD_08, AbletonNotes.NOTE_07});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, LaunchControl.PAD_06, AbletonNotes.NOTE_08});


	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.WIDTH, 1500 );
//		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
//		p.appConfig.setProperty( AppSettings.INIT_BEADS_AUDIO, true );
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, true );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)frames);
	}


	public void setup() {
		super.setup();
	}
	
	protected void initObjects() {
		w = Math.round(p.width / 3f); // p.width;// 
		h = Math.round(p.height / 3f); // Math.round(w * (9f/16f));
		w = p.width; 
		h = p.height;
		
		OpenGLUtil.setTextureRepeat(g);
		
		_textures = new BaseTexture[]{
//			new TextureShaderTimeStepper( w, h, "_drawing-stuff.glsl" ),
//			new TextureFractalPolygons( w, h ),
//			new TextureWebCam( w, h ),
//			new TextureEQFloatParticles( w, h ),
//			new TextureEQBandDistribute( w, h ),
//			new TextureOuterSphere( w, h ),
//			new TextureWaveformSimple( w, h ),
//			new TexturePixelatedAudio( w, h ),
				
//			new TextureAudioBlocksDeform( w, h ),
//			new TextureBlocksSheet( w, h ),
			new TextureSvg3dExtruded( w, h ),
//			new TextureSvgExtruded( w, h ),
			
			
//			new TextureAudioTube( w, h ),
//			new TextureTwistingSquares( w, h ),
//		    new TextureImageTimeStepper( w, h ),
//		    new TextureStarTrails( w, h ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-down-void.glsl" ),
//			new TextureSphereAudioTextures( w, h ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-diagonal-stripes.glsl" ),
//			new TextureShaderTimeStepper( w, h, "iq-voronoise.glsl" ),
//			new TextureShaderTimeStepper( w, h, "sdf-01-auto.glsl" ),
//			new TextureShaderTimeStepper( w, h, "sdf-02-auto.glsl" ),
//			new TextureMeshDeform( w, h ),
//			new TextureMeshDeform( w, h ),
//			new TextureTwistingSquares( w, h ),
//			new TextureVectorFieldEQ( w, h ),
//			new TextureShaderTimeStepper( w, h, "bw-scroll-rows.glsl" ),
//			new TextureShaderTimeStepper( w, h, "light-leak.glsl" ),
//			new TextureShaderTimeStepper( w, h, "primitives-2d.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-tiled-moire.glsl" ),
//			new TextureShaderTimeStepper( w, h, "shiny-circle-wave.glsl" ),
//			new TextureShaderTimeStepper( w, h, "radial-waves.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-checkerboard-stairs.glsl" ),
//			new TextureShaderTimeStepper( w, h, "sin-waves.glsl" ),
//			new TextureShaderTimeStepper( w, h, "gradient-line.glsl" ),
//			new TextureShaderTimeStepper( w, h, "lines-scroll-diag.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-squound-tunnel.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-grid-noise-warp.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-liquid-moire.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-liquid-moire-camo-alt.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-checkerboard-stairs.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-folded-wrapping-paper.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-concentric-plasma.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-scrolling-radial-twist.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-warp-vortex.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-distance-blobs.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-metaballs.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-drunken-holodeck.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-triangle-wobble-stairs.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-stripe-waves.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-concentric-hex-lines.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-scrolling-dashed-lines.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-op-wavy-rotate.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-repeating-circles.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-dots-on-planes.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-asterisk-wave.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cacheflowe-concentric-hypno-lines.glsl" ),
//			new TextureShaderTimeStepper( w, h, "swirl.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-dazzle-voronoi.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-radial-wave.glsl" ),
//			new TextureShaderTimeStepper( w, h, "supershape-2d.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-waves.glsl" ),
//			new TextureShaderTimeStepper( w, h, "cubert.glsl" ),
//			new TextureShaderTimeStepper( w, h, "sdf-04-better.glsl" ),
//			new TextureShaderTimeStepper( w, h, "sdf-02.glsl" ),
//			new TextureShaderTimeStepper( w, h, "morphing-bokeh-shape.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-motion-illusion.glsl" ),
//			new TextureShaderTimeStepper( w, h, "light-leak.glsl" ),
//
//			new TextureShaderTimeStepper( w, h, "wobble-sin.glsl" ),
//			new TextureShaderTimeStepper( w, h, "docking-tunnel.glsl" ),
//			new TextureShaderTimeStepper( w, h, "shiny-circle-wave.glsl" ),
//			new TextureShaderTimeStepper( w, h, "stars-nice.glsl" ),
//			new TextureShaderTimeStepper( w, h, "triangle-perlin.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-circles.glsl" ),
//			new TextureShaderTimeStepper( w, h, "dot-grid-dof.glsl" ),
//			new TextureShaderTimeStepper( w, h, "flame-wisps.glsl" ),
//
//			new TextureShaderTimeStepper( w, h, "bubbles-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "spinning-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "inversion-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "radial-waves.glsl" ),
//			new TextureShaderTimeStepper( w, h, "radial-burst.glsl" ),
//			new TextureShaderTimeStepper( w, h, "wavy-3d-tubes.glsl" ),
//			new TextureShaderTimeStepper( w, h, "fade-dots.glsl" ),
//			new TextureBlobSheet( w, h ),
//			new TextureOuterSphere( w, h ),
//			new TextureSvgPattern( w, h ),
//			new TextureWaveformCircle( w, h ),
//			new TextureRotatingRings( w, h ),
//			new TextureLinesEQ( w, h ),
//			new TextureBarsEQ( w, h ),
//			new TextureEQConcentricCircles( w, h ),
			new TextureEQGrid( w, h ),
//			new TextureWaveformSimple( w, h ),
//			new TextureAppFrameEq2d( w, h ),
//			new TextureAppFrame2d( w, h ),
//			new TextureAppFrameWaveformCircle( w, h ),
//			new TextureBasicWindowShade( w, h ),
//			new TextureRotatorShape( w, h ),
//			new TextureWaveformCircle( w, h ),

//			new TextureMeshAudioDeform( w, h ),
//		    new TextureColorAudioSlide( w, h ),
//		    new TextureSphereRotate( w, h ),
//		    new TextureShaderScrubber( w, h, "cog-tunnel.glsl" ),
//			new TextureVideoPlayer( w, h, "video/cacheflowe/render-2015-04-24-11-06-26-3x (Converted 2).mov" ),
//			new TextureVideoPlayer( w, h, "video/cacheflowe/render-2015-07-28-10-03-01-desktop.m4v" ),
//		    new TextureShaderTimeStepper( w, h, "cog-tunnel.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "space-swirl.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "matrix-rain.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "water-smoke.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "square-fade.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "gradient-line.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "stars-scroll.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "square-twist.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "hex-alphanumerics.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "bw-eye-jacker-02.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "bw-expand-loop.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "bw-clouds.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "circle-parts-rotate.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "warped-tunnel.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "stars-fractal-field.glsl" ),
//			new TextureShaderTimeStepper( w, h, "morphing-bokeh-shape.glsl" ),
//			new TextureShaderTimeStepper( w, h, "basic-diagonal-stripes.glsl" ),
//			new TextureShaderTimeStepper( w, h, "basic-checker.glsl" ),
//			new TextureShaderTimeStepper( w, h, "water-smoke.glsl" ),
//			new TextureShaderTimeStepper( w, h, "flexi-spiral.glsl" ),
//			new TextureShaderTimeStepper( w, h, "noise-function.glsl" ),
//			new TextureShaderTimeStepper( w, h, "noise-simplex-2d-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "light-leak.glsl" )
		};
		
		for(BaseTexture tex : _textures) {
			tex.setActive(true);
		}

		
//		_texture = new TextureWebCam();
	}

	public void drawApp() {
		if(p.frameCount == 1) initObjects();
		float frameInc = P.TWO_PI / frames;
		background(40);
		
		simulateMidiAndBeats();
		
		int x = 0;
		int y = 0;
		for (int i = 0; i < _textures.length; i++) {
			BaseTexture tex = _textures[i];
//			tex.update();
			if(tex.getClass().getName() == TextureShaderTimeStepper.class.getName()) {
				((TextureShaderTimeStepper) tex).updateDrawWithTime(p.frameCount * frameInc);
			} else {
				tex.update();
			}
//			LeaveBlackFilter.instance(p).setMix(P.map(p.mouseX, 0, p.width, 0f, 1f));
//			LeaveBlackFilter.instance(p).applyTo(tex.texture()); // test filter
			p.image( tex.texture(), x, y );
			
			x += w;
			if(x + w > p.width) {
				x = 0;
				y += h;
			}
		}
		
//		postProcessForRendering();
//		SaturationFilter.instance(p).setSaturation(2.5f);
//		SaturationFilter.instance(p).applyTo(p);
//		FXAAFilter.instance(p).applyTo(p);
	}
	
	protected void simulateMidiAndBeats() {
		if(p.frameCount % 45 == 0 || _timingTrigger.triggered()) {
			for(BaseTexture tex : _textures) {
				tex.updateTiming();
			}
		}
		if(p.frameCount % 220 == 0 || _timingSectionTrigger.triggered()) {
			for(BaseTexture tex : _textures) {
				tex.updateTimingSection();
			}
			for(BaseTexture tex : _textures) {
				tex.setActive(false);
				tex.setActive(true);
			}
		}
		if(p.frameCount % 60 == 0 || _colorTrigger.triggered()) {
			for(BaseTexture tex : _textures) {
				tex.setColor(p.color(p.random(0, 255), p.random(0, 255), p.random(0, 255)));
			}
		}
		if(p.frameCount % 180 == 0 || _lineModeTrigger.triggered()) {
			for(BaseTexture tex : _textures) {
				tex.newLineMode();
			}
		}
		if(p.frameCount % 250 == 0 || _modeTrigger.triggered()) {
			for(BaseTexture tex : _textures) {
				tex.newMode();
			}
		}
		if(p.frameCount % 75 == 0 || _rotationTrigger.triggered()) {
			for(BaseTexture tex : _textures) {
				tex.newRotation();
			}
		}
	}
	
	protected void postProcessForRendering() {
		DrawUtil.fadeInOut(p.g, p.color(0), 1, 400, 50);
		
//		float time = p.millis() / 10000f;
//		ColorDistortionFilter.instance(p).setTime(time);
//		ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(time/10f));
//		ColorDistortionFilter.instance(p).applyTo(p);
//		VignetteFilter.instance(p).applyTo(p);
	}

}
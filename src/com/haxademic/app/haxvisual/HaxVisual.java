package com.haxademic.app.haxvisual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxvisual.pools.HaxVisualTexturePools;
import com.haxademic.app.interphase.sequencing.Interphase;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.FlipHFilter;
import com.haxademic.core.draw.filters.pshader.GlitchImageGlitcherFilter;
import com.haxademic.core.draw.filters.pshader.GlitchPseudoPixelSortingFilter;
import com.haxademic.core.draw.filters.pshader.GlitchShaderAFilter;
import com.haxademic.core.draw.filters.pshader.GlitchShakeFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneCamoFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneFilter;
import com.haxademic.core.draw.filters.pshader.HueFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.LeaveBlackFilter;
import com.haxademic.core.draw.filters.pshader.LiquidWarpFilter;
import com.haxademic.core.draw.filters.pshader.MaskThreeTextureFilter;
import com.haxademic.core.draw.filters.pshader.MirrorQuadFilter;
import com.haxademic.core.draw.filters.pshader.Pixelate2Filter;
import com.haxademic.core.draw.filters.pshader.PixelateFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.filters.pshader.RepeatFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SphereDistortionFilter;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.image.ImageCyclerBuffer;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.devices.AbletonNotes;
import com.haxademic.core.hardware.midi.devices.AkaiMpdPads;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.devices.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UIButton;

import processing.core.PGraphics;
import processing.core.PImage;


public class HaxVisual
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// BEAT TRIGGER TIMING

	protected float BEAT_DIVISOR = 1; // 10 to test, 1 by default
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_LINE_MODE = (int) Math.ceil(13f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_MODE = (int) Math.ceil(80f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(10f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(120f / BEAT_DIVISOR);

	// TEXTURE POOLS

	protected ArrayList<BaseTexture> bgTexturePool;
	protected ArrayList<BaseTexture> fgTexturePool;
	protected ArrayList<BaseTexture> audioTexturePool;
	protected ArrayList<BaseTexture> topLayerPool;
	protected ArrayList<BaseTexture>[] texturePools;
	protected ArrayList<BaseTexture> _curTexturePool;
	protected int layerSwapIndex = 0;	// which texture pool to swap on next big change trigger (x/4)
	protected int[] poolCurTextureIndexes;

	// TEXTURE DEBUG

	protected boolean _debugTextures = false;
	protected boolean DEBUG_TEXTURE_SAVE_IMAGE_PREVIEWS = false;

	// MIDI CONFIG

	protected int midiInChannel = 0;
	protected int displaceMapLayerKnob = 21;
	protected int overlayModeKnob = 41;
	protected int invertKnob = 22;
	protected int vignetteKnob = 42;
	protected int distAmpKnob = 23;
	protected int distTimeKnob = 43;
	protected int warpKnobAmp = 44;
	protected int warpKnobFreq = 24;
	protected int kaledioKnob = 25;
	protected int effectsKnob = 46;
	protected int pixelateKnob = 26;
	protected int contrastKnob = 28;
	protected int brightnessKnob = 48;
	protected int interstitialKnob = 47;

	// MULTI-INPUT CONFIG

	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, LaunchControl.PAD_03, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, LaunchControl.PAD_04, AbletonNotes.NOTE_02});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, LaunchControl.PAD_01, AbletonNotes.NOTE_03});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, LaunchControl.PAD_05, AbletonNotes.NOTE_04});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, LaunchControl.PAD_02, AbletonNotes.NOTE_05});
	//	protected InputTrigger _allSameTextureTrigger = new InputTrigger(new char[]{'a'},new String[]{TouchOscPads.PAD_06},new Integer[]{AkaiMpdPads.PAD_06, AbletonNotes.NOTE_06});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, LaunchControl.PAD_08, AbletonNotes.NOTE_07});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, LaunchControl.PAD_06, AbletonNotes.NOTE_08});

	// input debug
	float debugEaseInc = 0.1f;
	protected LinearFloat colorTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat rotationTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat timingTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat timingSectionTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat modeTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat lineModeTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat bigChangeTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat[] triggerDebugLinearFloats = new LinearFloat[] { colorTriggerIndicator, rotationTriggerIndicator, timingTriggerIndicator, modeTriggerIndicator, timingSectionTriggerIndicator, bigChangeTriggerIndicator, lineModeTriggerIndicator };
	
	// extra controls
	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{26});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{25});
	protected InputTrigger _brightnessUpTrigger = new InputTrigger(new char[]{']'},new String[]{},new Integer[]{});
	protected InputTrigger _brightnessDownTrigger = new InputTrigger(new char[]{'['},new String[]{},new Integer[]{});
	protected InputTrigger _keystoneResetTrigger = new InputTrigger(new char[]{'k'},new String[]{},new Integer[]{});
	protected InputTrigger _debugTexturesTrigger = new InputTrigger(new char[]{'d'},new String[]{},new Integer[]{});

	// USER INPUT OVERRIDE/TIMEOUT

	protected int _lastInputMillis = 0;
	protected int numBeatsDetected = 0;
	protected int lastTimingUpdateTime = 0;
	protected String TIMING_MIN_DELAY = "TIMING_MIN_DELAY";

	// DMX LIGHTS

	protected RandomLightTiming _dmxLights;

	// COLORIZE COMPOSITION

	protected PGraphics colorizeSourceTexture;
	protected ImageGradient imageGradient;
	protected boolean colorizeWithGradient = true;
	protected boolean imageGradientLuma = false;

	// DISPLACEMENT LAYER

	protected int displacementTextureIndex = 0;
	protected int overlayMode = 0;
	protected float brightnessVal = 1f;
	protected PGraphics displacementBlurBuffer;

	// GLITCH EFFECTS
	protected LinearFloat glitchProgress = new LinearFloat(0, 0.015f);
	protected GlitchMode glitchMode;	
	
	// PER-TEXTURE POST EFFECTS
	protected int[] textureEffectsIndices;	// store a effects number for each texture position after the first
	protected int numTextureEffects = 16 + 8; // +8 to give a good chance at removing the filter from the texture slot
	protected boolean perTextureEffects = false;

	// CORNER-PINNED BUFFER or MULTI-OUTPUT MAPPING

	protected PGraphics _pg;
	protected PGraphicsKeystone _pgPinnable;
	protected float scaleDownPG = 1f; // 0.5f;
	protected boolean multiOutput = false;
	
	// LET'S PLAY OUR OWN MUSIC

	protected Interphase interphase;
	

	//////////////////////////////////////////////////
	// INIT
	//////////////////////////////////////////////////

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.OSC_ACTIVE, false );
		//		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, true );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
//				p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, true );
//				p.appConfig.setProperty( AppSettings.INIT_BEADS_AUDIO, true );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.MIDI_DEBUG, false );
		p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
	}

	protected void setupFirstFrame() {
		P.store.addListener(this);
//		initDMX();
		if(multiOutput == false) {
			buildCanvas();
		} else {
			buildCanvasMultiOutput();
		}
		buildTextures();
		buildPostProcessingChain();
		// buildInterstitial();
		
		interphase = new Interphase();
	}

	protected void buildCanvas() {
		int w = P.round(p.width * scaleDownPG);
		int h = P.round(p.height * scaleDownPG);
		_pg = p.createGraphics(w, h, P.P3D);
//		_pg.noSmooth();
		OpenGLUtil.setTextureRepeat(_pg);
//		_pgPinnable = new PGraphicsKeystone( p, _pg, 12, FileUtil.getFile("text/keystoning/hax-visual-two.txt") );
	}

	// fancy mapping blended output
	
	protected int OVERLAP_PIXELS = 890;
	protected String BLEND_LEFT = "BLEND_LEFT";
	protected String BLEND_RIGHT = "BLEND_RIGHT";
	protected String BLEND_WIDTH = "BLEND_WIDTH";
	protected PGraphics fadeEdge;
	
	protected void buildCanvasMultiOutput() {
		int w = 1920 * 4 - OVERLAP_PIXELS;
		int h = P.round(1080 * 4f/3f);
		_pg = p.createGraphics(w, h, P.P3D);
		_pg.noSmooth();
		OpenGLUtil.setTextureRepeat(_pg);
		
		// add sliders for blending
		p.ui.addSlider(BLEND_LEFT, OVERLAP_PIXELS / 2, -100, 2020, 1);
		p.ui.addSlider(BLEND_RIGHT, OVERLAP_PIXELS / 2, -100, 2020, 1);
		p.ui.addSlider(BLEND_WIDTH, 100, 0, 1000, 1);
		
		fadeEdge = p.createGraphics(1920, h, P.P3D);
	}
	
	protected void buildPostProcessingChain() {
		displacementBlurBuffer = p.createGraphics(_pg.width/20, _pg.height/20, PRenderers.P3D);
		
		colorizeSourceTexture = p.createGraphics(128, 4, PRenderers.P3D);
		p.debugView.setTexture("colorizeSourceTexture", colorizeSourceTexture);
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);

		KaleidoFilter.instance(p).setAngle(0f);
		KaleidoFilter.instance(p).setSides(2f);

		HalftoneFilter.instance(p).setSizeT(256f, 256f);
		HalftoneFilter.instance(p).setAngle(P.HALF_PI);
		HalftoneFilter.instance(p).setCenter(0.5f, 0.5f);
		HalftoneFilter.instance(p).setScale(1f);

		PixelateFilter.instance(p).setDivider(20f, _pg.width, _pg.height);
		
		p.midiState.controllerChange(midiInChannel, contrastKnob, (int) 70);

		p.midiState.controllerChange(midiInChannel, vignetteKnob, (int) 70);
	}

//	protected void initDMX() {
//		if(p.appConfig.getInt(AppSettings.DMX_LIGHTS_COUNT, 0) > 0) {
//			_dmxLights = new RandomLightTiming(p.appConfig.getInt(AppSettings.DMX_LIGHTS_COUNT, 0));
//		}
//	}
	
	
	protected void buildUI() {
		p.ui.addSlider(TIMING_MIN_DELAY, 100, 50, 1000, 1, false);
	}
	
	//////////////////////////////////////////////
	// GETTERS
	//////////////////////////////////////////////

	protected BaseTexture topLayer() {
		return _curTexturePool.get(_curTexturePool.size() - 1);
	}

	protected BaseTexture audioLayer() {
		return _curTexturePool.get(_curTexturePool.size() - 2);
	}

	//////////////////////////////////////////////
	// DRAW
	//////////////////////////////////////////////

	public void drawApp() {
		if(p.frameCount == 3) buildUI(); 
		background(0);
		handleInputTriggers();
		checkBeat();
		drawPre();
		getDisplacementLayer();
		if(perTextureEffects) filterActiveTextures();
		updateTextures();
		drawLayers();
		drawAltTopLayerOrDisplacement();
		postSpecialEffectsFilters();
		drawTopLayer();
		postFinalFilters();
		if(interphase != null) interphase.update(null); // _pg
		if(imageCycler != null) drawInterstitial();
		// draw pinned pgraphics
		if(_debugTextures == true && _pgPinnable != null) _pgPinnable.drawTestPattern();
//		p.debugView.setTexture(_pg);
		if(multiOutput == false && _pgPinnable != null) _pgPinnable.update(p.g);
		else if(multiOutput == false) ImageUtil.cropFillCopyImage(_pg, p.g, false);
		else drawCanvasToMultiScreens();
		// sendDmxLights();
		runDebugHelpers();
	}

	protected void updateColorizeTexture() {
		colorizeSourceTexture.beginDraw();
		colorizeSourceTexture.noStroke();
		ImageUtil.copyImage(imageGradient.texture(), colorizeSourceTexture);
		colorizeSourceTexture.blendMode(PBlendModes.MULTIPLY);
		
		// then draw on top - replace this with a collection of audioreactive textures
		for (int i = 0; i < colorizeSourceTexture.width; i++) {
			float eqAmp = 0.3f + P.p.audioFreq(i + 20) * 20f;
			colorizeSourceTexture.fill(255 * eqAmp);
			colorizeSourceTexture.rect(i, 0, 1, colorizeSourceTexture.height);
		}

		// close context
		colorizeSourceTexture.blendMode(PBlendModes.BLEND);
		colorizeSourceTexture.endDraw();
	}
	
	protected void updateEdgeBlending() {
		if(fadeEdge != null && p.frameCount < 1000) {
			fadeEdge.beginDraw();
			fadeEdge.clear();
			fadeEdge.background(0, 0);
			PG.setDrawCenter(fadeEdge);
			fadeEdge.noStroke();
			
			// draw projector-blending gradient
			int fadeSize = (int) p.ui.value(BLEND_WIDTH);
			fadeEdge.pushMatrix();
			fadeEdge.translate((fadeSize * 0.5f), fadeEdge.height / 2);
			Gradients.linear(fadeEdge, fadeSize, fadeEdge.height, p.color(1, 0), p.color(0,255));
			fadeEdge.popMatrix();
			
			// draw solid portion
			float solidSize = fadeEdge.width - fadeSize; 
			fadeEdge.translate(fadeEdge.width - (solidSize * 0.5f), fadeEdge.height / 2);
			fadeEdge.fill(0);
			fadeEdge.rect(0, 0, solidSize, fadeEdge.height);
			fadeEdge.endDraw();
			
			// p.debugView.setTexture(fadeEdge);
		}
	}
	
	protected void drawPre() {
		updateColorizeTexture();
		updateEdgeBlending();
	}
	
	protected void updateTextures() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			BaseTexture tex = _curTexturePool.get(i);
			if(tex != null && tex.texture() != null) {
				tex.update();
			}
		}	
	}

	protected void drawLayers() {
		// composite textures
		if(overlayMode != 3 || displacementTextureIndex == 3) {	// we'll use the mask shader if 3, and no need to draw here
			_pg.beginDraw();
			_pg.background(0);
			_pg.blendMode(PBlendModes.EXCLUSION);
//			_pg.blendMode(PBlendModes.ADD);
//			_pg.blendMode(PBlendModes.LIGHTEST);
//			_pg.blendMode(PBlendModes.DARKEST);
			//		OpenGLUtil.setBlending(p.g, true);
			//		OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.DARK_INVERSE);
			for( int i=0; i < _curTexturePool.size() - 1; i++ ) {	
				if(i != displacementTextureIndex) {	// don't draw displacement layer
					BaseTexture tex = _curTexturePool.get(i);
					if(tex != null && tex.texture() != null) {
						ImageUtil.drawImageCropFill(tex.texture(), _pg, true);
					}
				}
			}
			_pg.blendMode(PBlendModes.BLEND);
			_pg.endDraw();
		}
	}

	protected void drawTopLayer() {
		_pg.beginDraw();
//		_pg.blendMode(PBlendModes.BLEND);
		_pg.blendMode(PBlendModes.ADD);
		ImageUtil.drawImageCropFill(topLayer().texture(), _pg, true);
		_pg.blendMode(PBlendModes.BLEND);
		_pg.endDraw();
	}
	
	protected void drawCanvasToMultiScreens() {
		if(p.debugView.active()) {
			// show the whole thing
			ImageUtil.cropFillCopyImage(_pg, p.g, false);
		} else {
			// draw for GG
			int outW = 1920;
			int outH = 1080;
	//		int sourceW = _pg.width / 3;
			int sourceH = _pg.height;
	
			// screen 1 - left end
			p.g.copy(_pg,   0, 0, 1920, sourceH, 
							0, 0, outW, outH);
			// screen 2-3
			p.g.copy(_pg, 	1920, 0, 1920, sourceH, 
							outW, 0, outW, outH);
			p.g.copy(_pg, 	_pg.width - 1920 * 2, 0, 1920, sourceH, 
							0, outH, outW, outH);
			
			// screen 4 - right end
			p.g.copy(_pg, 	_pg.width - 1920, 0, 1920, sourceH, 
							outW, outH, outW, outH);
			
			// screen 2-3 blending
			PG.setDrawCenter(p.g);
			p.g.image(fadeEdge, outW + outW / 2 + p.ui.value(BLEND_LEFT), outH / 2, fadeEdge.width, outH);
			
			p.pushMatrix();
			p.g.translate( -outW + p.ui.value(BLEND_RIGHT), outH + outH / 2);
			p.g.rotate(P.PI);
			p.g.image(fadeEdge, 0, 0, fadeEdge.width, outH);
			p.popMatrix();
			
			PG.setDrawCorner(p.g);
		}
	}

	/////////////////////////////////////////////////////////////////
	// POST PROCESSING EFFECTS
	/////////////////////////////////////////////////////////////////

	protected void getDisplacementLayer() {		
		displacementTextureIndex = P.round(P.map(p.midiState.midiCCPercent(midiInChannel, displaceMapLayerKnob), 0, 1, 0, 3));
		overlayMode = P.round(P.map(p.midiState.midiCCPercent(midiInChannel, overlayModeKnob), 0, 1, 0, 3));
		p.debugView.setValue("HAXVISUAL :: displacementLayer", displacementTextureIndex);
		p.debugView.setValue("HAXVISUAL :: overlayMode", overlayMode);
	}

	protected void drawAltTopLayerOrDisplacement() {	
		// DISPLACEMENT MAP ////////////////////////
		// This does special drawing modes if layers weren't drawn 
		// which layer to use for displacement?
		if(displacementTextureIndex < 3) {
			// choose displacement buffer
			if(displacementTextureIndex >= _curTexturePool.size()) displacementTextureIndex = _curTexturePool.size() - 1; // protection!
			PGraphics displacementBuffer = _curTexturePool.get(displacementTextureIndex).texture();
			
			// smooth displacement buffer
//			if(overlayMode == 0 || overlayMode == 3) {
				// add blur to displacement image
				boolean scaleByTextureResize = true;
				if(scaleByTextureResize) {
					p.debugView.setTexture("displacementBlurBuffer", displacementBlurBuffer);
					ImageUtil.copyImage(displacementBuffer, displacementBlurBuffer);			// scale down to tiny buffer
//					ImageUtil.copyImage(displacementBlurBuffer, displacementBuffer);			// instead of copying back up,
					BlendTowardsTexture.instance(p).setSourceTexture(displacementBlurBuffer);	// lerp it back up for smoothness
					BlendTowardsTexture.instance(p).setBlendLerp(0.25f);
					BlendTowardsTexture.instance(p).applyTo(displacementBuffer);
					// extra blur to smooth edges
					BlurHFilter.instance(p).setBlurByPercent(3f, displacementBuffer.width);		// then blur again for more smoothness
					BlurVFilter.instance(p).setBlurByPercent(3f, displacementBuffer.height);
					BlurHFilter.instance(p).applyTo(displacementBuffer);
					BlurVFilter.instance(p).applyTo(displacementBuffer);
				} else {
					float blurPercent = 2f; // p.mousePercentX() * 10f;
					BlurHFilter.instance(p).setBlurByPercent(blurPercent, _pg.width);
					BlurVFilter.instance(p).setBlurByPercent(blurPercent, _pg.height);
					BlurHFilter.instance(p).applyTo(displacementBuffer);
					BlurVFilter.instance(p).applyTo(displacementBuffer);
					BlurHFilter.instance(p).applyTo(displacementBuffer);
					BlurVFilter.instance(p).applyTo(displacementBuffer);
				}
//			}
			
			// apply displacement mode
			if(overlayMode == 0) {
				// DISPLACEMENT MAP FILTER
				// set current layer as displacer & apply effect
				DisplacementMapFilter.instance(p).setMap(displacementBuffer);
				DisplacementMapFilter.instance(p).setMode(3);
				DisplacementMapFilter.instance(p).applyTo(_pg);
			} else if(overlayMode == 1) {
				// OVERLAY WITH ONLY BLACK
				LeaveBlackFilter.instance(p).setCrossfade(1f);
				LeaveBlackFilter.instance(p).applyTo(displacementBuffer);
				_pg.beginDraw();
				ImageUtil.drawImageCropFill(displacementBuffer, _pg, true);
				_pg.endDraw();
			} else if(overlayMode == 2) {
				// DRAW DISPLACEMENT BUFFER NORMALLY
				_pg.beginDraw();
				_pg.blendMode(PBlendModes.EXCLUSION);
				ImageUtil.drawImageCropFill(displacementBuffer, _pg, true);
				_pg.blendMode(PBlendModes.BLEND);
				_pg.endDraw();
			} else if(overlayMode == 3) {
				// ADD SHADER TO MASK & REVERSE MASK THE OPPOSITE 2 TEXTURES
				PGraphics tex1;
				PGraphics tex2;
				if(displacementTextureIndex == 0) { 		tex1 = _curTexturePool.get(1).texture(); tex2 = _curTexturePool.get(2).texture(); }
				else if(displacementTextureIndex == 1) { 	tex1 = _curTexturePool.get(0).texture(); tex2 = _curTexturePool.get(2).texture(); }
				else { 										tex1 = _curTexturePool.get(0).texture(); tex2 = _curTexturePool.get(1).texture(); }
				MaskThreeTextureFilter.instance(p).setMask(displacementBuffer);
				MaskThreeTextureFilter.instance(p).setTexture1(tex1);
				MaskThreeTextureFilter.instance(p).setTexture2(tex2);
				MaskThreeTextureFilter.instance(p).applyTo(_pg);
			}
		}
	}

	protected void postSpecialEffectsFilters() {		
		// CONTRAST ////////////////////////
//		p.midiState.controllerChange(midiInChannel, contrastKnob, (int) (0.25f * 127f));
		if( p.midiState.midiCCPercent(midiInChannel, contrastKnob) != 0 ) {
			if(p.midiState.midiCCPercent(midiInChannel, contrastKnob) > 0.1f) {
				ContrastFilter.instance(p).setContrast(p.midiState.midiCCPercent(midiInChannel, contrastKnob) * 7f);
//				if(p.mousePercentX() > 0.5f) ContrastFilter.instance(p).applyTo(_pg);

			}
		}

		// MULTIPLE EFFECTS KNOB ////////////////////////
		boolean halftone = ( p.midiState.midiCCPercent(midiInChannel, effectsKnob) > 0.25f && p.midiState.midiCCPercent(midiInChannel, effectsKnob) < 0.5f );
		if( halftone ) HalftoneFilter.instance(p).applyTo(_pg);

		boolean edged = ( p.midiState.midiCCPercent(midiInChannel, effectsKnob) > 0.5f && p.midiState.midiCCPercent(midiInChannel, effectsKnob) < 0.75f );
		if( edged ) EdgesFilter.instance(p).applyTo(_pg);

		boolean pixelated = ( p.midiState.midiCCPercent(midiInChannel, effectsKnob) > 0.75f );
		if( pixelated ) {
			float pixAmout = P.round(p.midiState.midiCCPercent(midiInChannel, pixelateKnob) * 40f);
			PixelateFilter.instance(p).setDivider(p.width/pixAmout, _pg.width, _pg.height);
			if(p.midiState.midiCCPercent(midiInChannel, pixelateKnob) > 0) PixelateFilter.instance(p).applyTo(_pg);
		}

		// INVERT ////////////////////////
		boolean inverted = ( p.midiState.midiCCPercent(midiInChannel, invertKnob) > 0.5f );
		if( inverted ) InvertFilter.instance(p).applyTo(_pg);

		// COLOR DISTORTION ///////////////////////
		// color distortion auto
		int distAutoFrame = p.frameCount % 6000;
		float distFrames = 100f;
		if(distAutoFrame <= distFrames) {
			float distAmpAuto = P.sin(distAutoFrame/distFrames * P.PI);
			p.midiState.controllerChange(0, distAmpKnob, P.round(127 * distAmpAuto));
			p.midiState.controllerChange(0, distTimeKnob, P.round(127 * distAmpAuto));
		}

		// color distortion
		float colorDistortionAmp = p.midiState.midiCCPercent(midiInChannel, distAmpKnob) * 2.5f;
		float colorDistortionTimeMult = p.midiState.midiCCPercent(midiInChannel, distTimeKnob);
		if(colorDistortionAmp > 0) {
			float prevTime = ColorDistortionFilter.instance(p).getTime();
			ColorDistortionFilter.instance(p).setTime(prevTime + 1/100f * colorDistortionTimeMult);
			ColorDistortionFilter.instance(p).setAmplitude(colorDistortionAmp);
			ColorDistortionFilter.instance(p).applyTo(_pg);
		}

		// WARP /////////////////////////
		float warpAmp = p.midiState.midiCCPercent(midiInChannel, warpKnobAmp) * 0.1f;
		float warpFreq = p.midiState.midiCCPercent(midiInChannel, warpKnobFreq) * 10f;
		if(warpAmp > 0) {
			LiquidWarpFilter.instance(p).setAmplitude(warpAmp);
			LiquidWarpFilter.instance(p).setFrequency(warpFreq);
			LiquidWarpFilter.instance(p).setTime(p.frameCount / 40f);
			LiquidWarpFilter.instance(p).applyTo(_pg);
		}
		
		// GLITCH
		doGlitchEffect(_pg);

		// KALEIDOSCOPE ////////////////////////
		float kaleidoSides = P.round( p.midiState.midiCCPercent(midiInChannel, kaledioKnob) * 12f );
		p.debugView.setValue("HAXVISUAL :: kaleidoSides", kaleidoSides);
		if( kaleidoSides > 0 ) {
			if( kaleidoSides == 1 ) {
				MirrorQuadFilter.instance(p).setZoom(0.5f);// + 0.1f * P.sin(p.frameCount * 0.01f));
				MirrorQuadFilter.instance(p).applyTo(_pg);
			} else if( kaleidoSides == 3 ) {
				ReflectFilter.instance(p).applyTo(_pg);
			} else {
				KaleidoFilter.instance(p).setAngle(0f);
				KaleidoFilter.instance(p).setSides(kaleidoSides);
				KaleidoFilter.instance(p).applyTo(_pg);
			}
		}
	}
	
	protected void postFinalFilters() {
		applyColorizeFilter();
		// bloomFilter();
//		fakeLightFilter();
		vignetteFilter();
		postBrightness();	
	}

	protected void applyColorizeFilter() {
		if(colorizeWithGradient) {
			ColorizeFromTexture.instance(p).setTexture(colorizeSourceTexture);
			ColorizeFromTexture.instance(p).setLumaMult(false);
			ColorizeFromTexture.instance(p).setCrossfade(0.75f); // p.mousePercentX());
			ColorizeFromTexture.instance(p).applyTo(_pg);
		}	
	}
	
	protected void bloomFilter() {
		BloomFilter.instance(p).setStrength(1f);
		BloomFilter.instance(p).setBlurIterations(4);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(_pg);

		GodRays.instance(p).setDecay(0.5f);
		GodRays.instance(p).setWeight(0.5f);
		GodRays.instance(p).setRotation(P.sin(p.frameCount * 0.1f));
		GodRays.instance(p).setAmp(0.5f + 0.5f * P.sin(p.frameCount * 0.1f));
		GodRays.instance(p).applyTo(_pg);
	}

	protected void fakeLightFilter() {
		FakeLightingFilter.instance(p).setAmbient(2f);
		FakeLightingFilter.instance(p).setGradAmp(0.66f);
		FakeLightingFilter.instance(p).setGradBlur(1f);
		FakeLightingFilter.instance(p).setSpecAmp(1.25f);
		FakeLightingFilter.instance(p).setDiffDark(0.5f);
		FakeLightingFilter.instance(p).applyTo(_pg);
	}
	
	protected void vignetteFilter() {
		// VIGNETTE FROM CENTER ////////////////////////
		float vignetteVal = p.midiState.midiCCPercent(midiInChannel, vignetteKnob);
		float vignetteDarkness = P.map(vignetteVal, 0, 1, 13f, -13f);
		VignetteAltFilter.instance(p).setSpread(0.5f);
		VignetteAltFilter.instance(p).setDarkness(1f); // vignetteDarkness
//		VignetteAltFilter.instance(p).applyTo(_pg);

		// normal vignette
		VignetteFilter.instance(p).setDarkness(0.6f);
		VignetteFilter.instance(p).applyTo(_pg);
	}

	protected void postBrightness() {
		if(p.midiState.midiCCPercent(midiInChannel, brightnessKnob) != 0) brightnessVal = p.midiState.midiCCPercent(midiInChannel, brightnessKnob) * 5f;
		BrightnessFilter.instance(p).setBrightness(brightnessVal);
		BrightnessFilter.instance(p).applyTo(_pg);	
	}
	
	//////////////////////////
	// GLITCH STUFF
	//////////////////////////
	
	enum GlitchMode {
		Pixelate2,
		ShaderA,
		PixelSorting,
		Shake,
		ImageGlitcher,
		Invert,
		HFlip,
		Edges,
		Repeat,
		Mirror,
		ColorDistortion,
//		BadTV,
		BadTV2,
		Grain,
		Slide,
//		OrangeSweep,
	};
	
	// funky enum randomization helpers
	private static final List<GlitchMode> VALUES = Collections.unmodifiableList(Arrays.asList(GlitchMode.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	public static GlitchMode randomGlitchMode()  {
	  return VALUES.get(RANDOM.nextInt(SIZE));
	}
	
	protected void doGlitchEffect(PGraphics buffer) {
		// update glitch progress
		glitchProgress.update();
		boolean isGlitching = glitchProgress.value() > 0 && glitchProgress.value() < 1;
		p.debugView.setValue("HAXVISUAL :: isGlitching", isGlitching);
		if(isGlitching == false) return;
		float progressInverse = 1f - glitchProgress.value();
		float shaderTime = p.frameCount * 0.01f;
		float shaderTimeStepped = P.floor(p.frameCount/5) * 0.01f;

		// apply glitchy filters to buffer 
		switch (glitchMode) {
			case Pixelate2:
				Pixelate2Filter.instance(p).setDivider(10f * progressInverse);
				Pixelate2Filter.instance(p).applyTo(buffer);
				break;

			case ShaderA:
				GlitchShaderAFilter.instance(p).setTime(shaderTimeStepped);
				GlitchShaderAFilter.instance(p).setAmp(P.constrain(progressInverse, 0.1f, 1f));
				GlitchShaderAFilter.instance(p).applyTo(buffer);
				break;
	
			case PixelSorting:
				GlitchPseudoPixelSortingFilter.instance(p).setThresholdThresholdsCurved(progressInverse);
				GlitchPseudoPixelSortingFilter.instance(p).applyTo(buffer);
				break;
				
			case Shake:
				GlitchShakeFilter.instance(p).setTime(shaderTimeStepped);
				GlitchShakeFilter.instance(p).setGlitchSpeed(0.4f);	 				// config?
				GlitchShakeFilter.instance(p).setAmp(progressInverse);
				GlitchShakeFilter.instance(p).setCrossfade(1f);
				GlitchShakeFilter.instance(p).setSubdivide1(64f);	 				// config?
				GlitchShakeFilter.instance(p).setSubdivide2(64f);	 				// config?
				GlitchShakeFilter.instance(p).applyTo(buffer);
				break;
				
			case ImageGlitcher:
				GlitchImageGlitcherFilter.instance(p).setTime(shaderTimeStepped);
				GlitchImageGlitcherFilter.instance(p).setAmp(progressInverse);
				GlitchImageGlitcherFilter.instance(p).setCrossfade(1f);
				GlitchImageGlitcherFilter.instance(p).setColorSeparation(true);
				GlitchImageGlitcherFilter.instance(p).setBarSize(0.5f);	 			// config?
				GlitchImageGlitcherFilter.instance(p).setGlitchSpeed(0.5f);			// config?
				GlitchImageGlitcherFilter.instance(p).setNumSlices(20f);			// config?
				GlitchImageGlitcherFilter.instance(p).applyTo(buffer);
				break;
				
			case Invert:
				InvertFilter.instance(p).applyTo(buffer);
				break;

			case HFlip:
				FlipHFilter.instance(p).applyTo(buffer);
				break;
				
			case Edges:
				EdgesFilter.instance(p).applyTo(buffer);
				break;
				
			case Repeat:
				float progressInverseEased = Penner.easeInExpo(progressInverse, 0, 1, 1);
				RepeatFilter.instance(p).setZoom(1f + 5f * progressInverseEased);
				RepeatFilter.instance(p).applyTo(buffer);
				break;

			case Mirror:
				ReflectFilter.instance(p).applyTo(buffer);
				break;
			
			case ColorDistortion:
				ColorDistortionFilter.instance(p).setAmplitude(progressInverse);
				ColorDistortionFilter.instance(p).setTime(shaderTime * 1f);
				ColorDistortionFilter.instance(p).applyTo(buffer);
				break;
				
			case BadTV2:
				BadTVLinesFilter.instance(P.p).setTime(shaderTime);
				BadTVLinesFilter.instance(P.p).setGrayscale(0);
				BadTVLinesFilter.instance(P.p).setIntensityN(progressInverse);
				BadTVLinesFilter.instance(P.p).setIntensityS(progressInverse);
				BadTVLinesFilter.instance(P.p).setCountS(4096.0f / 1f);
				BadTVLinesFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Grain:
				GrainFilter.instance(p).setTime(shaderTime);
				GrainFilter.instance(p).setCrossfade(progressInverse * 0.5f);
				GrainFilter.instance(p).applyTo(buffer);
				break;
				
			case Slide:
				float xSlide = progressInverse * (-1f + 2f * p.noise(p.frameCount * 0.001f));
				float ySlide = progressInverse * (-1f + 2f * p.noise((p.frameCount + 10000) * 0.001f));
				RepeatFilter.instance(p).setOffset(xSlide, ySlide);
				RepeatFilter.instance(p).applyTo(buffer);
				break;
				
//			case OrangeSweep:
//				LumaColorReplaceFilter.instance(p).setTargetColor(1f, 0.274f, 0.023f, 1f);
//				LumaColorReplaceFilter.instance(p).setDiffRange(0.1f);
//				// LumaColorReplaceFilter.instance(p).setLumaTarget(3f * progressInverse - 1f);	// slide from 2 -> -1
//				LumaColorReplaceFilter.instance(p).setLumaTarget(progressInverse);
//				LumaColorReplaceFilter.instance(p).applyTo(buffer);
//				break;
				
			default:
				// P.out("No glitch filter selected!");
				break;
		}
	}
	
	public void startGlitchMode(GlitchMode newGlitchMode) {
		// reset glitch progress
		glitchProgress.setCurrent(0);
		glitchProgress.setTarget(1);
		
		// select a glitch mode
		glitchMode = newGlitchMode;
		
		// TODO: effect-specific configuration
		// TODO: change glitchProgress increment for different speeds
		glitchProgress.setInc(0.02f);
	}

	protected void startGlitchMode() {
		startGlitchMode(randomGlitchMode());
		// startGlitchMode(GlitchMode.Slide);
	}


	/////////////////////////////////////////////////////////////////
	// BEAT DETECTION 
	/////////////////////////////////////////////////////////////////

	protected void checkBeat() {
		if( p.audioData.isBeat() == true && isBeatDetectMode() == true && interphase == null ) {
			updateTiming();
		}
	}

	protected boolean isBeatDetectMode() {
		return ( p.millis() > _lastInputMillis + 10000 );
	}

	public void resetBeatDetectMode() {
		_lastInputMillis = p.millis();
		//		numBeatsDetected = 1;
	}

	/////////////////////////////////////////////////////////////////
	// INPUT 
	/////////////////////////////////////////////////////////////////

	public void keyPressed() {
		super.keyPressed();
		if(interphase != null) interphase.keyPressed();
	}
	
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
			resetBeatDetectMode();
			newMode();
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
			resetBeatDetectMode();
			updateTimingSection();
		}
		if ( _bigChangeTrigger.triggered() == true ) {
			resetBeatDetectMode();
			bigChangeTrigger();
		}
		//		if ( _allSameTextureTrigger.active() == true ) {
		//			resetBeatDetectMode();
		//			randomLayers();
		//		}
		if ( _audioInputUpTrigger.triggered() == true ) p.audioData.setGain(p.audioData.gain() + 0.05f);
		if ( _audioInputDownTrigger.triggered() == true ) p.audioData.setGain(p.audioData.gain() - 0.05f);
		if ( _brightnessUpTrigger.triggered() == true ) brightnessVal += 0.1f;
		if ( _brightnessDownTrigger.triggered() == true ) brightnessVal -= 0.1f;
		if ( _keystoneResetTrigger.triggered() == true && _pgPinnable != null) _pgPinnable.resetCorners();
		if ( _debugTexturesTrigger.triggered() == true ) _debugTextures = !_debugTextures;
		
		p.debugView.setValue("HAXVISUAL :: isBeatDetectMode()", isBeatDetectMode());
	}

	/////////////////////////////////////////////////////////////////
	// TIMING & PARAMETER UPDATES 
	/////////////////////////////////////////////////////////////////

	protected void newMode() {
		modeTriggerIndicator.setCurrent(1);
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newMode();
		}
	}

	protected void updateColor() {
		colorTriggerIndicator.setCurrent(1);
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).setColor( randomColor(1) );
		}
		if(colorizeWithGradient) {
			if(MathUtil.randBooleanWeighted(0.2f)) imageGradient.randomGradientTexture();
		}
		imageGradientLuma = true; // MathUtil.randBoolean();
		//		}
	}

	protected void updateLineMode() {
		lineModeTriggerIndicator.setCurrent(1);
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newLineMode();
		}
	}

	protected void updateRotation() {
		rotationTriggerIndicator.setCurrent(1);
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newRotation();
		}
	}

	protected void updateTiming() {
		timingTriggerIndicator.setCurrent(1);
		// tell all textures to update timing
		int frameDelay = (p.ui.has(TIMING_MIN_DELAY)) ? p.ui.valueInt(TIMING_MIN_DELAY) : 0;
		if(p.millis() > lastTimingUpdateTime + frameDelay) {
			for( int i=0; i < _curTexturePool.size(); i++ ) {
				_curTexturePool.get(i).updateTiming();
			}
			lastTimingUpdateTime = p.millis();
			// run beat-counted mode triggers on each beat
			autoBeatMode();
			p.debugView.setValue("HAXVISUAL :: lastTimingUpdateTime", lastTimingUpdateTime);
		}
		if(_dmxLights != null) _dmxLights.updateDmxLightsOnBeat();
	}

	protected void autoBeatMode() {
		if( isBeatDetectMode() == false ) return; 
		
		numBeatsDetected++;
		p.debugView.setValue("HAXVISUAL :: isBeatDetectMode()", isBeatDetectMode());
		p.debugView.setValue("HAXVISUAL :: numBeatsDetected", numBeatsDetected);

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
		}

		if( numBeatsDetected % BEAT_INTERVAL_LINE_MODE == 0 ) {
			//			P.println("BEAT_INTERVAL_ALL_SAME");
			updateLineMode();
		}

		if( numBeatsDetected % BEAT_INTERVAL_NEW_MODE == 0 ) {
			//			P.println("BEAT_INTERVAL_ALL_SAME");
			newMode();
		}

		if( numBeatsDetected % BEAT_INTERVAL_NEW_TIMING == 0 ) {
			//			P.println("BEAT_INTERVAL_NEW_TIMING");
			updateTimingSection();
		}

		// every 400 beats, do something bigger
		if( numBeatsDetected % BEAT_INTERVAL_BIG_CHANGE == 0 ) {
						P.println("BEAT_INTERVAL_BIG_CHANGE: ", numBeatsDetected, BEAT_INTERVAL_BIG_CHANGE, numBeatsDetected % BEAT_INTERVAL_BIG_CHANGE);
			bigChangeTrigger();
		}
	}

	protected void updateTimingSection() {
		timingSectionTriggerIndicator.setCurrent(1);
		
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).updateTimingSection();
		}

		// swap displacement filter option
		displacementTextureIndex = MathUtil.randRange(0, 3);
		overlayMode = MathUtil.randRange(0, 3);	
		p.midiState.controllerChange(midiInChannel, displaceMapLayerKnob, P.round((127f/3.1f) * displacementTextureIndex));
		p.midiState.controllerChange(midiInChannel, overlayModeKnob, P.round((127f/3.1f) * overlayMode));
		// P.println(P.round((127f/3.1f) * displacementLayer), P.round((127f/3.1f) * overlayMode));

		// change kaleido
		float kaleidoSides = MathUtil.randRangeDecimal(0, 1);
		if(kaleidoSides < 0.25f) kaleidoSides = 0;					// 0
		else if(kaleidoSides < 0.35f) kaleidoSides = 0.25f * 127f;	// 3
		else if(kaleidoSides < 0.7f) kaleidoSides = 0.1f * 127f;	// 1
		else if(kaleidoSides < 0.85f) kaleidoSides = 0.3f * 127f;	// 4
		else kaleidoSides = 0.5f * 127f;							// 6
		p.midiState.controllerChange(midiInChannel, kaledioKnob, (int) kaleidoSides);

		// start glitch mode
		startGlitchMode();
	}

	protected void bigChangeTrigger() {
		bigChangeTriggerIndicator.setCurrent(1);
		
		// swap each layer in succession, and loop around
		layerSwapIndex++;
		if(layerSwapIndex >= texturePools.length) layerSwapIndex = 0;

		// cycle through textures in pools
		poolCurTextureIndexes[layerSwapIndex] += 1;
		if(poolCurTextureIndexes[layerSwapIndex] >= texturePools[layerSwapIndex].size()) {
			poolCurTextureIndexes[layerSwapIndex] = 0;
			Collections.shuffle(texturePools[layerSwapIndex]);	// shuffle after showing all textures in pool
		}
		reloadLayers();

		// add new effects to each layer
		if(perTextureEffects) {
			selectNewActiveTextureFilters();
		}
		colorizeWithGradient = MathUtil.randBoolean();

		// debug values
		p.debugView.setValue("HAXVISUAL :: layerSwapIndex", layerSwapIndex);
		p.debugView.setValue("HAXVISUAL :: poolCurTextureIndexes", Arrays.toString(poolCurTextureIndexes));

		// make sure time steppers don't go wild
		SystemUtil.setTimeout(updateTimingCallback, 20);
	}

	protected ActionListener updateTimingCallback = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			updateTiming();
		}
	};

	/////////////////////////////////////////////////////////////////
	// COLORS?
	/////////////////////////////////////////////////////////////////

	protected int randomColor( float mult ) {
		float baseR = 180 + 55 * P.sin(p.frameCount/100);
		float baseG = 180 + 55 * P.sin(p.frameCount/120);
		float baseB = 180 + 55 * P.sin(p.frameCount/135);
		return p.color(
				(baseR + p.random(-20, 20)) * mult,
				(baseG + p.random(-20, 20)) * mult,
				(baseB + p.random(-20, 20)) * mult
				);
	}

	/////////////////////////////////////////////////////////////////
	// TEXTURE POOL MANAGEMENT
	/////////////////////////////////////////////////////////////////

	protected void buildTextures() {
		bgTexturePool = new ArrayList<BaseTexture>();
		fgTexturePool = new ArrayList<BaseTexture>();
		audioTexturePool = new ArrayList<BaseTexture>();
		topLayerPool = new ArrayList<BaseTexture>();
		texturePools = new ArrayList[]{bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool};
		_curTexturePool = new ArrayList<BaseTexture>();

//		HaxVisualTexturePools.addTexturesToPoolMinimal(_pg, bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool);
//		HaxVisualTexturePools.addTexturesToPool(_pg, bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool);
		HaxVisualTexturePools.addTexturesInterphase(_pg, bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool);
		//		HaxVisualTexturePools.addTexturesToPoolSG(_pg, bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool);
		//		HaxVisualTexturePools.addTexturesToPoolClient(_pg, bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool);

		// make sure all textures are not playing videos, etc
		for(BaseTexture tex : bgTexturePool) tex.setActive(false);
		for(BaseTexture tex : fgTexturePool) tex.setActive(false);
		for(BaseTexture tex : audioTexturePool) tex.setActive(false);
		for(BaseTexture tex : topLayerPool) tex.setActive(false);

		// randomize all pools
		Collections.shuffle(bgTexturePool);
		Collections.shuffle(fgTexturePool);
		Collections.shuffle(audioTexturePool);
		Collections.shuffle(topLayerPool);

		// build cur texture indexes and per-texture effect indexes
		poolCurTextureIndexes = new int[texturePools.length];
		for (int i = 0; i < poolCurTextureIndexes.length; i++) poolCurTextureIndexes[i] = 0;
		textureEffectsIndices = new int[texturePools.length];
		for (int i = 0; i < textureEffectsIndices.length; i++) textureEffectsIndices[i] = 0;

		// add inital textures to current array
		reloadLayers();

		// output to images
		if(DEBUG_TEXTURE_SAVE_IMAGE_PREVIEWS == true) {
			outputTestImages(bgTexturePool);
			outputTestImages(fgTexturePool);
			outputTestImages(audioTexturePool);
			outputTestImages(topLayerPool);
		}
	}

	protected void clearCurrentLayers() {
		for(BaseTexture tex : _curTexturePool) tex.setActive(false);
		for(BaseTexture tex : _curTexturePool) tex.setKnockoutBlack(false);
		//		for(BaseTexture tex : _curTexturePool) tex.setAsOverlay(false);
		// remove from debug panel
		for (int i = 0; i < _curTexturePool.size(); i++) {
			p.debugView.removeTexture("layer-"+i);
		}
		_curTexturePool.clear();
	}

	protected void reloadLayers() {
		clearCurrentLayers();

		// reload current 4 layers
		for (int i = 0; i < texturePools.length; i++) {
			_curTexturePool.add( texturePools[i].get(poolCurTextureIndexes[i]) );
			// debug info
			p.debugView.setTexture("layer-"+i, texturePools[i].get(poolCurTextureIndexes[i]).texture());
			p.debugView.setValue("HAXVISUAL :: texture "+i, texturePools[i].get(poolCurTextureIndexes[i]).toString());
		}

		// set current textures as active
		for(BaseTexture tex : _curTexturePool) { 
			tex.setActive(true); 
		}

		// tell the top layer
		topLayer().setCurTexturePool(_curTexturePool);
		p.debugView.setValue("HAXVISUAL :: _curTexturePool.size()", _curTexturePool.size());
	}

	/////////////////////////////////////////////////////////////////
	// TEXTURE-SPECIFIC POST-PROCESSING
	/////////////////////////////////////////////////////////////////

	protected void selectNewActiveTextureFilters() {
		for(int i=0; i < textureEffectsIndices.length; i++) {
			if(MathUtil.randBooleanWeighted(0.2f)) {
				textureEffectsIndices[i] = MathUtil.randRange(0, numTextureEffects);
			} else {
				textureEffectsIndices[i] = 0;
			}
		}
		p.debugView.setValue("HAXVISUAL :: textureEffectsIndices", Arrays.toString(textureEffectsIndices));
	}

	protected void filterActiveTextures() {
		// loop through textures and apply per-texture effect via `textureEffectsIndices`
		for( int i=0; i < _curTexturePool.size() - 1; i++ ) {
			if(_curTexturePool.get(i).isActive() == true) {
				PGraphics pg = _curTexturePool.get(i).texture();
				applyFilterToTexture(pg, i);
			}
		}

		// set kaleido on audio layer
		PGraphics audioLayer = _curTexturePool.get(_curTexturePool.size() - 2).texture();
		KaleidoFilter.instance(p).setAngle(0f);
		KaleidoFilter.instance(p).setSides(6f);
		KaleidoFilter.instance(p).applyTo(audioLayer);
	}

	public void applyFilterToTexture(PGraphics pg, int effectIndex) {
		float filterTime = p.frameCount / 40f;

		int textureEffectIndex = textureEffectsIndices[effectIndex];
		if(textureEffectIndex == 1) {
			KaleidoFilter.instance(p).setAngle(filterTime / 10f);
			KaleidoFilter.instance(p).setSides(4);
			KaleidoFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 2) {
			MirrorQuadFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 3) {
			EdgesFilter.instance(p).applyTo(pg);
			// smooth out edges with a blur
			BlurProcessingFilter.instance(p).setBlurSize(3);
			BlurProcessingFilter.instance(p).setSigma(2f);
			BlurProcessingFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 4) {
			ReflectFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 5) {
			WobbleFilter.instance(p).setTime(filterTime);
			WobbleFilter.instance(p).setSpeed(0.5f);
			WobbleFilter.instance(p).setStrength(0.0004f);
			WobbleFilter.instance(p).setSize( 200f);
			WobbleFilter.instance(p).applyTo(pg);
			//			} else if(textureEffectIndex == 6) {
			//				InvertFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 7) {
			RadialRipplesFilter.instance(p).setTime(filterTime);
			RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(filterTime));
			RadialRipplesFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 8) {
			BadTVLinesFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 10) {
			CubicLensDistortionFilterOscillate.instance(p).setTime(filterTime);
			CubicLensDistortionFilterOscillate.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 11) {
			SphereDistortionFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 12) {
			HalftoneFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 13) {
			PixelateFilter.instance(p).setDivider(15f, pg.width, pg.height);
			PixelateFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 14) {
			HalftoneCamoFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 15) {
			RotateFilter.instance(p).setRotation(effectIndex + filterTime / 10f);
			RotateFilter.instance(p).setZoom(2f + P.sin(effectIndex + p.frameCount * 0.05f));
			RotateFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 16) {
			HueFilter.instance(p).setTime(filterTime);
			HueFilter.instance(p).applyTo(pg);
		}
	}

	/////////////////////////////////////////////////////////////////
	// SPECIAL INTERSTITIAL MODE 
	/////////////////////////////////////////////////////////////////

	protected ImageCyclerBuffer imageCycler;
	protected void buildInterstitial() {
		String imagesPath = "images/_sketch/glissline-interstitials/";
		ArrayList<String> imageFiles = FileUtil.getFilesInDirOfType(FileUtil.getFile(imagesPath), "jpg");
		PImage[] images = new PImage[imageFiles.size()];
		for (int i = 0; i < imageFiles.size(); i++) {
			images[i] = p.loadImage(imagesPath + imageFiles.get(i));
			P.println(imageFiles.get(i));
		}
		imageCycler = new ImageCyclerBuffer(1398, 1080, images, 500, 0.5f);
	}

	protected void drawInterstitial() {
		float interstitialAlpha = (p.midiState.midiCCPercent(midiInChannel, interstitialKnob) != 0) ? p.midiState.midiCCPercent(midiInChannel, interstitialKnob) : 0;
		if(interstitialAlpha > 0) {
			imageCycler.update();
			_pg.beginDraw();
			PG.setPImageAlpha(_pg, interstitialAlpha);
			ImageUtil.drawImageCropFill(imageCycler.image(), _pg, false);
			PG.resetPImageAlpha(_pg);
			_pg.endDraw();
		}
	}

	/////////////////////////////////////////////////////////////////
	// DMX LIGHTING
	/////////////////////////////////////////////////////////////////

	protected float dmxMultiplier() {
		return p.midiState.midiCCPercent(midiInChannel, 41) * 1.5f;
	}

	protected void sendDmxLights() {
		int dmxKnob = 47;
		if(_dmxLights != null) {
			_dmxLights.update();
			float knobValue = p.midiState.midiCCPercent(midiInChannel, dmxKnob);
			if(knobValue == 0) {
				_dmxLights.setBrightness(1);
			} else if(knobValue > 0.1f) {
				_dmxLights.setBrightness((p.midiState.midiCCPercent(midiInChannel, dmxKnob)-0.1f) * 50f);
			} else {
				_dmxLights.setBrightness(0);
			}
			if(_debugTextures == true) _dmxLights.drawDebug(p.g);
		}
	}

	/////////////////////////////////////////////////////////////////
	// DEBUG 
	/////////////////////////////////////////////////////////////////

	protected void runDebugHelpers() {
		// show which triggers have been activated
		for (int i = 0; i < triggerDebugLinearFloats.length; i++) {
			triggerDebugLinearFloats[i].update();
		}
		p.debugView.setValue("HAXVISUAL :: colorTrigger", colorTriggerIndicator.value() > 0);
		p.debugView.setValue("HAXVISUAL :: rotationTrigger", rotationTriggerIndicator.value() > 0);
		p.debugView.setValue("HAXVISUAL :: timingTrigger", timingTriggerIndicator.value() > 0);
		p.debugView.setValue("HAXVISUAL :: modeTrigger", modeTriggerIndicator.value() > 0);
		p.debugView.setValue("HAXVISUAL :: timingSectionTrigger", timingSectionTriggerIndicator.value() > 0);
		p.debugView.setValue("HAXVISUAL :: lineModeTrigger", lineModeTriggerIndicator.value() > 0);
		p.debugView.setValue("HAXVISUAL :: bigChangeTrigger", bigChangeTriggerIndicator.value() > 0);
		
		// debug render time
		for (int i = 0; i < texturePools.length; i++) p.debugView.setValue("HAXVISUAL :: texture "+i, texturePools[i].get(poolCurTextureIndexes[i]).toString());
	}
	
	// debug textures to image files
	protected void outputTestImages(ArrayList<BaseTexture> texturePool) {
		for(BaseTexture tex : texturePool) {
			tex.update();
			tex.update();
			tex.update();
			tex.texture().save(FileUtil.getHaxademicOutputPath() + "hax-visual-textures/" + tex.toString());
			P.println("output: ", tex.toString());
		}
	}

	/////////////////////////////////////////////////////////////////
	// UIControls listener
	/////////////////////////////////////////////////////////////////

	public void uiButtonClicked(UIButton button) {
		if(interphase != null) interphase.uiButtonClicked(button);
	}

	/////////////////////////////////////////////////////////////////
	// IAppStoreListener callbacks
	/////////////////////////////////////////////////////////////////
	
	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			int sequencerIndex = val.intValue();
			if(sequencerIndex < 3) {
				updateTiming();
				p.debugView.setValue(Interphase.SEQUENCER_TRIGGER, val.intValue());
			}
		}
	}

	public void updatedString(String key, String val) {
	}

	public void updatedBoolean(String key, Boolean val) {
	}

	public void updatedImage(String key, PImage val) {
	}

	public void updatedBuffer(String key, PGraphics val) {
	}

}

package com.haxademic.app.haxvisual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxvisual.pools.HaxVisualTexturePools;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
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
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.HalftoneCamoFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneFilter;
import com.haxademic.core.draw.filters.pshader.HueFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.LeaveBlackFilter;
import com.haxademic.core.draw.filters.pshader.LiquidWarpFilter;
import com.haxademic.core.draw.filters.pshader.MaskThreeTextureFilter;
import com.haxademic.core.draw.filters.pshader.MirrorQuadFilter;
import com.haxademic.core.draw.filters.pshader.PixelateFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
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
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;

/**
 * 
 * TODO:  
 * Add new concepts about layout, rather than just relying on displacement & mask effects 
 * Add text cycling texture
 */

public class HaxVisualTwo
extends PAppletHax {
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
	float debugEaseInc = 0.05f;
	protected LinearFloat colorTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat rotationTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat timingTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat modeTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat timingSectionTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat bigChangeTriggerIndicator = new LinearFloat(0, debugEaseInc);
	protected LinearFloat lineModeTriggerIndicator = new LinearFloat(0, debugEaseInc);
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
	protected int lastTimingUpdateDelay = 500;

	// DMX LIGHTS

	protected RandomLightTiming _dmxLights;

	// COLORIZE COMPOSITION

	protected int COLORIZE_NONE = 0;
	protected int COLORIZE_BLUE = 1;
	protected int COLORIZE_RANDOM = 2;
	protected int COLORIZE_MODES = 3;
	protected int colorizeMode = COLORIZE_RANDOM;
	protected ImageGradient imageGradient;
	//	protected ImageGradient imageGradientBlue;
	//	protected boolean colorizeWithGradient = true;
	protected boolean imageGradientLuma = false;
	protected boolean imageGradientFilter = true;

	// DISPLACEMENT LAYER

	protected int displacementLayer = 0;
	protected int overlayMode = 0;
	protected float brightnessVal = 1f;

	// PER-TEXTURE POST EFFECTS
	protected int[] textureEffectsIndices;	// store a effects number for each texture position after the first
	protected int numTextureEffects = 16 + 8; // +8 to give a good chance at removing the filter from the texture slot
	protected boolean perTextureEffects = false;

	// CORNER-PINNED BUFFER

	protected PGraphics _pg;
	protected PGraphicsKeystone _pgPinnable;
	protected float scaleDownPG = 1f; // 0.5f;

	//////////////////////////////////////////////////
	// INIT
	//////////////////////////////////////////////////

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.OSC_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.DMX_LIGHTS_COUNT, 0 );
		//		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, true );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, true );
		//		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, true );
//				p.appConfig.setProperty( AppSettings.INIT_BEADS_AUDIO, true );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.MIDI_DEBUG, false );
		p.appConfig.setProperty( AppSettings.RETINA, false );
		p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
	}

	protected void setupFirstFrame() {
		initDMX();
		buildCanvas();
		buildTextures();
		buildPostProcessingChain();
		// buildInterstitial();
	}

	protected void buildCanvas() {
		int w = P.round(p.width * scaleDownPG);
		int h = P.round(p.height * scaleDownPG);
		_pg = p.createGraphics(w, h, P.P3D);
		_pg.noSmooth();
		OpenGLUtil.setTextureRepeat(_pg);
		_pgPinnable = new PGraphicsKeystone( p, _pg, 12, FileUtil.getFile("text/keystoning/hax-visual-two.txt") );
	}

	protected void buildPostProcessingChain() {
		//		if(colorizeWithGradient) {
		//			imageGradientBlue = new ImageGradient(P.p.loadImage(FileUtil.getFile("images/_sketch/sendgrid/palette-sendgrid.png")));
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		//		}

		KaleidoFilter.instance(p).setAngle(0f);
		KaleidoFilter.instance(p).setSides(2f);

		HalftoneFilter.instance(p).setSizeT(256f, 256f);
		HalftoneFilter.instance(p).setAngle(P.HALF_PI);
		HalftoneFilter.instance(p).setCenter(0.5f, 0.5f);
		HalftoneFilter.instance(p).setScale(1f);

		PixelateFilter.instance(p).setDivider(20f, _pg.width, _pg.height);

		p.midiState.controllerChange(midiInChannel, vignetteKnob, (int) 70);
	}

	protected void initDMX() {
		if(p.appConfig.getInt(AppSettings.DMX_LIGHTS_COUNT, 0) > 0) {
			_dmxLights = new RandomLightTiming(p.appConfig.getInt(AppSettings.DMX_LIGHTS_COUNT, 0));
		}
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
		background(0);
		handleInputTriggers();
		checkBeat();
		getDisplacementLayer();
		if(perTextureEffects) filterActiveTextures();
		updateTextures();
		drawLayers();
		drawAltTopLayerOrDisplacement();
		postProcessFilters();
		colorizeFilter();
		// bloomFilter();
		vignetteFilter();
		drawTopLayer();
		postBrightness();
		if(imageCycler != null) drawInterstitial();
		// draw pinned pgraphics
		if(_debugTextures == true) _pgPinnable.drawTestPattern();
		p.debugView.setTexture(_pg);
		_pgPinnable.update(p.g);
		// sendDmxLights();
		runDebugHelpers();
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
		if(overlayMode != 3 || displacementLayer == 3) {	// we'll use the mask shader if 3, and no need to draw here
			_pg.beginDraw();
			_pg.background(0);
			_pg.blendMode(PBlendModes.EXCLUSION);
			//		OpenGLUtil.setBlending(p.g, true);
			//		OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.DARK_INVERSE);
			for( int i=0; i < _curTexturePool.size() - 1; i++ ) {	
				if(i != displacementLayer) {	// don't draw displacement layer
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
		_pg.blendMode(PBlendModes.BLEND);
		ImageUtil.drawImageCropFill(topLayer().texture(), _pg, true);
		_pg.endDraw();
	}

	/////////////////////////////////////////////////////////////////
	// POST PROCESSING EFFECTS
	/////////////////////////////////////////////////////////////////

	protected void getDisplacementLayer() {		
		displacementLayer = P.round(P.map(p.midiState.midiCCPercent(midiInChannel, displaceMapLayerKnob), 0, 1, 0, 3));
		overlayMode = P.round(P.map(p.midiState.midiCCPercent(midiInChannel, overlayModeKnob), 0, 1, 0, 3));
		p.debugView.setValue("displacementLayer", displacementLayer);
		p.debugView.setValue("overlayMode", overlayMode);
	}

	protected void drawAltTopLayerOrDisplacement() {	
		// DISPLACEMENT MAP ////////////////////////
		// This does special drawing modes if layers weren't drawn 
		// which layer to use for displacement?
		if(displacementLayer < 3) {
			if(displacementLayer >= _curTexturePool.size()) displacementLayer = _curTexturePool.size() - 1; // protection!
			PGraphics displacementBuffer = _curTexturePool.get(displacementLayer).texture();
			if(overlayMode == 0) {
				// DISPLACEMENT MAP FILTER
				// add blur to displacement image
				float blurPercent = 2f; // p.mousePercentX() * 10f;
				BlurHFilter.instance(p).setBlurByPercent(blurPercent, _pg.width);
				BlurVFilter.instance(p).setBlurByPercent(blurPercent, _pg.height);
				BlurHFilter.instance(p).applyTo(displacementBuffer);
				BlurVFilter.instance(p).applyTo(displacementBuffer);
				BlurHFilter.instance(p).applyTo(displacementBuffer);
				BlurVFilter.instance(p).applyTo(displacementBuffer);
				// set current layer as displacer & apply effect
				DisplacementMapFilter.instance(p).setMap(displacementBuffer);
				DisplacementMapFilter.instance(p).setMode(3);
				DisplacementMapFilter.instance(p).applyTo(_pg);
			} else if(overlayMode == 1) {
				// OVERLAY WITH ONLY BLACK
				LeaveBlackFilter.instance(p).setMix(1f);
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
				if(displacementLayer == 0) { 		tex1 = _curTexturePool.get(1).texture(); tex2 = _curTexturePool.get(2).texture(); }
				else if(displacementLayer == 1) { 	tex1 = _curTexturePool.get(0).texture(); tex2 = _curTexturePool.get(2).texture(); }
				else { 								tex1 = _curTexturePool.get(0).texture(); tex2 = _curTexturePool.get(1).texture(); }
				MaskThreeTextureFilter.instance(p).setMask(displacementBuffer);
				MaskThreeTextureFilter.instance(p).setTexture1(tex1);
				MaskThreeTextureFilter.instance(p).setTexture2(tex2);
				MaskThreeTextureFilter.instance(p).applyTo(_pg);
			}
		}
	}

	protected void postProcessFilters() {		
		// CONTRAST ////////////////////////
		if( p.midiState.midiCCPercent(midiInChannel, contrastKnob) != 0 ) {
			if(p.midiState.midiCCPercent(midiInChannel, contrastKnob) > 0.1f) {
				ContrastFilter.instance(p).setContrast(p.midiState.midiCCPercent(midiInChannel, contrastKnob) * 7f);
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

		// KALEIDOSCOPE ////////////////////////
		float kaleidoSides = P.round( p.midiState.midiCCPercent(midiInChannel, kaledioKnob) * 12f );
		if( kaleidoSides > 0 ) {
			if( kaleidoSides == 3 ) {
				ReflectFilter.instance(p).applyTo(_pg);
			} else {
				KaleidoFilter.instance(p).setAngle(0f);
				KaleidoFilter.instance(p).setSides(kaleidoSides);
				KaleidoFilter.instance(p).applyTo(_pg);
			}
		}
	}

	protected void colorizeFilter() {
		// COLORIZE FROM TEXTURE ////////////////////////
		p.debugView.setValue("colorizeMode", colorizeMode);
		if(colorizeMode != COLORIZE_NONE) {
			//			if(colorizeMode == COLORIZE_BLUE) ColorizeFromTexture.instance(p).setTexture(imageGradientBlue.texture());
			if(colorizeMode == COLORIZE_RANDOM) ColorizeFromTexture.instance(p).setTexture(imageGradient.texture());
			ColorizeFromTexture.instance(p).setLumaMult(imageGradientLuma);
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

	protected void vignetteFilter() {
		// VIGNETTE FROM CENTER ////////////////////////
		float vignetteVal = p.midiState.midiCCPercent(midiInChannel, vignetteKnob);
		float vignetteDarkness = P.map(vignetteVal, 0, 1, 13f, -13f);
		VignetteAltFilter.instance(p).setSpread(0.5f);
		VignetteAltFilter.instance(p).setDarkness(1f); // vignetteDarkness
		VignetteAltFilter.instance(p).applyTo(_pg);

		// normal vignette
		VignetteFilter.instance(p).setDarkness(0.56f);
		VignetteFilter.instance(p).applyTo(_pg);
	}

	protected void postBrightness() {
		if(p.midiState.midiCCPercent(midiInChannel, brightnessKnob) != 0) brightnessVal = p.midiState.midiCCPercent(midiInChannel, brightnessKnob) * 5f;
		BrightnessFilter.instance(p).setBrightness(brightnessVal);
		BrightnessFilter.instance(p).applyTo(_pg);	
	}

	/////////////////////////////////////////////////////////////////
	// BEAT DETECTION 
	/////////////////////////////////////////////////////////////////

	protected void checkBeat() {
		if( p.audioData.isBeat() == true && isBeatDetectMode() == true ) {
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
		if ( _keystoneResetTrigger.triggered() == true ) _pgPinnable.resetCorners();
		if ( _debugTexturesTrigger.triggered() == true ) _debugTextures = !_debugTextures;
		
		p.debugView.setValue("isBeatDetectMode()", isBeatDetectMode());
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
		if(colorizeMode == COLORIZE_RANDOM) {
			if(imageGradientFilter && MathUtil.randBooleanWeighted(p, 0.2f)) imageGradient.randomGradientTexture();
		}
		imageGradientLuma = true; // MathUtil.randBoolean(p);
		imageGradientFilter = true; // MathUtil.randBoolean(p);
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
		if(p.millis() > lastTimingUpdateTime + lastTimingUpdateDelay) {
			for( int i=0; i < _curTexturePool.size(); i++ ) {
				_curTexturePool.get(i).updateTiming();
			}
			lastTimingUpdateTime = p.millis();
			// run beat-counted mode triggers on each beat
			autoBeatMode();
		}
		if(_dmxLights != null) _dmxLights.updateDmxLightsOnBeat();
	}

	protected void autoBeatMode() {
		if( isBeatDetectMode() == false ) return; 
		
		numBeatsDetected++;
		p.debugView.setValue("isBeatDetectMode()", isBeatDetectMode());
		p.debugView.setValue("numBeatsDetected", numBeatsDetected);

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
		displacementLayer = MathUtil.randRange(0, 3);
		overlayMode = MathUtil.randRange(0, 3);	
		p.midiState.controllerChange(midiInChannel, displaceMapLayerKnob, P.round((127f/3.1f) * displacementLayer));
		p.midiState.controllerChange(midiInChannel, overlayModeKnob, P.round((127f/3.1f) * overlayMode));
		// P.println(P.round((127f/3.1f) * displacementLayer), P.round((127f/3.1f) * overlayMode));

		// change kaleido
		float kaleidoSides = MathUtil.randRangeDecimal(0, 1);
		if(kaleidoSides < 0.2f) kaleidoSides = 0;
		else if(kaleidoSides < 0.7f) kaleidoSides = 0.25f * 127f;
		else if(kaleidoSides < 0.85f) kaleidoSides = 0.3f * 127f;
		else kaleidoSides = 0.5f * 127f;
		p.midiState.controllerChange(midiInChannel, kaledioKnob, (int) kaleidoSides);
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
		colorizeMode = MathUtil.randRange(0, COLORIZE_MODES - 1);
		//		colorizeWithGradient = MathUtil.randBoolean(p);

		// debug values
		p.debugView.setValue("layerSwapIndex", layerSwapIndex);
		p.debugView.setValue("poolCurTextureIndexes", Arrays.toString(poolCurTextureIndexes));

		// make sure time steppers don't go wild
		SystemUtil.setTimeout(updateTimingCallback, 1);
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
		HaxVisualTexturePools.addTexturesToPool(_pg, bgTexturePool, fgTexturePool, audioTexturePool, topLayerPool);
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
			p.debugView.removeTexture(_curTexturePool.get(i).texture());
		}
		_curTexturePool.clear();
	}

	protected void reloadLayers() {
		clearCurrentLayers();

		// reload current 4 layers
		for (int i = 0; i < texturePools.length; i++) {
			_curTexturePool.add( texturePools[i].get(poolCurTextureIndexes[i]) );
			// debug info
			p.debugView.setTexture(texturePools[i].get(poolCurTextureIndexes[i]).texture());
			p.debugView.setValue("texture "+i, texturePools[i].get(poolCurTextureIndexes[i]).toString());
		}

		// set current textures as active
		for(BaseTexture tex : _curTexturePool) { 
			tex.setActive(true); 
		}

		// tell the top layer
		topLayer().setCurTexturePool(_curTexturePool);
		p.debugView.setValue("_curTexturePool.size()", _curTexturePool.size());
	}

	/////////////////////////////////////////////////////////////////
	// TEXTURE-SPECIFIC POST-PROCESSING
	/////////////////////////////////////////////////////////////////

	protected void selectNewActiveTextureFilters() {
		for(int i=0; i < textureEffectsIndices.length; i++) {
			if(MathUtil.randBooleanWeighted(p, 0.2f)) {
				textureEffectsIndices[i] = MathUtil.randRange(0, numTextureEffects);
			} else {
				textureEffectsIndices[i] = 0;
			}
		}
		p.debugView.setValue("textureEffectsIndices", Arrays.toString(textureEffectsIndices));
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
			DrawUtil.setPImageAlpha(_pg, interstitialAlpha);
			ImageUtil.drawImageCropFill(imageCycler.image(), _pg, false);
			DrawUtil.resetPImageAlpha(_pg);
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
	// DEBUG TEXTURES TO IMAGE FILES
	/////////////////////////////////////////////////////////////////

	protected void runDebugHelpers() {
		// show which triggers have been activated
		for (int i = 0; i < triggerDebugLinearFloats.length; i++) {
			triggerDebugLinearFloats[i].update();
		}
		p.debugView.setValue("colorTrigger", colorTriggerIndicator.value() > 0);
		p.debugView.setValue("rotationTrigger", rotationTriggerIndicator.value() > 0);
		p.debugView.setValue("timingTrigger", timingTriggerIndicator.value() > 0);
		p.debugView.setValue("modeTrigger", modeTriggerIndicator.value() > 0);
		p.debugView.setValue("timingSectionTrigger", timingSectionTriggerIndicator.value() > 0);
		p.debugView.setValue("lineModeTrigger", lineModeTriggerIndicator.value() > 0);
		p.debugView.setValue("bigChangeTrigger", bigChangeTriggerIndicator.value() > 0);
		
		// debug render time
		for (int i = 0; i < texturePools.length; i++) p.debugView.setValue("texture "+i, texturePools[i].get(poolCurTextureIndexes[i]).toString());
	}
	
	
	protected void outputTestImages(ArrayList<BaseTexture> texturePool) {
		for(BaseTexture tex : texturePool) {
			tex.update();
			tex.update();
			tex.update();
			tex.texture().save(FileUtil.getHaxademicOutputPath() + "hax-visual-textures/" + tex.toString());
			P.println("output: ", tex.toString());
		}
	}

}

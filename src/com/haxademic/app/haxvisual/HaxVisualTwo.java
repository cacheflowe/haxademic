package com.haxademic.app.haxvisual;

import java.util.ArrayList;
import java.util.Collections;

import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureAppFrameEq2d;
import com.haxademic.app.haxmapper.textures.TextureAudioTube;
import com.haxademic.app.haxmapper.textures.TextureBlobSheet;
import com.haxademic.app.haxmapper.textures.TextureCyclingRadialGradient;
import com.haxademic.app.haxmapper.textures.TextureEQBandDistribute;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureEQFloatParticles;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureFractalPolygons;
import com.haxademic.app.haxmapper.textures.TextureLinesEQ;
import com.haxademic.app.haxmapper.textures.TextureMeshDeform;
import com.haxademic.app.haxmapper.textures.TextureOuterSphere;
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
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.PGraphicsKeystone;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.midi.AbletonNotes;
import com.haxademic.core.hardware.midi.AkaiMpdPads;
import com.haxademic.core.hardware.midi.LaunchControl;
import com.haxademic.core.hardware.osc.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.image.PBlendModes;
import com.haxademic.core.image.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.image.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.image.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.image.filters.shaders.DeformBloomFilter;
import com.haxademic.core.image.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.image.filters.shaders.EdgesFilter;
import com.haxademic.core.image.filters.shaders.HalftoneFilter;
import com.haxademic.core.image.filters.shaders.HueFilter;
import com.haxademic.core.image.filters.shaders.KaleidoFilter;
import com.haxademic.core.image.filters.shaders.LeaveBlackFilter;
import com.haxademic.core.image.filters.shaders.LiquidWarpFilter;
import com.haxademic.core.image.filters.shaders.MirrorFilter;
import com.haxademic.core.image.filters.shaders.PixelateFilter;
import com.haxademic.core.image.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.image.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.image.filters.shaders.VignetteAltFilter;
import com.haxademic.core.image.filters.shaders.WobbleFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;


public class HaxVisualTwo
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float BEAT_DIVISOR = 1; // 10 to test
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(8f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ALL_SAME = (int) Math.ceil(150f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(40f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(220f / BEAT_DIVISOR);


	protected String _inputFileLines[];
	protected ArrayList<BaseTexture> _bgTexturePool;
	protected ArrayList<BaseTexture> _fgTexturePool;
	protected ArrayList<BaseTexture> _overlayTexturePool;
	
	protected ArrayList<BaseTexture> _curTexturePool;

	protected BaseTexture _bgTexture;
	protected BaseTexture _fgTexture;
	protected BaseTexture _overlayTexture;
	
	protected int layerSwapIndex = 0;
	protected int[] poolCurTextureIndexes = new int[]{0,0,0};
	protected ArrayList<BaseTexture>[] texturePools;

	protected boolean _debugTextures = false;
	protected boolean DEBUG_TEXTURE_SAVE_IMAGE_PREVIEWS = false;
	
	protected int midiInChannel = 0;

	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, LaunchControl.PAD_03, AbletonNotes.NOTE_01});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, LaunchControl.PAD_01, AbletonNotes.NOTE_03});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, LaunchControl.PAD_02, AbletonNotes.NOTE_05});
//	protected InputTrigger _allSameTextureTrigger = new InputTrigger(new char[]{'a'},new String[]{TouchOscPads.PAD_06},new Integer[]{AkaiMpdPads.PAD_06, AbletonNotes.NOTE_06});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, LaunchControl.PAD_08, AbletonNotes.NOTE_07});

	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, LaunchControl.PAD_04, AbletonNotes.NOTE_02});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, LaunchControl.PAD_05, AbletonNotes.NOTE_04});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, LaunchControl.PAD_06, AbletonNotes.NOTE_08});

	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{26});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{25});
	protected InputTrigger _brightnessUpTrigger = new InputTrigger(new char[]{']'},new String[]{},new Integer[]{});
	protected InputTrigger _brightnessDownTrigger = new InputTrigger(new char[]{'['},new String[]{},new Integer[]{});
	protected InputTrigger _debugTexturesTrigger = new InputTrigger(new char[]{'d'},new String[]{},new Integer[]{});
	protected int _lastInputMillis = 0;
	protected int numBeatsDetected = 0;

	protected InputTrigger _programDownTrigger = new InputTrigger(new char[]{'1'},new String[]{TouchOscPads.PAD_15},new Integer[]{AkaiMpdPads.PAD_15, 27});
	protected InputTrigger _programUpTrigger = new InputTrigger(new char[]{'2'},new String[]{TouchOscPads.PAD_16},new Integer[]{AkaiMpdPads.PAD_16, 28});
//	protected int _programIndex = 0;

	protected RandomLightTiming _dmxLights;

	protected float _brightnessVal = 1f;
	protected PShader _blurH;
	protected PShader _blurV;

	protected PShader invert;
	protected PShader kaleido;
	protected PShader edge;
	protected PShader dotScreen;
	protected PShader mirror;
	protected PShader pixelate;
	protected PShader contrast;
	protected PShader displacementShader;
	protected PShader maskShader;
	
	protected int displacementLayer = 0;
	protected int overlayMode = 0;
		
	// global effects processing
	protected int[] _textureEffectsIndices = {0,0,0,0,0,0,0};	// store a effects number for each texture position after the first
	protected int _numTextureEffects = 16 + 8; // +8 to give a good chance at removing the filter from the texture slot

	// keystonable screen
	protected PGraphics _pg;
	protected PGraphicsKeystone _pgPinnable;
	protected float scaleDownPG = 2.0f;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( "osc_active", false );
//		p.appConfig.setProperty( "dmx_lights_count", 4 );
		p.appConfig.setProperty( AppSettings.HIDE_CURSOR, true );
		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, false );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.MIDI_DEBUG, true );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
	}

	public void setup() {
		super.setup();
		noStroke();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( P.round(p.width / scaleDownPG), P.round(p.height / scaleDownPG), P.P3D );
		OpenGLUtil.setTextureRepeat(_pg);
//		_pg.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		_pgPinnable = new PGraphicsKeystone( p, _pg, 12, FileUtil.getFile("text/keystoning/hax-visual-two.txt") );
	}

	protected void initDMX() {
		if(p.appConfig.getInt("dmx_lights_count", 0) > 0) {
			_dmxLights = new RandomLightTiming(p.appConfig.getInt("dmx_lights_count", 0));
		}
	}
	
	protected void setupDeferred() {
		initDMX();
		buildCanvas();
		buildTextures();
		buildPostProcessingChain();
	}

	protected void buildTextures() {
		_bgTexturePool = new ArrayList<BaseTexture>();
		_fgTexturePool = new ArrayList<BaseTexture>();
		_overlayTexturePool = new ArrayList<BaseTexture>();
		texturePools = new ArrayList[]{_bgTexturePool, _fgTexturePool, _overlayTexturePool};
		_curTexturePool = new ArrayList<BaseTexture>();
		addTexturesToPool();
	}


	protected void buildPostProcessingChain() {
		_blurH = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set("h", 1.0f );
		_blurV = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set("v", 1.0f );

		invert = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" );

		kaleido = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/kaleido.glsl" );
		kaleido.set("sides", 2.0f);
		kaleido.set("angle", 0.0f);

		edge = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/edges.glsl" );

		dotScreen = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/halftone.glsl" );
		dotScreen.set("tSize", 256f, 256f);
		dotScreen.set("center", 0.5f, 0.5f);
		dotScreen.set("angle", 1.57f);
		dotScreen.set("scale", 1f);

		mirror = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/mirror.glsl" );

		pixelate = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/pixelate.glsl" );
		pixelate.set("divider", p.width/20f, p.height/20f);

		contrast = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/contrast.glsl" );
		contrast.set("contrast", 2f);

		displacementShader = loadShader(FileUtil.getFile("shaders/filters/displacement-map.glsl"));
		maskShader = loadShader(FileUtil.getFile("shaders/filters/three-texture-opposite-mask.glsl"));
	}

	public void drawApp() {
		if(p.frameCount == 1) setupDeferred();
		background(0);
		getDisplacementLayer();
		checkBeat();
		drawLayers();
		filterActiveTextures();
		postProcessFilters();
		// draw pinned pgraphics
		if(_debugTextures == true) _pgPinnable.drawTestPattern();
		_pgPinnable.update(p.g, true);
		if(_debugTextures == true) {
			_pgPinnable.drawTestPattern();
			debugTextures();
		}
		sendDmxLights();
		
	}

	protected void drawLayers() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			BaseTexture tex = _curTexturePool.get(i);
			if(tex != null && tex.texture() != null) {
				tex.update();
			}
		}
		
		if(overlayMode != 3 || displacementLayer == 3) {	// we'll use the mask shader if 3, and no need to draw here
			_pg.beginDraw();
			_pg.background(0);
			_pg.blendMode(PBlendModes.EXCLUSION);
	//		OpenGLUtil.setBlending(p.g, true);
	//		OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.DARK_INVERSE);
			for( int i=0; i < _curTexturePool.size() - 1; i++ ) {	// don't draw the current filter layer
				if(i != displacementLayer) {	// don't draw displacement layer
					BaseTexture tex = _curTexturePool.get(i);
					if(tex != null && tex.texture() != null) {
						PGraphics textue = tex.texture();
						float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(_pg.width, _pg.height, textue.width, textue.height, true);
						_pg.image(tex.texture(), offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
					}
				}
			}
			_pg.blendMode(PBlendModes.BLEND);
			_pg.endDraw();
		}
	}
	
	protected void getDisplacementLayer() {
		int displaceMapLayerKnob = 21;
		int overlayModeKnob = 41;
		
		displacementLayer = P.round(P.map(p.midi.midiCCPercent(midiInChannel, displaceMapLayerKnob), 0, 1, 0, 3));
		overlayMode = P.round(P.map(p.midi.midiCCPercent(midiInChannel, overlayModeKnob), 0, 1, 0, 3));	
	}
	
	protected void postProcessFilters() {

		int invertKnob = 22;
		int vignetteKnob = 42;
		int distAmpKnob = 23;
		int distTimeKnob = 43;
		int warpKnobAmp = 44;
		int warpKnobFreq = 24;
		int kaledioKnob = 25;
		int effectsKnob = 46;
		int pixelateKnob = 26;
		int contrastKnob = 28;
		int brightnessKnob = 48;

		// DISPLACEMENT MAP ////////////////////////
		// which layer to use for displacement?
		if(displacementLayer < 3) {
			if(overlayMode == 0) {
				// zoom into displacement image
	//			DrawUtil.zoomReTexture(_curTexturePool.get(displacementLayer).texture(), 0.66f + 0.33f * P.sin(p.frameCount * 0.01f));
				// add blur to displacement image
				BlurProcessingFilter.instance(p).setBlurSize(4);
				BlurProcessingFilter.instance(p).setSigma(2);
				BlurProcessingFilter.instance(p).applyTo(_curTexturePool.get(displacementLayer).texture());
				// set current layer as displacer & apply effect
				displacementShader.set("map", _curTexturePool.get(displacementLayer).texture() );
				displacementShader.set("mode", 0 );
				_pg.filter(displacementShader);
			} else if(overlayMode == 1) {
				PGraphics textue = _curTexturePool.get(displacementLayer).texture();
				LeaveBlackFilter.instance(p).setMix(1f);
				LeaveBlackFilter.instance(p).applyTo(textue);
				float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(_pg.width, _pg.height, textue.width, textue.height, true);
				_pg.beginDraw();
				_pg.image(textue, offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
				_pg.endDraw();
			} else if(overlayMode == 2) {
				PGraphics textue = _curTexturePool.get(displacementLayer).texture();
				float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(_pg.width, _pg.height, textue.width, textue.height, true);
				_pg.beginDraw();
				_pg.blendMode(PBlendModes.EXCLUSION);
				_pg.image(textue, offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
				_pg.blendMode(PBlendModes.BLEND);
				_pg.endDraw();
			} else if(overlayMode == 3) {
				// ADD SHADER TO MASK & REVERSE MASK THE OPPOSITE 2 TEXTURES
				PGraphics maskTexture = _curTexturePool.get(displacementLayer).texture();
				PGraphics tex1;
				PGraphics tex2;
				if(displacementLayer == 0) { 		tex1 = _curTexturePool.get(1).texture(); tex2 = _curTexturePool.get(2).texture(); }
				else if(displacementLayer == 1) { 	tex1 = _curTexturePool.get(0).texture(); tex2 = _curTexturePool.get(2).texture(); }
				else { 								tex1 = _curTexturePool.get(0).texture(); tex2 = _curTexturePool.get(1).texture(); }
				maskShader.set("mask", maskTexture );
				maskShader.set("tex1", tex1 );
				maskShader.set("tex2", tex2 );
				_pg.filter(maskShader);
			}
		}
		
		// BRIGHTNESS ////////////////////////
		if(p.midi.midiCCPercent(midiInChannel, brightnessKnob) != 0) _brightnessVal = p.midi.midiCCPercent(midiInChannel, brightnessKnob) * 5f;
		BrightnessFilter.instance(p).setBrightness(_brightnessVal);
		BrightnessFilter.instance(p).applyTo(_pg);

		// CONTRAST ////////////////////////
		if( p.midi.midiCCPercent(midiInChannel, contrastKnob) != 0 ) {
			contrast.set("contrast", p.midi.midiCCPercent(midiInChannel, contrastKnob) * 7 );
			if(p.midi.midiCCPercent(midiInChannel, contrastKnob) > 0.1f) _pg.filter(contrast);
		}

		// MULTIPLE EFFECTS KNOB ////////////////////////
		boolean halftone = ( p.midi.midiCCPercent(midiInChannel, effectsKnob) > 0.25f && p.midi.midiCCPercent(midiInChannel, effectsKnob) < 0.5f );
		if( halftone ) _pg.filter(dotScreen);

		boolean edged = ( p.midi.midiCCPercent(midiInChannel, effectsKnob) > 0.5f && p.midi.midiCCPercent(midiInChannel, effectsKnob) < 0.75f );
		if( edged ) _pg.filter(edge);

		boolean pixelated = ( p.midi.midiCCPercent(midiInChannel, effectsKnob) > 0.75f );
		if( pixelated ) {
			float pixAmout = P.round(p.midi.midiCCPercent(midiInChannel, pixelateKnob) * 40f);
			pixelate.set("divider", p.width/pixAmout, p.height/pixAmout);
			if(p.midi.midiCCPercent(midiInChannel, pixelateKnob) > 0) _pg.filter(pixelate);
		}

		// INVERT ////////////////////////
		boolean inverted = ( p.midi.midiCCPercent(midiInChannel, invertKnob) > 0.5f );
		if( inverted ) _pg.filter(invert);


		
		// COLOR DISTORTION ///////////////////////
		// color distortion auto
		int distAutoFrame = p.frameCount % 6000;
		float distFrames = 100f;
		if(distAutoFrame <= distFrames) {
			float distAmpAuto = P.sin(distAutoFrame/distFrames * P.PI);
			p.midi.controllerChange(0, distAmpKnob, P.round(127 * distAmpAuto));
			p.midi.controllerChange(0, distTimeKnob, P.round(127 * distAmpAuto));
		}
		
		// color distortion
		float colorDistortionAmp = p.midi.midiCCPercent(midiInChannel, distAmpKnob) * 2.5f;
		float colorDistortionTimeMult = p.midi.midiCCPercent(midiInChannel, distTimeKnob);
		if(colorDistortionAmp > 0) {
			float prevTime = ColorDistortionFilter.instance(p).getTime();
			ColorDistortionFilter.instance(p).setTime(prevTime + 1/100f * colorDistortionTimeMult);
			ColorDistortionFilter.instance(p).setAmplitude(colorDistortionAmp);
			ColorDistortionFilter.instance(p).applyTo(_pg);
		}

		// WARP /////////////////////////
//		int warpAutoFrame = p.frameCount % 200;
//		float warpFrames = 100f;
//		if(warpAutoFrame <= warpFrames) {
//			float warpAmpAuto = P.sin(warpAutoFrame/warpFrames * P.PI);
//			p.midi.controllerChange(0, warpKnobAmp, 86);
//			p.midi.controllerChange(0, warpKnobFreq, P.round(0.1f * P.round(127 * warpAmpAuto)));
//		}

		float warpAmp = p.midi.midiCCPercent(midiInChannel, warpKnobAmp) * 0.1f;
		float warpFreq = p.midi.midiCCPercent(midiInChannel, warpKnobFreq) * 10f;
		if(warpAmp > 0) {
			LiquidWarpFilter.instance(p).setAmplitude(warpAmp);
			LiquidWarpFilter.instance(p).setFrequency(warpFreq);
			LiquidWarpFilter.instance(p).setTime(p.frameCount / 40f);
			LiquidWarpFilter.instance(p).applyTo(_pg);
		}

		// KALEIDOSCOPE ////////////////////////
		float kaleidoSides = P.round( p.midi.midiCCPercent(midiInChannel, kaledioKnob) * 12f );
		kaleido.set("sides", kaleidoSides );
		if( kaleidoSides > 0 ) {
			if( kaleidoSides == 3 ) {
				_pg.filter(mirror);
			} else {
				_pg.filter(kaleido);
			}
		}

		// VIGNETTE ////////////////////////
		float vignetteVal = p.midi.midiCCPercent(midiInChannel, vignetteKnob);
		float vignetteDarkness = P.map(vignetteVal, 0, 1, 13f, -13f);
		VignetteAltFilter.instance(p).setSpread(0.5f);
		VignetteAltFilter.instance(p).setDarkness(vignetteDarkness);
		VignetteAltFilter.instance(p).applyTo(_pg);
	}

	protected void debugTextures() {
		// debug current textures
		int i=0;
		for( i=0; i < _curTexturePool.size(); i++ ) {
			p.image(_curTexturePool.get(i).texture(), i * 100, 0, 100, 100);
		}
		if(_dmxLights != null) _dmxLights.drawDebug(p.g);
	}

	protected float dmxMultiplier() {
		return p.midi.midiCCPercent(midiInChannel, 41) * 1.5f;
	}

	protected void sendDmxLights() {
		int dmxKnob = 47;
		if(_dmxLights != null) {
			_dmxLights.update();
			float knobValue = p.midi.midiCCPercent(midiInChannel, dmxKnob);
			if(knobValue == 0) {
				_dmxLights.setBrightness(1);
			} else if(knobValue > 0.1f) {
				_dmxLights.setBrightness((p.midi.midiCCPercent(midiInChannel, dmxKnob)-0.1f) * 50f);
			} else {
				_dmxLights.setBrightness(0);
			}
			if(_debugTextures == true) _dmxLights.drawDebug(p.g);
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

	public void resetBeatDetectMode() {
		_lastInputMillis = p.millis();
		numBeatsDetected = 1;
	}

	public void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );

//		if( p.key == 'a' || p.key == 'A' ){
//			_isAutoPilot = !_isAutoPilot;
//			P.println("_isAutoPilot = "+_isAutoPilot);
//		}
//		if( p.key == 'S' ){
//			_isStressTesting = !_isStressTesting;
//			P.println("_isStressTesting = "+_isStressTesting);
//		}
		if ( _colorTrigger.active() == true ) {
			resetBeatDetectMode();
			updateColor();
			_lastInputMillis = p.millis();
		}
		if ( _modeTrigger.active() == true ) {
			newMode();
			_lastInputMillis = p.millis();
		}
		if ( _lineModeTrigger.active() == true ) {
			resetBeatDetectMode();
			updateLineMode();
			_lastInputMillis = p.millis();
		}
		if ( _rotationTrigger.active() == true ) {
			resetBeatDetectMode();
			updateRotation();
			_lastInputMillis = p.millis();
		}
		if ( _timingTrigger.active() == true ) {
			resetBeatDetectMode();
			updateTiming();
			_lastInputMillis = p.millis();
		}
		if ( _timingSectionTrigger.active() == true ) {
			updateTimingSection();
			_lastInputMillis = p.millis();
		}
		if ( _bigChangeTrigger.active() == true ) {
			resetBeatDetectMode();
			bigChangeTrigger();
			_lastInputMillis = p.millis();
		}
//		if ( _allSameTextureTrigger.active() == true ) {
//			resetBeatDetectMode();
//			randomLayers();
//			_lastInputMillis = p.millis();
//		}
		if ( _audioInputUpTrigger.active() == true ) P.p._audioInput.gainUp();
		if ( _audioInputDownTrigger.active() == true ) P.p._audioInput.gainDown();
		if ( _brightnessUpTrigger.active() == true ) _brightnessVal += 0.1f;
		if ( _brightnessDownTrigger.active() == true ) _brightnessVal -= 0.1f;
		if ( _debugTexturesTrigger.active() == true ) _debugTextures = !_debugTextures;

		/*
		if ( _programDownTrigger.active() == true ) {
			if(_programIndex > 0) _programIndex--;
			reloadLayers();
		}
		if ( _programUpTrigger.active() == true ) {
//			_programIndex = (_programIndex < _bgTexturePool.size() - 1) ? _programIndex + 1 : 0;
			_programIndex++;
			reloadLayers();
		}
*/
	}

	protected void newMode() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newMode();
		}
	}

	protected void updateColor() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).setColor( randomColor(1) );
		}
	}

	protected void updateLineMode() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newLineMode();
		}
	}

	protected void updateRotation() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newRotation();
		}
	}

	protected void updateTiming() {
		// tell all textures to update timing
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).updateTiming();
		}
		if(_dmxLights != null) _dmxLights.updateDmxLightsOnBeat();
		// run auto beat mode
		autoBeatMode();
	}

	protected void autoBeatMode() {
		if( isBeatDetectMode() == true ) numBeatsDetected++;

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
//		updateColor();

		if( numBeatsDetected % BEAT_INTERVAL_ALL_SAME == 0 ) {
//			P.println("BEAT_INTERVAL_ALL_SAME");
			updateLineMode();
		}

		if( numBeatsDetected % BEAT_INTERVAL_NEW_TIMING == 0 ) {
//			P.println("BEAT_INTERVAL_NEW_TIMING");
			updateTimingSection();
		}

		// every 400 beats, do something bigger
		if( numBeatsDetected % BEAT_INTERVAL_BIG_CHANGE == 0 ) {
//			P.println("BEAT_INTERVAL_BIG_CHANGE");
			bigChangeTrigger();
		}
	}


	protected void updateTimingSection() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).updateTimingSection();
		}
	}

	protected void bigChangeTrigger() {
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
		// selectNewActiveTextureFilters();
	}

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

	protected void addTexturesToPool() {

		int videoW = 640;
		int videoH = 360;
		int textureW = P.round(p.width/scaleDownPG);
		int textureH = P.round(p.height/scaleDownPG);
		
		
		// complex textures in the back
		_bgTexturePool.add( new TextureAudioTube( textureW, textureH ) );
		_bgTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatingRings( textureW, textureH ) );
		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );

		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sdf-01-auto.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sdf-02-auto.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sdf-03.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bubbles-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-clouds.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-kaleido.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-radial-wave.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-tiled-moire.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cog-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cubert.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "firey-spiral.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "flame-wisps.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "flexi-spiral.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "glowwave.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "hex-alphanumerics.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "inversion-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "iq-iterations-shiny.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "light-leak.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-burst.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-waves.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "spinning-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "star-field.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-fractal-field.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-nice.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-screensaver.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-scroll.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "supershape-2d.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "warped-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "water-smoke.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wavy-3d-tubes.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "space-swirl.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "docking-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "hughsk-metaballs.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "hughsk-tunnel.glsl" ));

		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/NudesInLimbo-1983.mp4" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/Microworld 1980 with William Shatner.mp4" ));

		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));

		
		
		_fgTexturePool.add( new TextureCyclingRadialGradient( textureW, textureH ));
		_fgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_fgTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
		_fgTexturePool.add( new TextureEQGrid( textureW, textureH ));
		_fgTexturePool.add( new TextureFractalPolygons( textureW, textureH ));
		_fgTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_fgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_fgTexturePool.add( new TextureScrollingColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereRotate( textureW, textureH ));
		_fgTexturePool.add( new TextureSvgPattern( textureW, textureH ));
		_fgTexturePool.add( new TextureTwistingSquares( textureW, textureH ));
		_fgTexturePool.add( new TextureWaveformSimple( textureW, textureH ));
		_fgTexturePool.add( new TextureWaveformCircle( textureW, textureH ));

		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-checker.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-diagonal-stripes.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-dazzle-voronoi.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-expand-loop.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-01.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-02.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-motion-illusion.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-scroll-rows.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-waves.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "circle-parts-rotate.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "dots-orbit.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "fade-dots.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "gradient-line.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "lines-scroll-diag.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "matrix-rain.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-waves.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "shiny-circle-wave.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-grey.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-fade.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-twist.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "swirl.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "triangle-perlin.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wobble-sin.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "dot-grid-dof.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "morphing-bokeh-shape.glsl" ));

		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-asterisk-wave.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-checkerboard-stairs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hex-lines.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hypno-lines.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-plasma.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-distance-blobs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-dots-on-planes.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-drunken-holodeck.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-folded-wrapping-paper.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-grid-noise-warp.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire-camo-alt.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-op-wavy-rotate.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-repeating-circles.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-dashed-lines.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-radial-twist.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-squound-tunnel.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-stripe-waves.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-triangle-wobble-stairs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-warp-vortex.glsl" ));

		

		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-asterisk-wave.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-checkerboard-stairs.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hex-lines.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hypno-lines.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-plasma.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-distance-blobs.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-dots-on-planes.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-drunken-holodeck.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-folded-wrapping-paper.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-grid-noise-warp.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire-camo-alt.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-op-wavy-rotate.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-repeating-circles.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-dashed-lines.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-radial-twist.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-squound-tunnel.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-stripe-waves.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-triangle-wobble-stairs.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-warp-vortex.glsl" ));
		
		_overlayTexturePool.add( new TextureAppFrameEq2d( textureW, textureH ));

//		_bgTexturePool.add( new TextureSphereAudioTextures( videoW, videoH ));
//		_bgTexturePool.add( new TextureWebCam( videoW, videoH ));
//		_bgTexturePool.add( new TextureImageTimeStepper( textureW, textureH ));
		_fgTexturePool.add( new TextureMeshDeform( textureW, textureH ));

		// make sure all textures are not playing videos, etc
		for(BaseTexture tex : _bgTexturePool) tex.setActive(false);
		for(BaseTexture tex : _fgTexturePool) tex.setActive(false);
		for(BaseTexture tex : _overlayTexturePool) tex.setActive(false);

		// randomize all pools
		Collections.shuffle(_bgTexturePool);
		Collections.shuffle(_fgTexturePool);
		Collections.shuffle(_overlayTexturePool);

		// add inital textures to current array
		reloadLayers();

		// output to images
		if(DEBUG_TEXTURE_SAVE_IMAGE_PREVIEWS == true) {
			outputTestImages(_bgTexturePool);
			outputTestImages(_fgTexturePool);
			outputTestImages(_overlayTexturePool);
		}
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

	protected void clearCurrentLayers() {
		for(BaseTexture tex : _curTexturePool) tex.setActive(false);
		for(BaseTexture tex : _curTexturePool) tex.setKnockoutBlack(false);
//		for(BaseTexture tex : _curTexturePool) tex.setAsOverlay(false);
		_curTexturePool.clear();
	}
	
	protected void reloadLayers() {
		clearCurrentLayers();
		
		// reload current 3 layers
		for (int i = 0; i < texturePools.length; i++) {
			_curTexturePool.add( texturePools[i].get(poolCurTextureIndexes[i]) );
		}
		
//		_curTexturePool.add( _bgTexturePool.get(_programIndex % _bgTexturePool.size()) );
//		_curTexturePool.add( _fgTexturePool.get(_programIndex % _fgTexturePool.size()) );
////		_curTexturePool.get(_curTexturePool.size()-1).setKnockoutBlack(true); // set mid layer as overlay
//		_curTexturePool.add( _fgTexturePool.get((_programIndex + 3) % _fgTexturePool.size()) );
////		_curTexturePool.add( _overlayTexturePool.get(_programIndex % _overlayTexturePool.size()) );
		if(_debugTextures == true) P.println("== New Textures: ==============="); 
		for(BaseTexture tex : _curTexturePool) { 
			tex.setActive(true); 
			if(_debugTextures == true) P.println(tex.toString()); 
		}
	}

	protected void randomLayers() {
		clearCurrentLayers();
		
		_curTexturePool.add( randomTexture( _bgTexturePool ) );
		_curTexturePool.add( randomTexture( _fgTexturePool ) );
//		_curTexturePool.get(_curTexturePool.size()-1).setKnockoutBlack(true); // set mid layer as overlay
		_curTexturePool.add( randomTexture( _overlayTexturePool ) );
//		_curTexturePool.add( randomTexture( _overlayTexturePool ) );
		
		for(BaseTexture tex : _curTexturePool) { tex.setActive(true); P.println(tex.toString()); }
	}


	protected BaseTexture randomTexture(ArrayList<BaseTexture> pool) {
		BaseTexture newTexture = pool.get( MathUtil.randRange(0, pool.size()-1 ) );
//		if(newTexture instanceof TextureVideoPlayer) {
//			newTexture.setActive(true);
//		}
		return newTexture;
	}
	
	
	/////////////////////////////////////////////////////////////////
	// Texture-level post-processing effects
	/////////////////////////////////////////////////////////////////

	protected void selectNewActiveTextureFilters() {
		for(int i=1; i < _textureEffectsIndices.length; i++) {
			if(MathUtil.randRange(0, 10) > 8) {
				_textureEffectsIndices[i] = MathUtil.randRange(0, _numTextureEffects);
			}
		}
		P.println("_textureEffectsIndices", _textureEffectsIndices);
	}
	
	protected void filterActiveTextures() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			if(_curTexturePool.get(i).isActive() == true) {
				PGraphics pg = _curTexturePool.get(i).texture();
				float filterTime = p.frameCount / 40f;
				
				if(_textureEffectsIndices[i] == 1) {
					KaleidoFilter.instance(p).setSides(4);
					KaleidoFilter.instance(p).setAngle(filterTime / 10f);
					KaleidoFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 2) {
					DeformTunnelFanFilter.instance(p).setTime(filterTime);
					DeformTunnelFanFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 3) {
					EdgesFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 4) {
					MirrorFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 5) {
					WobbleFilter.instance(p).setTime(filterTime);
					WobbleFilter.instance(p).setSpeed(0.5f);
					WobbleFilter.instance(p).setStrength(0.0004f);
					WobbleFilter.instance(p).setSize( 200f);
					WobbleFilter.instance(p).applyTo(pg);
	//			} else if(_textureEffectsIndices[i] == 6) {
	//				InvertFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 7) {
					RadialRipplesFilter.instance(p).setTime(filterTime);
					RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(filterTime));
					RadialRipplesFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 8) {
					BadTVLinesFilter.instance(p).applyTo(pg);
	//			} else if(_textureEffectsIndices[i] == 9) {
	//				EdgesFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 10) {
					CubicLensDistortionFilter.instance(p).setTime(filterTime);
					CubicLensDistortionFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 11) {
					SphereDistortionFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 12) {
					HalftoneFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 13) {
					PixelateFilter.instance(p).setDivider(15f, pg.width, pg.height);
					PixelateFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 14) {
					DeformBloomFilter.instance(p).setTime(filterTime);
					DeformBloomFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 15) {
					DeformTunnelFanFilter.instance(p).setTime(filterTime);
					DeformTunnelFanFilter.instance(p).applyTo(pg);
				} else if(_textureEffectsIndices[i] == 16) {
					HueFilter.instance(p).setTime(filterTime);
					HueFilter.instance(p).applyTo(pg);
				}
	//			WarperFilter.instance(p).setTime( _timeEaseInc / 5f);
	//			WarperFilter.instance(p).applyTo(pg);
	//			ColorDistortionFilter.instance(p).setTime( _timeEaseInc / 5f);
	//			ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(radsComplete));
	//			ColorDistortionFilter.instance(p).applyTo(pg);
	//			OpenGLUtil.setTextureRepeat(_buffer);
			}
		}
	}
}

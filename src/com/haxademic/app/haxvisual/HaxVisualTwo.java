package com.haxademic.app.haxvisual;

import java.util.ArrayList;
import java.util.Collections;

import com.haxademic.app.haxmapper.dmxlights.RandomLightTiming;
import com.haxademic.app.haxmapper.textures.BaseTexture;
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
import com.haxademic.app.haxmapper.textures.TextureOuterSphere;
import com.haxademic.app.haxmapper.textures.TextureRotatingRings;
import com.haxademic.app.haxmapper.textures.TextureRotatorShape;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereAudioTextures;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureStarTrails;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureVectorFieldEQ;
import com.haxademic.app.haxmapper.textures.TextureWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.DeformBloomFilter;
import com.haxademic.core.draw.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.draw.filters.shaders.EdgesFilter;
import com.haxademic.core.draw.filters.shaders.HalftoneFilter;
import com.haxademic.core.draw.filters.shaders.HueFilter;
import com.haxademic.core.draw.filters.shaders.KaleidoFilter;
import com.haxademic.core.draw.filters.shaders.LeaveBlackFilter;
import com.haxademic.core.draw.filters.shaders.LiquidWarpFilter;
import com.haxademic.core.draw.filters.shaders.MirrorFilter;
import com.haxademic.core.draw.filters.shaders.PixelateFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.VignetteAltFilter;
import com.haxademic.core.draw.filters.shaders.WobbleFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.devices.AbletonNotes;
import com.haxademic.core.hardware.midi.devices.AkaiMpdPads;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.devices.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.CursorToggle;

import processing.core.PGraphics;
import processing.opengl.PShader;

/**
 * 
 * TODO:  
 * Add audio debug panel - show input * audio gain controls
 * Build Debug panel a la NikePatterns project 
 * Add new texture shaders and special effects like two-color shader and screen repeating & rotation shaders  
 * Add new concepts about layout, rather than just relying on displacement & mask effects 
 * Add text cycling texture
 * Add DrawUtil image rotation, just like the new post-draw scale function
 * Fix keyboard / MIDI overlapping issue
 * Add tinting to layers - maybe a shader to re-color everything with a gradient map
 * mirror or kaleido the boring audio reactive textures
 * do something with the unicorn .obj model
     * 3d model layer always on top - receives current textures to apply to self
     * can we recreate the MeshDeform class from the old viz app - yes
     * Use DrawMesh.drawPointsWithAudio() with PShape. Also, deform style from SphereTextureLines class would be good - MeshUtil.deformMeshWithAudio()
 * Displacement layer should act as mesh displace map
 * Fix some old shaders - they go too fast
 */

public class HaxVisualTwo
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float BEAT_DIVISOR = 1; // 10 to test, 1 by default
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(8f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ALL_SAME = (int) Math.ceil(150f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_MODE = (int) Math.ceil(80f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(40f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(120f / BEAT_DIVISOR);


	protected String _inputFileLines[];
	protected ArrayList<BaseTexture> _bgTexturePool;
	protected ArrayList<BaseTexture> _fgTexturePool;
	protected ArrayList<BaseTexture> _overlayTexturePool;
	protected TextureSphereAudioTextures topLayer;
	
	protected ArrayList<BaseTexture> _curTexturePool;
	
	protected int layerSwapIndex = 0;
	protected int[] poolCurTextureIndexes = new int[]{0,0,0};
	protected ArrayList<BaseTexture>[] texturePools;

	protected boolean _debugTextures = false;
	protected boolean DEBUG_TEXTURE_SAVE_IMAGE_PREVIEWS = false;
	
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

	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, LaunchControl.PAD_03, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, LaunchControl.PAD_04, AbletonNotes.NOTE_02});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, LaunchControl.PAD_01, AbletonNotes.NOTE_03});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, LaunchControl.PAD_05, AbletonNotes.NOTE_04});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, LaunchControl.PAD_02, AbletonNotes.NOTE_05});
//	protected InputTrigger _allSameTextureTrigger = new InputTrigger(new char[]{'a'},new String[]{TouchOscPads.PAD_06},new Integer[]{AkaiMpdPads.PAD_06, AbletonNotes.NOTE_06});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, LaunchControl.PAD_08, AbletonNotes.NOTE_07});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, LaunchControl.PAD_06, AbletonNotes.NOTE_08});


	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{26});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{25});
	protected InputTrigger _brightnessUpTrigger = new InputTrigger(new char[]{']'},new String[]{},new Integer[]{});
	protected InputTrigger _brightnessDownTrigger = new InputTrigger(new char[]{'['},new String[]{},new Integer[]{});
	protected InputTrigger _debugTexturesTrigger = new InputTrigger(new char[]{'d'},new String[]{},new Integer[]{});
	protected int _lastInputMillis = 0;
	protected int numBeatsDetected = 0;
	protected int lastTimingUpdateTime = 0;
	protected int lastTimingUpdateDelay = 500;

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
	protected static int[] _textureEffectsIndices = {0,0,0,0,0,0,0};	// store a effects number for each texture position after the first
	protected int _numTextureEffects = 16 + 8; // +8 to give a good chance at removing the filter from the texture slot

	// keystonable screen
	protected PGraphics _pg;
	protected PGraphicsKeystone _pgPinnable;
	protected float scaleDownPG = 1f;
	protected static boolean SECOND_SCREEN = false;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, true );
		p.appConfig.setProperty( AppSettings.OSC_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.DMX_LIGHTS_COUNT, 0 );
		p.appConfig.setProperty( AppSettings.HIDE_CURSOR, false );
		p.appConfig.setProperty( AppSettings.AUDIO_DEBUG, false );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, true );
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, true );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty( AppSettings.MIDI_DEBUG, false );
		p.appConfig.setProperty( AppSettings.RETINA, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}

	public void settings() {
		super.settings();
		if(SECOND_SCREEN == true) p.fullScreen(2);
	}
	
	public void setup() {
		super.setup();
		noStroke();
		CursorToggle toggle = new CursorToggle(false);
	}

	protected void buildCanvas() {
		//_pg = p.createGraphics( P.round(p.width / scaleDownPG), P.round(p.height / scaleDownPG), P.P3D );
		int w = P.round(p.width * scaleDownPG);
		int h = w / 4;
		_pg = p.createGraphics(p.width, p.height, P.P3D);
//		_pg = p.createGraphics(w, h, P.P3D);
//		_pg = p.createGraphics(2048, 512, P.P3D);
		OpenGLUtil.setTextureRepeat(_pg);
		_pg.noSmooth();
		_pgPinnable = new PGraphicsKeystone( p, _pg, 12, FileUtil.getFile("text/keystoning/hax-visual-two.txt") );
	}

	protected void initDMX() {
		if(p.appConfig.getInt(AppSettings.DMX_LIGHTS_COUNT, 0) > 0) {
			_dmxLights = new RandomLightTiming(p.appConfig.getInt(AppSettings.DMX_LIGHTS_COUNT, 0));
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
		topLayer = new TextureSphereAudioTextures( _pg.width, _pg.height );
		texturePools = new ArrayList[]{_bgTexturePool, _fgTexturePool, _overlayTexturePool};
		_curTexturePool = new ArrayList<BaseTexture>();
		addTexturesToPool();
		prepareTexturePools();
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
		
		p.midiState.controllerChange(midiInChannel, vignetteKnob, (int) 40);

	}

	public void drawApp() {
		if(p.frameCount == 1) setupDeferred();
		handleInputTriggers();
		background(0);
		getDisplacementLayer();
		checkBeat();
		drawLayers();
//		filterActiveTextures();
		postProcessFilters();
		drawTopLayer();
		postBrightness();
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
		// update textures
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			BaseTexture tex = _curTexturePool.get(i);
			if(tex != null && tex.texture() != null) {
				tex.update();
				// kaleido audio layer
				if(i == _curTexturePool.size() - 1) {
					tex.texture();
					kaleido.set("sides", 6f );
					tex.texture().filter(kaleido);
				}
			}
		}

		// custom update for the sphere
		topLayer.update();
		
		// composite textures
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
//			float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(_pg.width, _pg.height, topLayer.texture().width, topLayer.texture().height, true);
//			_pg.image(topLayer.texture(), offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
			_pg.endDraw();
		}
	}
	
	protected void drawTopLayer() {
		_pg.beginDraw();
		float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(_pg.width, _pg.height, topLayer.texture().width, topLayer.texture().height, true);
		_pg.image(topLayer.texture(), offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
		_pg.endDraw();
	}

	protected void getDisplacementLayer() {		
		displacementLayer = P.round(P.map(p.midiState.midiCCPercent(midiInChannel, displaceMapLayerKnob), 0, 1, 0, 3));
		overlayMode = P.round(P.map(p.midiState.midiCCPercent(midiInChannel, overlayModeKnob), 0, 1, 0, 3));
	}
	
	protected void postProcessFilters() {
		// DISPLACEMENT MAP ////////////////////////
		// which layer to use for displacement?
		if(displacementLayer < 3) {
			if(displacementLayer >= _curTexturePool.size()) displacementLayer = _curTexturePool.size() - 1; // protection!
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
		
		// CONTRAST ////////////////////////
		if( p.midiState.midiCCPercent(midiInChannel, contrastKnob) != 0 ) {
			contrast.set("contrast", p.midiState.midiCCPercent(midiInChannel, contrastKnob) * 7 );
			if(p.midiState.midiCCPercent(midiInChannel, contrastKnob) > 0.1f) _pg.filter(contrast);
		}

		// MULTIPLE EFFECTS KNOB ////////////////////////
		boolean halftone = ( p.midiState.midiCCPercent(midiInChannel, effectsKnob) > 0.25f && p.midiState.midiCCPercent(midiInChannel, effectsKnob) < 0.5f );
		if( halftone ) _pg.filter(dotScreen);

		boolean edged = ( p.midiState.midiCCPercent(midiInChannel, effectsKnob) > 0.5f && p.midiState.midiCCPercent(midiInChannel, effectsKnob) < 0.75f );
		if( edged ) _pg.filter(edge);

		boolean pixelated = ( p.midiState.midiCCPercent(midiInChannel, effectsKnob) > 0.75f );
		if( pixelated ) {
			float pixAmout = P.round(p.midiState.midiCCPercent(midiInChannel, pixelateKnob) * 40f);
			pixelate.set("divider", p.width/pixAmout, p.height/pixAmout);
			if(p.midiState.midiCCPercent(midiInChannel, pixelateKnob) > 0) _pg.filter(pixelate);
		}

		// INVERT ////////////////////////
		boolean inverted = ( p.midiState.midiCCPercent(midiInChannel, invertKnob) > 0.5f );
		if( inverted ) _pg.filter(invert);


		
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
//		int warpAutoFrame = p.frameCount % 200;
//		float warpFrames = 100f;
//		if(warpAutoFrame <= warpFrames) {
//			float warpAmpAuto = P.sin(warpAutoFrame/warpFrames * P.PI);
//			p.midi.controllerChange(0, warpKnobAmp, 86);
//			p.midi.controllerChange(0, warpKnobFreq, P.round(0.1f * P.round(127 * warpAmpAuto)));
//		}

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
		kaleido.set("sides", kaleidoSides );
		if( kaleidoSides > 0 ) {
			if( kaleidoSides == 3 ) {
				_pg.filter(mirror);
			} else {
				_pg.filter(kaleido);
			}
		}

		// VIGNETTE ////////////////////////
		float vignetteVal = p.midiState.midiCCPercent(midiInChannel, vignetteKnob);
		float vignetteDarkness = P.map(vignetteVal, 0, 1, 13f, -13f);
		VignetteAltFilter.instance(p).setSpread(0.5f);
		VignetteAltFilter.instance(p).setDarkness(1f); // vignetteDarkness
		VignetteAltFilter.instance(p).applyTo(_pg);
	}

	protected void postBrightness() {
		// BRIGHTNESS ////////////////////////
		if(p.midiState.midiCCPercent(midiInChannel, brightnessKnob) != 0) _brightnessVal = p.midiState.midiCCPercent(midiInChannel, brightnessKnob) * 5f;
		BrightnessFilter.instance(p).setBrightness(_brightnessVal);
		BrightnessFilter.instance(p).applyTo(_pg);	
	}
	
	protected void debugTextures() {
		// debug current textures
		int i=0;
		for( i=0; i < _curTexturePool.size(); i++ ) {
			p.image(_curTexturePool.get(i).texture(), i * 200, 0, 200, 100);
		}
		if(_dmxLights != null) _dmxLights.drawDebug(p.g);
	}

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

	public void keyPressed() {
		super.keyPressed();
		if(p.keyCode == 8) _pgPinnable.resetCorners(p.g);
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
			_lastInputMillis = p.millis();
		}
		if ( _modeTrigger.triggered() == true ) {
			newMode();
			_lastInputMillis = p.millis();
		}
		if ( _lineModeTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateLineMode();
			_lastInputMillis = p.millis();
		}
		if ( _rotationTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateRotation();
			_lastInputMillis = p.millis();
		}
		if ( _timingTrigger.triggered() == true ) {
			resetBeatDetectMode();
			updateTiming();
			_lastInputMillis = p.millis();
		}
		if ( _timingSectionTrigger.triggered() == true ) {
			updateTimingSection();
			_lastInputMillis = p.millis();
		}
		if ( _bigChangeTrigger.triggered() == true ) {
			resetBeatDetectMode();
			bigChangeTrigger();
			_lastInputMillis = p.millis();
		}
//		if ( _allSameTextureTrigger.active() == true ) {
//			resetBeatDetectMode();
//			randomLayers();
//			_lastInputMillis = p.millis();
//		}
		if ( _audioInputUpTrigger.triggered() == true ) P.p._audioInput.gainUp();
		if ( _audioInputDownTrigger.triggered() == true ) P.p._audioInput.gainDown();
		if ( _brightnessUpTrigger.triggered() == true ) _brightnessVal += 0.1f;
		if ( _brightnessDownTrigger.triggered() == true ) _brightnessVal -= 0.1f;
		if ( _debugTexturesTrigger.triggered() == true ) _debugTextures = !_debugTextures;

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
		topLayer.newMode();
	}

	protected void updateColor() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).setColor( randomColor(1) );
		}
		topLayer.setColor( randomColor(1) );
	}

	protected void updateLineMode() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newLineMode();
		}
		topLayer.newLineMode();
	}

	protected void updateRotation() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).newRotation();
		}
		topLayer.newRotation();
	}

	protected void updateTiming() {
		// tell all textures to update timing
		if(p.millis() > lastTimingUpdateTime + lastTimingUpdateDelay) {
			for( int i=0; i < _curTexturePool.size(); i++ ) {
				_curTexturePool.get(i).updateTiming();
			}
			topLayer.updateTiming();
			lastTimingUpdateTime = p.millis();
			// run auto beat mode
			autoBeatMode();
		}
		if(_dmxLights != null) _dmxLights.updateDmxLightsOnBeat();
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
//			P.println("BEAT_INTERVAL_BIG_CHANGE");
			bigChangeTrigger();
		}
	}


	protected void updateTimingSection() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			_curTexturePool.get(i).updateTimingSection();
		}
		topLayer.updateTimingSection();
		
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
		selectNewActiveTextureFilters();
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
	
	protected void prepareTexturePools() {
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
		
		// tell the top layer
		topLayer.setCurTexturePool(_curTexturePool);
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
		for(int i=0; i < _textureEffectsIndices.length; i++) {
			if(MathUtil.randRange(0, 10) > 8) {
				_textureEffectsIndices[i] = MathUtil.randRange(0, _numTextureEffects);
			}
		}
//		P.println("_textureEffectsIndices", _textureEffectsIndices.toString());
	}
	
	protected void filterActiveTextures() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			if(_curTexturePool.get(i).isActive() == true) {
				PGraphics pg = _curTexturePool.get(i).texture();
				applyFilterToTexture(pg, i);
			}
		}
	}
	
	public static void applyFilterToTexture(PGraphics pg, int effectIndex) {
		float filterTime = p.frameCount / 40f;
		
		int textureEffectIndex = _textureEffectsIndices[effectIndex];
		if(textureEffectIndex == 1) {
			KaleidoFilter.instance(p).setSides(4);
			KaleidoFilter.instance(p).setAngle(filterTime / 10f);
			KaleidoFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 2) {
			DeformTunnelFanFilter.instance(p).setTime(filterTime);
			DeformTunnelFanFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 3) {
			EdgesFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 4) {
			MirrorFilter.instance(p).applyTo(pg);
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
//			} else if(textureEffectIndex == 9) {
//				EdgesFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 10) {
			CubicLensDistortionFilter.instance(p).setTime(filterTime);
			CubicLensDistortionFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 11) {
			SphereDistortionFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 12) {
			HalftoneFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 13) {
			PixelateFilter.instance(p).setDivider(15f, pg.width, pg.height);
			PixelateFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 14) {
			DeformBloomFilter.instance(p).setTime(filterTime);
			DeformBloomFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 15) {
			DeformTunnelFanFilter.instance(p).setTime(filterTime);
			DeformTunnelFanFilter.instance(p).applyTo(pg);
		} else if(textureEffectIndex == 16) {
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
	
	
	
	
	
	
	
	
	
	
	protected void addTexturesToPoolMinimal() {
		int textureW = P.round(_pg.width);
		int textureH = P.round(_pg.height);
		
		// complex textures in the back
		_bgTexturePool.add( new TextureAudioTube( textureW, textureH ) );
		_bgTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatingRings( textureW, textureH ) );
		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_bgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );

		_fgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );

//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
//		_overlayTexturePool.add( );
	}
	
	protected void addTexturesToPool() {

		int videoW = 640;
		int videoH = 360;
		int textureW = P.round(_pg.width/2);
		int textureH = P.round(_pg.height/2);
		
		
		// complex textures in the back
		_bgTexturePool.add( new TextureAudioTube( textureW, textureH ) );
		_bgTexturePool.add( new TextureBlobSheet( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatorShape( textureW, textureH ) );
		_bgTexturePool.add( new TextureRotatingRings( textureW, textureH ) );
		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
		_bgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );

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
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "docking-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "hughsk-metaballs.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "hughsk-tunnel.glsl" ));

//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/NudesInLimbo-1983.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/Microworld 1980 with William Shatner.mp4" ));
//
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));

		
		
		_fgTexturePool.add( new TextureCyclingRadialGradient( textureW, textureH ));
		_fgTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_fgTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
		_fgTexturePool.add( new TextureEQGrid( textureW, textureH ));
		_fgTexturePool.add( new TextureFractalPolygons( textureW, textureH ));
		_fgTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_fgTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
//		_fgTexturePool.add( new TextureScrollingColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereRotate( textureW, textureH ));
		_fgTexturePool.add( new TextureStarTrails( textureW, textureH ));
//		_fgTexturePool.add( new TextureSvgPattern( textureW, textureH ));
		_fgTexturePool.add( new TextureTwistingSquares( textureW, textureH ));
		_fgTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
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
//		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-triangle-wobble-stairs.glsl" ));
		_fgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-warp-vortex.glsl" ));

		

//		_overlayTexturePool.add( new TextureCyclingRadialGradient( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQBandDistribute( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_overlayTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQFloatParticles( textureW, textureH ));
		_overlayTexturePool.add( new TextureEQGrid( textureW, textureH ));
//		_overlayTexturePool.add( new TextureFractalPolygons( textureW, textureH ));
		_overlayTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_overlayTexturePool.add( new TextureOuterSphere( textureW, textureH ) );
//		_overlayTexturePool.add( new TextureScrollingColumns( textureW, textureH ));
		_overlayTexturePool.add( new TextureSphereRotate( textureW, textureH ));
		_overlayTexturePool.add( new TextureStarTrails( textureW, textureH ));
//		_overlayTexturePool.add( new TextureSvgPattern( textureW, textureH ));
//		_overlayTexturePool.add( new TextureTwistingSquares( textureW, textureH ));
		_overlayTexturePool.add( new TextureVectorFieldEQ( textureW, textureH ) );
		_overlayTexturePool.add( new TextureWaveformSimple( textureW, textureH ));
		_overlayTexturePool.add( new TextureWaveformCircle( textureW, textureH ));

//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-checker.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-diagonal-stripes.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-dazzle-voronoi.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-expand-loop.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-01.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-02.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-motion-illusion.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-scroll-rows.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-waves.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "circle-parts-rotate.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "dots-orbit.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "fade-dots.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "gradient-line.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "lines-scroll-diag.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "matrix-rain.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-waves.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "shiny-circle-wave.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-grey.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-fade.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-twist.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "swirl.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "triangle-perlin.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wobble-sin.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "dot-grid-dof.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW/2, textureH/2, "morphing-bokeh-shape.glsl" ));
//
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-asterisk-wave.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-checkerboard-stairs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hex-lines.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-hypno-lines.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-concentric-plasma.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-distance-blobs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-dots-on-planes.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-drunken-holodeck.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-folded-wrapping-paper.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-grid-noise-warp.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire-camo-alt.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-liquid-moire.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-metaballs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-op-wavy-rotate.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-repeating-circles.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-dashed-lines.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-scrolling-radial-twist.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-squound-tunnel.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-stripe-waves.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-triangle-wobble-stairs.glsl" ));
//		_overlayTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cacheflowe-warp-vortex.glsl" ));
		
//		_overlayTexturePool.add( new TextureAppFrameEq2d( textureW, textureH ));

//		_bgTexturePool.add( new TextureSphereAudioTextures( videoW, videoH ));
//		_bgTexturePool.add( new TextureWebCam( videoW, videoH ));
//		_bgTexturePool.add( new TextureImageTimeStepper( textureW, textureH ));
//		_fgTexturePool.add( new TextureMeshDeform( textureW, textureH ));
	}
	
	
}

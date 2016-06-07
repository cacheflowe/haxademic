package com.haxademic.app.haxvisual;

import java.util.ArrayList;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureAppFrame2d;
import com.haxademic.app.haxmapper.textures.TextureAppFrameEq2d;
import com.haxademic.app.haxmapper.textures.TextureAppFrameWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureAudioTube;
import com.haxademic.app.haxmapper.textures.TextureBarsEQ;
import com.haxademic.app.haxmapper.textures.TextureBasicWindowShade;
import com.haxademic.app.haxmapper.textures.TextureBlobSheet;
import com.haxademic.app.haxmapper.textures.TextureColorAudioFade;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureImageTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureLinesEQ;
import com.haxademic.app.haxmapper.textures.TextureOuterSphere;
import com.haxademic.app.haxmapper.textures.TextureRotatingRings;
import com.haxademic.app.haxmapper.textures.TextureRotatorShape;
import com.haxademic.app.haxmapper.textures.TextureScrollingColumns;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereAudioTextures;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.app.haxmapper.textures.TextureSvgPattern;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureVideoPlayer;
import com.haxademic.app.haxmapper.textures.TextureWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.dmx.DmxInterface;
import com.haxademic.core.hardware.midi.AbletonNotes;
import com.haxademic.core.hardware.midi.AkaiMpdPads;
import com.haxademic.core.hardware.osc.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;


public class HaxVisualTwo
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static int MAX_ACTIVE_TEXTURES = 4;
	public static int MAX_ACTIVE_TEXTURES_PER_GROUP = 2;
	public static int MAX_ACTIVE_MOVIE_TEXTURES = 2;

	protected float BEAT_DIVISOR = 1; // 10 to test
	protected int BEAT_INTERVAL_COLOR = (int) Math.ceil(6f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ROTATION = (int) Math.ceil(8f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_TRAVERSE = (int) Math.ceil(20f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_ALL_SAME = (int) Math.ceil(150f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_NEW_TIMING = (int) Math.ceil(40f / BEAT_DIVISOR);
	protected int BEAT_INTERVAL_BIG_CHANGE = (int) Math.ceil(400f / BEAT_DIVISOR);


	protected String _inputFileLines[];
	protected ArrayList<BaseTexture> _bgTexturePool;
	protected ArrayList<BaseTexture> _fgTexturePool;
	protected ArrayList<BaseTexture> _overlayTexturePool;
	protected ArrayList<BaseTexture> _curTexturePool;

	protected BaseTexture _bgTexture;
	protected BaseTexture _fgTexture;
	protected BaseTexture _overlayTexture;

	protected boolean _debugTextures = false;

	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, AbletonNotes.NOTE_01});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, AbletonNotes.NOTE_03});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, AbletonNotes.NOTE_05});
	protected InputTrigger _allSameTextureTrigger = new InputTrigger(new char[]{'a'},new String[]{TouchOscPads.PAD_06},new Integer[]{AkaiMpdPads.PAD_06, AbletonNotes.NOTE_06});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, AbletonNotes.NOTE_07});

	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, AbletonNotes.NOTE_02});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, AbletonNotes.NOTE_04});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, AbletonNotes.NOTE_08});

	protected InputTrigger _audioInputUpTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav1"},new Integer[]{26});
	protected InputTrigger _audioInputDownTrigger = new InputTrigger(new char[]{},new String[]{"/7/nav2"},new Integer[]{25});
	protected InputTrigger _brightnessUpTrigger = new InputTrigger(new char[]{']'},new String[]{},new Integer[]{});
	protected InputTrigger _brightnessDownTrigger = new InputTrigger(new char[]{'['},new String[]{},new Integer[]{});
	protected InputTrigger _debugTexturesTrigger = new InputTrigger(new char[]{'d'},new String[]{},new Integer[]{});
	protected int _lastInputMillis = 0;
	protected int numBeatsDetected = 0;

	protected InputTrigger _programDownTrigger = new InputTrigger(new char[]{'1'},new String[]{TouchOscPads.PAD_15},new Integer[]{AkaiMpdPads.PAD_15, 27});
	protected InputTrigger _programUpTrigger = new InputTrigger(new char[]{'2'},new String[]{TouchOscPads.PAD_16},new Integer[]{AkaiMpdPads.PAD_16, 28});
	protected int _programIndex = 0;

	protected DmxInterface _dmx;
	protected ColorHaxEasing _color1;
	protected ColorHaxEasing _color2;

	protected PShader _brightness;
	protected float _brightnessVal = 1f;
	protected PShader _blurH;
	protected PShader _blurV;

	protected PShader invert;
	protected PShader vignette;
	protected PShader kaleido;
	protected PShader edge;
	protected PShader dotScreen;
	protected PShader mirror;
	protected PShader pixelate;
	protected PShader badtv;
	protected PShader contrast;
	protected PShader _chromaKeyFilter;

//	public void oscEvent(OscMessage theOscMessage) {
//		super.oscEvent(theOscMessage);
//		String oscMsg = theOscMessage.addrPattern();
//		// handle brightness slider
//		if( oscMsg.indexOf("/7/fader0") != -1) {
//			_brightnessVal = theOscMessage.get(0).floatValue() * 3.0f;
//		}
//	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, "false" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( "osc_active", "false" );
		p.appConfig.setProperty( AppSettings.HIDE_CURSOR, "false" );
	}

	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		noStroke();
		buildPhysicalLighting();
		buildTextures();
		buildPostProcessingChain();
	}

	protected void buildPhysicalLighting() {
		_dmx = new DmxInterface(2);
		_color1 = new ColorHaxEasing("#000000", 5);
		_color2 = new ColorHaxEasing("#000000", 5);
	}

	protected void buildTextures() {
		_bgTexturePool = new ArrayList<BaseTexture>();
		_fgTexturePool = new ArrayList<BaseTexture>();
		_overlayTexturePool = new ArrayList<BaseTexture>();

		_curTexturePool = new ArrayList<BaseTexture>();
		addTexturesToPool();
	}


	protected void buildPostProcessingChain() {
		_brightness = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/brightness.glsl" );
		_brightness.set("brightness", 1.0f );

		_blurH = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-horizontal.glsl" );
		_blurH.set("h", 1.0f );
		_blurV = p.loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/blur-vertical.glsl" );
		_blurV.set("v", 1.0f );


		invert = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/invert.glsl" );

		kaleido = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/kaleido.glsl" );
		kaleido.set("sides", 2.0f);
		kaleido.set("angle", 0.0f);

		vignette = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/vignette.glsl" );
		vignette.set("darkness", 0.85f);
		vignette.set("spread", 0.15f);

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

		_chromaKeyFilter = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-gpu.glsl" );
		_chromaKeyFilter.set("thresholdSensitivity", 0.65f);
		_chromaKeyFilter.set("smoothing", 0.19f);
		_chromaKeyFilter.set("colorToReplace", 0.48f,0.8f,0.2f);
	}

	public void drawApp() {

		background(0);

		checkBeat();
		drawLayers();
		postProcessFilters();
		sendDmxLights();
		if(_debugTextures == true) {
			debugTextures();
		}
	}

//		DrawUtil.setColorForPImage(p);
//		DrawUtil.resetPImageAlpha(p);

	protected void postProcessFilters() {
		int contrastKnob = 0;
		int kaledioKnob = 26;
		int invertKnob = 27;
		int effectsKnob = 46;
		int pixelateKnob = 47;
		int vignetteKnob = 47;
		int brightnessKnob = 48;

		if(p.midi.midiCCPercent(0, brightnessKnob) != 0) _brightnessVal = p.midi.midiCCPercent(0, brightnessKnob) * 5f;
		_brightness.set("brightness", _brightnessVal);
		p.filter( _brightness );

		if( p.midi.midiCCPercent(0, contrastKnob) != 0 ) {
			contrast.set("contrast", p.midi.midiCCPercent(0, contrastKnob) * 7 );
			if(p.midi.midiCCPercent(0, contrastKnob) > 0.1f) p.filter(contrast);
		}

		float kaleidoSides = P.round( p.midi.midiCCPercent(0, kaledioKnob) * 10f );
		kaleido.set("sides", kaleidoSides );
		if( kaleidoSides > 0 ) {
			if( kaleidoSides == 3 ) {
				p.filter(mirror);
			} else {
				p.filter(kaleido);
			}
		}

		// several effects on 1 knob ---
		boolean halftone = ( p.midi.midiCCPercent(0, effectsKnob) > 0.25f && p.midi.midiCCPercent(0, effectsKnob) < 0.5f );
		if( halftone ) p.filter(dotScreen);

		boolean edged = ( p.midi.midiCCPercent(0, effectsKnob) > 0.5f && p.midi.midiCCPercent(0, effectsKnob) < 0.75f );
		if( edged ) p.filter(edge);

		boolean pixelated = ( p.midi.midiCCPercent(0, effectsKnob) > 0.75f );
		if( pixelated ) {
			float pixAmout = P.round(p.midi.midiCCPercent(0, pixelateKnob) * 40f);
			pixelate.set("divider", p.width/pixAmout, p.height/pixAmout);
			if(p.midi.midiCCPercent(0, pixelateKnob) > 0) p.filter(pixelate);
		}

		vignette.set("spread", p.midi.midiCCPercent(0, vignetteKnob));
		if( p.midi.midiCCPercent(0, vignetteKnob) > 0 ) p.filter(vignette);

		boolean inverted = ( p.midi.midiCCPercent(0, invertKnob) > 0.5f );
		if( inverted ) p.filter(invert);

//		_chromaKeyFilter.set("thresholdSensitivity", p.midi.midiCCPercent(5, 7));
//		_chromaKeyFilter.set("smoothing", p.midi.midiCCPercent(6, 7));
//		_chromaKeyFilter.set("colorToReplace", p.midi.midiCCPercent(7, 7), p.midi.midiCCPercent(8, 7), p.midi.midiCCPercent(9, 7));




//		p.filter(_chromaKeyFilter);
	}

	protected void debugTextures() {
		// debug current textures
		int i=0;
		for( i=0; i < _curTexturePool.size(); i++ ) {
			p.image(_curTexturePool.get(i).texture(), i * 100, 0, 100, 100);
		}

		// debug dmx lights
		p.fill(_color1.colorInt(dmxMultiplier()));
		p.rect(i * 100, 0, 100, 100);
		i++;
		p.fill(_color2.colorInt(dmxMultiplier()));
		p.rect(i * 100, 0, 100, 100);
	}

	protected float dmxMultiplier() {
		return p.midi.midiCCPercent(0, 41) * 1.5f;
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
		if ( _allSameTextureTrigger.active() == true ) {
			resetBeatDetectMode();
			randomLayers();
			_lastInputMillis = p.millis();
		}
		if ( _audioInputUpTrigger.active() == true ) P.p._audioInput.gainUp();
		if ( _audioInputDownTrigger.active() == true ) P.p._audioInput.gainDown();
		if ( _brightnessUpTrigger.active() == true ) _brightnessVal += 0.1f;
		if ( _brightnessDownTrigger.active() == true ) _brightnessVal -= 0.1f;
		if ( _debugTexturesTrigger.active() == true ) _debugTextures = !_debugTextures;

		if ( _programDownTrigger.active() == true ) {
			if(_programIndex > 0) _programIndex--;
			reloadLayers();
		}
		if ( _programUpTrigger.active() == true ) {
			if(_programIndex < _bgTexturePool.size() - 1) _programIndex++;
			reloadLayers();
		}

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
		updateDmxLightsOnBeat();
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

		// every 40 beats, do something bigger
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
		for( int i=0; i < _curTexturePool.size(); i++ ) {
//			_curTexturePool.get(i).randomTextureToRandomPolygon();
		}
//		pickNewColors();
	}


	protected void drawLayers() {
		for( int i=0; i < _curTexturePool.size(); i++ ) {
			BaseTexture tex = _curTexturePool.get(i);
			if(tex != null && tex.texture() != null) {
				tex.update();
				PGraphics textue = tex.texture();
				float[] offsetAndSize = ImageUtil.getOffsetAndSizeToCrop(p.width, p.height, textue.width, textue.height, true);
				p.image(tex.texture(), offsetAndSize[0], offsetAndSize[1], offsetAndSize[2], offsetAndSize[3]);
			}
		}
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

	protected void updateDmxLightsOnBeat() {
		// light 1
		if(MathUtil.randBoolean(p) == true) {
			if(MathUtil.randBoolean(p) == true) {
				_color1.setCurrentColorInt(p.color(200));
				_color1.setTargetColorInt(p.color(0));
			} else {
				_color1.setTargetColorInt(p.color(p.random(50,150)));
			}
		}
		// light 2
		if(MathUtil.randBoolean(p) == true) {
			if(MathUtil.randBoolean(p) == true) {
				_color2.setCurrentColorInt( randomColor(0.7f) );
				_color2.setTargetColorInt(p.color(0));
			} else {
				_color2.setTargetColorInt( randomColor( p.random( 0.2f, 0.7f ) ) );
			}
		}
	}

	protected void sendDmxLights() {
		_color1.update();
		_color2.update();
//		_dmx.updateColors(_color1.colorInt(dmxMultiplier()), _color2.colorInt(dmxMultiplier()));
	}


	protected void addTexturesToPool() {

		int videoW = 640;
		int videoH = 360;

//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/smoke-loop.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/tree-loop.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-in-water.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/ink-grow-shrink.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/fire.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/bubbles.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/clouds-timelapse.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/loops/water.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/NudesInLimbo-1983.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/Microworld 1980 with William Shatner.mp4" ));




//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/lunar-lodge/crystal-growth-2.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/lunar-lodge/crystal-growth-3-desktop.m4v" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/lunar-lodge/crystal-growth-4.mp4" ));
//		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/lunar-lodge/crystal-growth-desktop.m4v" ));

//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Cassettes with Titles.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 720, 480, "video/david-last/Decay Flashes.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Dots Scape.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Dots Storm Slow.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 720, 480, "video/david-last/Fur-Composite2.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/KERSHAW_ANIMATION.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Mayu1-Sized.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Mayu2-Sized.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Mayu3-Sized.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 720, 480, "video/david-last/Microbes Flasher.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 720, 480, "video/david-last/Sidewalk Inv Dark+Long.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Star.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Static Garden Pixelviz.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Washi Spot.mov" ));

		int textureW = 1024; // p.width/2;
		int textureH = 1024; // p.height/2;

		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-checker.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "basic-diagonal-stripes.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bubbles-iq.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-circles.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-clouds.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-expand-loop.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-01.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-eye-jacker-02.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-kaleido.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-motion-illusion.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "bw-simple-sin.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "circle-parts-rotate.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "cog-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "docking-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "dot-grid-dof.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "dots-orbit.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "fade-dots.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "firey-spiral.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "flame-wisps.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "flexi-spiral.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "glowwave.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "gradient-line.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "hex-alphanumerics.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "hughsk-metaballs.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "hughsk-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "inversion-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "iq-iterations-shiny.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "light-leak.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "lines-scroll-diag.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "matrix-rain.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "morphing-bokeh-shape.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-burst.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "radial-waves.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "shiny-circle-wave.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-grey.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "sin-waves.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "space-swirl.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "spinning-iq.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-fade.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "square-twist.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "star-field.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-fractal-field.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-nice.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-screensaver.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "stars-scroll.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "supershape-2d.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "swirl.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "triangle-perlin.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "warped-tunnel.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "water-smoke.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wavy-3d-tubes.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wavy-checker-planes.glsl" ));
		_bgTexturePool.add( new TextureShaderTimeStepper( textureW, textureH, "wobble-sin.glsl" ));


//		_fgTexturePool.add( new TextureMeshDeform( textureW, textureH ));
		_bgTexturePool.add( new TextureWaveformSimple( textureW, textureH ));
		_bgTexturePool.add( new TextureSphereRotate( textureW, textureH ));
		_bgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );
		_bgTexturePool.add( new TextureScrollingColumns( textureW, textureH ));
		_bgTexturePool.add( new TextureTwistingSquares( textureW, textureH ));
		_bgTexturePool.add( new TextureImageTimeStepper( textureW, textureH ));
		_bgTexturePool.add( new TextureOuterSphere( textureW, textureH ));
		_bgTexturePool.add( new TextureTwistingSquares( textureW, textureH ));

		_fgTexturePool.add( new TextureEQColumns( textureW, textureH ));
		_fgTexturePool.add( new TextureEQGrid( textureW, textureH ));
		_fgTexturePool.add( new TextureColorAudioFade( textureW, textureH ));
		_fgTexturePool.add( new TextureColorAudioSlide( textureW, textureH ));
		_fgTexturePool.add( new TextureEQConcentricCircles( textureW, textureH ) );

		_fgTexturePool.add( new TextureSvgPattern( textureW, textureH ));
		_fgTexturePool.add( new TextureBasicWindowShade( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereAudioTextures( textureW, textureH ));
		_fgTexturePool.add( new TextureWaveformCircle( textureW, textureH ));
		_fgTexturePool.add( new TextureRotatorShape( textureW, textureH ));
		_fgTexturePool.add( new TextureRotatingRings( textureW, textureH ));
		_fgTexturePool.add( new TextureLinesEQ( textureW, textureH ));
		_fgTexturePool.add( new TextureBlobSheet( textureW, textureH ));
		_fgTexturePool.add( new TextureBarsEQ( textureW, textureH ));


		_fgTexturePool.add( new TextureAudioTube( textureW, textureH ));
		_fgTexturePool.add( new TextureSphereRotate( textureW, textureH ));


		_overlayTexturePool.add( new TextureAppFrameEq2d( textureW, textureH ));
		_overlayTexturePool.add( new TextureAppFrame2d( textureW, textureH ));
		_overlayTexturePool.add( new TextureAppFrameWaveformCircle( textureW, textureH ));


		/*
		// AMI
		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/KERSHAW_ANIMATION.mov" ));
		_fgTexturePool.add( new TextureWaveformCircle( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrame2d( p.width/2, p.height/2 ));

		// the grind
		_bgTexturePool.add( new TextureShaderTimeStepper( p.width/3, p.width/3, "square-twist.glsl" ));
		_fgTexturePool.add( new TextureRotatorShape( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrameEq2d( p.width/2, p.height/2 ));

//		_bgTexturePool.add( new TextureShaderScrubber( p.width/2, p.width/2, "bw-expand-loop.glsl" ));
//		_fgTexturePool.add( new TextureEQConcentricCircles( p.width/2, p.height/2 ));
//		BaseTexture scroller = new TextureScrollingColumns( p.width/2, p.height/2 );
//		scroller.setKnockoutBlack(true);
//		_fgTexturePool.add( scroller );
//		_fgTexturePool.add( new TextureWaveformSimple( p.width/2, p.height/2 ));
//		_overlayTexturePool.add( new TextureAppFrameWaveformCircle( p.width/2, p.height/2 ));

		// yuki
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/david-last/Static Garden Pixelviz.mov" ));
		_fgTexturePool.add( new TextureWaveformCircle( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrameWaveformCircle( p.width/2, p.height/2 ));
//		BaseTexture overlay = new TextureEQConcentricCircles( p.width/2, p.height/2 );
//		overlay.setAsOverlay(true);
//		_overlayTexturePool.add( overlay );

		// Resin drop
		_bgTexturePool.add( new TextureShaderTimeStepper( p.width/4, p.width/4, "stars-scroll.glsl" ));
//		_bgTexturePool.add( new TextureShaderTimeStepper( p.width/2, p.width/2, "matrix-rain.glsl" ));
		_fgTexturePool.add( new TextureAudioTube( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrameEq2d( p.width/2, p.height/2 ));

		// Sinking
//		_bgTexturePool.add( new TextureShaderScrubber( p.width/2, p.width/2, "bw-expand-loop.glsl" ));
		_bgTexturePool.add( new TextureVideoPlayer( 720, 480, "video/david-last/Decay Flashes.mov" ));
//		_bgTexturePool.add( new TextureVideoPlayer( 720, 480, "video/david-last/Fur-Composite2.mov" ));
		_fgTexturePool.add( new TextureRotatingRings( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrame2d( p.width/2, p.height/2 ));


		// Noches
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/lunar-lodge/crystal-growth-desktop.m4v" ));
		_fgTexturePool.add( new TextureRotatorShape( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureRotatorShape( p.width/2, p.height/2 ));
////		_overlayTexturePool.add( new TextureAppFrame2d( p.width/2, p.height/2 ));
//		BaseTexture overlay = new TextureRotatorShape( p.width/2, p.height/2 );
//		overlay.setAsOverlay(true);
//		_overlayTexturePool.add( overlay );

		// Color Blox
		_bgTexturePool.add( new TextureShaderTimeStepper( p.width/3, p.width/3, "hex-alphanumerics.glsl" ));
//		_bgTexturePool.add( new TextureShaderScrubber( p.width/2, p.width/2, "bw-expand-loop.glsl" ));
		_fgTexturePool.add( new TextureOuterSphere( p.width/2, p.height/2 ));
//		_fgTexturePool.add( new TextureSvgPattern( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrameWaveformCircle( p.width/2, p.height/2 ));


		// Base 7
		_bgTexturePool.add( new TextureVideoPlayer( 640, 480, "video/david-last/Dots Storm Slow.mov" ));
		_fgTexturePool.add( new TextureSvgPattern( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrame2d( p.width/2, p.height/2 ));

		// September Drive
		_bgTexturePool.add( new TextureVideoPlayer( videoW, videoH, "video/david-last/Clutter.mov" ));
		_fgTexturePool.add( new TextureMeshDeform( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrame2d( p.width/2, p.height/2 ));

		// Whisper story
		_bgTexturePool.add( new TextureShaderTimeStepper( p.width/3, p.width/3, "warped-tunnel.glsl" ));
		_fgTexturePool.add( new TextureWaveformSimple( p.width/2, p.height/2 ));
		_overlayTexturePool.add( new TextureAppFrameWaveformCircle( p.width/2, p.height/2 ));


//		_texturePool.add( new TextureWebCam() );
		 */

		// store just movies to restrain the number of concurrent movies
//		for( int i=0; i < _texturePool.size(); i++ ) {
//			if( _texturePool.get(i) instanceof TextureVideoPlayer ) {
//				_movieTexturePool.add( _texturePool.get(i) );
//			}
//		}

		// make sure all textures are not playing videos, etc
		for(BaseTexture tex : _bgTexturePool) tex.setActive(false);
		for(BaseTexture tex : _fgTexturePool) tex.setActive(false);
		for(BaseTexture tex : _overlayTexturePool) tex.setActive(false);


		// add inital textures to current array
		reloadLayers();

	}

	protected void reloadLayers() {
		for(BaseTexture tex : _curTexturePool) tex.setActive(false);
		_curTexturePool.clear();

		_curTexturePool.add( _bgTexturePool.get(_programIndex % _bgTexturePool.size()) );
		_curTexturePool.add( _fgTexturePool.get(_programIndex % _fgTexturePool.size()) );
		_curTexturePool.add( _overlayTexturePool.get(_programIndex % _overlayTexturePool.size()) );
		for(BaseTexture tex : _curTexturePool) {
			tex.setActive(true);
			P.println(tex.toString());
		}
	}

	protected void randomLayers() {
		for(BaseTexture tex : _curTexturePool) tex.setActive(false);
		_curTexturePool.clear();

		_curTexturePool.add( randomTexture( _bgTexturePool ) );
		_curTexturePool.add( randomTexture( _fgTexturePool ) );
		_curTexturePool.add( randomTexture( _overlayTexturePool ) );
	}


	protected BaseTexture randomTexture(ArrayList<BaseTexture> pool) {
		BaseTexture newTexture = pool.get( MathUtil.randRange(0, pool.size()-1 ) );
		if(newTexture instanceof TextureVideoPlayer) {
			newTexture.setActive(true);
		}
		return newTexture;
	}
}

package com.haxademic.demo.draw.textures;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.TextureImageTileScroll;
import com.haxademic.core.draw.textures.pgraphics.TextureImageTimeStepper;
import com.haxademic.core.draw.textures.pgraphics.TextureRotatorShape;
import com.haxademic.core.draw.textures.pgraphics.TextureSvgPattern;
import com.haxademic.core.draw.textures.pgraphics.TextureVideoPlayer;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.devices.AbletonNotes;
import com.haxademic.core.hardware.midi.devices.AkaiMpdPads;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.devices.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_BasicVJ 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// texture pool collections
	protected ArrayList<BaseTexture> bgTexturePool;
	protected ArrayList<BaseTexture> fgTexturePool;
	protected ArrayList<BaseTexture> fxTexturePool;
	protected int textureCycleIndex = 0;

	// input triggers
	protected InputTrigger _colorTrigger = new InputTrigger(new char[]{'c'},new String[]{TouchOscPads.PAD_01},new Integer[]{AkaiMpdPads.PAD_01, LaunchControl.PAD_03, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger(new char[]{'v'},new String[]{TouchOscPads.PAD_02},new Integer[]{AkaiMpdPads.PAD_02, LaunchControl.PAD_04, AbletonNotes.NOTE_02});
	protected InputTrigger _timingTrigger = new InputTrigger(new char[]{'n'},new String[]{TouchOscPads.PAD_03},new Integer[]{AkaiMpdPads.PAD_03, LaunchControl.PAD_01, AbletonNotes.NOTE_03});
	protected InputTrigger _modeTrigger = new InputTrigger(new char[]{'m'},new String[]{TouchOscPads.PAD_04},new Integer[]{AkaiMpdPads.PAD_04, LaunchControl.PAD_05, AbletonNotes.NOTE_04});
	protected InputTrigger _timingSectionTrigger = new InputTrigger(new char[]{'f'},new String[]{TouchOscPads.PAD_05},new Integer[]{AkaiMpdPads.PAD_05, LaunchControl.PAD_02, AbletonNotes.NOTE_05});
	protected InputTrigger _bigChangeTrigger = new InputTrigger(new char[]{' '},new String[]{TouchOscPads.PAD_07},new Integer[]{AkaiMpdPads.PAD_07, LaunchControl.PAD_08, AbletonNotes.NOTE_07});
	protected InputTrigger _lineModeTrigger = new InputTrigger(new char[]{'l'},new String[]{TouchOscPads.PAD_08},new Integer[]{AkaiMpdPads.PAD_08, LaunchControl.PAD_06, AbletonNotes.NOTE_08});

	protected WavPlayer player;
	protected String audioTrack = "audio/jets-play.mp3";
//	protected String audioTrack = "audio/false-reeds.mp3";
//	protected String audioTrack = "audio/kit808/bass.wav";
	
	// TODO:
	// * Toggle audio vs. frame-based testing
	

	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.WIDTH, 1500 );
//		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
//		p.appConfig.setProperty( AppSettings.INIT_BEADS_AUDIO, true );
//		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, true );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
	}


	protected void setupFirstFrame() {
		OpenGLUtil.setTextureRepeat(g);

		initAudioPlayer();
		initTextures();
		
		// add sliders
		// p.ui.addSlider(TEX_INDEX, 0, 0, allTextures.length - 1, 1, false);
	}
	
	protected void initAudioPlayer() {
		player = new WavPlayer(); // WavPlayer.newAudioContext()
		P.p.setAudioInput(new AudioInputBeads(player.context()));
		player.loopWav(FileUtil.getFile(audioTrack));		
	}
	
	//////////////////////
	// TEXTURE POOLS
	//////////////////////
	
	protected void initTextures() {
		int w = p.width; 
		int h = p.height;
		
		bgTexturePool = new ArrayList<BaseTexture>();
		bgTexturePool.add(new TextureImageTimeStepper(w, h));	// TODO: create alternate constructor with an image directory
		bgTexturePool.add(new TextureVideoPlayer(w, h, DemoAssets.movieFractalCubePath));
		cycleTexturePool(bgTexturePool);
		
		fgTexturePool = new ArrayList<BaseTexture>();
		fgTexturePool.add(new TextureImageTileScroll(w, h));	// TODO: create alternate constructor with an image directory
		fgTexturePool.add(new TextureSvgPattern(w, h));			// TODO: create alternate constructor with an svg directory
		cycleTexturePool(fgTexturePool);

		fxTexturePool = new ArrayList<BaseTexture>();
		fxTexturePool.add(new TextureRotatorShape(w, h));
		fxTexturePool.add(new TextureEQConcentricCircles(w, h));
		cycleTexturePool(fxTexturePool);
	}
	
	protected BaseTexture bgCurTexture() {
		return bgTexturePool.get(0);
	}

	protected BaseTexture fgCurTexture() {
		return fgTexturePool.get(0);
	}
	
	protected BaseTexture fxCurTexture() {
		return fxTexturePool.get(0);
	}
	
	protected void cycleTexturePool(ArrayList<BaseTexture> texturePool) {
		BaseTexture oldTexture = texturePool.remove(0);
		oldTexture.setActive(false);
		texturePool.add(oldTexture);
		texturePool.get(0).setActive(true);
	}	
	
	//////////////////////
	// TEXTURE POOLS
	//////////////////////
		
	public void drawApp() {
		// set context
		background(0);
		player.setVolume(FileUtil.getFile(audioTrack), 0.99f);
		
		// input
		simulateMidiAndBeats();
		
		// draw
		drawLayers();
	}
	
	protected void drawLayers() {
		// update
		bgCurTexture().update();
		fgCurTexture().setKnockoutBlack(false);
		fgCurTexture().update();
		fxCurTexture().update();
		
		// apply post fx
		// heavy blur on fx layer for displacement smoothness
		float blurPercent = 2f; // p.mousePercentX() * 10f;
		BlurHFilter.instance(p).setBlurByPercent(blurPercent, fxCurTexture().texture().width);
		BlurVFilter.instance(p).setBlurByPercent(blurPercent, fxCurTexture().texture().height);
		BlurHFilter.instance(p).applyTo(fxCurTexture().texture());
		BlurVFilter.instance(p).applyTo(fxCurTexture().texture());
		BlurHFilter.instance(p).applyTo(fxCurTexture().texture());
		BlurVFilter.instance(p).applyTo(fxCurTexture().texture());

		
		// displace the fg layer
		DisplacementMapFilter.instance(p).setMap(fxCurTexture().texture());
		DisplacementMapFilter.instance(p).setMode(3);
		DisplacementMapFilter.instance(p).setAmp(p.audioFreq(20) * 0.2f);
		DisplacementMapFilter.instance(p).applyTo(fgCurTexture().texture());
		
		// knock out black background on fg layer
		ChromaColorFilter.instance(P.p).setColorToReplace(0f, 0f, 0f);
		ChromaColorFilter.instance(P.p).setThresholdSensitivity(0.2f);
		ChromaColorFilter.instance(P.p).setSmoothing(0.25f);
		ChromaColorFilter.instance(P.p).applyTo(fgCurTexture().texture());
		
		// draw
		p.image(bgCurTexture().texture(), 0, 0);
		p.image(fgCurTexture().texture(), 0, 0);
//		p.image(fxCurTexture().texture(), 0, 0);
	}
	
	protected void cycleNextTexturePool() {
		// cycle to next texture pool
		textureCycleIndex++;
		textureCycleIndex = textureCycleIndex % 3;
		
		if(textureCycleIndex == 0) cycleTexturePool(bgTexturePool);
		if(textureCycleIndex == 1) cycleTexturePool(fgTexturePool);
		if(textureCycleIndex == 2) cycleTexturePool(fxTexturePool);
	}

	//////////////////////
	// INPUT & TIMING
	//////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') cycleNextTexturePool();
		if(p.key == '2') {}
	}
	
	protected void simulateMidiAndBeats() {
		if(p.frameCount % 45 == 0 || _timingTrigger.triggered()) {
			bgCurTexture().updateTiming();
			fgCurTexture().updateTiming();
			fxCurTexture().updateTiming();
		}
		if(p.frameCount % 220 == 0 || _timingSectionTrigger.triggered()) {
			bgCurTexture().updateTimingSection();
			fgCurTexture().updateTimingSection();
			fxCurTexture().updateTimingSection();
//			for(BaseTexture tex : allTextures) {
//				tex.setActive(false);
//				tex.setActive(true);
//			}
		}
		if(p.frameCount % 60 == 0 || _colorTrigger.triggered()) {
			bgCurTexture().setColor(ColorsHax.COLOR_GROUPS[1][MathUtil.randRange(0, 4)]);
			fgCurTexture().setColor(ColorsHax.COLOR_GROUPS[1][MathUtil.randRange(0, 4)]);
			fxCurTexture().setColor(ColorsHax.COLOR_GROUPS[1][MathUtil.randRange(0, 4)]);
		}
		if(p.frameCount % 180 == 0 || _lineModeTrigger.triggered()) {
			bgCurTexture().newLineMode();
			fgCurTexture().newLineMode();
			fxCurTexture().newLineMode();
		}
		if(p.frameCount % 250 == 0 || _modeTrigger.triggered()) {
			bgCurTexture().newMode();
			fgCurTexture().newMode();
			fxCurTexture().newMode();
		}
		if(p.frameCount % 75 == 0 || _rotationTrigger.triggered()) {
			bgCurTexture().newRotation();
			fgCurTexture().newRotation();
			fxCurTexture().newRotation();
		}
	}
	
}
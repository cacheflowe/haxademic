package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.TextureConcentricDashedCubes;
import com.haxademic.core.draw.textures.pgraphics.TextureEQChladni;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQFloatParticles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesConnected;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesTerrain;
import com.haxademic.core.draw.textures.pgraphics.TextureEQPointsDeformAndTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureEQRadialLollipops;
import com.haxademic.core.draw.textures.pgraphics.TextureEQTextLog;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterCube;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterSphere;
import com.haxademic.core.draw.textures.pgraphics.TextureVectorFieldEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureWaveformCircle;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.MediaTimecodeTrigger;
import com.haxademic.core.media.MediaTimecodeTrigger.IMediaTimecodeTriggerDelegate;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioHistoryTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.media.video.VLCVideo;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.video.Movie;

public class Demo_SoundViz
extends PAppletHax
implements IMediaTimecodeTriggerDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Make grid 4x5 and add more
	// - Add line thinckness and smaller number of lines at times to Concentric circles
	// - Better sounds - use video soundtrack
	// - FloatParticles should also move outward from center
	// - Particle launcher based on frequency triggers. higher frequencies move faster and are smaller

	protected WavPlayer player;
	protected String[] oneshots = new String[] {
		"data/audio/kit808/kick.wav",	
		"data/audio/kit808/snare.wav",	
		"data/audio/communichords/cacheflowe/mid-buzz-synth.wav",	
	};
	protected String soundbed = "data/audio/communichords/bass/operator-organ-bass.aif";
	protected Movie soundVideo;
	protected VLCVideo video;
	protected String AUDIO_FILE;
	protected String AUDIO_RESTART = "AUDIO_RESTART";
	protected MediaTimecodeTrigger audioRestartTrigger;

	// audio viz objects
	protected AudioHistoryTexture history;
	protected BaseTexture audioTextureConcentricCircles;
	protected BaseTexture audioTextureRadialLollipops;
	protected BaseTexture audioTextureVectorField;
	protected BaseTexture audioTextureLinesConnected;
	protected BaseTexture audioTextureFloatParticles;
	protected BaseTexture audioTextureWaveformCircle;
	protected BaseTexture audioTextureTextLog;
	protected BaseTexture audioTextureOuterCube;
	protected BaseTexture audioTextureOuterSphere;
	protected BaseTexture audioTextureEQLinesTerrain;
	protected BaseTexture audioTextureConcentricDashedCubes;
	protected BaseTexture audioTextureEQPointsDeformAndTexture;

	protected int layoutIndex = 0;
	protected int layoutX = 0;
	protected int layoutY = 0;
	protected int cols = 4;
	protected int padding = 40;
	protected int spacing = 350;
	protected	int vizW = 300;
	protected	int vizH = 300;


	protected void config() {
		Config.setAppSize(1430, 1460);
		Config.setAppLocation(100, 100);
		// Config.setAppSize(1600, 800);
	}
	
	protected void firstFrame() {
		// make sure we're selecting the proper audio device before anything else
		AudioUtil.printMixerInfo();
		AudioUtil.setPrimaryMixer();
		
		// build WavPlayer object, and have them share an AudioContext.
		// this ensures that audio analysis can be done on the shared context's output
		player = new WavPlayer();

		// video = new VLCVideo(p);
		if(video != null) {
			AUDIO_FILE = "D:\\workspace\\att-connected-canvas\\_assets\\audio-viz\\connected-canvas-bball-audio-test_AME\\Comp_1.wav";
			String videoPath = "D:\\workspace\\att-connected-canvas\\_assets\\audio-viz\\connected-canvas-bball-audio-test_AME\\Comp_1.noaudio.mp4";
			player.loopWav(AUDIO_FILE);
			// soundVideo = new Movie(p, videoPath);
			// soundVideo.play();
			video.open(videoPath);
			audioRestartTrigger = new MediaTimecodeTrigger(AUDIO_FILE, 0f, AUDIO_RESTART, this);
		}
		
		// send Beads audio player analyzer to PAppletHax.
		// this automatically writes audio data to the global 
		AudioIn.instance(new AudioInputBeads(player.context()));
		AudioIn.setDampeningFFT(0.5f);

		// Make sure audio data buffers are created before trying to use them
		// Afterwards, AudioIn creates a listener for Processing's `pre()` method, 
		// ensuring that they're coninuoulsy updated as the program runs
		AudioIn.drawBufferFFT();
		AudioIn.drawBufferWaveform();

		// audio history texture. used for shader effects
		history = new AudioHistoryTexture();
		audioTextureConcentricCircles = new TextureEQConcentricCircles(vizW, vizH);
		audioTextureRadialLollipops = new TextureEQRadialLollipops(vizW, vizH);
		audioTextureVectorField = new TextureVectorFieldEQ(vizW, vizH);
		audioTextureLinesConnected = new TextureEQLinesConnected(vizW, vizH);
		audioTextureFloatParticles = new TextureEQFloatParticles(vizW, vizH);
		audioTextureWaveformCircle = new TextureWaveformCircle(vizW, vizH);
		audioTextureTextLog = new TextureEQTextLog(vizW, vizH);
		audioTextureOuterCube = new TextureOuterCube(vizW, vizH);
		audioTextureOuterCube = new TextureEQChladni(vizW, vizH);
		audioTextureOuterSphere = new TextureOuterSphere(vizW, vizH);
		audioTextureEQLinesTerrain = new TextureEQLinesTerrain(650, vizH);
		audioTextureConcentricDashedCubes = new TextureConcentricDashedCubes(vizW, vizH);
		audioTextureEQPointsDeformAndTexture = new TextureEQPointsDeformAndTexture(vizW, vizH);
	}
	
	protected void drawApp() {
		// AudioIn.setDampeningFFT(0.5f);

		p.background(20);
		PG.drawGrid(p.g, 0xff111111, 0xff222222, p.width / 10, p.height / 10, 1, false);
		PG.setDrawFlat2d(p.g, true);

		// keep audio playing
		if(video == null) autoPlay();
		DebugView.setValue("AudioContext :: numinputs", player.activeConnections());

		// check timecode trigger
		if(video != null) {
			float audioPositionSFX = player.position(AUDIO_FILE) / 1000f;
			audioRestartTrigger.update(AUDIO_FILE, audioPositionSFX);
			DebugView.setValue("audioPlayer.position", audioPositionSFX);
		}

		// update/draw
		updateVizTextures();
		drawVizTextures();
		// updateVizTexturesTemp();
		// drawVizTexturesTemp();
	}

	protected void updateVizTexturesTemp() {
		history.updateFFT();
		history.updateWaveform();
		audioTextureConcentricCircles.update();
		audioTextureLinesConnected.update();
	}

	protected void updateVizTextures() {
		// update specific audio viz
		history.updateFFT();
		history.updateWaveform();
		audioTextureConcentricCircles.update();
		audioTextureRadialLollipops.update();
		audioTextureVectorField.update();
		audioTextureLinesConnected.update();
		audioTextureFloatParticles.update();
		audioTextureWaveformCircle.update();
		audioTextureTextLog.update();
		audioTextureOuterCube.update();
		audioTextureOuterSphere.update();
		audioTextureEQLinesTerrain.update();
		audioTextureConcentricDashedCubes.update();
		audioTextureEQPointsDeformAndTexture.update();
	}

	protected void drawVizTexturesTemp() {
		p.image(audioTextureConcentricCircles.texture(), 0, 0);
		p.image(audioTextureLinesConnected.texture(), 800, 0);
	}

	protected void nextLayoutCell() {
	}
	
	protected void drawViz(String title, PImage img) {
		layoutX = spacing * MathUtil.gridXFromIndex(layoutIndex, cols);
		layoutY = spacing * MathUtil.gridYFromIndex(layoutIndex, cols);
		p.text(title, padding + layoutX, padding + layoutY);
		p.image(img, padding + layoutX, padding + layoutY + 30);
		layoutIndex++;
	}
	
	protected void drawVizExtra(String title, PImage img) {
		layoutIndex--;
		layoutX = spacing * MathUtil.gridXFromIndex(layoutIndex, cols);
		layoutY = spacing * MathUtil.gridYFromIndex(layoutIndex, cols);
		p.text(title, padding + layoutX, padding + layoutY + 40);
		p.image(img, padding + layoutX, padding + layoutY + 72, 300, 256);
		layoutIndex++;
	}
	
	protected void drawVizTextures() {
		// draw viz to screen
		layoutIndex = 0;

		DemoAssets.setDemoFont(p.g);
		
		if(DebugView.active() == false) AudioIn.drawDebugBuffer();

		drawViz("TextureEQConcentricCircles", audioTextureConcentricCircles.texture());
		drawViz("TextureEQPointsDeformAndTexture", audioTextureEQPointsDeformAndTexture.texture());
		drawViz("TextureEQRadialLollipops", audioTextureRadialLollipops.texture());
		drawViz("TextureWaveformCircle", audioTextureWaveformCircle.texture());
		drawViz("TextureEQLinesConnected", audioTextureLinesConnected.texture());
		drawViz("TextureVectorFieldEQ", audioTextureVectorField.texture());
		drawViz("TextureOuterCube", audioTextureOuterCube.texture());
		drawViz("TextureOuterSphere", audioTextureOuterSphere.texture());
		drawViz("TextureConcentricDashedCubes", audioTextureConcentricDashedCubes.texture());
		drawViz("TextureEQFloatParticles", audioTextureFloatParticles.texture());
		drawViz("TextureEQLinesTerrain", audioTextureEQLinesTerrain.texture()); layoutIndex++;
		drawViz("AudioIn.bufferDebug()", AudioIn.bufferDebug());
		drawViz("AudioIn.bufferFFT()", AudioIn.bufferFFT());
		drawVizExtra("history.textureFFT()", history.textureFFT());
		drawViz("AudioIn.bufferWaveform()", AudioIn.bufferWaveform());
		drawVizExtra("history.textureWaveform()", history.textureWaveform());
		drawViz("TextureEQTextLog", audioTextureTextLog.texture());

		// draw video
		if(video != null && video.width > 100) {
			layoutX = padding + spacing * MathUtil.gridXFromIndex(layoutIndex, cols);
			layoutY = padding + spacing * MathUtil.gridYFromIndex(layoutIndex, cols) + 30;
			// ImageUtil.cropFillCopyImage(video, p.g, layoutX, layoutY, 650, vizH, true);
			p.image(video, layoutX, layoutY, 650, video.height * (650f / video.width));
		}
	}

	protected void updateVizTiming() {
		audioTextureOuterSphere.newRotation();
		audioTextureOuterSphere.newMode();
		audioTextureOuterCube.newRotation();
		audioTextureWaveformCircle.newLineMode();
		audioTextureLinesConnected.newLineMode();
		audioTextureTextLog.newMode();
		audioTextureConcentricDashedCubes.updateTiming();
		audioTextureConcentricDashedCubes.updateTimingSection();
		audioTextureConcentricDashedCubes.newLineMode();
	}
	
	protected void autoPlay() {
		if(FrameLoop.frameModLooped(30)) {
			if(FrameLoop.frameModLooped(60)) {
				player.playWav(oneshots[0], 1, WavPlayer.PAN_CENTER, false, 0, 0, 0, 0, 0);
				updateVizTiming();
			} else {
				// player.playWav(oneshots[0], 1, WavPlayer.PAN_CENTER, false, 0, 0, 0, 0, 0);
				// player.playWav(oneshots[1], 1, WavPlayer.PAN_CENTER, false, 0, 0, 0, 0, 0);
			}
		}
		if(FrameLoop.frameModLooped(120)) {
			player.playWav(oneshots[2], 1, WavPlayer.PAN_CENTER, false, -8, 0, 300, 1000, 0);
		}
	}

	//////////////////////////////////////////////////////
	// IMediaTimecodeTriggerDelegate callback
	//////////////////////////////////////////////////////

	public void mediaTimecodeTriggered(String mediaId, float time, String action) {
		// P.out(mediaId, time, action);
		// soundVideo.pause();
		// soundVideo.jump(0);
		// soundVideo.play();

		video.setPosition(0);
		video.play();
	}

}

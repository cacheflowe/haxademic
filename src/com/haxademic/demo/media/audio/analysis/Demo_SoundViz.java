package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.TextureConcentricDashedCubes;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQFloatParticles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesConnected;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesTerrain;
import com.haxademic.core.draw.textures.pgraphics.TextureEQRadialLollipops;
import com.haxademic.core.draw.textures.pgraphics.TextureEQTextLog;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterCube;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterSphere;
import com.haxademic.core.draw.textures.pgraphics.TextureVectorFieldEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureWaveformCircle;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioHistoryTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.render.FrameLoop;

public class Demo_SoundViz
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Add EasingFloats to TextureEQConcentricCircles
	// - Add line thinckness and smaller number of lines at times to Concentric circles
	// - Better sounds
	// - FloatParticles should also move outward from center

	protected WavPlayer player;
	protected String[] oneshots = new String[] {
		"data/audio/kit808/kick.wav",	
		"data/audio/kit808/snare.wav",	
		"data/audio/communichords/cacheflowe/mid-buzz-synth.wav",	
	};
	protected String soundbed = "data/audio/communichords/bass/operator-organ-bass.aif";
	
	// audio viz objects
	protected AudioHistoryTexture history;
	protected BaseTexture audioTextureCircles;
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

	protected void config() {
		Config.setAppSize(1800, 1130);
	}
	
	protected void firstFrame() {
		// make sure we're selecting the proper audio device before anything else
		AudioUtil.printMixerInfo();
		AudioUtil.setPrimaryMixer();
		
		// build WavPlayer object, and have them share an AudioContext.
		// this ensures that audio analysis can be done on the shared context's output
		player = new WavPlayer();
		
		// send Beads audio player analyzer to PAppletHax.
		// this automatically writes audio data to the global 
		AudioIn.instance(new AudioInputBeads(player.context()));

		// Make sure audio data buffers are created before trying to use them
		// Afterwards, AudioIn creates a listener for Processing's `pre()` method, 
		// ensuring that they're coninuoulsy updated as the program runs
		AudioIn.drawBufferFFT();
		AudioIn.drawBufferWaveform();

		// audio history texture. used for shader effects
		history = new AudioHistoryTexture();
		audioTextureCircles = new TextureEQConcentricCircles(300, 300);
		audioTextureRadialLollipops = new TextureEQRadialLollipops(300, 300);
		audioTextureVectorField = new TextureVectorFieldEQ(300, 300);
		audioTextureLinesConnected = new TextureEQLinesConnected(300, 300);
		audioTextureFloatParticles = new TextureEQFloatParticles(300, 300);
		audioTextureWaveformCircle = new TextureWaveformCircle(300, 300);
		audioTextureTextLog = new TextureEQTextLog(300, 300);
		audioTextureOuterCube = new TextureOuterCube(300, 300);
		audioTextureOuterSphere = new TextureOuterSphere(300, 300);
		audioTextureEQLinesTerrain = new TextureEQLinesTerrain(650, 300);
		audioTextureConcentricDashedCubes = new TextureConcentricDashedCubes(300, 300);
	}
	
	protected void drawApp() {
		p.background(20);
		PG.drawGrid(p.g, 0xff111111, 0xff222222, p.width / 10, p.height / 10, 1, false);
		PG.setDrawFlat2d(p.g, true);

		// keep audio playing
		autoPlay();
		DebugView.setValue("AudioContext :: numinputs", player.activeConnections());

		// update specific audio viz
		history.updateFFT();
		history.updateWaveform();
		audioTextureCircles.update();
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

		// draw viz to screen
		int y = 40;
		DemoAssets.setDemoFont(p.g);

		p.text("AudioIn.bufferDebug()", 50, y);
		if(DebugView.active() == false) AudioIn.drawDebugBuffer();
		p.image(AudioIn.bufferDebug(), 50, y += 30);

		p.text("AudioIn.bufferFFT()", 50, y += 320);
		p.image(AudioIn.bufferFFT(), 50, y += 30, 300, 2);

		p.text("history.textureFFT()", 50, y += 14);
		p.image(history.textureFFT(), 50, y += 30, 300, 256);
		
		p.text("AudioIn.bufferWaveform()", 50, y += 276);
		p.image(AudioIn.bufferWaveform(), 50, y += 30, 300, 2);

		p.text("history.textureWaveform()", 50, y += 14);
		p.image(history.textureWaveform(), 50, y += 30, 300, 256);

		// reset y
		y = 40;

		p.text("TextureEQConcentricCircles", 400, y += 0);
		p.image(audioTextureCircles.texture(), 400, y += 30);

		p.text("TextureOuterCube", 400, y += 320);
		p.image(audioTextureOuterCube.texture(), 400, y += 30);

		p.text("TextureEQTextLog", 400, y += 320);
		p.image(audioTextureTextLog.texture(), 400, y += 30);

		// reset y
		y = 40;

		p.text("TextureEQRadialLollipops", 750, y += 0);
		p.image(audioTextureRadialLollipops.texture(), 750, y += 30);

		p.text("TextureVectorFieldEQ", 750, y += 320);
		p.image(audioTextureVectorField.texture(), 750, y += 30);

		p.text("TextureOuterSphere", 750, y += 320);
		p.image(audioTextureOuterSphere.texture(), 750, y += 30);

		// reset y
		y = 40;

		p.text("TextureEQLinesConnected", 1100, y += 0);
		p.image(audioTextureLinesConnected.texture(), 1100, y += 30);

		p.text("TextureEQFloatParticles", 1100, y += 320);
		p.image(audioTextureFloatParticles.texture(), 1100, y += 30);
		
		p.text("TextureEQLinesTerrain", 1100, y += 320);
		p.image(audioTextureEQLinesTerrain.texture(), 1100, y += 30);
		
		
		// reset y
		y = 40;

		p.text("TextureWaveformCircle", 1450, y += 0);
		p.image(audioTextureWaveformCircle.texture(), 1450, y += 30);
		
		p.text("TextureConcentricDashedCubes", 1450, y += 320);
		p.image(audioTextureConcentricDashedCubes.texture(), 1450, y += 30);
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

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') player.playWav(oneshots[0], 1, WavPlayer.PAN_CENTER, false, MathUtil.randRange(-10, 10), 0, 0, 0, 0);
		if(p.key == '3') player.playWav(oneshots[1], 1, WavPlayer.PAN_LEFT, false, MathUtil.randRange(-10, 10), MathUtil.randRange(0, 500), 0, 0, 0);
	}
}

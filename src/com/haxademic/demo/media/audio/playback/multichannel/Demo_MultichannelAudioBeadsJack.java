package com.haxademic.demo.media.audio.playback.multichannel;

import org.jaudiolibs.beads.AudioServerIO;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UIButton;

import beads.AudioContext;
import beads.Gain;
import beads.IOAudioFormat;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import processing.core.PFont;

public class Demo_MultichannelAudioBeadsJack
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AudioContext audioContext;
	protected SamplePlayer player;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
	}

	public void setupFirstFrame() {
		// beads
		// needs https://github.com/jaudiolibs/jnajack/releases
		// need to update a beads library to talk to JNAJack properly
		// - https://groups.google.com/d/msg/beadsproject/4E_73DZMTMg/31RJD02WWegJ
		// - https://code.google.com/archive/p/java-audio-utils/downloads
//		audioContext = new AudioContext();
		audioContext = new AudioContext(new AudioServerIO.Jack(), 1024, new IOAudioFormat(44100, 16, 2, 4));	// 2 inputs, 4 outputs. match sample & bit rates & buffer size to current soundcrad settings
		audioContext.postAudioFormatInfo();
		audioContext.start();
		P.out("audioContext.out.getOuts()", audioContext.out.getOuts());
	}

	public void drawApp() {
		p.background(0);
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 20);
		FontCacher.setFontOnContext(p.g, font, 255, 1, PTextAlign.LEFT, PTextAlign.TOP);
	}
	
	public void playWav(String filePath) {
		// load sound
//		P.println("Playing:", filePath);
		Sample audioSample = SampleManager.sample(filePath);
		if(audioSample != null) {
			player = new SamplePlayer(audioContext, audioSample);
			player.setKillOnEnd(false);
			
			Gain gain = new Gain(audioContext, 2, 1f);
			gain.addInput(player);
			
//			audioContext.out.addInput(gain);	 	// default beads play stero signal
			audioContext.out.addInput(0, gain, 0);  // play left channel (3rd arg) out of specific output (1st arg)
			audioContext.out.addInput(3, gain, 0);  // play left channel (3rd arg) out of specific output (1st arg)

			player.start(0);
		} else {
			DebugUtil.printErr("Bad audio file: " + filePath);
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') playWav(FileUtil.getFile("audio/kit808/snare.wav"));
	}
	
	// UIButton callback
	
	public void uiButtonClicked(UIButton button) {
		P.out("uiButtonClicked: please override", button.id(), button.value());
	}

}

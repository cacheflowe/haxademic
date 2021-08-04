package com.haxademic.core.media.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import com.haxademic.core.app.P;

import beads.AudioContext;
import beads.AudioServerIO;
import beads.JavaSoundAudioIO;

public class AudioUtil {

	public static int DEFAULT_AUDIO_MIXER_INDEX = 4;
	
	public static void printMixerInfo() {
		JavaSoundAudioIO.printMixerInfo();
	}
	
	public static int getAudioMixerIndex(String mixerSearchString) {
		if(mixerSearchString != null) {
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
			for(int i = 0; i < mixerInfo.length; i++) {
				String mixerName = mixerInfo[i].getName();
				P.out("["+i+"]" + " - " + mixerName);
				if(mixerName.indexOf(mixerSearchString) == 0) {
					P.out("SELECTED!" + " - " + mixerName);
					// Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
					return i;
				}
			} 
		}
		return DEFAULT_AUDIO_MIXER_INDEX;
	}

	
	// Beads defaut AudioContext init got weird with the switch from Java 8 to 11.
	// Thanks to @hamoid for the solution: 
	// https://discourse.processing.org/t/solution-beads-audio-library-error-java-lang-illegalargumentexception-line-unsupported/9454/6
	
	public static AudioContext getBeadsContext() {
		return getBeadsContext(DEFAULT_AUDIO_MIXER_INDEX);
	}
	
	public static AudioContext getBeadsContextJavaSound() {
		return new AudioContext(new AudioServerIO.JavaSound());
	}
	
	public static AudioContext getBeadsContextBeadsJavaSound() {
		return new AudioContext(new beads.JavaSoundAudioIO());
	}
	
	public static AudioContext getBeadsContext(int mixerIndex) {
		JavaSoundAudioIO jsaIO = new JavaSoundAudioIO();
		jsaIO.selectMixer(mixerIndex);
		AudioContext ctx = new AudioContext(jsaIO);
		return ctx;
	}
}

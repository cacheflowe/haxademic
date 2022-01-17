package com.haxademic.core.media.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import beads.AudioContext;
import beads.AudioFileType;
import beads.AudioServerIO;
import beads.Gain;
import beads.JavaSoundAudioIO;
import beads.RecordToSample;
import beads.Sample;
import ddf.minim.Minim;

public class AudioUtil {

	public static int DEFAULT_AUDIO_MIXER_INDEX = 4;
	
	public static void printMixerInfo() {
		JavaSoundAudioIO.printMixerInfo();
	}
	
	public static void setPrimaryMixer() {
		DEFAULT_AUDIO_MIXER_INDEX = AudioUtil.getAudioMixerIndex("Primary");
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

	public static Mixer getMixerFromIndex(int index) {
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		Mixer mixer = AudioSystem.getMixer(mixerInfo[index]);
		return mixer;
	}

	@SuppressWarnings("deprecation")
	public static void setMinimAudioMixer(Minim minim, int index) {
		minim.setOutputMixer(getMixerFromIndex(index));
	}
	
	//////////////////////////////////////////////////
	// Beads defaut AudioContext init got weird with the switch from Java 8 to 11.
	// Thanks to @hamoid for the solution: 
	// https://discourse.processing.org/t/solution-beads-audio-library-error-java-lang-illegalargumentexception-line-unsupported/9454/6
	//////////////////////////////////////////////////
	
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
	
	//////////////////////////////////////////////////
	// RECORDING
	//////////////////////////////////////////////////
	
	public static RecordToSample rts;
	public static Sample outputSample;
	
	public static void buildRecorder(AudioContext ac, int recordTime) {
		P.out("Started recording audio!");
		try {
			// specify the recording format
//			AudioFormat af = new AudioFormat(44100.0f, 16, 1, true, true);
			// create a buffer for the recording
			outputSample = new Sample(0, 2, 44100);
			// initialize the RecordToSample object
			rts = new RecordToSample(ac, outputSample, RecordToSample.Mode.INFINITE);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Gain gain = new Gain(ac, 2);
		gain.setGain(0.75f);
		gain.addInput(ac.out);
		rts.addInput(gain);
		ac.out.addDependent(rts);
	}
	
	public static void finishRecording() {
		String baseDir = FileUtil.haxademicOutputPath() + "audio-recordings" + FileUtil.SEPARATOR;
		FileUtil.createDir(baseDir);
		String filename = "recording-" + SystemUtil.getTimestamp() + ".wav";
		rts.pause(true);
		try{
			outputSample.write(baseDir + filename, AudioFileType.WAV);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		rts.kill();
		P.out("Recorded audio:", filename);
	}
	

}

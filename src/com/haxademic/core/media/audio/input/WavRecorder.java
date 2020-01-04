package com.haxademic.core.media.audio.input;

import java.io.File;

import javax.sound.sampled.Mixer;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import ddf.minim.AudioInput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;

public class WavRecorder {

	protected Minim minim;
	protected static AudioInput audioLineIn;
	protected AudioRecorder recorder;
	protected boolean recorded;
	protected String newWavFilePath;
	
	Mixer.Info[] mixerInfo;
	
	public WavRecorder() {
		minim = new Minim(P.p);	// TODO: make this ref static?
		if(audioLineIn == null) audioLineIn = minim.getLineIn(Minim.MONO, 2048);
		//		mixerInfo = AudioSystem.getMixerInfo();
		//		for (int i = 0; i < mixerInfo.length; i++) {
		//			P.println("["+i+"] "+mixerInfo[i].getName());
		//		}
		//		minim.setInputMixer(AudioSystem.getMixer(mixerInfo[1]));
	}
	
	public boolean isRecording() {
		if(recorder != null && recorder.isRecording()) {
			return true;
		}
		return false;
	}
	
	public int waveformArraySize() {
		return audioLineIn.left.size();
	}
	
	public float inputAmp() {
		return audioLineIn.left.level();
	}
	
	public float waveformDataAtIndex(int index) {
		return audioLineIn.left.get(index % waveformArraySize());
	}
	
	public void startRecording() {
		startRecording(null);
	}
	
	public void startRecording(String newFilePath) {
		// create save dir if filename has more path in it
		String saveDir = FileUtil.pathForFile(newFilePath);
		P.println("creating dir", saveDir);
		if(FileUtil.fileOrPathExists(saveDir) == false) FileUtil.createDir(saveDir);
		
		// use filename or generate a timestamp
		newWavFilePath = (newFilePath != null) ? 
				newFilePath : 
				FileUtil.haxademicOutputPath() + "audio" + File.separator + SystemUtil.getTimestamp() + ".wav";
		
		// start the recording
		recorder = minim.createRecorder(audioLineIn, newWavFilePath);
		recorder.beginRecord();
	}
	
	public void stopRecording(boolean normalize) {
		if(recorder != null && recorder.isRecording()) {
			recorder.endRecord();
			recorder.save();
			recorded = true;
			if(normalize) NormalizeMonoWav.normalize(newWavFilePath, true, 0.85f);
		} else {
			DebugUtil.printErr("[ERROR] WavRecorder.endRecording() - no recorder object, or not recording");
		}
	}
	
}

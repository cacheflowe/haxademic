package com.haxademic.demo.media.audio.playback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.SystemUtil;

import processing.core.PFont;
import processing.sound.FFT;
import processing.sound.SoundFile;

public class Demo_SoundFile_FromDirectory
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SoundFile curSound;
	protected String soundFileName;
	protected ArrayList<SoundFile> sounds;

	// paths
	protected String soundsPath = "E:\\cacheflowe\\samples\\sample-packs\\new york house samples";
	protected String outputPathInterphase = "D:\\workspace\\interphase\\data\\audio\\samples2";
	protected String[] outputDirs;
	protected ArrayList<String> soundPaths;
	protected String curFilePath;
	protected int playlistIndex = 0;
	
	protected FFT fft;
	protected int bands = 128;
	protected float smoothingFactor = 0.2f;
	protected float[] sum = new float[bands];
	protected int fftScale = 2;
	protected float barWidth;
	
	/*
	 * TODO:
	 * - Add ability to tune a sample to C
	 * - Add ability to chop out a segment & save
	 * - More efficient wave drawing - divide samples by width
	 * Notes:
	 * - http://www.labbookpages.co.uk/audio/javaWavFiles.html
	 * - https://processing.org/reference/libraries/sound/index.html
	 * - http://www.labbookpages.co.uk/audio/javaWavFiles.html
	 */

	protected void config() {
		Config.setAppSize(1800, 1000);
	}
	
	protected void firstFrame() {
		fft = new FFT(this, bands);
		loadSounds();
		setIndex(0);
		getOutputDirs();
	}
	
	protected void getOutputDirs() {
	    outputDirs = FileUtil.getDirsInDir(outputPathInterphase);
	}

	public void folderSelected(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			println("User selected " + selection.getAbsolutePath());
			soundsPath = selection.getAbsolutePath();
            loadSounds();
		}
	}

	protected void loadSounds() {
		// search for sounds in directory
		P.out("####################### ", soundsPath);
		sounds = new ArrayList<SoundFile>();
		soundPaths = FileUtil.getFilesInDirOfTypes(soundsPath, "wav,WAV,mp3,MP3,aif,AIF,aiff", true);
		for (int j = 0; j < soundPaths.size(); j++) {
//			String filePath = soundPaths.get(j);
//			P.out(filePath);
			// create lazy-populated array
			sounds.add(null);
		}
		P.out("Found " + sounds.size() + " sounds");
	}

	protected void setIndex(int index) {
		// set index and stop previous sound
		playlistIndex = index;
		if(curSound != null) {
			curSound.stop();
			curSound.removeFromCache();
		}
		
		// load file or pull from cache
		curFilePath = soundPaths.get(playlistIndex);
		soundFileName = FileUtil.fileNameFromPath(curFilePath);
		if(sounds.get(playlistIndex) == null) {	// lazy load sounds
			try {
				SoundFile newSound = new SoundFile(this, curFilePath);
				sounds.set(playlistIndex, newSound);
			} catch(ArrayIndexOutOfBoundsException e) {
				P.error("Couldn't load sound!", curFilePath);
			} catch(NullPointerException e) {
				P.error("Couldn't load sound! NPE", curFilePath);
			}
		}
		
		try {
			// activate current sound
			curSound = sounds.get(playlistIndex);
			if(curSound != null && curSound.duration() > 0) {
				fft.input(curSound);
			}
		} catch(NullPointerException e) {
			P.error("Couldn't load sound! NPE", curFilePath);
		}
		
		// play it!
		playSound();
	}

	public void keyPressed() {
		super.keyPressed();
		
		// CTRL + V to paste a path
		if(p.keyCode == 86 && KeyboardState.instance().isKeyOn(17)) {
			String clipboard = SystemUtil.getClipboardContents();
			if(FileUtil.fileOrPathExists(clipboard)) {
				soundsPath = clipboard;
				loadSounds();
			}
		}
	}

	protected void runKeyCommands() {
	    // is shift down?
		float keyScale = (KeyboardState.keyOn(16)) ? 10 : 1;
		// PREV
		if(KeyboardState.keyTriggered('q')) {
			int newIndex = (playlistIndex - P.round(1 * keyScale)) % sounds.size();
			if(newIndex < 0) newIndex = sounds.size() - 1;
			setIndex(newIndex);
		}
		// NEXT
		if(KeyboardState.keyTriggered('w')) {
			int newIndex = (playlistIndex + P.round(1 * keyScale)) % sounds.size();
			setIndex(newIndex);
		}
		// PLAY
		if(KeyboardState.keyTriggered('e')) {
		    playSound();
		}
		// STOP
		if(KeyboardState.keyTriggered('r')) {
			if(curSound.isPlaying()) curSound.stop();
		}
		// Not working...
		if(KeyboardState.keyTriggered('c')) {
			curSound.resize(curSound.frames() / 2);
		}
		if(KeyboardState.keyTriggered(' ')) {
//			WavFileReaderWriter readerWriter = new WavFileReaderWriter();
//			readerWriter.writeAudioFile(null, outputPathSuffix, null, null);
		}
		// choose folder
		if(KeyboardState.keyTriggered('o')) {
			p.selectFolder("Select a folder", "folderSelected");
		}
		// copy to output folders
		try {
		    if(KeyboardState.keyTriggered('1')) { FileUtil.copyFile(curFilePath, outputDirs[0] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('2')) { FileUtil.copyFile(curFilePath, outputDirs[1] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('3')) { FileUtil.copyFile(curFilePath, outputDirs[2] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('4')) { FileUtil.copyFile(curFilePath, outputDirs[3] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('5')) { FileUtil.copyFile(curFilePath, outputDirs[4] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('6')) { FileUtil.copyFile(curFilePath, outputDirs[5] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('7')) { FileUtil.copyFile(curFilePath, outputDirs[6] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		    if(KeyboardState.keyTriggered('8')) { FileUtil.copyFile(curFilePath, outputDirs[7] + FileUtil.SEPARATOR + soundFileName.replaceAll(" ", "_")); }
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} 

	}

	protected void playSound() {
		if(curSound == null) return;
		try {
			if(curSound.duration() > 0) {
				curSound.stop();
				curSound.play();
			}
		} catch(NullPointerException e) {
			P.error("Couldn't play sound! NPE");
			curSound = null;
		}
	}

	protected void drawApp() {
		p.background(0);
		runKeyCommands();
		drawSound();
		drawSoundInfo();
	}
	
	protected void drawSound() {
		if(curSound == null) return;
		
		// draw wave data
		p.fill(255, 127);
		p.strokeWeight(1);
		float spacing = p.width / (float) curSound.frames();
		DebugView.setValue("spacing", spacing);
		for (int i = 0; i < curSound.frames(); i++) {
			float x = i * spacing;
			set((int) x, round(P.map(curSound.read(i, 0), -1f, 1f, p.height/2 - 200, p.height/2)), 255);
			if(curSound.channels() > 1) {
				set((int) x, round(P.map(curSound.read(i, 1), -1f, 1f, p.height/2, p.height/2 + 200)), 255);
			}
		}
		
		// playhead
		float progress = (float) curSound.position() / curSound.duration();
		p.fill(255, 127);
		p.rect(progress * p.width, 0, 2, p.height);
		
		// fft
		p.fill(255, 80);
		barWidth = width/(float) bands;
		fft.analyze();
		for (int i = 0; i < bands; i++) {
			// Smooth the FFT spectrum data by smoothing factor
			sum[i] += (fft.spectrum[i] - sum[i]) * smoothingFactor;
			// Draw the rectangles, adjust their height using the scale factor
			rect(i*barWidth, p.height, barWidth, -sum[i]*p.height*fftScale);
		}

	}
	
	protected void drawSoundInfo() {
		// set font
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 32);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);

		if(curSound == null) {
			p.text(
					playlistIndex+1 + " of " + soundPaths.size() + FileUtil.NEWLINE + 
					"File = " + soundFileName + FileUtil.NEWLINE + 
					"BAD!"
					, 300, 100);
		} else {
			p.text(
					playlistIndex+1 + " of " + soundPaths.size() + FileUtil.NEWLINE + 
					"File = " + soundFileName + FileUtil.NEWLINE + 
					"Channels = " + curSound.channels() + FileUtil.NEWLINE + 
					"SampleRate = " + curSound.sampleRate() + " Hz" + FileUtil.NEWLINE + 
					"Samples = " + curSound.frames() + " samples" + " Hz" + FileUtil.NEWLINE +
					"Duration = " + curSound.duration() + " seconds" + FileUtil.NEWLINE
					, 300, 100);
		}
	}

}

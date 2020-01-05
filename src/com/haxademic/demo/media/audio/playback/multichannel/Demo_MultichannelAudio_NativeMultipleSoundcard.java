package com.haxademic.demo.media.audio.playback.multichannel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

import beads.AudioContext;
import processing.core.PFont;

public class Demo_MultichannelAudio_NativeMultipleSoundcard
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AudioContext audioContext;
	
	protected ArrayList<Line.Info> linesOut;
	protected ArrayList<Mixer> mixers;
	protected File audioSample;
	protected String LINE_INDEX = "LINE_INDEX";
	protected String MIXER_INDEX = "MIXER_INDEX";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		// load assets
		audioSample = FileUtil.fileFromPath(FileUtil.getPath("audio/kit808/snare.wav"));
		
		// store audio lines
		linesOut = new ArrayList<Line.Info>();
		mixers = new ArrayList<Mixer>();
		
		// beads
//		audioContext = new AudioContext();
//		audioContext.postAudioFormatInfo();
//		P.out("audioContext.out.getOuts()", audioContext.out.getOuts());
//		audioContext.start();

		printAllMixerNames();
		P.out("=============");
		getDeviceInfos("");
	}

	protected void printAllMixerNames() {
	    for(Mixer.Info info : AudioSystem.getMixerInfo()) {
	        P.out(info.getName(), " - ", info.getDescription());
			Mixer m = AudioSystem.getMixer(info);
			mixers.add(m);
			UI.addButton(info.getName(), false);
	    }
	}

	//filter may be all if you want to include microphone 
	public void getDeviceInfos(String filter) {
		Info[] infos = AudioSystem.getMixerInfo();
		for (int i = 0; i < infos.length; i++) {
			Info info = infos[i];
			P.out("Mixer Name: " + info.getName());
			P.out("Mixer Description: " + info.getDescription());
			P.out("Mixer Vendor: " + info.getVendor());

			// Mixer m = AudioSystem.getMixer(info);

			// outputs
//			Line.Info[] sourceInfos = m.getSourceLineInfo();
//			for (int s = 0; s < sourceInfos.length; s++) {
//				P.out("=========================");
//				Line.Info lineInfo = sourceInfos[s];
////				linesOut.add(lineInfo);
//				P.out("    info: " + lineInfo);
//				try {
//					Line line = AudioSystem.getLine(lineInfo);
//					if (line instanceof SourceDataLine) {
//						Arrays.asList(((DataLine.Info) line.getLineInfo()).getFormats()).forEach(format -> {
//							P.out("#######");
//							P.out("Channels: " + format.getChannels());
//							P.out("Size in Bits: " + format.getSampleSizeInBits());
//							P.out("Frame Rate: " + format.getFrameRate());
//							P.out("Frame Size: " + format.getFrameSize());
//							P.out("Encoding: " + format.getEncoding());
//							P.out("Sample Rate: " + format.getSampleRate());
//
//						});
//					}
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//				P.out("=========================");
//			}

			// inputs
//			Line.Info[] targetInfos = m.getTargetLineInfo();
//			for (int t = 0; t < targetInfos.length; t++) {
//				P.out("=========================");
//				Line.Info lineInfo = targetInfos[t];
//				linesOut.add(lineInfo);
//				P.out("    info: " + lineInfo);
//				try {
//					Line line = AudioSystem.getLine(lineInfo);
//					if (line instanceof TargetDataLine) {
//						Arrays.asList(((DataLine.Info) line.getLineInfo()).getFormats()).forEach(format -> {
//							P.out("#######");
//							P.out("Channels: " + format.getChannels());
//							P.out("Size in Bits: " + format.getSampleSizeInBits());
//							P.out("Frame Rate: " + format.getFrameRate());
//							P.out("Frame Size: " + format.getFrameSize());
//							P.out("Encoding: " + format.getEncoding());
//							P.out("Sample Rate: " + format.getSampleRate());
//
//						});
//					}
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//				P.out("=========================");
//			}
		}
		
		// add UI to select line out
//		int numLinesOut = linesOut.size();
//		UI.addSlider(LINE_INDEX, 0, 0, numLinesOut - 1, 1, false);
		int numMixers = mixers.size();
		UI.addSlider(MIXER_INDEX, 0, 0, numMixers - 1, 1, false);
	}
	
	protected void buildLinesFromCurMixer() {
		Line.Info[] sourceInfos = curMixer().getSourceLineInfo();
		for (int s = 0; s < sourceInfos.length; s++) {
			P.out("=========================");
			Line.Info lineInfo = sourceInfos[s];
			linesOut.add(lineInfo);
			P.out("    info: " + lineInfo);
			try {
				Line line = AudioSystem.getLine(lineInfo);
				if (line instanceof SourceDataLine) {
					Arrays.asList(((DataLine.Info) line.getLineInfo()).getFormats()).forEach(format -> {
						P.out("#######");
						P.out("Channels: " + format.getChannels());
						P.out("Size in Bits: " + format.getSampleSizeInBits());
						P.out("Frame Rate: " + format.getFrameRate());
						P.out("Frame Size: " + format.getFrameSize());
						P.out("Encoding: " + format.getEncoding());
						P.out("Sample Rate: " + format.getSampleRate());

					});
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			P.out("=========================");
		}
	}
	
	protected Mixer curMixer() {
		int mixerIndex = UI.valueInt(MIXER_INDEX);
		mixerIndex = P.constrain(mixerIndex, 0, mixers.size() - 1);
		return mixers.get(mixerIndex);
	}
	
	protected Clip clipFromDefault() {
		try {
	    	Line.Info lineInfo = new Line.Info(Clip.class);
	    	Line line = AudioSystem.getLine(lineInfo);
	        Clip clip = (Clip)line;
			return clip;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Clip clipFromMixer() {
		try {
			int mixerIndex = UI.valueInt(MIXER_INDEX);
			mixerIndex = P.constrain(mixerIndex, 0, mixers.size() - 1);
			Clip clip;
			clip = AudioSystem.getClip(mixers.get(mixerIndex).getMixerInfo());
			return clip;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Clip clipFromLine() {
		try {
			// did this even work??
			int lineIndex = UI.valueInt(LINE_INDEX);
			lineIndex = P.constrain(lineIndex, 0, linesOut.size() - 1);
			Line.Info lineInfo = linesOut.get(lineIndex);
	    	Line line = AudioSystem.getLine(lineInfo);
	        Clip clip = (Clip)line;

			return clip;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Clip clipFromCurMixerLine() {
		try {
			Line.Info[] sourceInfos = curMixer().getSourceLineInfo();
			for (int s = 0; s < sourceInfos.length; s++) {
				P.out("-- sourceInfos", sourceInfos[s]);
			}
			Line.Info lineInfo = sourceInfos[1];
			P.out("*** info: " + lineInfo);
			Line line = AudioSystem.getLine(lineInfo);
//			AudioSystem.get
			DataLine.Info dataLineInfo = ((DataLine.Info) line.getLineInfo());
//			SourceDataLine srcDataLine = AudioSystem.getSourceDataLine(format, curMixer().getMixerInfo());
//			P.out("*** srcDataLine info: " + srcDataLine.getLineInfo());
			Line lineSpecific = null;
			P.out("dataLineInfo.getFormats().length", dataLineInfo.getFormats().length);
			AudioFormat format = dataLineInfo.getFormats()[0];
//			if (line instanceof SourceDataLine) {
//				Arrays.asList(((DataLine.Info) line.getLineInfo()).getFormats()).forEach(format -> {
//					P.out("#######");
					P.out("Channels: " + format.getChannels());
					P.out("Size in Bits: " + format.getSampleSizeInBits());
					P.out("Frame Rate: " + format.getFrameRate());
					P.out("Frame Size: " + format.getFrameSize());
					P.out("Encoding: " + format.getEncoding());
					P.out("Sample Rate: " + format.getSampleRate());

//				});
//			}
			DataLine.Info info = new DataLine.Info(Clip.class, format);
//			SourceDataLine srcDataLine = AudioSystem.getSourceDataLine(format, curMixer().getMixerInfo());
			lineSpecific = curMixer().getLine(info);
//			Line lineSpecific = AudioSystem.getLine(srcDataLine.getLineInfo());
//			Line lineSpecific = curMixer().getLine(srcDataLine.getLineInfo());
			Port.Info infoooo = (Port.Info)lineSpecific.getLineInfo();
			P.out("infoooo", infoooo);

			Clip clip = (Clip)lineSpecific;
			return clip;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void play(File file, Clip clip) {
	    try {
	        // get a mixer and play clip like that
	        clip.addLineListener(new LineListener() {
	            @Override
	            public void update(LineEvent event) {
	                if (event.getType() == LineEvent.Type.STOP) {
	                    clip.close();
	                }
	            }
	        });
	        clip.open(AudioSystem.getAudioInputStream(file));
	        clip.start();
	    } catch (Exception exc) {
	        exc.printStackTrace(System.out);
	    }
	}

	protected void drawApp() {
		p.background(0);
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 20);
		FontCacher.setFontOnContext(p.g, font, 255, 1, PTextAlign.LEFT, PTextAlign.TOP);
		
		// write cur lineInfo
//		int lineIndex = UI.valueInt(LINE_INDEX);
//		lineIndex = P.constrain(lineIndex, 0, linesOut.size() - 1);
		// p.g.text(""+linesOut.get(lineIndex) + FileUtil.NEWLINE + FileUtil.NEWLINE, 300, 100, 500, 1000);
		
		// show mixer info
		int mixerIndex = UI.valueInt(MIXER_INDEX);
		mixerIndex = P.constrain(mixerIndex, 0, mixers.size() - 1);
		p.g.text("Mixer:" + mixers.get(mixerIndex).getMixerInfo() + FileUtil.NEWLINE, 300, 100, 500, 1000);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') play(audioSample, clipFromMixer());
		if(p.key == 'a') play(audioSample, clipFromCurMixerLine());
		if(p.key == 'm') buildLinesFromCurMixer();
	}
	
	// UIButton callback
	
	public void uiButtonClicked(UIButton button) {
		P.out("uiButtonClicked: please override", button.id(), button.value());
	}

}

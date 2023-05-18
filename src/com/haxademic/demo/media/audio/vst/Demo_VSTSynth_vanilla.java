package com.haxademic.demo.media.audio.vst;

import java.io.FileNotFoundException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;
import com.synthbot.audioio.vst.JVstAudioThread;
import com.synthbot.audioplugin.vst.JVstLoadException;
import com.synthbot.audioplugin.vst.vst2.JVstHost2;
import com.synthbot.audioplugin.vst.vst2.JVstHostListener;

public class Demo_VSTSynth_vanilla 
extends PAppletHax
implements JVstHostListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// VST props
	private static final float SAMPLE_RATE = 44100f;
	private static final int BLOCK_SIZE = 8912;
	private JVstHost2 vst;
	private JVstAudioThread audioThread;
	private int channel = 0;
	private int velocity = 127;
	protected int curNote = 0;
	protected float[][] vstOutput = new float[][] {
		new float[BLOCK_SIZE],
		new float[BLOCK_SIZE]
	};

	// UI
	protected String UI_NOTE_INTERVAL = "UI_NOTE_INTERVAL";
	protected String UI_NOTE_START = "UI_NOTE_START";
	protected String UI_NOTE_END = "UI_NOTE_END";

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		initVST();
		initUI();
	}

	protected void initUI() {
		UI.addTitle("NOTE INTERVAL");
		UI.addSlider(UI_NOTE_INTERVAL, 60, 1, 360, 1, false);
		UI.addSlider(UI_NOTE_START, 1, 1, 360, 1, false);
		UI.addSlider(UI_NOTE_END, 50, 1, 360, 1, false);
	}

	protected void initVST() {
		// init VST on own thread
		JVstHostListener self = this;
		new Thread(new Runnable() { public void run() {
			// these are happy
			String vstFile;
			vstFile = FileUtil.getPath("vst/synth/Zebra2(x64).dll");
			vstFile = FileUtil.getPath("vst/synth/PG-8X.dll");
			vstFile = FileUtil.getPath("vst/synth/xhip_8_64bit.dll");
			vstFile = FileUtil.getPath("vst/synth/Dolphin-x64.dll");
			vstFile = FileUtil.getPath("vst/synth/YoozBL303_x64.dll");
			vstFile = FileUtil.getPath("vst/synth/Charlatan.dll");
			// these ones don't like their window opened, or at least opened automatically:
			//			String vstFile = FileUtil.getPath("vst/synth/JuceOPLVSTi_ax64.dll");
			//			String vstFile = FileUtil.getPath("vst/synth/synister64.dll");
			
			try {
				vst = JVstHost2.newInstance(FileUtil.fileFromPath(vstFile), SAMPLE_RATE, BLOCK_SIZE);
				P.out("[VST] ###################################################################");
				P.out("[VST] Loaded - " + vst.getEffectName() + " by " + vst.getVendorName());
				P.out("[VST] ..with - " + vst.numParameters() + " parameters");
				for (int i = 0; i < vst.numParameters(); i++) {
					P.out("[VST] Param [" + i + "] - " + vst.getParameterName(i) + " (" + vst.getParameterLabel(i) + "}");
				}
				P.out("[VST] ..with - " + vst.numPrograms() + " programs");
				for (int i = 0; i < vst.numPrograms(); i++) {
					P.out("[VST] Program [" + i + "] - " + vst.getProgramName(i));
				}
				vst.openEditor(vst.getEffectName());
				P.out("[VST] ###################################################################");
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace(System.err);
			} catch (JVstLoadException jvle) {
				jvle.printStackTrace(System.err);
			}
			vst.addJVstHostListener(self);
			
			// INTERVAL audio thread
			audioThread = new JVstAudioThread(vst);
			Thread thread = new Thread(audioThread);
			thread.start();
		}}).start();
	}

	protected void drawApp() {
		background(0);
		if(vst != null) {
			// play notes
			if(FrameLoop.frameMod(UI.valueInt(UI_NOTE_INTERVAL)) == UI.valueInt(UI_NOTE_START)) playMidiNote();
			if(FrameLoop.frameMod(UI.valueInt(UI_NOTE_INTERVAL)) == UI.valueInt(UI_NOTE_END)) stopMidiNote();
			
			// extract & draw waveform
//			vst.processReplacing(vstOutput, vstOutput, BLOCK_SIZE);
//			p.fill(0);
//			p.stroke(255);
//			for (int i = 0; i < vstOutput[0].length; i++) {
//				p.point(i, p.height / 2 + vstOutput[0][i] * 300f);
//			}
//			for (int i = 0; i < vstOutput[1].length; i++) {
//				p.point(i, p.height / 2 + vstOutput[1][i] * 300f);
//			}
		}
		if(Mouse.xNorm > 0.9f) oscAllParams();
		
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') randomizeAllParams();
		if(p.key == 's') stopAllNotes();
	}
	
	protected void randomizeAllParams() {
		// don't touch these charlatan params
		int[] excludeIndices = new int[] { 5, 9, 10, 11, 23, 49 };

		// randomize params!
		for (int i = 0; i < vst.numParameters(); i++) {
			if(ArrayUtil.indexOfInt(excludeIndices, i) == -1) {
				vst.setParameter(i, p.random(1));
			}
		}
		
		// charlatan-specific override ranges
		vst.setParameter(3, P.p.random(0.5f, 1)); // Osc1 Volume (dB}
		vst.setParameter(9, P.p.random(0.5f, 1)); // Osc2 Volume (dB}
		vst.setParameter(23, P.p.random(0.3f, 1)); // Filter Cutoff (Hz}
		vst.setParameter(35, P.p.random(0, 0.4f)); // Amp Attack (ms}
		// make sure some params are in range
//	    vst.setParameter(27, 1);
//	    vst.setParameter(35, 0);
//	    vst.setParameter(40, 0);
//	    vst.setParameter(47, 1);
//	    vst.setParameter(14, 1);
//	    vst.setParameter(15, 1);
		
		stopAllNotes();
	}
	
	protected void oscAllParams() {
		for (int i = 0; i < vst.numParameters(); i++) {
			vst.setParameter(i, 0.5f + 0.5f * P.sin(i + p.frameCount * 0.01f));
		}
		// make sure some params are in range
//		vst.setParameter(27, 1);
//		vst.setParameter(35, 0);
//		vst.setParameter(40, 0);
//		vst.setParameter(47, 1);
//		vst.setParameter(14, 1);
//		vst.setParameter(15, 1);
	}
	
	protected void playMidiNote() {
		try {			
			curNote = (int) (Math.random() * 36) + 36; // 48;
//			curNote = 39;
			ShortMessage midiMessage = new ShortMessage();
			midiMessage.setMessage(ShortMessage.NOTE_ON, channel, curNote, velocity);
			vst.queueMidiMessage(midiMessage);
		} catch (InvalidMidiDataException imde) {
			imde.printStackTrace(System.err);
		}
	}
	
	protected void stopMidiNote() {
		try {
			ShortMessage midiMessage = new ShortMessage();
			midiMessage.setMessage(ShortMessage.NOTE_OFF, channel, curNote, 0);
			vst.queueMidiMessage(midiMessage);
		} catch (InvalidMidiDataException imde) {
			imde.printStackTrace(System.err);
		}
	}

	protected void stopAllNotes() {
		try {
			for (int i = 0; i < 127; i++) {
				ShortMessage midiMessage = new ShortMessage();
				midiMessage.setMessage(ShortMessage.NOTE_OFF, channel, i, 0);
				vst.queueMidiMessage(midiMessage);
			}
		} catch (InvalidMidiDataException imde) {
			imde.printStackTrace(System.err);
		}
	}

	/////////////////////////////////////
	// JVstHostListener callbacks
	/////////////////////////////////////

	public void onAudioMasterAutomate(JVstHost2 arg0, int arg1, float arg2) {}
	public void onAudioMasterBeginEdit(JVstHost2 arg0, int arg1) {}
	public void onAudioMasterEndEdit(JVstHost2 arg0, int arg1) {}
	public void onAudioMasterIoChanged(JVstHost2 arg0, int arg1, int arg2, int arg3, int arg4) {}
	public void onAudioMasterProcessMidiEvents(JVstHost2 arg0, ShortMessage arg1) {}
}

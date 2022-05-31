package com.haxademic.demo.media.audio.vst;

import java.io.FileNotFoundException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;
import com.synthbot.audioio.vst.JVstAudioThread;
import com.synthbot.audioplugin.vst.JVstLoadException;
import com.synthbot.audioplugin.vst.vst2.JVstHost2;
import com.synthbot.audioplugin.vst.vst2.JVstHostListener;

public class Demo_VSTSynth_vanilla 
extends PAppletHax
implements JVstHostListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

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

	protected void firstFrame() {
		// init VST on own thread
		JVstHostListener self = this;
		new Thread(new Runnable() { public void run() {
			// these are happy
//			String vstFile = FileUtil.getPath("vst/synth/Zebra2(x64).dll");
			String vstFile = FileUtil.getPath("vst/synth/PG-8X.dll");
//			String vstFile = FileUtil.getPath("vst/synth/Charlatan.dll");
//			String vstFile = FileUtil.getPath("vst/synth/YoozBL303_x64.dll");
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
			
			// start the audio thread
			audioThread = new JVstAudioThread(vst);
			Thread thread = new Thread(audioThread);
			thread.start();
		}}).start();
	}

	protected void drawApp() {
		background(0);
		if(vst != null) {
			// play notes
			if(FrameLoop.frameMod(60) == 1) playMidiNote();
			if(FrameLoop.frameMod(60) == 49) stopMidiNote();
			
			// extract & draw waveform
			vst.processReplacing(vstOutput, vstOutput, BLOCK_SIZE);
			p.fill(0);
			p.stroke(255);
			for (int i = 0; i < vstOutput[0].length; i++) {
				p.point(i, p.height / 2 + vstOutput[0][i] * 300f);
			}
			for (int i = 0; i < vstOutput[1].length; i++) {
				p.point(i, p.height / 2 + vstOutput[1][i] * 300f);
			}
		}
		if(Mouse.xNorm > 0.9f) oscAllParams();
		
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') randomizeAllParams();
	}
	
	protected void randomizeAllParams() {
	    for (int i = 0; i < vst.numParameters(); i++) {
	    	vst.setParameter(i, p.random(1));
	    }
	    // make sure some params are in range
//	    vst.setParameter(27, 1);
//	    vst.setParameter(35, 0);
//	    vst.setParameter(40, 0);
//	    vst.setParameter(47, 1);
//	    vst.setParameter(14, 1);
//	    vst.setParameter(15, 1);
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

	/////////////////////////////////////
	// JVstHostListener callbacks
	/////////////////////////////////////

	public void onAudioMasterAutomate(JVstHost2 arg0, int arg1, float arg2) {}
	public void onAudioMasterBeginEdit(JVstHost2 arg0, int arg1) {}
	public void onAudioMasterEndEdit(JVstHost2 arg0, int arg1) {}
	public void onAudioMasterIoChanged(JVstHost2 arg0, int arg1, int arg2, int arg3, int arg4) {}
	public void onAudioMasterProcessMidiEvents(JVstHost2 arg0, ShortMessage arg1) {}
}

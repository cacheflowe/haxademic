package com.haxademic.core.media.audio.vst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.system.SystemUtil;
import com.synthbot.audioio.vst.JVstAudioThread;
import com.synthbot.audioplugin.vst.JVstLoadException;
import com.synthbot.audioplugin.vst.vst2.JVstHost2;
import com.synthbot.audioplugin.vst.vst2.JVstHostListener;

public class VSTPlugin
implements JVstHostListener {

	private static final float SAMPLE_RATE = 44100f;
	private static final int BLOCK_SIZE = 8912;
	
	protected String vstPath;
	protected JVstHost2 vst;
	protected JVstAudioThread audioThread;
	protected boolean allowsWindowOpen = true;
	protected boolean vstWindowOpen = false;
	
	private int channel = 0;
	private int velocity = 101;

	
	protected float[][] vstInput = new float[][] {
		new float[BLOCK_SIZE],
		new float[BLOCK_SIZE]
	};
	protected float[][] vstOutput = new float[][] {
		new float[BLOCK_SIZE],
		new float[BLOCK_SIZE]
	};


	public VSTPlugin(String vstPath) {
		this.vstPath = vstPath;
		initVst();
	}
	
	protected void initVst() {
		JVstHostListener self = this;
		new Thread(new Runnable() { public void run() {
			// these are happy
			String vstFile = FileUtil.getPath(vstPath);
			try {
				vst = JVstHost2.newInstance(FileUtil.fileFromPath(vstFile), SAMPLE_RATE, BLOCK_SIZE);
				printVstDebug();
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
	
	public void printVstDebug() {
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
	    P.out("[VST] ###################################################################");
	}
	
	public void playRandomNote() {
		playRandomNote(300);
	}
	
	public void playRandomNote(int holdTimeMS) {
//		int randNote = (int) (Math.random() * 36) + 36; // 48;
		int randNote = 24 + Scales.CUR_SCALE[MathUtil.randIndex(Scales.CUR_SCALE.length)];
		if(MathUtil.randBoolean()) randNote += 12;
		if(MathUtil.randBoolean()) randNote += 12;
		playMidiNote(randNote, holdTimeMS);
	}
	
	public void playMidiNote(int note, int holdTimeMS) {
		if(vst == null) return;
		try {			
			ShortMessage midiMessage = new ShortMessage();
			midiMessage.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
			vst.queueMidiMessage(midiMessage);
			
			ActionListener noteOffAction = new ActionListener() { public void actionPerformed(ActionEvent e) {
				stopMidiNote(note);
			}};
			SystemUtil.setTimeout(noteOffAction, holdTimeMS);
		} catch (InvalidMidiDataException imde) {
			imde.printStackTrace(System.err);
		}
	}
	
	public void stopMidiNote(int note) {
		if(vst == null) return;
		try {
			ShortMessage midiMessage = new ShortMessage();
			midiMessage.setMessage(ShortMessage.NOTE_OFF, channel, note, 0);
			vst.queueMidiMessage(midiMessage);
		} catch (InvalidMidiDataException imde) {
			imde.printStackTrace(System.err);
		}
	}
	
	public float[][] updateWaveform() {
		if(vst != null) {
			vst.processReplacing(vstInput, vstOutput, BLOCK_SIZE);
		}
		return vstOutput;
	}
	
	public void randomizeAllParams() {
		randomizeAllParams(null);
	}
	
	public void randomizeAllParams(int[] exceptIndexes) {
		if(vst == null) return;
	    for (int i = 0; i < vst.numParameters(); i++) {
	    	if(exceptIndexes == null || ArrayUtil.indexOfInt(exceptIndexes, i) == -1 ) {
	    		vst.setParameter(i, P.p.random(1));
	    	}
	    }
	}

	public void toggleVstUI() {
		if(!allowsWindowOpen) {
			P.error("This VST doesn't like its window opened :(");
			return;
		}
		vstWindowOpen = !vstWindowOpen;
		if(vstWindowOpen) {
			vst.openEditor(vst.getEffectName());
			vst.topEditor();
		} else {
			vst.closeEditor();
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

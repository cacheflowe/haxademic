package com.haxademic.core.media.audio.vst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;
import com.haxademic.demo.media.audio.vst.JVstAudioThreadCustom;
import com.synthbot.audioio.vst.JVstAudioThread;
import com.synthbot.audioplugin.vst.JVstLoadException;
import com.synthbot.audioplugin.vst.vst2.JVstHost2;
import com.synthbot.audioplugin.vst.vst2.JVstHostListener;

import processing.core.PGraphics;
import processing.core.PImage;

public class VSTPlugin
implements JVstHostListener, IAppStoreListener {

	private static final float SAMPLE_RATE = 44100f;
	private static final int BLOCK_SIZE = 2048;
	
	protected String vstPath;
	protected JVstHost2 vst;
	protected JVstAudioThread audioThread;
	protected boolean allowsWindowOpen = true;
	protected boolean vstWindowOpen = false;
	
	protected boolean hasUI = false;
	protected String randomizeButtonUIKey;
	
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
		this(vstPath, false, false, true);
	}
	
	public VSTPlugin(String vstPath, boolean openVstUI, boolean buildUI, boolean startsAudioThread) {
		this.vstPath = vstPath;
		P.store.addListener(this);

		// load VST
		JVstHostListener self = this;
		new Thread(new Runnable() { public void run() {
			// load VST
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
			
			// add UI options
			if(openVstUI) toggleVstUI();
			if(buildUI) buildUISliders();
			
			// start the audio thread
			if(startsAudioThread) startAudioThread();
		}}).start();
	}
	
  public void startAudioThread() {
		audioThread = new JVstAudioThread(vst);
		Thread thread = new Thread(audioThread);
		thread.start();
	}

  public void startAudioThreadWithFX(JVstHost2 vstFX) {
		JVstAudioThreadCustom audioThread2 = new JVstAudioThreadCustom(vst, vstFX, vstOutput);
		Thread thread2 = new Thread(audioThread2);
		thread2.start();
	}

	public JVstHost2 vst() {
		return vst;
	}

	public void printVstDebug() {
		P.out("[VST] ###################################################################");
		P.out("[VST] Loaded - " + vst.getEffectName() + " by " + vst.getVendorName());
		P.out("[VST] ..with - " + vst.numParameters() + " parameters");
		for (int i = 0; i < vst.numParameters(); i++) {
			P.out("[VST] Param [" + i + "] - " + vst.getParameterName(i) + " (" + vst.getParameterLabel(i) + ")");
		}
		P.out("[VST] ..with - " + vst.numPrograms() + " programs");
		for (int i = 0; i < vst.numPrograms(); i++) {
			P.out("[VST] Program [" + i + "] - " + vst.getProgramName(i));
		}
		P.out("[VST] ###################################################################");
	}
	
	public String getVstName() {
		return vst.getEffectName();
	}
	
	public String getVstVendor() {
		return vst.getVendorName();
	}
	
	protected String vstUIKeyForParamIndex(int i) {
		return getVstName() + " |" + i + "| " + vst.getParameterName(i);
	}
	
	protected void buildUISliders() {
		hasUI = true;
		UI.addTitle(getVstName());
		randomizeButtonUIKey = getVstName() + " | Randomize!";
		UI.addButton(randomizeButtonUIKey, false);
			for (int i = 0; i < vst.numParameters(); i++) {
				float initVal = vst.getParameter(i);
				UI.addSlider(vstUIKeyForParamIndex(i), initVal, 0, 1, 0.005f, false);
			}
	}
	
	protected void syncUIToVstUI() {
		if(!hasUI) return;
			for (int i = 0; i < vst.numParameters(); i++) {
				float initVal = vst.getParameter(i);
				UI.setValue(vstUIKeyForParamIndex(i), initVal);
			}
	}
	
	public void playRandomNote() {
		playRandomNote(300);
	}
	
	public void playRandomNote(int holdTimeMS) {
		playRandomNote(24, holdTimeMS);
	}
	
	public void playRandomNote(int baseNote, int holdTimeMS) {
//		int randNote = (int) (Math.random() * 36) + 36; // 48;
		int randNote = 36 + Scales.CUR_SCALE[MathUtil.randIndex(Scales.CUR_SCALE.length)];
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
		syncUIToVstUI();
	}
	
	public void randomizeAllParams(int[] excludeIndices) {
		if(vst == null) return;
			for (int i = 0; i < vst.numParameters(); i++) {
				if(excludeIndices == null || ArrayUtil.indexOfInt(excludeIndices, i) == -1 ) {
					vst.setParameter(i, P.p.random(1));
				}
			}
			syncUIToVstUI();
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

	/////////////////////////////////////
	// AppStore callbacks
	/////////////////////////////////////
	
	public void updatedNumber(String key, Number val) {
		// get index from UI slider key, and update params
		if(vst != null && hasUI && key.contains(getVstName())) {
			int indexIndex = key.indexOf("|") + 1;
			int index2Index = key.indexOf("|", indexIndex);
			if(indexIndex > 0 && index2Index > 0) {
				int vstParamIndex = ConvertUtil.stringToInt(key.substring(indexIndex, index2Index));
				vst.setParameter(vstParamIndex, val.floatValue());
			}
		}
		if(vst != null && hasUI && key.equals(randomizeButtonUIKey)) {
			randomizeAllParams();
		}
	}
	public void updatedString(String key, String val) {
//		if(key.equals(PEvents.KEY_PRESSED) && val.equals("p")) playRandomNote(300);
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}

package com.haxademic.demo.media.audio.vst;

import java.io.FileNotFoundException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;
import com.synthbot.audioplugin.vst.JVstLoadException;
import com.synthbot.audioplugin.vst.vst2.JVstHost2;
import com.synthbot.audioplugin.vst.vst2.JVstHostListener;
import com.synthbot.audioplugin.vst.vst2.VstPluginCanDo;

public class Demo_VSTSynthAndEffects 
extends PAppletHax
implements JVstHostListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// docs: https://github.com/mhroth/jvsthost/blob/master/src/com/synthbot/audioplugin/vst/vst2/JVstHost2.java
	// look at: https://www.javatips.net/api/jvsthost-master/src/com/synthbot/audioio/vst/JVstAudioThread.java
	
	private static final float SAMPLE_RATE = 44100f;
	private static final int BLOCK_SIZE = 8912;
	private JVstHost2 vst;
	private JVstHost2 vstFX;
	private JVstAudioThreadCustom audioThread;
	private int channel = 0;
	private int velocity = 127;
	protected int curNote = 0;
	protected float[][] vstInput = new float[][] {new float[BLOCK_SIZE], new float[BLOCK_SIZE]};
	protected float[][] vstOutput = new float[][] {new float[BLOCK_SIZE], new float[BLOCK_SIZE]};
	protected float[][] vst2Input = new float[][] {new float[BLOCK_SIZE], new float[BLOCK_SIZE]};
	protected float[][] vst2Output = new float[][] {new float[BLOCK_SIZE], new float[BLOCK_SIZE]};

	protected void firstFrame() {
		for (int i = 0; i < BLOCK_SIZE; i++) { vstInput[0][i] = 0; vstInput[1][i] = 0; }
		for (int i = 0; i < BLOCK_SIZE; i++) { vstOutput[0][i] = 0; vstOutput[1][i] = 0; }
		for (int i = 0; i < BLOCK_SIZE; i++) { vst2Input[0][i] = 0; vst2Input[1][i] = 0; }
		for (int i = 0; i < BLOCK_SIZE; i++) { vst2Output[0][i] = 0; vst2Output[1][i] = 0; }
		
		// init VST on own thread
		JVstHostListener self = this;
		new Thread(new Runnable() { public void run() {
//			String vstFile = FileUtil.getPath("vst/synth/Phosphor.dll");
			String vstFile = FileUtil.getPath("vst/synth/PG-8X.dll");
			// String vstFile = FileUtil.getPath("vst/synth/YoozBL303_x64.dll");
//			String vstFile = FileUtil.getPath("vst/synth/synister64.dll");
//			String vstFile = FileUtil.getPath("vst/synth/Charlatan.dll");
//			String vstFile2 = FileUtil.getPath("vst/fx/Protoverb_x64.dll");
//			String vstFile2 = FileUtil.getPath("vst/fx/ValhallaFreqEcho_x64.dll");
			String vstFile2 = FileUtil.getPath("vst/fx/DubStation_15.dll");
			try {
				vst = JVstHost2.newInstance(FileUtil.fileFromPath(vstFile), SAMPLE_RATE, BLOCK_SIZE);
				P.out("[VST] ###################################################################");
				P.out("[VST] Loaded - " + vst.getEffectName() + " by " + vst.getVendorName());
				P.out("[VST] Class - " + vst.getClass().getSimpleName());
				P.out("[VST] ..with - " + vst.numParameters() + " parameters");
					for (int i = 0; i < vst.numParameters(); i++) {
						P.out("[VST] Param [" + i + "] - " + vst.getParameterName(i) + " (" + vst.getParameterLabel(i) + "}");
					}
					P.out("[VST] ..with - " + vst.numPrograms() + " programs");
					for (int i = 0; i < vst.numPrograms(); i++) {
						P.out("[VST] Program [" + i + "] - " + vst.getProgramName(i));
					}
//			    vst.openEditor(vst.getEffectName());
					P.out("[VST] ###################################################################");
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace(System.err);
			} catch (JVstLoadException jvle) {
				jvle.printStackTrace(System.err);
			}
			try {
				vstFX = JVstHost2.newInstance(FileUtil.fileFromPath(vstFile2), SAMPLE_RATE, BLOCK_SIZE);
				P.out("[VST] ###################################################################");
				P.out("[VST] Loaded - " + vstFX.getEffectName() + " by " + vstFX.getVendorName());
				P.out("[VST] Class - " + vstFX.getClass().getSimpleName());
				P.out("[VST] ..with - " + vstFX.numParameters() + " parameters");
				for (int i = 0; i < vstFX.numParameters(); i++) {
					P.out("[VST] Param [" + i + "] - " + vstFX.getParameterName(i) + " (" + vstFX.getParameterLabel(i) + "}");
				}
				P.out("[VST] ..with - " + vstFX.numPrograms() + " programs");
				for (int i = 0; i < vstFX.numPrograms(); i++) {
					P.out("[VST] Program [" + i + "] - " + vstFX.getProgramName(i));
				}
//				vstFX.openEditor(vstFX.getEffectName());
//				vstFX.setBlockSize(BLOCK_SIZE);
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace(System.err);
			} catch (JVstLoadException jvle) {
				jvle.printStackTrace(System.err);
			}
			vst.addJVstHostListener(self);
//			vstFX.addJVstHostListener(self);
//			vstFX.turnOn();
			
				P.out(vst.canReplacing() + "?");
				P.out(vstFX.canReplacing() + "??");

//			vst.processReplacing(vstInput, vstOutput, BLOCK_SIZE);
			P.out(vstFX.canDo(VstPluginCanDo.RECEIVE_VST_EVENTS));
//			vstFX.processReplacing(vst2Input, vst2Output, BLOCK_SIZE);
			
			// start the audio thread
//			audioThread = new JVstAudioThreadCustom(vst, vstOutput);
//			Thread thread = new Thread(audioThread);
//			thread.start();
			audioThread = new JVstAudioThreadCustom(vst, vstFX, vstOutput);
			Thread thread2 = new Thread(audioThread);
			thread2.start();
			
			while (true) {
//				vst.processReplacing(vstOutput, vstOutput, BLOCK_SIZE);
//				vstFX.processReplacing(vstOutput, vstOutput, 1024);
//
//				//		        vst.processReplacing(fInputs, fOutputs, blockSize);
//				//		        sourceDataLine.write(floatsToBytes(fOutputs, bOutput), 0, bOutput.length);
			}
		}}).start();
	}

	protected void drawApp() {
		background(0);
		if(vst != null) {
			if(FrameLoop.frameMod(60) == 1) playMidiNote();
			if(FrameLoop.frameMod(60) == 19) stopMidiNote();
			
			
//			vst.processReplacing(vstInput, vstOutput, BLOCK_SIZE);
//			vst.processReplacing(vstOutput, vstOutput, 1024);
//			vstFX.processReplacing(vst2Input, vst2Output, BLOCK_SIZE);

			/*
			p.fill(0);
			p.stroke(255);
			for (int i = 0; i < vstOutput[0].length; i++) {
				p.point(i, p.height / 2 + vstOutput[0][i] * 300f);
			}
			for (int i = 0; i < vstOutput[1].length; i++) {
				p.point(i, p.height / 2 + vstOutput[1][i] * 300f);
			}
			*/
		}
		if(Mouse.xNorm > 0.9f) oscAllParams();
		
		
		if(frameCount == 100) {
			vst.openEditor(vst.getEffectName());
//			vstFX.openEditor(vstFX.getEffectName());
		}
		
		// hard-coded audio fx
		if(vstFX != null) {
//			vstFX.setProgram(5);
			vstFX.setParameter(0, 0.75f);
			vstFX.setParameter(1, 0.5f);
			vstFX.setParameter(2, 0.005f);
			vstFX.setParameter(3, 0.5f);
//		vstFX.setParameter(4, 0.5f);
//		vstFX.setParameter(8, 0.5f);
//		vstFX.setParameter(9, 0.5f);
//		vstFX.setParameter(10, 0.5f);
		}
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
		vst.setParameter(27, 1);
		vst.setParameter(35, 0);
		vst.setParameter(40, 0);
		vst.setParameter(47, 1);
		vst.setParameter(14, 1);
		vst.setParameter(15, 1);
	}
	
	protected void oscAllParams() {
		for (int i = 0; i < vst.numParameters(); i++) {
			vst.setParameter(i, 0.5f + 0.5f * P.sin(i + p.frameCount * 0.01f));
		}
		// make sure some params are in range
		vst.setParameter(27, 1);
		vst.setParameter(35, 0);
		vst.setParameter(40, 0);
		vst.setParameter(47, 1);
		vst.setParameter(14, 1);
		vst.setParameter(15, 1);
	}
	
	protected void playMidiNote() {
		try {			
			curNote = (int) (Math.random() * 36) + 36; // 48;
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

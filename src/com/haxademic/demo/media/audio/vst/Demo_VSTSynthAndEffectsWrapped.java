package com.haxademic.demo.media.audio.vst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.synth.SynthDolphin;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VSTSynthAndEffectsWrapped 
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Metronome metronome;
	
	protected VSTPlugin vstSynth;
	protected VSTPlugin vstFX;
	
	protected LinearFloat trigger = new LinearFloat(0, 0.03f);

	protected void config() {
		Config.setAppSize(512, 512);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		
		// build metronome & bpm slider
		metronome = new Metronome(true);
		P.store.setNumber(Interphase.BPM, 96);
		P.store.addListener(this);
		UI.addSlider(Interphase.BPM, P.store.getInt(Interphase.BPM), 20, 240, 1, false);
		
		// build synth & fx
		vstSynth = new SynthDolphin(true, true, false);
		vstFX = new VSTPlugin("vst/fx/DubStation_15.dll", true, true, false);

		// wait for async audio threads to be ready
		SystemUtil.setTimeout(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vstSynth.startAudioThreadWithFX(vstFX.vst());
			}
		}, 100);
	}

	protected void drawApp() {
		trigger.update();
		background(30 * trigger.value());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') ((SynthDolphin) vstSynth).randomizeAllParams();
		if(p.key == 'v') vstSynth.toggleVstUI();
		if(p.key == 'p' && vstSynth != null) {
			vstSynth.playRandomNote(300);
			trigger.setTarget(0).setCurrent(1);
		}
	}

    @Override
    public void updatedNumber(String key, Number val) {
        if(key.equals(Interphase.CUR_STEP)) {
            if(MathUtil.randBooleanWeighted(0.3f)) {
							trigger.setTarget(0).setCurrent(1);
              vstSynth.playRandomNote(36, 300);
            }
        }
    }
    public void updatedString(String key, String val) {}
    public void updatedBoolean(String key, Boolean val) {}
    public void updatedImage(String key, PImage val) {}
    public void updatedBuffer(String key, PGraphics val) {}

}

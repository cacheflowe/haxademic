package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.dmx.DMXWrapper;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.media.audio.analysis.AudioIn;

import beads.AudioContext;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;

public class Demo_DmxUSBProMIDIFeet
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx;
	
	protected boolean audioActive = false;
	
	AudioContext ac;
	Sample sample01;
	int sampleTime01 = 0;
	Sample sample02;
	int sampleTime02 = 0;
	
	protected InputTrigger trigger1 = new InputTrigger().addKeyCodes(new char[]{'1'}).addMidiNotes(new Integer[]{43});
	protected EasingColor color1 = new EasingColor(0xff00ff00, 8);
	protected InputTrigger trigger2 = new InputTrigger().addKeyCodes(new char[]{'2'}).addMidiNotes(new Integer[]{49});
	protected EasingColor color2 = new EasingColor(0xffff0000, 8);


	public void firstFrame() {
		AudioIn.instance();
		dmx = new DMXWrapper();

		ac = new AudioContext();
		sample01 = SampleManager.sample(FileUtil.getPath("audio/kit808/kick.wav"));
		sample02 = SampleManager.sample(FileUtil.getPath("audio/kit808/snare.wav"));
		ac.start();
	}

	public void drawApp() {
		background(0);
		
		if(trigger1.triggered() && p.millis() > sampleTime01 + 200) {
			sampleTime01 = p.millis();
			color1.setCurrentInt(0xffffffff);
			color1.setTargetInt(0xff005500);
			SamplePlayer samplePlayer01 = new SamplePlayer(ac, sample01);
			samplePlayer01.setKillOnEnd(true);
			ac.out.addInput(samplePlayer01);
			samplePlayer01.start();
		}
		if(trigger2.triggered() && p.millis() > sampleTime02 + 200) {
			sampleTime02 = p.millis();
			color2.setCurrentInt(0xffffffff);
			color2.setTargetInt(0xff000055);
			SamplePlayer samplePlayer02 = new SamplePlayer(ac, sample02);
			samplePlayer02.setKillOnEnd(true);
			ac.out.addInput(samplePlayer02);
			samplePlayer02.start();
		}
		
		color1.update();
		color2.update();
		
		if(!audioActive) {
			dmx.setValue(1, (int)color1.r());
			dmx.setValue(2, (int)color1.g());
			dmx.setValue(3, (int)color1.b());
			dmx.setValue(4, (int)color2.r());
			dmx.setValue(5, (int)color2.g());
			dmx.setValue(6, (int)color2.b());
		} else {
			dmx.setValue(1, P.round(255 * AudioIn.audioFreq(10)));
			dmx.setValue(2, P.round(255 * AudioIn.audioFreq(20)));
			dmx.setValue(3, P.round(255 * AudioIn.audioFreq(40)));
			dmx.setValue(4, P.round(255 * AudioIn.audioFreq(60)));
			dmx.setValue(5, P.round(255 * AudioIn.audioFreq(80)));
			dmx.setValue(6, P.round(255 * AudioIn.audioFreq(100)));
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') audioActive = !audioActive;
	}
}






package com.haxademic.core.media.audio.interphase;


import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.math.easing.LinearFloat;

import processing.core.PGraphics;
import processing.core.PImage;

public class SequencerTexture
implements ISequencerDrawable {
	
	protected int index;
	protected int lastStep = -1;
	protected SequenceRect[] rects;
	protected PGraphics buffer;
	
	public SequencerTexture(int index) {
		this.index = index;
		buffer = PG.newPG(120, 320);
		DebugView.setTexture("SequencerTexture_"+index, buffer);
		rects = new SequenceRect[Interphase.NUM_STEPS];
		for (int i = 0; i < Interphase.NUM_STEPS; i++) {
			rects[i] = new SequenceRect(i);
		}
	}
	
	public PGraphics buffer() {
		return buffer;
	}
	
	// ISequencerDrawable methods
	
	public void update(boolean[] steps, int curStep) {
		// draw update
		buffer.beginDraw();
		buffer.background(0);
		buffer.noStroke();
		if(curStep != lastStep) {
			lastStep = curStep;
			for (int i = 0; i < Interphase.NUM_STEPS; i++) {
				rects[i].setBeat(curStep);
			}
		}
		for (int i = 0; i < Interphase.NUM_STEPS; i++) {
			rects[i].setActive(steps[i]);
			rects[i].update(buffer);
		}
		
		// aftermarket image overlay
		/*
		if(P.store.getInt(Interphase.BEAT) % 128 >= 64) {
			PImage overlay = ImageCacher.get("images/_sketch/sheraton-window-overlay.png");
			buffer.image(overlay, 0, 0, buffer.width, buffer.height);
		}
		*/
		
		
		buffer.endDraw();
	}
	
	// internal objects
	
	public class SequenceRect {

		protected static final int COLOR_INT_WHITE = 0xffffffff;
		protected static final int COLOR_INT_BLACK = 0xff000000;
		protected static final int COLOR_INT_CLEAR = 0x00ffffff;

		protected EasingColor stepColors;
		protected EasingColor stepActiveColors;
		protected LinearFloat stepCirclesProgress;
		protected LinearFloat stepFlashProgress;
		
		protected int rectIndex;
		protected boolean active = false;

		public SequenceRect(int rectIndex) {
			this.rectIndex = rectIndex;
			// init lerping color objects
			stepColors = new EasingColor(0xffffffff, 8);
			stepActiveColors = new EasingColor(0xffffffff, 8);
			stepCirclesProgress = new LinearFloat(0, 0.05f);
			stepFlashProgress = new LinearFloat(0, 0.1f);
		}
		
		public void update(PGraphics buffer) {
			// update lerping colors
			stepColors.update();
			stepActiveColors.update();
			stepCirclesProgress.update();
			stepFlashProgress.update();
			
			// position rect
			float rectW = buffer.width;
			float rectH = buffer.height / Interphase.NUM_STEPS;
			float rectY = rectH * rectIndex;
			
			// draw rect
			buffer.push();
			
			// base color
			float padding = 1;
			float padding2 = padding * 2;
			float padding4 = padding * 4;
			buffer.fill(stepColors.colorInt());
			buffer.rect(padding2, rectY + padding2, rectW - padding4, rectH - padding4);

			// flash color
			buffer.fill(255, stepFlashProgress.value() * 255);
			buffer.rect(padding2, rectY + padding2, rectW - padding4, rectH - padding4);
			
			buffer.pop();
		}
		
		public void setBeat(int curStep) {
			if(curStep == rectIndex) {
				stepFlashProgress.setCurrent(1);
				stepFlashProgress.setTarget(0);
			}
		}

		public void setActive(boolean isActive) {
			// save state only when switching
//			if(isActive != active) {
				active = isActive;	
				
				// show sequence pattern
				int activeColor = (active) ? ColorsHax.COLOR_GROUPS[1][rectIndex % 4] : 0xff333333;
				stepColors.setTargetInt(activeColor);
//			}
		}
	}
	
}

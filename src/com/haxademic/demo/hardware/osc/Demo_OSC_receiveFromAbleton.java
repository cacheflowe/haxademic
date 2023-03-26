package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscProperties;

public class Demo_OSC_receiveFromAbleton 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	OscP5 oscP5;
	protected StringBufferLog logOut = new StringBufferLog(10);
	protected boolean kickQueued = false;
	protected boolean hatQueued = false;
	protected boolean hat2Queued = false;
	protected boolean snareQueued = false;
	protected boolean noteOn = false;
	protected int[] colors;
	protected int colorSetIndex = 0;
	protected EasingFloat zoom = new EasingFloat(1f, 0.2f);
	protected EasingFloat rot = new EasingFloat(0f, 0.2f);
	protected EasingFloat offsetX = new EasingFloat(0f, 0.2f);
	protected EasingFloat offsetY = new EasingFloat(0f, 0.2f);

	protected void firstFrame() {
		// The 255... multicast address works! Add this info into the
		// Ableton "Connection Kit" -> OSC MIDI Send plugin
		OscProperties properties = new OscProperties();
		properties.setNetworkProtocol(OscProperties.MULTICAST);
		properties.setRemoteAddress("255.255.255.255", 7777);
		oscP5 = new OscP5(this, properties);    

		// add event listener methods
		oscP5.plug(this, "receive", "/Note1");
		oscP5.plug(this, "receive", "/Note2");
		oscP5.plug(this, "receive", "/Note3");
		oscP5.plug(this, "receive", "/Note4");
		oscP5.plug(this, "receiveVel", "/Velocity1");
		oscP5.plug(this, "receiveVel", "/Velocity2");
		oscP5.plug(this, "receiveVel", "/Velocity3");
		oscP5.plug(this, "receiveVel", "/Velocity4");

		// keep debug up
		DebugView.autoHide(false);
		DebugView.active(true);
	}

	protected void drawApp() {
		// draw shapes
		PG.setTextureRepeat(pg, true);
		pg.beginDraw();
		
		// background / feedback
		colors = ColorsHax.COLOR_GROUPS[colorSetIndex];
		PG.feedback(pg, colors[0], 0.01f, 1);
		
		// fade out
		if(kickQueued) {
			BrightnessStepFilter.instance().setBrightnessStep(-25f/255f);
			BrightnessStepFilter.instance().applyTo(pg);
		}
		
		// feedback
		zoom.update();
		rot.update();
		offsetX.update();
		offsetY.update();
		RotateFilter.instance().setZoom(zoom.value());
		RotateFilter.instance().setOffset(offsetX.value(), offsetY.value());
		RotateFilter.instance().setRotation(rot.value());
		RotateFilter.instance().applyTo(pg);
		
		// draw shapes
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.push();
		if(kickQueued) {
			pg.stroke(colors[2]);
			pg.noFill();
			pg.strokeWeight(10);
			pg.rect(0, 0, 200, 200);
			kickQueued = false;
		}
		if(hatQueued) {
			pg.stroke(colors[1]);
			pg.noFill();
			pg.strokeWeight(10);
			pg.push();
			pg.rotate(P.QUARTER_PI);
			pg.rect(0, 0, 50, 50);
			pg.pop();
			hatQueued = false;
		}
		if(hat2Queued) {
			pg.stroke(colors[4]);
			pg.noFill();
			pg.strokeWeight(10);
			pg.push();
//			pg.rotate(p.random(-P.PI, P.PI));
			pg.rect(0, 0, 50, 50);
			pg.pop();
			hat2Queued = false;
		}
		if(snareQueued) {
			pg.stroke(colors[3]);
			pg.noFill();
			pg.strokeWeight(20);
			pg.ellipse(0, 0, 200, 200);
			snareQueued = false;
			
			// update effects params
			zoom.setTarget(MathUtil.randRangeDecimal(0.97f, 1.02f));
			rot.setTarget(MathUtil.randRangeDecimal(-0.005f, 0.005f));
			offsetX.setTarget(MathUtil.randRangeDecimal(-0.005f, 0.005f));
			offsetY.setTarget(MathUtil.randRangeDecimal(-0.005f, 0.005f));
			
			// and colors
			if(MathUtil.randBooleanWeighted(0.2f)) {
				colorSetIndex = MathUtil.randIndex(ColorsHax.COLOR_GROUPS.length);
			}
		}
		pg.pop();
		pg.endDraw();
		
		p.image(pg, 0, 0);
		
		// debug
		logOut.printToScreen(p.g, 20, 340);
	}
	
	protected void newNote(int note) {
		if(!noteOn) return;
		if(note == 60) kickQueued = true;
		if(note == 61) snareQueued = true;
		if(note == 62) hatQueued = true;
		if(note == 63) hat2Queued = true;
	}

	////////////////////////
	// OscP5 callbacks
	////////////////////////

	// plugged routes must match the incoming data type!
	
	public void receive(int i) {
		logOut.update("receive     " + i);
		newNote(i);
	}

	public void receiveVel(int i) {
		// velocity comes in before note. if it's an `off` signal, don't queue the next note
		logOut.update("receiveVel " + i);
		noteOn = i > 0;
	}
	
	// common callback 
	
	void oscEvent(OscMessage theOscMessage) {
		if (theOscMessage.isPlugged() == false) {
			P.out("UNPLUGGED: " + theOscMessage);
		}
	}

}
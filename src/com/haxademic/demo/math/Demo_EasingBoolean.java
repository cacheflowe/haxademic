package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingBoolean.IEasingBooleanCallback;
import com.haxademic.core.math.easing.LinearFloat;

public class Demo_EasingBoolean 
extends PAppletHax
implements IEasingBooleanCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingBoolean booleanSwitch;
	protected LinearFloat showUpdatedValue = new LinearFloat(0, 0.025f);
	
	protected void firstFrame() {
		booleanSwitch = new EasingBoolean(false, 120, this);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCenter(p);
		
		// update boolean and set the current target
		booleanSwitch.target(Mouse.xNorm > 0.5f);
		booleanSwitch.update();

		// debug: show progress
		p.fill(255);
		p.rect(booleanSwitch.progress() * p.width, p.height - 50, 100, 100);
		
		// debug: show updated valu
		PG.setDrawCorner(p);
		showUpdatedValue.update();
		if(showUpdatedValue.value() > 0) {
			p.fill(255f * showUpdatedValue.value());
			p.textSize(100);
			p.textAlign(P.CENTER, P.CENTER);
			p.text("" + booleanSwitch.value(), 0, 0, p.width, p.height - 100);
		}
	}

	@Override
	public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
		// switch timing per direction
		if(value == true) booleanSwitch.setInc(60);
		else booleanSwitch.setInc(120);
		
		// debug text fade reset
		showUpdatedValue.setCurrent(1);
		showUpdatedValue.setTarget(0);
	}
}

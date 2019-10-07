package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.IEasingValue;
import com.haxademic.core.math.easing.IEasingValue.IEasingValueDelegate;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

public class Demo_IEasingValueDelegate
extends PAppletHax
implements IEasingValueDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingFloat easingFloat;
	protected LinearFloat linearFloat;

	public void setupFirstFrame() {
		easingFloat = new EasingFloat(0, 0.1f, this);
		easingFloat.setTarget(1);
		linearFloat = new LinearFloat(0, 0.015f, this);
		linearFloat.setTarget(1);
	}

	public void drawApp() {
		// set context
		p.background(0);
		p.fill(255);
		p.stroke(0, 255, 0);
		PG.setDrawCenter(p.g);
		
		// update values & draw objects
		
		// EasingFloat
		easingFloat.update(true);	
		easingFloat.setCompleteThreshold(0.001f);
		text("EasingFloat: " + 
				FileUtil.NEWLINE + "easeFactor: " + easingFloat.easeFactor() + 
				FileUtil.NEWLINE + "value: " + easingFloat.value() + 
				FileUtil.NEWLINE + "target: " + easingFloat.target() 
				, 0.25f * p.width, 0.333f * p.height - 60);
		p.circle(0.25f * p.width + 0.5f * easingFloat.value() * p.width, 0.333f * p.height, 10);
		
		// LinearFloat
		linearFloat.update();
		text("LinearFloat: " + 
				FileUtil.NEWLINE + "inc: " + linearFloat.inc() + 
				FileUtil.NEWLINE + "value: " + linearFloat.value() + 
				FileUtil.NEWLINE + "target: " + linearFloat.target()
				, 0.25f * p.width, 0.666f * p.height - 60);
		p.circle(0.25f * p.width + 0.5f * linearFloat.value() * p.width, 0.666f * p.height, 10);
		
		float easedLinear = Penner.easeInOutQuad(linearFloat.value());
		p.circle(0.25f * p.width + 0.5f * easedLinear * p.width, 0.666f * p.height + 20, 10);
	}
	
	//////////////////////////////////////
	// IEasingValueDelegate callback
	//////////////////////////////////////

	@Override
	public void complete(IEasingValue easingObject) {
		// pick random new easing speeds, and toggle direction on complete
		if(easingObject == easingFloat) {
			P.out("EasingFloat", easingObject.value());
			easingFloat.setTarget((easingFloat.value() == 1) ? 0 : 1);
			easingFloat.setEaseFactor(MathUtil.randRangeDecimal(0.1f, 0.25f));
			if(MathUtil.randBoolean()) easingFloat.setDelay(MathUtil.randRange(0, 40));
		}
		if(easingObject == linearFloat) {
			P.out("LinearFloat", easingObject.value());
			linearFloat.setTarget((linearFloat.value() == 1) ? 0 : 1);
			linearFloat.setInc(MathUtil.randRangeDecimal(0.003f, 0.05f));
			if(MathUtil.randBoolean()) linearFloat.setDelay(MathUtil.randRange(0, 40));
		}
	}

}

package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;

public class DistributionCurveTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();
		background(0);
	}
	
	public void drawApp() {
		p.stroke(255);
		for (int i = 0; i < 300; i++) {
			float x = MathUtil.randRange(0, p.width);
			float y = randomCurvedDistribution() * (float) p.height;
			p.point(x, y);
		}
	}
	
	public float randomCurvedDistribution() {
		/*
		 * t: current time
		 * b: start value
		 * c: change in value
		 * d: duration
		 * http://easings.net/
		 */
		float equationPadding = 0.4f;
		float easedPercent = Penner.easeInOutCirc(MathUtil.randRangeDecimal(equationPadding,1f-equationPadding), 0, 1, 1);
		float minCurveVal = Penner.easeInOutCirc(equationPadding, 0, 1, 1);
		easedPercent = P.map(easedPercent, minCurveVal, 1f - minCurveVal, 0f, 1f);	// since we're starting on the penner curve, we need to remap from min/max values on that curve
		easedPercent += 0.5f;
		if(easedPercent > 1f) easedPercent -= 1f;
		return easedPercent;
	}
}

package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;

public class Demo_TrigDistribute
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String numPoints = "numPoints";
	protected String radius = "radius";
	protected String connectionDivisions = "connectionDivisions";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}

	public void setupFirstFrame() {
		p.ui.addSlider(numPoints, 3, 3, 90, 1, false);
		p.ui.addSlider(radius, 100, 0, 300, 1f, false);
		p.ui.addSlider(connectionDivisions, 2, 0, 20, 1, false);
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.stroke(255);
		p.strokeWeight(2);

		int centerX = p.width / 2;
		int centerY = p.height / 2;
		float segmentRadians = P.TWO_PI / p.ui.value(numPoints);
		for( int i=0; i < p.ui.value(numPoints); i++ ) {
			float amp = 1 + 1*p.audioFreq(i%p.ui.valueInt(numPoints));
			float x = centerX + P.sin(segmentRadians * i) * p.ui.value(radius) * amp;
			float y = centerY + P.cos(segmentRadians * i) * p.ui.value(radius) * amp;			
			p.ellipse(x, y, 10, 10);
			
			// connect lines
			for( int j=1; j <= p.ui.value(connectionDivisions); j++ ) {
				float xDiv = centerX + P.sin(segmentRadians * ((i+p.ui.value(numPoints)/j)%p.ui.value(numPoints))) * p.ui.value(radius) * amp;
				float yDiv = centerY + P.cos(segmentRadians * ((i+p.ui.value(numPoints)/j)%p.ui.value(numPoints))) * p.ui.value(radius) * amp;			
				p.line(x, y, xDiv, yDiv);
			}
		}
	}
}

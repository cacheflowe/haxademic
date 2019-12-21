package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.ui.UI;

public class Demo_TrigDistribute
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String numPoints = "numPoints";
	protected String radius = "radius";
	protected String connectionDivisions = "connectionDivisions";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	public void firstFrame() {
		AudioIn.instance();
		UI.addSlider(numPoints, 3, 3, 90, 1, false);
		UI.addSlider(radius, 100, 0, 300, 1f, false);
		UI.addSlider(connectionDivisions, 2, 0, 20, 1, false);
	}

	public void drawApp() {
		background(0);
		PG.setDrawCenter(p);
		p.fill(255);
		p.stroke(255);
		p.strokeWeight(2);

		int centerX = p.width / 2;
		int centerY = p.height / 2;
		float segmentRadians = P.TWO_PI / UI.value(numPoints);
		for( int i=0; i < UI.value(numPoints); i++ ) {
			float amp = 1 + 1*AudioIn.audioFreq(i%UI.valueInt(numPoints));
			float x = centerX + P.sin(segmentRadians * i) * UI.value(radius) * amp;
			float y = centerY + P.cos(segmentRadians * i) * UI.value(radius) * amp;			
			p.ellipse(x, y, 10, 10);
			
			// connect lines
			for( int j=1; j <= UI.value(connectionDivisions); j++ ) {
				float xDiv = centerX + P.sin(segmentRadians * ((i+UI.value(numPoints)/j)%UI.value(numPoints))) * UI.value(radius) * amp;
				float yDiv = centerY + P.cos(segmentRadians * ((i+UI.value(numPoints)/j)%UI.value(numPoints))) * UI.value(radius) * amp;			
				p.line(x, y, xDiv, yDiv);
			}
		}
	}
}

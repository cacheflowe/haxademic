package com.haxademic.demo.math;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.ui.UI;

public class Demo_FloatBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FloatBuffer buffX;
	protected FloatBuffer buffY;
	protected String AUTO_MODE = "AUTO_MODE";
	
	protected void firstFrame() {
		// build buffers for 2 axis
		buffX = new FloatBuffer(60);
		buffY = new FloatBuffer(60);
		
		// UI
		UI.addTitle("Demo Controls");
		UI.addToggle(AUTO_MODE, false, false);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		PG.setDrawCenter(p);
		
		// collect data
		if(UI.valueToggle(AUTO_MODE)) {
			// random positions
			buffX.update(MathUtil.randRangeDecimal(p.width/2 - 100, p.width/2 + 100));
			buffY.update(MathUtil.randRangeDecimal(p.height/2 - 100, p.height/2 + 100));
		} else {
			// update mouse position
			buffX.update(Mouse.x);
			buffY.update(Mouse.y);
		}
		
		// display average
		p.fill(0, 255, 0);
		p.ellipse(buffX.average(), buffY.average(), 40, 40);
		
		// extra info
		p.fill(255);
		p.text("Variance: " + buffX.variance(), 20, p.height - 50);
	}
}

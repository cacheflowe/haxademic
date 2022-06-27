package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;

public class Demo_Arcsine_SquareToCircle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setAppSize(1024, 1024);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCenter(p);
		
		// remap x coords to circular constraints
		p.fill(255);
		float spacing = 32;
		float yScroll = 0; // p.frameCount % spacing;
		for (float x = + spacing/2; x <= p.width; x+=spacing) {
			for (float y = spacing/2 + yScroll; y <= p.height; y+=spacing) {
				float xMapped = MathUtil.mapCoordToCircular(x, y - spacing, p.width, p.height - spacing * 2);
				float xAmp = mapCoordToCircularAmp(x, y - spacing, p.width, p.height - spacing * 2);
				float finalX = P.lerp(x, xMapped, Mouse.xNorm);
				p.circle(finalX, y, spacing/2);
			}
		}
	}
	
	protected float mapCoordToCircularAmp(float x, float y, float boundsW, float boundsH) {
		float xRemapped = MathUtil.mapCoordToCircular(x, y, boundsW, boundsH);
		float distFromOrig = P.abs(xRemapped - x);
		float maxDist = boundsW / 2;
		return 1 - distFromOrig / maxDist;
	}
	
}


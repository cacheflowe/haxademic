package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.PinchPolesFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

public class Demo_Arcsine_PinchPoles_Shader
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
				p.circle(x, y, spacing/2);
			}
		}
		
		// draw an image
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), p.g, true);
		
		// apply shader
		PinchPolesFilter.instance().updateHotSwap();
		PinchPolesFilter.instance().setCrossfade(Mouse.xNorm);
		PinchPolesFilter.instance().applyTo(p.g);
	}
	
}


package com.haxademic.demo.data;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.DemoAssets;

public class Demo_DemoAssets
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void drawApp() {
		background(0);
		p.image(DemoAssets.noSignal(), 0, 0);
	}	

}


package com.haxademic.demo.file;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.CounterToFile;
import com.haxademic.core.media.DemoAssets;

public class Demo_CounterToFile 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected CounterToFile counter;
	
	protected void firstFrame () {
		counter = new CounterToFile("_test_user_count");
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			counter.increment();
		}
	}
	
	protected void drawApp() {
		p.background(0);
		DemoAssets.setDemoFont(p.g);
		p.text(counter.count(), 100, 100);
	}
	
}

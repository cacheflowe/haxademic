package com.haxademic.demo.file;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.file.PrefToText;
import com.haxademic.core.media.DemoAssets;

public class Demo_PrefToText_Counter 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int count = 0;
	
	protected void firstFrame () {
		count = PrefToText.getValueI("count", count);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			count++;
			new Thread(new Runnable() { public void run() {
				PrefToText.setValue("count", count);
			}}).start();
		}
	}
	
	protected void drawApp() {
		p.background(0);
		DemoAssets.setDemoFont(p.g);
		p.text(count, 100, 100);
	}
	
}

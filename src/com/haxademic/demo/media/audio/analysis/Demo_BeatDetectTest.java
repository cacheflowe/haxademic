package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_BeatDetectTest 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public void drawApp() {
		AudioIn.instance();
		
		PG.setDrawCenter(p);
		p.noStroke();
		
		int x = p.frameCount % p.width;
		if(p.frameCount == 1) background(0);
		if(x <= 1) background(0);
				
		if(AudioIn.isBeat()) {
			p.fill(0, 255, 0);
			p.rect(x, p.height / 2, 1, 50);
		} else {
			p.fill(255);
			p.rect(x, p.height / 2, 1, 10);
		}
	}

}

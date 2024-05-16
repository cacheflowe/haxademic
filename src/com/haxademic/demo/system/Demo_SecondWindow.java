package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.system.SecondWindow;

import processing.core.PGraphics;

public class Demo_SecondWindow 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SecondWindow secondWindow;
	protected PGraphics buffer;
	
	protected void firstFrame() {
		buffer = PG.newPG(512, 512);
		secondWindow = new SecondWindow(buffer);
	}

	protected void drawApp() {
		PG.setDrawCenter(p);
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		p.fill(255);
		p.translate(p.width/2, p.height/2);
		p.rotate(p.frameCount * 0.01f);
		p.rect(0, 0, 100, 100);

		// buffer.beginDraw();
		// buffer.background(0, 255, 0);
		// buffer.fill(255);
		// buffer.translate(buffer.width/2, buffer.height/2);
		// buffer.rotate(p.frameCount * 0.01f);
		// buffer.rect(0, 0, 100, 100);
		// buffer.endDraw();
	}

	public void keyReleased(){
		if(key == 'f') {
		}
	} 
	
}

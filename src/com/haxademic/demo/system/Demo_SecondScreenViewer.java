package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.system.SecondScreenViewer;

public class Demo_SecondScreenViewer 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SecondScreenViewer appViewerWindow;
	
	public void setupFirstFrame() {
		appViewerWindow = new SecondScreenViewer(p.g, 0.5f);
	}

	public void drawApp() {
		PG.setDrawCenter(p);
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		p.fill(255);
		p.translate(p.width/2, p.height/2);
		p.rotate(p.frameCount * 0.01f);
		p.rect(0, 0, 100, 100);
	}

	public void keyReleased(){
		if(key == 'f') {
		}
	} 
	
}

package com.haxademic.demo.net;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.old.DashboardPoster;

public class Demo_DashboardPoster
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DashboardPoster dashboardPoster;
	
	protected void firstFrame() {
		dashboardPoster = new DashboardPoster("test", "http://localhost/haxademic/www/dashboard/", 20, 0.5f, 0.5f);
		dashboardPoster.setImage(p.g);
		dashboardPoster.setDebug(true);
	}
	
	protected void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		
		// square
		pg.pushMatrix();
		pg.translate(p.width/2, p.height/2);
		pg.rotate(p.frameCount * 0.01f);
		pg.rect(0, 0, 100, 100);
		pg.popMatrix();
		
		// draw to screen
		pg.endDraw();
		p.image(pg, 0, 0);
		
		// add custom debug data 
		dashboardPoster.setCustomValue("mouseX", ""+p.mouseX);
		dashboardPoster.setCustomValue("mouseY", ""+p.mouseY);
 	}
	
}

package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDiscTextured 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 160 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		pg = PG.newPG(512, 32);
		DebugView.setTexture("pg", pg);
	}

	protected void drawApp() {
		// set context
		background(0);
		// PG.setBetterLights(p.g);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		// draw texture for disc
		if(FrameLoop.frameModLooped(60)) {
			pg.beginDraw();
			pg.background(0);
			pg.noStroke();
			for (int i = 0; i < pg.height; i++) {
				pg.fill(p.random(255));
				pg.rect(0, i, pg.width, 1);
			}
			pg.endDraw();
		}
		
		// draw disc
		p.noStroke();
		Shapes.drawDiscTextured(p.g, 300, 200, 100, pg);
	}
}

package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;

public class Demo_MathUtil_saw
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	
	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.WIDTH, "520" );
		Config.setProperty( AppSettings.HEIGHT, "120" );
	}
		
	protected void firstFrame() {
	}
	
	protected void drawApp() {
		p.background(0);

		// texture feedback
		// pg.copy(pg, 0, 0, pg.width, pg.height, -1.5f, 0, pg.width, pg.height);
//		pg.copy(pg, 0, 0, pg.width, pg.height, -2, -2, pg.width, pg.height + 4);

		// start texture drawing
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.fill( 255, 255, 255, 255 );
		pg.noStroke();
		
		// fade out
		pg.fill( 0, 7f );
		pg.rect(p.width/2f, p.height/2f, p.width, p.height);

		// increment oscillations
		float incrementer = p.frameCount / 20f;
		float halfH = p.height/2f;
		
		// draw sin
		float sin = P.sin(incrementer);
		pg.fill(255, 255, 0);
		pg.ellipse(250, halfH + 20 * sin, 20, 20);
		
		// draw saw
		float saw = MathUtil.saw(incrementer);
		pg.fill(255, 0, 255);
		pg.ellipse(350, halfH + 20 * saw, 20, 20);
		
		// draw saw tan
		float sawtan = MathUtil.sawTan(incrementer * 2f);
		pg.fill(0, 255, 255);
		pg.ellipse(450, halfH + 20 * sawtan, 20, 20);
		
		// finish drawing
		pg.endDraw();
		
		// draw texture to stage
		PG.setColorForPImage(p);
		p.image(pg, 0, 0);
	}
}

package com.haxademic.demo.render.joons;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

public class Demo_Joons_RenderBasicShape
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, true );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, true );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH );

		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDER_DEMO_SCREENSHOT_FRAME, 2);
	}

	protected void drawApp() {
		JoonsWrapper joons = Renderer.instance().joons;
		if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == false) {
			p.background(0);
			p.lights();
		}
//		joons.jr.background(JoonsWrapper.BACKGROUND_GI);
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		p.translate(0, 0, -width);

		setUpRoom();
		
		// draw boxes
		p.rotateX(-0.3f);
		p.rotateY(P.QUARTER_PI);
		
		
		p.fill( 50, 100, 50 );
		joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 50, 100, 50, 0.75f);
		p.box(180);
		
		p.fill( 255, 255, 255 );
		joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 255, 255, 255, 0.75f);
		p.box(90, 360, 90);
		
		// draw 2nd plane box
		p.fill( 20, 20, 20 );
		joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 20, 20, 20, 0.75f);
		p.box(1000, 10, 1000);

	}
	
	public void keyPressed() {
		if (key == 'r' || key == 'R') Renderer.instance().joons.jr.render(); // Press 'r' key to start rendering.
	}

	protected void setUpRoom() {
		JoonsWrapper joons = Renderer.instance().joons;
		p.pushMatrix();
		translate(0, 0, 0);
		float radiance = 30;
		int samples = 16;
		joons.jr.background(JoonsWrapper.CORNELL_BOX, 
				width * 6, height * 4, 3000,			// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				120, 120, 120, 							// left rgb
				120, 120, 120, 							// right rgb
				120, 120, 120,	 						// back rgb
				120, 120, 120, 							// top rgb
				120, 120, 120  							// bottom rgb
		); 
		popMatrix();		
	}
	
}
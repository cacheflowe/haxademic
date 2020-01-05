package com.haxademic.demo.render.joons;

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
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_LOW );

		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
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
		
		// set material
		p.fill( 50, 200, 50 );
		joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 50, 200, 50, 0.75f);

		// draw box
		p.rotateY(1);
		p.rotateZ(1);
		p.box(180);
	}

	protected void setUpRoom() {
		JoonsWrapper joons = Renderer.instance().joons;
		p.pushMatrix();
		translate(0, 0, 0);
		float radiance = 20;
		int samples = 16;
		joons.jr.background(JoonsWrapper.CORNELL_BOX, 
				width * 6, height * 4, 3000,			// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, 							// left rgb
				40, 40, 40, 							// right rgb
				60, 60, 60,	 							// back rgb
				60, 60, 60, 							// top rgb
				60, 60, 60  							// bottom rgb
		); 
		popMatrix();		
	}
	
}
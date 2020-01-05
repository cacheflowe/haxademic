
package com.haxademic.demo.render.joons;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

public class Demo_Joons_Materials 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, true );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, true );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_LOW );

		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
	}

	protected void drawApp() {
		JoonsWrapper joons = Renderer.instance().joons;
		joons.jr.background(0, 0, 0); //background(gray), or (r, g, b), like Processing.
		joons.jr.background("gi_instant"); //Global illumination, normal mode.
		joons.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.

		pushMatrix();
		translate(0, 0, -120);
		joons.jr.background("cornell_box", 100, 100, 100); //cornellBox(width, height, depth);
		popMatrix();

		pushMatrix();
		translate(-40, 20, -140);
		pushMatrix();
		rotateY(-PI/8);

		//_jw.jr.fill("light"); or
		//_jw.jr.fill("light", r, g, b); or
		//_jw.jr.fill("light", r, g, b, int samples);
		joons.jr.fill("light", 5, 5, 5);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("mirror"); or
		//_jw.jr.fill("mirror", r, g, b);    
		joons.jr.fill("mirror", 255, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("diffuse"); or
		//_jw.jr.fill("diffuse", r, g, b);
		joons.jr.fill("diffuse", 150, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("shiny"); or
		//_jw.jr.fill("shiny", r, g, b);  or
		//_jw.jr.fill("shiny", r, g, b, shininess);  or
		joons.jr.fill("shiny", 150, 255, 255, 0.1f);
		sphere(13);
		translate(27, 0, 0);
		popMatrix();
		rotateY(PI/8);
		translate(-10, -27, 30);

		//_jw.jr.fill("ambient_occlusion"); or
		//_jw.jr.fill("ambient_occlusion", bright r, bright g, bright b); or
		//_jw.jr.fill("ambient occlusion", bright r, bright g, bright b, dark r, dark g, dark b, maximum distance, int samples);
		joons.jr.fill("ambient_occlusion", 150, 255, 255, 0, 0, 255, 50, 16);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("phong", r, g, b);
		joons.jr.fill("phong", 150, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("glass", r, g, b);
		joons.jr.fill("glass", 255, 255, 255);
		sphere(13);
		translate(27, 0, 0);

		//_jw.jr.fill("constant", r, g, b);
		joons.jr.fill("constant", 150, 255, 255);
		sphere(13);
		popMatrix();
	}

	public void keyPressed() {
		JoonsWrapper joons = Renderer.instance().joons;
		if (key == 'r' || key == 'R') joons.jr.render(); //Press 'r' key to start rendering.
	}

}

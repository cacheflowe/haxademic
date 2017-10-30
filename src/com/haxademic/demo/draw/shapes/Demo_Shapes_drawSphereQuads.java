package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.Penner;

import processing.core.PVector;

public class Demo_Shapes_drawSphereQuads 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 120;
	protected ArrayList<PVector> points;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void drawApp() {
		p.background(0);
		p.lights();
		p.translate(p.width/2, p.height/2, -200);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = percentComplete * P.TWO_PI;

		rotateY(P.sin(radsComplete) * 1.25f); 
		
		p.stroke(255, 140, 200);
		p.fill(255 * 0.2f, 140 * 0.2f, 200 * 0.2f);
		Shapes.drawSphereQuads(p.g, p.height * 0.4f);
	}
}

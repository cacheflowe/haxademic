package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.shapes.Shapes;

import processing.core.PVector;

public class Demo_Shapes_drawSphereWithQuads 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PVector> points;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 120 );
	}

	public void drawApp() {
		p.background(0);
		p.lights();
		p.translate(p.width/2, p.height/2, -200);
		
		rotateY(P.sin(loop.progressRads()) * 1.25f); 
		
		p.stroke(255, 140, 200);
		p.fill(255 * 0.2f, 140 * 0.2f, 200 * 0.2f);
		Shapes.drawSphereWithQuads(p.g, p.height * 0.4f);
	}
}

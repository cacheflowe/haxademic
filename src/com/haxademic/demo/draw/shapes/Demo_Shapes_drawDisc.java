package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;

import processing.core.PVector;

public class Demo_Shapes_drawDisc 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PVector> points;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 240 );
	}

	public void drawApp() {
		p.background(0);
		DrawUtil.setBetterLights(p);
		p.translate(p.width/2, p.height/2);
		
		rotateY(P.sin(loop.progressRads()) * 0.25f); 
		
		p.noStroke();
		p.fill(255, 140, 200);
		
		// rotate to flat bottom
		float vertices = 5f + P.round(2f * P.sin(loop.progressRads()));
		p.rotate(-P.HALF_PI);
		float rot = P.TWO_PI / vertices;
		if(vertices % 2 == 0) p.rotate(-rot/2f);
		
		// draw shape
		Shapes.drawDisc(p, 440, 390, (int) vertices);
		Shapes.drawDisc(p, 370, 330, (int) vertices);
		Shapes.drawDisc(p, 300, 270, (int) vertices);
		Shapes.drawDisc(p, 230, 210, (int) vertices);
	}
}

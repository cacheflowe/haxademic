package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Extrude2dPoints;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;
import processing.core.PVector;

public class DemoExtrude2dPoints 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 120;
	protected ArrayList<PVector> points;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setup() {
		super.setup();
		
		points = new ArrayList<PVector>();
		float vertices = 5;
		float vertexRads = P.TWO_PI / vertices;
		float radsOffset = -P.HALF_PI;
		float radius = p.width * 0.4f;
		for (int i = 0; i < vertices; i++) {
			points.add(new PVector(
					radius * P.cos(radsOffset + vertexRads * i),
					radius * P.sin(radsOffset + vertexRads * i),
					0
			));
		}
	}

	public void drawApp() {
		p.background(0);
		p.lights();
		p.translate(p.width/2, p.height/2, -200);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		float radsComplete = percentComplete * P.TWO_PI;
//		float radsCompleteEased = easedPercent * P.TWO_PI;

		rotateY(P.sin(radsComplete) * 1.25f); 
		
		p.fill(255, 140, 200);
		p.noStroke();
		Extrude2dPoints.drawExtruded2dPointList(p, points, 100);
	}
}

package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.shapes.Extrude2dPoints;

import processing.core.PVector;

public class Demo_Extrude2dPoints 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 120;
	protected ArrayList<PVector> points;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setup() {
		super.setup();
		
		points = new ArrayList<PVector>();
		float vertices = 36;
		float vertexRads = P.TWO_PI / vertices;
		float radsOffset = -P.HALF_PI;
		float radius = p.width * 0.4f;
		float radiusInner = p.width * 0.3f;
		
		for (int i = 0; i < vertices; i++) {
			points.add(new PVector(
					radius * P.cos(radsOffset + vertexRads * i),
					radius * P.sin(radsOffset + vertexRads * i),
					0
			));
		}
		// connect to very first point
		points.add(new PVector(
				radius * P.cos(radsOffset),
				radius * P.sin(radsOffset),
				0
		));
		
		// inner hole in reverse
		for (int i = 0; i < vertices; i++) {
			points.add(new PVector(
					radiusInner * P.cos(radsOffset - vertexRads * i),
					radiusInner * P.sin(radsOffset - vertexRads * i),
					0
			));
		}
		// connect to inner first point
		points.add(new PVector(
				radiusInner * P.cos(radsOffset),
				radiusInner * P.sin(radsOffset),
				0
		));

	}

	public void drawApp() {
		p.background(0);
		p.lights();
		p.translate(p.width/2, p.height/2, -200);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		// float easedPercent = Penner.easeInOutQuart(percentComplete);
		float radsComplete = percentComplete * P.TWO_PI;
//		float radsCompleteEased = easedPercent * P.TWO_PI;

		rotateY(P.sin(radsComplete) * 1.25f); 
		
		p.stroke(0);
		p.fill(0, 255, 200);
		Extrude2dPoints.drawExtruded2dPointList(p, points, 100);
	}
}

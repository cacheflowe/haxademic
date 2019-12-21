package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.DisplacementPoint;
import com.haxademic.core.ui.UI;

public class Demo_DisplacementPoint
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String POINT_SIZE = "POINT_SIZE";
	protected String DISPLACE_AMP = "DISPLACE_AMP";
	protected String FRICTION = "FRICTION";
	protected String ACCELERATION = "ACCELERATION";
	protected String INFLUENCE_BY_DISTANCE = "INFLUENCE_BY_DISTANCE";
	
	protected DisplacementPoint[] points;
	protected int cols = 40;
	protected int rows = 30;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1440);
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty( AppSettings.SHOW_UI, true);
	}

	public void setupFirstFrame() {
		// lay out grid
		float spacing = 40;
		float startX = p.width / 2 - (cols / 2) * spacing + spacing/2;
		float startY = p.height / 2 - (rows / 2) * spacing + spacing/2;
		points = new DisplacementPoint[rows * cols];
		for (int i = 0; i < points.length; i++) {
			int x = i % cols;
			int y = P.floor(i / cols);
			points[i] = new DisplacementPoint(startX + x * spacing, startY + y * spacing);
		}
		
		// set up sliders
		UI.addSlider(POINT_SIZE, 10, 1, 100, 1, false);
		UI.addSlider(DISPLACE_AMP, 30, 1, 300, 1, false);
		UI.addSlider(FRICTION, 0.9f, 0.1f, 0.99f, 0.001f, false);
		UI.addSlider(ACCELERATION, 0.1f, 0.01f, 0.99f, 0.001f, false);
		UI.addSlider(INFLUENCE_BY_DISTANCE, 1, 0, 1, 0.01f, false);
	}

	protected int indexByXY(int x, int y, int cols) {
		return y * cols + x;
	}
	
	protected void autoHideMouse() {
	}
	
	public void drawApp() {
		p.noCursor();
		// set up context
		background(0);
		PG.setDrawCenter(p);
		p.fill(255);
		//p.noStroke();
		p.stroke(255);
		
		// update properties w/sliders & draw points
		float pointSize = UI.value(POINT_SIZE);
		for (int i = 0; i < points.length; i++) {
			// set properties 
			points[i].displaceAmp(UI.value(DISPLACE_AMP));
			points[i].friction(UI.value(FRICTION));
			points[i].acceleration(UI.value(ACCELERATION));
			points[i].influenceByDistance(UI.value(INFLUENCE_BY_DISTANCE));
			points[i].update(p.mouseX, p.mouseY);
		}
		// draw after updating all, so vertices match up between `update()` calls
		for (int i = 0; i < points.length; i++) {
			// draw points
			p.ellipse(points[i].x(), points[i].y(), pointSize, pointSize);
			
			// draw connections
			int x = i % cols;
			int y = P.floor(i / cols);
			if(x < cols - 1) {
				int rightIndex = indexByXY(x + 1, y, cols);
				p.line(points[i].x(), points[i].y(), points[rightIndex].x(), points[rightIndex].y());
			}
			if(y < rows - 1) {
				int downIndex = indexByXY(x, y + 1, cols);
				p.line(points[i].x(), points[i].y(), points[downIndex].x(), points[downIndex].y());
			}
		}

	}
}

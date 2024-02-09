package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.DisplacementPoint;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

public class Demo_DisplacementPoint_Blast
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

	protected LinearFloat blastProgress = new LinearFloat(0, 0.01f);

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1440);
		Config.setProperty( AppSettings.HEIGHT, 1080);
		Config.setProperty( AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		// init blast progress
		blastProgress.setTarget(1);

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
		UI.addSlider(DISPLACE_AMP, 250, 1, 300, 1, false);
		UI.addSlider(FRICTION, 0.38f, 0.1f, 0.99f, 0.001f, false);
		UI.addSlider(ACCELERATION, 0.06f, 0.01f, 0.99f, 0.001f, false);
		UI.addSlider(INFLUENCE_BY_DISTANCE, 1, 0, 1, 0.01f, false);
	}

	protected void drawApp() {
		// set up context
		background(0);
		PG.setDrawCenter(p);
		
		// update blast
		blastProgress.setInc(0.005f);
		if(blastProgress.value() == 1) {
			blastProgress.setCurrent(0).setTarget(1);
		}
		blastProgress.update();
		
		// draw blast
		float blastSizeMax = p.width * 2;
		float blastSize = blastSizeMax * blastProgress.value();
		p.push();
		PG.setCenterScreen(p);
		p.noFill();
		p.stroke(255, 0, 0);
		p.strokeWeight(10);
		p.circle(0, 0, blastSize);
		p.pop();
		DemoAssets.setDemoFont(p.g);
		p.text(blastSize, 20, 30);
		
		// update properties w/sliders & draw points
		p.fill(255);
		p.noStroke();
		float pointSize = UI.value(POINT_SIZE);
		for (int i = 0; i < points.length; i++) {
			p.fill(255);

			// set properties 
			points[i].displaceAmp(UI.value(DISPLACE_AMP) * (1 - blastProgress.value())); // descending blast influence? ->  * (1 - blastProgress.value())
			points[i].friction(UI.value(FRICTION));
			points[i].acceleration(UI.value(ACCELERATION));
			points[i].influenceByDistance(UI.value(INFLUENCE_BY_DISTANCE));

			// calculate edge of circle
			float centerX = p.width / 2;
			float centerY = p.height / 2;
			float blastRadius = blastSize / 2;
			float radsToCircleEdge = MathUtil.getRadiansToTarget(centerX, centerY, points[i].x(), points[i].y());
			float circleEdgeX = centerX + P.cos(radsToCircleEdge) * blastRadius;
			float circleEdgeY = centerY + P.sin(radsToCircleEdge) * blastRadius;
			float distToCenter = MathUtil.getDistance(points[i].x(), points[i].y(), centerX, centerY);
			boolean isInsideBlast = distToCenter < blastRadius;
			if(isInsideBlast) {
				// points[i].displaceAmp(UI.value(DISPLACE_AMP) * 0.5f);
				// points[i].acceleration(UI.value(ACCELERATION) * 0.5f);
				p.fill(255, 0, 0);
			}
			points[i].update(circleEdgeX, circleEdgeY);

			// since we're not making connections anymore, we can just draw the points
			p.ellipse(points[i].x(), points[i].y(), pointSize, pointSize);
			// p.text(distToCenter, points[i].x(), points[i].y());
		}
	}
}

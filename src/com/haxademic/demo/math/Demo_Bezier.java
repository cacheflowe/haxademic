package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PVector;

public class Demo_Bezier
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String controlPoint1X = "controlPoint1X";
	protected String controlPoint1Y = "controlPoint1Y";
	protected String controlPoint2X = "controlPoint2X";
	protected String controlPoint2Y = "controlPoint2Y";
//	protected EasingFloat easingX = new EasingFloat(0, 6f);

	protected void config() {
		Config.setAppSize(960, 960);
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		UI.addSlider(controlPoint1X, 0.2f, -1, 2, 0.001f, false);
		UI.addSlider(controlPoint1Y, 0.2f, -1, 2, 0.001f, false);
		UI.addSlider(controlPoint2X, 0.8f, -1, 2, 0.001f, false);
		UI.addSlider(controlPoint2Y, 0.8f, -1, 2, 0.001f, false);
	}

	protected void drawApp() {
		// set context
		background(0);
		PG.setDrawFlat2d(p, true);
		float simSize = p.width * 0.8f;
		float simPadding = p.width * 0.1f;
		p.translate(simPadding, simPadding);
		
		// set colors
		int[] colors = ColorsHax.COLOR_GROUPS[0];
		int colorStroke = colors[3];
		int colorFill = colors[0];
		
		// draw bounds
		PG.setDrawCorner(p);
		p.fill(colorFill);
		p.stroke(255);
		p.strokeWeight(4);
		p.rect(0, 0, simSize, simSize);
		
		// draw control points
		PG.setDrawCenter(p);
		float controlPointSize = p.width * 0.05f;
		float ctrl1X = UI.value(controlPoint1X) * simSize;
		float ctrl1Y = UI.value(controlPoint1Y) * simSize;
		float ctrl2X = UI.value(controlPoint2X) * simSize;
		float ctrl2Y = UI.value(controlPoint2Y) * simSize;
		
		// lines
		p.stroke(colorStroke);
		p.line(0, 0, ctrl1X, ctrl1Y);
		p.line(simSize, simSize, ctrl2X, ctrl2Y);
		
		// circles
		p.stroke(colorStroke);
		p.fill(colorFill);
		p.circle(ctrl1X, ctrl1Y, controlPointSize);
		p.circle(ctrl2X, ctrl2Y, controlPointSize);
		
		// draw bezier w/Processing
		p.noFill();
		p.bezier(0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, simSize, simSize);
		
		// draw dots on top
		for (int i = 0; i < 50; i++) {
			float curveProg = i / 50f;
			PVector curBezLoc = getBezierXY(curveProg, 0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, simSize, simSize);
			p.circle(curBezLoc.x, curBezLoc.y, p.width * 0.01f);
		}
		
		// draw progress
		float bezProgress = FrameLoop.frameMod(100) / 100f;
		p.push();
		PVector curBezLoc = getBezierXY(bezProgress, 0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, simSize, simSize);
		float curBezAngle = getBezierAngle(bezProgress, 0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, simSize, simSize);
		p.translate(curBezLoc.x, curBezLoc.y);
		p.rotate(curBezAngle);
		p.image(DemoAssets.arrow(), 0, 0, controlPointSize * 2, controlPointSize * 2);
		p.pop();
		
		// draw x-easing
		PG.setDrawCorner(p);
		p.fill(colors[3]);
		ctrl1X = UI.value(controlPoint1X);
		ctrl1Y = UI.value(controlPoint1Y);
		ctrl2X = UI.value(controlPoint2X);
		ctrl2Y = UI.value(controlPoint2Y);
		// option 1 - distance check... doesn't really work like this
//		PVector bezOutputProgress = getBezierXY(bezProgress, 0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, 1, 1);
//		float bezEase = 1f - MathUtil.getDistance(bezOutputProgress.x, bezOutputProgress.y, 1, 1) / MathUtil.getDistance(0, 0, 1, 1);
//		DebugView.setValue("bezOutputProgress.x", bezOutputProgress.x);
//		DebugView.setValue("bezOutputProgress.y", bezOutputProgress.y);
//		DebugView.setValue("bezEase.y", bezEase);
		// option 2 - just use one dimension of the resulting position along the bezier path
		float bezEase = getBezierXY(bezProgress, 0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, 1, 1).x;
		DebugView.setValue("bezEase", bezEase);
		p.rect(0, simSize, bezEase * simSize, 20);
	}
	
	// code ported from: http://www.independent-software.com/determining-coordinates-on-a-html-canvas-bezier-curve.html
	
	protected PVector returnVal = new PVector();
	protected PVector getBezierXY(float t, float sx, float sy, float cp1x, float cp1y, float cp2x, float cp2y, float ex, float ey) {
		returnVal.x = P.pow(1-t,3f) * sx + 3f * t * P.pow(1 - t, 2) * cp1x + 3f * t * t * (1f - t) * cp2x + t * t * t * ex;
		returnVal.y = P.pow(1-t,3f) * sy + 3f * t * P.pow(1 - t, 2) * cp1y + 3f * t * t * (1f - t) * cp2y + t * t * t * ey;
		return returnVal;
	}
	
	protected float getBezierAngle(float t, float sx, float sy, float cp1x, float cp1y, float cp2x, float cp2y, float ex, float ey) {
		float dx = P.pow(1f-t, 2f)*(cp1x-sx) + 2f*t*(1-t)*(cp2x-cp1x) + t * t * (ex - cp2x);
		float dy = P.pow(1f-t, 2f)*(cp1y-sy) + 2f*t*(1-t)*(cp2y-cp1y) + t * t * (ey - cp2y);
		return -P.atan2(dx, dy) + 0.5f * P.PI;
	}

}

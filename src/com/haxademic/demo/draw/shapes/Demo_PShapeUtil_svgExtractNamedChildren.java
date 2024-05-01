package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class Demo_PShapeUtil_svgExtractNamedChildren
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape[] shapeChildren;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		shape = p.loadShape(P.path("svg/compound-frame.svg"));
		
		// store child references
		shapeChildren = new PShape[shape.getChildCount()];
		for (int i = 0; i < shapeChildren.length; i++) {
			// create child references
			PShape child = shape.getChild(i);
			shapeChildren[i] = child;
			
			int vertexCount = PShapeUtil.vertexCount(child);
			P.out(child.getName());
			P.out("vertexCount allClones", vertexCount);
			// we should ignore children with no vertices. not sure where these groups come from
		}
	}
		
	protected void drawApp() {
		// clear the screen
		background(20);
		p.noStroke();
		PG.setDrawCorner(p.g);

		// draw original shape
		// p.shape(shape);
		for (int i = 0; i < shapeChildren.length; i++) {
			p.push();
			p.translate(0, FrameLoop.osc(0.02f, -20, 20, i));
			p.shape(shapeChildren[i]);
			p.pop();
		}		
	}
	
}
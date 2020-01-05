package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.LineTrail;

import processing.core.PVector;

public class Demo_LineTrail 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LineTrail trail;
	protected PVector mouseVec = new PVector();

	protected void drawApp() {
		p.background(0);
		p.noFill();
		p.stroke(40, 255, 40);
		
		mouseVec.set(p.mouseX, p.mouseY);
		if(trail == null) trail = new LineTrail(10);
		trail.update(p.g, mouseVec, p.color(255), p.color(255, 0));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') trail.reset(mouseVec);
	}
}

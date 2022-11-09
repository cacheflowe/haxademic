package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.LineTrail;

public class Demo_LineTrail 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LineTrail trail;

	protected void drawApp() {
		p.background(0);
		p.noFill();
		p.stroke(40, 255, 40);
		p.strokeWeight(20);
		
		if(trail == null) {
		    trail = new LineTrail(100);
		    trail.reset(p.width/2, p.height/2);
		}
		trail.update(p.g, p.mouseX, p.mouseY, p.color(255), p.color(255, 0, 0, 0));
		trail.smoothLine();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') trail.reset(p.mouseX, p.mouseY);
	}
}

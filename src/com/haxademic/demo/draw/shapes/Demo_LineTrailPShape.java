package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.LineTrailCustom;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

public class Demo_LineTrailPShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LineTrailCustom trail;

	protected void drawApp() {
		p.background(0);
		// /ImageUtil.cropFillCopyImage(DemoAssets.noSignal(), p.g, true);
		
        if(trail == null) {
            trail = new LineTrailCustom(200, 20, 100);
            trail.reset(p.width/2, p.height/2);
        }
        
        float x = p.mouseX;
        float y = p.mouseY;
        if(Mouse.xNorm > 0.5f) {
            float rads = FrameLoop.count(0.03f);
            float radius = 200f;
            x = p.width / 2 + P.cos(rads) * radius * 2f;
            y = p.height / 2 + P.sin(rads * 2f) * radius / 1f;
        }
        
        int colorStart = p.color(FrameLoop.osc(0.03f, 0, 255), FrameLoop.osc(0.02f, 0, 255), FrameLoop.osc(0.028f, 0, 255));
        int colorEnd = p.color(FrameLoop.osc(0.04f, 0, 255), FrameLoop.osc(0.05f, 0, 255), 0, 255);
        trail.update(p.g, x, y, colorStart, colorEnd);
        trail.setTaperStart(0.85f);
        trail.setLineWeight(FrameLoop.osc(0.03f, 5,  40));
		trail.smoothLine();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') trail.reset(p.mouseX, p.mouseY);
	}
}

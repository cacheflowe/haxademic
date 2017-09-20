package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.SecondScreenViewer;

public class SecondScreenViewerTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SecondScreenViewer viewer;

	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.FPS, 60 );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		viewer = new SecondScreenViewer(p.g, 0.5f);
	}

	public void drawApp() {
		DrawUtil.setDrawCenter(p);
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		p.fill(255);
		p.translate(p.width/2, p.height/2);
		p.rotate(p.frameCount * 0.01f);
		p.rect(0, 0, 100, 100);
	}

	public void keyReleased(){
		if(key == 'f') {
		}
	} 
	
}

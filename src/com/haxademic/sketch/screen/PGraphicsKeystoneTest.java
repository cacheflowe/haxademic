package com.haxademic.sketch.screen;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.PGraphicsKeystone;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;

public class PGraphicsKeystoneTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer;
	protected PGraphicsKeystone pgPinnable;
	protected boolean testPattern = true;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setup() {
		super.setup();	
		buildCanvas();
	}

	protected void buildCanvas() {
		buffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
//		_pgPinnable = new PGraphicsKeystone( p, _pg, 12 );
		pgPinnable = new PGraphicsKeystone( p, buffer, 12, FileUtil.getFile("text/keystoning/keystone-demo.txt") );
	}

	public void drawApp() {
		p.background(0);
		// draw pinned pgraphics
		if(testPattern == true) pgPinnable.drawTestPattern();
		pgPinnable.update(p.g, true);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') testPattern = !testPattern;
		if(p.keyCode == 8) pgPinnable.resetCorners(p.g);
	}

}

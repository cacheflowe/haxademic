package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;

public class Demo_PGraphicsKeystone_Basic
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer;
	protected PGraphicsKeystone keystonedPG;
	protected boolean testPattern = true;
	protected boolean debug = true;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setupFirstFrame() {
		buildCanvas();
	}

	protected void buildCanvas() {
		buffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		keystonedPG = new PGraphicsKeystone( p, buffer, 12, FileUtil.getFile("text/keystoning/keystone-demo.txt") );
	}

	public void drawApp() {
		p.background(0);
		
		buffer.beginDraw();
		buffer.background(0,255,0);
		buffer.fill(0);
		buffer.rect(40, 40, 100, 100);
		buffer.endDraw();
		
		if(testPattern == true) keystonedPG.drawTestPattern();
		keystonedPG.update(p.g);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') {
			debug = !debug;
			keystonedPG.setActive(debug);
		}
		if(p.key == 't') testPattern = !testPattern;
		if(p.key == 'r') keystonedPG.resetCorners();
	}

}

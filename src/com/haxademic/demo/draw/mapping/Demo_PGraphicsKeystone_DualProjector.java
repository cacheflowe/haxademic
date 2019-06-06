package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;

public class Demo_PGraphicsKeystone_DualProjector
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer;
	protected PGraphicsKeystone keystone1;
	protected PGraphicsKeystone keystone2;
	protected boolean debug1 = true;
	protected boolean debug2 = true;
	protected boolean testPattern = true;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
//		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
//		p.appConfig.setProperty( AppSettings.SCREEN_X, 0 );
//		p.appConfig.setProperty( AppSettings.SCREEN_Y, 0 );
//		p.appConfig.setProperty( AppSettings.WIDTH, 3840 );
//		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
	}

	public void setupFirstFrame() {
		buildCanvas();
	}

	protected void buildCanvas() {
		buffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		keystone1 = new PGraphicsKeystone( p, buffer, 12, FileUtil.getFile("text/keystoning/keystone-demo1.txt") );
		keystone2 = new PGraphicsKeystone( p, buffer, 12, FileUtil.getFile("text/keystoning/keystone-demo2.txt") );
	}

	public void drawApp() {
		p.background(0);
		
		// draw test content
		buffer.beginDraw();
		PG.setDrawCenter(buffer);
		buffer.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		buffer.fill(255);
		buffer.translate(buffer.width/2, buffer.height/2);
		buffer.rotate(p.frameCount * 0.01f);
		buffer.rect(0, 0, 100, 100);
		buffer.endDraw();
		
		// draw test pattern
		if(testPattern == true) {
			keystone1.drawTestPattern();
			keystone2.drawTestPattern();
			PG.setPImageAlpha(p, 0.6f);
		} else {
			PG.setPImageAlpha(p, 1f);
		}
		
		// draw to screen
		keystone1.update(p.g);
		keystone2.update(p.g);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') {
			debug1 = !debug1;
			keystone1.setActive(debug1);
		}
		if(p.key == '2') {
			debug2 = !debug2;
			keystone2.setActive(debug2);
		}
		if(p.key == 't') testPattern = !testPattern;
		if(p.key == 'r') {
			keystone1.setPosition(0, 0, buffer.width, buffer.height);
			keystone2.setPosition(buffer.width / 2, 0, buffer.width, buffer.height);
		}
	}

}

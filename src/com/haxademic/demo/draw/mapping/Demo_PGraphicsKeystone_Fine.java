package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_PGraphicsKeystone_Fine
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer;
	protected PGraphicsKeystone keystonedPG;
	protected boolean testPattern = true;
	protected boolean debug = true;
	
	protected int subdivisions = 16;
	protected float[] offsetsX;
	protected float[] offsetsY;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setupFirstFrame() {
		buildCanvas();
		
		p.debugView.setHelpLine("__ Key Commands", "__\n");
		p.debugView.setHelpLine("ESC |", "Quit");
		p.debugView.setHelpLine("D |", "Toggle keystone active");
		p.debugView.setHelpLine("T |", "Test pattern toggle");
		p.debugView.setHelpLine("R |", "Reset corners");
		
		p.debugView.setHelpLine("X |", "Next col");
		p.debugView.setHelpLine("Z |", "Prev col");
		p.debugView.setHelpLine("C |", "Next row");
		p.debugView.setHelpLine("V |", "Prev row");
		p.debugView.setHelpLine("[ |", "Adjust down");
		p.debugView.setHelpLine("] |", "Adjust up");
		p.debugView.setHelpLine("E |", "Export config");
	}
	
	protected void buildCanvas() {
		// build buffer and keystone object
		buffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		keystonedPG = new PGraphicsKeystone( p, buffer, subdivisions, FileUtil.getFile("text/keystoning/keystone-demo.txt"), FileUtil.getFile("text/keystoning/fine-mapping-demo.txt") );
	}
	
	public void drawApp() {
		p.background(0);
		
		// oscuillateOffsets();
		
		// update mapped texture
		buffer.beginDraw();
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), buffer, true);
		buffer.endDraw();
		
		// draw keystone debug & to screen 
		if(testPattern == true) keystonedPG.drawTestPattern();
		keystonedPG.update(p.g);
	}
	
//	protected void debugOffsets() {
//		p.debugView.setValue("selectedRowIndex", selectedRowIndex);
//		p.debugView.setValue("selectedColIndex", selectedColIndex);
//		for (int i = 0; i < offsetsX.length; i++) {
//			p.debugView.setValue("offsetsX["+i+"]", offsetsX[i]);
//		}
//	}
	
	protected void oscillateOffsets() {
		for (int i = 0; i < offsetsX.length; i++) {
			offsetsX[i] = P.sin(i + p.frameCount * 0.03f) * 0.02f;
		}
		for (int i = 0; i < offsetsY.length; i++) {
			offsetsY[i] = P.sin(i + p.frameCount * 0.03f) * 0.02f;
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		// standard controls
		if(p.key == 'd') {
			debug = !debug;
			keystonedPG.setActive(debug);
		}
		if(p.key == 't') testPattern = !testPattern;
		if(p.key == 'r') keystonedPG.resetCorners();
	}
}

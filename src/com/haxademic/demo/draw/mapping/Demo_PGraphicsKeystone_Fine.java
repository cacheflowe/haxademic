package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_PGraphicsKeystone_Fine
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics buffer;
	protected PGraphicsKeystone keystonedPG;
	protected boolean testPattern = true;
	protected boolean debug = true;
	
	protected int subdivisions = 16;
	protected float[] offsetsX;
	protected float[] offsetsY;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 700 );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.FULLSCREEN, false );
	}

	protected void firstFrame() {
		buildCanvas();
		
		DebugView.setHelpLine("__ Key Commands", "__\n");
		DebugView.setHelpLine("ESC |", "Quit");
		DebugView.setHelpLine("D |", "Toggle keystone active");
		DebugView.setHelpLine("T |", "Test pattern toggle");
		DebugView.setHelpLine("R |", "Reset corners");
		DebugView.setHelpLine("0 |", "Reset offsets");
		DebugView.setHelpLine("A |", "Reset offsets X");
		DebugView.setHelpLine("S |", "Reset offsets Y");

		DebugView.setHelpLine("X |", "Next col");
		DebugView.setHelpLine("Z |", "Prev col");
		DebugView.setHelpLine("O |", "Adjust col down");
		DebugView.setHelpLine("P |", "Adjust col up");

		DebugView.setHelpLine("C |", "Next row");
		DebugView.setHelpLine("V |", "Prev row");
		DebugView.setHelpLine("{ |", "Adjust row down");
		DebugView.setHelpLine("} |", "Adjust row up");
		
		DebugView.setHelpLine("E |", "Export config");
	}
	
	protected void buildCanvas() {
		// build buffer and keystone object
		buffer = PG.newPG(p.width/2, p.height/2);
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		keystonedPG = new PGraphicsKeystone( p, buffer, subdivisions, FileUtil.getPath("text/keystoning/keystone-demo.txt"), FileUtil.getPath("text/keystoning/fine-mapping-demo.txt") );
	}
	
	protected void drawApp() {
		p.background(0);
		
//		oscillateOffsets();
		
		// update mapped texture
		buffer.beginDraw();
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), buffer, true);
		buffer.endDraw();
		
		// draw keystone debug & to screen 
		if(testPattern == true) keystonedPG.drawTestPattern();
		keystonedPG.update(p.g);
	}
	
//	protected void debugOffsets() {
//		DebugView.setValue("selectedRowIndex", selectedRowIndex);
//		DebugView.setValue("selectedColIndex", selectedColIndex);
//		for (int i = 0; i < offsetsX.length; i++) {
//			DebugView.setValue("offsetsX["+i+"]", offsetsX[i]);
//		}
//	}
	
	protected void oscillateOffsets() {
		offsetsX = new float[subdivisions + 1];
		for (int i = 1; i < offsetsX.length - 1; i++) {
			offsetsX[i] = P.sin(i + p.frameCount * 0.03f) * 0.02f;
		}
		keystonedPG.setOffsetsX(offsetsX);
		
		offsetsY = new float[subdivisions + 1];
		for (int i = 1; i < offsetsY.length - 1; i++) {
			offsetsY[i] = P.sin(i + p.frameCount * 0.03f) * 0.02f;
		}
		keystonedPG.setOffsetsY(offsetsY);
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

package com.haxademic.demo.hardware.printer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.printer.PrintPageDirect;

import processing.core.PGraphics;

public class Demo_PrintDirect 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PrintPageDirect printDirect;
	protected PGraphics printBuffer;
	
	public void setupFirstFrame () {
		printDirect = new PrintPageDirect(false);
		printBuffer = P.p.createGraphics(PrintPageDirect.PRINT_W * 2, PrintPageDirect.PRINT_H, PRenderers.P3D);
	}

	public void drawApp() {
		p.background( 0 );
//		if(p.frameCount % 100 == 0) 		printBuffer = P.p.createGraphics(PrintPageDirect.PRINT_W, PrintPageDirect.PRINT_H, PRenderers.P3D);


		////////////////////////////////////////
		// draw stuff!
		
		printBuffer.beginDraw();
		
		// prep black on white bg
		printBuffer.background(255);
		printBuffer.fill(255);
		
		// add border
		float borderSize = 16;
		printBuffer.stroke(0);
		printBuffer.strokeWeight(borderSize);
		DrawUtil.setDrawCorner(printBuffer);
		printBuffer.rect(borderSize/2, borderSize/2, printBuffer.width - borderSize, printBuffer.height - borderSize);
		
		// draw squares
		printBuffer.stroke(0);
		printBuffer.strokeWeight(6);
		DrawUtil.setCenterScreen(printBuffer);
		DrawUtil.setDrawCenter(printBuffer);
		for (int i = 0; i < 400; i++) {
			float randSize = p.random(20, 500);
			float randRot = p.random(P.TWO_PI);
			printBuffer.pushMatrix();
			printBuffer.rotate(randRot);
			printBuffer.ellipse(p.random(0, printBuffer.width), p.random(0, printBuffer.height), randSize, randSize);
			printBuffer.popMatrix();
		}
		
		printBuffer.endDraw();
		
		// end draw stuff
		////////////////////////////////////////

		// draw image to screen
		ImageUtil.cropFillCopyImage(printBuffer, p.g, false);
		
		// debug printer buffer
		// p.image(printDirect.printBuffer(), 0, 0, printDirect.printBuffer().width * 0.1f, printDirect.printBuffer().height * 0.1f);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') printDirect.printImage(printBuffer);
	}

}

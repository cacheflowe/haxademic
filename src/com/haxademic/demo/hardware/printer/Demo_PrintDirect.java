package com.haxademic.demo.hardware.printer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.printer.PrintPageDirectNew;

import processing.core.PGraphics;

public class Demo_PrintDirect 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PrintPageDirectNew printDirect;
	protected PGraphics printBuffer;
	
	
	public void setupFirstFrame () {
		printDirect = new PrintPageDirectNew(false);
		// printBuffer = P.p.createGraphics(PrintPageDirect.PRINT_W * 4, PrintPageDirect.PRINT_W * 3, PRenderers.P3D);
		printBuffer = P.p.createGraphics(1600, 2000, PRenderers.P3D);
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
		PG.setDrawCorner(printBuffer);
		printBuffer.rect(borderSize/2, borderSize/2, printBuffer.width - borderSize, printBuffer.height - borderSize);
		
		// draw squares
		printBuffer.stroke(0);
		printBuffer.strokeWeight(6);
		PG.setCenterScreen(printBuffer);
		PG.setDrawCenter(printBuffer);
		for (int i = 0; i < 100; i++) {
			float randSize = p.random(20, 1000);
			float randRot = p.random(P.TWO_PI);
			printBuffer.pushMatrix();
			printBuffer.rotate(randRot);
			printBuffer.rect(p.random(0, printBuffer.width), p.random(0, printBuffer.height), randSize, randSize);
			printBuffer.popMatrix();
		}
		

		
		// draw moire lines
		float numLines = 90f;
		float spacing = printBuffer.height / numLines;
//		printBuffer.translate(0, printBuffer.height);	// rotate
//		printBuffer.rotate(-P.HALF_PI);
//		for (int i = 2; i < numLines - 1; i++) {
//			float y = i * spacing;
//			float strokeW = P.map(i, 0, numLines, 12f, 5f);
//			printBuffer.noFill();
//			printBuffer.stroke(0);
//			printBuffer.strokeWeight(strokeW);
//			printBuffer.beginShape();
//			printBuffer.vertex(0, y);
//			printBuffer.quadraticVertex(printBuffer.width * 0.25f, y + P.sin(i * 0.1f) * strokeW * 8f, printBuffer.width / 2f, y);
//			printBuffer.quadraticVertex(printBuffer.width * 0.75f, y + -P.sin(i * 0.1f) * strokeW * 8f, printBuffer.width, y);
//			printBuffer.endShape();
//		}
		 
		
//		printBuffer.blendMode(PBlendModes.SUBTRACT);
//		printBuffer.translate(-printBuffer.width/2 + 35, -printBuffer.height/2 + 35);
//		PG.drawTestPattern(printBuffer);
//		printBuffer.blendMode(PBlendModes.BLEND);
		
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

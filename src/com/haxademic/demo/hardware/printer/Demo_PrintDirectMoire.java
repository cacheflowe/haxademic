package com.haxademic.demo.hardware.printer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.printer.PrintPageDirect;

public class Demo_PrintDirectMoire 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PrintPageDirect printDirect;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 960);
		p.appConfig.setProperty(AppSettings.HEIGHT, 960);
	}
	
	public void setupFirstFrame () {
		printDirect = new PrintPageDirect(false);
		pg = P.p.createGraphics(PrintPageDirect.PRINT_W, PrintPageDirect.PRINT_H, PRenderers.P2D);
	}

	public void drawApp() {
		p.background( 0 );
//		if(p.frameCount % 100 == 0) 		pg = P.p.createGraphics(PrintPageDirect.PRINT_W, PrintPageDirect.PRINT_H, PRenderers.P3D);


		////////////////////////////////////////
		// draw stuff!
		
		pg.beginDraw();
		
		// prep black on white bg
		pg.background(255);
		pg.fill(255);
		
		// add border
		float borderSize = 16;
		pg.stroke(0);
		pg.strokeWeight(borderSize);
		DrawUtil.setDrawCorner(pg);
//		pg.rect(borderSize/2, borderSize/2, pg.width - borderSize, pg.height - borderSize);
		
		// draw lines
		float numLines = 100f;
		float spacing = pg.height / numLines;
		for (int i = 0; i < numLines; i++) {
			float y = i * spacing;
			float strokeW = P.map(i, 0, numLines, 20f, 1.5f);
			pg.noFill();
			pg.strokeWeight(strokeW);
			pg.beginShape();
			pg.vertex(0, y);
			pg.quadraticVertex(pg.width * 0.25f, y + P.sin(i * 0.1f) * strokeW * 10f, pg.width / 2f, y);
			pg.quadraticVertex(pg.width * 0.75f, y + -P.sin(i * 0.1f) * strokeW * 10f, pg.width, y);
			pg.endShape();
		}
		
		// draw shape overlay
		pg.pushMatrix();
		DrawUtil.setDrawCenter(pg);
//		DrawUtil.setCenterScreen(pg);
		pg.translate(pg.width / 2, pg.height / 2);
		pg.strokeWeight(10);
		pg.fill(255, 255);
		pg.stroke(0);
		pg.noStroke();
		pg.ellipse(0, 0, pg.width * 0.5f, pg.width * 0.51f);
		pg.popMatrix();
		
		
		pg.endDraw();
		
		// end draw stuff
		////////////////////////////////////////

		// draw image to screen
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// debug printer buffer
		// p.image(printDirect.pg(), 0, 0, printDirect.pg().width * 0.1f, printDirect.pg().height * 0.1f);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') printDirect.printImage(pg);
	}

}

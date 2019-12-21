package com.haxademic.demo.hardware.printer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.printer.PrintPageDirect;

public class Demo_PrintDirectMoire 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PrintPageDirect printDirect;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 960);
		Config.setProperty(AppSettings.HEIGHT, 960);
	}
	
	public void firstFrame () {
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
		PG.setDrawCorner(pg);
//		pg.rect(borderSize/2, borderSize/2, pg.width - borderSize, pg.height - borderSize);
		
		// draw lines
		float numLines = 80f;
		float spacing = pg.height / numLines;
		for (int i = 0; i < numLines; i++) {
			float y = i * spacing;
			float strokeW = P.map(i, 0, numLines, 20f, 5f);
			pg.noFill();
			pg.strokeWeight(strokeW);
			pg.beginShape();
			pg.vertex(0, y);
			pg.quadraticVertex(pg.width * 0.25f, y + P.sin(i * 0.1f) * strokeW * 11f, pg.width / 2f, y);
			pg.quadraticVertex(pg.width * 0.75f, y + -P.sin(i * 0.1f) * strokeW * 11f, pg.width, y);
			pg.endShape();
		}
		
		// draw shape overlay
		/*
		pg.pushMatrix();
		PG.setDrawCenter(pg);
//		PG.setCenterScreen(pg);
		pg.translate(pg.width / 2, pg.height / 2);
		pg.strokeWeight(6);
		pg.fill(255, 255);
		pg.stroke(0);
//		pg.noStroke();
		pg.ellipse(0, 0, pg.width * 0.45f, pg.width * 0.45f);
		pg.popMatrix();
		*/
		pg.blendMode(PBlendModes.ADD);
		PG.drawTestPattern(pg);
		pg.blendMode(PBlendModes.BLEND);
		
		pg.endDraw();
		
		// end draw stuff
		////////////////////////////////////////

		// draw image to screen
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// debug printer buffer
		// p.image(printDirect.printBuffer(), 0, 0, printDirect.printBuffer().width * 0.1f, printDirect.printBuffer().height * 0.1f);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') printDirect.printImage(pg);
	}

}

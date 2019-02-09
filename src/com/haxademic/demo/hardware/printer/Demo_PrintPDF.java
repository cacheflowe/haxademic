package com.haxademic.demo.hardware.printer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.printer.PrintPageDirect;
import com.haxademic.core.hardware.printer.PrintPageDirectNew;

import processing.core.PGraphics;

public class Demo_PrintPDF 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
	}

	public void drawApp() {
		if(p.frameCount == 1) {
			p.beginRecord(PDF, FileUtil.getHaxademicOutputPath() + "pdf/outputt.pdf");
			p.background( 255 );

		
			// draw moire lines
			float numLines = 100f;
			float spacing = p.height / numLines;
	//		printBuffer.translate(0, printBuffer.height);	// rotate
	//		printBuffer.rotate(-P.HALF_PI);
			for (int i = 2; i < numLines - 1; i++) {
				float y = i * spacing;
				float strokeW = P.map(i, 0, numLines, 6f, 4f);
				p.noFill();
				p.stroke(0);
				p.strokeWeight(strokeW);
				p.beginShape();
				p.vertex(0, y);
				p.quadraticVertex(p.width * 0.25f, y + P.sin(i * 0.1f) * strokeW * 8f, p.width / 2f, y);
				p.quadraticVertex(p.width * 0.75f, y + -P.sin(i * 0.1f) * strokeW * 8f, p.width, y);
				p.endShape();
			}
		 
			p.endRecord();
		}
	}
	
}

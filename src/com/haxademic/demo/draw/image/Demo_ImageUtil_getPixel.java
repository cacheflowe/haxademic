package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PFont;
import processing.core.PVector;

public class Demo_ImageUtil_getPixel 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	protected String A = "A";
	protected PVector samplePoint;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		// UI 
		UI.addTitle("DRAW COLOR");
		UI.addSlider(R, 127, 0, 255, 1, false);
		UI.addSlider(G, 127, 0, 255, 1, false);
		UI.addSlider(B, 127, 0, 255, 1, false);
		UI.addSlider(A, 127, 0, 255, 1, false);
		
		samplePoint = new PVector(p.width / 2, p.height / 2);
	}
	
	protected void drawApp() {
		background(0);
		
		// DRAW AN IMAGE WITH TRANSPARENCY
		// SEE IF READING THE PIXEL CAN MAINTAIN THE RGB VALUES AS ALPHA DECREASES
		// SOLUTION! MUST USE **REPLACE** BLEND MODE
		int drawColor = p.color(UI.value(R), UI.value(G), UI.value(B), UI.value(A));
		pg.beginDraw();
		pg.blendMode(PBlendModes.REPLACE);
		pg.clear();
		pg.background(0, 0);
		pg.fill(drawColor);
		pg.rect(pg.width * 0.25f, pg.height * 0.25f, pg.width * 0.5f, pg.height * 0.5f);
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// load pixels
		pg.loadPixels();
		
		// sample color
		int sampleX = P.round(samplePoint.x);
		int sampleY = P.round(samplePoint.y);
		int readColor = ImageUtil.getPixelColor(pg, sampleX, sampleY);
		String color = 
//				pg.format + FileUtil.NEWLINE +
				"Draw: " +
				P.round(p.red(drawColor)) + ", " + 
				P.round(p.green(drawColor)) + ", " + 
				P.round(p.blue(drawColor)) + ", " + 
				P.round(p.alpha(drawColor)) + " | " + 
				P.hex(drawColor) + FileUtil.NEWLINE +
				"Read: " +
				P.round(p.red(readColor)) + ", " + 
				P.round(p.green(readColor)) + ", " + 
				P.round(p.blue(readColor)) + ", " + 
				P.round(p.alpha(readColor)) + " | " + 
				P.hex(readColor);
		
		// show sample point
		p.push();
		PG.setDrawCenter(p);
		p.noFill();
		p.stroke(255, 0, 0);
		p.circle(sampleX, sampleY, 5);
		p.pop();
		
		// PROVE IT!
		// WRITE RGBA VALUES IN TEXT TO SCREEN
		// FOLLOW MOUSE
		
		// draw text to screen
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 36);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1.3f, PTextAlign.CENTER, PTextAlign.TOP);
		p.text(color, 0, p.height - 120, p.width, 120);
	}
	
	public void mouseClicked() {
		super.mouseClicked();
		samplePoint.set(Mouse.x, Mouse.y);
	}
}

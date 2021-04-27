package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.AppUtil;
import com.haxademic.core.system.ScreenshotUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PFont;
import processing.core.PImage;

public class Demo_ScreenUtil_ColorPicker
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String _x = "_x";
	protected String _y = "_y";
	protected String curHex = "ff0000";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 320 );
		Config.setProperty( AppSettings.HEIGHT, 320 );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, true );
	}

	protected void firstFrame() {
		UI.addSlider(_x, 0, 0, p.displayWidth * 3, 4, false);
		UI.addSlider(_y, 0, 1, p.displayHeight * 3, 4, false);
		OpenGLUtil.setTextureQualityLow(pg);	// pixelated (nearest-neighbor) scale-up
	}

	protected void drawApp() {
		p.background(0);
		p.noStroke();
		
		// get screenshot & scale up
		PImage screenshot = ScreenshotUtil.getScreenShotAsPImage(UI.valueInt(_x), UI.valueInt(_y), 40, 40);
		ImageUtil.copyImage(screenshot, pg);
		p.image(pg, 0, 0);
		
		// load pixels and get color
		pg.loadPixels();
		int sampleColor = ImageUtil.getPixelColor(pg, mouseX, mouseY);
		p.fill(0, 180);
		p.rect(0, p.height - 32, p.width, 32);
		p.fill(255);
		
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 24);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		curHex = "#"+P.hex(sampleColor).substring(2);
		p.text(curHex, 0, p.height - 40, p.width, 40);
		
		// draw selection
		p.noFill();
		p.stroke(0, 255, 0);
		p.strokeWeight(2);
		p.rect(mouseX - (mouseX % 8), mouseY - (mouseY % 8), 8, 8);
		
		// update title
		AppUtil.setTitle(P.p, P.window.getBounds().getX() + ", " + P.window.getBounds().getY() + " | " + P.round(p.frameRate) + "fps");
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.keyCode == 67 && KeyboardState.instance().isKeyOn(17)) { // ctrl + c
			SystemUtil.copyStringToClipboard(curHex);
			P.out("COPIED", curHex);
		}
	}

}

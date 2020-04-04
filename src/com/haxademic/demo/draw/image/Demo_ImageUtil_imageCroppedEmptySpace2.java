
package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.FitTextSourceBuffer;
import com.haxademic.core.text.RandomStringUtil;

import processing.core.PImage;

public class Demo_ImageUtil_imageCroppedEmptySpace2
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FitTextSourceBuffer fitText;
	protected PImage croppedTextResult = null;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setup()	{
		croppedTextResult = p.createImage(128, 128, P.ARGB);
		
		int fontSize = 200;
		fitText = new FitTextSourceBuffer(DemoAssets.fontDSEG7(fontSize), p.color(255));
		
		newTestString();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newTestString();
	}

	protected void drawApp() {
		background(100,155,100);
		
		p.image(fitText.buffer(), 100, 100);
		if(croppedTextResult != null) p.image(croppedTextResult, 20, 500);
	}
	
	protected void newTestString() {
		String str = RandomStringUtil.randomStringOfLength(MathUtil.randRange(3, 10), RandomStringUtil.ALPHANUMERIC);
		fitText.updateText(str);
		// ImageUtil.imageCroppedEmptySpace(fitText.buffer(), croppedTextResult, ImageUtil.BLACK_INT, true); // EMPTY_INT
		ImageUtil.imageCroppedEmptySpace(fitText.buffer(), croppedTextResult, ImageUtil.EMPTY_INT, false, new int[] {20, 20, 20, 20}, new int[] {0, 0, 0, 0}, p.color(0)); // EMPTY_INT
	}

}

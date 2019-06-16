package com.haxademic.demo.hardware.dmx;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.dmx.DMXFixture;

public class Demo_DmxFixturesPride
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ArrayList<DMXFixture> fixture;
	protected String brightness = "brightness";
	protected String speed = "speed";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.DMX_PORT, "COM6" );
		p.appConfig.setProperty(AppSettings.DMX_BAUD_RATE, 9600 );
	}

	public void setupFirstFrame() {
		fixture = new ArrayList<DMXFixture>(); 
		for (int i = 0; i < 12; i++) {
			fixture.add((new DMXFixture(1 + i * 3)).setEaseFactor(0.1f));
		}
		p.ui.addSlider(brightness, 0.5f, 0, 1, 0.01f);
		p.ui.addSlider(speed, 0.3f, 0, 5, 0.01f);
		p.ui.addWebInterface(false);
	}

	public void drawApp() {
		// update pride texture
		pg.beginDraw();
		pg.noStroke();
		float numColors = ColorsHax.PRIDE.length;
		for (int i = 0; i < numColors; i++) {float colW = pg.width / numColors;
			float x = colW * i;
			pg.fill(ColorsHax.PRIDE[i]);
			pg.rect(x, 0, colW, pg.height);
		}
		pg.endDraw();
		BrightnessFilter.instance(p).setBrightness(p.ui.value(brightness));
		BrightnessFilter.instance(p).applyTo(pg);
		RotateFilter.instance(p).applyTo(pg);
		
		// draw & scroll
		p.image(pg, 0, 0);
		RotateFilter.instance(p).setOffset(p.frameCount * (0.001f * p.ui.value(speed)), 0);
		
		// send colors to fixtures
		p.loadPixels();
		for (int i = 0; i < fixture.size(); i++) {
			fixture.get(i).color().setTargetInt(ImageUtil.getPixelColor(p.g, 10 + i * 30, pg.height/2));
//			background(fixture.get(i).color().colorInt());
		}
	}
}






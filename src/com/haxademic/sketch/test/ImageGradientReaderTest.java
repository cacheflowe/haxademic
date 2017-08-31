package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.file.FileUtil;

public class ImageGradientReaderTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	ImageGradient imageGradient;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 90 );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P3D );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();
		imageGradient = new ImageGradient(p.loadImage(FileUtil.getFile("images/gradient.png")));
	}

	public void drawApp() {
		float colorProgress = 0.5f + 0.5f * P.sin(p.frameCount * 0.01f);
		p.background(imageGradient.getColorAtProgress(colorProgress));
		
		p.pushMatrix();
		p.translate(p.width/2 - imageGradient.texture().width/2, p.height/2);
		imageGradient.drawDebug(p.g);
		p.popMatrix();
	}
}


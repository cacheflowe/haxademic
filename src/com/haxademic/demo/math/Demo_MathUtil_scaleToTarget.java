package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_MathUtil_scaleToTarget 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage img;
	
	public void firstFrame() {

		img = DemoAssets.justin();
		OpenGLUtil.setTextureQualityHigh(p.g);
	}
	
	public void drawApp() {
		p.background(0);
		PG.setDrawCenter(p);
		
		p.ambient(127);
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(p.color(200)); 
		p.emissive(p.color(20)); 
		p.shininess(5.0f); 
		
		float imageSize = 300f;
		float imgScale = MathUtil.scaleToTarget(img.height, imageSize);
		p.translate(p.width / 2, p.height / 2);
		p.rotateY(0.2f * P.sin(p.frameCount * 0.03f));
		
		// draw image at 300 pixels tall
		p.image(img, 0, 0, img.width * imgScale, img.height * imgScale);
		
		// put down floor
		p.translate(0, img.height * imgScale / 2);
		p.rotateX(P.HALF_PI);
		p.fill(100,0,100);
		p.rect(0, 0, imageSize, imageSize);
	}
}

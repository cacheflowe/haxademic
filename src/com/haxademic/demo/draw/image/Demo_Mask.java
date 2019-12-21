package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.video.Movie;

public class Demo_Mask
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics mask;
	protected PGraphics maskInverse;
	protected PGraphics image1, image2;
	protected PShape svgMask;
	protected Movie video;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 1000 );
	}

	public void firstFrame() {
		mask = PG.newPG(p.width, p.height);
		maskInverse = PG.newPG(p.width, p.height);
		image1 = PG.newPG(p.width, p.height);
		image2 = PG.newPG(p.width, p.height);

		video = DemoAssets.movieFractalCube();
		video.loop();
		
		svgMask = DemoAssets.shapeX();
		PShapeUtil.scaleShapeToHeight(svgMask, p.height * 0.75f);
		PShapeUtil.centerShape(svgMask);
		svgMask.disableStyle();
	}

	public void drawApp() {
		p.background(0);
		
		// draw mask buffer
		mask.beginDraw();
		mask.background(0);
		mask.fill(255, 127 + 127 * P.sin(p.frameCount * 0.02f));
		PG.setDrawCorner(mask);
		PG.setCenterScreen(mask);
		mask.rotate(p.frameCount * 0.01f);
		mask.shape(svgMask, 0, 0);
		mask.endDraw();
		
		// draw image 1 for mask 1
		image1.beginDraw();
		image1.background(0);
		PG.setDrawCenter(image1);
		PG.setCenterScreen(image1);
		image1.rotate(p.millis()/800f);
		if(p.frameCount % 1000 < 500) {
			image1.image(DemoAssets.squareTexture(), 0, 0, image1.width, image1.height );
		} else {
			image1.image(video, 0, 0, image1.width, image1.height );
		}
		image1.endDraw();
		image1.mask(mask);
		p.image(image1, 0, 0);
		
		// draw inverse mask
		maskInverse.beginDraw();
		maskInverse.background(255);
		maskInverse.fill(0);
		PG.setDrawCorner(mask);
		PG.setCenterScreen(maskInverse);
		maskInverse.rotate(p.frameCount * 0.01f);
		maskInverse.shape(svgMask, 0, 0);
		maskInverse.endDraw();

		// draw 2nd image with inverse mask
		image2.beginDraw();
		image2.background(0);
		PG.setDrawCenter(image2);
		image2.translate(image2.width/2, image2.height/2);
		image2.rotate(p.millis()/1000f);
		image2.image(DemoAssets.textureNebula(), 0, 0, image2.width*2, image2.height*2 );
		image2.endDraw();
		image2.mask( maskInverse );
		p.image(image2, 0, 0);
		
		// draw elements for debug
		p.image(mask, 0, 0, 100, 100);
		p.image(image1, 0, 100, 100, 100);
		p.image(maskInverse, 0, 200, 100, 100);
		p.image(image2, 0, 300, 100, 100);
	}

}
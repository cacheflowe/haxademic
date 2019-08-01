package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Movie;

public class Demo_ParticlesFromMap 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video;
	protected ArrayList<ShapeParticle> shapes = new ArrayList<ShapeParticle>();
	protected PImage[] particleImages;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 160 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		// create map
		video = DemoAssets.movieKinectSilhouette();
		video.loop();
		
		// load particle source textures
		particleImages = new PImage[] {
			DemoAssets.particle(),
		};
	}
	
	public void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		if(video.width > 100) ImageUtil.cropFillCopyImage(video, pg, false);
		pg.endDraw();
		pg.loadPixels();
		
		// launch particles
		int numLaunched = 0;
		for (int i = 0; i < 2000; i++) {
			int checkX = MathUtil.randRange(0, pg.width);
			int checkY = MathUtil.randRange(0, pg.height);
			int pixelColor = ImageUtil.getPixelColor(pg, checkX, checkY);
			float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
			if(redColor > 0.5f && numLaunched < 50) {
				launchShape(checkX, checkY);
				numLaunched++;
			}
		}
		// draw particles on top
		pg.beginDraw();
		PG.setDrawCenter(pg);
		for (int i = 0; i < shapes.size(); i++) {
			shapes.get(i).update(pg);
		}
		PG.setDrawCorner(pg);
		pg.endDraw();
		
		p.debugView.setValue("shapes.size()", shapes.size());
		p.image(pg, 0, 0);
	}
	
	protected void launchShape(float x, float y) {
		// look for an available shape
		for (int i = 0; i < shapes.size(); i++) {
			if(shapes.get(i).available()) {
				shapes.get(i).launch(x, y);
				return;
			}
		}
		// didn't find one
		if(shapes.size() < 10000) {
			ShapeParticle newShape = new ShapeParticle();
			newShape.launch(x, y);
			shapes.add(newShape);
		}
	}
	
	
	public class ShapeParticle {
		
		protected PVector pos = new PVector(0, -100, 0);
		protected PVector speed = new PVector(0, 0, 0);
		protected PVector gravity = new PVector(0, 0, 0);
		protected float size = 10f;
		protected float rotation = 30f;
		protected LinearFloat sizeProgress = new LinearFloat(0, 0.04f);
		protected int color;
		protected PImage image;
		
		public ShapeParticle() {
			
		}
		
		public void update(PGraphics pg) {
			if(available()) return;
			
			// update position
			gravity.x *= 0.97f;
			speed.add(gravity);
			pos.add(speed);
			rotation += gravity.z;
			
			// update size
			sizeProgress.update();
			float curSize = size * Penner.easeOutBack(sizeProgress.value(), 0, 1, 1);
			if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
			
			// draw image
			pg.pushMatrix();
			pg.translate(pos.x, pos.y);
			pg.rotate(rotation);
			pg.image(image, 0, 0, curSize * 2f, curSize * 2f);
			pg.popMatrix();
		}
		
		public void launch(float x, float y) {
			// get random particle texture
			if(particleImages != null) image = particleImages[MathUtil.randRange(0, particleImages.length - 1)];
			
			// random params
			size = MathUtil.randRangeDecimal(10, 20);
			sizeProgress.setCurrent(0);
			sizeProgress.setTarget(1);
			
			// set motion properties
			pos.set(x, y, 0);
			speed.set(0, -0.5f, 0);
			rotation = P.p.random(P.TWO_PI);
			gravity.set(MathUtil.randRangeDecimal(-0.05f, 0.05f), MathUtil.randRangeDecimal(-0.02f, -0.09f), MathUtil.randRangeDecimal(-0.02f, 0.02f)); // z is rotation!
		}
		
		public boolean available() {
			return pos.y < -100 || pos.y > p.height + 100 || (sizeProgress.value() == 0 && sizeProgress.target() == 0);
		}
	}


		
}
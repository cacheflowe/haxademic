package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PVector;

public class Demo_MathUtil_getRadiansDirectionToTarget
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics arrowImg;
	protected ArrowDirection[] arrows;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
	}

	public void setupFirstFrame() {

		
		// load texture
		arrowImg = ImageUtil.imageToGraphics(DemoAssets.arrow());

		// layout vars
		int spacing = p.width / 6;
		int startX = P.round(p.width / 2 - spacing * 1.5f);
		int startY = P.round(p.height / 2 - spacing * 1.5f);

		// new arrows
		arrows = new ArrowDirection[16];
		for (int i = 0; i < arrows.length; i++) {
			arrows[i] = new ArrowDirection();
			
			// layout
			int x = startX + (i % 4) * spacing;
			int y = startY + P.floor(i / 4) * spacing;
			arrows[i].setPosition(x, y);
		}
	}

	public void drawApp() {
		background(200,100,255);
		PG.setDrawCenter(p);
		p.noStroke();
		
		// sometimes set a random rotation
		if(p.frameCount % 120 == 60) {
			int randomIndex = MathUtil.randRange(0, arrows.length - 1);
//			int randomIndex = MathUtil.randRange(0, 1);
			arrows[randomIndex].setRandomRotation();
		}
		
		// sometimes pick two and have them point away from each other
		if(p.frameCount % 120 == 1) {
			int randomIndex1 = MathUtil.randRange(0, arrows.length - 1);
			int randomIndex2 = MathUtil.randRange(0, arrows.length - 1);
			while(randomIndex2 == randomIndex1) randomIndex2 = MathUtil.randRange(0, arrows.length - 1);
//			int randomIndex1 = MathUtil.randRange(0, 1);
//			int randomIndex2 = MathUtil.randRange(0, 1);
			
			arrows[randomIndex1].pointAwayFrom(arrows[randomIndex2]);
			arrows[randomIndex2].pointAwayFrom(arrows[randomIndex1]);
		}
		
		// update/draw arrows
		for (int i = 0; i < arrows.length; i++) {
			arrows[i].update();
		}
	}
	
	public class ArrowDirection {
		
		public EasingFloat rotation = new EasingFloat(0, 0.1f);
		public PVector position;
		public LinearFloat pointAwayProgress = new LinearFloat(0, 0.025f);
		
		public ArrowDirection() {
			position = new PVector(p.random(p.width), p.random(p.height), 0);
		}
		
		public void update() {
			rotation.updateRadians();
			
			pointAwayProgress.update();
			
			// draw arrow
			p.pushMatrix();

			p.translate(position.x, position.y);
			p.rotate(rotation.value());
			p.image(arrowImg, 0, 0, arrowImg.width * 0.2f, arrowImg.height * 0.2f);
			
			// show indication that we're pointing away
			p.fill(255, 255f * pointAwayProgress.value());
			p.rect(0, 0, arrowImg.width * 0.25f, arrowImg.height * 0.25f);

			
			p.popMatrix();
		}
		
		public void setPosition(float x, float y) {
			position.set(x, y);
		}
		
		public void setRandomRotation() {
			rotation.setTarget(p.random(P.TWO_PI));
		}
		
		public void pointAwayFrom(ArrowDirection otherArrow) {
			// show indication
			pointAwayProgress.setCurrent(1);
			pointAwayProgress.setTarget(0);

			// do rotation calculation
			float directionAway = MathUtil.getRadiansToTarget(position.x, position.y, otherArrow.position.x, otherArrow.position.y) + P.PI;
			
			// rotate 180 degrees away from other
			rotation.setTarget(directionAway);
			
			// rotate slightly away from other
//			float dir = MathUtil.getRadiansDirectionToTarget(rotation.value(), directionAway);
//			rotation.setTarget(rotation.value() + dir * 0.8f);
		}
		
	}

}


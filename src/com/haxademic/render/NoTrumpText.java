package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PShape;

public class NoTrumpText
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape textNo;
	protected PShape textHate;
	protected PShape textWar;
	protected PShape textLies;
	protected PShape textTrump;
	protected PShape[] badWords;
	protected float frames = 700;
	protected int colorR = ColorUtil.colorFromHex("#cc0A26");
	protected int colorW = ColorUtil.colorFromHex("#ffffff");
	protected int colorB = ColorUtil.colorFromHex("#17134d"); // #2C285B

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
//		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int) frames + 1 );
	}

	public void setup()	{
		super.setup();
		textToPShape = new TextToPShape();
		String fontFile = FileUtil.getFile("fonts/CenturyGothic.ttf");
		
		float depth = 10;
		textNo = textToPShape.stringToShape3d("NO", depth, fontFile);
		textNo.disableStyle();
		textHate = textToPShape.stringToShape3d("HATE", depth, fontFile);
		textHate.disableStyle();
		textWar = textToPShape.stringToShape3d("WAR", depth, fontFile);
		textWar.disableStyle();
		textLies = textToPShape.stringToShape3d("LIES", depth, fontFile);
		textLies.disableStyle();
		textTrump = textToPShape.stringToShape3d("TRUMP", depth, fontFile);
		textTrump.disableStyle();
		
		badWords = new PShape[] { textHate, textWar, textLies, textTrump };
//		PShapeUtil.scaleObjToExtent(word3d, 800);
//		PShapeUtil.scaleObjToExtent(word2d, 800);
	}

	public void drawApp() {
		// anim progress
		float progress = (p.frameCount % frames) / frames; 
		float loopProgress = progress % 1f;
		float easedProgress = Penner.easeInOutCubic(loopProgress, 0, 1, 1);
//		float easedPercent = Penner.easeInOutQuart(progress % 1, 0, 1, 1);
		float progressRads = progress * P.TWO_PI;
		
		// text scale
		float textScale = 1.7f;
		
		
//		ortho();
//		PG.setBetterLights(p);
		lights();
		background(0); // colorB
		translate(width/2, height/2, 0);
//		rotateX(P.map(p.mouseY, 0, p.height, -1f, 1f));
		//rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
		p.rotateY(0.02f * P.sin(progressRads));

		// draw NO
		p.pushMatrix();
//		p.rotateX(0.1f * P.sin(progressRads));
		p.rotateX(0.05f * P.sin(progressRads));
		p.translate(-300,0,0);
		p.fill(colorW);
		p.scale(textScale * 1.4f + (textScale * 0.2f) * P.sin(progressRads));
		p.shape(textNo);
		p.popMatrix();
		
		// draw bad words
		float drawColumnSize = 12000f;
		float startZ = -drawColumnSize - drawColumnSize * progress / 2f;
		int wordIndex = 0;
		for (float i = startZ; i < drawColumnSize; i += 250) {
			p.pushMatrix();
//			p.rotateX(1.f * P.sin(progressRads));
			p.rotateX(0.7f + 0.3f * P.sin(progressRads));
			p.translate(200, i, 0); // -200f + 200f * P.sin(progressRads)
			p.fill(colorR);
			p.scale(textScale);
			p.shape(badWords[wordIndex % badWords.length]);
			p.popMatrix();
			wordIndex++;
		}
	}

}

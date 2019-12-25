package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class StayWokeVote
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape wordStay;
	protected PShape wordWoke;
	protected PShape wordVote[];
	protected float wordVoteXPositions[];
	protected LinearFloat stayProgress = new LinearFloat(0, 0.025f);
	protected LinearFloat wokeProgress = new LinearFloat(0, 0.025f);
	protected LinearFloat voteProgress = new LinearFloat(0, 0.025f);
	protected EasingColor bgColor = new EasingColor("#000000");
	
	PShape starSvg;
	protected LinearFloat starsProgress = new LinearFloat(2, 0.015f);

	protected void config() {
		int FRAMES = 300;
		Config.setProperty( AppSettings.WIDTH, 1080 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 1) + 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 2) + 1 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void firstFrame()	{
		textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
		String fontFile = FileUtil.getPath("fonts/LubalinGraph-Demi.ttf");
		wordStay = textToPShape.stringToShape3d("STAY", 20, fontFile);
		wordWoke = textToPShape.stringToShape3d("WOKE", 20, fontFile);
		String fontFile2 = FileUtil.getPath("fonts/Nexa-XBold.ttf");
		PShapeUtil.scaleShapeToHeight(wordStay, p.height * 0.3f);
		PShapeUtil.scaleShapeToHeight(wordWoke, p.height * 0.3f);
		wordStay.disableStyle();
		wordWoke.disableStyle();
		
		// vote letters
		wordVote = new PShape[] {
				textToPShape.stringToShape3d("V", 40, fontFile2),
				textToPShape.stringToShape3d("O", 40, fontFile2),
				textToPShape.stringToShape3d("T", 40, fontFile2),
				textToPShape.stringToShape3d("E", 40, fontFile2),
		};
		for (int i = 0; i < wordVote.length; i++) {
			PShapeUtil.centerShape(wordVote[i]);
			PShapeUtil.scaleShapeToHeight(wordVote[i], p.height * 0.5f);
			wordVote[i].disableStyle();
		}
		wordVoteXPositions = new float[] {-0.4f, -0.2f, 0.2f, 0.4f};
		
		// star
		starSvg = p.loadShape(FileUtil.getPath("svg/star-5.svg")).getTessellation();
		PShapeUtil.repairMissingSVGVertex(starSvg);
		PShapeUtil.centerShape(starSvg);
		PShapeUtil.scaleShapeToMaxAbsY(starSvg, p.height * 0.1f);
		starSvg = PShapeUtil.createExtrudedShape(starSvg, p.height * 0.1f);
		starSvg.disableStyle();
	}

	public void drawApp() {
		if(FrameLoop.progress() < 0.55f || FrameLoop.progress() > 0.85f) bgColor.setTargetHex("#000000"); else bgColor.setTargetHex("#ffffff");
		bgColor.update();
		p.background(bgColor.colorInt());
		PG.setCenterScreen(p);
		p.translate(0, 0, -600f);
		
		// setup lighting props
//		p.ambient(10);
		float specularr = 210;
		p.lightSpecular(specularr, specularr, specularr); 
		p.directionalLight(200, 200, 200, -10f, -10f, 1); 
		p.directionalLight(200, 200, 200, 10f, 10f, -10); 
		p.specular(p.color(200)); 
		p.shininess(5.0f); 

		// mouse rotation
		rotateX(P.cos(FrameLoop.progressRads() * 2f) * 0.1f);
		rotateY(P.sin(FrameLoop.progressRads() * 2f) * 0.1f);
//		rotateX(P.map(p.mouseY, 0, p.height, -1f, 1f));
//		rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));

		// draw word
		p.fill(255);
		
		
		// overall animation props
		// stay woke anim props
		p.pushMatrix();
		float distFromCenter = (p.height * 0.23f);// + showProgressEased * (p.height * 0.7f);
		
		// STAY
		stayProgress.setTarget((FrameLoop.progress() <= 0.45f || FrameLoop.progress() >= 0.95f) ? 1 : 0);
		stayProgress.update();
		float stayProgressEased = Penner.easeInOutExpo(stayProgress.value(), 0, 1, 1);

		p.fill(255);

		p.pushMatrix();
//		p.translate(0, -distFromCenter, P.map(showProgressEased, 0, 1, 0, p.width/1f));
		p.scale(P.map(stayProgressEased, 1, 0, 1.5f, 1f));
		p.rotateY(P.map(stayProgressEased, 1, 0, 0.2f, 0));
//		p.rotateX(P.map(stayProgressEased, 1, 0, 0, P.QUARTER_PI));
		p.translate(p.width * -0.09f, -distFromCenter, 0);
		if(stayProgress.target() == 0)
			p.translate(0, -P.map(stayProgressEased, 1, 0, 0, p.width * 5f), 0);
		else 
			p.translate(P.map(stayProgressEased, 1, 0, 0, p.width * -5f), 0, 0);
		p.shape(wordStay);
		p.popMatrix();

		// WOKE
		wokeProgress.setTarget((FrameLoop.progress() <= 0.5f) ? 1 : 0);
		wokeProgress.update();
		float wokeProgressEased = Penner.easeInOutExpo(wokeProgress.value(), 0, 1, 1);


		p.pushMatrix();
//		p.translate(0, distFromCenter, P.map(wokeProgressEased, 1, 0, 0, p.width/1f));
		p.scale(P.map(wokeProgressEased, 1, 0, 1.5f, 1f));
		p.rotateY(P.map(wokeProgressEased, 1, 0, -0.2f, 0));
//		p.rotateX(P.map(wokeProgressEased, 1, 0, 0, -P.QUARTER_PI));
		p.translate(0, distFromCenter, 0);
		if(wokeProgress.target() == 0)
			p.translate(0, P.map(wokeProgressEased, 1, 0, 0, p.width * 5f), 0);
		else 
			p.translate(P.map(wokeProgressEased, 1, 0, 0, p.width * 5f), 0, 0);
		p.shape(wordWoke);
		p.popMatrix();
		
		p.popMatrix();
		
		
		// VOTE
		float showTarget = (FrameLoop.progress() <= 0.525f || FrameLoop.progress() >= 0.95f) ? 0 : 1; 
		voteProgress.setTarget(showTarget);
		voteProgress.update();
		float voteProgressEased = Penner.easeInOutExpo(voteProgress.value(), 0, 1, 1);
		
		p.fill(0, 0, 127);

		p.pushMatrix();
		float maxVoteScale = 1.25f;
		if(voteProgress.target() == 1) {
			p.scale(P.map(voteProgressEased, 0, 1, 0, maxVoteScale));
			p.translate(0, 0, P.map(voteProgressEased, 0, 1, -p.width, 0));
		} else {
			p.scale(maxVoteScale);
			p.translate(0, P.map(voteProgressEased, 0, 1, 10f * p.width, 0), 0);
		}
		
		wordVoteXPositions[0] = -0.45f;
		wordVoteXPositions[1] = -0.13f;
		wordVoteXPositions[2] = 0.16f;
		wordVoteXPositions[3] = 0.45f;
		
		for (int i = 0; i < wordVote.length; i++) {
			p.pushMatrix();
			float letterX = pg.width * wordVoteXPositions[i];
			if(voteProgress.target() == 0) {	// transition out explode
				letterX *= (1f + 2.1f * -(voteProgress.value() - 1f));
			}
			
			p.translate(letterX, 0);
			p.rotateX(0.1f * P.sin(i * 1f + 4f * FrameLoop.progressRads()));
			p.rotateY(0.1f * P.sin(i * 10f + 4f * FrameLoop.progressRads()));
			p.translate(0, pg.height * 0.05f * P.sin(i + 4f * FrameLoop.progressRads()));
			
			// add red circle stripes behind "O"
			if(i == 1) drawCircleBg();
			
			p.shape(wordVote[i]);
			p.popMatrix();
		}
		p.popMatrix();
		
		
		// stars!
		if(FrameLoop.progress() > 0.83f && FrameLoop.progress() < 0.85f) {
			starsProgress.setInc(0.02f);
			starsProgress.setCurrent(-0.5f);
			starsProgress.setTarget(2f);
		}
		starsProgress.update();
		
//		if(starsProgress.value() > 0 && starsProgress.value() < 1) {
			p.pushMatrix();
			p.pushStyle();
			p.translate(0, 0, -p.height * 0.75f);
			p.rotateX(P.QUARTER_PI / 2f);
			p.noStroke();
			float lastX = 0;
			float lastY = 0;
			float colorIndex = 0;
			float maxRadius = p.width * 3f;
			for (float i = 0; i < maxRadius; i+= 10) {
				if(colorIndex % 3 == 0) p.fill(255, 25, 25);
				if(colorIndex % 3 == 1) p.fill(255, 255, 255);
				if(colorIndex % 3 == 2) p.fill(25, 25, 255);
				float curRadius = i;
				float rads = i * 0.9f;
				float newX = P.cos(rads) * curRadius;
				float newY = P.sin(rads) * curRadius;
				float distFromRadiusWave = P.abs(curRadius - starsProgress.value() * maxRadius);
				float scale = P.map(distFromRadiusWave, p.width * 1.2f, 0, 0, 1);
				scale = P.constrain(scale, 0, 1);
				if(MathUtil.getDistance(newX, newY, lastX, lastY) > 300) {
					float z = P.sin(i * 0.8f) * p.height * 0.1f;
					p.pushMatrix();
					p.translate(newX, newY, z);
					p.rotate(-rads);
					p.scale(scale);
					p.shape(starSvg);
					p.popMatrix();
					lastX = newX;
					lastY = newY;
					colorIndex++;
				}
			}
			p.popStyle();
			p.popMatrix();
//		}
	}
	
	protected void drawCircleBg() {
		// vote bg
		p.pushMatrix();
		p.pushStyle();
		p.translate(0, 0, -600);
		PG.setDrawCenter(p);
		float spacing = p.width * 0.2f;
		for (int i = 40; i > 0; i--) {
			// color radial stripes
			p.noFill();
			p.strokeWeight(spacing / 8f);
			if(i % 2 == 0 && FrameLoop.progress() > 0.5f && FrameLoop.progress() < 0.98f) {
				p.stroke(255, 70, 70);
				// draw circles
				p.translate(0, 0, i);
				float circleSize = (spacing * -24f) + (spacing * 18 * FrameLoop.progress()) + spacing * FrameLoop.progress() * 20 + i * spacing;
				if(circleSize > 0) {
					p.ellipse(0, 0, circleSize, circleSize);
				}
			}
		}
		PG.setDrawCorner(p);
		p.popStyle();
		p.popMatrix();

	}

}

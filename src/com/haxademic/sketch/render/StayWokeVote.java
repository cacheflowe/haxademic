package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PShape;

public class StayWokeVote
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape wordStay;
	protected PShape wordWoke;
	protected PShape wordVote[];
	protected float wordVoteXPositions[];
	protected LinearFloat stayProgress = new LinearFloat(0, 0.025f);
	protected LinearFloat wokeProgress = new LinearFloat(0, 0.025f);
	protected LinearFloat voteProgress = new LinearFloat(0, 0.025f);

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1040 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 240 );
	}

	public void setupFirstFrame()	{
		textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
		String fontFile = FileUtil.getFile("fonts/LubalinGraph-Demi.ttf");
		wordStay = textToPShape.stringToShape3d("STAY", 20, fontFile);
		wordWoke = textToPShape.stringToShape3d("WOKE", 20, fontFile);
		String fontFile2 = FileUtil.getFile("fonts/Nexa-XBold.ttf");
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
	}

	public void drawApp() {
		p.background(ColorUtil.colorFromHex("#010C58"));
		p.background(255);
		DrawUtil.setCenterScreen(p);
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
		rotateX(P.cos(p.loop.progressRads() * 2f) * 0.1f);
		rotateY(P.sin(p.loop.progressRads() * 2f) * 0.1f);
//		rotateX(P.map(p.mouseY, 0, p.height, -1f, 1f));
//		rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));

		// draw word
		p.fill(255);
		
		
		// overall animation props
		// stay woke anim props
		p.pushMatrix();
		float distFromCenter = (p.height * 0.23f);// + showProgressEased * (p.height * 0.7f);
		
		// STAY
		stayProgress.setTarget((p.loop.progress() <= 0.45f || p.loop.progress() >= 0.95f) ? 1 : 0);
		stayProgress.update();
		float stayProgressEased = Penner.easeInOutExpo(stayProgress.value(), 0, 1, 1);

		p.fill(127, 0, 0);

		p.pushMatrix();
//		p.translate(0, -distFromCenter, P.map(showProgressEased, 0, 1, 0, p.width/1f));
		p.scale(P.map(stayProgressEased, 1, 0, 1.5f, 1f));
		p.rotateY(P.map(stayProgressEased, 1, 0, 0.2f, 0));
//		p.rotateX(P.map(stayProgressEased, 1, 0, 0, P.QUARTER_PI));
		p.translate(P.map(stayProgressEased, 1, 0, p.width * -0.09f, p.width * -5f), -distFromCenter, 0);
		p.shape(wordStay);
		p.popMatrix();

		// WOKE
		wokeProgress.setTarget((p.loop.progress() <= 0.5f) ? 1 : 0);
		wokeProgress.update();
		float wokeProgressEased = Penner.easeInOutExpo(wokeProgress.value(), 0, 1, 1);


		p.pushMatrix();
//		p.translate(0, distFromCenter, P.map(wokeProgressEased, 1, 0, 0, p.width/1f));
		p.scale(P.map(wokeProgressEased, 1, 0, 1.5f, 1f));
		p.rotateY(P.map(wokeProgressEased, 1, 0, -0.2f, 0));
//		p.rotateX(P.map(wokeProgressEased, 1, 0, 0, -P.QUARTER_PI));
		p.translate(P.map(wokeProgressEased, 1, 0, 0, p.width * 5f), distFromCenter, 0);
		p.shape(wordWoke);
		p.popMatrix();
		
		p.popMatrix();
		
		// VOTE
		float showTarget = (p.loop.progress() <= 0.525f || p.loop.progress() >= 0.95f) ? 0 : 1; 
		voteProgress.setTarget(showTarget);
		voteProgress.update();
		float voteProgressEased = Penner.easeInOutExpo(voteProgress.value(), 0, 1, 1);
		
		p.fill(0, 0, 127);

		p.pushMatrix();
		p.translate(0, 0, P.map(voteProgressEased, 0, 1, -p.width, 0));
		p.scale(P.map(voteProgressEased, 0, 1, 0, 1.25f));
		
		wordVoteXPositions[0] = -0.45f;
		wordVoteXPositions[1] = -0.13f;
		wordVoteXPositions[2] = 0.16f;
		wordVoteXPositions[3] = 0.45f;
		
		for (int i = 0; i < wordVote.length; i++) {
			p.pushMatrix();
			p.translate(pg.width * wordVoteXPositions[i], 0);
			p.rotateX(0.1f * P.sin(i * 1f + 4f * p.loop.progressRads()));
			p.rotateY(0.1f * P.sin(i * 10f + 4f * p.loop.progressRads()));
			p.translate(0, pg.height * 0.05f * P.sin(i + 4f * p.loop.progressRads()));
			p.shape(wordVote[i]);
			p.popMatrix();
		}
		p.popMatrix();
		
	}

}

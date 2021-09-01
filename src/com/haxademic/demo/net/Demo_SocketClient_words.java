package com.haxademic.demo.net;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_SocketClient_words
extends Demo_SocketClient {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
//	protected EasingFloat rotX = new EasingFloat(0, 0.1f);
	protected StringBufferLog logOut = new StringBufferLog(10);
	protected String curWord = null;
	protected SimplexNoiseTexture simplexNoise;
	
	protected HashMap<String, int[]> charCodes;
	protected String letterQueue = "";
	protected int lastLetterDrawTime = -10000;
	protected int numWordsFound = 0;
	protected float patternRadius = 100;
	protected float patternRadsCur = 0;
	protected float patternRadsInc = P.TWO_PI / 12f;
	protected float feedbackAmp = 0.003f;
	protected EasingFloat feedbackAmpLerp = new EasingFloat(feedbackAmp, 0.1f);
	
	protected PImage colors;
	
	public void firstFrame() {
		super.firstFrame();
		
		// load image
		colors = P.getImage("images/_sketch/reddymade-color-cloud.png");
		DebugView.setTexture("colors", colors);
		colors.loadPixels();
		
		// why?
		simplexNoise = new SimplexNoiseTexture(128, 128);
		
		// build character codes
		charCodes = new HashMap<String, int[]>();
		charCodes.put("a", new int[] {1,0,1,1,1,1,1});
		charCodes.put("b", new int[] {1,1,0,1,1,0,0});
		charCodes.put("c", new int[] {1,0,0,1,1,1,0});
		charCodes.put("d", new int[] {1,1,1,1,1,0,1});
		charCodes.put("e", new int[] {1,1,0,1,0,0,0});
		charCodes.put("f", new int[] {1,1,1,1,1,1,0});
		charCodes.put("g", new int[] {0,0,0,1,0,1,1});
		charCodes.put("h", new int[] {0,1,0,0,0,0,1});
		charCodes.put("i", new int[] {1,0,0,1,0,0,0});
		charCodes.put("j", new int[] {1,0,1,1,0,0,1});
		charCodes.put("k", new int[] {1,0,0,0,1,0,0});
		charCodes.put("l", new int[] {1,0,1,1,1,0,1});
		charCodes.put("m", new int[] {1,0,1,1,0,1,1});
		charCodes.put("n", new int[] {1,0,1,0,0,0,0});
		charCodes.put("o", new int[] {0,1,0,0,0,1,1});
		charCodes.put("p", new int[] {0,1,1,0,1,0,1});
		charCodes.put("q", new int[] {1,0,1,1,0,0,0});
		charCodes.put("r", new int[] {0,0,1,0,0,0,1});
		charCodes.put("s", new int[] {1,0,0,1,1,1,1});
		charCodes.put("t", new int[] {1,1,1,1,0,1,1});
		charCodes.put("u", new int[] {0,0,0,0,1,0,1});
		charCodes.put("v", new int[] {1,0,1,1,1,0,0});
		charCodes.put("w", new int[] {0,0,0,1,1,0,1});
		charCodes.put("x", new int[] {1,0,1,0,1,1,1});
		charCodes.put("y", new int[] {0,1,0,1,0,1,0});
		charCodes.put("z", new int[] {0,0,0,1,0,0,1});
	}
	
	protected void drawApp() {
//		simplexNoise.update(0.2f + FrameLoop.osc(0.01f, -0.1f, 0.1f), FrameLoop.count(0.01f), 0, 0);
		
		background(0);
				
		// draw debug
		pg.beginDraw();
//		pg.background(0);
//		drawServerLocation();
//		drawText();
		drawPattern();
		pg.endDraw();
		p.image(pg, 0, 0);

		// words log
		logOut.printToScreen(p.g, 20, 20);
		p.text(letterQueue, 20, pg.height - 50);
	}
	
	protected void drawPattern() {
//		PG.feedback(pg, -1);
		BrightnessStepFilter.instance(p).setBrightnessStep(-0.005f);
		BrightnessStepFilter.instance(p).applyTo(pg);
		
		// do feedback
		FeedbackRadialFilter.instance(p).setAmp(feedbackAmpLerp.value());
		FeedbackRadialFilter.instance(p).applyTo(pg);

		// remove non-letter characters if they're not in our pattern database
		while(letterQueue.length() > 0 && charCodes.containsKey(letterQueue.substring(0, 1)) == false) {
			letterQueue = letterQueue.substring(1);
		}
		
		// update pattern/anim vars
		patternRadius = 40;
		patternRadsInc = P.TWO_PI / 7f;
		feedbackAmpLerp.setTarget(feedbackAmp);
		feedbackAmpLerp.update(true);
		
		// draw patterns if we have a good letter
		if(letterQueue.length() > 0 && FrameLoop.frameModLooped(5)) {
			// get current letter for pattern
			String curLetter = letterQueue.substring(0, 1);
			
			// draw pattern
			int[] pattern = charCodes.get(curLetter);
			float spacing = 70;
			float numShapes = pattern.length;
			float totalW = numShapes * spacing;
			
			pg.push();
			PG.setCenterScreen(pg);
			PG.setDrawCenter(pg);
			for (int i = 0; i < pattern.length; i++) {
				pg.push();
	//			patternRadius = 100;
				patternRadsCur += patternRadsInc;
				pg.translate(P.cos(patternRadsCur) * patternRadius, P.sin(patternRadsCur) * patternRadius);
				if(pattern[i] > 0) {
					pg.fill(ImageUtil.getPixelColor(colors, MathUtil.randRange(1, colors.width - 1), MathUtil.randRange(1, colors.height - 1)));
					pg.circle(0, 0, spacing * 0.8f);
				}
				pg.pop();
			}
//			pg.translate(-totalW / 2f - spacing / 2f, 0);
//			for (int i = 0; i < pattern.length; i++) {
//				pg.translate(spacing, 0);
//				if(pattern[i] > 0) {
//					pg.fill(ImageUtil.getPixelColor(colors, MathUtil.randRange(1, colors.width - 1), MathUtil.randRange(1, colors.height - 1)));
//					pg.circle(0, 0, spacing * 0.8f);
//				}
//			}
			pg.pop();
			
			// after timeout, move to next letter and shift the last off the front
//			int patternInvterval = 100;
//			if(p.millis() > lastLetterDrawTime + patternInvterval) {
				letterQueue = letterQueue.substring(1);
				lastLetterDrawTime = p.millis();
				 P.out("draw pattern:", letterQueue);
//			}
		}
	}
	
	protected void drawText() {
		// do feedback
		FeedbackRadialFilter.instance(p).setAmp(0.1f);
		FeedbackRadialFilter.instance(p).applyTo(pg);
//		FeedbackMapFilter.instance(p).setMap(simplexNoise.texture());
//		FeedbackMapFilter.instance(p).setAmp(0.001f);
//		FeedbackMapFilter.instance(p).setBrightnessStep(-0.001f);
////		FeedbackMapFilter.instance(p).setAlphaStep(UI.value(feedbackAlphaStep));
////		FeedbackMapFilter.instance(p).setRadiansStart(UI.value(feedbackRadiansStart));
////		FeedbackMapFilter.instance(p).setRadiansRange(UI.value(feedbackRadiansRange));
//		FeedbackMapFilter.instance(p).applyTo(pg);
		
		BrightnessStepFilter.instance(p).setBrightnessStep(-0.001f);
		BrightnessStepFilter.instance(p).applyTo(pg);
		
		// only draw words one frame at a time
		if(curWord == null) return;
		
		// draw centered word
		int[] colorArray = ColorsHax.COLOR_GROUPS[5];
		int randomColor = colorArray[MathUtil.randIndex(colorArray.length)];
		pg.pushMatrix();
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 70);
		FontCacher.setFontOnContext(pg, font, randomColor, 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		pg.noStroke();
		pg.text(curWord, MathUtil.randRangeDecimal(-pg.width/3, pg.width/3), MathUtil.randRangeDecimal(-pg.height/3, pg.height/3), pg.width, pg.height);
		pg.popMatrix();
		
		// clear word queue
		curWord = null;
	}
	
	protected void newWord(String word) {
		logOut.update(word);
		curWord = word;
		if(curWord.equals("sublime") || curWord.equals("bright") || curWord.equals("water")) {
			letterQueue += word;
			if(curWord.equals("sublime")) feedbackAmp = 0.001f;
			if(curWord.equals("bright")) feedbackAmp = 0.003f;
			if(curWord.equals("water")) feedbackAmp = 0.005f;
		}
		// the sunset over the was nothing short of sublime. the bright sky turned dark
		// the water 
//		numWordsFound++;
//		if(numWordsFound % 6 == 0) {
//			letterQueue += word;		// draw every 6 words
//			feedbackAmp = 0.001f * MathUtil.randRange(1, 5);
//		}
	}
	
	////////////////////////////////////////////
	// ISocketClientDelegate methods
	////////////////////////////////////////////

	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		if(message.indexOf("word") != -1) {
			JSONObject eventData = JSONObject.parse(message);
		    String newWord = eventData.getString("word");
		    newWord(newWord);
		}
	}
	
}

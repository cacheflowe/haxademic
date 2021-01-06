package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.text.StrokeText;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;

public class Reset 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 60 * 16;  // neon animation
	protected boolean wobbles = true;

	protected String basePath = "images/_sketch/reset/";
	protected PShape iabLogo;
	
	// recycle logo
	protected int colorRecycle = 0x44ED2024;
	protected PShape recycleLogo;

	// background layer
	protected PGraphics bgTexture;
	protected PShape bgMeshSheet;
	protected SimplexNoiseTexture noiseTexture;
	protected PGraphics bgBlurMap;
	protected int colorBgRedLight = 0xff960000;
	protected int colorBgRedDark = 0xff880000;
	
	// texts
	protected PGraphics[] texts;
	protected String[] words = new String[] {
		"identity",				// 4
		"creative",
		"great",				// 0, 9
		"compliance",
		"addressability",		// 6
		"privacy",				// 1
		"attribution",
		"streaming",			// 7
		"data",					// 2
		"video",
		"measurement",			// 8
		"inclusion",			// 3
		"retail",
		"consumer",				// 7
	};
//		"audio",

	protected String fontFile;
	protected PFont font;
	protected int fontSize = 490;
	protected int paddingX = 4;
	protected int colorTextRedLight = 0xffff0202;
	protected int colorTextWhite = 0xffffffff;
	
	// animation #1: scroll!
	protected EasingFloat scrollProgress = new EasingFloat(0, 0.1f);
	protected LinearFloat scrollPageProgress = new LinearFloat(0, 0.025f);
	protected float scrollCurPageStart = 0;
	protected float scrollCurPageEnd = 0;
	
	// animation #2: neon!
	protected LinearFloat[] wordProgressShow;
	protected LinearFloat[] wordProgressColor;
	protected int[][] wordIndexes = new int[][] {
			new int[] {0, 6,  9},
			new int[] {1, 7,  11},
			new int[] {2, 2,  2},
			new int[] {3, 8,  12},
			new int[] {4, 9,  5},
			new int[] {5, 10, 10},
	};
	protected int[] wordCurIndex = new int[] {0, 0, 0, 0, 0, 0};
	
	// filled version
	protected PGraphics extraGreat; 
	
	// extra space mode
	protected static boolean isRightAligned = false;
	
	// text position/layout
	float textH = 0;
	float wordSpacing = 0;
	int numWords = 0;
	float startY = 0;

	
	protected final int APP_W = 1280;
	protected final int APP_H = 830;
	
	protected void config() {
		Config.setAppSize(APP_W, APP_H);
		if(isRightAligned) {
			Config.setPgSize(APP_W * 3, APP_H * 2);
		} else {
//			Config.setPgSize(P.round(APP_W * 1.75f), APP_H * 2);
			Config.setPgSize(APP_W * 2, APP_H * 2);
		}
		Config.setProperty(AppSettings.RESIZABLE, true);
		Config.setProperty(AppSettings.SHOW_FPS_IN_TITLE, true);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		// set renderer
		Renderer.instance().videoRenderer.setPG(pg);
		
		// load assets
		iabLogo = p.loadShape(FileUtil.getPath(basePath + "iab-lockup.svg"));
		buildRecycleLogo();
		buildBackgroundTexture();
		buildWords();
	}
	
	/////////////////////////////////
	// draw loop
	/////////////////////////////////

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') buildWords();
	}
	
	protected void drawApp() {
//		isRightAligned=Mouse.xNorm > 0.5f;
		p.background(0);
		
		// calculate layout
		textH = pg.height * 0.14f;
		wordSpacing = textH * 0.93f;
		numWords = words.length;
		startY = (isRightAligned) ? -pg.height * 0.21f : -pg.height * 0.355f;
		
		// pre-draw
		drawBgBlurMap();
//		drawBgTexture();
		updateBackgroundNoise();
		
		// set up pg context
		pg.beginDraw();
		pg.push();
		pg.background(127, 0, 5);
		pg.perspective();
		PG.setCenterScreen(pg);
		
		// camera/rotation
		//	CameraUtil.setCameraDistance(pg, 0.1f, 20000);
		//	PG.basicCameraFromMouse(pg, 0.3f);
		wobbles = false;
		if(wobbles) {
			pg.rotateY(-0.2f + 0.1f * P.sin(P.HALF_PI + FrameLoop.progressRads()));
			float wobbleX = 0.07f;
			pg.rotateX(wobbleX + wobbleX * P.sin(-P.HALF_PI + FrameLoop.progressRads()));
		}
		
		// draw objects
		drawBackground();
		drawRecycleLogo();
//		pg.pop();
		pg.push();
		pg.rotate(-0.45f);
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCorner(pg);
		drawTexts();
		if(!isRightAligned) drawIabLogo();
		pg.pop();
		
		// close context
		pg.pop();
		pg.endDraw();
		
		// draw composition to screen
		ImageUtil.drawImageCropFill(pg, p.g, false);
	}
	
	/////////////////////////////////
	// Text Builder
	/////////////////////////////////
	
	protected void buildWords() {
		// load font
		fontFile = basePath + "AvantGarde-Bold.ttf";
		fontSize = P.round(pg.height * 0.25f);
		font = FontCacher.getFont(fontFile, fontSize);
		
		// build PGraphics objects with styled text
		texts = new PGraphics[words.length];
		for (int i = 0; i < words.length; i++) {
			texts[i] = buildText(words[i], i);
			if(i == 2) extraGreat = buildText(words[i], i, true);
		}
	}
	
	protected PGraphics buildText(String word, int index) {
		return buildText(word, index, false);
	}
	
	protected PGraphics buildText(String word, int index, boolean filled) {
		// sentence concat
		String sentence = "the " + word + " reset";
		
		// calc font width on main PGraphics before creating the cached texture
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		int charW = P.ceil(p.textWidth(sentence));
		
		// create buffer & draw text
		PGraphics textBuffer = PG.newPG(charW + paddingX * 2, P.round(fontSize * 1.1f));
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.background(0);
		FontCacher.setFontOnContext(textBuffer, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		if(filled) {			
			textBuffer.text(sentence, textBuffer.width * -0.5f, textBuffer.height * -0.66f, textBuffer.width * 2f, textBuffer.height * 1.85f);
		} else {
			StrokeText.draw(textBuffer, sentence, textBuffer.width * -0.5f, textBuffer.height * -0.66f, textBuffer.width * 2f, textBuffer.height * 1.85f, p.color(255), p.color(0), pg.height * 0.006f, 36);
		}
		textBuffer.endDraw();
		
		// knock out background
		LeaveWhiteFilter.instance(p).setCrossfade(1f);
		LeaveWhiteFilter.instance(p).applyTo(textBuffer);
		
		// debug textures
		DebugView.setTexture("textBuffer"+index, textBuffer);
		return textBuffer;
	}
	
	protected void drawTexts() {
		drawScrollVersion();
//		drawNeonVersion();
//		drawSlideVersion();
	}
	
	protected void drawScrollVersion() {
		// override filled "great" text version
		texts[2] = extraGreat;
		
		// page scroll
		float curPageScrollDist = scrollCurPageEnd - scrollCurPageStart;
		float curPageProgress = scrollCurPageStart + Penner.easeInOutQuint(scrollPageProgress.value()) * curPageScrollDist;
		float easedScrollPosition = curPageProgress;//Penner.easeInOutQuint(scrollPageProgress.value());
		
		// scroll measurements
		float centerY = startY + (textH * 2f); // 3rd word from top
		float scrollDistance = wordSpacing * numWords;
		float scrollStacks = 2;
		float startScrollOffset = scrollDistance * scrollStacks * easedScrollPosition; // scrollProgress.value()
		startScrollOffset = scrollDistance * easedScrollPosition; // scrollProgress.value()
		float startScroll = startY - startScrollOffset;
		
		// scroll steps
		float scrollSingleStep = 1f / numWords;
		
		// boundaries for add/remove animations 
		float topEdge = startY;
		float bottomEdge = startY + wordSpacing * 5f;
		
		// scroll to new location on specific frames
		// scroll speed
		scrollPageProgress.setInc(0.0075f);
		scrollPageProgress.setInc(0.012f);
		
		float framesDivided = 1f / 12f; // numWords;
		boolean newScroll = false;
		float newScrollEnd = 0;
		// scroll
		if(FrameLoop.loopCurFrame() == 1) 													 { newScroll = true; newScrollEnd = scrollSingleStep * 3; scrollCurPageEnd = 0; }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 1f)) { newScroll = true; newScrollEnd = scrollSingleStep * 6; }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 2f)) { newScroll = true; newScrollEnd = scrollSingleStep * 9; }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 3f)) { newScroll = true; newScrollEnd = scrollSingleStep * 12; }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 4f)) { newScroll = true; newScrollEnd = scrollSingleStep * (numWords * 1f); }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 6f)) { newScroll = true; newScrollEnd = scrollSingleStep * (numWords * 1f + 2f); }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 7f)) { newScroll = true; newScrollEnd = scrollSingleStep * (numWords * 1f + 5f); }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 8f)) { newScroll = true; newScrollEnd = scrollSingleStep * (numWords * 1f + 8f); }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 9f)) { newScroll = true; newScrollEnd = scrollSingleStep * (numWords * 1f + 11f); }
		if(FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * framesDivided * 10f)) { newScroll = true; newScrollEnd = scrollSingleStep * (numWords * 2f); }
		
		if(newScroll) {
			// reset page turn w/start & end scroll positions
			scrollPageProgress.setCurrent(0).setTarget(1);
			scrollCurPageStart = scrollCurPageEnd;
			scrollCurPageEnd = newScrollEnd;
		}
		
		// scroll progress
		scrollPageProgress.update();
		DebugView.setValue("scrollPageProgress", scrollPageProgress.value());
		DebugView.setValue("curPageScrollDist", curPageScrollDist);
		DebugView.setValue("scrollCurPageStart", scrollCurPageStart);
		DebugView.setValue("scrollCurPageEnd", scrollCurPageEnd);
		
		
		// draw words - draw far beyond start and end boundaries
		for (int i = 0; i < numWords * 8; i++) {
			// individual word position
			int loopIndx = i % numWords;
			float wordY = startScroll + i * wordSpacing;
			float distToCenter = P.abs(wordY - centerY);
			float offsetY = 0;
			float textScale = MathUtil.scaleToTarget(texts[loopIndx].height, textH);
			float textW = texts[loopIndx].width * textScale;
			float textX = (isRightAligned) ? 
					pg.width * 0.4f - textW : 
					pg.width * 0.37f - textW;
			
			// set word color and edge animation offsets
			if(wordY < topEdge) {
				float distToEdge = P.abs(wordY - topEdge);
				float lerpColFromDist = (distToEdge / wordSpacing);
				offsetY = lerpColFromDist * -wordSpacing;
				lerpColFromDist = Penner.easeInOutCubic(lerpColFromDist);
				lerpColFromDist *= 1.15f;	// speed up fade-out
				int lerpCol = p.lerpColor(colorTextRedLight, 0x00ff0000, lerpColFromDist);
				pg.tint(lerpCol);
			} else if(wordY > bottomEdge) {
				float distToEdge = P.abs(wordY - bottomEdge);
				float lerpColFromDist = (distToEdge / wordSpacing);
				offsetY = lerpColFromDist * wordSpacing;
				lerpColFromDist = Penner.easeInCubic(lerpColFromDist);
//				lerpColFromDist *= 0.9f;
				int lerpCol = p.lerpColor(colorTextRedLight, 0x00ff0000, lerpColFromDist);
				pg.tint(lerpCol);
			} else if(distToCenter > wordSpacing / 2f) {
				pg.tint(colorTextRedLight);
			} else {
				float lerpColFromDist = 1f - (distToCenter / wordSpacing);
//				lerpColFromDist = Penner.easeInOutCubic(lerpColFromDist);
				int lerpCol = p.lerpColor(colorTextRedLight, colorTextWhite, lerpColFromDist);
				pg.tint(lerpCol);
			}
			
			// draw image to screen
			pg.push();
			pg.translate(textX, wordY + offsetY);
			pg.image(texts[loopIndx], 0, 0, textW, texts[loopIndx].height * textScale);
			pg.pop();
		}
	}
	
	/////////////////////////////////
	// Recycle Logo
	/////////////////////////////////
	
	protected void buildRecycleLogo() {
		recycleLogo = p.loadShape(FileUtil.getPath(basePath + "recycle-logo.svg"));
		recycleLogo.disableStyle();
	}
	
	protected void drawRecycleLogo() {
//		colorRecycle = 0x44ED2024;
		
		// set context
		pg.fill(colorRecycle);
		PG.setDrawFlat2d(pg, true);
		pg.push();
		PG.setDrawCenter(pg);
		float recycleX = (isRightAligned) ? pg.width * 0.203f : 0;
		float scaleTarget = (isRightAligned) ? 0.9f : 0.95f;
		pg.translate(recycleX, 0, -pg.height * 0.15f);	// 0 centered
		
		pg.rotateZ(FrameLoop.progressRads());
		// extra wobble
		if(wobbles) {
			float easedProgress = Penner.easeInOutQuint(FrameLoop.progress());
	//		pg.rotate(easedProgress * P.TWO_PI * 1f);
			float wobbleX = 0.07f;
			pg.rotateX(wobbleX + wobbleX * P.sin(-P.HALF_PI + FrameLoop.progressRads()));
		}
		
		// draw logo
		float logoScale = MathUtil.scaleToTarget(recycleLogo.height, pg.height * scaleTarget);
		pg.shape(recycleLogo, 0, 0, recycleLogo.width * logoScale, recycleLogo.height * logoScale);
		pg.pop();
	}
	
	/////////////////////////////////
	// Iab Logo
	/////////////////////////////////
	
	protected void buildIabLogo() {
		recycleLogo = p.loadShape(FileUtil.getPath(basePath + "recycle-logo.svg"));
	}
	
	protected void drawIabLogo() {
		PG.setDrawFlat2d(pg, true);
		pg.push();
		PG.setDrawCenter(pg);
		pg.translate(-pg.width * 0.3f, -pg.height * 0.575f);
		pg.rotate(P.HALF_PI);
		float logoScale = MathUtil.scaleToTarget(iabLogo.height, pg.height * 0.11f);
		pg.shape(iabLogo, 0, 0, iabLogo.width * logoScale, iabLogo.height * logoScale);
		pg.pop();
	}
	
	/////////////////////////////////
	// Background sheet
	/////////////////////////////////
	
	protected void buildBackgroundTexture() {
		// build a square texture
		int bgScale = 2;
		bgTexture = PG.newPG(pg.width * bgScale, pg.width * bgScale);
		drawBgTexture();
		DebugView.setTexture("bgTexture", bgTexture);
		
		// init noise displacement map
		noiseTexture = new SimplexNoiseTexture(512, 512);
		noiseTexture.update(0.5f, 0, 0, 0);
		DebugView.setTexture("noiseTexture", noiseTexture.texture());
		
		// build sheet mesh
		bgMeshSheet = Shapes.createSheet(60, bgTexture);
		
		// build bg blur texture
		bgBlurMap = PG.newPG(128, 64);
		drawBgBlurMap();
		DebugView.setTexture("bgBlurMap", bgBlurMap);
	}
	
	protected void drawBgTexture() {
		int rows = 60;
		float shapeSize = (float) bgTexture.width / (float) rows; 
		bgTexture.beginDraw();
		PG.setDrawCorner(bgTexture);
		bgTexture.noStroke();
		bgTexture.background(colorBgRedDark);
		bgTexture.fill(colorBgRedLight);
		for (int x = 0; x < bgTexture.width; x+=shapeSize) {
			for (int y = 0; y < bgTexture.height; y+=shapeSize) {
				bgTexture.ellipse(x, y, shapeSize, shapeSize);
			}
		}
		bgTexture.endDraw();	
	}
	
	protected void drawBgBlurMap() {
		bgBlurMap.beginDraw();
		bgBlurMap.background(0);
		bgBlurMap.translate(bgBlurMap.width * 0.2f, bgBlurMap.height * 0.5f);
		Gradients.linear(bgBlurMap, bgBlurMap.width, bgBlurMap.height, 0xffffffff, 0xff000000);
		bgBlurMap.endDraw();
	}
	
	protected void updateBackgroundNoise() {
		// update perlin texture
		noiseTexture.update(
				2.2f,								// zoom 
				FrameLoop.progressRads(),		// rotation
				0, 0);							// offset
	}
	
	protected void drawBackground() {
		pg.push();
		PG.setDrawCorner(pg);
		pg.translate(-pg.width * -.1f, -pg.height * 0.2f, -pg.height * 0.4f);
		pg.rotateY(-0.3f + 0.05f * P.sin(FrameLoop.progressRads()));
		
		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(noiseTexture.texture());
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(250f);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(200f + 150f * P.sin(-P.HALF_PI + FrameLoop.progressRads()));
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(pg);
		// set texture using PShape method
		bgMeshSheet.setTexture(bgTexture);
		
		float bgScale = MathUtil.scaleToTarget(bgTexture.height, pg.height * 8f);	// texture is same size as mesh
		pg.scale(bgScale);
		pg.shape(bgMeshSheet);
		pg.resetShader();
		pg.pop();

		/*
			// postprocess on background
			GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
			GrainFilter.instance(p).setCrossfade(0.08f);
	//		GrainFilter.instance(p).applyTo(pg);
			
			BlurHMapFilter.instance(p).setMap(bgBlurMap);
			BlurHMapFilter.instance(p).setAmpMin(0f);
			BlurHMapFilter.instance(p).setAmpMax(1f);
			BlurVMapFilter.instance(p).setMap(bgBlurMap);
			BlurVMapFilter.instance(p).setAmpMin(0f);
			BlurVMapFilter.instance(p).setAmpMax(1f);
	
	//		BlurHMapFilter.instance(p).applyTo(pg);
	//		BlurVMapFilter.instance(p).applyTo(pg);
	//		BlurHMapFilter.instance(p).applyTo(pg);
	//		BlurVMapFilter.instance(p).applyTo(pg);
		*/
	}
	
	
	
	/*
		protected void drawNeonVersion() {
		// lazy init
		if(wordProgressShow == null) {
			wordProgressShow = new LinearFloat[6];
			for (int i = 0; i < wordProgressShow.length; i++) {
				wordProgressShow[i] = new LinearFloat(0.5f, 0.025f);
			}
		}
		
		// update positions/progress
		for (int i = 0; i < wordProgressShow.length; i++) {
			wordProgressShow[i].update();
		}
		
		// set animation props on specific frames
		if(
				FrameLoop.loopCurFrame() == 10 || 
				FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * 0.33f) || 
				FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * 0.66f)
			) {
			for (int i = 0; i < wordProgressShow.length; i++) {
				wordProgressShow[i].setInc(0.03f);
				wordProgressShow[i].setDelay(i * 7).setCurrent(0.5f).setTarget(0);
			}
		}
		
		// check for linearfloat progress completions
		for (int i = 0; i < wordProgressShow.length; i++) {
			// come back from full white
			if(wordProgressShow[i].value() == 1 && wordProgressShow[i].target() == 1) {
				wordProgressShow[i].setDelay(30).setTarget(0.5f);
			}
			// show after fading out
			if(wordProgressShow[i].value() == 0 && wordProgressShow[i].target() == 0) {
				wordProgressShow[i].setDelay(60).setTarget(0.49f);
				// increment word index when fading word back up
				wordCurIndex[i]++;
				wordCurIndex[i] = wordCurIndex[i] % wordIndexes[0].length;
			}
			// slight delay from red to white
			if(wordProgressShow[i].value() == 0.49f && wordProgressShow[i].target() == 0.49f) {
				wordProgressShow[i].setDelay(30).setTarget(1);
			}
		}

		
		// draw words - only 6 slots
		for (int i = 0; i < 6; i++) {
			// individual word position
			int loopIndx = i % numWords;
			float offsetY = 0;
			PGraphics curTextPg = texts[wordIndexes[loopIndx][wordCurIndex[i]]]; // wordIndexes
			float textScale = MathUtil.scaleToTarget(curTextPg.height, textH);
			float textW = curTextPg.width * textScale;
			float textX = pg.width * 0.65f - textW;
			float wordY = startY + i * wordSpacing;

			// set word color and edge animation offsets
			float colorMix = P.map(wordProgressShow[i].value(), 0.5f, 1, 0, 1);
			float alphaMix = P.map(wordProgressShow[i].value(), 0f, 0.5f, 0, 1);
			if(i == 2) colorMix = 1;
			if(i == 2) alphaMix = 1;
			int lerpCol = p.lerpColor(colorTextRedLight, colorTextWhite, colorMix);
			pg.tint(lerpCol, alphaMix * 255f);
			
			// draw word to screen
			pg.push();
			pg.translate(-pg.width * 0.3f, wordY + offsetY);
			if(i == 2) {
				float fadeMix = P.map(wordProgressShow[i].value(), 0.5f, 1, 0, 1);
				PG.setPImageAlpha(pg, 1f - fadeMix);
				pg.image(curTextPg, textX, 0, textW, curTextPg.height * textScale);
				PG.resetPImageAlpha(pg);
				
				PG.setPImageAlpha(pg, fadeMix);
				pg.image(extraGreat, textX, 0, textW, extraGreat.height * textScale);
				PG.resetPImageAlpha(pg);
			} else {
				pg.image(curTextPg, textX, 0, textW, curTextPg.height * textScale);
			}
			pg.pop();
		}
	}
	
	protected void drawSlideVersion() {
		// lazy init
		if(wordProgressShow == null) {
			wordProgressShow = new LinearFloat[6];
			wordProgressColor = new LinearFloat[6];
			for (int i = 0; i < wordProgressShow.length; i++) {
				wordProgressShow[i] = new LinearFloat(0.5f, 0.025f);
				wordProgressColor[i] = new LinearFloat(1, 0.025f);
			}
		}
		
		// update positions/progress
		for (int i = 0; i < wordProgressShow.length; i++) {
			wordProgressShow[i].update();
			wordProgressColor[i].update();
		}
		
		// set animation props on specific frames
		// clear screen every 3rd of the animation
		if(
				FrameLoop.loopCurFrame() == 10 || 
				FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * 0.33f) || 
				FrameLoop.loopCurFrame() == P.round(FrameLoop.loopFrames() * 0.66f)
				) {
			for (int i = 0; i < wordProgressShow.length; i++) {
				wordProgressShow[i].setInc(0.015f);
				wordProgressColor[i].setInc(0.06f);
				wordProgressShow[i].setDelay(i * 7).setCurrent(0.5f).setTarget(1);
				wordProgressColor[i].setCurrent(0.001f).setTarget(0.001f);
			}
		}
		
		// check for linearfloat progress completions
		for (int i = 0; i < wordProgressShow.length; i++) {
			// restart
			if(wordProgressShow[i].value() == 1 && wordProgressShow[i].target() == 1) {
				wordProgressShow[i].setDelay(30).setCurrent(0).setTarget(0.5f);
				// increment word index when sliding back in
				wordCurIndex[i]++;
				wordCurIndex[i] = wordCurIndex[i] % wordIndexes[0].length;
			}
			// slight delay from red to white when words land on screen
			if(wordProgressShow[i].value() == 0.5f && wordProgressShow[i].target() == 0.5f && wordProgressColor[i].value() == 0.001f && wordProgressColor[i].target() != 1) {
//				wordProgressShow[i].setDelay(180).setTarget(1);
				wordProgressColor[i].setDelay(20 + i * 1).setCurrent(0).setTarget(1);
			}
			// pulse back to red
			if(wordProgressColor[i].value() == 1 && wordProgressColor[i].target() == 1) {
				wordProgressColor[i].setDelay(90 + i * 1).setCurrent(1).setTarget(0);
			}
		}
		
		
		// draw words - only 6 slots
		for (int i = 0; i < 6; i++) {
			// individual word position
			int loopIndx = i % numWords;
			float offsetY = 0;
			PGraphics curTextPg = texts[wordIndexes[loopIndx][wordCurIndex[i]]]; // wordIndexes
			float textScale = MathUtil.scaleToTarget(curTextPg.height, textH);
			float textW = curTextPg.width * textScale;
			float textX = pg.width * 0.65f - textW;
			float wordY = startY + i * wordSpacing;
			
			// set word color and edge animation offsets
			float colorMix = wordProgressColor[i].value();
			if(i == 2) colorMix = 1;
			int lerpCol = p.lerpColor(colorTextRedLight, colorTextWhite, colorMix);
			pg.tint(lerpCol);
			
			// offset x for sliding
			float offsetX = (wordProgressShow[i].value() <= 0.5f) ?
					pg.width * 2f - pg.width * 2f * Penner.easeOutQuint(P.map(wordProgressShow[i].value(), 0, 0.5f, 0, 1)) :
					-pg.width * 2f * Penner.easeInQuint(P.map(wordProgressShow[i].value(), 0.5f, 1, 0, 1));	
			if(i != 2) textX += offsetX;
			
			// draw word to screen
			pg.push();
			pg.translate(-pg.width * 0.3f, wordY + offsetY);
			if(i == 2) {
				float fadeMix = wordProgressColor[i].value();
				PG.setPImageAlpha(pg, 1f - fadeMix);
				pg.image(curTextPg, textX, 0, textW, curTextPg.height * textScale);
				PG.resetPImageAlpha(pg);
				
				PG.setPImageAlpha(pg, fadeMix);
				pg.image(extraGreat, textX, 0, textW, extraGreat.height * textScale);
				PG.resetPImageAlpha(pg);
			} else {
				pg.image(curTextPg, textX, 0, textW, curTextPg.height * textScale);
			}
			pg.pop();
		}
	}
	 */
}
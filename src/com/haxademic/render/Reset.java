package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHMapFilter;
import com.haxademic.core.draw.filters.pshader.BlurVMapFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.text.StrokeText;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;

public class Reset 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 60 * 10;

	protected String basePath = "images/_sketch/reset/";
	protected PShape iabLogo;
	
	// recycle logo
	protected int colorRecycle = 0xAAED2024;
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
		"streaming",
		"data",
		"great",
		"accessibility",
		"privacy",
		"retail",
		"identity",
		"workplace",
	};
	protected String fontFile;
	protected PFont font;
	protected int fontSize = 490;
	protected int paddingX = 4;
	protected int colorTextRedLight = 0xffff0202;
	protected int colorTextWhite = 0xffffffff;
	
	protected void config() {
		Config.setAppSize(1280, 830);
		Config.setPgSize(1280, 830);
		Config.setProperty(AppSettings.SHOW_FPS_IN_TITLE, true);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
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
		p.background(0);
		
		// pre-draw
		drawBgBlurMap();
//		drawBgTexture();
		updateBackgroundNoise();
		
		// set up context
		pg.beginDraw();
		pg.push();
		pg.background(127, 0, 5);
		pg.perspective();
		PG.setCenterScreen(pg);
		
		// camera/rotation
//		CameraUtil.setCameraDistance(pg, 0.1f, 20000);
//		pg.push();
//		PG.basicCameraFromMouse(pg, 0.3f);
//		pg.rotateX(-0.35f + P.sin(FrameLoop.progressRads()) * 0.125f);
////		pg.rotateY(P.PI * easedRotY);
		pg.rotateY(-0.2f + 0.1f * P.sin(P.HALF_PI + FrameLoop.progressRads()));
//		pg.rotateX(0.2f + 0.1f * P.sin(P.HALF_PI + FrameLoop.progressRads()));
		
		// draw objects
		drawBackground();
//		pg.ortho();
		drawRecycleLogo();
//		pg.pop();
		pg.push();
		pg.rotate(-0.45f);
		drawTexts();
		drawIabLogo();
		pg.pop();
		
		// close context
		pg.pop();
		pg.endDraw();
		
		// draw composition to screeen
		ImageUtil.drawImageCropFill(pg, p.g, true);
		
//		if(FrameLoop.loopCurFrame() == FRAMES-2) for (int i = 0; i < heartFaces.length; i++) heartFaces[i].reset();
////		if(FrameLoop.loopCurFrame() % 100 == 0) for (int i = 0; i < heartFaces.length; i++) heartFaces[i].heartComplete();
//		if(FrameLoop.loopCurFrame() == 360) for (int i = 0; i < heartFaces.length; i++) heartFaces[i].heartComplete();
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
		}
	}
	
	protected PGraphics buildText(String word, int index) {
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
		StrokeText.draw(textBuffer, sentence, textBuffer.width * -0.5f, textBuffer.height * -0.66f, textBuffer.width * 2f, textBuffer.height * 1.85f, p.color(255), p.color(0), pg.height * 0.006f, 36);
		textBuffer.endDraw();
		
		// knock out background
		LeaveWhiteFilter.instance(p).setCrossfade(1f);
		LeaveWhiteFilter.instance(p).applyTo(textBuffer);
		
		// debug textures
		DebugView.setTexture("textBuffer"+index, textBuffer);
		return textBuffer;
	}
	
	protected void drawTexts() {
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCorner(pg);
		float textH = pg.height * 0.14f;
		float wordSpacing = textH * 0.93f;
		float wordStackH = wordSpacing * texts.length;
		float easedProgress = Penner.easeInOutQuint(FrameLoop.progress());
		float startY = wordStackH + wordStackH * 5f * easedProgress;
		float centerY = -textH;
		float topEdge = -wordSpacing * 3.1f;
		float bottomEdge = wordSpacing * 4;
		for (int i = 0; i < texts.length * 8; i++) {
			int loopIndx = i % texts.length;
			pg.push();
			float wordY = -startY -pg.height * 0.4f + i * wordSpacing;
			float distToCenter = P.abs(wordY - centerY);
			float offsetY = 0;
			if(wordY < topEdge) {
				float distToEdge = P.abs(wordY - topEdge);
				float lerpColFromDist = (distToEdge / wordSpacing);
				offsetY = lerpColFromDist * -wordSpacing;
				lerpColFromDist = Penner.easeInOutCubic(lerpColFromDist);
				int lerpCol = p.lerpColor(colorTextRedLight, 0x00ff0000, lerpColFromDist);
				pg.tint(lerpCol);
			} else if(wordY > bottomEdge) {
				float distToEdge = P.abs(wordY - bottomEdge);
				float lerpColFromDist = (distToEdge / wordSpacing);
				offsetY = lerpColFromDist * wordSpacing;
				lerpColFromDist = Penner.easeInOutCubic(lerpColFromDist);
				int lerpCol = p.lerpColor(colorTextRedLight, 0x00ff0000, lerpColFromDist);
				pg.tint(lerpCol);
			} else if(distToCenter > wordSpacing / 2f) {
				pg.tint(colorTextRedLight);
			} else {
				float lerpColFromDist = 1f - (distToCenter / wordSpacing);
				lerpColFromDist = Penner.easeInOutCubic(lerpColFromDist);
				int lerpCol = p.lerpColor(colorTextRedLight, colorTextWhite, lerpColFromDist);
				pg.tint(lerpCol);
			}
			float textScale = MathUtil.scaleToTarget(texts[loopIndx].height, textH);
			float textW = texts[loopIndx].width * textScale;
			pg.translate(-pg.width * 0.3f, wordY + offsetY);
			pg.image(texts[loopIndx], pg.width * 0.7f -textW, 0, textW, texts[loopIndx].height * textScale);
			pg.pop();
		}
	}
	
	/////////////////////////////////
	// Recycle Logo
	/////////////////////////////////
	
	protected void buildRecycleLogo() {
		recycleLogo = p.loadShape(FileUtil.getPath(basePath + "recycle-logo.svg"));
	}
	
	protected void drawRecycleLogo() {
		recycleLogo.disableStyle();
		pg.fill(colorRecycle);
		
		PG.setDrawFlat2d(pg, true);
		pg.push();
		PG.setDrawCenter(pg);
		pg.translate(0, 0, -pg.height * 0.15f);
		float easedProgress = Penner.easeInOutQuint(FrameLoop.progress());
		pg.rotate(easedProgress * P.TWO_PI * 3f);
		float logoScale = MathUtil.scaleToTarget(recycleLogo.height, pg.height * 0.9f);
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
//		pg.rotateY(-0.3f + 0.05f * P.sin(FrameLoop.progressRads()));
		
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

		// postprocess on background
		GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
		GrainFilter.instance(p).setCrossfade(0.08f);
		GrainFilter.instance(p).applyTo(pg);
		
		BlurHMapFilter.instance(p).setMap(bgBlurMap);
		BlurHMapFilter.instance(p).setAmpMin(0f);
		BlurHMapFilter.instance(p).setAmpMax(2f);
		BlurVMapFilter.instance(p).setMap(bgBlurMap);
		BlurVMapFilter.instance(p).setAmpMin(0f);
		BlurVMapFilter.instance(p).setAmpMax(2f);

		BlurHMapFilter.instance(p).applyTo(pg);
		BlurVMapFilter.instance(p).applyTo(pg);
		BlurHMapFilter.instance(p).applyTo(pg);
		BlurVMapFilter.instance(p).applyTo(pg);
	}
	
}
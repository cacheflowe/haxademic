package com.haxademic.demo.draw.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.text.StrokeText;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_LetterTextureCache
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	protected PGraphics textBuffer;
	protected PFont fontBig;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		int FRAMES = 60 * 4;
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}

	protected void firstFrame() {
		buildText();
	}
	
	public void buildText() {
		// font config
		int fontSize = 490;
		int paddingX = 4;
		String letter = "WOBBLE";
		
		// calc font width on main PGraphics before creaing the cached texture
//		PFont font = FontCacher DemoAssets.fontOpenSans(fontSize);
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, fontSize);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		int charW = P.ceil(p.textWidth(letter));
		
		// create buffer & draw text
		textBuffer = PG.newPG(charW + paddingX * 2, P.round(fontSize * 0.8f));
		textBuffer.beginDraw();
		textBuffer.clear();
		textBuffer.background(0, 0);
		FontCacher.setFontOnContext(textBuffer, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
//		textBuffer.text(letter, textBuffer.width * -0.5f, textBuffer.height * -0.65f, textBuffer.width * 2f, textBuffer.height * 1.85f);
		StrokeText.draw(textBuffer, letter, textBuffer.width * -0.5f, textBuffer.height * -0.65f, textBuffer.width * 2f, textBuffer.height * 1.85f, p.color(0, 255, 0), p.color(0), 8, 36);
		textBuffer.endDraw();
		
		// debug textures
		DebugView.setTexture("textBuffer", textBuffer);
//		DebugView.setTexture("texture", texture);
	}

	protected void drawApp() {
		// rebuild on interval for testing
		if(FrameLoop.frameModLooped(60)) {
			buildText();
		}
		
		pg.beginDraw();
		pg.pushMatrix();
		
		// set main app context
		pg.background(0);
		pg.ortho();
//		pg.perspective();
//		 PG.setBetterLights(pg);
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		
		float rotX = -0.2f + 0.06f * P.sin(FrameLoop.progressRads());
		pg.rotateX(rotX);
		pg.rotateY(0.4f);
//		PG.basicCameraFromMouse(p.g);
		
		// draw one
//		p.image(textBuffer, 0, 0);
		
		// draw stack
		int numDrawn = 100;
		float spacing = 25;
		pg.push();
		pg.translate(0, 0, numDrawn * -spacing);
		for (int i = 0; i < numDrawn; i++) {
			float curScale = 0.4f + 0.03f * P.sin(FrameLoop.progressRads() + i/5f);
			pg.translate(0, 0, spacing);
			pg.push();
			pg.rotateX(0.05f * P.sin(FrameLoop.progressRads() + i/8f));
			pg.rotateY(0.3f * P.sin(FrameLoop.progressRads() + i/8f));
			pg.rotateZ(0.15f * P.sin(2f * FrameLoop.progressRads() + i/5f));
			pg.image(textBuffer, 0, 50f * P.sin(FrameLoop.progressRads() + i/5f), textBuffer.width * curScale, textBuffer.height * curScale);
			pg.pop();
		}
		pg.pop();
		
		pg.popMatrix();
		pg.endDraw();
		
		// post process
//		VignetteFilter.instance(p).applyTo(pg);
//		VignetteFilter.instance(p).setDarkness(0.9f);
		VignetteFilter.instance(p).applyTo(pg);
		
		// post process
		BloomFilter.instance(p).setStrength(0.05f);
		BloomFilter.instance(p).setBlurIterations(5);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_DARKEST);
//		BloomFilter.instance(p).applyTo(pg);
//		BloomFilter.instance(p).applyTo(pg);
		
		GodRays.instance(p).setDecay(0.8f);
		GodRays.instance(p).setWeight(0.5f);
		GodRays.instance(p).setRotation(1.9f + 0.2f * P.sin(FrameLoop.progressRads() * 3f));
		GodRays.instance(p).setAmp(0.08f);
//		GodRays.instance(p).applyTo(pg);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
		GrainFilter.instance(p).setCrossfade(0.02f);
		GrainFilter.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
	}

}

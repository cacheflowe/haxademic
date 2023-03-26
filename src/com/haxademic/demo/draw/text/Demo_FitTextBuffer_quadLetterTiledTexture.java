package com.haxademic.demo.draw.text;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.text.FitTextBuffer;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_FitTextBuffer_quadLetterTiledTexture
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FitTextBuffer fitText1;
	protected FitTextBuffer fitText2;
	protected FitTextBuffer fitText3;
	protected FitTextBuffer fitText4;
	
//	protected ArrayList<PGraphics> textPg;
	protected ArrayList<TiledTexture> textTex;
	
	protected void config() {
		int FRAMES = 60 * 3;
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}

	protected void firstFrame() {
		// build tiled textures
		String letter = "H";
		textTex = new ArrayList<TiledTexture>();
		textTex.add(createTextBufferAndTile(DemoAssets.fontOpenSansPath, letter));
		textTex.add(createTextBufferAndTile(DemoAssets.fontRalewayPath, letter));
		textTex.add(createTextBufferAndTile(DemoAssets.fontHelloDenverPath, letter));
		textTex.add(createTextBufferAndTile(DemoAssets.fontInterPath, letter));
	}
	
	protected TiledTexture createTextBufferAndTile(String fontPath, String str) {
		float fontSize = p.height * 0.9f;
		
		// create text crop - will generate at different heights, based on font difference
		PFont font = p.createFont( fontPath, fontSize );
		fitText1 = new FitTextBuffer(font, p.color(255));
		fitText1.updateText(str);
		
		// normalize to a specific output height into a new PGraphics
		PGraphics centeredTextPG = drawTextAtHeight(fitText1.crop(), 0.7f);
		DebugView.setTexture("pg_"+textTex.size(), centeredTextPG);
		TiledTexture tiledTexture = new TiledTexture(centeredTextPG);
		return tiledTexture;
	}
	
	protected PGraphics drawTextAtHeight(PImage img, float hNorm) {
		PGraphics buffer = PG.newPG(p.width, p.height);
		buffer.beginDraw();
		buffer.background(0, 0);
		PG.setDrawCenter(buffer);
		PG.setCenterScreen(buffer);
		float newScale = MathUtil.scaleToTarget(img.height, buffer.height * hNorm);
		buffer.image(img, 0, 0, img.width * newScale, img.height * newScale);
		buffer.endDraw();
		return buffer;
	}
	
	protected void drawApp() {
		p.background(0);
		
		// overlap with exclusion
		p.blendMode(PBlendModes.EXCLUSION);
		for (int i = 0; i < textTex.size(); i++) {
			TiledTexture tex = textTex.get(i);
			float offsetX = 0;
			float offsetY = 0;
			if(i == 0) offsetX = Penner.easeInOutQuart(FrameLoop.progress());
			if(i == 1) { offsetX = Penner.easeInOutQuart(FrameLoop.progress()) * 1f; offsetY = Penner.easeInOutQuart(FrameLoop.progress()) * -1f; }
			if(i == 2) offsetY = Penner.easeInOutQuart(FrameLoop.progress());
			if(i == 3) { offsetX = Penner.easeInOutQuart(FrameLoop.progress()) * -1f; offsetY = Penner.easeInOutQuart(FrameLoop.progress()) * -1f; }
			tex.setOffset(offsetX, offsetY);
			float maxOffset = P.max(P.abs(offsetX), P.abs(offsetY));
			tex.setZoom(1 + 1.1f * P.sin(maxOffset * P.PI), 1 + 1.1f * P.sin(maxOffset * P.PI));
			tex.draw(p.g, p.width, p.height, false);
		}
		
		// post FX
		VignetteFilter.instance().setDarkness(0.5f);
		VignetteFilter.instance().applyTo(p.g);

		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.11f);
		GrainFilter.instance().applyTo(p.g);

	}

}

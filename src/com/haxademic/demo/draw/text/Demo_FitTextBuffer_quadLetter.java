package com.haxademic.demo.draw.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FitTextBuffer;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PImage;

public class Demo_FitTextBuffer_quadLetter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FitTextBuffer fitText;
	protected FitTextBuffer fitText2;
	protected FitTextBuffer fitText3;
	protected FitTextBuffer fitText4;
	
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
		float fontSize = p.height * 0.9f;
		String text = "A"; 
		
		PFont font = p.createFont( DemoAssets.fontOpenSansPath, fontSize );
		fitText = new FitTextBuffer(font, p.color(255));
		fitText.updateText(text);

		PFont font2 = p.createFont( DemoAssets.fontRalewayPath, fontSize );
		fitText2 = new FitTextBuffer(font2, p.color(255));
		fitText2.updateText(text);

		PFont font3 = p.createFont( DemoAssets.fontHelloDenverPath, fontSize );
		fitText3 = new FitTextBuffer(font3, p.color(255));
		fitText3.updateText(text);	
		
		PFont font4 = p.createFont( DemoAssets.fontInterPath, fontSize );
		fitText4 = new FitTextBuffer(font4, p.color(255));
		fitText4.updateText(text);	
	}
	
	protected void drawApp() {
		p.background(0);
		
		// overlap with exclusion
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.blendMode(PBlendModes.EXCLUSION);
		float drawH = p.height * 0.7f;
		drawTextAtHeight(fitText.crop(), FrameLoop.progressOsc(-7f, 7f), 0, drawH);
		drawTextAtHeight(fitText2.crop(), FrameLoop.progressOsc(2f, -2f, 0.25f), FrameLoop.progressOsc(-2f, 2f, 0.25f), drawH);
		drawTextAtHeight(fitText3.crop(), FrameLoop.progressOsc(-3f, 3f, 0.5f), FrameLoop.progressOsc(-4f, 4f, 0.5f), drawH);
		drawTextAtHeight(fitText4.crop(), 0, FrameLoop.progressOsc(4f, -4f, 0.75f), drawH);
		
		// or draw quads
//		p.background(0);
//		DebugView.setTexture("fitText.crop()", fitText.crop());
//		p.copy(fitText.crop(), 0, 0, fitText.crop().width/2, fitText.crop().height/2, 0, 0, p.width/2, p.height/2);
//		p.copy(fitText2.crop(), fitText2.crop().width/2, 0, fitText2.crop().width/2, fitText2.crop().height/2, p.width/2, 0, p.width/2, p.height/2);
	}
	
	protected void drawTextAtHeight(PImage img, float x, float y, float h) {
		float newScale = MathUtil.scaleToTarget(img.height, h);
		p.image(img, x, y, img.width * newScale, img.height * newScale);
	}

}

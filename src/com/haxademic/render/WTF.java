package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;

public class WTF
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	protected int FRAMES = 60 * 6;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setPgSize(1024*2, 1024*2);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}

	protected void drawApp() {
		p.background(0);
		PG.setDrawFlat2d(p.g, true);
		PG.setDrawCenter(p.g);
		p.ortho();
		p.blendMode(PBlendModes.LIGHTEST);
		
		// draw bg
		p.push();
		PG.setCenterScreen(p.g);
		int color1bg = p.color(
				127 + 127f * P.sin(FrameLoop.progressRads() + 0.5f + 0 * P.TWO_PI),
				50 + 50f * P.sin(FrameLoop.progressRads() * 1f + P.PI + 0 * P.TWO_PI * 3f),
				200 + 55f * P.sin(FrameLoop.progressRads() * 2f + P.QUARTER_PI + 0 * P.TWO_PI * 2f)
				);
		int color2bg = p.color(
				127 + 127f * P.sin(FrameLoop.progressRads() + 0.5f * P.TWO_PI + P.HALF_PI),
				50 + 50f * P.sin(FrameLoop.progressRads() * 1f + P.PI * P.TWO_PI * 3f + P.HALF_PI),
				200 + 55f * P.sin(FrameLoop.progressRads() * 2f + P.QUARTER_PI * P.TWO_PI * 2f + P.HALF_PI)
				);

		Gradients.radial(p.g, p.width * 1.6f, p.width * 1.6f, color2bg, color1bg, 120);
		p.pop();
		
		// draw text
		String fontFile = DemoAssets.fontHelloDenverPath;
		float fontSize = 350;
		float heightHalf = fontSize/2; 
		PFont font = FontCacher.getFont(fontFile, fontSize);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		
		// draw texts
		float numDraw = p.height / 2 + heightHalf;
		for (float i=0; i < numDraw; i++) {
			float progressGradient = i/numDraw;
			p.push();
			p.fill(
				127 + 127f * P.sin(FrameLoop.progressRads() + 0.5f + progressGradient * P.TWO_PI * 2f),
				50 + 50f * P.sin(FrameLoop.progressRads() * 1f + P.PI + progressGradient * P.TWO_PI * 3f),
				200 + 55f * P.sin(FrameLoop.progressRads() * 2f + P.QUARTER_PI + progressGradient * P.TWO_PI * 1f), 
				255
			);
			if(i == numDraw - 2) p.fill(255);
			p.translate(p.width / 2 + 40f * P.sin(FrameLoop.progressRads() + progressGradient * 3f * P.PI), p.height + fontSize * 0.9f - i*1f);
			p.rotate(0.2f * P.sin(FrameLoop.progressRads() + progressGradient * P.TWO_PI));
//			p.rotateY(0.1f * P.sin(FrameLoop.count(0.01f) + progress * 0.2f * P.PI));
//			p.rotateX(0.6f + 0.3f * P.sin(FrameLoop.count(0.01f) + progress * 0.2f * P.PI));
			if(i % 2 == 1) p.text("WTF?", 0, -heightHalf * 1.2f);
			p.pop();
		}
		
	}
}

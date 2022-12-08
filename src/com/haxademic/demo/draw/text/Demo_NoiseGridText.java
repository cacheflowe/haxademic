package com.haxademic.demo.draw.text;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;

public class Demo_NoiseGridText
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String word = "TOUCHDOWN";
	protected SimplexNoise3dTexture noiseTexture;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame()	{
		int noiseScaleDivider = 10;
		noiseTexture = new SimplexNoise3dTexture(p.width/noiseScaleDivider, p.height/noiseScaleDivider);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
	}
	
	protected void drawApp() {
		background(0);
		
		// update perlin texture
		noiseTexture.update(1, 0, 0, 0, p.frameCount * 0.01f, false, false);
		noiseTexture.texture().loadPixels();
		// draw to screen
		PG.setPImageAlpha(p.g, 0.2f);
		p.image(noiseTexture.texture(), 0, 0, p.width, p.height);  
		PG.resetPImageAlpha(p.g);


		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 54);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		
		// draw grid
		PG.setDrawCenter(p.g);
		int cols = word.length();
		int rows = 8;
		float spacing = 80;
		float centerX = p.width / 2;
		float centerY = p.height / 2;
		float offsetX = (cols * spacing) / -2f + spacing / 2;
		float offsetY = (rows * spacing) / -2f + spacing / 2;
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				// get pixel position
				float progressX = x / (float) cols;
				float progressY = y / (float) rows;
				int pixelColor = ImageUtil.getPixelColorNorm(noiseTexture.texture(), progressX, progressY);
				float letterScale = P.map(p.brightness(pixelColor), 0, 255, 0.0f, 1.3f);
				// place letter
				float curX = centerX + offsetX + x * spacing;
				float curY = centerY + offsetY + y * spacing;
				p.push();
				p.translate(curX, curY);
				p.scale(letterScale);
				p.text(word.charAt(x), 0, 0);
				p.pop();
			}
		}
	}

}

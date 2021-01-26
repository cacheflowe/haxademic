package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_ArtNetDataSender_MatrixFromBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected BaseTexture texture;
	protected PGraphics ledTexture;
	protected int numPixels = 256;

	protected void firstFrame() {
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 0, numPixels);
		texture = new TextureEQConcentricCircles(256, 256);
		ledTexture = PG.newPG2DFast(16, 16);
		AudioIn.instance(AudioInputLibrary.ESS);
	}

	protected void drawApp() {
		createColors();
		artNetDataSender.send();
		ImageUtil.cropFillCopyImage(texture.texture(), p.g, false);
	}
	
	protected void createColors() {
		// update audio texture and copy to tiny texture
		texture.update();
		{ // override texture & draw text on top
			texture.texture().beginDraw();
			String fontFile2 = DemoAssets.fontDSEG7Path;
			PFont font = FontCacher.getFont(fontFile2, 200);
			FontCacher.setFontOnContext(texture.texture(), font, p.color(255, 0, 0), 1.8f, PTextAlign.CENTER, PTextAlign.CENTER);
			texture.texture().text(((P.second()%10)+""), 0, 0, texture.texture().width, texture.texture().height);
			texture.texture().endDraw();
		}
		ImageUtil.copyImage(texture.texture(), ledTexture);
		ledTexture.loadPixels();
		
		// build entire LED data, to loop through afterwards
		for(int i=0; i < numPixels; i++) {
			// get texture pixel color components
			int x = MathUtil.gridColFromIndex(i, 16);
			int y = MathUtil.gridRowFromIndex(i, 16);
			int pixelColor = ImageUtil.getPixelColor(ledTexture, x, y);
			float r = ColorUtil.redFromColorInt(pixelColor);
			float g = ColorUtil.greenFromColorInt(pixelColor);
			float b = ColorUtil.blueFromColorInt(pixelColor);
			
			// pixel index, stepping through single-row sequential layout, 1 by 1
			// zigzag remap
			int pixelIndex = i * 3;
			int matrixSize = 16;
			int rowStartI = P.floor(i / matrixSize) * matrixSize;
			int twoRowIndex = i % (matrixSize * 2);
			int zigZagRevIndex = 16 - 1 - (i % 16);
			if(twoRowIndex < matrixSize) {
				pixelIndex = (rowStartI + zigZagRevIndex) * 3;
			}
			
			// set data
			artNetDataSender.setColorAtIndex(pixelIndex, r, g, b);
		}
	}
}
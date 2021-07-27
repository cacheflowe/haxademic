package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_ArtNetDataSender_sendMatrixFromBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected PGraphics ledTexture;
	protected int matrixSize = 16;
	protected int numPixels = matrixSize * matrixSize;

	protected BaseTexture texture;
	protected SimplexNoise3dTexture noise3d;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// prepare ArtNetSender & matrix texture
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 4, numPixels);
		ledTexture = PG.newPG2DFast(matrixSize, matrixSize);
		DebugView.setTexture("ledTexture", ledTexture);

		// build textures
		texture = new TextureEQConcentricCircles(256, 256);
		AudioIn.instance(AudioInputLibrary.Minim);
		// alt texture
		noise3d = new SimplexNoise3dTexture(256, 256);
	}

	protected void drawApp() {
		p.background(0);
		
		// update texture
		PGraphics curTexture;
		if(Mouse.xNorm < 0.5f) {
			texture.update();
			curTexture = texture.texture();
		} else {
			updateNoiseTexture();
			curTexture = noise3d.texture();
		}
		if(Mouse.yNorm > 0.5f) overlayNumberOnTexture(curTexture);
		
		// copy to tiny texture, reduce brightness & send!
		ImageUtil.copyImage(curTexture, ledTexture);
		BrightnessFilter.instance(p).setBrightness(0.1f);
		BrightnessFilter.instance(p).applyTo(ledTexture);
		
		// send it!
		artNetDataSender.sendMatrixFromBuffer(ledTexture, matrixSize);
		
		// show original texture on screen
		ImageUtil.cropFillCopyImage(curTexture, p.g, false);
	}
	
	protected void overlayNumberOnTexture(PGraphics curTexture) {
		// for orientation confirmation
		curTexture.beginDraw();
		String fontFile2 = DemoAssets.fontDSEG7Path;
		PFont font = FontCacher.getFont(fontFile2, curTexture.height*0.8f);
		FontCacher.setFontOnContext(curTexture, font, p.color(0, 0, 0), 1.8f, PTextAlign.CENTER, PTextAlign.CENTER);
		curTexture.text(((P.second()%10)+""), 0, 0, curTexture.width, curTexture.height);
		curTexture.endDraw();
	}

	protected void updateNoiseTexture() {
		// update noise map
		noise3d.update(1.8f, 0, 0, -FrameLoop.count(0.01f), FrameLoop.count(0.0075f), false, false);

		// post-process noise map
		ColorizeFromTexture.instance(p).setTexture(ImageGradient.BLACK_HOLE());
		ColorizeFromTexture.instance(p).setTexture(ImageGradient.SPARKS_FLAMES());
		ColorizeFromTexture.instance(p).applyTo(noise3d.texture());
		SaturationFilter.instance(p).setSaturation(3f);
		SaturationFilter.instance(p).applyTo(noise3d.texture());
		BrightnessFilter.instance(p).setBrightness(0.5f);
		BrightnessFilter.instance(p).applyTo(noise3d.texture());
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(noise3d.texture());
		
		BlurHFilter.instance(p).setBlurByPercent(1f, noise3d.texture().width);
		BlurVFilter.instance(p).setBlurByPercent(1f, noise3d.texture().height);
		for (int i = 0; i < 10; i++) {
			BlurHFilter.instance(p).applyTo(noise3d.texture());
			BlurVFilter.instance(p).applyTo(noise3d.texture());
		}
	}
}
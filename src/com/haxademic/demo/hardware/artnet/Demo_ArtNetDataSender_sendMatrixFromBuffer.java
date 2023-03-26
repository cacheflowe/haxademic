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
import com.haxademic.core.draw.textures.pgraphics.TextureEQBandDistribute;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_ArtNetDataSender_sendMatrixFromBuffer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected PGraphics ledTexture;
//	protected int matrixSize = 16;
	protected int numPixels;

	protected BaseTexture texture;
	protected SimplexNoise3dTexture noise3d;
	protected PGraphics verticalPG;
	
	protected String BRIGHTNESS = "BRIGHTNESS";
	protected String FLIP_H = "FLIP_H";
	protected String FLIP_V = "FLIP_V";
	protected String ROT_180 = "ROT_180";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// prepare ArtNetSender & matrix texture
		ledTexture = PG.newPG(48, 12);
		numPixels = ledTexture.width * ledTexture.height;
		DebugView.setTexture("ledTexture", ledTexture);
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 6, numPixels);

		// build textures
		texture = new TextureEQBandDistribute(256, 256);
//		texture = new TextureAudioTube(256, 256);
		texture.setActive(true);
		AudioIn.instance(AudioInputLibrary.Minim);
		// alt texture
		noise3d = new SimplexNoise3dTexture(ledTexture.width, ledTexture.height);
		// video
		DemoAssets.movieTestPattern().loop();
		// vertical texture, to be rotated
		verticalPG = PG.newPG(12, 48);
		
		// Add UI
		UI.addTitle("LED Config");
		UI.addSlider(BRIGHTNESS, 0.2f, 0, 1, 0.01f, false);
		UI.addToggle(FLIP_H, false, false);
		UI.addToggle(FLIP_V, false, false);
		UI.addToggle(ROT_180, false, false);
	}

	protected void drawApp() {
		p.background(0);
		
		// update texture
		PGraphics curTexture;
		if(Mouse.xNorm < 0.5f) {
			// audioreactive texture
			texture.update();
			curTexture = texture.texture();
			// numbers
			if(p.frameCount % 5 == 0) {
				verticalPG.copy(0, 0, 16, 48, 0, 16, 16, 48);
				verticalPG.beginDraw();
				verticalPG.fill(0);
				verticalPG.rect(0, 0, 16, 16);
				verticalPG.noStroke();
				FontCacher.setFontOnContext(verticalPG, FontCacher.getFont(DemoAssets.fontDSEG7Path, 15), p.color(255, 0, 127), 1.8f, PTextAlign.LEFT, PTextAlign.TOP);
				verticalPG.text(P.round(p.frameCount/10 % 10) + "", 0, 0, 16, 16);
				verticalPG.endDraw();
				DebugView.setTexture("verticalPG", verticalPG);
			}
			ImageUtil.drawImageCropFillRotated90deg(verticalPG, ledTexture, true, false, true);
			// video override
//			ImageUtil.drawImageCropFillRotated90deg(DemoAssets.movieTestPattern(), ledTexture, false, false, true);
			ImageUtil.copyImage(DemoAssets.movieTestPattern(), ledTexture);
			DebugView.setTexture("movieTestPattern", DemoAssets.movieTestPattern());
		} else {
			updateNoiseTexture();
			curTexture = noise3d.texture();
		}
		if(Mouse.yNorm > 0.5f) overlayNumberOnTexture(curTexture);
		
		// copy to tiny texture, reduce brightness & send!
//		ImageUtil.copyImage(curTexture, ledTexture);
//		ImageUtil.cropFillCopyImage(curTexture, ledTexture, true);
		BrightnessFilter.instance().setBrightness(UI.value(BRIGHTNESS));
		BrightnessFilter.instance().applyTo(ledTexture);
		
		// prep rotation
		if(UI.valueToggle(FLIP_H)) ImageUtil.flipH(ledTexture);
		if(UI.valueToggle(FLIP_V)) ImageUtil.flipV(ledTexture);
		if(UI.valueToggle(ROT_180)) ImageUtil.rotate180(ledTexture);
		
		// send it!
		artNetDataSender.sendMatrixFromBuffer(ledTexture);
		
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
		ColorizeFromTexture.instance().setTexture(ImageGradient.BLACK_HOLE());
		ColorizeFromTexture.instance().setTexture(ImageGradient.SPARKS_FLAMES());
		ColorizeFromTexture.instance().applyTo(noise3d.texture());
		SaturationFilter.instance().setSaturation(3f);
		SaturationFilter.instance().applyTo(noise3d.texture());
		BrightnessFilter.instance().setBrightness(0.5f);
		BrightnessFilter.instance().applyTo(noise3d.texture());
		ContrastFilter.instance().setContrast(2f);
		ContrastFilter.instance().applyTo(noise3d.texture());
		
		BlurHFilter.instance().setBlurByPercent(1f, noise3d.texture().width);
		BlurVFilter.instance().setBlurByPercent(1f, noise3d.texture().height);
		for (int i = 0; i < 10; i++) {
			BlurHFilter.instance().applyTo(noise3d.texture());
			BlurVFilter.instance().applyTo(noise3d.texture());
		}
	}
}
package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.hardware.dmx.artnet.ArtNetLedStrip_DEPRECATE;
import com.haxademic.core.render.FrameLoop;

public class Demo_ArtNetLedStrip
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoise3dTexture noise3d;
	protected ArtNetLedStrip_DEPRECATE strip;

	protected void firstFrame() {
		noise3d = new SimplexNoise3dTexture(p.width, p.height);
		strip = new ArtNetLedStrip_DEPRECATE("192.168.1.101", 0, 100);
	}

	protected void drawApp() {
		// update noise map
		noise3d.offsetZ(p.frameCount / 10f);
		noise3d.update(1f, 0, 0, 0, FrameLoop.count(0.001f), false, false);

		// post-process noise map
		ContrastFilter.instance().setContrast(3f);
		ContrastFilter.instance().setOnContext(noise3d.texture());
		ColorizeFromTexture.instance().setTexture(ImageGradient.BLACK_HOLE());
		ColorizeFromTexture.instance().setOnContext(noise3d.texture());

		// draw to screen
		p.image(noise3d.texture(), 0, 0);
		
		// update artnet strip
		strip.update(noise3d.texture(), 0.5f, 0.7f);
	}
}
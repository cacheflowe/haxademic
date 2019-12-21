package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.ui.UI;

public class Demo_SimplexNoiseTexture 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoiseTexture noiseTexture;
	protected String ZOOM = "ZOOM";
	protected String ROTATION = "ROTATION";
	protected String OFFSET_X = "OFFSET_X";
	protected String OFFSET_Y = "OFFSET_Y";

	protected void firstFrame() {
		// init controls
		UI.addSlider(ZOOM, 1f, 0.01f, 20f, 0.01f, false);
		UI.addSlider(ROTATION, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(OFFSET_X, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Y, 0f, -100f, 100f, 0.01f, false);
		
		// init noise object
		noiseTexture = new SimplexNoiseTexture(p.width, p.height);
		noiseTexture.update(0.07f, 0, 0, 0);
	}

	public void drawApp() {
		background(0);
		
		// update perlin texture
		noiseTexture.update(
				UI.value(ZOOM),
				UI.value(ROTATION),
				UI.value(OFFSET_X),
				UI.value(OFFSET_Y)
		);
		
		// draw to screen
		p.image(noiseTexture.texture(), 0, 0);  
	}
		
}
package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.ui.UI;

public class Demo_SimplexNoise3dTexture 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoise3dTexture noiseTexture;
	protected String ZOOM = "ZOOM";
	protected String ROTATION = "ROTATION";
	protected String OFFSET_X = "OFFSET_X";
	protected String OFFSET_Y = "OFFSET_Y";
	protected String OFFSET_Z = "OFFSET_Z";
	protected String FRACTAL_MODE = "FRACTAL_MODE";

	protected void firstFrame() {
		// init controls
		UI.addSlider(ZOOM, 1f, 0.01f, 20f, 0.01f, false);
		UI.addSlider(ROTATION, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(OFFSET_X, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Y, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Z, 0f, -100f, 100f, 0.001f, false);
		UI.addToggle(FRACTAL_MODE, false, false);
		
		// init noise object
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false);
	}

	protected void drawApp() {
		background(0);
		
		// update perlin texture
		noiseTexture.update(
				UI.valueEased(ZOOM),
				UI.valueEased(ROTATION),
				UI.valueEased(OFFSET_X),
				UI.valueEased(OFFSET_Y),
				UI.valueEased(OFFSET_Z),
				UI.valueToggle(FRACTAL_MODE)
		);
		
		// draw to screen
		p.image(noiseTexture.texture(), 0, 0);  
	}
		
}
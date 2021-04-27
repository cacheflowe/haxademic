package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

public class Demo_SimplexNoise3dTexture_toCylinderTexture 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	// noise
	protected SimplexNoise3dTexture noiseTexture;
	protected String ZOOM = "ZOOM";
	protected String ROTATION = "ROTATION";
	protected String OFFSET_X = "OFFSET_X";
	protected String OFFSET_Y = "OFFSET_Y";
	protected String OFFSET_Z = "OFFSET_Z";
	protected String FRACTAL_MODE = "FRACTAL_MODE";
	protected String X_REPEAT_MODE = "X_REPEAT_MODE";
	
	// shape
	protected String ROT_X = "ROT_X";
	protected String ROT_Y = "ROT_Y";

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// init noise controls
		UI.addTitle("Noise controls");
		UI.addSlider(ZOOM, 1f, 0.01f, 20f, 0.01f, false);
		UI.addSlider(ROTATION, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(OFFSET_X, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Y, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Z, 0f, -100f, 100f, 0.01f, false);
		UI.addToggle(FRACTAL_MODE, false, false);
		UI.addToggle(X_REPEAT_MODE, true, false);

		// init noise object
//		TextureShader.HOT_SWAP = true;
		noiseTexture = new SimplexNoise3dTexture(P.round(100 * P.TWO_PI), 100);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, true);
		DebugView.setTexture("noiseTexture.texture()", noiseTexture.texture());
		
		// init cylinder
		UI.addTitle("Shape controls");
		UI.addSlider(ROT_X, -0.5f, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(ROT_Y, 0f, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		
		shape = Shapes.createCan(p.width * 0.2f, p.height * 0.3f, 140); 
		shape.setTexture(noiseTexture.texture());
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
				UI.valueToggle(FRACTAL_MODE),
				UI.valueToggle(X_REPEAT_MODE)
		);
		
		// set cylinder context
		PG.setBetterLights(p);
		PG.setCenterScreen(p);
		p.rotateX(UI.valueEased(ROT_X));
		p.rotateY(UI.valueEased(ROT_Y));
		p.shape(shape);
	}
		
}
package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.ui.UI;

public class PerlinNoise3d
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float increment = 0;
	public float detail = 0;
	public float xProgress = 0;
	public float yProgress = 0;
	
	protected String noiseScale = "noiseScale";
	protected String octaves = "octaves";
	protected String noiseSpeed = "noiseSpeed";
	protected String falloff = "falloff";
	protected String spacing = "spacing";

	protected float frames = 60 * 12;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 1000 );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false);
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
	}

	public void firstFrame() {
	

		UI.addSlider(noiseScale, 0.003f, 0.0001f, 0.025f, 0.0001f, false);
		UI.addSlider(octaves, 3f, 1, 8, 1, false);
		UI.addSlider(noiseSpeed, 0.02f, 0, 0.04f, 0.001f, false);
		UI.addSlider(falloff, 0.5f, 0, 1f, 0.001f, false);
		UI.addSlider(spacing, 40, 5, 150f, 1f, false);
	}

	public void drawApp() {
		float progress = (p.frameCount % frames) / frames;
		
//		p.blendMode(P.BLEND);
		p.blendMode(PBlendModes.BLEND);
		if(p.frameCount >= 1) p.background(255);
//		PG.feedback(p.g, p.color(255), 0.6f, 0.1f);
		p.ortho();
		
		p.blendMode(PBlendModes.SUBTRACT);

//		PG.setDrawFlat2d(p, true);
		p.pushMatrix();
		float halfSize = p.width * 0.2f;

		p.translate(p.width/2, p.height/2 + UI.value(spacing)/4f, -halfSize);
		p.rotateY(progress * P.TWO_PI);
		p.rotateX(progress * P.TWO_PI);
//		p.rotateY(P.PI/4f);
//		p.rotateX(P.PI/4f);
		
		float autoFalloff = (progress < 0.5f) ? P.map(progress, 0, 0.5f, 0, 1) : P.map(progress, 0.5f, 1f, 1, 0);
		p.noiseDetail(UI.valueInt(octaves), autoFalloff); // falloff

		// For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
		p.stroke(255);
		p.strokeWeight(1.85f);
		float spacingg = UI.value(spacing);
		for (float x = -halfSize; x < halfSize; x += spacingg) {
			for (float y = -halfSize; y < halfSize; y += spacingg) {
				for (float z = -halfSize; z < halfSize; z += spacingg) {
					p.stroke(p.noise(x) * 127f - 0f, p.noise(y) * 127f - 0f, p.noise(z) * 127f - 0f);
					float value = getNoise(x,y,z);
					float valueX = getNoise(x+spacingg,y,z);
					float valueY = getNoise(x,y+spacingg,z);
					float valueZ = getNoise(x,y,z+spacingg);
					if(value >= 0.5f) {
						if(valueX > 0.5f && x + spacingg < halfSize) p.line(x, y, z, x+spacingg, y, z);
						if(valueY > 0.5f && y + spacingg < halfSize) p.line(x, y, z, x, y+spacingg, z);
						if(valueZ > 0.5f && z + spacingg < halfSize) p.line(x, y, z, x, y, z+spacingg);
					}
				}
			}
		}
		p.popMatrix();
		
//		CubicLensDistortionFilter.instance(p).applyTo(p);
//		BadTVLinesFilter.instance(p).applyTo(p.g);
//		VignetteFilter.instance(p).applyTo(p.g);
		
	}
	
	protected float getNoise(float x, float y, float z ) {
		return p.noise(
				p.frameCount * UI.value(noiseSpeed) + x * UI.value(noiseScale), 
				p.frameCount * UI.value(noiseSpeed) + y * UI.value(noiseScale), 
				p.frameCount * UI.value(noiseSpeed) + z * UI.value(noiseScale)
		);
	}
	

}

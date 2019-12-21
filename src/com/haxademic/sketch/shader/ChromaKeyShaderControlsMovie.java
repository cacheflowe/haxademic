package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.textures.pgraphics.TextureShaderTimeStepper;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.video.Movie;


public class ChromaKeyShaderControlsMovie 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;

	PShader _chromaKeyFilter;
	public String thresholdSensitivity = "thresholdSensitivity";
	public String smoothing = "smoothing";
	public String colorToReplaceR = "colorToReplaceR";
	public String colorToReplaceG = "colorToReplaceG";
	public String colorToReplaceB = "colorToReplaceB";

	TextureShaderTimeStepper underlay;
	Movie movie;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 720 );
		Config.setProperty( AppSettings.HEIGHT, 1280 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 3 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1000 );
	}
	
	public void firstFrame() {


		underlay = new TextureShaderTimeStepper( p.width, p.height, "basic-checker.glsl" );
		
		movie = new Movie(this, FileUtil.getFile("video/dam_ghost_outline.mov")); 
//		movie = new Movie(this, FileUtil.getFile("video/dancelab/AlphaTest.mov")); 
		movie.play();
		movie.loop();
		movie.speed(1f);

		_pg = p.createGraphics( p.width, p.height, P.P3D );
		setupChromakey();
	}
		
	protected void setupChromakey() {
		UI.addSlider(thresholdSensitivity, 0.73f, 0, 1, 0.01f, false);
		UI.addSlider(smoothing, 0.18f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceR, 0.71f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceG, 0.99f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceB, 0.02f, 0, 1, 0.01f, false);
	}

	public void drawApp() {
		
		
//		PG.feedback(p.g, p.color(255), 0.05f, 6f);
		
		// add post effects
//		PixelateFilter.instance(p).setDivider(6, p.width, p.height);
//		PixelateFilter.instance(p).applyTo(p);
//		WobbleFilter.instance(p).applyTo(p);
//		WobbleFilter.instance(p).setTime(p.frameCount * 0.01f);
//		WobbleFilter.instance(p).setSpeed(0.5f);
//		WobbleFilter.instance(p).setSize(50f);
//		WobbleFilter.instance(p).setStrength(0.003f);

		// draw a background
		underlay.updateDrawWithTime(p.frameCount * 0.01f);
//		PG.setPImageAlpha(p, 0.05f);
		p.image(underlay.texture(), 0, 0);
//		PG.resetPImageAlpha(p);
		
//		p.fill(255, 127);
//		p.rect(0, 0, p.width, p.height);
//		p.fill(255);
//		SphereDistortionFilter.instance(p).applyTo(p);
//		BrightnessFilter.instance(p).setBrightness(1.5f);
//		BrightnessFilter.instance(p).applyTo(p);

//		p.pushMatrix();
//		p.translate(p.width/2, p.height/2);
//		Gradients.radial(p, p.width * 2, p.height * 2, p.color(127 + 127f * sin(p.frameCount/10f)), p.color(127 + 127f * sin(p.frameCount/11f)), 100);
//		p.popMatrix();
		

		// reset chroma key uniforms
		ChromaColorFilter.instance(p).setColorToReplace(UI.value(colorToReplaceR), UI.value(colorToReplaceG), UI.value(colorToReplaceB));
		ChromaColorFilter.instance(p).setSmoothing(UI.value(smoothing));
		ChromaColorFilter.instance(p).setThresholdSensitivity(UI.value(thresholdSensitivity));

		// draw frame to offscreen buffer
		_pg.beginDraw();
		_pg.clear();
		_pg.image(movie, 0, 0, _pg.width, _pg.height);
		_pg.endDraw();
		
		// apply filter & draw to scren
		ChromaColorFilter.instance(p).applyTo(_pg);
		p.image(_pg, 0, 0);
		
		
		
		// hide controls
		// p.translate(900000, 0);
	}
}

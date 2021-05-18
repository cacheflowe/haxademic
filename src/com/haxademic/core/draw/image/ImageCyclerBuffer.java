package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class ImageCyclerBuffer {
	
	protected PGraphics pg;
	protected PGraphics[] imageFrames;
	protected PShader transitionShader;
	protected float frames;
	protected int imageIndex = 0;
	protected int nextIndex = 1;
	protected float transitionFrames;
	
	// transition shaders
	
	public static String directional_wipe = "directional-wipe.glsl";
	public static String circle_open = "circle-open.glsl";
	public static String color_lerp = "color-lerp.glsl";
	public static String colour_distance = "colour-distance.glsl";
	public static String cross_zoom = "cross-zoom.glsl";
	public static String cube = "cube.glsl";
	public static String dissolve = "dissolve.glsl";
	public static String fly_eye = "fly-eye.glsl";
	public static String glitch_displace = "glitch-displace.glsl";
	public static String hsv_blend = "hsv-blend.glsl";
	public static String morph = "morph.glsl";
	public static String polka_dots_curtain = "polka-dots-curtain.glsl";
	public static String swap = "swap.glsl";
	public static String warp_fade = "warp-fade.glsl";
	public static String wind = "wind.glsl";
	
	// constructors

	public ImageCyclerBuffer(int w, int h, PImage[] images, int frames, float transitionFrames) {
		this(w, h, images, frames, transitionFrames, 0, true, directional_wipe);
	}
	
	public ImageCyclerBuffer(int w, int h, PImage[] images, int frames, float transitionFrames, int bgColor, boolean cropFill) {
		this(w, h, images, frames, transitionFrames, bgColor, cropFill, directional_wipe);
	}
	
	public ImageCyclerBuffer(int w, int h, PImage[] images, int frames, float transitionFrames, int bgColor, boolean cropFill, String shaderPath) {
		pg = PG.newPG(w, h);
		this.frames = frames;
		this.transitionFrames = transitionFrames;
		
		// prep & crop fill images
		imageFrames = new PGraphics[images.length];
		for (int i = 0; i < imageFrames.length; i++) {
			imageFrames[i] = PG.newPG2DFast(w, h);
			imageFrames[i].beginDraw();
			if(bgColor != -1) {
				imageFrames[i].background(bgColor);
			} else {
				imageFrames[i].clear();
			}
			ImageUtil.drawImageCropFill(images[i], imageFrames[i], cropFill);
			imageFrames[i].endDraw();
		}

		// load transition shader & set image uniforms
		transitionShader = P.p.loadShader(shaderPath(shaderPath));
		transitionShader.set("from", imageFrames[imageIndex]);
		transitionShader.set("to", imageFrames[nextIndex]);
	}
	
	public static String shaderPath(String fragFile) {
		return FileUtil.getPath("haxademic/shaders/transitions/" + fragFile);
	}
	
	public PImage image() {
		return pg;
	}
	
	public void update() {
		float loopFrame = P.p.frameCount % frames;
		float progress = loopFrame / frames; 
		
		// switch to next image
		if(progress == 0f) {
			imageIndex = nextIndex;
			nextIndex++;
			if(nextIndex >= imageFrames.length) nextIndex = 0;
			transitionShader.set("from", imageFrames[imageIndex]);
			transitionShader.set("to", imageFrames[nextIndex]);
		}
		
		// map progress through transition
		float loopProgress = 0;
		if(loopFrame >= frames - transitionFrames) loopProgress = P.map(loopFrame, frames - transitionFrames, frames, 0f, 1f);
		float easedProgress = Penner.easeInOutCubic(loopProgress);
		transitionShader.set("progress", easedProgress);
		pg.filter(transitionShader);	
	}
	
}
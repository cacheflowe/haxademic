package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class TextureImageTileScroll
extends BaseTexture {

	protected PGraphics img1;
	protected PGraphics img2;
	protected PGraphics resultBuffer;
	protected ArrayList<PImage> images;
	protected int imageIndex = 0;
	protected boolean showNextDirty = false;
	protected LinearFloat transitionProgress = new LinearFloat(0, 0.025f);
	protected EasingFloat rotation = new EasingFloat(0, 0.05f);
	protected EasingFloat xOffset = new EasingFloat(0, 0.05f);
	protected EasingFloat yOffset = new EasingFloat(0, 0.05f);
	protected EasingFloat scale = new EasingFloat(1, 0.05f);
	protected PShader transitionShader;
	protected TiledTexture tiledImg;

	public TextureImageTileScroll( int width, int height ) {
		super(width, height);
		
		img1 = P.p.createGraphics(512, 512, PGraphics.P2D);
		img2 = P.p.createGraphics(512, 512, PGraphics.P2D);
		resultBuffer = P.p.createGraphics(768, 768, PGraphics.P2D);

		tiledImg = new TiledTexture(resultBuffer);

		loadShader();
		loadImages();
	}
	
	protected void loadShader() {
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/colour-distance.glsl"));
//		transitionShader.set("interpolationPower", 2f);
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/polka-dots-curtain.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/fly-eye.glsl"));
		transitionShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/transitions/circle-open.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/color-lerp.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/directional-wipe.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/warp-fade.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/morph.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/swap.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/dissolve.glsl"));
//		transitionShader.set("blocksize", 6f);
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/cross-zoom.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/hsv-blend.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/wind.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/cube.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/glitch-displace.glsl"));
		
		transitionShader.set("from", img1);
		transitionShader.set("to", img2);
	}
	
	protected void loadImages() {
		// load images
		String imgBase = FileUtil.getPath("images/_sketch/occult-symbols/");
		ArrayList<String> files = FileUtil.getFilesInDirOfType(imgBase, "png");
		FileUtil.shuffleFileList( files );
		images = new ArrayList<PImage>();
		for( int i=0; i < files.size(); i++ ) {
			images.add(P.p.requestImage(imgBase + files.get(i)));
		}
		nextImage();
	}

	public void preDraw() {
		// redraw buffers when cycling to next image 
		// if(P.p.frameCount % frames == 0) showNextImage();
		if(showNextDirty) showNextImage();
		
		// run transition shaders
		transitionProgress.update();
		float easedProgress = Penner.easeInOutCubic(transitionProgress.value());
		transitionShader.set("progress", easedProgress);
		resultBuffer.filter(transitionShader);		
	}
	
	public void updateDraw() {
		// draw transition result to texture
		_texture.background(255);
		PG.setCenterScreen(_texture);
		PG.setDrawCenter(_texture);
		_texture.fill(255); // todo: remove this
		
		// update tiled texture object to fill texture
		scale.update(true);
		rotation.update(true);
		xOffset.update(true);
		yOffset.update(true);
		tiledImg.setRotation(rotation.value());
		tiledImg.setOffset(xOffset.value(), yOffset.value());
		tiledImg.setSize(scale.value(), scale.value());
		tiledImg.update();
		tiledImg.draw(_texture, width, height);
		
		InvertFilter.instance(P.p).applyTo(_texture);
	}
	
	public PImage image() {
		return images.get(imageIndex);
	}
	
	public PImage nextImage() {
		return images.get((imageIndex + 1) % images.size());
	}
	
	protected void showNextImage() {
		imageIndex++;
		if( imageIndex >= images.size() ) imageIndex = 0;
		
		transitionProgress.setInc(0.01f);
		transitionProgress.setCurrent(0);
		transitionProgress.setTarget(1);
		
		img1.beginDraw();
		img1.clear();
		img1.background(0,0);
		ImageUtil.drawImageCropFill(image(), img1, false);
		img1.endDraw();

		img2.beginDraw();
		img2.clear();
		img2.background(0,0);
		ImageUtil.drawImageCropFill(nextImage(), img2, false);
		img2.endDraw();

		transitionShader.set("from", image());
		transitionShader.set("to", nextImage());

		showNextDirty = false;
		
		scale.setTarget(MathUtil.randRangeDecimal(2f, 5f));
		rotation.setTarget(P.PI + MathUtil.randRangeDecimal(-0.2f, 0.2f));
		xOffset.setTarget(xOffset.target() + MathUtil.randRange(-2f, 2f) * 0.5f);
		yOffset.setTarget(yOffset.target() + MathUtil.randRange(-2f, 2f) * 0.5f);
	}

	public void updateTiming() {
//		showNextDirty = true;
	}
	
	public void updateTimingSection() {
		showNextDirty = true;
	}

}

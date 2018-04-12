package com.haxademic.core.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ImageGradient {

	protected PImage gradientImg;
	protected float gradientProgress;
	protected int sampleX;
	protected int sampleY;

	public ImageGradient(PImage texture) {
		gradientImg = texture;
		gradientImg.loadPixels();
		sampleY = P.floor(gradientImg.height * 0.5f);
	}

	public PImage texture() {
		return gradientImg;
	}

	public int getColorAtProgress(float progress) {
		sampleX = P.round(gradientImg.width * progress);
		sampleX = P.constrain(sampleX, 0, gradientImg.width - 1);
		return ImageUtil.getPixelColor(gradientImg, sampleX, sampleY);
	}

	public void drawDebug(PGraphics pg) {
		pg.image(gradientImg, 0, 0);
		pg.stroke(255, 0, 0);
		pg.noFill();
		pg.rect(sampleX - 1, 0, 3, gradientImg.height);
		pg.fill(255);
		pg.noStroke();
	}

	// Preset images ///////////////////////////////////////////////////////////////////////////////////////////////////////

	public static PImage BLACK_HOLE = null;
	public static PImage BLACK_HOLE() {
		if(BLACK_HOLE == null) BLACK_HOLE = P.p.loadImage(FileUtil.getFile("haxademic/images/palettes/chandra-black-hole-burst.jpg"));
		return BLACK_HOLE;
	}

	public static PImage PASTELS = null;
	public static PImage PASTELS() {
		if(PASTELS == null) PASTELS = P.p.loadImage(FileUtil.getFile("haxademic/images/palettes/pastels-gradient.png"));
		return PASTELS;
	}

	public static PImage SPARKS_FLAMES = null;
	public static PImage SPARKS_FLAMES() {
		if(SPARKS_FLAMES == null) SPARKS_FLAMES = P.p.loadImage(FileUtil.getFile("haxademic/images/palettes/sparks-flames.jpg"));
		return SPARKS_FLAMES;
	}
	
	public static String[] coolors = new String[] {
			"54f2f2-84a3cc-f2a9a9-f2ba8c-b4d693-crop.min.png",
			"002145-fcff4b-ba274a-ffffff-2191fb.png.min.png",
			"26547c-a805fa-f25757-e7e6f7-fcfcfc.png.min.png",
			"c0caad-899e8b-99c5b5-5b6c5d-36453b.png.min.png",
			"c1c1c1-1d3461-40484a-d6ae0e-fcfafa.png.min.png",
			"da373c-1c3144-2e282a-e8c547-f6e8ea.png.min.png",
			"ecd1be-fce097-a89f8e-dce4ed-4ed8a8.png.min.png",
			"ee6400-2e2f26-8dd517-5e161c-d6cb8a.png.min.png",
			"f4a0ac-e7d2f4-5783bf-d7926b-f5d4ac.png.min.png",
			"f9ffef-abff19-98f000-78bd00-477000.png.min.png",
			"fe7171-110085-ee4266-fac8cd-961876.png.min.png",
			"ff9481-110085-fac8cd-f24b93-961876.png.min.png",
			"ffb82b-5b2515-94dbf7-ffffff-000000.png.min.png",
	};
	
	public static PImage randomCoolor() {
		return P.p.loadImage(FileUtil.getFile("haxademic/images/palettes/coolors/" + coolors[MathUtil.randRange(0, coolors.length - 1)]));
	}

}

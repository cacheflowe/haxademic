package com.haxademic.core.draw.color;

import java.util.ArrayList;

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
	
	public static ArrayList<String> coolors = null; 	
	public static void getCoolorFilenames() {
		String imagesPath = FileUtil.getFile("haxademic/images/palettes/coolors/");
		coolors = FileUtil.getFilesInDirOfType(imagesPath, "png");
		for (int i = 0; i < coolors.size(); i++) {
			coolors.set(i, imagesPath + coolors.get(i)); 
		}
	}
	
	public static PImage randomCoolor() {
		if(coolors == null) getCoolorFilenames();
		return P.p.loadImage(coolors.get(MathUtil.randRange(0, coolors.size() - 1)));
	}

}

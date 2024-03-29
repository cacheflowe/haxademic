package com.haxademic.core.hardware.depthcamera.ar;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectV2SkeletonsAR;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;

import processing.core.PGraphics;
import processing.core.PImage;

public class ArElementImage 
extends ArObjectBase {

	protected ImageSequenceMovieClip imageSequence;
	protected PImage image;
	protected LinearFloat fadeIn;

	public ArElementImage(String filePath, float baseScale, BodyTrackType bodyTrackType) {
		this(filePath, baseScale, bodyTrackType, 30);
	}
	
	public ArElementImage(String filePath, float baseScale, BodyTrackType bodyTrackType, int animFps) {
		super(baseScale, bodyTrackType);

		imageSequence = new ImageSequenceMovieClip(FileUtil.getPath(filePath), "png", animFps);
		imageSequence.loop();
	}

	public ArElementImage(PImage image, float baseScale, BodyTrackType bodyTrackType) {
		super(baseScale, bodyTrackType);
		this.image = image;
	}
	
	public void addFadeIn() {
		fadeIn = new LinearFloat(0, 0.05f);
	}
	
	public void updatePre(PGraphics pg) {
		if(imageSequence != null) {
			imageSequence.preCacheImages(pg);
			imageSequence.update();
		}
	}

	public void play() {
		imageSequence.stopLooping();
		imageSequence.playFromStart();
	}
	
	public boolean isPlaying() {
		return imageSequence.isPlaying();
	}
	
	public float progress() {
		return imageSequence.progress();
	}
	
	public PImage image() {
		return (imageSequence != null) ? imageSequence.image() : image;
	}
	
	protected void drawLoadingBar(PGraphics pg) {
		if(imageSequence != null && imageSequence.loadProgress() < 1) {
			PG.setDrawCorner(pg);
			pg.noStroke();
			pg.fill(0);
			pg.rect(-102, -22, 104, 24);
			pg.fill(0, 255, 0);
			pg.rect(-100, -20, 100 * imageSequence.loadProgress(), 20);
		}
	}
	
	public void draw(PGraphics pg) {
		if(fadeIn != null) {
			if(this.isReset == true) {
				fadeIn.setCurrent(0).setTarget(1);
			}
			fadeIn.update();
		}
		
		float curScale = MathUtil.scaleToTarget(image().height, KinectV2SkeletonsAR.CAMERA_HEIGHT * baseScale);
		float imgW = image().width * curScale * userScale; 
		float imgH = image().height * curScale * userScale; 
		PG.setDrawCenter(pg);
		pg.push();
		pg.translate(position.x, position.y);
		setRotationOnContext(pg);
		float responsiveHeight = KinectV2SkeletonsAR.CAMERA_HEIGHT * baseScale;
		pg.translate(
				positionOffset.x * userScale * responsiveHeight, 
				positionOffset.y * userScale * responsiveHeight, 
				positionOffset.z * userScale * responsiveHeight
		);
		if(fadeIn != null) PG.setPImageAlpha(pg, fadeIn.value());
		pg.image(image(), pivotOffset.x * imgW, pivotOffset.y * imgH, imgW, imgH);
		PG.resetPImageAlpha(pg);
		
		drawLoadingBar(pg);
		pg.pop();
		PG.setDrawCorner(pg);

		// once we've drawn, we're lerping
		this.isReset = false;
	}
	
}
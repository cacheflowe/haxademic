package com.haxademic.core.hardware.depthcamera.ar;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ArElementImage 
extends ArObjectBase
implements IArElement {

	protected ImageSequenceMovieClip imageSequence;
	protected PImage image;

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
	
	public void updatePre(PGraphics pg) {
		if(imageSequence != null) {
			imageSequence.preCacheImages(pg);
			imageSequence.update();
		}
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
		float curScale = MathUtil.scaleToTarget(image().height, pg.height * baseScale);
		float imgW = image().width * curScale * userScale; 
		float imgH = image().height * curScale * userScale; 
		PG.setDrawCenter(pg);
		pg.push();
		pg.translate(position.x, position.y);
		setRotationOnContext(pg);
		float responsiveHeight = pg.height * baseScale;
		pg.translate(
				positionOffset.x * userScale * responsiveHeight, 
				positionOffset.y * userScale * responsiveHeight, 
				positionOffset.z * userScale * responsiveHeight
		);
		pg.image(image(), pivotOffset.x * imgW, pivotOffset.y * imgH, imgW, imgH);
		
		drawLoadingBar(pg);
		pg.pop();
		PG.setDrawCorner(pg);

		// once we've drawn, we're lerping
		this.isReset = false;
	}
	
}
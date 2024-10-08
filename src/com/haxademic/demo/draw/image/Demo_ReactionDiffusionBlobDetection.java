package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BlobFinder;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.compound.ReactionDiffusionStepFilter;
import com.haxademic.core.draw.image.ImageFramesHistory;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.ui.UI;

import blobDetection.Blob;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ReactionDiffusionBlobDetection
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String RD_ITERATIONS = "RD_ITERATIONS";
	protected String RD_BLUR_ITERATIONS = "RD_BLUR_ITERATIONS";
	protected String RD_BLUR_X = "RD_BLUR_X";
	protected String RD_BLUR_Y = "RD_BLUR_Y";
	protected String RD_SHARPEN = "RD_SHARPEN";
	protected String RD_THRESH_ACTIVE = "RD_THRESH_ACTIVE";
	protected String RD_THRESH_CROSSFADE = "RD_THRESH_CROSSFADE";
	protected String RD_THRESH_CUTOFF = "RD_THRESH_CUTOFF";

	protected BlobFinder blobFinder;
	
	protected ImageFramesHistory recorder;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 300 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 2100 );
	}

	protected void firstFrame() {
		// set up UI
//		UI.addTitle("ReactionDiffusionStepFilter");
		UI.addSlider(RD_ITERATIONS, 3, 0, 20, 1, false);
		UI.addSlider(RD_BLUR_ITERATIONS, 3, 0, 20, 1, false);
		UI.addSlider(RD_BLUR_X, 1.5f, 0, 1.7f, 0.01f, false);
		UI.addSlider(RD_BLUR_Y, 1.5f, 0, 1.7f, 0.01f, false);
		UI.addSlider(RD_SHARPEN, 30f, 0, 30, 0.1f, false);
		UI.addToggle(RD_THRESH_ACTIVE, true, false);
		UI.addSlider(RD_THRESH_CROSSFADE, 0.5f, 0, 1, 0.01f, false);
		UI.addSlider(RD_THRESH_CUTOFF, 0.5f, 0, 1, 0.01f, false);

		// init blob detection
		blobFinder = new BlobFinder(pg, 0.4f);
		
		// init PGraphics array
		recorder = new ImageFramesHistory(pg.width, pg.height, 32);
	}

	public void drawOuterEnclosure(PGraphics pg, int color) {
		// outer circle enclosure
		PG.setDrawCenter(pg);
		pg.noFill();
		pg.stroke(color);
		pg.strokeWeight(300);
		//		pg.ellipse(pg.width/2, pg.height/2, 900, 900);
		pg.ellipse(pg.width/2, pg.height/2, pg.width, pg.height);
		pg.ellipse(pg.width/2, pg.height/2, pg.width * 1.2f, pg.height * 1.2f);
	}

	public void drawSeedCircle(PGraphics pg, int color) {
		// outer circle enclosure
		PG.setDrawCenter(pg);
		pg.noFill();
		pg.stroke(color);
		pg.strokeWeight(pg.width * 0.05f);
		//		pg.ellipse(pg.width/2, pg.height/2, 900, 900);
		pg.ellipse(pg.width/2, pg.height/2, pg.width * 0.3f, pg.height * 0.3f);
	}

	protected void drawApp() {
		p.background(255);

		// set up drawing context & border shape
		DebugView.setTexture("pg", pg);
		pg.beginDraw();
		if(p.frameCount == 1 || p.frameCount % 360 == 1) {
			pg.background(0);
			drawSeedCircle(pg, 255);
		}
//		if(p.frameCount % 120 == 1) drawSeedCircle(pg, 255);
		drawOuterEnclosure(pg, 255);

		// R/D effect
		ReactionDiffusionStepFilter.applyTo(pg, 
				UI.valueInt(RD_ITERATIONS), 
				UI.valueInt(RD_BLUR_ITERATIONS), 
				UI.valueEased(RD_BLUR_X),
				UI.valueEased(RD_BLUR_Y), 
				UI.valueEased(RD_SHARPEN),
				UI.valueToggle(RD_THRESH_ACTIVE),
				UI.valueEased(RD_THRESH_CROSSFADE),
				UI.valueEased(RD_THRESH_CUTOFF)
				);

		// zoom/rotate
		RotateFilter.instance().setZoom(1f);
		RotateFilter.instance().setRotation(0.004f);
		RotateFilter.instance().setOffset(0, 0.0f);
		RotateFilter.instance().applyTo(pg);

		pg.endDraw();

		// draw to screen
		p.image(pg, 0, 0);

		// blob finder
//		drawBlobs(p.g);
		
		// update buffer array
		recorder.addFrame(pg);
		// do some post-processing
		InvertFilter.instance().applyTo(recorder.getCurFrame());
		EdgesFilter.instance().applyTo(recorder.getCurFrame());
		LeaveWhiteFilter.instance().setCrossfade(0.95f);
		LeaveWhiteFilter.instance().applyTo(recorder.getCurFrame());
		
		// show debug frames
		DebugView.setTexture("recorder", recorder.getCurFrame());
		
		// display frames to screen
		p.background(0);
		p.ortho();
//		p.perspective();
//		recorder.drawDebug(p.g);
		float spacing = 150;
		PG.setCenterScreen(p);
		p.rotateX(Mouse.yNorm * P.TWO_PI);
		p.rotateY(Mouse.xNorm * P.TWO_PI);
		p.translate(0, 0, pg.height/4f);
		p.blendMode(PBlendModes.ADD);
		p.blendMode(PBlendModes.BLEND);
		int numFrames = recorder.images().length;
		p.translate(0, 0, -spacing * numFrames);
		PG.setDrawCenter(p);
		for (int i = 0; i < numFrames; i++) {
			p.push();
			p.translate(0, 0, spacing * i);
			p.tint(255 * ((float) i / recorder.images().length));
			float dispScale = 1f;
			PImage img = recorder.getSortedFrame(numFrames - 1 - i);
			p.tint(127f + 127 * P.sin(i/10f), 127f + 127 * P.sin(1 + i/10f), 127f + 127 * P.sin(2 + i/10f));
			p.image(img, 0, 0, img.width * dispScale, img.width * dispScale);
			p.pop();
		}
	}

	protected void drawBlobs(PGraphics pg) {
		// do detection
		blobFinder.update();

		// draw edges. scale up to screen size
		Blob blob;
		EdgeVertex eA; // , eB;
		float blobScaleW = pg.width;
		float blobScaleH = pg.height;

		pg.stroke(255, 0, 0);
		pg.strokeWeight(5);

		// loop through blobs
		int numBlobs = blobFinder.numBlobs();
		for (int i=0 ; i < numBlobs; i++) {
			blob = blobFinder.blobDetection().getBlob(i);
			if ( blob != null ) {
				// draw blob vertices
				pg.beginShape();
				int numBlobSegments = blob.getEdgeNb();
				for (int m = 0; m < numBlobSegments; m++) {
					eA = blob.getEdgeVertexA(m);
					// eB = blob.getEdgeVertexB(m);
					float segmentX = eA.x * blobScaleW;
					float segmentY = eA.y * blobScaleH;
					pg.vertex(segmentX, segmentY);
					pg.endShape(P.CLOSE);
				}
			}
		}

	}
}


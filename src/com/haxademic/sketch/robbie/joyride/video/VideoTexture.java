package com.haxademic.sketch.robbie.joyride.video;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.sketch.robbie.CameraController;
import com.haxademic.sketch.robbie.joyride.JoyrideSF;
import com.haxademic.sketch.robbie.joyride.JoyrideSF.App;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class VideoTexture 
implements IAppStoreListener {

	protected JoyrideSF p;
	protected PGraphics pg;
	
	protected PGraphics video1;
	protected Movie joyrideMovie;
	
	protected PGraphics mask;
	protected PImage maskImage;
	protected float maskScale;
	
	protected PGraphics diagram;
	protected PImage diagramImage;
	
	protected CameraController cameraController;
	
	protected boolean showDiagram = true;
	protected InputTrigger key1 = (new InputTrigger()).addKeyCodes(new char[]{'1'});

	
	public VideoTexture() {
		p = (JoyrideSF) P.p;
		pg = p.pg;
		P.store.addListener(this);

		video1 = PG.newPG(pg.width, pg.height);
		
		joyrideMovie = new Movie(p, FileUtil.getFile("images/nike_joyride_vid1_1920x2160.mp4/"));
		joyrideMovie.loop();

		mask = PG.newPG(pg.width, pg.height);
		maskImage = p.loadImage(FileUtil.getFile("images/joyrideSF_mask_1920x2160.png"));
		
		diagram = PG.newPG(pg.width, pg.height);
		diagramImage = p.loadImage(FileUtil.getFile("images/Joyride_Jumbotron_811x964.png"));
		
		cameraController = new CameraController(pg);
	}
	
	public void drawPre(int frameCount) {
		// check inputs
		if(key1.triggered()) {
			showDiagram = !showDiagram;
		}
				
		// draw diagram buffer
		if (showDiagram) {
			diagram.beginDraw();
			diagram.background(229, 234, 228);
			PG.setDrawCenter(diagram);
			PG.setCenterScreen(diagram);
			ImageUtil.cropFillCopyImage(diagramImage, diagram, false);
			diagram.endDraw();
		}
		
		// draw mask buffer
		mask.beginDraw();
		PG.setDrawCenter(mask);
		PG.setCenterScreen(mask);
		maskScale = 0.705f;
		mask.image(maskImage, -6, -26, mask.width * maskScale, mask.height * maskScale);
		mask.endDraw();
		
		// draw video buffer
		if(joyrideMovie.width > 10) {
			video1.beginDraw();
			video1.background(0, 0, 0);
			video1.image(joyrideMovie, 0, 0);
			video1.endDraw();
			
			video1.mask(mask);
		}
		
		
	}
	
	
	public void draw(int frameCount) {
		pg.background(0);
		
		// Camera rotation
		cameraController.draw();
		
		// draw diagram
		if (showDiagram) {
			pg.background(229, 234, 228);
			pg.image(diagram, 0, 0);
		}
		
		// draw video buffer
		pg.image(video1, 0, 0, pg.width, pg.height);
		
	}
	
	
	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////
	
	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(App.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
		if(key.equals(App.ANIMATION_FRAME)) draw(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {
//		if(key.equals(App.CYCLE_TEXTURE)) textureCycleNeeded = true;
	}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
	
}
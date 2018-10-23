package com.haxademic.app.slideshow.text;

import com.haxademic.app.slideshow.Slideshow;
import com.haxademic.app.slideshow.slides.SlideshowState;
import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class SlideTitle
implements IAppStoreListener {

	protected String title = null;
	protected String titleQueued = null;
	protected Slideshow p;
	protected LinearFloat showProgress = new LinearFloat(0, 0.013f);
	protected float height = 100;
	protected float fontSize = 50;
	protected float textOffsetY = 10;
	protected float opacity = 170; // of 255
	protected PFont font;

	public SlideTitle() {
		p = (Slideshow) P.p;
		height = p.height;
		fontSize = height * 0.1f;
		font = P.p.createFont( FileUtil.getFile(Slideshow.fontFile), fontSize );
	}
	
	protected boolean isShowing() {
		return showProgress.value() > 0.1f;
	}
	
	public void setTitle(String newTitle) {
		if(newTitle == null) {
			showProgress.setTarget(0);
			titleQueued = null;
		} else {
			if(isShowing() == true && title.equals(newTitle) == false) {
				showProgress.setTarget(0);
				titleQueued = newTitle;
			} else {
				title = newTitle;
				showProgress.setTarget(1);			
			}
		}
	}
	
	public void update(PGraphics pg) {
		showProgress.update();
		
		if(titleQueued != null && showProgress.value() == 0) {
			title = titleQueued;
			titleQueued = null;
			showProgress.setTarget(1);	
		}
		
		if(title != null) {
			DrawUtil.setDrawCorner(pg);
			pg.noStroke();
			
			// get eased y
			float easedProgress = Penner.easeInOutCubic(showProgress.value(), 0, 1, 1);
			
			pg.pushMatrix();
			
			// draw bg
			pg.fill(0, opacity * easedProgress);
			pg.rect(0, 0, pg.width, pg.height); 

			// draw text
			// buffer.textLeading(font.getSize() * 0.75f);
			pg.fill(255, 255 * easedProgress);
			pg.textAlign(P.CENTER, P.CENTER);
			pg.textFont(font);
			pg.text(title, 0, 0, pg.width, pg.height);
			
			pg.popMatrix();
			DrawUtil.setDrawCenter(pg);
		}
	}

	@Override
	public void updatedNumber(String storeKey, Number val) {
		if(storeKey == SlideshowState.SLIDE_INDEX.id()) {
			int slideIndex = val.intValue();
			if(slideIndex >= 0 && slideIndex < p.slides().size()) {
				setTitle(p.slides().get(slideIndex).title());
			} else {
				setTitle(null);
			}
		}		
	}

	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
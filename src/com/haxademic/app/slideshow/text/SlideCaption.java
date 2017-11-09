package com.haxademic.app.slideshow.text;

import com.haxademic.app.slideshow.Slideshow;
import com.haxademic.app.slideshow.slides.SlideshowState;
import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreUpdatable;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PFont;
import processing.core.PGraphics;

public class SlideCaption
implements IAppStoreUpdatable {

	protected String caption = null;
	protected String captionQueued = null;
	protected Slideshow p;
	protected LinearFloat showProgress = new LinearFloat(0, 0.013f);
	protected float height = 100;
	protected float fontSize = 50;
	protected float textOffsetY = 10;
	protected PFont font;

	public SlideCaption() {
		p = (Slideshow) P.p;
		height = P.round(p.height * 0.07f);
		fontSize = height * 0.5f;
		textOffsetY = height * 0.1f;
		font = P.p.createFont( FileUtil.getFile("fonts/CenturyGothic.ttf"), fontSize );
	}
	
	protected boolean isShowing() {
		return showProgress.value() > 0.1f;
	}
	
	public void setCaption(String newCaption) {
		if(newCaption == null) {
			showProgress.setTarget(0);
			captionQueued = null;
		} else {
			if(isShowing() == true && caption.equals(newCaption) == false) {
				showProgress.setTarget(0);
				captionQueued = newCaption;
			} else {
				caption = newCaption;
				showProgress.setTarget(1);			
			}
		}
	}
	
	public void update(PGraphics pg) {
		showProgress.update();
		
		if(captionQueued != null && showProgress.value() == 0) {
			caption = captionQueued;
			captionQueued = null;
			showProgress.setTarget(1);	
		}
		
		if(caption != null) {
			DrawUtil.setDrawCorner(pg);
			pg.noStroke();
			
			// get eased y
			float easedProgress = Penner.easeInOutCubic(showProgress.value(), 0, 1, 1);
			float curY = pg.height - height * easedProgress;
			
			pg.pushMatrix();
			
			// draw bg
			pg.fill(0, 127);
			pg.rect(0, curY, pg.width, height); // curY

			// draw text
			// buffer.textLeading(font.getSize() * 0.75f);
			pg.fill(255);
			pg.textAlign(P.CENTER, P.CENTER);
			pg.textFont(font);
			pg.text(caption, textOffsetY * 5f, curY - textOffsetY, pg.width - 50, height);
			
			pg.popMatrix();
			DrawUtil.setDrawCenter(pg);
		}
	}

	@Override
	public void updatedAppStoreValue(String storeKey, Number val) {
		if(storeKey == SlideshowState.SLIDE_INDEX.id()) {
			int slideIndex = val.intValue();
			if(slideIndex >= 0 && slideIndex < p.slides().size()) {
				setCaption(p.slides().get(slideIndex).caption());
			} else {
				setCaption(null);
			}
		}		
	}

	@Override
	public void updatedAppStoreValue(String key, String val) {
		// TODO Auto-generated method stub
		
	}
}
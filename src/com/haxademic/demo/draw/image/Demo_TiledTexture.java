package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.media.DemoAssets;

public class Demo_TiledTexture
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected TiledTexture tiledImg;
	
	protected String ROT = "ROT"; 
	protected String OFFSET_X = "OFFSET_X"; 
	protected String OFFSET_Y = "OFFSET_Y"; 
	protected String SIZE = "SIZE"; 

	public void setupFirstFrame() {
		tiledImg = new TiledTexture(DemoAssets.smallTexture());
		
		p.ui.addSlider(ROT, 0, 0, P.TWO_PI, 0.02f, false);
		p.ui.addSlider(OFFSET_X, 0, -20, 20, 0.01f, false);
		p.ui.addSlider(OFFSET_Y, 0, -20, 20, 0.01f, false);
		p.ui.addSlider(SIZE, 1, 0, 10, 0.01f, false);
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		p.pushMatrix();
		PG.setCenterScreen(p);

		tiledImg.setRotation(p.ui.value(ROT));
		tiledImg.setOffset(p.ui.value(OFFSET_X), p.ui.value(OFFSET_Y));
		tiledImg.setSize(p.ui.value(SIZE), p.ui.value(SIZE));
		tiledImg.update();
		tiledImg.drawCentered(p.g, p.width, p.height);
		p.popMatrix();
		
		tiledImg.drawDebug(p.g);
	}	

}
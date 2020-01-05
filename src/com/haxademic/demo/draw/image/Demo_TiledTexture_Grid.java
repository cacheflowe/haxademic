package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

public class Demo_TiledTexture_Grid
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected TiledTexture tiledImg;
	
	protected String ROT = "ROT"; 
	protected String OFFSET_X = "OFFSET_X"; 
	protected String OFFSET_Y = "OFFSET_Y"; 
	protected String SIZE = "SIZE"; 

	protected void firstFrame() {
		tiledImg = new TiledTexture(DemoAssets.smallTexture());
		
		UI.addSlider(ROT, 0, 0, P.TWO_PI, 0.02f, false);
		UI.addSlider(OFFSET_X, 0, -20, 20, 0.01f, false);
		UI.addSlider(OFFSET_Y, 0, -20, 20, 0.01f, false);
		UI.addSlider(SIZE, 1, 0, 10, 0.01f, false);
	}
	
	protected void drawApp() {
		p.background(255);
		p.noStroke();
		p.pushMatrix();
//		PG.setCenterScreen(p);

		tiledImg.setRotation(UI.valueEased(ROT));
		tiledImg.setOffset(UI.valueEased(OFFSET_X), UI.valueEased(OFFSET_Y));
		tiledImg.setSize(UI.valueEased(SIZE), UI.valueEased(SIZE));
		tiledImg.update();
		tiledImg.draw(p.g, p.width, p.height, false);
		p.popMatrix();
		
		tiledImg.drawDebug(p.g);
	}	

}
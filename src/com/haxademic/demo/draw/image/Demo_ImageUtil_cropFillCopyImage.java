package com.haxademic.demo.draw.image;

import java.awt.Rectangle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_ImageUtil_cropFillCopyImage 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Rectangle rect1 = new Rectangle(20, 20, 100, 200);
	protected Rectangle rect2 = new Rectangle(300, 300, 200, 100);
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		
		// config
		boolean cropFill = p.mousePercentX() < 0.5f;
		
		// fullscreen crop fill
		p.fill(255);
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), p.g, cropFill);
		
		// specific rectangle
		// bg frame
		p.fill(255);
		p.rect(rect1.x - 2, rect1.y - 2, rect1.width + 4, rect1.height + 4);
		p.fill(0);
		p.rect(rect1.x, rect1.y, rect1.width, rect1.height);
		
		p.fill(255);
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), p.g, rect1.x, rect1.y, rect1.width, rect1.height, cropFill);

		// specific rectangle
		// bg frame
		p.fill(255);
		p.rect(rect2.x - 2, rect2.y - 2, rect2.width + 4, rect2.height + 4);
		p.fill(0);
		p.rect(rect2.x, rect2.y, rect2.width, rect2.height);
		
		p.fill(255);
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), p.g, rect2.x, rect2.y, rect2.width, rect2.height, cropFill);
	}
}

package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.SecondWindow;

import processing.core.PGraphics;

public class Demo_SecondWindow 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SecondWindow secondWindow;
	protected SecondWindow secondWindow2;
	protected PGraphics buffer;
	
	protected void config() {
		Config.setAppSize(512, 512);
	}

	protected void firstFrame() {
		buffer = PG.newPG(256, 256);
		DebugView.setTexture("buffer", buffer);
		secondWindow = new SecondWindow(buffer, 100, 0);
	}

	protected void drawApp() {
		// create another app
		// if(p.frameCount == 50) secondWindow2 = new SecondWindow(buffer, 400, 300);

		// draw main app
		PG.setDrawCenter(p);
		p.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		p.fill(255);
		p.translate(p.width/2, p.height/2);
		p.rotate(p.frameCount * 0.01f);
		p.rect(0, 0, 100, 100);

		// draw test graphics for second window
		buffer.beginDraw();
		PG.setCenterScreen(buffer);
		PG.setDrawCenter(buffer);
		buffer.rotate(p.frameCount * 0.01f);
		buffer.image(DemoAssets.justin(), 0, 0);
		buffer.endDraw();

		// keystrokes on the main thread - important for doing any window operations
		if(KeyboardState.keyTriggered('s')) {
			// setting window size can crash pretty often. should research better ways of doing this
			secondWindow.setSize(MathUtil.randRange(64, 256), MathUtil.randRange(64, 256));
			// secondWindow.setSize(buffer.width, buffer.height);
		}
		if(KeyboardState.keyTriggered('l')) {
			secondWindow.setLocation(MathUtil.randRange(0, 100), MathUtil.randRange(0, 100));
			// secondWindow.setLocation(0, 0);
		}
		if(KeyboardState.keyTriggered('t')) {
			secondWindow.moveToTop();
		}
	}
	
}

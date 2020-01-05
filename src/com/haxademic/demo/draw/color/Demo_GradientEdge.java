package com.haxademic.demo.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_GradientEdge 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics fadeEdge;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		float fadeSize = 0.7f;
		float solidSize = 1f - fadeSize;
		
		
		fadeEdge = p.createGraphics(1024, 1024, P.P3D);
		fadeEdge.beginDraw();
		PG.setDrawCenter(fadeEdge);
		// draw gradient
		fadeEdge.translate(fadeEdge.width * (fadeSize * 0.5f), fadeEdge.height / 2);
		fadeEdge.noStroke();
		Gradients.linear(fadeEdge, fadeEdge.width * fadeSize, fadeEdge.height, p.color(1, 1), p.color(0,255));
		// draw black portion
		fadeEdge.translate(fadeEdge.width * (solidSize * 0.5f + fadeSize * 0.5f), 0);
		fadeEdge.fill(0);
		fadeEdge.rect(0, 0, fadeEdge.width * solidSize, fadeEdge.height);
		fadeEdge.endDraw();
	}

	protected void drawApp() {
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), p.g, true);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		
		// draw
		p.rotate(P.map(Mouse.yNorm, 0, 1, P.PI, -P.PI));
		p.image(fadeEdge, 0, 0, 
				fadeEdge.width * (0.5f + 0.2f * P.sin(p.frameCount * 0.01f)),
				fadeEdge.height * (0.5f + 0.2f * P.sin(p.frameCount * 0.015f))
				);
	}
		
}
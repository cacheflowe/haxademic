package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DitherColorBandsPatricio;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

public class Demo_ColorBandingDitherPatricio
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setAppSize(1400, 720);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.FILLS_SCREEN, false);
		Config.setProperty(AppSettings.PG_32_BIT, false);
	}
	
	protected void drawApp() {
		p.background(0);
		
		// draw gradient into buffer
		pg.beginDraw();
		pg.background(0);
		PG.setCenterScreen(pg);
		pg.translate(FrameLoop.osc(0.01f,	-50, 50), 0);
		Gradients.radial(pg, pg.width * 4, pg.height * 4, 0xffffffff, 0xff000000, 100);
		Gradients.linear(pg, pg.width, pg.height, 0xff333333, 0xffcc5555);
		pg.fill(255);
		pg.rect(200, 200, 200, 200);
		pg.endDraw();

		// run dither
//			DitherColo/rBandsPatricio.instance().updateHotSwap();
		DitherColorBandsPatricio.instance().setTime(p.millis() / 1000.0f);
		DitherColorBandsPatricio.instance().setAmp(Mouse.yNorm);
		DitherColorBandsPatricio.instance().applyTo(pg);
		DebugView.setValue("running shader", true);
		
		if(Mouse.xNorm > 0.75f) {
			ContrastFilter.instance().setContrast(2.15f);
			ContrastFilter.instance().applyTo(pg);
		}
		
		// draw buffer to screen
		p.image(pg, 0, 0);
//		ImageUtil.cropFillCopyImage(pg, p.g, false);
	}

}

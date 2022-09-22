package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.AppUtil;
import com.haxademic.core.ui.UI;

public class Demo_ColorBandingDither
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	
	protected void config() {
		Config.setAppSize(1600, 800);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.FILLS_SCREEN, true);
		Config.setProperty(AppSettings.PG_32_BIT, false);
		Config.setPgSize(1920, 1080);
	}
	
	protected void firstFrame() {
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/color-dither.glsl"));
		UI.addTitle("Shader Uniforms");
//		UI.addSlider("crossfade", 1f, 0.0f, 1f, 0.01f, false);
		AppUtil.setLocation(p, 0, 0);
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') reset();
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		
		// draw into buffer
		pg.beginDraw();
//		pg.clear();
		pg.background(100);
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), pg, true);
		PG.setCenterScreen(pg);
		pg.translate(FrameLoop.osc(0.01f,	-50, 50), 0);
		Gradients.radial(pg, pg.width * 4, pg.height * 4, 0xffffffff, 0xff000000, 100);
		Gradients.linear(pg, pg.width, pg.height, 0xff333333, 0xff555555);
		pg.fill(255);
		pg.rect(200, 200, 200, 200);
		pg.endDraw();

//		shader.shader().set("time", 0);
//		shader.shader().set("crossfade", UI.valueEased("crossfade"));
		if(FrameLoop.frameMod(120) < 60) {
			
//			DitherFilter.instance(p).applyTo(pg);
			
			BlurProcessingFilter.instance(p).setBlurSize(40);
			BlurProcessingFilter.instance(p).setSigma(40);

			// do it again?!
			// run filter on buffer
			shader.update();
			shader.shader().set("blueNoiseTex", ImageCacher.get("haxademic/images/noise/blue-noise-512.png"));
			shader.shader().set("time", FrameLoop.count(0.00000001f));
//			shader.shader().set("time", 0f);
			shader.shader().set("noiseAmp", 7f);
			pg.filter(shader.shader());
			DebugView.setValue("running shader", true);

			// to help see the results
		} else {
			DebugView.setValue("running shader", false);
		}
		ContrastFilter.instance(p).setContrast(2.15f);
//		ContrastFilter.instance(p).applyTo(pg);
		
		// draw buffer to screen
		p.image(pg, 0, 0);
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// show shader compilation
		shader.showShaderStatus(p.g);
		DebugView.setValue("isValid()", shader.isValid());
	}

}

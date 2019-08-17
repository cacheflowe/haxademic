package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GlitchSuite;
import com.haxademic.core.draw.filters.pshader.GlitchSuite.GlitchMode;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_GlitchSuite
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics pg;
	protected GlitchSuite glitches;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 960 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setupFirstFrame() {
		shape = DemoAssets.shapeX().getTessellation();
		shape.disableStyle();
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.7f);
		pg = PG.newPG(p.width, p.height);
		
		glitches = new GlitchSuite();
		glitches = new GlitchSuite(new GlitchMode[] {
				GlitchMode.ShaderA,
				GlitchMode.PixelSorting,
				GlitchMode.Shake,
				GlitchMode.ImageGlitcher,
				GlitchMode.Invert,
				GlitchMode.HFlip,
				GlitchMode.Edges,
				GlitchMode.Repeat,
				GlitchMode.Mirror,
				GlitchMode.ColorDistortion,
				GlitchMode.BadTV2,
				GlitchMode.Grain,
				GlitchMode.Slide,
		});
	}

	public void drawApp() {
		background(255);
		
		// update drawing
		pg.beginDraw();
		pg.background(255);
		pg.fill(255);
		ImageUtil.cropFillCopyImage(WebCam.instance().image(), pg, true);
		PG.setCenterScreen(pg);
		pg.rotate(p.frameCount * 0.04f);
		pg.blendMode(PBlendModes.EXCLUSION);
		pg.shape(shape, 0, 0);
		pg.blendMode(PBlendModes.BLEND);
		pg.endDraw();
		
		// glitch
		if(p.frameCount % 100 == 1) {
			glitches.newGlitchMode();
			// glitches.startGlitchMode(GlitchMode.Repeat);
		}
		glitches.applyTo(pg);
		
		// draw shadow buffer to screen
		p.image(pg, 0, 0);
	}

}


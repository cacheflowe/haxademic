package com.haxademic.demo.draw.shapes.shader;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_MatCap
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// Ported from @wlut's work: https://wblut.com/code/matcap.zip
	// Get matcap images: https://github.com/nidorx/matcaps
	
	protected PShape shape;
	protected PShader matCapShader;
	protected ArrayList<PImage> matCapImages;
	protected String RANGE = "RANGE";
	protected String MATCAP_IMG_INDEX = "MATCAP_IMG_INDEX";

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		// load shape, scale & center
		shape = DemoAssets.objSkullRealistic().getTessellation();
//		shape = PShapeUtil.createSphere(100, 0xff00ff00).getTessellation();
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.55f);
		PShapeUtil.centerShape(shape);

		// shader + UI
		matCapImages = FileUtil.loadImagesFromDir(FileUtil.getPath("haxademic/images/matcap/"), "png,jpg");
		matCapShader = p.loadShader(
				FileUtil.getPath("haxademic/shaders/lights/matcap/matcap-frag.glsl"), 
				FileUtil.getPath("haxademic/shaders/lights/matcap/matcap-vert.glsl")
		);
		UI.addSlider(RANGE, 0.96f, 0, 5, 0.01f, false);
		UI.addSlider(MATCAP_IMG_INDEX, 0, 0, matCapImages.size() - 1, 1, false);
	}

	protected void drawApp() {
		// set context
		p.background(0);
		PG.setCenterScreen(p);
//		PG.basicCameraFromMouse(p.g, 0.1f);
		p.rotateY(P.sin(FrameLoop.count(0.05f)) * 0.4f);
		
		// set shader
		p.noLights();
		matCapShader.set("range", UI.value(RANGE));
		matCapShader.set("matcap", matCapImages.get(UI.valueInt(MATCAP_IMG_INDEX)));
		p.shader(matCapShader);
		p.noStroke();
		
		// show cur matcap
		DebugView.setTexture("matcap", matCapImages.get(UI.valueInt(MATCAP_IMG_INDEX)));
		
		// draw shapes
		p.shape(shape);
		
		p.translate(200, 0);
		p.rotateX(p.frameCount * 0.01f);
		p.rotateZ(p.frameCount * 0.01f);
		p.box(50);
		
		p.resetShader();
	}

}

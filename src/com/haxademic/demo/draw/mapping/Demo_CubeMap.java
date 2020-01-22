package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_CubeMap
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics image;
	protected TextureShader curShader;
	
	// split original image into 4 quads
	protected float midX1 = 0.2f;
	protected float midX2 = 1f - midX1;
	protected int subdivisions = 10;
	protected float xPad = 0;
	protected String SUBDIVISIONS = "SUBDIVISIONS";
	protected String CORNER_X_PAD = "CORNER_X_PAD";
	protected String MID_X_PAD = "MID_X_PAD";
	protected String SCALE = "SCALE";
	protected String IMAGE_MODE = "IMAGE_MODE";


	protected void firstFrame()	{
		// image setup
		image = PG.newPG(1000, 500);
		DebugView.setTexture("image", image);

		curShader = new TextureShader(TextureShader.cacheflowe_down_void);
		
		// ui
		UI.addSlider(SUBDIVISIONS, 10, 1, 20, 1, false);
		UI.addSlider(CORNER_X_PAD, 0, 0, 0.5f, 0.001f, false);
		UI.addSlider(MID_X_PAD, 0.2f, 0, 0.5f, 0.001f, false);
		UI.addSlider(SCALE, 1, 0.5f, 5f, 0.01f, false);
		UI.addSlider(IMAGE_MODE, 0, 0, 2, 1, false);
	}

	protected void drawApp() {
		// overwrite with test pattern
		PG.drawTestPattern(image);

		if(UI.valueInt(IMAGE_MODE) == 1) {
			// update image w/test pattern
			image.beginDraw();
			ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), image, true);
			image.endDraw();
		}

		if(UI.valueInt(IMAGE_MODE) == 2) {
			// overwrite with shader
			curShader.setTimeMult(0.003f);
			curShader.updateTime();
			image.filter(curShader.shader());
		}
		
		// draw UV mapping on texture itself
		drawUvVisualization();

		// prep context w/camera rotate
		p.background(0);
		p.noStroke();
		p.lights();
		PG.setCenterScreen(p);
		p.translate(0, -100, -500);
		PG.basicCameraFromMouse(p.g);
		p.pushMatrix();
		p.scale(UI.value(SCALE));
		
		// mapping calcs
		subdivisions = UI.valueInt(SUBDIVISIONS);
		xPad = UI.value(CORNER_X_PAD);
		midX1 = UI.value(MID_X_PAD);
		midX2 = 1f - midX1;
		
		// draw back wall
		Shapes.textureQuadSubdivided(p.g, image, subdivisions, 
				-400, -200, 0,  xPad, 0,
				400, -200,  0,  1 - xPad, 0,
				400, 200,   0,  midX2, 0.5f,
				-400, 200,  0,  midX1, 0.5f);

		// draw floor
		Shapes.textureQuadSubdivided(p.g, image, subdivisions, 
				-400, 200, 0,     midX1, 0.5f,
				400,  200, 0,     midX2, 0.5f,
				400,  200, 400,   1 - xPad, 1f,
				-400, 200, 400,   xPad, 1f);
		
		// draw left wall
		Shapes.textureQuadSubdivided(p.g, image, subdivisions, 
				-400, -200, 400,  0, 0.5f,
				-400, -200, 0,    xPad, 0,
				-400, 200,  0,    midX1, 0.5f,
				-400, 200,  400,  xPad, 1);

		// draw right wall
		Shapes.textureQuadSubdivided(p.g, image, subdivisions, 
				400, -200, 400,  1, 0.5f,
				400, -200, 0,    1 - xPad, 0,
				400, 200,  0,    midX2, 0.5f,
				400, 200,  400,  1 - xPad, 1);

		// end
		p.popMatrix();
	}
	
	protected void drawUvVisualization() {
		// draw visualization
		image.beginDraw();
		image.stroke(255, 0, 0);
		image.strokeWeight(10);
		image.noFill();
		
		// back wall
		image.beginShape();
		image.vertex(image.width * (xPad), image.height * 0);
		image.vertex(image.width * (1 - xPad), image.height * 0);
		image.vertex(image.width * midX2, image.height * 0.5f);
		image.vertex(image.width * midX1, image.height * 0.5f);
		image.endShape(P.CLOSE);
		
		// floor
		image.beginShape();
		image.vertex(image.width * midX1, image.height * 0.5f);
		image.vertex(image.width * midX2, image.height * 0.5f);
		image.vertex(image.width * (1 - xPad), image.height * 1);
		image.vertex(image.width * xPad, image.height * 1);
		image.endShape(P.CLOSE);
		
		// left wall
		image.beginShape();
		image.vertex(image.width * 0, image.height * 0.5f);
		image.vertex(image.width * xPad, image.height * 0);
		image.vertex(image.width * midX1, image.height * 0.5f);
		image.vertex(image.width * xPad, image.height * 1);
		image.endShape(P.CLOSE);
		
		// right wall
		image.beginShape();
		image.vertex(image.width * 1, image.height * 0.5f);
		image.vertex(image.width * (1 - xPad), image.height * 0);
		image.vertex(image.width * midX2, image.height * 0.5f);
		image.vertex(image.width * (1 - xPad), image.height * 1);
		image.endShape(P.CLOSE);
		
		image.endDraw();
	}

}

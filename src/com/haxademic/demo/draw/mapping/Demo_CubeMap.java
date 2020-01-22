package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

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
		textureQuadSubdivided(p.g, image, subdivisions, 
				-400, -200, 0,  xPad, 0,
				400, -200,  0,  1 - xPad, 0,
				400, 200,   0,  midX2, 0.5f,
				-400, 200,  0,  midX1, 0.5f);

		// draw floor
		textureQuadSubdivided(p.g, image, subdivisions, 
				-400, 200, 0,     midX1, 0.5f,
				400,  200, 0,     midX2, 0.5f,
				400,  200, 400,   1 - xPad, 1f,
				-400, 200, 400,   xPad, 1f);
		
		// draw left wall
		textureQuadSubdivided(p.g, image, subdivisions, 
				-400, -200, 400,  0, 0.5f,
				-400, -200, 0,    xPad, 0,
				-400, 200,  0,    midX1, 0.5f,
				-400, 200,  400,  xPad, 1);

		// draw right wall
		textureQuadSubdivided(p.g, image, subdivisions, 
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

	public static void textureQuadSubdivided(PGraphics pg, PImage texture, int subDivideSteps, 
			float x1, float y1, float z1, float u1, float v1,
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3,
			float x4, float y4, float z4, float u4, float v4
		) {
		
		pg.beginShape(P.QUAD);
		pg.textureMode(PConstants.NORMAL);
		pg.texture(texture);

		// subdivide quad for better resolution
		// vertices go top-left, clockwise to bottom left
		float stepsX = subDivideSteps;
		float segmentX = 1f / stepsX;
		float stepsY = subDivideSteps;
		float segmentY = 1f / stepsY;

		// draw a subdivided grid
		for( float x=0; x < stepsX; x++ ) {
			// calculate spread of mesh grid and uv coordinates
			float xNorm = x * segmentX;
			float xNormNext = (x+1) * segmentX;

			for( float y=0; y < stepsY; y++ ) {
				// calculate grid cells' uv coordinates
				float yNorm = y * segmentY;
				float yNormNext = (y+1) * segmentY;

				// calc grid positions based on interpolating columns between corners
				// we only need the xProgress for this, since we're slicing by column
				float colTopX = P.lerp(x1, x2, xNorm);
				float colTopY = P.lerp(y1, y2, xNorm);
				float colTopZ = P.lerp(z1, z2, xNorm);
				float colTopU = P.lerp(u1, u2, xNorm);
				float colTopV = P.lerp(v1, v2, xNorm);
				float colBotX = P.lerp(x4, x3, xNorm);
				float colBotY = P.lerp(y4, y3, xNorm);
				float colBotZ = P.lerp(z4, z3, xNorm);
				float colBotU = P.lerp(u4, u3, xNorm);
				float colBotV = P.lerp(v4, v3, xNorm);

				float nextColTopX = P.lerp(x1, x2, xNormNext);
				float nextColTopY = P.lerp(y1, y2, xNormNext);
				float nextColTopZ = P.lerp(z1, z2, xNormNext);
				float nextColTopU = P.lerp(u1, u2, xNormNext);
				float nextColTopV = P.lerp(v1, v2, xNormNext);
				float nextColBotX = P.lerp(x4, x3, xNormNext);
				float nextColBotY = P.lerp(y4, y3, xNormNext);
				float nextColBotZ = P.lerp(z4, z3, xNormNext);
				float nextColBotU = P.lerp(u4, u3, xNormNext);
				float nextColBotV = P.lerp(v4, v3, xNormNext);

				// calc quad coords
				float quadTopLeftX = P.lerp(colTopX, colBotX, yNorm);
				float quadTopLeftY = P.lerp(colTopY, colBotY, yNorm);
				float quadTopLeftZ = P.lerp(colTopZ, colBotZ, yNorm);
				float quadTopLeftU = P.lerp(colTopU, colBotU, yNorm);
				float quadTopLeftV = P.lerp(colTopV, colBotV, yNorm);
				float quadTopRightX = P.lerp(nextColTopX, nextColBotX, yNorm);
				float quadTopRightY = P.lerp(nextColTopY, nextColBotY, yNorm);
				float quadTopRightZ = P.lerp(nextColTopZ, nextColBotZ, yNorm);
				float quadTopRightU = P.lerp(nextColTopU, nextColBotU, yNorm);
				float quadTopRightV = P.lerp(nextColTopV, nextColBotV, yNorm);
				float quadBotRightX = P.lerp(nextColTopX, nextColBotX, yNormNext);
				float quadBotRightY = P.lerp(nextColTopY, nextColBotY, yNormNext);
				float quadBotRightZ = P.lerp(nextColTopZ, nextColBotZ, yNormNext);
				float quadBotRightU = P.lerp(nextColTopU, nextColBotU, yNormNext);
				float quadBotRightV = P.lerp(nextColTopV, nextColBotV, yNormNext);
				float quadBotLeftX = P.lerp(colTopX, colBotX, yNormNext);
				float quadBotLeftY = P.lerp(colTopY, colBotY, yNormNext);
				float quadBotLeftZ = P.lerp(colTopZ, colBotZ, yNormNext);
				float quadBotLeftU = P.lerp(colTopU, colBotU, yNormNext);
				float quadBotLeftV = P.lerp(colTopV, colBotV, yNormNext);

				// draw subdivided quads
				pg.vertex(quadTopLeftX, quadTopLeftY, quadTopLeftZ, quadTopLeftU, quadTopLeftV);
				pg.vertex(quadTopRightX, quadTopRightY, quadTopRightZ, quadTopRightU, quadTopRightV);
				pg.vertex(quadBotRightX, quadBotRightY, quadBotRightZ, quadBotRightU, quadBotRightV);
				pg.vertex(quadBotLeftX, quadBotLeftY, quadBotLeftZ, quadBotLeftU, quadBotLeftV);
			}
		}
		
		pg.endShape(P.CLOSE);
	}
	
}

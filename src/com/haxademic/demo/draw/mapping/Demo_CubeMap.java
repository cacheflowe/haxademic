package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PConstants;
import processing.core.PGraphics;

public class Demo_CubeMap
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics image;
	protected TextureShader curShader;

	protected void firstFrame()	{
		image = PG.newPG(1000, 500);
		curShader = new TextureShader(TextureShader.cacheflowe_down_void);
	}

	protected void drawApp() {
		// update image w/test pattern
		image.beginDraw();
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), image, true);
		image.endDraw();
		DebugView.setTexture("image", image);

		// overwrite with test pattern
		PG.drawTestPattern(image);

		// overwrite with shader
		curShader.setTimeMult(0.001f);
		curShader.updateTime();
		image.filter(curShader.shader());

		// prep context w/camera rotate
		p.background(0);
		p.noStroke();
		p.lights();
		PG.setCenterScreen(p);
		p.translate(0, -100, -500);
		PG.basicCameraFromMouse(p.g);
		p.pushMatrix();

		// draw back wall
		p.beginShape();
		p.textureMode(PConstants.NORMAL);
		p.texture(image);
//		textureQuadSubdivided(p.g, 10, 
//				-400, -200, 0,  0f, 0,
//				400, -200,  0,  1f, 0,
//				400, 200,   0,  0.8f, 0.5f,
//				-400, 200,  0,  0.2f, 0.5f);
		p.vertex(-400, -200, 0,  0f, 0);
		p.vertex(400, -200,  0,  1f, 0);
		p.vertex(400, 200,   0,  0.8f, 0.5f);
		p.vertex(-400, 200,  0,  0.2f, 0.5f);
		p.endShape();

		// draw floor
		p.beginShape();
		p.textureMode(PConstants.NORMAL);
		p.texture(image);
		p.vertex(-400, 200, 0,     0.2f, 0.5f);
		p.vertex(400,  200, 0,     0.8f, 0.5f);
		p.vertex(400,  200, 400,   1f, 1f);
		p.vertex(-400, 200, 400,   0f, 1f);
		p.endShape();

		// draw left wall
		p.beginShape();
		p.textureMode(PConstants.NORMAL);
		p.texture(image);
		p.vertex(-400, -200, 400,  0.0f, 0.5f);
		p.vertex(-400, -200, 0,    0f, 0f);
		p.vertex(-400, 200,  0,    0.2f, 0.5f);
		p.vertex(-400, 200,  400,  0f, 1f);
		p.endShape();


		// draw right wall
		p.beginShape();
		p.textureMode(PConstants.NORMAL);
		p.texture(image);
		p.vertex(400, -200, 400,  1.0f, 0.5f);
		p.vertex(400, -200, 0,    1f, 0f);
		p.vertex(400, 200,  0,    0.8f, 0.5f);
		p.vertex(400, 200,  400,  1f, 1f);
		p.endShape();

		// end
		p.popMatrix();

		// debug
		// p.image(image, 0, 0);
	}

	public static void textureQuadSubdivided(PGraphics pg, int subDivideSteps, 
			float x1, float y1, float z1, float u1, float v1,
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3,
			float x4, float y4, float z4, float u4, float v4
		) {
		// subdivide quad for better resolution
		// vertices go top-left, clockwise to bottom left
		float stepsX = subDivideSteps;
		float stepsY = subDivideSteps;
		float stepsZ = subDivideSteps;

		// draw a subdivided grid
		for( float x=0; x < stepsX; x++ ) {
			// calculate spread of mesh grid and uv coordinates
			float xPercent = x/stepsX;
			float xPercentNext = (x+1f)/stepsX;
//			if( xPercentNext > 1 ) xPercentNext = 1;
			float uPercent = xPercent; // P.map(xPercent, 0, 1, u1, v2);
			float uPercentNext = xPercentNext;

			for( float y=0; y < stepsY; y++ ) {
				// calculate spread of mesh grid and uv coordinates
				float yPercent = y/stepsY;
				float yPercentNext = (y+1f)/stepsY;
//				if( yPercentNext > 1 ) yPercentNext = 1;
				float vPercent = yPercent;
				float vPercentNext = yPercentNext;

				// calc grid positions based on interpolating columns between corners
				float colTopX = interp(x1, x2, xPercent);
				float colTopY = interp(y1, y2, xPercent);
				float colBotX = interp(x4, x3, xPercent);
				float colBotY = interp(y4, y3, xPercent);

				float nextColTopX = interp(x1, x2, xPercentNext);
				float nextColTopY = interp(y1, y2, xPercentNext);
				float nextColBotX = interp(x4, x3, xPercentNext);
				float nextColBotY = interp(y4, y3, xPercentNext);

				// calc quad coords
				float quadTopLeftX = interp(colTopX, colBotX, yPercent);
				float quadTopLeftY = interp(colTopY, colBotY, yPercent);
				float quadTopRightX = interp(nextColTopX, nextColBotX, yPercent);
				float quadTopRightY = interp(nextColTopY, nextColBotY, yPercent);
				float quadBotRightX = interp(nextColTopX, nextColBotX, yPercentNext);
				float quadBotRightY = interp(nextColTopY, nextColBotY, yPercentNext);
				float quadBotLeftX = interp(colTopX, colBotX, yPercentNext);
				float quadBotLeftY = interp(colTopY, colBotY, yPercentNext);

				// draw subdivided quads
				pg.vertex(quadTopLeftX, quadTopLeftY, 0, 	uPercent, 		vPercent);
				pg.vertex(quadTopRightX, quadTopRightY, 0, 	uPercentNext, 	vPercent);
				pg.vertex(quadBotRightX, quadBotRightY, 0, 	uPercentNext, 	vPercentNext);
				pg.vertex(quadBotLeftX, quadBotLeftY, 0, 	uPercent, 		vPercentNext);
			}
		}
	}
	
	public static float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}


}

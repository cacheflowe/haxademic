package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

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
		curShader.setTimeMult(0.003f);
		curShader.updateTime();
//		image.filter(curShader.shader());

		// prep context w/camera rotate
		p.background(0);
		p.noStroke();
		p.lights();
		PG.setCenterScreen(p);
		p.translate(0, -100, -500);
		PG.basicCameraFromMouse(p.g);
		p.pushMatrix();
		
		// split original image into 4 quads
		float midX1 = 0.2f;
		float midX2 = 1f - midX1;

		// draw back wall
		textureQuadSubdivided(p.g, image, 10, 
				-400, -200, 0,  0f, 0,
				400, -200,  0,  1, 0,
				400, 200,   0,  midX2, 0.5f,
				-400, 200,  0,  midX1, 0.5f);

		// draw floor
		textureQuadSubdivided(p.g, image, 10, 
				-400, 200, 0,     midX1, 0.5f,
				400,  200, 0,     midX2, 0.5f,
				400,  200, 400,   1f, 1f,
				-400, 200, 400,   0f, 1f);

		
		// draw left wall
		textureQuadSubdivided(p.g, image, 10, 
				-400, -200, 400,  0.0f, 0.5f,
				-400, -200, 0,    0f, 0f,
				-400, 200,  0,    midX1, 0.5f,
				-400, 200,  400,  0f, 1f);


		// draw right wall
		textureQuadSubdivided(p.g, image, 10, 
				400, -200, 400,  1.0f, 0.5f,
				400, -200, 0,    1f, 0f,
				400, 200,  0,    midX2, 0.5f,
				400, 200,  400,  1f, 1f);

		// end
		p.popMatrix();

		// debug
		// p.image(image, 0, 0);
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
		pg.endShape();

	}
	
}

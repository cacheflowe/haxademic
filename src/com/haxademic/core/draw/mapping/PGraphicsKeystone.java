package com.haxademic.core.draw.mapping;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class PGraphicsKeystone
extends BaseSavedQuadUI {

	protected PGraphics pg;
	protected float subDivideSteps;

	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps, String filePath ) {
		super(p.width, p.height, filePath);
		this.pg = pg;
		this.subDivideSteps = subDivideSteps;
	}
	
	public PGraphics pg() {
		return pg;
	}
	
	public void fillSolidColor( PGraphics canvas, int fill ) {
		// default single mapped quad
		canvas.noStroke();
		canvas.fill(fill);
		canvas.beginShape(PConstants.QUAD);
		canvas.vertex(topLeft.x, topLeft.y, 0);
		canvas.vertex(topRight.x, topRight.y, 0);
		canvas.vertex(bottomRight.x, bottomRight.y, 0);
		canvas.vertex(bottomLeft.x, bottomLeft.y, 0);
		canvas.endShape();
	}
		
	public void update( PGraphics canvas ) {
		update(canvas, true, pg);
	}
	
	public void update( PGraphics canvas, boolean subdivide ) {
		update(canvas, subdivide, pg);
	}
	
	public void update( PGraphics canvas, boolean subdivide, PImage texture ) {
		update(canvas, subdivide, texture, 0, 0, texture.width, texture.height);
	}
	
	public void update( PGraphics canvas, boolean subdivide, PImage texture, float mapX, float mapY, float mapW, float mapH) {
		// draw to screen with pinned corner coords
		// inspired by: https://github.com/davidbouchard/keystone & http://marcinignac.com/blog/projectedquads-source-code/
		canvas.textureMode(PConstants.IMAGE);
		canvas.noStroke();
		canvas.stroke(255);
		canvas.fill(255);
		canvas.beginShape(PConstants.QUAD);
		canvas.texture(texture);
		
		if( subdivide == true ) {
			// subdivide quad for better resolution
			float stepsX = subDivideSteps;
			float stepsY = subDivideSteps;

			for( float x=0; x < stepsX; x += 1f ) {
				float xPercent = x/stepsX;
				float xPercentNext = (x+1f)/stepsX;
				if( xPercentNext > 1 ) xPercentNext = 1;
				
				for( float y=0; y < stepsY; y += 1f ) {
					float yPercent = y/stepsY;
					float yPercentNext = (y+1f)/stepsY;
					if( yPercentNext > 1 ) yPercentNext = 1;

					// calc grid positions based on interpolating columns between corners
					float colTopX = interp(topLeft.x, topRight.x, xPercent);
					float colTopY = interp(topLeft.y, topRight.y, xPercent);
					float colBotX = interp(bottomLeft.x, bottomRight.x, xPercent);
					float colBotY = interp(bottomLeft.y, bottomRight.y, xPercent);
					
					float nextColTopX = interp(topLeft.x, topRight.x, xPercentNext);
					float nextColTopY = interp(topLeft.y, topRight.y, xPercentNext);
					float nextColBotX = interp(bottomLeft.x, bottomRight.x, xPercentNext);
					float nextColBotY = interp(bottomLeft.y, bottomRight.y, xPercentNext);
					
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
					canvas.vertex(quadTopLeftX, quadTopLeftY, 0, 	mapX + mapW * xPercent, 		mapY + mapH * yPercent);
					canvas.vertex(quadTopRightX, quadTopRightY, 0, 	mapX + mapW * xPercentNext, 	mapY + mapH * yPercent);
					canvas.vertex(quadBotRightX, quadBotRightY, 0, 	mapX + mapW * xPercentNext, 	mapY + mapH * yPercentNext);
					canvas.vertex(quadBotLeftX, quadBotLeftY, 0, 	mapX + mapW * xPercent, 		mapY + mapH * yPercentNext);
				}
			}
		} else {
			// default single mapped quad
			canvas.vertex(topLeft.x, topLeft.y, 0, 			mapX, mapY);
			canvas.vertex(topRight.x, topRight.y, 0, 		mapX + mapW, mapY);
			canvas.vertex(bottomRight.x, bottomRight.y, 0, 	mapX + mapW, mapY + mapH);
			canvas.vertex(bottomLeft.x, bottomLeft.y, 0, 	mapX, mapY + mapH);
		}

		canvas.endShape();
		
		// draw UI after mapping
		drawDebug(canvas, false);
	}
	
	protected float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}

	public void drawTestPattern() {
		pg.beginDraw();
		pg.noStroke();
		
		float spacingX = (float) pg.width / (float) subDivideSteps;
		float spacingY = (float) pg.height / (float) subDivideSteps;
		float spacing2x = spacingX * 2f;
		
		for( int x=0; x < subDivideSteps; x++) {
			for( int y=0; y < subDivideSteps; y++) {
				if( ( x % 2 == 0 && y % 2 == 1 ) || ( y % 2 == 0 && x % 2 == 1 ) ) {
					pg.fill(0, 160);
				} else {
					pg.fill(255, 160);
				}
				pg.rect(x * spacingX, y * spacingY, spacingX, spacingY);
			}
		}
		pg.endDraw();
	}

}

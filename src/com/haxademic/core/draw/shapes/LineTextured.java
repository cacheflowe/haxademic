package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class LineTextured {
	
	public static void draw(PGraphics pg, PImage texture, float xStart, float yStart, float xEnd, float yEnd, int color, float thickness, float texOffset) {
		// calc textured rectangle rotation * distance
		float startToEndAngle = MathUtil.getRadiansToTarget(xStart, yStart, xEnd, yEnd);
		float dist = MathUtil.getDistance(xStart, yStart, xEnd, yEnd);
		
		// set context
		OpenGLUtil.setTextureRepeat(pg);
		pg.push();
		pg.translate(xStart, yStart);
		pg.rotate(-startToEndAngle);
		
		// draw textured rect
		pg.noStroke();
		pg.beginShape(PShapeTypes.QUADS);
		pg.texture(texture);
		pg.textureMode(P.IMAGE);
		pg.tint(color);
		pg.vertex(0, -thickness/2, 0, texOffset, 0);
		pg.vertex(dist, -thickness/2, 0, texOffset + dist, 0);
		pg.vertex(dist, thickness/2, 0, texOffset + dist, texture.height);
		pg.vertex(0, thickness/2, 0, texOffset, texture.height);
		pg.endShape();
		pg.noTint();
		pg.pop();
	}
	
}

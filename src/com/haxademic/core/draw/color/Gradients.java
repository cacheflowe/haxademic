package com.haxademic.core.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Gradients {
	
	public static void linear( PApplet p, float width, float height, int colorStart, int colorStop ) {
		linear(p.g, width, height, colorStart, colorStop);
	}
	
	public static void linear( PGraphics p, float width, float height, int colorStart, int colorStop )
	{
		p.pushMatrix();
		
		float halfW = width/2;
		float halfH = height/2;
		
		p.beginShape();
		p.fill(colorStart);
		p.vertex(-halfW, -halfH);
		p.fill(colorStop);
		p.vertex(halfW, -halfH);
		p.fill(colorStop);
		p.vertex(halfW, halfH);
		p.fill(colorStart);
		p.vertex(-halfW, halfH);
		p.endShape(P.CLOSE);
		
		p.popMatrix();
	}
	
	public static void quad( PApplet p, float width, float height, int colorTL, int colorTR, int colorBR, int colorBL ) {
		quad(p.g, width, height, colorTL, colorTR, colorBR, colorBL);
	}
	public static void quad( PGraphics p, float width, float height, int colorTL, int colorTR, int colorBR, int colorBL ) {
		p.pushMatrix();
		
		float halfW = width/2;
		float halfH = height/2;
		
		p.beginShape();
		p.fill(colorTL);
		p.vertex(-halfW, -halfH);
		p.fill(colorTR);
		p.vertex(halfW, -halfH);
		p.fill(colorBR);
		p.vertex(halfW, halfH);
		p.fill(colorBL);
		p.vertex(-halfW, halfH);
		p.endShape(P.CLOSE);
		
		p.popMatrix();
	}
	
	public static void radial( PApplet p, float width, float height, int colorInner, int colorOuter, int numSegments ) {
		radial(p.g, width, height, colorInner, colorOuter, numSegments);
	}
	
	public static void radial( PGraphics p, float width, float height, int colorInner, int colorOuter, int numSegments ) {
		p.pushMatrix();
		p.noStroke();

		float halfW = width/2f;
		float halfH = height/2f;
		
		float segmentRadians = P.TWO_PI / (float) numSegments;
		for(float r=0; r < P.TWO_PI; r += segmentRadians) {
			float r2 = r + segmentRadians;
			p.beginShape();
			p.fill(colorInner);
			p.vertex(0,0);
			p.fill(colorOuter);
			p.vertex(P.cos(r) * halfW, P.sin(r) * halfH);
			p.vertex(P.cos(r2) * halfW, P.sin(r2) * halfH);
			p.endShape(P.CLOSE);
		}
		
		p.popMatrix();
	}
	
	public static PGraphics textureFromColorArray(int width, int height, int[] colors) {
		return textureFromColorArray(width, height, colors, false);
	}
	
	public static PGraphics textureFromColorArray(int width, int height, int[] colors, boolean loops) {
		// calculate sections
		int numColors = colors.length - 1;
		if(loops) numColors++; // repeat first color if looping
		int sectionW = width / numColors;
		
		// new buffer
		PGraphics pg = PG.newPG(width, height, true, true);
		PG.setTextureRepeat(pg, false);	// prevent wrapping blur bleed
		pg.beginDraw();
		pg.noStroke();
		pg.translate(sectionW/2, height/2);	// set to middle of first gradient section
		
		// draw gradient sections
		for (int i = 0; i <= colors.length; i++) {
			Gradients.linear(pg, sectionW, height, colors[i % colors.length], colors[(i+1) % colors.length]);
			pg.translate(sectionW, 0);
		}
		
		// blur it!
		for (int i = 0; i < 10; i++) {
			BlurProcessingFilter.instance(P.p).setBlurSize(20);
			BlurProcessingFilter.instance(P.p).setSigma(10);
			BlurProcessingFilter.instance(P.p).applyTo(pg);
		}
		
		// return it
		pg.endDraw();
		return pg;
	}
}

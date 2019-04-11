package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.draw.context.OrientationUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Shapes {
	
	public static void drawDisc3D( PGraphics p, float radius, float innerRadius, float cylinderHeight, int numSegments, int color, int wallcolor ) {
		// draw triangles
		p.beginShape(P.TRIANGLES);
		
		float segmentRads = P.TWO_PI / numSegments;
		float halfHeight = cylinderHeight / 2;
		
		for( int i = 0; i < numSegments; i++ )
		{
			if( color > 0 ) p.fill( color );
			
			// top disc
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, halfHeight );
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius, halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, halfHeight );
			
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, halfHeight );
			
			// bottom disc
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, -halfHeight );
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius, -halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, -halfHeight );
			
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, -halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, -halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, -halfHeight );
			
			if( wallcolor > 0 ) p.fill( wallcolor );
			// outer wall
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius, halfHeight );
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius, -halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, halfHeight );
			
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius, -halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, halfHeight );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, -halfHeight );
			
			// only draw inner radius if needed
			if( innerRadius > 0 )
			{
				if( wallcolor > 0 ) p.fill(wallcolor);
				// inner wall
				p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, halfHeight );
				p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, -halfHeight );
				p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, halfHeight );
				
				p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, -halfHeight );
				p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, halfHeight );
				p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, -halfHeight );
			}
		}
		
		p.endShape();
	}
	public static void drawDisc3D( PApplet p, float radius, float innerRadius, float cylinderHeight, int numSegments, int color, int wallcolor )
	{
		drawDisc3D(p.g, radius, innerRadius, cylinderHeight, numSegments, color, wallcolor);
	}

	public static void drawDisc( PApplet p, float radius, float innerRadius, int numSegments ) {
		drawDisc(p.g, radius, innerRadius, numSegments);
	}
	public static void drawDisc( PGraphics p, float radius, float innerRadius, int numSegments ) {
		p.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
		for( int i = 0; i < numSegments; i++ ) {
			p.beginShape(P.TRIANGLES);
			
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, 0 );
			p.vertex( P.cos( i * segmentRads ) * radius, P.sin( i * segmentRads ) * radius, 0 );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, 0 );
			
			p.vertex( P.cos( i * segmentRads ) * innerRadius, P.sin( i * segmentRads ) * innerRadius, 0 );
			p.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, 0 );
			p.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, 0 );
			p.endShape();
		}
		
		p.popMatrix();
	}
	
	public static void drawDiscAudio( PGraphics pg, float radius, float innerRadius, int numSegments, float ampH, boolean radial) {
		float segmentRads = P.TWO_PI / numSegments;
		pg.beginShape(P.TRIANGLES);
		for( int i = 0; i < numSegments; i++ ) {
			float amp = P.p.audioData.waveform()[i];
			float ampNext = P.p.audioData.waveform()[(i+1) % P.p.audioData.waveform().length];
			amp *= ampH;
			ampNext *= ampH;
			
			
			if(radial == false) {
				pg.vertex( P.cos( i * segmentRads ) * innerRadius,  P.sin( i * segmentRads ) * innerRadius, amp );
				pg.vertex( P.cos( i * segmentRads ) * radius, 		P.sin( i * segmentRads ) * radius, amp );
				pg.vertex( P.cos( (i + 1) * segmentRads ) * radius, P.sin( (i + 1) * segmentRads ) * radius, ampNext );
				
				pg.vertex( P.cos( i * segmentRads ) * innerRadius, 		 P.sin( i * segmentRads ) * innerRadius, amp );
				pg.vertex( P.cos( (i + 1) * segmentRads ) * innerRadius, P.sin( (i + 1) * segmentRads ) * innerRadius, ampNext );
				pg.vertex( P.cos( (i + 1) * segmentRads ) * radius, 	 P.sin( (i + 1) * segmentRads ) * radius, ampNext );
			} else {
				pg.vertex( P.cos( i * segmentRads ) * (innerRadius + amp),  P.sin( i * segmentRads ) * (innerRadius + amp), 0 );
				pg.vertex( P.cos( i * segmentRads ) * (radius + amp), 		P.sin( i * segmentRads ) * (radius + amp), 0 );
				pg.vertex( P.cos( (i + 1) * segmentRads ) * (radius + ampNext), P.sin( (i + 1) * segmentRads ) * (radius + ampNext), 0 );
				
				pg.vertex( P.cos( i * segmentRads ) * (innerRadius + amp), 		 P.sin( i * segmentRads ) * (innerRadius + amp), 0 );
				pg.vertex( P.cos( (i + 1) * segmentRads ) * (innerRadius + ampNext), P.sin( (i + 1) * segmentRads ) * (innerRadius + ampNext), 0 );
				pg.vertex( P.cos( (i + 1) * segmentRads ) * (radius + ampNext), 	 P.sin( (i + 1) * segmentRads ) * (radius + ampNext), 0 );
			}
		}
		pg.endShape();
	}
	
	public static void drawDiscTextured(PGraphics pg, float radius, float innerRadius, int numSegments, PImage texture) {
		pg.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
		for( int i = 0; i < numSegments; i++ ) {
			float curRads = i * segmentRads;
			float nextRads = (i+1) * segmentRads;
			float progress = curRads / P.TWO_PI;
			float progressNext = nextRads / P.TWO_PI;
			
			pg.beginShape(P.TRIANGLES);
			pg.textureMode(P.NORMAL);
			pg.texture(texture);
			
			pg.vertex( P.cos(curRads) * innerRadius, P.sin(curRads) * innerRadius, 0, progress, 1 );
			pg.vertex( P.cos(curRads) * radius, P.sin(curRads) * radius, 0, progress, 0 );
			pg.vertex( P.cos(nextRads) * radius, P.sin(nextRads) * radius, 0, progressNext, 0 );
			
			pg.vertex( P.cos(curRads) * innerRadius, P.sin(curRads) * innerRadius, 0, progress, 1 );
			pg.vertex( P.cos(nextRads) * innerRadius, P.sin(nextRads) * innerRadius, 0, progressNext, 1 );
			pg.vertex( P.cos(nextRads) * radius, P.sin(nextRads) * radius, 0, progressNext, 0 );
			pg.endShape();
		}
		
		pg.popMatrix();
	}
	
	public static void drawStar( PApplet p, float spikes, float outerrad, float innerradpercent, float h, float rot) {
		drawStar(p.g, spikes, outerrad, innerradpercent, h, rot);
	}
	
	public static void drawStar( PGraphics p, float spikes, float outerrad, float innerradpercent, float h, float rot)
	{
		p.pushMatrix();

		int pi;
		float futil;
		p.beginShape(P.TRIANGLE_STRIP);
		for(pi=0;pi<spikes+1;pi++)
		{
			p.vertex(0,0,h/2);
		    futil=(pi/spikes)  * P.TWO_PI;    //current angle 
		    p.vertex(  P.cos(futil+rot)*outerrad, P.sin(futil+rot)*outerrad, 0);
		    futil=futil+ (  (1/spikes)/2 *P.TWO_PI  );
		    p.vertex(  P.cos(futil+rot)*outerrad*innerradpercent, P.sin(futil+rot)*outerrad*innerradpercent, 0);
		}
		p.endShape();
		p.beginShape(P.TRIANGLE_STRIP);
		for(pi=0;pi<spikes+1;pi++)
		{
			p.vertex(0,0,-h/2);
			futil=(pi/spikes)  * P.TWO_PI;    //current angle 
			p.vertex(  P.cos(futil+rot)*outerrad, P.sin(futil+rot)*outerrad, 0);
			futil=futil+ (  (1/spikes)/2 *P.TWO_PI  );
			p.vertex(  P.cos(futil+rot)*outerrad*innerradpercent, P.sin(futil+rot)*outerrad*innerradpercent, 0);
		}
		p.endShape();
		
		p.popMatrix();
	}

		
	public static void drawPyramid( PApplet p, float shapeHeight, float baseWidth, boolean drawBase ){
		drawPyramid(p.g, shapeHeight, baseWidth, drawBase);
	}
	public static void drawPyramid( PGraphics p, float shapeHeight, float baseWidth, boolean drawBase ){
		baseWidth *= P.HALF_PI;
		
		p.pushMatrix();
		p.rotateZ(P.radians(-45.0f));
		p.beginShape(P.TRIANGLES);
		
		int numSides = 4;
		float segmentCircumference = (2f*P.PI) / numSides;
		float halfBaseW = baseWidth / 2f;

		for( int i = 0; i < numSides; i++ )
		{
			p.vertex( 0, 0, shapeHeight );
			p.vertex( P.sin( i * segmentCircumference ) * halfBaseW, P.cos( i * segmentCircumference ) * halfBaseW, 0 );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * halfBaseW, P.cos( (i + 1) * segmentCircumference ) * halfBaseW, 0 );
		}
		
		if( drawBase ) {
			// base
			p.vertex( P.sin( 0 * segmentCircumference ) * halfBaseW, P.cos( 0 * segmentCircumference ) * halfBaseW, 0 );
			p.vertex( P.sin( 1 * segmentCircumference ) * halfBaseW, P.cos( 1 * segmentCircumference ) * halfBaseW, 0 );
			p.vertex( P.sin( 2 * segmentCircumference ) * halfBaseW, P.cos( 2 * segmentCircumference ) * halfBaseW, 0 );
	
			p.vertex( P.sin( 2 * segmentCircumference ) * halfBaseW, P.cos( 2 * segmentCircumference ) * halfBaseW, 0 );
			p.vertex( P.sin( 3 * segmentCircumference ) * halfBaseW, P.cos( 3 * segmentCircumference ) * halfBaseW, 0 );
			p.vertex( P.sin( 0 * segmentCircumference ) * halfBaseW, P.cos( 0 * segmentCircumference ) * halfBaseW, 0 );
		}
		
		p.endShape();
		p.popMatrix();
	}
	
	// from: https://processing.org/discourse/beta/num_1256759256.html
	public static void drawSphereWithQuads(PGraphics pg, float size) {
		float radius = size;
		float rho = radius;
		float x, y, z;

		float phi = 0;
		int phiSteps = 20;
		float phiFactor = P.PI / phiSteps;

		float theta;
		int thetaSteps = 20;
		float thetaFactor = P.TWO_PI / thetaSteps;

		for(int p = 0; p < phiSteps; p++) {
			pg.beginShape(P.QUAD_STRIP);
			theta = 0.0f;
			for(int t = 0; t < thetaSteps + 1; t++) {
				x = rho * P.sin(phi) * P.cos(theta);
				z = rho * P.sin(phi) * P.sin(theta);
				y = -rho * P.cos(phi);

				pg.normal(x, y, z);
				pg.vertex(x, y, z);

				x = rho * P.sin(phi + phiFactor) * P.cos(theta);
				z = rho * P.sin(phi + phiFactor) * P.sin(theta);
				y = -rho * P.cos(phi + phiFactor);

				pg.normal(x, y, z);
				pg.vertex(x, y, z);

				theta += thetaFactor;
			}
			phi += phiFactor;
			pg.endShape(P.CLOSE);
		}
	}
	
	public static void drawTexturedCube(PGraphics pg, float size, PImage texture) {
		pg.beginShape(P.QUADS);
		pg.texture(texture);

		// BL, BR, TR, TL
		// front
		pg.vertex(-size,  size,  size, 		0, texture.height);
		pg.vertex( size,  size,  size, 		texture.width, texture.height);
		pg.vertex( size, -size,  size,		texture.width, 0);
		pg.vertex(-size, -size,  size,		0, 0);

		// back
		pg.vertex( size,  size, -size, 		0, texture.height);
		pg.vertex(-size,  size, -size, 		texture.width, texture.height);
		pg.vertex(-size, -size, -size,		texture.width, 0);
		pg.vertex( size, -size, -size,		0, 0);

		// left
		pg.vertex(-size,  size, -size, 		0, texture.height);
		pg.vertex(-size,  size,  size, 		texture.width, texture.height);
		pg.vertex(-size, -size,  size,		texture.width, 0);
		pg.vertex(-size, -size, -size,		0, 0);

		// right
		pg.vertex( size,  size,  size, 		0, texture.height);
		pg.vertex( size,  size, -size, 		texture.width, texture.height);
		pg.vertex( size, -size, -size,		texture.width, 0);
		pg.vertex( size, -size,  size,		0, 0);
		
		// floor
		pg.vertex(-size,  size, -size, 		0, 0);
		pg.vertex( size,  size, -size, 		texture.width, 0);
		pg.vertex( size,  size,  size,		texture.width, texture.height);
		pg.vertex(-size,  size,  size,		0, texture.height);

		// ceiling
		pg.vertex(-size, -size, -size, 		0, 0);
		pg.vertex( size, -size, -size, 		texture.width, 0);
		pg.vertex( size, -size,  size,		texture.width, texture.height);
		pg.vertex(-size, -size,  size,		0, texture.height);

		pg.endShape();
	}
	
	public static void drawTexturedCubeInside(PGraphics pg, float w, float h, float d, PImage texture1, PImage texture2, PImage texture3, PImage texture4, PImage floor, PImage ceiling) {
		// front - BR, BL, TL, TR
		pg.beginShape(P.QUAD);
		pg.texture(texture1);
		pg.vertex(-w,  h,  d, 		texture1.width, texture1.height);
		pg.vertex( w,  h,  d, 		0, texture1.height);
		pg.vertex( w, -h,  d,		0, 0);
		pg.vertex(-w, -h,  d,		texture1.width, 0);
		pg.endShape();

		// right
		pg.beginShape(P.QUAD);
		pg.texture(texture4);
		pg.vertex( w,  h,  d, 		texture4.width, texture4.height);
		pg.vertex( w,  h, -d, 		0, texture4.height);
		pg.vertex( w, -h, -d,		0, 0);
		pg.vertex( w, -h,  d,		texture4.width, 0);
		pg.endShape();

		// back
		pg.beginShape(P.QUAD);
		pg.texture(texture3);
		pg.vertex( w,  h, -d, 		texture3.width, texture3.height);
		pg.vertex(-w,  h, -d, 		0, texture3.height);
		pg.vertex(-w, -h, -d,		0, 0);
		pg.vertex( w, -h, -d,		texture3.width, 0);
		pg.endShape();

		// left
		pg.beginShape(P.QUAD);
		pg.texture(texture2);
		pg.vertex(-w,  h, -d, 		texture2.width, texture2.height);
		pg.vertex(-w,  h,  d, 		0, texture2.height);
		pg.vertex(-w, -h,  d,		0, 0);
		pg.vertex(-w, -h, -d,		texture2.width, 0);
		pg.endShape();

		// floor
		pg.beginShape(P.QUAD);
		pg.texture(floor);
		pg.vertex(-w,  h, -d, 		floor.width, floor.height);
		pg.vertex( w,  h, -d, 		0, floor.height);
		pg.vertex( w,  h,  d,		0, 0);
		pg.vertex(-w,  h,  d,		floor.width, 0);
		pg.endShape();

		// ceiling
		pg.beginShape(P.QUAD);
		pg.texture(ceiling);
		pg.vertex(-w, -h, -d, 		ceiling.width, ceiling.height);
		pg.vertex( w, -h, -d, 		0, ceiling.height);
		pg.vertex( w, -h,  d,		0, 0);
		pg.vertex(-w, -h,  d,		ceiling.width, 0);
		pg.endShape();
	}

	public static void drawTexturedRect(PGraphics pg, PImage texture) {
		pg.beginShape(P.QUAD);
		pg.textureMode(P.NORMAL);
		pg.texture(texture);
		pg.vertex(-texture.width/2, -texture.height/2, 			0, 0);
		pg.vertex( texture.width/2, -texture.height/2, 			1, 0);
		pg.vertex( texture.width/2,  texture.height/2, 			1, 1);
		pg.vertex(-texture.width/2,  texture.height/2, 			0, 1);
		pg.endShape();
	}

	public static PShape createCan(float radius, float height, int detail) {
		P.p.textureMode(P.NORMAL); 
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUAD_STRIP);
		sh.noStroke();
		for (int i = 0; i <= detail; i++) {
			float angle = P.TWO_PI / detail;
			float x = P.sin(i * angle);
			float z = P.cos(i * angle);
			float u = (float)i / detail;
			sh.normal(x, 0, z);
			sh.vertex(x * radius, -height/2, z * radius, u, 0);
			sh.vertex(x * radius, +height/2, z * radius, u, 1);
		}
		sh.endShape();
		P.p.textureMode(P.IMAGE); 	// reset 
		return sh;
	}
	
	public static PShape createStrip(float width, float height, int detail) {
		P.p.textureMode(P.NORMAL); 
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUAD_STRIP);
		sh.noStroke();
		float xStart = -width / 2f;
		float yStart = -height / 2f;
		float xSegment = width / (float) detail;
		for (int i = 0; i <= detail; i++) {
			float x = xStart + i * xSegment;
			float u = (float)i / (float)detail; // x progress
			sh.vertex(x, yStart, 		  0, u, 0);
			sh.vertex(x, yStart + height, 0, u, 1);
		}
		sh.endShape();
		P.p.textureMode(P.IMAGE); 	// reset 
		return sh;
	}
	
	public static PShape createBox(float size) {
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		
		// BL, BR, TR, TL
		// front
		sh.vertex(-size,  size,  size, 		0, 1);
		sh.vertex( size,  size,  size, 		1, 1);
		sh.vertex( size, -size,  size,		1, 0);
		sh.vertex(-size, -size,  size,		0, 0);

		// back
		sh.vertex( size,  size, -size, 		0, 1);
		sh.vertex(-size,  size, -size, 		1, 1);
		sh.vertex(-size, -size, -size,		1, 0);
		sh.vertex( size, -size, -size,		0, 0);

		// left
		sh.vertex(-size,  size, -size, 		0, 1);
		sh.vertex(-size,  size,  size, 		1, 1);
		sh.vertex(-size, -size,  size,		1, 0);
		sh.vertex(-size, -size, -size,		0, 0);

		// right
		sh.vertex( size,  size,  size, 		0, 1);
		sh.vertex( size,  size, -size, 		1, 1);
		sh.vertex( size, -size, -size,		1, 0);
		sh.vertex( size, -size,  size,		0, 0);
		
		// floor
		sh.vertex(-size,  size, -size, 		0, 0);
		sh.vertex( size,  size, -size, 		1, 0);
		sh.vertex( size,  size,  size,		1, 1);
		sh.vertex(-size,  size,  size,		0, 1);

		// ceiling
		sh.vertex(-size, -size, -size, 		0, 0);
		sh.vertex( size, -size, -size, 		1, 0);
		sh.vertex( size, -size,  size,		1, 1);
		sh.vertex(-size, -size,  size,		0, 1);

		sh.endShape();
		return sh;
	}
	
	public static PShape createBoxSingleUV(float size, float uvX, float uvY) {
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		sh.textureMode(P.NORMAL);
		
		// BL, BR, TR, TL
		// front
		sh.vertex(-size,  size,  size, 		uvX, uvY);
		sh.vertex( size,  size,  size, 		uvX, uvY);
		sh.vertex( size, -size,  size,		uvX, uvY);
		sh.vertex(-size, -size,  size,		uvX, uvY);
		
		// back
		sh.vertex( size,  size, -size, 		uvX, uvY);
		sh.vertex(-size,  size, -size, 		uvX, uvY);
		sh.vertex(-size, -size, -size,		uvX, uvY);
		sh.vertex( size, -size, -size,		uvX, uvY);
		
		// left
		sh.vertex(-size,  size, -size, 		uvX, uvY);
		sh.vertex(-size,  size,  size, 		uvX, uvY);
		sh.vertex(-size, -size,  size,		uvX, uvY);
		sh.vertex(-size, -size, -size,		uvX, uvY);
		
		// right
		sh.vertex( size,  size,  size, 		uvX, uvY);
		sh.vertex( size,  size, -size, 		uvX, uvY);
		sh.vertex( size, -size, -size,		uvX, uvY);
		sh.vertex( size, -size,  size,		uvX, uvY);
		
		// floor
		sh.vertex(-size,  size, -size, 		uvX, uvY);
		sh.vertex( size,  size, -size, 		uvX, uvY);
		sh.vertex( size,  size,  size,		uvX, uvY);
		sh.vertex(-size,  size,  size,		uvX, uvY);
		
		// ceiling
		sh.vertex(-size, -size, -size, 		uvX, uvY);
		sh.vertex( size, -size, -size, 		uvX, uvY);
		sh.vertex( size, -size,  size,		uvX, uvY);
		sh.vertex(-size, -size,  size,		uvX, uvY);
		
		sh.endShape();
		return sh;
	}
	
	public static PShape createRectSingleUV(float size, float uvX, float uvY) {
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		sh.textureMode(P.NORMAL);
		
		// BL, BR, TR, TL
		sh.vertex(-size,  size,  0, 		uvX, uvY);
		sh.vertex( size,  size,  0, 		uvX, uvY);
		sh.vertex( size, -size,  0,		uvX, uvY);
		sh.vertex(-size, -size,  0,		uvX, uvY);
		
		sh.endShape();
		return sh;
	}
	
	public static PShape createSheet(int detail, float width, float height) {
		P.p.textureMode(P.NORMAL); 
		// P.println("Shapes.createSheet() setting textureMode is weird to do here... Maybe should be PAppletHax default?");
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		sh.noStroke();
//		sh.texture(tex);
		float cellW = width / detail;
		float cellH = height / detail;
		// int numVertices = 0;
		for (int col = 0; col < detail; col++) {
			for (int row = 0; row < detail; row++) {
				float xU = col * cellW;
				float yV = row * cellH;
				float x = -width/2f + col * cellW;
				float y = -height/2f + row * cellH;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, P.map(xU, 0, width, 0, 1), P.map(yV, 0, height, 0, 1));
				sh.vertex(x, y + cellH, z, P.map(xU, 0, width, 0, 1), P.map(yV + cellH, 0, height, 0, 1));    
				sh.vertex(x + cellW, y + cellH, z, P.map(xU + cellW, 0, width, 0, 1), P.map(yV + cellH, 0, height, 0, 1));    
				sh.vertex(x + cellW, y, z, P.map(xU + cellW, 0, width, 0, 1), P.map(yV, 0, height, 0, 1));
				// numVertices += 4;
			}
		}
		// P.println("createSheet() vertices:", numVertices);
		sh.endShape(); 
		P.p.textureMode(P.IMAGE); 	// reset 
		return sh;
	}
	
	public static PShape createSheet(int cols, int rows, PImage tex) {
		P.p.textureMode(P.NORMAL); 
		// P.println("Shapes.createSheet() setting textureMode is weird to do here... Maybe should be PAppletHax default?");
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		sh.noStroke();
		sh.texture(tex);
		float cellW = (float) tex.width / (float) cols;
		float cellH = (float) tex.height / (float) rows;
		// int numVertices = 0;
		for (int col = 0; col < cols; col++) {
			for (int row = 0; row < rows; row++) {
				float xU = col * cellW;
				float yV = row * cellH;
				float x = -tex.width/2f + col * cellW;
				float y = -tex.height/2f + row * cellH;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, 					P.map(xU, 0, tex.width, 0, 1), 			P.map(yV, 0, tex.height, 0, 1));
				sh.vertex(x, y + cellH, z, 			P.map(xU, 0, tex.width, 0, 1), 			P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y + cellH, z, 	P.map(xU + cellW, 0, tex.width, 0, 1), 	P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y, z, 			P.map(xU + cellW, 0, tex.width, 0, 1), 	P.map(yV, 0, tex.height, 0, 1));
				// numVertices++;
			}
		}
		// P.println("createSheet() vertices:", numVertices);
		sh.endShape(); 
		P.p.textureMode(P.IMAGE); 	// reset 
		return sh;
	}
	
	public static PShape createSheet(int detail, PImage tex) {
		P.p.textureMode(P.NORMAL); 
		// P.println("Shapes.createSheet() setting textureMode is weird to do here... Maybe should be PAppletHax default?");
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		sh.noStroke();
		sh.texture(tex);
		float cellW = tex.width / detail;
		float cellH = tex.height / detail;
		// int numVertices = 0;
		for (int col = 0; col < detail; col++) {
			for (int row = 0; row < detail; row++) {
				float xU = col * cellW;
				float yV = row * cellH;
				float x = -tex.width/2f + col * cellW;
				float y = -tex.height/2f + row * cellH;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, P.map(xU, 0, tex.width, 0, 1), P.map(yV, 0, tex.height, 0, 1));
				sh.vertex(x, y + cellH, z, P.map(xU, 0, tex.width, 0, 1), P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y + cellH, z, P.map(xU + cellW, 0, tex.width, 0, 1), P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y, z, P.map(xU + cellW, 0, tex.width, 0, 1), P.map(yV, 0, tex.height, 0, 1));
				// numVertices++;
			}
		}
		// P.println("createSheet() vertices:", numVertices);
		sh.endShape(); 
		P.p.textureMode(P.IMAGE); 	// reset 
		return sh;
	}
	
	public static PShape createSheetPoints(int detail, float width, float height) {
		PShape sh = P.p.createShape();
		sh.beginShape(PConstants.POINTS);
		sh.stroke(255);
		sh.strokeWeight(1);
		sh.noFill();
		float cellW = width / detail;
		float cellH = height / detail;
		// int numVertices = 0;
		for (int col = 0; col < detail; col++) {
			for (int row = 0; row < detail; row++) {
				float xU = col * cellW;
				float yV = row * cellH;
				float x = -width/2f + col * cellW;
				float y = -height/2f + row * cellH;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, P.map(xU, 0, width, 0, 1), P.map(yV, 0, height, 0, 1));
				// numVertices += 1;
			}
		}
		// P.println("createSheet() vertices:", numVertices);
		sh.endShape(); 
		return sh;
	}
	
	public static void drawDashedCube(PGraphics pg, float cubeSize, float dashLength, boolean dashRounds) {
		drawDashedBox(pg, cubeSize, cubeSize, cubeSize, dashLength, dashRounds);
	}
	
	public static void drawDashedBox(PGraphics pg, float w, float h, float d, float dashLength, boolean dashRounds) {
		float halfW = w / 2f;
		float halfH = h / 2f;
		float halfD = d / 2f;

		// set stroke params
		pg.noFill();
//		pg.stroke(255);
//		pg.strokeWeight(2f);
		pg.strokeCap(P.ROUND);	// SQUARE, PROJECT, or ROUND

		// front face: top, right, bottom, left
		float frontFaceZ = halfD;
		drawDashedLine(pg, -halfW, -halfH, frontFaceZ, halfW, -halfH, frontFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, halfW, -halfH, frontFaceZ, halfW, halfH, frontFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, halfW, halfH, frontFaceZ, -halfW, halfH, frontFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, -halfW, halfH, frontFaceZ, -halfW, -halfH, frontFaceZ, dashLength, dashRounds);
		
		// back face: top, right, bottom, left
		float backFaceZ = -halfD;
		drawDashedLine(pg, -halfW, -halfH, backFaceZ, halfW, -halfH, backFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, halfW, -halfH, backFaceZ, halfW, halfH, backFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, halfW, halfH, backFaceZ, -halfW, halfH, backFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, -halfW, halfH, backFaceZ, -halfW, -halfH, backFaceZ, dashLength, dashRounds);
		
		// connect front & back faces, start at top left, clockwise, front to back
		drawDashedLine(pg, -halfW, -halfH, frontFaceZ, -halfW, -halfH, backFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, halfW, -halfH, frontFaceZ, halfW, -halfH, backFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, halfW, halfH, frontFaceZ, halfW, halfH, backFaceZ, dashLength, dashRounds);
		drawDashedLine(pg, -halfW, halfH, frontFaceZ, -halfW, halfH, backFaceZ, dashLength, dashRounds);
	}
		
	public static void drawDashedLine(PGraphics pg, float x1, float y1, float z1, float x2, float y2, float z2, float dashLength) {
		drawDashedLine(pg, x1, y1, z1, x2, y2, z2, dashLength, true);
	}
	
	public static void drawDashedLine(PGraphics pg, float x1, float y1, float z1, float x2, float y2, float z2, float dashLength, boolean rounds) {
		float lineLength = MathUtil.distance3d(x1, y1, z1, x2, y2, z2);
		float numDashes = (rounds) ? P.round(lineLength / dashLength) : lineLength / dashLength;
		float numSegments = (numDashes * 2) - 1;
		for (float i = 0; i < numSegments; i++) {
			if(i % 2 == 0) {
				float lineProgress = i / numSegments;
				float curX = P.lerp(x1, x2, lineProgress);
				float curY = P.lerp(y1, y2, lineProgress);
				float curZ = P.lerp(z1, z2, lineProgress);
				float nextProgress = (i + 1) / numSegments;
				if(nextProgress > 1) nextProgress = 1;
				float nextX = P.lerp(x1, x2, nextProgress);
				float nextY = P.lerp(y1, y2, nextProgress);
				float nextZ = P.lerp(z1, z2, nextProgress);
				pg.line(curX, curY, curZ, nextX, nextY, nextZ);
			}
		}
	}

	public static void drawCylinder(PGraphics pg, int sides, float r, float rBot, float h, boolean drawCaps) {
		float segemtnRadians = P.TWO_PI / (float) sides;
		float halfHeight = h / 2;
		
		if(drawCaps) {
			// draw top shape
			pg.beginShape();
			for (int i = 0; i < sides; i++) {
				float x = P.cos( segemtnRadians * i ) * r;
				float z = P.sin( segemtnRadians * i ) * r;
				pg.vertex( x, -halfHeight, z );    
			}
			pg.endShape();
			// draw bottom shape
			pg.beginShape();
			for (int i = 0; i < sides; i++) {
				float x = P.cos( segemtnRadians * i ) * rBot;
				float z = P.sin( segemtnRadians * i ) * rBot;
				pg.vertex( x, halfHeight, z );    
			}
			pg.endShape();
		}
		
		// draw body - smooth lighting, but has a seam where strip closes :(
//		pg.beginShape(PShapeTypes.TRIANGLE_STRIP);
//		for (int i = 0; i <= sides; i++) {
//			float curRads = segemtnRadians * i;
//		    float x = P.cos(curRads);
//		    float z = P.sin(curRads);
//		    pg.vertex( x * r, -halfHeight, z * r);    
//		    pg.vertex( x * rBot, halfHeight, z * rBot);
//		}
//		pg.endShape();  
		
		// draw body - individual quads - doesn't do smooth 
		pg.beginShape(PShapeTypes.QUADS);
		for (int i = 1; i <= sides; i++) {
			float curRads = segemtnRadians * i;
		    float x = P.cos(curRads);
		    float z = P.sin(curRads);
		    float lastRads = segemtnRadians * (i-1);
		    float lastx = P.cos(lastRads);
		    float lastz = P.sin(lastRads);
		    pg.vertex( lastx * r, -halfHeight, lastz * r);    
		    pg.vertex( x * r, -halfHeight, z * r);    
		    pg.vertex( x * rBot, halfHeight, z * rBot);
		    pg.vertex( lastx * rBot, halfHeight, lastz * rBot);
		}
		pg.endShape();  

	}

	/////////////////////////////////////
	// DRAW SHAPES BETWEEN 2 POINTS
	/////////////////////////////////////

	public static void boxBetween( PGraphics pg, PVector point1, PVector point2, float thickness ) {
		pg.pushMatrix();
			
		// set orientation 
		OrientationUtil.setMidPoint(pg, point1, point2);
		OrientationUtil.setRotationTowards(pg, point1, point2);

		// draw box
		pg.box( thickness, point1.dist(point2), thickness );

		pg.popMatrix(); 
	}
	
	public static void cylinderBetween( PGraphics pg, PVector point1, PVector point2, int resolution, float radius, float radiusBot ) {
		pg.pushMatrix();
			
		// set orientation 
		OrientationUtil.setMidPoint(pg, point1, point2);
		OrientationUtil.setRotationTowards(pg, point1, point2);

		// draw box
		Shapes.drawCylinder(pg, resolution, radius, radiusBot, point1.dist(point2), false);

		pg.popMatrix(); 
	}


}

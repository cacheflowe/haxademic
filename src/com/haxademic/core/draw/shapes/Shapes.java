package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

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
	
	public static void drawStar( PApplet p, float spikes, float outerrad, float innerradpercent, float h, float rot)
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
		float x, y, z, u, v;

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

		pg.vertex(-size,  size,  size, 		0, 0);
		pg.vertex( size,  size,  size, 		texture.width, 0);
		pg.vertex( size, -size,  size,		texture.width, texture.height);
		pg.vertex(-size, -size,  size,		0, texture.height);

		pg.vertex( size,  size,  size, 		0, 0);
		pg.vertex( size,  size, -size, 		texture.width, 0);
		pg.vertex( size, -size, -size,		texture.width, texture.height);
		pg.vertex( size, -size,  size,		0, texture.height);

		pg.vertex( size,  size, -size, 		0, 0);
		pg.vertex(-size,  size, -size, 		texture.width, 0);
		pg.vertex(-size, -size, -size,		texture.width, texture.height);
		pg.vertex( size, -size, -size,		0, texture.height);

		pg.vertex(-size,  size, -size, 		0, 0);
		pg.vertex(-size,  size,  size, 		texture.width, 0);
		pg.vertex(-size, -size,  size,		texture.width, texture.height);
		pg.vertex(-size, -size, -size,		0, texture.height);

		pg.vertex(-size,  size, -size, 		0, 0);
		pg.vertex( size,  size, -size, 		texture.width, 0);
		pg.vertex( size,  size,  size,		texture.width, texture.height);
		pg.vertex(-size,  size,  size,		0, texture.height);

		pg.vertex(-size, -size, -size, 		0, 0);
		pg.vertex( size, -size, -size, 		texture.width, 0);
		pg.vertex( size, -size,  size,		texture.width, texture.height);
		pg.vertex(-size, -size,  size,		0, texture.height);

		pg.endShape();
	}
	
	public static PShape createCan(float radius, float height, int detail) {
		//		textureMode(NORMAL);
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
		return sh;
	}
	
	public static PShape createSheet(int detail, PImage tex) {
		P.p.textureMode(P.NORMAL); 
		P.println("Shapes.createSheet() setting textureMode is weird... Maybe should be PAppletHax default?");
		PShape sh = P.p.createShape();
		sh.beginShape(P.QUADS);
		sh.noStroke();
		sh.texture(tex);
		float cellW = tex.width / detail;
		float cellH = tex.height / detail;
		int numVertices = 0;
		for (int col = 0; col < tex.width; col += cellW) {
			for (int row = 0; row < tex.height; row += cellH) {
				float xU = col;
				float yV = row;
				float x = -tex.width/2f + xU;
				float y = -tex.height/2f + yV;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, P.map(xU, 0, tex.width, 0, 1), P.map(yV, 0, tex.height, 0, 1));
				sh.vertex(x, y + cellH, z, P.map(xU, 0, tex.width, 0, 1), P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y + cellH, z, P.map(xU + cellW, 0, tex.width, 0, 1), P.map(yV + cellH, 0, tex.height, 0, 1));    
				sh.vertex(x + cellW, y, z, P.map(xU + cellW, 0, tex.width, 0, 1), P.map(yV, 0, tex.height, 0, 1));
				numVertices++;
			}
		}
		P.println("createSheet() vertices:", numVertices);
		sh.endShape(); 
		return sh;
	}
	
//	public static PShape createSphere(int detail, PImage tex) {
//		P.p.textureMode(P.NORMAL);
//		PShape sh = P.p.createShape();
//		sh.beginShape(P.SPHERE);
//		sh.stroke(255);
//		sh.noFill();
//		sh.endShape(); 
//		return sh;
//	}

}

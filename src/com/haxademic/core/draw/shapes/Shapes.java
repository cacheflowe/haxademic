package com.haxademic.core.draw.shapes;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class Shapes {
	
	public static void drawDisc3D( PApplet p, float radius, float innerRadius, float cylinderHeight, int numSegments, int color, int wallcolor )
	{
		// draw triangles
		p.beginShape(P.TRIANGLES);
		
		float segmentCircumference = (2f*P.PI) / numSegments;
		float halfHeight = cylinderHeight / 2;

		for( int i = 0; i < numSegments; i++ )
		{
			if( color > 0 ) p.fill( color );

			// top disc
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			// bottom disc
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			if( wallcolor > 0 ) p.fill( wallcolor );
			// outer wall
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			// only draw inner radius if needed
			if( innerRadius > 0 )
			{
				if( wallcolor > 0 ) p.fill(wallcolor);
				// inner wall
				p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
				p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
				p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				
				p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
				p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			}
		}
		
		p.endShape();
	}

	public static void drawDisc( PApplet p, float radius, float innerRadius, int numSegments )
	{
		p.pushMatrix();

		// draw triangles
		
		for( int i = 0; i < numSegments; i++ )
		{
			p.beginShape(P.TRIANGLES);
			float segmentCircumference = (2f*P.PI) / numSegments;
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius );
			p.vertex( P.sin( i * segmentCircumference ) * radius, P.cos( i * segmentCircumference ) * radius );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * radius, P.cos( (i + 1) * segmentCircumference ) * radius );
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

	public static void drawPyramid( PApplet p, float shapeHeight, int baseWidth, boolean drawBase )
	{
		baseWidth *= P.HALF_PI;
		
		p.pushMatrix();
		p.rotateZ(P.radians(-45.0f));
		p.beginShape(P.TRIANGLES);
		
		int numSides = 4;
		float segmentCircumference = (2f*P.PI) / numSides;
		float halfBaseW = baseWidth / 2;

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
}

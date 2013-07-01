package com.haxademic.core.draw.shapes;

import processing.core.PApplet;
import processing.core.PConstants;

import com.haxademic.core.app.P;

public class CacheFloweLogo {
	
	public static void drawCacheFloweLogo( PApplet p, float scale, float thickness, int c1, int c2 )
	{
		p.rectMode(PConstants.CORNER);

		// outer disc
		int outerDiscRadius = 29;
		int outerDiscStartRadius = 89;
		int centPrecision = 25;
		int discPrecision = 50;

		CacheFloweLogo.drawCent3D( p, scale, thickness, centPrecision, c1, c2 );
		Shapes.drawDisc3D( p, ( outerDiscStartRadius + outerDiscRadius ) * scale, outerDiscStartRadius * scale, thickness, discPrecision, c1, c2 );
	}
	
	public static void drawCent3D( PApplet p, float scale, float cylinderHeight, int numSegments, int color, int wallcolor )
	{
		p.pushMatrix();
		// draw triangles
		p.beginShape(P.TRIANGLES);
		
		// 87.5% around for "C"
		float segmentCircumference = ( 0.875f * (2f*P.PI) ) / numSegments;
		float halfHeight = cylinderHeight / 2;
		
		float knobCenterY = 65 * scale;
		float knobWidth = 28 * scale;
		float halfKnobWidth = 14 * scale;
		float outerRadius = 55 * scale;
		float innerRadius = 25 * scale;
		
		for( int j = 0; j < numSegments; j++ )
		{
			// start from magic rotation number
			float i = j + 8.911f;

			// set fill color
			p.fill( color );
			
			// top disc
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 	P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		P.cos( i * segmentCircumference ) * outerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 			P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, 	P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, 		P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			
			// bottom disc
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 	P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		P.cos( i * segmentCircumference ) * outerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 			P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, 	P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, 		P.cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
			
			
			// set fill color
			p.fill( wallcolor );
			
			// outer wall
			p.vertex( P.sin( i * segmentCircumference ) * outerRadius, P.cos( i * segmentCircumference ) * outerRadius, halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * outerRadius, P.cos( i * segmentCircumference ) * outerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * outerRadius, P.cos( i * segmentCircumference ) * outerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
			
			// inner wall
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			
			p.vertex( P.sin( i * segmentCircumference ) * innerRadius, P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			
			
			// handle disc ends' extra segment, from same inner radius point
			if( j == 0 || j == numSegments - 1 )
			{
				if( j == 0 )
				{
					// extra triangle at end
					p.fill( color );
					
					// top disc
					p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 		 P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
					p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		 P.cos( i * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					// bottom disc
					p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 		 P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
					p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		 P.cos( i * segmentCircumference ) * outerRadius, -halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );
					
					p.fill( wallcolor );
					
					// draw outer wall for extra triangle
					p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		 P.cos( i * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		 P.cos( i * segmentCircumference ) * outerRadius, -halfHeight );
					
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( i * segmentCircumference ) * outerRadius, 		 P.cos( i * segmentCircumference ) * outerRadius, -halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );

					// draw "C" end cap
					p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 		 P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );
					
					p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 		 P.cos( i * segmentCircumference ) * innerRadius, halfHeight );
					p.vertex( P.sin( i * segmentCircumference ) * innerRadius, 		 P.cos( i * segmentCircumference ) * innerRadius, -halfHeight );
					p.vertex( P.sin( (i - 1) * segmentCircumference ) * outerRadius, P.cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );
				}
				if( j == numSegments - 1 )
				{
					// extra triangle at end
					p.fill( color );
					
					// top disc
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, halfHeight );
					// bottom disc
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );

					p.fill( wallcolor );
					
					// draw outer wall for extra triangle
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * outerRadius, P.cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
					
					// draw "C" end cap
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
					p.vertex( P.sin( (i + 2) * segmentCircumference ) * outerRadius, P.cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					p.vertex( P.sin( (i + 1) * segmentCircumference ) * innerRadius, P.cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				}
			}			
		}
		
		p.endShape();
		

		// "C" top - move to center of block
		p.pushMatrix();
		p.translate( 0, -knobCenterY, 0 );
		
		// front and back panel
		p.fill( color );
		p.pushMatrix();
		p.translate( 0, 0, halfHeight );
		p.rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		p.translate( 0, 0, -cylinderHeight );
		p.rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		p.popMatrix();
		
		// side panels
		p.fill( wallcolor );

		p.pushMatrix();
		p.rotateY( P.radians(90.0f) );
		p.translate( 0, 0, -halfKnobWidth );
		p.rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		p.translate( 0, 0, knobWidth );
		p.rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		p.popMatrix();
		
		// top cap
		p.pushMatrix();
		p.rotateX( P.radians(90.0f) );
		p.translate( 0, 0, halfKnobWidth );
		p.rect( -halfKnobWidth, -halfHeight, knobWidth, cylinderHeight );
		p.popMatrix();

		p.popMatrix();
		
		
		// "C" bottom - move to center of block
		p.pushMatrix();
		p.translate( 0, knobCenterY, 0 );
		
		// front and back panel
		p.fill( color );
		p.pushMatrix();
		p.translate( 0, 0, halfHeight );
		p.rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		p.translate( 0, 0, -cylinderHeight );
		p.rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		p.popMatrix();
		
		// side panels
		p.fill( wallcolor );

		p.pushMatrix();
		p.rotateY( P.radians(90.0f) );
		p.translate( 0, 0, -halfKnobWidth );
		p.rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		p.translate( 0, 0, knobWidth );
		p.rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		p.popMatrix();
		
		// top cap
		p.pushMatrix();
		p.rotateX( P.radians(-90.0f) );
		p.translate( 0, 0, halfKnobWidth );
		p.rect( -halfKnobWidth, -halfHeight, knobWidth, cylinderHeight );
		p.popMatrix();

		p.popMatrix();

		p.popMatrix();
	}
	
}

package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PImage;

public class PixelTriFilter
extends BaseVideoFilter {
	
	protected float _pixelSize;
	PImage blobBufferImg;

	
	public PixelTriFilter( int width, int height, int pixelSize ) {
		super(width, height);
		
		_pixelSize = pixelSize;
	}
	
	public void setPixelSize( int pxSize ) {
		_pixelSize = pxSize;
	}
	
	public void update() {
		destBuffer.beginDraw();
		ImageUtil.clearPGraphics( destBuffer );
		destBuffer.clear();
		destBuffer.noStroke();
		
		float pixelSizeHalf = _pixelSize / 2f;
		int row = 0;
		
		sourceBuffer.loadPixels();
		
		for( int x=0; x <= sourceBuffer.width; x += _pixelSize ) {
			row = 0;
			for( int y=0; y < sourceBuffer.height; y += _pixelSize ) {
				// normalize pixel color grabbbing locations
				float centerX = x + pixelSizeHalf;
				float centerY = y + pixelSizeHalf;
				if( centerX > sourceBuffer.width - 2 ) centerX = sourceBuffer.width - 1;
				if( centerY > sourceBuffer.height - 2 ) centerY = sourceBuffer.height - 1;

				if( row % 2 == 0 ) {
					// get center color of triangle				
					destBuffer.beginShape(P.TRIANGLES);
					destBuffer.fill( ImageUtil.getPixelColor( sourceBuffer, (int) x, (int) centerY ) );
					destBuffer.vertex( x, y, 0 );
					destBuffer.vertex( x + pixelSizeHalf, y + _pixelSize, 0 );
					destBuffer.vertex( x - pixelSizeHalf, y + _pixelSize, 0 );
					destBuffer.endShape();
	
					destBuffer.beginShape(P.TRIANGLES);
					destBuffer.fill( ImageUtil.getPixelColor( sourceBuffer, (int) centerX, (int) centerY ) );
					destBuffer.vertex( x, y, 0 );
					destBuffer.vertex( x + _pixelSize, y, 0 );
					destBuffer.vertex( x + pixelSizeHalf, y + _pixelSize, 0 );
					destBuffer.endShape();
				} else {
					// invert every other row for triangle lineup :)	
					
					destBuffer.beginShape(P.TRIANGLES);
					destBuffer.fill( ImageUtil.getPixelColor( sourceBuffer, (int) x, (int) centerY ) );
					destBuffer.vertex( x, y + _pixelSize, 0 );
					destBuffer.vertex( x + pixelSizeHalf, y, 0 );
					destBuffer.vertex( x - pixelSizeHalf, y, 0 );
					destBuffer.endShape();
	
					destBuffer.beginShape(P.TRIANGLES);
					destBuffer.fill( ImageUtil.getPixelColor( sourceBuffer, (int) centerX, (int) centerY ) );
					destBuffer.vertex( x, y + _pixelSize, 0 );
					destBuffer.vertex( x + _pixelSize, y + _pixelSize, 0 );
					destBuffer.vertex( x + pixelSizeHalf, y, 0 );
					destBuffer.endShape();
				}
				
				row++;
			}
		}
		destBuffer.endDraw();
	}
}

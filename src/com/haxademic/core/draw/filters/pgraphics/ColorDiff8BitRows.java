package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.ImageUtil;

public class ColorDiff8BitRows
extends BaseVideoFilter {
	
	protected int rowSize;
	protected boolean isVertical;
	
	public ColorDiff8BitRows( int width, int height, int rowSize ) {
		super(width, height);
		
		this.rowSize = rowSize;
		isVertical = false;
	}
	
	public void setPixelSize( int pxSize ) {
		rowSize = pxSize;
	}
	
	public void setIsVertical( boolean isVertical ) {
		this.isVertical = isVertical;
	}
	
	public void update() {
		sourceBuffer.loadPixels();

		destBuffer.beginDraw();
		ImageUtil.clearPGraphics( destBuffer );
		destBuffer.noStroke();
		
		if( isVertical == true ) {
			drawVertical();
		} else {
			drawHorizontal();
		}

		destBuffer.endDraw();
	}
	
	protected void drawHorizontal() {	
		for( int y=0; y < sourceBuffer.height; y += rowSize ) {
			int lastDrawnX = 0;
			int color = ImageUtil.getPixelColor( sourceBuffer, 0, y );
			for( int x=0; x < sourceBuffer.width; x += rowSize ) {
				int checkColor = ImageUtil.getPixelColor( sourceBuffer, x, y );
				if( ImageUtil.brightnessDifference( P.p, color, checkColor ) > 0.05f ){
					destBuffer.fill(color);
					destBuffer.rect( lastDrawnX, y, x - lastDrawnX, rowSize );
					color = checkColor;
					lastDrawnX = x;
				}
			}
		}
	}
		
	protected void drawVertical() {
		for( int x=0; x < sourceBuffer.width; x += rowSize ) {
			int lastDrawnY = 0;
			int color = ImageUtil.getPixelColor( sourceBuffer, x, 0 );
			for( int y=0; y < sourceBuffer.height; y += rowSize ) {
				int checkColor = ImageUtil.getPixelColor( sourceBuffer, x, y );
				if( ImageUtil.brightnessDifference( P.p, color, checkColor ) > 0.1f ){
					destBuffer.fill(color);
					destBuffer.rect( x, lastDrawnY, rowSize, y - lastDrawnY );
					color = checkColor;
					lastDrawnY = y;
				}
			}
		}
	}
}

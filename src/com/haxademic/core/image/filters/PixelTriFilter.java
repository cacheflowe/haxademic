package com.haxademic.core.image.filters;

import processing.core.PGraphics;
import processing.core.PImage;
import blobDetection.BlobDetection;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.ImageUtil;

public class PixelTriFilter {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected int _pixelSize;
	protected PGraphics _pg;
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	
	public PixelTriFilter( int width, int height, int pixelSize ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		_pixelSize = pixelSize;
		_pg = p.createGraphics( _width, _height, P.P3D );
	}
	
	public PImage updateWithPImage( PImage source ) {
		drawPixels( source );
		return _pg;
	}
	
	protected void drawPixels( PImage source ) {
		ImageUtil.clearPGraphics( _pg );
		_pg.noStroke();
		// _pg.smooth();
		
		int pixelSizeHalf = _pixelSize / 2;
		
		for( int x=0; x < source.width; x += _pixelSize ) {
			for( int y=0; y < source.height; y += _pixelSize ) {
				// get center color of triangle
				_pg.beginDraw();
				
				_pg.fill( ImageUtil.getPixelColor( source, x, y + pixelSizeHalf ) );
				_pg.beginShape(P.TRIANGLES);
				_pg.vertex( x, y, 0 );
				_pg.vertex( x + pixelSizeHalf, y + _pixelSize, 0 );
				_pg.vertex( x - pixelSizeHalf, y + _pixelSize, 0 );
				_pg.endShape();

				_pg.fill( ImageUtil.getPixelColor( source, x + pixelSizeHalf, y + pixelSizeHalf ) );
				_pg.beginShape(P.TRIANGLES);
				_pg.vertex( x, y, 0 );
				_pg.vertex( x + _pixelSize, y, 0 );
				_pg.vertex( x + pixelSizeHalf, y + _pixelSize, 0 );
				_pg.endShape();

				_pg.endDraw();
			}
		}
	}
}

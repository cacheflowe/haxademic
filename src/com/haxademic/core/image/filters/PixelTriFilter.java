package com.haxademic.core.image.filters;

import processing.core.PGraphics;
import processing.core.PImage;
import blobDetection.BlobDetection;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.ImageUtil;

public class PixelTriFilter {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected float _pixelSize;
	protected PGraphics _pg;
	protected PImage _image;
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	
	public PixelTriFilter( int width, int height, int pixelSize ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		_pixelSize = pixelSize;
		_image = p.createImage( _width, _height, P.ARGB );
		_pg = p.createGraphics( _width, _height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	public void setPixelSize( int pxSize ) {
		_pixelSize = pxSize;
	}
	
	public PImage pg() {
		return _pg;
	}
	
	public PImage updateWithPImage( PImage source ) {
		drawPixels( source );
		return _image;
	}
	
	protected void drawPixels( PImage source ) {
		_pg.beginDraw();
		ImageUtil.clearPGraphics( _pg );
		_pg.clear();
		_pg.noStroke();
		
		float pixelSizeHalf = _pixelSize / 2f;
		int row = 0;
		
		for( int x=0; x <= source.width; x += _pixelSize ) {
			row = 0;
			for( int y=0; y < source.height; y += _pixelSize ) {
				// normalize pixel color grabbbing locations
				float centerX = x + pixelSizeHalf;
				float centerY = y + pixelSizeHalf;
				if( centerX > source.width - 2 ) centerX = source.width - 1;
				if( centerY > source.height - 2 ) centerY = source.height - 1;

				if( row % 2 == 0 ) {
					// get center color of triangle				
					_pg.beginShape(P.TRIANGLES);
					_pg.fill( ImageUtil.getPixelColor( source, (int) x, (int) centerY ) );
					_pg.vertex( x, y, 0 );
//					_pg.fill( 0.99f * ImageUtil.getPixelColor( source, (int) x, (int) centerY ) );
					_pg.vertex( x + pixelSizeHalf, y + _pixelSize, 0 );
					_pg.vertex( x - pixelSizeHalf, y + _pixelSize, 0 );
					_pg.endShape();
	
					_pg.beginShape(P.TRIANGLES);
					_pg.fill( ImageUtil.getPixelColor( source, (int) centerX, (int) centerY ) );
					_pg.vertex( x, y, 0 );
//					_pg.fill( 0.99f * ImageUtil.getPixelColor( source, (int) centerX, (int) centerY ) );
					_pg.vertex( x + _pixelSize, y, 0 );
					_pg.vertex( x + pixelSizeHalf, y + _pixelSize, 0 );
					_pg.endShape();
				} else {
					// invert every other row for triangle lineup :)	
					
					_pg.beginShape(P.TRIANGLES);
					_pg.fill( ImageUtil.getPixelColor( source, (int) x, (int) centerY ) );
					_pg.vertex( x, y + _pixelSize, 0 );
//					_pg.fill( 0.99f * ImageUtil.getPixelColor( source, (int) x, (int) centerY ) );
					_pg.vertex( x + pixelSizeHalf, y, 0 );
					_pg.vertex( x - pixelSizeHalf, y, 0 );
					_pg.endShape();
	
					_pg.beginShape(P.TRIANGLES);
					_pg.fill( ImageUtil.getPixelColor( source, (int) centerX, (int) centerY ) );
					_pg.vertex( x, y + _pixelSize, 0 );
					_pg.vertex( x + _pixelSize, y + _pixelSize, 0 );
//					_pg.fill( 0.99f * ImageUtil.getPixelColor( source, (int) centerX, (int) centerY ) );
					_pg.vertex( x + pixelSizeHalf, y, 0 );
					_pg.endShape();
				}
				
				row++;
			}
		}
		_pg.endDraw();
		
		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
	}
}

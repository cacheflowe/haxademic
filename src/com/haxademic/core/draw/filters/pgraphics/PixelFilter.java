package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;

import blobDetection.BlobDetection;
import processing.core.PGraphics;
import processing.core.PImage;

public class PixelFilter {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected int _pixelSize;
	protected PGraphics _pg;
	protected PImage _image;
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	
	public PixelFilter( int width, int height, int pixelSize ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		_pixelSize = pixelSize;
		_pg = p.createGraphics( _width, _height );
		_image = p.createImage( _width, _height, P.ARGB );
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
		_pg.noStroke();
		_pg.fill(0,0);
		
		for( int x=0; x < source.width; x += _pixelSize ) {
			for( int y=0; y < source.height; y += _pixelSize ) {
				int color = ImageUtil.getPixelColor( source, x, y );
				_pg.fill(color);
				_pg.rect( x, y, _pixelSize, _pixelSize );
			}
		}
		_pg.endDraw();
		
		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
	}
}

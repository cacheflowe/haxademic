package com.haxademic.core.image.filters;

import processing.core.PGraphics;
import processing.core.PImage;
import blobDetection.BlobDetection;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.ImageUtil;

public class ReflectionFilter {
	
	protected PAppletHax p;
	protected int _width;
	protected int _height;
	protected PGraphics _pg;
	protected PImage _image;
	BlobDetection theBlobDetection;
	PImage blobBufferImg;

	
	public ReflectionFilter( int width, int height ) {
		p = (PAppletHax) P.p;
		_width = width;
		_height = height;
		_pg = p.createGraphics( _width, _height, P.P3D );
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
				
		_pg.copy(source, 0, 0, source.width, source.height, 0, 0, source.width, source.height );
		_pg.copy( ImageUtil.getReversePImageFast( source ), source.width / 2, 0, source.width / 2, source.height, source.width / 2, 0, source.width / 2, source.height );
		_pg.endDraw();
		
		_image.copy( _pg, 0, 0, _width, _height, 0, 0, _width, _height );
	}
}

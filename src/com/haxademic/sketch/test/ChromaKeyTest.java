package com.haxademic.sketch.test;

import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class ChromaKeyTest
extends PAppletHax  
{
	PImage _image;
	PImage _dest;
	float threshRange = 20f;
	
	public void setup() {
		super.setup();
		_image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/green-screen-2.png" );
		_dest = new PImage( _image.width, _image.height, P.ARGB );
//		_dest.copy( _image, 0, 0, _image.width, _image.height, 0, 0, _image.width, _image.height );
	}

	public void drawApp() {
		chromaImage( _image, _dest );
		p.background(255,0,0);
		p.image(_dest, 0, 0);
	}
	
	public void chromaImage( PImage sourceImg, PImage dest ) {
		float r = 0;
		float g = 0;
		float b = 0;
		for( int x=0; x < sourceImg.width; x++ ){
			for( int y=0; y < sourceImg.height; y++ ){
				int pixelColor = ImageUtil.getPixelColor( sourceImg, x, y );
				// float pixelBrightness = p.brightness( pixelColor );
				
				r = ColorHax.redFromColorInt( pixelColor );
				g = ColorHax.greenFromColorInt( pixelColor );
				b = ColorHax.blueFromColorInt( pixelColor );
				
				// if green is greater than both other color components, black it out
				if( g > r && g > b ) {
					dest.set( x, y, p.color( 255, 0 ) );
				} else if( g > r - threshRange && g > b - threshRange ) {
					dest.set( x, y, p.color( r, g, b ) );
				} 
				else {
					_dest.set( x, y, p.color( r, g, b ) );
				}
			}
		}
	}
}


package com.haxademic.sketch.text;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.text.CustomFontText2D;

public class Text2DTTF
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	CustomFontText2D _fontRenderer;

	public void setup()	{
		super.setup();
//		size(displayWidth,displayHeight, P.P3D);
		_fontRenderer = new CustomFontText2D( this, "../data/fonts/bitlow.ttf", 70.0f, color(0,255,0), CustomFontText2D.ALIGN_CENTER, 450, 100 );
	}

//	public int sketchWidth() {
//		return displayWidth;
//	}
//
//	public int sketchHeight() {
//		return displayHeight;
//	}
//
//	public String sketchRenderer() {
//		return P3D; 
//	}

//	public boolean sketchFullScreen() {
//		return true;
//	}

	public void draw() {
		background(0);
		translate(mouseX, height/2, 0); 
		_fontRenderer.updateText( frameCount+"" );
		image( _fontRenderer.getTextPImage(), 0, 0 );
	}


}

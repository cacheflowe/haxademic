package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.text.CustomFontText2D;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.system.FileUtil;


public class ScoreDrawTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected CustomFontText2D _scoreFontRenderer;
	
	public void setup() {
		super.setup();	
		
		String fontFile = FileUtil.getHaxademicDataPath() + "fonts/GothamBold.ttf";
		_scoreFontRenderer = new CustomFontText2D( p, fontFile, 36, ColorUtil.colorFromHex("#ff00ff"), CustomFontText2D.ALIGN_CENTER, 80, 80 );

	}

	public void drawApp() {
		p.background(255);
		DrawUtil.setDrawCenter( p );
		p.pushMatrix();
		
		if( p.frameCount % 5 == 0 ) {
			_scoreFontRenderer.updateText(""+p.frameCount);
		}

		p.translate( 71f, 50 );
		p.noStroke();
		
		// draw shadow
		p.fill(0, 25);
		p.ellipse( -6f, 11f, 80f, 80f );
		
		// draw large bg
		p.fill( p.color(55,100,200) );
		p.ellipse( 0, 0, 80f, 80f );
		
		// draw small shadow
		p.fill(0, 25);
		p.ellipse( 0, 3f, 60f, 60f );
		
		// draw small shadow
		p.fill(255);
		p.ellipse( 0, 0, 60f, 60f );
		
		// draw text 
		p.image(_scoreFontRenderer.getTextPImage(), 0, 0); // , _scoreFontRenderer.getTextPImage().width, _scoreFontRenderer.getTextPImage().height
		
		p.popMatrix();

	}
}

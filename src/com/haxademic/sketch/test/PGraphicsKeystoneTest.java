package com.haxademic.sketch.test;

import processing.core.PApplet;
import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.PGraphicsKeystone;
import com.haxademic.core.draw.util.OpenGLUtil;

@SuppressWarnings("serial")
public class PGraphicsKeystoneTest
extends PAppletHax {

	protected PGraphics pg;
	protected PGraphicsKeystone _pgPinnable;

	public static void main(String args[]) {
		_isFullScreen = true;
		_hasChrome = false;
		PApplet.main(new String[] { PGraphicsKeystoneTest.class.getName() });
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "60" );
		_appConfig.setProperty( "width", "432" );
		_appConfig.setProperty( "height", "927" );
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "fullscreen", "false" );
	}

	public void setup() {
		super.setup();	
		buildCanvas();
		drawTestPattern();
	}

	protected void buildCanvas() {
		pg = p.createGraphics( p.width, p.height, P.OPENGL );
		pg.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		_pgPinnable = new PGraphicsKeystone( p, pg, 12 );
	}

	protected void drawTestPattern() {
		// redraw pgraphics grid
		pg.beginDraw();
		pg.clear();
		pg.noStroke();
		for( int x=0; x < pg.width; x+= 50) {
			for( int y=0; y < pg.height; y+= 50) {
				if( ( x % 100 == 0 && y % 100 == 0 ) || ( x % 100 == 50 && y % 100 == 50 ) ) {
					pg.fill(0);
				} else {
					pg.fill(255);
				}
				pg.rect(x,y,50,50);
			}
		}
		pg.endDraw();
	}

	public void drawApp() {
		p.background(0);
		// draw pinned pgraphics
		_pgPinnable.update(p.g, true);
	}

}

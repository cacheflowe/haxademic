package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.PGraphicsKeystone;
import com.haxademic.core.draw.util.OpenGLUtil;

import processing.core.PGraphics;

public class PGraphicsKeystoneTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;
	protected PGraphicsKeystone _pgPinnable;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1432" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "927" );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, "false" );
	}

	public void setup() {
		super.setup();	
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_MEDIUM);
		_pgPinnable = new PGraphicsKeystone( p, _pg, 12 );
	}

	protected void drawTestPattern( PGraphics pg ) {
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
		drawTestPattern( _pg );
		_pgPinnable.update(p.g, true);
	}

}

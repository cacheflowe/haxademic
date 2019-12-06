package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;

public class RotateRectVertices
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected TiledTexture tiledBg;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setupFirstFrame() {

	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
	
		p.translate(p.width/2, p.height/2);
		
		float rot = (P.floor(p.frameCount / 30f) % 2 == 0) ? 0 : 0.2f * P.sin(p.frameCount * 0.03f);
		float size = 1f + 0.2f * P.sin(p.frameCount * 0.03f);
//		rot = p.frameCount * 0.1f;
		drawRect(p.g, 200, 100, size, rot);
	}	
	
	public void drawRect(PGraphics pg, float drawW, float drawH, float size, float rotation) {
		PG.setTextureRepeat(pg, true);
		float halfDrawW = drawW / 2f;
		float halfDrawH = drawH / 2f;
		float halfSizeX = halfDrawW * size;
		float halfSizeY = halfDrawH * size;
				
		
		pg.noFill();
		pg.stroke(255,0,0);
		pg.beginShape();
		if(rotation == 0) {
			pg.vertex(-halfSizeX, -halfSizeY);
			pg.vertex(halfSizeX, -halfSizeY);
			pg.vertex(halfSizeX, halfSizeY);
			pg.vertex(-halfSizeX, halfSizeY);
			pg.vertex(-halfSizeX, -halfSizeY);
		} else {
			float curRot = rotation;
			float radius = MathUtil.getDistance(0, 0, halfSizeX, halfSizeY);
			float tlRads = -MathUtil.getRadiansToTarget(-halfSizeX, -halfSizeY, 0, 0) + curRot;
			float trRads = -MathUtil.getRadiansToTarget(halfSizeX, -halfSizeY, 0, 0) + curRot;
			float brRads = -MathUtil.getRadiansToTarget(halfSizeX, halfSizeY, 0, 0) + curRot;
			float blRads = -MathUtil.getRadiansToTarget(-halfSizeX, halfSizeY, 0, 0) + curRot;
			pg.vertex(radius * P.cos(tlRads), -radius * P.sin(tlRads));
			pg.vertex(radius * P.cos(trRads), -radius * P.sin(trRads));
			pg.vertex(radius * P.cos(brRads), -radius * P.sin(brRads));
			pg.vertex(radius * P.cos(blRads), -radius * P.sin(blRads));
			pg.vertex(radius * P.cos(tlRads), -radius * P.sin(tlRads));
		}
		pg.endShape();
	}

}

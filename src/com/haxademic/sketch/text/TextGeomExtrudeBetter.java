package com.haxademic.sketch.text;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.text.TextToPShape;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class TextGeomExtrudeBetter
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape word2d;
	protected PShape word3d;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 840 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
	}

	public void setup()	{
		super.setup();
		smooth(8);
		textToPShape = new TextToPShape();
		String fontFile = FileUtil.getFile("fonts/CenturyGothic.ttf");
		word2d = textToPShape.stringToShape2d("CACHEFLOWE", fontFile);
		word3d = textToPShape.stringToShape3d("CACHEFLOWE", 100, fontFile);
		PShapeUtil.scaleObjToExtent(word3d, 800);
		PShapeUtil.scaleObjToExtent(word2d, 800);
	}

	public void draw() {
		DrawUtil.setBetterLights(p);
		background(0);
		translate(width/2,height/3,-600);
		rotateX(P.map(p.mouseY, 0, p.height, -1f, 1f));
		rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));

		// draw word
		word3d.disableStyle();
		p.fill(255);
		p.shape(word3d);
		
		// draw word
		translate(0,height/3,0);
		word3d.disableStyle();
		p.fill(255);
		p.shape(word2d);
	}

}

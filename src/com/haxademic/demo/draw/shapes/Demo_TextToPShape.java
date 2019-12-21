package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class Demo_TextToPShape
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextToPShape textToPShape;
	protected PShape word2d;
	protected PShape word3d;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1040 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setup()	{

		textToPShape = new TextToPShape(TextToPShape.QUALITY_HIGH);
		String fontFile = FileUtil.getFile("fonts/HelloDenverDisplay-Regular.ttf");
		word2d = textToPShape.stringToShape2d("CACHEFLOWE", fontFile);
		word3d = textToPShape.stringToShape3d("CACHEFLOWE", 100, fontFile);
		PShapeUtil.scaleShapeToExtent(word3d, 800);
		PShapeUtil.scaleShapeToExtent(word2d, 800);
	}

	public void drawApp() {
		PG.setBetterLights(p);
		background(0);
		translate(width/2,height/4,-600);
		rotateX(P.map(p.mouseY, 0, p.height, -1f, 1f));
		rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));

		// draw word
		word3d.disableStyle();
		p.fill(255);
		p.shape(word3d);
		
		// draw word
		translate(0,height/2,0);
		word3d.disableStyle();
		p.fill(255);
		p.shape(word2d);
	}

}

package com.haxademic.sketch.particle;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.draw.util.DrawUtil;

public class ImageAlphaTest
extends PAppletHax  
{	
	protected PGraphics _texture;
	protected MeshPool _objPool;
	protected PShape _shape;
	protected PImage _image;
	
	protected int animCount = 0;
	
	public void setup() {
		super.setup();
		
		_image = p.loadImage("../data/images/Ello.Black.png");
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}
		
	public void drawApp() {
		p.background(255);
		p.noStroke();
		p.noFill();
		
		DrawUtil.setColorForPImage(p);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenter( p );
		DrawUtil.setBasicLights( p );
		
		if(p.mouseX < p.width/2f) {
			
			// draw image after rect - alpha is fine
			p.pushMatrix();
			p.fill(11, 149, 14);
			p.translate(0, 100, -400);
			p.rotateX(P.PI/2f);
			p.rect(0, 0, 100, 2000);
			p.popMatrix();
			
			p.pushMatrix();
			p.translate(0, 0, -300);
			p.scale(0.2f);
			p.image(_image, 0, 0);
			p.popMatrix();
			
		} else {
			
			// draw rect after image - alpha is jacked
			p.pushMatrix();
			p.translate(0, 0, -300);
			p.scale(0.2f);
			p.image(_image, 0, 0);
			p.popMatrix();
			
			p.pushMatrix();
			p.fill(11, 149, 14);
			p.translate(0, 100, -400);
			p.rotateX(P.PI/2f);
			p.rect(0, 0, 100, 2000);
			p.popMatrix();

		}
	}
}

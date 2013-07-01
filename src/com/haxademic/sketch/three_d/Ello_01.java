package com.haxademic.sketch.three_d;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;

@SuppressWarnings("serial")
public class Ello_01
extends PAppletHax  
{	
	protected PGraphics _texture;
	protected MeshPool _objPool;
	protected PShape _shape;
	protected PImage _image;
	
	protected int animCount = 0;
	
	public void setup() {
		super.setup();
		
		// create texture
		_texture = P.p.createGraphics( 520, 120, P.P3D );
		_texture.background(255);
		
		// create ello mesh
		_objPool = new MeshPool( p );
		_objPool.addMesh( "ELLO_SVG", MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/Ello.Black.svg", -1, 20, 2f ), 5 ), 1 );

		 _shape = p.loadShape("../data/svg/Ello.Black.svg");
		 
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
		
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setColorForPImage(p);
		DrawUtil.setDrawCenter(p);

		DrawUtil.setCenterScreen( p );
		DrawUtil.setBasicLights( p );
		
//		p.perspective( (float)Math.PI/3f, (float)p.width / (float)p.height, 1f, 2000000f );
//
//		p.directionalLight(51, 255, 126, 0, 1, 0);
//		p.directionalLight(51, 100, 255, 0, 1, 0);

		

		
		p.smooth();
		
		p.translate(0, 360, 300);

		
		p.rotateX(-p.frameCount * (float)Math.PI/60f);
		
		float imgs = 5;
		float rotInc = P.TWO_PI / imgs;
		
		for(int i=0; i < imgs; i++) {
			p.rotateX(rotInc);
			p.pushMatrix();
			p.translate(0, -626, 0);
//			p.image(_image, 0, 0);
			p.fill(0);
			p.noStroke();
			toxi.mesh( _objPool.getMesh("ELLO_SVG"), true );
			p.popMatrix();
		}
		
		
		
		
				
		p.translate(0, -260, 0);
		
		p.fill(0, 255);	// ello black
		p.noStroke();
		
		int spacing = 1000;
//		float furthest = -spacing * 5;
		animCount += 50;
		animCount = animCount % spacing;
		
		// p.shape(_shape);
		
		
//		float z = -spacing * 1 + animCount;
//		p.fill(0, 255 - ((z/furthest)*255) );
//		p.translate(0, 0, z);
//		toxi.mesh( _objPool.getMesh("ELLO_SVG"), false, 0 );
//		
//		z = -spacing * 2 + animCount;
//		p.fill(0, 255 - ((z/furthest)*255) );
//		p.translate(0, 0, z);
//		toxi.mesh( _objPool.getMesh("ELLO_SVG"), false, 0 );
//		
//		z = -spacing * 3 + animCount;
//		p.fill(0, 255 - ((z/furthest)*255) );
//		p.translate(0, 0, z);
//		toxi.mesh( _objPool.getMesh("ELLO_SVG"), false, 0 );
//		
//		z = -spacing * 4 + animCount;
//		p.fill(0, 255 - ((z/furthest)*255) );
//		p.translate(0, 0, z);
//		toxi.mesh( _objPool.getMesh("ELLO_SVG"), false, 0 );
//		
//		z = -spacing * 5 + animCount;
//		p.fill(0, 255 - ((z/furthest)*255) );
//		p.translate(0, 0, z);
//		toxi.mesh( _objPool.getMesh("ELLO_SVG"), false, 0 );
		
	}
}

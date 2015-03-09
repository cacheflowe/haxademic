package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

import controlP5.ControlP5;

@SuppressWarnings("serial")
public class RoadScrollTest
extends PAppletHax {
	
	public float speed = 0;
	public float angle = 0;
	protected ControlP5 _cp5;
	
	protected float _x = 0;
	protected float _y = 0;
	protected float _z = 0;
	protected float _tileSize = 160;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "60" );
		_appConfig.setProperty( "fills_screen", "false" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_cp5 = new ControlP5(this);
		_cp5.addSlider("speed").setPosition(20,20).setWidth(200).setRange(0,30);
		_cp5.addSlider("angle").setPosition(20,50).setWidth(200).setRange(0.55f,1f);
		
		_x = p.width / 2;
		_z = 0;
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);

		_y = p.height * angle;

		_z += speed;
		_z = _z % _tileSize;
		
		p.pushMatrix();
		
		
		float curZ = _z + _tileSize * 4; // start a little behind the camera
		while (curZ > -5000) {
			curZ -= _tileSize;
			
			// draw road tiles
			p.fill(255);
			p.pushMatrix();
			p.translate(_x, _y, curZ);
			p.rotateX(P.PI/2f);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			// draw extra elements coming at you
			p.fill(255,0,0);
			p.pushMatrix();
			p.translate(_x + _tileSize * 2, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			p.fill(255,0,0);
			p.pushMatrix();
			p.translate(_x - _tileSize * 2, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			// draw extra elements coming at you
			p.fill(255,100,100);
			p.pushMatrix();
			p.translate(_x + _tileSize * 3.1f, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			p.fill(255,100,100);
			p.pushMatrix();
			p.translate(_x - _tileSize * 3.1f, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
		}
		
		p.popMatrix();
		
	}

}

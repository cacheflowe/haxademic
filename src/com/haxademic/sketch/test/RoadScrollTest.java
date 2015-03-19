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
	
	float _frames = 200;

	
	protected float _x = 0;
	protected float _y = 0;
	protected float _z = 0;
	protected float _tileSize = 160;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "30" );
		_appConfig.setProperty( "fills_screen", "false" );
		
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );

		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "60" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );

	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

//		_cp5 = new ControlP5(this);
//		_cp5.addSlider("speed").setPosition(20,20).setWidth(200).setRange(0,30);
//		_cp5.addSlider("angle").setPosition(20,50).setWidth(200).setRange(0.55f,1f);
		
		_x = p.width / 2;
		_z = 0;
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);


		
		float frameRadians = P.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radiansComplete = P.TWO_PI * percentComplete;

		_y = p.height * (0.55f + 0.45f/2f + 0.45f/2f * P.sin(radiansComplete));
		_z = percentComplete * _tileSize * 30;

//		_y = p.height * angle;
//		_z += speed;
//		_z = _z % _tileSize;
		
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
			p.fill(180,180,180);
			p.pushMatrix();
			p.translate(_x + _tileSize * 2, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			p.fill(180,180,180);
			p.pushMatrix();
			p.translate(_x - _tileSize * 2, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			// draw extra elements coming at you
			p.fill(100,100,100);
			p.pushMatrix();
			p.translate(_x + _tileSize * 3.1f, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
			
			p.fill(100,100,100);
			p.pushMatrix();
			p.translate(_x - _tileSize * 3.1f, _y - _tileSize/2f, curZ);
			p.rect(0, 0, _tileSize, _tileSize);
			p.popMatrix();
		}
		
		p.popMatrix();
		
		
		if( p.frameCount == _frames * 2 + 2 ) {
			if(_appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}

}

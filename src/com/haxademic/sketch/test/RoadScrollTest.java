package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;

public class RoadScrollTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float speed = 0;
	public float angle = 0;
	
	float _frames = 200;
	
	protected float _x = 0;
	protected float _y = 0;
	protected float _z = 0;
	protected float _tileSize = 160;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}

	public void setupFirstFrame() {
		_x = p.width / 2;
		_z = 0;
	}

	public void drawApp() {
		background(0);
		PG.setDrawCenter(p);
		
//		float frameRadians = P.TWO_PI / _frames;
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
	}

}

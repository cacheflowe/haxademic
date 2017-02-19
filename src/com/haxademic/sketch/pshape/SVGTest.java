package com.haxademic.sketch.pshape;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PShape;

public class SVGTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ArrayList<PShape> _svgs;
	protected int _curShapeIndex = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1200" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "900" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}
	
	public void setup() {
		super.setup();
		
		_svgs = new ArrayList<PShape>();
		
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun-uzi.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/microphone.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/weed.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/speaker.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money-bag.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/diamond.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/car.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/coin.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cacheflowe-logo.svg" ) );
	}
	
	public void drawApp() {
		p.background(100);
		
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setDrawCenter(p);

		_timeEaser.update();

		PShape curShape = _svgs.get( _curShapeIndex );
		float size = 200f + 100f * P.sin(_timeEaser.value()/100f);
		float spacing = size * 1.2f;
		float ratioW = size / curShape.width;
		float ratioH = size / curShape.height;
		float shorterRatio = ratioW > ratioH ? ratioH : ratioW;

		curShape.disableStyle();
		float newW = shorterRatio * curShape.width;
		float newH = shorterRatio * curShape.height;

		p.fill(0);
		p.noStroke();

		for( float i=-300; i < p.width + 300; i+= spacing ) {
			for( float j=-300; j < p.height + 300; j+= spacing ) {
				p.pushMatrix();
				p.translate(i, j);
				p.rotateZ(0.5f * P.sin((_timeEaser.value() +j)/75f));
				p.scale( 1f + 0.5f * P.sin((_timeEaser.value() + i + j)/75f));
				p.shape( curShape, 0, 0, newW, newH );
				p.popMatrix();
			}			
		}
	}
	
	public void keyPressed() {
		if(p.key == ' ') {
			_curShapeIndex++;
			if( _curShapeIndex >= _svgs.size() ) _curShapeIndex = 0; 
		} else if (p.key == 'm') {
			_timeEaser.setTarget(_timeEaser.value() + 30);
		}
	}
}
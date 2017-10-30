package com.haxademic.app.haxmapper.textures;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureSvgPattern 
extends BaseTexture {

	protected ArrayList<PShape> _svgs;
	protected int _curShapeIndex = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);

	protected int _timingFrame = 0;

	public TextureSvgPattern( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		PApplet p = P.p;
		_svgs = new ArrayList<PShape>();
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/wuki.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/ello.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cursor.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/hexagon.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/eye.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/x.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/weed.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/diamond.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun-uzi.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/coin.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/heart.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cacheflowe-logo.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/smiley.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/star.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/triangle-stroke.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money-bag.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/speaker.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/car.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/note-1.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/microphone.svg" ) );
	}
	
	public void newLineMode() {
	}
	
	public void updateTiming() {
		_timeEaser.setTarget(_timeEaser.value() + 30);
		if( _timingFrame % 4 == 0 ) {
		}
		_timingFrame++;
	}
	
	public void updateTimingSection() {
		_curShapeIndex++;
		if( _curShapeIndex >= _svgs.size() ) _curShapeIndex = 0; 
	}


	public void updateDraw() {
//		_texture.clear();
		feedback(0.5f,0.2f);
		
		DrawUtil.resetGlobalProps(_texture);
		DrawUtil.setDrawCenter(_texture);

		_timeEaser.update();

		float quarterW = _texture.width * 0.25f;
		float space = _texture.width * 0.05f;
		float scaleMult = _texture.width  * 0.001f;
		
		PShape curShape = _svgs.get( _curShapeIndex );
		float size = (space*2f)  + space * MathUtil.saw( _timeEaser.value() / 100f );
		float spacing = size * 1.95f;
		float ratioW = size / curShape.width;
		float ratioH = size / curShape.height;
		float shorterRatio = ratioW > ratioH ? ratioH : ratioW;

		curShape.disableStyle();
		float newW = shorterRatio * curShape.width;
		float newH = shorterRatio * curShape.height;

		_texture.fill(255);
		_texture.noStroke();

		for( float i=-quarterW; i < _texture.width + quarterW; i+= spacing ) {
			for( float j=-quarterW; j < _texture.height + quarterW; j+= spacing ) {
				_texture.pushMatrix();
				_texture.translate(i, j);
				_texture.rotateZ(1.5f * P.sin((_timeEaser.value() +j)/75f));
				_texture.scale( (scaleMult*2f) + scaleMult * P.sin((_timeEaser.value() + i + j)/75f));
				_texture.shape( curShape, 0, 0, newW, newH );
				_texture.popMatrix();
			}			
		}
	}
	
}

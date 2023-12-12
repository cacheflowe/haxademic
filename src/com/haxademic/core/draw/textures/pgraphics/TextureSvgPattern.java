package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class TextureSvgPattern 
extends BaseTexture {

	protected ArrayList<PShape> _svgs;
	protected int _curShapeIndex = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);

	protected int _timingFrame = 0;

	public TextureSvgPattern( int width, int height ) {
		super(width, height);

		_svgs = new ArrayList<PShape>();
		_svgs.add(DemoAssets.shapeX());
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/wuki.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/ello.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cursor.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/hexagon.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/eye.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/x.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/weed.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/diamond.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun-uzi.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/coin.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/heart.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cacheflowe-logo.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/smiley.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/star.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/triangle-stroke.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money-bag.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/speaker.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/car.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/note-1.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun.svg" ) );
////		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/microphone.svg" ) );
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


	public void draw() {
//		_texture.clear();
		PG.feedback(pg, 0xff000000, 0.2f, 0.5f);
//		PG.resetGlobalProps(_texture);
		PG.setDrawCenter(pg);

		_timeEaser.update();

		float quarterW = width * 0.25f;
		float space = width * 0.05f;
		float scaleMult = width  * 0.001f;
		
		PShape curShape = _svgs.get( _curShapeIndex );
		float size = (space*2f)  + space * MathUtil.saw( _timeEaser.value() / 100f );
		float spacing = size * 1.95f;
		float ratioW = size / curShape.width;
		float ratioH = size / curShape.height;
		float shorterRatio = ratioW > ratioH ? ratioH : ratioW;

		curShape.disableStyle();
		float newW = shorterRatio * curShape.width;
		float newH = shorterRatio * curShape.height;

		pg.fill(255);
		pg.noStroke();

		for( float i=-quarterW; i < width + quarterW; i+= spacing ) {
			for( float j=-quarterW; j < height + quarterW; j+= spacing ) {
				pg.pushMatrix();
				pg.translate(i, j);
				pg.rotateZ(1.5f * P.sin((_timeEaser.value() +j)/75f));
				pg.scale( (scaleMult*2f) + scaleMult * P.sin((_timeEaser.value() + i + j)/75f));
				pg.shape( curShape, 0, 0, newW, newH );
				pg.popMatrix();
			}			
		}
	}
	
}

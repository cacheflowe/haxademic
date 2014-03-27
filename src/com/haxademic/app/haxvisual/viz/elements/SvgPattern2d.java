package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

public class SvgPattern2d
extends ElementBase 
implements IVizElement {
	
	protected float _amp;
	
	protected float _cols = 32;
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;

	protected ArrayList<PShape> _svgs;
	protected int _curShapeIndex = 0;
	protected EasingFloat _timeEaser = new EasingFloat(0, 5);

	protected int _timingFrame = 0;


	public SvgPattern2d( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		_svgs = new ArrayList<PShape>();
		
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/wuki.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/wuki-2.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cursor.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/hexagon.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/eye.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/x.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/weed.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/diamond.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun-uzi.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/coin.svg" ) );
		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/heart.svg" ) );
//		_svgs.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cacheflowe-logo.svg" ) );
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
	
	public void setDrawProps(float width, float height) {

	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setDrawCenter(p);

		_timeEaser.update();

		PShape curShape = _svgs.get( _curShapeIndex );
		float size = 200f + 100f * MathUtil.saw( _timeEaser.value() / 100f );
		float spacing = size * 1.5f;
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

	public void nextImage() {
		_curShapeIndex++;
		if( _curShapeIndex >= _svgs.size() ) _curShapeIndex = 0; 
	}
	
	public void reset() {
		
	}

	public void dispose() {
		_audioData = null;
	}
	
	public void updateTiming() {
		if( _timingFrame % 4 == 0 ) {
			_timeEaser.setTarget(_timeEaser.value() + 30);
		}
		_timingFrame++;
	}

	public void updateSection() {
		nextImage();
	}

}

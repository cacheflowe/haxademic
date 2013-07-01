package com.haxademic.app.kacheout.screens;

import processing.core.PImage;
import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.image.ScreenUtil;
import com.haxademic.core.math.easing.EasingFloat3d;
import com.haxademic.core.math.easing.ElasticFloat;

public class IntroScreenWedding {
	protected KacheOut p;
	
	protected final TColor MODE_SET_GREY = new TColor( TColor.newRGB( 96, 96, 96 ) );
	protected final TColor MODE_SET_BLUE = new TColor( TColor.newRGB( 0, 200, 234 ) );
	protected final TColor CACHEFLOWE_YELLOW = new TColor( TColor.newRGB( 255, 249, 0 ) );
	protected final TColor WHITE = new TColor( TColor.WHITE );
	
	protected EasingFloat3d _logo;
	protected EasingFloat3d _credits;
	protected PImage _logoImg;
	protected PImage _creditsImg;
	
	protected int _frameCount;
	protected int _frame1, _frame2, _frame3, _frame4;//, _frame5, _frame6, _frame7, _frame8;	// horrible, but whatever

	public IntroScreenWedding() {
		p = (KacheOut) P.p;
		
		_logo = new EasingFloat3d( 0, 0, 0, 5 );
		_credits = new EasingFloat3d( 0, 0, 0, 5 );
		
		_logoImg = p.loadImage( "../data/images/kacheout/logo.png" );
		_creditsImg = p.loadImage( "../data/images/kacheout/credits.jpg" );
		
		// set up keyframes
		_frame1 = 0;
		_frame2 = _frame1 + 200;	// logo
		_frame3 = _frame2 + 240;	// credits
		_frame4 = _frame3 + 20;		// built by
	}
	
	public void reset() {
		_frameCount = 0;
		
		_logo.setCurrentY( p.stageHeight() );
		_logo.setTargetY( p.stageHeight() );
		_credits.setCurrentY( p.stageHeight() );
		_credits.setTargetY( p.stageHeight() );

	}
	
	public void update() {
		updateAnimationsOnFrameCount();
		updatePositions();
		drawObjects();
		_frameCount++;
	}
	
	protected void updateAnimationsOnFrameCount() {
		// animate on certain frames
		if( _frameCount == _frame1 ) {
			_logo.setTargetY( 0 );
		} else if( _frameCount == _frame2 ) {
			_logo.setTargetY( -p.stageHeight() );
			_credits.setTargetY( 0 );
		} else if( _frameCount == _frame3 ) {
			_credits.setTargetY( -p.stageHeight() );
		} else if( _frameCount == _frame4 ) {
			p.setGameMode( KacheOut.GAME_INSTRUCTIONS );
		}

	}
	
	protected void updatePositions() {
		// update eased positions
		_logo.update();
		_credits.update();
	}
	
	protected void drawObjects() {
		// set up for drawing objects
		p.pushMatrix();
		DrawUtil.setCenter( p );
		p.translate( 0, 0, p.gameBaseZ() );
		
		// draw slides
		drawImageAtLoc( _logoImg, _logo.x(), _logo.y(), _logo.z() );
		drawImageAtLoc( _creditsImg, _credits.x(), _credits.y(), _credits.z() );
		
		// reset
		p.popMatrix();
	}
	
	protected void drawImageAtLoc( PImage image, float x, float y, float z ) {
		if( y > -p.stageHeight() && y < p.stageHeight() ) {
			p.pushMatrix();
			p.translate( x, y, z );
			p.fill( 255 );
			p.noStroke();
//			p.toxi.mesh( mesh );
			p.image( image, x, y );
			p.popMatrix();	
		}
	}
}

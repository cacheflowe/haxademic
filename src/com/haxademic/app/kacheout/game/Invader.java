package com.haxademic.app.kacheout.game;

import java.util.ArrayList;

import toxi.color.TColor;
import toxi.geom.AABB;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.shapes.Meshes;
import com.haxademic.core.math.MathUtil;

public class Invader {
	
	protected KacheOut p;
	protected ArrayList<Block> _boxes;
	protected ArrayList<Block> _boxesAlt;
	protected ArrayList<Block> _curBoxesArray;
	protected boolean _hasBeenHit = false;
	protected boolean _isAnimating = false;
	protected int _numActiveBlocks = 999;
	
	protected int _x, _y, _row;
	protected float _scale;
	

	public Invader( int x, int y, float scale, int row ) {
		p = (KacheOut) P.p;
		
		_x = x;
		_y = y;
		_row = row;
		_scale = scale;
		
		buildInvader();
		reset();
	}
	
	protected void buildInvader() {
		// create 2 arrays of blocks
		_boxes = new ArrayList<Block>();
		_boxesAlt = new ArrayList<Block>();
		TColor color = null;
		ArrayList<AABB> boxes = null, boxesAlt = null;
		if( _row % 3 == 0 ) {
			boxes = Meshes.invader1Boxes( 0, _scale );
			boxesAlt = Meshes.invader1Boxes( 1, _scale );
			color = TColor.newRGB( 136f/255f, 248f/255f, 56f/255f );
			color = new TColor( TColor.GREEN );
		} else if( _row % 3 == 1 ) {
			boxes = Meshes.invader2Boxes( 0, _scale );
			boxesAlt = Meshes.invader2Boxes( 1, _scale );
			color = TColor.newRGB( 248f/255f, 248f/255f, 152f/255f );
			color = new TColor( TColor.YELLOW );
		} else if( _row % 3 == 2 ) {
			boxes = Meshes.invader3Boxes( 0, _scale );
			boxesAlt = Meshes.invader3Boxes( 1, _scale );
			color = TColor.newRGB( 255f/255f, 60f/255f, 60f/255f );
			color = new TColor( TColor.RED );
		}
		
		// populate arrays while positioning individual blocks to the center of this invader
		for( int i=0; i < boxes.size(); i++ ) {
			boxes.get( i ).set( boxes.get( i ).x + _x, boxes.get( i ).y + _y, 0 );
			_boxes.add( new Block( boxes.get( i ), i, _scale*100f, color ) );
		}
		for( int i=0; i < boxesAlt.size(); i++ ) {
			boxesAlt.get( i ).set( boxesAlt.get( i ).x + _x, boxesAlt.get( i ).y + _y, 0 );
			_boxesAlt.add( new Block( boxesAlt.get( i ), i, _scale*100f, color ) );
		}
		
		// set this once - from here out, they'll reanimate after animating back to original positions after die()
		_curBoxesArray = _boxes;
	}
	
	public void reset() {
		_numActiveBlocks = 999;
		_isAnimating = false;
		_hasBeenHit = false;
		
		for( int i=0; i < _boxes.size(); i++ ) {
			boolean shouldResetPositionsImmediately = ( _curBoxesArray != _boxes );
			_boxes.get( i ).reset( shouldResetPositionsImmediately );
		}
		for( int i=0; i < _boxesAlt.size(); i++ ) {
			boolean shouldResetPositionsImmediately = ( _curBoxesArray != _boxesAlt );
			_boxesAlt.get( i ).reset( shouldResetPositionsImmediately );
		}
	}
	
	public void gameOver() {
		_isAnimating = false;
		_hasBeenHit = true;
		for( int i=0; i < _curBoxesArray.size(); i++ ) {
			_curBoxesArray.get( i ).die( 0, MathUtil.randRangeDecimal( -1f, 1f ) );
		}
	}
	
	public void display() {
		animateInvaderState();
		startAnimatingOnRestoredPosition();
		drawBoxes();
	}
	
	protected void animateInvaderState() {
		// animate - make sure every invader's on the same frame with double mod
		if( _isAnimating == true && p.frameCount % 15 == 0 ) {
			_curBoxesArray = ( p.frameCount % 30 == 0 ) ? _boxesAlt : _boxes;
		}
	}
	
	protected void startAnimatingOnRestoredPosition() {
		// check to see if we should start animating
		if( _isAnimating == false && _hasBeenHit == false ) {
			int numResettingBlocks = 0;
			for( int i=0; i < _boxes.size(); i++ ) {
				if( _boxes.get( i ).isReset() == false  ) {
					numResettingBlocks++;
				}
			}
			for( int i=0; i < _boxesAlt.size(); i++ ) {
				if( _boxesAlt.get( i ).isReset() == false  ) {
					numResettingBlocks++;
				}
			}
			if( numResettingBlocks == 0 ) {
				_isAnimating = true;
			}
		}
	}
	
	protected void drawBoxes() {
		// draw boxen
		_numActiveBlocks = 0;
		for( int i=0; i < _curBoxesArray.size(); i++ ) {
			_curBoxesArray.get( i ).display();
			if( _curBoxesArray.get( i ).active() == true ) _numActiveBlocks++;
		}
	}
	
	public int numActiveBlocks() {
		return _numActiveBlocks;
	}
	
	public boolean detectCollisions( Ball ball ) {
		boolean collided = false;
		for( int i=0; i < _curBoxesArray.size(); i++ ) {
			if( _curBoxesArray.get( i ).active() == true && ball.detectBox( _curBoxesArray.get( i ).box() ) == true ) {
				_curBoxesArray.get( i ).die( ball.speedX(), ball.speedY() );
				collided = true;
				_isAnimating = false;
				_hasBeenHit = true;
			}
		}
		return collided;
	}
	
}

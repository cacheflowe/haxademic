package com.haxademic.app.kacheout.game;

import java.util.ArrayList;

import toxi.color.TColor;

import com.haxademic.app.haxvisual.viz.elements.GridEQ;
import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.data.FloatRange;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.ElasticFloat;

public class GamePlay {
	protected KacheOut p;
	
	// game dimensions
	protected int _gameLeft, _gameRight, _gameWidth;
	protected int _cols = 4;
	protected int _rows = 5;
	
	// main game objects
	protected Ball _ball;
	protected Paddle _paddle;
	protected Walls _walls;
	protected GridEQ _background;
	protected ArrayList<Invader> _invaders;
	
	// controls
	protected float K_PIXEL_SKIP = 6;
	protected FloatRange _kinectRange;
	protected FloatRange _kinectCurrent;
	protected boolean _isKinectReversed = true;
	protected EasingFloat _gameRotation = new EasingFloat( 0, 10 );
	protected EasingFloat _gameBaseY = new EasingFloat( 0, 7 );
	
	// colors
	protected TColor _winColor = new TColor( TColor.GREEN );
	protected TColor _loseColor = new TColor( TColor.RED );
	protected TColor _waitingColor = new TColor( TColor.YELLOW );
	protected TColor _readyColor = new TColor( TColor.GREEN );
	protected TColor _countdownColor = new TColor( TColor.WHITE );
	
	// state 
	protected int _gameIndex;
	protected boolean _playerReady = false;
	protected int _playerDetectedFrames = 0;
	protected ElasticFloat _playerReadyTextScale;
	protected int _countdownFrames = 0;
	protected boolean _hasClearedBoard = false;
	protected boolean _didWin = false;
	protected ElasticFloat _gameOverTextScale;
	protected int _gameOverFrameCount = 0;
	protected int _gameNum = 0;

	
	public GamePlay( int gameIndex, int gameLeft, int gameRight, FloatRange kinectRange ) {
		p = (KacheOut) P.p;
		_gameIndex = gameIndex;
		_gameLeft = gameLeft;
		_gameRight = gameRight;
		_gameWidth = gameRight - gameLeft;
		_kinectRange = kinectRange;
		_kinectCurrent = new FloatRange( -1, -1 );
		
		// create blocks
		_invaders = new ArrayList<Invader>();
		int index = 0;
		float spacingX = (float)_gameWidth / (float)(_cols+1f);
		float spacingY = spacingX * 5f/6f;
		float boxScale = (spacingX) / 15f; // terrible, but invader max width is 12 blocks
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				// Initialize each object
				float centerX = i * spacingX + spacingX;
				float centerY = j * spacingY + spacingY;
				_invaders.add( new Invader( (int)centerX, (int)centerY, boxScale, j ) );
				index++;
			}
		}
		
		// create game objects
		_background = new GridEQ( p, p.toxi, p._audioInput );
		_background.updateColorSet( p.gameColors() );

		_ball = new Ball();
		_paddle = new Paddle();
		_walls = new Walls();
		
		_gameOverTextScale = new ElasticFloat( 0, 0.7f, 0.4f );
		_playerReadyTextScale = new ElasticFloat( 0, 0.7f, 0.4f );
	}
	
	public void reset() {
		for (int i = 0; i < _invaders.size(); i++) {
			_invaders.get( i ).reset();
		}
		_ball.reset();
		_playerReady = false;
		_playerDetectedFrames = 0;
		_playerReadyTextScale.setValue( 0 );
		_playerReadyTextScale.setTarget( 1 );
		_hasClearedBoard = false;
		_didWin = false;
		_gameOverTextScale.setValue( 0 );
		_gameOverTextScale.setTarget( 0 );
		_gameOverFrameCount = 0;
		_gameBaseY.setTarget( 0 );
	}
	
	public void gameOver() {
		_didWin = ( _hasClearedBoard == true ) ? true : false;
		for (int i = 0; i < _invaders.size(); i++) {
			_invaders.get( i ).gameOver();
		}
		_gameOverTextScale.setTarget( 1 );
		_gameNum++;
	}
	
	public boolean hasClearedBoard() {
		return _hasClearedBoard;
	}
	
	public boolean isPlayerReady() {
		return _playerReady;
	}
	
	public void startCountdown() {
		_playerReadyTextScale.setTarget( 0 );
		_countdownFrames = 0;
	}
	
	public void update( int gameIndex ) {
		positionGameCenter();
//		drawBackground();
		updateControls();
		drawGameObjects();
		drawSpecialModes();
		if( p.gameState() == KacheOut.GAME_ON ) detectCollisions();
	}
	
	protected void positionGameCenter(){
		DrawUtil.setTopLeft( p );
		p.translate( 0, 0, p.gameBaseZ() );
//		p.rotateX( p.PI / 16f );
		
		// rotate to kinect position
		// pivot from center
		_gameBaseY.update();
		p.translate( p.gameWidth() / 2 + _gameIndex * p.gameWidth(), _gameBaseY.value(), 0 );
		
		// ease the rotation 
		float rotateExtent = P.PI / 10f;
		_gameRotation.setTarget( rotateExtent * _paddle.xPosPercent() - rotateExtent / 2f );
		_gameRotation.update();
		p.rotateY( _gameRotation.value() );
		
		// slide back half width
		p.translate( -p.gameWidth() / 2 , 0, 0 );
	}
	
	protected void drawBackground(){
		// draw bg
		p.pushMatrix();
		p.translate( 0, 0, -2000 );
		_background.update();
		p.popMatrix();
	}
	
	protected void findKinectCenterX() {
		// loop through point grid and skip over pixels on an interval, finding the horizonal extents of an object in the appropriate range
		int pixelDepth;
		float minX = -1f;
		float maxX = -1f;
		
		// loop through kinect data within player's control range
		for ( int x = (int)_kinectRange.min(); x < (int)_kinectRange.max(); x += K_PIXEL_SKIP ) {
			for ( int y = KacheOut.KINECT_TOP; y < KacheOut.KINECT_BOTTOM; y += K_PIXEL_SKIP ) { // only use the vertical middle portion of the kinect data
				pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > KacheOut.KINECT_MIN_DIST && pixelDepth < KacheOut.KINECT_MAX_DIST ) {
					// keep track of kinect range
					if( minX == -1 || x < minX ) minX = x;
					if( maxX == -1 || x > maxX ) maxX = x;
				}
			}
		}
		_kinectCurrent.set( minX, maxX );
		
		if( minX != -1 && maxX != -1 ) {
			detectPlayerReady();
		} else {
			_playerDetectedFrames = 0;
		}
	}
	
	protected void detectPlayerReady() {
		// recognize that a player is in the area
		if( _playerReady == false ) {
			_playerDetectedFrames++;
			if( _playerDetectedFrames == 3 ) {
				p.sounds.playSound( KacheOut.STEP_UP_SOUND );
			}
			if( _playerDetectedFrames > 60 ) {
				_playerReady = true;
				p.sounds.playSound( KacheOut.READY_SOUND );
			}
		}
	}
	
	protected void updateControls() {
		// update keyboard or Kinect, and pass the value to the paddle
		float paddleX = 0.5f;
		boolean inputDetected = false;
		if( p.kinectWrapper != null && p.kinectWrapper.isActive() == true ) {
			findKinectCenterX();
			// send kinect data to games - calculate based off number of games vs. kinect width
			if( _playerReady == false ) {	// _kinectCurrent.center() == -1 || 
				paddleX = 0.5f;
				inputDetected = true;
			} else {
				if( _kinectCurrent.center() != -1 ) {
					paddleX = MathUtil.getPercentWithinRange( _kinectRange.min(), _kinectRange.max(), _kinectCurrent.center() );
					inputDetected = true;
				}
			}
			if( inputDetected == true ) _paddle.setTargetXByPercent( 1f - paddleX );
		} else {
			_paddle.setTargetXByPercent( 1f - MathUtil.getPercentWithinRange( 0, p.gameWidth(), p.mouseX ) );
		}
	}
	
	protected void drawGameObjects() {
		p.pushMatrix();
		
//		OpenGLUtil.enableBlending( p, true );
//		OpenGLUtil.setBlendMode( p, OpenGLUtil.ADDITIVE );
		
		// draw the blocks
		int index = 0;
		int numActiveBlocks = 0;
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				_invaders.get( index ).display();
				numActiveBlocks += _invaders.get( index ).numActiveBlocks();
				index++;
			}
		}
		if( numActiveBlocks == 0 ) _hasClearedBoard = true;
		p.popMatrix();
		
//		OpenGLUtil.enableBlending( p, false );
//		OpenGLUtil.setBlendMode( p, OpenGLUtil.NORMAL );
		
		// draw other objects
		_paddle.display();
		_walls.display();
		if( p.gameState() == KacheOut.GAME_ON ) {
			_ball.display( _paddle );
		}
		if( p.isDebugging() == true ) {
			drawDebugLines();
			drawPlayerKinectPoints();
		}
		
	}
	
	protected void drawSpecialModes() {
		_playerReadyTextScale.update();
		if( p.gameState() == KacheOut.GAME_INSTRUCTIONS || p.gameState() == KacheOut.GAME_COUNTDOWN ) drawInstructionsMode();
		if( p.gameState() == KacheOut.GAME_COUNTDOWN ) drawCountdownMode();
		if( p.gameState() == KacheOut.GAME_OVER ) drawGameOverMode();
	}
	
	protected void drawInstructionsMode() {
		p.pushMatrix();
		p.translate( _paddle.x(), _paddle.y() - _paddle.height(), _paddle.height() );
		
		// update win/lose text scale and draw it
		if( _playerReady == false ) {
			if( _playerReadyTextScale.val() > 0 ) {
				if( _playerDetectedFrames > 0 ) {
					if( _playerDetectedFrames % 2 == 0 ) {
						p.fill( _readyColor.toARGB() );
					} else {
						p.fill( _waitingColor.toARGB() );
					}
				} else {
					p.fill( _waitingColor.toARGB() );
				}
				p.meshPool.getMesh( KacheOut.STEP_UP_TEXT ).scale( _playerReadyTextScale.val() );
				p.toxi.mesh( p.meshPool.getMesh( KacheOut.STEP_UP_TEXT ) );
				p.meshPool.getMesh( KacheOut.STEP_UP_TEXT ).scale( 1f / _playerReadyTextScale.val() );
			}
		} else {
			if( _playerReadyTextScale.val() > 0 ) {
				p.meshPool.getMesh( KacheOut.READY_TEXT ).scale( _playerReadyTextScale.val() );
				p.fill( _readyColor.toARGB() );
				p.toxi.mesh( p.meshPool.getMesh( KacheOut.READY_TEXT ) );
				p.meshPool.getMesh( KacheOut.READY_TEXT ).scale( 1f / _playerReadyTextScale.val() );
			}
		}
		
		p.popMatrix();
	}
	
	protected void drawCountdownMode() {
		p.pushMatrix();
		p.translate( _paddle.x(), _paddle.y() - _paddle.height() - 120, _paddle.height() * 3f );
		
		_countdownFrames++;
		
		// update win/lose text scale and draw it
		if( _countdownFrames >= 11 && _countdownFrames < 40 ) {
			if( _countdownFrames == 11 && _gameIndex == 0 ) {
				p.sounds.playSound( KacheOut.COUNTDOWN_1 );
				p.sounds.playSound( KacheOut.COUNTDOWN_3_VOX );
			}
			p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_3 ).scale( 1 );
			p.fill( _countdownColor.toARGB() );
			p.toxi.mesh( p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_3 ) );
			p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_3 ).scale( 1f / 1 );
		} else if( _countdownFrames >= 40 && _countdownFrames < 70 ) {
			if( _countdownFrames == 40 && _gameIndex == 0 ) {
				p.sounds.playSound( KacheOut.COUNTDOWN_2 );
				p.sounds.playSound( KacheOut.COUNTDOWN_2_VOX );
			}
			p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_2 ).scale( 1 );
			p.fill( _countdownColor.toARGB() );
			p.toxi.mesh( p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_2 ) );
			p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_2 ).scale( 1f / 1 );
		} else if( _countdownFrames >= 70 && _countdownFrames < 100 ) {
			if( _countdownFrames == 70 && _gameIndex == 0 ) {
				p.sounds.playSound( KacheOut.COUNTDOWN_3 );
				p.sounds.playSound( KacheOut.COUNTDOWN_1_VOX );
			}
			p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_1 ).scale( 1 );
			p.fill( _countdownColor.toARGB() );
			p.toxi.mesh( p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_1 ) );
			p.meshPool.getMesh( KacheOut.COUNTDOWN_TEXT_1 ).scale( 1f / 1 );
		} else if( _countdownFrames >= 100 ) {
			p.setGameMode( KacheOut.GAME_ON );
		}
		
		p.popMatrix();
	}
		
	protected void drawGameOverMode() {
		// position drawing
		p.pushMatrix();
		p.translate( _paddle.x(), _paddle.y() - _paddle.height(), 0 );
		
		// update win/lose text scale and draw it
		_gameOverTextScale.update();
		if( _gameOverTextScale.val() > 0 ) {
			if( _didWin == true ) {
				p.meshPool.getMesh( KacheOut.WIN_TEXT ).scale( _gameOverTextScale.val() );
				p.fill( _winColor.toARGB() );
				p.toxi.mesh( p.meshPool.getMesh( KacheOut.WIN_TEXT ) );
				p.meshPool.getMesh( KacheOut.WIN_TEXT ).scale( 1f / _gameOverTextScale.val() );
			} else {
				p.meshPool.getMesh( KacheOut.LOSE_TEXT ).scale( _gameOverTextScale.val() );
				p.fill( _loseColor.toARGB() );
				p.toxi.mesh( p.meshPool.getMesh( KacheOut.LOSE_TEXT ) );
				p.meshPool.getMesh( KacheOut.LOSE_TEXT ).scale( 1f / _gameOverTextScale.val() );
			}
		}
		
		// time out the outro animations
		_gameOverFrameCount++;
		if( _gameOverFrameCount == 80 ) {
			_gameOverTextScale.setTarget( 0 );
		}
		if( _gameOverFrameCount == 100 ) {
			_gameBaseY.setTarget( p.stageHeight() * 2 );
			if( _gameIndex == 0 ) p.sounds.playSound( KacheOut.INSERT_COIN );
		}
		if( _gameOverFrameCount == 135 ) {
			p.setGameMode( KacheOut.GAME_INTRO );
		}

		// make sure we didn't jack anything up elsewhere
		p.popMatrix();
	}
	
	protected void drawPlayerKinectPoints() {
		// draw point cloud
		p.pushMatrix();
		DrawUtil.setCenter( p );
//		float xTravel = p.gameWidth() - KinectWrapper.KWIDTH;
		float scale = 1;
		p.translate( -KinectWrapper.KWIDTH/2f, 0, -1000 );
//		float scale = 100f;	// 22f
//		p.translate( (_gameIndex*60f) + -_paddle.xPosPercent() * 50f, 26, -400 );
		
		if( p.kinectWrapper != null ) p.kinectWrapper.drawPointCloudForRect( p, true, 8, 0.5f, scale, KacheOut.KINECT_MIN_DIST, KacheOut.KINECT_MAX_DIST, KacheOut.KINECT_TOP, (int)_kinectRange.max(), KacheOut.KINECT_BOTTOM, (int)_kinectRange.min() );
		p.popMatrix();
		
//		p.image(p.kinectWrapper.getRgbImage(),0,0);
//		p.image(p.kinectWrapper.getIRImage(),0,0);
//		p.image(p.kinectWrapper.getDepthImage(),0,0);
	}
	
	protected void drawDebugLines() {
		// draw debug positioning vertical lines
		p.pushMatrix();
		DrawUtil.setCenter( p );
		p.translate( -KinectWrapper.KWIDTH/2, 0, -700 );
		p.fill( 255, 255, 255, 127 );
		p.rect(_kinectRange.min(), 0, 2, p.stageHeight());
		p.rect(_kinectRange.max(), 0, 2, p.stageHeight());
		p.fill( 255, 0, 0, 127 );
		p.rect(_kinectCurrent.min(), 0, 2, p.stageHeight());
		p.rect(_kinectCurrent.max(), 0, 2, p.stageHeight());
		p.fill( 0, 255, 0, 127 );
		p.rect(_kinectCurrent.center(), 0, 2, p.stageHeight());
		p.popMatrix();
	}
	
	public void launchBall() {
		_ball.launch( _paddle );
	}
	
	public void detectCollisions() {
		// TODO: don't pass the paddle into ball so much! booo.
		// paddle collision
		if( _ball.detectBox( _paddle.box() ) == true ) {
			if( _ball.y() < _paddle.y() ) {
				_ball.bounceOffPaddle( _paddle );
			}
		}
		// paddle misses ball
		if( _ball.y() > p.stageHeight() ) {
			_paddle.ballPassedPaddle();
			if( _ball.y() > p.stageHeight() + 200 ) {
				_ball.resetY( _paddle );
				_ball.launch( _paddle );
				_paddle.launch();
			}
		}
		// walls
		if( _walls.detectSphere( _ball.sphere() ) == true ) {
			_ball.detectWalls( _walls.leftHit(), _walls.topHit(), _walls.rightHit() );
			_walls.resetCollisions();
			p.sounds.getSound( "WALL_BOUNCE" ).play(0);
		}
		// blocks
		for (int i = 0; i < _invaders.size(); i++) {
			_invaders.get( i ).detectCollisions( _ball );
		}
	}

	public boolean shouldTakeScreenshot() {
		if( _gameOverFrameCount == 20 ) {
			return true;
		} else {
			return false;
		}
	}

}

package com.haxademic.app.matchgame;

import processing.core.PApplet;
import processing.core.PImage;

import com.haxademic.app.matchgame.game.MatchGameAssets;
import com.haxademic.app.matchgame.game.MatchGameConfetti;
import com.haxademic.app.matchgame.game.MatchGameControls;
import com.haxademic.app.matchgame.game.MatchGamePlay;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.cameras.common.ICamera;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class MatchGame
extends PAppletHax  
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	public static void main(String args[]) {
		// "--present",
		_hasChrome = false;
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.app.matchgame.MatchGame" });
	}

	// input
	public static float KINECT_MIN_DIST;
	public static float KINECT_MAX_DIST;
	public static float KINECT_WIDTH_PERCENT;
	public static int KINECT_TOP;
	public static int KINECT_BOTTOM;
	public static float K_PIXEL_SKIP = 6;
	protected boolean _isDebuggingKinect = false;
	
	// debug 
	protected boolean _isDebugging = false;
	
	// dimensions and stuff
	protected int _stageWidth;
	protected int _stageHeight;	
	protected int _gameWidth;
	protected int _numAverages = 32;

	protected ICamera _curCamera = null;
	
	// game state
	protected int _curMode;
	public static boolean KIDS_MODE;
	public static float CURSOR_MULTIPLIER;
	
	// more game state - screen transition / detection
	protected int _playerDetectStartTime = -1;
	protected int _skeletonDetectStartTime = -1;
	protected int _gameOverStartTime = -1;
	protected PImage _winRGBImage;
	protected String _bestGameTimeString = null;
	
	// game objects
	protected MatchGamePlay _gamePlay;
	protected MatchGameControls _controls;
	protected MatchGameConfetti _confetti;

	// game state
	protected int _gameState;
	protected int _gameStateQueued;	// wait until beginning on the next frame to switch modes to avoid mid-frame conflicts
	public static int GAME_ON = 3;
	public static int GAME_OVER = 4;
	public static int GAME_PLAYER_DETECT = 5;
	public static int GAME_SKELETON_DETECT = 6;
	public static int GAME_COUNTDOWN = 7;

	
	protected float _cameraZFromHeight = 0;
	
	public void setup() {
		_customPropsFile = "../data/properties/matchgame.properties";
		super.setup();
		initGame();
	}

	public void initGame() {
		_stageWidth = width;
		_stageHeight = height;
		newCamera();
		
		initKinectOptions();
		buildGameObjects();
		
		// external config
		KIDS_MODE = _appConfig.getBoolean( "kids_mode", false );
		CURSOR_MULTIPLIER = _appConfig.getFloat( "cursor_multiplier", 1f );
		setGameMode( GAME_PLAYER_DETECT );
	}
	
	void initKinectOptions() {
		// default kinect camera distance is for up-close indoor testing. not good for real games - suggested use is 2300-3300
		// default pixel rows are the center 200 kinect data rows
		KINECT_MIN_DIST = _appConfig.getInt( "kinect_min_mm", 1500 );
		KINECT_MAX_DIST = _appConfig.getInt( "kinect_max_mm", 2000 );
		KINECT_WIDTH_PERCENT = _appConfig.getFloat( "kinect_width_percent", 0.5f );
		KINECT_TOP = _appConfig.getInt( "kinect_top_pixel", 240 );
		KINECT_BOTTOM = _appConfig.getInt( "kinect_bottom_pixel", 400 );
		if(kinectWrapper != null) kinectWrapper.setMirror( true );
	}

	void buildGameObjects() {
		MatchGameAssets.initAssets();
		_controls = new MatchGameControls();
		_gamePlay = new MatchGamePlay( _controls );
		_confetti = new MatchGameConfetti( 300 );
	}
	
	// HAXADEMIC STUFF --------------------------------------------------------------------------------------
	void newCamera() {
//		_curCamera = new CameraDefault( p, 0, 0, 0 );
//		_curCamera.setPosition( _stageWidth/2f, _stageHeight/2f, 0 );	// _cameraZFromHeight
//		_curCamera.setTarget( _stageWidth/2f, _stageHeight/2f, 0 );
//		_curCamera.reset();
	}
	
	// INPUT --------------------------------------------------------------------------------------

	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		if ( p.key == 'd' || p.key == 'D' ) {
			_isDebugging = !_isDebugging;
			if( kinectWrapper != null ) {
				kinectWrapper.enableRGB( !_isDebugging );
				kinectWrapper.enableDepth( !_isDebugging );
			}
		}
	}
	
	// PUBLIC ACCESSORS FOR GAME OBJECTS --------------------------------------------------------------------------------------
	public int gameWidth() { return _gameWidth; }
	public int stageWidth() { return _stageWidth; }
	public int stageHeight() { return _stageHeight; }
	public float gameBaseZ() { return -_stageHeight; }
	public int gameState() { return _gameState; }
	public boolean isDebugging() { return _isDebugging; }
	
	
	// GAME LOGIC --------------------------------------------------------------------------------------
	
	public void setGameMode( int mode ) {
		_gameStateQueued = mode;
	}
	
	public int getGameMode() {
		return _gameState;
	}
	
	public void swapGameMode() {
		_gameState = _gameStateQueued;
		if( _gameState == GAME_PLAYER_DETECT ) {

		} else if( _gameState == GAME_SKELETON_DETECT ) {
			_controls.stopTrackingAllUsers();
			_controls.enableSkeletonTracking();
			_skeletonDetectStartTime = p.millis();
		} else if( _gameState == GAME_COUNTDOWN ) {
			_gamePlay.startCountdown();
		} else if( _gameState == GAME_ON ) {

		} else if( _gameState == GAME_OVER ) {
			_winRGBImage = p.kinectWrapper.getRgbImage();
			FileUtil.createDir( FileUtil.getProjectAbsolutePath() + "/bin/output/" );
			FileUtil.createDir( FileUtil.getProjectAbsolutePath() + "/bin/output/matchgame/" );
			_winRGBImage.save( FileUtil.getProjectAbsolutePath() + "/bin/output/matchgame/matchgame-" + SystemUtil.getTimestampFine( p ) + "-rgb.png" );
			_gameOverStartTime = p.millis();
			_confetti.explode();
		}
	}
		
	// FRAME LOOP --------------------------------------------------------------------------------------
	
	public void drawApp() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setTopLeft( p );
		p.shininess(1000f); 
		p.lights();
		p.background(255);	
		p.camera();
//		_curCamera.update();
		
		// draw bg
		DrawUtil.setColorForPImage( p );
		DrawUtil.setDrawCorner( p );
		p.image( MatchGameAssets.UI_BACKGROUND, 0, 0 );
		

		if( _gameState != _gameStateQueued ) swapGameMode();
		if( _gameState == GAME_PLAYER_DETECT ) {
			// draw "step up" image
			p.image( MatchGameAssets.UI_STEP_UP, 262, 386 );
			// check for player in game area box
			boolean playerIsInArea = _controls.userIsInGameArea();
			if( playerIsInArea == true ) {
				if( _playerDetectStartTime == -1 ) _playerDetectStartTime = p.millis();
				drawLoader();
				if( p.millis() - _playerDetectStartTime > 3000 ) { 
					setGameMode( GAME_SKELETON_DETECT );
					_playerDetectStartTime = -1;
				}
			} else {
				_playerDetectStartTime = -1;
			}
		} else if( _gameState == GAME_SKELETON_DETECT ) {
			p.image( MatchGameAssets.UI_PLAYER_DETECT, 193, 242 );
			drawLoader();
			if( _controls.hasASkeleton() == true && p.millis() - _skeletonDetectStartTime > 3000 ) {
				setGameMode( GAME_COUNTDOWN );
			}
		} else if( _gameState == GAME_ON || _gameState == GAME_COUNTDOWN ) {
			p.image( MatchGameAssets.UI_GAME_LOGO, 244, 262 );
			_controls.update();
			_gamePlay.update();
		} else if( _gameState == GAME_OVER ) {
			p.image( MatchGameAssets.UI_GAME_LOGO, 244, 262 );
			p.image( MatchGameAssets.UI_WINNER_CONGRATS, 246, 172 );
			p.image( _winRGBImage, 243, 313, 540, 406 );
			if( p.millis() - _gameOverStartTime > 10000 ) setGameMode( GAME_PLAYER_DETECT );
		}
		
		drawBestTime();
		_confetti.update();
		
//		if( _isDebugging == true ) displayDebug();
	}
	
	protected void drawLoader() {
		DrawUtil.setDrawCenter(p);
		p.pushMatrix();
		p.translate( 515, 567 );
		p.rotate( ( P.TWO_PI / 10f ) * (float) p.frameCount );
		p.image( MatchGameAssets.UI_LOADER, 0, 0 );
		p.popMatrix();
		DrawUtil.setDrawCorner(p);
	}

	public void setBestTime( String bestTime ) {
		_bestGameTimeString = bestTime;
	}
	
	protected void drawBestTime() {
		if( _bestGameTimeString != null ) {
			DrawUtil.setDrawCorner(p);
			p.image( MatchGameAssets.UI_BEST_TIME, 43, 40 );
			MatchGameAssets.BEST_TIME_FONT_RENDERER.updateText( _bestGameTimeString );
			p.image( MatchGameAssets.BEST_TIME_FONT_RENDERER.getTextPImage(), 30, 60 );
		}
	}
		
	protected void displayDebug() {
		if( p.frameCount % ( _fps * 60 ) == 0 ) {
			P.println( "time: "+P.minute()+":"+P.second() );
		}
	}
	
	protected void drawDebug() {
		// draw depth image
		DrawUtil.setCenter( p );
		p.translate( 0, 0, -1350 );
		p.fill(255, 255);
		p.noStroke();
		p.rect(0, 0, KinectWrapper.KWIDTH*1.1f, KinectWrapper.KHEIGHT*1.1f);
		p.translate( 0, 0, 100 );
		p.rotateY( (float)Math.PI );
//		p.image( _kinectWrapper.getDepthImage(), 0, 0, _kinectWrapper.KWIDTH, _kinectWrapper.KHEIGHT );
		p.image( kinectWrapper.getDepthImage(), 0, 0, _stageWidth, _stageHeight );

	}

	
	
}

package com.haxademic.app.matchgame.game;

import java.util.ArrayList;

import com.haxademic.app.matchgame.MatchGame;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class MatchGamePlay {
	
	protected MatchGame p;
	
	protected int ROWS = 3;
	protected int COLS = 4;
	protected ArrayList<MatchGamePiece> _pieces;
	protected int[] _pieceMatchIDs;
	
	protected MatchGameControls _controls;
	protected int _cursorLeftPieceID = -1;
	protected int _cursorRightPieceID = -1;
	protected int _lastCursorRightPieceID = -1;
	protected int _lastCursorLeftPieceID = -1;
	protected boolean _twoPiecesSelected = false;
	
	protected float _matchHeldStartTime = -1;
	protected float MATCH_HELD_TIME = 1300f;
	protected float MATCH_SHOW_TIME = 1700f;
	protected float _matchTriggeredStartTime = -1;
	protected boolean _piecesMatched = false;
	float _controlDrawPercent = 0;
	
	protected int _gameStartTime = 0;
	protected int _gameBestTimeSeconds = 99999999;	
	protected int _countdownStartTime = -1;
	
	public MatchGamePlay( MatchGameControls controls ) {
		p = (MatchGame) P.p;
		_controls = controls;
		init();
	}
	
	protected void init() {
				
		// build game pieces
		_pieces = new ArrayList<MatchGamePiece>();
		int i = 0;
		for( int x=0; x < COLS; x++ ) {
			for( int y=0; y < ROWS; y++ ) {
				_pieces.add( new MatchGamePiece( i, x, y ) );
				i++;
			}
		}
		
		// set up array to distribute piece IDs 
		_pieceMatchIDs = new int[_pieces.size()];
		int curID = -1;
		for( i=0; i < _pieceMatchIDs.length; i++ ) {
			if( i % 2 == 0 ) curID++;
			_pieceMatchIDs[i] = curID;
		}
		
		// randomize and reset props
		reset();
	}
	
	public void reset() {
		// give game pieces new match IDs
		randomizeIntArray( _pieceMatchIDs );
		for( int i=0; i < _pieceMatchIDs.length; i++ ) {
			_pieces.get( i ).setMatchID( _pieceMatchIDs[i] );
			_pieces.get( i ).reset();
		}
		_gameStartTime = p.millis();

//		debug to make sure IDs are randomized and good
//		for( int i=0; i < _pieceMatchIDs.length; i++ ) {
//			P.println("match ID: "+_pieceMatchIDs[i]);
//		}
	}
	
	public void startGame() {
//		_curGameTime = 0;
	}
	
	public void startCountdown() {
		_countdownStartTime = p.millis();
	}

	protected void randomizeIntArray( int[] arr ) {
		for( int i=0; i < arr.length; i++ ) {
			int tmp = arr[i];
			int randomNum = MathUtil.randRange(0, arr.length - 1);
			arr[i] = arr[randomNum];
			arr[randomNum] = tmp;
		}
	}

	/** 
	 * Main game play update loop
	 */
	public void update() {
		boolean isActive = ( p.getGameMode() == MatchGame.GAME_ON );
		boolean isControlActive = ( _matchTriggeredStartTime == -1 );
		_controlDrawPercent = 0;

		if( isActive == true ) {
			if( isControlActive == true ) {
				getCursorCollisionPieceIDs();
				checkForTwoPiecesSelected();
				checkForMatchComplete();
			}
			drawTimer();
			checkGameIsDone();
		}
		checkFinishMatchDisplay();
		updateGamePieces( isActive, isControlActive );
		if( p.getGameMode() == MatchGame.GAME_COUNTDOWN ) drawCountdown();
		_controls.drawControls( _controlDrawPercent, isControlActive );
	}
	
	protected void drawCountdown() {
		DrawUtil.setColorForPImage( p );
		DrawUtil.setDrawCorner(p);
		if( p.millis() - _countdownStartTime > 3000 ) {
			p.setGameMode( MatchGame.GAME_ON );
			reset();
		} else if( p.millis() - _countdownStartTime > 2000 ) {
			p.image( MatchGameAssets.UI_COUNTDOWN_1, 345, 348 );
		} else if( p.millis() - _countdownStartTime > 1000 ) {
			p.image( MatchGameAssets.UI_COUNTDOWN_2, 345, 348 );
		} else if( p.millis() - _countdownStartTime > 0 ) {
			p.image( MatchGameAssets.UI_COUNTDOWN_3, 345, 348 );
		}
	}
	
	/**
	 * Update pieces and reset/check collisions with the 2 cursors
	 */
	protected void updateGamePieces( boolean isGameActive, boolean isControlActive ) {
		p.pushMatrix();
		boolean collision, isFlipped;
		for( int i=0; i < _pieces.size(); i++ ) {
			collision = false;
			if( _pieces.get( i ).isActive() == true ) collision = checkPieceForCollision( _pieces.get( i ) );
			isFlipped = ( isControlActive == false && collision == true ); 
			_pieces.get( i ).update( collision, isGameActive, isFlipped );
		}
		p.popMatrix();
	}
	
	/**
	 * Store current pieces IDs if they're under either cursor
	 */
	protected void getCursorCollisionPieceIDs() {
		// store last cursor collisions to see if they change
		_lastCursorLeftPieceID = _cursorLeftPieceID;
		_lastCursorRightPieceID = _cursorRightPieceID;
		_cursorLeftPieceID = -1;
		_cursorRightPieceID = -1;
		
		// find current cursor piece collisions
		for( int i=0; i < _pieces.size(); i++ ) {
			if( _pieces.get( i ).isActive() == true ) {
				if( _pieces.get( i ).rect.intersects( _controls.handLeftRect ) ) _cursorLeftPieceID = _pieces.get( i ).index();
				if( _pieces.get( i ).rect.intersects( _controls.handRightRect ) ) _cursorRightPieceID = _pieces.get( i ).index();
			}
		}
	}
	
	/**
	 * Check to see if an individual game piece is selected by either cursor
	 */
	protected boolean checkPieceForCollision( MatchGamePiece piece ) {
		// if either cursor intersected the piece, return true
		if( _cursorLeftPieceID == piece.index() || _cursorRightPieceID == piece.index() )
			return true;
		else
			return false;
	}
	
	/**
	 * If both cursors are over two different pieces, or it's a new pair of pieces, start the match timer.
	 * Keeps track of the selected and unselected states of the pieces, and sets flags for further piece-matching.
	 */
	protected void checkForTwoPiecesSelected() {
		if( _cursorLeftPieceID != -1 && _cursorRightPieceID != -1 && _cursorLeftPieceID != _cursorRightPieceID ) {
			boolean isNewPair = ( _lastCursorLeftPieceID != _cursorLeftPieceID || _lastCursorRightPieceID != _cursorRightPieceID );
			if( !_twoPiecesSelected || isNewPair ) {
				selectedTwoPieces();
			} 
		} else {
			if( _twoPiecesSelected ) unselectedTwoPieces();
		}
	}
	
	protected void selectedTwoPieces() {
		// if we had 2 selected, reset stuff so we can start this new pair fresh
		if( _twoPiecesSelected == true ) unselectedTwoPieces();
		// P.println("2 pieces selected!");
		_twoPiecesSelected = true;
		_matchHeldStartTime = p.millis();
	}
	
	protected void unselectedTwoPieces() {
		// P.println("2 UNselected");
		_twoPiecesSelected = false;
		_matchHeldStartTime = 0;
	}
	
	/**
	 * Check to see if two pieces have matched after the match timeout has been reached.
	 */
	protected void checkForMatchComplete() {
		// check for a match timeout
		if( _twoPiecesSelected == true ) {
			if( p.millis() - _matchHeldStartTime > MATCH_HELD_TIME ) {
				if( _pieces.get( _cursorLeftPieceID ).matchID() == _pieces.get( _cursorRightPieceID ).matchID() )
					piecesHeldComplete( true );
				else
					piecesHeldComplete( false );
			}
			_controlDrawPercent = ( (float) p.millis() - _matchHeldStartTime ) / MATCH_HELD_TIME;
		}
	}
	
	/**
	 * Tell the pieces that were selected and attempted to match whether they match or not
	 */
	protected void piecesHeldComplete( boolean didMatch ) {
		// start timer to re-enable cursor controls
		_matchTriggeredStartTime = p.millis();
		_piecesMatched = didMatch;
	}
	
	/**
	 * Dismiss pieces after they've been displayed 
	 */
	protected void checkFinishMatchDisplay() {
		boolean showPiecesTimeOut = ( MatchGame.KIDS_MODE == true ) ? true : ( p.millis() - _matchTriggeredStartTime > MATCH_SHOW_TIME );
		if( _matchTriggeredStartTime != -1 && showPiecesTimeOut ) {
			// kill or keep selected pieces
			for( int i=0; i < _pieces.size(); i++ ) {
				// check actual match between piece IDs)
				if( _pieces.get( i ).index() == _cursorLeftPieceID || _pieces.get( i ).index() == _cursorRightPieceID ) {
					_pieces.get( i ).matched( _piecesMatched );
				}
			}
			// reset cursor hover IDs
			_cursorLeftPieceID = -1;
			_cursorRightPieceID = -1;
			// reset matched flag and timer
			_piecesMatched = false;
			_matchTriggeredStartTime = -1;
		}
	}
	
	/**
	 * Draws the current game's time elapsed
	 */
	protected void drawTimer() {
		DrawUtil.setDrawCorner(p);
		int seconds = P.round( ( p.millis() - _gameStartTime ) * 0.001f );
		p.image( MatchGameAssets.UI_YOUR_TIME, 839, 672 );
		MatchGameAssets.TIME_FONT_RENDERER.updateText( formatTimeFromSeconds( seconds ) );
		p.image( MatchGameAssets.TIME_FONT_RENDERER.getTextPImage(), 809, 694 );
	}
	
	protected String formatTimeFromSeconds( int seconds ) {
		int minutes = P.floor( seconds / 60f );
		int secondsOnly = seconds % 60;
		String secondsText = ( secondsOnly < 10 ) ? "0"+secondsOnly : ""+secondsOnly;
		String minutesText = ( minutes < 10 ) ? "0"+minutes : ""+minutes;
		return minutesText+":"+secondsText;
	}
	
	/**
	 * Reset game if all pieces are cleared
	 */
	protected void checkGameIsDone() {
		int numIncompletePieces = 0;
		for( int i=0; i < _pieces.size(); i++ ) {
			if( _pieces.get( i ).isActive() == true ) numIncompletePieces++;
		}
		if( numIncompletePieces == 0 ) {
			finishGame();
		}
	}
	
	/**
	 * Run final game cleanup
	 */
	protected void finishGame() {
		storeBestGameTime();
		reset();
		p.setGameMode( MatchGame.GAME_OVER );
	}
	
	/**
	 * If time is best, store the formatted time string
	 */
	protected void storeBestGameTime() {
		int seconds = P.round( ( p.millis() - _gameStartTime ) * 0.001f );
		if( seconds < _gameBestTimeSeconds ) {
			_gameBestTimeSeconds = seconds;
			p.setBestTime( formatTimeFromSeconds( seconds ) );
		}
	}
}

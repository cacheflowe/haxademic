package com.haxademic.app.matchgame.game;

import java.util.ArrayList;

import processing.core.PImage;
import toxi.color.TColor;

import com.haxademic.app.matchgame.MatchGame;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.text.CustomFontText2D;

public class MatchGameAssets {
	protected static MatchGame p;
	
	public static TColor DARK_BLUE;
	public static TColor DARK_BLUE_TRANS;
	
	public static PImage PIECE_BACKFACE;
	public static PImage PIECE_COMPLETE;
	public static PImage PIECE_1;
	public static PImage PIECE_2;
	public static PImage PIECE_3;
	public static PImage PIECE_4;
	public static PImage PIECE_5;
	public static PImage PIECE_6;
	public static ArrayList<PImage> PIECE_IMAGES;
	
	public static PImage UI_BACKGROUND;
	public static PImage UI_CURSOR;
	public static PImage UI_CURSOR_BAD;
	public static PImage UI_BEST_TIME;
	public static PImage UI_YOUR_TIME;
	public static PImage UI_COUNTDOWN_1;
	public static PImage UI_COUNTDOWN_2;
	public static PImage UI_COUNTDOWN_3;
	public static PImage UI_PLAYER_DETECT;
	public static PImage UI_PLAYER_LOST;
	public static PImage UI_GAME_LOGO;
	public static PImage UI_STEP_UP;
	public static PImage UI_WINNER_CONGRATS;
	public static PImage UI_LOADER;
	
	// custom font rendering
	public static CustomFontText2D TIME_FONT_RENDERER;
	public static CustomFontText2D BEST_TIME_FONT_RENDERER;
	
	
	public MatchGameAssets() {
		
	}
	
	public static void initAssets() {
		p = (MatchGame) P.p;
		
		// colors
		DARK_BLUE = TColor.newHex("00457c");
		DARK_BLUE_TRANS = DARK_BLUE.copy();
		DARK_BLUE_TRANS.alpha = 0.5f;

		// game match pieces
		PIECE_BACKFACE =	p.loadImage( "../data/images/match-game/match-piece-backface.png" );
		PIECE_COMPLETE =	p.loadImage( "../data/images/match-game/match-piece-complete.png" );
		PIECE_1 = 			p.loadImage( "../data/images/match-game/match-piece-01.png" );
		PIECE_2 = 			p.loadImage( "../data/images/match-game/match-piece-02.png" );
		PIECE_3 = 			p.loadImage( "../data/images/match-game/match-piece-03.png" );
		PIECE_4 = 			p.loadImage( "../data/images/match-game/match-piece-04.png" );
		PIECE_5 = 			p.loadImage( "../data/images/match-game/match-piece-05.png" );
		PIECE_6 = 			p.loadImage( "../data/images/match-game/match-piece-06.png" );

		// use this array to use piece images based on array indexes 
		PIECE_IMAGES = new ArrayList<PImage>();
		PIECE_IMAGES.add( PIECE_1 );
		PIECE_IMAGES.add( PIECE_2 );
		PIECE_IMAGES.add( PIECE_3 );
		PIECE_IMAGES.add( PIECE_4 );
		PIECE_IMAGES.add( PIECE_5 );
		PIECE_IMAGES.add( PIECE_6 );
		
		// ui images
		UI_BACKGROUND		= p.loadImage( "../data/images/match-game/ui-background.png" );
		UI_CURSOR			= p.loadImage( "../data/images/match-game/ui-controls-cursor.png" );
		UI_CURSOR_BAD		= p.loadImage( "../data/images/match-game/ui-controls-cursor-bad.png" );
		UI_BEST_TIME		= p.loadImage( "../data/images/match-game/ui-best-time.png" );
		UI_YOUR_TIME		= p.loadImage( "../data/images/match-game/ui-your-time.png" );
		UI_COUNTDOWN_1		= p.loadImage( "../data/images/match-game/ui-countdown-1.png" );
		UI_COUNTDOWN_2		= p.loadImage( "../data/images/match-game/ui-countdown-2.png" );
		UI_COUNTDOWN_3		= p.loadImage( "../data/images/match-game/ui-countdown-3.png" );
		UI_PLAYER_DETECT	= p.loadImage( "../data/images/match-game/ui-player-detect.png" );
		UI_PLAYER_LOST		= p.loadImage( "../data/images/match-game/ui-player-lost.png" );
		UI_GAME_LOGO		= p.loadImage( "../data/images/match-game/ui-game-logo.png" );
		UI_STEP_UP			= p.loadImage( "../data/images/match-game/ui-step-up.png" );
		UI_WINNER_CONGRATS	= p.loadImage( "../data/images/match-game/ui-winner-congrats.png" );
		UI_LOADER			= p.loadImage( "../data/images/match-game/ui-loader.png" );
		
		// The font must be located in the sketch's "data" directory to load successfully
		TIME_FONT_RENDERER = new CustomFontText2D( p, "../data/fonts/GothamBold.ttf", 25.0f, DARK_BLUE.toARGB(), CustomFontText2D.ALIGN_RIGHT, 100, 25 );
		BEST_TIME_FONT_RENDERER = new CustomFontText2D( p, "../data/fonts/GothamBold.ttf", 40.0f, TColor.WHITE.toARGB(), CustomFontText2D.ALIGN_RIGHT, 130, 40 );

	}

}

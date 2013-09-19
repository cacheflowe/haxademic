package com.haxademic.app.kacheout.screens;

import java.util.ArrayList;

import processing.core.PImage;
import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.easing.EasingFloat3d;
import com.haxademic.core.math.easing.ElasticFloat;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class IntroScreenBordo {
	protected KacheOut p;

	protected final TColor MODE_SET_GREY = new TColor( TColor.newRGB( 96, 96, 96 ) );
	protected final TColor MODE_SET_BLUE = new TColor( TColor.newRGB( 0, 200, 234 ) );
	protected final TColor CACHEFLOWE_YELLOW = new TColor( TColor.newRGB( 255, 249, 0 ) );
	protected final TColor WHITE = new TColor( TColor.WHITE );

	//	protected EasingFloat3d _cdLogoLoc;
	//	protected EasingFloat3d _cdTextLoc;

	protected EasingFloat3d _imgBordoBelloLoc;
	protected EasingFloat3d _imgAirwalkLoc;
	protected EasingFloat3d _imgMotchkaLoc;
	//	protected EasingFloat3d _imgBoardpusherLoc;
	protected ArrayList<EasingFloat3d> _sponsorImages;

	protected EasingFloat3d _kacheOutLoc;
	protected EasingFloat3d _builtByLoc;
	protected EasingFloat3d _ufoLoc;
	protected EasingFloat3d _modeSetLogoLoc;
	protected ElasticFloat _modeSetLogoZ;
	protected EasingFloat3d _modeSetTextLoc;
	protected EasingFloat3d _cacheFloweLogoLoc;
	protected EasingFloat3d _cacheFloweTextLoc;
	protected EasingFloat3d _designByLoc;
	protected EasingFloat3d _designJonLoc;
	protected EasingFloat3d _designRyanLoc;

	protected int _frameCount;
	protected int _frame1, _frame12, _frame13, _frame14, _frame2, _frame3, _frame4, _frame5, _frame6, _frame7, _frame8;	// horrible, but whatever

	protected PImage _imgBordoBello;
	protected PImage _imgAirwalk;
	protected PImage _imgMotchka;
	//	protected PImage _imgBoardpusher;


	public IntroScreenBordo() {
		p = (KacheOut) P.p;

		// set up easing values for objects to fly onto screen
		_imgBordoBelloLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_imgAirwalkLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_imgMotchkaLoc = new EasingFloat3d( 0, 0, 0, 5 );
		
		

		_kacheOutLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_builtByLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_ufoLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_modeSetLogoLoc = new EasingFloat3d( 0, 0, 0, 4 );
		_modeSetLogoZ = new ElasticFloat( -1400f, 0.8f, 0.4f );
		_modeSetTextLoc = new EasingFloat3d( 0, 0, 0, 9 );
		_cacheFloweLogoLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_cacheFloweTextLoc = new EasingFloat3d( 0, 0, 0, 7 );
		_designByLoc = new EasingFloat3d( 0, 0, 0, 5 );
		_designJonLoc = new EasingFloat3d( 0, 0, 0, 7 );
		_designRyanLoc = new EasingFloat3d( 0, 0, 0, 9 );

		// set up keyframes
		_frame1 = 0;
		_frame12 = _frame1 + 160;
		_frame13 = _frame12 + 160;
		_frame14 = _frame13 + 160;
		_frame2 = _frame14 + 110;	// create denver
		_frame3 = _frame2 + 90;	// kacheout
		_frame4 = _frame3 + 90;		// built by
		_frame5 = _frame4 + 90;		// mode set
		_frame6 = _frame5 + 50;		// cacheflowe
		_frame7 = _frame6 + 80;		// designers
		_frame8 = _frame7 + 20;		// end


		_imgBordoBello = p.loadImage( FileUtil.getHaxademicDataPath() + "images/kacheout/dsw/galvanize-logo-mark.png" );
		_imgAirwalk = p.loadImage( FileUtil.getHaxademicDataPath() + "images/kacheout/dsw/g2-concept.png" );
		_imgMotchka = p.loadImage( FileUtil.getHaxademicDataPath() + "images/kacheout/dsw/oh-heck-yeah-logo.png" );
		//		_imgBoardpusher = p.loadImage( "../data/images/kacheout/bb/boardpusher.png" );


	}

	public void reset() {
		_frameCount = 0;

		//		_cdLogoLoc.setCurrentY( p.stageHeight() );
		//		_cdLogoLoc.setTargetY( p.stageHeight() );
		//		_cdTextLoc.setCurrentY( p.stageHeight() );
		//		_cdTextLoc.setTargetY( p.stageHeight() );

		_imgBordoBelloLoc.setCurrentY( p.stageHeight() );
		_imgBordoBelloLoc.setTargetY( p.stageHeight() );
		_imgAirwalkLoc.setCurrentY( p.stageHeight() );
		_imgAirwalkLoc.setTargetY( p.stageHeight() );
		_imgMotchkaLoc.setCurrentY( p.stageHeight() );
		_imgMotchkaLoc.setTargetY( p.stageHeight() );
		//		_imgBoardpusherLoc.setCurrentY( p.stageHeight() );
		//		_imgBoardpusherLoc.setTargetY( p.stageHeight() );


		_kacheOutLoc.setCurrentY( p.stageHeight() );
		_kacheOutLoc.setTargetY( p.stageHeight() );
		_builtByLoc.setCurrentY( p.stageHeight() );
		_builtByLoc.setTargetY( p.stageHeight() );
		_ufoLoc.setCurrentY( 100 );
		_ufoLoc.setTargetY( 100 );
		_ufoLoc.setCurrentX( -p.stageWidth() );
		_ufoLoc.setTargetX( -p.stageWidth() );
		_modeSetLogoLoc.setCurrentY( p.stageHeight() );
		_modeSetLogoLoc.setTargetY( p.stageHeight() * 10f );
		_modeSetTextLoc.setCurrentY( p.stageHeight() );
		_modeSetTextLoc.setTargetY( p.stageHeight() );
		_cacheFloweLogoLoc.setCurrentY( p.stageHeight() );
		_cacheFloweLogoLoc.setTargetY( p.stageHeight() );
		_cacheFloweTextLoc.setCurrentY( p.stageHeight() );
		_cacheFloweTextLoc.setTargetY( p.stageHeight() );
		_designByLoc.setCurrentY( p.stageHeight() );
		_designByLoc.setTargetY( p.stageHeight() );
		_designJonLoc.setCurrentY( p.stageHeight() );
		_designJonLoc.setTargetY( p.stageHeight() );
		_designRyanLoc.setCurrentY( p.stageHeight() );
		_designRyanLoc.setTargetY( p.stageHeight() );

		_modeSetLogoZ.setValue( -800f );
		_modeSetLogoZ.setTarget( -800f );

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
			_imgBordoBelloLoc.setTargetY( 0 );
		} else if( _frameCount == _frame12 ) {
			_imgBordoBelloLoc.setTargetY( -p.stageHeight() );
			_imgAirwalkLoc.setTargetY( 0 );
		} else if( _frameCount == _frame13 ) {
			_imgAirwalkLoc.setTargetY( -p.stageHeight() );
			_imgMotchkaLoc.setTargetY( 0 );
		} else if( _frameCount == _frame14 ) {
			_imgMotchkaLoc.setTargetY( -p.stageHeight() );
			_kacheOutLoc.setTargetY( 0 );
		} else if( _frameCount == _frame3 ) {
			_kacheOutLoc.setTargetY( -p.stageHeight() );
			_builtByLoc.setTargetY( -80 );
			_ufoLoc.setTargetX( 0 );
		} else if( _frameCount == _frame4 ) {
			_builtByLoc.setTargetY( -p.stageHeight() );
			_ufoLoc.setTargetX( p.stageWidth() );
			_modeSetLogoLoc.setTargetY( -60 );
			_modeSetTextLoc.setTargetY( 180 );
			_modeSetLogoZ.setTarget( 0 );
		} else if( _frameCount == _frame5 ) {
			_modeSetLogoLoc.setTargetY( -p.stageHeight() );
			_modeSetTextLoc.setTargetY( -p.stageHeight() );
			_cacheFloweLogoLoc.setTargetY( -60 );
			_cacheFloweTextLoc.setTargetY( 160 );
		} else if( _frameCount == _frame6 ) {
			_cacheFloweLogoLoc.setTargetY( -p.stageHeight() );
			_cacheFloweTextLoc.setTargetY( -p.stageHeight() );
			_designByLoc.setTargetY( -130 );
			_designJonLoc.setTargetY( 20 );
			_designRyanLoc.setTargetY( 140 );
		} else if( _frameCount == _frame7 ) {
			_designByLoc.setTargetY( -p.stageHeight() );
			_designJonLoc.setTargetY( -p.stageHeight() );
			_designRyanLoc.setTargetY( -p.stageHeight() );
		} else if( _frameCount == _frame8 ) {
			p.setGameMode( KacheOut.GAME_INSTRUCTIONS );
		}
	}

	protected void updatePositions() {
		// update eased positions
		//		_cdLogoLoc.update();
		//		_cdTextLoc.update();

		_imgBordoBelloLoc.update();
		_imgAirwalkLoc.update();
		_imgMotchkaLoc.update();
		//		_imgBoardpusherLoc.update();

		_kacheOutLoc.update();
		_builtByLoc.update();
		_ufoLoc.update();
		_modeSetLogoLoc.update();
		_modeSetLogoZ.update();
		_modeSetTextLoc.update();
		_cacheFloweLogoLoc.update();
		_cacheFloweTextLoc.update();
		_designByLoc.update();
		_designJonLoc.update();
		_designRyanLoc.update();
	}

	protected void drawObjects() {
		// set up for drawing objects
		p.pushMatrix();
		DrawUtil.setCenter( p );
		p.translate( 0, 0, p.gameBaseZ() );


		drawImageAtLoc( _imgBordoBello, _imgBordoBelloLoc.x(), _imgBordoBelloLoc.y(), _imgBordoBelloLoc.z() );
		drawImageAtLoc( _imgAirwalk, _imgAirwalkLoc.x(), _imgAirwalkLoc.y(), _imgAirwalkLoc.z() );
		drawImageAtLoc( _imgMotchka, _imgMotchkaLoc.x(), _imgMotchkaLoc.y(), _imgMotchkaLoc.z() );
		//		drawImageAtLoc( _imgBoardpusher, _imgBoardpusherLoc.x(), _imgBoardpusherLoc.y(), _imgBoardpusherLoc.z() );


		// draw create denver text & "presents"
		//		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.CREATE_DENVER_LOGO ), _cdLogoLoc.valueX(), _cdLogoLoc.valueY(), 0, WHITE.toARGB() );
		//		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.CREATE_DENVER_TEXT ), _cdTextLoc.valueX(), _cdTextLoc.valueY(), 0, WHITE.toARGB() );

		// draw kacheout logo
		int kacheOutAnim = p.frameCount % 40;
		if( kacheOutAnim < 20 ) {
			drawObjectAtLoc( p.meshPool.getMesh( KacheOut.KACHEOUT_LOGO ), _kacheOutLoc.x(), _kacheOutLoc.y(), -200, WHITE.toARGB() );
		} else {
			drawObjectAtLoc( p.meshPool.getMesh( KacheOut.KACHEOUT_LOGO_ALT ), _kacheOutLoc.x(), _kacheOutLoc.y(), -200, WHITE.toARGB() );
		}

		// draw built by: text & ufo
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.BUILT_BY_TEXT ), _builtByLoc.x(), _builtByLoc.y(), 0, WHITE.toARGB() );
		int ufoAnim = p.frameCount % 12;
		if( ufoAnim < 4 ) {
			drawObjectAtLoc( p.meshPool.getMesh( KacheOut.UFO_1 ), _ufoLoc.x(), _ufoLoc.y(), 0, WHITE.toARGB() );
		} else if( ufoAnim < 8 ) {
			drawObjectAtLoc( p.meshPool.getMesh( KacheOut.UFO_2 ), _ufoLoc.x(), _ufoLoc.y(), 0, WHITE.toARGB() );
		} else if( ufoAnim <= 12 ) {
			drawObjectAtLoc( p.meshPool.getMesh( KacheOut.UFO_3 ), _ufoLoc.x(), _ufoLoc.y(), 0, WHITE.toARGB() );
		}

		// draw mode set logo & text
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.MODE_SET_LOGO ), _modeSetLogoLoc.x(), _modeSetLogoLoc.y(), _modeSetLogoZ.val(), MODE_SET_BLUE.toARGB() );
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.MODE_SET_LOGOTYPE ), _modeSetTextLoc.x(), _modeSetTextLoc.y(), 0, MODE_SET_GREY.toARGB() );

		// draw cacheflowe logo & text
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.CACHEFLOWE_LOGO ), _cacheFloweLogoLoc.x(), _cacheFloweLogoLoc.y(), _cacheFloweLogoLoc.z(), CACHEFLOWE_YELLOW.toARGB() );
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.CACHEFLOWE_LOGOTYPE), _cacheFloweTextLoc.x(), _cacheFloweTextLoc.y(), _cacheFloweTextLoc.z(), CACHEFLOWE_YELLOW.toARGB() );

		// draw design credits
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.DESIGN_BY ), _designByLoc.x(), _designByLoc.y(), _designByLoc.z(), WHITE.toARGB() );
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.JON_DESIGN), _designJonLoc.x(), _designJonLoc.y(), _designJonLoc.z(), WHITE.toARGB() );
		drawObjectAtLoc( p.meshPool.getMesh( KacheOut.RYAN_DESIGN), _designRyanLoc.x(), _designRyanLoc.y(), _designRyanLoc.z(), WHITE.toARGB() );



		// reset
		p.popMatrix();
	}

	protected void drawObjectAtLoc( WETriangleMesh mesh, float x, float y, float z, int color ) {
		if( y > -p.stageHeight() && y < p.stageHeight() ) {
			p.pushMatrix();
			p.translate( x, y, z );
			p.fill( color );
			p.noStroke();
			p.toxi.mesh( mesh );
			p.popMatrix();	
		}
	}

	protected void drawImageAtLoc( PImage image, float x, float y, float z ) {
		if( y > -p.stageHeight() && y < p.stageHeight() ) {
			p.pushMatrix();
			p.translate( x, y, z );
			p.fill( 255 );
			p.noStroke();
			p.image( image, x, y );
			p.popMatrix();	
		}
	}
}


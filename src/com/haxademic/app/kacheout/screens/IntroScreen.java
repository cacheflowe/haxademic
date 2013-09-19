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

public class IntroScreen {
	protected KacheOut p;

	protected final TColor MODE_SET_GREY = new TColor( TColor.newRGB( 96, 96, 96 ) );
	protected final TColor MODE_SET_BLUE = new TColor( TColor.newRGB( 0, 200, 234 ) );
	protected final TColor CACHEFLOWE_YELLOW = new TColor( TColor.newRGB( 255, 249, 0 ) );
	protected final TColor WHITE = new TColor( TColor.WHITE );

	protected ArrayList<EasingFloat3d> _sponsorImagePositions;
	protected ArrayList<PImage> _sponsorImages;
	protected int _sponsorImageShowTimeFrames = 160;

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

	protected ArrayList<Number> _frameLengths;
	protected int _curDisplayIndex = 0;
	protected int _frameCount;


	public IntroScreen( String sponsorImagesFromPropsFile ) {
		p = (KacheOut) P.p;

		_frameLengths = new ArrayList<Number>();
		
		// add sponsor images to the front of the credits sequence
		if( sponsorImagesFromPropsFile.length() > 0 ) {
			_sponsorImagePositions = new ArrayList<EasingFloat3d>();
			_sponsorImages = new ArrayList<PImage>();
			
			String[] files = sponsorImagesFromPropsFile.split(",");
			for( int i=0; i < files.length; i++ ) {
				_frameLengths.add( _sponsorImageShowTimeFrames * i );
				_sponsorImagePositions.add( new EasingFloat3d( 0, 0, 0, 6 ) );
				_sponsorImages.add( p.loadImage( FileUtil.getHaxademicDataPath() + files[i] ) );
			}
		}
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + _sponsorImageShowTimeFrames );

		// custom object positions
		_kacheOutLoc = new EasingFloat3d( 0, 0, 0, 6 );
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + 90 );

		_builtByLoc = new EasingFloat3d( 0, 0, 0, 6 );
		_ufoLoc = new EasingFloat3d( 0, 0, 0, 6 );
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + 50 );

		_modeSetLogoLoc = new EasingFloat3d( 0, 0, 0, 4 );
		_modeSetLogoZ = new ElasticFloat( -1400f, 0.8f, 0.4f );
		_modeSetTextLoc = new EasingFloat3d( 0, 0, 0, 9 );
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + 75 );

		_cacheFloweLogoLoc = new EasingFloat3d( 0, 0, 0, 6 );
		_cacheFloweTextLoc = new EasingFloat3d( 0, 0, 0, 7 );
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + 40 );

		_designByLoc = new EasingFloat3d( 0, 0, 0, 6 );
		_designJonLoc = new EasingFloat3d( 0, 0, 0, 7 );
		_designRyanLoc = new EasingFloat3d( 0, 0, 0, 9 );
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + 50 );

		// end
		_frameLengths.add( _frameLengths.get(_frameLengths.size()-1).intValue() + 20 );

	}

	public void reset() {
		_frameCount = 0;
		_curDisplayIndex = 0;

		// move objects to the bottom of the screen
		for( int i=0; i < _sponsorImagePositions.size(); i++ ) {
			_sponsorImagePositions.get(i).setCurrentY( p.stageHeight() );
			_sponsorImagePositions.get(i).setTargetY( p.stageHeight() );
		}

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
		_modeSetLogoZ.setValue( -800f );
		_modeSetLogoZ.setTarget( -800f );

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
	}

	public void update() {
		updateAnimationsOnFrameCount();
		updatePositions();
		drawObjects();
		_frameCount++;
	}

	protected void updateAnimationsOnFrameCount() {
		
		for( int i=0; i < _sponsorImages.size(); i++ ) {
			if( _frameCount == _frameLengths.get(i).intValue() ) {
				// always move the current sponsor image
				_sponsorImagePositions.get(i).setTargetY( 0 );
				// move previous image off
				if( i > 0 ) {
					_sponsorImagePositions.get(i-1).setTargetY( -p.stageHeight() );
				}
			}
		}

		
		// animate on certain frames
		if( _frameCount == _frameLengths.get( _sponsorImages.size() + 0 ).intValue() ) {
			// move last sponsor image off
			_sponsorImagePositions.get( _sponsorImages.size() - 1 ).setTargetY( -p.stageHeight() );
			_kacheOutLoc.setTargetY( 0 );
		} else if( _frameCount == _frameLengths.get( _sponsorImages.size() + 1 ).intValue() ) {
			_kacheOutLoc.setTargetY( -p.stageHeight() );
			_builtByLoc.setTargetY( -80 );
			_ufoLoc.setTargetX( 0 );
		} else if( _frameCount == _frameLengths.get( _sponsorImages.size() + 2 ).intValue() ) {
			_builtByLoc.setTargetY( -p.stageHeight() );
			_ufoLoc.setTargetX( p.stageWidth() );
			_modeSetLogoLoc.setTargetY( -60 );
			_modeSetTextLoc.setTargetY( 180 );
			_modeSetLogoZ.setTarget( 0 );
		} else if( _frameCount == _frameLengths.get( _sponsorImages.size() + 3 ).intValue() ) {
			_modeSetLogoLoc.setTargetY( -p.stageHeight() );
			_modeSetTextLoc.setTargetY( -p.stageHeight() );
			_cacheFloweLogoLoc.setTargetY( -60 );
			_cacheFloweTextLoc.setTargetY( 160 );
		} else if( _frameCount == _frameLengths.get( _sponsorImages.size() + 4 ).intValue() ) {
			_cacheFloweLogoLoc.setTargetY( -p.stageHeight() );
			_cacheFloweTextLoc.setTargetY( -p.stageHeight() );
			_designByLoc.setTargetY( -130 );
			_designJonLoc.setTargetY( 20 );
			_designRyanLoc.setTargetY( 140 );
		} else if( _frameCount == _frameLengths.get( _sponsorImages.size() + 5 ).intValue() ) {
			_designByLoc.setTargetY( -p.stageHeight() );
			_designJonLoc.setTargetY( -p.stageHeight() );
			_designRyanLoc.setTargetY( -p.stageHeight() );
		} else if( _frameCount == _frameLengths.get( _sponsorImages.size() + 6 ).intValue() ) {
			p.setGameMode( KacheOut.GAME_INSTRUCTIONS );
		}
	}

	protected void updatePositions() {
		// update eased positions
		for( int i=0; i < _sponsorImagePositions.size(); i++ ) {
			_sponsorImagePositions.get(i).update();
		}

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

		// draw sponsor images
		for( int i=0; i < _sponsorImages.size(); i++ ) {
			drawImageAtLoc( _sponsorImages.get(i), _sponsorImagePositions.get(i).x(), _sponsorImagePositions.get(i).y(), _sponsorImagePositions.get(i).z() );
		}

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


package com.haxademic.app.matchgame.game;

import java.awt.Rectangle;

import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.Vec3D;
import SimpleOpenNI.SimpleOpenNI;

import com.haxademic.app.matchgame.MatchGame;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat3d;

public class MatchGameControls {

	protected MatchGame p;
	
	public SimpleOpenNI  _kinectContext;
	protected int _userLeftPixel;
	protected int _userRightPixel;
	protected int _curUserId = -1;
	protected float _controlsMultiplier = 1;
	protected EasingFloat3d _handLeft;
	protected EasingFloat3d _handRight;
	public Rectangle handLeftRect;
	public Rectangle handRightRect;
	protected float CURSOR_DIAMETER = 36;
	protected float CURSOR_EASING_FACTOR = 7;
	
	protected boolean _userInGameArea = false;
		
	protected PVector _utilPVec = new PVector();
	protected PVector _utilPVec2 = new PVector();
	
	protected PImage testHead;
	protected PImage testHand;
	
	public MatchGameControls() {
		p = (MatchGame) P.p;
		init();
	}
	
	protected void init() {
		// Set Kinect user/skeleton tracking - most of the setup and updating happens in PAppletHax
		_kinectContext = p.kinectWrapper.openni();
		enableSkeletonTracking();
		
		// get kinect player rectangle range
		float halfKinectW = KinectWrapper.KWIDTH / 2f;
		_userLeftPixel =  P.round( halfKinectW - halfKinectW * MatchGame.KINECT_WIDTH_PERCENT );
		_userRightPixel = P.round( halfKinectW + halfKinectW * MatchGame.KINECT_WIDTH_PERCENT );
		
		// set ratio of controls based on screen size vs kinect depth
		_handLeft = new EasingFloat3d( p.width/2, p.height/2, 0, CURSOR_EASING_FACTOR );
		_handRight = new EasingFloat3d( p.width/2, p.height/2, 0, CURSOR_EASING_FACTOR );
		handLeftRect = new Rectangle( 0, 0, 1, 1 );
		handRightRect = new Rectangle( 0, 0, 1, 1 );
	}

	/** 
	 * Main game play update loop
	 */
	public void update() {
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);

//		P.println("userIsInGameArea() :: "+userIsInGameArea());
		
		// find closest skeleton and only use that one
		getClosestUser();
		
		// find hands & ease position
		getHands( _curUserId );

		// draw the skeletons for debugging
//		drawSkeletons();
//		drawUserBlobs();
		
		DrawUtil.setDrawCenter(p);
	}
	
	public void drawControls( float heldTimePercent, boolean controlsActive ) {
		DrawUtil.setDrawCenter(p);
//		drawHead( _curUserId );
		drawHands( heldTimePercent, controlsActive );		
	}
	
	public EasingFloat3d getRightHandPos() {
		return _handRight;
	}
	
	public EasingFloat3d getLeftHandPos() {
		return _handLeft;
	}
	
	public boolean userIsInGameArea() {
		// loop through point grid and skip over pixels on an interval, finding the horizonal extents of an object in the appropriate range
		int pixelDepth;
		boolean objectInRect = false;
		
		// loop through kinect data within player's control range
		for ( int x = _userLeftPixel; x < _userRightPixel; x += MatchGame.K_PIXEL_SKIP ) {
			for ( int y = MatchGame.KINECT_TOP; y < MatchGame.KINECT_BOTTOM; y += MatchGame.K_PIXEL_SKIP ) {
				if( objectInRect == false ) {
					pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > MatchGame.KINECT_MIN_DIST && pixelDepth < MatchGame.KINECT_MAX_DIST ) {
						objectInRect = true;
						break;
					}
				}
			}
		}
		return objectInRect;
	}
	
	public void enableSkeletonTracking() {
		_kinectContext.enableUser( SimpleOpenNI.SKEL_PROFILE_ALL, this );	// optional `this` routes OPENNI callbacks here instead of PApplet. nice.
	}
	
	public void stopTrackingAllUsers() {
//		int[] users = _kinectContext.getUsers();
//		for(int i=0; i < users.length; i++) { 
//			_kinectContext.stopTrackingSkeleton( users[i] );
//		}
//		_curUserId = -1;
	}
	
	public boolean hasASkeleton() {
		getClosestUser();
		if( _curUserId != -1 ) return true;
		return false;
	}
	
	public void getClosestUser() {
		int[] users = _kinectContext.getUsers();
		if( users.length == 0 ) {
			_curUserId = -1;
		} else {
			_curUserId = -1;
			// use skeleton closest to center, who's in the min/mix distance boundaries
			// find & track closest user that is still on-screen userHasHands()
			float xDist = 999999f;
			boolean isInZBounds = false;
			for(int i=0; i < users.length; i++) { 
				_kinectContext.getCoM( users[i], _utilPVec );							// PVec comes back with real-world `z` in millimeters
				isInZBounds = ( _utilPVec.z > MatchGame.KINECT_MIN_DIST && _utilPVec.z < MatchGame.KINECT_MAX_DIST );
				// if in-z-bounds, user.x is legit, closer-to-center & has a good skeleton...
				if( isInZBounds == true && _utilPVec.x != 0.0 && P.abs(_utilPVec.x) < xDist && userHasHands( users[i] ) ) {
					_curUserId = users[i];
					xDist = _utilPVec.x;
					// P.println( "closest user: "+users[i]+" at "+zDist );
				}
			}
		}
		// P.println("curUser: "+_curUserId+" of "+_kinectContext.getUsers().length);
	}
	
	// returns true if OpenNI says we've got a left hand
	public boolean userHasHands( int userId ) {
		if( _kinectContext.getJointPositionSkeleton( userId, SimpleOpenNI.SKEL_LEFT_HAND, _utilPVec ) > 0.001f ) {
			return true;
		} else {
			// P.println("USER GONE!");
			return false;
		}
	}
	
	// find and project the hand positions to 2d space
	public void getHands( int userId ) {
		// PVecs are returned from SimpleOpenNI API
		PVector _utilPVec = new PVector();	// limb PVec in 3D space
		PVector _utilPVec2 = new PVector();	// limb PVec in 2D space
		float confidence = 0f;
		p.fill(0,255,255);
		DrawUtil.setDrawCorner(p);

		// left hand
		confidence = _kinectContext.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_LEFT_HAND,_utilPVec);
		if (confidence > 0.001f) {			
			_kinectContext.convertRealWorldToProjective(_utilPVec,_utilPVec2);
			mapHandCursorLocation( _handLeft, _utilPVec2 );
		}
		_handLeft.update();
		
		// right hand
		confidence = _kinectContext.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,_utilPVec);
		if (confidence > 0.001f) {			
			_kinectContext.convertRealWorldToProjective(_utilPVec,_utilPVec2);
			mapHandCursorLocation( _handRight, _utilPVec2 );
		}
		_handRight.update();
		
		handLeftRect.x = (int) _handLeft.x();
		handLeftRect.y = (int) _handLeft.y();
		handRightRect.x = (int) _handRight.x();
		handRightRect.y = (int) _handRight.y();
	}
	
	// map the kinect 2d location to the game's size w/additional movement factor
	protected void mapHandCursorLocation( EasingFloat3d handEasingFloat, PVector handVec2d ) {
		handEasingFloat.setTargetX( ( p.width * 0.5f ) + ( -0.5f + MathUtil.getPercentWithinRange( 0, KinectWrapper.KWIDTH, handVec2d.x ) ) * p.width * MatchGame.CURSOR_MULTIPLIER );
		handEasingFloat.setTargetY( ( p.height * 0.5f ) + ( -0.5f + MathUtil.getPercentWithinRange( 0, KinectWrapper.KHEIGHT, handVec2d.y ) ) * p.height * MatchGame.CURSOR_MULTIPLIER );
	}
	
	public void drawHands( float heldTimePercent, boolean controlsActive ) {
		if( heldTimePercent > 0 ) {
			p.fill( MatchGameAssets.DARK_BLUE.toARGB() );
			p.arc( _handLeft.x(), _handLeft.y(), CURSOR_DIAMETER, CURSOR_DIAMETER, 0, heldTimePercent * (float) P.TWO_PI );
			p.arc( _handRight.x(), _handRight.y(), CURSOR_DIAMETER, CURSOR_DIAMETER, 0, heldTimePercent * (float) P.TWO_PI );
		}
		// always draw outer cursor circle
		DrawUtil.setColorForPImage( p );
		if( controlsActive == false ) DrawUtil.setPImageAlpha( p, 0.3f );
		if( _curUserId != -1 ) {
			p.image( MatchGameAssets.UI_CURSOR, _handLeft.x(), _handLeft.y() );
			p.image( MatchGameAssets.UI_CURSOR, _handRight.x(), _handRight.y() );
		} else {
			p.image( MatchGameAssets.UI_CURSOR_BAD, _handLeft.x(), _handLeft.y() );
			p.image( MatchGameAssets.UI_CURSOR_BAD, _handRight.x(), _handRight.y() );
		}
		DrawUtil.resetPImageAlpha( p );
	}
	
 	public void drawHead( int userId )
	{
		float confidence = _kinectContext.getJointPositionSkeleton( userId, SimpleOpenNI.SKEL_HEAD, _utilPVec );
		_kinectContext.convertRealWorldToProjective( _utilPVec, _utilPVec2 );
		if (confidence > 0.001f) {
			DrawUtil.setColorForPImage( p );
			p.image( testHead, _utilPVec2.x, _utilPVec2.y + testHead.height/2 );
		}
	}
	
	protected void drawUserBlobs() {
		int[] users = _kinectContext.getUsers();
		for(int i=0; i < users.length; i++) {
			if( _curUserId == users[i] ) {
				drawUserBlob( users[i], p.color(0, 255, 0, 255) );
			} else {
				drawUserBlob( users[i], p.color(0, 255, 0, 255) );
			}
		}
	}
	
	protected void drawUserBlob( int user, int userColor ) {
		PImage userImg = p.createImage( 640, 480, P.ARGB );
		int[] kinectPixels = _kinectContext.getUsersPixels( user );
		for ( int i = 0; i < userImg.pixels.length; i++ ) {
			if( kinectPixels[i] == 1 ) userImg.pixels[i] = userColor;  
		}
		DrawUtil.setColorForPImage(p);
		p.image( userImg, 0, p.height - 480 );
	}
	
	protected void drawSkeletons() {
		int[] users = _kinectContext.getUsers();
		for(int i=0; i < users.length; i++) {
			if( _curUserId == users[i] ) {
				p.stroke( 0, 255, 0 );
			} else {
				p.stroke( 255, 0, 0 );
			}
			drawSkeleton( users[i] );
		}
	}


	// draw the skeleton with the selected joints
	public void drawSkeleton( int userId )
	{
		// default limb drawing
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		_kinectContext.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  
	}

	// -----------------------------------------------------------------
	// SimpleOpenNI events

	public void onNewUser(int userId)
	{
		P.println("onNewUser - userId: " + userId);
		P.println("  start pose detection");

		_kinectContext.startPoseDetection("Psi",userId);
		_kinectContext.requestCalibrationSkeleton(userId,true);
	}

	public void onLostUser(int userId)
	{
		P.println("onLostUser - userId: " + userId);
	}

	public void onStartCalibration(int userId)
	{
		P.println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull)
	{
		P.println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);

		if (successfull) 
		{ 
			P.println("  User calibrated !!!");
			_kinectContext.startTrackingSkeleton(userId); 
		} 
		else 
		{ 
			P.println("  Failed to calibrate user !!!");
			P.println("  Start pose detection");
			_kinectContext.startPoseDetection("Psi",userId);
		}
	}

	public void onStartPose(String pose,int userId)
	{
		P.println("onStartPose - userId: " + userId + ", pose: " + pose);
		P.println(" stop pose detection");

		_kinectContext.stopPoseDetection(userId); 
		_kinectContext.requestCalibrationSkeleton(userId, true);

	}

	public void onEndPose(String pose,int userId)
	{
		P.println("onEndPose - userId: " + userId + ", pose: " + pose);
	}
}

package com.haxademic.core.hardware.depthcamera.cameras;

import com.haxademic.core.debug.DebugUtil;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class KinectWrapperV1 
implements IDepthCamera{
	
	// The sensor has an angular field of view of 57 degrees horizontally and 43 degrees vertically, while the motorized pivot is capable of tilting the sensor up to 27 degrees either up or down
	// http://en.wikipedia.org/wiki/Field_of_view
	
	protected PApplet p;
	protected SimpleOpenNI _kinect;
	protected boolean _kinectActive = true;
	public static boolean KINECT_ERROR_SHOWN = false;
	protected int cameraIndex = 0;
	
	public static int KWIDTH = 640;
	public static int KHEIGHT = 480;

	public int[] _depthArray;
	public PVector[] _realWorldMap;
	public boolean _flipped = false;
	protected int _hardwareTilt = 0;

	// multithread the kinect communication
	protected KinectUpdater _loader;
	protected Thread _updateThread;
	protected Boolean _updateComplete = true;
	

	public KinectWrapperV1( PApplet p, boolean initRGB, boolean initDepthImage ) {
		this(p, initRGB, initDepthImage, 0);
	}
	
	public KinectWrapperV1( PApplet p, boolean initRGB, boolean initDepthImage, int cameraIndex ) {
		this.p = p;
		this.cameraIndex = cameraIndex;
		
		// init camera object. 2nd camera (and theoretically beyond) need a different constructor
		if(this.cameraIndex == 0) {
			_kinect = new SimpleOpenNI( p, SimpleOpenNI.RUN_MODE_DEFAULT );
		} else {
			_kinect = new SimpleOpenNI( SimpleOpenNI.RUN_MODE_MULTI_THREADED, p, 1 );
		}

		// init kinect properties
		boolean initSuccess = _kinect.enableDepth();
		if(initRGB) _kinect.enableRGB();
//		_kinect.setMirror(false);
		// _kinect.enableIR();  // IR doesn't like being enabled off the bat - it kills the RGB camera?!
		
		// enable depthMap generation 
		if(initSuccess == false && KINECT_ERROR_SHOWN == false) {
			DebugUtil.alert("Can't access the Kinect. Make sure it's plugged into the computer and a power outlet.");
			_kinectActive = false;
			KINECT_ERROR_SHOWN = true;
			p.exit();
		} else {
			// capture one frame of depth data so the array exists. otherwise the new threading would return null on the first frame
			_depthArray = _kinect.depthMap();
		}
	}
	
	
	class KinectUpdater implements Runnable {
		public KinectUpdater() {}    

		public void run() {
			if(_kinect != null && _kinect.isInit() == true) {
				_kinect.update();
				_depthArray = _kinect.depthMap();
				if(_flipped == true) reverse(_depthArray);
				_realWorldMap = _kinect.depthMapRealWorld();
				_updateComplete = true;
			}
		} 
	}

	public void update() {
		if( _kinectActive == true && _updateComplete == true ) {
			_updateComplete = false;
			if(_loader == null) _loader = new KinectUpdater();
			_updateThread = new Thread( _loader );
			_updateThread.start();
		}
	}
	
	public void reverse(final int[] array) {
	    if (array == null) {
	        return;
	    }
	    int i = 0;
	    int j = array.length - 1;
	    int tmp;
	    while (j > i) {
	        tmp = array[j];
	        array[j] = array[i];
	        array[i] = tmp;
	        j--;
	        i++;
	    }
	}
	
	public PImage getDepthImage() {
		return _kinect.depthImage();
	}
	
	public PImage getIRImage() {
		return _kinect.irImage();
	}
	
	public PImage getRgbImage() {
		return _kinect.rgbImage();
	}
	
	public int rgbWidth() {return 640;};
	public int rgbHeight() {return 480;};
	
	public int[] getDepthData() {
		return _depthArray;
	}
	
	public void enableDepth( boolean enable ) {
		if( _kinectActive == true && enable == true ) _kinect.enableDepth();
	}
	
	public void enableIR( boolean enable ) {
		if( _kinectActive == true && enable == true ) _kinect.enableIR();
	}
	
	public void enableRGB( boolean enable ) {
		if( _kinectActive == true && enable == true ) _kinect.enableRGB();
	}
	
	public boolean isActive() {
		return _kinectActive;
	}
	
	public SimpleOpenNI openni() {
		return _kinect;
	}
	
	public void setMirror( boolean mirrored ) {
		_kinect.setMirror( mirrored );
	}
	
	public void setFlipped( boolean flipped ) {
		_flipped = flipped;
	}
	
	public boolean isMirrored() {
		return _kinect.mirror();
	}
	
	public void drawCamFrustum() {
		_kinect.drawCamFrustum();
	}
	
	public void tiltUp() {
//		_hardwareTilt += 5;
//		_hardwareTilt = P.constrain(_hardwareTilt, -20, 30);
//		_kinect.tilt(_hardwareTilt);
	}
	
	public void tiltDown() {
//		_hardwareTilt -= 5;
//		_hardwareTilt = P.constrain(_hardwareTilt, -20, 30);
//		_kinect.tilt(_hardwareTilt);
	}
	
	public void drawPointCloudForRect( PApplet p, boolean mirrored, int pixelSkip, float alpha, float scale, float depthClose, float depthFar, int top, int right, int bottom, int left ) {
		p.pushMatrix();

		// Translate and rotate
		int curZ;
		
		// Scale up by 200
		float scaleFactor = scale;
		
		p.noStroke();
		
		for (int x = left; x < right; x += pixelSkip) {
			for (int y = top; y < bottom; y += pixelSkip) {
				curZ = getMillimetersDepthForKinectPixel(x, y);
				// draw a point within the specified depth range
				if( curZ > depthClose && curZ < depthFar ) {
					p.fill( 255, alpha * 255f );
				} else {
					p.fill( 255, 0, 0, alpha * 255f );
				}
				p.pushMatrix();
				p.translate( x * scaleFactor, y * scaleFactor, scaleFactor * curZ/40f );
				// Draw a point
				p.point(0, 0);
				p.rect(0, 0, 4, 4);
				p.popMatrix();
			}
		}
		p.popMatrix();
	}
	
	/**
	 * Shuts down Kinect properly when the PApplet shuts down
	 */
	public void stop() {
		if( _kinectActive ) {
			_kinect.close();
			_kinect.dispose();
		}
	}
	
	public PVector getRealWorldDepthForKinectPixel( int x, int y ) {
		int offset = x + y * KWIDTH;
		if( _depthArray[offset] == 0 || offset >= _realWorldMap.length ) {
			return null;
		} else {
			return _realWorldMap[offset];			
		}
	}
	
	public int getMillimetersDepthForKinectPixel( int x, int y ) {
		int offset = x + y * KWIDTH;
		if( offset >= _depthArray.length ) {
			return 0;
		} else {
			return _depthArray[offset];
		}
	}
	
	/////////////////////////////////
	// Skeleton helpers
	/////////////////////////////////
	public void startTrackingSkeleton(int userId) {
		_kinect.startTrackingSkeleton(userId);
		
	}
	public void enableUser(int i) {
		_kinect.enableUser(i);
		
	}
	public int[] getUsers() {
		return _kinect.getUsers();
	}
	public void getCoM(int i, PVector _utilPVec) {
		_kinect.getCoM(i, _utilPVec);
		
	}
	public float getJointPositionSkeleton(int userId, int skelLeftHand,
			PVector _utilPVec) {
		return _kinect.getJointPositionSkeleton(userId, skelLeftHand, _utilPVec);
	}
	public void convertRealWorldToProjective(PVector _utilPVec,
			PVector _utilPVec2) {
		_kinect.convertRealWorldToProjective(_utilPVec, _utilPVec2);	
	}
	public void drawLimb(int userId, int skelHead, int skelNeck) {
		_kinect.drawLimb(userId, skelHead, skelNeck);
	}
	
	
	
	
	
	/*
	// should go into any app that uses skeleton detection
	public void onNewUser(SimpleOpenNI curContext,int userId)
	{
	  println("onNewUser - userId: " + userId);
	  println("\tstart tracking skeleton");
	  p.kinectWrapper.startTrackingSkeleton(userId);
	}

	public void onLostUser( SimpleOpenNI curContext, int userId ) {
	  println("onLostUser - userId: " + userId);
	}

	public void onVisibleUser( SimpleOpenNI curContext, int userId ) {
	  // println("onVisibleUser - userId: " + userId);
	}
	*/
}

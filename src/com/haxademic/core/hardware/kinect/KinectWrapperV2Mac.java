package com.haxademic.core.hardware.kinect;

import org.openkinect.processing.Kinect2;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

//Kinect Wrapper for Microsoft Kinect V2 for Windows
public class KinectWrapperV2Mac implements IKinectWrapper {
	
	protected PApplet p;
	protected Kinect2 _kinect;
	protected boolean _kinectActive = true;
	public static boolean KINECT_ERROR_SHOWN = false;

	public static int KWIDTH = 512;
	public static int KHEIGHT = 424;

	//Kinect V2
	public int[] _depthArray;
	public PVector[] _realWorldMap;
	private boolean mirror = false;
	public boolean _flipped = false;
	
	
	public KinectWrapperV2Mac( PApplet p, boolean initDepth, boolean initRGB, boolean initDepthImage ) {
		this.p = p;
		
		new Thread(new Runnable() { public void run() {
			
			KinectSize.setSize(KWIDTH, KHEIGHT);
			
			_kinect = new Kinect2(p);
			_kinect.initVideo();
			_kinect.initDepth();
			_kinect.initIR();
			_kinect.initRegistered();
			// Start all data
			_kinect.initDevice();
			
			
//		_depthArray = new int[_kinect.getRawDepth().length];
			setMirror(false);
			
	    }}).start();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#update()
	 */
	@Override
	public void update() {
		// Get the raw depth as array of integers
		if( _kinectActive == true && _kinect != null ) {
			_depthArray = _kinect.getRawDepth();
		}
	}
	
	public static int getUnsignedInt(int x) {
	    return (x & 0x00ffffff);
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getDepthImage()
	 */
	@Override
	public PImage getDepthImage() {
		return _kinect.getDepthImage();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getIRImage()
	 */
	@Override
	public PImage getIRImage() {
		return _kinect.getIrImage();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getRgbImage()
	 */
	@Override
	public PImage getRgbImage() {
		return _kinect.getVideoImage();
	}
	
	public int rgbWidth() {return 1920;};
	public int rgbHeight() {return 1080;};
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getDepthData()
	 */
	@Override
	public int[] getDepthData() {
		return _depthArray;
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#enableDepth(boolean)
	 */
	@Override
	public void enableDepth( boolean enable ) {
		if( _kinectActive == true && enable == true ) _kinect.initDepth();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#enableIR(boolean)
	 */
	@Override
	public void enableIR( boolean enable ) {
		if( _kinectActive == true && enable == true ) _kinect.initIR();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#enableRGB(boolean)
	 */
	@Override
	public void enableRGB( boolean enable ) {
		if( _kinectActive == true && enable == true ) _kinect.initVideo();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#isActive()
	 */
	@Override
	public boolean isActive() {
		return _kinectActive;
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#openni()
	 */
	@Override
	public SimpleOpenNI openni() {
		return null;
		//return _kinect;
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#setMirror(boolean)
	 */
	@Override
	public void setMirror( boolean mirrored ) {
		this.mirror = mirrored;
	}
	
	public void setFlipped( boolean flipped ) {
		_flipped = flipped;
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#isMirrored()
	 */
	@Override
	public boolean isMirrored() {
		return this.mirror;
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#drawCamFrustum()
	 */
	@Override
	public void drawCamFrustum() {
		//_kinect.drawCamFrustum();
		//throw new Exception("Not Implemented");
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#tiltUp()
	 */
	@Override
	public void tiltUp() {
//		_hardwareTilt += 5;
//		_hardwareTilt = P.constrain(_hardwareTilt, -20, 30);
//		_kinect.tilt(_hardwareTilt);
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#tiltDown()
	 */
	@Override
	public void tiltDown() {
//		_hardwareTilt -= 5;
//		_hardwareTilt = P.constrain(_hardwareTilt, -20, 30);
//		_kinect.tilt(_hardwareTilt);
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#drawPointCloudForRect(processing.core.PApplet, boolean, int, float, float, float, float, int, int, int, int)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#stop()
	 */
	@Override
	public void stop() {
		if( _kinectActive ) {
			_kinect.dispose();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getRealWorldDepthForKinectPixel(int, int)
	 */
	@Override
	public PVector getRealWorldDepthForKinectPixel( int x, int y ) {
		int offset = x + y * _kinect.depthWidth;
		return depthToPointCloudPos(x, y, _depthArray[offset]);
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getMillimetersDepthForKinectPixel(int, int)
	 */
	@Override
	public int getMillimetersDepthForKinectPixel( int x, int y ) {
		if(_kinect == null) return 0;
		int offset = x + y * _kinect.depthWidth;
		if( offset >= _depthArray.length ) {
			return 0;
		} else {
			return _depthArray[offset];
		}
	}
	
	//calculte the xyz camera position based on the depth data
	PVector depthToPointCloudPos(int x, int y, float depthValue) {
		PVector point = new PVector();
		point.z = (depthValue);// / (1.0f); // Convert from mm to meters
		point.x = (x - CameraParams.cx) * point.z / CameraParams.fx;
		point.y = (y - CameraParams.cy) * point.z / CameraParams.fy;
		return point;
	}
	
	//camera information based on the Kinect v2 hardware
	public static class CameraParams {
	  static float cx = 254.878f;
	  static float cy = 205.395f;
	  static float fx = 365.456f;
	  static float fy = 365.456f;
	  static float k1 = 0.0905474f;
	  static float k2 = -0.26819f;
	  static float k3 = 0.0950862f;
	  static float p1 = 0.0f;
	  static float p2 = 0.0f;
	}


	
	//TODO: Need to implement skeleton APIs for Kinect V2
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#startTrackingSkeleton(int)
	 */
	@Override
	public void startTrackingSkeleton(int userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableUser(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getCoM(int i, PVector _utilPVec) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getJointPositionSkeleton(int userId, int skelLeftHand,
			PVector _utilPVec) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void convertRealWorldToProjective(PVector _utilPVec,
			PVector _utilPVec2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLimb(int userId, int skelHead, int skelNeck) {
		// TODO Auto-generated method stub
		
	}
}

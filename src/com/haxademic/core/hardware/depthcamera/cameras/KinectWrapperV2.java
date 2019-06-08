package com.haxademic.core.hardware.depthcamera.cameras;

import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import KinectPV2.KinectPV2;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

//Kinect Wrapper for Microsoft Kinect V2 for Windows
public class KinectWrapperV2 
implements IDepthCamera {
	
	protected PApplet p;
	protected KinectPV2 _kinect;
	protected boolean _kinectActive = true;
	public static boolean KINECT_ERROR_SHOWN = false;

	public static int KWIDTH = 512;
	public static int KHEIGHT = 424;

	//Kinect V2
	public int[] _depthArray;
	public PVector[] _realWorldMap;
	private boolean mirror = false;
	public boolean _flipped = false;
	
	
	public KinectWrapperV2( PApplet p, boolean initDepth, boolean initRGB, boolean initDepthImage ) {
		this.p = p;
		
		DepthCameraSize.setSize(KWIDTH, KHEIGHT);
		
		_kinect = new KinectPV2(p);
		_kinect.enableDepthImg(initDepthImage);
		_kinect.enableColorImg(initRGB);
		_kinect.enableDepthMaskImg(true);
		_kinect.enableBodyTrackImg(true);
		_kinect.enableInfraredImg(true);
		_kinect.init();
		
		//TODO: Setup configurations to activate each individually
		//_kinect.activateRawColor(true);
		//_kinect.enableInfraredImg(true);
		//_kinect.enableLongExposureInfraredImg(true);
		int bufSize = _kinect.getDepthImage().height * _kinect.getDepthImage().width;
		_depthArray = new int[bufSize];
		setMirror(false);
						
		// enable depthMap generation
		/* Commented out until we get an updated jar
		if(_kinect.isRunningKinect() == false && KINECT_ERROR_SHOWN == false) {
			DebugUtil.alert("Can't access the Kinect. Make sure it's plugged into the computer and a power outlet.");
			_kinectActive = false;
			KINECT_ERROR_SHOWN = true;
			p.exit();
		}
		*/
	}
	
	@Override
	public void update() {
		// Get the raw depth as array of integers
		if( _kinectActive == true ) {
			// Commented out until we get an updated jar
			int[] depth16Array = _kinect.getRawDepthData();
			if(_depthArray != null && depth16Array !=null) {			
				//Convert from 2 byte short to 4 byte int.  
				//Kinect V2 provides a 16 bit depth value. SimpleOpenNI presents as 32 bit int array
				for(int i=0; i < depth16Array.length; i++) {
					_depthArray[i] = (int)depth16Array[i];
				}
			}
			
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
		return _kinect.getInfraredImage();
//		return _kinect.getBodyTrackImage();
	}
	
	public PImage getRgbImage() {
		return _kinect.getColorImage();
	}
	
	public int rgbWidth() {return 1920;};
	public int rgbHeight() {return 1080;};
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getDepthData()
	 */
	public int[] getDepthData() {
		return _depthArray;
	}
	
	public boolean isActive() {
		return _kinectActive;
	}
	
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
	
	public void stop() {
		if( _kinectActive ) {
			_kinect.dispose();
		}
	}
	
	public int getMillimetersDepthForKinectPixel( int x, int y ) {
		int offset = x + y * KinectWrapperV2.KWIDTH;
		if( offset >= _depthArray.length ) {
			return 0;
		} else {
			return _depthArray[offset];
		}
	}

}

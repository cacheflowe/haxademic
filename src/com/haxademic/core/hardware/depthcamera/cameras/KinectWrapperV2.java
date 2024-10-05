package com.haxademic.core.hardware.depthcamera.cameras;

import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import KinectPV2.KinectPV2;
import processing.core.PApplet;
import processing.core.PImage;

public class KinectWrapperV2 
implements IDepthCamera {
	
	// Info on Kinect v2 readings: 
	// - https://medium.com/@lisajamhoury/understanding-kinect-v2-joints-and-coordinate-system-4f4b90b9df16
	// 
	// Troubleshooting:
	// - Make sure to update your graphics drivers to the latest version
	// - Make sure you're using the fastest USB 3+ port on your computer. Some are faster than others!
	// - Good luck using USB extensions... They generally don't work.
	// - Don't disable the Kinect v2 microphone from the Windows Sound Input! This can throw the driver into a death loop, turning on & off on a short cycle
	// - Also, make sure you keep permissions on in Microphone Privacy settings:
	//     - Info here: https://social.msdn.microsoft.com/Forums/sqlserver/en-US/bcd775ef-64b0-4e94-8d26-ce297d6d60ea/kinect-v2-keeps-disconnecting-after-every-10-seconds-on-windows-10-1903?forum=kinectv2sdk
	//     - and here: https://social.msdn.microsoft.com/Forums/en-US/8855395e-e181-474a-b994-82f89d96f35e/cannot-connect-to-the-kinect-studio-host-service?forum=kinectv2sdk
	// - The latest SDK and Runtime should be: 
	//     - SDK: https://www.microsoft.com/en-us/download/details.aspx?id=44561
	//     - Runtime 2.2.1905: https://www.microsoft.com/en-us/download/details.aspx?id=100067
	//     - Runtime 2.2.1811: https://www.microsoft.com/en-us/download/details.aspx?id=57578
	// - Restart KinectMonitor.exe in the Windows Services panel: 
	//     - `Win+R -> services.msc -> KinectMonitor`
	// - Download different KinectSensor drivers if you must: 
	//     - https://www.catalog.update.microsoft.com/Search.aspx?q=VID_045E+PID_02C4
	// - To solve weird "black w/a few white pixels" issue where Kinect doesn't fully start up, try this:
	//     - Uninstall Kinect device from Device Manager
	// 		 - Uninstall Kinect SDK
	//     - Upgrade NVIDIA driver
	//     - Reinstall Kinect SDK - KinectSDK-v2.0_1409-Setup.exe
	//     - `Win+R -> services.msc -> KinectMonitor` - restart service
	
	protected PApplet p;
	protected KinectPV2 kinect;
	protected boolean kinectActive = true;

	public static int KWIDTH = 512;
	public static int KHEIGHT = 424;

	//Kinect V2
	public int[] _depthArray;
	private boolean mirror = false;
	public boolean _flipped = false;
	
	
	public KinectWrapperV2( PApplet p, boolean initRGB, boolean initDepthImage ) {
		this.p = p;
		
		DepthCameraSize.setSize(KWIDTH, KHEIGHT);
		
		kinect = new KinectPV2(p);
		kinect.enableDepthImg(initDepthImage);
		kinect.enableColorImg(initRGB);
		kinect.enableDepthMaskImg(true);
		kinect.enableBodyTrackImg(true);
		kinect.enableInfraredImg(true);
		kinect.init();
		
		//TODO: Setup configurations to activate each individually
		//_kinect.activateRawColor(true);
		//_kinect.enableInfraredImg(true);
		//_kinect.enableLongExposureInfraredImg(true);
		int bufSize = kinect.getDepthImage().height * kinect.getDepthImage().width;
		_depthArray = new int[bufSize];
		setMirror(false);
	}
	
	@Override
	public void update() {
		// Get the raw depth as array of integers
		if( kinectActive == true ) {
			// Commented out until we get an updated jar
			int[] depth16Array = kinect.getRawDepthData();
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
		return kinect.getDepthImage();
	}
	
	/* (non-Javadoc)
	 * @see com.haxademic.core.hardware.kinect.IKinectWrapper#getIRImage()
	 */
	@Override
	public PImage getIRImage() {
		return kinect.getInfraredImage();
//		return _kinect.getBodyTrackImage();
	}
	
	public PImage getRgbImage() {
		return kinect.getColorImage();
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
		return kinectActive;
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
				curZ = getDepthAt(x, y);
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
		if( kinectActive ) {
			kinect.dispose();
		}
	}
	
	public int getDepthAt( int x, int y ) {
		int offset = x + y * KinectWrapperV2.KWIDTH;
		if( offset >= _depthArray.length ) {
			return 0;
		} else {
			return _depthArray[offset];
		}
	}

}

package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.ui.UI;

import KinectPV2.HDFaceData;
import KinectPV2.KinectPV2;

public class Demo_KinectV2_HDFaceVertices
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// from: https://github.com/ThomasLengeling/KinectPV2/blob/master/KinectPV2/examples/HDFaceVertex/HDFaceVertex.pde
	
	protected KinectPV2 kinect;
	protected String startIndex = "startIndex";
	protected String endIndex = "endIndex";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init kinect
		kinect = new KinectPV2(p);
		kinect.enableHDFaceDetection(true);
		kinect.enableColorImg(true); //to draw the color image
//		kinect.enableInfraredImg(true);
//		kinect.enableFaceDetection(true);
		kinect.init();
		
		// init ui
		UI.addSlider(startIndex, 0, 0, KinectPV2.HDFaceVertexCount, 1, false);
		UI.addSlider(endIndex, 1347, 0, KinectPV2.HDFaceVertexCount, 1, false);
	}

	protected void drawApp() {
		p.background(0);
		strokeWeight(5);
		
		// Draw the color Image
		image(kinect.getColorImage(), 0, 0);
		
//		ArrayList<FaceData> faceData = kinect.getFaceData();
//		DebugView.setValue("faceData.size()", faceData.size());
//		for (int j = 0; j < faceData.size(); j++) {
//			FaceData faceDataa = (FaceData)faceData.get(j);
//			KRectangle faceRect = faceDataa.getBoundingRectColor();
//			stroke(0, 255, 0);
//			rect(faceRect.getX(), faceRect.getY(), faceRect.getWidth(), faceRect.getHeight());
//		}

		// Obtain the Vertex Face Points
		// 1347 Vertex Points for each user.
		ArrayList<HDFaceData> hdFaceData = kinect.getHDFaceVertex();
		DebugView.setValue("hdFaceData.size()", hdFaceData.size());

		for (int j = 0; j < hdFaceData.size(); j++) {
			//obtain a the HDFace object with all the vertex data
			HDFaceData HDfaceData = (HDFaceData)hdFaceData.get(j);
			DebugView.setValue("isTracked", HDfaceData.isTracked());
			if (HDfaceData.isTracked()) {
				//draw the vertex points
				stroke(0, 255, 0);
				beginShape(POINTS);
				for (int i = UI.valueInt(startIndex); i < UI.valueInt(endIndex) - 1; i++) {
					float x = HDfaceData.getX(i);
					float y = HDfaceData.getY(i);
					vertex(x, y);
				}
				endShape();
			}
		}
	}
	
}

package com.haxademic.sketch.hardware.kinect_v2_windows;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

import KinectPV2.HDFaceData;
import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class Kinect2HDFaceVertices
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// from: https://github.com/ThomasLengeling/KinectPV2/blob/master/KinectPV2/examples/HDFaceVertex/HDFaceVertex.pde
	
	protected KinectPV2 kinect;
	protected PGraphics buffer;
	protected PShader leaveBlackShader;
	protected int RD_ITERATIONS = 2;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		kinect = new KinectPV2(p);
		kinect.enableHDFaceDetection(true);
		kinect.enableColorImg(true); //to draw the color image
		kinect.init();
	}

	public void drawApp() {
		p.background(0);
		
		// Draw the color Image
		image(kinect.getColorImage(), 0, 0);

		//Obtain the Vertex Face Points
		// 1347 Vertex Points for each user.
		ArrayList<HDFaceData> hdFaceData = kinect.getHDFaceVertex();
		p.debugView.setValue("hdFaceData.size()", hdFaceData.size());

		for (int j = 0; j < hdFaceData.size(); j++) {
			//obtain a the HDFace object with all the vertex data
			HDFaceData HDfaceData = (HDFaceData)hdFaceData.get(j);

			if (HDfaceData.isTracked()) {
				p.debugView.setValue("isTracked", "true");
				//draw the vertex points
				stroke(0, 255, 0);
				beginShape(POINTS);
				for (int i = 0; i < KinectPV2.HDFaceVertexCount; i++) {
					float x = HDfaceData.getX(i);
					float y = HDfaceData.getY(i);
					vertex(x, y);
				}
				endShape();
			} else {
				p.debugView.setValue("isTracked", "false");
			}
		}
	}
	
}

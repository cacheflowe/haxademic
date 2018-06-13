package com.haxademic.demo.hardware.kinect.pv2;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

import KinectPV2.HDFaceData;
import KinectPV2.KinectPV2;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;
import wblut.geom.WB_Point;
import wblut.geom.WB_Triangulate;
import wblut.geom.WB_Triangulation2D;

public class Demo_KinectV2_HDFaceVertices_Triangulation
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from: https://github.com/ThomasLengeling/KinectPV2/blob/master/KinectPV2/examples/HDFaceVertex/HDFaceVertex.pde

	protected KinectPV2 kinect;
	protected PGraphics bufferrgb;
	protected PShader leaveBlackShader;
	protected int RD_ITERATIONS = 2;
	protected String startIndex = "startIndex";
	protected String endIndex = "endIndex";

	WB_Point[] points;
	int numPoints;
	int[] triangles;
	int[] savedTriangles;
	
	PShape shape;
	boolean dirtyRegen = false;
	boolean doTriangulate = true;
	int numTriangles = 0;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		// init kinect
		kinect = new KinectPV2(p);
		kinect.enableHDFaceDetection(true);
		kinect.enableColorImg(true); //to draw the color image
		kinect.init();

		// init ui
		p.prefsSliders.addSlider(startIndex, 0, 0, KinectPV2.HDFaceVertexCount, 1, false);
		p.prefsSliders.addSlider(endIndex, KinectPV2.HDFaceVertexCount, 0, KinectPV2.HDFaceVertexCount, 1, false);

	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') dirtyRegen = true;
		if(p.key == 't') doTriangulate = !doTriangulate;
	}

	public void drawApp() {
		p.background(0);
		p.strokeWeight(1f);
		//			DrawUtil.setBetterLights(p);

		// Draw the color Image
		image(kinect.getColorImage(), 0, 0);


		// COPY rgb image
		if(bufferrgb == null && kinect.getColorImage() != null) bufferrgb = p.createGraphics(kinect.getColorImage().width, kinect.getColorImage().height, PRenderers.P2D);
		ImageUtil.copyImage(kinect.getColorImage(), bufferrgb);
		bufferrgb.loadPixels();

		// prep delaunay points
//		points = null;


		// Obtain the Vertex Face Points
		// 1347 Vertex Points for each user.
		ArrayList<HDFaceData> hdFaceData = kinect.getHDFaceVertex();
		p.debugView.setValue("hdFaceData.size()", hdFaceData.size());

		for (int j = 0; j < hdFaceData.size(); j++) {
			//obtain a the HDFace object with all the vertex data
			HDFaceData HDfaceData = (HDFaceData)hdFaceData.get(j);
			p.debugView.setValue("isTracked", HDfaceData.isTracked());
			if (HDfaceData.isTracked()) {
				//draw the vertex points
				int pointIndex = 0;
//				points=new WB_Point[p.prefsSliders.valueInt(endIndex) - p.prefsSliders.valueInt(startIndex) - 1];

				if(bufferrgb != null) {
					// create he_mesh points from Kinect data for triangulation
					if(points == null) {
						points=new WB_Point[KinectPV2.HDFaceVertexCount];
						for (int i = 0; i < KinectPV2.HDFaceVertexCount; i++) {
							points[i] = new WB_Point(HDfaceData.getX(i), HDfaceData.getY(i));
						}
					} else {
						for (int i = 0; i < KinectPV2.HDFaceVertexCount; i++) {
							points[i].set(HDfaceData.getX(i), HDfaceData.getY(i));
						}
					}
					
					// he_mesh triangulation
					if(doTriangulate) {
						WB_Triangulation2D triangulation=WB_Triangulate.triangulate2D(points);	
						triangles = triangulation.getTriangles();
					}

					p.stroke(255, 80);
					p.strokeWeight(0.3f);

					// regenerate PShape mesh
					p.debugView.setValue("triangles", triangles.length);
					if(numTriangles < triangles.length || dirtyRegen) {
						dirtyRegen = false;
						numTriangles = triangles.length;
						savedTriangles = new int[numTriangles];
						System.arraycopy(triangles, 0, savedTriangles, 0, triangles.length);
						P.println("regenerate!", numTriangles);
						
						// create PShape
						shape = p.createShape();
						shape.beginShape(P.TRIANGLES);
						shape.fill(255,100);
						shape.stroke(0);
						for(int i=0; i < savedTriangles.length; i += 3){
//							p.fill(ImageUtil.getPixelColor(bufferrgb, (int)points[triangles[i]].xf(), (int)points[triangles[i]].yf()));
							float zDisp = 0;
							shape.vertex(points[savedTriangles[i]].xf(), points[savedTriangles[i]].yf(), zDisp);
							shape.vertex(points[savedTriangles[i+1]].xf(), points[savedTriangles[i+1]].yf(), zDisp);
							shape.vertex(points[savedTriangles[i+2]].xf(), points[savedTriangles[i+2]].yf(), zDisp);
						}
						shape.endShape();
					}
					
					if(shape != null) {
						p.debugView.setValue("vertexCount", shape.getVertexCount());
//						P.println("==================");
						// update shape vertices
						for(int i=0; i < shape.getVertexCount() - 1; i ++) {
//							shape.setVertex(i, v.x, radius * P.sin(-newRads), radius * P.cos(-newRads));

//							shape.setVertex(savedTriangles[i], points[savedTriangles[i]].xf(), points[savedTriangles[i]].yf());
//							P.println(savedTriangles[i]);
							shape.setVertex(i, points[savedTriangles[i]].xf(), points[savedTriangles[i]].yf());
							
//							shape.setVertex(i, points[savedTriangles[i]].xf(), points[savedTriangles[i]].yf());
						}
						
					}
					
					// draw triangles
//					for(int i=0; i<triangles.length; i+=3){
//						p.fill(ImageUtil.getPixelColor(bufferrgb, (int)points[triangles[i]].xf(), (int)points[triangles[i]].yf()));
//						p.beginShape();
//						float zDisp = 0; // 200 + 200f * P.sin(p.frameCount * 0.01f + points[triangles[i]].xf() * 0.1f);
//						p.vertex(points[triangles[i]].xf(), points[triangles[i]].yf(), zDisp);
//						p.vertex(points[triangles[i+1]].xf(), points[triangles[i+1]].yf(), zDisp);
//						p.vertex(points[triangles[i+2]].xf(), points[triangles[i+2]].yf(), zDisp);
//						p.endShape();
//					}

					// draw shape
					if(shape != null) p.shape(shape);
				}
			}
		}

	}

}

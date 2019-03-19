package com.haxademic.sketch.volume;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import newhull.Point3d;
import newhull.QuickHull3D;
import processing.core.PImage;

public class NewHull 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	QuickHull3D hull = new QuickHull3D(); //init quickhull
	Point3d[] points; //init Point3d array
	float[] intervals; //init Point3d array
	PImage img;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void setup() {
		super.setup();

		//point array
		points = new Point3d[] {
				new Point3d (0.0, 0.0, 0.0),
				new Point3d (0.0, 60.0, 50.0),
				new Point3d (0.0, -50.0, 0.0),
				new Point3d (0.0, 0.0, 0.0),
				new Point3d (0.0, 60.0, 50.0),
				new Point3d (0.0, -50.0, 0.0),
				new Point3d (0.0, 50.0, 150.0)
		};
		intervals = new float[] {
				8f,
				16f,
				16f,
				11f,
				12f,
				14f,
				16f
		};
		
		img = DemoAssets.textureJupiter();

	}  

	public void drawApp() {
		DrawUtil.setBetterLights(p);
		background(0);
		fill(255);
		noStroke();
//		noFill();
//		stroke(255);
		smooth();
		translate(width/2f, height/2f);
		//rotateY(frameCount * 0.01f);

		int numPoints = points.length;

		//check that our hull is valid
//		if (hull.myCheck(points, numPoints) == true) {
		
			for( int i=0; i < numPoints; i++ ) {
				points[i].x += 8* Math.sin(frameCount/intervals[i]);
				points[i].y += 8* Math.cos(frameCount/intervals[i]);
				points[i].z += 8* Math.cos(frameCount/intervals[i]);
			}

			hull.build(points);  //build hull
			hull.triangulate();  //triangulate faces
			Point3d[] vertices = hull.getVertices();  //get vertices
			
			if(p.frameCount % 20 < 10) {
				DrawUtil.setTextureRepeat(p.g, true);
			} else {
				DrawUtil.setTextureRepeat(p.g, false);
			}
			beginShape(TRIANGLES);
			texture(img);
			int[][] faceIndices = hull.getFaces();
			//run through faces (each point on each face), and draw them
			for (int i = 0; i < faceIndices.length; i++) {
				for (int k = 0; k < faceIndices[i].length; k++) {

					//get points that correspond to each face
					Point3d pnt2 = vertices[faceIndices[i][k]];
					float x = (float)pnt2.x;
					float y = (float)pnt2.y;
					float z = (float)pnt2.z;
					vertex(x,y,z, x,y);
				}
			}
			endShape(CLOSE);
//		}
	}

}

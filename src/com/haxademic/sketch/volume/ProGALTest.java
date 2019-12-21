package com.haxademic.sketch.volume;



import java.util.Arrays;
import java.util.List;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import wblut.external.ProGAL.AlphaComplex;
import wblut.external.ProGAL.CTriangle;
import wblut.external.ProGAL.Point;

public class ProGALTest 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	List<Point> points;
	float[] intervals; //init Point3d array
	PImage img;

	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.WIDTH, "1280" );
		Config.setProperty( AppSettings.HEIGHT, "1024" );
	}

	public void firstFrame() {

		smooth();

		img = DemoAssets.textureJupiter();

		//point array
		float max = 800;
		float maxHalf = max/2f;
		points = Arrays.asList( 
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf),
				new Point(Math.random()*max-maxHalf, Math.random()*max-maxHalf, Math.random()*max-maxHalf)
				);

		float intRange = 10;
		float minInt = 4;
		intervals = new float[] {
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt,
				(float) Math.random()*intRange+minInt
		};


	}  

	public void drawApp() {
		PG.setBasicLights(p);
		background(0);
		fill(255);
		noStroke();
//				noFill();
//				stroke(255);
		translate(width/2f, height/2f, -500);

		int numPoints = points.size();

		for( int i=0; i < numPoints; i++ ) {
			points.get(i).setX( points.get(i).x() + 8* Math.sin(frameCount/intervals[i]) );
			points.get(i).setY( points.get(i).y() + 8* Math.cos(frameCount/intervals[i]) );
			points.get(i).setZ( points.get(i).z() + 8* Math.sin(frameCount/intervals[i]) );
		}


		drawAlphaComplex();
	}
	
	protected void drawAlphaComplex() {
		// draw alpha complex
		AlphaComplex ac = new AlphaComplex(points, 200.8);
		for(CTriangle tri: ac.getTriangles()){		
			beginShape(TRIANGLE_STRIP);
			texture(img);

			vertex( (float) tri.getP1().x(), (float) tri.getP1().y(), (float) tri.getP1().z(),    P.abs( (float) tri.getP1().x()*2f+1 ), P.abs( (float) tri.getP1().y()*2f+1 ) );
			vertex( (float) tri.getP2().x(), (float) tri.getP2().y(), (float) tri.getP2().z(),    P.abs( (float) tri.getP2().x()*2f+1 ), P.abs( (float) tri.getP2().y()*2f+1 ) );
			vertex( (float) tri.getP3().x(), (float) tri.getP3().y(), (float) tri.getP3().z(),    P.abs( (float) tri.getP3().x()*2f+1 ), P.abs( (float) tri.getP3().y()*2f+1 ) );

			endShape(CLOSE);
		}
	}

}

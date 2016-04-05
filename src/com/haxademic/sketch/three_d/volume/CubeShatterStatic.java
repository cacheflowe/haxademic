package com.haxademic.sketch.three_d.volume;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.shapes.Voronoi3D;
import com.haxademic.core.draw.util.DrawUtil;

import processing.core.PApplet;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;
import wblut.core.processing.WB_Render;
import wblut.geom.core.WB_Plane;
import wblut.hemesh.core.HE_Mesh;

public class CubeShatterStatic 
extends PApplet
{
	PApplet p;

	// from example: http://www.wblut.com/2010/10/20/hemesh-voronoi-example/
	float[][] points;
	int numpoints;
	HE_Mesh container;
	HE_Mesh[] cells;
	int numcells;
	WB_Plane P1,P2;
	WB_Render render;

	ArrayList<WETriangleMesh> meshes;
	ToxiclibsSupport toxi;

	public void setup () {
		p = this;
		// set up stage and drawing properties
		//size( 800, 800, "hipstersinc.P5Sunflow" );
		p.size( 800, 800, P.P3D );				//size(screen.width,screen.height,P3D);
		p.frameRate( 30 );

		p.shininess(1000); 
		p.lights();

		toxi = new ToxiclibsSupport( p );

		meshes = Voronoi3D.getShatteredBox( p, 200 );
	}

	public void draw() {
		DrawUtil.setCenter( p );
		p.translate(0,0,-1000);
		p.background( 0, 0, 0 );

		rotateX(1f/height*mouseY*TWO_PI-PI);
		rotateY(1f/width*mouseX*TWO_PI-PI);

		// draw toxiclibs mesh
		fill(200);
		for(int j=0;j<meshes.size();j++) {
			toxi.mesh( meshes.get( j ) );
		}
	}

}

package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.toxi.Voronoi3D;

import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class Demo_CubeShatter 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from example: http://www.wblut.com/2010/10/20/hemesh-voronoi-example/
	ArrayList<WETriangleMesh> meshes;
	ToxiclibsSupport toxi;

	protected void firstFrame() {
		toxi = new ToxiclibsSupport( p );
		meshes = Voronoi3D.getShatteredBox( p, 200 );
	}

	protected void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);
		PG.setBetterLights(p.g);
		PG.basicCameraFromMouse(p.g);

		// draw toxiclibs mesh
		p.fill(40);
		p.stroke(40, 255, 40);
		for(int j=0;j<meshes.size();j++) {
			toxi.mesh( meshes.get( j ) );
		}
	}

}

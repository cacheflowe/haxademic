package com.haxademic.sketch.three_d;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.Mesh;

@SuppressWarnings("serial")
public class MeshLibraryTest
extends PAppletHax {

	protected Mesh myMesh;

	protected void overridePropsFile() {
		appConfig.setProperty( "width", "1000" );
		appConfig.setProperty( "height", "800" );
	}

	public void setup() {
		super.setup();

		myMesh = new Mesh(Mesh.CORKSCREW, 200, 200, -4, 4, -PI, PI);
		myMesh.setColorRange(192, 192, 50, 50, 50, 50, 100);
	}

	public void drawApp() {
		p.background(0);


		// setup drawing style 
		noStroke();
//		strokeWeight(1);
//		stroke(255);
		fill(0);

		// setup lights
		lightSpecular(230, 230, 230); 
		directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		specular(color(200)); 
		shininess(5.0f); 

		// setup view
		translate(width*0.5f, height*0.5f);
		rotateX(0.5f); 
		rotateY(frameCount/20f); 

		scale(100);
		myMesh.draw(p.g);
//		p.box(100);
	}

}
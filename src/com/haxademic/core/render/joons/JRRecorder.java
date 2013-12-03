package com.haxademic.core.render.joons;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphics3D;

/*
 * The purpose of JRRecorder is to geometry used in P5 sketch, and reproduce them in sunflow.
 * Remember, JRRecorder is a secondary PGraphics3D object, and methods are echoed to it by PApplet.
 * Say, when you call PApplet.method(), PApplet will do something like:
 * public void method() { primaryPG3D.method(); secondaryPG3D.method(); }
 */

public class JRRecorder extends PGraphics3D {
    
	private boolean writingVertices = false;
	private ArrayList<Float> tempVertices;
	int kind, vertexCount;
	
	public JRRecorder() {
		//standard construction for a PGraphics object
		setParent(JRStatics.P);
		setPrimary(false);
		setSize(JRStatics.P.width, JRStatics.P.height);
		init();
	}
	
	private void init() {
		JRStatics.initializeFillers(); //emptying out fillers
		JRStatics.FILLERS_ARE_VALID=true; //true unless proven false		
		tempVertices = new ArrayList<Float>(); //emptying out vertices
		writingVertices=false; //init beginShape()
	}
	
	public void beginDraw(){
		//this method is echoed before every draw() loop.
		init();
	}
	
	public void endDraw() {
		//We need this empty override.
	}
	
	public void perspective(float fov, float aspect, float zNear, float zFar){
    	//zNear and zFar are unused in sunflow.
		JRStatics.FOV=fov;
		JRStatics.ASPECT=aspect;
    }
	
	public void beginShape() {
		PApplet.println("Joons-Renderer: Please use beginShape(TRIANGLES) or beginShape(QUADS).");
		PApplet.println("Joons-Renderer: Your vertices will be rendered using beginShape(TRIANGLES).");
		beginShape(PConstants.TRIANGLES);
	}

	public void beginShape(int kind) {
		this.kind=kind;
		writingVertices=true;
		vertexCount=0;
	}
	
	public void vertex(float x, float y, float z) {
		if(writingVertices){
			vertexCount++;
			tempVertices.add(x);
			tempVertices.add(y);
			tempVertices.add(z);
			
			if(kind==PConstants.QUADS && vertexCount==4){
				//if more than 1 quad, simply start a new one.
				endShape();
				beginShape(PConstants.QUADS);
			}
		}
	}
	
	public void endShape(){
		if(kind == PConstants.QUADS && vertexCount==4){
			//adding vertices to make two triangles from four points
			//abcd -> abc dac
			tempVertices.add(tempVertices.get(0));//ax
			tempVertices.add(tempVertices.get(1));//ay
			tempVertices.add(tempVertices.get(2));//az
			tempVertices.add(tempVertices.get(6));//cx
			tempVertices.add(tempVertices.get(7));//cy
			tempVertices.add(tempVertices.get(8));//cz
		}
		
		for(int i=0; i<tempVertices.size()/3; i++){ //divide by 3, because 3 numbers make 1 point.
			float[] tCoord = JRStatics.applyTransform(tempVertices.get(i*3), 
											tempVertices.get(i*3+1),
											tempVertices.get(i*3+2));
			JRStatics.getCurrentFiller().getVertices().add(tCoord[0]);
			JRStatics.getCurrentFiller().getVertices().add(tCoord[1]);
			JRStatics.getCurrentFiller().getVertices().add(tCoord[2]);
		}
		
		writingVertices = false;
		vertexCount = 0;
		tempVertices = new ArrayList<Float>(); //emptying out vertices
	}

	//implementations of 3D primitives	
	public void box(float d) {
		box(d,d,d);
	}
	
	public void box(float width, float height, float depth) {
		float w=width/2;
		float h=height/2;
		float d=depth/2;

		beginShape(PConstants.QUADS);// top
		vertex(w, h, d);
		vertex(-w, h, d);
		vertex(-w, -h, d);
		vertex(w, -h, d);
		endShape();

		beginShape(PConstants.QUADS);// +x side
		vertex(w, h, d);
		vertex(w, -h, d);
		vertex(w, -h, -d);
		vertex(w, h, -d);
		endShape();

		beginShape(PConstants.QUADS);// -x side
		vertex(-w, h, -d);
		vertex(-w, -h, -d);
		vertex(-w, -h, d);
		vertex(-w, h, d);
		endShape();

		beginShape(PConstants.QUADS);// +y side
		vertex(w, h, d);
		vertex(w, h, -d);
		vertex(-w, h, -d);
		vertex(-w, h, d);
		endShape();

		beginShape(PConstants.QUADS);// -y side
		vertex(-w, -h, d);
		vertex(-w, -h, -d);
		vertex(w, -h, -d);
		vertex(w, -h, d);
		endShape();

		beginShape(PConstants.QUADS);// bottom
		vertex(-w, h, -d);
		vertex(w, h, -d);
		vertex(w, -h, -d);
		vertex(-w, -h, -d);
		endShape();
	}

	public void sphere(float r) {
		//Sunflow seems to offer an optimized render for a perfect sphere,
		//meaning no triangle polygonal mess from Processing
		float[] sph = JRStatics.applyTransform(0,0,0);
		JRStatics.getCurrentFiller().addSphere(sph[0],sph[1],sph[2],r);
	}
	
}
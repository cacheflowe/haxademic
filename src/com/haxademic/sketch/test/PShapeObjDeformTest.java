package com.haxademic.sketch.test;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PJOGL;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class PShapeObjDeformTest 
extends PAppletHax {

	protected PShape obj;
	protected PShape objOrig;
	protected float _spectrumInterval;
	
	protected ArrayList<PVector> vertices;
	protected ArrayList<Integer> sharedVertexIndices;
	
	protected float _size = 0;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void setup() {
		super.setup();	
		OpenGLUtil.setQuality( p, OpenGLUtil.SMOOTH_HIGH );

		String objFile = "";
		objFile = "mode-set.obj";
		objFile = "Space_Shuttle.obj";
		objFile = "cacheflowe-3d.obj";
		objFile = "pointer_cursor_2_hollow.obj";
		objFile = "chicken.obj";
		objFile = "lego-man.obj";
		objFile = "poly-hole-tri.obj";
		objFile = "skull.obj";
		
		obj = p.loadShape( FileUtil.getHaxademicDataPath() + "models/" + objFile );
		objOrig = p.loadShape( FileUtil.getHaxademicDataPath() + "models/" + objFile );
		
		
		// build data of shared vertex connections
		vertices = new ArrayList<PVector>();
		sharedVertexIndices = new ArrayList<Integer>();
		
		// loop through model, finding vertices
		for (int j = 0; j < obj.getChildCount(); j++) {
			for (int i = 0; i < obj.getChild(j).getVertexCount(); i++) {
				// store vertex
				PVector vertex = objOrig.getChild(j).getVertex(i);
				vertices.add(vertex);
				
				// look for matching vertex, and store vertex to shared vertex match, if we find one.  
				boolean foundMatch = false;
				for(int v=0; v < vertices.size(); v++) {
					if(foundMatch == false && vertices.get(v).dist(vertex) == 0) {
						sharedVertexIndices.add(v);
						foundMatch = true;
					}
				}
				
				// otherwise, add a new vertex match
				if(foundMatch == false) {
					sharedVertexIndices.add(vertices.size() - 1);
				}
			}
		}
		
		// find mesh size extent to responsively scale the mesh
		float outermostVertex = 0;
		for (PVector vertex : vertices) {
			if(vertex.x > outermostVertex) outermostVertex = vertex.x;
			if(vertex.y > outermostVertex) outermostVertex = vertex.y;
			if(vertex.z > outermostVertex) outermostVertex = vertex.z;
		}
		_size = outermostVertex;
		
		// spread spectrum across vertices
		_spectrumInterval = 512f / sharedVertexIndices.size();
		P.println("vertex count: ", vertices.size());
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}

	public void drawApp() {
		background(0);

		// wireframe hotness!
		GL2 gl = ((PJOGL)beginPGL()).gl.getGL2();
		if(P.round(p.frameCount/20) % 2 == 0) {
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_LINE );
		} else {
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_FILL );
		}
		
		// setup lights
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(color(200)); 
		p.shininess(5.0f); 


		p.translate(p.width/2f, p.height/2f);
		p.translate(0, 0, -1000);
		p.rotateY(p.mouseX * 0.01f);
		p.rotateZ(p.mouseY * 0.01f);

		
		// DrawUtil.setDrawCenter(p);
		
//		for (int j = 0; j < obj.getChildCount(); j++) {
//			for (int i = 0; i < obj.getChild(j).getVertexCount(); i++) {
//				PVector v = obj.getChild(j).getVertex(i);
//				v.x += random(-0.01f,0.01f);
//				v.y += random(-0.01f,0.01f);
//				v.z += random(-0.01f,0.01f);
//				obj.getChild(j).setVertex(i,v.x,v.y,v.z);
//			}
//		}

		
		// deform from original copy
//		int spectrumIndex = 0;
//		for (int j = 0; j < obj.getChildCount(); j++) {
//			for (int i = 0; i < obj.getChild(j).getVertexCount(); i++) {
//				float amp = 1 + 0.9f * P.p.audioIn.getEqAvgBand( P.floor(_spectrumInterval * spectrumIndex) );
//				PVector v = obj.getChild(j).getVertex(i);
//				PVector vOrig = objOrig.getChild(j).getVertex(i);
//				v.x = vOrig.x * amp;
//				v.y = vOrig.y * amp;
//				v.z = vOrig.z * amp;
//				obj.getChild(j).setVertex(i,v.x,v.y,v.z);
//				spectrumIndex++;
//			}
//		}
		

		// deform from original copy, using vertexIndex as the key to find the shared index
		int vertexIndex = 0;
		for (int j = 0; j < obj.getChildCount(); j++) {
			for (int i = 0; i < obj.getChild(j).getVertexCount(); i++) {
				int sharedVertexIndex = sharedVertexIndices.get(vertexIndex);
				PVector vOrig = vertices.get(vertexIndex);
				
				float amp = 1 + 0.5f * P.p._audioInput.getFFT().spectrum[ P.floor(_spectrumInterval * sharedVertexIndex) ]; // get shared vertex deformation
//				float amp = 1 + 0.1f * P.p.audioIn.getEqAvgBand( P.floor(_spectrumInterval * sharedVertexIndex) ); // get shared vertex deformation
				obj.getChild(j).setVertex(i, vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
				vertexIndex++;
			}
		}

		
		// draw!
		obj.disableStyle();
		p.fill(200, 255, 200);
		p.noStroke();
//		p.stroke(255);
//		p.strokeWeight(2);
		p.scale(p.height/_size * 0.8f);
		p.shape(obj);
	}
}
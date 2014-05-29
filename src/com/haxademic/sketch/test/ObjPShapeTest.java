package com.haxademic.sketch.test;

import processing.core.PShape;
import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ObjPShapeTest 
extends PAppletHax {

	protected PShape obj;
	protected PShape objOrig;
	protected float _spectrumInterval;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "true" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void setup() {
		super.setup();	
		OpenGLUtil.setQuality( p, OpenGLUtil.SMOOTH_HIGH );

		String objFile = "poly-hole-tri.obj";
		objFile = "skull.obj";
		objFile = "lego-man.obj";
		objFile = "mode-set.obj";
		objFile = "chicken.obj";
		objFile = "cacheflowe-3d.obj";
//		objFile = "Space_Shuttle.obj";
//		objFile = "pointer_cursor_2_hollow.obj";
		obj = p.loadShape( FileUtil.getHaxademicDataPath() + "models/" + objFile );
		objOrig = p.loadShape( FileUtil.getHaxademicDataPath() + "models/" + objFile );
		
		_spectrumInterval = 512f / obj.getChildCount() * 3;
	}

	public void drawApp() {
		background(0);
		
		p.shininess(1000f); 
		p.lights();


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
		int spectrumIndex = 0;
		for (int j = 0; j < obj.getChildCount(); j++) {
			for (int i = 0; i < obj.getChild(j).getVertexCount(); i++) {
				float amp = 1 + 0.9f * P.p.audioIn.getEqAvgBand( P.floor(_spectrumInterval * spectrumIndex) );
				PVector v = obj.getChild(j).getVertex(i);
				PVector vOrig = objOrig.getChild(j).getVertex(i);
				v.x = vOrig.x * amp;
				v.y = vOrig.y * amp;
				v.z = vOrig.z * amp;
				obj.getChild(j).setVertex(i,v.x,v.y,v.z);
				spectrumIndex++;
			}
		}
		

		// draw!
		obj.disableStyle();
		p.fill(0, 255, 0);
		p.noStroke();
//		p.stroke(255);
//		p.strokeWeight(2);
		p.scale(90);
		p.shape(obj);
	}
}
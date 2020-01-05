package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class KinectShaderVertexDeform
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PShape obj;
	PShader texShader;
	float _frames = 240;

	// uv coordinates
	protected String kinectLeft = "kinectLeft";
	protected String kinectRight = "kinectRight";
	protected String kinectTop = "kinectTop";
	protected String kinectBottom = "kinectBottom";
	
	protected PGraphics texDisplace;
	protected PGraphics tex;


	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		
		UI.addSlider(kinectLeft, -0.08f, -1.0f, 1.0f, 0.01f, false);
		UI.addSlider(kinectRight, 1.06f, 0f, 2f, 0.01f, false);
		UI.addSlider(kinectTop, -0.08f, -1.0f, 1.0f, 0.01f, false);
		UI.addSlider(kinectBottom, 1.04f, 0f, 2f, 0.01f, false);

		tex = p.createGraphics(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		texDisplace = p.createGraphics(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		
		obj = Shapes.createSheet(200, tex);
	}
	
	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		background(0);
		PG.setCenterScreen(p.g);
		if(p.key != ' ') PG.basicCameraFromMouse(p.g);
		
		// update mapped texture
		tex.beginDraw();
		tex.image(depthCamera.getRgbImage(), tex.width * UI.value(kinectLeft), tex.height * UI.value(kinectTop), tex.width * UI.value(kinectRight), tex.height * UI.value(kinectBottom));
		tex.endDraw();
				
		texDisplace.beginDraw();
//		texDisplace.image(p.kinectWrapper.getDepthImage(), texDisplace.width * kinectLeft, texDisplace.height * kinectTop, texDisplace.width * kinectRight, texDisplace.height * kinectBottom);
		texDisplace.image(depthCamera.getDepthImage(), 0, 0, texDisplace.width, texDisplace.height);
		texDisplace.endDraw();
		
		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texDisplace);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(-100f);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);
		// set texture using PShape method

		// draw mesh
//		p.scale(4f);
		obj.setTexture(tex);
		p.shape(obj);
		p.resetShader();
	}


	PShape createSheet(int detail, PImage tex) {
		p.textureMode(NORMAL);
		PShape sh = p.createShape();
		sh.beginShape(QUADS);
		sh.noStroke();
		sh.noFill();
		sh.texture(tex);
		float cellW = tex.width / detail;
		float cellH = tex.height / detail;
		int numVertices = 0;
		for (int col = 0; col < tex.width; col += cellW) {
			for (int row = 0; row < tex.height; row += cellH) {
				float xU = col;
				float yV = row;
				float x = -tex.width/2f + xU;
				float y = -tex.height/2f + yV;
				float z = 0;
				sh.normal(x, y, z);
				sh.vertex(x, y, z, 					P.map(xU, 0, tex.width, 0, 1f), P.map(yV, 0, tex.height, 0, 1f));
				sh.vertex(x, y + cellH, z, 			P.map(xU, 0, tex.width, 0, 1f), P.map(yV + cellH, 0, tex.height, 0, 1f));    
				sh.vertex(x + cellW, y + cellH, z,	P.map(xU + cellW, 0, tex.width, 0, 1f), P.map(yV + cellH, 0, tex.height, 0, 1f));    
				sh.vertex(x + cellW, y, z, 			P.map(xU + cellW, 0, tex.width, 0, 1f), P.map(yV, 0, tex.height, 0, 1f));
				numVertices++;
			}
		}
		P.println(numVertices, "vertices");
		sh.endShape(); 
		return sh;
	}

}


package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.hardware.kinect.KinectSize;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class KinectShaderVertexDeform
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PShape obj;
	PShader texShader;
	float _frames = 240;

	// uv coordinates
	public float kinectLeft = 0;
	public float kinectRight = 1;
	public float kinectTop = 0;
	public float kinectBottom = 1;
	
	protected ControlP5 _cp5;
	
	protected PGraphics texDisplace;
	protected PGraphics tex;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}

	public void setupFirstFrame() {

		int controlY = 0;
		int controlSpace = 12;
		_cp5 = new ControlP5(this);

		_cp5.addSlider("kinectLeft").setPosition(20,controlY+=controlSpace).setWidth(100).setRange(-1.0f,1.0f).setValue(-0.08f);
		_cp5.addSlider("kinectRight").setPosition(20,controlY+=controlSpace).setWidth(100).setRange(0f,2f).setValue(1.06f);
		_cp5.addSlider("kinectTop").setPosition(20,controlY+=controlSpace).setWidth(100).setRange(-1.0f,1.0f).setValue(-0.08f);
		_cp5.addSlider("kinectBottom").setPosition(20,controlY+=controlSpace).setWidth(100).setRange(0,2f).setValue(1.04f);

		tex = p.createGraphics(KinectSize.WIDTH, KinectSize.HEIGHT);
		texDisplace = p.createGraphics(KinectSize.WIDTH, KinectSize.HEIGHT);
		
		obj = Shapes.createSheet(200, tex);
	}
	
	public void drawApp() {
		background(0);
		DrawUtil.setCenterScreen(p.g);
		if(p.key != ' ') DrawUtil.basicCameraFromMouse(p.g);
		
		// update mapped texture
		tex.beginDraw();
		tex.image(p.kinectWrapper.getRgbImage(), tex.width * kinectLeft, tex.height * kinectTop, tex.width * kinectRight, tex.height * kinectBottom);
		tex.endDraw();
				
		texDisplace.beginDraw();
//		texDisplace.image(p.kinectWrapper.getDepthImage(), texDisplace.width * kinectLeft, texDisplace.height * kinectTop, texDisplace.width * kinectRight, texDisplace.height * kinectBottom);
		texDisplace.image(p.kinectWrapper.getDepthImage(), 0, 0, texDisplace.width, texDisplace.height);
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


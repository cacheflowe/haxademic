package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_DepthCamera_ShaderVertexDeform
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// mesh & textures
	PShape obj;
	PShader texShader;
	protected PGraphics texDisplace;
	protected PGraphics tex;
	
	// UI controls
	// uv coordinates
	protected String kinectLeft = "kinectLeft";
	protected String kinectRight = "kinectRight";
	protected String kinectTop = "kinectTop";
	protected String kinectBottom = "kinectBottom";
	// mesh
	protected String meshScale = "meshScale";
	protected String displaceAmp = "displaceAmp";


	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.Realsense);
		
		// ui
		UI.addTitle("Align texture w/depth");
		UI.addSlider(kinectLeft, -0.08f, -1.0f, 1.0f, 0.01f, false);
		UI.addSlider(kinectRight, 1.06f, 0f, 2f, 0.01f, false);
		UI.addSlider(kinectTop, -0.08f, -1.0f, 1.0f, 0.01f, false);
		UI.addSlider(kinectBottom, 1.04f, 0f, 2f, 0.01f, false);
		UI.addTitle("Mesh");
		UI.addSlider(meshScale, 1f, 0.1f, 3f, 0.01f, false);
		UI.addSlider(displaceAmp, 200f, 0f, 2000f, 1f, false);

		// textures
		tex = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		texDisplace = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		
		// mesh
		obj = Shapes.createSheet(200, tex);
		obj.setTexture(tex);
	}
	
	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		background(0);
		PG.setCenterScreen(p.g);
		if(p.key != ' ') PG.basicCameraFromMouse(p.g, 0.1f);
		
		// update mapped texture
		tex.beginDraw();
		tex.background(255);
		tex.image(depthCamera.getRgbImage(), tex.width * UI.value(kinectLeft), tex.height * UI.value(kinectTop), tex.width * UI.value(kinectRight), tex.height * UI.value(kinectBottom));
		tex.endDraw();
		DebugView.setTexture("tex", tex);
				
		// update displacement texture
		texDisplace.beginDraw();
		texDisplace.background(0);
//		texDisplace.image(p.kinectWrapper.getDepthImage(), texDisplace.width * kinectLeft, texDisplace.height * kinectTop, texDisplace.width * kinectRight, texDisplace.height * kinectBottom);
		texDisplace.image(depthCamera.getDepthImage(), 0, 0, texDisplace.width, texDisplace.height);
		texDisplace.endDraw();
		DebugView.setTexture("texDisplace", texDisplace);
		
		// deform mesh
		MeshDeformAndTextureFilter.instance().setDisplacementMap(texDisplace);
		MeshDeformAndTextureFilter.instance().setDisplaceAmp(UI.value(displaceAmp));
		MeshDeformAndTextureFilter.instance().setSheetMode(true);
		MeshDeformAndTextureFilter.instance().setOnContext(p.g);

		// draw mesh
		p.scale(UI.value(meshScale));
		p.shape(obj);
		p.resetShader();
	}

}


package com.haxademic.demo.draw.shapes.shader;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.PShapeUtil.PShapeCopy;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_VertexShader_MoveSpheres 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO:
	// - Try with other shapes (digits?)
	// - Add rotation to individual shapes
	// - Make a new version with a particle system, a la GPU particles
	// - Make a grid that Kinect displaces
	// - Use a real-time texture (camera) as color map
	// - Rotate shapes in vertex shader
	//   - https://stackoverflow.com/questions/35248095/rotation-of-model-with-translation-results-in-rotation-not-at-origin
	//   - https://learnopengl.com/Getting-started/Transformations
	
	protected ArrayList<PShape> shared = new ArrayList<PShape>();
	protected PShape group;
	protected PShaderHotSwap polygonShader;
	protected SimplexNoiseTexture noiseTexture;
	protected PGraphics displaceTexture;

	protected String CAMERA_ON = "CAMERA_ON";
	protected String NOISE_SPEED_X = "NOISE_SPEED_X";
	protected String NOISE_SPEED_Y = "NOISE_SPEED_Y";
	protected String DISPLACE_AMP = "DISPLACE_AMP";
	protected String DISPLACE_OFFSET_X = "DISPLACE_OFFSET_X";
	protected String DISPLACE_OFFSET_Y = "DISPLACE_OFFSET_Y";
	protected String LIGHT_DIR = "LIGHT_DIR";
	protected String LIGHT_COL = "LIGHT_COL";
	protected String LIGHT_AMBIENT = "LIGHT_AMBIENT";
	protected String LIGHT_SHININESS = "LIGHT_SHININESS";

	protected void config() {
		Config.setAppSize(1280, 720);
	}
	
	protected void firstFrame() {
		// UI
		UI.addTitle("Spheres config");
		UI.addToggle(CAMERA_ON, true, false);
		UI.addSlider(NOISE_SPEED_X, 0.001f, 0, 0.1f, 0.00001f, false);
		UI.addSlider(NOISE_SPEED_Y, 0.001f, 0, 0.1f, 0.00001f, false);
		UI.addSlider(DISPLACE_AMP, 200, 0, 1000, 1, false);
		UI.addSlider(DISPLACE_OFFSET_X, 0, 0, 1000, 0.01f, false);
		UI.addSlider(DISPLACE_OFFSET_Y, 0, 0, 1000, 0.01f, false);
		UI.addSliderVector(LIGHT_DIR, 1f, -10f, 10f, 0.01f, false);
		UI.addSliderVector(LIGHT_COL, 1f, 0, 1, 0.001f, false);
		UI.addSliderVector(LIGHT_AMBIENT, 1f, 0, 1, 0.001f, false);
		UI.addSlider(LIGHT_SHININESS, 1f, 0, 1000, 0.01f, false);
		
		// config build shapes
		float shapeSize = 12;
		float shapeSpacing = 12;
		p.sphereDetail(10);
		int vertCount = PShapeUtil.vertexCount(PShapeUtil.createSphere(1, 0));

		// create PShapes inside a group
		int startBuildTime = p.millis();
		int cols = 300;
		int rows = 200;
		startBuildTime = p.millis();
		group = p.createShape(P.GROUP);
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float gridX = -(shapeSpacing * cols/2) + (x * shapeSpacing);
				float gridY = -(shapeSpacing * rows/2) + (y * shapeSpacing);
//				PShape shape = PShapeUtil.createSphere(shapeSize, gridX, gridY, 0, 127 + 127 * p.color(P.sin(x/10f), 127 + 127 * P.sin(y/10f), 127 + 127 * P.sin(x+y/100f)), 0, 0);
				PShape shape = PShapeUtil.createBox(shapeSize, shapeSize, shapeSize, gridX, gridY, 0, 127 + 127 * p.color(P.sin(x/10f), 127 + 127 * P.sin(y/10f), 127 + 127 * P.sin(x+y/100f)), 0, 0);
				// give the shape attributes for the shader to pick out their UV coord from grid index
				shape.attrib("x", x);
				shape.attrib("y", y);
				group.addChild(shape);
			}
		}
		DebugView.setValue("Group PShape time", p.millis() - startBuildTime + "ms");
		DebugView.setValue("Num shapes", cols * rows);
		DebugView.setValue("Num verts", cols * rows * vertCount);
		
		// outer sphere
		shared.add(PShapeUtil.createSphere(4000, p.color(50, 0)));
		PShape innerSphere = PShapeCopy.copyShape(shared.get(shared.size() - 1));
		PShapeUtil.setBasicShapeStyles(innerSphere, 0, p.color(20), 10);
		innerSphere.scale(0.6f);
		shared.add(innerSphere);
		
		// load shader to move spheres
		polygonShader = new PShaderHotSwap(
				FileUtil.getPath("haxademic/shaders/vertex/mesh-3d-deform-vert.glsl"),
				FileUtil.getPath("haxademic/shaders/vertex/mesh-3d-deform-frag.glsl") 
			);
		noiseTexture = new SimplexNoiseTexture(cols, rows, true);
		displaceTexture = PG.newPG32(cols, rows, false, false);
		DebugView.setTexture("noiseTexture", noiseTexture.texture());
		DebugView.setTexture("displaceTexture", displaceTexture);
	}
	
	protected void drawApp() {
		// setup context
		background(0);
		PG.setCenterScreen(p);
//		PG.setBetterLights(p);
		if(UI.valueToggle(CAMERA_ON)) {
			PG.basicCameraFromMouse(p.g, 0.2f);
		}
		
		// update shader & displacement map
		noiseTexture.offsetX(UI.value(DISPLACE_OFFSET_X) + FrameLoop.count(UI.value(NOISE_SPEED_X)));
		noiseTexture.offsetY(UI.value(DISPLACE_OFFSET_Y) + FrameLoop.count(UI.value(NOISE_SPEED_Y)));
		noiseTexture.update();
		// lerp proper 32-bit texture for displacement
		BlendTowardsTexture.instance(p).setBlendLerp(0.05f);
		BlendTowardsTexture.instance(p).setSourceTexture(noiseTexture.texture());
		BlendTowardsTexture.instance(p).applyTo(displaceTexture);
		// draw & generate shapes
//		pg.beginDraw();
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		polygonShader.shader().set("time", p.frameCount);
		polygonShader.shader().set("displacementMap", displaceTexture);
		polygonShader.shader().set("displaceAmp", UI.value(DISPLACE_AMP));
//		polygonShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		polygonShader.shader().set("lightDir", UI.valueX(LIGHT_DIR), UI.valueY(LIGHT_DIR), UI.valueZ(LIGHT_DIR));
		polygonShader.shader().set("lightCol", UI.valueX(LIGHT_COL), UI.valueY(LIGHT_COL), UI.valueZ(LIGHT_COL));
		polygonShader.shader().set("lightAmbient", UI.valueX(LIGHT_AMBIENT), UI.valueY(LIGHT_AMBIENT), UI.valueZ(LIGHT_AMBIENT));
		polygonShader.shader().set("materialShininess", UI.value(LIGHT_SHININESS));
		polygonShader.update();
		
		// draw mesh group
		p.shader(polygonShader.shader());  
		p.shape(group);
		p.resetShader();
		// shared elements
		// for (int i = 0; i < shared.size(); i++) p.shape(shared.get(i));
	}
		
}
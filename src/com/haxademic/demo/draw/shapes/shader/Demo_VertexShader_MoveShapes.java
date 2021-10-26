package com.haxademic.demo.draw.shapes.shader;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;

public class Demo_VertexShader_MoveShapes 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO:
	// - Add rotation to individual shapes - done, but lighting rotates with the shape. need to update normals?
	// - Try with other shapes (digits?)
	// - Make a new version with a particle system, a la GPU particles
	// - Make a grid that Kinect displaces
	// - Use a real-time texture (camera) as color map
	
	protected ArrayList<PShape> shared = new ArrayList<PShape>();
	protected PShape group;
	protected PShaderHotSwap polygonShader;
	protected SimplexNoiseTexture noiseTexture;
	protected PGraphics displaceTexture;

	protected String CAMERA_ON = "CAMERA_ON";
	protected String ORTHO_CAMERA = "ORTHO_CAMERA";
	protected String DRAW_FLAT = "DRAW_FLAT";
	protected String ADD_BLEND = "ADD_BLEND";
	protected String TEXTURE_MODE = "TEXTURE_MODE";
	protected String GLOBAL_SCALE = "GLOBAL_SCALE";
	protected String SPREAD_SCALE = "SPREAD_SCALE";
	protected String INDIVIDUAL_MESH_SCALE = "INDIVIDUAL_MESH_SCALE";
	protected String NOISE_SPEED_X = "NOISE_SPEED_X";
	protected String NOISE_SPEED_Y = "NOISE_SPEED_Y";
	protected String NOISE_ZOOM = "NOISE_ZOOM";
	protected String ROTATE_AMP = "ROTATE_AMP";
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
		pg = PG.newPG32(pg.width, pg.height, false, false);
		
		// UI
		UI.addTitle("Spheres config");
		UI.addToggle(CAMERA_ON, true, false);
		UI.addToggle(ORTHO_CAMERA, false, false);
		UI.addToggle(DRAW_FLAT, false, false);
		UI.addToggle(ADD_BLEND, false, false);
		UI.addToggle(TEXTURE_MODE, true, false);
		UI.addSlider(GLOBAL_SCALE, 1f, 0.001f, 10f, 0.01f, false);
		UI.addSlider(SPREAD_SCALE, 1f, 0f, 30f, 0.1f, false);
		UI.addSlider(INDIVIDUAL_MESH_SCALE, 2f, 0f, 20f, 0.1f, false);
		UI.addSlider(NOISE_SPEED_X, 0.001f, 0, 0.1f, 0.00001f, false);
		UI.addSlider(NOISE_SPEED_Y, 0.001f, 0, 0.1f, 0.00001f, false);
		UI.addSlider(NOISE_ZOOM, 3f, 0.2f, 10f, 0.01f, false);
		UI.addSlider(DISPLACE_AMP, 10, 0, 1000, 1, false);
		UI.addSlider(ROTATE_AMP, 2, 0, 20, 0.01f, false);
		UI.addSlider(DISPLACE_OFFSET_X, 0, 0, 1000, 0.01f, false);
		UI.addSlider(DISPLACE_OFFSET_Y, 0, 0, 1000, 0.01f, false);
		UI.addSliderVector(LIGHT_DIR, 1f, -10f, 10f, 0.01f, false);
		UI.addSliderVector(LIGHT_COL, 1f, 0, 1, 0.001f, false);
		UI.addSliderVector(LIGHT_AMBIENT, 1f, 0, 1, 0.001f, false);
		UI.addSlider(LIGHT_SHININESS, 1f, 0, 1000, 0.01f, false);
		
		// config build shapes
		float shapeSize = 12;
		float shapeSpacing = shapeSize * 1.2f;
		float shapeSpacingHalf = shapeSpacing / 2f;
		p.sphereDetail(7);

		// create PShapes inside a group
		int startBuildTime = p.millis();
		int cols = 200;
		int rows = 100;
		startBuildTime = p.millis();
		int numVerts = 0;
		group = p.createShape(P.GROUP);
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float gridX = shapeSpacingHalf + -(shapeSpacing * cols/2) + (x * shapeSpacing);
				float gridY = shapeSpacingHalf + -(shapeSpacing * rows/2) + (y * shapeSpacing);
				float gridZ = 0;
//				PShape shape = PShapeUtil.createSphere(shapeSize, gridX, gridY, gridZ, 127 + 127 * p.color(P.sin(x/10f), 127 + 127 * P.sin(y/10f), 127 + 127 * P.sin(x+y/100f)), 0, 0);
//				PShape shape = PShapeUtil.createBox(shapeSize, shapeSize, shapeSize, gridX, gridY, 0, 127 + 127 * p.color(P.sin(x/10f), 127 + 127 * P.sin(y/10f), 127 + 127 * P.sin(x+y/100f)), 0, 0);
//				shape.setTexture(DemoAssets.textureJupiter());
				PShape shape = PShapeUtil.createTexturedRect(shapeSize, shapeSize, gridX, gridY, 0, DemoAssets.particle());
				numVerts += shape.getVertexCount();
				// give the shape attributes for the shader to pick out their UV coord from grid index
				shape.attrib("x", x);
				shape.attrib("y", y);
				shape.attrib("shapeCenterX", gridX);
				shape.attrib("shapeCenterY", gridY);
				shape.attrib("shapeCenterZ", gridZ);
				group.addChild(shape);
			}
		}
		DebugView.setValue("Group PShape time", p.millis() - startBuildTime + "ms");
		DebugView.setValue("Num shapes", cols * rows);
		DebugView.setValue("Num verts", numVerts);
		
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
		p.background(0);
		PG.setDrawFlat2d(p, UI.valueToggle(DRAW_FLAT));
		PG.setCenterScreen(p);
		
		// camera
		if(UI.valueToggle(ORTHO_CAMERA)) p.ortho();
		else p.perspective();
		if(UI.valueToggle(CAMERA_ON)) {
			PG.basicCameraFromMouse(p.g, 0.1f);
		}
		p.rotateX(P.PI);	// textures are flipped on y-axis
		
		// update shader & displacement map
		noiseTexture.offsetX(UI.valueEased(DISPLACE_OFFSET_X) + FrameLoop.count(UI.valueEased(NOISE_SPEED_X)));
		noiseTexture.offsetY(UI.valueEased(DISPLACE_OFFSET_Y) + FrameLoop.count(UI.valueEased(NOISE_SPEED_Y)));
		noiseTexture.zoom(UI.valueEased(NOISE_ZOOM));
		noiseTexture.update();
		
		// lerp proper 32-bit texture for displacement
		BlendTowardsTexture.instance(p).setBlendLerp(0.5f);
		BlendTowardsTexture.instance(p).setSourceTexture(noiseTexture.texture());
		BlendTowardsTexture.instance(p).applyTo(displaceTexture);
		
		// draw & generate shapes
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
		polygonShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		polygonShader.shader().set("time", p.frameCount);
		polygonShader.shader().set("displacementMap", displaceTexture);
		polygonShader.shader().set("displaceAmp", UI.valueEased(DISPLACE_AMP));
		polygonShader.shader().set("rotateAmp", UI.valueEased(ROTATE_AMP));
		polygonShader.shader().set("globalScale", UI.valueEased(GLOBAL_SCALE));
		polygonShader.shader().set("spreadScale", UI.valueEased(SPREAD_SCALE));
		polygonShader.shader().set("individualMeshScale", UI.valueEased(INDIVIDUAL_MESH_SCALE));
		polygonShader.shader().set("textureMode", UI.valueToggle(TEXTURE_MODE) ? 1 : 0);
		polygonShader.shader().set("lightDir", UI.valueX(LIGHT_DIR), UI.valueY(LIGHT_DIR), UI.valueZ(LIGHT_DIR));
		polygonShader.shader().set("lightCol", UI.valueX(LIGHT_COL), UI.valueY(LIGHT_COL), UI.valueZ(LIGHT_COL));
		polygonShader.shader().set("lightAmbient", UI.valueX(LIGHT_AMBIENT), UI.valueY(LIGHT_AMBIENT), UI.valueZ(LIGHT_AMBIENT));
		polygonShader.shader().set("materialShininess", UI.valueEased(LIGHT_SHININESS));
		polygonShader.update();
		
		// draw mesh group
		if(UI.valueToggle(ADD_BLEND)) p.blendMode(PBlendModes.ADD);
		p.shader(polygonShader.shader());
		p.shape(group);
		p.resetShader();
		p.blendMode(PBlendModes.BLEND);
	}
		
}
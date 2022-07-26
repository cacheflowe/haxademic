package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PShape;
import processing.data.JSONObject;

public class Demo_VertexShader_GPUParticlesMoveShapes_CubeOfCubes
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected enum UI_Cube {
		SceneRotation_,
		CenterPoint_,
		RotationAmp,
	}
	protected int FRAMES = 800;
	protected PShaderHotSwap polygonShader;
	protected PShape cubes;
	
	protected void config() {
//		Config.setProperty(AppSettings.SHOW_DEBUG, true);
//		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.LOOP_TICKS, 8);
		Config.setProperty(AppSettings.WIDTH, 1024);
		Config.setProperty(AppSettings.HEIGHT, 1024);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES - FRAMES/4);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 - FRAMES/4);
	}
	
	protected void firstFrame() {
		buildCubes();
		
		MidiDevice.init(LaunchControlXL.deviceName);
		
		UI.addTitle("CUBE CONTROL");
		UI.addSlider(UI_Cube.RotationAmp.toString(), 0, 0, 0.01f, 0.0001f, false, LaunchControlXL.SLIDERS[5]);
		UI.addSliderVector(UI_Cube.SceneRotation_.toString(), 0, -P.TWO_PI, P.TWO_PI, 0.001f, false, LaunchControlXL.KNOBS_ROW_2[0], LaunchControlXL.KNOBS_ROW_2[1], LaunchControlXL.KNOBS_ROW_2[2]);
		UI.addSliderVector(UI_Cube.CenterPoint_.toString(), 0, -pg.width, pg.width, 1f, false, LaunchControlXL.KNOBS_ROW_3[0], LaunchControlXL.KNOBS_ROW_3[1], LaunchControlXL.KNOBS_ROW_3[2]);
	}
	
	protected void buildCubes() {
		// build mesh
		cubes = p.createShape(P.GROUP);
		int startBuildTime = p.millis();
		int numVerts = 0;
		int numShapes = 0;
		float cubeDetail = 10;
		float cellSize = 80;
		float cubeSize = cellSize * cubeDetail;
		float cellSizeHalf = cellSize / 2f;
		float cubeSizeHalf = cubeSize / 2f;
		for (float x=0; x < cubeDetail; x++) {
			for (float y=0; y < cubeDetail; y++) {
				for (float z=0; z < cubeDetail; z++) {		// only half a sphere
					// position
					float baseX = (-cubeSizeHalf + cellSizeHalf + x * cellSize);
					float baseY = (-cubeSizeHalf + cellSizeHalf + y * cellSize);
					float baseZ = (-cubeSizeHalf + cellSizeHalf + z * cellSize);

					// create sub shape
					PShape box = PShapeUtil.createBox(cellSize, cellSize, cellSize, baseX, baseY, baseZ, p.color(255,0,0), p.color(255), 3);
					numVerts += box.getVertexCount();
					numShapes++;
					
					// give the shape attributes for the shader to pick out their UV coord from grid index
					box.attrib("x", x);
					box.attrib("y", y);
					box.attrib("z", z);
					box.attrib("shapeCenterX", (float) baseX);
					box.attrib("shapeCenterY", (float) baseY);
					box.attrib("shapeCenterZ", (float) baseZ);
					box.setStroke(false);

					cubes.addChild(box);
				}
			}
		}
		DebugView.setValue("Group PShape time", p.millis() - startBuildTime + "ms");
		DebugView.setValue("Num shapes", numShapes);
		DebugView.setValue("Num verts", numVerts);
		
		
		// load shader to move spheres
		polygonShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/vertex/mesh-3d-cubes-deform-vert.glsl"),
			FileUtil.getPath("haxademic/shaders/vertex/mesh-3d-cubes-deform-frag.glsl") 
		);

	}
	
	protected void drawApp() {
		p.background(0);
		PG.setCenterScreen(p.g);
//		p.g.translate(0, 0, FrameLoop.progressOsc(0.f, -p.height * 1f));
//		p.ortho();
//		p.perspective();
		p.rotateX(FrameLoop.progressOsc(-0.6f, 0.6f));
		p.rotateY(FrameLoop.progressOsc(P.QUARTER_PI, P.QUARTER_PI));
//		p.rotateX(UI.valueXEased(UI_Cube.SceneRotation_.toString()));
//		p.rotateY(UI.valueYEased(UI_Cube.SceneRotation_.toString()));
//		p.rotateZ(UI.valueZEased(UI_Cube.SceneRotation_.toString()));
//		p.lights();
		
		// displacement location
		float displaceX = UI.valueXEased(UI_Cube.CenterPoint_.toString());
		float displaceY = UI.valueYEased(UI_Cube.CenterPoint_.toString());
		float displaceZ = UI.valueZEased(UI_Cube.CenterPoint_.toString());
		p.push();
		p.noStroke();
		p.fill(200);
		p.translate(displaceX, displaceY, displaceZ);
		p.pop();
		
		// draw cube of cubes
		// apply deform shader and draw mesh - CANNOT HAVE PROCESSING LIGHTS TURNED ON!
//		polygonShader.shader().set("modelviewInv", ((PGraphicsOpenGL) g).modelviewInv);
		polygonShader.shader().set("time", p.frameCount);
//		polygonShader.shader().set("displacementMap", displaceTexture);
		polygonShader.shader().set("curlZoom", FrameLoop.progressOsc(0.f, 0.002f));
		polygonShader.shader().set("osc", FrameLoop.progressOsc(0.f, 1.1f));
		polygonShader.shader().set("spreadScale", FrameLoop.progressOsc(0.f, 0.f));
		polygonShader.shader().set("globalScale", 0.45f);
//		polygonShader.shader().set("rotateAmp", UI.valueEased(ROTATE_AMP));
//		polygonShader.shader().set("globalScale", UI.valueEased(GLOBAL_SCALE));
//		polygonShader.shader().set("spreadScale", UI.valueEased(SPREAD_SCALE));
//		polygonShader.shader().set("individualMeshScale", UI.valueEased(INDIVIDUAL_MESH_SCALE));
//		polygonShader.shader().set("textureMode", UI.valueToggle(TEXTURE_MODE) ? 1 : 0);
//		polygonShader.shader().set("lightDir", UI.valueX(LIGHT_DIR), UI.valueY(LIGHT_DIR), UI.valueZ(LIGHT_DIR));
//		polygonShader.shader().set("lightCol", UI.valueX(LIGHT_COL), UI.valueY(LIGHT_COL), UI.valueZ(LIGHT_COL));
//		polygonShader.shader().set("lightAmbient", UI.valueX(LIGHT_AMBIENT), UI.valueY(LIGHT_AMBIENT), UI.valueZ(LIGHT_AMBIENT));
//		polygonShader.shader().set("materialShininess", UI.valueEased(LIGHT_SHININESS));
		polygonShader.update();
		
		// draw mesh group
//		p.blendMode(PBlendModes.ADD);
		p.shader(polygonShader.shader());
		p.shape(cubes);
		p.resetShader();
		p.blendMode(PBlendModes.BLEND);

		// post fx

		/*
		ToneMappingFilter.instance(P.p).setMode(1);
		ToneMappingFilter.instance(P.p).setGamma(1.75f);
		ToneMappingFilter.instance(P.p).setCrossfade(1f);
		ToneMappingFilter.instance(P.p).applyTo(p.g);
		
		// add some saturation back in
		SaturationFilter.instance(p).setSaturation(1.1f);
		SaturationFilter.instance(p).applyTo(p.g);
		ContrastFilter.instance(p).setContrast(1.1f);
		ContrastFilter.instance(p).applyTo(p.g);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.12f);
		GrainFilter.instance(p).applyTo(p.g);

		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p.g);
		 */
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '-') {
			UI.setValueX(UI_Cube.SceneRotation_.toString(), -P.QUARTER_PI/1.5f);
			UI.setValueY(UI_Cube.SceneRotation_.toString(), P.QUARTER_PI);
			UI.setValueZ(UI_Cube.SceneRotation_.toString(), 0);
		}
		if(p.key == '1') P.out(JsonUtil.jsonToSingleLine(UI.valuesToJSON()));
		if(p.key == '2') UI.loadValuesFromJSON(JSONObject.parse(""));
//		if(p.key == '3') UI.loadValuesFromJSON(JSONObject.parse("{ \"CellSize\": 30.0, \"CubeDetail\": 15.0, \"Spread__X\": 1.0, \"Spread__Y\": 1.0, \"Spread__Z\": 0.0, \"EffectDist\": 1.0, \"Displace\": 0.0, \"Shrink\": 0.0, \"RotationAmp\": 0.0, \"SceneRotation__X\": 0.0, \"SceneRotation__Y\": 0.0, \"SceneRotation__Z\": 0.0, \"CenterPoint__X\": 0.0, \"CenterPoint__Y\": 0.0, \"CenterPoint__Z\": -30.0 }"));

	}
}

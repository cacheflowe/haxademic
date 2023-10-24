package com.haxademic.demo.draw.image;


import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.DepthBuffer;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;


public class Demo_DepthBuffer_SSAO
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DepthBuffer depthBuffer;

	protected PShaderHotSwap ssaoShader;
	protected PGraphics result;

	protected String onlyAO = "onlyAO";
	protected String aoClamp = "aoClamp";
	protected String lumInfluence = "lumInfluence";
	protected String cameraNear = "cameraNear";
	protected String cameraFar = "cameraFar";
	protected String samples = "samples";
	protected String radius = "radius";
	protected String useNoise = "useNoise";
	protected String noiseAmount = "noiseAmount";
	protected String diffArea = "diffArea";
	protected String gDisplace = "gDisplace";
	protected String diffMult = "diffMult";
	protected String gaussMult = "gaussMult";

	protected String config1 = """
		{
			"gDisplace": 0.5600005388259888,
			"diffMult": 307,
			"gaussMult": -2,
			"cameraNear": 200,
			"aoClamp": -2.980001449584961,
			"samples": 128,
			"cameraFar": 1400,
			"onlyAO": 1,
			"noiseAmount": 0.0001,
			"lumInfluence": 1.1099995374679565,
			"radius": 0.035,
			"diffArea": 0.5399996638298035,
			"useNoise": 1
		}
		""";

	protected int APP_W = 800;
	protected int APP_H = 800;
	protected void config() {
		Config.setAppSize(APP_W * 3, APP_H);
		Config.setPgSize(APP_W, APP_H);
	}

	public void setup() {
		super.setup();
		
		// build custom frame buffer...
		// this has to happen in setup(), probably because of something I'm doing in the settings/setup process
		depthBuffer = new DepthBuffer(800, 800);
	}

	protected void firstFrame() {
		UI.addSlider(onlyAO, 0, 0, 1, 1, false);
		UI.addSlider(aoClamp, 1.5f, -5f, 5f, 0.01f, false);
		UI.addSlider(lumInfluence, 0.2f, -5f, 5f, 0.01f, false);
		UI.addSlider(cameraNear, 175, 1, 500, 1, false);
		UI.addSlider(cameraFar, 1700, 500f, 2000f, 1, false);
		UI.addSlider(samples, 32, 2, 128, 1, false);
		UI.addSlider(radius, 1, 0, 2, 0.001f, false);
		UI.addSlider(diffArea, 0.65f, 0, 5, 0.01f, false);
		UI.addSlider(gDisplace, 0.65f, 0, 5, 0.01f, false);
		UI.addSlider(diffMult, 100f, 1, 1000, 1f, false);
		UI.addSlider(gaussMult, -2, -4, 4, 0.01f, false);
		UI.addSlider(useNoise, 12, 0, 1, 1, false);
		UI.addSlider(noiseAmount, 0.00003f, 0.00003f, 0.003f, 0.00001f, false);
		UI.loadValuesFromJSON(JsonUtil.jsonFromString(config1));

		// ssaoShader = loadShader(FileUtil.getPath("haxademic/shaders/vertex/ssao-frag.glsl"));
		ssaoShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/vertex/ssao-frag-depth-map.glsl"));
		// ssaoShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/vertex/ssao-frag.glsl"));

		pg = PG.newPG32(pg.width, pg.height, true, false);
		result = PG.newPG32(pg.width, pg.height, true, false);
	}

	protected void drawApp() {
		p.background(0);
		
		// Draw some spheres
		pg.beginDraw();
		pg.lights();
		pg.background(255, 0, 0);

		// background plane
		pg.push();
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		pg.translate(0, 0, -700);
		pg.fill(0, 255, 0);
		pg.rect(0, 0, pg.width * 3, pg.height * 3);
		pg.pop();
		// floor
		pg.push();
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		pg.translate(0, 310, 0);
		pg.rotateX(P.HALF_PI);
		pg.fill(255, 0, 0);
		pg.rect(0, 0, pg.width * 3f, pg.height * 3f);
		pg.pop();

		// set near/far
		CameraUtil.setCameraDistanceGood(pg, UI.value(cameraNear), UI.value(cameraFar));

		pg.fill(0xff1E58F5);
		pg.noStroke();
		int numSpheres = 24;
		for (int i = 0; i < numSpheres; i++) {
			float frame = frameCount * 0.4f;
			pg.pushMatrix();
			pg.translate(width * i / (float) numSpheres, height / 2 + 300 * sin(frame * 0.02f + i), -200 * sin(frame * 0.01f + i));
			pg.rotateY(i + frame/100f);
			pg.rotateZ(i + frame/100f);
			pg.box(140);
			pg.popMatrix();
		}

		// Draw a humanoid
		PG.setCenterScreen(pg);
		pg.translate(0, pg.height * 0.4f);
		pg.scale(100, 100, 200);
		pg.rotateY(p.frameCount * 0.01f + P.HALF_PI);
		pg.shape(DemoAssets.objHumanoid());
		
		pg.endDraw();
		
		// Copy depth buffer from `pg`
		depthBuffer.copyDepthToPG(pg);
		
		updateShader();

		// Draw original scene
		p.image(pg, 0, 0);
		p.image(depthBuffer.image(), p.width * 0.3333f, 0);
		
		// p.image(result, p.width * 0.6666f, 0);
		// p.blendMode(PBlendModes.MULTIPLY);
		// p.image(pg, p.width * 0.6666f, 0);
		// p.blendMode(PBlendModes.BLEND);

		InvertFilter.instance().applyTo(result);
		p.image(pg, p.width * 0.6666f, 0);
		p.blendMode(PBlendModes.SUBTRACT);
		p.image(result, p.width * 0.6666f, 0);
		p.blendMode(PBlendModes.BLEND);

		DebugView.setTexture("depthBuffer", depthBuffer.image());
		DebugView.setTexture("pg", pg);
		DebugView.setTexture("result", result);
	}

	protected void updateShader() {
		ssaoShader.update();
		ssaoShader.shader().set("time", p.frameCount);
		ssaoShader.shader().set("size", (float) pg.width, (float) pg.height);
		ssaoShader.shader().set("tDiffuse", pg);
		ssaoShader.shader().set("tDepth", depthBuffer.image());
		ssaoShader.shader().set("onlyAO", UI.value(onlyAO) == 1);
		ssaoShader.shader().set("aoClamp", UI.value(aoClamp));
		ssaoShader.shader().set("lumInfluence", UI.value(lumInfluence));
		ssaoShader.shader().set("cameraNear", UI.value(cameraNear));
		ssaoShader.shader().set("cameraFar", UI.value(cameraFar));

		ssaoShader.shader().set("samples", UI.valueInt(samples));
		ssaoShader.shader().set("radius", UI.value(radius));
		ssaoShader.shader().set("useNoise", UI.value(useNoise) == 1);
		ssaoShader.shader().set("noiseAmount", UI.value(noiseAmount) * 10f);
		ssaoShader.shader().set("diffArea", UI.value(diffArea));
		ssaoShader.shader().set("gDisplace", UI.value(gDisplace));
		ssaoShader.shader().set("diffMult", UI.value(diffMult));
		ssaoShader.shader().set("gaussMult", UI.value(gaussMult));

		result.beginDraw();
		PG.setTextureRepeat(result, false);
		result.filter(ssaoShader.shader());
		result.endDraw();

		BlurProcessingFilter.instance().setBlurSize(15);
		BlurProcessingFilter.instance().setSigma(15);
		BlurProcessingFilter.instance().applyTo(result);
		BlurProcessingFilter.instance().applyTo(result);
		// BlurProcessingFilter.instance().applyTo(result);
		// BlurProcessingFilter.instance().applyTo(result);
		// BlurProcessingFilter.instance().applyTo(result);
	}

}

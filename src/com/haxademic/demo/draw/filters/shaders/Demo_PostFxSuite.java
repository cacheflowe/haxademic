package com.haxademic.demo.draw.filters.shaders;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.filters.pshader.ToneMappingFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_PostFxSuite
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	protected PGraphics fakeLightingMap;
	protected PGraphics coralPG;
	protected CoralLSystem coral;
	protected TextureShader causticsShader;
	protected PGraphics causticsPG;
	// tonemapping UI
	protected String ACTIVE = "ACTIVE";
	protected String MODE = "MODE";
	protected String GAMMA = "GAMMA";
	protected String CROSSFADE = "CROSSFADE";
	// extra effects UI
	protected String SATURATION = "SATURATION";
	protected String CONTRAST = "CONTRAST";
	// grain UI
	protected String GRAIN_ACTIVE = "GRAIN_ACTIVE";
	protected String GRAIN_AMOUNT = "GRAIN_AMOUNT";
	// vignette UI
	protected String VIGNETTE_ACTIVE = "VIGNETTE_ACTIVE";
	protected String VIGNETTE_DARKNESS = "VIGNETTE_DARKNESS";
	protected String VIGNETTE_SPREAD = "VIGNETTE_SPREAD";
	// god rays UI
	protected String GODRAYS_ACTIVE = "GODRAYS_ACTIVE";
	protected String GODRAYS_DECAY = "GODRAYS_DECAY";
	protected String GODRAYS_DENSITY = "GODRAYS_DENSITY";
	protected String GODRAYS_WEIGHT = "GODRAYS_WEIGHT";
	protected String GODRAYS_AMP = "GODRAYS_AMP";
	protected String GODRAYS_LIGHT_X = "GODRAYS_LIGHT_X";
	protected String GODRAYS_LIGHT_Y = "GODRAYS_LIGHT_Y";
	// fake lighting UI
	protected String FAKELIGHTING_ACTIVE = "FAKELIGHTING_ACTIVE";
	protected String FAKELIGHTING_AMBIENT = "FAKELIGHTING_AMBIENT";
	protected String FAKELIGHTING_GRAD_AMP = "FAKELIGHTING_GRAD_AMP";
	protected String FAKELIGHTING_GRAD_BLUR = "FAKELIGHTING_GRAD_BLUR";
	protected String FAKELIGHTING_SPEC_AMP = "FAKELIGHTING_SPEC_AMP";
	protected String FAKELIGHTING_DIFF_DARK = "FAKELIGHTING_DIFF_DARK";
	// coral sway UI
	protected String CORAL_SWAY_AMP = "CORAL_SWAY_AMP";
	protected String CORAL_SWAY_SPEED = "CORAL_SWAY_SPEED";
	// coral blobby UI
	protected String CORAL_BLOBBY_ACTIVE = "CORAL_BLOBBY_ACTIVE";
	protected String CORAL_BLOBBY_BLUR = "CORAL_BLOBBY_BLUR";
	protected String CORAL_BLOBBY_THRESHOLD = "CORAL_BLOBBY_THRESHOLD";
	// caustics UI
	protected String CAUSTICS_ACTIVE = "CAUSTICS_ACTIVE";
	protected String CAUSTICS_ZOOM = "CAUSTICS_ZOOM";
	protected String CAUSTICS_AMP = "CAUSTICS_AMP";
	protected String CAUSTICS_SPEED = "CAUSTICS_SPEED";

	protected void config() {
		Config.setAppSize(1080, 1920);
		Config.setProperty(AppSettings.SHOW_UI, true);	
	}
	
	protected void firstFrame() {
		// grow the coral - separate from all the post-fx setup/logic below
		coralPG = PG.newPG(p.width, p.height);
		coral = new CoralLSystem(p.width * 0.5f, p.height, p.height * 0.16f, 14f, 0xffd4e8f0);
		
		// water caustics texture, drawn to its own buffer and composited additively
		causticsShader = new TextureShader(TextureShader.water_caustics);
		causticsPG = PG.newPG(p.width, p.height);
		DebugView.setTexture("causticsPG", causticsPG);
		
		// add tonemapping UI
		UI.addTitle("Tonemapping");
		UI.addToggle(ACTIVE, true, false);
		UI.addSlider(MODE, 1, 0, 9, 1, false);
		UI.addSlider(GAMMA, 2.2f, 0, 10, 0.01f, false);
		UI.addSlider(CROSSFADE, 1, 0, 1, 0.01f, false);
		
		// extra controls
		UI.addTitle("Postprocessing");
		UI.addSlider(CONTRAST, 1, 0, 3, 0.01f, false);
		UI.addSlider(SATURATION, 1, 0, 3, 0.01f, false);
		
		// grain
		UI.addTitle("Grain");
		UI.addToggle(GRAIN_ACTIVE, true, false);
		UI.addSlider(GRAIN_AMOUNT, 0.1f, 0, 1, 0.01f, false);
		
		// vignette
		UI.addTitle("Vignette");
		UI.addToggle(VIGNETTE_ACTIVE, true, false);
		UI.addSlider(VIGNETTE_DARKNESS, 0.85f, 0, 1, 0.01f, false);
		UI.addSlider(VIGNETTE_SPREAD, 0.15f, 0, 1, 0.01f, false);
		
		// god rays
		UI.addTitle("God Rays");
		UI.addToggle(GODRAYS_ACTIVE, false, false);
		UI.addSlider(GODRAYS_DECAY, 0.97f, 0, 1, 0.001f, false);
		UI.addSlider(GODRAYS_DENSITY, 0.5f, 0, 1, 0.01f, false);
		UI.addSlider(GODRAYS_WEIGHT, 0.1f, 0, 1, 0.01f, false);
		UI.addSlider(GODRAYS_AMP, 1f, 0, 3, 0.01f, false);
		UI.addSlider(GODRAYS_LIGHT_X, 0.5f, -0.5f, 1.5f, 0.01f, false);
		UI.addSlider(GODRAYS_LIGHT_Y, 0.5f, -0.5f, 1.5f, 0.01f, false);
		
		// fake lighting
		UI.addTitle("Fake Lighting");
		UI.addToggle(FAKELIGHTING_ACTIVE, false, false);
		UI.addSlider(FAKELIGHTING_AMBIENT, 2f, 0.3f, 6f, 0.01f, false);
		UI.addSlider(FAKELIGHTING_GRAD_AMP, 0.66f, 0f, 6f, 0.005f, false);
		UI.addSlider(FAKELIGHTING_GRAD_BLUR, 1f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(FAKELIGHTING_SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(FAKELIGHTING_DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f, false);
		
		// coral sway - gentle underwater-current feel
		UI.addTitle("Coral Sway");
		UI.addSlider(CORAL_SWAY_AMP, 10f, 0, 40f, 0.5f, false);
		UI.addSlider(CORAL_SWAY_SPEED, 0.02f, 0, 0.1f, 0.001f, false);
		
		// coral blobby - blur + threshold to merge thin branches into blobs
		UI.addTitle("Coral Blobby");
		UI.addToggle(CORAL_BLOBBY_ACTIVE, true, false);
		UI.addSlider(CORAL_BLOBBY_BLUR, 1.5f, 0, 5f, 0.01f, false);
		UI.addSlider(CORAL_BLOBBY_THRESHOLD, 0.5f, 0, 1, 0.01f, false);
		
		// water caustics
		UI.addTitle("Caustics");
		UI.addToggle(CAUSTICS_ACTIVE, true, false);
		UI.addSlider(CAUSTICS_ZOOM, 2f, 0.2f, 10f, 0.01f, false);
		UI.addSlider(CAUSTICS_AMP, 0.6f, 0, 2f, 0.01f, false);
		UI.addSlider(CAUSTICS_SPEED, 0.01f, 0, 0.05f, 0.001f, false);
	}
	
	protected void drawApp() {
		p.background(0);
		drawCoral();		
		doFakeLighting();
		doToneMapping();
		doGodRays();
		doCaustics();
		doGrain();
		doVignette();
	}

	protected void drawCoral() {
		coral.setSwayAmp(UI.value(CORAL_SWAY_AMP));
		coral.draw(coralPG, p.frameCount * UI.value(CORAL_SWAY_SPEED));
		doCoralBlobby();
		p.image(coralPG, 0, 0);

		// darken the a bit to make the coral pop more
		BrightnessFilter.instance().setBrightness(0.9f);
		BrightnessFilter.instance().applyTo(coralPG);
	}
	
	protected void doCoralBlobby() {
		if(UI.valueToggle(CORAL_BLOBBY_ACTIVE)) {
			BlurHFilter.instance().setBlurByPercent(UI.value(CORAL_BLOBBY_BLUR), coralPG.width);
			BlurVFilter.instance().setBlurByPercent(UI.value(CORAL_BLOBBY_BLUR), coralPG.height);
			BlurHFilter.instance().applyTo(coralPG);
			BlurVFilter.instance().applyTo(coralPG);
			
			ThresholdFilter.instance().setCutoff(UI.value(CORAL_BLOBBY_THRESHOLD));
			ThresholdFilter.instance().applyTo(coralPG);
		}
	}
	
	protected void doCaustics() {
		if(UI.valueToggle(CAUSTICS_ACTIVE)) {
			causticsShader.shader().set("zoom", UI.value(CAUSTICS_ZOOM));
			causticsShader.setAmp(UI.value(CAUSTICS_AMP));
			causticsShader.setTimeMult(UI.value(CAUSTICS_SPEED));
			causticsShader.updateTime();
			causticsPG.filter(causticsShader.shader());
			
			// ...then composite it into the scene additively, like light shimmering on/through water
			p.blendMode(PBlendModes.ADD);
			PG.setPImageAlpha(p, 0.2f);
			p.image(causticsPG, 0, 0);
			p.blendMode(PBlendModes.BLEND);
			PG.resetPImageAlpha(p);
		}
	}
	
	protected void doToneMapping() {
		if(UI.valueToggle(ACTIVE)) {
			ToneMappingFilter.instance().setMode(UI.valueInt(MODE));
			ToneMappingFilter.instance().setGamma(UI.value(GAMMA));
			ToneMappingFilter.instance().setCrossfade(UI.value(CROSSFADE));
			ToneMappingFilter.instance().applyTo(p.g);
			
			// add some saturation back in
			SaturationFilter.instance().setSaturation(UI.value(SATURATION));
			SaturationFilter.instance().applyTo(p.g);
			ContrastFilter.instance().setContrast(UI.value(CONTRAST));
			ContrastFilter.instance().applyTo(p.g);
		}
	}
	
	protected void doGrain() {
		if(UI.valueToggle(GRAIN_ACTIVE)) {
			GrainFilter.instance().setCrossfade(UI.value(GRAIN_AMOUNT));
			GrainFilter.instance().applyTo(p.g);
		}
	}
	
	protected void doVignette() {
		if(UI.valueToggle(VIGNETTE_ACTIVE)) {
			VignetteFilter.instance().setDarkness(UI.value(VIGNETTE_DARKNESS));
			VignetteFilter.instance().setSpread(UI.value(VIGNETTE_SPREAD));
			VignetteFilter.instance().applyTo(p.g);
		}
	}
	
	protected void doGodRays() {
		if(UI.valueToggle(GODRAYS_ACTIVE)) {
			GodRays.instance().setDecay(UI.value(GODRAYS_DECAY));
			GodRays.instance().setDensity(UI.value(GODRAYS_DENSITY));
			GodRays.instance().setWeight(UI.value(GODRAYS_WEIGHT));
			GodRays.instance().setLightPos(UI.value(GODRAYS_LIGHT_X), UI.value(GODRAYS_LIGHT_Y));
			GodRays.instance().setAmp(UI.value(GODRAYS_AMP));
			GodRays.instance().applyTo(p.g);
		}
	}
	
	protected void doFakeLighting() {
		if(UI.valueToggle(FAKELIGHTING_ACTIVE)) {
			// lazy-init a blurred map for the shader to derive fake normals/lighting from -
			// the shader crashes without a map bound, so we can't rely on the default null
			if(fakeLightingMap == null) {
				fakeLightingMap = PG.newPG(p.width, p.height);
			}
			// update blurred fake lighting map each frame, so the shader can use it to derive fake normals/lighting
			ImageUtil.cropFillCopyImage(coralPG, fakeLightingMap, true);
			BlurHFilter.instance().setBlurByPercent(1, fakeLightingMap.width);
			BlurVFilter.instance().setBlurByPercent(1, fakeLightingMap.height);
			BlurHFilter.instance().applyTo(fakeLightingMap);
			BlurVFilter.instance().applyTo(fakeLightingMap);
			DebugView.setTexture("fakeLightingMap", fakeLightingMap);
			
			FakeLightingFilter.instance().setMap(fakeLightingMap);
			FakeLightingFilter.instance().setAmbient(UI.value(FAKELIGHTING_AMBIENT));
			FakeLightingFilter.instance().setGradAmp(UI.value(FAKELIGHTING_GRAD_AMP));
			FakeLightingFilter.instance().setGradBlur(UI.value(FAKELIGHTING_GRAD_BLUR));
			FakeLightingFilter.instance().setSpecAmp(UI.value(FAKELIGHTING_SPEC_AMP));
			FakeLightingFilter.instance().setDiffDark(UI.value(FAKELIGHTING_DIFF_DARK));
			FakeLightingFilter.instance().applyTo(p.g);
		}
	}






	/**
	 * Small branching L-system that procedurally grows a coral-like structure of
	 * line segments, recursively splitting into thinner/shorter child branches.
	 * 
	 * Kept isolated from Demo_PostFxSuite's post-processing code on purpose -
	 * this class only knows how to grow + draw branches, nothing about
	 * shaders/UI.
	 * 
	 * Generation is hard-capped at MAX_BRANCHES so a demo/UI slider fat-fingered
	 * toward deep recursion can't runaway and tank the frame rate.
	 */
	public class CoralLSystem {

		public static final int MAX_BRANCHES = 1000;

		protected static class Branch {
			public float x1, y1, x2, y2;
			public float thickness;
			public int depth; // generation index at the branch's start point (x1,y1) - its end point (x2,y2) is depth+1

			public Branch(float x1, float y1, float x2, float y2, float thickness, int depth) {
				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x2;
				this.y2 = y2;
				this.thickness = thickness;
				this.depth = depth;
			}
		}

		protected ArrayList<Branch> branches = new ArrayList<Branch>();
		protected int strokeColor;
		protected int maxDepth = 1;
		protected float swayAmp = 10f;
		protected float swayFreq = 0.01f;

		public CoralLSystem(float originX, float originY, float startLength, float startThickness, int strokeColor) {
			this.strokeColor = strokeColor;
			grow(originX, originY, -P.HALF_PI, startLength, startThickness, 0);
		}

		public void setSwayAmp(float swayAmp) {
			this.swayAmp = swayAmp;
		}

		public void setSwayFreq(float swayFreq) {
			this.swayFreq = swayFreq;
		}

		// recursively grow branches from (x,y) heading in `angle`, stopping once
		// MAX_BRANCHES is hit, or once a branch gets too thin/short to matter
		protected void grow(float x, float y, float angle, float length, float thickness, int depth) {
			if (branches.size() >= MAX_BRANCHES)
				return;
			if (length < 2f || thickness < 0.6f)
				return;

			float x2 = x + P.cos(angle) * length;
			float y2 = y + P.sin(angle) * length;
			branches.add(new Branch(x, y, x2, y2, thickness, depth));
			if (depth + 1 > maxDepth)
				maxDepth = depth + 1;

			// coral tends to fork into 2 branches near the base, tapering to single
			// tips further out so it doesn't look like a uniform tree
			int numChildren = (depth < 2 || MathUtil.randRangeDecimal(0, 1) < 0.65f) ? 2 : 1;
			float lengthFalloff = MathUtil.randRangeDecimal(0.72f, 0.86f);
			float thicknessFalloff = 0.72f;
			float spread = MathUtil.randRangeDecimal(0.35f, 0.7f);

			for (int i = 0; i < numChildren; i++) {
				if (branches.size() >= MAX_BRANCHES)
					break;
				float sign = (i == 0) ? -1f : 1f;
				float childAngle = angle + sign * spread * MathUtil.randRangeDecimal(0.6f, 1.4f);
				grow(x2, y2, childAngle, length * lengthFalloff, thickness * thicknessFalloff, depth + 1);
			}
		}

		// gentle underwater-current sway - offset is purely a function of each point's
		// ORIGINAL (unswayed) position + depth + time, so a joint shared between a parent's
		// tip and its child's base always computes the identical offset and the branches
		// stay visually connected instead of drifting apart
		protected float swayAmpAtDepth(int depth) {
			return swayAmp * P.constrain((float) depth / (float) maxDepth, 0f, 1f);
		}

		protected float swayOffsetX(float x, float y, int depth, float swayTime) {
			return swayAmpAtDepth(depth) * P.sin(swayTime + y * swayFreq);
		}

		protected float swayOffsetY(float x, float y, int depth, float swayTime) {
			return swayAmpAtDepth(depth) * P.cos(swayTime * 0.8f + x * swayFreq);
		}

		public void draw(PGraphics pg, float swayTime) {
			pg.beginDraw();
			pg.background(0);
			pg.pushStyle();
			pg.noFill();
			pg.stroke(strokeColor);
			for (int i = 0; i < branches.size(); i++) {
				Branch b = branches.get(i);
				pg.strokeWeight(b.thickness);
				float x1 = b.x1 + swayOffsetX(b.x1, b.y1, b.depth, swayTime);
				float y1 = b.y1 + swayOffsetY(b.x1, b.y1, b.depth, swayTime);
				float x2 = b.x2 + swayOffsetX(b.x2, b.y2, b.depth + 1, swayTime);
				float y2 = b.y2 + swayOffsetY(b.x2, b.y2, b.depth + 1, swayTime);
				pg.line(x1, y1, x2, y2);
			}
			pg.popStyle();
			pg.endDraw();
		}

		public int numBranches() {
			return branches.size();
		}

	}

}

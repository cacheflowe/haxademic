package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PShape;
import processing.core.PVector;

public class HeadSkullRenderVirus
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String emissiveMaterial = "emissiveMaterial";
	protected String ambientLight = "ambientLight";
	protected String specularMaterial = "specularMaterial";
	protected String specularLight = "specularLight";
	protected String shininessVal = "shininessVal";
	protected String lightsFalloffVal = "lightsFalloffVal";
	protected String lightsFalloffConstantVal = "lightsFalloffConstantVal";
	protected String spotLightConeAngle = "spotLightConeAngle";
	protected String spotLightConcentration = "spotLightConcentration";
	protected String BG_COLOR_BOT = "BG_COLOR_BOT";
	
	public float centerX;
	public float centerY;

	public int _frames = 250;

	protected PShape outerSphere;
	protected PShape obj;
	protected PVector[] objFaceCenters;
	protected PShape skullObj;
	protected PShape objOrig;
	protected float maxHeadExtent;
	protected Virus[] virii;
	protected Virus[] viriiSm;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.LOOP_FRAMES, _frames );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 201 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 201 + _frames );
		Config.setProperty( AppSettings.SHOW_UI, false );
	}

	protected void firstFrame() {
		centerX = p.width/2;
		centerY = p.height/2;

		// controls
		UI.addSlider(emissiveMaterial, 200f, 0, 255, 0.5f, false);
		UI.addSlider(ambientLight, 200f, 0, 255, 0.5f, false);
		UI.addSlider(specularMaterial, 150, 0, 255, 0.5f, false);
		UI.addSlider(specularLight, 184, 0, 255, 0.5f, false);
		UI.addSlider(shininessVal, 40f, 0, 100, 0.5f, false);
		UI.addSlider(lightsFalloffVal, 0.005f, 0, 0.01f, 0.0001f, false);
		UI.addSlider(lightsFalloffConstantVal, 0.017f, 0, 2f, 0.01f, false);
		UI.addSlider(spotLightConeAngle, 0.2f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(spotLightConcentration, 100f, 0, 1000f, 1f, false);
		UI.addSliderVector(BG_COLOR_BOT, 100f, 0, 255, 1f, true);


		buildHeadModel();
		buildViruses();
		
		// build outer virus?
//		outerSphere = p.loadShape(FileUtil.getPath("models/cv/Virus.obj"));
//		PShapeUtil.centerShape(outerSphere);
//		PShapeUtil.scaleShapeToHeight(outerSphere, p.height * 20f);

	}
	
	protected void buildHeadModel() {
		// load model
		skullObj = p.loadShape( FileUtil.getPath("models/skull-realistic.obj"));
		objOrig = p.loadShape( FileUtil.getPath("models/Trump_lowPoly_updated.obj"));
		obj = p.loadShape( FileUtil.getPath("models/Trump_lowPoly_updated.obj"));
		
		PShapeUtil.scaleShapeToHeight(skullObj, p.height * 0.78f * 2f);
		PShapeUtil.scaleShapeToHeight(objOrig, p.height * 0.7f * 2f);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.7f * 2f);
		
		// get centers of each face
		objFaceCenters = new PVector[obj.getChildCount()];
		for (int i = 0; i < obj.getChildren().length; i++ ) {
			PShape child = obj.getChild(i);
			if(child.getVertexCount() == 3) {
				objFaceCenters[i] = MathUtil.computeTriangleCenter( child.getVertex(0), child.getVertex(1), child.getVertex(2) ).copy();
			} else {
				objFaceCenters[i] = MathUtil.computeQuadCenter( child.getVertex(0), child.getVertex(1), child.getVertex(2), child.getVertex(3) ).copy();
			}
		}
		
		maxHeadExtent = PShapeUtil.getMaxExtent(obj);
	}
	
	protected void buildViruses() {
		UI.addTitle("Viruses");
		int numVirus = 10;
		virii = new Virus[numVirus];
		for(int i=0; i < numVirus; i++) {
			virii[i] = new Virus(i);
		}
		UI.addTitle("Viruses Small");
		viriiSm = new Virus[numVirus];
		for(int i=0; i < numVirus; i++) {
			viriiSm[i] = new Virus(i+100);
		}
	}
	
	protected void addDirectionalLight() {
		// add global directional light
		float directionalOsc = FrameLoop.progress() * P.TWO_PI;
		float pointX = centerX + centerX/2 * P.sin(P.PI + directionalOsc) ;
		float pointY = centerY + centerY/2 * P.cos(P.PI + directionalOsc) ;
//		p.directionalLight(155, 135, 135, 2f * P.sin(directionalOsc), 2f * P.cos(directionalOsc), -1);
		p.directionalLight(145, 105, 105, 2f * P.sin(directionalOsc), -3, -4);
		// show debug light direction
		if(UI.active()) {
			p.pushMatrix();
			p.fill(100, 100, 100);
			p.translate(pointX, pointY, 0);
			p.sphere(10);
			p.popMatrix();
		}
	}
	
	protected void addPointLight() {
		// adds a non-directional light source
		float pointX = centerX + centerX/2 * P.sin(FrameLoop.progress() * P.TWO_PI) ;
		p.pointLight(40, 40, 40, pointX, centerY + 200, 0);
		// show debug light position
		if(UI.active()) {
			p.pushMatrix();
			p.fill(51, 102, 126);
			p.translate(pointX, p.height/2 + 200, 0);
			p.sphere(10);
			p.popMatrix();
		}
	}
	
	protected void addSpotLight() {
		// adds a directional, focusable light source
		float spotLightX = centerX + p.width * 0.1f;
		float spotLightY = centerX + p.width * 0.1f;
		p.spotLight(200, 255, 200, spotLightX, spotLightY, 100, 0, 0, -1, UI.value(spotLightConeAngle), UI.value(spotLightConcentration));
		// show debug light position
		if(UI.active()) {
			p.pushMatrix();
			p.fill(0, 255, 0);
			p.translate(spotLightX, centerY + p.height * 0.1f, 100);
			p.sphere(10);
			p.popMatrix();
		}
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		p.sphereDetail(100);
		p.pushMatrix();

		drawBgGradient();
		
		////////////////////////////////
		// global lights & materials setup
		////////////////////////////////
		// basic global lights:
		p.lightFalloff(UI.value(lightsFalloffConstantVal), UI.value(lightsFalloffVal), 0.0f);
		p.ambientLight(UI.value(ambientLight), UI.value(ambientLight), UI.value(ambientLight));
		p.lightSpecular(UI.value(specularLight), UI.value(specularLight), UI.value(specularLight));

		// materials:
		p.emissive(UI.value(emissiveMaterial), UI.value(emissiveMaterial), UI.value(emissiveMaterial));
		p.specular(UI.value(specularMaterial), UI.value(specularMaterial), UI.value(specularMaterial));
		p.shininess(UI.value(shininessVal));	// affects the specular blur

		////////////////////////////////
		// additional lights
		////////////////////////////////
		addDirectionalLight();
		addPointLight();
//		addSpotLight();

		////////////////////////////////
		// background
		////////////////////////////////
//		p.push();
//		p.translate(0, 0, -10000);
//		p.shape(outerSphere);
//		p.pop();

		////////////////////////////////
		// position & draw shapes grid
		////////////////////////////////
		drawObj();

		// post
		float ampAmount = 0.4f;
		if(FrameLoop.progress() > 0.2f && FrameLoop.progress() < 0.8f) {
			float distortAmp = (FrameLoop.progress() < 0.5f) ? P.map(FrameLoop.progress(), 0.2f, 0.5f, 0, ampAmount) : P.map(FrameLoop.progress(), 0.5f, 0.8f, ampAmount, 0);
			ColorDistortionFilter.instance(p).setAmplitude(distortAmp);
			ColorDistortionFilter.instance(p).setTime(p.frameCount * 0.02f);
//			ColorDistortionFilter.instance(p).applyTo(p);
		}
		SaturationFilter.instance(p).setSaturation(1.4f);
		SaturationFilter.instance(p).applyTo(p);
		
		// close context
		p.popMatrix();
		p.noLights();

		// postprocess
		VignetteFilter.instance(p).setDarkness(0.6f);
		VignetteFilter.instance(p).setSpread(0.3f);
		VignetteFilter.instance(p).applyTo(p.g);

		GrainFilter.instance(p).setTime(p.frameCount * 0.03f);
		GrainFilter.instance(p).setCrossfade(0.02f);
		GrainFilter.instance(p).applyTo(p.g);

	}
		
	protected void drawBgGradient() {
		p.pushMatrix();
		p.translate(p.width/2, p.height/2, -p.width * 2f);
		p.rotate(P.HALF_PI);
		p.scale(5f + 0.5f * P.sin(FrameLoop.progressRads() * 4));
//		Gradients.linear(p, p.width, p.height, p.color(0), p.color(UI.valueX(BG_COLOR_BOT), UI.valueY(BG_COLOR_BOT), UI.valueZ(BG_COLOR_BOT)));
		Gradients.linear(p, p.width, p.height, p.color(35f + 35f * P.sin(P.PI + 4f * FrameLoop.progressRads()), 0, 0), p.color(25f + 25f * P.sin(4f * FrameLoop.progressRads()), 0, 0));
		p.popMatrix();
	}
	

	protected void drawObj() {
		////////////////
		// set context
		////////////////
		p.pushMatrix();
		p.translate(p.width/2, p.height * 0.45f, -p.width);
		p.rotateZ(P.PI);

		////////////////
		// draw virus
		////////////////
		p.pushMatrix();
		p.rotateY(P.sin(P.TWO_PI * FrameLoop.progress()) * 0.05f);
		for (int i = 0; i < virii.length; i++) {
			viriiSm[i].update();
		}
		p.popMatrix();
		

		// rotate head & inner virii
//		float easedProg = Penner.easeInOutCirc(FrameLoop.progress());
		p.rotateY(P.sin(P.TWO_PI * FrameLoop.progress()) * 0.45f);
		
		////////////////
		// draw skull
		////////////////
		/*
		p.pushMatrix();
		p.translate(0, p.height * -0.03f, 0);
		skullObj.disableStyle();
		p.fill(120 + 30f * P.sin(FrameLoop.progress() * P.TWO_PI * 50f), 0, 0);
//		p.scale(0.73f + 0.1f * P.sin(P.TWO_PI * FrameLoop.progress()));
		p.scale(0.71f);
		p.shape(skullObj);
		p.popMatrix();
		*/
		
		////////////////
		// draw virus
		////////////////
		for (int i = 0; i < virii.length; i++) {
			virii[i].update();
		}
		
		////////////////
		// draw head
		////////////////
		p.pushMatrix();
		p.translate(0, p.height * -0.18f, 12);
		p.fill(255);
//		p.scale(0.9f + 0.2f * P.sin(P.PI + P.TWO_PI * FrameLoop.progress()));
		
		// sweeping x progress for distance check
		float xProg = P.map(FrameLoop.progress(), 0, 1, -maxHeadExtent * 1.3f, maxHeadExtent * 1.3f);
		
		// shrink/grow adjusted mesh
		for (int i = 0; i < obj.getChildren().length; i++ ) {
			PShape child = obj.getChild(i);
			PShape childOrig = objOrig.getChild(i);
			for(int vIndex = 0; vIndex < child.getVertexCount(); vIndex++) {
				// PVector vertex = child.getVertex(vIndex);
				PVector vertexOrig = childOrig.getVertex(vIndex);
//				float amp = 1.2f + 0.2f * P.sin((float)i/100f + FrameLoop.progress() * P.TWO_PI);
//				float amp = 1.2f + 0.2f * P.sin((float)i/100f + P.abs(vertexOrig.x) + P.abs(vertexOrig.y) + P.abs(vertexOrig.z) + FrameLoop.progress() * P.TWO_PI);
				// float amp = 1;
//				child.setVertex(vIndex, vertexOrig.x * amp, vertexOrig.y * amp, vertexOrig.z * amp);
				
				// float indexOffset = (float)i / 100f;
//				float easedProgress = Penner.easeInOutCubic(0.5f + 0.5f * P.sin(indexOffset + FrameLoop.progress() * P.TWO_PI), 0, 1, 1);
				
				// get distance to sweeping x coord
				float dist = MathUtil.getDistance(xProg, 0, objFaceCenters[i].x, 0);
				float easedProgress = 0;
				float distanceMax = maxHeadExtent * 0.65f;
				if(P.abs(dist) < distanceMax) {
//					easedProgress = P.map(dist, distanceMax, 0, 0, 1);
					easedProgress = Penner.easeInOutExpo(P.map(dist, distanceMax, 0, 0, 1), 0, 1, 1);
				}

				// set vertices of manipulated object
				child.setVertex(
					vIndex, 
					P.map(easedProgress, 0, 1, vertexOrig.x, objFaceCenters[i].x), 
					P.map(easedProgress, 0, 1, vertexOrig.y, objFaceCenters[i].y), 
					P.map(easedProgress, 0, 1, vertexOrig.z, objFaceCenters[i].z)
				);
			}
		}
		
		// draw debug box for x-distance check on shrinking triangles
		if(UI.active()) {
			p.pushMatrix();
			p.translate(xProg, 300);
			p.fill(255);
			p.box(20);
			p.popMatrix();
		}
		
		// draw lerped head triangles
		p.shape(obj);
		
		// draw debug wireframe
//		p.noFill();
//		p.stroke(255, 100);
//		p.strokeWeight(1);
//		PShapeUtil.drawTriangles(p.g, objOrig, null, 1);

		// reset
		p.popMatrix();

		// reset context
		p.popMatrix();
	}
	
	public class Virus {
		
		protected PShape virus;
		protected PVector virusExtent;
		protected String id;
		protected int index;
		
		public Virus(int index) {
			this.index= index;
			this.id = ""+index;
			
			// load virus
			virus = p.loadShape(FileUtil.getPath("models/cv/Virus.obj"));
			// normalize shape (scaling, center)
			PShapeUtil.centerShape(virus);
			PShapeUtil.scaleShapeToHeight(virus, p.height * 0.5f);

			// check size
			virusExtent = new PVector(PShapeUtil.getWidth(virus), PShapeUtil.getHeight(virus), PShapeUtil.getDepth(virus));
			DebugView.setValue("shape.width", virusExtent.x);
			DebugView.setValue("shape.height", virusExtent.y);
			DebugView.setValue("shape.depth", virusExtent.z);
			
			// add UI
			UI.addSliderVector(uiKeyPos(), 0, -1000, 1000, 1, true);
			UI.addSliderVector(uiKeyRot(), 0, 0, P.TWO_PI, 0.01f, true);
			UI.addSlider(uiKeyScale(), 1, 0.01f, 10f, 0.01f, true);
		}
		
		protected String uiKeyPos() {
			return "VIRUS_"+id+"_POS";
		}
		
		protected String uiKeyRot() {
			return "VIRUS_"+id+"_ROT";
		}
		
		protected String uiKeyScale() {
			return "VIRUS_"+id+"_SCALE";
		}
		
		public void update() {
			float oscY = P.sin(index/2f + FrameLoop.progressRads() * 4f) * 40f * (UI.value(uiKeyScale()));
			p.pushMatrix();
			p.translate(UI.valueX(uiKeyPos()), UI.valueY(uiKeyPos()) + oscY, UI.valueZ(uiKeyPos()));
			p.pushMatrix();
			p.rotateX(UI.valueX(uiKeyRot()));
			p.rotateY(UI.valueY(uiKeyRot()));
			p.rotateZ(UI.valueZ(uiKeyRot()));
			p.scale(UI.value(uiKeyScale()));
			p.shape(virus);
			p.popMatrix();
			// text
//			p.translate(0, 0, 100);
//			p.fill(255);
//			p.textSize(70);
//			p.rotateZ(P.PI);
//			p.text(id, 0, 0);
			// end text
			p.popMatrix();
		}
	}
	
}

package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.ui.UI;

import processing.core.PShape;
import processing.core.PVector;

public class HeadSkullRender
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
	
	public float centerX;
	public float centerY;

	public float _frames = 250;
	public float _progress = 0;

	protected PShape obj;
	protected PVector[] objFaceCenters;
	protected PShape skullObj;
	protected PShape objOrig;
	protected float maxObjExtent;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 201 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(201 + _frames-1) );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	public void firstFrame() {

		
		centerX = p.width/2;
		centerY = p.height/2;

		// controls
		UI.addSlider(emissiveMaterial, 13f, 0, 255, 0.5f, false);
		UI.addSlider(ambientLight, 24f, 0, 255, 0.5f, false);
		UI.addSlider(specularMaterial, 150, 0, 255, 0.5f, false);
		UI.addSlider(specularLight, 200, 0, 255, 0.5f, false);
		UI.addSlider(shininessVal, 40f, 0, 100, 0.5f, false);
		UI.addSlider(lightsFalloffVal, 0.005f, 0, 0.01f, 0.0001f, false);
		UI.addSlider(lightsFalloffConstantVal, 0.017f, 0, 2f, 0.01f, false);
		UI.addSlider(spotLightConeAngle, 0.2f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(spotLightConcentration, 100f, 0, 1000f, 1f, false);

		// load model
		skullObj = p.loadShape( FileUtil.getPath("models/skull-realistic.obj"));
		objOrig = p.loadShape( FileUtil.getPath("models/Trump_lowPoly_updated.obj"));
		obj = p.loadShape( FileUtil.getPath("models/Trump_lowPoly_updated.obj"));
		
		PShapeUtil.scaleShapeToExtent(skullObj, p.height * 0.78f);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.8f);
		
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
		
		maxObjExtent = PShapeUtil.getMaxExtent(obj);

	}
	
	protected void addDirectionalLight() {
		// add global directional light
		float directionalOsc = _progress * P.TWO_PI;
		float pointX = centerX + centerX/2 * P.sin(P.PI + directionalOsc) ;
		float pointY = centerY + centerY/2 * P.cos(P.PI + directionalOsc) ;
		p.directionalLight(155, 135, 135, 2f * P.sin(directionalOsc), 2f * P.cos(directionalOsc), -1);
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
		float pointX = centerX + centerX/2 * P.sin(_progress * P.TWO_PI) ;
		p.pointLight(255, 102, 126, pointX, centerY, 0);
		// show debug light position
		if(UI.active()) {
			p.pushMatrix();
			p.fill(51, 102, 126);
			p.translate(pointX, p.height/2, 0);
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
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		p.sphereDetail(100);
		p.pushMatrix();
		
		_progress = (p.frameCount % _frames) / _frames;
		
		
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
		// position & draw shapes grid
		////////////////////////////////
		drawObj();

		// post
		float ampAmount = 0.4f;
		if(_progress > 0.2f && _progress < 0.8f) {
			float distortAmp = (_progress < 0.5f) ? P.map(_progress, 0.2f, 0.5f, 0, ampAmount) : P.map(_progress, 0.5f, 0.8f, ampAmount, 0);
			ColorDistortionFilter.instance(p).setAmplitude(distortAmp);
			ColorDistortionFilter.instance(p).setTime(p.frameCount * 0.02f);
			ColorDistortionFilter.instance(p).applyTo(p);
		}
		SaturationFilter.instance(p).setSaturation(1.4f);
		SaturationFilter.instance(p).applyTo(p);
		
		// close context
		p.popMatrix();
		p.noLights();
	}
		
	protected void drawObj() {
		p.pushMatrix();
		p.translate(p.width/2, p.height * 0.45f, -p.width);
		p.rotateZ(P.PI);
		p.rotateY(P.sin(P.TWO_PI * _progress) * 0.45f);
		
		p.pushMatrix();
		p.translate(0, p.height * -0.03f, 0);
		skullObj.disableStyle();
		p.fill(120 + 30f * P.sin(_progress * P.TWO_PI * 50f), 0, 0);
//		p.scale(0.73f + 0.1f * P.sin(P.TWO_PI * _progress));
		p.scale(0.71f);
		p.shape(skullObj);
		p.popMatrix();

		p.pushMatrix();
		p.translate(0, p.height * -0.17f, 0);
		p.fill(255);
//		p.scale(0.9f + 0.2f * P.sin(P.PI + P.TWO_PI * _progress));
		
		
		float xProg = P.map(_progress, 0, 1, -maxObjExtent * 1.3f, maxObjExtent * 1.3f);
		
		for (int i = 0; i < obj.getChildren().length; i++ ) {
			PShape child = obj.getChild(i);
			PShape childOrig = objOrig.getChild(i);
			for(int vIndex = 0; vIndex < child.getVertexCount(); vIndex++) {
				// PVector vertex = child.getVertex(vIndex);
				PVector vertexOrig = childOrig.getVertex(vIndex);
//				float amp = 1.2f + 0.2f * P.sin((float)i/100f + _progress * P.TWO_PI);
//				float amp = 1.2f + 0.2f * P.sin((float)i/100f + P.abs(vertexOrig.x) + P.abs(vertexOrig.y) + P.abs(vertexOrig.z) + _progress * P.TWO_PI);
				// float amp = 1;
//				child.setVertex(vIndex, vertexOrig.x * amp, vertexOrig.y * amp, vertexOrig.z * amp);
				
				// float indexOffset = (float)i / 100f;
//				float easedProgress = Penner.easeInOutCubic(0.5f + 0.5f * P.sin(indexOffset + _progress * P.TWO_PI), 0, 1, 1);
				
				float dist = MathUtil.getDistance(xProg, 0, objFaceCenters[i].x, 0);
				float easedProgress = 0;
				float distanceMax = maxObjExtent * 0.65f;
				if(P.abs(dist) < distanceMax) {
//					easedProgress = P.map(dist, distanceMax, 0, 0, 1);
					easedProgress = Penner.easeInOutQuart(P.map(dist, distanceMax, 0, 0, 1), 0, 1, 1);
				}
//				if(dist < 0 && dist > -_progress) {
//					easedProgress = Penner.easeInOutSine(P.map(dist, -_progress * 2f, 0, 0, 1), 0, 1, 1);
//				} else if(dist >= 0 && dist < _progress) {
//					easedProgress = Penner.easeInOutSine(P.map(dist, 0, _progress * 2f, 1, 0), 0, 1, 1);
//				}

				
				child.setVertex(
						vIndex, 
						P.map(easedProgress, 0, 1, vertexOrig.x, objFaceCenters[i].x), 
						P.map(easedProgress, 0, 1, vertexOrig.y, objFaceCenters[i].y), 
						P.map(easedProgress, 0, 1, vertexOrig.z, objFaceCenters[i].z)
						);
			}
		}
		
//		p.pushMatrix();
//		p.translate(xProg, 300);
//		p.fill(255);
//		p.box(20);
//		p.popMatrix();
		
//		obj.disableStyle();
		p.fill(255,185,40);
		p.shape(obj);
		p.popMatrix();

		p.popMatrix();
	}
	
}

package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import controlP5.ControlP5;
import processing.core.PShape;

public class LightsMaterialTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ControlP5 _cp5;	
	public boolean _showControls = true;
	
	public float emissiveMaterial = 5f;
	public float ambientLight = 30f;
	public float specularMaterial = 150f;
	public float specularLight = 200f;
	public float shininessVal = 50f;
	public float lightsFalloffVal = 0.002f;
	public float lightsFalloffConstantVal = 0.02f;
	public float spotLightConeAngle = P.PI / 2f;
	public float spotLightConcentration = 100f;
	
	public float centerX;
	public float centerY;

	public float _frames = 100;
	public float _progress = 0;

	protected PShape obj;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.RETINA, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1001 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(1001 + _frames-1) );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();
		
		p.sphereDetail(100);
		
		centerX = p.width/2;
		centerY = p.height/2;

		// controls
		_showControls = true;
		_cp5 = new ControlP5(this);
		int spacing = 30;
		int cntrlY = 0;
		int cntrlW = 300;
		_cp5.addSlider("emissiveMaterial").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,255f).setValue(emissiveMaterial);
		_cp5.addSlider("ambientLight").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,255f).setValue(ambientLight);
		_cp5.addSlider("specularMaterial").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,255f).setValue(150);
		_cp5.addSlider("specularLight").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,255f).setValue(200);
		_cp5.addSlider("shininessVal").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,100f).setValue(shininessVal);
		_cp5.addSlider("lightsFalloffVal").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,0.01f).setValue(lightsFalloffVal);
		_cp5.addSlider("lightsFalloffConstantVal").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,2f).setValue(lightsFalloffConstantVal);
		_cp5.addSlider("spotLightConeAngle").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,P.TWO_PI).setValue(spotLightConeAngle);
		_cp5.addSlider("spotLightConcentration").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(1,1000f).setValue(spotLightConcentration);

		// load model
		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));
		obj = p.loadShape( FileUtil.getFile("models/Trump_lowPoly.obj"));
		P.println(obj.getChildren().length);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.8f);
	}
	
	protected void addDirectionalLight() {
		// add global directional light
		float directionalOsc = _progress * P.TWO_PI;
		float pointX = centerX + centerX/2 * P.sin(P.PI + directionalOsc) ;
		float pointY = centerY + centerY/2 * P.cos(P.PI + directionalOsc) ;
		p.directionalLight(155, 135, 135, 2f * P.sin(directionalOsc), 2f * P.cos(directionalOsc), -1);
		// show debug light direction
		if(_showControls == true) {
			p.pushMatrix();
			p.fill(255, 0, 0);
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
		if(_showControls == true) {
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
		p.spotLight(200, 255, 200, spotLightX, spotLightY, 100, 0, 0, -1, spotLightConeAngle, spotLightConcentration);
		// show debug light position
		if(_showControls == true) {
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
		p.pushMatrix();
		
		_progress = (p.frameCount % _frames) / _frames;
		
		
		////////////////////////////////
		// global lights & materials setup
		////////////////////////////////
		// basic global lights:
		p.lightFalloff(lightsFalloffConstantVal, lightsFalloffVal, 0.0f);
		p.ambientLight(ambientLight, ambientLight, ambientLight);
		p.lightSpecular(specularLight, specularLight, specularLight);

		// materials:
		p.emissive(emissiveMaterial, emissiveMaterial, emissiveMaterial);
		p.specular(specularMaterial, specularMaterial, specularMaterial);
		p.shininess(shininessVal);	// affects the specular blur


		////////////////////////////////
		// additional lights
		////////////////////////////////
		addDirectionalLight();
		addPointLight();
		addSpotLight();


		////////////////////////////////
		// position & draw shapes grid
		////////////////////////////////
//		drawPrimitives();
		drawObj();


		// reset for ControlP5
		p.popMatrix();
		if(_showControls == false) p.translate(9999999, 999999);
		p.noLights();
	}
	
	protected void drawPrimitives() {
		p.pushMatrix();

		p.translate(p.width/2, p.height/2, -p.width);
		p.rotateX(p.mouseY * 0.01f);
		p.rotateY(p.mouseX * 0.01f);
		
		// build grid of primitives
		int rowSize = 4;
		int gridSize = 1000;
		int gridSizeHalf = gridSize/2;
		int spacing = gridSize / rowSize;
		int size = spacing/3;
		
		for (int x = 0; x < rowSize; x++) {
			for (int y = 0; y < rowSize; y++) {
				p.pushMatrix();
				p.translate(-gridSizeHalf + spacing/2 + x * spacing, -gridSizeHalf + spacing/2 + y * spacing, 0);
			
				p.fill(127);
				
				if(x % 2 == 0) {
					sphere(size);
				} else {
					box(size);
				}
				p.popMatrix();
			}
		}
		p.popMatrix();
	}
	
	protected void drawObj() {
		p.pushMatrix();
		p.translate(p.width/2, p.height * 0.45f, -p.width);
		p.rotateZ(P.PI);
		p.rotateY(P.sin(P.PI + P.TWO_PI * _progress) * 0.5f);
//		obj.disableStyle();
		p.fill(70);
		p.shape(obj);
		p.popMatrix();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			_showControls = !_showControls;
		}
	}

}

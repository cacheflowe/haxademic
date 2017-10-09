package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.PBlendModes;
import com.haxademic.core.draw.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.render.JoonsWrapper;

import controlP5.ControlP5;

public class VoxelSphere
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float increment = 0;
	public float detail = 0;
	public float xProgress = 0;
	public float yProgress = 0;
	float noiseScale = 0.003f;
	int octaves = 3;
	float noiseSpeed = 0.02f;
	float falloff = 0.5f;
	protected float frames = 60 * 6;
	protected float progress = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SUNFLOW, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
		p.appConfig.setProperty( AppSettings.RETINA, true );
	}

	public void setup() {
		super.setup();	
	}

	public void drawApp() {
		// rendering progress
		progress = (p.frameCount % frames) / frames;
		float progressRads = progress * P.TWO_PI;
		if(p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
			if(p.frameCount > 2 + p.appConfig.getInt(AppSettings.RENDERING_MOVIE_STOP_FRAME, 0)) p.exit();
		}
		
		// set background color & environment
		if(p.appConfig.getBoolean(AppSettings.SUNFLOW, false) == true) {
			joons.jr.background(JoonsWrapper.BACKGROUND_AO);
			joons.jr.background(1, 1, 1); //background(gray), or (r, g, b), like Processing.
			setUpRoom();
		} else {
			if(p.frameCount >= 1) p.background(255);
		}
		addLights();
		p.noStroke();
		p.noFill();

		// calculate sphere size
		p.pushMatrix();
		float hullSize = (float) p.width * 0.5f;
//		float startScale = 1f - progress * 0.9f;
		float numCubes = 4f + p.floor(progress * 25f);
//		float numCubes = (progress < 0.9f) ? 4f + P.floor(progress * 25f) : 4f + P.floor(P.map(progress, 0.9f, 1f, 1f, 0) * 25f);
		float cubeSize = hullSize / numCubes;
		float halfWidth = hullSize * 0.5f - 0.001f;// cubeSize * numCubes * 0.5f; // - 0.001f; // (1.5f + P.sin(progressRads) * 0.5f);
		float addRadius = (progress < 0.5f) ? 0 : P.map(progress, 0.5f, 1, 0, 1f);
		float sphereScale = 1f - addRadius * 0.25f;// + 1.0f * P.sin(P.PI + progressRads);

//		p.translate(p.width/2, p.height/2 + spacing/4f, -halfSize);
		p.translate(0, 0, -p.width * 1.2f);
		p.rotateY(progress * P.TWO_PI);
//		p.rotateX(progress * P.TWO_PI);
		
		// show center sphere
//		p.fill(255,0,0);
//		p.sphere(p.width * 0.01f);
		
		// set scale
		p.noStroke();
		p.noFill();
		p.scale(sphereScale);
		
		// For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
		for (float x = -halfWidth; x <= halfWidth; x += cubeSize) {
			for (float y = -halfWidth; y <= halfWidth; y += cubeSize) {
				for (float z = -halfWidth; z <= halfWidth; z += cubeSize) {
					// distance
					float distFromCenter = distance(0,0,0,x,y,z);
					if(distFromCenter < halfWidth + addRadius * halfWidth) { //  + halfWidth * 0.75f * P.sin(progressRads)
						// color from dist
						float r = P.sin(x*0.01f) * 120f + 60f;
						float g = P.sin(y*0.01f) * 170f + 70f;
						float b = P.sin(z*0.01f) * 120f + 100f;
//						r = g = b = 180;
						p.fill(r, g, b);
//						joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, r, g, b);
						// shape
						p.pushMatrix();
						p.translate(x, y, z);
						p.box(cubeSize);
						p.popMatrix();
					}
				}
			}
		}
		p.popMatrix();
	}
	
	protected void addLights() {
		float emissiveMaterial = 15f;
		float ambientLight = 30f;
		float specularMaterial = 150f;
		float specularLight = 200f;
		float shininessVal = 70f;
		float lightsFalloffVal = 0.002f;
		float lightsFalloffConstantVal = 0.02f;
		float spotLightConeAngle = P.PI / 2f;
		float spotLightConcentration = 100f;

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
		float centerX = p.width/2;
		float centerY = p.height/2;
		float lightZ = 500;

		// global directional light
		float directionalOsc = progress * P.TWO_PI;
		p.directionalLight(255, 135, 135, P.sin(directionalOsc), P.cos(directionalOsc), -1);
		// show debug light direction
//		p.pushMatrix();
//		p.fill(255, 0, 0);
//		p.translate(pointX, pointY, 0);
//		p.sphere(10);
//		p.popMatrix();
		
		
		// adds a non-directional light source
		float pointX = centerX + centerX/2 * P.sin(progress * P.TWO_PI);
		float pointY = centerY + centerY/2 * P.cos(P.PI + directionalOsc) ;
		p.pointLight(102, 255, 126, pointX, pointY, lightZ);
		// show debug light position
//		p.pushMatrix();
//		p.fill(51, 102, 126);
//		p.translate(pointX, p.height/2, 0);
//		p.sphere(10);
//		p.popMatrix();
		
		
		// adds a directional, focusable light source
		float spotLightX = centerX + p.width * 0.1f;
		float spotLightY = centerX + p.width * 0.1f;
		p.spotLight(200, 255, 200, spotLightX, spotLightY, lightZ, 0, 0, -1, spotLightConeAngle, spotLightConcentration);
		// show debug light position
//		p.pushMatrix();
//		p.fill(0, 255, 0);
//		p.translate(spotLightX, centerY + p.height * 0.1f, 100);
//		p.sphere(10);
//		p.popMatrix();


	}
	
	protected float distance( float x1, float y1, float z1, float x2, float y2, float z2 ) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
	    return P.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	protected float getNoise(float x, float y, float z ) {
		return p.noise(
				p.frameCount * noiseSpeed + x * noiseScale, 
				p.frameCount * noiseSpeed + y * noiseScale, 
				p.frameCount * noiseSpeed + z * noiseScale
		);
	}
	
	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 20;
		int samples = 16;
		int grey = 255;
		joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				grey, grey, grey, // left rgb
				grey, grey, grey, // right rgb
				grey, grey, grey, // back rgb
				grey, grey, grey, // top rgb
				grey, grey, grey  // bottom rgb
		); 
		popMatrix();		
	}



}

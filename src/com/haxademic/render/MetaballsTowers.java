package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.LiquidWarpFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class MetaballsTowers
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PGraphics texture;
	PShape mesh;
	float angle;
//	PShader textureShader;
	float _frames = 300;
	float displaceAmp = 320f; 
	float percentComplete;
	
	Metaballs metaballs;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 960 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF, false );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, 40 );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, 15 );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, 1 );
//		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, (int)_frames );

	}

	public void setup() {
		super.setup();	

		texture = createGraphics(p.width, p.height, P.P2D);
		texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(texture);
		
		metaballs = new Metaballs();
		
//		textureShader = P.p.loadShader( FileUtil.getFile("haxademic/shaders/textures/bw-clouds.glsl")); 
//		textureShader.set("time", 0 );

		mesh = Shapes.createSheet(600, texture);
	}

	public void drawApp() {
		background(0);
		p.pushMatrix();
		
		
		// rendering
		percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);

		angle = P.TWO_PI * percentComplete;

		// set center screen & rotate
//		translate(width/2, height*0.4f, -width/3);
		translate(width/2, height*0.6f, -width * 0.1f);
//		scale(1.4f);
//		rotateX(P.PI * p.mouseY * 0.01f); 
		rotateX(0.9f + 0.4f * P.sin(percentComplete * P.TWO_PI)); 
//		rotateZ(percentComplete * P.TWO_PI); 
//		rotateZ(-P.PI/4f); 

		// update metaballs texture
		metaballs.update();
		texture.beginDraw();
		texture.clear();
		texture.background(0);
		texture.image(metaballs.texture(), 0, 0);
		texture.endDraw();
		
		// some texture pre-filters
		VignetteFilter.instance(p).setDarkness(1.3f);
		VignetteFilter.instance(p).setSpread(0.2f);
		VignetteFilter.instance(p).applyTo(texture);

		LiquidWarpFilter.instance(p).setTime(0.5f * P.sin(percentComplete * P.TWO_PI));
		LiquidWarpFilter.instance(p).applyTo(texture);
		
//		PixelateFilter.instance(p).applyTo(texture);
//
//		BlurProcessingFilter.instance(p).setBlurSize(5);
//		BlurProcessingFilter.instance(p).setSigma(5f);
//		BlurProcessingFilter.instance(p).applyTo(texture);
		


		// set shader properties & set on processing context
//		p.displacementShader.set("displaceStrength", displaceAmp + displaceAmp * P.sin(percentComplete * P.TWO_PI));
		// deform mesh
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(texture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(displaceAmp);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(p);

		// unset shader deformation
		p.shape(mesh);
		p.resetShader();
		
		// post
		VignetteFilter.instance(p).setDarkness(1.3f);
		VignetteFilter.instance(p).setSpread(0.3f);
		VignetteFilter.instance(p).applyTo(p);

		FXAAFilter.instance(p).applyTo(p);
		InvertFilter.instance(p).applyTo(p);
		
		p.popMatrix();
		
		// debug metaballs texture
//		DrawUtil.setDrawFlat2d(p, true);
//		p.image(texture, 0, 0);
	}
	
	public class Metaballs {
		MetaBall balls[];
		PImage img;

		final int NUM_BALLS = 7;
		final float MIN_SIZE = p.width * 0.003f;
		final float MAX_SIZE = p.width * 0.020f;
		final int NUM_BANDS = 20;
		float BAND;
		int HALF_W;
		int HALF_H;


		public Metaballs() {
			setup();
		}
		
		public void setup() {	

			balls = new MetaBall[NUM_BALLS];
			img = createImage(width, height, ALPHA);
			img.loadPixels();

			HALF_W = width/2;
			HALF_H = height/2;

			BAND = 255f/NUM_BANDS;

			for (int i = 0; i < NUM_BALLS; i++) {
				balls[i] = new MetaBall(random(MIN_SIZE, MAX_SIZE), random(0, P.TWO_PI), i);
			}
		}
		
		public PImage texture() {
			return img;
		}

		public void update() {
			for (int i = 0; i < NUM_BALLS; i++) balls[i].update();

			// non-nested loop gives us about +5 ms per frame
			for (int i = 0; i < height * width; i++) {
				float col = 0.0f;

				for (int m = 0; m < NUM_BALLS; m++) {
					int y = P.floor(i / width); // faster than using int
					int x = i % width;

					float xx = (balls[m].pos.x + HALF_W) - x;
					float yy = (balls[m].pos.y + HALF_H) - y;

					col += balls[m].radius() / P.sqrt(xx * xx + yy * yy);
				}
				img.pixels[i] = color(colorLookup(255 * col), 255.0f);
			}

			img.updatePixels();
		}

		class MetaBall {
			private PVector pos;
			private float radius;
			private float index;
			private float baseRadians;
			private float travel;

			MetaBall(float r, float baseRadians, int index) {
				this.pos = new PVector(0, 0);
				this.radius = r;
				this.travel = radius * p.random(5f, 24f);
				this.index = index;
				this.baseRadians = baseRadians;
			}
			
			public float radius() {
				return radius * 1.5f + (radius * P.sin(radius + percentComplete * P.TWO_PI));
			}

			void update() {
				pos.set(
						P.sin(baseRadians + 0.2f * P.sin(radius + percentComplete * P.TWO_PI)) * travel * P.sin(radius + percentComplete * P.TWO_PI), 
						P.cos(baseRadians + 0.2f * P.cos(radius + percentComplete * P.TWO_PI)) * travel * P.cos(radius + percentComplete * P.TWO_PI)
						);
				
				pos.set(
						P.sin(baseRadians + 0.2f * P.sin(radius + percentComplete * P.TWO_PI)) * travel * 6f * (-0.5f + p.noise(radius + 0.2f * P.sin(radius + percentComplete * P.TWO_PI))), 
						P.cos(baseRadians + 0.2f * P.cos(radius + percentComplete * P.TWO_PI)) * travel * 6f * (-0.5f + p.noise(radius + 0.2f * P.cos(radius + percentComplete * P.TWO_PI)))
						);
				
			}
		}


		float colorLookup(float i) {
			return P.floor((i/255.0f) * NUM_BANDS) * BAND;
		}

	}

}


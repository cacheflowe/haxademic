package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageCyclerBuffer;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class GradientBlobs 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 600;
	protected int COLOR_1 = ColorUtil.colorFromHex("#7B73DB");
	protected int COLOR_2 = ColorUtil.colorFromHex("#9B6CBB");
	protected int COLOR_3 = ColorUtil.colorFromHex("#FC655F");
	protected int COLOR_4 = ColorUtil.colorFromHex("#FD8C6B");
	
	protected GradientBlob blob_1;
	protected GradientBlob blob_2;
	protected GradientBlob blob_3;
	
	protected PShape shapeIcos_1;
	protected PShape shapeIcos_2;
	protected PShape shapeIcos_3;
	protected PGraphics sphereTexture1;
	protected PGraphics sphereTexture2;
	protected PerlinTexture perlinTexture;
	protected PShapeSolid shapeIcos_solid;
	
	protected int noiseSeed = 853;
	protected boolean debugNoiseSeed = false;
	protected float circleMaskScale = 0.36f;
	protected float blobScale = 0.38f;

	protected ImageCyclerBuffer imageCycler;


	protected float progress;
	protected float easedPercent;
	protected float progressRadians;
	
	protected PGraphics overlayMask;
	
	public float directionLight = 180;
	public float emissiveMaterial = 1f; // 5f
	public float ambientLight = 50f;
	public float specularMaterial = 50f;
	public float specularLight = 100f;
	public float shininessVal = 5f; // 50f
	public float lightsFalloffVal = 0.12f;
	public float lightsFalloffConstantVal = 0.12f;
//	public float spotLightConeAngle = P.PI / 2f;
//	public float spotLightConcentration = 100f;

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void firstFrame() {

		p.noStroke();
		p.noiseSeed(noiseSeed);
		OpenGLUtil.setQuality(p.g, OpenGLUtil.GL_QUALITY_HIGH);
	}
	
	public void mouseMoved() {
		super.mouseMoved();
		if(debugNoiseSeed == false) return;
		int newSeed = (int)P.map(p.mouseX, 0, p.width, 0, 1000f);
		P.println("newSeed", newSeed);
		p.noiseSeed(newSeed);
	}
	
	protected void initObjects() {
		buildOverlay();
		buildBlobs();
		buildImageCycler();
		buildSphere();
	}
	
	protected void buildOverlay() {		
		overlayMask = p.createGraphics(p.width, p.height, P.P2D);
		overlayMask.smooth(8);
		overlayMask.beginDraw();
		overlayMask.clear();

		overlayMask.fill(0);
		overlayMask.noStroke();
		overlayMask.beginShape();
		// Exterior part of shape, clockwise winding
		overlayMask.vertex(0, 0);
		overlayMask.vertex(overlayMask.width, 0);
		overlayMask.vertex(overlayMask.width, overlayMask.height);
		overlayMask.vertex(0, p.height);
		// Interior part of shape, counter-clockwise winding
		overlayMask.beginContour();
		float segments = 360f;
		float segmentRads = P.TWO_PI / segments;
		float radius = overlayMask.width * circleMaskScale;
		for(float i = 0; i < segments; i++) {
			overlayMask.vertex(overlayMask.width * 0.5f + radius * P.cos(-i * segmentRads), overlayMask.height * 0.5f + radius * P.sin(-i * segmentRads));
		}
		overlayMask.endContour();
		overlayMask.endShape(CLOSE);	
		overlayMask.endDraw();
	}
	
	protected void buildBlobs() {
		blob_1 = new GradientBlob(COLOR_1, COLOR_2, 3f);
		blob_2 = new GradientBlob(COLOR_3, COLOR_2, 1.2f);
		blob_3 = new GradientBlob(COLOR_4, COLOR_1, 2f);
	}
	
	protected PShape newSphere(float size, PImage texture) {
		PShape shape = p.createShape(P.SPHERE, size);
		shape.setTexture(texture);
//		float extent = PShapeUtil.getSvgMaxExtent(shape);
//		PShapeUtil.addUVsToPShape(shape, extent);
		return shape;
	}
	
	protected PShapeSolid newSolidSphere(float size, PImage texture) {
		PShape group = createShape(GROUP);
		group.addChild(newSphere(size, texture));
		return new PShapeSolid(group);
	}
	
	protected PShape newIcosa(float size, PImage texture) {
		PShape shape = Icosahedron.createIcosahedron(p.g, 7, texture);
		PShapeUtil.scaleShapeToExtent(shapeIcos_2, p.height * 0.24f);
		return shape;
	}
	
	protected PShapeSolid newSolidIcos(float size, PImage texture) {
		PShape group = createShape(GROUP);
		PShape icos = Icosahedron.createIcosahedron(p.g, 5, texture);
		PShapeUtil.scaleShapeToExtent(icos, size);
		group.addChild(icos);
		return new PShapeSolid(group);
	}
	
	protected void buildSphere() {
		perlinTexture = new PerlinTexture(p, 200, 200);


		shapeIcos_solid = newSolidIcos(p.width * blobScale, imageCycler.image());
		shapeIcos_1 = newSphere(p.width * 0.3f, sphereTexture1);
		shapeIcos_2 = newSphere(p.width * 0.18f, sphereTexture1);
		shapeIcos_3 = newSphere(p.width * 0.16f, blob_3.image());
	}
	
	protected void buildImageCycler() {
		sphereTexture1 = p.createGraphics(p.width, p.height, P.P3D);
		sphereTexture1.smooth(8);
		sphereTexture2 = p.createGraphics(p.width, p.height, P.P3D);
		sphereTexture2.smooth(8);
		
		updateSphereTexture();
		updateSphereTexture2();


		PImage[] images = new PImage[] {
				sphereTexture1,
				sphereTexture2
		};
		imageCycler = new ImageCyclerBuffer(p.width, p.height, images, _frames/2, 0.1f);
	}

	public void drawApp() {
		if(p.frameCount == 1) initObjects();
		p.background(0);
		PG.setDrawCenter(p);
		
		// get progress
		progress = ((float)(p.frameCount%_frames)/_frames);
		easedPercent = Penner.easeInOutQuart(progress % 1, 0, 1, 1);
		progressRadians = progress * P.TWO_PI;
		
		// update blob texture
		blob_1.update();
		blob_2.update();
		blob_3.update();
		
		// update textures
		imageCycler.update();
		// p.image(imageCycler.image(), 20, 20);

		// draw different styles
//		drawBlobs2d();
		drawTextureOnSphere();
		
	}
	
	protected void updateSphereTexture() {
		sphereTexture1.beginDraw();
		sphereTexture1.noStroke();
		sphereTexture1.translate(sphereTexture1.width * 0.25f, sphereTexture1.height * 0.5f);
		sphereTexture1.rotate(progressRadians);
		// Gradients.linear(sphereTexture1, sphereTexture1.width * 0.5f, p.height, COLOR_1, COLOR_3);
		Gradients.quad(sphereTexture1, sphereTexture1.width * 0.5f, sphereTexture1.height * 0.999f, COLOR_1, COLOR_3, COLOR_2, COLOR_4);
		sphereTexture1.endDraw();
	}
	
	protected void updateSphereTexture2() {
		sphereTexture2.beginDraw();
		sphereTexture2.noStroke();
		sphereTexture2.translate(sphereTexture2.width * 0.25f, sphereTexture2.height * 0.5f);
		sphereTexture2.rotate(progressRadians);
		// Gradients.linear(sphereTexture2, sphereTexture2.width * 0.5f, p.height, COLOR_2, COLOR_4);
		Gradients.quad(sphereTexture2, sphereTexture2.width * 0.5f, sphereTexture2.height * 0.999f, COLOR_2, COLOR_3, COLOR_4, COLOR_1);
		sphereTexture2.endDraw();
	}
	
	protected void drawTextureOnSphere() {
		p.pushMatrix();
		p.translate(p.width * 0.5f, p.height * 0.5f);
		PG.setDrawCorner(p);

		updateSphereTexture();
		updateSphereTexture2();
		
		// set shader properties & set on processing context
		// perlinTexture.update(0.01f, 0.1f, P.sin(progressRadians), P.cos(progressRadians));

		shapeIcos_solid.shape().setTexture(imageCycler.image());
//		shapeIcos_solid.shape().setTexture(sphereTexture1);
		
		// lights
//		p.ambient(127);
		p.lightSpecular(230, 230, 230); 
//		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(directionLight, directionLight, directionLight, 0.0f, 0.0f, -1); 
//		p.specular(p.color(200)); 
//		p.shininess(15.0f); 
		
		////////////////////////////////
		// global lights & materials setup
		////////////////////////////////
		// basic global lights:
		p.lightFalloff(lightsFalloffConstantVal, lightsFalloffVal, 0.0f);
		p.ambientLight(ambientLight, ambientLight, ambientLight);
//		p.lightSpecular(specularLight, specularLight, specularLight);

		// materials:
		p.emissive(emissiveMaterial, emissiveMaterial, emissiveMaterial);
		p.specular(specularMaterial, specularMaterial, specularMaterial);
		p.shininess(shininessVal);	// affects the specular blur

		
		// draw solid sphere
//		shapeIcos_solid.setVertexColorWithAudio(p.color(255,0,0));
//		shapeIcos_solid.updateWithTrig(false, progress * 1f, 0.08f, 3.f);
		shapeIcos_solid.updateWithNoise(progress * P.TWO_PI, 1f, 0.3f, 2, 0.15f);
		p.pushMatrix();
		// p.rotateX(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
		p.shape(shapeIcos_solid.shape());
		p.popMatrix();
		p.popMatrix();
		
		// flat overlay
		PG.setDrawFlat2d(p, true);
		p.image(overlayMask, 0, 0);
		// p.image(imageCycler.image(), 0, 0);
		PG.setDrawFlat2d(p, false);

	}
	
	protected void drawBlobs2d() {
		p.translate(p.width * 0.5f, p.height * 0.5f);
		PG.setDrawCenter(p);

		// draw blobs
//		p.blendMode(PBlendModes.SCREEN);
		
		p.pushMatrix();
		p.image(blob_3.image(), 0, 0);
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(p.width * -0.1f, p.height * -0.1f);
		p.rotate(2f);
		p.image(blob_1.image(), 0, 0);
		p.popMatrix();
		
		p.pushMatrix();
		p.translate(p.width * 0.2f, p.height * 0.2f);
		p.rotate(1f);
		p.image(blob_2.image(), 0, 0);
		p.popMatrix();
		
		// overlay mask
		p.image(overlayMask, 0, 0);
	}
	
	protected void buildOverlayOld() {
		overlayMask = p.createGraphics(p.width, p.height, P.P3D);
		overlayMask.smooth(8);
		
		float maskScale = 0.65f;
		overlayMask.beginDraw();
		overlayMask.clear();
		overlayMask.background(0);
		overlayMask.noStroke();
		overlayMask.fill(255);
		overlayMask.ellipse(overlayMask.width * 0.5f, overlayMask.height * 0.5f, overlayMask.width * maskScale, overlayMask.height * maskScale);
		overlayMask.endDraw();
		
		// turn white to transparent
		PShader brightnessToAlpha = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/brightness-to-alpha.glsl"));
		overlayMask.filter(brightnessToAlpha);
	}
	
	public class GradientBlob {
		
		protected PGraphics shape;
		protected PGraphics gradient;
		protected PGraphics mask;
		protected int color1;
		protected int color2;
		protected float scale;
		
		public GradientBlob(int color1, int color2, float scale) {
			this.color1 = color1;
			this.color2 = color2;
			this.scale = scale;
			shape = p.createGraphics(P.round(p.width * scale), P.round(p.height * scale), P.P3D);
			shape.smooth(8);
			gradient = p.createGraphics(P.round(p.width * scale), P.round(p.height * scale), P.P3D);
			gradient.smooth(8);
		}
		
		public PImage image() {
			return gradient;
		}
				
		public void update() {
			drawGradient();
			drawBlob();
			gradient.mask(shape);
		}
		
		public void drawBlob() {
			shape.beginDraw();
			shape.clear();
			shape.background(0);
			shape.noStroke();
			shape.fill(255);
			shape.translate(shape.width * 0.5f, shape.height * 0.5f);
			shape.beginShape();
			
			float numVertices = 6;
			float outerRadius = shape.width * 0.3f;
			float innerRadius = shape.width * 0.2f;
			float radiusOsc = shape.width * 0.08f;
			float segmentRads = P.TWO_PI / numVertices;
			for(float i=0; i <= numVertices + 2; i++) {
				float curRads = segmentRads * i;
				float curRadius = (i % 2 == 0) ? outerRadius : innerRadius;
				curRadius += radiusOsc * P.sin(curRads * 1f + progressRadians);
				float vertX = P.cos(curRads) * curRadius;
				float vertY = P.sin(curRads) * curRadius;
				shape.curveVertex(vertX, vertY);
			}
			
			shape.endShape();
			shape.endDraw();
		}
		
		public void drawGradient() {
			float gradientScale = 0.99f;
			gradient.beginDraw();
			gradient.noStroke();
			gradient.translate(gradient.width * 0.5f, gradient.height * 0.5f);
			gradient.rotate(progressRadians);
			Gradients.linear(gradient, p.width * gradientScale, p.width * gradientScale, color1, color2);
			gradient.endDraw();
//			Gradients.linear(p, p.width/2, p.width/2, COLOR_1, COLOR_2);
//			Gradients.radial(p, p.width/2, p.width/2, COLOR_1, COLOR_2, 36);
		}
	}
}

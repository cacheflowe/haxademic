package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.OrientationUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Demo_BumpMap_Box
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// Ported into haxademic from: 
	// https://github.com/codeanticode/pshader-experiments
	
	protected PGraphics texture;
	protected PGraphics bumpMap;
	protected PGraphics specMap;
	protected PGraphics alphaMap;
	protected PGraphics aoMap;
	protected PShape plane;
	protected PShader bumpMapShader;
	protected PShape shape;

	protected void config() {
		Config.setAppSize(1080, 1920);
		Config.setProperty(AppSettings.LOOP_FRAMES, 900);
		Config.setProperty(AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE);
	}
	
	protected void firstFrame() {
		// test anisotropy levels?
		// the walls are super moire artifacty
		// Helpful info: https://forum.unity.com/threads/interference-moire-pattern-with-bumped-shaders.508496/
		OpenGLUtil.setTextureQualityLow(p.g);
//		PGraphicsOpenGL.maxAnisoAmount = 0;// GL.GL_TEXTURE_MAX_ANISOTROPY_EXT;
//		OpenGLUtil.listAvailableProfiles();

		
		// load textures
		texture = ImageUtil.imageToGraphics(P.getImage("haxademic/images/bumpmap/jersey_stitch_Base_Color_white.jpg"));
		bumpMap = ImageUtil.imageToGraphics(P.getImage("haxademic/images/bumpmap/jersey_stitch_Height.jpg"));
		specMap = ImageUtil.imageToGraphics(P.getImage("haxademic/images/bumpmap/jersey_stitch_Ambient_Occlusion.jpg"));
		alphaMap = ImageUtil.imageToGraphics(P.getImage("haxademic/images/bumpmap/jersey_stitch_Opacity.jpg"));
		aoMap = ImageUtil.imageToGraphics(P.getImage("haxademic/images/bumpmap/jersey_stitch_Ambient_Occlusion.jpg"));
		
		BrightnessFilter.instance().setBrightness(1.5f);
		BrightnessFilter.instance().applyTo(texture);

		// show in debug
		DebugView.setTexture("bumpMap", bumpMap);
		DebugView.setTexture("specMap", specMap);

		// load shader
		bumpMapShader = loadShader(
			P.path("haxademic/shaders/vertex/bump-mapping-frag.glsl"), 
			P.path("haxademic/shaders/vertex/bump-mapping-vert.glsl")
		);
		bumpMapShader.set("texMap", texture);
		bumpMapShader.set("bumpMap", bumpMap);
		bumpMapShader.set("specularMap", specMap);
		bumpMapShader.set("bumpScale", 0.01f);
		bumpMapShader.set("alphaMap", alphaMap);
		bumpMapShader.set("useAlphaMap", true);
		bumpMapShader.set("aoMap", aoMap);
		bumpMapShader.set("useAoMap", true);
		
		// create geometry
		float rectW = p.width * 2f;
		float rectH = p.height * 2f;
		PShape planeBack = createPlane(rectW, rectH);
		PShape planeLeft = createPlane(rectW, rectH);
		planeLeft.rotateY(-P.HALF_PI);
		planeLeft.translate(-rectW/2, 0, rectW/2);
		PShape planeRight = createPlane(rectW, rectH);
		planeRight.rotateY(P.HALF_PI);
		planeRight.translate(rectW/2, 0, rectW/2);
		
		PShape planeTop = createPlane(rectW, rectW);
		planeTop.rotateX(P.HALF_PI);
		planeTop.translate(0, -rectH/2, rectW/2);
		PShape planeBot = createPlane(rectW, rectW);
		planeBot.rotateX(-P.HALF_PI);
		planeBot.translate(0, rectH/2, rectW/2);
		plane = PShapeUtil.addShapesToGroup(planeBack, planeLeft, planeRight, planeTop, planeBot);
		
		loadOBJ();
	}
	
	protected void loadOBJ() {
		// build shape and assign texture
		shape = PShapeUtil.loadObjCustomVertexColors(p.g, FileUtil.getPath("models/VICEVERSA_00.obj"));
		
		// normalize shape (scaling centers)
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.05f);
		PShapeUtil.setOnGround(shape);
		
		// check size
		PVector modelSize = new PVector(PShapeUtil.getWidth(shape), PShapeUtil.getHeight(shape), PShapeUtil.getDepth(shape));
		DebugView.setValue("shape.width", modelSize.x);
		DebugView.setValue("shape.height", modelSize.y);
		DebugView.setValue("shape.depth", modelSize.z);
		DebugView.setValue("shape.vertexCount", PShapeUtil.vertexCount(shape));
	}
	
	protected PShape createPlane(float rectW, float rectH) {
		PShape shape = createShape(P.RECT, -rectW/2, -rectH/2, rectW, rectH);
		shape.setStroke(false);
		shape.setSpecular(color(255));
		shape.setEmissive(255);
		shape.setAmbient(255);
		shape.setShininess(20);
		return shape;
		
//		shape = shape.getTessellation();	// required to move vertices around via PShapeUtil
//		PShapeUtil.meshRotateOnAxis(planeRight, P.HALF_PI, P.Y);
//		PShapeUtil.offsetShapeVertices(planeRight, rectSize/2, 0, 0);
	}
	
	protected void drawApp() {
//		texture.beginDraw();
//		texture.background(255);
//		texture.endDraw();
//		bumpMap.beginDraw();
//		bumpMap.background(255);
//		bumpMap.endDraw();
//		specMap.beginDraw();
//		specMap.background(255);
//		specMap.endDraw();
		
		// set context
		background(0, 0, FrameLoop.osc(0.04f, 0, 50));
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g, 0.1f, 0.025f);
		
		// light source emitting from the right of the camera
		p.pointLight(255, 255, 255, 
				P.map(Mouse.xNorm, 0, 1, -1500, 1500), 
				P.map(Mouse.yNorm, 0, 1, -3000, 3000), 
				-900);
//		p.pointLight(255, 255, 255, P.map(Mouse.xNorm, 0, 1, -1000, 1000), p.height * 1.5f, 500);
		
		// update shader
		bumpMapShader.set("bumpScale", Mouse.yNorm * 0.01f);
		bumpMapShader.set("bumpScale", 0.001f);
		bumpMapShader.set("aoAmp", Mouse.yNorm);
		bumpMapShader.set("aoAmp", 0.2f);
		bumpMapShader.set("useAlphaMap", true);
		bumpMapShader.set("useAoMap", true);
		
		// draw from center
		p.push();
//		p.directionalLight(255,  255, 255, P.map(Mouse.xNorm, 0, 1, -200, 200), -200, 200);
		p.shader(bumpMapShader);
		p.translate(0, 0, p.width * -2f);
//		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
//		p.rotateY(FrameLoop.progressRads());
		p.shape(plane);
		p.resetShader();
		
		
		// numbers formation
		p.translate(0, 0, p.width * 2f);
		// temp box
//		PG.setBasicLights(p.g);
		p.pointLight(255, 255, 255, 
				P.map(Mouse.xNorm, 0, 1, -200, 200), 
				P.map(Mouse.yNorm, 0, 1, -3000, 3000), 
				1000);
//		p.box(p.width * 1.9f, p.height * 1.9f, p.width * 2.9f);
		drawShapes();
		p.pop();
	}
	
	protected PVector util = new PVector();
	protected PVector center = new PVector();
	protected void drawShapes() {
		// draw shapes
		float startRads = FrameLoop.count(0.01f);
		float numShapes = 100;
		float rads = 1f / numShapes * P.TWO_PI;
		float shapeScale = p.width * 0.2f;
		float scaleOscAmp = 0.3f;
		float scaleOscFreq = 5f;
		for (int i = 0; i < numShapes; i++) {
			float progress = (float) i / numShapes;
			float progressOsc = P.sin(progress * scaleOscFreq * P.TWO_PI);
			float radsX = P.cos(startRads + rads * 4 * i);
			float radsY = P.sin(startRads + rads * 3 * i);
			float radsZ = P.cos(startRads + rads * 5 * i);
			float x = radsX * (shapeScale * (1f + scaleOscAmp * P.sin(progressOsc)));
			float y = radsY * (shapeScale * 2f * (1f + scaleOscAmp * P.sin(progressOsc)));
			float z = radsZ * (shapeScale * (1f + scaleOscAmp * P.sin(progressOsc)));
			
			p.push();
//			p.fill(ColorsHax.colorFromGroupAt(11, i));
			p.translate(x, y, z);
			util.set(x, y, z);
			OrientationUtil.setRotationTowards2(p.g, util, center);
//			p.rotateZ(radsZ);
//			p.rotate(-radsY);
//			p.rotateY(-radsY);
//			p.sphere(boxScale);
			p.shape(shape);
			p.pop();
		}
	}

}

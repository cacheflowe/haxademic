package com.haxademic.sketch.volume;



import java.util.ArrayList;
import java.util.List;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;
import wblut.external.ProGAL.AlphaComplex;
import wblut.external.ProGAL.CTriangle;
import wblut.external.ProGAL.Point;

public class ConvexHullObj 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected List<Point> points;
	protected float[] intervals; //init Point3d array
	
	protected PShape obj;
//	protected PShapeSolid objSolid;
	protected float modelExtent;
	protected float modelExtentHalf;
	protected PImage img;
	protected float _frames = 200;
	protected float percentCompete;
	protected float radsComplete;
	protected PShader bgShader;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();
		smooth();
		
		buildModel();
		
		bgShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/basic-diagonal-stripes.glsl"));
		
		points= new ArrayList<Point>();
	}  
	
	protected void buildModel() {
		// load texture
		img = p.loadImage(FileUtil.getFile("images/justin-home.png"));
		
		// build obj PShape and scale to window
		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
//		obj = p.loadShape( FileUtil.getFile("models/lego-man.obj"));	
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.4f);
		
		// add UV coordinates to OBJ
		modelExtent = PShapeUtil.getMaxExtent(obj);
		modelExtentHalf = modelExtent / 2f;
		PShapeUtil.addTextureUVToShape(obj, img, modelExtent);
		// obj.setTexture(img);
		
		// build solid, deformable PShape object
//		objSolid = new PShapeSolid(obj);
		P.println("modelExtent", modelExtent);
	}
	
	protected void drawShape() {
		// setup lights
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(color(255)); 
		p.shininess(5.0f); 

		// rotate
		p.translate(p.width/2f, p.height/2.1f, -300f);
//		p.rotateX(P.sin(-percentCompete * P.TWO_PI) * 0.1f);
		p.rotateZ(P.PI);
		p.rotateY(P.sin(-percentCompete * P.TWO_PI) * 0.2f);
//		p.rotateY(percentCompete * P.TWO_PI);

		
		// draw!
		obj.disableStyle();
		p.fill(90);
		p.noStroke();
//		p.shape(obj);
		
//		p.scale(0.85f);
		p.fill(0);
		float numStripes = 4f;
		float stripeHeight = modelExtent / numStripes;
		float startY = -modelExtent + stripeHeight * 2f * percentCompete;
		for (int i = 0; i < numStripes * 2f; i++) {
			if(i % 2 == 0) {
				drawSlice(stripeHeight/ 2f, startY + i * stripeHeight);
			}
		}
	}
	
	protected void drawSlice(float stripeHeight, float stripeY) {
//		p.fill(255);
//		p.stroke(255);
		points.clear();
		for (int j = 0; j < obj.getChildCount(); j++) {
			for (int i = 0; i < obj.getChild(j).getVertexCount() - 1; i++) {
				// get vertex
				PVector v = obj.getChild(j).getVertex(i);
				if(P.abs(v.y - stripeY) < stripeHeight) {	// check if it's in the stripe
					Point point = new Point(v.x, v.y, v.z);
					points.add(point);
					// debug draw
	//				p.pushMatrix();
	//				p.translate(v.x, v.y, v.z);
	//				p.point(0, 0, 0);
	//				p.popMatrix();
				}
			}
		}
		drawAlphaComplex();
	}

	public void drawApp() {
		percentCompete = ((float)(p.frameCount%_frames)/_frames);
		radsComplete = percentCompete * P.TWO_PI;

		background(255);
		bgShader.set("size", 16f + 8f * P.sin(radsComplete));
		p.filter(bgShader);
		drawShape();
		postProcess();
	}
	
	protected void postProcess() {
		WobbleFilter.instance(p).setTime( p.millis() * 0.00001f);
		WobbleFilter.instance(p).setSpeed( 0.0001f * P.sin(radsComplete));
		WobbleFilter.instance(p).setStrength( 0.003f + 0.003f * P.sin(radsComplete));
		WobbleFilter.instance(p).setSize( 50f * P.sin(radsComplete));
		WobbleFilter.instance(p).applyTo(p);
		
		VignetteAltFilter.instance(p).setDarkness(-1.75f);
		VignetteAltFilter.instance(p).setSpread(-1.25f);
		VignetteAltFilter.instance(p).applyTo(p);
	}
	
	protected void drawAlphaComplex() {
		// draw alpha complex
//		stroke(255);
		float scale = 114f;
		AlphaComplex ac = new AlphaComplex(points, 300);
		List<CTriangle> triangles = ac.getTriangles();
		for(CTriangle tri: triangles){		
			beginShape(TRIANGLE_STRIP);
			textureMode(NORMAL);
			texture(img);
			
			drawVertex(tri.getP1(), scale);
			drawVertex(tri.getP2(), scale);
			drawVertex(tri.getP3(), scale);

			endShape(CLOSE);
		}
	}
	
	protected void drawVertex(Point point, float scale) {
		// x, y, z, u, v
		vertex(
				scale * (float) point.x(), 
				scale * (float) point.y(), 
				scale * (float) point.z(),
				P.map((float) point.x(), -modelExtentHalf, modelExtentHalf, 0.28f, 0.72f),
				P.map((float) point.y(), -modelExtentHalf, modelExtentHalf, 0.65f, 0.25f)
		);
	}

}

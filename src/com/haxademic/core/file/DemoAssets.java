package com.haxademic.core.file;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.shapes.PShapeUtil;

import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.Movie;

public class DemoAssets {

	// IMAGES
	
	public static PImage smallTexture = null;
	public static PImage smallTexture() {
		if(smallTexture == null) smallTexture = P.p.loadImage(FileUtil.getFile("images/smiley-big.png"));
		return smallTexture;
	}
	
	public static PImage justin = null;
	public static PImage justin() {
		if(justin == null) justin = P.p.loadImage(FileUtil.getFile("images/textures/justin-spike-portrait.png"));
		return justin;
	}
	
	public static PImage grayscale = null;
	public static PImage grayscale() {
		if(grayscale == null) grayscale = P.p.loadImage(FileUtil.getFile("images/textures/grayscale/shader-1.jpg"));
		return grayscale;
	}
	
	public static PImage textureJupiter = null;
	public static PImage textureJupiter() {
		if(textureJupiter == null) textureJupiter = P.p.loadImage(FileUtil.getFile("images/textures/space/spherical/jupiter.jpg"));
		return textureJupiter;
	}
	
	public static PImage textureNebula = null;
	public static PImage textureNebula() {
		if(textureNebula == null) textureNebula = P.p.loadImage(FileUtil.getFile("images/textures/space/spherical/carina-nebula.jpg"));
		return textureNebula;
	}
	
	public static PImage squareTexture = null;
	public static PImage squareTexture() {
		if(squareTexture == null) squareTexture = P.p.loadImage(FileUtil.getFile("images/textures/space/sun.jpg"));
		return squareTexture;
	}
	
	// SVG
	
	public static PShape shapeX = null;
	public static PShape shapeX() {
		if(shapeX == null) shapeX = P.p.loadShape(FileUtil.getFile("svg/x.svg"));
		return shapeX;
	}
	
	public static PShape shapeFractal = null;
	public static PShape shapeFractal() {
		if(shapeFractal == null) shapeFractal = P.p.loadShape(FileUtil.getFile("svg/fractal-2013-09-15-20-27-38-01.svg"));
		return shapeFractal;
	}
	
	// MODELS
	
	public static PShape objSkeleton = null;
	public static PShape objSkeleton() {
		if(objSkeleton == null) {
			objSkeleton = P.p.loadShape(FileUtil.getFile("models/skeleton-lowpoly.obj"));
			PShapeUtil.meshRotateOnAxis(objSkeleton, P.PI, P.Z);
		}
		return objSkeleton;
	}
	
	public static PShape objSkullRealistic = null;
	public static PShape objSkullRealistic() {
		if(objSkullRealistic == null) {
			objSkullRealistic = P.p.loadShape(FileUtil.getFile("models/skull-realistic.obj"));
			PShapeUtil.meshRotateOnAxis(objSkullRealistic, P.PI, P.Z);
		}
		return objSkullRealistic;
	}
	
	public static PShape objHumanoid = null;
	public static PShape objHumanoid() {
		if(objHumanoid == null) {
			objHumanoid = P.p.loadShape(FileUtil.getFile("models/man-lowpoly.obj"));
			PShapeUtil.meshRotateOnAxis(objHumanoid, P.PI, P.Z);
			PShapeUtil.meshRotateOnAxis(objHumanoid, P.HALF_PI, P.Y);
		}
		return objHumanoid;
	}
	
	// VIDEO
	
	public static Movie movieFractalCube = null;
	public static Movie movieFractalCube() {
		if(movieFractalCube == null) movieFractalCube = new Movie(P.p, FileUtil.getFile("video/fractal-cube.mp4"));
		return movieFractalCube;
	}

	// FONTS
	
	public static PFont fontBitlow = null;
	public static PFont fontBitlow(float fontSize) {
		if(fontBitlow == null) fontBitlow = P.p.createFont( FileUtil.getFile("fonts/bitlow.ttf"), fontSize );
		return fontBitlow;
	}
	
	public static PFont fontHelloDenver = null;
	public static PFont fontHelloDenver(float fontSize) {
		if(fontHelloDenver == null) fontHelloDenver = P.p.createFont( FileUtil.getFile("fonts/HelloDenverDisplay-Regular.ttf"), fontSize );
		return fontHelloDenver;
	}
	
	public static PFont fontOhmegaSans = null;
	public static PFont fontOhmegaSans(float fontSize) {
		if(fontOhmegaSans == null) fontOhmegaSans = P.p.createFont( FileUtil.getFile("fonts/OhmegaSans-Regular.ttf"), fontSize );
		return fontOhmegaSans;
	}
	
	public static PFont fontDSEG7 = null;
	public static PFont fontDSEG7(float fontSize) {
		if(fontDSEG7 == null) fontDSEG7 = P.p.createFont( FileUtil.getFile("fonts/DSEG7ClassicMini-Regular.ttf"), fontSize );
		return fontDSEG7;
	}

}

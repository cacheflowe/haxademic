package com.haxademic.core.media;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.Movie;

public class DemoAssets {

	// IMAGES
	
	public static PImage smallTexture = null;
	public static PImage smallTexture() {
		if(smallTexture == null) smallTexture = P.p.loadImage(FileUtil.getPath("haxademic/images/smiley.png"));
		return smallTexture;
	}
	
	public static PImage particle = null;
	public static PImage particle() {
		if(particle == null) particle = P.p.loadImage(FileUtil.getPath("haxademic/images/particle.png"));
		return particle;
	}
	
	public static PImage arrow = null;
	public static PImage arrow() {
		if(arrow == null) arrow = P.p.loadImage(FileUtil.getPath("haxademic/images/arrow-right.png"));
		return arrow;
	}
	
	public static PImage justin = null;
	public static PImage justin() {
		if(justin == null) justin = P.p.loadImage(FileUtil.getPath("haxademic/images/justin-spike-portrait.png"));
		return justin;
	}
	
	public static PImage textureJupiter = null;
	public static PImage textureJupiter() {
		if(textureJupiter == null) textureJupiter = P.p.loadImage(FileUtil.getPath("haxademic/images/spherical/jupiter.jpg"));
		return textureJupiter;
	}
	
	public static PImage textureNebula = null;
	public static PImage textureNebula() {
		if(textureNebula == null) textureNebula = P.p.loadImage(FileUtil.getPath("haxademic/images/space/carina-nebula.jpg"));
		return textureNebula;
	}
	
	public static PImage squareTexture = null;
	public static PImage squareTexture() {
		if(squareTexture == null) squareTexture = P.p.loadImage(FileUtil.getPath("haxademic/images/space/sun.jpg"));
		return squareTexture;
	}
	
	public static PImage textureCursor = null;
	public static PImage textureCursor() {
		if(textureCursor == null) textureCursor = P.p.loadImage(FileUtil.getPath("haxademic/images/cursor-finger-trans.png"));
		return textureCursor;
	}
	
	// SVG
	
	public static String shapeXPath = "haxademic/svg/x.svg";
	public static PShape shapeX = null;
	public static PShape shapeX() {
		if(shapeX == null) shapeX = P.p.loadShape(FileUtil.getPath(shapeXPath));
		return shapeX;
	}
	
	public static PShape shapeFractal = null;
	public static PShape shapeFractal() {
		if(shapeFractal == null) shapeFractal = P.p.loadShape(FileUtil.getPath("haxademic/svg/fractal-2013-09-15-20-27-38-01.svg"));
		return shapeFractal;
	}
	
	// MODELS
	
	public static PShape objSkeleton = null;
	public static PShape objSkeleton() {
		if(objSkeleton == null) {
			objSkeleton = P.p.loadShape(FileUtil.getPath("haxademic/models/skeleton-lowpoly.obj"));
			PShapeUtil.meshRotateOnAxis(objSkeleton, P.PI, P.Z);
		}
		return objSkeleton;
	}
	
	public static PShape objSkullRealistic = null;
	public static PShape objSkullRealistic() {
		if(objSkullRealistic == null) {
			objSkullRealistic = P.p.loadShape(FileUtil.getPath("haxademic/models/skull-realistic.obj"));
			PShapeUtil.meshRotateOnAxis(objSkullRealistic, P.PI, P.Z);
		}
		return objSkullRealistic;
	}
	
	public static PShape objHumanoid = null;
	public static PShape objHumanoid() {
		if(objHumanoid == null) {
			objHumanoid = P.p.loadShape(FileUtil.getPath("haxademic/models/man-lowpoly.obj"));
			PShapeUtil.meshRotateOnAxis(objHumanoid, P.PI, P.Z);
			PShapeUtil.meshRotateOnAxis(objHumanoid, P.HALF_PI, P.Y);
		}
		return objHumanoid;
	}
	
	// VIDEO
	
	public static String movieFractalCubePath = "haxademic/video/fractal-cube.mp4";
	public static Movie movieFractalCube = null;
	public static Movie movieFractalCube() {
		if(movieFractalCube == null) movieFractalCube = new Movie(P.p, FileUtil.getPath(movieFractalCubePath));
		return movieFractalCube;
	}
	
	public static Movie movieKinectSilhouette = null;
	public static Movie movieKinectSilhouette() {
		if(movieKinectSilhouette == null) movieKinectSilhouette = new Movie(P.p, FileUtil.getPath("haxademic/video/kinect-silhouette.mp4"));
		return movieKinectSilhouette;
	}

	// AUDIO
	
	public static String audioBiggerLoop = "haxademic/audio/cacheflowe_bigger_loop.wav";

	// FONTS
	
	public static String fontOpenSansPath = "haxademic/fonts/OpenSans-Regular.ttf";
	public static PFont fontOpenSans = null;
	public static PFont fontOpenSans(float fontSize) {
		if(fontOpenSans == null) fontOpenSans = P.p.createFont( FileUtil.getPath(fontOpenSansPath), fontSize );
		return fontOpenSans;
	}
	
	public static String fontInterPath = "haxademic/fonts/Inter-Regular.ttf";
	public static PFont fontInter = null;
	public static PFont fontInter(float fontSize) {
		if(fontInter == null) fontInter = P.p.createFont( FileUtil.getPath(fontInterPath), fontSize );
		return fontInter;
	}
	public static PFont fontDebug = null;
	public static PFont fontDebug() {
		if(fontDebug == null) fontDebug = P.p.createFont( FileUtil.getPath(fontInterPath), 11 );
		return fontDebug;
	}
	
	public static String fontRalewayPath = "haxademic/fonts/Raleway-Regular.ttf";
	public static PFont fontRaleway = null;
	public static PFont fontRaleway(float fontSize) {
		if(fontRaleway == null) fontRaleway = P.p.createFont( FileUtil.getPath(fontRalewayPath), fontSize );
		return fontRaleway;
	}
	
	public static String fontBitlowPath = "haxademic/fonts/bitlow.ttf";
	public static PFont fontBitlow = null;
	public static PFont fontBitlow(float fontSize) {
		if(fontBitlow == null) fontBitlow = P.p.createFont( FileUtil.getPath(fontBitlowPath), fontSize );
		return fontBitlow;
	}
	
	public static String fontHelloDenverPath = "haxademic/fonts/HelloDenverDisplay-Regular.ttf";
	public static PFont fontHelloDenver = null;
	public static PFont fontHelloDenver(float fontSize) {
		if(fontHelloDenver == null) fontHelloDenver = P.p.createFont( FileUtil.getPath(fontHelloDenverPath), fontSize );
		return fontHelloDenver;
	}
	
	public static String fontOhmegaSansPath = "haxademic/fonts/OhmegaSans-Regular.ttf";
	public static PFont fontOhmegaSans = null;
	public static PFont fontOhmegaSans(float fontSize) {
		if(fontOhmegaSans == null) fontOhmegaSans = P.p.createFont( FileUtil.getPath(fontOhmegaSansPath), fontSize );
		return fontOhmegaSans;
	}
	
	public static String fontDSEG7Path = "haxademic/fonts/DSEG7ClassicMini-Regular.ttf";
	public static PFont fontDSEG7 = null;
	public static PFont fontDSEG7(float fontSize) {
		if(fontDSEG7 == null) fontDSEG7 = P.p.createFont( FileUtil.getPath(fontDSEG7Path), fontSize );
		return fontDSEG7;
	}

}

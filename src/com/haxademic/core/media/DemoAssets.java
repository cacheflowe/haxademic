package com.haxademic.core.media;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.Movie;

public class DemoAssets {

	// IMAGES
	
	public static PImage smallTexture() {
		return ImageCacher.get("haxademic/images/smiley.png");
	}
	public static PImage particle() {
		return ImageCacher.get("haxademic/images/particle.png");
	}
	public static PImage particleLight() {
	    return ImageCacher.get("haxademic/images/particle-light.png");
	}
	public static PImage particleMedium() {
	    return ImageCacher.get("haxademic/images/particle-medium.png");
	}
	public static PImage particleHeavy() {
	    return ImageCacher.get("haxademic/images/particle-heavy.png");
	}
	public static PImage arrow() {
		return ImageCacher.get("haxademic/images/arrow-right.png");
	}
	public static PImage justin() {
		return ImageCacher.get("haxademic/images/justin-spike-portrait.png");
	}
	public static PImage textureJupiter() {
		return ImageCacher.get("haxademic/images/spherical/jupiter.jpg");
	}
	public static PImage textureNebula() {
		return ImageCacher.get("haxademic/images/space/carina-nebula.jpg");
	}
	public static PImage squareTexture() {
		return ImageCacher.get("haxademic/images/space/sun.jpg");
	}
	public static PImage textureCursor() {
		return ImageCacher.get("haxademic/images/cursor-finger-trans.png");
	}
	public static PImage noSignal() {
		return ImageCacher.get("haxademic/images/no-signal.png");
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
	
	public static String objSkullRealisticPath = "haxademic/models/skull-realistic.obj";
	public static PShape objSkullRealistic = null;
	public static PShape objSkullRealistic() {
		if(objSkullRealistic == null) {
			objSkullRealistic = P.p.loadShape(FileUtil.getPath(objSkullRealisticPath));
			PShapeUtil.meshRotateOnAxis(objSkullRealistic, P.PI, P.Z);
		}
		return objSkullRealistic;
	}
	
	public static PShape objHumanoid = null;
	public static PShape objHumanoid() {
		if(objHumanoid == null) {
//			objHumanoid = P.p.loadShape(FileUtil.getPath("haxademic/models/man-lowpoly.obj"));
			objHumanoid = P.p.loadShape(FileUtil.getPath("haxademic/models/humanoid-lowpoly.obj"));
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

	public static Movie movieTestPattern = null;
	public static Movie movieTestPattern() {
		if(movieTestPattern == null) movieTestPattern = new Movie(P.p, FileUtil.getPath("haxademic/video/test-pattern.mp4"));
		return movieTestPattern;
	}
	
	// AUDIO
	
	public static String audioBiggerLoop = "haxademic/audio/cacheflowe_bigger_loop.wav";
	public static String audioBrimBeat = "haxademic/audio/brim-beat-4.wav";

	// FONTS

	// cached font setters

	public static void setDemoFont(PGraphics pg) {
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 18);
		setFont(pg, font, ColorsHax.WHITE);
	}

	public static void setFont(PGraphics pg, PFont font, int color) {
		// PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 18);
		FontCacher.setFontOnContext(pg, font, color, 1.2f, PTextAlign.LEFT, PTextAlign.TOP);
	}

	// individual fonts to cache
	
	public static String fontOpenSansPath = "haxademic/fonts/OpenSans-Regular.ttf";
	public static PFont fontOpenSans = null;
	public static PFont fontOpenSans(float fontSize) {
		if(fontOpenSans == null) fontOpenSans = P.p.createFont( FileUtil.getPath(fontOpenSansPath), fontSize );
		return fontOpenSans;
	}
	
	public static String fontInterPath = "haxademic/fonts/Inter-Regular.ttf";
	public static PFont fontInter = null;
	public static PFont fontInter(float fontSize) {
		if(fontInter == null) {
			fontInter = (FileUtil.fileExists(FileUtil.getPath(fontInterPath))) ?
					P.p.createFont( FileUtil.getPath(fontInterPath), fontSize ) : 
					P.p.createFont("Arial", fontSize );
		}
		return fontInter;
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
	
	public static String fontMonospacePath = "haxademic/fonts/Inconsolata.otf";
	public static PFont fontMonospace = null;
	public static PFont fontMonospace(float fontSize) {
		if(fontMonospace == null) fontMonospace = P.p.createFont( FileUtil.getPath(fontMonospacePath), fontSize );
		return fontMonospace;
	}
	
	public static String fontDSEG7Path = "haxademic/fonts/DSEG7ClassicMini-Regular.ttf";
	public static PFont fontDSEG7 = null;
	public static PFont fontDSEG7(float fontSize) {
		if(fontDSEG7 == null) fontDSEG7 = P.p.createFont( FileUtil.getPath(fontDSEG7Path), fontSize );
		return fontDSEG7;
	}

}

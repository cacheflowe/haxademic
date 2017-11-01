package com.haxademic.core.file;

import com.haxademic.core.app.P;

import processing.core.PImage;
import processing.core.PShape;

public class DemoAssets {

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
	
	public static PImage sphericalTexture = null;
	public static PImage sphericalTexture() {
		if(sphericalTexture == null) sphericalTexture = P.p.loadImage(FileUtil.getFile("images/textures/space/spherical/jupiter.jpg"));
		return sphericalTexture;
	}
	
	public static PImage squareTexture = null;
	public static PImage squareTexture() {
		if(squareTexture == null) squareTexture = P.p.loadImage(FileUtil.getFile("images/textures/space/sun.jpg"));
		return squareTexture;
	}
	
	public static PShape objSkeleton = null;
	public static PShape objSkeleton() {
		//	obj = p.loadShape( FileUtil.getFile("models/poly-hole-square.obj"));	
		//	obj = p.loadShape( FileUtil.getFile("models/man-lowpoly.obj"));	
		//	obj = p.loadShape( FileUtil.getFile("models/lego-man.obj"));	
		//	obj = p.loadShape( FileUtil.getFile("models/bomb.obj"));	
		//	obj = p.loadShape( FileUtil.getFile("models/poly-hole-tri.obj"));	
		if(objSkeleton == null) objSkeleton = P.p.loadShape(FileUtil.getFile("models/skeleton-lowpoly.obj"));
		return objSkeleton;
	}
	
	public static PShape objSkullRealistic = null;
	public static PShape objSkullRealistic() {
		if(objSkullRealistic == null) objSkullRealistic = P.p.loadShape(FileUtil.getFile("models/skull-realistic.obj"));
		return objSkullRealistic;
	}
	

}

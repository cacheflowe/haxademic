package com.haxademic.core.draw.image;

import java.util.HashMap;

import com.haxademic.core.app.P;

import processing.core.PImage;

public class ImageCacher {

	public static HashMap<String, PImage> images = new HashMap<String, PImage>();
	
	public static PImage get(String imagePath) {
		return get(imagePath, true);
	}
	
	public static PImage get(String imagePath, boolean localPath) {
		String key = imagePath;
		if(images.containsKey(key) == false) {
			if(localPath) images.put(key, P.getImage(imagePath));
			else          images.put(key, P.p.loadImage(imagePath));
		}
		return images.get(key);
	}
	
}
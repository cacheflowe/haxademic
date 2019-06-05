package com.haxademic.core.draw.image;

import java.util.HashMap;

import com.haxademic.core.app.P;

import processing.core.PImage;

public class ImageCacher {

	public static HashMap<String, PImage> images = new HashMap<String, PImage>();
	
	public static PImage get(String imagePath) {
		String key = imagePath;
		if(images.containsKey(key) == false) {
			images.put(key, P.getImage(imagePath));
		}
		return images.get(key);
	}
	
}
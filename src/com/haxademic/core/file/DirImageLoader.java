package com.haxademic.core.file;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

import com.haxademic.core.app.P;

public class DirImageLoader {
	
	public ArrayList<String> _formats;
	public ArrayList<PImage> images;
	
	/**
	 * Creates an image load that reads a directory and loads all of the images
	 * of the specified file types into a PImage ArrayList.
	 * @param	directory	The directory to load images from
	 * @param	formats		A comma-delimited list of image file extensions to load
	 */
	public DirImageLoader( PApplet p, String directory, String formats ) {
		_formats = new ArrayList<String>();
		images = new ArrayList<PImage>();
				
		// parse and loop through the format/extensions and load from the directory each time (there's certainly a better way to do this)
		String[] extensions = formats.split(",");
		for( int i=0; i < extensions.length; i++ ) {
			P.println("--- loading "+extensions[i] + " images ---");
			ArrayList<String> imageFiles = FileUtil.getFilesInDirOfType( directory, extensions[i] );
			for( int j=0; j < imageFiles.size(); j++ ) {
				P.println("--- "+directory + imageFiles.get(j));
				images.add( p.loadImage( directory + imageFiles.get(j) ) );
			}
		}
	}
}

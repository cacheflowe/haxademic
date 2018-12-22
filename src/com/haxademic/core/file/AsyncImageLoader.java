package com.haxademic.core.file;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Asynchronous, non-blocking PImage loader.
 * Based on code by Marius Watz (http://workshop.evolutionzone.com/ & http://code.google.com/p/codeandform/)
 * @author cacheflowe
 *
 */
public class AsyncImageLoader {

	public static PImage pImage = new PImage(10,10);

	protected ImageLoader _loader;
	protected Thread _loadThread;
	protected Boolean _complete;
	protected Boolean _error;
	protected PImage _image;

	public AsyncImageLoader( PApplet p, String imgLocation ) {
		_image = pImage;
		_complete = _error = false;
		_loader = new ImageLoader( p, imgLocation );
		_loadThread = new Thread( _loader );
		_loadThread.start();
	}

	public PImage image() {
		return _image;
	}

	public Boolean isFinished() {
		return _complete;
	}

	class ImageLoader implements Runnable {

		protected String _imgLocation;
		protected PApplet p;

		public ImageLoader( PApplet p, String imgLocation ) {
			_imgLocation = imgLocation;
			this.p = p;
		}    

		public void run() {
			P.println("Loading image: "+_imgLocation);
			_image = p.loadImage( _imgLocation );
			if( _imgLocation == null ) _error = true;
			_complete = true;
			if( _error ) 
				P.println("Error occurred when loading image: "+_imgLocation);
			//			else 
			//				P.println("Load successful. Image size is "+_image.width+"x"+_image.height+".");
		} 
	}
}
package com.haxademic.core.draw.image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHax;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGL;
import processing.opengl.Texture;

public class ImageUtil {
	
	public static final int BLACK_INT = -16777216;
	public static final int WHITE_INT = 16777215;
	public static final int CLEAR_INT = 48356;
	public static final int EMPTY_INT = 0;
	public static final int EMPTY_WHITE_INT = -1;
	
	public static int getPixelIndex( PImage image, int x, int y ) {
		return (int) x + y * image.width;
	}
	public static int getPixelIndex( PApplet image, int x, int y ) {
		return (int) x + y * image.width;
	}
	
	/**
	 * Return the color int for a specific pixel of a PImage
	 * @param image	Image to grab pixel color from
	 * @param x		x pixel of the image
	 * @param y		y pixel of the image
	 * @return		The color as an int
	 */
	public static int getPixelColor( PImage image, int x, int y ) {
		if( x < 0 || y < 0 || x > image.width - 1 || y > image.height - 1 || image.pixels == null || image.pixels.length < getPixelIndex( image, x, y ) ) return 0;
		return image.pixels[ getPixelIndex( image, x, y ) ];
	}
	public static int getPixelColor( PGraphics image, int x, int y ) {
		if( x < 0 || y < 0 || x > image.width - 1 || y > image.height - 1 || image.pixels == null || image.pixels.length < getPixelIndex( image, x, y ) ) return 0;
		return image.pixels[ getPixelIndex( image, x, y ) ];
	}
	public static int getPixelColor( PApplet image, int x, int y ) {
		if( x < 0 || y < 0 || x > image.width - 1 || y > image.height - 1 || image.pixels == null || image.pixels.length < getPixelIndex( image, x, y ) ) return 0;
		return image.pixels[ getPixelIndex( image, x, y ) ];
	}
	
	// needs testing....
	public static int getPixelColorFast( PApplet p, PGraphics image, int x, int y ) {
	    PGL pgl = image.beginPGL();
	    ByteBuffer buffer = ByteBuffer.allocateDirect(1 * 1 * Integer.SIZE / 8);
	
	    pgl.readPixels(x, y, 1, 1, PGL.RGBA, PGL.UNSIGNED_BYTE, buffer); 
	
	    // get the first three bytes
	    int r = buffer.get() & 0xFF;
	    int g = buffer.get() & 0xFF;
	    int b = buffer.get() & 0xFF;
	    buffer.clear();
	    image.endPGL();
	    return p.color(r, g, b);
	}
	
	public static float getBrightnessForPixel( PApplet p, PImage image, int x, int y ) {
		return p.brightness( image.pixels[ getPixelIndex( image, x, y ) ] ) * 0.1f;
	}
	
	public static float colorDifference( PApplet p, int color1, int color2 ) {
		return (Math.abs(p.red(color1) - p.red(color2)) + Math.abs(p.green(color1) - p.green(color2)) + Math.abs(p.blue(color1) - p.blue(color2)) ) / 765f;
	}
	
	public static float brightnessDifference( PApplet p, int color1, int color2 ) {
		return Math.abs((p.red(color1) + p.green(color1) + p.blue(color1)) - (p.red(color2) + p.green(color2) + p.blue(color2))) / 765f;
	}
	
	public static PImage getReversePImage( PImage image ) {
		PImage reverse = new PImage( image.width, image.height );
		for( int i=0; i < image.width; i++ ){
			for(int j=0; j < image.height; j++){
				reverse.set( image.width - 1 - i, j, image.get(i, j) );
			}
		}
		return reverse;
	}

	public static PImage getReversePImageFast( PImage image ) {
		PImage reverse = new PImage( image.width, image.height );
		reverse.loadPixels();
		for (int i = 0; i < image.width; i++) {
			for (int j = 0; j < image.height; j++) { 
				reverse.pixels[j*image.width+i] = image.pixels[(image.width - i - 1) + j*image.width]; // Reversing x to mirror the image
			}
		}
		reverse.updatePixels();
		return reverse;
	}

	public static PImage getScaledImage( PImage image, int newWidth, int newHeight ) {
		PImage scaled = new PImage( newWidth, newHeight );
		scaled.copy( image, 0, 0, image.width, image.height, 0, 0, newWidth, newHeight );
		return scaled;
	}
	
	public static void clearPGraphics( PGraphics pg ) {
//		pg.beginDraw();
		pg.background(0,0);
//		pg.endDraw();
	}
	
	public static PImage bufferedToPImage( BufferedImage bimg ) {
		try {
			PImage img = new PImage(bimg.getWidth(),bimg.getHeight(),PConstants.ARGB);
			bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
			img.updatePixels();
			return img;
		}
		catch(Exception e) {
			System.err.println("Can't create image from buffer");
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage pImageToBuffered( PImage pimg ) {
		return (BufferedImage) pimg.getNative();
//		BufferedImage dest = new BufferedImage( pimg.width, pimg.height, BufferedImage.TYPE_INT_ARGB );
//		Graphics2D g2 = dest.createGraphics();
//		g2.drawImage( pimg.getImage(), 0, 0, null );
//		g2.finalize();
//		g2.dispose();
//		return dest;
	}
	
	public static PGraphics imageToGraphics(PImage img) {
		PGraphics pg = P.p.createGraphics(img.width, img.height, P.P2D);
		pg.beginDraw();
		pg.image(img,0,0);
		pg.endDraw();
		return pg;
	}  

	public static PGraphics imageToGraphicsCropFill(PImage img, PGraphics pg) {
		pg.beginDraw();
		ImageUtil.cropFillCopyImage(img, pg, true);
		pg.endDraw();
		return pg;
	}  
	
	
	public static void cropFillCopyImage( PImage src, PImage dest, boolean cropFill ) {
		float containerW = dest.width;
		float containerH = dest.height;
		float imageW = src.width;
		float imageH = src.height;
		
		float ratioW = containerW / imageW;
		float ratioH = containerH / imageH;
		float shorterRatio = ratioW > ratioH ? ratioH : ratioW;
		float longerRatio = ratioW > ratioH ? ratioW : ratioH;
		float resizedW = cropFill ? (float)Math.ceil(imageW * longerRatio) : (float)Math.ceil(imageW * shorterRatio);
		float resizedH = cropFill ? (float)Math.ceil(imageH * longerRatio) : (float)Math.ceil(imageH * shorterRatio);
		float offsetX = (float)Math.ceil((containerW - resizedW) * 0.5f);
		float offsetY = (float)Math.ceil((containerH - resizedH) * 0.5f);
		
		dest.copy( src, 0, 0, (int) imageW, (int) imageH, (int) offsetX, (int) offsetY, (int) resizedW, (int) resizedH );
	}
	
	public static float[] offsetAndSize = new float[]{0,0,0,0};
	public static float[] getOffsetAndSizeToCrop( float containerW, float containerH, float imageW, float imageH, boolean cropFill ) {
		float ratioW = containerW / imageW;
		float ratioH = containerH / imageH;
		float shorterRatio = ratioW > ratioH ? ratioH : ratioW;
		float longerRatio = ratioW > ratioH ? ratioW : ratioH;
		float resizedW = (cropFill) ? (float) Math.ceil(imageW * longerRatio) : (float) Math.ceil(imageW * shorterRatio);
		float resizedH = (cropFill) ? (float) Math.ceil(imageH * longerRatio) : (float) Math.ceil(imageH * shorterRatio);
		float offsetX = (float) Math.ceil((containerW - resizedW) * 0.5f);
		float offsetY = (float) Math.ceil((containerH - resizedH) * 0.5f);
		offsetAndSize[0] = offsetX;
		offsetAndSize[1] = offsetY;
		offsetAndSize[2] = resizedW;
		offsetAndSize[3] = resizedH;
		return offsetAndSize;
	}
	
	public static void removeImageFromGraphicsCache(PImage img, PGraphics pg) {
		// https://forum.processing.org/two/discussion/6898/how-to-correctly-release-pimage-memory
		// https://github.com/jeffThompson/ProcessingTeachingSketches/blob/master/Utilities/AvoidPImageMemoryLeaks/AvoidPImageMemoryLeaks.pde
		// https://forum.processing.org/one/topic/pimage-memory-leak-example.html
//		for (int i = 0; i < imageSequence.size(); i++) {
			Object cache = pg.getCache(img);
			pg.removeCache(img);
			if (cache instanceof Texture) {
				((Texture) cache).disposeSourceBuffer();
			}
//		}
//		imageSequence.clear();
	}


	public static void chromaKeyImage( PApplet p, PImage sourceImg, PImage dest ) {
		float threshRange = 20f;
		float r = 0;
		float g = 0;
		float b = 0;
		for( int x=0; x < sourceImg.width; x++ ){
			for( int y=0; y < sourceImg.height; y++ ){
				int pixelColor = ImageUtil.getPixelColor( sourceImg, x, y );
				
				r = ColorHax.redFromColorInt( pixelColor );
				g = ColorHax.greenFromColorInt( pixelColor );
				b = ColorHax.blueFromColorInt( pixelColor );
				
				// if green is greater than both other color components, black it out
				if( g > r && g > b ) {
					dest.set( x, y, p.color( 255, 0 ) );
				} else if( g > r - threshRange && g > b - threshRange ) {
					dest.set( x, y, p.color( r, g, b ) );
				} else {
					dest.set( x, y, p.color( r, g, b ) );
				}
			}
		}
	}

}
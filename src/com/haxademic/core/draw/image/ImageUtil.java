package com.haxademic.core.draw.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.util.DrawUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
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
	
	public static void setPixelColor( PImage image, int x, int y, int color ) {
		if( x < 0 || y < 0 || x > image.width - 1 || y > image.height - 1 || image.pixels == null || image.pixels.length < getPixelIndex( image, x, y ) ) return;
		image.pixels[ getPixelIndex( image, x, y ) ] = color;
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
		PGraphics pg = P.p.createGraphics(img.width, img.height, P.P3D);
		pg.beginDraw();
		pg.image(img,0,0);
		pg.endDraw();
		return pg;
	}  

	public static PGraphics shapeToGraphics(PShape shape) {
		return shapeToGraphics(shape, 1f);
	}  
	
	public static PGraphics shapeToGraphics(PShape shape, float scale) {
		return shapeToGraphics(shape, scale, -999);
	}  
	
	public static PGraphics shapeToGraphics(PShape shape, float scale, int bgColor) {
		PGraphics pg = P.p.createGraphics(P.ceil((float) shape.width * scale), P.ceil((float) shape.height * scale), P.P3D);
		pg.beginDraw();
		if(bgColor != -999) pg.background(bgColor);
		pg.shape(shape, 0, 0, pg.width, pg.height);
		pg.endDraw();
		return pg;
	}  
	
	public static PGraphics imageToGraphicsWithPadding(PImage img, float fillAmount) {
		PGraphics image = ImageUtil.imageToGraphics(img);
		image.beginDraw();
		DrawUtil.setDrawCenter(image);
		image.clear();
		image.translate(image.width/2, image.height/2);
		image.image(img, 0, 0, img.width * fillAmount, img.height * fillAmount);
		image.endDraw();
		return image;
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

	public static int[] zero4 = new int[] {0, 0, 0, 0};
	public static void imageCroppedEmptySpace(PGraphics sourceImg, PImage destImg, int emptyColor, boolean debug) {
		imageCroppedEmptySpace(sourceImg, destImg, emptyColor, debug, zero4, zero4, P.p.color(0,0));
	}
	
	public static void imageCroppedEmptySpace(PGraphics sourceImg, PImage destImg, int emptyColor, boolean debug, int[] padding, int[] cropIn, int bgColor) {
		if(debug) P.println("SEARCHING =======================");
		Rectangle bounds = null;
		sourceImg.loadPixels();
		
		// debug
		if(debug) sourceImg.beginDraw();
		if(debug) sourceImg.fill(255,0,0, 255);
		
		// find initial low-resolution bounds
		int searchSpacing = 10;
		for(int x=0; x < sourceImg.width; x += searchSpacing) {
			for(int y=0; y < sourceImg.height; y += searchSpacing) {
				int pixelColor = ImageUtil.getPixelColor(sourceImg, x, y);
				if(pixelColor != emptyColor) {
					if(bounds == null) bounds = new Rectangle(x, y, 1, 1);
					if(debug) sourceImg.rect(x, y, 1, 1);
					bounds.add(x, y);
				}
			}			
		}
		if(debug) P.println("low res bounds:", bounds);
		
		// create boundary padded by spacing to search within
		int refineX = P.max(0, bounds.x - searchSpacing);
		int refineY = P.max(0, bounds.y - searchSpacing);
		int refineW = P.min(sourceImg.width, bounds.width + searchSpacing * 2);
		int refineH = P.min(sourceImg.height, bounds.height + searchSpacing * 2);

		Rectangle refineBounds = new Rectangle(
				refineX,
				refineY,
				refineW,
				refineH
		);
		if(debug) P.println("refineBounds:", refineBounds);

		if(debug) sourceImg.fill(255,255,0, 127);	 // set refine color

		// move out one spacing and run through the process again per-pixel
		// vertical
		for(int x = refineBounds.x; x < refineBounds.x + refineBounds.width; x++) {
			for(int y = refineBounds.y; y < refineBounds.y + searchSpacing; y++) {
				int pixelColor = ImageUtil.getPixelColor(sourceImg, x, y);
				if(pixelColor != emptyColor) {
					if(debug) sourceImg.rect(x, y, 1, 1);
					bounds.add(x, y);
				}
			}			
			for(int y = refineBounds.y + refineBounds.height - searchSpacing; y < refineBounds.y + refineBounds.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(sourceImg, x, y);
				if(pixelColor != emptyColor) {
					if(debug) sourceImg.rect(x, y, 1, 1);
					bounds.add(x, y);
				}
			}			
		}
		
		// horizontal
		for(int y = refineBounds.y; y < refineBounds.y + refineBounds.height; y++) {
			for(int x = refineBounds.x; x < refineBounds.x + searchSpacing; x++) {
				int pixelColor = ImageUtil.getPixelColor(sourceImg, x, y);
				if(pixelColor != emptyColor) {
					if(debug) sourceImg.rect(x, y, 1, 1);
					bounds.add(x, y);
				}
			}
			for(int x = refineBounds.x + refineBounds.width - searchSpacing; x < refineBounds.x + refineBounds.width; x++) {
				int pixelColor = ImageUtil.getPixelColor(sourceImg, x, y);
				if(pixelColor != emptyColor) {
					if(debug) sourceImg.rect(x, y, 1, 1);
					bounds.add(x, y);
				}
			}
		}

		// show result outline 
		if(debug) {
			sourceImg.noFill();
			sourceImg.stroke(0,255,0, 127);
			sourceImg.rect(bounds.x, bounds.y, bounds.width, bounds.height-1);
			sourceImg.stroke(255,255,0, 255);
			sourceImg.rect(refineBounds.x, refineBounds.y, refineBounds.width, refineBounds.height-1);
		}
		
		// copy to cropped image buffer
		// padding & crop arrays go top, right, bottom, left
		// resize destination image
		int destW = bounds.width + 1;
		int destH = bounds.height + 1;
		int cropW = destW - cropIn[1] - cropIn[3];
		int cropH = destH - cropIn[0] - cropIn[2];
		destW += padding[1] + padding[3] - cropIn[1] - cropIn[3];
		destH += padding[0] + padding[2] - cropIn[0] - cropIn[2];
		destImg.resize(destW, destH);
		if(debug) P.println("destW, destH", destW, destH);
		// get size of image to crop
		// clear destination image
		destImg.loadPixels();
		int numPixels = destImg.width * destImg.height;
		for (int i = 0; i < numPixels; i++) destImg.pixels[i] = bgColor;
		destImg.updatePixels();
		// copy with padding
		destImg.copy(sourceImg, bounds.x + cropIn[3], bounds.y + cropIn[0], cropW, cropH, padding[3], padding[0], cropW, cropH);
		if(debug) P.println(bounds);
		if(debug) P.println(refineBounds);
		if(debug) sourceImg.endDraw();
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
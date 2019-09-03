package com.haxademic.core.draw.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.ScreenUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGL;
import processing.opengl.Texture;

public class ImageUtil {
	
	public static final int BLACK_INT = -16777216;
	public static final int TRANSPARENT_PNG = 16777215;
	public static final int CLEAR_INT = 48356;
	public static final int CLEAR_INT_PG = 13421772;
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
	
	public static PImage bufferedToPImage(BufferedImage bimg) {
		try {
			PImage img = new PImage(bimg.getWidth(), bimg.getHeight(), PConstants.ARGB);
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

	public static void copyBufferedToPImagePixels(BufferedImage buffImg, PImage pimg) {
		// get pixels from BufferedImage
//		int pixels[] = ((DataBufferInt) buffImg.getRaster().getDataBuffer()).getData();
		// Copy array to PImage and update
		buffImg.getRGB(0, 0, pimg.width, pimg.height, pimg.pixels, 0, pimg.width);
		pimg.updatePixels();
	}
	
	public static PImage newPImageForBase64Jpeg(int w, int h) {
		// if we're going to encode a PImage to base64, it has to be RGB
		return new PImage(w, h, PConstants.RGB);
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
	
	public static BufferedImage newBufferedImage( int w, int h ) {
		return new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
//		Graphics2D g2 = dest.createGraphics();
//		g2.drawImage( pimg.getImage(), 0, 0, null );
//		g2.finalize();
//		g2.dispose();
//		return dest;
	}
	
	public static PGraphics imageToGraphics(PImage img) {
		PGraphics pg = P.p.createGraphics(img.width, img.height, PRenderers.P3D);
		pg.beginDraw();
		pg.background(0, 0);
		pg.image(img, 0, 0);
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
		PGraphics pg = P.p.createGraphics(P.ceil((float) shape.width * scale), P.ceil((float) shape.height * scale));
		pg.beginDraw();
		if(bgColor != -999) pg.background(bgColor);
		pg.shape(shape, 0, 0, pg.width, pg.height);
		pg.endDraw();
		return pg;
	}  
	
	public static PGraphics imageToGraphicsWithPadding(PImage img, float fillAmount) {
		PGraphics image = ImageUtil.imageToGraphics(img);
		image.beginDraw();
		PG.setDrawCenter(image);
		image.clear();
		image.translate(image.width/2, image.height/2);
		image.image(img, 0, 0, img.width * fillAmount, img.height * fillAmount);
		image.endDraw();
		return image;
	}  
	
	public static PImage imageToImageWithPadding(PImage img, float scaleCanvasUp) {
		PGraphics pg = P.p.createGraphics(P.ceil((float) img.width * scaleCanvasUp), P.ceil((float) img.height * scaleCanvasUp));
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.clear();
		pg.translate(pg.width/2, pg.height/2);
		pg.image(img, 0, 0);
		pg.endDraw();
		return pg.copy();
	}  
	
	public static PGraphics imageToGraphicsCropFill(PImage img, PGraphics pg) {
		pg.beginDraw();
		ImageUtil.cropFillCopyImage(img, pg, true);
		pg.endDraw();
		return pg;
	}  
	
	public static void copyImage(PImage src, PImage dest) {
		dest.copy(src, 0, 0, src.width, src.height, 0, 0, dest.width, dest.height);
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
	
	public static void drawImageCropFill(PImage img, PGraphics dest, boolean cropFill) {
		drawImageCropFill(img, dest, cropFill, false);
	}
	
	// fills a specific rectangle without depending on offsetting off an entire buffer/canvas like the other version of this method
	public static void cropFillCopyImage(PImage src, PImage dest, int destX, int destY, int destW, int destH, boolean cropFill) {
		int imageW = src.width;
		int imageH = src.height;
		float ratioW = (float) destW / imageW;
		float ratioH = (float) destH / imageH;
		if(cropFill) {
			float fillRatio = ratioW > ratioH ? ratioW : ratioH;
			int scaledDestW = P.round(destW / fillRatio);	// scale destination dimension to match source, so we can puLl the rect at the right scale
			int scaledDestH = P.round(destH / fillRatio);
			int srcX = cropFill ? P.round(imageW/2 - scaledDestW/2) : 0;
			int srcY = cropFill ? P.round(imageH/2 - scaledDestH/2) : 0;
			int srcW = cropFill ? P.round(scaledDestW) : 0;
			int srcH = cropFill ? P.round(scaledDestH) : 0;
			dest.copy(src, srcX, srcY, srcW, srcH, destX, destY, destW, destH);
		} else {
			float letterboxRatio = ratioW > ratioH ? ratioH : ratioW;
			int resizedW = P.ceil(imageW * letterboxRatio);
			int resizedH = P.ceil(imageH * letterboxRatio);
			int offsetX = P.ceil((destW - resizedW) * 0.5f);
			int offsetY = P.ceil((destH - resizedH) * 0.5f);
			dest.copy(src, 0, 0, imageW, imageH, destX + offsetX, destY + offsetY, resizedW, resizedH);
		}
	}
	
	public static void drawImageCropFill(PImage img, PGraphics dest, boolean cropFill, boolean openDestContext) {
		float ratioW = MathUtil.scaleToTarget(img.width, dest.width);
		float ratioH = MathUtil.scaleToTarget(img.height, dest.height);
		float scale = (ratioH < ratioW) ? ratioH : ratioW;			// letterbox
		if(cropFill) scale = (ratioH > ratioW) ? ratioH : ratioW;		// crop fill
		if(openDestContext) dest.beginDraw();
		PG.setDrawCenter(dest);
		dest.image(img, dest.width/2, dest.height/2, img.width * scale, img.height * scale);
		PG.setDrawCorner(dest);
		if(openDestContext) dest.endDraw();
	}
	
	public static void drawImageCropFillRotated(PImage img, PGraphics dest, boolean cropFill, boolean positive, boolean openDestContext) {
		float ratioW = MathUtil.scaleToTarget(img.height, dest.width);
		float ratioH = MathUtil.scaleToTarget(img.width, dest.height);
		float scale = (ratioH < ratioW) ? ratioH : ratioW;				// letterbox
		if(cropFill) scale = (ratioH > ratioW) ? ratioH : ratioW;		// crop fill
		if(openDestContext) dest.beginDraw();
		PG.push(dest);
		PG.setDrawCenter(dest);
		PG.setCenterScreen(dest);
		dest.rotate(P.HALF_PI * ((positive) ? 1f : -1f));
		dest.image(img, 0, 0, img.width * scale, img.height * scale);
		PG.setDrawCorner(dest);
		PG.pop(dest);
		if(openDestContext) dest.endDraw();
	}
	
	public static void copyImageFlipH(PImage src, PImage dest) {
		dest.copy(src, 0, 0, src.width, src.height, dest.width, 0, -dest.width, dest.height);
	}
	
	public static void flipH(PImage img) {
		img.copy(0, 0, img.width, img.height, img.width, 0, -img.width, img.height);
	}
	
	public static void flipV(PImage img) {
		img.copy(0, 0, img.width, img.height, 0, img.height, img.width, -img.height);
	}
	
	protected static HashMap<String, PImage> rescaleBlurImgs = new HashMap<String, PImage>();
	public static void blurByRescale(PGraphics img, float scaleBlur) {
		// lazy init image per scale, store in hash
		int scaleDownW = P.constrain(P.round(img.width * scaleBlur), 10, 99999);
		int scaleDownH = P.constrain(P.round(img.height * scaleBlur), 10, 99999);
		String sizeKey = scaleDownW + "," + scaleDownH;
		if(rescaleBlurImgs.containsKey(sizeKey) == false) {
			rescaleBlurImgs.put(sizeKey, new PImage(scaleDownW, scaleDownH)); // PG.newPG(scaleDownW, scaleDownH));
		}
		PImage scaleDest = rescaleBlurImgs.get(sizeKey);
		// ping pong to smaller image, then scale back up
		ImageUtil.copyImage(img, scaleDest);
		ImageUtil.copyImage(scaleDest, img);
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
				
				r = ColorUtil.redFromColorInt( pixelColor );
				g = ColorUtil.greenFromColorInt( pixelColor );
				b = ColorUtil.blueFromColorInt( pixelColor );
				
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

	
	public static void drawTextureMappedRect(PGraphics dest, PImage texture, int subdivideX, int subdivideY, float topLeftX, float topLeftY, float topRightX, float topRightY, float bottomRightX, float bottomRightY, float bottomLeftX, float bottomLeftY) {
		// draw to screen with pinned corner coords
		// generalized version ported from PGraphicsKeystone
		// inspired by: https://github.com/davidbouchard/keystone & http://marcinignac.com/blog/projectedquads-source-code/
		dest.textureMode(PConstants.IMAGE);
		dest.noStroke();
		dest.fill(255);
		dest.beginShape(PConstants.QUAD);
		dest.texture(texture);
		
		if(subdivideX > 0) {
			// subdivide quad for better resolution
			float stepsX = subdivideX;
			float stepsY = subdivideY;

			for( float x=0; x < stepsX; x += 1f ) {
				// calculate spread of mesh grid and uv coordinates
				float xPercent = x/stepsX;
				float xPercentNext = (x+1f)/stepsX;
				if( xPercentNext > 1 ) xPercentNext = 1;
				float uPercent = xPercent;
				float uPercentNext = xPercentNext;

				for( float y=0; y < stepsY; y += 1f ) {
					// calculate spread of mesh grid and uv coordinates
					float yPercent = y/stepsY;
					float yPercentNext = (y+1f)/stepsY;
					if( yPercentNext > 1 ) yPercentNext = 1;
					float vPercent = yPercent;
					float vPercentNext = yPercentNext;

					// calc grid positions based on interpolating columns between corners
					float colTopX = interp(topLeftX, topRightX, xPercent);
					float colTopY = interp(topLeftY, topRightY, xPercent);
					float colBotX = interp(bottomLeftX, bottomRightX, xPercent);
					float colBotY = interp(bottomLeftY, bottomRightY, xPercent);
					
					float nextColTopX = interp(topLeftX, topRightX, xPercentNext);
					float nextColTopY = interp(topLeftY, topRightY, xPercentNext);
					float nextColBotX = interp(bottomLeftX, bottomRightX, xPercentNext);
					float nextColBotY = interp(bottomLeftY, bottomRightY, xPercentNext);
					
					// calc quad coords
					float quadTopLeftX = interp(colTopX, colBotX, yPercent);
					float quadTopLeftY = interp(colTopY, colBotY, yPercent);
					float quadTopRightX = interp(nextColTopX, nextColBotX, yPercent);
					float quadTopRightY = interp(nextColTopY, nextColBotY, yPercent);
					float quadBotRightX = interp(nextColTopX, nextColBotX, yPercentNext);
					float quadBotRightY = interp(nextColTopY, nextColBotY, yPercentNext);
					float quadBotLeftX = interp(colTopX, colBotX, yPercentNext);
					float quadBotLeftY = interp(colTopY, colBotY, yPercentNext);
					
					// draw subdivided quads
					dest.vertex(quadTopLeftX, quadTopLeftY, 0, 	texture.width * uPercent, 		texture.height * vPercent);
					dest.vertex(quadTopRightX, quadTopRightY, 0, 	texture.width * uPercentNext, 	texture.height * vPercent);
					dest.vertex(quadBotRightX, quadBotRightY, 0, 	texture.width * uPercentNext, 	texture.height * vPercentNext);
					dest.vertex(quadBotLeftX, quadBotLeftY, 0, 	texture.width * uPercent, 		texture.height * vPercentNext);
				}
			}
		} else {
			// default single mapped quad
			dest.vertex(topLeftX, topLeftY, 0, 			0, 0);
			dest.vertex(topRightX, topRightY, 0, 			texture.width, 0);
			dest.vertex(bottomRightX, bottomRightY, 0, 	texture.width, texture.height);
			dest.vertex(bottomLeftX, bottomLeftY, 0, 	0,  texture.height);
		}

		dest.endShape();
	}
	
	public static float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}

}
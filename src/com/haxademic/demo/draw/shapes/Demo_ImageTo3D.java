package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_ImageTo3D 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage image;
	protected boolean isWebCam;
	float _frames = 60;
	float size = 18f;

	public void setupFirstFrame() {

		image = DemoAssets.smallTexture();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, false );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "800" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		

		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, ""+Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "30" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}

	public void drawApp() {
		if(p.appConfig.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == true) {
			joons.jr.background(0,0,0); //background(gray), or (r, g, b), like Processing.
			joons.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.
			joons.jr.background("gi_instant"); //Global illumination, normal mode.
			joons.jr.background("cornell_box", p.width, p.height, p.height); //cornellBox(width, height, depth);
		} else {			
			background(255);
			PG.setBasicLights(p);
		}
		p.noStroke();

		// position center
		PG.setCenterScreen(p.g);
		p.translate(0, 0, image.width * -10);
		PG.basicCameraFromMouse(p.g);

//		drawImgWebCam();
		drawImgBoxes();
//		drawImgPyra();
	}

	public void drawImgBoxes() {
		image.loadPixels();
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.EMPTY_WHITE_INT && pixelColor != ImageUtil.TRANSPARENT_PNG ) {
//				if( pixelColor != ImageUtil.EMPTY_WHITE_INT && pixelColor != ImageUtil.WHITE_INT ) {
					p.pushMatrix();
					float xScaled = -image.width*size/2 + x * size;
					float yScaled  = -image.height*size/2 + y * size;
					p.translate(xScaled, yScaled);
					float osc = (float) p.frameCount/(float) _frames;
					p.fill( EasingColor.redFromColorInt(pixelColor), EasingColor.greenFromColorInt(pixelColor), EasingColor.blueFromColorInt(pixelColor), 255 );
					if(joons != null) joons.jr.fill("shiny", EasingColor.redFromColorInt(pixelColor), EasingColor.greenFromColorInt(pixelColor), EasingColor.blueFromColorInt(pixelColor));
					if(pixelBrightness > 0) p.box(size, size, pixelBrightness * 1.5f + 0f * P.sin((x+y)/3f + (P.TWO_PI * osc)));
					p.popMatrix();
				}
			}
		}
	}

	public void drawImgPyra() {
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.CLEAR_INT && pixelColor != ImageUtil.EMPTY_WHITE_INT ) {
					p.pushMatrix();
					float xScaled = -image.width*size/2 + x * size;
					float yScaled  = -image.height*size/2 + y * size;
					p.translate(xScaled, yScaled);
					p.fill( pixelColor );
					float osc = (float) p.frameCount/(float) _frames;
					float pyramidSize = (pixelBrightness * 0.5f) + 20f * P.sin((x+y)/3f + (P.TWO_PI * osc));
					Shapes.drawPyramid( p, pyramidSize, (int)size, false );
					p.popMatrix();
				}
			}
		}
	}
	
	public void drawImgWebCam() {
		// float size = 24f;
		image = ImageUtil.getScaledImage(WebCam.instance().image(), 64, 48 );
		drawImgPyra();
//		drawImgBoxes();
	}

}

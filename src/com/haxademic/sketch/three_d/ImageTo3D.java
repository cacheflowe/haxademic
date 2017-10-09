package com.haxademic.sketch.three_d;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PImage;

public class ImageTo3D 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage image;
	protected boolean isWebCam;
	float _frames = 30;

	public void setup() {
		super.setup();
//		image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/justin-tiny-color1.png" );
		image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/ello-tiny-edit.png" );
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
			DrawUtil.setBasicLights(p);
		}
		p.noStroke();

		translate(width/2, height * 0.45f, -400);
//		translate(0,0,-1400);
		
//		p.shininess(200); 
//		p.lights();
//		p.ambientLight(0.3f,0.3f,0.3f, 0, 0, 6000);
//		p.ambientLight(0.3f,0.3f,0.3f, 0, 0, -6000);

	
//		p.rotateX(mouseY*0.01f);
//		p.rotateY(mouseX*0.01f);
		p.rotateX(P.PI/10);
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
//		p.rotateY(percentComplete * P.TWO_PI);

//		drawImgWebCam();
//		drawImgBoxes();
		drawImgPyra();
	}

	public void drawImgBoxes() {
		image.loadPixels();
		float size = 18f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.EMPTY_WHITE_INT && pixelColor != ImageUtil.WHITE_INT ) {
//				if( pixelColor != ImageUtil.EMPTY_WHITE_INT && pixelColor != ImageUtil.WHITE_INT ) {
					p.pushMatrix();
					float xScaled = -image.width*size/2 + x * size;
					float yScaled  = -image.height*size/2 + y * size;
					p.translate(xScaled, yScaled);
					float osc = (float) p.frameCount/(float) _frames;
					p.fill( ColorHax.redFromColorInt(pixelColor), ColorHax.greenFromColorInt(pixelColor), ColorHax.blueFromColorInt(pixelColor), 255 );
					if(joons != null) joons.jr.fill("shiny", ColorHax.redFromColorInt(pixelColor), ColorHax.greenFromColorInt(pixelColor), ColorHax.blueFromColorInt(pixelColor));
					if(pixelBrightness > 0) p.box(size, size, pixelBrightness * 1.5f + 0f * P.sin((x+y)/3f + (P.TWO_PI * osc)));
					p.popMatrix();
				}
			}
		}
	}

	public void drawImgPyra() {
		float size = 30f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = 255 - p.brightness( pixelColor );
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
		WebCamWrapper.initWebCam( this, 0 );
		image = ImageUtil.getScaledImage( WebCamWrapper.getImage(), 64, 48 );
		drawImgPyra();
//		drawImgBoxes();
	}

}

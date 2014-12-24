package com.haxademic.sketch.three_d;

import processing.core.PConstants;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.image.ImageUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ImageTo3D 
extends PAppletHax {

	protected PImage image;
	protected boolean isWebCam;
	float _frames = 50;

	public void setup() {
		super.setup();
		image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/justin-tiny-color1.png" );
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "800" );
		_appConfig.setProperty( "rendering", "false" );
		

		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}

	public void drawApp() {
		background(255);
		p.noStroke();

		translate(width/2, height/2, -400);
		DrawUtil.setBasicLights(p);
	
//		p.rotateX(mouseY*0.01f);
//		p.rotateY(mouseX*0.01f);
//		p.rotateX(P.PI/25);
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
//		p.rotateY(percentComplete * P.TWO_PI);

//		drawImgWebCam();
		drawImgBoxes();
	}

	public void drawImgBoxes() {
		float size = 18f;
		for( int x=0; x < image.width; x++ ){
			for(int y=0; y < image.height; y++){
				int pixelColor = ImageUtil.getPixelColor( image, x, y );
				float pixelBrightness = p.brightness( pixelColor );
				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.EMPTY_WHITE_INT && pixelColor != ImageUtil.WHITE_INT ) {
					p.fill( ColorHax.redFromColorInt(pixelColor), ColorHax.greenFromColorInt(pixelColor), ColorHax.blueFromColorInt(pixelColor), 255 );
					p.pushMatrix();
					float xScaled = -image.width*size/2 + x * size;
					float yScaled  = -image.height*size/2 + y * size;
					p.translate(xScaled, yScaled);
					if(pixelBrightness > 10) p.box(size, size, pixelBrightness * 1.3f);
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
				float pixelBrightness = p.brightness( pixelColor );
				if( pixelColor != ImageUtil.BLACK_INT && pixelColor != ImageUtil.CLEAR_INT ) {
					p.fill( pixelColor );
					p.pushMatrix();
					float xScaled = -image.width*size/2 + x * size;
					float yScaled  = -image.height*size/2 + y * size;
					p.translate(xScaled, yScaled);
					Shapes.drawPyramid( p, pixelBrightness * 2, (int)size, false );
					p.popMatrix();
				}
			}
		}
	}
	
	public void drawImgWebCam() {
		// float size = 24f;
		WebCamWrapper.initWebCam( this, 640, 480 );
		image = ImageUtil.getScaledImage( WebCamWrapper.getImage(), 64, 48 );
		drawImgPyra();
//		drawImgBoxes();
	}

}

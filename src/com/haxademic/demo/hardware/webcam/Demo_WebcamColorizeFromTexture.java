package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamColorizeFromTexture
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected ImageGradient imageGradient;
	protected PGraphics gradientTexture;
	protected PGraphics gradientTexture2;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame () {
		// build palette
//		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient = new ImageGradient(ImageGradient.RAINBOWISH());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		
		// override with custom gradient texture
		// 0xffFFB742, 0xffFFE406, 0xff32E003, 0xff53D5FF, 0xff1F27E9, 0xff9232D7, 0xffFF00FF, 0xffFF2601, 0xffFFB742
		gradientTexture = Gradients.textureFromColorArray(512, 8, new int[] {0xffFF2601, 0xffFFE406, 0xff32E003, 0xff53D5FF}, true); 
		gradientTexture2 = Gradients.textureFromColorArray(512, 8, new int[] {0xff1F27E9, 0xff9232D7, 0xffFF00FF, 0xffFFB742}, true); 
		imageGradient.setTexture(gradientTexture);
		
		// add gradient to debug
		DebugView.setTexture("imageGradient.texture()", imageGradient.texture());

		// capture webcam frames
		WebCam.instance().setDelegate(this);
		WebCam.instance().selectCamGstreamer(1920, 1080, 30, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = PG.newPG(frame.width, frame.height);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		DebugView.setTexture("webcam", flippedCamera);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			imageGradient.randomGradientTexture();
		}
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);

		// show camera & colorize
		if(flippedCamera != null) {
//			if(FrameLoop.frameModLooped(360)) {
//				imageGradient.setTexture((imageGradient.texture() == gradientTexture) ? gradientTexture2 : gradientTexture);
//			}
			
			ImageUtil.cropFillCopyImage(flippedCamera, p.g, true);
			
			ContrastFilter.instance().setContrast(3f);
//			ContrastFilter.instance().applyTo(p);
			BrightnessFilter.instance().setBrightness(1.7f);
			BrightnessFilter.instance().setOnContext(p);
			
			ColorizeFromTexture.instance().setTexture(imageGradient.texture());
			ColorizeFromTexture.instance().setLumaMult(Mouse.xNorm > 0.5f);
			ColorizeFromTexture.instance().setCrossfade(Mouse.yNorm);
			ColorizeFromTexture.instance().setOffset(FrameLoop.count(0.005f));
			ColorizeFromTexture.instance().setOnContext(p);
		}
	}

}

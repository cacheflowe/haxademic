package com.haxademic.demo.draw.cv;

import java.util.ArrayList;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.filters.pshader.compound.ColorAdjustmentFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ZXING_BarcodeScanner
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics webcamBuffer;
	protected PGraphics bwBuffer;

  protected ZXING4P qr;
  protected ArrayList<String> barcodeTypes = new ArrayList<String>();
  protected String lastResult = "";

  protected String UI_THRESH_ACTIVE = "UI_THRESH_ACTIVE";
  protected String UI_THRESH_MIX = "UI_THRESH_MIX";
  protected String UI_THRESH_CUTOFF = "UI_THRESH_CUTOFF";
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	protected void firstFrame () {
    buildCamera();
    buildReader();
  }
  
  protected void buildCamera() {
    WebCam.instance().setDelegate(this);

    webcamBuffer = PG.newPG(p.width, p.height);
    bwBuffer = PG.newPG(p.width, p.height);

    ColorAdjustmentFilter.buildUI("two", true);
    UI.addToggle(UI_THRESH_ACTIVE, false, false);
    UI.addSlider(UI_THRESH_CUTOFF, 0.5f, 0, 1, 0.01f, false);
    UI.addSlider(UI_THRESH_MIX, 0.5f, 0, 1, 0.01f, false);
  }

  protected void buildReader() {
    qr = new ZXING4P();
    P.out("ZXING version: "); 
    qr.version();
    // SUPPORTED BARCODE TYPES:
    // AZTEC
    // CODABAR
    // CODE_128
    // CODE_39
    // CODE_93
    // DATA_MATRIX
    // EAN_13
    // EAN_8
    // ITF
    // MAXICODE
    // PDF_417
    // QR_CODE
    // RSS_14
    // RSS_EXPANDED
    // UPC_A
    // UPC_E
    // UPC_EAN_EXTENSION
    barcodeTypes.add("EAN_8");
    barcodeTypes.add("EAN_13");
    barcodeTypes.add("UPC_A");
    barcodeTypes.add("UPC_E");
    barcodeTypes.add("UPC_EAN_EXTENSION");
    barcodeTypes.add("DATA_MATRIX");
    barcodeTypes.add("QR_CODE");
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
				
		// show webcam
		p.image(bwBuffer, 0, 0);

    // TODO: needs to be threaded: Use a PImage copy - see example from OCR demo
    String result = qr.barcodeReader(bwBuffer.get(), false, barcodeTypes);
    if(result != null && result != lastResult && result.length() > 0) {
      DebugView.setValue("barcodeReader", result);
      lastResult = result;
      P.out("QR code found: ", result);
    }
	}

	@Override
	public void newFrame(PImage frame) {
		// set textures for debug view
		DebugView.setValue("newframe", p.frameCount);
		DebugView.setTexture("webcamBuffer", webcamBuffer);
		DebugView.setTexture("bwBuffer", bwBuffer);
		
		// copy webcam to current buffer
		ImageUtil.cropFillCopyImage(WebCam.instance().image(), webcamBuffer, true);
		// ImageUtil.flipH(webcamBuffer);
		ImageUtil.copyImage(webcamBuffer, bwBuffer);

    // post-process difference buffer w/ threshold of black & white falloff,
    ColorAdjustmentFilter.applyFromUI(bwBuffer, "two");
    if(UI.valueToggle(UI_THRESH_ACTIVE)) {
      ThresholdFilter.instance().setCutoff(UI.value(UI_THRESH_CUTOFF));
      ThresholdFilter.instance().setCrossfade(UI.value(UI_THRESH_MIX));
      ThresholdFilter.instance().applyTo(bwBuffer);
    }
	}

}

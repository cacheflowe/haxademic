package com.haxademic.demo.draw.cv;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Tess4J 
extends PAppletHax
implements IWebCamCallback {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    // Tess4J docs: 
    // https://javadoc.io/static/net.sourceforge.tess4j/tess4j/5.3.0/net/sourceforge/tess4j/ITesseract.html
    
    // Potential problem - seems to happen when I'm doing other things with my computer: 
    // - !w_it.cycled_list():Error:Assert failed:in file C:\Projects\github\tesseract-ocr\src\ccstruct\pageres.cpp, line 1408
    // - Only in Multithreaded situation
    //   - https://github.com/nguyenq/tess4j/issues/143
    
    // OCR help: 
    // - https://towardsdatascience.com/pre-processing-in-ocr-fc231c6035a7
    
    protected Tesseract tesseract;
    protected boolean ocrBusy = false;
    protected BufferedImage imgBuff;
    protected String result = "";
    protected int resultTimeOCR = 0;
    protected int resultTimeRects = 0;
    protected List<Rectangle> resultRects;
    protected List<Word> resultWords;
    protected Rectangle rectangle;
    
    protected PGraphics webcamBuffer;
    protected PGraphics bwBuffer;
    protected PImage ocrInputImg;
    
    protected String UI_SHOW_DEBUG = "UI_SHOW_DEBUG";
    protected String UI_BRIGHTNESS = "UI_BRIGHTNESS";
    protected String UI_CONTRAST = "UI_CONTRAST";
    protected String UI_THRESH_CUTOFF = "UI_THRESH_CUTOFF";
    protected String UI_THRESH_MIX = "UI_THRESH_MIX";
    protected String UI_RECT_X = "UI_RECT_X";
    protected String UI_RECT_Y = "UI_RECT_Y";
    protected String UI_RECT_W = "UI_RECT_W";
    protected String UI_RECT_H = "UI_RECT_H";

    protected void config() {
        Config.setProperty(AppSettings.WIDTH, 640);
        Config.setProperty(AppSettings.HEIGHT, 840);
    }

    protected void firstFrame () {
        WebCam.instance().setDelegate(this);

        webcamBuffer = PG.newPG(640, 480);
        bwBuffer = PG.newPG(640, 480);
        ocrInputImg = ImageUtil.newImage(webcamBuffer.width, webcamBuffer.height);
        rectangle = new Rectangle(0, 0, webcamBuffer.width, webcamBuffer.height);
        
        DebugView.setTexture("ocrInputImg", ocrInputImg);

        initOCR();
        initUI();
    }

    protected void initOCR() {
        tesseract = new Tesseract();
        tesseract.setDatapath(P.path("haxademic/tess4j"));
        tesseract.setOcrEngineMode(TessOcrEngineMode.OEM_LSTM_ONLY);
        tesseract.setLanguage("eng");
        
        // example native Java call 
//      File imgPath = FileUtil.fileFromPath(P.path("haxademic/images/no-signal.png"));
//      BufferedImage image = ImageIO.read(imgPath);
//      String result = tesseract.doOCR(image);
    }
    
    protected void initUI() {
        UI.addTitle("OCR Debug");
        UI.addToggle(UI_SHOW_DEBUG, true, false);
        UI.addTitle("Image pre-processing");
        UI.addSlider(UI_BRIGHTNESS, 1, 0.5f,  3, 0.01f, false);
        UI.addSlider(UI_CONTRAST, 1.4f, 1, 4, 0.01f, false);
        UI.addSlider(UI_THRESH_CUTOFF, 0.5f, 0,  1, 0.01f, false);
        UI.addSlider(UI_THRESH_MIX, 0.5f, 0, 1, 0.01f, false);
        UI.addTitle("Rect");
        UI.addSlider(UI_RECT_X, 0, 0, bwBuffer.width, 1, false);
        UI.addSlider(UI_RECT_Y, 0, 0, bwBuffer.height, 1, false);
        UI.addSlider(UI_RECT_W, bwBuffer.width, 1, bwBuffer.width, 1, false);
        UI.addSlider(UI_RECT_H, bwBuffer.height, 1, bwBuffer.height, 1, false);
    }
    
    protected void prepareOCR() {
        // post-process buffer w/ threshold 
        ImageUtil.cropFillCopyImage(webcamBuffer, bwBuffer, true);
        // pre-process effects
        SaturationFilter.instance().setSaturation(0);
        SaturationFilter.instance().applyTo(bwBuffer);
        BrightnessFilter.instance().setBrightness(UI.value(UI_BRIGHTNESS));
        BrightnessFilter.instance().applyTo(bwBuffer);
        ContrastFilter.instance().setContrast(UI.value(UI_CONTRAST));
        ContrastFilter.instance().applyTo(bwBuffer);
        ThresholdFilter.instance().setCutoff(UI.value(UI_THRESH_CUTOFF));
        ThresholdFilter.instance().setCrossfade(UI.value(UI_THRESH_MIX));
        ThresholdFilter.instance().applyTo(bwBuffer);

        // need to copy the buffered image on the main thread
        ImageUtil.copyImage(bwBuffer, ocrInputImg);
        imgBuff = ImageUtil.pImageToBuffered(ocrInputImg);
    }
    
    protected void doOCR() {
        if(ocrBusy) return;
        
        
        // then thread the analysis
        new Thread(new Runnable() { public void run() {
            try {
                // start thread
                ocrBusy = true;
                
                // do the OCR
                int timeStart = p.millis();
                rectangle.setBounds(UI.valueInt(UI_RECT_X), UI.valueInt(UI_RECT_Y), UI.valueInt(UI_RECT_W), UI.valueInt(UI_RECT_H));
                result = tesseract.doOCR(imgBuff, rectangle);
                resultTimeOCR = p.millis() - timeStart;
                
                // get rectangles for debug
                // resultRects = tesseract.getSegmentedRegions(imgBuff, TessPageIteratorLevel.RIL_SYMBOL); // just throws errors!
                if(UI.valueToggle(UI_SHOW_DEBUG)) {
                    int timeStart2 = p.millis();
                    resultWords = tesseract.getWords(imgBuff, TessPageIteratorLevel.RIL_WORD);
                    resultTimeRects = p.millis() - timeStart2;
                    DebugView.setValue("resultTimeRects", resultTimeRects);
                }
                
                // end thread
                DebugView.setValue("result", JsonUtil.jsonToSingleLine(result));
                ocrBusy = false;
            } 
            catch (TesseractException e) { P.out(e.getCause().getMessage()); e.printStackTrace(); }
        }}).start();
    }

    protected void drawApp() {
        // set up context
        p.background( 0 );
        DebugView.logUptime();

        // test single image
        // ImageUtil.copyImage(ImageCacher.get("haxademic/images/no-signal.png"), bwBuffer);
        
        if(ocrBusy == false) {
            prepareOCR();
            doOCR();
        }
        
        // draw camera image
        p.image(webcamBuffer, 0, 0);
        if(UI.valueToggle(UI_SHOW_DEBUG)) {
            p.image(ImageUtil.bufferedToPImage( ImageUtil.pImageToBuffered(bwBuffer)), 0, 0);
        }
        
        // draw input rectangle
        p.noFill();
        p.stroke(0, 0, 255);
        p.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        // draw OCR rectangles
        if(UI.valueToggle(UI_SHOW_DEBUG)) {
            p.noFill();
            p.stroke(0, 255, 0);
            if(resultWords != null) {
                resultWords.forEach((word) -> {
                    Rectangle rect = word.getBoundingBox();
                    p.rect(rect.x, rect.y, rect.width, rect.height);
                });
            }
        }
        
        // draw OCR results
        String fontFile = DemoAssets.fontOpenSansPath;
        PFont font = FontCacher.getFont(fontFile, 24);
        FontCacher.setFontOnContext(p.g, font, p.color(0, 255, 0), 1f, PTextAlign.LEFT, PTextAlign.TOP);
        p.text("OCR time: " + resultTimeOCR, 50, 530);
        if(UI.valueToggle(UI_SHOW_DEBUG)) {
            p.text("Debug time: " + resultTimeRects, 300, 530);
        }
        FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
        p.text(result, 50, 580, 540, 1000);
    }

    @Override
    public void newFrame(PImage frame) {
        // set textures for debug view
        DebugView.setValue("newframe", p.frameCount);
        DebugView.setTexture("webcamBuffer", webcamBuffer);
        DebugView.setTexture("bwBuffer", bwBuffer);

        // copy webcam to current buffer
        ImageUtil.cropFillCopyImage(WebCam.instance().image(), webcamBuffer, true);
        //		ImageUtil.flipH(webcamBuffer);
    }

}

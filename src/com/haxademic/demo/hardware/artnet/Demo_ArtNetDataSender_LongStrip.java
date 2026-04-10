package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_ArtNetDataSender_LongStrip
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected ArtNetDataSender artNetDataSender;
    //	protected int numPixels = 256 * 3;//400;
    protected int numPixels = 384 * 32 + 1;
    protected int colorChannels = numPixels * 3;

    protected String MAX_INDEX = "MAX_INDEX";
    protected String SOLID_COLOR = "SOLID_COLOR";
    protected String COLOR_R = "COLOR_R";
    protected String COLOR_G = "COLOR_G";
    protected String COLOR_B = "COLOR_B";

    protected PGraphics debugPG;

    protected void config() {
        Config.setProperty(AppSettings.SHOW_DEBUG, true);
        Config.setProperty(AppSettings.SHOW_UI, true);
    }

    protected void firstFrame() {
        // build artnet obj
        artNetDataSender = new ArtNetDataSender("192.168.1.253", 0, numPixels);

        // build debug buffer for visualizing artnet data array
        debugPG = PG.newPG(128, 128);
        DebugView.setTexture("debugPG", debugPG);

        // UI
        UI.addTitle("ArtNetDataSender");
        UI.addSlider(MAX_INDEX, colorChannels, 0, colorChannels, 1, false);
        UI.addToggle(SOLID_COLOR, false, false);
        UI.addSlider(COLOR_R, 50, 0, 255, 1, false);
        UI.addSlider(COLOR_G,  0, 0, 255, 1, false);
        UI.addSlider(COLOR_B,  0, 0, 255, 1, false);
    }

    protected void drawApp() {
        background(0);
        createColors();
        artNetDataSender.send();
        artNetDataSender.drawDebug(debugPG, true);
    }

    protected void createColors() {
        // build entire LED data, to loop through afterwards
        float colorSpeed = 0.02f;
        float colorFreq = 0.05f;
        if(UI.valueToggle(SOLID_COLOR) == false) {
            for(int i=0; i < numPixels; i++) {
                // set rgb colors
                int r = P.round(255f * Penner.easeInOutExpo(0.25f + 0.75f * P.sin(0+(i*colorFreq) + -frameCount * colorSpeed*1f)));
                int g = P.round(255f * Penner.easeInOutExpo(0.25f + 0.75f * P.sin(1+(i*colorFreq) + -frameCount * colorSpeed*0.8f)));
                int b = P.round(255f * Penner.easeInOutExpo(0.25f + 0.75f * P.sin(2+(i*colorFreq) + -frameCount * colorSpeed*0.6f)));

                // set data
                artNetDataSender.setColorAtIndex(i, r, g, b);
            }
        } else {
            for(int i=0; i < colorChannels; i+=3) {
                if(i < UI.valueInt(MAX_INDEX) - 3) {
                    artNetDataSender.setColorAtIndex(i+0, UI.valueInt(COLOR_R), 0, 0);
                    artNetDataSender.setColorAtIndex(i+1, UI.valueInt(COLOR_G), 0, 0);
                    artNetDataSender.setColorAtIndex(i+2, UI.valueInt(COLOR_B), 0, 0);
                } else {
                    artNetDataSender.setColorAtIndex(i, 0, 0, 0);
                }
            }
        }
    }
}
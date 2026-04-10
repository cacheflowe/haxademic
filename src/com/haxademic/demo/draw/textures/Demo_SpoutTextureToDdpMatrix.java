package com.haxademic.demo.draw.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorCorrectionFilter;
import com.haxademic.core.draw.filters.pshader.DitherColorBands;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SpoutTexture;
import com.haxademic.core.hardware.dmx.ddp.DdpDataSender;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_SpoutTextureToDdpMatrix
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected SpoutTexture spoutTexture;
    protected PGraphics ledTexture;
    protected DdpDataSender ddpDataSender;
    protected int numPixels;

    protected String BRIGHTNESS = "BRIGHTNESS";
    protected String GAMMA = "GAMMA";
    protected String DITHER_ON = "DITHER_ON";
    protected String DITHER_AMP = "DITHER_AMP";
    protected String PARTIAL_UPDATES = "PARTIAL_UPDATES";
    protected String FULL_REFRESH_INTERVAL = "FULL_REFRESH_INTERVAL";
    protected String FLIP_H = "FLIP_H";
    protected String FLIP_V = "FLIP_V";
    protected String ROT_180 = "ROT_180";

    protected void config() {
        Config.setAppSize(1024, 684);
        Config.setProperty(AppSettings.SHOW_DEBUG, true);
        Config.setProperty(AppSettings.SHOW_UI, true);
    }

    protected void firstFrame() {
        spoutTexture = new SpoutTexture(384, 256, "chromeyumm_spout_output");
        DebugView.setTexture("spoutTexture.texture()", spoutTexture.texture());
        ledTexture = PG.newPG(384, 32);

        numPixels = ledTexture.width * ledTexture.height;
        ddpDataSender = new DdpDataSender("192.168.1.253", 0, numPixels);

        UI.addTitle("LED Config");
        UI.addSlider(BRIGHTNESS, 0.9f, 0, 1, 0.01f, false);
        UI.addSlider(GAMMA, 1f, 0.5f, 3f, 0.01f, false);
        UI.addToggle(DITHER_ON, true, false);
        UI.addSlider(DITHER_AMP, 2.5f, 0f, 4f, 0.05f, false);
        UI.addToggle(PARTIAL_UPDATES, true, false);
        UI.addSlider(FULL_REFRESH_INTERVAL, 120, 10, 600, 1, false);
        UI.addToggle(FLIP_H, false, false);
        UI.addToggle(FLIP_V, false, false);
        UI.addToggle(ROT_180, false, false);
    }

    protected void drawApp() {
        background(0);

        spoutTexture.update();
        if(spoutTexture.texture() != null) {
            ImageUtil.copyImage(spoutTexture.texture(), ledTexture);
            ImageUtil.cropFillCopyImage(spoutTexture.texture(), ledTexture, true);
        }

        if(UI.valueToggle(FLIP_H)) ImageUtil.flipH(ledTexture);
        if(UI.valueToggle(FLIP_V)) ImageUtil.flipV(ledTexture);
        if(UI.valueToggle(ROT_180)) ImageUtil.rotate180(ledTexture);

        // Keep image conditioning in the normal shader pipeline, not in the DDP sender.
        BrightnessFilter.instance().setBrightness(UI.value(BRIGHTNESS));
        BrightnessFilter.instance().applyTo(ledTexture);
        ColorCorrectionFilter.instance().setBrightness(0f);
        ColorCorrectionFilter.instance().setContrast(1f);
        ColorCorrectionFilter.instance().setGamma(UI.value(GAMMA));
        ColorCorrectionFilter.instance().applyTo(ledTexture);
        if(UI.valueToggle(DITHER_ON)) {
            DitherColorBands.instance().setNoiseAmp(UI.value(DITHER_AMP));
            DitherColorBands.instance().applyTo(ledTexture);
        }

        ddpConfigure();

        PG.setCenterScreen(p);
        PG.setDrawCenter(p);
        p.image(ledTexture, 0, 0);

        int startTime = P.p.millis();
        ddpDataSender.sendRgbDirectFromPixelsArray(ledTexture, true);
        int sendTime = P.p.millis() - startTime;
        DebugView.setValue("DDP Send", sendTime + "ms");
        DebugView.setValue("DDP Pixels", numPixels);
        DebugView.setValue("DDP Packets", P.ceil((numPixels * 3f) / DdpDataSender.MAX_DATA_LENGTH));
        DebugView.setValue("DDP Partial", ddpDataSender.partialUpdatesEnabled());
        DebugView.setValue("DDP Sent Packets", ddpDataSender.lastSentPackets());
        DebugView.setValue("DDP Changed Packets", ddpDataSender.lastChangedPackets());
        DebugView.setValue("DDP Total Packets", ddpDataSender.lastTotalPackets());
    }

    protected void ddpConfigure() {
        ddpDataSender.setPartialUpdatesEnabled(UI.valueToggle(PARTIAL_UPDATES));
        ddpDataSender.setFullFrameRefreshInterval(P.round(UI.value(FULL_REFRESH_INTERVAL)));
    }

    public void dispose() {
        if(ddpDataSender != null) ddpDataSender.close();
        super.dispose();
    }
}
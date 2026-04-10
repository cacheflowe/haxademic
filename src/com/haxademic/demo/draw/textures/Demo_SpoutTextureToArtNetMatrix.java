package com.haxademic.demo.draw.textures;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SpoutTexture;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_SpoutTextureToArtNetMatrix 
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected SpoutTexture spoutTexture;
    protected PGraphics ledTexture;
    protected ArtNetDataSender artNetDataSender;
    protected int numPixels;

    protected String BRIGHTNESS = "BRIGHTNESS";
    protected String FLIP_H = "FLIP_H";
    protected String FLIP_V = "FLIP_V";
    protected String ROT_180 = "ROT_180";

    protected void firstFrame() {
        // SpoutTexture dimensions must match sender dimensions!
        spoutTexture = new SpoutTexture(384, 256, "chromeyumm_spout_output");
        DebugView.setTexture("spoutTexture.texture()", spoutTexture.texture());
        ledTexture = PG.newPG(384, 32);

        // Add ArtNet
        numPixels = spoutTexture.texture().width * spoutTexture.texture().height;
        artNetDataSender = new ArtNetDataSender("192.168.1.253", 0, numPixels);

        // Add UI
        UI.addTitle("LED Config");
        UI.addSlider(BRIGHTNESS, 0.2f, 0, 1, 0.01f, false);
        UI.addToggle(FLIP_H, false, false);
        UI.addToggle(FLIP_V, false, false);
        UI.addToggle(ROT_180, false, false);
    }

    protected void drawApp() {
        background(127);

        // update Spout & draw to screen
        spoutTexture.update();
        if(spoutTexture.texture() != null) {
            ImageUtil.copyImage(spoutTexture.texture(), ledTexture);
            ImageUtil.cropFillCopyImage(spoutTexture.texture(), ledTexture, true);
        }

        // draw debug on top
//        ledTexture.beginDraw();
//        int panelsW = 6;
//        int panelsH = 1;
//        PG.drawGrid(ledTexture, 0xff000000, 0x77ffff00, panelsW, panelsH, 1, false);
//        ledTexture.endDraw();

        // rotation correction operations
        if(UI.valueToggle(FLIP_H)) ImageUtil.flipH(ledTexture);
        if(UI.valueToggle(FLIP_V)) ImageUtil.flipV(ledTexture);
        if(UI.valueToggle(ROT_180)) ImageUtil.rotate180(ledTexture);

        // draw to screen
        PG.setCenterScreen(p);
        PG.setDrawCenter(p);
        p.image(spoutTexture.texture(), 0, 0);

        // send it!
        artNetDataSender.sendMatrixFromBuffer(ledTexture, false);
    }
}
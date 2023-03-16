package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageUtil_imageCroppedEmptySpace 
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected PGraphics textBuffer;
    protected PFont fontBig;
    protected PImage textCropped;
    protected TiledTexture tiledImg;
    protected int FRAMES = 140;

    protected void config() {
        Config.setProperty(AppSettings.WIDTH, 1024);
        Config.setProperty(AppSettings.HEIGHT, 582);
        Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
        Config.setProperty( AppSettings.RENDERING_MOVIE, false );
        Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
        Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
    }

    protected void firstFrame() {
        // create buffer & font
        textBuffer = PG.newPG(p.width, p.height);
        fontBig = DemoAssets.fontHelloDenver(200);
        textCropped = ImageUtil.newImage(128, 128);

        // draw text
        textBuffer.beginDraw();
        textBuffer.clear();
        textBuffer.background(0, 0);
        textBuffer.fill(255);
        textBuffer.textAlign(P.CENTER, P.CENTER);
        textBuffer.textFont(fontBig);
        textBuffer.textSize(fontBig.getSize());
        textBuffer.text("HELLO", 0, 0, textBuffer.width, textBuffer.height); 
        textBuffer.endDraw();
        DebugView.setTexture("textBuffer", textBuffer);

        // crop text
        int[] padding = new int[] {20, 20, 20, 20};
        int[] cropIn = new int[] {0, 0, 0, 0};
        ImageUtil.imageCroppedEmptySpace(textBuffer, textCropped, ImageUtil.EMPTY_INT, false, padding, cropIn, p.color(0, 255, 0, 0));
        DebugView.setTexture("textCropped", textCropped);

        // create tiled texture
        tiledImg = new TiledTexture(textCropped);
    }

    protected void drawApp() {
        background(0);
        PG.setCenterScreen(p);

        // draw tiled texture
        float size = 1f + 0.2f * P.sin(FrameLoop.progressRads());
        tiledImg.setRotation(0.01f * P.sin(FrameLoop.progressRads()));
        tiledImg.setOffset(0, -1f * FrameLoop.progress());
        tiledImg.setZoom(size, size);
        tiledImg.update();
        tiledImg.draw(p.g, p.width, p.height);
    }
}

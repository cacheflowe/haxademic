package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.MaskSliderFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

public class Demo_MaskSliderFilter
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    protected EasingFloat img1PosX = new EasingFloat(0, 0.2f);
    protected EasingFloat img1MaskX = new EasingFloat(0, 0.2f);

    protected void drawApp() {
        p.background(127);
        MaskSliderFilter.instance().updateHotSwap();
        
        ///////////////////////////////////////////////////
        // update easing
        ///////////////////////////////////////////////////
        if(FrameLoop.frameMod(300) == 150) {
            img1PosX.setEaseFactor(0.1f);
            img1MaskX.setEaseFactor(0.1f);
            img1PosX.setCurrent(600).setTarget(0);
            img1MaskX.setCurrent(1).setTarget(0);
        }
        if(FrameLoop.frameMod(300) == 0) {
            img1PosX.setEaseFactor(0.07f);
            img1MaskX.setEaseFactor(0.07f);
            img1PosX.setCurrent(0).setTarget(-600);
            img1MaskX.setCurrent(0).setTarget(-1.1f);
        }
        
        img1PosX.update(true);
        img1MaskX.update(true);
        
        ///////////////////////////////////////////////////
        // draw bg image with saturation post effect
        ///////////////////////////////////////////////////
        p.push();
        p.translate(0, img1PosX.value() * -0.5f);
        ImageUtil.drawImageCropFill(DemoAssets.justin(), p.g, true);
        MaskSliderFilter.instance().setOffset(0, img1MaskX.value());
        MaskSliderFilter.instance().applyTo(p.g);
        BrightnessFilter.instance().setBrightness(0.3f);
        BrightnessFilter.instance().applyTo(p);
        p.pop();

        ///////////////////////////////////////////////////
        // mask a PImage with a shader
        ///////////////////////////////////////////////////
        p.push();
        PG.setCenterScreen(p);
        PG.setDrawCenter(p);
        MaskSliderFilter.instance().setOffset(img1MaskX.value(), 0);
        MaskSliderFilter.instance().setOnContext(p);
        p.image(DemoAssets.noSignal(), img1PosX.value(), 0);
        MaskSliderFilter.instance().resetContext(p);
        p.pop();
    }

}
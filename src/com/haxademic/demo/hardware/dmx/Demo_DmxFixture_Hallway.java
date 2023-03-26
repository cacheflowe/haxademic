package com.haxademic.demo.hardware.dmx;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.ToneMappingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureShaderTimeStepper;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_DmxFixture_Hallway
extends PAppletHax {
    public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

    // props
    protected ArrayList<DMXFixture> fixtures;
    protected PGraphics ledBuffer;
    protected PGraphics ledBufferGlow;
    protected PGraphics ledBufferWall;
    protected PGraphics simBuffer;
    protected int numFixtures = 150;

    // patterns
    protected SimplexNoise3dTexture noiseTexture;
    protected TextureShaderTimeStepper shaderTexture1;
    protected TextureShaderTimeStepper shaderTexture2;

    // UI
    protected String brightness = "brightness";
    protected String speed = "speed";
    protected String hallwayOffset = "hallwayOffset";

    protected void config() {
        Config.setAppSize(1536, 1024);
    }

    protected void firstFrame() {
        buildLights();
        buildBuffers();
        buildTextures();
    }

    protected void buildBuffers() {
        simBuffer = PG.newPG(pg.width, pg.height);
        ledBuffer = PG.newPG(pg.width/4, pg.height/4);
        ledBufferGlow = PG.newPG(pg.width/4, pg.height/4);
        ledBufferWall = PG.newPG(pg.width/4, pg.height/4);
    }

    protected void buildTextures() {
        noiseTexture = new SimplexNoise3dTexture(ledBuffer.width, ledBuffer.height);
        shaderTexture1 = new TextureShaderTimeStepper(ledBuffer.width, ledBuffer.height, TextureShader.cacheflowe_scrolling_dashed_lines);
        shaderTexture2 = new TextureShaderTimeStepper(ledBuffer.width, ledBuffer.height, TextureShader.cacheflowe_scrolling_radial_twist);
    }

    protected void buildLights() {
        DMXUniverse.instanceInit("COM8", 9600);

        fixtures = new ArrayList<DMXFixture>(); 
        for (int i = 0; i < numFixtures; i++) {
            fixtures.add((new DMXFixture(1 + i * 3)).setEaseFactor(0.1f));
        }
        UI.addSlider(brightness, 1f, 0, 1, 0.01f);
        UI.addSlider(speed, 0.3f, -5, 5, 0.01f);
        UI.addSlider(hallwayOffset, -0.45f, -0.5f, 0.5f, 0.005f);
        //        UI.addWebInterface(false);
    }

    protected void drawBuffer() {
        pg.beginDraw();
        pg.noStroke();
        float numColors = ColorsHax.PRIDE.length;
        for (int i = 0; i < numColors; i++) {
            float colW = pg.width / numColors;
            float x = colW * i;
            pg.fill(ColorsHax.PRIDE[i]);
            pg.rect(x, 0, colW, pg.height);
        }
        pg.endDraw();

        // toggle alternate textures
        if(Mouse.xNorm < 0.25f) {
            RotateFilter.instance().setOffset(p.frameCount * (0.001f * UI.value(speed)), 0);
            RotateFilter.instance().applyTo(pg);
            BrightnessFilter.instance().setBrightness(UI.value(brightness));
            BrightnessFilter.instance().applyTo(pg);
        } else if(Mouse.xNorm > 0.25f && Mouse.xNorm < 0.5f) {
            noiseTexture.update(
                    1f,  // zoom
                    0,  // rotation
                    p.frameCount * -0.005f,  // x
                    0,  // y
                    p.frameCount * 0.005f,  // z
                    false,  // fractal mode
                    false // x repeat mode
                    );
            BrightnessFilter.instance().setBrightness(1.f);
            BrightnessFilter.instance().applyTo(noiseTexture.texture());
            ContrastFilter.instance().setContrast(2.5f);
            ContrastFilter.instance().applyTo(noiseTexture.texture());
            ImageUtil.copyImage(noiseTexture.texture(), pg);
            
            ColorizeFromTexture.instance().setTexture(ImageGradient.BLACK_HOLE());
            ColorizeFromTexture.instance().setLumaMult(true);
            ColorizeFromTexture.instance().setCrossfade(1);
            ColorizeFromTexture.instance().applyTo(pg);

        } else if(Mouse.xNorm > 0.5f && Mouse.xNorm < 0.75f) {
            shaderTexture1.setActive(true);
            shaderTexture1.updateDrawWithTime(p.frameCount * 0.01f);
            ImageUtil.copyImage(shaderTexture1.texture(), pg);
            RotateFilter.instance().setOffset(0, 0.02f);
            RotateFilter.instance().applyTo(pg);
        } else if(Mouse.xNorm > 0.75f) {
            shaderTexture2.setActive(true);
            shaderTexture2.updateDrawWithTime(p.frameCount * 0.01f);
            ImageUtil.copyImage(shaderTexture2.texture(), pg);
        }
    }

    protected void copyCenterYPixels(PGraphics src, PGraphics dest) {
        dest.copy(src, 0, src.height/2, src.width, 1, 0, 0, dest.width, dest.height);
    }

    protected void copyBuffers() {
        // scale down buffer
        ImageUtil.copyImage(pg, ledBuffer);
        copyCenterYPixels(pg, ledBufferGlow);

        DebugView.setTexture("ledBuffer", ledBuffer);

        // blur glow
        BlurProcessingFilter.instance().setBlurSize(20);
        BlurProcessingFilter.instance().setSigma(10);
        BlurProcessingFilter.instance().applyTo(ledBufferGlow);
        BlurProcessingFilter.instance().applyTo(ledBufferGlow);
        BlurProcessingFilter.instance().applyTo(ledBufferGlow);
        DebugView.setTexture("ledBufferGlow", ledBufferGlow);

        // apply to walls
        int blendTop = 0xff999999;
        int blendBot = 0xff111111;

        ledBufferWall.beginDraw();
        ImageUtil.copyImage(ledBufferGlow, ledBufferWall);
        PG.setCenterScreen(ledBufferWall);
        ledBufferWall.blendMode(PBlendModes.MULTIPLY);
        Gradients.quad(ledBufferWall, ledBufferWall.width, ledBufferWall.height, blendTop, blendTop, blendBot, blendBot);
        ledBufferWall.blendMode(PBlendModes.BLEND);
        ledBufferWall.endDraw();
        DebugView.setTexture("ledBufferWall", ledBufferWall);
        
        // diffuse the floor a bit more
        BlurProcessingFilter.instance().setBlurSize(10);
        BlurProcessingFilter.instance().setSigma(30);
        BlurProcessingFilter.instance().applyTo(ledBufferGlow);
        BlurProcessingFilter.instance().applyTo(ledBufferGlow);
        BlurProcessingFilter.instance().applyTo(ledBufferGlow);

    }

    protected void bufferToFixtures() {
        ledBuffer.loadPixels();
        ledBufferGlow.loadPixels();
        for (int i = 0; i < fixtures.size(); i++) {
            float progress = (float) i / fixtures.size();
            fixtures.get(i).color().setTargetInt(ImageUtil.getPixelColorNorm(ledBuffer, progress, 0.5f));
        }
    }

    protected void drawApp() {
        p.background(0);
        drawBuffer();
        copyBuffers();
        bufferToFixtures();
        drawSimulation();
        p.image(simBuffer, 0, 0);
    }

    protected void drawSimulation() {
        // begin context
        simBuffer.beginDraw();
        simBuffer.background(0);

        // translate "camera"
        PG.setCenterScreen(simBuffer);
        simBuffer.translate(0, simBuffer.height * 0.3f, simBuffer.height * 0.3f);
        PG.basicCameraFromMouse(simBuffer, 0.09f, 0.05f);
        
        // lights
//        PG.setBetterLightsAbove(simBuffer);
        simBuffer.lightSpecular(130, 130, 130); 
        simBuffer.directionalLight(200, 200, 200, -0.0f, -1.0f, 1); 
        simBuffer.directionalLight(200, 200, 200, 0.0f, -1.0f, -1); 

        float roomOffset = UI.valueEased(hallwayOffset);

        // measurements
        float oneFoot = simBuffer.height * 0.06f;
        float sixFoot = oneFoot * 6;
        float hallwayWidth = oneFoot * 6;
        float hallwayLength = oneFoot * 132; // 6 * 22;
        float ceilingHeight = oneFoot * 10;

        // draw person
        PShape person = DemoAssets.objHumanoid();
        if(frameCount == 1) {
            PShapeUtil.scaleShapeToHeight(person, sixFoot);
            PShapeUtil.meshRotateOnAxis(person, P.HALF_PI, P.Y);
            person.disableStyle();
        }
        simBuffer.push();
        int targetColor = ImageUtil.getPixelColorNorm(ledBufferGlow, P.map(roomOffset, -0.5f, 0.5f, 0, 1), 0.5f);
        simBuffer.fill(p.lerpColor(0xff555555, targetColor, 0.75f));
        simBuffer.shape(person);
        simBuffer.pop();

        { // room
            // slide room
            simBuffer.translate(0, 0, hallwayLength * roomOffset);

            // person at the end of the hallway
            simBuffer.push();
            simBuffer.translate(0, 0, hallwayLength / -2);
            simBuffer.rotateY(P.PI);
            targetColor = ImageUtil.getPixelColorNorm(ledBufferGlow, 1, 0.5f);
            simBuffer.fill(p.lerpColor(0xff555555, targetColor, 0.75f));
            simBuffer.shape(person);
            simBuffer.pop();

            // person in the middle of the hallway
            simBuffer.push();
            simBuffer.translate(hallwayWidth / -3, 0, 0);
            simBuffer.rotateY(P.PI);
            targetColor = ImageUtil.getPixelColorNorm(ledBufferGlow, 0.5f, 0.5f);
            simBuffer.fill(p.lerpColor(0xff555555, targetColor, 0.75f));
            simBuffer.shape(person);
            simBuffer.pop();
            
            // draw floor
            simBuffer.push();
            simBuffer.noLights();
            PG.setDrawCenter(simBuffer);
            simBuffer.rotateX(P.HALF_PI);
            simBuffer.rotateZ(-P.HALF_PI);
            Shapes.drawTexturedRect(simBuffer, ledBufferGlow, hallwayLength, hallwayWidth);
            simBuffer.fill(0, 210);
            simBuffer.rect(0, 0, hallwayLength, hallwayWidth);
            simBuffer.pop();

            // draw walls
            simBuffer.push();
            simBuffer.translate(-hallwayWidth/2, -ceilingHeight/2, 0);
            PG.setDrawCenter(simBuffer);
            simBuffer.rotateY(P.HALF_PI);
            Shapes.drawTexturedRect(simBuffer, ledBufferWall, hallwayLength, ceilingHeight);
            simBuffer.translate(0, 0, hallwayWidth);
            Shapes.drawTexturedRect(simBuffer, ledBufferWall, hallwayLength, ceilingHeight);
            //            simBuffer.rect(0, 0, hallwayLength, ceilingHeight);
            simBuffer.pop();

            // draw ladder 
            simBuffer.push();
            simBuffer.noLights();

            // draw ladder rails
            simBuffer.push();
            simBuffer.translate(0, -ceilingHeight, 0);
            simBuffer.fill(100);
            simBuffer.translate(-hallwayWidth/2, 0);
            simBuffer.box(4, 8, hallwayLength);
            simBuffer.translate(hallwayWidth, 0);
            simBuffer.box(4, 8, hallwayLength);
            simBuffer.pop();

            // rungs
            simBuffer.translate(0, -ceilingHeight, hallwayLength / 2f);
            float numLights = fixtures.size();
            float spacing = hallwayLength / numLights;
            for (int i = 0; i < numLights; i++) {
                simBuffer.push();
                simBuffer.translate(0, 0, i * -spacing);
                int fixtureColor = fixtures.get(i).color().colorInt();
                simBuffer.fill(fixtureColor);
                simBuffer.box(hallwayWidth, 4, 4);
                simBuffer.pop();
            }
            simBuffer.pop();

        }

        // end context
        simBuffer.endDraw();

        // post fx
        ToneMappingFilter.instance().setMode(0);
        ToneMappingFilter.instance().setGamma(1.25f);
        ToneMappingFilter.instance().setCrossfade(1f);
        ToneMappingFilter.instance().applyTo(simBuffer);

        BloomFilter.instance().setStrength(1.5f);
        BloomFilter.instance().setBlurIterations(6);
        BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
        BloomFilter.instance().applyTo(simBuffer);
    }
}


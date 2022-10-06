package com.haxademic.demo.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.DitherColorBands;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_TemporalSuperSampling
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	

	// temporal supersampling method, ported (to shaders) from Dave's (Beesandbombs) original CPU version
	
	protected PGraphics[] buffers;
	protected int samplesPerFrame = 12;
	protected float shutterAngle = .9f;
	protected PShader shader;
	protected float SS_PROGRESS;

	protected void config() {
		int FRAMES = 360;
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty( AppSettings.LOOP_TICKS, 16 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );

	}

	protected void firstFrame() {
		// build 12 buffers
		buffers = new PGraphics[samplesPerFrame];
		for (int i = 0; i < buffers.length; i++) {
			// create buffer
			buffers[i] = PG.newPG(p.width, p.height);
//			buffers[i].smooth(8);

			// set default context
			buffers[i].beginDraw();
			buffers[i].background(0, 255, 0);
			buffers[i].noFill();
			PG.setDrawCenter(buffers[i]);
			buffers[i].endDraw();

			// debug show buffers
			DebugView.setTexture("buff_"+i, buffers[i]);
		}
		
		shader = p.loadShader(P.path("haxademic/shaders/filters/temporal-super-sampling.glsl"));
	}

	protected void drawApp() {
		// draw scene 12 times
		runRender();
		
		// run temporal averaging shader 
		for (int i = 0; i < buffers.length; i++) {
			shader.set("tex"+(i+1), buffers[i]);
		}
		pg.filter(shader);
		
		// display to screen!
		ImageUtil.cropFillCopyImage(pg, p.g, false);
	}

	void runRender() {
		shutterAngle = 1.5f;
		for (int sa=0; sa<samplesPerFrame; sa++) {
			SS_PROGRESS = map(frameCount-1 + sa*shutterAngle/samplesPerFrame, 0, FrameLoop.loopFrames(), 0, 1);
			SS_PROGRESS = SS_PROGRESS % 1;
			drawScene(sa);
		}
	}
	
	void drawScene(int sampleIndex) {
		PGraphics buff = buffers[sampleIndex];
		

		buff.beginDraw();
		buff.background(0);
		buff.ortho();
		PG.setBasicLights(buff);
		PG.setBetterLightsAbove(buff);
		
//		buff.rotateY(0.1f + 0.2f * P.sin(P.TWO_PI*SS_PROGRESS * 1f));
		buff.rotateX(0.25f + 0.25f * P.sin(P.TWO_PI*SS_PROGRESS * 1f));
		float offsetY = -1f - P.sin(P.TWO_PI*SS_PROGRESS * 1f);
		buff.translate(0, offsetY * pg.height * 0.3f, -pg.width * 2.5f);
		int numRects = 15;
		for (int i = 0; i < numRects; i++) {
			float w = buff.width * 1.85f;
			w *= 1.65f + 0.76f * P.sin(i/5f + P.TWO_PI*SS_PROGRESS * 2f);
			float h = w + 50f * P.sin(P.TWO_PI*SS_PROGRESS * 1f);
			
			float twist = Penner.easeInOutCubic(i/30f + (SS_PROGRESS * 2) % 1) * P.TWO_PI;
			float posY = 30f * P.sin(i/30f + P.TWO_PI * SS_PROGRESS * 1f);

			buff.push();
			buff.fill(
					P.sin(2.3f + i/3f + P.TWO_PI * SS_PROGRESS * 1f) * 100f,
					P.sin(3.3f + i/3f + P.TWO_PI * SS_PROGRESS * 1f) * 30f,
					P.sin(4.3f + i/2f + P.TWO_PI * SS_PROGRESS * 1f) * 50f
					);
			buff.stroke(255);
			buff.strokeWeight(2.8f);
			buff.translate(buff.width/2, buff.height/2 - posY, i * 100f);
			buff.rotate(twist + P.QUARTER_PI);
			float stepDown = 1f / numRects * 1.02f;
			buff.box(w * (1f - i * stepDown), h * (1f - i * stepDown), pg.width * 0.09f);
			buff.pop();
		}

		buff.endDraw();
		
		DitherColorBands.instance(p).setTime(FrameLoop.count(0.00000001f));
		DitherColorBands.instance(p).setNoiseAmp(2);
		DitherColorBands.instance(p).applyTo(buff);

	}


}

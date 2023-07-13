package com.haxademic.core.draw.filters.pshader;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.Console;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

/* Poisson Filling for Processing
 * Made possible with support from The Frank-Ratchye STUDIO For Creative Inquiry
 * At Carnegie Mellon University. http://studioforcreativeinquiry.org/
 * from: https://gist.github.com/LingDong-/09d4e65d0c320246b950206db1382092
 * Modified by @cacheflowe to fix the pyramid size order
 */

public class PoissonFill{
	protected PShader shader1;
	protected PShader shader2;

	protected ArrayList<PGraphics> downs;
	protected ArrayList<PGraphics> ups;

	protected int w;
	protected int h;
	protected int depth;

	protected int buffIndex = 0;

	public PoissonFill(int _w, int _h) {
		this(_w, _h, P.floor(P.log(P.min(_w,_h))/P.log(2))-1);
	}
	

	public PoissonFill(int _w, int _h, int _depth){    
		w = _w;
		h = _h;
		depth = _depth;
		P.outColor(Console.CYAN_BOLD, "PoissonFill()", w, h, depth);

		shader1 = poissonStepShader();
		shader2 = poissonStepShader();
		downs = new ArrayList<PGraphics>();
		ups = new ArrayList<PGraphics>();
		for (int i = 0; i < depth; i++){
			downs.add(buffer(_w, _h));
			_w/=2;
			_h/=2;
		}
		// modified to keep same texture sizes as downs
		// original version scaled back up by multiplying, and sizes lost precision when scaling down above
		for (int i = 0; i < downs.size(); i++){
			int revIndex = downs.size()-i-1;
			ups.add(buffer(downs.get(revIndex).width, downs.get(revIndex).height));
		}
	}
	
	protected PGraphics buffer(int w, int h) {
		PGraphics newPG = PG.newPG(w, h, false, true);
//		newPG.hint(PApplet.DISABLE_TEXTURE_MIPMAPS);
		OpenGLUtil.setTextureQualityHigh(newPG);;
		PG.setTextureRepeat(newPG, false);
		// debug
		// DebugView.setTexture("poisson_"+buffIndex, newPG);
		// buffIndex++;
		return newPG;
	}
	
	protected PShader poissonStepShader() {
		return P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/poisson-fill.glsl"));
	}
	
	public void applyTo(PImage tex) {
		applyTo(tex, false);
	}

	public void applyTo(PImage tex, boolean blurs) {
		int i;
		pass(shader1, downs.get(0), tex, null, blurs);
		for (i = 1; i < depth; i++) {
			pass(shader1, downs.get(i), downs.get(i-1), null, blurs);
		}
		pass(shader2, ups.get(0), downs.get(depth-2), downs.get(depth-1), blurs);
		for (i = 1; i < depth-1; i++) {
			pass(shader2, ups.get(i), downs.get(depth-i-2), ups.get(i-1), blurs);
		}
		pass(shader2, ups.get(depth-1), tex, ups.get(depth-2), blurs);
	}

	public PImage output(){
		return ups.get(depth-1);
	}

	void pass(PShader shader, PGraphics pg, PImage tex1, PImage tex2, boolean blurs) {
		pg.beginDraw();
		shader.set("unf", tex1);
		if (tex2 != null){
			shader.set("fil", tex2);
		}
		shader.set("isup", tex2 != null);
		shader.set("w", pg.width);
		shader.set("h", pg.height);
		pg.clear();
		pg.noStroke();
		pg.fill(255);
		pg.shader(shader);
		pg.rect(0, 0, pg.width, pg.height);
		if(blurs) {
			float blurAmp = (pg.width / (float) w);
			blurAmp = P.map(blurAmp, 0, 1, 0.75f, 1);
			// blurAmp = 1;
			BlurProcessingFilter.instance().setSigma(20 * blurAmp);
			BlurProcessingFilter.instance().setBlurSize(P.ceil(10 * blurAmp));
			BlurProcessingFilter.instance().applyTo(pg);
		}
		pg.endDraw();
	}

}
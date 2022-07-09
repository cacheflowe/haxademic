package com.haxademic.core.draw.filters.pshader;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;

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
	PShader shader1;
	PShader shader2;

	public ArrayList<PGraphics> downs;
	public ArrayList<PGraphics> ups;

	int w;
	int h;
	public int depth;

	public PoissonFill(int _w, int _h, int _depth){    
		w = _w;
		h = _h;
		depth = _depth;

		shader1 = shader2way();
		shader2 = shader2way();
		downs = new ArrayList<PGraphics>();
		ups = new ArrayList<PGraphics>();
		for (int i = 0; i < depth; i++){
			downs.add(buffer(_w, _h));
			_w/=2;
			_h/=2;
		}
		for (int i = 0; i < depth; i++){
			_w*=2;
			_h*=2;
			ups.add(buffer(_w, _h));
		}
	}
	
	public PoissonFill(int _w, int _h){
		this(_w,_h,P.floor(P.log(P.min(_w,_h))/P.log(2))-1);
	}
	
	protected PGraphics buffer(int w, int h) {
		PGraphics newPG = PG.newPG(w, h, false, true);
//		newPG.hint(PApplet.DISABLE_TEXTURE_MIPMAPS);
		PG.setTextureRepeat(newPG, false);
		return newPG;
	}
	
	protected PShader shader2way(){
		return P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/poisson-fill.glsl"));
	}
	
	public void applyTo(PImage tex){
		int i;
		pass(shader1,downs.get(0),tex,null);
		for (i = 1; i < depth; i++) {
			pass(shader1,downs.get(i),downs.get(i-1),null);
		}
		pass(shader2,ups.get(0),downs.get(depth-2),downs.get(depth-1));
		for (i = 1; i < depth-1; i++) {
			pass(shader2,ups.get(i),downs.get(depth-i-2),ups.get(i-1));
		}
		pass(shader2,ups.get(depth-1),tex,ups.get(depth-2));
	}

	public PImage output(){
		return ups.get(depth-1);
	}

	void pass(PShader shader, PGraphics pg, PImage tex1, PImage tex2){
		pg.beginDraw();
		shader.set("unf", tex1);
		if (tex2 != null){
			shader.set("fil", tex2);
		}
		shader.set("isup", tex2 != null);
		shader.set("w",pg.width);
		shader.set("h",pg.height);
		pg.clear();
		pg.noStroke();
		pg.fill(255);
		pg.shader(shader);
		pg.rect(0, 0, pg.width, pg.height);
		pg.endDraw();
	}

}
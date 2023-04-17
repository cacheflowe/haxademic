package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.Pixelate2Filter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

public class Demo_PImageShaderContained
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextureShader textureShader;
	
	protected void firstFrame() {
		textureShader = new TextureShader(TextureShader.cacheflowe_chevron);
	}

	protected void drawApp() {
		p.background(0);
		p.noStroke();
		
		///////////////////////////////////////////////////
		// draw bg image with saturation post effect
		///////////////////////////////////////////////////
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		SaturationFilter.instance().setSaturation(FrameLoop.osc(0.02f, 0, 2));
		SaturationFilter.instance().applyTo(p.g);
		
		///////////////////////////////////////////////////
		// draw PImage with shader filter
		///////////////////////////////////////////////////
		p.push();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		Pixelate2Filter.instance().setDivider(FrameLoop.osc(0.1f, 20, 1));
		Pixelate2Filter.instance().setOnContext(p);
		p.image(DemoAssets.smallTexture(), 0, 0);
		Pixelate2Filter.instance().resetContext(p);
		p.pop();

		///////////////////////////////////////////////////
		// try a pattern shader on a polygon
		///////////////////////////////////////////////////
		p.push();
		p.translate(50,  50, 0);
		
		textureShader.setTimeMult(0.007f);
		textureShader.updateTime();
		p.shader(textureShader.shader());

		p.beginShape(P.QUADS);
		p.textureMode(P.NORMAL);
		p.texture(ImageUtil.blankImage()); // square image - probably would us different image sizes or different UVs if using square image
		p.vertex(0, 0, 0, 0);
		p.vertex(200, 0, 1, 0);
		p.vertex(200, 200, 1, 1); 
		p.vertex(0, 200, 0, 1);
		p.endShape();

		p.resetShader();
		p.pop();

		///////////////////////////////////////////////////
		// test a filter on a certain part of the screen
		///////////////////////////////////////////////////
		Pixelate2Filter.instance().setDivider(FrameLoop.osc(0.05f, 20, 1));
		Pixelate2Filter.instance().setOnContext(p);

		p.beginShape(P.QUADS);
		p.textureMode(P.NORMAL);
		p.texture(p.g); //  p.texture(p.g.get()); // need copy of image data if we want up-to-date texture
		p.vertex(500, 100, 500f/p.width, 100f/p.height);
		p.vertex(700, 300, 700f/p.width, 300f/p.height);
		p.vertex(700, 600, 700f/p.width, 600f/p.height); 
		p.vertex(500, 400, 500f/p.width, 400f/p.height);
		p.endShape();

		Pixelate2Filter.instance().resetContext(p);

		// test shader as filter
		// p.filter(textureShader.shader());
	}

}

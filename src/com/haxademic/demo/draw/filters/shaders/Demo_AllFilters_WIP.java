package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.shaders.textures.TextureShader;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.hardware.shared.InputTrigger;

public class Demo_AllFilters_WIP
extends PAppletHax { public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextureShader texture;
	protected int textureIndex = 0;

	protected InputTrigger triggerPrev = new InputTrigger(new char[]{'1'});
	protected InputTrigger triggerNext = new InputTrigger(new char[]{'2'});

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		texture = new TextureShader(TextureShader.bw_clouds);
	}

	public void drawApp() {
		// cycle
//		if(triggerPrev.triggered()) textureIndex = (textureIndex > 0) ? textureIndex - 1 : textures.length - 1;
//		if(triggerNext.triggered()) textureIndex = (textureIndex < textures.length - 1) ? textureIndex + 1 : 0;

		// update cur shader & draw to screen
		texture.updateTime();
		p.filter(texture.shader());
		
		// draw some text to make sure we know orientation
		p.stroke(0);
		p.strokeWeight(5f);
		p.fill(127 + 127f * P.sin(p.frameCount * 0.01f));
		p.textFont(DemoAssets.fontBitlow(100));
		p.textAlign(P.CENTER, P.CENTER);
		p.text("FILTER", 0, 0, p.width, p.height);
		
		// apply some filters
		DrawUtil.setTextureRepeat(p, true);
		CubicLensDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
		CubicLensDistortionFilter.instance(p).setSeparation(P.map(p.mouseY, 0, p.height, 0, 3f));
		CubicLensDistortionFilter.instance(p).applyTo(p);

		// old distortion
//		CubicLensDistortionFilterOscillate.instance(p).setTime(p.frameCount * 0.01f);
//		CubicLensDistortionFilterOscillate.instance(p).applyTo(p);
	}

}

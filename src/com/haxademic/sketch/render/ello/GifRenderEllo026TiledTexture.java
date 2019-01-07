package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;

public class GifRenderEllo026TiledTexture
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected TiledTexture tiledBg;
	protected int frames = 140;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, frames + 1 );
	}

	public void setup() {
		super.setup();
		tiledBg = new TiledTexture(p.loadImage(FileUtil.getFile("images/pot-leaf.png")));
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		float progress = (float)(p.frameCount % frames) / frames;
//		float easedPercent = Penner.easeInOutQuart(progress % 1, 0, 1, 1);
		float progressRads = progress * P.TWO_PI;

	
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		float rot = (P.floor(p.frameCount / 30f) % 2 == 0) ? 0 : 0.01f;
		rot = p.frameCount * 0.03f;
		tiledBg.setRotation(P.sin(progressRads * 2f) * 0.1f);
		tiledBg.setRotation(P.sin(P.HALF_PI + progressRads) * 0.2f);
//		tiledBg.setOffset(30000f * P.sin(p.frameCount * 0.01f), 10f * P.cos(p.frameCount * 0.01f));
		tiledBg.setSize(6f + 5.6f * P.sin(progressRads), 6f + 5.6f * P.sin(progressRads));
		tiledBg.update();
		tiledBg.drawCentered(p.g, p.width, p.height);
		p.popMatrix();
		
		
//		tiledBg.drawDebug(p.g);
	}	

}
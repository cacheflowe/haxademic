package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;

public class GifRenderEllo026TiledTexture
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected TiledTexture tiledBg;
	protected int frames = 140;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.FPS, 30 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, frames + 1 );
	}

	protected void firstFrame() {

		tiledBg = new TiledTexture(p.loadImage(FileUtil.getPath("images/pot-leaf.png")));
	}
	
	protected void drawApp() {
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
		tiledBg.draw(p.g, p.width, p.height);
		p.popMatrix();
		
		
//		tiledBg.drawDebug(p.g);
	}	

}
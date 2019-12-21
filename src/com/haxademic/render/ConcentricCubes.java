package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;

public class ConcentricCubes
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _x = 0;
	protected int _y = 0;

	protected int _frames = 100;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );

	}

	public void drawApp() {
		background(20);
		PG.setDrawCenter(p);
		p.noFill();
		p.translate(p.width/2, p.height/2, -1200);

		float percentComplete = ((float)(p.frameCount%_frames)/_frames);

		p.sphereDetail(4);

		//				p.blendMode(P.SCREEN);
//		p.blendMode(P.MULTIPLY);
//		p.blendMode(P.ADD);

		float cubeSize = p.width/1f;
		float cubScaleDown = 300f + 270 * P.sin(P.TWO_PI * percentComplete);
		int i=0;
		while(cubeSize > 10) {
			p.pushMatrix();
			p.stroke(127 + 127f * P.sin(i/20f), 127 + 127f * P.sin(i/30f), 127 + 127f * P.sin(i/10f));
			p.strokeWeight(2.2f);
//						p.rotateY(0.5f * P.sin(P.TWO_PI * percentComplete + i));
			p.rotateY(1.1f * P.sin(P.TWO_PI * percentComplete + i * 0.03f));
			p.rotateZ(2.1f * P.sin(P.TWO_PI * percentComplete + i * 0.03f));
			p.rotateX(1.1f * P.sin(P.TWO_PI * percentComplete + i * 0.03f));
//			p.box(cubeSize);
			p.sphere(cubeSize);
			p.popMatrix();
			cubeSize -= p.width / cubScaleDown;
			i++;
		}
	}
}




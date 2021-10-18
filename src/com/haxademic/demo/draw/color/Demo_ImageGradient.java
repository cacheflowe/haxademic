package com.haxademic.demo.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;

public class Demo_ImageGradient
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	ImageGradient imageGradient1;
	ImageGradient imageGradient2;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		imageGradient1 = new ImageGradient(ImageGradient.PASTELS());
//		imageGradient1.addTexturesFromPath(ImageGradient.COOLORS_PATH);
//		imageGradient2 = new ImageGradient(Gradients.textureFromColorArray(512, 8, ColorsHax.PRIDE));
		imageGradient2 = new ImageGradient(Gradients.textureFromColorArray(512, 16, ColorsHax.colorGroupAt(6)));
	}

	protected void drawApp() {
		p.noStroke();
		float colorProgress = 0.5f + 0.5f * P.sin(p.frameCount * 0.025f);
		
		// draw sampled color
		p.fill(imageGradient1.getColorAtProgress(colorProgress));
		p.rect(0, 0, p.width, p.height/2);
		
		p.fill(imageGradient2.getColorAtProgress(colorProgress));
		p.rect(0, p.height/2, p.width, p.height/2);
		if(p.frameCount % 240 == 1) imageGradient1.randomGradientTexture();
		
		// draw gradient 1
		p.push();
		p.translate(p.width/2 - imageGradient1.texture().width/2, p.height * 0.25f);
		imageGradient1.drawDebug(p.g);
		p.pop();

		// draw gradient 2
		p.push();
		p.translate(p.width/2 - imageGradient2.texture().width/2, p.height * 0.75f);
		imageGradient2.drawDebug(p.g);
		p.pop();
	}
}


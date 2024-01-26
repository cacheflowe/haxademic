package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDashedRect 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected LinearFloat showHide;
	protected LinearFloat rotate;

	protected void config() {
		Config.setAppSize(600, 600);
	}

	protected void firstFrame() {
		showHide = new LinearFloat(0, 0.025f);
		rotate = new LinearFloat(0, 0.025f);
	}
	
	protected void drawApp() {
		// context setup
		p.background(0);
		p.push();
		PG.drawGrid(p.g, 0xff000000, 0xff111111, 20, 20, 2, false);
		p.pop();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		PG.setDrawFlat2d(p, true);

		// p.blendMode(PBlendModes.ADD);
	
		// design sizing
		float w = 200;
		float h = 300;
		float hHalf = h / 2f;
		float strokeW = 10;
		float strokeWH = strokeW / 2f;
		float hQuarter = hHalf / 2f - strokeW;

		// update progress
		float target = (FrameLoop.frameMod(120) < 60) ? 1 : 0;
		showHide.setTarget(target).update();
		boolean isBack = showHide.value() > 0.5f;
		float progressEased = Penner.easeInOutExpo(showHide.value());
		
		// rotate
		float rotTarget = (FrameLoop.frameMod(240) < 120) ? 1 : 0;
		rotate.setTarget(rotTarget).update();
		float rotEased = Penner.easeInOutExpo(rotate.value());
		float rotX = P.map(rotEased, 0, 1, 0, 0.95f);
		float yOffset = 0; // P.map(rotEased, 0, 1, 0, -h * 0.1f);
		p.translate(0, yOffset);
		p.rotateX(rotX);

		// draw rectangles
		int strokeColor = ColorsHax.colorFromGroupAt(0, 0);
		p.push();
		if(isBack) {
			p.translate(0, -strokeWH);
			p.translate(0, -hQuarter);
			Shapes.drawDashedRect(p.g, w, hHalf, 20, strokeW, 0x00000000, strokeColor);
			p.translate(0, hHalf - strokeW);
			PG.drawStrokedRect(p.g, w, hHalf, strokeW, 0x00000000, strokeColor);
		} else {
			p.translate(0, strokeWH);
			p.translate(0, hQuarter);
			Shapes.drawDashedRect(p.g, w, hHalf, 20, strokeW, 0x00000000, strokeColor);
			p.translate(0, -hHalf + strokeW);
			PG.drawStrokedRect(p.g, w, hHalf, strokeW, 0x00000000, strokeColor);
		}
		p.pop();

		// draw dot
		float circleY = P.map(progressEased, 0, 1, -hQuarter, hQuarter);
		float circleSize = 40;
		p.push();
		p.fill(strokeColor);
		p.ellipse(0, circleY, circleSize, circleSize);
		p.pop();
	}
	
}

package com.haxademic.demo.draw.color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.ui.UI;

public class Demo_Gradients_SlidersRGB
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String COLOR_1 = "COLOR_1";
	protected String COLOR_2 = "COLOR_2";
	protected String COLOR_3 = "COLOR_3";
	
	public void setupFirstFrame() {
		UI.addSliderVector(COLOR_1, 0, 0, 255, 1, false);
		UI.addSliderVector(COLOR_2, 0, 0, 255, 1, false);
		UI.addSliderVector(COLOR_3, 0, 0, 255, 1, false);
	}
	
	public void drawApp() {
		background(0);
		
		int color1 = p.color(UI.valueX(COLOR_1), UI.valueY(COLOR_1), UI.valueZ(COLOR_1));
		int color2 = p.color(UI.valueX(COLOR_2), UI.valueY(COLOR_2), UI.valueZ(COLOR_2));
		int color3 = p.color(UI.valueX(COLOR_3), UI.valueY(COLOR_3), UI.valueZ(COLOR_3));
		
		p.g.translate(p.width * 0.25f, p.height / 2);
		Gradients.linear(p.g, p.width/2f, p.height, color1, color2);
		p.g.translate(p.width * 0.5f, 0);
		Gradients.linear(p.g, p.width/2f, p.height, color2, color3);
	}
	
}


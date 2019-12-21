package com.haxademic.demo.ui;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.text.ValidateUtil;
import com.haxademic.core.ui.UITextInput;

public class Demo_UITextInput 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean debugMode = true;
	protected ArrayList<UITextInput> textInputs = new ArrayList<UITextInput>();
	
	public void firstFrame () {
		int inputX = 100;
		int inputY = 30;
		int inputW = 300;
		for (int i = 0; i < 6; i++) {
			int inputH = 20 + 20 * i;
			textInputs.add(new UITextInput("demo"+i, DemoAssets.fontOpenSansPath, PTextAlign.LEFT, inputX, inputY, inputW, inputH));
			inputY += inputH + 20;
		}
		
		// make last input only numeric characters
		textInputs.get(textInputs.size()-1).filter(ValidateUtil.NOT_NUMERIC);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debugMode = !debugMode;
	}
	
	public void drawApp() {
		p.background(0);
		for (int i = 0; i < textInputs.size(); i++) {
			textInputs.get(i).update(p.g);
		}
	}
	
}

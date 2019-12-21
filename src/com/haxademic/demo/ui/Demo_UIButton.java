package com.haxademic.demo.ui;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.ui.IUIControl;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;
import com.haxademic.core.ui.UIButton.IUIButtonDelegate;

public class Demo_UIButton 
extends PAppletHax
implements IUIButtonDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean activeButtons = true;
	protected ArrayList<IUIControl> mouseables;

	public void setupFirstFrame () {
		int buttonX = 40;
		int buttonY = 10;
		int buttonW = 200;
		int buttonH = 40;
		mouseables = new ArrayList<IUIControl>();
		mouseables.add( new UIButton( this, "1", buttonX, buttonY +=  0, buttonW, buttonH, false ) );
		mouseables.add( new UIButton( this, "2", buttonX, buttonY += 60, buttonW, buttonH, false ) );
		mouseables.add( new UIButton( this, "3", buttonX, buttonY += 60, buttonW, buttonH - 10, true ) );
		
		// add a test slider to compare style
		UI.addSlider("TEST SLIDER", 255, 0, 255, 0.5f);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') activeButtons = !activeButtons;
	}
	
	public void drawApp() {
		p.background(0);
		
		// draw buttons
		if(activeButtons) {
			for( int i=0; i < mouseables.size(); i++ ) mouseables.get(i).draw(p.g);
		}
	}
	
	public void clicked(UIButton button) {
		P.out(button.id(), button.value());
	}
	
}

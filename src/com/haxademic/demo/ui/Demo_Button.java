package com.haxademic.demo.ui;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.ui.Button;
import com.haxademic.core.ui.Button.IButtonDelegate;
import com.haxademic.core.ui.IMouseable;

public class Demo_Button 
extends PAppletHax
implements IButtonDelegate {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean activeButtons = true;
	protected ArrayList<IMouseable> mouseables;

	public void setupFirstFrame () {
		int buttonX = 10;
		int buttonY = 10;
		int buttonW = 200;
		int buttonH = 40;
		mouseables = new ArrayList<IMouseable>();
		mouseables.add( new Button( this, "1", buttonX, buttonY +=  0, buttonW, buttonH ) );
		mouseables.add( new Button( this, "2", buttonX, buttonY += 60, buttonW, buttonH ) );
		mouseables.add( new Button( this, "3", buttonX, buttonY += 60, buttonW, buttonH - 10 ) );
//		_mouseables.add( new TextButton( p, "double syllable", "2", 300, 520, 200, 50 ) );
//		_mouseables.add( new TextButton( p, "any word", "1", 560, 420, 200, 50 ) );
//		_mouseables.add( new TextButton( p, "line end", "2", 560, 520, 200, 50 ) );

	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') activeButtons = !activeButtons;
	}
	
	public void drawApp() {
		p.background(0);
		
		// draw buttons
		for( int i=0; i < mouseables.size(); i++ ) mouseables.get(i).update(p.g, p.mouseX, p.mouseY);
	}
	
	public void clicked(String buttonId) {
		P.out(buttonId);
	}
	
}

package com.haxademic.core.ui;

import java.awt.Rectangle;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.event.MouseEvent;

public class Button
implements IMouseable {
	
	public interface IButtonDelegate {
		public void clicked(String buttonId); 		
	}
	 
	protected IButtonDelegate delegate;
	protected String id;
	protected Rectangle rect;
	protected Boolean over;
	protected Boolean pressed;
	
	public Button( IButtonDelegate delegate, String id, int x, int y, int w, int h ) {
		this.delegate = delegate;
		this.id = id;
		rect = new Rectangle( x, y, w, h );
		over = false;
		pressed = false;
		
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
	}
	
	public String id() {
		return id;
	}
	
	public void update(PGraphics pg, int mouseX, int mouseY) {
		// draw
		pg.noStroke();
		if( pressed == true ) {
			pg.fill( 255, 255, 127 );
		} else if( over == true ) {
			pg.fill( 255, 255, 255 );
		} else {
			pg.fill( 127, 255, 127);
		}
		pg.rect( rect.x, rect.y, rect.width, rect.height );
	}
	
	public void mouseEvent(MouseEvent event) {
		int mouseX = event.getX();
		int mouseY = event.getY();

		switch (event.getAction()) {
			case MouseEvent.PRESS:
				pressed = rect.contains(mouseX, mouseY);
				break;
			case MouseEvent.RELEASE:
				if(pressed && over) delegate.clicked(id);
				pressed = false;
				break;
			case MouseEvent.MOVE:
				over = rect.contains(mouseX, mouseY);
				break;
			case MouseEvent.DRAG:
				over = rect.contains(mouseX, mouseY);
				break;
		}
	}
	
}

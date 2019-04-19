package com.haxademic.core.ui;

import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.DemoAssets;

import processing.core.PFont;
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
	
	public Button(IButtonDelegate delegate, String id, int x, int y, int w, int h) {
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
		// background
		if(over) pg.fill(ColorsHax.BUTTON_BG_HOVER);
		else if(pressed) pg.fill(ColorsHax.BUTTON_BG_PRESS);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.noStroke();
		pg.rect(rect.x, rect.y, rect.width, rect.height);

		// outline
		pg.strokeWeight(1);
		if(over || pressed) pg.stroke(ColorsHax.BUTTON_OUTLINE_HOVER);
		else pg.stroke(ColorsHax.BUTTON_OUTLINE);
		pg.noFill();
		pg.rect(rect.x, rect.y, rect.width, rect.height);

		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, rect.height * 0.6f);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		// text label
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(id, rect.x, rect.y - 4, rect.width, rect.height);
//		pg.text(key + ": " + value, x + 4, y + 0, w, 20);
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

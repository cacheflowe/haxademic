package com.haxademic.core.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.PrefToText;
import com.haxademic.core.system.SystemUtil;

import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class UITextInput
implements IUIControl {
	
	public static UITextInput ACTIVE_INPUT = null;
	protected String id;
	protected Rectangle rect;
	protected int align;
	protected boolean mouseHovered;
	protected boolean mousePressed;
	protected boolean active = true;
	protected String value;
	protected String fontFile;
	protected float fontSize;
	protected boolean focused;
	protected float cursorX;
	protected float cursorPadding;
	protected float textY;
	protected int padX;
	protected String filter = null;
	protected boolean saves = false;

	public UITextInput(String id, String fontFile, int align, int x, int y, int w, int h ) {
		this(id, "", fontFile, align, x, y, w, h, false);
	}
	
	public UITextInput(String id, String value, String fontFile, int align, int x, int y, int w, int h, boolean saves) {
		this.id = id;
		this.value = (saves) ? PrefToText.getValueS(id, value) : value;
		this.fontFile = fontFile;
		this.align = align;
		fontSize = h * 0.5f;
		padX = 10;
		cursorPadding = Math.round( fontSize / 6f ); 
		rect = new Rectangle(x, y, w, h);
		textY = rect.y + rect.height * 0.5f - fontSize * 0.3f;
		mouseHovered = false;
		mousePressed = false;
		focused = false;
		this.saves = saves;
		P.p.registerMethod(PRegisterableMethods.mouseEvent, this);
		P.p.registerMethod(PRegisterableMethods.keyEvent, this);
	}
	
	public static boolean active() {
		return ACTIVE_INPUT != null;
	}
	
	public String id() {
		return id;
	}
	
	public String valueString() {
		return value;
	}
	
	public void set(String newText) {
		value = newText;
		updateStore();
	}
	
	public boolean hovered() {
		return mouseHovered;
	}
	
	public void blur() {
		focused = false;
	}
	
	public void focus() {
		focused = true;
	}
	
	public void reset() {
		value = "";
		updateStore();
	}
	
	public int x() {
		return rect.x;
	}
	
	public int y() {
		return rect.y;
	}
	
	public int width() {
		return rect.width;
	}
	
	public int height() {
		return rect.height;
	}
	
	public String filter() {
		return filter;
	}
	
	public void filter(String filter) {
		this.filter = filter;
	}
	
	public void draw( PGraphics pg ) {
		pg.pushMatrix();
		PG.setDrawCorner(pg);
		
		// outline
		pg.noStroke();
		pg.fill(ColorsHax.BUTTON_OUTLINE);
		pg.rect(rect.x, rect.y, rect.width, rect.height);

		// draw input background
		if( mousePressed == true || focused == true ) {
			pg.fill(ColorsHax.BUTTON_BG_PRESS);
		} else if( mouseHovered == true ) {
			pg.fill(ColorsHax.BUTTON_BG_HOVER);
		} else {
			pg.fill(ColorsHax.BUTTON_BG);
		}
		pg.rect(rect.x+1, rect.y+1, rect.width-2, rect.height-2);

		// set font on context
		boolean isUIComponent = (rect.height == IUIControl.controlH);
		if(isUIComponent) {  	// lock to UI size if we're a UI component
			IUIControl.setFont(pg);
			pg.fill(ColorsHax.BUTTON_TEXT);
		} else {
			PFont font = FontCacher.getFont(fontFile, fontSize);
			FontCacher.setFontOnContext(pg, font, ColorsHax.BUTTON_TEXT, 1f, align, PTextAlign.CENTER);
		}
		
		// get text width for cursor and to "scroll" text
		String displayText = value;
		float textW = pg.textWidth(displayText);
		int maxTextW = rect.width - padX * 2;
		while(textW > maxTextW) {
			displayText = displayText.substring(1);	// shift chars off the front of the text
			textW = pg.textWidth(displayText);
		}
		if(isUIComponent) {
			pg.text(displayText, rect.x + TEXT_INDENT, rect.y, rect.width, rect.height);
		} else {
			pg.text(displayText, rect.x + padX, rect.y - rect.height * 0.05f, rect.width, rect.height);
		}

		// draw blinking cursor
		cursorX = rect.x + padX + textW + cursorPadding;
		if(isUIComponent) cursorX -= 3;
		if(align == PTextAlign.CENTER) cursorX = rect.x + rect.width/2 + textW/2 + cursorPadding * 3f;
		if(focused == true) {
			pg.noStroke();
			pg.fill(ColorsHax.BUTTON_TEXT);
			if( P.p.millis() % 1000f > 500 ) pg.rect( cursorX, rect.y + rect.height * 0.25f, 2f, fontSize );
		}
		pg.popMatrix();
	}

	/////////////////////////////////////////
	// Mouse events
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		if(active == false) return;
		int mouseX = event.getX();
		int mouseY = event.getY();
		
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			mousePressed = rect.contains(mouseX,  mouseY);
			// if no textinputs are clicked, clear out ACTIVE_INPUT
			break;
		case MouseEvent.RELEASE:
			mousePressed = false;
			focused = rect.contains(mouseX, mouseY);
			ACTIVE_INPUT = null;
			if(focused) {
				SystemUtil.setTimeout(activeTimeout, 10);
			} else {
				if(saves) PrefToText.setValue(id, value);
			}
			break;
		case MouseEvent.MOVE:
			boolean currentlyHovered = rect.contains(mouseX,  mouseY);
			if(mouseHovered == true && currentlyHovered == false) P.p.cursor(P.ARROW);	// mouse out
			mouseHovered = currentlyHovered;
			if(mouseHovered) P.p.cursor(P.TEXT);										// mouse over
			break;
		case MouseEvent.DRAG:
			break;
		}
	}
	
	protected ActionListener activeTimeout = new ActionListener() {
		public void actionPerformed(ActionEvent e) { setActive(); }
	};
	protected void setActive() {
		ACTIVE_INPUT = this;
	}
	
	protected void updateStore() {
		P.store.setString(id, value);
	}

	/////////////////////////////////////////
	// Keyboard events
	/////////////////////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(active == false) return;
		if(ACTIVE_INPUT != this) return;
		if(e.getAction() == KeyEvent.PRESS) {
			char key = e.getKey();
			int keyCode = e.getKeyCode();
			// P.out(keyCode, PConstants.SHIFT);
			if(key == PConstants.BACKSPACE) {
				if(value.length() > 0) {
					value = value.substring( 0, value.length() - 1 );
					updateStore();
				}
			} else if(key == PConstants.RETURN || key == PConstants.ENTER || keyCode == PConstants.SHIFT || key == PConstants.TAB) {
				// do nothing for special keys
			} else {
				value += key;
				if(filter != null) value = value.replaceAll(filter, "");
				updateStore();
			}
		}
	}
	
	/////////////////////////////////
	// Vestigial IUIControl methods... unused for textfields
	/////////////////////////////////

	@Override
	public String type() {
		return IUIControl.TYPE_TEXTFIELD;
	}

	@Override
	public void set(float val) {
		
	}

	@Override
	public float value() {
		return 0;
	}

	@Override
	public float valueEased() {
		return 0;
	}

	@Override
	public float step() {
		return 0;
	}

	@Override
	public float valueMin() {
		return 0;
	}

	@Override
	public float valueMax() {
		return 0;
	}

	@Override
	public float toggles() {
		return 0;
	}

	@Override
	public float layoutW() {
		return 1;
	}

	@Override
	public void layoutW(float val) {
		
	}

	@Override
	public void update() {
		
	}

}

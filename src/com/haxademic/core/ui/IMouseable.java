package com.haxademic.core.ui;

import processing.core.PApplet;

public interface IMouseable {
	public void update( PApplet p );
	public String id();
	public Boolean checkPress( int mouseX, int mouseY );
	public Boolean checkRelease( int mouseX, int mouseY );
	public Boolean checkOver( int mouseX, int mouseY );
}

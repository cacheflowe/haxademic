package com.haxademic.app.haxvisual.viz;

public interface IVizModule 
{
	public void init();
	public void update();
	public void focus();
	public void handleKeyboardInput();
//	public void hasFocus();
	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount );
}

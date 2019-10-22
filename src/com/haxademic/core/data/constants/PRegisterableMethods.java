package com.haxademic.core.data.constants;

public class PRegisterableMethods {
	public static String pre = "pre";					// called just after beginDraw(), meaning that it can affect drawing.
	public static String draw = "draw";					// at the end of draw(), but before endDraw()
	public static String post = "post";					// after draw has completed and the frame is done. No drawing allowed.
	public static String mouseEvent = "mouseEvent";		// (MouseEvent e) 
	public static String keyEvent = "mouseEvent";		// (KeyEvent e) 
	public static String stop = "stop";					// Called to halt execution. Can be called by users, for instance movie.stop() will shut down a movie that's being played, or camera.stop() stops capturing video. server.stop() will shut down the server and shut it down completely. May be called multiple times.
	public static String dispose = "dispose";			// Called to free resources before shutting down. This should only be called by PApplet. The dispose() method is what gets called when the host applet is being shut down, so this should stop any threads, disconnect from the net, unload memory, etc.
	public static String pause = "pause"; 
	public static String resume = "resume"; 
}

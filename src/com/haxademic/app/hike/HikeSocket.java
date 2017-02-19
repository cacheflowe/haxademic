package com.haxademic.app.hike;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.net.WebSocketRelay;
import com.haxademic.core.ui.IMouseable;
import com.haxademic.core.ui.TextButton;

import processing.data.JSONObject;


public class HikeSocket 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ArrayList<IMouseable> _mouseables;
	
	protected WebSocketRelay _server;
	protected int _userFoundTime = -1;
	
	protected String hostName;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
	}
	
	public void setup() {
		super.setup();
		
		// fire up websocket server
		_server = new WebSocketRelay();
		_server.start();
		
		// build buttons
		_mouseables = new ArrayList<IMouseable>();
		_mouseables.add( new TextButton( p, "ACTIVE", "1", 20, 60, 180, 40 ) );
		_mouseables.add( new TextButton( p, "INACTIVE", "2", 20, 120, 180, 40 ) );
		_mouseables.add( new TextButton( p, "CAPTURE", "3", 20, 180, 180, 40 ) );

	}

	public void drawApp() {
		// reset drawing 
		p.background(0);
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		p.noStroke();
		
		if(p.frameCount > 20) {
			if(hostName == null) hostName = "Server: http://"+_server.localHost + ":" + _server.portStr;
			
			// show websocket address
			p.fill(255);
			p.textSize(14);
			p.text(hostName, 20, 20);
		}
		
		// draw buttons
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).update( p );
		}
	}
	
	public void sendSocketMessage(String service, String command, String type, String message) {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString("service", service);
	    jsonOut.setString("command", command);
	    jsonOut.setString("type", type);
	    jsonOut.setString("text", message);
	    _server.sendMessage(jsonOut.toString().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " "));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if (p.key == ' ') {
//			_kinectGrid.toggleDebugOverhead();
		}
	}

	public void mouseReleased() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			if( _mouseables.get(i).checkRelease( p.mouseX, p.mouseY ) ) {
				if(_mouseables.get(i).id() == "1") sendSocketMessage("process", "capture", null, null);
				if(_mouseables.get(i).id() == "2") sendSocketMessage("process", null, "user", "inactive");
				if(_mouseables.get(i).id() == "3") sendSocketMessage("process", null, "capture", "take some pictures!");
			}
		}
	}

	public void mousePressed() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkPress( p.mouseX, p.mouseY );
		}
	}

	public void mouseMoved() {
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkOver( p.mouseX, p.mouseY );
		}
	}

}


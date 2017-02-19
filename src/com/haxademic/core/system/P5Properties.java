package com.haxademic.core.system;

import java.io.IOException;
import java.util.Properties;

import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;

/**
 * simple convenience wrapper object for the standard
 * Properties class to return pre-typed data
 */
public class P5Properties 
extends Properties 
{
	private static final long serialVersionUID = 1L;
	protected PApplet p;
	
	public P5Properties(PApplet p) {
		super();
		this.p = p;
		loadPropertiesFile( FileUtil.getHaxademicDataPath() + "properties/run.properties" );
	}
	
	public void loadPropertiesFile( String file ) {
		try {
			load( p.createInput( file ) );
		} catch(IOException e) {
			DebugUtil.printErr("couldn't read run.properties config file...");
		}
	}
 
	// string helpers
	public String getString(String id, String defState) {
		return getProperty(id,defState);
	}
 
	// boolean helpers
	public synchronized Object setProperty(String id, boolean state) {
		return super.setProperty(id, ""+state);
	}
 
	public boolean getBoolean(String id, boolean defState) {
		return Boolean.parseBoolean(getProperty(id,""+defState));
	}
	
	// int helpers
	public synchronized Object setProperty(String id, int val) {
		return super.setProperty(id, ""+val);
	}
	
	public int getInt(String id, int defVal) {
		return Integer.parseInt(getProperty(id,""+defVal));
	}
 
	// float helpers
	public synchronized Object setProperty(String id, float val) {
		return super.setProperty(id, ""+val);
	}
	
	public float getFloat(String id, float defVal) {
		return new Float(getProperty(id,""+defVal)); 
  	}  
}
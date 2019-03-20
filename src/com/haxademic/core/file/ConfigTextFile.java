package com.haxademic.core.file;

public class ConfigTextFile {
	
	protected String path;
	protected String defaultValue;
	protected String[] lines;
	
	public ConfigTextFile(String path, String defaultValue) {
		this.path = path;
		this.defaultValue = defaultValue;
		createDir();
		loadFile();
	}
	
	// create directory if it doesn't exist
	protected void createDir() {
		String filePath = FileUtil.pathForFile(this.path);
		if(FileUtil.fileOrPathExists(filePath) == false) {
			FileUtil.createDir(filePath);
		}
	}
	
	// load a text file lines to String array
	protected void loadFile() {
		// load stored positions if file exists
		if(FileUtil.fileOrPathExists(this.path) == true) {
			lines = FileUtil.readTextFromFile(this.path); // p.loadStrings(this.path);
		} else {
			writeSingleLine(defaultValue);
		}
	}
	
	// write a multi-line text file
	public void writeFile(String[] value) {
		this.lines = value;
		FileUtil.writeTextToFile(path, String.join("\n", lines));
	}
	
	// helper to just write a single line
	public void writeSingleLine(String value) {
		writeFile( new String[] { value } );
	}
	
	// helper to return the first line as a String array 
	public String[] getSingleLineCSV() {
		return lines[0].split(",");
	}
}
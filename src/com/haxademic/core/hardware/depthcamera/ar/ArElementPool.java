package com.haxademic.core.hardware.depthcamera.ar;

import java.util.ArrayList;

public class ArElementPool {

	protected ArrayList<IArElement> elements;
	protected int curIndex = 0;
	
	public ArElementPool() {
		elements = new ArrayList<IArElement>();
	}
	
	public void addElement(IArElement newElement) {
		elements.add(newElement);
	}
	
	public ArrayList<IArElement> elements() {
		return elements;
	}
	
	public IArElement elementAt(int index) {
		return elements.get(index);
	}
	
	public IArElement nextArElement() {
		// loop to next element
		nextIndex();
		
		// keep checking the next element until we find a good, inactive AR element 
		int attempts = 0;
		while(elements.get(curIndex).isActive() == true && attempts < 20) {
			nextIndex();
			attempts++;
		}
			
		// return next element!
		if(elements.get(curIndex).isActive() == false) {
			return elements.get(curIndex);
		} else {
			// P.out("??????? No inactive AR elementfound");
			return null;
		}
	}
	
	protected void nextIndex() {
		curIndex++;
		if(curIndex >= elements.size()) curIndex = 0;
	}
		
}

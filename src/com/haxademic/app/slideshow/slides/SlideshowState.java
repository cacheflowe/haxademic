package com.haxademic.app.slideshow.slides;

public enum SlideshowState {
	SLIDE_INDEX ("slide-index");
	
	private final String id;
	public String id() { return id; }
	SlideshowState(String id) {
        this.id = id;
    }
}


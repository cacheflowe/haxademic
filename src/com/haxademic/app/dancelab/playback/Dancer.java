package com.haxademic.app.dancelab.playback;

import com.haxademic.core.system.FileUtil;

public enum Dancer {
	MORGAN (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-morgan/"),
	EVAN   (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-evan/"),
	MARIAN (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-marian/"),
	DAMIEN (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-damien/"),
	SARAH  (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-sarah/"),
	CORBIN (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-corbin/"),
	AMY	   (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-amy/"),
	CONNER (FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-conner/");
	
	public String path;
	
	Dancer(String path) {
		this.path = path;
	}
}

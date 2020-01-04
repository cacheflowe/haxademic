package com.haxademic.app.dancelab.playback;

import com.haxademic.core.file.FileUtil;

public enum Dancer {
	MORGAN (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-morgan/"),
	EVAN   (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-evan/"),
	MARIAN (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-marian/"),
	DAMIEN (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-damien/"),
	SARAH  (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-sarah/"),
	CORBIN (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-corbin/"),
	AMY	   (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-amy/"),
	CONNER (FileUtil.haxademicDataPath() + "video/dancelab/image-sequence-test-conner/");
	
	public String path;
	
	Dancer(String path) {
		this.path = path;
	}
}

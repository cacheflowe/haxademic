package com.haxademic.core.file;

import com.haxademic.core.system.shell.IScriptCallback;
import com.haxademic.core.system.shell.ScriptRunner;

public class ImageSequenceFilesToMovie
extends ScriptRunner {
	
	public ImageSequenceFilesToMovie(IScriptCallback delegate) {
		super("image-sequence-to-video", delegate);
	}
}

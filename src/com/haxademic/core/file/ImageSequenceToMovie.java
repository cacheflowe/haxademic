package com.haxademic.core.file;

import java.io.IOException;

import com.haxademic.core.app.P;
import com.haxademic.core.system.SystemUtil;

public class ImageSequenceToMovie {
	
	protected IScriptCallback delegate;

	public ImageSequenceToMovie() {
		
	}
	
	public void convertImagesPath(String imagesPath, IScriptCallback delegate) {
		this.delegate = delegate;
		try {
			runScript(imagesPath);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	protected void runScript(String imagesPath) throws InterruptedException {
		String scriptName = (SystemUtil.isOSX() == true) ? "image-sequence-to-video.sh" : "image-sequence-to-video.cmd";
		String scriptPath = FileUtil.getScript(scriptName);
		String[] command = (SystemUtil.isOSX() == true) ? 
				new String[]{"/bin/sh", "-c", scriptPath + " " + imagesPath} : 
				new String[]{"cmd.exe", "/C", scriptPath, imagesPath};
				
		P.println("Running shell command:");
		P.println(scriptPath + " " + imagesPath);
		
		// run script
        Process p;
		try {
			p = new ProcessBuilder(command)
			        .redirectErrorStream(true)
			        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
			        .start();
			p.waitFor();
			System.out.println("Exit value: " + p.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// callback
		if(delegate != null) delegate.scriptComplete();
	}

	public interface IScriptCallback {
		public void scriptComplete();
	}
}

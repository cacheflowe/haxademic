package com.haxademic.core.system.shell;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class ScriptRunner {
	
	protected IScriptCallback delegate;
	protected String scriptName;

	public ScriptRunner(String scriptName, IScriptCallback delegate) {
		this.scriptName = scriptName;
		this.scriptName += (SystemUtil.isOSX() == true) ? ".sh" : ".cmd";
		this.delegate = delegate;
	}
	
	public void runWithParams(Object ...args) {
		// create string args array
		String[] argz = new String[args.length];
		for (int i = 0; i < argz.length; i++) {
			argz[i] = (String) args[i];
		}
		
		// run script
		try {
			runScript(argz);
		} catch (InterruptedException e) {
			e.printStackTrace();
			P.out("Script failed (InterruptedException)");
		}		
	}
	
	protected void runScript(String[] args) throws InterruptedException {
		String scriptPath = FileUtil.getScript(scriptName);
		String[] command;
		if(SystemUtil.isOSX()) { 
			command = new String[]{"/bin/sh", "-c", scriptPath + " " + String.join(" ", args)};				// spaces in between args
		} else {
			command = new String[]{"cmd.exe", "/C", scriptPath};
			command = Stream.concat(Arrays.stream(command), Arrays.stream(args)).toArray(String[]::new);	// concat arrays via https://stackoverflow.com/a/23188881
		}
		
		P.out("Running shell command:");
		P.out(scriptPath + " " + Arrays.toString(args));
		
		// run script
        Process p;
		try {
			p = new ProcessBuilder(command)
			        .redirectErrorStream(true)
			        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
			        .start();
			p.waitFor();
			P.out("Exit value: " + p.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
			P.out("Script failed");
		}
		
		// callback
		if(delegate != null) delegate.scriptComplete();
	}

}

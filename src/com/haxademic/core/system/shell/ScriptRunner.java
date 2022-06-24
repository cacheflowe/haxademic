package com.haxademic.core.system.shell;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class ScriptRunner {
	
	protected IScriptCallback delegate;
	protected String scriptName;
	protected Process process;

	public ScriptRunner(String scriptName, IScriptCallback delegate) {
		this.scriptName = scriptName;
		this.scriptName += (SystemUtil.isOSX() == true) ? ".sh" : ".cmd";
		this.delegate = delegate;
	}
	
	public Process process() {
		return process;
	}
	
	public void runWithParams(Object ...args) {
		// create string args array
		String[] argz = new String[args.length];
		for (int i = 0; i < argz.length; i++) {
			if(args[i] instanceof String) {
				argz[i] = (String) args[i];
			} else {
				DebugUtil.printErr("[ERROR]: ScriptRunner arg is not a String: " + args[i]);
				argz[i] = "";
			}
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
		P.out(String.join(" ", command));
		
		// run script
		try {
			process = new ProcessBuilder(command)
			        .redirectErrorStream(true)
			        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
			        .start();
			process.waitFor();
			
			if(process.isAlive() == false) {
				P.out("Exit value: " + process.exitValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
			P.out("Script failed");
		}
		
		// callback
		if(delegate != null) delegate.scriptComplete();
	}

}

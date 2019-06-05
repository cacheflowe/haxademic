package com.haxademic.demo.system;

import java.io.IOException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class Demo_WindowsComputerRestart
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void setup() {
		super.setup();		
	}

	public void drawApp() {
		p.background(0);
		p.text(p.frameCount, 20, 30);
		if(p.frameCount == 90) {
			// "cmd.exe /C";
			String command = "SHUTDOWN -r -t 10";
	        Process p;
			try {
				p = new ProcessBuilder(command)
				        .redirectErrorStream(true)
				        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
				        .start();
				P.out("Exit value: " + p.exitValue());
			} catch (IOException e) {
				e.printStackTrace();
				P.out("Script failed");
			}

		}
	}
}

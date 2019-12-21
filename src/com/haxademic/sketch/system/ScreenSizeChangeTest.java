package com.haxademic.sketch.system;

import java.awt.AWTEvent;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.math.MathUtil;

public class ScreenSizeChangeTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Rectangle screenSize;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screenSize = ge.getMaximumWindowBounds();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event) {
				// take a look at http://stackoverflow.com/questions/10123735/get-effective-screen-size-from-java
				
				Rectangle newSize = ge.getMaximumWindowBounds();
				if (newSize.width != screenSize.width || newSize.height != screenSize.height) {
					screenSize.setSize(newSize.width, newSize.height);
					resize();
				}
			}
		}, AWTEvent.PAINT_EVENT_MASK);

	}

	protected void resize() {
		P.println("Screen resized");
	}

	public void drawApp() {
		background(0);
		if(p.frameCount % 120 == 0) {
			p.getSurface().setLocation(0, 0);
			p.getSurface().setSize(p.width, p.height + MathUtil.randRange(-10, 10));
		}
//		DebugView.setValue("screenSize.width", screenSize.width);
//		DebugView.setValue("screenSize.height", screenSize.height);
//		DebugView.setValue("displayWidth", displayWidth);
//		DebugView.setValue("displayHeight", displayHeight);
//		if(p.frameCount % 300 == 0) {
//			DebugView.setValue("Toolkit.getDefaultToolkit().getScreenSize().width", Toolkit.getDefaultToolkit().getScreenSize().width);
//			DebugView.setValue("Toolkit.getDefaultToolkit().getScreenSize().height", Toolkit.getDefaultToolkit().getScreenSize().height);
//		}
	}
}

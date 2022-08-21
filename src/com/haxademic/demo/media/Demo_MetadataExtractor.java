package com.haxademic.demo.media;

import com.drew.metadata.Metadata;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.MediaMetaData;

public class Demo_MetadataExtractor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Metadata metadata;

	protected void firstFrame() {
		MediaMetaData.printAllForMedia(P.path(DemoAssets.movieFractalCubePath), false);
		MediaMetaData.printAllForMedia(P.path("haxademic/images/space/sun-nasa.jpg"), true);
		MediaMetaData.printAllForMedia(P.path("images/_sketch/ann0912a.jpg"), true);
		MediaMetaData.printAllForMedia(P.path("images/_sketch/sheraton-street-view.png"), true);
	}

	protected void drawApp() {
		p.background(0);
	}	

}

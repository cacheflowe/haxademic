package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.video.Movie;


public class KinectChromaTest 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Movie _movie;
	protected float[] _cropProps = null;

	PGraphics _chromaBuffer;
	
	protected String thresholdSensitivity = "thresholdSensitivity";
	protected String smoothing = "smoothing";
	protected String colorToReplaceR = "colorToReplaceR";
	protected String colorToReplaceG = "colorToReplaceG";
	protected String colorToReplaceB = "colorToReplaceB";

	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);

		_chromaBuffer = p.createGraphics(p.width, p.height, P.P3D);

		UI.addSlider(thresholdSensitivity, 0.75f, 0, 1, 0.01f, false);
		UI.addSlider(smoothing, 0.26f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceR, 0.29f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceG, 0.93f, 0, 1, 0.01f, false);
		UI.addSlider(colorToReplaceB, 0.14f, 0, 1, 0.01f, false);
		
		// build movie layer		
		_movie = new Movie( p, FileUtil.getPath("video/nike/nike-hike-gray-loop.mov") );

		_movie.jump(0);
		_movie.loop();
		_movie.play();

	}

	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		// reset drawing 
		p.background(0);
		PG.resetGlobalProps( p );
		PG.setDrawCorner(p);
		PG.setColorForPImage(p);
		p.noStroke();
		
		// draw webcam to buffer & apply chroma filter
		_chromaBuffer.beginDraw();
		_chromaBuffer.clear();
		_chromaBuffer.image(depthCamera.getRgbImage(), 0, 0);
		_chromaBuffer.endDraw();
		
		ChromaColorFilter.instance(p).setColorToReplace(UI.value(colorToReplaceR), UI.value(colorToReplaceG), UI.value(colorToReplaceB));
		ChromaColorFilter.instance(p).setSmoothing(UI.value(smoothing));
		ChromaColorFilter.instance(p).setThresholdSensitivity(UI.value(thresholdSensitivity));
		ChromaColorFilter.instance(p).applyTo(_chromaBuffer);
		
		// draw movie
		if(_movie.width > 1) {
			_cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width, p.height, _movie.width, _movie.height, true);
			PG.setPImageAlpha(p, 1);
			p.image(_movie, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
		}

		// draw chroma'd cam
		p.image(_chromaBuffer, 0, 0);
	}
}


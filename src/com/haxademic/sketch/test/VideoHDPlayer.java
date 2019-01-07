package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.video.Movie;

public class VideoHDPlayer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie _movie;
	protected Movie _movieColor;
	protected float[] _cropProps = null;

	PGraphics _maskImage;
	PShader _maskShader;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "1920" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1080" );
	}

	public void setup() {
		super.setup();		
		_movie = new Movie( p, FileUtil.getFile("video/nike/nike-hike-gray-loop.mov") );
		_movieColor = new Movie( p, FileUtil.getFile("video/nike/nike-hike-color-loop.mov") );

		_movie.jump(0);
		_movie.loop();
		_movie.play();

		_movieColor.jump(0);
		_movieColor.loop();
		_movieColor.play();

		_maskImage = createGraphics(p.width, p.height, P2D);
		_maskImage.noSmooth();

		_maskShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/mask.glsl"));
		_maskShader.set("mask", _maskImage);


	}

	public void drawApp() {
		// reset drawing 
		p.background(0);
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		p.noStroke();

		// update mask
		_maskImage.beginDraw();
		_maskImage.background(0);
		if (mouseX != 0 && mouseY != 0) {  
			_maskImage.noStroke();
			_maskImage.fill(255, 0, 0);
			_maskImage.ellipse(mouseX, mouseY, 450, 450);
		}
		_maskImage.endDraw();
		_maskShader.set("mask", _maskImage);


		// draw movie
		if(_movie.width > 1 && _movieColor.width > 1) {
			_cropProps = ImageUtil.getOffsetAndSizeToCrop(p.width, p.height, _movie.width, _movie.height, true);
			DrawUtil.setPImageAlpha(p, 1);
			p.image(_movie, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			p.shader(_maskShader);
			p.image(_movieColor, _cropProps[0], _cropProps[1], _cropProps[2], _cropProps[3]);
			p.resetShader();
		}

	}

}

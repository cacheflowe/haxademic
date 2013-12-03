package com.haxademic.sketch.test;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.ImageHistogramFilter;
import com.haxademic.core.render.VideoFrameGrabber;
import com.haxademic.core.system.FileUtil;


@SuppressWarnings("serial")
public class ChromaKeyTest
extends PAppletHax  
{
	PImage _image;
	PImage _dest;
	PImage _destClouds;
	PGraphics _imageGfx;
	
	VideoFrameGrabber _videoFrames;
	
	PGraphics _bg;
	PShader _clouds;
	PShader _fxaa;
	PShader _chroma;

	ImageHistogramFilter _histogramFilter;
	ImageHistogramFilter _histogramFilterClouds;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "1920" );
		_appConfig.setProperty( "height", "1080" );
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "600" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void setup() {
		super.setup();
		_image = p.loadImage( FileUtil.getHaxademicDataPath() + "images/green-screen-2.png" );
		_dest = new PImage( _image.width, _image.height, P.ARGB );
		_imageGfx = p.createGraphics(p.width, p.height, P.P2D);

		_fxaa = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/fxaa.glsl" ); 
		_fxaa.set("resolution", 1f, (float)(p.width/p.height));
		
		_chroma = loadShader( FileUtil.getHaxademicDataPath()+"shaders/filters/chroma-gpu.glsl" );
		_chroma.set("thresholdSensitivity", 0.5f);
		_chroma.set("smoothing", 0.1f);
		_chroma.set("colorToReplace", 0.2f,0.8f,0.2f);
		
		_bg = p.createGraphics(p.width, p.height, P.OPENGL);
		_destClouds = new PImage( _bg.width, _bg.height, P.ARGB );
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/clouds-iq.glsl" ); 

		_histogramFilter = new ImageHistogramFilter( _image.width, _image.height, 1 );
		_histogramFilterClouds = new ImageHistogramFilter( _bg.width, _bg.height, 2 );
		
		_videoFrames = new VideoFrameGrabber(p, "/Users/cacheflowe/Documents/workspace/plasticsoundsupply/resources/_releases/PSS020 - ambient compilation/commercial/raw-footage/PSS_Ultrasoft_BRoll_1080_h264.mov", 30, 0);
	}

	public void drawApp() {
		p.background(255,0,0);
		
		_clouds = loadShader( FileUtil.getHaxademicDataPath()+"shaders/textures/clouds-iq.glsl" ); 
		_clouds.set("resolution", 1f, (float)(p.width/p.height));
		_clouds.set("time", p.frameCount * 0.01f);
		_clouds.set("mouse", (float)mouseX/p.width, (float)mouseY/p.height - 0.5f);		

		_bg.beginDraw();
		_bg.filter(_clouds);
		_bg.endDraw();
		_destClouds.copy(_bg, 0, 0, _bg.width, _bg.height, 0, 0, _bg.width, _bg.height);
		p.image( _destClouds, 0, 0);
		
		_videoFrames.setFrameIndex(10590 + (int)Math.floor(p.frameCount / 3f) + 1);
		_imageGfx.beginDraw();
		_imageGfx.image(_videoFrames.movie(), 0, 0, p.width, p.height);
		_chroma.set("thresholdSensitivity", (float)p.mouseX/(float)p.width);
		_chroma.set("smoothing", 0.2f);
		_chroma.set("colorToReplace", 0.1f,(float)p.mouseY/(float)p.height,0.1f);
		_imageGfx.filter(_chroma);
		_imageGfx.endDraw();
		
		p.image( _imageGfx, 0, 0);
		
		p.filter(_fxaa);
	}	

}

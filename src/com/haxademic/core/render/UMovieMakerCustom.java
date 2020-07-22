package com.haxademic.core.render;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_QUICKTIME_PNG;
import static org.monte.media.VideoFormatKeys.HeightKey;
import static org.monte.media.VideoFormatKeys.WidthKey;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.media.quicktime.QuickTimeWriter;

import processing.core.PGraphics;

/**
 * UMovieMaker is a replacement for Dan Shiffman's MovieMaker class,
 * which was discontinued for Processing 2.0 beta. It uses the Monte 
 * Media Library (http://www.randelshofer.ch/monte/) by Werner 
 * Randelshofer and can write Quicktime without being dependent on 
 * QTJava.zip. 

 * <p>UMovieMaker currently only supports Quicktime PNG output, although the
 * Monte library is capable of writing AVI and other formats as well.
 * The library syntax is intentionally similar to MovieMaker, although it does  
 * not re-create all the functions of that library. 

 * <p>UMovieMaker has not been tested extensively on multiple platforms, but
 * it has performed as intended when producing 1080p video on a Windows 7
 * setup. Please report bugs on GitHub: https://github.com/mariuswatz/modelbuilder  
 * 
 * @author Marius Watz
 * @author Cacheflowe (modifications)
 *
 */

public class UMovieMakerCustom {

	protected QuickTimeWriter qt=null;
	protected int frameCount=0, vt, w, h;
	protected Graphics2D gg=null;
	protected Format videoFormat;
	protected BufferedImage img=null;
	protected BufferedImage prevImg=null;
	protected int[] data=null;
	protected int[] prevData=null;
	protected int prevImgDuration=0;
	protected int duration=100;
	protected PGraphics pg;
	
	public UMovieMakerCustom(PGraphics pg, String filename, int w, int h, int fps) {
		this.pg=pg;
		try {
			this.w=w;
			this.h=h;
			qt=new QuickTimeWriter(new File(filename));

			Format format=new Format(
					EncodingKey, ENCODING_QUICKTIME_PNG, 
					DepthKey, 24);
			format=format.prepend(
					MediaTypeKey, MediaType.VIDEO, //
					FrameRateKey, new Rational(fps, 1),//
					WidthKey, w, //
					HeightKey, h);

			qt.addTrack(format);

			//		      buf=new Buffer();
			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			data=((DataBufferInt)img.getRaster().getDataBuffer()).getData();
			prevImg=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			prevData=((DataBufferInt)prevImg.getRaster().getDataBuffer()).getData();
			gg=img.createGraphics();
			System.out.println("vt "+vt+" "+img.getWidth());
			gg.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int framesRendered() {
		return frameCount;
	}

	public void addFrame() {
		try {
			// always write new frame. original version compares frames and doesn't write if the new image is exactly the sdame as the old image
			BufferedImage frame=(BufferedImage)pg.getImage();
			gg.drawImage(frame, 0, 0, w, h, null);
			if (prevImgDuration!=0) {
				qt.write(vt, prevImg, 1);
			}
			prevImgDuration=duration;
			System.arraycopy(data, 0, prevData, 0, data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void finish() {
		try {
			qt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}







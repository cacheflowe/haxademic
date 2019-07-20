package com.haxademic.demo.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.net.HttpRequest;

import processing.core.PConstants;
import processing.core.PImage;

public class Demo_LoadRemoteBufferedImage
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected HttpRequest request;
	protected PImage img;
	protected boolean busy = false;
	
	public void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		pg.endDraw();
		
		// keep loading
//		if(busy == false) makeRequest();
		if(busy == false) makeRequestReuseImg();
		
		// draw loaded img
		if(img != null) {
			ImageUtil.cropFillCopyImage(img, pg, false);
		}
		p.image(pg, 0, 0);
 	}
	
	protected void makeRequest() {
		// put blocking ImageIO.read() request on thread
		busy = true;
		new Thread(new Runnable() { public void run() {
			try {
				// load web image to BufferedImage and copy to PImage
				BufferedImage image = ImageIO.read(new URL("http://192.168.1.56:88/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=cacheflowe&pwd=cachecam1"));
				img = ImageUtil.bufferedToPImage(image);
				busy = false;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}}).start();
	}
	
	protected void makeRequestReuseImg() {
		// put blocking ImageIO.read() request on thread
		busy = true;
		new Thread(new Runnable() { public void run() {
			try {
				// load web image to BufferedImage and copy to PImage
				BufferedImage image = ImageIO.read(new URL("http://192.168.1.56:88/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=cacheflowe&pwd=cachecam1"));
				if(img == null) img = new PImage(image.getWidth(), image.getHeight(), PConstants.ARGB);
				img.loadPixels();
				int[] pixels = image.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
				System.arraycopy(pixels, 0, img.pixels, 0, P.min(pixels.length, img.pixels.length));	// protect against out of bounds??
				img.updatePixels();
				busy = false;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}}).start();
	}
}

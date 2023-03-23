package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.image.Base64Image;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class SecondScreenViewer {

	public PImage srcBuffer;
	public PImage scaledBuffer;
	public float windowScale;
	public SecondScreenViewerWindow viewerWindow = null;
	public boolean active = true;
	
	public SecondScreenViewer(PGraphics buffer, float scale) {
		srcBuffer = buffer;
		windowScale = scale;
		scaledBuffer = ImageUtil.newImage(P.round(buffer.width * windowScale), P.round(buffer.height * windowScale));
		P.p.registerMethod("post", this);	 // update texture to 2nd window after main draw() execution
	}
	
	public void post() {
		if(viewerWindow == null && P.p.frameCount >= 10) viewerWindow = new SecondScreenViewerWindow();
		if(active == true && viewerWindow != null) {
			scaledBuffer.copy(srcBuffer, 0, 0, srcBuffer.width, srcBuffer.height, 0, 0, scaledBuffer.width, scaledBuffer.height);
			viewerWindow.setImage(scaledBuffer);
			viewerWindow.setMousePos(P.p.mouseX * windowScale, P.p.mouseY * windowScale);
		}
	}
	
	class SecondScreenViewerWindow extends PAppletHax {

		SecondScreenViewer parent;
		public PImage parentScreen;
		public PImage cursor;
		public float mouseX;
		public float mouseY;

		public SecondScreenViewerWindow() {
			runSketch(new String[] {"SecondScreenViewerWindow"}, this);
		}

		public void settings() {
			size(scaledBuffer.width, scaledBuffer.height, PRenderers.P3D);
//			noSmooth();
			cursor = Base64Image.decodePImageFromBase64("iVBORw0KGgoAAAANSUhEUgAAAGoAAAC1CAMAAACAl5pfAAAAYFBMVEVHcEyRkZEQEBAAAAB1dXVISEgmJib///8cHBwGBgYoKCj9/f3////T09O8vLzl5eX19fVubm4yMjJCQkJXV1eJiYmhoaEvLy+YmJjY2Ng6Ojqurq7Hx8f19fXo6OheXl4dpwGJAAAAIHRSTlMAbe//ibfYBOP5//7/7u3x9vP9+fbv7s/kJcVRNwoVoJWYs8AAAAPzSURBVHgBvM2FiURRAASw+679t3uGDSwamJcG8jXUNC/LvI6YtuXfftSnaVlGXecy7LqWvPpVXv0qr36VV7vKq1/l1a7yald5tau82lVe7SqvdpVXuypcUd3P3b2iettXVO0rq/KVVfnKqnxlVb6yKl9Zla+syldW5Sur8pVV+cqqfGVVvrIqX58VX17xBRVfUNkFFVxQwQUVXFDBpRVcWsGlFVxawcWVX1755ZVfXvkFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DRBRVcUMEFFVxQwQUVXFDBBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8QUVX1DxBRVfUPEFFV9Q8WWVX1799HIeWo6DMBTF3cBx7/3/v3L7xsqGvMXImXd62gWPuLKmZXmWFUVZam1kBc8sBqpSj3Urkrpu2raquq6Xryw3VPsA4VAWf+y2zOCIGoPr8dxQlQMqdUPl6jrKByiUL0S111GRI6q8vq3pvyiddxm+gvMQx2nq+1LK9yC5gGI/z6pSGSqMVTxl26d1WRbPC8PjiKL550riY0e2oFZQJdjWLOyCUdXfD6xNT/3NdgOqO5WKthXyUT352rcvz9bkxHBRVAmGhRTn0wsTlVFSoHpwBWMGiiza6vpO11FAB6oA24rcUdrQ+2ogQplcRwFzK9SLQ2eUMqGqf0mk3lNXFCWhbWVUhE6ompKiE5XzREhQZpJITxYwV7BfQRESXawXWInwuI5qKGmgrzCJsDnr/TKqVS+32sf7K6jJSfcuoipKSn83ot1OhOk1VEdJ/t+WN4Bt5UiEANU/kR41tTiKEKBySpJkkT4QIbmn3qxR4zuSCM9taYYIZUBjFs0WPFIxGr8EpEciTuMHKMN7pvci1KQwBmsUOI1IhKOtCKUFiSdCgArxyxrU+O1QeGV8EVIUPPNEhJ2rCKUFCYgQTEAAhct1PVEZbvwQhe6IsQjxBARQaQJQSIR4AjKgfKjmRHInIJ82KBS+CA/SNnAm7gS0+YSEE3MnoP3XJ6SAdGPjX8NwFTbx7USYCGawCOnsFzIQQISg3nmZGRMQCEOEw71/nl3jCYgf68bPT8KcgHDsRQgaP4iLCDVo/CBuIgQTEIiLCDUUoUP4ExCOXePXmi9C/gT0UREu94ow/zoRKnAFPytC3SoV3CNCPAFlNeHgW2Ve4+/Ihnhywo2/bAiIJww8AeVPHND2mSLsKhOI6oIvQpx0ETclxKB5FbdlAxx57OLORO9AsSduzmTeUAS6L1uEJH6YfPDfEJEMi/hQEskvBYd6T28tBWCnaBUfT+j/LIVNWOY7PppBCtOtd0oAAAAASUVORK5CYII=");
		}

		public void mouseClicked() {
			super.mouseClicked();
			active = !active;
		}
		
		public void setImage(PImage img) {
			parentScreen = img;
		}

		public void setMousePos(float x, float y) {
			mouseX = x;
			mouseY = y;
		}
		
		public void drawApp() {
			if(frameCount == 2) surface.setResizable(true);
			if(active == true) {
				fill(255);
				noStroke();
				// copy screen
				if(parentScreen != null && frameCount % 2 == 0) {
					ImageUtil.cropFillCopyImage(parentScreen, g, true);
				}
				// show mouse
				fill(255);
				stroke(0);
				image(cursor, mouseX, mouseY, 12, 20);
			} else {
				background(255, 0, 0);
				fill(255);
				textAlign(P.CENTER, P.CENTER);
				text("Screen viewer paused. Click to activate", 0, 0, width, height);
			}
		}

		public void exit() {
			dispose();
		}

		public void moveToTop() {
			surface.setAlwaysOnTop(true);
			surface.setAlwaysOnTop(false);
		}
	}
}

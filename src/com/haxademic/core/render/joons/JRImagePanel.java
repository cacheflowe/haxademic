package com.haxademic.core.render.joons;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.sunflow.core.Display;
import org.sunflow.image.Color;

import processing.core.PImage;

@SuppressWarnings("serial")
public class JRImagePanel extends JPanel implements Display {
	
    private static final int[] BORDERS = {Color.RED.toRGB(),
        Color.GREEN.toRGB(), Color.BLUE.toRGB(), Color.YELLOW.toRGB(),
        Color.CYAN.toRGB(), Color.MAGENTA.toRGB()};
    private BufferedImage image;
    private float xo, yo;
    private float w, h;
    private long repaintCounter;

    public JRImagePanel() {
        setPreferredSize(new Dimension(640, 480));
        image = null;
        xo = yo = 0;
        w = h = 0;
    }
    
    @Override
    public synchronized void imageBegin(int w, int h, int bucketSize) {
        if (image != null && w == image.getWidth() && h == image.getHeight()) {
            // dull image if it has same resolution (75%)
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int rgba = image.getRGB(x, y);
                    image.setRGB(x, y, ((rgba & 0xFEFEFEFE) >>> 1) + ((rgba & 0xFCFCFCFC) >>> 2));
                }
            }
        } else {
            // allocate new framebuffer
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            // center
            this.w = w;
            this.h = h;
            xo = yo = 0;
        }
        repaintCounter = System.nanoTime();
        repaint();
    }

    @Override
    public synchronized void imagePrepare(int x, int y, int w, int h, int id) {
        int border = BORDERS[id % BORDERS.length] | 0xFF000000;
        for (int by = 0; by < h; by++) {
            for (int bx = 0; bx < w; bx++) {
                if (bx == 0 || bx == w - 1) {
                    if (5 * by < h || 5 * (h - by - 1) < h) {
                        image.setRGB(x + bx, y + by, border);
                    }
                } else if (by == 0 || by == h - 1) {
                    if (5 * bx < w || 5 * (w - bx - 1) < w) {
                        image.setRGB(x + bx, y + by, border);
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public synchronized void imageUpdate(int x, int y, int w, int h, Color[] data, float[] alpha) {
        for (int j = 0, index = 0; j < h; j++) {
            for (int i = 0; i < w; i++, index++) {
                image.setRGB(x + i, y + j, data[index].copy().mul(1.0f / alpha[index]).toNonLinear().toRGBA(alpha[index]));
            }
        }
        repaint();
    }

    @Override
    public synchronized void imageFill(int x, int y, int w, int h, Color c, float alpha) {
        int rgba = c.copy().mul(1.0f / alpha).toNonLinear().toRGBA(alpha);
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                image.setRGB(x + i, y + j, rgba);
            }
        }
        fastRepaint();
    }

    @Override
    public void imageEnd() {
        repaint();
    }

    private void fastRepaint() {
        long t = System.nanoTime();
        if (repaintCounter + 125000000 < t) {
            repaintCounter = t;
            repaint();
        }
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }
        int x = Math.round(xo + (getWidth() - w) * 0.5f);
        int y = Math.round(yo + (getHeight() - h) * 0.5f);
        int iw = Math.round(w);
        int ih = Math.round(h);
        int x0 = x - 1;
        int y0 = y - 1;
        int x1 = x + iw + 1;
        int y1 = y + ih + 1;
        g.setColor(java.awt.Color.WHITE);
        g.drawLine(x0, y0, x1, y0);
        g.drawLine(x1, y0, x1, y1);
        g.drawLine(x1, y1, x0, y1);
        g.drawLine(x0, y1, x0, y0);
        g.drawImage(image, x, y, iw, ih, java.awt.Color.BLACK, this);
    }
    
    public PImage getInversedImage(){
        int iw = Math.round(w);
        int ih = Math.round(h);
		PImage inversed = new PImage(iw, ih);
		for(int i=0; i<ih; i++){
			for(int j=0; j<iw; j++){
				//PApplet.println(i);
				//PApplet.println(j);
				inversed.pixels[(ih-i-1)*iw+j] = image.getRGB(j, i);;
			}
		}
		return inversed;
    }    
}
package com.haxademic.sketch.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PAppletTransparentTest
extends PApplet {
	public static void main(String args[]) { PApplet.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// from: https://discourse.processing.org/t/transparent-translucent-window-drawing-graphics-on-desktop-how-to-capture-mouse-and-keyboard/1390/3
	
	public void settings() {
		super.settings();
		size(400, 400, PRenderers.JAVA2D);
	}

	//Declare Gobal Variables
	PGraphics pg;
	JFrame frame;
	JPanel panel;
	PApplet applet = this;

	public void setup()
	{
		super.setup();


		frame = (JFrame)((PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
		frame.removeNotify();
		frame.setUndecorated(true);
		frame.setLayout(null);
		frame.addNotify();
		frame.setAlwaysOnTop(true);

		pg = createGraphics(width, height);

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics graphics) {
				if (graphics instanceof Graphics2D) {
					Graphics2D g2d = (Graphics2D) graphics;
					g2d.drawImage(pg.image, 0, 0, null);
				}
			}
		};

		frame.setContentPane(panel);
		panel.setFocusable(true);
		panel.setFocusTraversalKeysEnabled(false);
		panel.requestFocus();
		panel.requestFocusInWindow();

		MouseAdapter mA = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				mousePressed = true;
				applet.mousePressed();
			}
			public void mouseReleased(MouseEvent me) {
				mousePressed = false;
				applet.mouseReleased();
			}
		};

		panel.addMouseListener(mA);

		MouseAdapter mA2 = new MouseAdapter() {

			public void mouseDragged(MouseEvent me) {
				mouseX = MouseInfo.getPointerInfo().getLocation().x-frame.getLocation().x;
				mouseY = MouseInfo.getPointerInfo().getLocation().y-frame.getLocation().y;
				applet.mouseDragged();
			}
			public void mouseMoved(MouseEvent me) {
				mouseX = MouseInfo.getPointerInfo().getLocation().x-frame.getLocation().x;
				mouseY = MouseInfo.getPointerInfo().getLocation().y-frame.getLocation().y;
				applet.mouseMoved();
			}
		};

		panel.addMouseMotionListener(mA2);

		KeyListener kL = new KeyListener() {

			public void keyTyped(KeyEvent e) {
				key = e.getKeyChar();
				keyCode = e.getKeyCode();
				applet.keyTyped();
			}

			public void keyReleased(KeyEvent e) {
				key = e.getKeyChar();
				keyCode = e.getKeyCode();
				applet.keyReleased();
				keyPressed = false;
			}

			public void keyPressed(KeyEvent e) {
				key = e.getKeyChar();
				keyCode = e.getKeyCode();
				applet.keyPressed();
				keyPressed = true;
			}
		};

		panel.addKeyListener(kL);
	}

	public void draw() {
		pg.beginDraw();
		pg.background(0, 0);
		if (mousePressed) {
			pg.fill(0, 153, 204, 126);
		} else {
			pg.fill(0, 204, 153, 126);
		}
//		pg.ellipse(frameCount % 100, frameCount % 100, 60, 60);
		pg.ellipse(30, 30, 60, 60);
		pg.fill(0, 255);
		pg.stroke(0, 255);
		pg.text(key, mouseX-3, mouseY-10);
		pg.endDraw();
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.repaint();
		
		frame.setLocation(P.round(300 + P.cos(frameCount * 0.1f) * 100f), P.round(300 + P.sin(frameCount * 0.1f) * 100f));
	}
}

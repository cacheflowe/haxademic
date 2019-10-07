package com.haxademic.demo.system;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.MouseUtil;

public class Demo_JFrameLauncher
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FrameWithBorderLayout frame;
	
	public void setupFirstFrame() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 frame = new FrameWithBorderLayout();
                 frame.setVisible(true);
                 frame.setSize(400, 200);
//                 frame.setUndecorated(true);
//                 frame.setOpacity(0);
            }
      });
	}
	
	public void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);
		
		if(frame != null) {
			p.background(0,255,0);
			if(p.frameCount % 100 == 0) {
				if(p.frameCount % 200 == 0) {
					MouseUtil.setCursorWait(frame);
				} else {
					MouseUtil.setInvisibleCursor(frame);
				}
			}
		}
	}

	@SuppressWarnings("serial")
	public class FrameWithBorderLayout 
	extends JFrame {

	    private JButton buttonEast; // The east button
	    private JButton buttonSouth; // The south button
	    private JButton buttonWest; // The west button
	    private JButton buttonNorth; // The north button
	    private JButton buttonCenter; // The center button

	    public FrameWithBorderLayout() {
	        // Call super class constructor with a title
	        super("Frame With Multiple Buttons");

	        // Create JButton objects
	        buttonEast = new JButton("East");
	        buttonSouth = new JButton("South");
	        buttonWest = new JButton("West");
	        buttonNorth = new JButton("North");
	        buttonCenter = new JButton("Center");
	        
	        // Add the JButton objects
	        add(buttonEast, BorderLayout.EAST);
	        add(buttonSouth, BorderLayout.SOUTH);
	        add(buttonWest, BorderLayout.WEST);
	        add(buttonNorth, BorderLayout.NORTH);
	        add(buttonCenter, BorderLayout.CENTER);
	        // Set when the close button is clicked, the application exits
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        // Reorganize the embedded components
	        pack();
	    }
	}

}


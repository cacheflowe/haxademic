package com.haxademic.demo.file;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.system.SystemUtil;

public class DropFileWindow {
	
	// Built because OpenGL modes don't allow file drag & drop >:|

	public interface IDropFileDelegate {
		public void fileDropped(String filePath);
	}
	
	protected IDropFileDelegate delegate;
	
	public DropFileWindow(IDropFileDelegate delegate) {
		this.delegate = delegate;
		initDropFileWindow();
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
		// listens for paste keyboard command on main app window
		// in addition to 
		if(KeyboardState.instance().isKeyOn(17) && KeyboardState.keyTriggered(86)) { // ctrl + v
			pasteClipboard();
		}
	}
	
	protected void pasteClipboard() {
		String filePath = SystemUtil.getClipboardContents();
		filePath = filePath.replace("\"", "").replace("\"", "");	// remove surrounding quotes if copied from Windows Explorer context menu
		delegate.fileDropped(filePath);
	}
	
	protected void initDropFileWindow() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				P.p.frame = new DropFile();
				P.p.frame.setVisible(true);
				P.p.frame.setSize(320, 240);
			}
		});
	}
	
	@SuppressWarnings("serial")
	public class DropFile 
	extends JFrame
	implements KeyListener {

	    public DropFile() {
	        // Call super class constructor with a title
	        super("DropFile");

	        // Create visual object to drop files onto
	        JLabel label = new JLabel("Drop files or paste paths here");
	        label.setAlignmentX(0.5f);
	        label.setAlignmentY(0.5f);
	        label.setHorizontalAlignment(SwingConstants.CENTER);
	        label.setBackground(Color.GREEN);
	        label.setOpaque(true);
	        label.setDropTarget(new DropTarget() {
	    	    @SuppressWarnings("unchecked")
				public synchronized void drop(DropTargetDropEvent evt) {
	    	        try {
	    	            evt.acceptDrop(DnDConstants.ACTION_COPY);
	    	            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
	    	            for (File file : droppedFiles) {
	    	            	delegate.fileDropped(file.getPath());
	    	            }
	    	        } catch (Exception ex) {
	    	            ex.printStackTrace();
	    	        }
	    	    }
	    	});
	        
	        // click listener
	        addKeyListener(this);
	        
	        // & Reorganize the embedded components
	        add(label, BorderLayout.CENTER);
	        pack();
	        // Set when the close button is clicked, the application exits
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	    }
	    
	    // KeyListener methods ------------------------------
	    
	    public void keyPressed(KeyEvent e) {
	        // System.out.println("keyPressed");
	    }

	    public void keyReleased(KeyEvent e) {
	        if(e.getKeyCode()== KeyEvent.VK_V) {
	        	pasteClipboard();
	        }
	    }
	    public void keyTyped(KeyEvent e) {
	        // System.out.println("keyTyped");
	    }
	}

}

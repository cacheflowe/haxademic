package spout;

//========================================================================================================
//
//                  Spout.Java
//
//		Adds support to the functions of the JSpout JNI library.
//
//		19.12.15 - Finalised Library class
//				 - Changed all parent.println to System.out.println to prevent compiler warning
//				 - Changed "(boolean)(invertMode == 1)" to "(invertMode == 1)" to prevent compiler warning
//				 - Documented all functions
//				 - Cleanup - previous revisions in older Spout.pde file
//		12.02.16 - Changed "ReceiveTexture()" to update and draw a local graphics object
//				 - Removed java.awt import - not needed for Processing 3 frame sizing
//		15.02.16 - Removed "createSender" function console output
//		26.02.16 - Updated JNISpout library dll files - tested Processing 3.0.2 64bit and 32bit 
//				   Spout 2.005 SDK as at 26.02.16 - incremented library version number to 2.0.5.2
//		01.03.16 - Separated initialization flag to bSenderInitialized and bReceiverInitialized
//				 - Added "updateSender" to JNISpout.java and the JNI dll
//				 - Introduced createSenderName using the sketch folder name as default
//		06.03.16 - Introduced object pointers for multiple senders / receivers
//		17.03.16 - Fixed release of receiver when the received sender closed
//		18.03.16 - Fixed initial detection of named sender for CreateReceiver
//		25.03.16 - Removed "Settings" from multiple examples to work with Processing 2.2.1
//		30.03.16 - Rebuild for Spout 2.005 release - version 2.0.5.3
//		28.04.16 - Added "receivePixels"
//		10.05.16 - Added SpoutControls example
//		12.05.16 - Library release - version 2.0.5.4
//		02.06.16 - Library release - version 2.0.5.5 for Spout 2.005 June 2016
//		07.07.16 - Updated for latest SDK functions
//				   co.zeal.spout project removed
//		09.10.16 - Introduced cleanup function for dispose
//				   https://github.com/processing/processing/issues/4381#issuecomment-252198109
//		15.01.17 - Change to Processing 3.2.3 core library.
//				   Added getShareMode - 0 Texture, 1 CPU, 2 Memory
//		26.01.17 - Rebuild for Spout 2.006 - version 2.0.6.0
//		27.01.17 - Some comment changes for CreateSender.
//				 - JNISpout - changes to OpenSpoutController to find a SpoutControls installation
//		08.02.17 - Change to Processing 3.2.4 core library.
//				 - SpoutControls example removed - duplicate of SpoutBox in the SpoutControls installation
//				 - Rebuild with current SDK files
//				 - Library release - version 2.0.6.0 for Spout 2.006 - February 2017
//
// ========================================================================================================

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.*;
import javax.swing.JOptionPane; // for infoBox

/**
 * Main Class to use with Processing
 * 
 * @author Lynn Jarvis, Martin Froehlich
 *
 */
public class Spout {
	
	// A pointer to the spout object created
	long spoutPtr = 0;

	PApplet parent;
	PGraphicsOpenGL pgl;
	PGraphics pgs; // local graphics object for receiving textures
	String senderName; // the sender name
	int[] dim = new int[2]; // Sender dimensions
	boolean bSenderInitialized; // sender initialization flag
	boolean bReceiverInitialized; // receiver initialization flag
	int invertMode; // User setting for texture invert

	
	/**
	 * Create a Spout Object.
	 * 
	 * A spout object is created within the JNI dll
	 * and a pointer to it saved in this class for
	 * use with all functions.
	 * 
	 * @param parent
	 */
	public Spout (PApplet parent) {
	
		// A pointer to the new spout object for this instance
		spoutPtr = JNISpout.init();
		
		if(spoutPtr == 0) 
			PGraphics.showWarning("Spout initialization failed");

		this.parent = parent;
		
		pgl = (PGraphicsOpenGL) parent.g;
		dim[0] = 0; // Sender width
		dim[1] = 0; // Sender height
		bSenderInitialized = false;
		bReceiverInitialized = false;
		senderName = "";
		invertMode = -1; // User has not set any mode - use function defaults
		
		parent.registerMethod("dispose", this);
		
	}  
	
	/**
	 * Can be called from the sketch to release the library resources.
	 * Such as from an over-ride of "exit" in the sketch for Processing 3.0.2
	 */
	public void release() {
		 // infoBox("Spout release");
		 dispose();
	}
		
	/**
	  * This method should be called automatically when the sketch is disposed.
	  * Differences observed between Processing versions :
	  * 3.0.1 calls it for [X] window close or esc but not for the "Stop" button,
	  * 3.0.2 does not call it at all.
	  * 3.2.3 calls it for esc of [X] but not for the stop button.
	  * Senders are apparently released because they can't be detected subsequently.  
	  */
	 public void dispose() {
		// infoBox("Spout dispose");
		spoutCleanup();
	}

	/**
	 * The class finalizer - adapted from Syphon.
	 * Never seems to be called.
	 */
	protected void finalize() throws Throwable {
		// infoBox("Spout finalize");
		try {
			spoutCleanup();
	    } finally {
	    	super.finalize();
	    }
	} 

	  
	// =========================================== //
	//                   SENDER                    //
	// =========================================== //
	/**
	 * Initialize a sender name.
	 * If the name is not specified, the sketch folder name is used.
	 * 
	 * @param name - sender name (up to 256 characters)
	 */
	public void createSenderName(String name) {
		 
		if(name.isEmpty() || name.equals("") ) {
			String path = parent.sketchPath();
			int index = path.lastIndexOf("\\");
			senderName = path.substring(index + 1);
		}
		else {
			senderName = name;
		}
	}
		
	/**
	 * Initialize a sender with the sketch window dimensions.
	 * If the sender name is not specified, the sketch folder name is used.
	 * 
	 * @param name - sender name (up to 256 characters)
	 * @return true if the sender was created
	 */	
	public boolean createSender(String name) {
		createSenderName(name);
		return createSender(name, parent.width, parent.height);
	}	
		
	/**
	 * Initialize a sender.
	 * 
	 * The name provided is registered in the list of senders
	 * Initialization is made using or whatever the user has selected
	 * with SpoutDXmode : Texture, CPU or Memory. Texture share only 
	 * succeeds if the graphic hardware is compatible, otherwise it
	 * defaults to CPU texture share mode.
	 *  
	 * @param name - sender name (up to 256 characters)
	 * @param Width - sender width
	 * @param Height - sender height
	 * @return true if the sender was created
	 */
	public boolean createSender(String name, int Width, int Height) {
		createSenderName(name);
		if(JNISpout.createSender(senderName, Width, Height, spoutPtr)) {
			bSenderInitialized = true;
			dim[0] = Width;
			dim[1] = Height;
			System.out.println("Created sender '" + senderName + "' (" + dim[0] + "x" + dim[1] + ")");
			spoutReport(bSenderInitialized); // console report
		}
		return bSenderInitialized;
	}
	
	/**
	 * Update the size of the current sender
	 * 
	 * @param Width - new width
	 * @param Height - new height
	 */
	public void updateSender(int Width, int Height) {
		if(bSenderInitialized) { // There is a sender name
			JNISpout.updateSender(senderName, Width, Height, spoutPtr);
			dim[0] = Width;
			dim[1] = Height;
		}
	}
	
	/**
	 * Close the sender. 
	 * 
	 * This releases the sender name from the list if senders
	 * and releases all resources for the sender.
	 */
	public void closeSender() {
		if(bSenderInitialized) {
			if(JNISpout.releaseSender(spoutPtr))
				System.out.println("Sender was closed");
			else
				System.out.println("No sender to close");
			bSenderInitialized = false;
		}
	} 

	/**
	 *	Write the sketch drawing surface texture to 
	 *	an opengl/directx shared texture
	 */
	public void sendTexture() {

		if(!bSenderInitialized)	{
			// Create a sender the dimensions of the sketch window
			System.out.println("sendTexture");
			createSender(senderName, parent.width, parent.height);
			return;
		}
		else if(dim[0] != parent.width || dim[1] != parent.height) {
			// Update the dimensions of the sender
			updateSender(parent.width, parent.height);
			return;
		}

		// Set the invert flag to the user setting if it has been selected
		// Processing Y axis is inverted with respect to OpenGL
		// so we need to invert the texture for this function
		boolean bInvert = true; 
		if(invertMode >= 0) bInvert = (invertMode == 1);
		
		pgl.beginPGL();
		// Load the current contents of the renderer's
		// drawing surface into its texture.
		pgl.loadTexture();
		// getTexture returns the texture associated with the
		// renderer's drawing surface, making sure is updated 
		// to reflect the current contents off the screen 
		// (or offscreen drawing surface).      
		Texture tex = pgl.getTexture();
		JNISpout.sendTexture(tex.glWidth, tex.glHeight, tex.glName, tex.glTarget, bInvert, spoutPtr);
		pgl.endPGL();

	}
	
	/**
	 * Write the texture of a graphics object.
	 * 
	 * @param pgr - the graphics object to be used.
	 */
	public void sendTexture(PGraphics pgr)
	{
		if(!bSenderInitialized) {
			// Create a sender the dimensions of the graphics object
			dim[0] = pgr.width;
			dim[1] = pgr.height;
			System.out.println("sendTexture graphics");
			createSender(senderName, dim[0], dim[1]);
			return;
		}
		else if(dim[0] != pgr.width || dim[1] != pgr.height) {
			// Update the dimensions of the sender
			updateSender(pgr.width, pgr.height);
			return;
		}
		
		boolean bInvert = true;
		if(invertMode >= 0) bInvert = (invertMode == 1);
		Texture tex = pgl.getTexture(pgr);
		JNISpout.sendTexture(tex.glWidth, tex.glHeight, tex.glName, tex.glTarget, bInvert, spoutPtr);

	}

	/**
	 *  Write the texture of an image object.
	 *  
	 * @param img - the image to be used.
	 */
	public void sendTexture(PImage img)
	{
		if(!bSenderInitialized)	{
			// Create a sender the dimensions of the image object
			System.out.println("sendTexture image");
			createSender(senderName, img.width, img.height);
			return;
		}
		else if(dim[0] != img.width || dim[1] != img.height) {
			// Update the dimensions of the sender
			updateSender(img.width, img.height);
			return;
		}
		
		boolean bInvert = false; // default for this function
		if(invertMode >= 0) bInvert = (invertMode == 1);
		Texture tex = pgl.getTexture(img);
		JNISpout.sendTexture(tex.glWidth, tex.glHeight, tex.glName, tex.glTarget, bInvert, spoutPtr);

	}

	// ================= SPOUTCONTROLS =================
	/**
	 * Create a control with defaults.
	 * 
	 * @param name - control name
	 * @param type - text (string), bool (checkbox), event (button), float (value)
	 * @return true for success
	 */
	public boolean createSpoutControl(String name, String type) {
		return(JNISpout.createControl(name, type, 0, 1, 1, "", spoutPtr));
	}

	/**
	 * Create a control with default value.
	 * 
	 * @param name - control name
	 * @param type - float, bool, event
	 * @return true for success
	 */
	public boolean createSpoutControl(String name, String type, float value) {
		return(JNISpout.createControl(name, type, 0, 1, value, "", spoutPtr));
	}

	/**
	 * Create a text control with default string.
	 * 
	 * @param name - control name
	 * @param type - text
	 * @return true for success
	 */	
	public boolean createSpoutControl(String name, String type, String text) {
		return(JNISpout.createControl(name, type, 0, 1, 1, text, spoutPtr));
	}

	/**
	 * Create a float control with defaults.
	 * Minimum, Maximum, Default
	 * 
	 * @param name - control name
	 * @param type - float
	 * @return true for success
	 */	
	public boolean createSpoutControl(String name, String type, float minimum, float maximum, float value) {
		return(JNISpout.createControl(name, type, minimum, maximum, value, "", spoutPtr));
	}

	/**
	 * Open SpoutControls
	 * 
	 * A sender creates the controls and then calls OpenControls with a control name
	 * so that the controller can set up a memory map and share data with the sender
	 * as it changes the controls.
	 * @param name - control map name (the sender name)
	 * @return true for success
	 */
	public boolean openSpoutControls(String name) {
		return(JNISpout.openControls(name, spoutPtr));
	}
	
	/**
	 * Check the controller for changed controls.
	 * 
	 * The value or text string are changed depending on the control type.
	 * 
	 * @param controlName
	 * @param controlType
	 * @param controlValue
	 * @param controlText
	 * @return The number of controls. Zero if no change.
	 */
	public int checkSpoutControls(String[] controlName, int[] controlType, float[] controlValue, String[] controlText ) {
		return JNISpout.checkControls(controlName, controlType, controlValue, controlText, spoutPtr);
	}
	
	/**
	 * Open the SpoutController executable to allow controls to be changed.
	 * 
	 * Requires SpoutControls installation
	 * or SpoutController.exe in the sketch path
	 * 
	 * @return true if the controller was found and opened
	 */
	public boolean openController() {
		return(JNISpout.openController(parent.sketchPath(), spoutPtr));
	}
	
	/**
	 * Close the link with the controller.
	 * 
	 * @return true for success
	 */
	public boolean closeSpoutControls() {
		return(JNISpout.closeControls(spoutPtr));
	}

	// ================= SHARED MEMORY =================
	/**
	 * Create a sender memory map.
	 * 
	 * @param name - sender name
	 * @param Width - map width
	 * @param Height - map height
	 * @return True for success
	 */
	public boolean createSenderMemory(String name, int Width, int Height) 
	{
		return (JNISpout.createSenderMemory(name, Width, Height, spoutPtr));
	}
	
	/**
	 * Change the size of a sender memory map.
	 * 
	 * @param name Sender name
	 * @param Width - new map width
	 * @param Height - new map height
	 * @return True for success
	 */
	public boolean updateSenderMemorySize(String name, int Width, int Height) 
	{
		return (JNISpout.updateSenderMemorySize(name, Width, Height, spoutPtr));
	}
	
	/**
	 * Write a string to the memory map.
	 * 
	 * The map size must be sufficient for the string.
	 * @param sValue - string to be written
	 * @return True for success
	 */
	public boolean writeSenderString(String sValue) 
	{
		return (JNISpout.writeSenderString(sValue, spoutPtr));
	}
	
	/**
	 * Close a sender memory map.
	 */
	public void closeSenderMemory() 
	{
		JNISpout.closeSenderMemory(spoutPtr);
	}
	/**
	 * Lock a memory map for write or read access.
	 * 
	 * @return Size of the memory map
	 */
	public long lockSenderMemory() 
	{
		return JNISpout.lockSenderMemory(spoutPtr);
	}

	/** 
	 * Unlock a memory map after locking.
	 * 
	 */
	public void unlockSenderMemory() 
	{
		JNISpout.unlockSenderMemory(spoutPtr);
	}
	
	
	// =========================================== //
	//                   RECEIVER                  //
	// =========================================== //
	/**
	 *  Initialize a Receiver. 
	 * 
	 * If the named sender is not running or if the name is not specified,
	 * the receiver will attempt to connect with the active sender.
	 * If the sender is found, the name is returned and set.
	 *  
	 * @param name - sender name to be used (optional)
	 * @return true if connection with a sender succeeded
	 */
	public boolean createReceiver(String name) {

		// Image size values passed in are modified and passed back
		// as the size of the sender that the receiver connects to.
		dim[0] = parent.width;
		dim[1] = parent.height;
		String newname;

		if(name.isEmpty() || name.equals("") ) {
			name = senderName; // existing name if any
		}
		else {
			senderName = name; // name has been specified
		}

		if(JNISpout.createReceiver(name, dim, spoutPtr)) {
			
			// Initialization succeeded and there was a sender running
			newname = JNISpout.getSpoutSenderName(spoutPtr);

			// dim will be returned with the size of the sender it connected to
			if(newname != null && newname.length() > 0) {
				bReceiverInitialized = true;
				senderName = newname;
				spoutReport(bReceiverInitialized);
				System.out.println("Found sender : '" + senderName + "' (" + dim[0] + "x" + dim[1] + ")" );
			}
		}
		else {
			bReceiverInitialized = false;
			return false;
		}

		return true;

	} // end Receiver initialization

	/**
	 * Close a receiver.
	 * 
	 * All resources of the receiver are released.
	 * 
	 */
	public void closeReceiver() {
		if(JNISpout.releaseReceiver(spoutPtr))
			System.out.println("Receiver closed");
		else
			System.out.println("No receiver to close");
		bReceiverInitialized = false;
	} 
	
	/**
	 * Receive into a local graphics object and draw it directly.
	 * 
	 * @return true if a texture was received.
	 */
	public boolean receiveTexture()
	{
		// If no sender, keep looking
		if(!bReceiverInitialized) {
			createReceiver("");
			return false;
		}

		boolean bInvert = true;
		if(invertMode >= 0) bInvert = (invertMode == 1);
		
		// Adjust the local graphics object to the current sender size
		if(pgs == null || dim[0] != pgs.width || dim[1] != pgs.height && dim[0] > 0 && dim[1] > 0) {
			pgs = parent.createGraphics(dim[0], dim[1], PConstants.P2D);
		}
		else {
			// Receive into the local graphics object and draw it.
			// Sender dimensions (dim) are sent as well as returned
			// The graphics size is adjusted next time round
			Texture tex = pgl.getTexture(pgs);
			if(JNISpout.receiveTexture(dim, tex.glName, tex.glTarget, bInvert, spoutPtr)) {
				parent.image(pgs, 0, 0, parent.width, parent.height);
			}
			else {
				JNISpout.releaseReceiver(spoutPtr);
				senderName = "";
				bReceiverInitialized = false;
				return false;
			}
		}
		return true;

	} // end receiveTexture

	/**
	 * Receive into graphics.
	 * 
	 * Sender changes are detected in JNISpout.ReceiveTexture
	 * and returned. The PGraphics is resized the next time.
	 * 
	 * @param pg - the graphics to be used and returned
	 * @return true if a texture was returned
	 */
	public PGraphics receiveTexture(PGraphics pg)
	{
		// If no sender, keep looking
		if(!bReceiverInitialized) {
			createReceiver("");
			return pg;
		}

		boolean bInvert = true; // default for this function
		if(invertMode >= 0) bInvert = (invertMode == 1);

		// Adjust the graphics to the current sender size
		if(dim[0] != pg.width || dim[1] != pg.height && dim[0] > 0 && dim[1] > 0) {
			pg = parent.createGraphics(dim[0], dim[1], PConstants.P2D);
		}
		else {
			// Sender dimensions (dim) are sent as well as returned
			// The graphics size is adjusted next time round
			Texture tex = pgl.getTexture(pg);
			if(!JNISpout.receiveTexture(dim, tex.glName, tex.glTarget, bInvert, spoutPtr)) {
				JNISpout.releaseReceiver(spoutPtr);
				senderName = "";
				pg.updatePixels();
				bReceiverInitialized = false;
			}
		}

		return pg;    
	}

	/**
	 * Receive into an image texture.
	 * 
	 * @param img - the image to be used and returned
	 * @return true if a texture was returned
	 */
	public PImage receiveTexture(PImage img) {

		// If no sender, keep looking
		if(!bReceiverInitialized) {
			createReceiver("");
			return img;
		}

		boolean bInvert = false; // default for this function
		if(invertMode >= 0) bInvert = (invertMode == 1);

		if(dim[0] != img.width || dim[1] != img.height && dim[0] > 0 && dim[1] > 0) {
			img.resize(dim[0], dim[1]);
		}
		else {
			Texture tex = pgl.getTexture(img);
			if(!JNISpout.receiveTexture(dim, tex.glName, tex.glTarget, bInvert, spoutPtr)) {
				JNISpout.releaseReceiver(spoutPtr);
				senderName = "";
				img.updatePixels();
				bReceiverInitialized = false;
			}
		}    

		return img;
	}

	
	/**
	 * Receive into image pixels.
	 * 
	 * @param img - the image to be used and returned
	 * @return true if pixels were returned
	 */
	public PImage receivePixels(PImage img) {

		// If no sender, keep looking
		if(!bReceiverInitialized) {
			createReceiver("");
			return img;
		}

		boolean bInvert = false; // default for this function
		if(invertMode >= 0) bInvert = (invertMode == 1);

		if(dim[0] != img.width || dim[1] != img.height && dim[0] > 0 && dim[1] > 0) {
			img.resize(dim[0], dim[1]);
		}
		else {
			img.loadPixels();
			if(!JNISpout.receivePixels(dim, img.pixels, spoutPtr)) {
				JNISpout.releaseReceiver(spoutPtr);
				senderName = "";
				bReceiverInitialized = false;
			}
		    img.updatePixels();
		}

		return img;
	}
	
	/**
	 * Pop up SpoutPanel to select a sender.
	 * 
	 * If the user selected a different one, attach to it.
	 * Requires Spout installation 2.004 or higher.
	 */
	public void selectSender()
	{
		JNISpout.senderDialog(spoutPtr);
	}
	
	/**
	 * Resize the receiver drawing surface and sketch window to that of the sender
	 * 
	 * Requires Processing 3.
	 * Optional.
	 */
	public void resizeFrame()
	{
		if(!bReceiverInitialized) return;
		if(parent.width != dim[0] || parent.height != dim[1]  && dim[0] > 0 && dim[1] > 0) {
			// Only for Processing 3
			parent.getSurface().setSize(dim[0], dim[1]);
		}
	}

	
	/**
	 * Release everything
	 */
	public void spoutCleanup()
	{
		// infoBox("spoutCleanup");
		if(bSenderInitialized) JNISpout.releaseSender(spoutPtr);
		if(bReceiverInitialized) JNISpout.releaseReceiver(spoutPtr);
		if(spoutPtr > 0) JNISpout.deInit(spoutPtr);
		bSenderInitialized = false;
		bReceiverInitialized = false;
		spoutPtr = 0;
    }
	
	// =========================================== //
	//                   UTILITY                   //
	// =========================================== //

	/**
	 * User option to set texture inversion for send and receive
	 * 
	 * @param bInvert - true or false as required
	 */
	public void setInvert(boolean bInvert)
	{
		// invertMode is -1 unless the user specifically selects it
		if(bInvert)
			invertMode = 1;
		else
			invertMode = 0;
	}

	/**
	 * Print current settings to the console.
	 * 
	 * @param bInit - the initialization mode
	 */
	public void spoutReport(boolean bInit)
	{
		int ShareMode = 0; // Texture share default
		if(bInit) {
			ShareMode = JNISpout.getShareMode(spoutPtr);
			if(ShareMode == 2)
				System.out.println("Spout initialized memory sharing");
			else if(ShareMode == 1)
				System.out.println("Spout initialized CPU texture sharing");
			else
				System.out.println("Spout initialized texture sharing");
		}
		else {
			PGraphics.showWarning("Spout intialization failed");
		}
	}
	
	/**
	 * Pop up a MessageBox dialog
	 * 
	 * @param infoMessage - the message to show
	 */
	public void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Spout", JOptionPane.INFORMATION_MESSAGE);
    }	

} // end class Spout


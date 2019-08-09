package spout;

public class JNISpout {

  static {
	
	// String jvm_location = System.getProperties().getProperty("java.home") + "\\" + "bin" + "\\" + "java.exe";
	// System.out.println(jvm_location);
	String jvm_version = System.getProperty("java.version");

	// Java instead of operating system
	String sunDataModel = System.getProperty("sun.arch.data.model");
	System.out.println("Spout " + sunDataModel +"bit v2.0.6.0 - Java " + jvm_version);
	// System.out.println("Java " + sunDataModel + "bit " + jvm_version);
	if(sunDataModel.equals("32"))	
		System.loadLibrary("JNISpout_32");
	else if(sunDataModel.equals("64"))	
		System.loadLibrary("JNISpout_64");
  }
  
  
  // Initialization - return a pointer to a spout object
  protected static native long init();
  protected static native void deInit(long ptr);
  
  //=================================================================== //
  //                            SENDER                                  //
  //=================================================================== //
  
  protected static native boolean createSender(String name, int width, int height, long ptr);
  
  protected static native boolean updateSender(String name, int width, int height, long ptr);
  
  protected static native boolean releaseSender(long ptr);
  
  protected static native boolean sendTexture(int w, int h, int texID, int texTarget, boolean bInvert, long ptr);

  // SpoutControls

  protected static native boolean createControl(String name, String type, float minimum, float maximum, float value, String text, long ptr);
  
  protected static native boolean openControls(String name, long ptr);

  protected static native int checkControls(String[] name, int[] type, float[] value, String[] text, long ptr);
  
  protected static native boolean openController(String path, long ptr);

  protected static native boolean closeControls(long ptr);
  
  // Shared memory

  protected static native boolean createSenderMemory(String name, int width, int height, long ptr);

  protected static native boolean updateSenderMemorySize(String name, int width, int height, long ptr);

  protected static native boolean writeSenderString(String buf, long ptr);

  protected static native void closeSenderMemory(long ptr);

  protected static native long lockSenderMemory(long ptr);

  protected static native void unlockSenderMemory(long ptr);
    

  //=================================================================== //
  //                           RECEIVER                                 //
  //=================================================================== //

  protected static native boolean createReceiver(String name, int[] dim, long ptr);
  
  protected static native boolean releaseReceiver(long ptr);

  protected static native boolean receivePixels(int[] dim, int[] pix, long ptr);

  protected static native boolean receiveTexture(int[] dim, int texID, int texTarget, boolean bInvert, long ptr);

  protected static native boolean drawTexture(boolean bInvert, long ptr);

  protected static native boolean senderDialog(long ptr);

  protected static native String getSpoutSenderName(long ptr);
  
  
  
  //=================================================================== //
  //                            COMMON                                  //
  //=================================================================== //

  protected static native int getTextureID(long ptr);

  protected static native boolean getMemoryShareMode(long ptr);
  
  protected static native int getShareMode(long ptr);
 
  
}

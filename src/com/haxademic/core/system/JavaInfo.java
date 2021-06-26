package com.haxademic.core.system;

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.io.File;
import java.io.PrintStream;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.LogManager;

import javax.net.ssl.KeyManagerFactory;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import com.haxademic.core.app.P;

public class JavaInfo {

	// https://stackoverflow.com/a/40766409/352456
	// https://stackoverflow.com/questions/25552/get-os-level-system-information
	
    public static PrintStream out = System.out;

    public static void printCurrentTime() {
        out.println("current date " + new Date());
        out.println("nano time "+System.nanoTime());        
    }

    public static void printAvailableCPUs() {
        out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
    }

    public static void printSystemEnvironment() {
    	for (Entry<String,String> e : System.getenv().entrySet())
    		out.println(e.getKey()+" "+e.getValue());
    }
    
    public static void printSystemProperties() {
        Properties props = System.getProperties();
        Enumeration<Object> enums = props.keys();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement().toString();
            out.println(key + " : " + props.getProperty(key));
        }
    }

    public static void printRuntimeMemory() {
        Runtime r = Runtime.getRuntime();
        out.println("free memory " + r.freeMemory());
        out.println("max memory " + r.maxMemory());
        out.println("total memory " + r.totalMemory());
    }

    public static void printCommandLineArguments() {
        out.print("JVM arguments");
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments())
            out.print(" "+arg);
        out.println();
    }

    public static void printClassLoaderInfo() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        out.println("thread context class loader "+cl.getClass().getName());
        out.println("system class loader "+ClassLoader.getSystemClassLoader().getClass().getName());
        ClassLoadingMXBean cx = ManagementFactory.getClassLoadingMXBean();
        out.println("loaded classes count "+cx.getLoadedClassCount());
    }

    public static void printOSInfo() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        out.println("OS name "+os.getName()+" version "+os.getVersion());
        out.println("architecture "+os.getArch());
        out.println("available processors "+os.getAvailableProcessors());
    }

    public static void printCPUUsage() {
        out.println("Current thread CPU time "+ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime());
        out.println("number of threads "+ManagementFactory.getThreadMXBean().getThreadCount());     
        out.println("system load average "+ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());        
    }

    public static void printDisplayInfo() {
        int g = 0;
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            out.println("graphics device #"+(++g)+": "+gd.getIDstring()+" type "+gd.getType());
            out.println("\tavailable accelerated memory " + gd.getAvailableAcceleratedMemory());
            int c = 0;
            for (GraphicsConfiguration gc : gd.getConfigurations()) {
                out.println("\tgraphics configuration #"+(++c)+":");
                out.println("\t\twidth "+gc.getBounds().getWidth()+" height "+gc.getBounds().getHeight());
                out.println("\t\tfull screen "+gc.getBufferCapabilities().isFullScreenRequired());
                ImageCapabilities ic = gc.getImageCapabilities();
                out.println("\t\tis accelerated "+ic.isAccelerated());

            }
            DisplayMode dm = gd.getDisplayMode();   
            out.println("\tdisplay mode bit width "+dm.getWidth()+" height "+dm.getHeight()+" bit depth "+dm.getBitDepth()+" refresh rate "+dm.getRefreshRate());
            int m = 0;
            for (DisplayMode d : gd.getDisplayModes())
                out.println("\talt display mode #"+(++m)+" bit width "+d.getWidth()+" height "+d.getHeight()+" bit depth "+d.getBitDepth()+" refresh rate "+d.getRefreshRate());    
        }
    }
    
	public static int numScreens() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		return gs.length;
	}

	public static int totalScreenHeight() {
		int h = 0;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for(GraphicsDevice curGs : gs)
		{
			DisplayMode dm = curGs.getDisplayMode();
			h += dm.getHeight();
		}
		return h;
	}

    public static void printFontsInfo() {
        out.println("available fonts: "+String.join(",", GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
    }

    public static void printLocaleInfo() {
        out.println("default locale: "+Locale.getDefault().getDisplayName()+" country "+Locale.getDefault().getCountry()+" language "+Locale.getDefault().getLanguage());
        out.println("available locales:");
        for (Locale l : Locale.getAvailableLocales())
            out.println("\t"+l.getDisplayName()+" country "+l.getCountry()+" language "+l.getLanguage());
    }

    public static void printDiskInfo() {
    	P.out("Current directory: "+Paths.get(".").toAbsolutePath().normalize().toString());
        File[] roots = File.listRoots();
        for (File r : roots) {
              out.println("File system root: " + r.getAbsolutePath());
              out.println("\tTotal space (bytes): " + r.getTotalSpace());
              out.println("\tFree space (bytes): " + r.getFreeSpace());
              out.println("\tUsable space (bytes): " + r.getUsableSpace());
              out.println("\tcan write "+r.canWrite());
            }
    }

    public static void printNetworkInfo() throws UnknownHostException {
        out.println("host name "+InetAddress.getLocalHost().getHostName());
        out.println("host IP address "+InetAddress.getLocalHost().getHostAddress());

    }

//    @SuppressWarnings("deprecation")
//	public static void printSecurityInfo() throws UnknownHostException {
//        SecurityManager security = System.getSecurityManager();
//         if (security != null) {
//             out.println("security manager "+security.getClass().getName()+" in check "+security.getInCheck());
//
//         } else {
//             out.println("no security manager");
//         }
//    }

    public static void printKeyManagerInfo() {
        out.println("key manager default algorithm "+KeyManagerFactory.getDefaultAlgorithm());      
        out.println("key store default type "+KeyStore.getDefaultType());
    }

    public static void printLoggingInfo() {
        for (String logger : LogManager.getLoggingMXBean().getLoggerNames()) {
            out.println("logger: \""+logger+"\" level \""+LogManager.getLoggingMXBean().getLoggerLevel(logger)+"\"");
        }

    }
    
	public static void printAudioInfo() {
		P.out("----------------- printAudioInfo -------------------");
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		for(int i = 0; i < mixerInfo.length; i++) {
			P.out("########## mixerInfo["+i+"]", mixerInfo[i].getName());

//			Mixer mixer = AudioSystem.getMixer(null); // default mixer
			Mixer mixer = AudioSystem.getMixer(mixerInfo[i]); // default mixer
			try {
				mixer.open();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
	
			P.out("Supported SourceDataLines of default mixer (%s):\n\n", mixer.getMixerInfo().getName());
			for(Line.Info info : mixer.getSourceLineInfo()) {
			    if(SourceDataLine.class.isAssignableFrom(info.getLineClass())) {
			        SourceDataLine.Info info2 = (SourceDataLine.Info) info;
			        P.out(info2);
			        System.out.printf("  max buffer size: \t%d\n", info2.getMaxBufferSize());
			        System.out.printf("  min buffer size: \t%d\n", info2.getMinBufferSize());
			        AudioFormat[] formats = info2.getFormats();
			        P.out("  Supported Audio formats: ");
			        for(AudioFormat format : formats) {
			        	P.out("    "+format);
			          System.out.printf("      encoding:           %s\n", format.getEncoding());
			          System.out.printf("      channels:           %d\n", format.getChannels());
			          System.out.printf(format.getFrameRate()==-1?"":"      frame rate [1/s]:   %s\n", format.getFrameRate());
			          System.out.printf("      frame size [bytes]: %d\n", format.getFrameSize());
			          System.out.printf(format.getSampleRate()==-1?"":"      sample rate [1/s]:  %s\n", format.getSampleRate());
			          System.out.printf("      sample size [bit]:  %d\n", format.getSampleSizeInBits());
			          System.out.printf("      big endian:         %b\n", format.isBigEndian());
			          
			          Map<String,Object> prop = format.properties();
			          if(!prop.isEmpty()) {
			        	  P.out("      Properties: ");
			              for(Map.Entry<String, Object> entry : prop.entrySet()) {
			                  System.out.printf("      %s: \t%s\n", entry.getKey(), entry.getValue());
			              }
			          }
			        }
			        P.out();
			    } else {
			    	P.out(info.toString());
			    }
			    P.out();
			}
			mixer.close();
		}
	}

    public static void printDebug() {
        out.println("****************************************");
        out.println("DATE INFO");
        printCurrentTime();
        out.println("");
        out.println("****************************************");
        out.println("JVM COMMAND LINE ARGUMENTS");
        printCommandLineArguments();
        out.println("");
        out.println("****************************************");
        out.println("ENVIRONMENT");
        printSystemEnvironment();
        out.println("");
        out.println("****************************************");
        out.println("SYSTEM PROPERTIES");
        printSystemProperties();
        out.println("");
        out.println("****************************************");
        out.println("CLASS LOADER");
        printClassLoaderInfo();
        out.println("");
        out.println("****************************************");
        out.println("OPERATING SYSTEM");
        printOSInfo();
        out.println("");
        out.println("****************************************");
        out.println("MEMORY");
        printRuntimeMemory();
        out.println("");
        out.println("****************************************");
        out.println("CPU");
        printCPUUsage();
        out.println("");
        out.println("****************************************");
        out.println("DISK");
        printDiskInfo();
        out.println("");
        out.println("****************************************");
        out.println("NETWORK");
        try {
			printNetworkInfo();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        out.println("");
        out.println("****************************************");
//        out.println("SECURITY");
//        try {
//			printSecurityInfo();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//        out.println("");
//        out.println("");
//        out.println("****************************************");
        out.println("LOG");
        printLoggingInfo();
        out.println("");
        out.println("****************************************");
        out.println("KEY MANAGER");
        printKeyManagerInfo();
        out.println("");
        out.println("****************************************");
        out.println("DISPLAY DEVICES");
        printDisplayInfo();
        out.println("");
        out.println("****************************************");
        out.println("AUDIO");
        printAudioInfo();
        out.println("");
        out.println("****************************************");
        out.println("FONTS");
        printFontsInfo();
        out.println("");
        out.println("****************************************");
        out.println("LOCALES");
        printLocaleInfo();
    }

}
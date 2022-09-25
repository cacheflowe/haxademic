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

import processing.core.PApplet;
import processing.video.LibraryLoader;

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
    
    public static void printJavaPathInfo() {
    	String classPath = System.getProperty("java.class.path");
    	// Should not be null, but cannot assume
    	if (classPath != null) {
    		String[] entries = PApplet.split(classPath, File.pathSeparatorChar);
    		// Usually, the most relevant paths will be at the front of the list,
    		// so hopefully this will not walk several entries.
    		P.out("classPath", classPath);
    		for (String entry : entries) {
    			P.out(entry);
    			File dir = new File(entry);
    			// If it's a .jar file, get its parent folder. This will lead to some
    			// double-checking of the same folder, but probably almost as expensive
    			// to keep track of folders we've already seen.
    			if (dir.isFile()) {
    				dir = dir.getParentFile();
    			}
    			File file = new File(dir, "gstreamer-1.0");
    			if (file.exists()) {
    				P.out("FOUND!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", file.getAbsolutePath());
    			}
    		}
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
    
    
    // Allows to set the amount of desired debug output from GStreamer, according to the following table:
    // https://gstreamer.freedesktop.org/documentation/tutorials/basic/debugging-tools.html?gi-language=c#printing-debug-information
    static int DEBUG_LEVEL = 1;
    
    // Path that the video library will use to load the GStreamer base libraries 
    // and plugins from. They can be passed from the application using the 
    // gstreamer.library.path and gstreamer.plugin.path system variables (see
    // comments in initImpl() below).
    static String gstreamerLibPath = "";
    static String gstreamerPluginPath = "";  
    
    static boolean usingGStreamerSystemInstall = false;
    
    // OpenGL texture used as buffer sink by default, when the renderer is 
    // GL-based. This can improve performance significantly, since the video 
    // frames are automatically copied into the texture without passing through 
    // the pixels arrays, as well as having the color conversion into RGBA handled 
    // natively by GStreamer.
    static boolean useGLBufferSink = true;    
    
    static boolean defaultGLibContext = false;
    
    static long INSTANCES_COUNT = 0;
    
    static int bitsJVM;

    static public void checkGstreamer() {

        // The video library loads the GStreamer libraries according to the following
        // priority:
        // 1) If the VM argument "gstreamer.library.path" exists, it will use it as the
        //    root location of the libraries. This is typically the case when running 
        //    the library from Eclipse.
        // 2) If the environmental variable is GSTREAMER_1_0_ROOT_X86(_64) is defined then 
        //    will try to use its contents as the root path of the system install of GStreamer.
        // 3) The bundled version of GStreamer will be used, if present.
        // 4) If none of the above works, then will try to use default install locations of GStreamer
        //    on Windows and Mac, if they exist.
        // In this way, priority is given to the system installation of GStreamer only if set in the
        // environmental variables, otherwise will try to load the bundled GStreamer, and if it does not
        // exist it will look for GStreamer in the system-wide locations. This gives the user the option
        // to remove the bundled GStreamer libs to default to the system-wide installation.
        String libPath = System.getProperty("gstreamer.library.path");
        if (libPath != null) {
          gstreamerLibPath = libPath;
          
          // If the GStreamer installation referred by gstreamer.library.path is not
          // a system installation, then the path containing the plugins needs to be
          // specified separately, otherwise the plugins will be automatically 
          // loaded from the default location. The system property for the plugin
          // path is "gstreamer.plugin.path"
          String pluginPath = System.getProperty("gstreamer.plugin.path");
          if (pluginPath != null) {
            gstreamerPluginPath = pluginPath;
          }
          
          usingGStreamerSystemInstall = false;
        } else {
          String rootPath = "";
          if (bitsJVM == 64 && System.getenv("GSTREAMER_1_0_ROOT_X86_64") != null) {
            // Get 64-bit root of GStreamer install
            rootPath = System.getenv("GSTREAMER_1_0_ROOT_X86_64");
          } else if (bitsJVM == 32 && System.getenv("GSTREAMER_1_0_ROOT_X86") != null) {
            // Get 32-bit root of GStreamer install
            rootPath = System.getenv("GSTREAMER_1_0_ROOT_X86");  
          }
          
          if (!rootPath.equals("")) {
            if (PApplet.platform == P.MACOS) {
              gstreamerLibPath = Paths.get(rootPath, "lib").toString();
            } else {
              gstreamerLibPath = Paths.get(rootPath, "bin").toString();
            }
            File path = new File(gstreamerLibPath);
            if (path.exists()) {
              // We have a system install of GStreamer
              usingGStreamerSystemInstall = true;
              buildSystemPaths(rootPath);
            } else {
              // The environmental variables contain invalid paths...
              gstreamerLibPath = "";
            }
          }
        } 
          
        if (libPath == null && !usingGStreamerSystemInstall) {
          // No GStreamer path in the VM arguments, and not system-wide install in environmental variables,
          // will try searching for the bundled GStreamer libs.
          buildBundldedPaths();
        }

        if (gstreamerLibPath.equals("")) {
          // Finally, no environmental variables defined and did not find bundled gstreamer,
          // will try some default system-wide locations.
          String rootPath = "";
          if (PApplet.platform == P.MACOS) {
            rootPath = "/Library/Frameworks/GStreamer.framework/Versions/1.0";
            gstreamerLibPath = Paths.get(rootPath, "lib").toString();
          } else if (PApplet.platform == P.WINDOWS) {
            if (bitsJVM == 64) {
              rootPath = "C:\\gstreamer\\1.0\\x86_64";
            } else {
              rootPath = "C:\\gstreamer\\1.0\\x86";
            }
            gstreamerLibPath = Paths.get(rootPath, "bin").toString();
          } else if (PApplet.platform == P.LINUX) {
            if (bitsJVM == 64) {
              rootPath = "/lib/x86_64-linux-gnu";
            } else {
              rootPath = "/lib/x86-linux-gnu";
            }
            File gstlib = new File(rootPath, "libgstreamer-1.0.so.0");
            if (gstlib.exists()) {
              gstreamerLibPath = Paths.get(rootPath).toString();
            }
          }

          P.out("gstreamerLibPath", gstreamerLibPath);
          File path = new File(gstreamerLibPath);
          P.out("path", path.toString());
          if (path.exists()) {
            // We have a system install of GStreamer
            if (bitsJVM == 64) {
              P.out("GSTREAMER_1_0_ROOT_X86_64", gstreamerLibPath, true);
            } else {
            	P.out("GSTREAMER_1_0_ROOT_X86", gstreamerLibPath, true);
            }
            buildSystemPaths(rootPath);
          } else {
            System.err.println("We could not find a system-wide or bundled installation of GStreamer, but video might still work if GStreamer was placed somewhere else");
          }
          usingGStreamerSystemInstall = true;
        }

        if (!gstreamerLibPath.equals("")) {
          // Should be safe because this is setting the jna.library.path,
          // not java.library.path, and JNA is being provided by the video library.
          // This will need to change if JNA is ever moved into more of a shared
          // location (i.e. part of core) because this would overwrite the prop.
          System.setProperty("jna.library.path", gstreamerLibPath);
        }
        
        P.out("GST_DEBUG", String.valueOf(DEBUG_LEVEL), true);

        if (!usingGStreamerSystemInstall) {
          // Disable the use of gst-plugin-scanner on environments where we're
          // not using the host system's installation of GStreamer
          // the problem with gst-plugin-scanner is that the library expects it
          // to exist at a specific location determined at build time
        	P.out("GST_REGISTRY_FORK", "no", true);

          // Prevent globally installed libraries from being used on platforms
          // where we ship GStreamer
          if (!gstreamerPluginPath.equals("")) {
        	  P.out("GST_PLUGIN_SYSTEM_PATH_1_0", "", true);
          }
        }

        if (!usingGStreamerSystemInstall && (PApplet.platform == P.WINDOWS || PApplet.platform == P.LINUX)) {
          // Pre-loading base GStreamer libraries on Windows and Linux,
          // otherwise dynamic dependencies cannot be resolved.
//          LibraryLoader loader = LibraryLoader.getInstance();
//          if (loader == null) {
//            System.err.println("Cannot load GStreamer libraries.");
//          }
        }

        String[] args = { "" };
        P.out(defaultGLibContext);
        P.out("Processing core video", args);
       
        if (!usingGStreamerSystemInstall) {
          // Plugins are scanned explicitly from the bindings if using the
          // local GStreamer
          P.out("addPlugins();");
        }
      
        // output GStreamer version, lib path, plugin path
        // and whether a system install is being used
        printGStreamerInfo();
      }
    
    static protected void printGStreamerInfo() {
        String locInfo = "";
        if (usingGStreamerSystemInstall) locInfo = "system-wide";
        else locInfo = "bundled";
        P.out("Processing video library using " + locInfo);// + " GStreamer " + Gst.getVersion());
      }

    /**
     * Search for an item by checking folders listed in java.library.path
     * for a specific name.
     */
    @SuppressWarnings("SameParameterValue")
    static private String searchLibraryPath(String what) {
      String libraryPath = System.getProperty("java.library.path");
      // Should not be null, but cannot assume
      if (libraryPath != null) {
    	P.out("searchLibraryPath() libraryPath", libraryPath);
        String[] folders = PApplet.split(libraryPath, File.pathSeparatorChar);
        // Usually, the most relevant paths will be at the front of the list,
        // so hopefully this will not walk several entries.
        for (String folder : folders) {
          // Skip /lib and /usr/lib folders because they contain the system-wide GStreamer on Linux
          // and they are on the Java library path.
          if (folder.startsWith("/lib/") || folder.startsWith("/usr/lib/")) continue;
          File file = new File(folder, what);
          P.out("searchLibraryPath() folder", folder, what);
          if (file.exists()) {
        	  P.out("searchLibraryPath() folder!", file.getAbsolutePath());
        	  return file.getAbsolutePath();
          }
        }
      }
      return null;
    }


    /**
     * Search for an item by checking folders listed in java.class.path
     * for a specific name.
     */
    @SuppressWarnings("SameParameterValue")
    static private String searchClassPath(String what) {
      String classPath = System.getProperty("java.class.path");
      // Should not be null, but cannot assume
      if (classPath != null) {
        String[] entries = PApplet.split(classPath, File.pathSeparatorChar);
        // Usually, the most relevant paths will be at the front of the list,
        // so hopefully this will not walk several entries.
        for (String entry : entries) {
          File dir = new File(entry);
          // If it's a .jar file, get its parent folder. This will lead to some
          // double-checking of the same folder, but probably almost as expensive
          // to keep track of folders we've already seen.
          if (dir.isFile()) {
            dir = dir.getParentFile();
          }
          File file = new File(dir, what);
          if (file.exists()) {
            return file.getAbsolutePath();
          }
        }
      }
      return null;
    }

    static protected void buildSystemPaths(String rootPath) {
      if (System.getenv("GST_PLUGIN_SYSTEM_PATH") != null) {
        gstreamerPluginPath = System.getenv("GST_PLUGIN_SYSTEM_PATH");
      } else {
        if (PApplet.platform == P.WINDOWS) {
          gstreamerPluginPath = Paths.get(rootPath, "lib", "gstreamer-1.0").toString();
        } else {
          gstreamerPluginPath = Paths.get(gstreamerLibPath, "gstreamer-1.0").toString();          }
      }
      File path = new File(gstreamerPluginPath);
      if (!path.exists()) {
        gstreamerPluginPath = "";
      }
    }

    static protected void buildBundldedPaths() {
      // look for the gstreamer-1.0 folder in the native library path
      // (there are natives adjacent to it, so this will work)
      gstreamerPluginPath = searchLibraryPath("gstreamer-1.0");
      if (gstreamerPluginPath == null) {
        gstreamerPluginPath = searchClassPath("gstreamer-1.0");
      }

      if (gstreamerPluginPath == null) {
        gstreamerPluginPath = "";
        gstreamerLibPath = "";
        usingGStreamerSystemInstall = true;
      } else {
        File gstreamerLibDir = new File(gstreamerPluginPath).getParentFile();
        gstreamerLibPath = gstreamerLibDir.getAbsolutePath();
      }
    }
}
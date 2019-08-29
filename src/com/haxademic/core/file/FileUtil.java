package com.haxademic.core.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.text.StringFormatter;

import processing.core.PImage;

public class FileUtil {
	
	public static String DATA_PATH = null;
	public static String OUTPUT_PATH = null;
	public static String BIN_PATH = null;
	public static String HAX_PATH = null;
	public static String UTIL_PATH = null;
	public static String SCRIPTS_PATH = null;
	public static String WWW_PATH = null;

	public static final String NEWLINE = "\r\n";	// works on windows, where "\n" doesn't
	public static final String SEPARATOR = File.separator;
	
	// HAXADEMIC PATHS
	
	public static String getHaxademicPath() {
		if( HAX_PATH != null ) return HAX_PATH;
		String binPath = getProjectAbsolutePath();
		Boolean hasBin = ( binPath.lastIndexOf(File.separator + "bin") != -1 ) ? true : false;
		HAX_PATH = ( hasBin == true ) ? binPath.substring(0, binPath.lastIndexOf(File.separator + "bin") ) : binPath;
		return HAX_PATH;
	}
	
	public static String getHaxademicBinPath() {
		if( BIN_PATH != null ) return BIN_PATH;
		BIN_PATH = getHaxademicPath().concat(File.separator + "bin" + File.separator);
		return BIN_PATH;
	}
	
	public static String getHaxademicDataPath() {
		if( DATA_PATH != null ) return DATA_PATH;
		DATA_PATH = getHaxademicPath().concat(File.separator + "data" + File.separator);
		return DATA_PATH;
	}
	
	public static String getHaxademicWebPath() {
		if( WWW_PATH != null ) return WWW_PATH;
		WWW_PATH = getHaxademicPath().concat(File.separator + "www" + File.separator);
		return WWW_PATH;
	}
	
	public static String getHaxademicUtilScriptsPath() {
		if( UTIL_PATH != null ) return UTIL_PATH;
		UTIL_PATH = getHaxademicPath().concat(File.separator + "util" + File.separator);
		return UTIL_PATH;
	}
	
	public static String getHaxademicScriptsPath() {
		if( SCRIPTS_PATH != null ) return SCRIPTS_PATH;
		SCRIPTS_PATH = getHaxademicPath().concat(File.separator + "scripts" + File.separator);
		return SCRIPTS_PATH;
	}
	
	public static String getHaxademicOutputPath() {
		if( OUTPUT_PATH != null ) return OUTPUT_PATH;
		OUTPUT_PATH = getHaxademicPath().concat(File.separator + "output" + File.separator);
		return OUTPUT_PATH;
	}
	
	// PATH HELPERS
	
	public static String pathForFile(String filePath) {
		filePath = safePath(filePath);
		return filePath.substring(0, filePath.lastIndexOf(File.separator));
	}

	public static String fileNameFromPath(String filePath) {
		return (new File(filePath)).getName();
	}
	
	public static File fileFromPath(String filePath) {
		return new File(filePath);
	}
	
	public static String getProjectAbsolutePath() {
		return new java.io.File("").getAbsolutePath();
	}
	
	public static String getFile(String path) {
		path = safePath(path);
		return getHaxademicDataPath() + path;
	}
	
	public static String getScript(String path) {
		return getHaxademicScriptsPath() + path;
	}
	
	public static String safePath(String path) {
		path = path.replace("/", File.separator);
		path = path.replace("\\", File.separator);
		return path;
	}
	
	// CHECK FILE EXISTENCE
	
	public static Boolean fileOrPathExists( String path ) {
		File f = new File( path );
		return f.exists();
	}
	
	public static Boolean fileExists( String path ) {
		return new File( path ).isFile();
	}
	
	// LIST FILES / DIRS
	
	/**
	 * Finds files of a specific type within a directory
	 * @param path Directory to search
	 * @param type File extension to search for
	 */
	public static ArrayList<String> getFilesInDirOfType( String directory, String type ) {
		type = "."+type;
		File dir = new File( directory );
		String[] children = dir.list();
		Arrays.sort(children);
		ArrayList<String> filesOfType = new ArrayList<String>();
		if (children == null) {
			P.println("FileUtil error: couldn't find file or directory");
		} else {
		    for (int i=0; i < children.length; i++) {
		        String filename = children[i];
		        if( filename.indexOf( type ) != -1 ) {	
			        	// P.println(filename);
			        	filesOfType.add( filename );
		        }
		    }
		}
		return filesOfType;
	}
	
	public static ArrayList<String> getFilesInDirOfTypes( String directory, String formats ) {
		File dir = new File( directory );
		String[] children = dir.list();
		ArrayList<String> filesOfType = new ArrayList<String>();
		if (children == null) {
			P.println("FileUtil error: couldn't find file or directory");
		} else {
			String[] extensions = formats.split(",");
		    for (int i=0; i < children.length; i++) {
		        String filename = children[i];
		        boolean added = false;
		        for( int j=0; j < extensions.length; j++ ) {
			        if( filename.indexOf( "." + extensions[j] ) != -1 && added == false) {	
			        	filesOfType.add( directory + "/" + filename );
			        	added = true;
			        }
		        }
		    }
		}
		return filesOfType;		
	}
	
	public static void shuffleFileList( ArrayList<String> files ) {
		String cur = null;
		String temp = null;
		int swapIndex = 0;
		for( int i=0; i < files.size(); i++ ) {
			swapIndex = MathUtil.randRange(0, files.size() - 1);
			temp = files.get( swapIndex );
			cur = files.get( i );
			files.set( swapIndex, cur );
			files.set( i, temp );
		}
	}
	
	public static void getFilesInDir( String directory ) {
		File dir = new File( directory );

		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        DebugUtil.print( filename );
		    }
		}

		// It is also possible to filter the list of returned files.
		// This example does not return any files that start with `.'.
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".");
		    }
		};
		children = dir.list(filter);


		// The list of files can also be retrieved as File objects
		// File[] files = dir.listFiles();

		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		};
		File[] files = dir.listFiles(fileFilter);
		P.println( files.length );
	}
	
	public static String[] getDirsInDir( String directory ) {
		File dir = new File( directory );
		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		};
		File[] files = dir.listFiles(fileFilter);
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].toString();
		}
		return fileNames;
	}
	
	public static ArrayList<PImage> loadImagesFromDir(String directory, String formats) {
		ArrayList<PImage> images = new ArrayList<PImage>();
				
		// parse and loop through the format/extensions and load from the directory each time (there's certainly a better way to do this)
		String[] extensions = formats.split(",");
		for( int i=0; i < extensions.length; i++ ) {
			// P.println("--- loading "+extensions[i] + " images ---");
			ArrayList<String> imageFiles = FileUtil.getFilesInDirOfType( directory, extensions[i] );
			for( int j=0; j < imageFiles.size(); j++ ) {
				// P.println("--- "+directory + imageFiles.get(j));
				images.add( P.p.loadImage( directory + imageFiles.get(j) ) );
			}
		}
		// Arrays.sort(children);
		return images;
	}
	
	public static String[] getFilesAndDirsInDir( String directory ) {
		File dir = new File( directory );
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory() || file.getName().endsWith("png") || file.getName().endsWith("gif") || file.getName().endsWith("jpg") || file.getName().endsWith("mov") || file.getName().endsWith("mp4");
		    }
		};
		File[] files = dir.listFiles(fileFilter);
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			if(files[i].toString().indexOf("\\._") == -1) {		// ignore weird hidden files
				fileNames[i] = files[i].toString();
			}
		}
		return fileNames;
	}
	
	public static ArrayList<String> wordsFromTextFile(String textFilePath) {
		String lines[] = P.p.loadStrings(textFilePath);
		ArrayList<String> words = new ArrayList<String>();
		String wordsPerLine[];
		for( int i=0; i < lines.length; i++ ) {
			if( lines[i].length() >= 1 ) {
				wordsPerLine = lines[i].split(" ");
				for( int j=0; j < wordsPerLine.length; j++ ) {
					if( wordsPerLine[j] != " " && wordsPerLine[j].length() >= 1 ) {
						words.add(StringFormatter.toAlphaNumericCharsNoDecimal(wordsPerLine[j]));
					}
				}
			}
		}
		return words;
	}

	public static void replaceStringInFile(String fileName, String oldString, String newString) throws IOException {
		Path path = Paths.get(fileName);
		Charset charset = StandardCharsets.UTF_8;
		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll(oldString, newString);
		Files.write(path, content.getBytes(charset));
	}
	
	// CREATE / APPEND

	/**
	 * Creates a new directory on the machine's filesystem
	 * @param path Directory to create
	 */
	public static boolean createDir( String path ) {
		File f = new File( path );
		try {
			boolean success = f.mkdirs();
			return success;
		} catch(Exception e) {
		    e.printStackTrace();
		    return false;
		}
	}
	
	// from: https://stackoverflow.com/a/50418060/352456
	public static void copyFolder(String srcPath, String destPath) throws IOException {
		Path src = Paths.get(srcPath);
		Path dest = Paths.get(destPath);
	    Files.walk(src)
	        .forEach(source -> copy(source, dest.resolve(src.relativize(source))));
	}
	
	public static void copy(Path source, Path dest) {
	    try {
	        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
	    } catch (Exception e) {
	        throw new RuntimeException(e.getMessage(), e);
	    }
	}
	
	/**
	 * Simple method to write text to a file
	 * @param file The filename (with full path) to write to
	 * @param text Text to write to the file
	 */
	public static final void writeTextToFile( String file, String text ) {
		new Thread(new Runnable() { public void run() {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	            writer.write( text );
	            writer.close();
			} catch (IOException e) { e.printStackTrace(); }
		}}).start();
	}
	
	/**
	 * Simple method to append text to a file
	 * @param file The filename (with full path) to write to
	 * @param text Text to appendto the file
	 */
	public static final void appendTextToFile( String file, String text ) {
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.print(text);
		    out.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Copies one file to another.
	 * From: http://stackoverflow.com/a/115086/352456
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile( String sourcePath, String destPath ) throws IOException {
		File sourceFile = new File( sourcePath );
		File destFile = new File( destPath );
		
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}

	// READ FILE
	
	public static String[] readTextFromFile(String textFilePath) {
		String[] linesArr = null;
		Path filePath = Paths.get(textFilePath);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(filePath, charset);
			linesArr = new String[lines.size()];
			for (int i = 0; i < lines.size(); i++) {
				linesArr[i] = lines.get(i);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return linesArr;
	}
	
	public static String textLinesJoined(String[] stringLines) {
		return String.join(FileUtil.NEWLINE, stringLines);
	}
	
	public static String getFileSize(String filePaTh) {
		// from: https://github.com/jeffThompson/ProcessingTeachingSketches/blob/master/Utilities/GetFileSize/GetFileSize.pde
		File f  = new File(filePaTh);           // read into File object
		float fs = f.length();                   // get file size in bytes

		String fileSize = "";
		if (fs < 1024) {                        // less than 1 kb, measure in bytes
			fileSize += fs + " bytes";
		} else if (fs > 1024 && fs < 1048576) {   // 1 kb - .99 MB, measure in kb
			fs /= 1024f;
			fileSize += fs + " kb";
		} else {
			fs /= 1048576f;
			fileSize += fs + " MB";               // larger? measure in megabytes
		}
		return fileSize;
	}

	// DELETION
	
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		return file.delete();
	}
	
	public static boolean deleteDir(String path) {
		if(FileUtil.fileOrPathExists(path) == false) return false;
		try {
			Files.walk(Paths.get(path))
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	
}

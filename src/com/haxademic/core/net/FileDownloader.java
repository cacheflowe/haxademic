package com.haxademic.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.haxademic.core.file.FileUtil;

public class FileDownloader {
	
	public static boolean downloadFile(String fileURL, String localPath, boolean overwrite) {
		// tell servers that we're a cool web browser & prevent 403 forbidden errors
		System.setProperty("http.agent", "Chrome");
		
		// remove file & replace if one already exists
		if(overwrite && FileUtil.fileExists(localPath)) {
			FileUtil.deleteFile(localPath);
		}
		
		// modern file downloader
		// from: https://stackoverflow.com/a/32472138/352456
		try(InputStream in = new URL(fileURL).openStream()){
		    Files.copy(in, Paths.get(localPath));
		    return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean downloadFiles(String[] fileURLs, String[] localPaths, boolean overwrite) {
		boolean allDownloaded = true;
		
		for (int i = 0; i < fileURLs.length; i++) {
			boolean downloadResult = downloadFile(fileURLs[i], localPaths[i], overwrite);
			if(downloadResult == false) allDownloaded = false;
		} 
		
		return allDownloaded;
	}
	
}

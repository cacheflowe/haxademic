package com.haxademic.demo.net;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.net.FileDownloader;

public class Demo_FileDownloader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
	}
	

	protected void drawApp() {
		background(0);
	}
	
	protected void downloadSingleFile() {
		FileUtil.createDir(FileUtil.getPath("downloads"));
		FileDownloader.downloadFile(
				"https://cacheflowe.com/images/code/installation/silhouect.mp4", 
				FileUtil.getPath("downloads/silhouect.mp4"), 
				true);
	}
	
	protected void downloadMultipleFiles() {
		FileUtil.createDir(FileUtil.getPath("downloads"));
		FileDownloader.downloadFiles(
				new String[] {
						"https://cacheflowe.com/images/code/installation/nike-beacon-01.jpg", 
						"https://cacheflowe.com/images/code/installation/silhouect.mp4", 
				},
				new String[] {
						FileUtil.getPath("downloads/nike-beacon-01.jpg"), 
						FileUtil.getPath("downloads/silhouect.mp4"), 
				},
				true);
	}
	
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			new Thread(new Runnable() { public void run() {
				downloadSingleFile();
			}}).start();
		}

		if(p.key == 'a') {
			new Thread(new Runnable() { public void run() {
				downloadMultipleFiles();
			}}).start();
		}
	}
	
}






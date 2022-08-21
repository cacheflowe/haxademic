package com.haxademic.core.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.haxademic.core.app.P;

public class MediaMetaData {

	public static ArrayList<String> getMetaDataForMedia(String mediaPath, boolean summary) {
		ArrayList<String> entries = new ArrayList<String>();
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(new File(mediaPath));
			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					if(summary == false) {
						entries.add(tag.toString());
					} else {
						if(tag.getDirectoryName().equals("File Type") ||
						   tag.getDirectoryName().equals("File") ||
						   tag.getTagName().toLowerCase().indexOf("width") != -1 ||
						   tag.getTagName().toLowerCase().indexOf("height") != -1) {
							entries.add(tag.toString());
						}
					}
				}
			}
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
	public static void printAllForMedia(String mediaPath, boolean summary) {
		P.out("====================================");
		P.out("Metadata for:", mediaPath);
		ArrayList<String> entries = getMetaDataForMedia(mediaPath, summary);
		for (int i = 0; i < entries.size(); i++) {
			P.out(entries.get(i));
		}
		P.out("====================================");
	}
	
}

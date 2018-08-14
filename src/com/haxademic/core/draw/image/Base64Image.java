package com.haxademic.core.draw.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.imageio.ImageIO;

import processing.core.PImage;

public class Base64Image {
	// adapted from: https://forum.processing.org/two/discussion/6958/pimage-base64-encode-and-decode
	// more info: https://docs.oracle.com/javase/tutorial/2d/images/saveimage.html
	// String writerNames[] = ImageIO.getWriterFormatNames();
	// [JPG, jpg, bmp, BMP, gif, GIF, WBMP, png, PNG, wbmp, jpeg, JPEG]
	
	public static String encodePImageToBase64(PImage img, String format) throws UnsupportedEncodingException, IOException {
		return encodePImageToBase64((BufferedImage)img.getNative(), format);
	}

	public static String encodePImageToBase64(BufferedImage img, String format) throws UnsupportedEncodingException, IOException {
		String result = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, format, out);
		result = Base64.getEncoder().encodeToString(out.toByteArray());
		return result;
	}
	
	public static PImage decodePImageFromBase64(String base64Str) {
		PImage result = null;
		byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
		ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
		BufferedImage bImageFromConvert;
		try {
			bImageFromConvert = ImageIO.read(in);
			BufferedImage convertedImg = new BufferedImage(bImageFromConvert.getWidth(), bImageFromConvert.getHeight(), BufferedImage.TYPE_INT_ARGB);
			convertedImg.getGraphics().drawImage(bImageFromConvert, 0, 0, null);
			result = new PImage(convertedImg);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

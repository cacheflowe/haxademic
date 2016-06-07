package com.haxademic.core.image;

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

	public static String encodePImageToBase64(PImage img, String format) throws UnsupportedEncodingException, IOException {
		String result = null;
		BufferedImage buffImage = (BufferedImage)img.getNative();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(buffImage, format, out);
		byte[] bytes = out.toByteArray();
		result = Base64.getEncoder().encodeToString(bytes);
		return result;
	}

	public static PImage decodePImageFromBase64(String base64Str) throws IOException {
		PImage result = null;
		byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
		ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
		BufferedImage bImageFromConvert = ImageIO.read(in);
		BufferedImage convertedImg = new BufferedImage(bImageFromConvert.getWidth(), bImageFromConvert.getHeight(), BufferedImage.TYPE_INT_ARGB);
		convertedImg.getGraphics().drawImage(bImageFromConvert, 0, 0, null);
		result = new PImage(convertedImg);
		return result;
	}
	
}

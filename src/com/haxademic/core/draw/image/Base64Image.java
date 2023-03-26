package com.haxademic.core.draw.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import processing.core.PImage;

public class Base64Image {
	// adapted from: https://forum.processing.org/two/discussion/6958/pimage-base64-encode-and-decode
	// more info: https://docs.oracle.com/javase/tutorial/2d/images/saveimage.html
	// String writerNames[] = ImageIO.getWriterFormatNames();
	// [JPG, jpg, bmp, BMP, gif, GIF, WBMP, png, PNG, wbmp, jpeg, JPEG]
	
	public static String encodePImageToBase64(PImage img, String format) {
		try {
			return encodeNativeImageToBase64((BufferedImage)img.getNative(), format);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String encodeNativeImageToBase64(BufferedImage img, String format) throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, format, out);
		String result = Base64.getEncoder().encodeToString(out.toByteArray());
		out.close();
		return result;
	}
	
	public static String encodeImageToBase64Jpeg(PImage img, float quality) {
		return encodeImageToBase64Jpeg((BufferedImage)img.getNative(), quality);
	}
		
	public static String encodeImageToBase64Jpeg(BufferedImage img, float quality) {
		try {
			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality(quality);
	
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jpgWriter.setOutput(new MemoryCacheImageOutputStream(out));
			IIOImage outputImage = new IIOImage(img, null, null);
			jpgWriter.write(null, outputImage, jpgWriteParam);
			jpgWriter.dispose();
			
			out.flush();
			String result = Base64.getEncoder().encodeToString(out.toByteArray());
			out.close();
			return result;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
			result = ImageUtil.bufferedToPImage(convertedImg);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

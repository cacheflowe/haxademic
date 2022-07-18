package com.haxademic.core.net;

import com.cage.zxing4p3.ZXING4P;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import processing.core.PImage;

public class QRCode
extends ZXING4P {
	
	// overriden to not keep creating new instances of the QR PImage
	// in the case that we want to keep updating the URL of a single image
	protected PImage pImage;
	
	public PImage image() {
		return pImage;
	}
	
	public PImage updateQRCode(String content, int width, int height) {
		return updateQRCode(content, width, height, 0, 16777215);
	}
	
	public PImage updateQRCode(String content, int width, int height, int colorBG, int colorFG) {
		if(pImage == null) pImage = new PImage(width, height);

		QRCodeWriter encoder = new QRCodeWriter();

		try {
			BitMatrix bitMatrix = encoder.encode(content, BarcodeFormat.QR_CODE, width, height);
			
			// COPY THE BYTEMATRIX TO THE PIMAGE
			for(int i=0; i<width; i++)
				for(int j=0; j<height; j++) {
					int colorValue = colorFG;
					if(!bitMatrix.get(j, i)) colorValue = colorBG;
					// ADD THE PIXEL TO THE IMAGE
					pImage.set(i, j, colorValue);
				} // for(int j=0; j<height; j++)
		} catch ( Exception e ) {
			System.out.println("Error generating QRCode image (PImage generateQRCode) " + e);
		} // try
		
		return pImage;
	}

}

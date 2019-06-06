package com.haxademic.core.hardware.printer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;

public class PrintPageDirect {

	// to draw an image into the printable canvas, look at:  
	// https://docs.oracle.com/javase/tutorial/2d/images/drawimage.html
	
	// original printing code example from:
	// http://www.java2s.com/Code/Java/2D-Graphics-GUI/PrintinJavapageformatanddocument.htm

	// printable size seems to be: "468 x 648", based on Java print demo. 2550 x 3300 is the standard pixel dimensions of a printed page 
	
	protected PGraphics printBuffer; 
	protected boolean useDialog; 
	
	public static int PRINT_W = 468 * 6;
	public static int PRINT_H = 648 * 6;
	public static float ASPECT;

	public PrintPageDirect() {
		this(false);
	}
	
	public PrintPageDirect(boolean useDialog) {
		this.useDialog = useDialog;
		ASPECT = ((float) PRINT_W / (float) PRINT_H);
				
		// printBuffer = p.createGraphics(2550, 3300, PRenderers.P3D); // apparent size of an 8.5 x 11 paper
		printBuffer = P.p.createGraphics(PRINT_W, PRINT_H, PRenderers.P3D);
		printBuffer.smooth(8);
	}
	
	public PGraphics printBuffer() {
		return printBuffer;
	}
	
	public void printImage(PGraphics source) {
		// letterbox copy to page buffer
		printBuffer.beginDraw();
		printBuffer.background(255);
		printBuffer.fill(255);
		
		// copy `source` to buffer with best rotation to fill page
		float sourceAspect = (float) source.width / (float) source.height;
		float sourceSpectDiff = P.abs(ASPECT - sourceAspect);
		float sourceAspectRotated = (float) source.height / (float) source.width;
		float sourceSpectRotatedDiff = P.abs(ASPECT - sourceAspectRotated);
		
		if(sourceSpectDiff < sourceSpectRotatedDiff) {
			// keep portrait
			P.out("Printing portrait");
			ImageUtil.drawImageCropFill(source, printBuffer, false);
		} else {
			// rotate to print bigger on page
			P.out("Printing landscape (auto-rotated to fit page better)");
			PG.setDrawCenter(printBuffer);
			PG.setCenterScreen(printBuffer);
			float scaleToFitW = MathUtil.scaleToTarget(source.width, printBuffer.height);
			float scaleToFitH = MathUtil.scaleToTarget(source.height, printBuffer.width);
			float scaleToFit = (scaleToFitH < scaleToFitW) ? scaleToFitH : scaleToFitW;
			printBuffer.pushMatrix();
			printBuffer.rotate(-P.HALF_PI);
			printBuffer.image(source, 0, 0, source.width * scaleToFit, source.height * scaleToFit);
			printBuffer.popMatrix();
			PG.setDrawCorner(printBuffer);
		}
		
		printBuffer.endDraw();
		P.p.debugView.setTexture(printBuffer);
		
		// send local buffer to print
		sendToDefaultPrinter();
	}
	
	public void sendToDefaultPrinter() {
		// configure the printing job & simple single-page document
		PrinterJob printJob = PrinterJob.getPrinterJob();
		Book book = new Book();
		book.append(new ImagePage(), printJob.defaultPage());
		PageFormat documentPageFormat = new PageFormat();
		/*
		// new settings to play with
		Paper pape = new Paper();
		pape.setSize(10000, 6000);
		pape.setImageableArea(0, 0, 10000, 6000);
		documentPageFormat.setPaper(pape);
		*/
		documentPageFormat.setOrientation(PageFormat.LANDSCAPE);
		printJob.setPageable(book);

		// use the print dialog UI or just print it!
		if(useDialog) {
			if (printJob.printDialog()) printPage(printJob);
		} else {
			printPage(printJob);
		}
	}
	
	protected void printPage(PrinterJob printJob) {
		try {
			printJob.print();
		} catch (Exception PrintException) {
			PrintException.printStackTrace();
		}
	}
	
	private class ImagePage implements Printable {
		
		public int print(Graphics g, PageFormat pageFormat, int page) {
			// Create the Graphics2D object, Translate the origin to 0,0 for the top left corner, Set the default drawing color to black
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			g2d.setPaint(Color.black);
			
			// Print the full-page PImage by converting to native BufferedImage
			P.out("PageFormat size: ", (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
			g2d.drawImage((BufferedImage) printBuffer.getNative(), 0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight(), null);
			
			return (PAGE_EXISTS);
		}
	}


	//////////////////////////////////////////////////////////////////////////////////////
	// Extra demo code below in case we want to do multi-page layouts
	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	
	//--- Private instances declarations
	private final static int POINTS_PER_INCH = 72;

	/**
	 * Constructor: Example3
	 * <p>
	 *  
	 */
	public void doPrintDemo() {

		//--- Create a new PrinterJob object
		PrinterJob printJob = PrinterJob.getPrinterJob();

		//--- Create a new book to add pages to
		Book book = new Book();

		//--- Add the cover page using the default page format for this print job
		book.append(new ImagePage(), printJob.defaultPage());
//		book.append(new IntroPage(), printJob.defaultPage());

		//--- Add the document page using a landscape page format
		PageFormat documentPageFormat = new PageFormat();
		documentPageFormat.setOrientation(PageFormat.PORTRAIT);
		//	    book.append(new Document(), documentPageFormat);

		//--- Add a third page using the same painter
		//	    book.append(new Document(), documentPageFormat);

		//--- Tell the printJob to use the book as the pageable object
		printJob.setPageable(book);

		//--- Show the print dialog box. If the user click the
		//--- print button we then proceed to print else we cancel
		//--- the process.

		// remove the print dialog and just print it!
		//	    if (printJob.printDialog()) {
		try {
			printJob.print();
		} catch (Exception PrintException) {
			PrintException.printStackTrace();
		}
		//	    }

	}

	/**
	 * Class: IntroPage
	 * <p>
	 * 
	 * This class defines the painter for the cover page by implementing the
	 * Printable interface.
	 * <p>
	 * 
	 * @author Jean-Pierre Dube <jpdube@videotron.ca>
	 * @version 1.0
	 * @since 1.0
	 * @see Printable
	 */
	@SuppressWarnings("unused")
	private class IntroPage implements Printable {

		/**
		 * Method: print
		 * <p>
		 * 
		 * @param g
		 *            a value of type Graphics
		 * @param pageFormat
		 *            a value of type PageFormat
		 * @param page
		 *            a value of type int
		 * @return a value of type int
		 */
		public int print(Graphics g, PageFormat pageFormat, int page) {

			//--- Create the Graphics2D object
			Graphics2D g2d = (Graphics2D) g;

			//--- Translate the origin to 0,0 for the top left corner
			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());

			//--- Set the default drawing color to black
			g2d.setPaint(Color.black);

			//--- Draw a border arround the page
			Rectangle2D.Double border = new Rectangle2D.Double(0, 0, pageFormat
					.getImageableWidth(), pageFormat.getImageableHeight());
			g2d.draw(border);

			//--- Print the title
			String titleText = "Printing in Java Part 2";
			Font titleFont = new Font("helvetica", Font.BOLD, 36);
			g2d.setFont(titleFont);

			//--- Compute the horizontal center of the page
			FontMetrics fontMetrics = g2d.getFontMetrics();
			double titleX = (pageFormat.getImageableWidth() / 2)
					- (fontMetrics.stringWidth(titleText) / 2);
			double titleY = 3 * POINTS_PER_INCH;
			g2d.drawString(titleText, (int) titleX, (int) titleY);

			return (PAGE_EXISTS);
		}
	}

	/**
	 * Class: Document
	 * <p>
	 * 
	 * This class is the painter for the document content.
	 * <p>
	 * 
	 * 
	 * @author Jean-Pierre Dube <jpdube@videotron.ca>
	 * @version 1.0
	 * @since 1.0
	 * @see Printable
	 */
	@SuppressWarnings("unused")
	private class Document implements Printable {

		/**
		 * Method: print
		 * <p>
		 * 
		 * @param g
		 *            a value of type Graphics
		 * @param pageFormat
		 *            a value of type PageFormat
		 * @param page
		 *            a value of type int
		 * @return a value of type int
		 */
		public int print(Graphics g, PageFormat pageFormat, int page) {

			//--- Create the Graphics2D object
			Graphics2D g2d = (Graphics2D) g;

			//--- Translate the origin to 0,0 for the top left corner
			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());

			//--- Set the drawing color to black
			g2d.setPaint(Color.black);

			//--- Draw a border arround the page using a 12 point border
			g2d.setStroke(new BasicStroke(12));
			Rectangle2D.Double border = new Rectangle2D.Double(0, 0, pageFormat
					.getImageableWidth(), pageFormat.getImageableHeight());

			g2d.draw(border);

			//--- Print page 1
			if (page == 1) {
				//--- Print the text one inch from the top and laft margins
				g2d.drawString("This the content page of page: " + page,
						POINTS_PER_INCH, POINTS_PER_INCH);
				return (PAGE_EXISTS);
			}

			//--- Print page 2
			else if (page == 2) {
				//--- Print the text one inch from the top and laft margins
				g2d.drawString("This the content of the second page: " + page,
						POINTS_PER_INCH, POINTS_PER_INCH);
				return (PAGE_EXISTS);
			}

			//--- Validate the page
			return (NO_SUCH_PAGE);

		}
	}

}

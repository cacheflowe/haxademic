package com.haxademic.demo.hardware.printer;

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
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;

public class Demo_PrintDirect 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics printBuffer; 

	// to draw an image into the printable canvas, look at:  
	// https://docs.oracle.com/javase/tutorial/2d/images/drawimage.html

	// original printing code example from:
	// http://www.java2s.com/Code/Java/2D-Graphics-GUI/PrintinJavapageformatanddocument.htm

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty(AppSettings.WIDTH, 800 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty(AppSettings.FULLSCREEN, false );
	}

	public void setupFirstFrame () {
		//		doPrintDemo();
		
		// printable size seems to be: 468 648
//		printBuffer = p.createGraphics(2550, 3300, PRenderers.P3D); // apparent size of an 8.5 x 11 paper
		printBuffer = p.createGraphics(468 * 5, 648 * 5, PRenderers.P3D);
		printBuffer.smooth(8);
	}

	protected void updateBuffer() {
		printBuffer.beginDraw();
		printBuffer.background(255);
		printBuffer.fill(255);
		printBuffer.stroke(0);
		printBuffer.strokeWeight(6);
		DrawUtil.setDrawCorner(printBuffer);
		printBuffer.rect(0, 0, printBuffer.width, printBuffer.height);
		DrawUtil.setCenterScreen(printBuffer);
		DrawUtil.setDrawCenter(printBuffer);
		for (int i = 0; i < 200; i++) {
			float randSize = p.random(20, 500);
			float randRot = p.random(P.TWO_PI);
			printBuffer.pushMatrix();
			printBuffer.rotate(randRot);
			printBuffer.rect(p.random(0, printBuffer.width), p.random(0, printBuffer.height), randSize, randSize);
			printBuffer.popMatrix();
		}
		printBuffer.endDraw();
	}

	public void drawApp() {
		p.background( 0 );
		updateBuffer();
		ImageUtil.cropFillCopyImage(printBuffer, p.g, false);

		//		if(p.frameCount == 10) doPrintDemo();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') doPrintDemo();
	}

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

	private class ImagePage implements Printable {
		
		public int print(Graphics g, PageFormat pageFormat, int page) {
			
			//--- Create the Graphics2D object
			Graphics2D g2d = (Graphics2D) g;
			
			//--- Translate the origin to 0,0 for the top left corner
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			
			//--- Set the default drawing color to black
			g2d.setPaint(Color.black);
			
			
			// draw an image
			P.out("pageFormat imageable size: ", (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
			g2d.drawImage((BufferedImage) printBuffer.getNative(), 0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight(), null);
			
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

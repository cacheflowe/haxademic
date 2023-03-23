package com.haxademic.demo.draw.shapes.polygons;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.SystemUtil;

public class Demo_RectangleSubdivision
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<Rectangle> _rectangles;
	protected int _numDivisions = 100;
	

	protected void firstFrame() {
		newRectanges();
	}
	
	public void newRectanges() {
		// start with 1 rect
		_rectangles = new ArrayList<Rectangle>();
		_rectangles.add(new Rectangle(0, 0, p.width, p.height));
		
		// split, remove, and add split children
		for( int i=0; i < _numDivisions; i++ ) {
			int randIndex = MathUtil.randRange(0, _rectangles.size()-1);
			Rectangle rectToSplit = _rectangles.get(randIndex);
			if(rectToSplit.width > 30 || rectToSplit.height > 30) {
				_rectangles.remove(randIndex);
				if(rectToSplit.width > rectToSplit.height) {
					// quantized split
					float horizSplit = Math.round(10f * MathUtil.randRangeDecimal(0.3f, 0.7f))/10f;	
					// split the width
					int totalW = rectToSplit.width;
					int rect1W = P.round(rectToSplit.width * horizSplit);
					int rect2W = totalW - rect1W;
					// create new rects
					_rectangles.add(new Rectangle(rectToSplit.x, rectToSplit.y, rect1W, rectToSplit.height));
					_rectangles.add(new Rectangle(rectToSplit.x + rect1W - 1, rectToSplit.y, rect2W + 1, rectToSplit.height));	// +/- 1 to overlap the rects by a pixel. remove this if there's not a stroke to overlap
				} else {
					float vertSplit = Math.round(10f * MathUtil.randRangeDecimal(0.3f, 0.7f))/10f;	// quantized split
					// split the height
					int totalH = rectToSplit.height;
					int rect1H = P.round(rectToSplit.height * vertSplit);
					int rect2H = totalH - rect1H;
					// create new rects
					_rectangles.add(new Rectangle(rectToSplit.x, rectToSplit.y, rectToSplit.width, rect1H));
					_rectangles.add(new Rectangle(rectToSplit.x, rectToSplit.y + rect1H - 1, rectToSplit.width, rect2H + 1));
				}
			}
		}
		
		// debug print
//		for (Rectangle rect : _rectangles) {
//			P.println(rect.x, rect.y, rect.width, rect.height);
//		}
	}

	protected void drawApp() {
		p.background(0);
		p.fill(0);
		p.stroke(255);
		for (Rectangle rect : _rectangles) {
			p.fill(255);
			p.pushMatrix();
			p.translate(rect.x, rect.y);
			PG.drawStrokedRect(p.g, rect.width, rect.height, 1, 0x00000000, 0xffffffff);
			// p.text(rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height, 10, 10, rect.width, 1000);
			p.popMatrix();
//			p.rect(rect.x, rect.y, rect.width, rect.height);	// imperfect rects!
		}
	}
	
	public void exportVertices() {
		String export = "";
		export += "#group#\n";
		for (Rectangle rect : _rectangles) {
			export += "#poly#";
			export += rect.x+","+rect.y+",";
			export += (rect.x+rect.width)+","+rect.y+",";
			export += (rect.x+rect.width)+","+(rect.y+rect.height)+",";
			export += rect.x+","+(rect.y+rect.height);
			export += "\n";
		}
		FileUtil.writeTextToFile(FileUtil.haxademicDataPath() + "text/mapping/mapping-"+SystemUtil.getTimestamp()+".txt", export);
	}

	
	public void mousePressed() {
		super.mousePressed();
		newRectanges();
	}
	
	public void keyPressed() {
		if(p.key == 'e') {
			exportVertices();
		}
	}

}
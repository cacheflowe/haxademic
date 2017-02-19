package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.text.CustomFontText2D;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

public class WordsOverlay2d
extends ElementBase 
implements IVizElement {
	
	protected float _amp;
	
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;

	protected ArrayList<String> _words;
	protected String _result;
	protected CustomFontText2D _fontRenderer;
	protected int _fontSize = 100;
	protected int _wordIndex = 0;


	public WordsOverlay2d( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		_fontSize = Math.round(p.width * 0.1f);
		_fontRenderer = new CustomFontText2D( p, FileUtil.getHaxademicDataPath() + "fonts/HelveticaNeueLTStd-Blk.ttf", (int)_fontSize, p.color(255), CustomFontText2D.ALIGN_CENTER, p.width, _fontSize + 20 );
		_fontRenderer.setTextColor(p.color(0, 0, 0, 127), p.color(100, 100));
		
		String lines[] = p.loadStrings(FileUtil.getHaxademicDataPath() + "text/gibson-text.txt");
		_words = new ArrayList<String>();
		String wordsPerLine[];
		for( int i=0; i < lines.length; i++ ) {
			if( lines[i].length() >= 1 ) {
				wordsPerLine = lines[i].split(" ");
				for( int j=0; j < wordsPerLine.length; j++ ) {
					if( wordsPerLine[j] != " " && wordsPerLine[j].length() >= 1 ) {
						_words.add( wordsPerLine[j].replaceAll("[^A-Za-z0-9\']", " ").toUpperCase() );
					}
				}
			}
		}
		_wordIndex = MathUtil.randRange( 0, _words.size() );
	}
	
	public void setDrawProps(float width, float height) {

	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();

		p.translate(0, 0, -1000);
		DrawUtil.setColorForPImage(p);
		DrawUtil.setPImageAlpha(p, 1);
		drawText();
		
		p.popMatrix();
	}
	
	public void drawText() {
		p.image( _fontRenderer.getTextPImage(), 0, 20 );

	}

	public void updateTiming() {
		_wordIndex++;
		if( _wordIndex >= _words.size() ) _wordIndex = 0;
		_fontRenderer.setTextColor(p.color(0, 0, 0, 127), p.color(100, 100));
		_fontRenderer.updateText( _words.get(_wordIndex) );
	}

	public void reset() {
		
	}

	public void dispose() {
		_audioData = null;
	}
	
}

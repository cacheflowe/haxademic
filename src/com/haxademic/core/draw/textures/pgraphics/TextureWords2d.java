package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PFont;
import toxi.color.TColor;

public class TextureWords2d 
extends BaseTexture {

	protected float _amp;
	
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;

	protected ArrayList<String> _words;
	protected String _result;
	protected PFont textFont;
	protected int _wordIndex = 0;
	protected int showFrame = 0;
	protected int showFrameCount = 20;


	public TextureWords2d( int width, int height ) {
		super(width, height);
		

		// load text file
		String lines[] = P.p.loadStrings(FileUtil.haxademicDataPath() + "text/gibson-text.txt");
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

		// load font
		textFont = P.p.createFont(FileUtil.getPath("fonts/OhmegaSans-Regular.ttf"), height * 0.150f);
	}
	
	public void newLineMode() {
	}

	public void draw() {
		// feedback alpha fade
		FeedbackRadialFilter.instance().setAmp(1f / 255f);
		FeedbackRadialFilter.instance().setSampleMult(0.93f);
		FeedbackRadialFilter.instance().setWaveAmp(0f);
		FeedbackRadialFilter.instance().setWaveFreq(0f);
		FeedbackRadialFilter.instance().setAlphaMult(0.94f);
		FeedbackRadialFilter.instance().applyTo(pg);

		// draw text
		showFrameCount = 50;
		if(P.p.frameCount < showFrame + showFrameCount) {
			pg.pushMatrix();
			pg.pushStyle();
			pg.fill(255);
			pg.noStroke();
			pg.textFont(textFont);
			pg.textAlign(P.CENTER, P.CENTER);
			pg.text(_words.get(_wordIndex), 0, 0, width, height);
			pg.popStyle();
			pg.popMatrix();
		}

	}
	
	public void updateTiming() {
		_wordIndex++;
		if( _wordIndex >= _words.size() ) _wordIndex = 0;
		showFrame = P.p.frameCount;
	}

}

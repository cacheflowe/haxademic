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
		super();
		buildGraphics( width, height );

		// load text file
		String lines[] = P.p.loadStrings(FileUtil.getHaxademicDataPath() + "text/gibson-text.txt");
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
		textFont = P.p.createFont(FileUtil.getFile("fonts/OhmegaSans-Regular.ttf"), _texture.height * 0.150f);
	}
	
	public void newLineMode() {
	}

	public void updateDraw() {
		// feedback alpha fade
		FeedbackRadialFilter.instance(P.p).setAmp(1f / 255f);
		FeedbackRadialFilter.instance(P.p).setSampleMult(0.93f);
		FeedbackRadialFilter.instance(P.p).setWaveAmp(0f);
		FeedbackRadialFilter.instance(P.p).setWaveFreq(0f);
		FeedbackRadialFilter.instance(P.p).setAlphaMult(0.94f);
		FeedbackRadialFilter.instance(P.p).applyTo(_texture);

		// draw text
		showFrameCount = 50;
		if(P.p.frameCount < showFrame + showFrameCount) {
			_texture.pushMatrix();
			_texture.pushStyle();
			_texture.fill(255);
			_texture.noStroke();
			_texture.textFont(textFont);
			_texture.textAlign(P.CENTER, P.CENTER);
			_texture.text(_words.get(_wordIndex), 0, 0, _texture.width, _texture.height);
			_texture.popStyle();
			_texture.popMatrix();
		}

	}
	
	public void updateTiming() {
		_wordIndex++;
		if( _wordIndex >= _words.size() ) _wordIndex = 0;
		showFrame = P.p.frameCount;
	}

}

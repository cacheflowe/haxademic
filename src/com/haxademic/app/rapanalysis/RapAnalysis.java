package com.haxademic.app.rapanalysis;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import processing.pdf.PGraphicsPDF;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.components.IMouseable;
import com.haxademic.core.components.TextButton;
import com.haxademic.core.draw.text.CustomFontText2D;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

@SuppressWarnings("serial")
public class RapAnalysis
extends PAppletHax{

	// capture --------------
	protected String _lines[];
	protected String _words[];
	protected String _result;
	protected CustomFontText2D _fontRenderer;
	protected int _fontSize = 100;
	protected int _wordIndex = 0;
	protected ArrayList<IMouseable> _mouseables;
	protected boolean _is212 = false;

	// render -----------------
	protected PGraphicsPDF _pdf;
	protected boolean _recordingPDF = true;
	protected String _lyricRenderString212 = "ameaaadslmedsaaaaaalaameaaaaadslaaameaabitaadslaameaaaaadslabitaaadsalmeaaaaaaaaaalaaaabitdsaaaalmeadsaaaaaaalaaaaaadslaaaabitaaaalaaameameaaadslaadsameaaadslaaaaaaalmeaacccdsdslmeaacccdsdslmeaacccdsdslmeaacccdsdslmeaacccdslmeaaaaaalaadsaldsaaaalaaalaaaaalmeaabitdslaaaaaalmeaaaaaalmeaaaalmeaaaalaaaalmeaaaaalmeaaadslaaaaaalaaaaaaaalaaaaaaaaaalaaadsaaadslaaaalaadsaaldsaadsalaabitaaaldsaaaaaaalaadsaalaaadsaldsdsadslaaaadslaaaaaaaaalaaaaalmeadsdslaaaaaalbitmeaaaaalmeaadslmeaaaalaalaaabitaalmeadsaccclmeadsaccclmeadsaccclmeadsacccldsdslmeaadsaaaaaaldsaaaaldsadsaaadsdslaaaalaaaaaaaaadsladsaaalaaaaaaadsalaaaaamedslamedslbitaaaaaaalaaamemelaaaaamedslamedslbitaaaaaaalaaamemelbitmeaaaaalaaaadslaaaaalaaaalaaaaalaabitaaaldsaaaaladsaaaaaalabitaaaaaaaaalaadsaadslaaaaalaaaalaaaaaalmedsaadsalaaaadslaaadslaaaalaaaaalaabitadslmeaaaaalameaaaaalaaaaaaaaaldsaalaaaaaalmeadsaccclaaaaamedslamedslbitaaaaaaalaaamemelaaaaamedslamedslbitaaaaaaalaaamemelaaamemelaaameme";
	protected String _lyricRenderString = "aaaaaaiilaaaaliaaaaaaalaaiaaaalaaaaablaaaalaaaliaaaaalaaaaaaalaaaaaaliaabiaaaiaaalaaaiaalaaaaaiaalaaaaasaaaaaaalaiaaaaaaaaaaaalaaaaaaaaaaaalaaaaaaaaalaaaaaaaaaaaalaaaaiaaaaalaaaiaaiaaiaaliaaaaaalaaaibaaaaaalaaaaiaaaalaaitaiaaaaaalaiaaaaiaaaaalaaaaaaaliiaaaaaaalaaaaaaaiilaaaaataiilaaaabaaaailaaaabaaaailaaaiaaaaailaaaiaaaaailaaaabaaaailaaaiaaaaailaiaaaliaaabaiaaalaaaaaaaalaaaaaiaaalaaaaaaiaaaaalaaaaaaaaaaliaaaabaiaaaaalaaaabiaaaaliaaaaaaalaaasaaaalaaaaaaaaaaalaaaaaaalaaaaaaaaaaliaaaaaiaaaaalaaaiaaaaaaliiaaaaailiaiaaaaaailaaaaaaaiilaaaaataiilaaaabaaaailaaaabaaaailaaaiaaaaailaaaiaaaaailaaaabaaaailaaaiaaaaailaaaaiiaalaaaaaaaaaalaaaaaaaaaalaaiabaaaaaalaaaaiiaalaaaaaaaaaalaaaaaaaaaalaaaabaaaaaalaaaaaaaiilaaaaataiilaaaabaaaailaaaabaaaailaaaiaaaaailaaaiaaaaailaaaabaaaailaaaiaaaaailaaaaaaaiilaaaaataiilaaaabaaaailaaaabaaaailaaaiaaaaailaaaiaaaaailaaaabaaaailaaaiaaaaai";
	protected int _gridW = 500;
	protected int _gridCols = 10;
	protected int _cellSize = Math.round( _gridW / _gridCols ); 
	protected boolean _wrapping = false; 

	// mode set
	protected boolean _isCapturingData = false;


	// general setup & context switching -------------------------------------------------------
	protected void overridePropsFile() {
		if( _isCapturingData == true ) {
			_appConfig.setProperty( "rendering", "false" );
		}
		_appConfig.setProperty( "fps", "100" );
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "1300" );
	}

	public void setup() {
		super.setup();

		if( _isCapturingData == true ) {
			setupCapture();
		} else {
			setupRender();
		}
	}

	public void drawApp() {
		if( _isCapturingData == true ) {
			stepThroughLyrics();
		} else {
			if( _is212 == true ) {
				drawLyricsString212();
			} else {
				try {
					drawLyricsStringDasMe();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// CAPTURE ------------------------------------------------------------------------------------
	public void setupCapture() {
		String file = ( _is212 == true ) ? "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/azalea-banks-212.txt"
				: "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/brooke-candy-das-me.txt";

		_lines = loadStrings(file);

		_fontRenderer = new CustomFontText2D( this, FileUtil.getHaxademicDataPath() + "fonts/GothamBold.ttf", _fontSize, color(255,255,255), CustomFontText2D.ALIGN_CENTER, width, _fontSize + 20 );
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_mouseables = new ArrayList<IMouseable>();
		if( _is212 == true ) {
			_mouseables.add( new TextButton( p, "bitch", "1", 40, 420, 200, 50 ) );
			_mouseables.add( new TextButton( p, "cunt", "2", 40, 520, 200, 50 ) );
			_mouseables.add( new TextButton( p, "I / Me / Mine", "1", 300, 420, 200, 50 ) );
			_mouseables.add( new TextButton( p, "double syllable", "2", 300, 520, 200, 50 ) );
			_mouseables.add( new TextButton( p, "any word", "1", 560, 420, 200, 50 ) );
			_mouseables.add( new TextButton( p, "line end", "2", 560, 520, 200, 50 ) );
		} else {
			_mouseables.add( new TextButton( p, "bitch", "1", 40, 420, 200, 50 ) );
			_mouseables.add( new TextButton( p, "bitches", "1", 40, 320, 200, 50 ) );
			_mouseables.add( new TextButton( p, "tit", "2", 40, 520, 200, 50 ) );
			_mouseables.add( new TextButton( p, "I / Me / Mine", "1", 300, 420, 200, 50 ) );
			_mouseables.add( new TextButton( p, "titties", "2", 300, 520, 200, 50 ) );
			_mouseables.add( new TextButton( p, "any word", "1", 560, 420, 200, 50 ) );
			_mouseables.add( new TextButton( p, "line end", "2", 560, 520, 200, 50 ) );
		}

		_result = "";

		analyzeLyrics();
	}

	public void analyzeLyrics() {
		int wordCount = 0;
		int lineCount = 0;
		String wordsPerLine[];

		// count lines and words
		for( int i=0; i < _lines.length; i++ ) {
			// count lines that contain characters
			if( _lines[i].length() >= 1 ) lineCount++;

			// count words per line
			wordsPerLine = _lines[i].split(" ");
			wordCount += wordsPerLine.length;	
		}

		// create word stream
		_words = new String[wordCount + _lines.length - 1];	// -1 for no newline on last line
		int wordPushInc = 0;
		for( int i=0; i < _lines.length; i++ ) {
			if( _lines[i].length() >= 1 ) {				
				wordsPerLine = _lines[i].split(" ");
				for( int j=0; j < wordsPerLine.length; j++ ) {
					_words[wordPushInc] = wordsPerLine[j];
					wordPushInc++;
				}
				// add a new line for every line except the last
				if( i < _lines.length - 1 ) {
					_words[wordPushInc] = "\\r";
					wordPushInc++;
				}
			}
		}

		P.println("wordCount = "+wordCount);
		P.println("lineCount = "+lineCount);
		P.println("words = "+(_words.length - _lines.length));
	}

	protected void stepThroughLyrics() {
		background(0);

		// draw buttons
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).update( p );
		}
		_mouseables.get(0).checkPress( p.mouseX, p.mouseY );

		// draw text to screen
		if( _wordIndex < _words.length ) {
			_fontRenderer.updateText( _words[ _wordIndex ] );
			p.image( _fontRenderer.getTextPImage(), 0, height/3f );	// height/2f - _fontSize/2f


			// test step through automatically
			//			_result += "a";
			//			P.println("_result = "+_result);
			//			if( _wordIndex < _words.length ) _wordIndex++;
		}

	}

	public void nextWord() {
		_wordIndex++;
	}

	public void mouseReleased() {
		if( _mouseables == null ) return;
		for( int i=0; i < _mouseables.size(); i++ ) {
			if( _mouseables.get(i).checkRelease( p.mouseX, p.mouseY ) ) {
				if( _is212 == true ) {	
					if( i == 0 ) {
						P.println("bitch!");
						_result += "bit";
					} else if( i == 1 ) {
						P.println("cunt!");
						_result += "ccc";
					} else if( i == 2 ) {
						P.println("I / Me / Mine");
						_result += "me";
					} else if( i == 3 ) {
						P.println("double syllable");
						_result += "ds";
					} else if( i == 4 ) {
						P.println("any word");
						_result += "a";
					} else if( i == 5 ) {
						P.println("new line");
						_result += "l";
					} 
				} else {
					if( i == 0 ) {
						P.println("bitch!");
						_result += "b";
					} else if( i == 1 ) {
						P.println("bitches!");
						_result += "s";
					} else if( i == 2 ) {
						P.println("tit!");
						_result += "t";
					} else if( i == 3 ) {
						P.println("I / Me / Mine");
						_result += "i";
					} else if( i == 4 ) {
						P.println("titties");
						_result += "b";
					} else if( i == 5 ) {
						P.println("any word");
						_result += "a";
					} else if( i == 6 ) {
						P.println("new line");
						_result += "l";
					} 
				}
				nextWord();
				P.println("_result = "+_result);
			}
		}
	}

	public void keyPressed() {
		if( _mouseables == null ) return;
		if( p.key == 'z' ) {
			if( _is212 == true ) {
				if( _result.lastIndexOf("bit") == _result.length() - 3 ) {
					P.println("removed bitch!");
					_result = _result.substring(0, _result.length() - 3);
					_wordIndex--;
				} else if( _result.lastIndexOf("ccc") == _result.length() - 3 ) {
					P.println("removed cunt!");
					_result = _result.substring(0, _result.length() - 3);
					_wordIndex--;
				} else if( _result.lastIndexOf("me") == _result.length() - 2 ) {
					P.println("removed I / Me / Mine!");
					_result = _result.substring(0, _result.length() - 2);
					_wordIndex--;
				} else if( _result.lastIndexOf("ds") == _result.length() - 2 ) {
					P.println("removed double syllable");
					_result = _result.substring(0, _result.length() - 2);
					_wordIndex--;
				} else if( _result.lastIndexOf("a") == _result.length() - 1 ) {
					P.println("removed any word");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("l") == _result.length() - 1 ) {
					P.println("removed new line");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				}
			} else {
				if( _result.lastIndexOf("b") == _result.length() - 1 ) {
					P.println("removed bitch!");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("s") == _result.length() - 1 ) {
					P.println("removed bitches!");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("i") == _result.length() - 1 ) {
					P.println("removed I / Me / Mine!");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("t") == _result.length() - 1 ) {
					P.println("removed tit");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("b") == _result.length() - 1 ) {
					P.println("removed titties");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("a") == _result.length() - 1 ) {
					P.println("removed any word");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				} else if( _result.lastIndexOf("l") == _result.length() - 1 ) {
					P.println("removed new line");
					_result = _result.substring(0, _result.length() - 1);
					_wordIndex--;
				}
			}
			P.println("_result = "+_result);
		}
	}

	public void mousePressed() {
		if( _mouseables == null ) return;
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkPress( p.mouseX, p.mouseY );
		}
	}

	public void mouseMoved() {
		if( _mouseables == null ) return;
		for( int i=0; i < _mouseables.size(); i++ ) {
			_mouseables.get(i).checkOver( p.mouseX, p.mouseY );
		}
	}

	// RENDER ------------------------------------------------------------------------------------

	protected void setupRender() {
	}

	protected void drawLyricsString212() {

		if( _recordingPDF == true ) {
			if( _gridCols < 31 && _wrapping == true ) 
				p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "kelly-monico-212-"+_gridCols+"col.pdf" );	// -"+ SystemUtil.getTimestamp(p) +"
			if( p.frameCount == 1 && _wrapping == false )
				p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "kelly-monico-212-lines.pdf" );
		}


		background(255);

		int drawX = 0;
		int drawY = 0;

		p.noStroke();
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		for( int i = 0; i < _lyricRenderString212.length(); i++ ) {
			char character = _lyricRenderString212.charAt(i);
			if( character == 'a' ) {
				p.fill(0);
				p.triangle(drawX, drawY + _cellSize, drawX + _cellSize, drawY + _cellSize, drawX + _cellSize, drawY);
			} else if( character == 'm' ) {
				p.fill(165,159,161);
				p.triangle(drawX, drawY + _cellSize, drawX + _cellSize, drawY + _cellSize, drawX + _cellSize, drawY);
			} else if( character == 'e' ) {
				p.fill(165,159,161);
				p.triangle(drawX, drawY, drawX, drawY + _cellSize, drawX + _cellSize, drawY + _cellSize);
			} else if( character == 'd' ) {
				p.fill(0);
				p.triangle(drawX, drawY + _cellSize, drawX + _cellSize, drawY + _cellSize, drawX + _cellSize, drawY);
			} else if( character == 's' ) {
				p.fill(0);
				p.triangle(drawX, drawY, drawX + _cellSize, drawY, drawX, drawY + _cellSize);
			} else if( character == 'b' ) {
				p.fill(122,205,209);
				p.triangle(drawX, drawY, drawX + _cellSize, drawY, drawX, drawY + _cellSize);
			} else if( character == 'i' ) {
				p.fill(122,205,209);
				p.rect(drawX, drawY, _cellSize, _cellSize);
			} else if( character == 't' ) {
				p.fill(122,205,209);
				p.triangle(drawX, drawY, drawX + _cellSize, drawY, drawX + _cellSize, drawY + _cellSize);
			} else if( character == 'c' ) {
				p.fill(160,139,53);
				p.triangle(drawX, drawY, drawX + _cellSize, drawY, drawX + _cellSize, drawY + _cellSize);
			} else if( character == 'l' ) {
				p.fill(240,228,196);
				p.rect(drawX, drawY, _cellSize, _cellSize);
				if( _wrapping == false ) {
					drawX = -_cellSize;
					drawY += _cellSize;
				}
			}  


			drawX += _cellSize;

			if( _wrapping == true ) {
				if( drawX >= _cellSize * _gridCols ) {
					drawX = 0;
					drawY += _cellSize;
				}
			}
		}



		// save pdf
		if( _recordingPDF == true ) {
			if( _gridCols < 31 || _wrapping == false ) {
				p.endRecord();
				_gridCols++;
			}
		}

	}

	public void drawLyricsStringDasMe() throws DocumentException, MalformedURLException, IOException {
		background(0);


		if( _recordingPDF == true && p.frameCount == 1 ) {
			//			if( _wrapping == true ) 
			//				p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "kelly-monico-das-me-"+_gridCols+"col.pdf" );	// -"+ SystemUtil.getTimestamp(p) +"
			//			if( _wrapping == false )
			p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "kelly-monico-das-me-lines.pdf" );



			Rectangle pageRect = new Rectangle (16000, 16000);
			pageRect.setBackgroundColor(new Color(0xff, 0xff, 0xff));
			Document document = new Document(pageRect);
			PdfWriter.getInstance(document,new FileOutputStream(FileUtil.getHaxademicOutputPath() + "kelly-monico-das-me-lines.pdf"));
			document.open();
			
//			protected PImage _bitch = loadImage( "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/bitch.png" );
//			protected PImage _bitches = loadImage( "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/bitches.png" );
//			protected PImage _me = loadImage( "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/me.png" );
//			protected PImage _tit = loadImage( "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/tit.png" );
//			protected PImage _titties = loadImage( "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/titties.png" );
//			protected PImage _word = loadImage( "/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/word.png" );

			int ppi = 180;
//			_bitches.scalePercent(100*72/ppi);

			Image _bitch = Image.getInstance("/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/bitch.png");
			_bitch.scalePercent(50);

			Image _bitches = Image.getInstance("/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/bitches.png" );
			_bitches.scalePercent(50);
			
			Image _me = Image.getInstance("/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/me.png" );
			_me.scalePercent(50);
			
			Image _tit = Image.getInstance("/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/tit.png" );
			_tit.scalePercent(50);
			
			Image _titties = Image.getInstance("/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/titties.png" );
			_titties.scalePercent(50);
			
			Image _word = Image.getInstance("/Users/cacheflowe/Documents/workspace/haxademic/assets/projects/bitches-and-hoes/dasme/word.png" );
			_word.scalePercent(50);
			



			int drawX = 0;
			int drawY = 0;


			for( int i = 0; i < _lyricRenderString.length(); i++ ) {
				char character = _lyricRenderString.charAt(i);
				if( character == 'a' ) {
					_word.setAbsolutePosition(drawX, drawY - _word.getScaledHeight() * 0.5f);
					document.add(_word);
					drawX += _word.getScaledWidth();
				} else if( character == 'b' ) {
					_bitch.setAbsolutePosition(drawX, drawY - _bitch.getScaledHeight() * 0.5f);
					document.add(_bitch);
					drawX += _bitch.getScaledWidth();
				} else if( character == 's' ) {
					_bitches.setAbsolutePosition(drawX, drawY - _bitches.getScaledHeight() * 0.5f);
					document.add(_bitches);
					drawX += _bitches.getScaledWidth();
				} else if( character == 't' ) {
					_tit.setAbsolutePosition(drawX, drawY - _tit.getScaledHeight() * 0.5f);
					document.add(_tit);
					drawX += _tit.getScaledWidth();
				} else if( character == 'i' ) {
					_me.setAbsolutePosition(drawX, drawY - _me.getScaledHeight() * 0.5f);
					document.add(_me);
					drawX += _me.getScaledWidth();
				} else if( character == 'b' ) {
					_titties.setAbsolutePosition(drawX, drawY - _titties.getScaledHeight() * 0.5f);
					document.add(_titties);
					drawX += _titties.getScaledWidth();
				} 


				if( character == 'l' ) {
					if( _wrapping == false ) {
						drawX = 0;
						drawY += 175;
					}
				}


				if( _wrapping == true ) {
					if( drawX >= _cellSize * _gridCols ) {
						drawX = 0;
						drawY += _cellSize;
					}
				}
			}

			document.close();

		} else
			p.exit();
	}


}

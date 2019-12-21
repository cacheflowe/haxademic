package com.haxademic.demo.speech;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.EasingColor;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class Demo_SpeechRecognitionColors
extends PAppletHax
implements ISpeechRecognizer {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FrameWithBorderLayout frame;
	protected String curColor;
	protected EasingColor color = new EasingColor(0xff0000);
	protected static HashMap<String, String> colors;
	static
    {
		colors = new HashMap<String, String>();
		colors.put("blue", "#0000ff");
		colors.put("red", "#ff0000");
		colors.put("yellow", "#ffff00");
		colors.put("banana", "#ffff00");
		colors.put("orange", "#ffa500");
		colors.put("green", "#00ff00");
		colors.put("purple", "#800080");
		colors.put("white", "#ffffff");
		colors.put("black", "#000000");
		colors.put("pink", "#ffc0cb");
		colors.put("brown", "#8b4513");
		colors.put("coral", "#F08080");
		colors.put("salmon", "#FA8072");
		colors.put("crimson", "#DC143C");
		colors.put("brick", "#B22222");
		colors.put("pink", "#FFC0CB");
		colors.put("tomato", "#FF6347");
		colors.put("orange", "#FFA500");
		colors.put("gold", "#FFD700");
		colors.put("yellow", "#FFFF00");
		colors.put("lemon", "#FFFACD");
		colors.put("papaya", "#FFEFD5");
		colors.put("moccasin", "#FFE4B5");
		colors.put("peach", "#FFDAB9");
		colors.put("khaki", "#F0E68C");
		colors.put("lavender", "#E6E6FA");
		colors.put("thistle", "#D8BFD8");
		colors.put("plum", "#DDA0DD");
		colors.put("violet", "#EE82EE");
		colors.put("orchid", "#DA70D6");
		colors.put("fuchsia", "#FF00FF");
		colors.put("magenta", "#FF00FF");
		colors.put("purple", "#800080");
		colors.put("indigo", "#4B0082");
		colors.put("chartreuse", "#7FFF00");
		colors.put("grass", "#7CFC00");
		colors.put("lime", "#00FF00");
		colors.put("seagreen", "#2E8B57");
		colors.put("forest", "#228B22");
		colors.put("green", "#008000");
		colors.put("olive", "#808000");
		colors.put("teal", "#008080");
		colors.put("aqua", "#00FFFF");
		colors.put("cyan", "#00FFFF");
		colors.put("aquamarine", "#7FFFD4");
		colors.put("turquoise", "#40E0D0");
		colors.put("skyblue", "#87CEEB");
		colors.put("royalblue", "#4169E1");
		colors.put("blue", "#0000FF");
		colors.put("navy", "#000080");
		colors.put("midnight", "#191970");
		colors.put("cornsilk", "#FFF8DC");
		colors.put("bisque", "#FFE4C4");
		colors.put("wheat", "#F5DEB3");
		colors.put("burlyWood", "#DEB887");
		colors.put("tan", "#D2B48C");
		colors.put("sand", "#F4A460");
		colors.put("peru", "#CD853F");
		colors.put("chocolate", "#D2691E");
		colors.put("sienna", "#A0522D");
		colors.put("brown", "#A52A2A");
		colors.put("maroon", "#800000");
		colors.put("white", "#FFFFFF");
		colors.put("snow", "#FFFAFA");
		colors.put("honeydew", "#F0FFF0");
		colors.put("azure", "#F0FFFF");
		colors.put("seashell", "#FFF5EE");
		colors.put("beige", "#F5F5DC");
		colors.put("ivory", "#FFFFF0");
		colors.put("linen", "#FAF0E6");
		colors.put("gainsboro", "#DCDCDC");
		colors.put("silver", "#C0C0C0");
		colors.put("darkgray", "#A9A9A9");
		colors.put("gray", "#808080");
		colors.put("slate", "#708090");
		colors.put("black", "#000000");
    }
	
	public void firstFrame() {
		Demo_SpeechRecognitionColors thiss = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 frame = new FrameWithBorderLayout();
                 frame.initWithDelegate(thiss);
                 frame.setVisible(false);
            }
      });
	}
	
	public void drawApp() {
		color.update();
		p.background(color.colorInt());
		if(curColor != null) {
			p.fill(255);
			p.textSize(100);
			p.textAlign(P.CENTER, P.CENTER);
			p.text(curColor, 0, 0, p.width, p.height);
		}
	}
	
	public void recognizedWord(String word) {
		P.out("WORD:", word);
		curColor = word;
		if(colors.containsKey(word)) {
			color.setTargetHex(colors.get(word));
		}
	}

	////////////////////////////
	// SECONDARY APP
	////////////////////////////

	@SuppressWarnings("serial")
	public class FrameWithBorderLayout 
	extends JFrame {
		
		protected LiveSpeechRecognizer recognizer; 
		protected ISpeechRecognizer delegate;

	    public FrameWithBorderLayout() {
			super("Speech frame");  					// Call super class constructor with a title
			setDefaultCloseOperation(EXIT_ON_CLOSE);  	// Set when the close button is clicked, the application exits
	    }
	    
	    public void initWithDelegate(ISpeechRecognizer delegate) {
	        this.delegate = delegate;
	        initSphinx4();
	    }
	    
		protected void initSphinx4() {

			try {
				// build configuration
				Configuration configuration = new Configuration();
				configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
				configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
				configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

				// only look for certain words
				// from: https://stackoverflow.com/a/41426932
				configuration.setGrammarPath("file:data/text/sphinx4");
				configuration.setGrammarName("colors");
				configuration.setUseGrammar(true);

				// init microphone
				recognizer = new LiveSpeechRecognizer(configuration);
				recognizer.startRecognition(true);
				startRecognition();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		protected void startRecognition() {
			SpeechResult result = recognizer.getResult();
			if(delegate != null) delegate.recognizedWord("green");

			while ((result = recognizer.getResult()) != null) {
				List<WordResult> words = result.getWords();
				for (Iterator<WordResult> iterator = words.iterator(); iterator.hasNext();) {
					WordResult wordResult = (WordResult) iterator.next();
					float wordScore = (float) wordResult.getScore();
					//					float wordScore = (float) LogMath.getLogMath().log(wordScore);
					if(P.abs(wordScore) > 5000000) {
						P.out("[GOOD] (" + wordScore + ")", wordResult.getWord().toString());
						if(delegate != null) delegate.recognizedWord(wordResult.getWord().toString());
					} else {
						P.out("[BAD] (" + wordScore + ")", wordResult.getWord().toString());
					}
				}

				// print possible results if not using grammar
				Collection<String> resultText = result.getNbest(10);
				for (@SuppressWarnings("rawtypes")
				Iterator iterator = resultText.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					P.out("result: ", string);
				}

				// System.out.format("Hypothesis: %s\n", result.getHypothesis());
			}
		}

	}

}


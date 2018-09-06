package com.haxademic.sketch.test;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class Sphinx4Test
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LiveSpeechRecognizer recognizer; 
	protected boolean ready = false;

	protected void overridePropsFile() {
	}

	public void setupFirstFrame() {
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
//		    configuration.setUseGrammar(true);

			// init microphone
			recognizer = new LiveSpeechRecognizer(configuration);
			new Thread(new Runnable() { public void run() {
				recognizer.startRecognition(true);
				ready = true;
			}}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	public void drawApp() {
		p.background(0);
		p.fill(255);
		if(recognizer != null && ready) {
			p.text("working", 100, 100);
//			new Thread(new Runnable() { public void run() {
			SpeechResult result = recognizer.getResult();

			while ((result = recognizer.getResult()) != null) {
				List<WordResult> words = result.getWords();
				for (Iterator<WordResult> iterator = words.iterator(); iterator.hasNext();) {
					WordResult wordResult = (WordResult) iterator.next();
					float wordScore = (float) wordResult.getScore();
//					float wordScore = (float) LogMath.getLogMath().log(wordScore);
					if(P.abs(wordScore) > 5000000) {
						P.out("[GOOD] (" + wordScore + ")", wordResult.getWord().toString());
					} else {
						P.out("[BAD] (" + wordScore + ")", wordResult.getWord().toString());
					}
				}
				
				// print possible results
                Collection<String> resultText = result.getNbest(10);
				for (Iterator iterator = resultText.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					P.out("result: ", string);
				}
                
				System.out.format("Hypothesis: %s\n", result.getHypothesis());
			}
//			}}).start();
		} else {			
			p.text("NOT working", 100, 100);
		}
	}
	
}


package com.haxademic.demo.speech;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class Demo_SpeechRecognitionCopypasta
extends PAppletHax
implements ISpeechRecognizer {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected FrameWithBorderLayout frame;
	protected String voiceCommand;
	
	protected Robot robot;
	protected ArrayList<KeySequence> keyQueue = new ArrayList<KeySequence>();

	public void setupFirstFrame() {
		Demo_SpeechRecognitionCopypasta thiss = this;
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame = new FrameWithBorderLayout();
				frame.initWithDelegate(thiss);
				frame.setVisible(false);
			}
		});
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void drawApp() {
		p.background(0);
		if(voiceCommand != null) {
			p.fill(255);
			p.textSize(100);
			p.textAlign(P.CENTER, P.CENTER);
			p.text(voiceCommand, 0, 0, p.width, p.height);
		}
		
		// process key queue
		if(keyQueue.size() > 0) {
			KeySequence keySeq = keyQueue.remove(0);
			if(keySeq.pressed) {
				robot.keyPress(keySeq.key);
				P.out("press", keySeq.key);
			} else {
				robot.keyRelease(keySeq.key);
				P.out("release", keySeq.key);
			}
		}
	}

	public void recognizedWord(String word) {
		P.out("WORD:", word);
		voiceCommand = word;

		int control = (P.platform == P.MACOSX) ? 157 : KeyEvent.VK_CONTROL;
		
		if(word.equals("copy")) {
			keyQueue.add(new KeySequence(true, control));
			keyQueue.add(new KeySequence(true, KeyEvent.VK_C));
			keyQueue.add(new KeySequence(false, KeyEvent.VK_C));
			keyQueue.add(new KeySequence(false, control));
		} else if(word.equals("paste")) {
			keyQueue.add(new KeySequence(true, control));
			keyQueue.add(new KeySequence(true, KeyEvent.VK_V));
			keyQueue.add(new KeySequence(false, KeyEvent.VK_V));
			keyQueue.add(new KeySequence(false, control));
		} else if(word.equals("undo")) {
			keyQueue.add(new KeySequence(true, control));
			keyQueue.add(new KeySequence(true, KeyEvent.VK_Z));
			keyQueue.add(new KeySequence(false, KeyEvent.VK_Z));
			keyQueue.add(new KeySequence(false, control));
		}
	}

	////////////////////////////
	// KEY COMMAND HELPER
	////////////////////////////
	
	public class KeySequence {
		
		public boolean pressed;
		public int key;
		
		public KeySequence(boolean pressed, int key) {
			this.pressed = pressed;
			this.key = key;
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
				configuration.setGrammarName("copypasta");
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
			if(delegate != null) delegate.recognizedWord("start");

			while ((result = recognizer.getResult()) != null) {
				List<WordResult> words = result.getWords();
				for (Iterator<WordResult> iterator = words.iterator(); iterator.hasNext();) {
					WordResult wordResult = (WordResult) iterator.next();
					float wordScore = (float) wordResult.getScore();
					if(P.abs(wordScore) > 3000000) {
						P.out("[GOOD] (" + wordScore + ")", wordResult.getWord().toString());
						if(delegate != null) delegate.recognizedWord(wordResult.getWord().toString());
					} else {
						P.out("[BAD] (" + wordScore + ")", wordResult.getWord().toString());
					}
				}
			}
		}

	}

}


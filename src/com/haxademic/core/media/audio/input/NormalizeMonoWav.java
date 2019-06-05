package com.haxademic.core.media.audio.input;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.sampled.TConversionTool;

import com.haxademic.core.app.P;

public class NormalizeMonoWav {

	// Normalization code from: https://github.com/gatech-csl/jes
	// And: http://coding-guru.com/normalizing-sounds/
	
	public NormalizeMonoWav() {}

	public static void normalize(String fileName, boolean overwrite) {
		normalize(fileName, overwrite, 1f);
	}
	
	public static void normalize(String fileName, boolean overwrite, float maxVolume) {
		// create a new instance because of internal classes' enclosure requirements
		new Thread(new Runnable() { public void run() {
			// thread it
			int startTime = P.p.millis();
			(new NormalizeMonoWav()).normalizeMonoWav(fileName, overwrite, maxVolume);
			P.println("Normalized in: " + (P.p.millis() - startTime) + "ms");
		}}).start();	
	}
	
	public void normalizeMonoWav(String fileName, boolean overwrite, float maxVolume) {
		SimpleSound sound = new SimpleSound(fileName);

		int largest = 0;
		SoundSample[] sampleArray = new SoundSample[sound.getNumSamples()];
		for (int i = 0; i < sampleArray.length; i++) {
			sampleArray[i] = sound.getSample(i);
		}
		SoundSample sample = null;
		int value = 0;

		// loop comparing the absolute value of the current value
		// to the current largest
		for (int i = 0; i < sampleArray.length; i++) { 
			sample = sampleArray[i]; 
			value = Math.abs(sample.getValue()); 
			if (value > largest)
				largest = value;
		}

		// now calculate the multiplier to multiply by
		double multiplier = 32767.0 / largest;
		multiplier *= (double) maxVolume;

		/*
		 * loop through all the samples and multiply by the multiplier
		 */
		for (int i = 0; i < sampleArray.length; i++) {
			sample = sampleArray[i];
			sample.setValue((int) (sample.getValue() * multiplier));
		}

		if(overwrite) {
			sound.writeToFile(sound.fileName);
		} else {
			sound.writeToFile(sound.fileName + ".norm.wav");
		}

	}



	/**
	 * The <code>SimpleSound</code> class is an implementation of the
	 * Java Sound API specifically designed for use with students.
	 * <br>
	 * http://java.sun.com/products/java-media/sound/index.html
	 * <p>
	 * This class allows for easy playback, and manipulation of AU,
	 * AIFF, and WAV files.
	 * <p>
	 * Code & ideas for this class related to playing and
	 * viewing the sound were borrowed from the Java Sound Demo:
	 * http://java.sun.com/products/java-media/sound/
	 * samples/JavaSoundDemo/
	 * <br>
	 * Also, some code borrowed from Tritonus as noted.
	 * <br>
	 * Copyright Georgia Institute of Technology 2004
	 * @author Ellie Harmon, ellie@cc.gatech.edu
	 * @author Barbara Ericson ericson@mindspring.com
	 *
	 * Changes merged by Buck Scharfnorth 22 May 2008
	 * default numberOfChannels left to 1
	 * stopPlaying() method added
	 *
	 * _SoundIndexOffset added (29 Oct 2008) -Buck
	 */

	public class SimpleSound {

		///////////////////////////// fields ////////////////////////

		/**
		 * Constant for max negative value
		 */
		public static final int MAX_NEG = -32768;

		/**
		 * Constant for max positive value
		 */
		public static final int MAX_POS = 32767;

		/**
		 * Constant for the default sampling rate
		 */
		public static final int SAMPLE_RATE = 22050;

		/**
		 * Constant for the index base (0 or 1)
		 */
		public static final int _SoundIndexOffset = 0;

		/**
		 * Constant for the default sample size in bits
		 * it is usual to have either 8 or 16
		 */
		private static final int NUM_BITS_PER_SAMPLE = 16;

		/**
		 * Flag to tell if in debug mode or not
		 */
		private static final boolean DEBUG = false;

		/**
		 * An array of bytes representing the sound.
		 */
		private byte[] buffer;

		/**
		 * Contains information about this sound such as its length,
		 * format, and type.
		 * @see AudioFormat
		 */
		private AudioFileFormat audioFileFormat = null;

		/**
		 * The name of the file from which this sound was created.  Gets
		 * updated every time load from file is called.
		 * @see #loadFromFile
		 */
		private String fileName = null;

		////////////////////////// constructors /////////////////////

		/**
		 * Constructs a <code>SimpleSound</code> of 3 seconds long.
		 */
		public SimpleSound() {
			this(SAMPLE_RATE * 3);
		}

		/**
		 * Constructs a <code>SimpleSound</code> of the specified length.
		 * This sound will simply consist of an empty byte array, and an
		 * <code>AudioFileFormat</code> with the following values:
		 * <ul>
		 * <li><code>AudioFileFormat.Type.WAVE</code>
		 * <li>22.05K sampling rate
		 * <li>16 bit sample
		 * <li>1 channel
		 * <li>signed PCM encoding
		 * <li>small-endian byte order
		 * </ul>
		 * Note that no new sound file is created, we only represent the
		 * sound with a buffer and the AudioFileFormat.  If a file is
		 * desired, then the method <code>writeToFile(String filename)
		 * </code> must be called on this newly created sound.
		 *
		 * @param numFrames the number of samples in the sound
		 * @see SimpleSound#write(String filename)
		 */
		public SimpleSound(int numFrames) {
			int numChannels = 1;    // the number of channels in the sound
			int bytesPerSample = NUM_BITS_PER_SAMPLE / 8;

			/*
	         Make a new sound with the default sampling rate, 16 bits,
	         1 channel(==1 sample/frame), signed, smallEndian
			 */
			AudioFormat audioFormat =
					new AudioFormat(SAMPLE_RATE, NUM_BITS_PER_SAMPLE,
							numChannels, true, false);

			/*
			 * The length in bytes is the number of channels
			 * times the number of frames and times the number of bytes per
			 * sample (2 bytes per sample)
			 */
			int lengthInFrames = numChannels * numFrames;
			int lengthInBytes = lengthInFrames * bytesPerSample;

			/*
	         Make a new WAV file format, with the AudioFormat described above
			 */
			audioFileFormat =
					new AudioFileFormat(AudioFileFormat.Type.WAVE,
							audioFormat, lengthInFrames);

			// create the buffer
			buffer = new byte[lengthInBytes];

		}

		/**
		 * Constructs a <code>SimpleSound</code> of the specified length.
		 * This sound will simply consist of an empty byte array, and an
		 * <code>AudioFileFormat</code> with the following values:
		 * <ul>
		 * <li><code>AudioFileFormat.Type.WAVE</code>
		 * <li>22.05K sampling rate
		 * <li>16 bit sample
		 * <li>1 channel
		 * <li>signed PCM encoding
		 * <li>small-endian byte order
		 * </ul>
		 * Note that no new sound file is created, we only represent the
		 * sound with a buffer and the AudioFileFormat.  If a file is
		 * desired, then the method <code>writeToFile(String filename)
		 * </code> must be called on this newly created sound.
		 *
		 * @param numFrames the number of samples in the sound
		 * @see SimpleSound#write(String filename)
		 */
		public SimpleSound(int numFrames, int sampleRate) {
			int numChannels = 1;    // the number of channels in the sound
			int bytesPerSample = NUM_BITS_PER_SAMPLE / 8;

			/*
	         Make a new sound with the default sampling rate, 16 bits,
	         1 channel(==1 sample/frame), signed, smallEndian
			 */
			AudioFormat audioFormat =
					new AudioFormat(sampleRate, NUM_BITS_PER_SAMPLE,
							numChannels, true, false);

			/*
			 * The length in bytes is the number of channels
			 * times the number of frames and times the number of bytes per
			 * sample (2 bytes per sample)
			 */
			int lengthInFrames = numChannels * numFrames;
			int lengthInBytes = lengthInFrames * bytesPerSample;

			/*
	         Make a new WAV file format, with the AudioFormat described above
			 */
			audioFileFormat =
					new AudioFileFormat(AudioFileFormat.Type.WAVE,
							audioFormat, lengthInFrames);

			// create the buffer
			buffer = new byte[lengthInBytes];

		}

		/**
		 * Constructs a simple sound with the given sample size in bits and
		 * type of endian (big or little)
		 */
		public SimpleSound(int sampleSizeInBits, boolean isBigEndian) {
			// calculate the number of bytes in the sample
			int numBytesInSample = sampleSizeInBits / 8;
			int numberOfChannels = 1;
			boolean signedFlag = true;

			// create the audio format
			AudioFormat audioFormat =
					new AudioFormat(SAMPLE_RATE, sampleSizeInBits,
							numberOfChannels,
							signedFlag, isBigEndian);

			// compute the length of the byte array
			int lengthInBytes =
					SAMPLE_RATE * numberOfChannels * 5 * numBytesInSample;

			// create the audio file format
			audioFileFormat =
					new AudioFileFormat(AudioFileFormat.Type.WAVE,
							audioFormat,
							lengthInBytes /
							(numBytesInSample * numberOfChannels));

			// create the buffer
			buffer = new byte[lengthInBytes];
		}

		/**
		 * Constructs a new SimpleSound from the given file.
		 * @param fileName The File from which to create this sound.
		 * @see SimpleSound#loadFromFile(String filename)
		 */
		public SimpleSound(String fileName) {
			try {
				// load the sound from the file
				loadFromFile(fileName);
			} catch (Exception ex) {
				try {
					printError("Exception during load of file " + fileName + "\n Please ensure this file exists and uses a filetype supported by JES (wav, aiff, au).");
				} catch (SoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * Constructor that creates a new SimpleSound by copying a passed SimpleSound
		 * @param sound the sound to copy
		 */
		public SimpleSound(SimpleSound sound) {
			this.audioFileFormat = sound.audioFileFormat;
			this.fileName = sound.fileName;

			// copy the samples
			if (sound.buffer != null) {
				this.buffer = new byte[sound.buffer.length];
				for (int i = 0; i < sound.buffer.length; i++) {
					this.buffer[i] = sound.buffer[i];
				}
			}
		}

		///////////////////////// accessors ///////////////////////////

		/**
		 * Method that returns the byte array representation of this simple sound.
		 * @return the sound represented as a byte array
		 */
		public byte[] getBuffer() {
			return buffer;
		}

		/**
		 * Method that returns the AudioFileFormat describing this simple sound.
		 * @return the AudioFileFormat describing this sound
		 * @see AudioFileFormat
		 */
		public AudioFileFormat getAudioFileFormat() {
			return audioFileFormat;
		}

		/**
		 * Method to get the sampling rate of this sound
		 * @return the sampling rate in number of samples per second
		 */
		public double getSamplingRate() {
			return audioFileFormat.getFormat().getSampleRate();
		}

		/**
		 * Method that returns the SoundExplorer
		 * @return the sound explorer
		 */
		//	    public SoundExplorer getSoundExplorer() {
		//	        return soundExplorer;
		//	    }

		/**
		 * Method to return the byte array
		 * @return an array of bytes which represents the simple sound
		 * @see SimpleSound#getBuffer
		 */
		public byte[] asArray() {
			return getBuffer();
		}

		/**
		 * Method that returns the vector of playback threads currently
		 * active on this sound.
		 * @return the vector of playback threads for this simple sound
		 */
		//	    public Vector getPlaybacks() {
		//	        return playbacks;
		//	    }

		/**
		 * Method that returns the name of the file this sound came from.
		 * If this sound did not originate with a file, this value will
		 * be null.
		 * @return the file name associated with this sound or null
		 * @see #loadFromFile(String fileName)
		 */
		public String getFileName() {
			return fileName;
		}

		////////////////////////////// modifiers ////////////////////////

		/**
		 * Changes the byte array that represents this sound.
		 * @param newBuffer a byte array representation of the new sound we
		 * want this to represent.
		 */
		public void setBuffer(byte[] newBuffer) {
			buffer = newBuffer;
		}

		/**
		 * Changes the byte array that represents this sound.
		 * @param newBuffer an integer with the number of bytes in the buffer
		 */
		public void setBuffer(int newBuffer) {
			buffer = new byte[newBuffer];
		}

		/**
		 * Changes the AudioFileFormat of this sound.
		 * @param newAudioFileFormat the new audioFileFormat that describes
		 * this sound.
		 * @see AudioFileFormat
		 */
		public void setAudioFileFormat(AudioFileFormat newAudioFileFormat) {
			audioFileFormat = newAudioFileFormat;
		}

		/**
		 * Changes the explorer of this object.
		 * @param soundExplorer the new SoundExplorer to use
		 * @see SoundExplorer
		 */
		//	    public void setSoundExplorer(SoundExplorer soundExplorer) {
		//	        this.soundExplorer = soundExplorer;
		//	    }



		///////////////////////// methods /////////////////////////////

		/**
		 * Creates an <code>AudioInputStream</code> for this sound from the
		 * <code>buffer</code> and the <code>audioFileFormat</code>.
		 * @return an AudioInputStream representing this sound.
		 * @see AudioInputStream
		 */
		public AudioInputStream makeAIS() {
			AudioFileFormat.Type fileType = audioFileFormat.getType();
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			int frameSize = audioFileFormat.getFormat().getFrameSize();

			AudioInputStream audioInputStream =
					new AudioInputStream(bais, audioFileFormat.getFormat(),
							buffer.length / frameSize);
			return audioInputStream;
		}

		/**
		 * Invokes <code>printError(message, null)</code>
		 *
		 * @see SimpleSound#printError(String message, Exception e)
		 * @throws SoundException Will throw under every circumstance.
		 *                            This way we can catch the exception
		 *                            in JES.
		 */
		public void printError(String message) throws SoundException {
			printError(message, null);
		}

		/**
		 * Prints the given String to the "standard" error output stream, then
		 * prints a stack trace on the exception, and then exits the program.  If
		 * the String is null, then nothing happens, the method just returns.  If
		 * the Exception is null, then it prints the String and then exits the
		 * program.
		 *
		 * @param message A description of the error
		 * @param e The exception, if any, that was caught regarding the error
		 * @throws SoundException Will throw under every circumstance.
		 *                            This way we can catch the exception
		 *                            in JES.
		 */
		public void printError(String message, Exception e) throws SoundException {
			if (message != null) {
				//SimpleOutput.showError(message);
				System.err.println(message);
				if (e != null) {
					e.printStackTrace();
				}
				//so we can catch the error
				throw(new SoundException(message));
			}
		}

		/**
		 * Method to check if a sound is stereo (2 channels) or not
		 * @return true if in stereo else false
		 */
		public boolean isStereo() {
			if (audioFileFormat.getFormat().getChannels() == 1) {
				return false;
			} else {
				return true;
			}
		}

		////////////////////////// File I/O ///////////////////////////////////

		/**
		 * Method to write this sound to a file
		 * @param fileName the name of the file to write to
		 */
		public void write(String fileName) {
			writeToFile(fileName);
		}

		/**
		 * Creates an audioInputStream from this sound, and then writes
		 * this stream out to the file with the specified name.  If no
		 * file exists, one is created.  If a file already exists, then it
		 * is overwritten.  This does not check the extension of the
		 * fileName passed in to make sure it agrees with the
		 * <code>AudioFileFormat.Type</code> of this sound.
		 *
		 * @param outFileName The name of the file to write this sound to
		 * @throws SoundException if any error is encountered while
		 * writing to the file.
		 */
		public void writeToFile(String outFileName) {

			/*
	         get an audioInputStream that represents this sound.
	         then, we will write from the stream to the file
			 */
			AudioInputStream audioInputStream = makeAIS();
			AudioFileFormat.Type type = audioFileFormat.getType();

			try {
				audioInputStream.reset();
			}//try reset audioInputStream
			catch (Exception e) {
				try {
					printError("Unable to reset the Audio stream.  Please " +
							"try again.", e);
				} catch (SoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}//catch


			//get the file to write to
			File file = new File(outFileName);
			if (!file.exists()) {
				//if the file doesn't exist, make one
				try {
					file.createNewFile();
				}//try
				catch (IOException e) {
					try {
						printError("That file does not already exist, and" +
								"there were problems creating a new file" +
								"of that name.  Are you sure the path" +
								"to: " + outFileName + "exists?", e);
					} catch (SoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}//catch
			}//if


			//write to the file
			try {
				if (AudioSystem.write(audioInputStream, type, file) == -1) {
					printError("Problems writing to file.  Please " +
							"try again.");
				}

				// if the write was successful then set the file name to the
				// new name
				else {
					this.fileName = outFileName;
				}
			}//try
			catch (FileNotFoundException e) {
				try {
					printError("The file you specified did not already exist " +
							"so we tried to create a new one, but were unable" +
							"to do so.  Please try again.  If problems " +
							"persit see your TA.", e);
				} catch (SoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (Exception e) {
				try {
					printError("Problems writing to file: " + outFileName, e);
				} catch (SoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}//catch


			//close the input stream, we're done writing
			try {
				audioInputStream.close();
			}//try
			catch (Exception e) {
				try {
					printError("Unable to close the Audio stream.");
				} catch (SoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}//catch

		}//writeToFile(String outFileName)


		/**
		 * Resets the fields of this sound so that it now represents the
		 * sound in the specified file.  If successful, the fileName
		 * ariable is updated such that it is equivalent to
		 * <code>inFileName</code>.
		 *
		 * @param inFileName the path and filename of the sound we want to
		 *                   represent.
		 * @throws SoundException if any problem is encountered while
		 *                            reading in from the file.
		 */
		public void loadFromFile(String inFileName) throws SoundException {

			// try to prevent a null pointer exception
			if (inFileName == null) {
				printError("You must pass in a valid file name.  Please try" +
						"again.");
			}

			/* get the File object representing the file named inFileName
			 * and make sure it exists */
			File file = new File(inFileName);
			if (!file.exists()) {
				printError("The file: " + inFileName + " doesn't exist");
			}

			// create an audioInputStream from this file

			AudioInputStream audioInputStream;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(file);
			} catch (Exception e) {
				printError("Unable to read from file " +
						inFileName + ".  The file type is unsupported.  " +
						"Are you sure you're using a WAV, AU, or " +
						"AIFF file (some .wav files are encoded " +
						"using mp3)?  Try using SimpleSound.convert(" +
						"String oldName, String newName) and then " +
						"try to read the new name.", e);
				return;
			}//catch

			/* We need to make an array representing this sound, so the
			 * number of bytes we will be storing cannot be greater than
			 * Integer.MAX_VALUE.  The JavaSound API also supports only
			 * integer length frame lengths.
			 * (See AudioFileFormat.getFrameLength().  I don't know why
			 * this is inconsistent with AudioInputStream.getFrameLength().)
			 */
			if ((audioInputStream.getFrameLength() *
					audioInputStream.getFormat().getFrameSize()) >
			Integer.MAX_VALUE) {
				printError("The sound in file: " + inFileName +
						" is too long." +
						"  Try using a shorter sound.");
			}
			int bufferSize = (int)audioInputStream.getFrameLength() *
					audioInputStream.getFormat().getFrameSize();

			buffer = new byte[bufferSize];

			int numBytesRead = 0;
			int offset = 0;

			//read all the bytes into the buffer
			while (true) {
				try {
					numBytesRead =
							audioInputStream.read(buffer, offset, bufferSize);
					if (numBytesRead == -1) { //no more data
						break;
					} else {
						offset += numBytesRead;
					}
				} catch (Exception e) {
					printError("Problems reading the input stream.  " +
							"You might want to try again using this " +
							" file: " + inFileName + "or a different" +
							" file.  If problems persist, ask your TA."
							, e);
				}//catch
			}//while


			/* set the format of the file, assuming that the extension
			 * is correct
			 */
			if (inFileName.toLowerCase().endsWith(".wav")) {
				audioFileFormat =
						new AudioFileFormat(AudioFileFormat.Type.WAVE,
								audioInputStream.getFormat(),
								(int)audioInputStream.getFrameLength());
			} else if (inFileName.toLowerCase().endsWith(".au")) {
				audioFileFormat =
						new AudioFileFormat(AudioFileFormat.Type.AU,
								audioInputStream.getFormat(),
								(int)audioInputStream.getFrameLength());
			} else if (inFileName.toLowerCase().endsWith(".aif") ||
					inFileName.toLowerCase().endsWith(".aiff")) {
				audioFileFormat =
						new AudioFileFormat(AudioFileFormat.Type.AIFF,
								audioInputStream.getFormat(),
								(int)audioInputStream.getFrameLength());
			} else {
				printError("Unsupported file type.  Please try again with a " +
						"file that ends in .wav, .au, .aif, or .aiff");
			}

			if (DEBUG) {
				System.out.println("New sound created from file: " + fileName);
				System.out.println("\tendianness: " +
						audioInputStream.getFormat().isBigEndian());
				System.out.println("\tencoding: " +
						audioInputStream.getFormat().getEncoding());
			}

			this.fileName = inFileName;

		}//loadFromFile(String inFileName)



		////////////////////// getting sound information /////////////////

		/**
		 * Returns an array containing all of the bytes in the specified
		 * frame.
		 *
		 * @param frameNum the index of the frame to access
		 * @return the array containing all of the bytes in frame
		 *         <code>frameNum</code>
		 * @throws SoundException if the frame number is invalid.
		 */
		public byte[] getFrame(int frameNum) throws SoundException {
			if (frameNum >= getAudioFileFormat().getFrameLength()) {
				printError("That index " + (frameNum) + ", does not exist. " +
						"The last valid index is " +
						(getAudioFileFormat().getFrameLength() - 1));
			}

			int frameSize = getAudioFileFormat().getFormat().getFrameSize();
			byte[] theFrame = new byte[frameSize];
			for (int i = 0; i < frameSize; i++) {
				theFrame[i] = buffer[frameNum * frameSize + i];
			}
			return theFrame;
		}


		/**
		 * Obtains the length of the audio data contained in the file,
		 * expressed in sample frames.
		 *
		 * @return the number of sample frames of audio data in the file
		 */
		public int getLengthInFrames() {
			return getAudioFileFormat().getFrameLength();
		}

		/**
		 * Returns the number of samples in this sound
		 * @return the number of sample frames
		 */
		public int getNumSamples() {
			return getAudioFileFormat().getFrameLength();
		}

		/**
		 * Method to create and return a SoundSample object for the given
		 * frame number
		 * @return a SoundSample object for this frame number
		 */
		public SoundSample getSample(int frameNum) {
			return new SoundSample(this, frameNum);
		}

		/**
		 * Method to create and return an array of SoundSample objects
		 * @return the array of SoundSample objects
		 */
		public SoundSample[] getSamples() {
			int numSamples = getLengthInFrames();
			SoundSample[] samples = new SoundSample[numSamples];
			for (int i = 0; i < numSamples; i++) {
				samples[i] = new SoundSample(this, i);
			}
			return samples;
		}

		/**
		 * Method to report an index exception for this sound
		 * @param index the index
		 * @param ex the exception (unused)
		 */
		private void reportIndexException(int index, Exception ex) {
			System.out.println("The index " + index +
					" isn't valid for this sound");
		}

		/**
		 * Method to get the sample at the passed index and handle
		 * any SoundExceptions
		 * @param index the desired index
		 * @return the sample value
		 */
		public int getSampleValueAt(int index) {
			int value = 0;

			try {
				value = getSampleValue(index);
			} catch (Exception ex) {
				reportIndexException(index, ex);
			}
			return value;
		}

		/**
		 * If this is a mono sound, obtains the single sample contained
		 * within this frame, else obtains the first (left) sample
		 * contained in the specified frame.
		 *
		 * @param frameNum the index of the frame to access
		 * @return an integer representation of the bytes contained within
		 * the specified frame
		 * @throws SoundException if the frame number is invalid.
		 */
		public int getSampleValue(int frameNum) throws SoundException {
			//Before we get started, lets make sure that frame exists
			if (frameNum >= getAudioFileFormat().getFrameLength()) {
				printError("You are trying to access the sample at index: "
						+ (frameNum) + ", but the last valid index is at " +
						(getAudioFileFormat().getFrameLength() - 1));
			} else if (frameNum < 0) {
				printError("You asked for the sample at index: " + (frameNum) +
						".  This number is less than zero.  Please try" +
						"again using an index in the range [0," +
						(getAudioFileFormat().getFrameLength() - 1) + "]");
			}

			AudioFormat format = getAudioFileFormat().getFormat();
			int sampleSizeInBits = format.getSampleSizeInBits();
			boolean isBigEndian = format.isBigEndian();

			byte[] theFrame = getFrame(frameNum);

			if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
				//since we're always returning the left sample,
				//we don't care if we're mono or stereo, left is
				//always first in the frame
				if (sampleSizeInBits == 8) { //8 bits == 1 byte
					return theFrame[0];
				} else if (sampleSizeInBits == 16)
					return TConversionTool.bytesToInt16(theFrame, 0,
							isBigEndian);
				else if (sampleSizeInBits == 24)
					return TConversionTool.bytesToInt24(theFrame, 0,
							isBigEndian);
				else if (sampleSizeInBits == 32)
					return TConversionTool.bytesToInt32(theFrame, 0,
							isBigEndian);
				else {
					printError("Unsupported audio encoding.  The sample " +
							"size is not recognized as a standard " +
							"format.");
					return -1;
				}
			} else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
				if (sampleSizeInBits == 8)
					return TTTConversionTool.unsignedByteToInt(theFrame[0]) -
							(int)Math.pow(2, 7);
				else if (sampleSizeInBits == 16)
					return TTTConversionTool.unsignedByteToInt16(theFrame, 0,
							isBigEndian) -
							(int)Math.pow(2, 15);
				else if (sampleSizeInBits == 24)
					return TTTConversionTool.unsignedByteToInt24(theFrame, 0,
							isBigEndian) -
							(int)Math.pow(2, 23);
				else if (sampleSizeInBits == 32)
					return TTTConversionTool.unsignedByteToInt32(theFrame, 0,
							isBigEndian) -
							(int)Math.pow(2, 31);
				else {
					printError("Unsupported audio encoding.  The sample " +
							"size is not recognized as a standard " +
							"format.");
					return -1;
				}
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
				return TTTConversionTool.alaw2linear(buffer[0]);
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
				return TTTConversionTool.ulaw2linear(buffer[0]);
			} else {
				printError("unsupported audio encoding: " +
						format.getEncoding() + ".  Currently only PCM, " +
						"ALAW and ULAW are supported.  Please try again" +
						"with a different file.");
				return -1;
			}
		}//getSample(int)

		/**
		 * Obtains the left sample of the audio data contained at the specified
		 * frame.
		 *
		 * @param frameNum the index of the frame to access
		 * @return an int representation of the bytes contained in the specified
		 *         frame.
		 * @throws SoundException if the frameNumber is invalid
		 */
		public int getLeftSample(int frameNum) throws SoundException {
			//default is to getLeftSample

			return getSampleValue(frameNum);
		}

		/**
		 * Obtains the right sample of the audio data contained at the specified
		 * frame.
		 *
		 * @param frameNum the index of the frame to access
		 * @return an int representation of the bytes contained in the specified
		 *         frame.
		 * @throws SoundException if the frameNumber is invalid, or
		 *                            the encoding isn't supported.
		 */
		public int getRightSample(int frameNum) throws SoundException {
			//Before we get started, lets make sure that frame exists
			if (frameNum >= getAudioFileFormat().getFrameLength()) {
				printError("You are trying to access the sample at index: "
						+ (frameNum) + ", but the last valid index is at " +
						(getAudioFileFormat().getFrameLength() - 1));
			} else if (frameNum < 0) {
				printError("You asked for the sample at index: " + (frameNum + 1) +
						".  This number is less than zero.  Please try" +
						" again using an index in the range [0," +
						(getAudioFileFormat().getFrameLength() - 1) + "].");
			}

			AudioFormat format = getAudioFileFormat().getFormat();
			int channels;
			if ((channels = format.getChannels()) == 1) {
				printError("Only stereo sounds have different right and left" +
						" samples.  You are using a mono sound, try " +
						"getSample(" + (frameNum) + ") instead");
				return -1;
			}
			int sampleSizeInBits = format.getSampleSizeInBits();
			boolean isBigEndian = format.isBigEndian();

			byte[] theFrame = getFrame(frameNum);

			if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
				if (sampleSizeInBits == 8) { //8 bits == 1 byte
					return theFrame[1];
				} else if (sampleSizeInBits == 16) {
					return TTTConversionTool.bytesToInt16(theFrame, 2, isBigEndian);
				} else if (sampleSizeInBits == 24) {
					return TTTConversionTool.bytesToInt24(theFrame, 3, isBigEndian);
				} else if (sampleSizeInBits == 32) {
					return TTTConversionTool.bytesToInt32(theFrame, 4, isBigEndian);
				} else {
					printError("Unsupported audio encoding.  The sample" +
							" size is not recognized as a standard" +
							" format.");
					return -1;
				}
			} else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
				if (sampleSizeInBits == 8) {
					return TTTConversionTool.unsignedByteToInt(theFrame[1]);
				} else if (sampleSizeInBits == 16) {
					return TTTConversionTool.unsignedByteToInt16(theFrame, 2, isBigEndian);
				} else if (sampleSizeInBits == 24) {
					return TTTConversionTool.unsignedByteToInt24(theFrame, 3, isBigEndian);
				} else if (sampleSizeInBits == 32) {
					return TTTConversionTool.unsignedByteToInt32(theFrame, 4, isBigEndian);
				} else {
					printError("Unsupported audio encoding.  The sample" +
							" size is not recognized as a standard" +
							" format.");
					return -1;
				}
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
				return TTTConversionTool.alaw2linear(buffer[1]);
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
				return TTTConversionTool.ulaw2linear(buffer[1]);
			} else {
				printError("unsupported audio encoding: " +
						format.getEncoding() + ".  Currently only PCM, " +
						"ALAW and ULAW are supported.  Please try again" +
						"with a different file.");
				return -1;
			}
		}



		/**
		 * Obtains the length of this sound in bytes.  Note, that this number is not
		 * neccessarily the same as the length of this sound's file in bytes.
		 *
		 * @return the sound length in bytes
		 */
		public int getLengthInBytes() {
			return buffer.length;
		}

		/**
		 * Method to return the length of the sound as the number of samples
		 * @return the length of the sound as the number of samples
		 */
		public int getLength() {
			return getNumSamples();
		}

		/**
		 * Obtains the number of channels of this sound.
		 *
		 * @return the number of channels (1 for mono, 2 for stereo), or
		 * <code>AudioSystem.NOT_SPECIFIED</code>
		 * @see AudioSystem#NOT_SPECIFIED
		 */
		public int getChannels() {
			return getAudioFileFormat().getFormat().getChannels();
		}


		/**************************************************************************/
		/************************** CHANGING THE SOUND ****************************/
		/**************************************************************************/

		/**
		 * Changes the value of each byte of the specified frame.
		 *
		 * @param frameNum the index of the frame to change
		 * @param theFrame the byte array that will be copied into this sound's
		 *                 buffer in place of the specified frame.
		 * @throws SoundException if the frameNumber is invalid.
		 */
		public void setFrame(int frameNum, byte[] theFrame) throws SoundException {
			if (frameNum >= getAudioFileFormat().getFrameLength()) {
				printError("That frame, number " + frameNum + ", does not exist. " +
						"The last valid frame number is " +
						(getAudioFileFormat().getFrameLength() - 1));
			}
			int frameSize = getAudioFileFormat().getFormat().getFrameSize();
			if (frameSize != theFrame.length)
				printError("Frame size doesn't match, line 383.  This should" +
						" never happen.  Please report the problem to a TA.");
			for (int i = 0; i < frameSize; i++) {
				buffer[frameNum * frameSize + i] = theFrame[i];
			}
		}

		/**
		 * Method to set the sample value at the passed index to the passed value
		 * @param index the index
		 * @param value the new value
		 */
		public void setSampleValueAt(int index, int value) {
			try {
				setSampleValue(index, value);
			} catch (Exception ex) {
				reportIndexException(index, ex);
			}
		}

		/**
		 * Changes the value of the sample found at the specified frame.  If this
		 * sound has more than one channel, then this defaults to setting only the
		 * first (left) sample.
		 *
		 * @param frameNum the index of the frame where the sample should be changed
		 * @param sample an int representation of the new sample to put in this
		 *               sound's buffer at the specified frame
		 * @throws SoundException if the frameNumber is invalid, or
		 *                            another problem is encountered
		 */
		public void setSampleValue(int frameNum, int sample) throws SoundException {
			AudioFormat format = getAudioFileFormat().getFormat();
			int sampleSizeInBits = format.getSampleSizeInBits();
			boolean isBigEndian = format.isBigEndian();

			byte[] theFrame = getFrame(frameNum);

			if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
				if (sampleSizeInBits == 8) { //8 bits = 1 byte = first cell in array
					theFrame[0] = (byte)sample;
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 16) { //2 bytes, first 2 cells in array
					TTTConversionTool.intToBytes16(sample, theFrame, 0, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 24) {
					TTTConversionTool.intToBytes24(sample, theFrame, 0, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 32) {
					TTTConversionTool.intToBytes32(sample, theFrame, 0, isBigEndian);
					setFrame(frameNum, theFrame);
				} else {
					printError("Unsupported audio encoding.  The sample" +
							"size is not recognized as a standard format");
				}
			}//if format == PCM_SIGNED
			else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
				if (sampleSizeInBits == 8) {
					theFrame[0] = TTTConversionTool.intToUnsignedByte(sample);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 16) {
					TTTConversionTool.intToUnsignedBytes16(sample, theFrame, 0, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 24) {
					TTTConversionTool.intToUnsignedBytes24(sample, theFrame, 0, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 32) {
					TTTConversionTool.intToUnsignedBytes32(sample, theFrame, 0, isBigEndian);
					setFrame(frameNum, theFrame);
				}

				else {
					printError("Unsupported audio encoding.  The sample" +
							" size is not recognized as a standard " +
							"format.");
				}
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
				if ((sample > Short.MAX_VALUE) || (sample < Short.MIN_VALUE))
					printError("You are trying to set the sample value to: " +
							sample + ", but the maximum value for a sample" +
							" in this format is: " + Short.MAX_VALUE +
							", and the minimum value is: " + Short.MIN_VALUE +
							".  Please choose a value in that range.");
				theFrame[0] = TTTConversionTool.linear2alaw((short)sample);
				setFrame(frameNum, theFrame);
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ULAW)) {

				if ((sample > Short.MAX_VALUE) || (sample < Short.MIN_VALUE))
					printError("You are trying to set the sample value to: " +
							sample + ", but the maximum value for a sample" +
							" in this format is: " + Short.MAX_VALUE +
							", and the minimum value is: " + Short.MIN_VALUE +
							".  Please choose a value in that range.");
				theFrame[0] = TTTConversionTool.linear2ulaw((short)sample);
				setFrame(frameNum, theFrame);
			} else {
				printError("unsupported audio encoding: " +
						format.getEncoding() + ".  Currently only PCM, " +
						"ALAW and ULAW are supported.  Please try again" +
						"with a different file.");
			}
		}//setSample(int, int)

		/**
		 * Method to set the left sample value at the passed index to the passed value
		 * @param frameNum the index of the frame where the sample should be changed
		 * @param sample an integer representation of the new sample
		 */
		public void setLeftSample(int frameNum, int sample) throws SoundException {
			setSampleValue(frameNum, sample);
		}

		/**
		 * Method to set the right sample value at the passed index to the passed value
		 * @param frameNum the index of the frame where the sample should be changed
		 * @param sample an integer representation of the new sample
		 */
		public void setRightSample(int frameNum, int sample) throws SoundException {
			AudioFormat format = getAudioFileFormat().getFormat();
			int sampleSizeInBits = format.getSampleSizeInBits();
			boolean isBigEndian = format.isBigEndian();

			if (format.getChannels() == 1)
				printError("this is a mono sound.  only stereo sounds have" +
						" different left and right samples.");

			byte[] theFrame = getFrame(frameNum);

			if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
				//right will always be the second in the frame
				if (sampleSizeInBits == 8) {
					theFrame[1] = (byte)sample;
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 16) {
					TTTConversionTool.intToBytes16(sample, theFrame, 2, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 24) {
					TTTConversionTool.intToBytes24(sample, theFrame, 3, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 32) {
					TTTConversionTool.intToBytes32(sample, theFrame, 4, isBigEndian);
					setFrame(frameNum, theFrame);
				} else {
					printError("Unsupported audio encoding.  The sample" +
							"size is not recognized as a standard format");
				}
			}//if format == PCM_SIGNED
			else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
				if (sampleSizeInBits == 8) {
					theFrame[1] = TTTConversionTool.intToUnsignedByte(sample);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 16) {
					TTTConversionTool.intToUnsignedBytes16(sample, theFrame, 2, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 24) {
					TTTConversionTool.intToUnsignedBytes24(sample, theFrame, 3, isBigEndian);
					setFrame(frameNum, theFrame);
				} else if (sampleSizeInBits == 32) {
					TTTConversionTool.intToUnsignedBytes32(sample, theFrame, 4, isBigEndian);
					setFrame(frameNum, theFrame);
				} else {
					printError("Unsupported audio encoding.  The sample" +
							" size is not recognized as a standard" +
							" format");
				}
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
				if ((sample > Short.MAX_VALUE) || (sample < Short.MIN_VALUE))
					printError("You are trying to set the sample value to: " +
							sample + ", but the maximum value for a sample" +
							" in this format is: " + Short.MAX_VALUE +
							", and the minimum value is: " + Short.MIN_VALUE +
							".  Please choose a value in that range.");
				theFrame[1] = TTTConversionTool.linear2alaw((short)sample);
				setFrame(frameNum, theFrame);
			} else if (format.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
				if ((sample > Short.MAX_VALUE) || (sample < Short.MIN_VALUE))
					printError("You are trying to set the sample value to: " +
							sample + ", but the maximum value for a sample" +
							" in this format is: " + Short.MAX_VALUE +
							", and the minimum value is: " + Short.MIN_VALUE +
							".  Please choose a value in that range.");
				theFrame[1] = TTTConversionTool.linear2ulaw((short)sample);
				setFrame(frameNum, theFrame);
			} else {
				printError("unsupported audio encoding: " +
						format.getEncoding() + ".  Currently only PCM, " +
						"ALAW and ULAW are supported.  Please try again" +
						"with a different file.");
			}
		}//setRightSample(int, int)

		//	    /**
		//	     * Method to convert a mp3 sound into a wav sound
		//	     * @param mp3File the file name of the mp3 to convert
		//	     * @param wavFile the file name to save the wav to
		//	     */
		//	    public static void convert(String mp3File, String wavFile) {
		//	        try {
		//	            Converter converter = new Converter();
		//	            converter.convert(mp3File, wavFile);
		//	        } catch (Exception ex) {
		//	            P.println("Couldn't covert the file " + mp3File);
		//	        }
		//	    }

		////////////////////// String methods ///////////////////////////////

		/**
		 * Obtains a string representation of this JavaSound.
		 * @return a String representation of this JavaSound.
		 */
		public String toString() {
			String output = "SimpleSound";

			// if there is a file name then add that to the output
			if (fileName != null) {
				output = output + " file: " + fileName;
			}

			// add the length
			output = output + " length: " + getLengthInBytes();

			return output;
		}
	}


	/**
	 * Class that represents a sample of a sound.  It knows what sound object
	 * it comes from and knows what frame number this sample is in the sound
	 * object.
	 * <br>
	 * Copyright Georgia Institute of Technology 2004
	 * @author Barb Ericson ericson@cc.gatech.edu
	 */
	class SoundSample {
		/** the sound that this element belongs to */
		private SimpleSound sound = null;

		/** the frame number of this sample in the buffer */
		private int frameNumber = 0;

		///////////////////// Constructors //////////////////////////////////

		/**
		 * Constructor that takes a sound and valueArray
		 * @param sound the sound object this sample comes from
		 * @param frameNumber the frameNumber of this sample in the sound
		 */
		public SoundSample(SimpleSound sound, int frameNumber) {
			this.sound = sound;
			this.frameNumber = frameNumber;
		}

		/////////////////// Methods /////////////////////////////////////////

		/**
		 * Method to get the value of this sample as in int
		 * and handle the possible sound exception
		 * @return the value of this sample as an int
		 */
		public int getValue() {
			int value = 0;
			try {
				value = sound.getSampleValue(frameNumber);
			} catch (SoundException ex) {
			}
			return value;
		}

		/**
		 * Method to set the value of this sample and
		 * handle the sound exception
		 * @param value the value to use
		 */
		public void setValue(int value) {
			try {
				sound.setSampleValue(frameNumber, value);
			} catch (SoundException ex) {
			}
		}

		/**
		 * Method to return a string with the information about
		 * this object
		 * @return a string with information about this object
		 */
		public String toString() {
			return "Sample at index " + frameNumber + " has value " + getValue();
		}
	}

	/**
	 * Class to use to report a sound exception
	 * <br>
	 * Copyright Georgia Institute of Technology 2004
	 * @author Unknown Undergraduate
	 * @author Barb Ericson ericson@cc.gatech.edu
	 */
	private class SoundException extends Exception {

		private static final long serialVersionUID = 7526471155622776147L;

		/** Constructor that takes String as the message for the SoundException */
		public SoundException(String message) {
			super(message);
		}
	} // end of SoundException class
}




class TTTConversionTool {

	/**
	 * Converts 2 successive bytes starting at <code>byteOffset</code> in
	 * <code>buffer</code> to a signed integer sample with 16bit range.
	 * <p>
	 * For little endian, buffer[byteOffset] is interpreted as low byte,
	 * whereas it is interpreted as high byte in big endian.
	 * <p> This is a reference function.
	 */
	public static int bytesToInt16(byte [] buffer, int byteOffset,
			boolean bigEndian) {
		return bigEndian ?
				((buffer[byteOffset] << 8) | (buffer[byteOffset + 1] & 0xFF)) :

					((buffer[byteOffset + 1] << 8) | (buffer[byteOffset] & 0xFF));
	}

	/**
	 * Converts 3 successive bytes starting at <code>byteOffset</code> in
	 * <code>buffer</code> to a signed integer sample with 24bit range.
	 * <p>
	 * For little endian, buffer[byteOffset] is interpreted as lowest byte,
	 * whereas it is interpreted as highest byte in big endian.
	 * <p> This is a reference function.
	 */
	public static int bytesToInt24(byte [] buffer, int byteOffset,
			boolean bigEndian) {
		return bigEndian ?
				((buffer[byteOffset] << 16) // let Java handle sign-bit
						| ((buffer[byteOffset + 1] & 0xFF) << 8) // inhibit sign-bit handling
						| ((buffer[byteOffset + 2] & 0xFF))) :

							((buffer[byteOffset + 2] << 16) // let Java handle sign-bit
									| ((buffer[byteOffset + 1] & 0xFF) << 8) // inhibit sign-bit handling
									| (buffer[byteOffset] & 0xFF));
	}

	/**
	 * Converts a 4 successive bytes starting at <code>byteOffset</code> in
	 * <code>buffer</code> to a signed 32bit integer sample.
	 * <p>
	 * For little endian, buffer[byteOffset] is interpreted as lowest byte,
	 * whereas it is interpreted as highest byte in big endian.
	 * <p> This is a reference function.
	 */
	public static int bytesToInt32(byte [] buffer, int byteOffset,
			boolean bigEndian) {
		return bigEndian ?
				((buffer[byteOffset] << 24) // let Java handle sign-bit
						| ((buffer[byteOffset + 1] & 0xFF) << 16) // inhibit sign-bit handling
						| ((buffer[byteOffset + 2] & 0xFF) << 8) // inhibit sign-bit handling
						| (buffer[byteOffset + 3] & 0xFF)) :

							((buffer[byteOffset + 3] << 24) // let Java handle sign-bit
									| ((buffer[byteOffset + 2] & 0xFF) << 16) // inhibit sign-bit handling
									| ((buffer[byteOffset + 1] & 0xFF) << 8) // inhibit sign-bit handling
									| (buffer[byteOffset] & 0xFF));
	}

	/////////////////////// ULAW ///////////////////////////////////////////

	private static final boolean ZEROTRAP = true;
	private static final short BIAS = 0x84;
	private static final int CLIP = 32635;
	private static final int exp_lut1[] = {
			0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3,
			4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
			5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7
	};


	/* u-law to linear conversion table */
	private static short [] u2l = {
			-32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956,
			-23932, -22908, -21884, -20860, -19836, -18812, -17788, -16764,
			-15996, -15484, -14972, -14460, -13948, -13436, -12924, -12412,
			-11900, -11388, -10876, -10364, -9852, -9340, -8828, -8316,
			-7932, -7676, -7420, -7164, -6908, -6652, -6396, -6140,
			-5884, -5628, -5372, -5116, -4860, -4604, -4348, -4092,
			-3900, -3772, -3644, -3516, -3388, -3260, -3132, -3004,
			-2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980,
			-1884, -1820, -1756, -1692, -1628, -1564, -1500, -1436,
			-1372, -1308, -1244, -1180, -1116, -1052, -988, -924,
			-876, -844, -812, -780, -748, -716, -684, -652,
			-620, -588, -556, -524, -492, -460, -428, -396,
			-372, -356, -340, -324, -308, -292, -276, -260,
			-244, -228, -212, -196, -180, -164, -148, -132,
			-120, -112, -104, -96, -88, -80, -72, -64,
			-56, -48, -40, -32, -24, -16, -8, 0,
			32124, 31100, 30076, 29052, 28028, 27004, 25980, 24956,
			23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764,
			15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412,
			11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316,
			7932, 7676, 7420, 7164, 6908, 6652, 6396, 6140,
			5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092,
			3900, 3772, 3644, 3516, 3388, 3260, 3132, 3004,
			2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980,
			1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436,
			1372, 1308, 1244, 1180, 1116, 1052, 988, 924,
			876, 844, 812, 780, 748, 716, 684, 652,
			620, 588, 556, 524, 492, 460, 428, 396,
			372, 356, 340, 324, 308, 292, 276, 260,
			244, 228, 212, 196, 180, 164, 148, 132,
			120, 112, 104, 96, 88, 80, 72, 64,
			56, 48, 40, 32, 24, 16, 8, 0
	};
	public static short ulaw2linear(byte ulawbyte) {
		return u2l[ulawbyte & 0xFF];
	}

	/**
	 * Converts a linear signed 16bit sample to a uLaw byte.
	 * Ported to Java by fb.
	 * <BR>Originally by:<BR>
	 * Craig Reese: IDA/Supercomputing Research Center <BR>
	 * Joe Campbell: Department of Defense <BR>
	 * 29 September 1989 <BR>
	 */
	public static byte linear2ulaw(int sample) {
		int sign, exponent, mantissa, ulawbyte;

		if (sample > 32767) {
			sample = 32767;
		} else if (sample < -32768) {
			sample = -32768;
		}
		/* Get the sample into sign-magnitude. */
		sign = (sample >> 8) & 0x80;    /* set aside the sign */
		if (sign != 0) {
			sample = -sample;    /* get magnitude */
		}
		if (sample > CLIP) {
			sample = CLIP;    /* clip the magnitude */
		}

		/* Convert from 16 bit linear to ulaw. */
		sample = sample + BIAS;
		exponent = exp_lut1[(sample >> 7) & 0xFF];
		mantissa = (sample >> (exponent + 3)) & 0x0F;
		ulawbyte = ~(sign | (exponent << 4) | mantissa);
		if (ZEROTRAP)
			if (ulawbyte == 0) {
				ulawbyte = 0x02;    /* optional CCITT trap */
			}
		return ((byte) ulawbyte);
	}


	/*
	 * This source code is a product of Sun Microsystems, Inc. and is provided
	 * for unrestricted use.  Users may copy or modify this source code without
	 * charge.
	 *
	 * linear2alaw() - Convert a 16-bit linear PCM value to 8-bit A-law
	 *
	 * linear2alaw() accepts an 16-bit integer and encodes it as A-law data.
	 *
	 *              Linear Input Code       Compressed Code
	 *      ------------------------        ---------------
	 *      0000000wxyza                    000wxyz
	 *      0000001wxyza                    001wxyz
	 *      000001wxyzab                    010wxyz
	 *      00001wxyzabc                    011wxyz
	 *      0001wxyzabcd                    100wxyz
	 *      001wxyzabcde                    101wxyz
	 *      01wxyzabcdef                    110wxyz
	 *      1wxyzabcdefg                    111wxyz
	 *
	 * For further information see John C. Bellamy's Digital Telephony, 1982,
	 * John Wiley & Sons, pps 98-111 and 472-476.
	 */
	private static final byte QUANT_MASK = 0xf; /* Quantization field mask. */
	private static final byte SEG_SHIFT = 4;  /* Left shift for segment number. */
	private static final short[] seg_end = {
			0xFF, 0x1FF, 0x3FF, 0x7FF, 0xFFF, 0x1FFF, 0x3FFF, 0x7FFF
	};


	/*
	 * conversion table alaw to linear
	 */
	private static short [] a2l = {
			-5504, -5248, -6016, -5760, -4480, -4224, -4992, -4736,
			-7552, -7296, -8064, -7808, -6528, -6272, -7040, -6784,
			-2752, -2624, -3008, -2880, -2240, -2112, -2496, -2368,
			-3776, -3648, -4032, -3904, -3264, -3136, -3520, -3392,
			-22016, -20992, -24064, -23040, -17920, -16896, -19968, -18944,
			-30208, -29184, -32256, -31232, -26112, -25088, -28160, -27136,
			-11008, -10496, -12032, -11520, -8960, -8448, -9984, -9472,
			-15104, -14592, -16128, -15616, -13056, -12544, -14080, -13568,
			-344, -328, -376, -360, -280, -264, -312, -296,
			-472, -456, -504, -488, -408, -392, -440, -424,
			-88, -72, -120, -104, -24, -8, -56, -40,
			-216, -200, -248, -232, -152, -136, -184, -168,
			-1376, -1312, -1504, -1440, -1120, -1056, -1248, -1184,
			-1888, -1824, -2016, -1952, -1632, -1568, -1760, -1696,
			-688, -656, -752, -720, -560, -528, -624, -592,
			-944, -912, -1008, -976, -816, -784, -880, -848,
			5504, 5248, 6016, 5760, 4480, 4224, 4992, 4736,
			7552, 7296, 8064, 7808, 6528, 6272, 7040, 6784,
			2752, 2624, 3008, 2880, 2240, 2112, 2496, 2368,
			3776, 3648, 4032, 3904, 3264, 3136, 3520, 3392,
			22016, 20992, 24064, 23040, 17920, 16896, 19968, 18944,
			30208, 29184, 32256, 31232, 26112, 25088, 28160, 27136,
			11008, 10496, 12032, 11520, 8960, 8448, 9984, 9472,
			15104, 14592, 16128, 15616, 13056, 12544, 14080, 13568,
			344, 328, 376, 360, 280, 264, 312, 296,
			472, 456, 504, 488, 408, 392, 440, 424,
			88, 72, 120, 104, 24, 8, 56, 40,
			216, 200, 248, 232, 152, 136, 184, 168,
			1376, 1312, 1504, 1440, 1120, 1056, 1248, 1184,
			1888, 1824, 2016, 1952, 1632, 1568, 1760, 1696,
			688, 656, 752, 720, 560, 528, 624, 592,
			944, 912, 1008, 976, 816, 784, 880, 848
	};

	public static short alaw2linear(byte ulawbyte) {
		return a2l[ulawbyte & 0xFF];
	}

	public static byte linear2alaw(short pcm_val)
	/* 2's complement (16-bit range) */
	{
		byte mask;
		byte seg = 8;
		byte aval;

		if (pcm_val >= 0) {
			mask = (byte) 0xD5;  /* sign (7th) bit = 1 */
		} else {
			mask = 0x55;  /* sign bit = 0 */
			pcm_val = (short)(-pcm_val - 8);
		}

		/* Convert the scaled magnitude to segment number. */
		for (int i = 0; i < 8; i++) {
			if (pcm_val <= seg_end[i]) {
				seg = (byte) i;
				break;
			}
		}

		/* Combine the sign, segment, and quantization bits. */
		if (seg >= 8) { /* out of range, return maximum value. */
			return (byte)((0x7F ^ mask) & 0xFF);
		} else {
			aval = (byte)(seg << SEG_SHIFT);
			if (seg < 2) {
				aval |= (pcm_val >> 4) & QUANT_MASK;
			} else {
				aval |= (pcm_val >> (seg + 3)) & QUANT_MASK;
			}
			return (byte)((aval ^ mask) & 0xFF);
		}
	}




	/**
	 * Converts a 16 bit sample of type <code>int</code> to 2 bytes in an array.
	 * <code>sample</code> is interpreted as signed (as Java does).
	 * <p>
	 * For little endian, buffer[byteOffset] is filled with low byte of sample,
	 * and buffer[byteOffset+1] is filled with high byte of sample + sign bit.
	 * <p> For big endian, this is reversed.
	 * <p> Before calling this function, it should be assured that
	 * <code>sample</code> is in the 16bit range - it will not be clipped.
	 * <p> This is a reference function.
	 */
	public static void intToBytes16(int sample, byte [] buffer, int byteOffset,
			boolean bigEndian) {
		if (bigEndian) {
			buffer[byteOffset++] = (byte)(sample >> 8);
			buffer[byteOffset] = (byte)(sample & 0xFF);
		} else {
			buffer[byteOffset++] = (byte)(sample & 0xFF);
			buffer[byteOffset] = (byte)(sample >> 8);
		}
	}

	/**
	 * Converts a 24 bit sample of type <code>int</code> to 3 bytes in an array.
	 * <code>sample</code> is interpreted as signed (as Java does).
	 * <p>
	 * For little endian, buffer[byteOffset] is filled with low byte of sample,
	 * and buffer[byteOffset+2] is filled with the high byte of sample +
	 * sign bit.
	 * <p> For big endian, this is reversed.
	 * <p> Before calling this function, it should be assured that
	 * <code>sample</code> is in the 24bit range - it will not be clipped.
	 * <p> This is a reference function.
	 */
	public static void intToBytes24(int sample, byte [] buffer,
			int byteOffset, boolean bigEndian) {
		if (bigEndian) {
			buffer[byteOffset++] = (byte)(sample >> 16);
			buffer[byteOffset++] = (byte)((sample >>> 8) & 0xFF);
			buffer[byteOffset] = (byte)(sample & 0xFF);
		} else {
			buffer[byteOffset++] = (byte)(sample & 0xFF);
			buffer[byteOffset++] = (byte)((sample >>> 8) & 0xFF);
			buffer[byteOffset] = (byte)(sample >> 16);
		}
	}

	/**
	 * Converts a 32 bit sample of type <code>int</code> to 4 bytes in an array.
	 * <code>sample</code> is interpreted as signed (as Java does).
	 * <p>
	 * For little endian, buffer[byteOffset] is filled with lowest byte of
	 * sample, and buffer[byteOffset+3] is filled with the high byte of
	 * sample + sign bit.
	 * <p> For big endian, this is reversed.
	 * <p> This is a reference function.
	 */
	public static void intToBytes32(int sample, byte [] buffer,
			int byteOffset, boolean bigEndian) {
		if (bigEndian) {
			buffer[byteOffset++] = (byte)(sample >> 24);
			buffer[byteOffset++] = (byte)((sample >>> 16) & 0xFF);
			buffer[byteOffset++] = (byte)((sample >>> 8) & 0xFF);
			buffer[byteOffset] = (byte)(sample & 0xFF);
		} else {
			buffer[byteOffset++] = (byte)(sample & 0xFF);
			buffer[byteOffset++] = (byte)((sample >>> 8) & 0xFF);
			buffer[byteOffset++] = (byte)((sample >>> 16) & 0xFF);
			buffer[byteOffset] = (byte)(sample >> 24);
		}
	}

	/*
	 * Byte<->Int conversions for unsigned pcm data were written
	 * by myself with help from Real's Java How-To:
	 * http://www.rgagnon.com/javadetails/java-0026.html
	 */

	public static int unsignedByteToInt(byte b) {
		/*
		 * & 0xFF while seemingly doing nothing to the individual bits,
		 * forces java to recognize the byte as unsigned.  so, we return to
		 * the calling function a number between 0 and 256.
		 */
		return ((int) b & 0xFF);
	}

	public static int unsignedByteToInt16(byte[] buffer, int offset,
			boolean isBigEndian) {
		/*
		 * here, we want to take the first byte and shift it left
		 * 8 bits then concatenate on the 8 bits in the second byte.
		 * now we have a 16 bit number that java will recognize as
		 * unsigned, so we return a number in the range [0, 65536]
		 */

		if (isBigEndian) {
			return ((unsignedByteToInt(buffer[offset]) << 8) |
					unsignedByteToInt(buffer[offset + 1]));
		} else {
			return ((unsignedByteToInt(buffer[offset + 1]) << 8) |
					unsignedByteToInt(buffer[offset]));
		}

	}

	public static int unsignedByteToInt24(byte[] buffer, int offset,
			boolean isBigEndian) {
		if (isBigEndian) {
			return ((unsignedByteToInt(buffer[offset]) << 16) |
					(unsignedByteToInt(buffer[offset + 1]) << 8) |
					unsignedByteToInt(buffer[offset + 2]));
		} else {
			return ((unsignedByteToInt(buffer[offset + 2]) << 16) |
					(unsignedByteToInt(buffer[offset + 1]) << 8) |
					unsignedByteToInt(buffer[offset]));
		}
	}

	public static int unsignedByteToInt32(byte[] buffer, int offset,
			boolean isBigEndian) {
		if (isBigEndian) {
			return ((unsignedByteToInt(buffer[offset]) << 24) |
					(unsignedByteToInt(buffer[offset + 1]) << 16) |
					(unsignedByteToInt(buffer[offset + 2]) << 8) |
					unsignedByteToInt(buffer[offset + 3]));
		} else {
			return ((unsignedByteToInt(buffer[offset + 3]) << 24) |
					(unsignedByteToInt(buffer[offset + 2]) << 16) |
					(unsignedByteToInt(buffer[offset + 1]) << 8) |
					unsignedByteToInt(buffer[offset]));
		}
	}

	public static byte intToUnsignedByte(int sample) {
		/*
		 * does the reverse of the function above
		 * we have an integer that is signed, so we're in the range
		 * [-128, 127], we want to convert to an unsigned number in
		 * the range [0,256], then put that into an unsigned byte
		 * all while java tries to treat everythign as signed.
		 *
		 * so.... say we want to set the sample value to -128
		 * in our unsigned byte, this translates to 0, so we want
		 * java's representation of -128: 10000000 to instead be stored
		 * as 0: 00000000 so, we simply xor with -128, flipping the sign bit
		 *
		 * another example we want to store the max value 127: 01111111
		 * translating into the unsigned range, the max is 256: 11111111
		 * again, you can see all we need to change is the sign bit.
		 *
		 * and lastly, for something in the middle:
		 * say we want to store the value 0: 00000000
		 * translating into the unsigned range, we have the middle
		 * value 128: 10000000
		 * again, we just want to flip the first bit
		 *
		 * something a little more tricky... say we want to store the value 32
		 * now this translates to 32--128 = 160 in unsigned representation
		 * so we start with 32 = 00100000 and we want to go to
		 *                 160 = 10100000
		 *
		 * see, we just flip the sign bit, its the same as adding 128 which
		 * is how we translate between  [-128,127] and [0,256].
		 */
		return ((byte)(sample ^ -128));
	}



	public static void intToUnsignedBytes16(int sample, byte [] buffer,
			int byteOffset, boolean bigEndian) {

		/*
		 * for this comment only, treat ^ not as XOR as we use it in java
		 * but as an exponent symbol like on a calculator, i thought 2^15
		 * would be clearer than 32768.
		 * the theory here is very simmilar to the 8 bit conversion we
		 * did above.  only now we have 16 bits we want to write into.
		 * so, we're going from the range [-2^15, 2^15-1] into the range
		 * [0, 2^16].  again, to translate, we just need to add 2^15 to
		 * our number, so we get the first byte, by shifting right 8 bits,
		 * (note: >>> is unsigned shift), and then XOR with -128 to flip the
		 * sign bit.  for the second byte, we just want the last 8 bits
		 * of our integer, so we & with 0xff to tell java to treat this
		 * as unsigned, and just copy over the bit values.
		 */
		if (bigEndian) {
			buffer[byteOffset] = (byte)(sample >>> 8 ^ -128);
			buffer[byteOffset + 1] = (byte)(sample & 0xff);
		} else {
			buffer[byteOffset + 1] = (byte)(sample >>> 8 ^ -128);
			buffer[byteOffset] = (byte)(sample & 0xff);
		}
	}

	public static void intToUnsignedBytes24(int sample, byte [] buffer,
			int byteOffset, boolean bigEndian) {
		if (bigEndian) {
			buffer[byteOffset] = (byte)(sample >>> 16 ^ -128);
			buffer[byteOffset + 1] = (byte)(sample >>> 8);
			buffer[byteOffset + 2] = (byte)(sample & 0xff);
		} else {
			buffer[byteOffset + 2] = (byte)(sample >>> 16 ^ -128);
			buffer[byteOffset + 1] = (byte)(sample >>> 8);
			buffer[byteOffset] = (byte)(sample & 0xff);
		}
	}

	public static void intToUnsignedBytes32(int sample, byte [] buffer,
			int byteOffset, boolean bigEndian) {
		if (bigEndian) {
			buffer[byteOffset] = (byte)(sample >>> 24 ^ -128);
			buffer[byteOffset + 1] = (byte)(sample >>> 16);
			buffer[byteOffset + 2] = (byte)(sample >>> 8);
			buffer[byteOffset + 3] = (byte)(sample & 0xff);
		} else {
			buffer[byteOffset + 3] = (byte)(sample >>> 24 ^ -128);
			buffer[byteOffset + 2] = (byte)(sample >>> 16);
			buffer[byteOffset + 1] = (byte)(sample >>> 8);
			buffer[byteOffset] = (byte)(sample & 0xff);
		}
	}
}



//package com.haxademic.app.dancelab.prototype;
//
//import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
//import java.util.ArrayList;
//
//import org.gstreamer.Buffer;
//import org.gstreamer.Bus;
//import org.gstreamer.GstObject;
//import org.gstreamer.elements.BufferDataAppSink;
//import org.gstreamer.elements.RGBDataAppSink;
//
//import com.haxademic.core.app.P;
//import com.haxademic.core.app.PAppletHax;
//import com.haxademic.core.app.config.AppSettings;
//import com.haxademic.core.app.config.Config;
//import com.haxademic.core.file.FileUtil;
//
//import processing.core.PApplet;
//import processing.video.Movie;
//
//public class MultipleMoviePlayers
//extends PAppletHax {
//	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
//	
//	// Performance TO_TRY:
//	// * Run on Windows
//	// * Try a different delegate object for Movie instances, launched on different threads
//	// * Java compile flags
//	
//	protected ArrayList<Movie> movies;
//	protected String movieLocation = "video/dancelab/mimics/";
//	protected String[] movieFiles = {
//			"001.portrait.mp4.noaudio.mp4",
//			"002.portrait.mp4.noaudio.mp4",
//			"003.portrait.mp4.noaudio.mp4",
//			"004.portrait.mp4.noaudio.mp4",
//			"005.portrait.mp4.noaudio.mp4",
//			"006.portrait.mp4.noaudio.mp4",
//			"007.portrait.mp4.noaudio.mp4",
//			"008.portrait.mp4.noaudio.mp4",
//			"009.portrait.mp4.noaudio.mp4",
//			"010.portrait.mp4.noaudio.mp4",
//			"011.portrait.mp4.noaudio.mp4",
//			"012.portrait.mp4.noaudio.mp4",
//			"013.portrait.mp4.noaudio.mp4",
//			"014.portrait.mp4.noaudio.mp4",
//			"015.portrait.mp4.noaudio.mp4",
//			"016.portrait.mp4.noaudio.mp4",
//			"017.portrait.mp4.noaudio.mp4",
//			"018.portrait.mp4.noaudio.mp4",
//			"019.portrait.mp4.noaudio.mp4",
//			"020.portrait.mp4.noaudio.mp4",
//			"021.portrait.mp4.noaudio.mp4",
//			"022.portrait.mp4.noaudio.mp4",
//			"023.portrait.mp4.noaudio.mp4",
//			"024.portrait.mp4.noaudio.mp4"
//	};
//	protected int videoLoadIndex = 0;
//	
//	protected int MAX_MOVIES = 4;
//		
//	protected void config() {
//		Config.setProperty( AppSettings.RENDERER, P.P2D ); // P.JAVA2D P.FX2D P.P2D P.P3D
//		Config.setProperty( AppSettings.WIDTH, 1800 );
//		Config.setProperty( AppSettings.HEIGHT, 355 );
//	}
//
//	protected void firstFrame() {
//		
//		// load movies
//		movies = new ArrayList<Movie>();
//		for(int i = 0; i < 1; i++) addNextMovie();
//	}
//	
//	public void addNextMovie() {
//		// add a new one into the mix
//		P.println("Loading: ", movieLocation + movieFiles[videoLoadIndex]);
//		OverriddenMovie movie = new OverriddenMovie(p, FileUtil.getFile(movieLocation + movieFiles[videoLoadIndex]));
//		P.println("Moive created: ", movieLocation + movieFiles[videoLoadIndex]);
//		movie.loop();
//		movies.add(movie);
//		P.println("Moive added: ", movieLocation + movieFiles[videoLoadIndex]);
//
//		// loop back on video library
//		videoLoadIndex++;
//		if(videoLoadIndex >= movieFiles.length) videoLoadIndex = 0;
//	}
//	
//	protected void restartAllMovies() {
//		// restart all movies
//		for (int i = 0; i < movies.size(); i++) {
//			movies.get(i).jump(0);
//		}
//	}
//	
//	public int numLoadedMovies() {
//		int loadedMovies = 0;
//		for (int i = 0; i < movies.size(); i++) {
//			if(movies.get(i).height > 0) loadedMovies++;
//		}
//		return loadedMovies;
//	}
//
//	protected void drawApp() {	
//		// load a new movie once in a while
//		if(p.frameCount % 100 == 0) {
////			loadMovieThread();
//			addNextMovie();
//		}
//	
//		// draw movies
//		if(movies.size() > 0) {
//			
//			if(numLoadedMovies() > MAX_MOVIES) removeOldestVideo();
//			
//			int vidW = p.width / MAX_MOVIES;
//			for (int i = 0; i < movies.size(); i++) {
//				if (movies.get(i).available()) movies.get(i).read();
//
//				int vidX = i * vidW;
//				Movie movie = movies.get(i);
//				if(movie.height > 0) {
//					float drawScale = (float)vidW / (float)movie.width;
//					p.image(movie, vidX, 0, movie.width * drawScale, movie.height * drawScale);
//				}
//			}
//		}
//		
//		// special effects
////		BrightnessFilter.instance(p).setBrightness(1.5f);
////		BrightnessFilter.instance(p).applyTo(p);
////		SaturationFilter.instance(p).setSaturation(0.3f);
////		SaturationFilter.instance(p).applyTo(p);
//	}
//	
//	
//	////////////////////////////////
//	// Load the next movie on a thread to prevent blocking
//	////////////////////////////////
//	protected UpdateAsync _updater;
//	protected boolean _updateComplete = true;
//	protected Thread _updateThread;
//
//	class UpdateAsync implements Runnable {
//		public UpdateAsync() {}    
//		public void run() {
//			addNextMovie();
////			restartAllMovies();
//			_updateComplete = true;
//			P.println("addNextMovie() complete!");
//		} 
//	}
//
//	public void loadMovieThread() {
//		if(_updateComplete == true) {
//			_updateComplete = false;
//			_updater = new UpdateAsync();	// if(_updater == null) 
//			_updateThread = new Thread( _updater );
//			_updateThread.start();
//		} else {
//			P.println("loadMovieThread() not complete. failed to load next movie.");
//		}
//	}
//	
//	////////////////////////////////
//	// Cleanup thread
//	////////////////////////////////
//	protected RemoveAsync _remover;
//	protected boolean _removeComplete = true;
//	protected Thread _removeThread;
//
//	class RemoveAsync implements Runnable {
//		// Dispose the oldest movie on a thread to prevent blocking
//		// Also do some manual garbage collection to help keep the heap small
//		public RemoveAsync() {}    
//		public void run() {
//			final Movie oldestMovie = movies.remove(0);
//			oldestMovie.stop();
//			oldestMovie.dispose();
//			System.gc();
//		} 
//	}
//
//	public void removeOldestVideo() {
//		if(_removeComplete == true) {
//			_removeComplete = false;
//			_remover = new RemoveAsync();	// if(_remover == null) 
//			_removeThread = new Thread( _remover );
//			_removeThread.start();
//		} else {
//			P.println("removeOldestVideo() not complete. failed to remove movie.");
//		}
//	}
//
//	
////	public void movieEvent(Movie m) {
////		if(m.loaded == false) P.println("MOVIE ERROR: NOT LOADED");
////		if(m.width == 0) P.println("MOVIE ERROR: NO WIDTH");
////		m.read();
////	}
//
//	
//	public class OverriddenMovie extends Movie {
//
//		public OverriddenMovie(PApplet arg0, String arg1) {
//			super(arg0, arg1);
//		}
//
//		protected void initSink() {
//
//			if (bufferSink != null || (parent.g.isGL())) {
//
//				useBufferSink = true;
//
//				if (bufferSink != null) {
//					getSinkMethods();
//				}
//
//				if (copyMask == null || copyMask.equals("")) {
//					initCopyMask();
//				}
//
//
//				natSink = new BufferDataAppSink("nat", copyMask,
//						new BufferDataAppSink.Listener() {
//					public void bufferFrame(int w, int h, Buffer buffer) {
//						invokeEvent(w, h, buffer);
//					}
//				});
//
//				natSink.setAutoDisposeBuffer(false);
//				playbin.setVideoSink(natSink);
//				// The setVideoSink() method sets the videoSink as a property of the
//				// PlayBin, which increments the refcount of the videoSink element.
//				// Disposing here once to decrement the refcount.
//				natSink.dispose();
//
//			} else {
//				rgbSink = new RGBDataAppSink("rgb",
//						new RGBDataAppSink.Listener() {
//					public void rgbFrame(int w, int h, IntBuffer buffer) {
//						invokeEvent(w, h, buffer);
//					}
//				});
//
//				// Setting direct buffer passing in the video sink.
//				rgbSink.setPassDirectBuffer(true);
//				playbin.setVideoSink(rgbSink);
//				// The setVideoSink() method sets the videoSink as a property of the
//				// PlayBin, which increments the refcount of the videoSink element.
//				// Disposing here once to decrement the refcount.
//				rgbSink.dispose();
//
//			}
//
//			// Creating bus to handle end-of-stream event.
//			Bus bus = playbin.getBus();
//			bus.connect(new Bus.EOS() {
//				public void endOfStream(GstObject element) {
//					eosEvent();
//				}
//			});
//
//			sinkReady = true;
//			newFrame = false;
//		}
//
//		
//		public synchronized void read() {
////			if(1==2) {
//			if (frameRate < 0) {
//				// Framerate not set yet, so we obtain from stream,
//				// which is already playing since we are in read().
//				frameRate = getSourceFrameRate();
//			}
//			if (volume < 0) {
//				// Idem for volume
//				volume = (float)playbin.getVolume();
//			}
//	
//			if (useBufferSink) { // The native buffer from gstreamer is copied to the buffer sink.
//				outdatedPixels = true;
//				if (natBuffer == null) {
//					return;
//				}
//	
//				if (firstFrame) {
//					super.init(bufWidth, bufHeight, ARGB, 1);
//					firstFrame = false;
//				}
//	
//				if (bufferSink == null) {
//					Object cache = parent.g.getCache(this);
//					if (cache == null) {
//						return;
//					}
//					setBufferSink(cache);
//					getSinkMethods();
//				}
//	
//				ByteBuffer byteBuffer = natBuffer.getByteBuffer();
//	
//				try {
//					sinkCopyMethod.invoke(bufferSink, new Object[] { natBuffer, byteBuffer, bufWidth, bufHeight });
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//	
//				natBuffer = null;
//			} else { // The pixels just read from gstreamer are copied to the pixels array.
//				if (copyPixels == null) {
//					return;
//				}
//	
//				if (firstFrame) {
//					super.init(bufWidth, bufHeight, RGB, 1);
//					firstFrame = false;
//				}
//	
//				int[] temp = pixels;
//				pixels = copyPixels;
//				updatePixels();
//				copyPixels = temp;
//			}
//	
//			available = false;
//			newFrame = true;
//			}
////		}
//	}
//}

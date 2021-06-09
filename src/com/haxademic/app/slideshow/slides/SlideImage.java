package com.haxademic.app.slideshow.slides;

import java.io.File;

import com.haxademic.app.slideshow.Slideshow;
import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import gifAnimation.Gif;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Movie;

public class SlideImage
implements IAppStoreListener {

	protected Slideshow p;

	// media objects
	protected PImage image;
	protected Movie movie;
	protected Gif gif;
	protected ImageSequenceMovieClip imageSequence;

	// movie helpers
	protected boolean moviePaused = false;
	protected boolean movieStopped = true;

	// media file loading
	protected String imagePath;
	protected int slideIndex;
	protected boolean loaded = false;

	// position
	protected float[] cropOffset;
	protected PVector startOffset = new PVector();
	protected PVector endOffset = new PVector();
	protected float speedZ = 1.05f;
	protected float curZ = 0;
	protected float curX = 0;
	protected float curY = 0;
	protected boolean letterbox = false;

	// fade
	protected float fadeSpeed = 0.02f;
	protected float fadeOutSpeed = 0.02f;
	protected float fadeAwaySpeed = 0.001f;
	protected LinearFloat fadeOutSpeedMult = new LinearFloat(0, 0.005f);
	protected LinearFloat queueDelay = new LinearFloat(0, fadeSpeed);
	protected LinearFloat fadeInProgress = new LinearFloat(0, fadeSpeed);
	protected LinearFloat fadeOutProgress = new LinearFloat(0, fadeOutSpeed);
	protected boolean fadesIn = true;
	protected boolean fadesOut = true;
	protected boolean fadesAway = false;
	protected boolean scalesUp = false;
	protected boolean scalesDown = false;

	// frame count
	protected float activeFrames = 0;
	protected int autoOutFrames = 10;

	// state
	public enum SlideState {
		INACTIVE,
		QUEUED,
		FADE_IN,
		SHOWING,
		FADE_OUT,
		REMOVE
	}
	protected SlideState state = SlideState.INACTIVE;

	// custom animation/behavior props
	protected boolean loops = false;
	protected boolean advanceAfterLoop = false;
	protected boolean advanceOnComplete = false;
	protected boolean autoAdvanceQueued = false;
	protected boolean noAnimDelay = false;
	protected int queueStartAnim = -1;
	protected int waitsFrames = 1;
	protected int autoClickFrames = -1;

	// caption
	protected String caption = null;
	protected String title = null;
	public static String newline = System.getProperty("line.separator");

	public SlideImage(String imagePath, int slideIndex) {
		p = (Slideshow) P.p;
		P.store.addListener(this);
		this.imagePath = imagePath;
		this.slideIndex = slideIndex;
		loadMedia();
		checkCustomSlideProps();
	}

	protected void loadMedia() {
		if(imagePath.indexOf(".png") != -1) image = P.p.requestImage(imagePath);
		if(imagePath.indexOf(".jpg") != -1) image = P.p.requestImage(imagePath);
		if(imagePath.indexOf(".mp4") != -1) movie = new Movie(P.p, imagePath);
		if(imagePath.indexOf(".mov") != -1) movie = new Movie(P.p, imagePath);
		if(imagePath.indexOf(".gif") != -1) gif = new Gif(P.p, imagePath);
		if(imagePath.indexOf(".anim") != -1) imageSequence = new ImageSequenceMovieClip(imagePath + File.separator, "png", 30);
	}

	protected void checkCustomSlideProps() {
		// letterbox
		if(imagePath.indexOf("letterbox") != -1) letterbox = true;
		// slide directions
		if(imagePath.indexOf("in_right") != -1) startOffset.set(-1f, 0);
		if(imagePath.indexOf("in_down") != -1) startOffset.set(0, -1f);
		if(imagePath.indexOf("in_left") != -1) startOffset.set(1f, 0);
		if(imagePath.indexOf("in_up") != -1) startOffset.set(0, 1f);
		if(imagePath.indexOf("out_right") != -1) endOffset.set(1f, 0);
		if(imagePath.indexOf("out_down") != -1) endOffset.set(0, 1);
		if(imagePath.indexOf("out_left") != -1) endOffset.set(-1f, 0);
		if(imagePath.indexOf("out_up") != -1) endOffset.set(0, -1);
		if(imagePath.indexOf("out_back") != -1);

		// slide into distance
		if(imagePath.indexOf("fade_away") != -1) fadesAway = true;
		// disable/tweak fades
		if(imagePath.indexOf("no_fade_in") != -1) fadesIn = false;
		if(imagePath.indexOf("no_fade_out") != -1) fadesOut = false;
		if(imagePath.indexOf("no_queue_delay") != -1) queueDelay.setInc(1f);
		if(imagePath.indexOf("no_exit_delay") != -1) fadeOutSpeed = 1;
		if(imagePath.indexOf("fast_fades") != -1) {
			fadeSpeed = 0.1f;
			fadeInProgress.setInc(fadeSpeed);
			fadeOutProgress.setInc(fadeSpeed);
		}
		// scale down while exiting?
		if(imagePath.indexOf("scale_up") != -1) scalesUp = true;
		if(imagePath.indexOf("scale_down") != -1) scalesDown = true;
		// video loops?
		if(imagePath.indexOf("loops") != -1 || gif != null) loops = true;
		if(imagePath.indexOf("no_anim_delay") != -1) noAnimDelay = true;
		if(imagePath.indexOf("advance_after_loop") != -1) advanceAfterLoop = true;
		if(imagePath.indexOf("advance_on_complete") != -1) advanceOnComplete = true;
		// prevent gif loops?
		if(imagePath.indexOf("noloop") != -1) loops = false;
		// how many slides do we wait to exit?
		int waitsStrIndex = imagePath.indexOf("waits");
		if(waitsStrIndex != -1) {
			waitsFrames = ConvertUtil.stringToInt(imagePath.substring(waitsStrIndex).split("-")[1]);
		}
		// auto-advance after x slides
		int autoClickIndex = imagePath.indexOf("autoclick");
		if(autoClickIndex != -1) {
			autoClickFrames = ConvertUtil.stringToInt(imagePath.substring(autoClickIndex).split("-")[1]);
		}
		// caption
		int captionIndex = imagePath.indexOf("caption");
		if(captionIndex != -1) {
			caption = imagePath.substring(captionIndex).split("-")[1];
			caption = caption.replaceAll("_", " ");
			caption = caption.replaceAll("dash", "-");
		}
		// title
		int titleIndex = imagePath.indexOf("title");
		if(titleIndex != -1) {
			title = imagePath.substring(titleIndex).split("-")[1];
			title = title.replaceAll("_", " ");
			title = title.replaceAll("qqq", "?");
			title = title.replaceAll("colon", ":");
			title = title.replaceAll("newline", newline);
		}
	}
	
	public SlideImage setLoops(boolean loops) { this.loops = loops; return this; }
	public SlideImage setAdvanceVideoOnComplete(boolean advanceOnComplete) { this.advanceOnComplete = advanceOnComplete; this.advanceAfterLoop = advanceOnComplete; return this; }
	public SlideImage setLetterbox(boolean letterbox) { this.letterbox = letterbox; return this; }
	public SlideImage setAutoClickFrames(int autoClickFrames) { this.autoClickFrames = autoClickFrames; return this; }
	public SlideImage setCrossfades() { queueDelay.setInc(1f); fadesOut = false; return this; }

	public PImage image() {
		if(movie != null) { movie.volume(0); movie.volume(0.1f); return movie; }
		if(gif != null) return gif;
		if(imageSequence != null) return imageSequence.image();
		return image;
	}
	
	public boolean isMovie() {
		return movie != null;
	}

	public String caption() {
		return caption;
	}

	public String title() {
		return title;
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	public boolean isShowing() {
		return state != SlideState.INACTIVE && state != SlideState.QUEUED;
	}

	public boolean canAdvanceAfterLoop() {
		return advanceAfterLoop == true && autoAdvanceQueued == false;
	}

	public boolean willAutoAdvance() {
		return autoAdvanceQueued == true;
	}

	public void advanceAfterComplete() {
		if(canAdvanceAfterLoop()) {
			autoAdvanceQueued = true;
			if(movie != null) movie.noLoop();
		}
	}

	protected void setState(SlideState newState) {
		SlideState lastState = state;
		if(state == newState) return;
		state = newState;
		switch(state) {
			case QUEUED :
				queueAnimation();
				queueDelay.setCurrent(0);
				queueDelay.setTarget(1f);
				break;
			case FADE_IN :
				launch();
				if(noAnimDelay == true) queueStartAnim = 0;
				break;
			case SHOWING :
				if(noAnimDelay == false) startAnimations();
				if(advanceOnComplete) autoAdvanceQueued = true;
				break;
			case FADE_OUT :
				if(fadesAway == true) {
					fadeOutSpeedMult.setCurrent(0);
					fadeOutSpeedMult.setTarget(1f);
					fadeOutProgress.setInc(fadeAwaySpeed);
				}
				else {
					fadeOutProgress.setInc(fadeOutSpeed);
				}
				fadeOutProgress.setCurrent(0);
				fadeOutProgress.setTarget(1f);
				autoAdvanceQueued = false;
				queueStartAnim = -1;
				break;
			case REMOVE :
				if(lastState == SlideState.QUEUED) resetQueue();
				if(lastState == SlideState.FADE_IN) fadeOutProgress.setCurrent( 1f - fadeInProgress.value() );
				if(lastState == SlideState.SHOWING) fadeOutProgress.setCurrent(0);
				fadeOutProgress.setInc(fadeSpeed);
				fadeOutProgress.setTarget(1f);
				autoAdvanceQueued = false;
				queueStartAnim = -1;
				break;
			case INACTIVE :
				stopAnimations();
				autoAdvanceQueued = false;
				queueStartAnim = -1;
				break;
			default:
				break;
		}
	}

	public void launch() {
		resetQueue();
		fadeInProgress.setCurrent(0);
		fadeInProgress.setTarget(1);
		curZ = 0;
		activeFrames = 0;
	}

	protected void resetQueue() {
		queueDelay.setCurrent(0);
		queueDelay.setTarget(0);
	}

	// Animation helpers

	protected void moviePauseOnFirstFrame() {
		movie.jump(0);
		movie.play();
		movie.pause();
		movie.volume(0);
		moviePaused = true;
		movieStopped = false;
	}

	protected void moviePlay() {
		movie.play();
		movie.volume(0);
		movieStopped = false;
	}

	protected void movieStop() {
		movie.stop();
		movieStopped = true;
	}

	protected void movieLoop() {
		movieUnpause();
		movie.loop();
		movie.volume(0);
		movieStopped = false;
	}

	protected void movieUnpause() {
		if(moviePaused == false) return;
		movie.pause();
		moviePaused = false;
		movieStopped = false;
	}

	protected void imageSequencePauseOnFirstFrame() {
		imageSequence.seek(0);
		imageSequence.play();
		imageSequence.pause();
	}

	protected void playGif() {
		gif.jump(0);
		if(loops == true) gif.loop();
		else gif.play();
	}

	protected void queueAnimation() {
		// pause on first frame until showing (but gifs can just play)
		if(movie != null) moviePauseOnFirstFrame();
		if(imageSequence != null) imageSequencePauseOnFirstFrame();
		if(gif != null) playGif();
	}

	protected void startAnimations() {
		if(movie != null) {
			if(loops == true) movieLoop();
			else moviePlay();
		}
		if(imageSequence != null) {
			if(loops == true) imageSequence.loop();
			else imageSequence.play();
		}
	}

	protected void stopAnimations() {
		if(movie != null) {
			movieStop();
			// movie.dispose();
		}
		if(gif != null) {
			gif.stop();
			// gif.dispose();
		}
		if(imageSequence != null) {
			imageSequence.stop();
			// gif.dispose();
		}
	}

	protected void preloadAndCache(PGraphics buffer) {
		// preload image sequences
		if(imageSequence != null) {
			imageSequence.preCacheImages(buffer);
			imageSequence.update();
		}

		// check image loaded status
		if(loaded == false && image() != null && image().width > 200) loaded = true;

		// get crop offset after loading
		if(loaded == true && cropOffset == null) {
			boolean cropFill = (letterbox == false);
			float[] cropProps = ImageUtil.getOffsetAndSizeToCrop(buffer.width, buffer.height, image().width, image().height, cropFill);
			cropOffset = cropProps.clone();
		}
	}

	public void update(PGraphics buffer) {

		// delayed launch to allow previous slide to exit
		if(state == SlideState.QUEUED) {
			queueDelay.update();
			if(queueDelay.value() == 1) setState(SlideState.FADE_IN);
		}

		preloadAndCache(buffer);
		updateFadeProgress();
		checkAutoAdvance();
		checkQueueAnim();
		if(isShowing() == true) {
			// if(movie != null) P.println(slideIndex, " - ", movie.time() / movie.duration());
			draw(buffer);
		}
	}

	protected void updateFadeProgress() {
		if(state == SlideState.FADE_OUT || state == SlideState.REMOVE) {
			fadeOutProgress.update();
			if(fadeOutProgress.value() >= 1) setState(SlideState.INACTIVE);
		} else if(state == SlideState.FADE_IN) {
			fadeInProgress.update();
			if(fadeInProgress.value() >= 1) setState(SlideState.SHOWING);
		} else if(state == SlideState.SHOWING) {
			if(waitsFrames == 0 && activeFrames >= autoOutFrames) {
				setState(SlideState.FADE_OUT);
			}
		}
	}

	protected boolean movieIsFinished() {
		// return movie.time() == movie.duration();
		DebugView.setValue("movie time", movie.time());
		DebugView.setValue("movie duration", movie.duration());
		return P.abs(movie.time() - movie.duration()) < 0.1f;
	}

	protected void checkQueueAnim() {
		// give 30 frames for movie to queue before playing without the full `fadeInProgress` delay
		if(queueStartAnim != -1) {
			queueStartAnim++;
			if(queueStartAnim >= 30) {
				queueStartAnim = -1;
				startAnimations();
			}
		}
	}
	
	protected void checkAutoAdvance() {
		if(state == SlideState.SHOWING) {
			// autoclick after delay
			if(autoClickFrames != -1 && activeFrames >= autoClickFrames) {
				if(slideIndex == p.getSlideIndex()) {
					p.nextSlide();
				}
			}
			// advance on movie complete
			if(movieStopped == false) {
				if(autoAdvanceQueued == true && movieIsFinished()) {
					p.nextSlide();
				}
			}
		}
	}

	protected float easeInProgress(float progress) {
		return Penner.easeInQuart(progress);
	}

	protected float easeOutProgress(float progress) {
		return Penner.easeOutQuart(progress);
	}

	protected void draw(PGraphics buffer) {
		activeFrames++;

		buffer.pushMatrix();

		// update position
		if(state == SlideState.FADE_IN) {
			curX = startOffset.x * (float) buffer.width * easeInProgress(1f - fadeInProgress.value());	// ease in since progress is reversed
			curY = startOffset.y * (float) buffer.height * easeInProgress(1f - fadeInProgress.value());
		} else if(state == SlideState.FADE_OUT) {
			if(fadesAway == true) {
				fadeOutSpeedMult.update();
				curZ -= speedZ * fadeOutSpeedMult.value();
			}
			curX = endOffset.x * (float) buffer.width * easeInProgress(fadeOutProgress.value());
			curY = endOffset.y * (float) buffer.height * easeInProgress(fadeOutProgress.value());
		} else {
			curX = 0;
			curY = 0;
		}
		buffer.translate(curX, curY, curZ);

		// fade to black
		float curFade = 1f;
		if((state == SlideState.FADE_OUT && fadesOut == true) || state == SlideState.REMOVE) curFade = 1f - fadeOutProgress.value();
		if(state == SlideState.FADE_IN && fadesIn == true) curFade = fadeInProgress.value();
		buffer.tint( 255, curFade * 255 );

		// draw image
		if(cropOffset != null) {
			// image size
			float imageW = cropOffset[2];
			float imageH = cropOffset[3];
			if(state == SlideState.FADE_IN && scalesUp == true) {
				imageW *= easeOutProgress(fadeInProgress.value());
				imageH *= easeOutProgress(fadeInProgress.value());
			}
			if(state == SlideState.FADE_OUT && scalesDown == true) {
				imageW *= easeInProgress(1f - fadeOutProgress.value());
				imageH *= easeInProgress(1f - fadeOutProgress.value());
			}

			if(image != null) buffer.image(image, 0, 0, imageW, imageH);
			if(movie != null) buffer.image(movie, 0, 0, imageW, imageH);
			if(gif != null) buffer.image(gif, 0, 0, imageW, imageH);
			if(imageSequence != null) {
				PImage frameImg = (imageSequence.isPlaying() == true) ? imageSequence.image() : imageSequence.getFrame(imageSequence.numImages() - 1);
				buffer.image(frameImg, 0, 0, imageW, imageH);
			}
		}
		PG.resetPImageAlpha(buffer);

		buffer.popMatrix();
	}

	public void updatedNumber(String storeKey, Number val) {
		if(storeKey == SlideshowState.SLIDE_INDEX.id()) {
			checkAppSlideIndex(val.intValue());
		}
	}

	protected void checkAppSlideIndex(int appIndex) {
		if(appIndex == slideIndex) setState(SlideState.QUEUED);
		else if(appIndex >= slideIndex + waitsFrames && waitsFrames != 0 && isShowing() == true) setState(SlideState.FADE_OUT);
		else if(appIndex < slideIndex && (isShowing() == true || state == SlideState.QUEUED)) setState(SlideState.REMOVE);
	}

	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
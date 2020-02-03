package com.haxademic.demo.media.video;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.media.video.MovieBuffer;
import com.haxademic.core.render.FrameLoop;

public class Demo_MovieBuffer_audioDisplacement 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MovieBuffer movieBuffer;
	protected WavPlayer player;
	protected BaseTexture audioTexture;
	
	protected void config() {
		Config.setAppSize(1920, 1080);
	}
	
	protected void firstFrame() {
		// init looping video
//		movieBuffer = new MovieBuffer("D:\\workspace\\ctd-design-studio\\_assets\\spatial-prototyping\\RFID.MOV");
		movieBuffer = new MovieBuffer(DemoAssets.movieFractalCube());
		movieBuffer.movie.loop();
		DebugView.setTexture("Video original", movieBuffer.movie);

		
		// init audio
		// ...and send Beads audio player analyzer to PAppletHax
		player = new WavPlayer(WavPlayer.newAudioContext());
		player.loopWav(FileUtil.getPath("haxademic/audio/cacheflowe_bigger_loop.wav"));
		AudioIn.instance(new AudioInputBeads(player.context()));
		
		// init audioreactive texture
		audioTexture = new TextureEQGrid(512, 128);
		DebugView.setTexture("audioreactive", audioTexture.texture());
	}

	protected void drawApp() {
		// update audio texture
		audioTexture.update();
		if(FrameLoop.frameMod(200)) audioTexture.newLineMode();
		
		// draw video to screen once we've received its frames
//		p.background(0);
		if(movieBuffer.buffer != null && movieBuffer.hasNewFrame) {
//			PG.setTextureRepeat(movieBuffer.buffer, true);
			
			// apply audio texture to displacement shader on video buffer
			DisplacementMapFilter.instance(P.p).setMode(6);
			DisplacementMapFilter.instance(P.p).setAmp(0.01f);
			DisplacementMapFilter.instance(P.p).setMap(audioTexture.texture());
			DisplacementMapFilter.instance(P.p).applyTo(movieBuffer.buffer);
			
			// draw to screen, letterboxed
			ImageUtil.cropFillCopyImage(movieBuffer.buffer, p.g, false);
		}
	}
}

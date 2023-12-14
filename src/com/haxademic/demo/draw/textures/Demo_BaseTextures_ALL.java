package com.haxademic.demo.draw.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.textures.pgraphics.TextureAppFrameWaveformCircle;
import com.haxademic.core.draw.textures.pgraphics.TextureAudioBlocksDeform;
import com.haxademic.core.draw.textures.pgraphics.TextureAudioSheetDeform;
import com.haxademic.core.draw.textures.pgraphics.TextureAudioTube;
import com.haxademic.core.draw.textures.pgraphics.TextureBarsEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureBlocksSheet;
import com.haxademic.core.draw.textures.pgraphics.TextureConcentricDashedCubes;
import com.haxademic.core.draw.textures.pgraphics.TextureCyclingRadialGradient;
import com.haxademic.core.draw.textures.pgraphics.TextureDashedLineSine;
import com.haxademic.core.draw.textures.pgraphics.TextureEQBandDistribute;
import com.haxademic.core.draw.textures.pgraphics.TextureEQChladni;
import com.haxademic.core.draw.textures.pgraphics.TextureEQColumns;
import com.haxademic.core.draw.textures.pgraphics.TextureEQConcentricCircles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQFloatParticles;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesConnected;
import com.haxademic.core.draw.textures.pgraphics.TextureEQLinesTerrain;
import com.haxademic.core.draw.textures.pgraphics.TextureEQPointsDeformAndTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureEQRadialLollipops;
import com.haxademic.core.draw.textures.pgraphics.TextureEQTextLog;
import com.haxademic.core.draw.textures.pgraphics.TextureFractalPolygons;
import com.haxademic.core.draw.textures.pgraphics.TextureImageTileScroll;
import com.haxademic.core.draw.textures.pgraphics.TextureImageTimeStepper;
import com.haxademic.core.draw.textures.pgraphics.TextureLinesEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureMeshAudioDeform;
import com.haxademic.core.draw.textures.pgraphics.TextureNoiseLines;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterCube;
import com.haxademic.core.draw.textures.pgraphics.TextureOuterSphere;
import com.haxademic.core.draw.textures.pgraphics.TexturePixelatedAudio;
import com.haxademic.core.draw.textures.pgraphics.TexturePolygonLerpedVertices;
import com.haxademic.core.draw.textures.pgraphics.TextureRadialGridPulse;
import com.haxademic.core.draw.textures.pgraphics.TextureRotatorShape;
import com.haxademic.core.draw.textures.pgraphics.TextureScrollingColumns;
import com.haxademic.core.draw.textures.pgraphics.TextureShaderTimeStepper;
import com.haxademic.core.draw.textures.pgraphics.TextureSphereOfBoxes;
import com.haxademic.core.draw.textures.pgraphics.TextureSphereOfCubes;
import com.haxademic.core.draw.textures.pgraphics.TextureSphereRotate;
import com.haxademic.core.draw.textures.pgraphics.TextureSvgPattern;
import com.haxademic.core.draw.textures.pgraphics.TextureTwistingSquares;
import com.haxademic.core.draw.textures.pgraphics.TextureVectorFieldEQ;
import com.haxademic.core.draw.textures.pgraphics.TextureWaveformCircle;
import com.haxademic.core.draw.textures.pgraphics.TextureWaveformSimple;
import com.haxademic.core.draw.textures.pgraphics.TextureWords2d;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.devices.AbletonNotes;
import com.haxademic.core.hardware.midi.devices.AkaiMpdPads;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.devices.TouchOscPads;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.ui.UI;

public class Demo_BaseTextures_ALL 
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	protected BaseTexture[] allTextures;
	protected int textureIndex = 0;
	protected String TEX_INDEX = "TEX_INDEX";
	float frames = 500;

	protected InputTrigger _colorTrigger = new InputTrigger().addKeyCodes(new char[]{'c'}).addOscMessages(new String[]{TouchOscPads.PAD_01}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_01, LaunchControl.PAD_03, AbletonNotes.NOTE_01});
	protected InputTrigger _rotationTrigger = new InputTrigger().addKeyCodes(new char[]{'v'}).addOscMessages(new String[]{TouchOscPads.PAD_02}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_02, LaunchControl.PAD_04, AbletonNotes.NOTE_02});
	protected InputTrigger _timingTrigger = new InputTrigger().addKeyCodes(new char[]{'n'}).addOscMessages(new String[]{TouchOscPads.PAD_03}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_03, LaunchControl.PAD_01, AbletonNotes.NOTE_03});
	protected InputTrigger _modeTrigger = new InputTrigger().addKeyCodes(new char[]{'m'}).addOscMessages(new String[]{TouchOscPads.PAD_04}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_04, LaunchControl.PAD_05, AbletonNotes.NOTE_04});
	protected InputTrigger _timingSectionTrigger = new InputTrigger().addKeyCodes(new char[]{'f'}).addOscMessages(new String[]{TouchOscPads.PAD_05}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_05, LaunchControl.PAD_02, AbletonNotes.NOTE_05});
	protected InputTrigger _bigChangeTrigger = new InputTrigger().addKeyCodes(new char[]{' '}).addOscMessages(new String[]{TouchOscPads.PAD_07}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_07, LaunchControl.PAD_08, AbletonNotes.NOTE_07});
	protected InputTrigger _lineModeTrigger = new InputTrigger().addKeyCodes(new char[]{'l'}).addOscMessages(new String[]{TouchOscPads.PAD_08}).addMidiNotes(new Integer[]{AkaiMpdPads.PAD_08, LaunchControl.PAD_06, AbletonNotes.NOTE_08});

	protected WavPlayer player;

	
	// TODO:
	// * Toggle audio vs. frame-based testing
	
	protected void config() {
		Config.setAppSize(1500, 1500);
		Config.setAppLocation(100, 100);
		Config.setProperty( AppSettings.FULLSCREEN, false );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)frames);
	}

	protected void firstFrame() {
		// send Beads audio player analyzer to PAppletHax
		AudioUtil.setPrimaryMixer();
		player = new WavPlayer(); // WavPlayer.newAudioContext()
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext));
		String soundFile = FileUtil.getPath(DemoAssets.audioBrimBeatPath);
		// soundFile = "D:\\workspace\\att-connected-canvas\\_assets\\audio-viz\\connected-canvas-bball-audio-test_AME\\Comp_1.wav";
		soundFile = "D:\\workspace\\att-connected-canvas\\_assets\\audio-viz\\b-ball-vids\\Recording_2023-12-13_160422-00.00.06.213-00.00.13.063.mp4.wav";
		player.loopWav(soundFile);
		// player.setVolume(soundFile, 0.01f);
		
		// init textures
		int w = p.width; 
		int h = p.height;
		
		OpenGLUtil.setTextureRepeat(g);
		
		allTextures = new BaseTexture[]{
			// new TextureEQConcentricCircles(w, h),
			new TextureEQChladni(w, h),
			new TextureEQRadialLollipops(w, h),
			// new TextureVectorFieldEQ(w, h),
			new TextureEQLinesConnected(w, h),
			new TextureEQFloatParticles(w, h),
			// new TextureWaveformCircle(w, h),
			new TextureEQTextLog(w, h),
			// new TextureOuterCube(w, h),
			// new TextureOuterSphere(w, h),
			// new TextureEQLinesTerrain(w, h),
			// new TextureConcentricDashedCubes(w, h),
			new TextureEQPointsDeformAndTexture(w, h),

			/*
//			new TextureAppFrame2d(w, h),			// not really a texture
//			new TextureAppFrameEq2d(w, h),			// not really a texture
			new TextureAppFrameWaveformCircle(w, h),// not really a texture
			new TextureAudioBlocksDeform(w, h),
			new TextureAudioSheetDeform(w, h),
			new TextureAudioTube(w, h),
			new TextureBarsEQ(w, h),
//			new TextureBasicWindowShade(w, h),
//			new TextureBlobSheet(w, h),				// NEEDS Z-DEFORM FIX
			new TextureBlocksSheet(w, h),			// NEEDS FIX
//			new TextureColorAudioFade(w, h),
//			new TextureColorAudioSlide(w, h),
			new TextureConcentricDashedCubes(w, h),
			new TextureCyclingRadialGradient(w, h),
			new TextureDashedLineSine(w, h),
			new TextureEQBandDistribute(w, h),
			new TextureEQColumns(w, h),
			new TextureEQConcentricCircles(w, h),
			new TextureEQFloatParticles(w, h),
			new TextureEQGrid(w, h),				// NEED MAX BLOCK SIZE
			new TextureEQLinesTerrain(w, h),		// NEEDS FIXING (but just worked??)
			new TextureFractalPolygons(w, h),
			new TextureImageTileScroll(w, h),
			new TextureImageTimeStepper(w, h),
			new TextureLinesEQ(w, h),
			new TextureMeshAudioDeform(w, h),
			new TextureNoiseLines(w, h),
			new TextureOuterCube(w, h),
			new TextureOuterSphere(w, h),
			new TexturePixelatedAudio(w, h),
			new TexturePolygonLerpedVertices(w, h),  // NEEDS FIXING. DOES NOTHING?
			new TextureRadialGridPulse(w, h),
//			new TextureRotatingRings(w, h),			 // NEEDS FIXING
			new TextureRotatorShape(w, h),
			new TextureScrollingColumns(w, h),
//			new TextureSphereAudioTextures_HaxVisualOnly(w, h),    // 
//			new TextureSphereAudioTextures_OLD(w, h),
			new TextureSphereOfBoxes(w, h),			// OLD AND BUSTED
			new TextureSphereOfCubes(w, h),
			new TextureSphereRotate(w, h),			
//			new TextureStarTrails(w, h),			// NEEDS FIXING. DOES NOTHING?
//			new TextureSvg3dExtruded(w, h),			// NEEDS FIXING
//			new TextureSvgExtruded(w, h),			// NEEDS FIXING
			new TextureSvgPattern(w, h),			// NEEDS POOL OF SVGs
			new TextureTwistingSquares(w, h),
			new TextureVectorFieldEQ(w, h),
//			new TextureVideoPlayer(w, h, DemoAssets.movieFractalCubePath),		// NEEDS FIXING
			new TextureWaveformCircle(w, h),
			new TextureWaveformSimple(w, h),
//			new TextureWebCam(w, h),
			new TextureWords2d(w, h),
		
			new TextureShaderTimeStepper(w, h, TextureShader.basic_checker),
			new TextureShaderTimeStepper(w, h, TextureShader.basic_diagonal_stripes),
			new TextureShaderTimeStepper(w, h, TextureShader.bubbles_iq),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_clouds),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_dazzle_voronoi),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_expand_loop),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_eye_jacker_01),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_eye_jacker_02),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_kaleido),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_motion_illusion),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_radial_stripes),
			new TextureShaderTimeStepper(w, h, TextureShader.BWNoiseInfiniteZoom),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_radial_wave),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_scroll_rows),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_simple_sin),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_tiled_moire),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_voronoi),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_waves),
			new TextureShaderTimeStepper(w, h, TextureShader.bw_wavy_lines),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_asterisk_wave),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_checkerboard_stairs),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_chevron_exact),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_chevron),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_concentric_hex_lines),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_concentric_hypno_lines),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_concentric_plasma),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_concentric_rectwist),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_distance_blobs),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_dots_on_planes),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_down_void),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_drunken_holodeck),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_folded_wrapping_paper),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_grid_noise_warp),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_halftone_dots),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_halftone_lines),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_liquid_moire_camo_alt),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_liquid_moire),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_metaballs),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_op_wavy_rotate),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_repeating_circles),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_rotating_stripes),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_scrolling_dashed_lines),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_scrolling_radial_twist),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_squound_tunnel),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_stripe_waves),
			new TextureShaderTimeStepper(w, h, TextureShader.cacheflowe_warp_vortex),
			new TextureShaderTimeStepper(w, h, TextureShader.circle_parts_rotate),
			new TextureShaderTimeStepper(w, h, TextureShader.cog_tunnel),
			new TextureShaderTimeStepper(w, h, TextureShader.cubert),
			new TextureShaderTimeStepper(w, h, TextureShader.cute_cloud),
			new TextureShaderTimeStepper(w, h, TextureShader.docking_tunnel),
			new TextureShaderTimeStepper(w, h, TextureShader.dot_grid_dof),
			new TextureShaderTimeStepper(w, h, TextureShader.dots_orbit),
			new TextureShaderTimeStepper(w, h, TextureShader.fade_dots),
			new TextureShaderTimeStepper(w, h, TextureShader.firey_spiral),
			new TextureShaderTimeStepper(w, h, TextureShader.flame_wisps),
			new TextureShaderTimeStepper(w, h, TextureShader.flexi_spiral),
			new TextureShaderTimeStepper(w, h, TextureShader.glowwave),
			new TextureShaderTimeStepper(w, h, TextureShader.gradient_line),
			new TextureShaderTimeStepper(w, h, TextureShader.hex_alphanumerics),
			new TextureShaderTimeStepper(w, h, TextureShader.hughsk_metaballs),
			new TextureShaderTimeStepper(w, h, TextureShader.hughsk_tunnel),
			new TextureShaderTimeStepper(w, h, TextureShader.inversion_iq),
			new TextureShaderTimeStepper(w, h, TextureShader.iq_iterations_shiny),
			new TextureShaderTimeStepper(w, h, TextureShader.iq_voronoise),
			new TextureShaderTimeStepper(w, h, TextureShader.light_leak),
			new TextureShaderTimeStepper(w, h, TextureShader.lines_scroll_diag),
			new TextureShaderTimeStepper(w, h, TextureShader.matrix_rain),
			new TextureShaderTimeStepper(w, h, TextureShader.morphing_bokeh_shape),
			new TextureShaderTimeStepper(w, h, TextureShader.noise_function),
			new TextureShaderTimeStepper(w, h, TextureShader.noise_simplex_2d_iq),
			new TextureShaderTimeStepper(w, h, TextureShader.primitives_2d),
			new TextureShaderTimeStepper(w, h, TextureShader.radial_burst),
			new TextureShaderTimeStepper(w, h, TextureShader.radial_waves),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_01_auto),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_01_mess),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_01),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_02_auto),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_02),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_03),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_04_better),
			new TextureShaderTimeStepper(w, h, TextureShader.sdf_04),
			new TextureShaderTimeStepper(w, h, TextureShader.shiny_circle_wave),
			new TextureShaderTimeStepper(w, h, TextureShader.sin_grey),
			new TextureShaderTimeStepper(w, h, TextureShader.sin_waves),
			new TextureShaderTimeStepper(w, h, TextureShader.sky_clouds_01),
			new TextureShaderTimeStepper(w, h, TextureShader.space_swirl),
			new TextureShaderTimeStepper(w, h, TextureShader.spinning_iq),
			new TextureShaderTimeStepper(w, h, TextureShader.square_fade),
			new TextureShaderTimeStepper(w, h, TextureShader.square_twist),
			new TextureShaderTimeStepper(w, h, TextureShader.star_field),
			new TextureShaderTimeStepper(w, h, TextureShader.stars_fractal_field),
			new TextureShaderTimeStepper(w, h, TextureShader.stars_nice),
			new TextureShaderTimeStepper(w, h, TextureShader.stars_screensaver),
			new TextureShaderTimeStepper(w, h, TextureShader.stars_scroll),
			new TextureShaderTimeStepper(w, h, TextureShader.supershape_2d),
			new TextureShaderTimeStepper(w, h, TextureShader.swirl),
			new TextureShaderTimeStepper(w, h, TextureShader.triangle_perlin),
			new TextureShaderTimeStepper(w, h, TextureShader.warped_tunnel),
			new TextureShaderTimeStepper(w, h, TextureShader.water_smoke),
			new TextureShaderTimeStepper(w, h, TextureShader.wavy_3d_tubes),
			new TextureShaderTimeStepper(w, h, TextureShader.wavy_checker_planes),
			new TextureShaderTimeStepper(w, h, TextureShader.wobble_sin),
			*/
		};
		
		// add sliders
		UI.addSlider(TEX_INDEX, 0, 0, allTextures.length - 1, 1, false);
	}

	protected void drawApp() {
		background(127);
		simulateMidiAndBeats();
		
		BaseTexture tex = allTextures[UI.valueInt(TEX_INDEX)];
		// BaseTexture tex = allTextures[textureIndex];
//		if(tex.getClass().getName() == TextureShaderTimeStepper.class.getName()) {
//			((TextureShaderTimeStepper) tex).updateDrawWithTime(p.frameCount * frameInc);
//		} else {
			tex.update();
//		}
		p.image(tex.texture(), 0, 0);
			
		// draw current texture name
		// set up context for more text
		p.fill(0, 100);
		p.rect(0, p.height - 60, p.width, 60);
		p.fill(255);
		p.textAlign(P.LEFT, P.CENTER);
		p.textFont(DemoAssets.fontRaleway(20));
		p.text(tex.toString(), 20, p.height - 30);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') {
			int newIndex = UI.valueInt(TEX_INDEX) - 1;
			if(newIndex < 0) newIndex = 0;
			UI.setValue(TEX_INDEX, newIndex);
		}
		if(p.key == '2') {
			int newIndex = UI.valueInt(TEX_INDEX) + 1;
			if(newIndex >= allTextures.length) newIndex = allTextures.length - 1;
			UI.setValue(TEX_INDEX, newIndex);
		}
	}
	
	protected void simulateMidiAndBeats() {
		if(p.frameCount % 45 == 0 || _timingTrigger.triggered()) {
			for(BaseTexture tex : allTextures) {
				tex.updateTiming();
			}
		}
		if(p.frameCount % 220 == 0 || _timingSectionTrigger.triggered()) {
			for(BaseTexture tex : allTextures) {
				tex.updateTimingSection();
			}
			for(BaseTexture tex : allTextures) {
				tex.setActive(false);
				tex.setActive(true);
			}
		}
		if(p.frameCount % 60 == 0 || _colorTrigger.triggered()) {
			for(BaseTexture tex : allTextures) {
				tex.setColor(ColorsHax.COLOR_GROUPS[1][MathUtil.randRange(0, 4)]);
			}
		}
		if(p.frameCount % 180 == 0 || _lineModeTrigger.triggered()) {
			for(BaseTexture tex : allTextures) {
				tex.newLineMode();
			}
		}
		if(p.frameCount % 250 == 0 || _modeTrigger.triggered()) {
			for(BaseTexture tex : allTextures) {
				tex.newMode();
			}
		}
		if(p.frameCount % 75 == 0 || _rotationTrigger.triggered()) {
			for(BaseTexture tex : allTextures) {
				tex.newRotation();
			}
		}
	}
	
}
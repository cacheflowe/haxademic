package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.shared.InputTrigger;

public class Demo_TextureShaders_ALL
extends PAppletHax { public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected TextureShader[] textures;
	protected int textureIndex = 0;

	protected InputTrigger triggerPrev = new InputTrigger(new char[]{'1'});
	protected InputTrigger triggerNext = new InputTrigger(new char[]{'2'});

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setupFirstFrame() {
		textures = new TextureShader[] {
			new TextureShader(TextureShader.basic_checker),
			new TextureShader(TextureShader.basic_diagonal_stripes),
			new TextureShader(TextureShader.bubbles_iq),
//			new TextureShader(TextureShader.bw_circles),
			new TextureShader(TextureShader.bw_clouds),
			new TextureShader(TextureShader.bw_dazzle_voronoi),
			new TextureShader(TextureShader.bw_expand_loop),
			new TextureShader(TextureShader.bw_eye_jacker_01),
			new TextureShader(TextureShader.bw_eye_jacker_02),
			new TextureShader(TextureShader.bw_kaleido),
			new TextureShader(TextureShader.bw_motion_illusion),
			new TextureShader(TextureShader.bw_radial_stripes),
			new TextureShader(TextureShader.BWNoiseInfiniteZoom),
			new TextureShader(TextureShader.bw_radial_wave),
			new TextureShader(TextureShader.bw_scroll_rows),
			new TextureShader(TextureShader.bw_simple_sin),
			new TextureShader(TextureShader.bw_tiled_moire),
			new TextureShader(TextureShader.bw_voronoi),
			new TextureShader(TextureShader.bw_waves),
			new TextureShader(TextureShader.bw_wavy_lines),
			new TextureShader(TextureShader.cacheflowe_asterisk_wave),
			new TextureShader(TextureShader.cacheflowe_checkerboard_stairs),
			new TextureShader(TextureShader.cacheflowe_chevron_exact),
			new TextureShader(TextureShader.cacheflowe_chevron),
			new TextureShader(TextureShader.cacheflowe_concentric_hex_lines),
			new TextureShader(TextureShader.cacheflowe_concentric_hypno_lines),
			new TextureShader(TextureShader.cacheflowe_concentric_plasma),
			new TextureShader(TextureShader.cacheflowe_concentric_rectwist),
			new TextureShader(TextureShader.cacheflowe_distance_blobs),
			new TextureShader(TextureShader.cacheflowe_dots_on_planes),
			new TextureShader(TextureShader.cacheflowe_down_void),
			new TextureShader(TextureShader.cacheflowe_drunken_holodeck),
			new TextureShader(TextureShader.cacheflowe_folded_wrapping_paper),
			new TextureShader(TextureShader.cacheflowe_grid_noise_warp),
			new TextureShader(TextureShader.cacheflowe_halftone_dots),
			new TextureShader(TextureShader.cacheflowe_halftone_lines),
			new TextureShader(TextureShader.cacheflowe_liquid_moire_camo_alt),
			new TextureShader(TextureShader.cacheflowe_liquid_moire),
			new TextureShader(TextureShader.cacheflowe_metaballs),
			new TextureShader(TextureShader.cacheflowe_op_wavy_rotate),
			new TextureShader(TextureShader.cacheflowe_repeating_circles),
			new TextureShader(TextureShader.cacheflowe_rotating_stripes),
			new TextureShader(TextureShader.cacheflowe_scrolling_dashed_lines),
			new TextureShader(TextureShader.cacheflowe_scrolling_radial_twist),
			new TextureShader(TextureShader.cacheflowe_squound_tunnel),
			new TextureShader(TextureShader.cacheflowe_stripe_waves),
//			new TextureShader(TextureShader.cacheflowe_triangle_wobble_stairs),
			new TextureShader(TextureShader.cacheflowe_warp_vortex),
			new TextureShader(TextureShader.circle_parts_rotate),
//			new TextureShader(TextureShader.clouds_iq),
			new TextureShader(TextureShader.cog_tunnel),
//			new TextureShader(TextureShader.cubefield),
			new TextureShader(TextureShader.cubert),
			new TextureShader(TextureShader.cute_cloud),
			new TextureShader(TextureShader.docking_tunnel),	// bad perf
			new TextureShader(TextureShader.dot_grid_dof),
			new TextureShader(TextureShader.dots_orbit),
			new TextureShader(TextureShader.fade_dots),
			new TextureShader(TextureShader.firey_spiral),
			new TextureShader(TextureShader.flame_wisps),
			new TextureShader(TextureShader.flexi_spiral),
			new TextureShader(TextureShader.glowwave),
			new TextureShader(TextureShader.gradient_line),
			new TextureShader(TextureShader.hex_alphanumerics),
			new TextureShader(TextureShader.hughsk_metaballs),	// bad perf
			new TextureShader(TextureShader.hughsk_tunnel),	// bad perf
			new TextureShader(TextureShader.inversion_iq),
			new TextureShader(TextureShader.iq_iterations_shiny),
			new TextureShader(TextureShader.iq_voronoise),
			new TextureShader(TextureShader.light_leak),
			new TextureShader(TextureShader.lines_scroll_diag),
//			new TextureShader(TextureShader.mandelbulb_morph),
			new TextureShader(TextureShader.matrix_rain),
			new TextureShader(TextureShader.morphing_bokeh_shape),	// bad perf
			new TextureShader(TextureShader.noise_function),
			new TextureShader(TextureShader.noise_simplex_2d_iq),
			new TextureShader(TextureShader.primitives_2d),
			new TextureShader(TextureShader.radial_burst),
			new TextureShader(TextureShader.radial_waves),
			new TextureShader(TextureShader.sdf_01_auto),
			new TextureShader(TextureShader.sdf_01_mess),
			new TextureShader(TextureShader.sdf_01),
			new TextureShader(TextureShader.sdf_02_auto),
			new TextureShader(TextureShader.sdf_02),
			new TextureShader(TextureShader.sdf_03),
			new TextureShader(TextureShader.sdf_04_better),
			new TextureShader(TextureShader.sdf_04),
			new TextureShader(TextureShader.shiny_circle_wave),
			new TextureShader(TextureShader.sin_grey),
			new TextureShader(TextureShader.sin_waves),
			new TextureShader(TextureShader.sky_clouds_01),
			new TextureShader(TextureShader.space_swirl),
			new TextureShader(TextureShader.spinning_iq),
			new TextureShader(TextureShader.square_fade),
			new TextureShader(TextureShader.square_twist),
			new TextureShader(TextureShader.star_field),
			new TextureShader(TextureShader.stars_fractal_field),
			new TextureShader(TextureShader.stars_nice),
			new TextureShader(TextureShader.stars_screensaver),
			new TextureShader(TextureShader.stars_scroll),
			new TextureShader(TextureShader.supershape_2d),
			new TextureShader(TextureShader.swirl),
			new TextureShader(TextureShader.triangle_perlin),
			new TextureShader(TextureShader.warped_tunnel),
			new TextureShader(TextureShader.water_smoke),
			new TextureShader(TextureShader.wavy_3d_tubes),
			new TextureShader(TextureShader.wavy_checker_planes),
			new TextureShader(TextureShader.wobble_sin),
		};
	}



	public void drawApp() {
		// cycle
		if(triggerPrev.triggered()) textureIndex = (textureIndex > 0) ? textureIndex - 1 : textures.length - 1;
		if(triggerNext.triggered()) textureIndex = (textureIndex < textures.length - 1) ? textureIndex + 1 : 0;

		// run cur shader
		TextureShader curShader = textures[textureIndex];
		curShader.setTimeMult(p.mousePercentX() * 0.07f);
		curShader.updateTime();
		p.filter(curShader.shader());
		
		// specific controls - refactor this into shader subclasses
		if(curShader.shaderPath().equals(TextureShader.cacheflowe_concentric_rectwist)) {
			curShader.setAmp(P.map(p.mouseX, 0, p.width, 0, 2f));
			curShader.setFreq(P.map(p.mouseY, 0, p.height, 0, 2f));
		}

		// show shader name
		p.noStroke();
		p.fill(0, 127);
		p.rect(0, p.height - 30, p.width, 30);
		p.fill(255);
		p.textAlign(P.RIGHT, P.CENTER);
		p.text(curShader.shaderPath(), 0, p.height - 30, p.width - 20, 30);
	}

}

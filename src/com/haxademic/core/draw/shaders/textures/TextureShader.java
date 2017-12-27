package com.haxademic.core.draw.shaders.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.opengl.PShader;

public class TextureShader {

	protected String shaderPath;
	protected PShader fragShader;
	protected float timeMult;

	public TextureShader(String shaderPath) {
		this(shaderPath, 0.01f);
	}

	public TextureShader(String shaderPath, float timeMult) {
		this.shaderPath = shaderPath;
		fragShader = TextureShader.loadShader(shaderPath);
		setTimeMult(timeMult);;
	}

	public String shaderPath() {
		return shaderPath;
	}

	public PShader shader() {
		return fragShader;
	}

	public static PShader loadShader(String shaderPath) {
		shaderPath = shaderPath.replaceAll("_", "-");
		return P.p.loadShader(FileUtil.getFile("shaders/textures/" + shaderPath));
	}

	public void setTimeMult(float mult) {
		timeMult = mult;
	}
	
	public void updateTime() {
		fragShader.set("time", P.p.frameCount * timeMult);
	}

	public void setTime(float time) {
		fragShader.set("time", time);
	}
	
	public void setAmp(float amp) {
		fragShader.set("amp", amp);
	}

	public void setFreq(float freq) {
		fragShader.set("freq", freq);
	}
	
	// SHADER PATHS

	public static String basic_checker = "basic_checker.glsl";
	public static String basic_diagonal_stripes = "basic_diagonal_stripes.glsl";
	public static String bubbles_iq = "bubbles_iq.glsl";
	public static String bw_circles = "bw_circles.glsl";
	public static String bw_clouds = "bw_clouds.glsl";
	public static String bw_dazzle_voronoi = "bw_dazzle_voronoi.glsl";
	public static String bw_expand_loop = "bw_expand_loop.glsl";
	public static String bw_eye_jacker_01 = "bw_eye_jacker_01.glsl";
	public static String bw_eye_jacker_02 = "bw_eye_jacker_02.glsl";
	public static String bw_kaleido = "bw_kaleido.glsl";
	public static String bw_motion_illusion = "bw_motion_illusion.glsl";
	public static String BWNoiseInfiniteZoom = "bw_noise_infinite_zoom.glsl";
	public static String bw_radial_wave = "bw_radial_wave.glsl";
	public static String bw_scroll_rows = "bw_scroll_rows.glsl";
	public static String bw_simple_sin = "bw_simple_sin.glsl";
	public static String bw_tiled_moire = "bw_tiled_moire.glsl";
	public static String bw_voronoi = "bw_voronoi.glsl";
	public static String bw_waves = "bw_waves.glsl";
	public static String cacheflowe_asterisk_wave = "cacheflowe_asterisk_wave.glsl";
	public static String cacheflowe_checkerboard_stairs = "cacheflowe_checkerboard_stairs.glsl";
	public static String cacheflowe_chevron_exact = "cacheflowe_chevron_exact.glsl";
	public static String cacheflowe_chevron = "cacheflowe_chevron.glsl";
	public static String cacheflowe_concentric_hex_lines = "cacheflowe_concentric_hex_lines.glsl";
	public static String cacheflowe_concentric_hypno_lines = "cacheflowe_concentric_hypno_lines.glsl";
	public static String cacheflowe_concentric_plasma = "cacheflowe_concentric_plasma.glsl";
	public static String cacheflowe_concentric_rectwist = "cacheflowe_concentric_rectwist.glsl";
	public static String cacheflowe_distance_blobs = "cacheflowe_distance_blobs.glsl";
	public static String cacheflowe_dots_on_planes = "cacheflowe_dots_on_planes.glsl";
	public static String cacheflowe_down_void = "cacheflowe_down_void.glsl";
	public static String cacheflowe_drunken_holodeck = "cacheflowe_drunken_holodeck.glsl";
	public static String cacheflowe_folded_wrapping_paper = "cacheflowe_folded_wrapping_paper.glsl";
	public static String cacheflowe_grid_noise_warp = "cacheflowe_grid_noise_warp.glsl";
	public static String cacheflowe_halftone_dots = "cacheflowe_halftone_dots.glsl";
	public static String cacheflowe_halftone_lines = "cacheflowe_halftone_lines.glsl";
	public static String cacheflowe_liquid_moire_camo_alt = "cacheflowe_liquid_moire_camo_alt.glsl";
	public static String cacheflowe_liquid_moire = "cacheflowe_liquid_moire.glsl";
	public static String cacheflowe_metaballs = "cacheflowe_metaballs.glsl";
	public static String cacheflowe_op_wavy_rotate = "cacheflowe_op_wavy_rotate.glsl";
	public static String cacheflowe_repeating_circles = "cacheflowe_repeating_circles.glsl";
	public static String cacheflowe_rotating_stripes = "cacheflowe_rotating_stripes.glsl";
	public static String cacheflowe_scrolling_dashed_lines = "cacheflowe_scrolling_dashed_lines.glsl";
	public static String cacheflowe_scrolling_radial_twist = "cacheflowe_scrolling_radial_twist.glsl";
	public static String cacheflowe_squound_tunnel = "cacheflowe_squound_tunnel.glsl";
	public static String cacheflowe_stripe_waves = "cacheflowe_stripe_waves.glsl";
	public static String cacheflowe_triangle_wobble_stairs = "cacheflowe_triangle_wobble_stairs.glsl";
	public static String cacheflowe_warp_vortex = "cacheflowe_warp_vortex.glsl";
	public static String circle_parts_rotate = "circle_parts_rotate.glsl";
	public static String clouds_iq = "clouds_iq.glsl";
	public static String cog_tunnel = "cog_tunnel.glsl";
	public static String cubefield = "cubefield.glsl";
	public static String cubert = "cubert.glsl";
	public static String cute_cloud = "cute_cloud.glsl";
	public static String docking_tunnel = "docking_tunnel.glsl";
	public static String dot_grid_dof = "dot_grid_dof.glsl";
	public static String dots_orbit = "dots_orbit.glsl";
	public static String fade_dots = "fade_dots.glsl";
	public static String firey_spiral = "firey_spiral.glsl";
	public static String flame_wisps = "flame_wisps.glsl";
	public static String flexi_spiral = "flexi_spiral.glsl";
	public static String glowwave = "glowwave.glsl";
	public static String gradient_line = "gradient_line.glsl";
	public static String hex_alphanumerics = "hex_alphanumerics.glsl";
	public static String hughsk_metaballs = "hughsk_metaballs.glsl";
	public static String hughsk_tunnel = "hughsk_tunnel.glsl";
	public static String inversion_iq = "inversion_iq.glsl";
	public static String iq_iterations_shiny = "iq_iterations_shiny.glsl";
	public static String iq_voronoise = "iq_voronoise.glsl";
	public static String light_leak = "light_leak.glsl";
	public static String lines_scroll_diag = "lines_scroll_diag.glsl";
	public static String mandelbulb_morph = "mandelbulb_morph.glsl";
	public static String matrix_rain = "matrix_rain.glsl";
	public static String morphing_bokeh_shape = "morphing_bokeh_shape.glsl";
	public static String noise_function = "noise_function.glsl";
	public static String noise_simplex_2d_iq = "noise_simplex_2d_iq.glsl";
	public static String primitives_2d = "primitives_2d.glsl";
	public static String radial_burst = "radial_burst.glsl";
	public static String radial_waves = "radial_waves.glsl";
	public static String sdf_01_auto = "sdf_01_auto.glsl";
	public static String sdf_01_mess = "sdf_01_mess.glsl";
	public static String sdf_01 = "sdf_01.glsl";
	public static String sdf_02_auto = "sdf_02_auto.glsl";
	public static String sdf_02 = "sdf_02.glsl";
	public static String sdf_03 = "sdf_03.glsl";
	public static String sdf_04_better = "sdf_04_better.glsl";
	public static String sdf_04 = "sdf_04.glsl";
	public static String shiny_circle_wave = "shiny_circle_wave.glsl";
	public static String sin_grey = "sin_grey.glsl";
	public static String sin_waves = "sin_waves.glsl";
	public static String space_swirl = "space_swirl.glsl";
	public static String spinning_iq = "spinning_iq.glsl";
	public static String square_fade = "square_fade.glsl";
	public static String square_twist = "square_twist.glsl";
	public static String star_field = "star_field.glsl";
	public static String stars_fractal_field = "stars_fractal_field.glsl";
	public static String stars_nice = "stars_nice.glsl";
	public static String stars_screensaver = "stars_screensaver.glsl";
	public static String stars_scroll = "stars_scroll.glsl";
	public static String supershape_2d = "supershape_2d.glsl";
	public static String swirl = "swirl.glsl";
	public static String triangle_perlin = "triangle_perlin.glsl";
	public static String warped_tunnel = "warped_tunnel.glsl";
	public static String water_smoke = "water_smoke.glsl";
	public static String wavy_3d_tubes = "wavy_3d_tubes.glsl";
	public static String wavy_checker_planes = "wavy_checker_planes.glsl";
	public static String wobble_sin = "wobble_sin.glsl";
}

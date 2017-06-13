package com.haxademic.core.app;

public class AppSettings {
	// Canvas setup
	public static final String RENDERER = "renderer";
	public static final String PDF_RENDERER_OUTPUT_FILE = "renderer_output_file";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String FULLSCREEN = "fullscreen";
	public static final String FULLSCREEN_SCREEN_NUMBER = "fullscreen_screen_number";
	public static final String FILLS_SCREEN = "fills_screen";
	public static final String RETINA = "is_retina";
	public static final String FORCE_FOREGROUND = "force_foreground";
	public static final String SMOOTHING = "smoothing";
	public static final int SMOOTH_NONE = 0;
	public static final int SMOOTH_LOW = 2;
	public static final int SMOOTH_DEFAULT = 3;
	public static final int SMOOTH_MEDIUM = 4;
	public static final int SMOOTH_HIGH = 8;
	// Rendering
	public static final String FPS = "fps";
	public static final String RENDERING_MOVIE = "rendering";
	public static final String RENDERING_MOVIE_START_FRAME = "rendering_startframe";
	public static final String RENDERING_MOVIE_STOP_FRAME = "rendering_stopframe";
	public static final String RENDER_AUDIO = "render_audio";
	public static final String RENDER_AUDIO_FILE = "render_audio_file";
	public static final String RENDER_MIDI = "render_midi";
	public static final String RENDER_MIDI_FILE = "render_midi_file";
	public static final String RENDER_MIDI_BPM = "render_midi_bpm";
	public static final String RENDER_MIDI_OFFSET = "render_midi_offset";
	public static final String RENDERING_GIF = "rendering_gif";
	public static final String RENDERING_GIF_FRAMERATE = "rendering_gif_framerate";
	public static final String RENDERING_GIF_QUALITY = "rendering_gif_quality";
	public static final String RENDERING_GIF_START_FRAME = "rendering_gif_startframe";
	public static final String RENDERING_GIF_STOP_FRAME = "rendering_gif_stopframe";
	// sunflow
	public static final String SUNFLOW = "sunflow";
	public static final String SUNFLOW_ACTIVE = "sunflow_active";
	public static final String SUNFLOW_QUALITY = "sunflow_quality";
	public static final String SUNFLOW_QUALITY_HIGH = "high";
	public static final String SUNFLOW_SAVE_IMAGES = "sunflow_save_images";
	// Input
	public static final String HIDE_CURSOR = "hide_cursor";
	public static final String DISABLE_ESC_KEY = "disable_esc";
	public static final String KINECT_ACTIVE = "kinect_active";
	public static final String KINECT_V2_WIN_ACTIVE = "kinect_v2_win_active";
	public static final String KINECT_V2_MAC_ACTIVE = "kinect_v2_mac_active";
	public static final String MIDI_DEVICE_IN_INDEX = "midi_device_in_index";
	public static final String MIDI_DEVICE_OUT_INDEX = "midi_device_out_index";
	public static final String MIDI_DEBUG = "midi_debug";
	public static final String OSC_ACTIVE = "osc_active";
	public static final String INIT_ESS_AUDIO = "init_ess_audio";
	public static final String INIT_MINIM_AUDIO = "init_minim_audio";
	// Output
	public static final String DMX_LIGHTS_COUNT = "dmx_lights_count";
	// Debugging
	public static final String SHOW_STATS = "show_stats";
	public static final String AUDIO_DEBUG = "audio_debug";
}

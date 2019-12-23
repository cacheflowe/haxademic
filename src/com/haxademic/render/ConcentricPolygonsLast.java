package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;

public class ConcentricPolygonsLast 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/**
  	 * @TODO: Add concentric polygons?
	 */
	
	protected WebServer server;
	protected boolean shouldRecord = false;
	boolean RENDERING = false;

	boolean easingEaseIn = true;
	float easingVal = 10f;
	float easingLevelActive = 0.025f;

	protected float startRads = 0;
	
	protected InputTrigger knob1 = new InputTrigger().addMidiNotes(new Integer[]{21}).addHttpRequests(new String[]{"slider1"}); 
	protected LinearFloat radius = new LinearFloat(50, easingVal / 2f);
	protected InputTrigger knob2 = new InputTrigger().addMidiNotes(new Integer[]{22}).addHttpRequests(new String[]{"slider2"});
	protected LinearFloat vertices = new LinearFloat(3, easingVal);
	protected InputTrigger knob3 = new InputTrigger().addMidiNotes(new Integer[]{23}).addHttpRequests(new String[]{"slider3"});
	protected LinearFloat maxLevels = new LinearFloat(1, easingVal);
	protected LinearFloat[] levelsActive = new LinearFloat[] { new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive) };
	protected LinearFloat[] circleLevelActive = new LinearFloat[] { new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive), new LinearFloat(0, easingLevelActive) };
	protected InputTrigger knob4 = new InputTrigger().addMidiNotes(new Integer[]{24}).addHttpRequests(new String[]{"slider4"});
	protected LinearFloat iterateShrink = new LinearFloat(0.1f, easingVal);
	protected InputTrigger knob5 = new InputTrigger().addMidiNotes(new Integer[]{25}).addHttpRequests(new String[]{"slider5"});
	protected LinearFloat lineWeight = new LinearFloat(1, easingVal);
	protected InputTrigger knob6 = new InputTrigger().addMidiNotes(new Integer[]{26}).addHttpRequests(new String[]{"slider6"});
	protected LinearFloat offsetRotation = new LinearFloat(0, easingVal);
	protected InputTrigger knob7 = new InputTrigger().addMidiNotes(new Integer[]{27}).addHttpRequests(new String[]{"slider7"});
	protected LinearFloat childDistanceAmp = new LinearFloat(1, easingVal);
	protected InputTrigger knob8 = new InputTrigger().addMidiNotes(new Integer[]{28}).addHttpRequests(new String[]{"slider8"});
	protected LinearFloat circleRadius = new LinearFloat(0, easingVal);
	protected InputTrigger knob9 = new InputTrigger().addMidiNotes(new Integer[]{LaunchControl.KNOB_01}).addHttpRequests(new String[]{"slider9"});
	protected LinearFloat radialConnections = new LinearFloat(0, easingVal);
	protected InputTrigger knob10 = new InputTrigger().addMidiNotes(new Integer[]{LaunchControl.KNOB_02}).addHttpRequests(new String[]{"slider10"});
	protected LinearFloat circleLevelDisplay = new LinearFloat(0, 1);

	protected InputTrigger renderTrigger = new InputTrigger().addKeyCodes(new char[]{'r'}).addMidiNotes(new Integer[]{LaunchControl.PAD_01}).addHttpRequests(new String[]{"button1"});
	protected InputTrigger saveConfigTrigger = new InputTrigger().addKeyCodes(new char[]{'s'}).addMidiNotes(new Integer[]{LaunchControl.PAD_02}).addHttpRequests(new String[]{"button2"});
	protected InputTrigger animatingTrigger = new InputTrigger().addKeyCodes(new char[]{'a'}).addMidiNotes(new Integer[]{LaunchControl.PAD_03}).addHttpRequests(new String[]{"button3"});
	protected InputTrigger prevTrigger = new InputTrigger().addKeyCodes(new char[]{'1'}).addMidiNotes(new Integer[]{LaunchControl.PAD_04}).addHttpRequests(new String[]{"button4"});
	protected InputTrigger nextTrigger = new InputTrigger().addKeyCodes(new char[]{'2'}).addMidiNotes(new Integer[]{LaunchControl.PAD_05}).addHttpRequests(new String[]{"button5"});
	
	protected ArrayList<float[]> animationStops = new ArrayList<float[]>();
	protected boolean isAnimating = false;
	protected int animateIndex = -1;
	
	// tempo-based animation
	float bpm = 125.03f;
	float bps = 60f / bpm;
	float fps = 60f;
	float framesPerBeat = bps * fps * 8f;
	float beatDisplay = 0;
	
	// draw analysis
	protected int circleResolution = 60;
	protected int numVertices = 0;
	protected float minY = -1;
	protected float maxY = 1;
	protected float shapeHeight = 0;
	protected boolean responsiveHeight = false;
	protected EasingFloat offsetY = new EasingFloat(0, easingVal);
	
	protected void config() {
		int FRAMES = (int) framesPerBeat; // 140;
		if(RENDERING == true) circleResolution = 300;
//		Config.setProperty( AppSettings.RENDERER, PRenderers.PDF );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.WIDTH, 1920 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, RENDERING );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 75 );		// num animations + 1. 4 will render a loop of 3 shapes
	}
	
	public void firstFrame() {
		MidiDevice.init(0, 0);
		server = new WebServer(new UIControlsHandler(), false);
		if(PRenderers.currentRenderer() == PRenderers.PDF) shouldRecord = true;
		
		// video 1080p selects
		// triangle
		/*
		animationStops.add(new float[] {113.457405f, 3.5973911f, 1, 0.09591983f, 4.0f, 0, 0.09591983f, 0, 0, 0.0f});
		animationStops.add(new float[] {137.03651f, 3.5973911f, 2, 0.20170206f, 4.0f, 0, 0.09591983f, 0, 0, 0.0f});
		animationStops.add(new float[] {117.03651f, 3.5973911f, 2, 0.44349f, 25.0f, 0.0f, 0.18659031f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {177.88118f, 3.5973911f, 2, 0.44349f, 12.0f, 0.0f, 0.21681382f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {197.88118f, 3.5973911f, 2, 0.7759485f, 4.0f, 0, 0.09591983f, 0, 0, 0.0f});
		animationStops.add(new float[] {145.66928f, 3.8200908f, 2.0f, 0.383043f, 6.0f, 0.0f, 0.383043f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {175.66928f, 3.8200908f, 2, 0.7155015f, 6.0f, 0.0f, 0.624831f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {145.66928f, 3.5973911f, 2, 1.4862006f, 5.0f, 0, 0.09591983f, 0, 0, 0.0f});
		animationStops.add(new float[] {195.77667f, 3.5973911f, 2, 1.0328481f, 5.0f, 0.0f, 0.09591983f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {115.66928f, 3.8200908f, 2, 2.0f, 6.0f, 0, 0.51904875f, 0, 0, 0.0f});
		animationStops.add(new float[] {145.66928f, 3.8200908f, 2, 1.1839657f, 6.0f, 0.0f, 0.6097192f, 0, 0.0f, 0.0f});
		animationStops.add(new float[] {227.98854f, 3.8200908f, 2.0f, 0.51904875f, 23.0f, 0.0f, 0.51904875f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {109.87831f, 3.8200908f, 2.0f, 1.1839657f, 6.0f, 0.0f, 1.2293009f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 3.8200908f, 2, 1.3804183f, 10.0f, 0.0058060926f, 0.5341605f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {249.46313f, 3.8200908f, 2.0f, 1.0026246f, 35.0f, 0.0f, 1.1386304f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {177.88118f, 3.8200908f, 3, 0.79106027f, 9.0f, 0, 0.83639544f, 0, 0, 0.0f});
		animationStops.add(new float[] {181.46028f, 3.8200908f, 3.0f, 0.987513f, 10.0f, 0.0f, 0.503937f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {356.83606f, 3.8200908f, 4.0f, 0.503937f, 6.0f, 0.0f, 0.503937f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {356.83606f, 3.8200908f, 4.0f, 0.7759485f, 4.0f, 0.0f, 0.23192555f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {127.7738f, 3.8200908f, 4.0f, 0.9119542f, 4.0f, 0.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {127.7738f, 3.8200908f, 4.0f, 1.0026246f, 35.0f, 0.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {185.03938f, 3.4860415f, 3.0f, 0.7759485f, 34.0f, 1.0f, 1.3804183f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {142.0902f, 3.708741f, 4.0f, 0.86661905f, 26.0f, 1.0f, 1.3804183f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {224.40945f, 3.4860415f, 4.0f, 0.39815477f, 34.0f, 1.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {195.77667f, 3.708741f, 4.0f, 0.54927224f, 32.0f, 1.0f, 1.0328481f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {195.77667f, 3.708741f, 4.0f, 0.35281953f, 53.0f, 1.0f, 1.0328481f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {84.82463f, 3.5973911f, 4.0f, 0.7608368f, 14.0f, 1.0f, 2.0f, 0, 0.0f, 0.0f});
		animationStops.add(new float[] {84.82463f, 3.5973911f, 4.0f, 0.63994277f, 4.0f, 1.0f, 1.6826533f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {356.83606f, 3.8200908f, 4.0f, 0.7759485f, 4.0f, 1.0f, 0.23192555f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {109.87831f, 3.5973911f, 3.0f, 1.0026246f, 8.0f, 1.0f, 1.0479599f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 3.5973911f, 3.0f, 1.5315359f, 8.0f, 1.0f, 1.0479599f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {70.50823f, 3.3746917f, 3.0f, 1.4408654f, 8.0f, 1.0f, 2.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {106.29921f, 3.319017f, 4.0f, 0.73061323f, 4.0f, 1.0f, 1.7733238f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {127.7738f, 3.708741f, 4.0f, 1.0026246f, 8.0f, 1.0f, 1.0328481f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {195.77667f, 3.708741f, 4.0f, 0.54927224f, 32.0f, 0.0f, 1.0328481f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {267.35864f, 3.708741f, 4.0f, 0.54927224f, 53.0f, 0.0f, 0.32259604f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 3.708741f, 4.0f, 1.0026246f, 8.0f, 0.0f, 1.0328481f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {292.4123f, 3.708741f, 4.0f, 1.0026246f, 5.0f, 0.0f, 0.20170206f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {91.98282f, 3.708741f, 4.0f, 1.0026246f, 8.0f, 0.0f, 1.6524298f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {88.40372f, 3.708741f, 4.0f, 1.0932951f, 8.0f, 0.0f, 1.3350831f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 3.708741f, 4.0f, 1.6070945f, 14.0f, 0.0f, 0.32259604f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {149.24838f, 3.708741f, 5.0f, 1.0026246f, 14.0f, 0.0f, 0.79106027f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {500.0f, 3.708741f, 4.0f, 0.54927224f, 44.0f, 0.0f, 0.503937f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {177.88118f, 3.8200908f, 3.0f, 0.79106027f, 4.0f, 0.0f, 0.80617195f, 0.0f, 0.3955301f, 0.0f});
		animationStops.add(new float[] {142.0902f, 3.8200908f, 3, 0.685278f, 13.0f, 0.0f, 1.4862006f, 0.0f, 0.3955301f, 0.0f});
		animationStops.add(new float[] {188.61847f, 3.7644157f, 3.0f, 0.685278f, 18.0f, 0.0f, 1.4862006f, 0.4798616f, 0.0f, 0.0f});
		animationStops.add(new float[] {180.3529f, 3.7644157f, 4.0f, 1.0026246f, 4.0f, 0.0f, 0.51904875f, 0.16251491f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 3.7644157f, 4.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.98093534f, 0.0f, 0.0f});
		animationStops.add(new float[] {74.087326f, 3.4860415f, 3.0f, 2.0f, 12.0f, 0.0f, 0.51904875f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 3.7644157f, 3.0f, 1.9848883f, 44.0f, 0.0f, 0.09591983f, 1.0310427f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 3.7644157f, 3.0f, 2.0f, 53.0f, 1.0f, 0.12614332f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 3.7644157f, 3.0f, 0.7759485f, 53.0f, 1.0f, 0.12614332f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 3.7644157f, 3.0f, 0.7759485f, 53.0f, 1.0f, 1.1990775f, 0.2460272f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 3.319017f, 4.0f, 1.1235187f, 53.0f, 1.0f, 1.1990775f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 3.319017f, 4.0f, 1.1235187f, 13.0f, 0.0f, 1.1990775f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 3.319017f, 4.0f, 1.0026246f, 4.0f, 0.0f, 1.0328481f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 3.8200908f, 3.0f, 1.2141892f, 4.0f, 0.0f, 1.0328481f, 0.0f, 0.45120496f, 0.0f});
		animationStops.add(new float[] {170.72296f, 3.8200908f, 3.0f, 1.0026246f, 6.0f, 0.0f, 1.0026246f, 0.0f, 0.45120496f, 0.0f});
		animationStops.add(new float[] {138.5111f, 3.8200908f, 4.0f, 1.0026246f, 9.0f, 0.0f, 1.0026246f, 0.0f, 0.45120496f, 0.0f});
		animationStops.add(new float[] {138.5111f, 3.8200908f, 4.0f, 1.0026246f, 11.0f, 0.0f, 1.0026246f, 0.4631591f, 0.60232246f, 3.0f});
		animationStops.add(new float[] {70.50823f, 3.7644157f, 4.0f, 1.4408654f, 4.0f, 0.0f, 1.1990775f, 0.0f, 0.60232246f, 3.0f});
		animationStops.add(new float[] {403.36435f, 3.7644157f, 4.0f, 1.0026246f, 4.0f, 0.0f, 0.09591983f, 0.0f, 0.60232246f, 3.0f});
		animationStops.add(new float[] {403.36435f, 3.7644157f, 4.0f, 1.0026246f, 12.0f, 0.0f, 0.17147857f, 0.0f, 0.60232246f, 3.0f});
		animationStops.add(new float[] {378.31067f, 3.7644157f, 4.0f, 1.0026246f, 12.0f, 1.0f, 0.12614332f, 0.0f, 0.60232246f, 0.0f});
		animationStops.add(new float[] {356.83606f, 3.7644157f, 5.0f, 1.0026246f, 12.0f, 1.0f, 0.12614332f, 0.0f, 0.60232246f, 0.0f});
		animationStops.add(new float[] {113.457405f, 3.7644157f, 5.0f, 1.0026246f, 4.0f, 1.0f, 1.0177364f, 0.0f, 0.60232246f, 0.0f});
		animationStops.add(new float[] {70.50823f, 3.9314404f, 5.0f, 1.0026246f, 4.0f, 1.0f, 2.0f, 0.0f, 0.60232246f, 0.0f});
		animationStops.add(new float[] {106.29921f, 3.9314404f, 5.0f, 0.70038974f, 21.0f, 1.0f, 1.818659f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {84.82463f, 3.9314404f, 5.0f, 1.0026246f, 21.0f, 1.0f, 1.818659f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {52.612743f, 3.9314404f, 4.0f, 1.4862006f, 4.0f, 1.0f, 1.3653066f, 0.09570507f, 0.49892628f, 0.0f});
		animationStops.add(new float[] {49.03364f, 3.9314404f, 4.0f, 1.939553f, 4.0f, 1.0f, 1.3653066f, 0.0f, 0.49892628f, 0.0f});
		animationStops.add(new float[] {49.03364f, 3.9314404f, 4.0f, 1.9546648f, 4.0f, 1.0f, 0.8212837f, 0.0f, 0.49892628f, 0.0f});
		animationStops.add(new float[] {88.40372f, 3.9314404f, 3.0f, 1.9546648f, 4.0f, 1.0f, 1.0328481f, 0.0f, 0.49892628f, 0.0f});
		animationStops.add(new float[] {95.56192f, 3.9314404f, 3.0f, 1.9546648f, 4.0f, 1.0f, 1.0328481f, 0.26272964f, 0.49892628f, 0.0f});
		animationStops.add(new float[] {95.56192f, 3.9314404f, 3.0f, 1.3350831f, 13.0f, 0.0f, 1.5315359f, 1.3650918f, 0.49892628f, 1.0f});
		animationStops.add(new float[] {74.087326f, 3.9314404f, 3.0f, 1.7582121f, 4.0f, 0.0f, 1.5315359f, 2.1f, 0.49892628f, 1.0f});
		animationStops.add(new float[] {95.56192f, 3.9314404f, 2.0f, 1.7582121f, 4.0f, 0.0f, 1.5315359f, 1.7492484f, 0.49892628f, 1.0f});
		animationStops.add(new float[] {163.56477f, 3.9314404f, 2.0f, 0.80617195f, 15.0f, 0.0f, 1.5315359f, 1.7492484f, 0.0f, 1.0f});
		animationStops.add(new float[] {210.09306f, 3.9314404f, 3.0f, 0.79106027f, 13.0f, 0.0f, 1.0479599f, 0.897423f, 0.0f, 1.0f});
		animationStops.add(new float[] {192.19757f, 3.9314404f, 5.0f, 0.35281953f, 34.0f, 0.0f, 2.0f, 0.012192794f, 0.0f, 1.0f});
		animationStops.add(new float[] {192.19757f, 3.9314404f, 5.0f, 0.42837825f, 45.0f, 0.0f, 2.0f, 0.012192794f, 0.0f, 1.0f});
		animationStops.add(new float[] {192.19757f, 3.3746917f, 5.0f, 0.42837825f, 45.0f, 0.0f, 1.4257536f, 2.1f, 0.0f, 1.0f});
		animationStops.add(new float[] {267.35864f, 3.3746917f, 5.0f, 0.383043f, 45.0f, 0.0f, 1.4257536f, 1.4486041f, 0.0f, 1.0f});
		animationStops.add(new float[] {267.35864f, 3.3746917f, 5.0f, 0.383043f, 26.0f, 0.0f, 1.5164242f, 1.5989263f, 0.0f, 1.0f});
		animationStops.add(new float[] {267.35864f, 3.3746917f, 5.0f, 0.383043f, 26.0f, 0.0f, 1.0177364f, 1.9496778f, 0.0f, 1.0f});
		*/
		
		// square
		/*
		animationStops.add(new float[] {0, 4.4881887f, 1.0f, 0.1f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {159.98569f, 4.4881887f, 1.0f, 0.1f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {159.98569f, 4.4881887f, 2.0f, 0.70038974f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {159.98569f, 4.4881887f, 2.0f, 1.0177364f, 11.0f, 1.0f, 1.0479599f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {181.46028f, 4.4881887f, 2.0f, 1.1235187f, 8.0f, 1.0f, 0.3074843f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {181.46028f, 4.4881887f, 2.0f, 1.4408654f, 12.0f, 1.0f, 0.54927224f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {181.46028f, 4.4881887f, 2.0f, 1.4559771f, 6.0f, 1.0f, 0.5341605f, 0.7805058f, 0.0f, 1.0f});
		animationStops.add(new float[] {181.46028f, 4.4881887f, 2.0f, 1.4106419f, 6.0f, 1.0f, 1.0177364f, 0.49656403f, 0.0f, 1.0f});
		animationStops.add(new float[] {159.98569f, 4.4881887f, 2.0f, 1.4106419f, 6.0f, 1.0f, 1.7431003f, 0.49656403f, 0.0f, 1.0f});
		animationStops.add(new float[] {70.50823f, 4.4881887f, 3.0f, 1.4106419f, 6.0f, 1.0f, 1.5466477f, 1.9663802f, 0.0f, 1.0f});
		animationStops.add(new float[] {70.50823f, 4.4881887f, 3.0f, 1.2293009f, 4.0f, 1.0f, 1.4862006f, 1.9663802f, 0.0f, 1.0f});
		animationStops.add(new float[] {77.66643f, 4.4881887f, 3.0f, 1.3955301f, 4.0f, 1.0f, 1.5466477f, 0.99763775f, 0.0f, 1.0f});
		animationStops.add(new float[] {52.612743f, 4.4881887f, 4.0f, 1.3955301f, 4.0f, 1.0f, 1.5164242f, 0.2460272f, 0.0f, 1.0f});
		animationStops.add(new float[] {49.03364f, 4.4881887f, 4.0f, 1.3501949f, 6.0f, 1.0f, 2.0f, 0.0f, 0.0f, 1.0f});
		animationStops.add(new float[] {49.03364f, 4.4881887f, 4.0f, 1.6675416f, 4.0f, 1.0f, 1.0328481f, 0.0f, 0.0f, 1.0f});
		animationStops.add(new float[] {49.03364f, 4.4881887f, 4.0f, 1.8639942f, 4.0f, 1.0f, 0.57949567f, 0.0f, 0.0f, 1.0f});
		animationStops.add(new float[] {49.03364f, 4.4881887f, 4.0f, 1.8488824f, 4.0f, 1.0f, 0.383043f, 0.0f, 0.6420902f, 2.0f});
		animationStops.add(new float[] {91.98282f, 4.4881887f, 3.0f, 1.939553f, 4.0f, 0.0f, 0.3377078f, 0.0f, 0.6420902f, 2.0f});
		animationStops.add(new float[] {113.457405f, 4.4881887f, 3.0f, 1.6070945f, 4.0f, 0.0f, 0.624831f, 0.0f, 0.6420902f, 2.0f});
		animationStops.add(new float[] {106.29921f, 4.4881887f, 3.0f, 1.6222062f, 4.0f, 0.0f, 0.86661905f, 0.41305175f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {66.92914f, 4.4881887f, 4.0f, 1.5617594f, 6.0f, 0.0f, 0.987513f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {163.56477f, 4.4881887f, 4.0f, 1.576871f, 4.0f, 0.0f, 1.3653066f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {163.56477f, 4.4881887f, 4.0f, 1.9848883f, 4.0f, 0.0f, 1.3653066f, 0.079002626f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {163.56477f, 4.4881887f, 4.0f, 1.6070945f, 4.0f, 0.0f, 1.0630716f, 0.079002626f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {84.82463f, 4.4881887f, 5.0f, 2.0f, 4.0f, 0.0f, 0.80617195f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {84.82463f, 4.4881887f, 5.0f, 2.0f, 4.0f, 0.0f, 0.503937f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {149.24838f, 4.4881887f, 5.0f, 1.6070945f, 4.0f, 0.0f, 0.503937f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {149.24838f, 4.4881887f, 5.0f, 1.7128769f, 4.0f, 0.0f, 0.36793125f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {149.24838f, 4.4881887f, 5.0f, 1.939553f, 4.0f, 0.0f, 0.09591983f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {185.03938f, 4.4881887f, 4.0f, 1.7128769f, 34.0f, 0.0f, 0.09591983f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {149.24838f, 4.4881887f, 3.0f, 1.939553f, 4.0f, 0.0f, 0.12614332f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {227.98854f, 4.7108884f, 3.0f, 1.4710889f, 4.0f, 0.0f, 0.12614332f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {421.25986f, 4.7108884f, 3.0f, 1.0026246f, 4.0f, 0.0f, 0.15636683f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {342.51968f, 4.7108884f, 3.0f, 1.0026246f, 4.0f, 0.0f, 0.3377078f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {263.77954f, 4.7108884f, 3.0f, 0.86661905f, 4.0f, 0.0f, 0.7608368f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {249.46313f, 4.7108884f, 3.0f, 1.0026246f, 18.0f, 0.0f, 0.7608368f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {149.24838f, 4.7108884f, 3.0f, 1.0026246f, 7.0f, 0.0f, 1.4862006f, 0.0f, 0.6420902f, 0.0f});
		animationStops.add(new float[] {131.3529f, 4.7108884f, 4.0f, 1.0026246f, 29.0f, 0.0f, 1.4862006f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {131.3529f, 4.7108884f, 5.0f, 0.7155015f, 4.0f, 1.0f, 1.5315359f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.7108884f, 5.0f, 0.51904875f, 4.0f, 1.0f, 2.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.6552134f, 4.0f, 0.48882526f, 4.0f, 1.0f, 2.0f, 0.42975423f, 0.0f, 0.0f});
		animationStops.add(new float[] {163.56477f, 4.6552134f, 4.0f, 0.48882526f, 21.0f, 1.0f, 2.0f, 0.4631591f, 0.0f, 0.0f});
		animationStops.add(new float[] {163.56477f, 4.6552134f, 4.0f, 0.4737135f, 46.0f, 1.0f, 2.0f, 0.4798616f, 0.0f, 0.0f});
		animationStops.add(new float[] {192.19757f, 4.6552134f, 4.0f, 0.29237255f, 29.0f, 1.0f, 2.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {217.25125f, 4.6552134f, 4.0f, 0.29237255f, 4.0f, 1.0f, 1.6524298f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {206.51396f, 4.6552134f, 4.0f, 0.39815477f, 4.0f, 0.0f, 1.6524298f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {134.93199f, 4.6552134f, 4.0f, 0.6550545f, 4.0f, 0.0f, 1.6826533f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {134.93199f, 4.6552134f, 3.0f, 0.6550545f, 4.0f, 0.0f, 1.6675416f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {131.3529f, 4.6552134f, 3.0f, 0.6550545f, 53.0f, 0.0f, 1.637318f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {131.3529f, 4.6552134f, 3.0f, 1.0781834f, 52.0f, 0.0f, 1.637318f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {145.66928f, 4.6552134f, 3.0f, 1.0026246f, 52.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {145.66928f, 4.6552134f, 3.0f, 1.0026246f, 33.0f, 0.0f, 1.6826533f, 0.2460272f, 0.0f, 0.0f});
		animationStops.add(new float[] {106.29921f, 4.6552134f, 4.0f, 1.0026246f, 4.0f, 0.0f, 1.6826533f, 0.2460272f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 4.6552134f, 4.0f, 1.2293009f, 7.0f, 0.0f, 0.88173074f, 0.04559771f, 0.0f, 0.0f});
		animationStops.add(new float[] {102.720116f, 4.6552134f, 4.0f, 1.0026246f, 7.0f, 1.0f, 0.88173074f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {74.087326f, 4.6552134f, 4.0f, 1.4106419f, 7.0f, 1.0f, 0.70038974f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 4.6552134f, 4.0f, 1.2897478f, 4.0f, 1.0f, 0.503937f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 4.6552134f, 4.0f, 1.3955301f, 5.0f, 1.0f, 0.503937f, 0.012192794f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {49.03364f, 4.6552134f, 4.0f, 1.8639942f, 4.0f, 1.0f, 0.44349f, 0.0f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {159.98569f, 4.6552134f, 3.0f, 1.3804183f, 7.0f, 1.0f, 0.39815477f, 0.0f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {124.1947f, 4.6552134f, 3.0f, 1.8337708f, 4.0f, 1.0f, 0.39815477f, 0.0f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {95.56192f, 4.6552134f, 3.0f, 1.8337708f, 4.0f, 1.0f, 0.745725f, 0.0f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {95.56192f, 4.6552134f, 2.0f, 2.0f, 4.0f, 1.0f, 1.9697765f, 0.34624192f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {95.56192f, 4.6552134f, 2.0f, 2.0f, 4.0f, 0.0f, 1.4559771f, 0.21262228f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {88.40372f, 4.6552134f, 3.0f, 1.939553f, 10.0f, 0.0f, 1.0328481f, 0.21262228f, 0.6341366f, 0.0f});
		animationStops.add(new float[] {88.40372f, 4.6552134f, 3.0f, 1.6675416f, 4.0f, 0.0f, 1.0630716f, 0.44645664f, 0.41939077f, 0.0f});
		animationStops.add(new float[] {99.14102f, 4.6552134f, 3.0f, 1.6675416f, 4.0f, 0.0f, 1.2444127f, 0.34624192f, 0.41939077f, 0.0f});
		animationStops.add(new float[] {49.03364f, 4.6552134f, 4.0f, 2.0f, 4.0f, 0.0f, 1.0177364f, 0.079002626f, 0.41939077f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.6552134f, 4.0f, 1.9848883f, 4.0f, 0.0f, 1.2141892f, 0.062300164f, 1.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.6552134f, 5.0f, 1.6070945f, 4.0f, 0.0f, 1.168854f, 0.0f, 1.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.6552134f, 5.0f, 1.818659f, 4.0f, 0.0f, 1.4408654f, 0.0f, 1.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.6552134f, 5.0f, 1.818659f, 8.0f, 0.0f, 1.5617594f, 0.0f, 1.0f, 0.0f});
		animationStops.add(new float[] {138.5111f, 4.6552134f, 5.0f, 1.3955301f, 4.0f, 0.0f, 1.5617594f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {84.82463f, 4.6552134f, 5.0f, 1.3955301f, 4.0f, 0.0f, 1.8639942f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {163.56477f, 4.6552134f, 5.0f, 1.0177364f, 4.0f, 0.0f, 1.8639942f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {213.67216f, 4.6552134f, 5.0f, 1.0026246f, 4.0f, 0.0f, 1.3955301f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {442.73444f, 4.6552134f, 5.0f, 0.54927224f, 4.0f, 0.0f, 1.5617594f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {245.88405f, 4.6552134f, 5.0f, 0.44349f, 4.0f, 0.0f, 1.4408654f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {245.88405f, 4.6552134f, 4.0f, 0.42837825f, 4.0f, 0.0f, 1.4408654f, 0.42975423f, 0.0f, 0.0f});
		animationStops.add(new float[] {224.40945f, 4.6552134f, 3.0f, 0.51904875f, 4.0f, 0.0f, 1.5164242f, 0.42975423f, 0.0f, 0.0f});
		animationStops.add(new float[] {224.40945f, 4.6552134f, 3.0f, 0.624831f, 4.0f, 0.0f, 1.2595243f, 0.26272964f, 0.0f, 0.0f});
		animationStops.add(new float[] {181.46028f, 4.6552134f, 3.0f, 0.7155015f, 4.0f, 1.0f, 1.1839657f, 0.26272964f, 0.0f, 0.0f});
		animationStops.add(new float[] {181.46028f, 4.6552134f, 3.0f, 0.7155015f, 8.0f, 1.0f, 0.79106027f, 0.14581245f, 0.0f, 0.0f});
		animationStops.add(new float[] {199.35576f, 4.6552134f, 2.0f, 0.94217765f, 8.0f, 1.0f, 0.80617195f, 0.16251491f, 0.0f, 0.0f});
		animationStops.add(new float[] {174.30208f, 4.6552134f, 2.0f, 1.1084069f, 8.0f, 1.0f, 0.80617195f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {149.24838f, 4.6552134f, 2.0f, 0.7155015f, 8.0f, 1.0f, 0.80617195f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {109.87831f, 4.933588f, 1.0f, 0.7155015f, 8.0f, 1.0f, 0.80617195f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {0, 4.4881887f, 1.0f, 0.1f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f});
		*/
		
		
		// hex
		/*
		animationStops.add(new float[] {0, 6f, 1.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {49.03364f, 6.937883f, 1.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {49.03364f, 6.937883f, 1.0f, 1.0026246f, 36.0f, 0.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {124.1947f, 6.937883f, 1.0f, 1.0026246f, 53.0f, 0.0f, 1.0177364f, 0.6134813f, 0.0f, 0.0f});
		animationStops.add(new float[] {124.1947f, 6.937883f, 2.0f, 1.0026246f, 53.0f, 0.0f, 1.0177364f, 0.5967788f, 0.0f, 0.0f});
		animationStops.add(new float[] {127.7738f, 6.937883f, 2.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.5967788f, 0.0f, 0.0f});
		animationStops.add(new float[] {127.7738f, 6.937883f, 2.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {106.29921f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {106.29921f, 6.937883f, 3.0f, 0.987513f, 18.0f, 0.0f, 1.0026246f, 0.8306132f, 0.0f, 0.0f});
		animationStops.add(new float[] {106.29921f, 6.937883f, 3.0f, 0.987513f, 4.0f, 0.0f, 1.2444127f, 0.8640181f, 0.0f, 0.0f});
		animationStops.add(new float[] {149.24838f, 6.937883f, 3.0f, 0.6550545f, 4.0f, 0.0f, 1.2141892f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {149.24838f, 6.937883f, 3.0f, 0.6550545f, 6.0f, 0.0f, 1.3501949f, 0.42975423f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 0.987513f, 6.0f, 0.0f, 1.3501949f, 0.312837f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 0.79106027f, 6.0f, 0.0f, 1.6524298f, 0.12911001f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 0.70038974f, 6.0f, 0.0f, 2.0f, 0.012192794f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 0.7759485f, 6.0f, 0.0f, 1.8791059f, 0.012192794f, 0.0f, 0.0f});
		animationStops.add(new float[] {99.14102f, 6.937883f, 3.0f, 0.79106027f, 4.0f, 0.0f, 1.8791059f, 0.012192794f, 0.769347f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 2.0f, 1.4559771f, 5.0f, 0.0f, 1.8791059f, 0.012192794f, 0.769347f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 2.0f, 1.2293009f, 11.0f, 0.0f, 1.5617594f, 0.012192794f, 0.769347f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 2.0f, 1.4559771f, 4.0f, 0.0f, 2.0f, 0.012192794f, 1.0f, 0.0f});
		animationStops.add(new float[] {145.66928f, 6.937883f, 2.0f, 0.92706597f, 12.0f, 0.0f, 2.0f, 0.012192794f, 1.0f, 0.0f});
		animationStops.add(new float[] {88.40372f, 6.937883f, 3.0f, 0.987513f, 4.0f, 0.0f, 2.0f, 0.012192794f, 0.0f, 0.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 0.987513f, 7.0f, 0.0f, 1.5315359f, 0.012192794f, 0.0f, 0.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 0.0f, 1.5315359f, 0.3629444f, 0.0f, 0.0f});
		animationStops.add(new float[] {106.29921f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 1.0f, 1.7733238f, 0.3629444f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 6.937883f, 3.0f, 1.3350831f, 4.0f, 1.0f, 1.3501949f, 0.26272964f, 0.0f, 0.0f});
		animationStops.add(new float[] {95.56192f, 6.937883f, 3.0f, 1.2444127f, 5.0f, 1.0f, 1.4710889f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 1.0026246f, 5.0f, 1.0f, 1.3653066f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 0.7759485f, 4.0f, 1.0f, 1.1839657f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {156.40659f, 6.937883f, 4.0f, 0.54927224f, 4.0f, 1.0f, 1.5013124f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {263.77954f, 6.937883f, 4.0f, 0.503937f, 4.0f, 1.0f, 0.88173074f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {263.77954f, 6.937883f, 3.0f, 0.503937f, 4.0f, 1.0f, 0.86661905f, 0.16251491f, 0.0f, 0.0f});
		animationStops.add(new float[] {253.04224f, 6.937883f, 3.0f, 0.503937f, 4.0f, 1.0f, 1.0026246f, 0.64688617f, 0.0f, 0.0f});
		animationStops.add(new float[] {195.77667f, 6.937883f, 3.0f, 0.57949567f, 4.0f, 1.0f, 1.168854f, 1.482009f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 1.0f, 1.1386304f, 1.4653065f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 3.0f, 1.1839657f, 4.0f, 1.0f, 1.1386304f, 0.8640181f, 0.0f, 0.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 2.0f, 1.8488824f, 11.0f, 1.0f, 2.0f, 0.99763775f, 0.0f, 0.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 2.0f, 1.9848883f, 4.0f, 1.0f, 1.0026246f, 0.8640181f, 0.0f, 0.0f});
		animationStops.add(new float[] {210.09306f, 6.937883f, 2.0f, 1.0026246f, 4.0f, 1.0f, 1.0177364f, 0.8640181f, 0.0f, 0.0f});
		animationStops.add(new float[] {152.82748f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 1.0f, 1.0177364f, 0.49656403f, 0.0f, 0.0f});
		animationStops.add(new float[] {152.82748f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 1.0f, 1.0177364f, 0.27943212f, 0.20464487f, 0.0f});
		animationStops.add(new float[] {152.82748f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.64688617f, 0.20464487f, 0.0f});
		animationStops.add(new float[] {142.0902f, 6.937883f, 3.0f, 0.6701663f, 4.0f, 0.021713195f, 1.3501949f, 0.0f, 0.22850552f, 0.0f});
		animationStops.add(new float[] {142.0902f, 6.937883f, 3.0f, 0.7608368f, 4.0f, 0.021713195f, 1.3501949f, 0.0f, 0.69776505f, 0.0f});
		animationStops.add(new float[] {134.93199f, 6.937883f, 3.0f, 0.7155015f, 4.0f, 0.021713195f, 1.7128769f, 0.0f, 0.69776505f, 0.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 0.021713195f, 1.7128769f, 0.0f, 0.69776505f, 0.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 3.0f, 0.7155015f, 4.0f, 0.021713195f, 1.7128769f, 2.1f, 0.66595083f, 1.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 0.021713195f, 1.7128769f, 1.665736f, 0.66595083f, 1.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 3.0f, 1.0026246f, 8.0f, 0.0f, 1.5013124f, 1.632331f, 0.66595083f, 1.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 1.0026246f, 8.0f, 0.0f, 1.576871f, 1.298282f, 0.66595083f, 2.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 1.0026246f, 12.0f, 0.0f, 1.576871f, 0.44645664f, 0.66595083f, 2.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 1.0026246f, 4.0f, 1.0f, 1.3501949f, 0.44645664f, 0.66595083f, 2.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 0.80617195f, 4.0f, 1.0f, 1.8942177f, 0.26272964f, 0.66595083f, 2.0f});
		animationStops.add(new float[] {102.720116f, 6.937883f, 4.0f, 0.6097192f, 4.0f, 1.0f, 2.0f, 0.04559771f, 0.0f, 2.0f});
		animationStops.add(new float[] {152.82748f, 6.937883f, 4.0f, 0.51904875f, 4.0f, 1.0f, 1.8639942f, 0.04559771f, 0.0f, 2.0f});
		animationStops.add(new float[] {152.82748f, 6.937883f, 4.0f, 0.36793125f, 4.0f, 1.0f, 2.0f, 0.4631591f, 0.0f, 2.0f});
		animationStops.add(new float[] {152.82748f, 6.937883f, 4.0f, 0.35281953f, 8.0f, 1.0f, 2.0f, 1.6156286f, 0.0f, 2.0f});
		animationStops.add(new float[] {199.35576f, 6.937883f, 3.0f, 0.4132665f, 14.0f, 1.0f, 1.4408654f, 0.5967788f, 0.0f, 2.0f});
		animationStops.add(new float[] {149.24838f, 6.937883f, 3.0f, 0.6550545f, 4.0f, 1.0f, 1.6070945f, 0.0f, 0.0f, 2.0f});
		animationStops.add(new float[] {149.24838f, 6.937883f, 3.0f, 0.59460753f, 4.0f, 1.0f, 1.818659f, 0.0f, 0.49892628f, 0.0f});
		animationStops.add(new float[] {74.087326f, 6.937883f, 4.0f, 1.0026246f, 4.0f, 1.0f, 1.9244413f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {70.50823f, 6.937883f, 4.0f, 1.2897478f, 4.0f, 1.0f, 1.0479599f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {113.457405f, 6.937883f, 4.0f, 1.5013124f, 11.0f, 1.0f, 0.09591983f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.937883f, 4.0f, 1.5013124f, 11.0f, 0.013759644f, 0.09591983f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {74.087326f, 6.937883f, 4.0f, 1.5013124f, 39.0f, 0.0f, 0.9119542f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {109.87831f, 6.937883f, 3.0f, 1.3653066f, 4.0f, 0.0f, 1.0177364f, 0.0f, 0.0f, 0.0f});
		animationStops.add(new float[] {117.03651f, 6.269785f, 4.0f, 1.0026246f, 4.0f, 0.0f, 1.0177364f, 0.012192794f, 0.0f, 2.0f});
		animationStops.add(new float[] {52.612743f, 6.269785f, 3.0f, 2.0f, 4.0f, 0.0f, 1.0479599f, 0.012192794f, 1.0f, 2.0f});
		animationStops.add(new float[] {188.61847f, 6.269785f, 3.0f, 1.0177364f, 4.0f, 0.0f, 0.4737135f, 0.012192794f, 0.0f, 2.0f});
		animationStops.add(new float[] {134.93199f, 6.269785f, 3.0f, 1.4710889f, 4.0f, 0.0f, 0.4737135f, 0.012192794f, 0.0f, 2.0f});
		animationStops.add(new float[] {84.82463f, 6.269785f, 3.0f, 2.0f, 36.0f, 0.0f, 0.09591983f, 0.012192794f, 0.0f, 2.0f});
		animationStops.add(new float[] {84.82463f, 6.269785f, 3.0f, 1.939553f, 4.0f, 0.0f, 0.4737135f, 0.012192794f, 0.0f, 2.0f});
		animationStops.add(new float[] {56.19184f, 6.269785f, 3.0f, 1.939553f, 4.0f, 0.0f, 1.3350831f, 0.32953948f, 0.0f, 2.0f});
		animationStops.add(new float[] {56.19184f, 6.436809f, 3.0f, 1.939553f, 4.0f, 0.0f, 2.0f, 0.49656403f, 0.7852541f, 2.0f});
		*/
		
		// david selects
//		animationStops.add(new float[] {271.9227f, 3.8200908f, 3.2502189f, 0.32259604f, 20.0f, 1.0f, 1.0781834f, 1.099141f, 0.0f, 1f});
//		animationStops.add(new float[] {299.5558f, 3.8200908f, 3.7274318f, 0.29237255f, 20.0f, 1.0f, 0.9119542f, 1.2438956f, 0.0f, 1f});
//		animationStops.add(new float[] {278.70633f, 3.8200908f, 3.6956174f, 0.20170206f, 18.0f, 0, 1.1990775f, 1.1659509f, 0.0f, 1f});
//		animationStops.add(new float[] {245.33487f, 3.8200908f, 4.045574f, 0.2772608f, 20.0f, 1.0f, 1.1990775f, 1.4331902f, 0.0f, 1f});
//		animationStops.add(new float[] {224.85864f, 3.8200908f, 4.045574f, 0.383043f, 20.0f, 0, 1.1990775f, 1.3441104f, 0.0f, 1f});
//		animationStops.add(new float[] {198.92818f, 3.6530662f, 4.1728306f, 0.35281953f, 15.0f, 0, 1.3955301f, 1.5315359f, 0.0f, 1f});


		// justin selects
//		animationStops.add(new float[] {290.46152f, 3.8200908f, 3.8546886f, 0.54927224f, 16.0f, 1.0f, 0.70038974f, 0.10812853f, 0.0f, 0.0f});
//		animationStops.add(new float[] {223.62204f, 6.4251966f, 1.031496f, 0.6102362f, 4.0f, 0, 0.69842523f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {81.90229f, 3.4860415f, 3.3456614f, 1.576871f, 13.0f, 1.0f, 0.20170206f, 0.0f, 0.12510936f, 0.0f});
//		animationStops.add(new float[] {174.30208f, 3.4664757f, 1.8185794f, 1.0f, 4.0f, 1.0f, 0.8212837f, 1.0f, 0, 0.0f});
//		animationStops.add(new float[] {256.62134f, 3.0f, 3.4092898f, 0.8067287f, 6.0f, 1.0f, 0.383043f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {210.09306f, 6.4251966f, 2.963891f, 0.5776664f, 4.0f, 1.0f, 0.6701663f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {424.29422f, 3.108566f, 5.0f, 0.50608444f, 4.0f, 0.0f, 0.503937f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {253.04224f, 3.108566f, 1.6913227f, 0.5848246f, 11.0f, 0, 0.685278f, 0.9856836f, 0.5f, 0.0f});
//		animationStops.add(new float[] {253.04224f, 3.108566f, 1.7867653f, 0.5848246f, 8.0f, 1.0f, 0.59460753f, 1.0f, 0, 0.0f});
//		animationStops.add(new float[] {122.76751f, 5.4946313f, 2.3276067f, 0.7494632f, 9.0f, 1.0f, 1.3350831f, 0.09806729f, 0, 0.0f});
//		animationStops.add(new float[] {253.04224f, 4.579973f, 2.2957926f, 0.70651394f, 4.0f, 1.0f, 0.6097192f, 0.20544022f, 0, 0.0f});
//		animationStops.add(new float[] {156.40659f, 3.7448502f, 3.2502189f, 0.51324266f, 4.0f, 0, 1.3048596f, 0.2412312f, 0, 0.0f});
//		animationStops.add(new float[] {399.99997f, 3.8200908f, 2.8366342f, 0.3074843f, 23.0f, 0.0f, 0.36793125f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {292.4123f, 4.222063f, 1.1504812f, 0.5776664f, 5.0f, 1.0f, 0.6097192f, 0.48460987f, 0, 0.0f});
//		animationStops.add(new float[] {195.77667f, 4.222063f, 2.1685357f, 0.8067287f, 5.0f, 1.0f, 0.6097192f, 0.23407301f, 0, 0.0f});
//		animationStops.add(new float[] {102.02553f, 3.5062437f, 3.1865902f, 0.634932f, 4.0f, 1.0f, 2.0f, 1.0f, 0, 0.0f});
//		animationStops.add(new float[] {127.7738f, 6.329754f, 2.7730055f, 0.599141f, 4.0f, 0, 1.0479599f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {206.51396f, 6.091148f, 1.7867653f, 0.72798854f, 23.0f, 1.0f, 0.94217765f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {399.99994f, 3.8200908f, 2.8366342f, 0.18659031f, 18.0f, 1.0f, 0.35281953f, 1.0657362f, 0, 1.0f});
//		animationStops.add(new float[] {142.0902f, 3.010101f, 2.3912354f, 0.6778812f, 7.0f, 0, 1.3955301f, 0.49176806f, 0, 0.0f});
//		animationStops.add(new float[] {199.2112f, 6.568361f, 2.4230494f, 0.34144592f, 4.0f, 0, 1.3350831f, 0.0f, 0, 0.0f});
//		animationStops.add(new float[] {97.58435f, 3.9039211f, 2.8366342f, 1.0f, 4.0f, 1.0f, 1.7582121f, 0.49892625f, 0, 0.0f});
//		animationStops.add(new float[] {260.44373f, 5.4946313f, 1.59588f, 0.87115246f, 7.0f, 0, 0.4132665f, 0.09806729f, 0, 0.0f});
//		animationStops.add(new float[] {107.44368f, 3.8200908f, 3.7274318f, 1.0026246f, 20.0f, 1.0f, 1.1084069f, 0.09699356f, 0.0f, 0.0f});

		if(isAnimating) nextAnimation(1);
		DebugView.setValue("framesPerBeat", framesPerBeat);
	}
	
	protected void updateControls() {
		// set up concentric polygon config
		if(knob1.triggered()) radius.setTarget(P.map(knob1.value(), 0.01f, 1, 50, 500));
		radius.update(true);
		DebugView.setValue("radius", radius.value());
		DebugView.setValue("radius target", radius.target());

		// num vertices
		if(knob2.triggered()) vertices.setTarget(3f + P.map(knob2.value(), 0.01f, 1, 0, 7));
		// vertices.setTarget(3);
		if(vertices.target() < 3) vertices.setTarget(3);
		vertices.update(easingEaseIn);
		
		// number of children
		if(knob3.triggered()) maxLevels.setTarget(P.round(P.map(knob3.value(), 0.01f, 1, 1, 5)));
		maxLevels.update(easingEaseIn);
		DebugView.setValue("maxLevels.value()",maxLevels.target());
		
		// set shrink amount
		if(knob4.triggered()) iterateShrink.setTarget(P.map(knob4.value(), 0.01f, 1, 0.1f, 2f));
		if(iterateShrink.target() < 0.01f) iterateShrink.setTarget(0);
		iterateShrink.update(easingEaseIn);
		
		// line weight
		if(knob5.triggered()) lineWeight.setTarget(P.round(3f + P.map(knob5.value(), 0.01f, 1, 1, 50)));
		if(lineWeight.target() < 0.01f) lineWeight.setTarget(0);
		lineWeight.update(easingEaseIn);

		// set toggleChildRotation
		if(knob6.triggered()) offsetRotation.setTarget(P.map(knob6.value(), 0.01f, 1, 0, 1f));
		if(offsetRotation.target() < 0.01f) offsetRotation.setTarget(0);
		offsetRotation.update(easingEaseIn);
		
		// set childDistanceAmp
		if(knob7.triggered()) childDistanceAmp.setTarget(P.map(knob7.value(), 0.01f, 1, 0.1f, 2f));
		if(childDistanceAmp.target() < 0.01f) childDistanceAmp.setTarget(0);
		childDistanceAmp.update(easingEaseIn);
		
		// set circleRadius
		if(knob8.triggered()) circleRadius.setTarget(P.map(knob8.value(), 0.01f, 1, 0.0f, 2.1f));
		if(circleRadius.target() < 0.01f) circleRadius.setTarget(0);
		circleRadius.update(easingEaseIn);
		
		// set radialConnections
		if(knob9.triggered()) radialConnections.setTarget(P.map(knob9.value(), 0.01f, 1, 0f, 1f));
		if(radialConnections.target() < 0.01f) radialConnections.setTarget(0);
		radialConnections.update(easingEaseIn);
		
		// set circleLevelCutoff
		if(knob10.triggered()) circleLevelDisplay.setTarget(P.round(P.map(knob10.value(), 0.01f, 1, 0f, 5)));
		circleLevelDisplay.update();
		
		// animation index
		if(prevTrigger.triggered()) nextAnimation(-1);
		if(nextTrigger.triggered()) nextAnimation(1);
	}
	
	protected void setLinearFloatInc(LinearFloat linearFloat) {
		linearFloat.setInc(P.abs(linearFloat.target() - linearFloat.value()) / framesPerBeat);
	}
	
	protected void setLinearFloatParams() {
//		P.println("=====================");
//		P.println("== New easing params!");
//		P.println("=====================");
		beatDisplay = 1;
		setLinearFloatInc(radius);
		setLinearFloatInc(vertices);
		setLinearFloatInc(maxLevels);
		setLinearFloatInc(iterateShrink);
		setLinearFloatInc(lineWeight);
		setLinearFloatInc(offsetRotation);
		setLinearFloatInc(childDistanceAmp);
		setLinearFloatInc(circleRadius);
		setLinearFloatInc(radialConnections);
		setLinearFloatInc(circleLevelDisplay);
		for (int i = 0; i < levelsActive.length; i++) {
			setLinearFloatInc(levelsActive[i]);
		}
	}

	protected void immediateLinearFloatInc(LinearFloat linearFloat) {
		linearFloat.setInc(P.abs(linearFloat.target() - linearFloat.value()));
	}
	
	protected void immediateLinearFloatParams() {
//		P.println("=====================");
//		P.println("== Immediate easing params!");
//		P.println("=====================");
		beatDisplay = 1;
		immediateLinearFloatInc(radius);
		immediateLinearFloatInc(vertices);
		immediateLinearFloatInc(maxLevels);
		immediateLinearFloatInc(iterateShrink);
		immediateLinearFloatInc(lineWeight);
		immediateLinearFloatInc(offsetRotation);
		immediateLinearFloatInc(childDistanceAmp);
		immediateLinearFloatInc(circleRadius);
		immediateLinearFloatInc(radialConnections);
		immediateLinearFloatInc(circleLevelDisplay);
		for (int i = 0; i < levelsActive.length; i++) {
			immediateLinearFloatInc(levelsActive[i]);
		}
	}
	
	protected void updateLevelsActive() {
		for (int i = 0; i < levelsActive.length; i++) {
			if(i < P.round(maxLevels.target() - 1f)) {
				levelsActive[i].setTarget(1);  
			} else {
				levelsActive[i].setTarget(0); 
			}
			levelsActive[i].update();
		}
	}
	
	protected void updateDebug() {
		DebugView.setValue("numVertices", numVertices);
		DebugView.setValue("minY", minY);
		DebugView.setValue("maxY", maxY);
		DebugView.setValue("shapeHeight", shapeHeight);
		DebugView.setValue("offsetY.value()", offsetY.value());
//		DebugView.setValue("(minY + maxY) / 2f", (minY + maxY) / 2f);	
	}
	
	protected void storeParams() {
		float[] paramsArray = new float[] {
				radius.target(),
				vertices.target(),
				maxLevels.target(),
				iterateShrink.target(),
				lineWeight.target(),
				offsetRotation.target(),
				childDistanceAmp.target(),
				circleRadius.target(),
				radialConnections.target(),
				circleLevelDisplay.target(),
		};
		animationStops.add(paramsArray);
		String toStr = "animationStops.add(new float[] {";
		for (int i = 0; i < paramsArray.length; i++) toStr += (i == 0) ? paramsArray[i] + "f" : ", " + paramsArray[i] + "f";
		toStr += "});";
		P.println(toStr);
		SystemUtil.copyStringToClipboard(toStr);
	}
	
	protected void nextAnimation(int step) {
		if(animationStops.size() == 0) return;
		animateIndex += step;
		if(animateIndex >= animationStops.size()) animateIndex = 0;
		if(animateIndex < 0) animateIndex = animationStops.size() - 1;
		DebugView.setValue("animateIndex", animateIndex + " / " + (animationStops.size() - 1));
		float[] paramsArray = animationStops.get(animateIndex);
		// apply stored params
		radius.setTarget(paramsArray[0]);
		vertices.setTarget(paramsArray[1]);
		maxLevels.setTarget(paramsArray[2]);
		updateLevelsActive();
		iterateShrink.setTarget(paramsArray[3]);
		lineWeight.setTarget(paramsArray[4]);
		offsetRotation.setTarget(paramsArray[5]);
		childDistanceAmp.setTarget(paramsArray[6]);
		circleRadius.setTarget(paramsArray[7]);
		radialConnections.setTarget(paramsArray[8]);
		circleLevelDisplay.setTarget(paramsArray[9]);
		// set to animate with bpm
		setLinearFloatParams();
	}
	
	protected void toggleAnimating() {
		isAnimating = !isAnimating;
		if(!isAnimating) {
			immediateLinearFloatParams();
		} else {
			setLinearFloatParams();
		}
	}
	
	public void drawApp() {
		// context setup
		p.background(0);
//		p.background(100 * beatDisplay);
//		if(beatDisplay > 0) beatDisplay -= 0.01f;
		
		if(renderTrigger.triggered()) shouldRecord = true;
		preparePDFRender();
		p.noStroke();
		PG.setDrawCenter(p);
		
		// override params if animating
		if(isAnimating && FrameLoop.progress() == 0) nextAnimation(1);
		if(!isAnimating) immediateLinearFloatParams();
		
		// handle input
		updateControls();
		updateLevelsActive();
		updateDebug();
		if(saveConfigTrigger.triggered()) storeParams();
		if(animatingTrigger.triggered()) toggleAnimating();
		
		// offset y
		p.translate(p.width/2, p.height/2);
		offsetY.setTarget(-(minY + maxY) / 2f);
		offsetY.update();
		p.translate(0, offsetY.value());
		
		// start rotation to keep polygon bottom flat
		float segmentRads = P.TWO_PI / (float) P.floor(vertices.value());
		// segmentRads = P.TWO_PI / vertices.value();
		startRads = P.HALF_PI + segmentRads / 2f;
		
		// draw shapes
		p.pushMatrix();
		p.fill(255);
		numVertices = 0;
		minY = 0;
		maxY = 0;
		drawDisc(p, radius.value(), radius.value() - lineWeight.value(), (int) vertices.value(), 0, childDistanceAmp.value(), 0, 0, 0);
		p.popMatrix();
		
		// responsive height
		shapeHeight = maxY - minY;
		if(responsiveHeight == true) radius.setTarget(radius.value() * MathUtil.scaleToTarget(shapeHeight, p.height * 0.75f));
		
		// save file
		finishPDFRender();
	}
	
	public void drawDisc( PApplet p, float radius, float innerRadius, int numSegments, float offsetRads, float childDistAmp, int level, float x, float y ) {
		p.pushMatrix();
		
		float segmentRads = P.TWO_PI / numSegments;
//		float halfThickness = lineWeight.value() / 2f;
		
		float leveActiveAmp = (level < 20) ? levelsActive[level].value() : 0;
		
		float nextRadius = radius * iterateShrink.value() * leveActiveAmp;
		float nextInnerRadius = innerRadius * iterateShrink.value() * leveActiveAmp;
		nextInnerRadius = nextRadius - lineWeight.value() * leveActiveAmp;
		
		offsetRads = (offsetRads == 0) ? (segmentRads / 2f) * offsetRotation.value() : 0;
		if(level < 1) offsetRads = 0;
		
		for( int i = 0; i < numSegments; i++ ) {
			
			// calc vertex
			float curRads = startRads + i * segmentRads + offsetRads;
			float nextRads = startRads + (i + 1) * segmentRads + offsetRads;
			
			// draw polygon mesh
			p.beginShape(P.TRIANGLES);
			p.vertex( P.cos( curRads ) * innerRadius, 	P.sin( curRads ) * innerRadius );
			p.vertex( P.cos( curRads ) * radius, 		P.sin( curRads ) * radius );
			p.vertex( P.cos( nextRads ) * radius, 		P.sin( nextRads ) * radius );
			
			p.vertex( P.cos( curRads ) * innerRadius, 	P.sin( curRads ) * innerRadius );
			p.vertex( P.cos( nextRads ) * innerRadius, 	P.sin( nextRads ) * innerRadius );
			p.vertex( P.cos( nextRads ) * radius, 		P.sin( nextRads ) * radius );
			p.endShape();
			
			// update analysis
			numVertices += 6;
			minY = P.min(minY, y + P.sin( curRads ) * radius);
			maxY = P.max(maxY, y + P.sin( curRads ) * radius);
			
			// draw radial sticks
			if(radialConnections.value() > 0 && level < 99) {
				p.pushStyle();
				p.stroke(255);
				p.strokeWeight(lineWeight.value() * radialConnections.value());
				p.line(0, 0, P.cos( curRads ) * innerRadius, P.sin( curRads ) * innerRadius);
				p.popStyle();
			}
			
			// draw circle
			if(circleRadius.value() > 0.0f && (level == circleLevelDisplay.target() || circleLevelDisplay.target() == 0) && level < 99) {
				float circleR = radius * circleRadius.value();
				float circleInnerR = circleR - lineWeight.value() / 2f;
				if((numVertices < 300000 || RENDERING == true) && circleR > 1) drawDisc(p, circleR, circleInnerR, circleResolution, offsetRads, 999, 999, x, y);
			}
			
			// draw children 
			if(level < maxLevels.target() && radius > 1) {
				// draw child polygon at vertices
				float radiusFromParent = (radius - ((radius - innerRadius)));
				radiusFromParent *= childDistAmp;
				float xAdd = P.cos( curRads ) * radiusFromParent;
				float yAdd = P.sin( curRads ) * radiusFromParent;
				p.pushMatrix();
				p.translate(xAdd, yAdd);	// recursion makes this additive
				if(numVertices < 300000 || RENDERING == true) drawDisc(p, nextRadius, nextInnerRadius, numSegments, offsetRads, childDistAmp, level + 1, x + xAdd, y + yAdd);
				p.popMatrix();
				
				// draw stick from parent to child
//				if(radialConnections.value() > 0 && level < 99) {
//					p.pushStyle();
//					p.stroke(255);
//					p.strokeWeight(lineWeight.value() * radialConnections.value());
//					p.line(0, 0, xAdd, yAdd);
//					p.popStyle();
//				}
			}
		}
		
		p.popMatrix();
	}

	protected void preparePDFRender() {
		if(shouldRecord == true) {
			p.beginRecord(P.PDF, FileUtil.getHaxademicOutputPath() + "/pdf/frame-"+SystemUtil.getTimestamp()+".pdf");
		}
	}
	
	protected void finishPDFRender() {
		if(shouldRecord == true) {
			p.endRecord();
			shouldRecord = false;
		}
	}
	
}

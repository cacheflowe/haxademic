package com.haxademic.core.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.data.patterns.ISequencerPattern;
import com.haxademic.core.data.patterns.PatternInterval;
import com.haxademic.core.data.patterns.PatternNoise;
import com.haxademic.core.data.patterns.PatternRandom;
import com.haxademic.core.data.patterns.PatternSine;
import com.haxademic.core.data.patterns.PatternTechno;
import com.haxademic.core.file.FileUtil;

public class SequencerConfig {
	
	public static String BASE_AUDIO_PATH = null;
	public int index;
	public String audioPath;
	public ISequencerPattern[] patterns;
	public float volume;
	public boolean playsNotes;
	public boolean playsOctaveNotes;
	public boolean playsChords;
	public boolean hasAttack;
	public boolean hasRelease;
	
	public SequencerConfig(int index, String audioPath, ISequencerPattern[] patterns, float volume, boolean playsNotes, boolean playsOctaveNotes, boolean playsChords, boolean hasAttack, boolean hasRelease) {
		this.index = index;
		if(BASE_AUDIO_PATH == null) setLocalProjectPath();
		this.audioPath = SequencerConfig.BASE_AUDIO_PATH + audioPath;
		P.out("SequencerConfig.audioPath: ", this.audioPath);
		this.patterns = patterns;
		this.volume = volume;
		this.playsNotes = playsNotes;
		this.playsOctaveNotes = playsOctaveNotes;
		this.playsChords = playsChords;
		this.hasAttack = hasAttack;
		this.hasRelease = hasRelease;
	}
	
	public static void setLocalProjectPath() {
		BASE_AUDIO_PATH = FileUtil.haxademicDataPath();
	}
	
	public static void setAbsolutePath() {
		BASE_AUDIO_PATH = "D:\\workspace\\interphase\\data\\";
	}
	
	//////////////////////////////////////
	// Sequencer config objects
	//////////////////////////////////////
	
	
	public static SequencerConfig[] interphaseChannels() {
		setAbsolutePath();
		return new SequencerConfig[] {
			new SequencerConfig(0, "audio/samples/01-kick", buildKickSnarePatterns(), 1f, false, false, false, false, false),
			new SequencerConfig(1, "audio/samples/02-snare",buildKickSnarePatterns(), 0.75f, false, false, false, false, false),
			new SequencerConfig(2, "audio/samples/03-hats", buildHatPatterns(), 0.6f, false, false, false, false, false),
			new SequencerConfig(3, "audio/samples/04-perc", buildSfxPatterns(), 0.8f, false, false, false, false, false),
			new SequencerConfig(4, "audio/samples/05-fx",   buildSfxPatterns(), 0.85f, false, false, false, false, false),
			new SequencerConfig(5, "audio/samples/06-bass", buildNotesPatterns(), 1f, true, true, false, true, true),
			new SequencerConfig(6, "audio/samples/07-keys", buildNotesPatterns(), 0.85f, true, false, true, true, true),
			new SequencerConfig(7, "audio/samples/08-lead", buildNotesPatterns(), 0.85f, true, true, false, true, true),
		};
	}
	
	public static SequencerConfig[] interphaseChannelsAlt() {
		setAbsolutePath();
		return new SequencerConfig[] {
				new SequencerConfig(0, "audio/samples2/01-kick", buildKickSnarePatterns(), 1f, false, false, false, false, false),
				new SequencerConfig(1, "audio/samples2/02-snare",buildKickSnarePatterns(), 0.75f, false, false, false, false, false),
				new SequencerConfig(2, "audio/samples2/03-hats", buildHatPatterns(), 0.6f, false, false, false, false, false),
				new SequencerConfig(3, "audio/samples2/04-perc", buildSfxPatterns(), 0.8f, false, false, false, false, false),
				new SequencerConfig(4, "audio/samples2/05-fx",   buildSfxPatterns(), 0.85f, false, false, false, false, false),
				new SequencerConfig(5, "audio/samples2/06-bass", buildNotesPatterns(), 1f, true, true, false, true, true),
				new SequencerConfig(6, "audio/samples2/07-keys", buildNotesPatterns(), 0.85f, true, false, true, true, true),
				new SequencerConfig(7, "audio/samples2/08-lead", buildNotesPatterns(), 0.85f, true, true, false, true, true),
		};
	}
	
	public static SequencerConfig[] interphaseChannelsMinimal() {
		setAbsolutePath();
		return new SequencerConfig[] {
			new SequencerConfig(0, "audio/samples/01-kick", buildKickPatterns(), 1f, false, false, false, false, false),
			new SequencerConfig(1, "audio/samples/06-bass", buildNotesPatterns(), 1f, true, true, false, true, true),
			new SequencerConfig(2, "audio/samples/07-keys", buildNotesPatterns(), 0.85f, true, false, true, true, true),
			new SequencerConfig(3, "audio/samples/08-lead", buildNotesPatterns(), 0.85f, true, true, false, true, true),
		};
	}
	
	//////////////////////////////////////
	// Pattern generator collections for different instruments
	//////////////////////////////////////

	public static ISequencerPattern[] buildKickPatterns() {
		return new ISequencerPattern[] { 
			new PatternTechno(),
		};
	}
	
	public static ISequencerPattern[] buildKickSnarePatterns() {
		return new ISequencerPattern[] { 
				new PatternInterval(),
				new PatternSine(5),
				new PatternNoise(5),
				new PatternSine(4),
				new PatternNoise(3),
		};
	}
	
	public static ISequencerPattern[] buildHatPatterns() {
		return new ISequencerPattern[] { 
			new PatternInterval(),
			new PatternSine(),
			new PatternNoise(),
			new PatternInterval(4, 8),
			new PatternSine(3),
			new PatternNoise(3),
		};
	}
	
	public static ISequencerPattern[] buildSfxPatterns() {
		return new ISequencerPattern[] { 
			new PatternRandom(0.15f, 2),
		};
	}
	
	public static ISequencerPattern[] buildNotesPatterns() {
		return new ISequencerPattern[] { 
			new PatternInterval(3, 6),
			new PatternSine(4),
			new PatternNoise(4),
			new PatternRandom(0.3f, 5),
			new PatternSine(3),
			new PatternNoise(3),
			new PatternRandom(0.3f, 3),
		};
	}
	
}

package com.haxademic.core.render;

// help from: http://stackoverflow.com/questions/3850688/reading-midi-files-in-java
// and: 	  http://stackoverflow.com/questions/2038313/midi-ticks-to-actual-playback-seconds-midi-music
// and: 	  http://stackoverflow.com/questions/7063437/midi-timestamp-in-seconds

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class MIDISequenceRenderer {
	protected PApplet p;

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
    protected Vector<MidiSequenceEvent> _messages;
    protected float _bpm = 120;
    protected float _ticksPerMin = 0;
    protected float _ticksPerSecond = 0;
    protected float _tickInMilliseconds = 0;
    
    // helps align audio and midi render timing
    protected float _frameOffset = 0;
    protected float _renderFPS = 30;
    
    public MIDISequenceRenderer( PApplet p ) {
    	this.p = p;
    } 
    
    public void loadMIDIFile( String midiFile, float midiBpm, float renderFPS, float frameOffset ) throws InvalidMidiDataException, IOException {
    	// load file
    	P.println("loading midi file: "+midiFile);
        Sequence sequence = MidiSystem.getSequence(new File(midiFile));
        P.println("sequence.getMicrosecondLength() = " + sequence.getMicrosecondLength());
        P.println("sequence.getTickLength() = " + sequence.getTickLength());
        P.println("sequence.getResolution() = " + sequence.getResolution());
        P.println("sequence.getDivisionType() = " + sequence.getDivisionType());
        
        // calculate midi event timing
        _frameOffset = frameOffset;
        _renderFPS = renderFPS;
        _bpm = midiBpm;
    	_ticksPerMin = _bpm * (float) sequence.getResolution();
    	_ticksPerSecond = _ticksPerMin / 60f;
    	_tickInMilliseconds = _ticksPerSecond / 1000.f;
        P.println("_bpm = " + _bpm);
        P.println("_ticksPerMin = " + _ticksPerMin);
        P.println("_ticksPerSecond = " + _ticksPerSecond);
        P.println("_tickInMilliseconds = " + _tickInMilliseconds);

        int trackNumber = 0;
        _messages = new Vector<MidiSequenceEvent>();
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
//            P.println("Track " + trackNumber + ": size = " + track.size() + ": ticks = " + track.ticks());
            for (int i=0; i < track.size(); i++) { 
                MidiEvent event = track.get(i);
//                P.println("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    float tickTime = (float) event.getTick() / _ticksPerSecond;
//                    P.println("Channel: " + sm.getChannel());
                    P.print("|| "+sm.getCommand()+" ||");
                    if (sm.getCommand() == NOTE_ON) {
                    	_messages.add( new MidiSequenceEvent( event, sm, tickTime ) );
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        P.print("### Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity + " tick: " + event.getTick() + " tickTime: " + tickTime);
                    } else if (sm.getCommand() == NOTE_OFF) {
                    	_messages.add( new MidiSequenceEvent( event, sm, tickTime ) );
                    	int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
//                        P.print("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity + " tick: " + event.getTick() + " tickTime: " + tickTime);
                    } else {
                        P.println("Command:" + sm.getCommand());
                    }
                } else {
                    P.println("Other message: " + message.getClass());
                }
            }
            P.println();
        }
    }
    
    // return current midi pitch of it's an NOTE_ON message. shift the event off the front of the vector to get ready for the next event
    // TODO: this should return an array of events if found, otherwise return null, so we don't need a while loop where it's being called
    public int checkForCurrentFrameNoteEvents() {
    	// get current time and add offset
    	float curAppletSeconds = (float)p.frameCount / _renderFPS;
    	curAppletSeconds += (_frameOffset * 1f/_renderFPS);

    	if( _messages.size() > 0 ) {
    		MidiSequenceEvent curEvent = _messages.firstElement();
    		if( curEvent != null ) {
    			if( curEvent.getSeconds() < curAppletSeconds ) {
    				_messages.remove( 0 );
    				if( curEvent.getMidiCommand() == NOTE_ON )
    					return curEvent.getMidiNotePitch();
    			}
    		} 
    	}
    	return -1;
    }
    
    public class MidiSequenceEvent {
    	protected ShortMessage _sm;
    	protected MidiEvent _event;
    	protected float _tickTimeSeconds;
    	
    	public MidiSequenceEvent( MidiEvent event, ShortMessage sm, float tickTime ) {
    		_sm = sm;
    		_event = event;
    		_tickTimeSeconds = tickTime;
    	}
    	
    	public float getMidiCommand() {
    		return _sm.getCommand();
    	}
    	
    	public float getSeconds() {
    		return _tickTimeSeconds;
    	}
    	
    	public int getMidiNotePitch() {
    		return _sm.getData1();
    	}
    	
    }
    
}
package com.haxademic.core.hardware.midi;

//from: http://www.elec.qmul.ac.uk/digitalmusic/people/robm/MidiRealTime.java

import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

/**
 * This is an example of how to get the real time (in ms) of MIDI events in Java.
 * This source code may be used freely but please do not publish it without consent.
 * This source code is provided "as is," without warranty of any kind, express or implied.
 * In no event shall the author or contributors be held liable for any damages arising in
 * any way from the use of this software. 
 * Copyright (c) 2009 Robert Macrae
 */

public class MidiRealTime {
    
    static Sequence seq;
    static int currentTrack = 0;
    static ArrayList<Integer> nextMessageOf = new ArrayList<Integer>();

    /**
     * args[0] should be a path to the MIDI file
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            System.out.println("Reading " + args[0]);
            seq = MidiSystem.getSequence(new File(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        convertMidi2RealTime(seq);
    }
    
    public static void convertMidi2RealTime(Sequence seqIn) {
    	seq = seqIn;
        double currentTempo = 500000;
        int tickOfTempoChange = 0;
        double msb4 = 0;
//        double division = seq.getResolution();
//        int lastTick = 0;
//        int count = 0;
        for (int track = 0; track < seq.getTracks().length; track ++) nextMessageOf.add(0);
        System.out.println();
        
        MidiEvent nextEvent;
        while ((nextEvent = getNextEvent()) != null) {
            int tick = (int)nextEvent.getTick();
	    if (noteIsOff(nextEvent)) {
                double time = (msb4+(((currentTempo/seq.getResolution())/1000)*(tick-tickOfTempoChange)));
                System.out.println("track="+currentTrack+" tick="+tick+" time="+(int)(time+0.5)+"ms "
                        +" note "+((int)nextEvent.getMessage().getMessage()[1] & 0xFF)+" off");
            } else if (noteIsOn(nextEvent)) {
                double time = (msb4+(((currentTempo/seq.getResolution())/1000)*(tick-tickOfTempoChange)));
                System.out.println("track="+currentTrack+" tick="+tick+" time="+(int)(time+0.5)+"ms "
                        +" note "+((int)nextEvent.getMessage().getMessage()[1] & 0xFF)+" on");
            } else if (changeTemp(nextEvent)) {
                String a = (Integer.toHexString((int)nextEvent.getMessage().getMessage()[3] & 0xFF));
                String b = (Integer.toHexString((int)nextEvent.getMessage().getMessage()[4] & 0xFF));
                String c = (Integer.toHexString((int)nextEvent.getMessage().getMessage()[5] & 0xFF));
                if (a.length() == 1) a = ("0"+a);
                if (b.length() == 1) b = ("0"+b);
                if (c.length() == 1) c = ("0"+c);
                String whole = a+b+c;
                int newTempo = Integer.parseInt(whole,16);
                double newTime = (currentTempo/seq.getResolution())*(tick-tickOfTempoChange);
                msb4 += (newTime/1000);
                tickOfTempoChange = tick;
                currentTempo = newTempo;
            }
        }
    }
    
    public static MidiEvent getNextEvent() {
        ArrayList<MidiEvent> nextEvent = new ArrayList<MidiEvent>();
        ArrayList<Integer> trackOfNextEvent = new ArrayList<Integer>();
        for (int track = 0; track < seq.getTracks().length; track ++) {
            if (seq.getTracks()[track].size()-1 > (nextMessageOf.get(track))) {
                nextEvent.add(seq.getTracks()[track].get(nextMessageOf.get(track)));
                trackOfNextEvent.add(track);
            }
        }
        if (nextEvent.size() == 0) return null;
        int closestMessage = 0;
        int smallestTick = (int)nextEvent.get(0).getTick();
        for (int trialMessage = 1; trialMessage < nextEvent.size(); trialMessage ++) {
            if ((int)nextEvent.get(trialMessage).getTick() < smallestTick) {
                smallestTick = (int)nextEvent.get(trialMessage).getTick();
                closestMessage = trialMessage;
            }
        }
        currentTrack = trackOfNextEvent.get(closestMessage);
        nextMessageOf.set(currentTrack,(nextMessageOf.get(currentTrack)+1));
        return nextEvent.get(closestMessage);
    }

    public static boolean noteIsOff(MidiEvent event) {
        if (Integer.toString((int)event.getMessage().getStatus(), 16).toUpperCase().charAt(0) == '8' ||
         (noteIsOn(event) && event.getMessage().getLength() >= 3 && ((int)event.getMessage().getMessage()[2] & 0xFF) == 0)) return true;
        return false;
    }

    public static boolean noteIsOn(MidiEvent event) {
        if (Integer.toString(event.getMessage().getStatus(), 16).toUpperCase().charAt(0) == '9') return true;
        return false;
    }

    public static boolean changeTemp(MidiEvent event) {
        if ((int)Integer.valueOf((""+Integer.toString((int)event.getMessage().getStatus(), 16).toUpperCase().charAt(0)), 16) == 15
         && (int)Integer.valueOf((""+((String)(Integer.toString((int)event.getMessage().getStatus(), 16).toUpperCase())).charAt(1)), 16) == 15
         && Integer.toString((int)event.getMessage().getMessage()[1],16).toUpperCase().length() == 2
         && Integer.toString((int)event.getMessage().getMessage()[1],16).toUpperCase().equals("51")
         && Integer.toString((int)event.getMessage().getMessage()[2],16).toUpperCase().equals("3")) return true;
        return false;
    }
}

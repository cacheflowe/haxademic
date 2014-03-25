package com.haxademic.core.hardware.midi;

import java.io.File;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

/**
 * This is an example of how to interpret MIDI sequences in Java.
 * This source code may be used freely but please do not publish it without consent.
 * This source code is provided "as is," without warranty of any kind, express or implied.
 * In no event shall the author or contributors be held liable for any damages arising in
 * any way from the use of this software. 
 * Copyright (c) 2009 Robert Macrae
 */

public class MidiText {

    /**
     * args[0] should be a path to the MIDI file
     */
    public static void main(String[] args) {
        Sequence midiFile = null;
        try {
            System.out.println("Reading " + args[0]);
            midiFile = MidiSystem.getSequence(new File(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        convertMidi2Text(midiFile);
    }
    
    public static void convertMidi2Text(Sequence seq) {
        System.out.println("divType = " + seq.getDivisionType());
        System.out.println("microsLength = " + seq.getMicrosecondLength());
        System.out.println("resolution = " + seq.getResolution());
        System.out.println("tickLength = " + seq.getTickLength());
        
        for (int track = 0; track < seq.getTracks().length; track ++) {
            System.out.println("\n======================= Track " + (track+1) + " =======================");
            for (int event = 0; event < seq.getTracks()[track].size(); event ++) {
                MidiMessage message = seq.getTracks()[track].get(event).getMessage();
                int statusInt = (int)message.getStatus();
                String statusCode = new String(Integer.toString(statusInt, 16).toUpperCase());
                int channel = (int)Integer.valueOf((""+statusCode.charAt(1)), 16)+1;

                int dataBytes = message.getLength();
                int[] data = new int[dataBytes];
                for (int datam = 0; datam < dataBytes; datam ++ )
                    data[datam] = ((int)message.getMessage()[datam] & 0xFF);
                System.out.print(seq.getTracks()[track].get(event).getTick()+" ");

                switch ((int)Integer.valueOf((""+statusCode.charAt(0)), 16)) {
                    case 8:
                        System.out.print("Note Off ");
                        System.out.print("cha="+channel+" ");
                        System.out.print("nn="+((int)message.getMessage()[1] & 0xFF) + " ");
                        System.out.print("vel="+((int)message.getMessage()[2] & 0xFF) + " ");
                        break;
                    case 9:
                        System.out.print("Note On ");
                        System.out.print("cha="+channel+" ");
                        System.out.print("nn=" + ((int)message.getMessage()[1] & 0xFF) + " ");
                        System.out.print("vel=" + ((int)message.getMessage()[2] & 0xFF) + " ");
                        break;
                    case 10:
                        System.out.print("polyphonic key pressure ");
                        System.out.print("cha="+channel+" ");
                        System.out.print("key="+((int)message.getMessage()[1] & 0xFF) + " ");
                        System.out.print("val="+((int)message.getMessage()[2] & 0xFF) + " ");
                        break;
                    case 11:
                        if (((int)message.getMessage()[1] & 0xFF) < 120) {
                            System.out.print("control change ");
                            System.out.print("cha=" +channel+" ");
                            System.out.print("ctr=" + ((int)message.getMessage()[1] & 0xFF) + " ");
                            System.out.print("val=" + ((int)message.getMessage()[2] & 0xFF) + " ");
                        } else {
                            System.out.print("channel mode message ");
                            if (((int)message.getMessage()[1] & 0xFF) == 120)
                                System.out.print("All Sound Off");
                            else if (((int)message.getMessage()[1] & 0xFF) == 121)
                                System.out.print("Reset All Controllers");
                            else if ((((int)message.getMessage()[1] & 0xFF) == 122) && ((int)message.getMessage()[2] & 0xFF) == 0)
                                System.out.print("Local Control: Off");
                            else if ((((int)message.getMessage()[1] & 0xFF) == 122) && ((int)message.getMessage()[2] & 0xFF) == 127)
                                System.out.print("Local Control: On");
                            else if (((int)message.getMessage()[1] & 0xFF) >= 123) {
                                System.out.print("All Notes Off");
                                if (((int)message.getMessage()[1] & 0xFF) == 124)
                                    System.out.print("& Omni Mode Off");
                                else if (((int)message.getMessage()[1] & 0xFF) == 125)
                                    System.out.print("& Omni Mode On");
                                else if ((((int)message.getMessage()[1] & 0xFF) == 126) && ((int)message.getMessage()[2] & 0xFF) == 0)
                                    System.out.print("& Mono Mode On");
                                else if ((((int)message.getMessage()[1] & 0xFF) == 126) && ((int)message.getMessage()[2] & 0xFF) >= 1)
                                    System.out.print("& Mono Mode On + " + ((int)message.getMessage()[2] & 0xFF) + " Channels");
                                else if (((int)message.getMessage()[1] & 0xFF) == 127)
                                    System.out.print("& Poly Mode On");
                            } else System.out.print("Uknown");
                        }
                        break;
                    case 12:
                        System.out.print("program change ");
                        System.out.print("cha="+channel+" ");
                        System.out.print("program="+((int)message.getMessage()[1] & 0xFF));
                        break;
                    case 13:
                        System.out.print("channel pressure ");
                        System.out.print("ch=" + (int)Integer.valueOf((""+statusCode.charAt(1)), 16)+" ");
                        System.out.print("val=" + ((int)message.getMessage()[1] & 0xFF));
                        break;
                    case 14:
                        System.out.print("pitch wheel change ");
                        System.out.print("ch="+(int)Integer.valueOf((""+statusCode.charAt(1)), 16));
                        System.out.print(" "+((int)message.getMessage()[1] & 0xFF));
                        System.out.print(" "+((int)message.getMessage()[2] & 0xFF));
                        break;
                    case 15:
                        switch ((int)Integer.valueOf((""+statusCode.charAt(1)), 16)) {
                            case 0:
                                System.out.print("system exclusive (details hidden)");
                                break;
                            case 1:
                                System.out.print("Midi Time QTR");
                                break;
                            case 2:
                                System.out.print("song position pointer ");
                                System.out.print("lsb " + ((int)message.getMessage()[1] & 0xFF) + " ");
                                System.out.print("msb " + ((int)message.getMessage()[2] & 0xFF));
                                break;
                            case 3:
                                System.out.print("song select ");
                                System.out.print("no " + ((int)message.getMessage()[1] & 0xFF));
                                break;
                            case 6:
                                System.out.print("tune request");
                                break;
                            case 7:
                                System.out.print("end of exclusive");
                                break;
                            case 8:
                                System.out.print("timing clock");
                                break;
                            case 9:
                                System.out.print("undefined");
                                break;
                            case 10:
                                System.out.print("start");
                                break;
                            case 11:
                                System.out.print("continue");
                                break;
                            case 12:
                                System.out.print("stop");
                                break;
                            case 13:
                                System.out.print("undefined");
                                break;
                            case 14:
                                System.out.print("avtice sensing");
                                break;
                            case 15:
                                switch ((Integer.toString((int)message.getMessage()[1],16).toUpperCase()).charAt(0)) {
                                    case '2':
                                        switch ((Integer.toString((int)message.getMessage()[1],16).toUpperCase()).charAt(1)) {
                                            case 'F':
                                                System.out.print("TrkEnd");
                                                break;
                                            default:
                                                System.out.print("Uknown FF 2");
                                                break;
                                        }
                                        break;
                                    case '5':
                                        switch ((Integer.toString((int)message.getMessage()[1],16).toUpperCase()).charAt(1)) {
                                            case '1':
                                                String a = (Integer.toHexString((int)message.getMessage()[3] & 0xFF));
                                                String b = (Integer.toHexString((int)message.getMessage()[4] & 0xFF));
                                                String c = (Integer.toHexString((int)message.getMessage()[5] & 0xFF));
                                                if (a.length() == 1) a = ("0"+a);
                                                if (b.length() == 1) b = ("0"+b);
                                                if (c.length() == 1) c = ("0"+c);
                                                String whole = a+b+c;
                                                int newTempo = Integer.parseInt(whole,16);
                                                System.out.print("Tempo Change: " + newTempo);
                                                break;
                                            case '4':
                                                System.out.print("SMPTE ");
                                                break;
                                            case '8':
                                                System.out.print("TimeSig ");
                                                System.out.print(Integer.toString(message.getMessage()[3]));
                                                System.out.print("/"+((int)message.getMessage()[4])*2);
                                                System.out.print(" " + (Integer.toString(message.getMessage()[5]))+ " ");
                                                System.out.print(" " + (Integer.toString(message.getMessage()[6]))+ " ");
                                                break;
                                        }  
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                System.out.print("Uknown System Message");
                                break;
                        }
                        break;
                    default:
                        System.out.print("Unknown Message");
                        break;
                }
                System.out.println();
            }
       }
    }
}

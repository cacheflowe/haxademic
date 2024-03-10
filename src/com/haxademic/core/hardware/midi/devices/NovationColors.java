package com.haxademic.core.hardware.midi.devices;

import com.haxademic.core.app.P;

public class NovationColors {

	public static int NONE = 76;
	public static int RED_WEAK = 77;
	public static int RED_MEDIUM = 78;
	public static int RED_RED_STRONG = 79;
	public static int ORANGE_WEAKER = 93;
	public static int ORANGE_WEAK = 94;
	public static int ORANGE_MEDIUM = 111;
	public static int ORANGE_STRONG = 95;
	public static int GREEN_WEAK = 92;
	public static int GREEN_MEDIUM = 108;
	public static int GREEN_STRONG = 124;
	public static int GREEN_LIME_2 = 125;
	public static int GREEN_LIME_WEAK = 109;
	public static int GREEN_LIME_1 = 126;
	public static int YELLOW_MEDIUM = 110;
	public static int YELLOW_STRONG = 127;
	
	public static final int[] colors = new int[] {
		NONE,
		RED_WEAK,
		RED_MEDIUM,
		RED_RED_STRONG,
		ORANGE_WEAKER,
		ORANGE_WEAK,
		ORANGE_MEDIUM,
		ORANGE_STRONG,
		GREEN_WEAK,
		GREEN_MEDIUM,
		GREEN_STRONG,
		GREEN_LIME_2,
		GREEN_LIME_WEAK,
		GREEN_LIME_1,
		YELLOW_MEDIUM,
		YELLOW_STRONG,
	};
	
	public static int colorByPercent(float percent) {
		return colors[P.round((colors.length - 1) * P.constrain(percent, 0, 1))];
	}
	
	public static byte NONE_BYTE = 12;
	public static byte RED_LOW_BYTE = 13;
	public static byte RED_FULL_BYTE = 15;
	public static byte ORANGE_LOW_BYTE = 29;
	public static byte ORANGE_FULL_BYTE = 63;
	public static byte GREEN_LOW_BYTE = 28;
	public static byte GREEN_FULL_BYTE = 60;
	public static byte YELLOW_BYTE = 62;

	public static final byte[] colorsSysex = new byte[] {
		NONE_BYTE,
		RED_LOW_BYTE, 
		RED_FULL_BYTE,
		ORANGE_LOW_BYTE, 
		ORANGE_FULL_BYTE,
		GREEN_LOW_BYTE,
		GREEN_FULL_BYTE,
		YELLOW_BYTE,
	};

	public static byte colorSysexByPercent(float percent) {
		float index = colorsSysex.length * percent;
		int i = (int) P.constrain(index, 0, colorsSysex.length - 1);
		return colorsSysex[i];
	}

}

	/*
	
	// from: http://forum.launchpad-pro.com/viewtopic.php?pid=3078#p3078

	FULL CHART:

	YELLOW

	Strong - 127 / 123 / 119 / 115 / 63 / 59 / 55 / 51 
	Medium - 110 / 106 / 102 / 98 / 46 / 42 / 38 / 34

	LIME

	Type 1 - 126 / 122 / 118 / 114 / 62 / 58 / 54 / 50
	Type 1 Weak - 109 / 105 / 101 / 97 / 45 / 41 / 37 / 33
	Type 2 - 125 / 121 / 117 / 113 / 61 / 57 / 53 / 49

	GREEN

	Strong - 124 / 120 / 116 / 112 / 60 / 56 / 52 / 48
	Medium - 108 / 104 / 100 / 96 / 44 / 40 / 36 / 32
	Weak - 92 / 88 / 84 / 80 / 28 / 24 / 20 / 16

	ORANGE

	Strong - 95 / 91 / 87 / 83 / 31 / 27 / 23 / 19 
	Medium - 111 / 107 / 103 / 99 / 47 / 43 / 39 / 35
	Weak - 94 / 90 / 86 / 82 / 30 / 26 / 22 / 18 
	Weaker - 93 / 89 / 85 / 81 / 29 / 25 / 21 / 17

	RED

	Strong - 79 / 75 / 71 / 67 / 15 / 11 / 7 / 3
	Medium - 78 / 74 / 70 / 66 / 14 / 10 / 6 / 2
	Weak - 77 / 73 / 69 / 65 / 13 / 9 / 5 / 1

	NO COLOR

	None - 76 / 72 / 68 / 64 / 12 / 8 / 4 / 0

	 */

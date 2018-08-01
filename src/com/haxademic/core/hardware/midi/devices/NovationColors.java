package com.haxademic.core.hardware.midi.devices;

public class NovationColors {

	// from: http://forum.launchpad-pro.com/viewtopic.php?pid=3078#p3078
	
	/*

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

	
	public static final int[] colors = new int[] {
			76, // - No Color
			77, // - Weak Red
			78, // - Medium Red
			79, // - Strong Red
			93, // - Weaker Orange
			94, // - Weak Orange
			111, // - Medium Orange
			95, // - Strong Orange
			92, // - Weak Green
			108, // - Medium Green
			124, // - Strong Green
			125, // - Lime 2
			109, // - Weak Lime 1
			126, // - Lime 1
			110, // - Medium Yellow
			127, // - Strong Yellow
	};
}


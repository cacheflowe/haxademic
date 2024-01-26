package com.haxademic.core.hardware.midi.devices;

public class LaunchControlXL {
	public static String deviceName = "Launch Control XL";
	public static String deviceName2 = "2- Launch Control XL";
	public static String deviceName3 = "3- Launch Control XL";

	public static int[] KNOBS_ROW_1   = new int[] {13, 14, 15, 16, 17, 18, 19, 20};	// CC
	public static int[] KNOBS_ROW_2   = new int[] {29, 30, 31, 32, 33, 34, 35, 36};	// CC
	public static int[] KNOBS_ROW_3   = new int[] {49, 50, 51, 52, 53, 54, 55, 56};	// CC
	public static int[] SLIDERS       = new int[] {77, 78, 79, 80, 81, 82, 83, 84};	// CC
	public static int[] BUTTONS_1     = new int[] {41, 42, 43, 44, 57, 58, 59, 60};	// NOTE
	public static int[] BUTTONS_2     = new int[] {73, 74, 75, 76, 89, 90, 91, 92};	// NOTE
	public static int BUTTON_UP       = 104;	// CC
	public static int BUTTON_DOWN     = 105;	// CC
	public static int BUTTON_LEFT     = 106;	// CC
	public static int BUTTON_RIGHT    = 107;	// CC
	public static int BUTTON_SIDE_1   = 105;	// NOTE
	public static int BUTTON_SIDE_2   = 106;	// NOTE
	public static int BUTTON_SIDE_3   = 107;	// NOTE
	public static int BUTTON_SIDE_4   = 108;	// NOTE
}
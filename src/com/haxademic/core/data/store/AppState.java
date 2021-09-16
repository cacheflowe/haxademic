package com.haxademic.core.data.store;

import com.haxademic.core.app.P;

public class AppState {
	
	// current & queued state

	public static final String APP_STATE = "APP_STATE";
	public static final String QUEUED_APP_STATE = "QUEUED_APP_STATE";
	public static final String NO_QUEUE = "NO_QUEUE";
	
	// app events 
	
	public static final String ANIMATION_FRAME = "ANIMATION_FRAME";
	public static final String ANIMATION_FRAME_PRE = "ANIMATION_FRAME_PRE";
	public static final String ANIMATION_FRAME_POST = "ANIMATION_FRAME_POST";
	
	// state handlers & helpers
	
	public static void init(String initialState) {
		P.store.setString(APP_STATE, initialState);
		P.store.setString(QUEUED_APP_STATE, initialState);
		P.store.setNumber(ANIMATION_FRAME, 0);
		P.store.setNumber(ANIMATION_FRAME_PRE, 0);
		P.store.setNumber(ANIMATION_FRAME_POST, 0);
	}
	
	public static void set(String newState) {
		P.store.setString(QUEUED_APP_STATE, newState);
	}

	public static String get() {
		return P.store.getString(APP_STATE);
	}

	public static boolean is(String state) {
		return P.store.getString(APP_STATE).equals(state);
	}
	
	public static void checkQueuedState() {
		String queuedState = P.store.getString(QUEUED_APP_STATE);
		if(!queuedState.equals(NO_QUEUE)) {
			P.store.setString(APP_STATE, queuedState);
			P.store.setString(QUEUED_APP_STATE, NO_QUEUE);
		}
	}
	
}

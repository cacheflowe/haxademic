package com.haxademic.core.data.store;

import com.haxademic.core.app.P;

public class AppState {
	
	// current & queued state

	public static final String APP_STATE = "APP_STATE";
	public static final String PREVIOUS_APP_STATE = "PREVIOUS_APP_STATE";
	public static final String QUEUED_APP_STATE = "QUEUED_APP_STATE";
	public static final String NO_QUEUE = "NO_QUEUE";
	
	// app events 
	
	public static final String ANIMATION_FRAME = "ANIMATION_FRAME";
	public static final String ANIMATION_FRAME_PRE = "ANIMATION_FRAME_PRE";
	public static final String ANIMATION_FRAME_POST = "ANIMATION_FRAME_POST";
	
	// state handlers & helpers
	
	public static void init(String initialState) {
		if(initialState == null) P.error("AppState.init(String initialState) cannot be set to null");
		P.store.setString(APP_STATE, initialState);
		P.store.setString(PREVIOUS_APP_STATE, initialState);
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

	public static String getPrevious() {
		return P.store.getString(PREVIOUS_APP_STATE);
	}

	public static boolean is(String state) {
		if(state == null || get() == null) {
			P.error("AppState.is() encountered a null value");
			return false;
		}
		return get().equals(state);
	}
	
	public static boolean isAny(String ...args) {
		String curState = get();
		for (int i = 0; i < args.length; i++) {
			if(curState != null && curState.equals(args[i])) return true;
		}
		return false;
	}
	
	public static void checkQueuedState() {
		String queuedState = P.store.getString(QUEUED_APP_STATE);
		if(!queuedState.equals(NO_QUEUE)) {
			String prevState = get();
			boolean stateChanged = queuedState.equals(prevState) == false;
			P.store.setString(PREVIOUS_APP_STATE, prevState);
			if(P.storeDistributed != null && stateChanged && AppStoreDistributed.autoBroadcastAppState) { // don't re-broadcast if app state hasn't changed. test this!
				P.storeDistributed.setString(APP_STATE, queuedState);
			} else {
				P.store.setString(APP_STATE, queuedState);
			}
			P.store.setString(QUEUED_APP_STATE, NO_QUEUE);
		}
	}
	
}

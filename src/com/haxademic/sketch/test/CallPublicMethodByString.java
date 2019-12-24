package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class CallPublicMethodByString
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public Object delegate;
	
	protected void dispatch(final String method) {
        boolean success = false;
        try {
            delegate.getClass().getMethod(
                    method
            ).invoke(
                    this.delegate
            );
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (success) {
                P.out(String.format("Callback %s();", method));
            }
        }
    }
    
    protected void dispatchWithArg(final String method, Class<?> clazz, Object obj) {
        boolean success = false;
        try {
        	delegate.getClass().getMethod(
                    method,
                    clazz
            ).invoke(
            		delegate,
                    obj
            );
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (success) {
                P.out(String.format("Callback %s();", method));
            }
        }
    }

	public void drawApp() {
		delegate = this;
		dispatch("testMethod");
	}
	
	public void testMethod() {
		P.out("testMethod called!");
	}
}

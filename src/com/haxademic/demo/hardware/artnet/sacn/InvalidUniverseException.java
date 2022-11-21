package com.haxademic.demo.hardware.artnet.sacn;

/**
*
* @author lbennette
*/
public class InvalidUniverseException extends Exception {
   public InvalidUniverseException(String message) {
       super(message);
   }
   public InvalidUniverseException(String message, Throwable throwable) {
       super(message, throwable);
   }
}
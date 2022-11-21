package com.haxademic.demo.hardware.artnet.sacn;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 *
 * @author lbennette
 */
public class ACNProtocol {
    public static boolean isUniverseValid(int universeNumber){
        return !(universeNumber <= 0 || universeNumber >= 64000);
    }
    
    public static byte[] generateACNHeaderLayers(int universeNumber, String sourceName){
        byte[] universe = new byte[2];
        universe[0] = (byte)(universeNumber / 255);
        universe[1] = (byte)(universeNumber % 255);
        
        ByteBuffer outputBytes = ByteBuffer.wrap(new byte[125]);
        // ROOT LAYER
        outputBytes.put(new byte[]{0x00, 0x10},0,2); // Preamble
        outputBytes.put(new byte[]{0x00, 0x00},0,2); // Post-amble
        outputBytes.put(new byte[]{0x41, 0x53, 0x43, 0x2d, 0x45, 0x31, 0x2e, 0x31, 0x37, 0x00, 0x00, 0x00}, 0, 12); // ACN Packet Identifier
        outputBytes.put(new byte[]{0x72, 0x6e},0,2); // Flags + length
        outputBytes.put(new byte[]{0x00, 0x00, 0x00, 0x04},0,4); // Vector
        outputBytes.put(getCID(), 0, 16); // CID
                             
        // FRAMING LAYER
        outputBytes.put(new byte[]{0x72, 0x58},0,2); // Flags and Length
        outputBytes.put(new byte[]{0x00, 0x00, 0x00, 0x02},0,4); // Vector
        outputBytes.put(getNameAsHeaderBytes(sourceName), 0 , 64); // Source name
        outputBytes.put((byte)0x64); // priority
        outputBytes.put(new byte[]{0x00, 0x00},0,2); // Reserved, do not use
        outputBytes.put((byte)0x00); // Sequence number
        outputBytes.put((byte)0x00); // options
        outputBytes.put(universe,0,2); // universe
        
        //DMP LAYER
        outputBytes.put(new byte[]{0x72, 0x0b},0,2); // Flags + Length
        outputBytes.put((byte)0x02); // Vector
        outputBytes.put((byte)0xa1); // Address Tyoe & Data Type
        outputBytes.put(new byte[]{0x00, 0x00},0,2); // First Property Address
        outputBytes.put(new byte[]{0x00, 0x01},0,2); // Address Increment
        outputBytes.put(new byte[]{0x02, 0x01},0,2); // Property value count
        
        return outputBytes.array();
    }
    
    private static byte[] getCID(){
        UUID cid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(cid.getMostSignificantBits());
        bb.putLong(cid.getLeastSignificantBits());
        return bb.array();
    }
    
    private static byte[] getNameAsHeaderBytes(String sourceName){
        ByteBuffer bb = ByteBuffer.wrap(new byte[64]);
        bb.put(sourceName.getBytes());
        return bb.array();
    }
}

package com.haxademic.demo.hardware.artnet.sacn;

import java.nio.ByteBuffer;

/**
 *
 * @author lbennette
 */
public class LiveOutput {
    private int universe;
    private String sourceName;
    private byte[] ACNHeader;
    private byte[] DMXvals;
    private byte[] output;
    
    public LiveOutput(int universe, String sourceName) throws InvalidUniverseException{
        this.universe = universe;
        this.sourceName = sourceName;
        if(!ACNProtocol.isUniverseValid(universe)){
            throw new InvalidUniverseException("InvalidUniverseException: All universes must be between 1-63999.");
        }
        ACNHeader = ACNProtocol.generateACNHeaderLayers(universe, sourceName);
        DMXvals = new byte[513];
        for(byte val : DMXvals){
            val = 0x00;
        }
        updateOutput();
    }
    
    private void updateOutput(){
        ByteBuffer b = ByteBuffer.wrap(new byte[638]);
        b.put(ACNHeader,0,125);
        b.put(DMXvals,0,513);
        output = b.array();
    }
    
    public byte[] getOutput(){
        output[111]++;
        ACNHeader[111]++;
        return output;
    }

    /**
     * @param index 0-511 DMX address
     * @param val 
     */
    public void setDMXVal(int index, byte val){
        DMXvals[index+1] = val;
        updateOutput();
    }
    
    public byte getDMXVal(int index){
        return DMXvals[index+1];
    }
    
    public int getUniverse() {
        return universe;
    }
    
    public void setPriority(byte prio){
        ACNHeader[108] = prio;
        updateOutput();
    }
    
}
package com.haxademic.demo.hardware.artnet.sacn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

/**
 *
 * @author lbennette
 */
public class OutputStreamer implements ActionListener{
    private final InetAddress group;
    private final MulticastSocket s;
    private final LiveOutput liveOut;
    private final Timer timer;
    
    public OutputStreamer(LiveOutput liveOut) throws UnknownHostException, IOException, InvalidUniverseException{
        this.liveOut = liveOut;
        group = InetAddress.getByName(ipFromUniverseNumber(liveOut.getUniverse()));
        s = new MulticastSocket(5568);
        s.joinGroup(group);
        
        timer = new Timer(1000, this);
        timer.start();
    }
    
    public void sendLiveOutput() throws IOException{
        send(liveOut.getOutput());
    }
    
    private void send(byte[] output) throws IOException{
        DatagramPacket hi = new DatagramPacket(output, output.length, group, 5568);
        s.send(hi);
    }
    
    public void destroy() throws IOException{
        s.leaveGroup(group);
        timer.stop();
    }
    
    private static String ipFromUniverseNumber(int universeNumber) throws InvalidUniverseException{
        String ip = "239.255.";
        if(universeNumber <= 0 || universeNumber >= 64000){
            throw new InvalidUniverseException("InvalidUniverseException: All universes must be between 1-63999.");
        }
        else if(universeNumber <= 255){
            ip += "0." + universeNumber;
            return ip;
        }else{
            int fieldFour = universeNumber % 255;
            int fieldThree = universeNumber / 255;
            ip += fieldThree + "." + fieldFour;
            return ip;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            sendLiveOutput();
        } catch (IOException ex) {
            Logger.getLogger(OutputStreamer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
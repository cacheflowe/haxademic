package com.haxademic.core.hardware.lidar;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;

import processing.serial.Serial;

public class RPLidar {
	
	// Code & info from: https://vimeo.com/344316345
	///// CODE EDITED BY OWEN LOWERY - WWW.OWENLOWERY.COM //////////
	///// ORIGINAL CODE BY ADAM CREEN - INFORMATION BELOW /////////

	/*  Copyright (C) 2014  Adam Green (https://github.com/adamgreen)

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
	*/
	/* 
	Processing code to interface with RoboPeak's RPLIDAR and display
   	the scan results on the screen. */

	/*  Copyright (C) 2014  Adam Green (https://github.com/adamgreen)

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
	 */
	/* Processing code to interface with RoboPeak's RPLIDAR and display
   the scan results on the screen. */

	
	protected static final byte cmdSyncByte = (byte)0xA5;
	protected static final byte cmdGetHealth = 0x52;
	protected static final byte cmdGetDeviceInfo = 0x50;
	protected static final byte cmdReset = 0x40;
	protected static final byte cmdStop = 0x25;
	protected static final byte cmdStart = 0x20;

	protected static final byte respSyncByte1 = cmdSyncByte;
	protected static final byte respSyncByte2 = 0x5A; 

	protected Serial             m_port;
	protected int                m_startTime;
	protected int                m_timeout;
	protected volatile boolean   m_isScanning;
	protected volatile LidarScan m_lastScan;
	protected LidarScan          m_currScan;
	public static boolean DEBUG = false;
	
	protected int baudRate = 115200;
	
	// TODO:
	// - Make Serial reading threaded
	
	
	public RPLidar(String portName)
	{
		m_isScanning = false;
		m_port = new Serial(P.p, portName, baudRate);
		m_port.setDTR(false);
		m_lastScan = new LidarScan();
		m_timeout = 5000;
		
		// start!
		LidarDeviceInfo deviceInfo = getDeviceInfo();
		P.out("deviceInfo = ", deviceInfo.toString());
		LidarHealth health = getHealth();
		P.out("health = " + health.toString());
		startScan();
	}

	public LidarHealth getHealth()
	{
		final int lengthOfDataPacket = 3;
		m_startTime = P.p.millis();

		if(DEBUG) P.out("->getHealth()");

		sendCommand(cmdGetHealth);
		LidarResponseDescriptor responseDescriptor = getResponseDescriptor();
		verifyResponseDescriptor(responseDescriptor, LidarResponseDescriptor.typeDeviceHealth, LidarResponseDescriptor.modeSingleResponse, lengthOfDataPacket);

		byte buffer[] = new byte[lengthOfDataPacket];
		readBytes(buffer);

		int status = P.parseInt(buffer[0]);
		int errorCode = (P.parseInt(buffer[1]) & 0xFF) | ((P.parseInt(buffer[2]) & 0xFF) << 8);
		if(DEBUG) P.out("status = " + status);
		if(DEBUG) P.out("errorCode = " + errorCode);
		if(DEBUG) P.out("<-getHealth()");
		return new LidarHealth(status, errorCode);
	}

	protected void sendCommand(byte command)
	{
		byte header[] = {cmdSyncByte, command};
		if(DEBUG) P.out("sendCommand: " + P.hex(header[0]) + " " + P.hex(header[1]));
		m_port.write(header);
	}

	protected LidarResponseDescriptor getResponseDescriptor()
	{
		if(DEBUG) P.out("->getResponseDescriptor()");

		waitForResponseSyncBytes();

		byte buffer[] = new byte[5];
		readBytes(buffer);

		int length = (P.parseInt(buffer[0]) & 0xFF) | ((P.parseInt(buffer[1]) & 0xFF) << 8) | ((P.parseInt(buffer[2]) & 0xFF) << 16) | ((P.parseInt(buffer[3]) & 0x3F) << 24);
		int mode = (P.parseInt(buffer[3]) & 0xC0) >> 6;
		int type = P.parseInt(buffer[4]);  

		if(DEBUG) P.out("length = " + length);
		if(DEBUG) P.out("mode = " + mode);
		if(DEBUG) P.out("type = " + type);

		if(DEBUG) P.out("<-getResponseDescriptor()");
		return new LidarResponseDescriptor(length, mode, type);
	}

	protected void waitForResponseSyncBytes()
	{
		int lastByte = 0;

		if(DEBUG) P.out("->waitForResponseSyncBytes()");

		while (P.p.millis() - m_startTime < m_timeout)
		{
			if (m_port.available() == 0) continue;
			byte currByte = (byte)m_port.read();
			if(DEBUG) P.out("currByte: " + currByte);
			if (currByte == respSyncByte2 && lastByte == respSyncByte1)
			{
				if(DEBUG) P.out("<-waitForResponseSyncBytes()");
				return;
			}
			lastByte = currByte;
		}

		if(DEBUG) P.out("TIMEOUT");
		throw new RuntimeException("Timed out waiting for response sync bytes");
	}

	protected void readBytes(byte buffer[])
	{
		int i = 0;
		if(DEBUG) P.out("->readBytes()");

		while (i < buffer.length && P.p.millis() - m_startTime < m_timeout)
		{
			if (m_port.available() == 0)
				continue;

			byte currByte = (byte)m_port.read();
			buffer[i++] = currByte;
		}

		if (P.p.millis() - m_startTime >= m_timeout)
		{
			if(DEBUG) P.out("TIMEOUT");
			throw new RuntimeException("Timed out waiting for bytes to read.");
		}
		if(DEBUG) P.out("<-readBytes()");
	}

	protected String byteArrayAsHex(byte[] bytes) {
		String serial = "";
		for (int i = 0 ; i < bytes.length ; i++) serial += P.hex(bytes[i]); 
		return serial;
	}

	protected void verifyResponseDescriptor(LidarResponseDescriptor responseDescriptor, int expectedDataType, int expectedSendMode, int expectedLength)
	{
		if (responseDescriptor.dataType != expectedDataType)
		{
			if(DEBUG) P.out("BAD TYPE");
			throw new RuntimeException("Unexpected response descriptor type: " + P.hex(responseDescriptor.dataType));
		}
		if (responseDescriptor.sendMode != expectedSendMode)
		{
			if(DEBUG) P.out("BAD SEND MODE");
			throw new RuntimeException("Unexpected response device health descriptor send mode: " + P.hex(responseDescriptor.sendMode));
		}
		if (responseDescriptor.length != expectedLength)
		{
			if(DEBUG) P.out("BAD LENGTH");
			throw new RuntimeException("Unexpected response length: " + responseDescriptor.length);
		}
	}

	public LidarDeviceInfo getDeviceInfo()
	{
		final int lengthOfDataPacket = 20;
		m_startTime = P.p.millis();

		if(DEBUG) P.out("->getDeviceInfo()");

		sendCommand(cmdGetDeviceInfo);
		LidarResponseDescriptor responseDescriptor = getResponseDescriptor();
		verifyResponseDescriptor(responseDescriptor, LidarResponseDescriptor.typeDeviceInfo, LidarResponseDescriptor.modeSingleResponse, lengthOfDataPacket);

		byte[] buffer = new byte[lengthOfDataPacket];
		readBytes(buffer);

		int model = P.parseInt(buffer[0]);
		int firmwareMinor = P.parseInt(buffer[1]);
		int firmwareMajor = P.parseInt(buffer[2]);
		int hardware = P.parseInt(buffer[3]);
		byte[] serialNumber = new byte[16];
		System.arraycopy(buffer, 4, serialNumber, 0, 16);

		if(DEBUG) P.out("<-getDeviceInfo()");
		return new LidarDeviceInfo(model, firmwareMinor, firmwareMajor, hardware, byteArrayAsHex(serialNumber));
	}

	public void reset()
	{
		if(DEBUG) P.out("->reset()");

		sendCommand(cmdReset);
		wait_ms(2);

		if(DEBUG) P.out("<-reset()");
	}

	protected void wait_ms(int millisecondsToWait)
	{
		int startTime = P.p.millis();
		while (P.p.millis() - startTime < millisecondsToWait)
		{
		}
	}

	public void stopScan()
	{
		if(DEBUG) P.out("->stopScan()");

		// Stop handling measurement packets in the background.
		m_isScanning = false;

		sendCommand(cmdStop);
		wait_ms(1);

		// Get rid of any data that is left over in serial buffer.
		m_port.clear();

		if(DEBUG) P.out("<-stopScan()");
	}

	public void startScan()
	{
		if(DEBUG) P.out("->startScan()");
		
		final int lengthOfDataPacket = 5;
		m_startTime = P.p.millis();

		stopScan();

		sendCommand(cmdStart);
		LidarResponseDescriptor responseDescriptor = getResponseDescriptor();
		verifyResponseDescriptor(responseDescriptor, LidarResponseDescriptor.typeMeasurement, LidarResponseDescriptor.modeMultipleResponse, lengthOfDataPacket);

		// Setup to process measurement packets in the background using serialEvent().
		m_currScan = new LidarScan();
		m_isScanning = true;
		m_port.buffer(lengthOfDataPacket);

		if(DEBUG) P.out("<-startScan()");
	}

//	public void serialEvent(Serial port)
	public void update()
	{
		// Handle data synchronously until actively scanning.
		if (!m_isScanning) return;

		final int lengthOfDataPacket = 5;
		byte[] packet = new byte[lengthOfDataPacket];

		while (m_port.available() >= lengthOfDataPacket) {
			m_port.readBytes(packet);
			parseMeasurement(packet);
		}
	}

	protected void parseMeasurement(byte[] packet)
	{
		// UNDONE: Could have some code which attempts to get back into sync when an invalid packet is encountered.
		if (!validPacket(packet)) return;
		if (isNewScanStart(packet))
		{
			m_currScan.scanRateInHz = 1000.0f / (float)(System.currentTimeMillis() - m_currScan.startTime);
			m_lastScan = m_currScan;
			m_currScan = new LidarScan();
		}

		int quality = (P.parseInt(packet[0]) & 0xFC) >> 2;
		int angleFixed = ((P.parseInt(packet[1]) & 0xFE) >> 1) | ((P.parseInt(packet[2]) & 0xFF) << 7);
		int distanceFixed = (P.parseInt(packet[3]) & 0xFF) | ((P.parseInt(packet[4]) & 0xFF) << 8);
		float angle = (float)angleFixed / 64.0f;
		float distance = (float)distanceFixed / 4.0f;
		m_currScan.measurements[m_currScan.measurementCount++] = new LidarMeasurement(angle, distance, quality);
	}

	protected boolean validPacket(byte[] packet)
	{
		byte firstByte = packet[0];

		// A valid packet should have S and ~S bits (bits 0 and 1 in first byte) set to opposite values.
		if (0 == ((firstByte & 0x1) ^ ((firstByte & 0x2) >> 1))) return false;
		// A valid packet should have the C bit (bit 0 in second byte) set.
		if (0 == (packet[1] & 0x1)) return false;
		return true; 
	}

	protected boolean isNewScanStart(byte[] packet)
	{
		return (0x1 == (packet[0] & 0x1));
	}

	public LidarScan getLatestScan()
	{
		return m_lastScan;
	}

	
	//////////////////////////////////////////
	// Helper objects
	//////////////////////////////////////////
	
	public class LidarScan
	{
		public LidarMeasurement[] measurements;
		public int                measurementCount;
		public long               startTime;
		public float              scanRateInHz;

		public LidarScan()
		{
			int numMeasurements = baudRate / 10 / 5;
			DebugView.setValue("numMeasurements", numMeasurements);
			measurements = new LidarMeasurement[numMeasurements];
			measurementCount = 0;
			startTime = System.currentTimeMillis();
			scanRateInHz = 0.0f;
		}
	}
	
	public class LidarResponseDescriptor
	{
		public int length;
		public int sendMode;
		public int dataType;

		public LidarResponseDescriptor(int length, int sendMode, int dataType)
		{
			this.length = length;
			this.sendMode = sendMode;
			this.dataType = dataType;
		}

		public static final int modeSingleResponse = 0;
		public static final int modeMultipleResponse = 1;
		public static final int typeMeasurement = 0x81;
		public static final int typeDeviceInfo = 0x4;
		public static final int typeDeviceHealth = 0x6;
	}

	public class LidarMeasurement
	{
		public float   angle;
		public float   distance;
		public int     quality;

		public LidarMeasurement(float angle, float distance, int quality)
		{
			this.angle = angle;
			this.distance = distance;
			this.quality = quality;
		}
	}

	public class LidarHealth {
		public int status;
		public int errorCode;

		public LidarHealth(int status, int errorCode)
		{
			this.status = status;
			this.errorCode = errorCode;
		}

		public static final int stateGood = 0;
		public static final int stateWarning = 1;
		public static final int stateError = 2;
		
		public String toString() {
			if(status == stateGood) return "LidarHealth: GOOD";
			if(status == stateWarning) return "LidarHealth: WARNING";
			if(status == stateError) return "LidarHealth: ERROR";
			return "LidarHealth: UNKNOWN";
		}
	}
	
	public class LidarDeviceInfo
	{
		public int  model;
		public int  firmwareMinor;
		public int  firmwareMajor;
		public int  hardware;
		public String serialNumber;

		public LidarDeviceInfo(int model, int firmwareMinor, int firmwareMajor, int hardware, String serialNumber)
		{
			this.model = model;
			this.firmwareMinor = firmwareMinor;
			this.firmwareMajor = firmwareMajor;
			this.hardware = hardware;
			this.serialNumber = serialNumber;
		}
		
		public String toString() {
			return "LidarDeviceInfo: " + FileUtil.NEWLINE +
					"model: " + model + FileUtil.NEWLINE + 
					"firmware: " + firmwareMajor + "." + firmwareMinor + FileUtil.NEWLINE + 
					"hardware: " + hardware + FileUtil.NEWLINE + 
					"serialNumber: " + serialNumber + FileUtil.NEWLINE;
		}
	}

}
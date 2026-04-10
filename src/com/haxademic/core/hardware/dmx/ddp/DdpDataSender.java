package com.haxademic.core.hardware.dmx.ddp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Distributed Display Protocol sender for large RGB LED surfaces.
 *
 * Practical notes from testing against a Kulp PocketScroller / FPP setup:
 *
 * DDP is a much better fit than Art-Net or sACN when the target is a large,
 * contiguous RGB pixel surface rather than DMX fixtures. Art-Net and sACN both
 * inherit the DMX universe model, which means RGB pixels are split into chunks
 * of 170 pixels per universe when transmitting 510 channels. That creates a lot
 * of small packets per frame. DDP skips the DMX abstraction and sends raw RGB
 * bytes directly, so it can fit up to 480 RGB pixels in one packet while still
 * staying inside a normal Ethernet MTU.
 *
 * In practice that means dramatically fewer packets per frame:
 *
 * - Art-Net / sACN: 170 RGB pixels per packet
 * - DDP: 480 RGB pixels per packet
 *
 * Example packet counts for one full frame:
 *
 * - 384 x 32  = 12,288 pixels = about 73 Art-Net universes or about 26 DDP packets
 * - 384 x 128 = 49,152 pixels = about 290 Art-Net universes or about 103 DDP packets
 *
 * That packet-count reduction is usually the main reason DDP produces a much
 * higher visible frame rate on LED panels even when the sender-side Java timing
 * already looked fast. The Java app may only spend a few milliseconds enqueuing
 * packets, but the receiver still has to ingest them, buffer them, and present
 * them on the panel output. DDP lowers that transport overhead substantially.
 *
 * This sender writes DDP version 1 packets with RGB888 payloads:
 *
 * - UDP port 4048 by default
 * - 10-byte DDP header
 * - data type 0x0B (8-bit RGB)
 * - PUSH flag set on the last packet of a frame
 * - monotonically cycling sequence number from 1..15
 * - absolute byte offset into the pixel stream for multi-packet frames
 *
 * The PUSH flag matters. A large frame is split into multiple packets, and the
 * receiver can wait until the last packet marked PUSH arrives before presenting
 * the completed frame. That reduces visible tearing or partial updates.
 *
 * Important visual note: DDP should not reduce color precision compared to the
 * Art-Net path in this project. Both paths send 8-bit RGB values. If the image
 * appears more quantized or banded after switching to DDP, the most likely cause
 * is that the panel is refreshing faster and exposing PWM / multiplexing / scan
 * artifacts that were less obvious at the lower effective frame rate. In other
 * words, the protocol usually improves transport efficiency, but the panel's own
 * refresh behavior still defines the final visible image quality.
 *
 * This class intentionally mirrors the high-level API of ArtNetDataSender so the
 * calling code can swap protocols with minimal changes while testing receiver and
 * panel throughput limits.
 */
public class DdpDataSender {

	public static final int PORT = 4048;
	public static final int HEADER_LENGTH = 10;
	public static final int MAX_PIXELS_PER_PACKET = 480;
	public static final int MAX_DATA_LENGTH = MAX_PIXELS_PER_PACKET * 3;

	protected static final int VERSION_1 = 0x40;
	protected static final int PUSH = 0x01;
	protected static final int DATA_TYPE_RGB888 = 0x0b;
	protected static final int DEFAULT_DESTINATION_ID = 0x01;

	protected String controllerAddress;
	protected int pixelStart;
	protected int dataOffset;
	protected int numPixels = 0;
	protected int port = PORT;
	protected int destinationId = DEFAULT_DESTINATION_ID;
	protected int sequence = 0;
	protected byte[] dmxData;
	protected byte[] previousDmxData;
	protected boolean partialUpdatesEnabled = false;
	protected int fullFrameRefreshInterval = 120;
	protected int framesSinceFullFrame = 0;
	protected int lastTotalPackets = 0;
	protected int lastChangedPackets = 0;
	protected int lastSentPackets = 0;
	protected DatagramSocket socket;
	protected InetAddress address;

	public static boolean DEBUG = true;

	public DdpDataSender(String controllerAddress, int pixelStart, int numPixels) {
		this(controllerAddress, pixelStart, numPixels, PORT, DEFAULT_DESTINATION_ID);
	}

	public DdpDataSender(String controllerAddress, int pixelStart, int numPixels, int destinationId) {
		this(controllerAddress, pixelStart, numPixels, PORT, destinationId);
	}

	public DdpDataSender(String controllerAddress, int pixelStart, int numPixels, int port, int destinationId) {
		this.controllerAddress = controllerAddress;
		this.pixelStart = pixelStart;
		this.dataOffset = pixelStart * 3;
		this.numPixels = numPixels;
		this.port = port;
		this.destinationId = destinationId;
		dmxData = new byte[numPixels * 3];
		previousDmxData = new byte[numPixels * 3];

		try {
			address = InetAddress.getByName(controllerAddress);
			socket = new DatagramSocket();
			socket.setSendBufferSize(1024 * 1024);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't initialize DDP sender", e);
		}
	}

	public String controllerAddress() { return controllerAddress; }
	public int pixelStart() { return pixelStart; }
	public int numPixels() { return numPixels; }
	public int port() { return port; }
	public boolean partialUpdatesEnabled() { return partialUpdatesEnabled; }
	public int fullFrameRefreshInterval() { return fullFrameRefreshInterval; }
	public int lastTotalPackets() { return lastTotalPackets; }
	public int lastChangedPackets() { return lastChangedPackets; }
	public int lastSentPackets() { return lastSentPackets; }

	public void setPartialUpdatesEnabled(boolean enabled) {
		partialUpdatesEnabled = enabled;
	}

	public void setFullFrameRefreshInterval(int frames) {
		fullFrameRefreshInterval = Math.max(1, frames);
	}

	public void setColorAtIndex(int pixelIndex, int r, int g, int b) {
		if(pixelIndex >= dmxData.length) {
			if(DEBUG) P.out("Bad pixelIndex in DdpDataSender.setColorAtIndex()", pixelIndex, dmxData.length);
			return;
		}
		dmxData[pixelIndex + 0] = P.parseByte(r);
		dmxData[pixelIndex + 1] = P.parseByte(g);
		dmxData[pixelIndex + 2] = P.parseByte(b);
	}

	public void send() {
		sendChannels(dmxData.length);
	}

	protected int nextSequence() {
		sequence = (sequence >= 15) ? 1 : sequence + 1;
		return sequence;
	}

	protected void sendChannels(int activeChannels) {
		activeChannels = Math.max(0, Math.min(activeChannels, dmxData.length));
		if(activeChannels == 0) {
			lastTotalPackets = 0;
			lastChangedPackets = 0;
			lastSentPackets = 0;
			return;
		}

		int packetCount = (int) Math.ceil(activeChannels / (float) MAX_DATA_LENGTH);
		lastTotalPackets = packetCount;

		if(partialUpdatesEnabled == false) {
			sendAllPackets(packetCount, activeChannels);
			lastChangedPackets = packetCount;
			lastSentPackets = packetCount;
			framesSinceFullFrame = 0;
			copyCurrentToPrevious(activeChannels);
			return;
		}

		boolean[] changedPackets = new boolean[packetCount];
		int changedCount = 0;
		for(int packetIndex = 0; packetIndex < packetCount; packetIndex++) {
			int packetOffset = packetIndex * MAX_DATA_LENGTH;
			int payloadLength = Math.min(MAX_DATA_LENGTH, activeChannels - packetOffset);
			if(packetChanged(packetOffset, payloadLength)) {
				changedPackets[packetIndex] = true;
				changedCount++;
			}
		}

		lastChangedPackets = changedCount;
		boolean forceFullRefresh = (framesSinceFullFrame >= fullFrameRefreshInterval);
		if(changedCount == 0 && forceFullRefresh == false) {
			lastSentPackets = 0;
			framesSinceFullFrame++;
			return;
		}

		if(forceFullRefresh) {
			sendAllPackets(packetCount, activeChannels);
			lastSentPackets = packetCount;
			framesSinceFullFrame = 0;
			copyCurrentToPrevious(activeChannels);
			return;
		}

		int sentCount = 0;
		for(int packetIndex = 0; packetIndex < packetCount; packetIndex++) {
			if(changedPackets[packetIndex] == false) continue;
			int packetOffset = packetIndex * MAX_DATA_LENGTH;
			int payloadLength = Math.min(MAX_DATA_LENGTH, activeChannels - packetOffset);
			sentCount++;
			boolean isLastChangedPacket = (sentCount == changedCount);
			sendPacket(nextSequence(), packetOffset, payloadLength, isLastChangedPacket);
		}
		lastSentPackets = sentCount;
		framesSinceFullFrame++;
		copyCurrentToPrevious(activeChannels);
	}

	protected void sendAllPackets(int packetCount, int activeChannels) {
		for(int packetIndex = 0; packetIndex < packetCount; packetIndex++) {
			int packetOffset = packetIndex * MAX_DATA_LENGTH;
			int payloadLength = Math.min(MAX_DATA_LENGTH, activeChannels - packetOffset);
			boolean isLastPacket = (packetIndex == packetCount - 1);
			sendPacket(nextSequence(), packetOffset, payloadLength, isLastPacket);
		}
	}

	protected boolean packetChanged(int packetOffset, int payloadLength) {
		for(int i=0; i < payloadLength; i++) {
			if(dmxData[packetOffset + i] != previousDmxData[packetOffset + i]) return true;
		}
		return false;
	}

	protected void copyCurrentToPrevious(int activeChannels) {
		System.arraycopy(dmxData, 0, previousDmxData, 0, activeChannels);
		if(activeChannels < previousDmxData.length) {
			for(int i=activeChannels; i < previousDmxData.length; i++) previousDmxData[i] = 0;
		}
	}

	protected void sendPacket(int sequence, int packetOffset, int payloadLength, boolean isLastPacket) {
		byte[] packetData = new byte[HEADER_LENGTH + payloadLength];
		packetData[0] = (byte) (VERSION_1 | (isLastPacket ? PUSH : 0));
		packetData[1] = (byte) sequence;
		packetData[2] = (byte) DATA_TYPE_RGB888;
		packetData[3] = (byte) destinationId;

		int absoluteOffset = dataOffset + packetOffset;
		packetData[4] = (byte) ((absoluteOffset >> 24) & 0xff);
		packetData[5] = (byte) ((absoluteOffset >> 16) & 0xff);
		packetData[6] = (byte) ((absoluteOffset >> 8) & 0xff);
		packetData[7] = (byte) (absoluteOffset & 0xff);
		packetData[8] = (byte) ((payloadLength >> 8) & 0xff);
		packetData[9] = (byte) (payloadLength & 0xff);

		System.arraycopy(dmxData, packetOffset, packetData, HEADER_LENGTH, payloadLength);

		try {
			DatagramPacket packet = new DatagramPacket(packetData, packetData.length, address, port);
			socket.send(packet);
		} catch (Exception e) {
			if(DEBUG) P.out("DDP send error", e.getMessage());
		}
	}

	public void sendMatrixFromBuffer(PImage texture) {
		sendMatrixFromBuffer(texture, texture.width, texture.height, 0, 0, 0, true, true);
	}

	public void sendMatrixFromBuffer(PImage texture, boolean zigZags) {
		sendMatrixFromBuffer(texture, texture.width, texture.height, 0, 0, 0, true, true, zigZags);
	}

	public void sendMatrixFromBuffer(PImage texture, int matrixSize) {
		sendMatrixFromBuffer(texture, matrixSize, matrixSize, 0, 0, 0, true, true);
	}

	public void sendMatrixFromBuffer(PImage texture, int matrixW, int matrixH) {
		sendMatrixFromBuffer(texture, matrixW, matrixH, 0, 0, 0, true, true);
	}

	public void sendMatrixFromBuffer(PImage texture, int matrixW, int matrixH, int pixelIndexStart, int offsetX, int offsetY, boolean shouldLoadPixels, boolean shouldSend) {
		sendMatrixFromBuffer(texture, matrixW, matrixH, pixelIndexStart, offsetX, offsetY, shouldLoadPixels, shouldSend, shouldSend);
	}

	public void sendMatrixFromBuffer(PImage texture, int matrixW, int matrixH, int pixelIndexStart, int offsetX, int offsetY, boolean shouldLoadPixels, boolean shouldSend, boolean zigZags) {
		if(shouldLoadPixels) texture.loadPixels();

		int oobIndex = -1;
		int numPixelsPerMatrix = matrixW * matrixH;
		for(int i=0; i < numPixelsPerMatrix; i++) {
			int x = MathUtil.gridXFromIndex(i, matrixW);
			int y = MathUtil.gridYFromIndex(i, matrixW);
			int pixelColor = ImageUtil.getPixelColor(texture, offsetX + x, offsetY + y);
			int r = ColorUtil.redFromColorInt(pixelColor);
			int g = ColorUtil.greenFromColorInt(pixelColor);
			int b = ColorUtil.blueFromColorInt(pixelColor);

			int pixelIndex = i * 3;
			int rowStartI = P.floor(i / matrixW) * matrixW;
			int twoRowIndex = i % (matrixW * 2);
			int zigZagRevIndex = matrixW - 1 - (i % matrixW);
			if(zigZags && twoRowIndex < matrixW) {
				pixelIndex = (rowStartI + zigZagRevIndex) * 3;
			}

			int ddpPixelIndex = (pixelIndexStart * 3) + pixelIndex;
			if(ddpPixelIndex <= dmxData.length - 3) setColorAtIndex(ddpPixelIndex, r, g, b);
			else oobIndex = ddpPixelIndex;
		}
		if(shouldSend) send();

		if(oobIndex > -1 && P.p.frameCount % 60 == 1) {
			if(DEBUG) P.out("ERROR: DDP data index is past array length in sendMatrixFromBuffer(): ", oobIndex);
		}
	}

	public void sendRgbDirectFromPixelsArray(PImage texture, boolean shouldLoadPixels) {
		if(shouldLoadPixels) texture.loadPixels();
		int[] pixelColors = texture.pixels;
		int pixelsToCopy = Math.min(pixelColors.length, numPixels);
		if(DEBUG && pixelColors.length != numPixels && P.p.frameCount % 60 == 1) {
			P.out("WARNING: DDP texture pixel count does not match sender pixel count", pixelColors.length, numPixels);
		}
		for(int i=0; i < pixelsToCopy; i++) {
			int pixelColor = pixelColors[i];
			setColorAtIndex(i * 3, (pixelColor >> 16) & 0xFF, (pixelColor >> 8) & 0xFF, pixelColor & 0xFF);
		}
		sendChannels(pixelsToCopy * 3);
	}

	public void close() {
		if(socket != null && socket.isClosed() == false) socket.close();
	}

	public void drawDebug(PGraphics pg) {
		drawDebug(pg, false);
	}

	public void drawDebug(PGraphics pg, boolean openContext) {
		if(openContext) {
			pg.beginDraw();
			pg.background(0);
		}
		pg.push();
		pg.noStroke();
		int pixSize = 4;
		int x = 0;
		int y = 0;
		for(int i=0; i < dmxData.length/3; i+=3) {
			int r = P.parseInt(dmxData[i + 0]);
			int g = P.parseInt(dmxData[i + 1]);
			int b = P.parseInt(dmxData[i + 2]);
			pg.fill(r, g, b);
			pg.rect(x, y, pixSize, pixSize);
			x += pixSize;
			if(x >= pg.width) {
				x = 0;
				y += pixSize;
			}
		}
		pg.pop();
		if(openContext) pg.endDraw();
	}
}
package com.haxademic.sketch.audio.jasiohost;
//package com.haxademic.demo.audio;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.sound.sampled.Line;
//import javax.sound.sampled.Mixer;
//
//import com.haxademic.core.app.P;
//import com.haxademic.core.app.PAppletHax;
//import com.haxademic.core.app.config.AppSettings;
//import com.haxademic.core.data.constants.PTextAlign;
//import com.haxademic.core.draw.text.FontCacher;
//import com.haxademic.core.file.DemoAssets;
//import com.haxademic.core.file.FileUtil;
//import com.haxademic.core.ui.UIButton;
//import com.synthbot.jasiohost.AsioChannel;
//import com.synthbot.jasiohost.AsioDriver;
//import com.synthbot.jasiohost.AsioDriverListener;
//
//import beads.AudioContext;
//import processing.core.PFont;
//
//public class Demo_MultichannelAudioJAsioHost
//extends PAppletHax implements AsioDriverListener {
//	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
//
//	protected AudioContext audioContext;
//	
//	protected ArrayList<Line.Info> linesOut;
//	protected ArrayList<Mixer> mixers;
//	protected File audioSample;
//	protected String LINE_INDEX = "LINE_INDEX";
//	protected String MIXER_INDEX = "MIXER_INDEX";
//
//	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
//		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
//		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
//	}
//
//	public void setupFirstFrame() {
//		List<String> driverNameList = AsioDriver.getDriverNames();
//
//		AsioDriver asioDriver = AsioDriver.getDriver(driverNameList.get(0));
//		int bufferSize = asioDriver.getBufferPreferredSize();
//
//		for(int i = 0; i < asioDriver.getNumChannelsInput(); i++){
////			addChannel(asioDriver.getChannelInput(i));
//		}
//		for(int i = 0; i < asioDriver.getNumChannelsOutput(); i++){
////			addChannel(asioDriver.getChannelOutput(i));
//		}
//
//		System.out.println("=== Cards ===");
//		for(AsioSoundCard card : soundCards){
//			System.out.println(card.toString());
//			card.start();
//		}
//
//		asioDriver.addAsioDriverListener(this);
//		asioDriver.createBuffers(new HashSet<>(activeChannels));
//		asioDriver.start();
//	}
//
//	private void addChannel(AsioChannel channel){
//		String name = channel.getChannelName().substring(0, channel.getChannelName().length()-2);
//		AsioSoundCard card = getCardByName(name);
//		if(card == null){
//			AsioSoundCard newCard = new AsioSoundCard(name, bufferSize);
//			newCard.addChannel(channel);
//			soundCards.add(newCard);
//		}else{
//			card.addChannel(channel);
//		}
//
//		activeChannels.add(channel);
//	}
//
//	public AsioSoundCard getCardByName(String name){
//		for(AsioSoundCard card : soundCards){
//			if(card.getName().equals(name)){
//				return card;
//			}
//		}
//		return null;
//	}
//
//	public void shutDown(){
//		asioDriver.shutdownAndUnloadDriver();
//	}
//
//	@Override
//	public void bufferSwitch(long systemTime, long samplePosition, Set<AsioChannel> channels) {
//		for(AsioSoundCard card : soundCards){
//			card.inputReadEvent(systemTime, samplePosition, channels);
//		}
//		for(AsioSoundCard card : soundCards){
//			card.outputWriteEvent(systemTime, samplePosition, channels);
//		}
//	}
//
//	@Override
//	public void sampleRateDidChange(double sampleRate) {}
//
//	@Override
//	public void resetRequest() {}
//
//	@Override
//	public void resyncRequest() {}
//
//	@Override
//	public void bufferSizeChanged(int bufferSize) {}
//
//	@Override
//	public void latenciesChanged(int inputLatency, int outputLatency) {}
//
//	public void close() {
//		asioDriver.shutdownAndUnloadDriver();
//	}
//
//	public void drawApp() {
//		p.background(0);
//		
//		// set font
//		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 20);
//		FontCacher.setFontOnContext(p.g, font, 255, 1, PTextAlign.LEFT, PTextAlign.TOP);
//		
//		// write cur lineInfo
////		int lineIndex = UI.valueInt(LINE_INDEX);
////		lineIndex = P.constrain(lineIndex, 0, linesOut.size() - 1);
//		// p.g.text(""+linesOut.get(lineIndex) + FileUtil.NEWLINE + FileUtil.NEWLINE, 300, 100, 500, 1000);
//		
//		// show mixer info
//		int mixerIndex = UI.valueInt(MIXER_INDEX);
//		mixerIndex = P.constrain(mixerIndex, 0, mixers.size() - 1);
//		p.g.text("Mixer:" + mixers.get(mixerIndex).getMixerInfo() + FileUtil.NEWLINE, 300, 100, 500, 1000);
//	}
//
//	public void keyPressed() {
//		super.keyPressed();
////		if(p.key == ' ') play(audioSample, clipFromMixer());
//	}
//	
//	// UIButton callback
//	
//	public void uiButtonClicked(UIButton button) {
//		P.out("uiButtonClicked: please override", button.id(), button.value());
//	}
//	
//	
//	
//	
//	public class AsioSoundCard {
//
//		private boolean started = false;
//
//		private String name;
//		private List<AsioChannel> inputChannels;
//		private List<AsioChannel> outputChannels;
//
//		private int bufferSize;
//		private float[][] inputBuffer; //[inputChannel][bufferSize]
//
//		private List<AsioSoundCard> sendingTo;
//
//		public AsioSoundCard(String name, int bufferSize) {
//			this.name = name;
//			this.bufferSize = bufferSize;
//			this.inputChannels = new ArrayList<>();
//			this.outputChannels = new ArrayList<>();
//			this.sendingTo = new ArrayList<>();
//		}
//
////		public float getPreFaderVolume(){
////			return Util.getMax(inputBuffer[0]);
////		}
//
//		public void addChannel(AsioChannel channel){
//			if(started)
//				throw new IllegalStateException("Can not add channel: Already started");
//			if(channel.isInput())
//				inputChannels.add(channel);
//			else
//				outputChannels.add(channel);
//		}
//
//		public void start(){
//			if(started)
//				throw new IllegalStateException("Can not start: Already started");
//			started = true;
//
//			inputBuffer = new float[inputChannels.size()][bufferSize];
//			Collections.sort(inputChannels, asioChannelComparer);
//			Collections.sort(outputChannels, asioChannelComparer);
//		}
//
//		Comparator<AsioChannel> asioChannelComparer = new Comparator<AsioChannel>() {
//			@Override
//			public int compare(AsioChannel o1, AsioChannel o2) {
//				return  o1.getChannelName().compareTo(o2.getChannelName());
//			}
//		};
//
//		public void inputReadEvent(long systemTime, long samplePosition, Set<AsioChannel> channels){
//			if(!started)
//				throw new IllegalStateException("Can not read: Not yet started");
//
//			for(int i = 0; i < inputChannels.size(); i++){
//				inputChannels.get(i).read(inputBuffer[i]);
//			}
//		}
//
//		public void outputWriteEvent(long systemTime, long samplePosition, Set<AsioChannel> channels) {
//			if(!started)
//				throw new IllegalStateException("Can not write: Not yet started");
//			for(int i = 0; i < sendingTo.size(); i++){
//				sendingTo.get(i).writeToOuputChannels(inputBuffer);
//			}
//		}
//
//		public void writeToOuputChannels(float[][] soundz){
//			if(!started)
//				throw new IllegalStateException("Can not write: Not yet started");
//			for(int i = 0; i < soundz.length && i < outputChannels.size(); i++){
//				outputChannels.get(i).write(soundz[i]);
//			}
//		}
//
//		@Override
//		public String toString() {
//			StringBuilder builder = new StringBuilder();
//			builder.append("AsioSoundCard [");
//			if (name != null) {
//				builder.append("name=");
//				builder.append(name);
//				builder.append(", ");
//			}
//			if (inputChannels != null) {
//				builder.append("inputChannels=");
//				builder.append(inputChannels.size());
//				builder.append(", ");
//			}
//			if (outputChannels != null) {
//				builder.append("outputChannels=");
//				builder.append(outputChannels.size());
//			}
//			builder.append("]");
//			return builder.toString();
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public void addFeedbackCard(AsioSoundCard feedback){
//			this.sendingTo.add(feedback);
//		}
//
//		public void removeFeedbackCard(AsioSoundCard feedback){
//			this.sendingTo.remove(feedback);
//		}
//
//
//
//
//
//
//	}
//
//}

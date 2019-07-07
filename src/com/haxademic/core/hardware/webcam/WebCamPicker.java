package com.haxademic.core.hardware.webcam;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UIButton;
import com.haxademic.core.ui.UIButton.IUIButtonDelegate;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Capture;

public class WebCamPicker
implements IUIButtonDelegate {

	protected String configId;
	protected String configFile;
	protected boolean camerasListed = false;
	protected static String[] camerasList = null;
	protected ArrayList<CameraConfig> cameraConfigs;
	protected String selectedConfig = null;
	protected int lastFrameUpdated = 0;
	public static PImage noCamera = new PImage(32, 32);
	public static final int BUTTON_W = 180;
	public static final int BUTTON_H = 24;

	public Capture webCam = null;

	// TODO:
	// - Check basic string array to see if webcam config exists, rather than loop through all the child objects
	// - Have a public method to re-scan for webcams attached
	// - If no cameras available, "There are no cameras available." error keeps printing
	
	public WebCamPicker() {
		this(null);
	}
	
	public WebCamPicker(String configId) {
		this.configId = configId;
		new Thread(new Runnable() { public void run() {
			P.out("CameraConfig :: getting cameras");
			camerasList = Capture.list();
		}}).start();	
	}

	protected void lazyLoadConfigs() {
		if(camerasList == null) return;
		camerasListed = true;
		if (camerasList.length == 0) {
			P.println("There are no cameras available.");
		} else if(cameraConfigs == null) {
			// setup
			cameraConfigs = new ArrayList<CameraConfig>();
			String lastCamName = "";

			// loop through cameras
			P.println("# Parsing cameras");
			for (int i = 0; i < camerasList.length; i++) {
				// get camera name & components
				String camera = camerasList[i];
				String[] cameraNameParts = camera.split(",");
				String cameraName = cameraNameParts[0].split("=")[1];
				String sizeStr = cameraNameParts[1].split("=")[1];
				String widthStr = sizeStr.split("x")[0];
				int width = ConvertUtil.stringToInt(widthStr);

				// when we find a new camera, make a new config object
				if(lastCamName.equals(cameraName) == false) {
					lastCamName = cameraName;
					cameraConfigs.add(new CameraConfig(this, cameraName));
				}

				// add config to camera object
				if(width > 420) {
					CameraConfig curConfig = cameraConfigs.get(cameraConfigs.size()-1);
					curConfig.addConfig(camera);
				}
			}
			
			// if we had a selected config from file, load it up
			if(configId != null) {
				configFile = FileUtil.getFile("text/prefs/camera/" + configId + ".txt");
				if(FileUtil.fileExists(configFile)) {
					FileUtil.createDir(FileUtil.getFile("text/prefs/camera/"));
					selectedConfig = FileUtil.readTextFromFile(configFile)[0];
					if(webcamConfigExists(selectedConfig)) {
						selectCam(selectedConfig);
					}
				}
			}
		}
	}
	
	protected boolean webcamConfigExists(String config) {
		for (int i = 0; i < cameraConfigs.size(); i++) {
			for (int j = 0; j < cameraConfigs.get(i).configs().size(); j++) {
				String mode = cameraConfigs.get(i).configs().get(j).config();
				if(mode.equals(config)) return true;
			}
		}
		return false;
	}

	public void drawMenu(PGraphics pg) {
		// draw background
		pg.fill(0, 180);
		pg.noStroke();
		pg.rect(0, 0, pg.width, pg.height);
		
		if(selectedConfig != null) {
			// draw header (camera title)
			PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
			FontCacher.setFontOnContext(pg, font, P.p.color(0, 255, 0), 2f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text(selectedConfig, 20, 20);
		} else if(camerasListed) {
			// none selected
			PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
			FontCacher.setFontOnContext(pg, font, P.p.color(255, 0, 0), 2f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text("No camera selected", 20, 20);
		}

		// draw menu
		if(cameraConfigs != null) {
			for (int i = 0; i < cameraConfigs.size(); i++) {
				int x = 20 + BUTTON_W * i;
				int y = 50;

				// draw header (camera title)
				PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
				FontCacher.setFontOnContext(pg, font, P.p.color(255), 2f, PTextAlign.LEFT, PTextAlign.TOP);
				pg.text(cameraConfigs.get(i).name(), x, y);

				// draw buttons for cameras
				for (int j = 0; j < cameraConfigs.get(i).configs().size(); j++) {
					int buttonY = y + BUTTON_H + BUTTON_H * j;
					CameraConfigMode cameraConfigMode = cameraConfigs.get(i).configs().get(j);
					cameraConfigMode.button().setPosition(x, buttonY);
					cameraConfigMode.button().update(pg);
					
					// draw selection
					if(selectedConfig != null && selectedConfig.equals(cameraConfigMode.config())) {
						pg.fill(255, 100);
						pg.stroke(0, 255, 0);
						pg.strokeWeight(4);
						pg.rect(x, buttonY, BUTTON_W, BUTTON_H);
					}
				}
			}
		} else if(camerasListed == true) {
			// Error - no cameras found!
			PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
			FontCacher.setFontOnContext(pg, font, P.p.color(255, 0, 0), 2f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text("No cameras detected!", 20, 20);
		} else {
			// Info - cameras initializing
			PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 12);
			FontCacher.setFontOnContext(pg, font, P.p.color(0, 255, 0), 2f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text("Cameras initializing", 20, 20);
		}
	}

	public PImage image() {
		lazyLoadConfigs();
		if(lastFrameUpdated < P.p.frameCount) {
			lastFrameUpdated = P.p.frameCount;
			if(webCam != null && webCam.available() == true) {
				webCam.read();
			}
		}
		return (webCam != null) ? webCam : noCamera;
	}

	@Override
	public void clicked(UIButton button) {
		selectCam(button.id());
		
		// if we had a selected config from file, store it on click!
		if(configId != null) {
			FileUtil.createDir(FileUtil.getFile("text/prefs/camera/"));
			FileUtil.writeTextToFile(configFile, selectedConfig);
		}
	}
	
	protected void selectCam(String camId) {
		if(webCam != null) webCam.stop();
		webCam = new Capture(P.p, camId);
		webCam.start();
		selectedConfig = camId;
	}

	public class CameraConfig {

		protected WebCamPicker picker;
		protected String name;
		protected String fps;
		protected ArrayList<CameraConfigMode> configs;

		public CameraConfig(WebCamPicker picker, String name) {
			this.picker = picker;
			this.name = name;
			configs = new ArrayList<CameraConfigMode>();
		}

		public void addConfig(String config) {
			boolean hasConfig = false;
			for (int i = 0; i < configs.size(); i++) {
				if(configs.get(i).config().equals(config)) hasConfig = true;
			}
			if(hasConfig == false) {
				configs.add(new CameraConfigMode(picker, config));
			}
		}

		public String name() {
			return name;
		}

		public ArrayList<CameraConfigMode> configs() {
			return configs;
		}

	}

	public class CameraConfigMode {

		protected String config;
		protected String size;
		protected String fps;
		protected UIButton button;

		public CameraConfigMode(WebCamPicker picker, String config) {
			this.config = config;
			String[] cameraNameParts = config.split(",");
			size = cameraNameParts[1].split("=")[1];
			fps = cameraNameParts[2].split("=")[1];

			button = new UIButton(picker, config, 0, 0, BUTTON_W, BUTTON_H, false);
			button.label(size + " / " + fps);
		}

		public String config() {
			return config;
		}

		public String size() {
			return size;
		}

		public String fps() {
			return fps;
		}

		public UIButton button() {
			return button;
		}
	}
}

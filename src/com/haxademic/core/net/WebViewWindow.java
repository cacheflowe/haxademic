package com.haxademic.core.net;


import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import processing.data.JSONObject;

public class WebViewWindow 
extends Application {
	
	public interface IWebViewDelegate {
		public void webViewCreated(WebViewWindow webView);
		public void webViewLoaded(WebViewWindow webView);
		public void webViewBridged(WebViewWindow webView);
		public void webViewClosed(WebViewWindow webView);
	}

	public static boolean UNDECORATED = false;
	protected IWebViewDelegate delegate;
	protected Stage stage;
	protected Browser browser;
	protected boolean createdCalledBack = false;
	
	///////////////////////////////////////////////
	// Static launcher helper
	///////////////////////////////////////////////
	
	public static int numWebViews = 0;

	public static void launchWebView(String url) {
		launchWebView(url, null);
	}
	
	public static void launchWebView(String url, IWebViewDelegate delegate) {
		// build JavaFX WebView Application thread
		Runnable webViewThread = new Runnable() {
			public void run() {             
				WebViewWindow newWebView = new WebViewWindow();
				newWebView.start(new Stage());
				newWebView.setDelegate(delegate);
				newWebView.loadURL(url);
			}
		};
		
		// subsequent window launches need to be called slightly differently
		if(numWebViews == 0) {
			Platform.startup(webViewThread);
		} else {
			Platform.runLater(webViewThread);
		}
	}

	protected WebViewWindow setDelegate(IWebViewDelegate delegate) {
		this.delegate = delegate;
		if(delegate != null) delegate.webViewCreated(this);
		return this;
	}
		
	///////////////////////////////////////////////
	// JavaFX Application overrides
	///////////////////////////////////////////////
	
	@Override
	public void start(Stage stage) {
		// set up stage
		this.stage = stage;
		setAppIcon();
		stage.setTitle("Haxademic | WebViewWindow");
		stage.initStyle((UNDECORATED) ? StageStyle.UNDECORATED : StageStyle.DECORATED);
		
		// create the browser & embed into Stage/Scene
		browser = new Browser();
		Scene scene = new Scene(browser, 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		stage.show();
		stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

		numWebViews++;
	}
	
	protected void setAppIcon() {
		String appIconFile = Config.getString(AppSettings.APP_ICON, "haxademic/images/haxademic-logo.png");
		String iconPath = FileUtil.getPath(appIconFile);
		if(FileUtil.fileExists(iconPath)) {
			stage.getIcons().add(new Image(iconPath));
		}
	}
	
	@Override
	public void stop(){
//	    P.out("Stage is closing");
	}
	
	private void closeWindowEvent(WindowEvent event) {
		numWebViews--;
		if(delegate != null) delegate.webViewClosed(this);
	}
	
	///////////////////////////////////////////////
	// JavaFX WebView component
	///////////////////////////////////////////////
	
	public class Browser extends Region {

		protected WebView webView;
		protected WebEngine webEngine;

		public Browser() {
			// init WebView
			webView = new WebView();
			// webView.setDisable(true);				// disable keyboard/mouse
			// webView.setContextMenuEnabled(false);	// disable right-click menu
			
			// get WebEngine
			webEngine = webView.getEngine();
			P.println(webEngine.getUserAgent());
			
			// add listeners
			webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
	            public void handle(WebEvent<String> ev) {
	                P.out(">>>>>>> WebViewWindow.alert() :: ", ev);
	            }
	        });
		    webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<Worker.State>() {
	            public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, State oldState, State newState) {
	            	// P.out("##### newState", newState);
	                if (newState == State.SUCCEEDED) {
	                    webPageLoaded();
	                }
	            }
	        });

			
			//apply the styles
			//add the web view to the scene
			getStyleClass().add("browser");
			getChildren().add(webView);
		}
		
		@Override protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(webView,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
		}

		public WebView webView() {
			return webView;
		}
		
		public WebEngine webEngine() {
			return webEngine;
		}
		
	}
	
	///////////////////////////////////////////////
	// WebView bridge 
	///////////////////////////////////////////////
	
	public void executeJavascript(String jsStr) {
        Platform.runLater(() -> {	// ensure .js gets called on main thread or errors are thrown
        	try {
        		// in case page hasn't initialized or is reloading, do try/catch
        		browser.webEngine.executeScript(jsStr);
        	} catch(JSException error) {
        		P.out(error.getMessage());
        	}
        });
	}
	
	protected void webPageLoaded() {
		// do created callback only once
		if(delegate != null) delegate.webViewLoaded(this);
		createdCalledBack = true;

		// kick off the building of the native bridge after any URL reload
		Platform.runLater(() -> {
			buildJsBridge();
		});
	}
	
	protected void buildJsBridge() {
		// Init js bridge with this class as the delegate
		// this creates a global object on the web page: `window.nativeBridgeJava`
		// this `nativeBridgeJava` allows calling public methods in this Java class.
		// For simplicity, everything should route through a single method, in the form of JSON
		JSObject win = (JSObject) browser.webEngine.executeScript("window");
		win.setMember("nativeBridge", this);
	}
	
	// callback from webview to native view
	// this public method can't be in an inner class!
	
	public static final String DATA_TYPE = "type";
	public static final String DATA_TYPE_NUMBER = "number";
	public static final String DATA_TYPE_STRING = "string";
	public static final String DATA_TYPE_BOOLEAN = "boolean";
	public static final String DATA_TYPE_JSON_KEY = "json";
	public static final String JSON_KEY = "key";
	public static final String JSON_VALUE = "value";

	public void webCallback(String message) {
		// P.out("webCallback", message);
		// parse incoming json and pass along to correct AppStore data type. Mimics AppStoreDistributed
		// format:
		// {
		//   "type": "string",
		//   "key": "cmd",
		//   "value": "test!",
		// }
		JSONObject jsonData = JSONObject.parse(message);
		
		// confirm bridge is connected
		if(!jsonData.isNull(JSON_KEY) && jsonData.getString(JSON_KEY).equals("bridge-init")) {
			if(delegate != null) delegate.webViewBridged(this);
		}
		
		// if `store` key exists, set on local store
		if(!jsonData.isNull(DATA_TYPE)) {
			// string, number, boolean
			if(jsonData.getString(DATA_TYPE).equals(DATA_TYPE_STRING)) {
				P.store.setString(jsonData.getString(JSON_KEY), jsonData.getString(JSON_VALUE));
			} else if(jsonData.getString(DATA_TYPE).equals(DATA_TYPE_NUMBER)) {
				P.store.setNumber(jsonData.getString(JSON_KEY), jsonData.getFloat(JSON_VALUE));
			} else if(jsonData.getString(DATA_TYPE).equals(DATA_TYPE_BOOLEAN)) {
				P.store.setBoolean(jsonData.getString(JSON_KEY), jsonData.getBoolean(JSON_VALUE));
			}
		} else {
			// if "type" property is omitted, we can just forward the JSON along to any objects that might be listening
			P.store.setString(DATA_TYPE_JSON_KEY, message);
		}
	}

	
	///////////////////////////////////////////////
	// Haxademic WebView interface
	///////////////////////////////////////////////
	
	public Stage stage() {
		return stage;
	}
	
	public Browser browser() {
		return browser;
	}
	
	public void loadURL(String url) {
		Platform.runLater(() -> {
			browser.webEngine.load(url);
		});
	}
	
	public void reload() {
		Platform.runLater(() -> {
			browser.webEngine.reload();
		});
	}

	public void hide() {
        Platform.runLater(() -> {	// ensure this is on main thread or errors are thrown
        	stage.toBack();
        });
	}
	
	public void setWindowTitle(String title) {
		stage.setTitle("Haxademic | WebViewWindow | " + title);
	}
	
	public void setFullscreen(boolean isFullscreen) {
        Platform.runLater(() -> {	// ensure this is on main thread or errors are thrown
        	stage.setFullScreen(isFullscreen);
        });

	}
	
	public void setLocation(int x, int y) {
		stage.setX(x);
		stage.setY(y);
	}
	
	public void setSize(int w, int h) {
		stage.setWidth(w);
		stage.setHeight(h);
	}
	
}
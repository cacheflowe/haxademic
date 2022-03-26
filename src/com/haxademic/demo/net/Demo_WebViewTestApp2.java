package com.haxademic.demo.net;

import com.haxademic.core.app.P;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Demo_WebViewTestApp2 
extends Application {

	public static Demo_WebViewTestApp2 instance = null;
	protected Browser browser;
	
	
	@Override
	public void start(Stage stage) {
		// create the browser
		stage.setTitle("Web Window");
		browser = new Browser();
		Scene scene = new Scene(browser, 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		//      scene.getStylesheets().add("webviewsample/BrowserToolbar.css");        
		stage.show();
		
		instance = this;
	}
	
	public void callJavascript(String jsStr) {
		browser.webEngine.executeScript(jsStr);
	}
	
    public void printSomething() {
        P.println("You called a method on the application");
    }


	// BROWSER CLASS

	class Browser extends Region {

		public WebView webView;
		public WebEngine webEngine;

		public Browser() {
			webView = new WebView();
			webEngine = webView.getEngine();
			P.println(webEngine.getUserAgent());
			//apply the styles
			getStyleClass().add("browser");
			// load the web page
			webEngine.load("http://www.cacheflowe.com/");
//			webEngine.load("http://inear.se/beanstalk/");	// no WebGL yet
//			webEngine.load("https://tonejs.github.io/");	// no WebAudio yet
			//add the web view to the scene
			getChildren().add(webView);

		}
		
//		private Node createSpacer() {
//			Region spacer = new Region();
//			HBox.setHgrow(spacer, Priority.ALWAYS);
//			return spacer;
//		}

		@Override protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
		}

		@Override protected double computePrefWidth(double height) {
			return 750;
		}

		@Override protected double computePrefHeight(double width) {
			return 500;
		}
	}
}
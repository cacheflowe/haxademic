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

public class Demo_WebViewTestApp 
extends Application {

	public static Demo_WebViewTestApp instance = null;
	
	@Override
	public void start(Stage stage) {
		//		Button btn = new Button("OK");
		//		Scene scene = new Scene(btn, 200, 250);
		//		stage.setTitle("OK");
		//		stage.setScene(scene);
		//		stage.show();
		
		// create the browser
		stage.setTitle("Web Window");
		Scene scene = new Scene(new Browser(),750,500, Color.web("#666970"));
		stage.setScene(scene);
		//      scene.getStylesheets().add("webviewsample/BrowserToolbar.css");        
		stage.show();
		
		instance = this;
	}
	
    public void printSomething() {
        P.println("You called a method on the application");
    }


	// BROWSER CLASS

	class Browser extends Region {

		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();

		public Browser() {
			P.println(webEngine.getUserAgent());
			//apply the styles
			getStyleClass().add("browser");
			// load the web page
			webEngine.load("http://www.cacheflowe.com/");
//			webEngine.load("https://tonejs.github.io/");
			//add the web view to the scene
			getChildren().add(browser);

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
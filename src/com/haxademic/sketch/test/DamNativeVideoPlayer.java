package com.haxademic.sketch.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.FileUtil;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import processing.awt.PSurfaceAWT.SmoothCanvas;
import processing.core.PImage;

public class DamNativeVideoPlayer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// Performance TO_TRY:
	// * Run on Windows
	// * Try a different delegate object for Movie instances, launched on different threads
	// * Java compile flags
	
	protected ArrayList<MediaPlayer> movies;
	protected String movieLocation = "video/dancelab/mimics/";
	protected String[] movieFiles = {
			"001.portrait.mp4.noaudio.mp4",
			"002.portrait.mp4.noaudio.mp4",
			"003.portrait.mp4.noaudio.mp4",
			"004.portrait.mp4.noaudio.mp4",
			"005.portrait.mp4.noaudio.mp4",
			"006.portrait.mp4.noaudio.mp4",
			"007.portrait.mp4.noaudio.mp4",
			"008.portrait.mp4.noaudio.mp4",
			"009.portrait.mp4.noaudio.mp4",
			"010.portrait.mp4.noaudio.mp4",
			"011.portrait.mp4.noaudio.mp4",
			"012.portrait.mp4.noaudio.mp4",
			"013.portrait.mp4.noaudio.mp4",
			"014.portrait.mp4.noaudio.mp4",
			"015.portrait.mp4.noaudio.mp4",
			"016.portrait.mp4.noaudio.mp4",
			"017.portrait.mp4.noaudio.mp4",
			"018.portrait.mp4.noaudio.mp4",
			"019.portrait.mp4.noaudio.mp4",
			"020.portrait.mp4.noaudio.mp4",
			"021.portrait.mp4.noaudio.mp4",
			"022.portrait.mp4.noaudio.mp4",
			"023.portrait.mp4.noaudio.mp4",
			"024.portrait.mp4.noaudio.mp4"
	};
	protected int videoLoadIndex = 0;
	
	protected int MAX_MOVIES = 4;
		
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, P.JAVA2D ); // P.JAVA2D P.FX2D P.P2D P.P3D
		p.appConfig.setProperty( AppSettings.WIDTH, 1800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, false );
	}

	public void setup() {
		super.setup();
	}
	
	JFXPanel fxPanel = new JFXPanel();
	MediaPlayer mediaPlayer;
	MediaView mv;
    JRootPane rootLayout;
    JLayeredPane rootLayoutLayeredPane;
    
    protected void initFX(JFXPanel fxPanel) {
    	
    	root = new Group();
        Scene scene = new Scene(root, p.width, p.height, Color.ALICEBLUE);
        fxPanel.setScene(scene);


		// add JFrame to Processing window?!   		        
		Frame realAppFrame = ((SmoothCanvas) p.getSurface().getNative()).getFrame(); // only with JAVA2D renderer  processing.awt.PSurfaceAWT.SmoothCanvas
		P.println("realAppFrame.getParent()",realAppFrame.getParent());
		rootLayout = (JRootPane)realAppFrame.getComponents()[0]; // is really a JRootPane
		
		P.println("rootLayout.getComponents().length",rootLayout.getComponents().length);
		P.println("rootLayout.getComponents()[0]",rootLayout.getComponents()[0]);
		
		
		JPanel rootLayoutPanel = (JPanel)rootLayout.getComponents()[0];
		rootLayoutLayeredPane = (JLayeredPane)rootLayout.getComponents()[1];
		rootLayoutLayeredPane.getComponentCount();
//		rootLayoutPanel.
		
		for (Component conponent : rootLayoutLayeredPane.getComponents()) {
			P.println("conponent",conponent);
		}
		JPanel otherpanel = (JPanel)rootLayoutLayeredPane.getComponents()[0];
		otherpanel.setOpaque(false);
		otherpanel.setVisible(false);
		
		
		// set main Processing canvas properties
		for (Component conponent : otherpanel.getComponents()) {
			P.println("otherpanel conponent",conponent);
		}
		SmoothCanvas surface = (SmoothCanvas)otherpanel.getComponents()[0];
//		surface.setBounds(new java.awt.Rectangle(50, 50, 100, 100));
//		surface.setVisible(true);
		

//		rootLayoutLayeredPane.add(fxPanel);
//		rootLayoutLayeredPane.setComponentZOrder(fxPanel, 1);
		

		
		fxPanel.setPreferredSize(new Dimension(p.width, p.height));
		fxPanel.setVisible(true);
		fxPanel.setPreferredSize(new Dimension(p.width, p.height));
		fxPanel.setBounds(new java.awt.Rectangle(0, 0, p.width, p.height));

		
		
    }

    Group root;
	public void addNextMovie() {
        Platform.runLater(new Runnable() {
            @Override public void run() {

            	// add a new one into the mix
            	P.println("Loading: ", movieLocation + movieFiles[videoLoadIndex]);
            	String filePath = FileUtil.getFile(movieLocation + movieFiles[videoLoadIndex]);
            	File f = new File(FileUtil.getFile(movieLocation), movieFiles[videoLoadIndex]);
            	mediaPlayer = new MediaPlayer(new Media(f.toURI().toString()));
            	mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            	mediaPlayer.play();
            	mv = new MediaView(mediaPlayer);
//        Rectangle r = new Rectangle(25,25,50,50);
//        r.setFill(Color.BLUE);
//        root.getChildren().add(r);
            	((Group)fxPanel.getScene().getRoot()).getChildren().add(mv);
//        root.getChildren().add(mv);
            	mv.setFitWidth(200);
            	mv.setFitHeight(400);
            	mv.setTranslateX(200 * videoLoadIndex);
            	mv.setTranslateY(0);
            	
            	
            	
            	
            	
            	P.println("Moive created: ", movieLocation + movieFiles[videoLoadIndex]);
//		movie.loop();
            	movies.add(mediaPlayer);
            	P.println("Moive added: ", movieLocation + movieFiles[videoLoadIndex]);
            	
            	
            	// loop back on video library
            	videoLoadIndex++;
            	if(videoLoadIndex >= movieFiles.length) videoLoadIndex = 0;
            }
        });

		
	}
	
	protected void restartAllMovies() {
		// restart all movies
		for (int i = 0; i < movies.size(); i++) {
//			movies.get(i).jump(0);
		}
	}
	
	public int numLoadedMovies() {
		int loadedMovies = 0;
		for (int i = 0; i < movies.size(); i++) {
//			if(movies.get(i).height > 0) loadedMovies++;
		}
		return loadedMovies;
	}

	public void drawApp() {	
		background(0,127,0);
		// load a new movie once in a while
		if(p.frameCount % 100 == 0) {
			if(movies.size() < 9) {
				addNextMovie();
			}
		}
		if(p.frameCount == 10) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    initFX(fxPanel);
                    movies = new ArrayList<MediaPlayer>();
                    for(int i = 0; i < 1; i++) addNextMovie();
                }
            });

		}
		if(p.frameCount > 13) {
		    BufferedImage bi = new BufferedImage(p.width, p.height, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g = bi.createGraphics();
		    fxPanel.print(g);
		    
		    PImage img = new PImage(bi);
		    p.image(img, 0, 0);
		}
	
		// draw movies
//		if(movies.size() > 0) {
//						
//			int vidW = p.width / MAX_MOVIES;
//			for (int i = 0; i < movies.size(); i++) {
//				if (movies.get(i).available()) movies.get(i).read();
//
//				int vidX = i * vidW;
//				Movie movie = movies.get(i);
//				if(movie.height > 0) {
//					float drawScale = (float)vidW / (float)movie.width;
//					p.image(movie, vidX, 0, movie.width * drawScale, movie.height * drawScale);
//				}
//			}
//		}
	}
	
	

}

package com.haxademic.core.media.video;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.demo.system.Demo_JavaFX_MediaPlayer.Player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import processing.core.PImage;

public class VideoWindowJavaFX 
extends Application {
    
    public interface IVideoDelegate {
        public void videoWindowCreated(VideoWindowJavaFX videoWindow);
        public void videoWindowLoaded(VideoWindowJavaFX videoWindow);
        public void videoWindowBridged(VideoWindowJavaFX videoWindow);
        public void videoWindowClosed(VideoWindowJavaFX videoWindow);
    }

    protected IVideoDelegate delegate;

    protected Stage stage;
    protected Media media;
    protected MediaPlayer player;
    protected MediaView view;
    protected static int W = 640;
    protected static int H = 360;

    public static int numvideoWindows = 0;

    public static void launchPlayer(String url, IVideoDelegate delegate) {
        // build JavaFX videoWindow Application thread
        Runnable videoWindowThread = new Runnable() {
            public void run() {             
                VideoWindowJavaFX newvideoWindow = new VideoWindowJavaFX();
                Stage stage = new Stage();
                stage.setWidth(W);
                stage.setHeight(H);
                newvideoWindow.start(stage);
                newvideoWindow.setDelegate(delegate);
                newvideoWindow.loadURL(url);
            }
        };
        
        // subsequent window launches need to be called slightly differently
        if(numvideoWindows == 0) {
            Platform.startup(videoWindowThread);
        } else {
            Platform.runLater(videoWindowThread);
        }
    }
    
    protected VideoWindowJavaFX setDelegate(IVideoDelegate delegate) {
        this.delegate = delegate;
        if(delegate != null) delegate.videoWindowCreated(this);
        return this;
    }


    
   public void start(Stage stage) {
        // set up stage
        this.stage = stage;
        stage.setTitle("Haxademic | ");
//      stage.initStyle((UNDECORATED) ? StageStyle.UNDECORATED : StageStyle.DECORATED);
        
        // build player
        // note the completely weird file path syntax...
        Player player = new Player("file:/D:/workspace/haxademic/data/haxademic/video/fractal-cube.mp4"); 

        
        // create the browser & embed into Stage/Scene
        Scene scene = new Scene(player, W, H, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
//      stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
    }
   
    public void loadURL(String url) {
        Platform.runLater(() -> {
//          loadVideo(url);
        });
    }
    
    public class Player extends BorderPane 
    {
        Media media;
//        MediaPlayer player;
//        MediaView view;
        Pane mpane;
        public Player(String file) { 
            
            // build media display
            media = new Media(file);
            player = new MediaPlayer(media);
            view = new MediaView(player);

            // add the view - not sure what all this is
            mpane = new Pane();
            mpane.getChildren().add(view); // Calling the function getChildren
            setCenter(mpane);
            
            // scale to fit
            view.setPreserveRatio(true);
            view.fitWidthProperty().bind(stage.widthProperty());
            view.fitHeightProperty().bind(stage.heightProperty());
            
            // media playback
            player.play();  
            player.setAutoPlay(true);// play the video
            player.setCycleCount(MediaPlayer.INDEFINITE);   // make it loop. -1 works as a value too
            
            player.setOnPlaying(new Runnable() {
                public void run() {
                    P.out("Playing");
                }
            });
            player.setOnRepeat(new Runnable() {
                public void run() {
                    P.out("Looped");
                }
            });
            
        }
    }



   ///////////////////////////////////////////////
   // JavaFX Image getter - saves the videoWindow as an image
   ///////////////////////////////////////////////
   
   protected WritableImage img;
   protected BufferedImage bImg;
   protected PImage pimg;
   
   public PImage getImage() {
       Platform.runLater(() -> {
           if(img == null) img = new WritableImage(1280, 720);
           stage.getScene().snapshot(img);
           bImg = SwingFXUtils.fromFXImage(img, bImg);           
       });
       if(pimg == null) {
           pimg = ImageUtil.bufferedToPImage(bImg);
       } else if(bImg != null) {
           ImageUtil.copyBufferedToPImagePixels(bImg, pimg);
       }
       return pimg;
   }
   
   public PImage getImage2() {
       return null;
   }
}
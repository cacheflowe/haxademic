package com.haxademic.demo.system;

import java.awt.image.BufferedImage;

import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import processing.core.PImage;
 
public class Demo_JavaFX_MediaPlayer extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    WritableImage img;
    Stage primaryStage;
    StackPane root;
    
    // Need to add this to the VM arguments!
    // --module-path "D:\workspace\haxademic\lib\processing-4\libraries\javafx\library\windows-amd64\modules" --add-modules=javafx.controls,javafx.media
    
    // info: 
    // - https://www.geeksforgeeks.org/javafx-building-a-media-player/
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("JavaFX Video Test");
        
        // build player
        // note the completely weird file path syntax...
        
        // build scene/window
        root = new StackPane();
        primaryStage.setScene(new Scene(root, 400, 400, Color.BLACK));
        primaryStage.show();

        // add player after root is ready
        Player player = new Player("file:/D:/workspace/haxademic/data/haxademic/video/fractal-cube.mp4"); 
        root.getChildren().add(player);
    }
    
    
    public class Player extends BorderPane 
    {
        Media media;
        MediaPlayer player;
        MediaView view;
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
            view.fitWidthProperty().bind(root.widthProperty());
            view.fitHeightProperty().bind(root.heightProperty());
            
            // media playback
            player.play();              // play the video
            player.setCycleCount(MediaPlayer.INDEFINITE);   // make it loop. -1 works as a value too
            
            player.setOnPlaying(new Runnable() {
                public void run() {
                    // 
                    img = new WritableImage(640, 640);
                    primaryStage.getScene().snapshot(img); // Image image = 
                    BufferedImage bImg = SwingFXUtils.fromFXImage(img, null);
                    
                    // save to disk in a weird roundabout way,
                    // but we're stepping towards hacking into processing
                    PImage pimg = ImageUtil.bufferedToPImage(bImg);
                    pimg.save(FileUtil.haxademicOutputPath() + "test.png");
                    
//                    Pane pane = new Pane();
//                    pane.getChildren().add(image); // Calling the function getChildren
//                    root.getChildren().add(image);
//                    setCenter(pane);
                }
            });
            
        }
    }

}
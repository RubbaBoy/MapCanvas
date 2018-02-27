package com.uddernetworks.videoplayer.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;

public class Standalone extends Application {

    private int index = 0;

    @Override
    public void start(Stage primaryStage) {

        Platform.setImplicitExit(false);

        Media pick = new Media(new File("E:\\VideoPlayer\\video\\rasputin.mp4").toURI().toString()); // replace this with your own audio file
        MediaPlayer player = new MediaPlayer(pick);


        // Add a mediaView, to display the media. Its necessary !
        // This mediaView is added to a Pane
        MediaView mediaView = new MediaView(player);

//        Group root = new Group(mediaView);
//        Scene scene = new Scene(root, 1280, 720);

        player.setOnReady(() -> {
            System.out.println("11111");
        });

        player.setOnStalled(() -> {
            System.out.println("222");
        });

        player.setOnPaused(() -> {
            System.out.println("333");
        });

        System.out.println("Ready");

//        new Thread(() -> {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            captureScreenshot(player, mediaView);
//        }).start();

        index++;
    }

    private double lastTime = -1;

    private void captureScreenshot(MediaPlayer player, MediaView mediaView) {
        System.out.println("Standalone.captureScreenshot");
        new Thread(() -> {
            Platform.runLater(() -> {
//                System.out.println(player.getCurrentTime());

                WritableImage writableImage = mediaView.snapshot(new SnapshotParameters(), null);

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", new File("E:\\VideoPlayer\\output\\image" + index + ".png"));
                } catch (Exception s) {
                    s.printStackTrace();
                }

                index++;

                player.seek(player.getCurrentTime().add(Duration.millis(40)));

//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                double currentTime = player.getCurrentTime().toSeconds();
//
//                System.out.println("lastTime = " + lastTime);
//                System.out.println("currentTime = " + currentTime);
//
//                if (lastTime == currentTime) {
//                    System.out.println("Ended");
//                    System.exit(0);
//                } else {
//                    lastTime = currentTime;
                    captureScreenshot(player, mediaView);
//                }
            });
        }).start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

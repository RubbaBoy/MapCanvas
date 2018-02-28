package com.uddernetworks.videoplayer.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class Standalone extends Application {

    private int index = 5878; // START AT 3137 BECAUSE IT LEFT OFF AT THERE
    private TestPane testPane;

    @Override
    public void start(Stage primaryStage) {

        Platform.setImplicitExit(false);

        Media pick = new Media(new File("E:\\VideoPlayer\\video\\rasputin.mp4").toURI().toString()); // replace this with your own audio file
        MediaPlayer player = new MediaPlayer(pick);


        // Add a mediaView, to display the media. Its necessary !
        // This mediaView is added to a Pane
        MediaView mediaView = new MediaView(player);

        Group root = new Group(mediaView);
        Scene scene = new Scene(root, 1280, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
        player.play();

        player.setAudioSpectrumNumBands(20);

        // double timestamp,    double duration,    float[] magnitudes,     float[] phases
        player.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            System.out.println("magnitudes = " + Arrays.toString(magnitudes));
            printMagnitudes(magnitudes);
        });

        JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(testPane = new TestPane());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        testPane.setMagnitudes(new float[] {
                -50, -40, -30, -20, -10, -20, -30, -40, -50
        });


//        player.setStartTime(Duration.millis(40 * 5878));

//        new Thread(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            Platform.runLater(() -> {
//                captureScreenshot(player, mediaView);
//            });
//        }).start();

        index++;
    }

    private void printMagnitudes(float[] magnitudes) {
        testPane.setMagnitudes(magnitudes);
    }

    private double lastTime = -1;

    private void captureScreenshot(MediaPlayer player, MediaView mediaView) {
        System.out.println("Standalone.captureScreenshot THREAD = " + Thread.currentThread().getName());

//            Platform.runLater(() -> {
//                System.out.println(player.getCurrentTime());

        WritableImage writableImage = mediaView.snapshot(new SnapshotParameters(), null);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", new File("E:\\VideoPlayer\\output\\image" + index + ".png"));
        } catch (Exception s) {
            s.printStackTrace();
        }

        index++;

        player.seek(player.getCurrentTime().add(Duration.millis(40)));

        Platform.runLater(() -> {
            captureScreenshot(player, mediaView);
        });


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
//                }
//            });

    }


    public static void main(String[] args) {
        launch(args);
    }


    public class TestPane extends JPanel {

        private float[] magnitudes = new float[0];

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 200);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.GREEN);

            float last = 0;
            int lastX = 0;
            int x = 0;

            for (float magnitude : magnitudes) {
                magnitude += 60;
//                magnitude -= 200;

                graphics.fillRect(x, 0, 15, (int) magnitude);

//                graphics.drawLine(lastX, (int) last, x, (int) magnitude);

                last = magnitude;
                lastX = x;
                x += 20;
            }
        }

        public float[] getMagnitudes() {
            return magnitudes;
        }

        public void setMagnitudes(float[] magnitudes) {
            this.magnitudes = magnitudes;
            repaint();
        }
    }
}

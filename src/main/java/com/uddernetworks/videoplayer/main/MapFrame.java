package com.uddernetworks.videoplayer.main;

import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFrame {

    private VideoPlayer videoPlayer;
    private List<List<byte[]>> maps;
    private List<byte[]> mapsSingle;
    private int width;
    private int height;

    public MapFrame(VideoPlayer videoPlayer, int width, int height) {
        this.videoPlayer = videoPlayer;
        this.maps = new ArrayList<>();
        this.mapsSingle = new ArrayList<>();
        this.width = width;
        this.height = height;
    }

    public void register(BufferedImage image) throws IOException {
        BufferedImage scaledImage = resizeImage(image, 512, 384);

        for (int imageY = 0; imageY < 3; imageY++) {
            List<byte[]> row = new ArrayList<>();
            for (int imageX = 0; imageX < 4; imageX++) {
//                AffineTransformOp transformOp = new AffineTransformOp(AffineTransform.getRotateInstance(90), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                BufferedImage subImage = rotateClockwise90(scaledImage.getSubimage(imageX * 128, imageY * 128, 128, 128));
//                BufferedImage subImage = null;
//                transformOp.filter(preSubImage, subImage);

//                ImageIO.write(subImage, "png", new File(videoPlayer.getDataFolder(), "frames\\sub_" + imageX + "_" + imageY + ".png"));

                byte[] colors = new byte[16384];
                byte[] colorCorrected = MapPalette.imageToBytes(subImage);


                for(int x2 = 127; x2 >= 0; x2--) {
                    for(int y2 = 0; y2 < 128; y2++) {
                        colors[x2 * 128 + y2] = colorCorrected[(127 - x2) * 128 + y2]; // Clockwise 90 degreese
                    }
                }

//                ImageIO.write(subImage, "png", new File(videoPlayer.getDataFolder(), "frames\\MODDEDsub_" + imageX + "_" + imageY + ".png"));

                row.add(colors);

                mapsSingle.add(colors);
            }

            maps.add(row);
        }
    }

    public static BufferedImage rotateClockwise90(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(h, w, src.getType());
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                dest.setRGB(y,x,src.getRGB(x,y));
        return dest;
    }

    public byte[] getForMap(int mapIndex) {
        int row = mapIndex / 4;
        int column = mapIndex % 4;

        System.out.println("index " + mapIndex + " goes to: (" + column + ", " + row + ")");

//        return this.maps.get(row).get(column);
        return this.mapsSingle.get(mapIndex);
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }
}

package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvas;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An object that can draw an image by the given URL/File.
 */
public class Image extends Clickable implements MapObject {
    private MapCanvas mapCanvas;
    private AtomicReference<BufferedImage> bufferedImage = new AtomicReference<>();
    private int x;
    private int y;
    private int width;
    private int height;
    private ObjectBounds objectBounds;
    private AtomicBoolean imageLoaded = new AtomicBoolean(false);
    private AtomicBoolean tryingToDraw = new AtomicBoolean(false);

    /**
     * Created the Image object with the image being the specified local File.
     * @param file The local image to be drawn to screen
     * @param x The X position from the bottom left corner of the image it should be drawn from
     * @param y The Y position from the bottom left corner of the image it should be drawn from
     * @param width The width of the image that should be drawn to the canvas. The image fetched
     *              will be scaled to this width
     * @param height The height of the image that should be drawn to the canvas. The image fetched
     *              will be scaled to this height
     */
    public Image(File file, int x, int y, int width, int height) {
        this(x, y, width, height);

        setImage(file);
    }

    /**
     * Created the Image object with the image being downloaded from the URL. Image downloads are
     * all done async to prevent main thread blocking. If the {@link Image#draw(MapCanvas)} method is called
     * before the image has been downloaded, it will be drawn as soon as the image has finished downloading.
     * @param url The String URL of the image that will be downloaded and put on the canvas
     * @param x The X position from the bottom left corner of the image where it should be drawn
     * @param y The Y position from the bottom left corner of the image where it should be drawn
     * @param width The width of the image that should be drawn to the canvas. The image fetched
     *              will be scaled to this width
     * @param height The height of the image that should be drawn to the canvas. The image fetched
     *              will be scaled to this height
     */
    public Image(String url, int x, int y, int width, int height) {
        this(x, y, width, height);

        setImage(url);
    }

    Image(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.objectBounds = new ObjectBounds(x, y, x + width, y + height);
    }

    void setImage(File file) {
        new Thread(() -> {
            try {
                this.bufferedImage.set(resizeImage(ImageIO.read(file), width, height));
                this.imageLoaded.set(true);

                if (this.tryingToDraw.get()) {
                    this.draw(mapCanvas);
                    mapCanvas.updateMaps();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    void setImage(String url) {
        new Thread(() -> {
            try {
                this.bufferedImage.set(resizeImage(ImageIO.read(new URL(url)), width, height));
                this.imageLoaded.set(true);

                if (this.tryingToDraw.get()) {
                    this.draw(mapCanvas);
                    mapCanvas.updateMaps();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.x = mapCanvas.migrateX(this.x);
        this.y = mapCanvas.migrateY(this.y + this.height);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        this.mapCanvas = mapCanvas;
        if (!this.imageLoaded.get()) {
            tryingToDraw.set(true);
            return;
        }

        BufferedImage image = this.bufferedImage.get();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int imageX = x + this.x;
                int imageY = y + this.y;

                Color imageColor = new Color(image.getRGB(x, y), true);

                if (imageColor.getAlpha() == 255) {
                    mapCanvas.setPixel(imageX, imageY, MapPalette.matchColor(imageColor));
                }
            }
        }
    }

    @Override
    public ObjectBounds getBounds() {
        return objectBounds;
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image.getWidth() == width && image.getHeight() == height) return image;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    /**
     * Gets the X coordinate of the current Image object.
     * @return The X coordinate of the current Image object
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the X coordinate of the current Image object. Changes will only
     * be visible when the {@link #draw(MapCanvas)} method is invoked.
     * @param x The X coordinate of the current Image object
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the Y coordinate of the current Image object.
     * @return The Y coordinate of the current Image object
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of the current Image object. Changes will only
     * be visible when the {@link #draw(MapCanvas)} method is invoked.
     * @param y The Y coordinate of the current Image object
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the width of the current Image object.
     * @return The width of the current Image object
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the current Image object. Changes will only
     * be visible when the {@link #draw(MapCanvas)} method is invoked.
     * @param width The width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the height of the current Image object.
     * @return The height of the current Image object
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the current Image object. Changes will only
     * be visible when the {@link #draw(MapCanvas)} method is invoked.
     * @param height The height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets if the image has been loaded already.
     * @return If the image has been loaded already
     */
    public boolean imageLoaded() {
        return this.imageLoaded.get();
    }
}

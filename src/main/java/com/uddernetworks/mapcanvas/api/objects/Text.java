package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvas;
import org.bukkit.map.MapFont;

import java.util.logging.Level;

/**
 * Makes text with the given MapFont.
 */
public class Text extends Clickable implements MapObject {

    private int x;
    private int y;
    private boolean centeredX;
    private boolean centeredY;
    private MapFont mapFont;
    private String text;
    private byte color;
    private ObjectBounds objectBounds;

    /**
     * Created text that will be drawn at the specified coordinates with the given color
     * using the given MapFont.
     * @param x The X position from the bottom left corner of the text where it should be drawn
     * @param y The Y position from the bottom left corner of the text where it should be drawn
     * @param mapFont The MapFont to use to render text
     * @param color The byte color the text should be drawn in
     * @param text The text that should be drawn to the map canvas
     */
    public Text(int x, int y, MapFont mapFont, byte color, String text) {
        this(x, y, false, false, mapFont, color, text);
    }

    /**
     * Created text that will be drawn at the specified coordinates with the given color
     * using the given MapFont.
     * @param x The X position from the bottom left corner of the text where it should be drawn
     * @param y The Y position from the bottom left corner of the text where it should be drawn
     * @param centeredX If the text should be centered on the X axis
     * @param centeredY If the text should be centered on the Y axis
     * @param mapFont The MapFont to use to render text
     * @param color The byte color the text should be drawn in
     * @param text The text that should be drawn to the map canvas
     */
    public Text(int x, int y, boolean centeredX, boolean centeredY, MapFont mapFont, byte color, String text) {
        this.x = x;
        this.y = y;
        this.centeredX = centeredX;
        this.centeredY = centeredY;
        this.mapFont = mapFont;
        this.text = text;
        this.color = color;
        this.objectBounds = new ObjectBounds(x, y, 0, 0);
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.y = mapCanvas.migrateY(this.y + this.mapFont.getHeight());
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        int x = this.x;
        int y = this.y;

        int xStart = x;

        if (!this.mapFont.isValid(this.text)) {
            mapCanvas.getPlugin().getLogger().log(Level.WARNING, "Text contains invalid characters.");
            return;
        }

        if (centeredX) {
            int width = getWidth(mapCanvas);
            xStart -= width / 2;
            x = xStart;
        }

        if (centeredY) {
            int height = getHeight(mapCanvas);
            y += height / 2;
        }

        int extra = this.mapFont.getHeight() / 8;

        for (int i = 0; i < this.text.length(); ++i) {
            char ch = this.text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y += this.mapFont.getHeight() + extra;
            } else {
                if (ch == 167) {
                    int j = this.text.indexOf(59, i);
                    if (j >= 0) {
                        try {
                            color = Byte.parseByte(this.text.substring(i + 1, j));
                            i = j;
                            continue;
                        } catch (NumberFormatException ignored) {}
                    }
                }

                MapFont.CharacterSprite sprite = this.mapFont.getChar(this.text.charAt(i));

                for (int r = 0; r < this.mapFont.getHeight(); ++r) {
                    for (int c = 0; c < sprite.getWidth(); ++c) {
                        if (sprite.get(r, c)) {
                            mapCanvas.setPixel(x + c, y + r, this.color);
                        }
                    }
                }

                x += sprite.getWidth() + extra + 1;
            }
        }

        this.objectBounds = new ObjectBounds(this.x, this.y, x, y);
    }

    private int getWidth(MapCanvas mapCanvas) {
        int maxWidth = 0;
        int tempMaxWidth = 0;

        if (!this.mapFont.isValid(this.text)) {
            mapCanvas.getPlugin().getLogger().log(Level.WARNING, "Text contains invalid characters.");
            return 0;
        }

        int extra = this.mapFont.getHeight() / 8;

        for (int i = 0; i < this.text.length(); ++i) {
            char ch = this.text.charAt(i);
            if (ch == '\n') {
                maxWidth = Math.max(tempMaxWidth, maxWidth);
                tempMaxWidth = 0;
            } else {
                MapFont.CharacterSprite sprite = this.mapFont.getChar(this.text.charAt(i));

                tempMaxWidth += sprite.getWidth() + extra + 1;
            }
        }

        return Math.max(maxWidth, tempMaxWidth);
    }

    private int getHeight(MapCanvas mapCanvas) {
        int lines = 1;

        if (!this.mapFont.isValid(this.text)) {
            mapCanvas.getPlugin().getLogger().log(Level.WARNING, "Text contains invalid characters.");
            return 0;
        }

        for (int i = 0; i < this.text.length(); ++i) {
            char ch = this.text.charAt(i);
            if (ch == '\n') {
                lines++;
            }
        }

        return lines * this.mapFont.getHeight();
    }

    @Override
    public ObjectBounds getBounds() {
        return objectBounds;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isCenteredX() {
        return centeredX;
    }

    public void setCenteredX(boolean centeredX) {
        this.centeredX = centeredX;
    }

    public boolean isCenteredY() {
        return centeredY;
    }

    public void setCenteredY(boolean centeredY) {
        this.centeredY = centeredY;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

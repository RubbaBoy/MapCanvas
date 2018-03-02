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
        this.x = x;
        this.y = y;
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

    @Override
    public ObjectBounds getBounds() {
        return objectBounds;
    }
}

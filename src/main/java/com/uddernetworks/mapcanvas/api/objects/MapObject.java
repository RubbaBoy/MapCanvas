package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvas;

/**
 * An item that can be rendered to a MapCanvas.
 */
public interface MapObject {
    /**
     * Ran once total, when the object is about to be drawn for the first time.
     * This method should not be invoked manually.
     * @param mapCanvas The MapCanvas the object is being drawn to
     */
    void initialize(MapCanvas mapCanvas);

    /**
     * Ran every time the whole MapCanvas is drawn, or manually when the current
     * object should be updated. If ran manually, the {@link MapCanvas#updateMaps()}
     * method should be invoked, to make updates of the maps be sent to the player.
     * @param mapCanvas The MapCanvas the object is being drawn to
     */
    void draw(MapCanvas mapCanvas);
}

package com.uddernetworks.mapcanvas.api.objects;

/**
 * Lets a {@link MapObject} be clickable, and calls the set ObjectClick
 * when the object is clicked.
 */
public abstract class Clickable {

    private ObjectClick objectClick;

    public Clickable() {}

    /**
     * Sets the current Clickable object to use the given ObjectClick.
     * @param objectClick The ObjectClick that should be used by the current object
     */
    public void setClick(ObjectClick objectClick) {
        this.objectClick = objectClick;
    }

    /**
     * Gets the ObjectClick used by the current object.
     * @return The ObjectClick used by the current object
     */
    public ObjectClick getClick() {
        return this.objectClick;
    }

    /**
     * Gets the bounds used by the current object.
     * @return The bounds used by the current object.
     */
    public abstract ObjectBounds getBounds();
}

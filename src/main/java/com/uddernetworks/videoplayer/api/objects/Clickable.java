package com.uddernetworks.videoplayer.api.objects;

public abstract class Clickable {

    private ObjectClick objectClick;

    public Clickable() {}

    public Clickable(ObjectClick objectClick) {
        this.objectClick = objectClick;
    }

    public void setClick(ObjectClick objectClick) {
        this.objectClick = objectClick;
    }

    public ObjectClick getClick() {
        return this.objectClick;
    }

    public abstract ObjectBounds getBounds();
}

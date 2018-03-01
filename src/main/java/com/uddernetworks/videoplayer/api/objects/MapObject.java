package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvas;

public interface MapObject {
    void initialize(MapCanvas mapCanvas);
    void draw(MapCanvas mapCanvas);
}

package com.uddernetworks.videoplayer.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapCanvasManager {

    private List<MapCanvas> mapCanvasList = new ArrayList<>();

    public MapCanvasManager() {

    }

    public List<MapCanvas> getViewedBy(UUID player) {
        return this.mapCanvasList.stream().filter(mapCanvas -> mapCanvas.getViewers().isEmpty() || mapCanvas.getViewers().contains(player)).collect(Collectors.toList());
    }

    public void addCanvas(MapCanvas mapCanvas) {
        this.mapCanvasList.add(mapCanvas);
    }
}

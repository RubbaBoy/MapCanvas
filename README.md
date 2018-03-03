# MapCanvas

MapCanvas is an API for drawing to walls of maps in Minecraft as a canvas. The API provides several native different things to draw to the canvas, with very easy support for adding new objects.

# Features
- The entire system uses only packets, providing per player canvases
- With packets, walls of maps can be updated faster than 20 times per second, and doesn't throttle world IO while saving
- Click events provide extremely easy click detection from far away
- Several default objects to draw to the canvas, such as:
  - Circles
  - Lines
  - Rectangles
  - Images from URL or local file (All non blocking requests)
  - Easy rendering of items' 2D textures to canvases
  - Text in the default Minecraft font
- Easy support for custom objects
- Caching of maps, so updating only sends changed maps to players

## Usage
_Note: This may override some maps in your world as long as the player doesn't log off. Because nothing in the APi changes the world, a relog will reset all maps to normal._

Everything shown here and more examples in a working plugin can be found in the project https://github.com/RubbaBoy/MapCanvasExample. JavaDocs with every method and class in the API may be found [here](https://rubbaboy.me/docs/mapcanvas/).

## Gradle
This API is on my nexus, which can be fetched by making your `build.gradle` similar to:

```
repositories {
    maven {
        url 'http://nexus.rubbaboy.me:85/repository/maven-public/'
    }
}

dependencies {
    compileOnly 'com.uddernetworks.mapcanvas:MapCanvas:1.0-SNAPSHOT'
}
```
Then you will want to depend on the MapCanvasAPI plugin, which can be found in the Spigot link at the top, or in the [releases](https://github.com/RubbaBoy/MapCanvasExample/releases) section of this GitHub repository.

## Hooking Into The API
For creating MapCanvases, you need to get an instance of the [MapCanvasAPI](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/MapCanvasAPI.html). This can be done simply by checking if the API plugin is enabled, if so, getting the instance of the plugin and saving it, in a method like the following:
```Java
private MapCanvasAPI mapCanvasAPI;

@Override
public void onEnable() {
    initializeCanvasAPI();
}

private void initializeCanvasAPI() {
    if (Bukkit.getPluginManager().isPluginEnabled("MapCanvasAPI")) {
        this.mapCanvasAPI = (MapCanvasAPI) Bukkit.getPluginManager().getPlugin("MapCanvasAPI");
        return;
    }
    getLogger().severe("MapCanvasAPI not found!");
    this.getPluginLoader().disablePlugin(this);
}
```
This method should probably only be only ran in the `onEnable` method, but nothing will break if ran more. 

## Creating A Map Wall

The first thing in making a Map Canvas is creating a map wall, if one hasn't been created already. The map wall starts at a certain ID at the top left, and goes from left to right placing blocks and Item Frames. The MapCanvas API provides a very easy way to create map walls.
First, create a new [MapWall](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/MapWall.html) object with the starting location, the [axis](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/WallDirection.html) the wall should be created in, the [BlockFace](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html) the maps should be placed on (It will not allow BlockFaces making ItemFrames going into the blocks), the [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) the wall should be made of, the width/height in blocks the wall should be, and finally the starting ID of the maps.
```Java
new MapWall(player.getLocation(), WallDirection.Z_AXIS, BlockFace.EAST, Material.WOOL, 20, 12 (short) 0);
```
This code will spawn a MapWall at the player `player`'s location, on the X axis, with the maps on the EAST side. The map wall is 20 blocks wide and 12 tall on wool, with the maps starting with the ID of 0.

## Creating a MapCanvas

Creating a [MapCanvas](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/MapCanvas.html) is extremely simple. The first step is to create a `MapCanvas` object, which takes the current `JavaPlugin`, the `MapCanvasAPI` instance retrieved earlier, the axis and map `BlockFace` (Which should be the same as in the wall creation), the width and height in blocks of the wall, and the first map ID used.
```Java
MapCanvas mapCanvas = new MapCanvas(this, mapCanvasAPI, WallDirection.Z_AXIS, BlockFace.EAST, 20, 12, 0);
```
The code block above matches the `MapWall` created in the section above.

## Adding Objects

All Objects you add onto a Map are [MapObject](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/objects/MapObject.html)s. In this example we will be adding a green [Rectangle](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/objects/Rectangle.html) to the canvas, for demonstration purposes. The `Rectangle` takes the lower left x and y pixel coordinates, along with the width and height in pixels for drawing the rectangle. Then, it takes a byte for the outer line color (-1 to disable line coloring), and the inner color. The byte color can be gotten from the Bukkit method [MapPalette.matchColor()](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/map/MapPalette.html#matchColor-java.awt.Color-) method.

```Java
Rectangle rectangle = new Rectangle(256, 0, 1024, 1024, (byte) -1, MapPalette.matchColor(Color.GREEN));
```
This code will draw a green rectangle 256 pixels left from the bottom left of the canvas, 1024x1024 pixels big. The rectangle doesn't have an outer line specific color, so it uses the next color, which is the Minecraft Map color closest to Java's `Color.GREEN`.

After you have created an Object, you must add it to the `MapCanvas`. This can be done by simply doing
```Java
mapCanvas.addObject(rectangle);
```

After you have added your Objects, you must intialize the `MapCanvas`, which invokes the [MapObject#initialize()](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/objects/MapObject.html#initialize-com.uddernetworks.mapcanvas.api.MapCanvas-) method on all added objects. This method should only be ran **once**.
```Java
mapCanvas.initialize(player.getWorld());
```
The code above initializes the MapCanvas in the world  the player is in, where the `MapWall` is.

After the `MapCanvas` has been initialized, you must paint everything to it, and send the packets to the players. This uses the [MapCanvas#paint()](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/MapCanvas.html#paint--) method, which invokes the [MapObject#draw()](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/objects/MapObject.html#draw-com.uddernetworks.mapcanvas.api.MapCanvas-) method on all `MapObject`s added to the canvas.
```Java
mapCanvas.paint();
```
Now the whole drawing process has been completed, this is what it looks like in Minecraft:
![Example Canvas](https://rubbaboy.me/images/181cp0c)


## Click Events

### General Click Event

The MapCanvas API provides a simple Event that can be listened to like a normal Bukkit event. This event is the [ClickMapEvent](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/event/ClickMapEvent.html), and it is called when you click and there is a `MapCanvas` on your cursor. The event gives you access to the coordinates of where you clicked (Relative to the canvas), how you clicked in the [ClickMapAction](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/event/ClickMapAction.html) enum, the `MapCanvas` you clicked, and some more data. An example of this event is shown below.

```Java
@EventHandler
public void onClickMapEvent(ClickMapEvent event) {
    // Gets the MapCanvas the user clicked
    MapCanvas mapCanvas = event.getMapCanvas();
    
    // Tells the player where they clicked, along with the MapCanvas's UUID
    event.getPlayer().sendMessage("Clicked at (" + event.getX() + ", " + event.getY() + ") on map: " + mapCanvas.getUUID());
    
    // Creates a Circle object, with an inner radius of 50 pixels, with a thickness of 20 red pixels
    Circle circle = new Circle(event.getX(), event.getY(), 50, 70, MapPalette.matchColor(Color.RED));
    
    // Adds the object to the clicked canvas
    mapCanvas.addObject(circle);
    
    // Draws the circle to the canvas
    circle.draw(mapCanvas);
    
    // Updates maps. This only updates the maps changed, to prevent unnecessary packets being sent 
    mapCanvas.updateMaps();
}
```
The code above draws a red circle wherever the player clicks, and tells the user where they clicked.

This is what it looks like in practice:

https://rubbaboy.me/images/163t6dd.gif

(Sorry, GitHub doesn't like long gifs)

### Per Object Click Events

The MapCanvas API also provides per-element click detection, for things like buttons. All you need to do is invoke the method [Clickable#setClick()](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/objects/Clickable.html#setClick-com.uddernetworks.mapcanvas.api.objects.ObjectClick-) on the element. Note: This only works on elements that extend [Clickable](https://rubbaboy.me/docs/mapcanvas/com/uddernetworks/mapcanvas/api/objects/Clickable.html).

For adding click detection to the `Rectangle` object previously created, this is what the code should look like:
```Java
rectangle.setClick((clickingPlayer, action, mapCanvasSection, x, y) -> {
    clickingPlayer.sendMessage(ChatColor.GREEN + "You clicked the green rectangle, action = " + action + " specifically at (" + x + ", " + y + ")");
});
```
This makes it so when the rectangle is clicked, it sends the clicker a message saying where they clicked and how they clicked. This is what it looks like in practice:

https://rubbaboy.me/images/2olmgtc.gif

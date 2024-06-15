package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;



/**
 * The TileManager class manages tiles for a map by providing methods to access, download, and cache the tiles.
 * <p>
 * It utilizes an LRU cache to limit the number of tiles loaded into memory.
 * <p>
 * The class also provides a TileId record for easy identification of tile properties.
 * The TileManager can be used to get an image for a given tile ID by searching in memory cache, disk cache, or downloading it with a web request.
 *
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */

public final class TileManager {
    private static final int CACHE_THRESHOLD = 100;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Path path;
    private final String serverName;
    private final LinkedHashMap<TileId, Image> cacheMemory;


    /**
     * A record containing tile properties such as the zoom level and coordinates.
     * It provides methods to determine the validity of a tile and to get the partial path of the tile or its nearest directory.
     *
     * @param zoom (int) the zoom level of the tile
     * @param x (int) the tile coordinate on the horizontal axis
     * @param y (int) the tile coordinate on the vertical axis
     */
     public record TileId(int zoom, int x, int y) {

        /**
         * Checks if the tile coordinates are valid for a given zoom level.
         *
         * @param zoom (int) the zoom level of the tile
         * @param x (int) the tile coordinate on the horizontal axis
         * @param y (int) the tile coordinate on the vertical axis
         * @return true if the tile coordinates are within the bounds of the zoom level
         */
        public static boolean isValid(int zoom, int x, int y){
            int indexUpperBoundExcluded = 1 << zoom;
            return 0<=x && x<indexUpperBoundExcluded
                    && 0<=y && y<indexUpperBoundExcluded;
        }


        /**
         * @return (String) partial path of the nearest directory of the tile
         */
         public String getDirectory() {
             return zoom + "/" + x;
         }

        /**
         * @return (String) partial path of the tile, including the file extension
         */
        @Override
         public String toString() {
             return new StringBuilder()
                     .append(zoom)
                     .append("/").append(x)
                     .append("/").append(y)
                     .append(".png")
                     .toString();
         }
     }

    /**
     * TileManager's public constructor
     *
     * @param path (Path) path to the directory where the tiles will be stored
     * @param serverName (String) the name of the server hosting the tiles
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
        cacheMemory = new LinkedHashMap<>(CACHE_THRESHOLD, DEFAULT_LOAD_FACTOR, true);
    }

    /**
     * given a tileId we will search for the image in this order
     * in memory cache else in disk cache else we download it with a web request
     *
     * @param tileId (TileId) the ID of the tile to get the image for
     * @return (Image) the image corresponding to the tile ID
     * @throws IOException if an error occurs during image retrieval or caching
     */
    public Image imageForTileAt(TileId tileId) throws IOException {


        //Image existant in Memory cache
        if(cacheMemory.containsKey(tileId)) return cacheMemory.get(tileId);


        Path tilePath = path.resolve(tileId.toString());
        //Image found in Disk cache
        if(Files.exists(tilePath)) {

            Image image =  new Image(new FileInputStream(tilePath.toString()));
            addToCacheMemory(tileId, image);
            return image;
        }
        Files.createDirectories(path.resolve(tileId.getDirectory()));

        //Image is not in the Memory cache nether in Disk cache
        URL u = new URL("https://"+serverName+"/"+tileId);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        try(InputStream i = c.getInputStream();
            OutputStream o = new FileOutputStream(tilePath.toString())){
            byte[] imageBytes = i.readAllBytes();
            o.write(imageBytes);
            ByteArrayInputStream imageInputStream = new ByteArrayInputStream(imageBytes);
            Image tileImage = new Image(imageInputStream);
            addToCacheMemory(tileId, tileImage);
            return tileImage;
        }
    }

    /**
     * Adds the given tile ID and image to the in-memory cache, and evicts the least recently used
     * entry if the cache has reached its maximum size.
     *
     * @param tileId (TileId) the ID of the tile to cache the image for
     * @param image (Image) the image to cache
     */
    private void addToCacheMemory(TileId tileId, Image image){
        if(cacheMemory.size() == CACHE_THRESHOLD) cacheMemory.remove(cacheMemory.keySet().iterator().next());
        cacheMemory.put(tileId, image);
    }
}

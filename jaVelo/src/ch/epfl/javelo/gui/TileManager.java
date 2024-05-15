package ch.epfl.javelo.gui;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe représente un gestionnaire de tuiles OSM. 
 * Son rôle est d'obtenir les tuiles depuis un serveur de tuile et de les stocker dans un cache mémoire et dans un cache disque.
 */

public final class TileManager {

    private final Map <TileId, Image> cacheMemoire = new LinkedHashMap<>(140, 75); // représente notre cache mémoire qui va stocker les tuiles.
    private final Path basePath;
    private final String serveurTuile;

    /**
     *
     * @param basePath
     *             représente le chemin d'accès au répertoire contenant le cache disque.
     * @param serveurTuile
     *             représente le nom du serveur de tuile, dans la plupart des cas : tile.openstreetmap.org.
     */

    public TileManager (Path basePath, String serveurTuile) {
        this.basePath = basePath;
        this.serveurTuile = serveurTuile;
    }

    /**
     *
     * @param tileId
     *            représente l'identité de la tuile à utiliser.
     * @return
     *            retourne l'image de la tuile d'identité tileId.
     * @throws IOException
     *            lève IOException en cas d'erreur d'entrée/sortie.
     *
     * Cette méthode cherche tout d'abord l'image dans le cache mémoire et la retourne. (1)
     * Si l'image n'existe pas dans le cache mémoire, elle est cherchée dans le cache disque, placée dans le cache mémoire et retournée. (2)
     * Sinon, elle est obtenue depuis le serveur de tuiles, placée dans le cache disque et puis dans le cache mémoire et retournée. (3)
     */

    public Image imageForTileAt (TileId tileId) throws IOException {

        int MAX_CAPACITY = 100;
        Image image;
        Path path = basePath.resolve(tileId.zoomLevel() + "/" + tileId.x() + "/" + tileId.y() + ".png");

        if (cacheMemoire.containsKey(tileId)) { // (1)
            return cacheMemoire.get(tileId);
        } else if (Files.exists(path)) { // (2)
            try (InputStream in = new FileInputStream(path.toString())) {
                image = new Image(in);
                if (cacheMemoire.size()==MAX_CAPACITY) cacheMemoire.remove(cacheMemoire.keySet().iterator().next()); //Si le cache mémoire est plein, il faut donc supprimer l'image utilisée le moins récemment.
                cacheMemoire.put(tileId, image);
                return image;
            }
        } else { // (3)
                URL u = new URL("https://" + serveurTuile + "/" + tileId.zoomLevel() + "/" + tileId.x() + "/" + tileId.y() + ".png");
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                Files.createDirectories(basePath.resolve(tileId.zoomLevel() + "/" + tileId.x()));
                try (InputStream i = c.getInputStream()) {
                    OutputStream o = new FileOutputStream(path.toString());
                    i.transferTo(o);
                }
                try (InputStream inputStream = new FileInputStream(path.toString())) {
                    image = new Image(inputStream);
                    if (cacheMemoire.size()==MAX_CAPACITY) cacheMemoire.remove(cacheMemoire.keySet().iterator().next()); //Si le cache mémoire est plein, il faut donc supprimer l'image utilisée le moins récemment.
                    cacheMemoire.put(tileId, image);
                    return image;
                }
        }
    }
    
    /**
     * L'enregistrement imbriqué, TileId, représente l'identité d'une tuile OSM.
     */

    public record TileId(int zoomLevel, int x, int y) {

        /**
         *
         * @param zoomLevel
         *             représente le niveau de zoom de la tuile.
         * @param x
         *             représente l'index X de la tuile.
         * @param y
         *             représente l'index Y de la tuile.
         * @throws IllegalArgumentException
         *             lève une exception si les paramètres passés à TileId ne sont pas valides.
         */

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, x, y));
        }
        
        /**
         * 
         * @param zoom
         *          représente le niveau de zoom de la tuile.
         * @param indexX
         *          représente l'index X de la tuile.
         * @param indexY
         *          représente l'index Y de la tuile.
         * @return
         *          retourne vrai ssi ces paramètres constituent une identité de tuile valide.
         */
                
        public static boolean isValid(int zoom, double indexX, double indexY) {

            double maxIndex = Math.pow(2, zoom) - 1; //Ce variable représente l'index maximum que doit avoir x ou y en fonction du zoom de la carte.

            return zoom >= 0 && indexX >= 0 && indexY >= 0 && indexX <= maxIndex && indexY <= maxIndex;
        }
    }
}

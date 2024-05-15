package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.io.IOException;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe gère l'affichage et l'interaction avec le fond de carte.
 */

public final class BaseMapManager {

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final WaypointsManager waypointsManager;
    private final Canvas canvas = new Canvas();
    private final Pane pane = new Pane(canvas);
    private final int ZOOM_FACTOR = 2;
    private boolean redrawNeeded; //ne sera vrai que si un redessin de la carte est nécessaire.

    /**
     *
     * @param tileManager
     *                    représente le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte.
     * @param waypointsManager
     *                    représente le gestionnaire des points de passage.
     * @param mapViewParameters
     *                    représente une propriété JavaFX contenant les paramètres de la carte affichée.
     */

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapViewParameters) {

        this.tileManager = tileManager;
        this.mapViewParameters = mapViewParameters;
        this.waypointsManager = waypointsManager;

        canvas.widthProperty().bind(pane.widthProperty()); //Le canvas et le pane doivent avoir la meme largeur.
        canvas.heightProperty().bind(pane.heightProperty()); //Le canvas et le pane doivent avoir la meme longeur.
        canvas.widthProperty().addListener(o -> redrawOnNextPulse()); //Si la largeur du canvas change, un redessin est demandé.
        canvas.heightProperty().addListener(o -> redrawOnNextPulse()); //Si la longeur du canvas change, un redessin est demandé.

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        scroll(); //changement du niveau de zoom
        slide(); //glissement de la carte
        addPoint(); //ajout de points de passage
    }

    /**
     * Cette méthode ajoute un point de passage si on clique sur la carte.
     */

    private void addPoint () {
        pane.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) waypointsManager.addWaypoint(event.getX(), event.getY());
        });
    }

    /**
     * Cette méthode "Slide" représente le glissement de la carte. Elle est appelée dans le constructeur.
     */

    private void slide() {

        pane.setOnMousePressed(event -> {
            ObjectProperty<Point2D> point = new SimpleObjectProperty<>(new Point2D(event.getX(), event.getY())); //Ce point est une propriété pour que je puisse l'utiliser dans la lambda.
            pane.setOnMouseDragged(event2 -> {
                Point2D newPoint = new Point2D(mapViewParameters.get().x() + (point.get().getX()-event2.getX()), mapViewParameters.get().y() + (point.get().getY()-event2.getY()));
                mapViewParameters.set(mapViewParameters.get().withMinXY(newPoint));
                point.set(new Point2D(event2.getX(), event2.getY()));
                redrawOnNextPulse(); // Si la carte change je dois faire appel à cette méthode.
            });
        });
    }

    /**
     * Cette méthode "Slide" représente le changement du niveau de zoom de la carte. Elle est appelée dans le constructeur.
     */

    private void scroll() {

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(event -> {
            if (event.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(event.getDeltaY());

            int newZoom = mapViewParameters.get().zoom() + zoomDelta;
            if (newZoom>19 || newZoom<8) return; //le niveau de zoom doit etre entre 8 (inclus) et 19 (inclus).

            mapViewParameters.set( //Je change la carte chaque fois que zoome (multiplication par 2 et puis ajustement) ou que je dézoome (ajustement et puis division par 2). L'ajustement est fait pour que le curseur ne change pas de place lorsque le niveau de zoom de la carte change.
                    zoomDelta>0 ?
                            new MapViewParameters(newZoom, mapViewParameters.get().x() * ZOOM_FACTOR + event.getX(), mapViewParameters.get().y() * ZOOM_FACTOR + event.getY())
                            : new MapViewParameters(newZoom, (mapViewParameters.get().x() - event.getX()) / ZOOM_FACTOR, (mapViewParameters.get().y() - event.getY()) / ZOOM_FACTOR));

            redrawOnNextPulse(); // Si la carte change je dois faire appel à cette méthode pour le redessin.
        });
    }

    /**
     * @return retourne le panneau JavaFX affichant le fond de carte.
     */

    public Pane pane() {
        return pane;
    }

    /**
     * Cette méthode effectue le redessin de la carte ssi l'attribut redrawNeeded est true.
     */

    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        Point2D topLeftPoint = mapViewParameters.get().topLeft();

        int TILE_SIDE = 256;
        int minX = (int) (topLeftPoint.getX() / TILE_SIDE);
        int minY = (int) (topLeftPoint.getY() / TILE_SIDE);
        int maxX = (int) ((topLeftPoint.getX() + canvas.getWidth()) / TILE_SIDE);
        int maxY = (int) ((topLeftPoint.getY() + canvas.getHeight()) / TILE_SIDE);

        if ((topLeftPoint.getX() + canvas.getWidth()) % TILE_SIDE == 0)  maxX-=1; // pour recadrer le l'index x de la tuile.
        if ((topLeftPoint.getY() + canvas.getHeight()) % TILE_SIDE == 0)  maxY-=1; // pour recadrer le l'index y de la tuile.

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                try {
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(mapViewParameters.get().zoom(), x, y));
                    graphicsContext.drawImage(image, x * TILE_SIDE - topLeftPoint.getX(), y * TILE_SIDE - topLeftPoint.getY());
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Cette méthode privée permet à ma classe de demander un redessin au prochain battement. Je l'appelle lorsqu'un dessin est nécessaire.
     */

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
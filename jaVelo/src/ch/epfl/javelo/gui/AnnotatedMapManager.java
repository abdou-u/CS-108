package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.util.function.Consumer;

/**
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe gère l'affichage du fond de carte au-dessus duquel sont superposés l'itinéraire et les points de passage.
 */

public final class AnnotatedMapManager {
    private final StackPane pane;
    private final RouteBean routeBean;
    private final DoubleProperty highlightPosition = new SimpleDoubleProperty(Double.NaN);
    private final ObjectProperty<MapViewParameters> mapViewParameters = new SimpleObjectProperty<>();
    private final ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();
    private final BooleanBinding mouseInMap = Bindings.createBooleanBinding(() -> !Double.isNaN(highlightPosition.get()), highlightPosition);

    /**
     *
     * @param graph
     *             représente le graphe du réseau routier.
     * @param tileManager
     *             représente le gestionnaire de tuiles OpenStreetMap.
     * @param routeBean
     *             représente le bean de l'itinéraire.
     * @param consumer
     *             représente un consommateur d'erreurs permettant de signaler une erreur.
     *
     * Le constructeur crée un gestionnaire de fond de carte (BaseMapManager), un gestionnaire de points de passage (WaypointsManager)
     * et un gestionnaire d'itinéraire (RouteManager) et combine leurs panneaux dans un stackPane.
     */

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer) {

        this.routeBean = routeBean;

        mapViewParameters.set(new MapViewParameters(12, 543200, 370650)); // On construit la carte initiale avec les valeurs demandées.
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParameters, routeBean.getWaypoints(), consumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParameters);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParameters);
        pane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        pane.getStylesheets().add("map.css");

        routeBean.routeProperty().addListener(observable -> adjustHighlightPosition(mousePosition.get())); // La position du cercle dépend de l'itinéraire.
        mapViewParameters.addListener((observable, oldMap, newMap) -> { // La position du cercle dépend des paramètres du fond de carte.
            if (oldMap.zoom()!= newMap.zoom()) adjustHighlightPosition(mousePosition.get());
            else adjustHighlightPosition(new Point2D(mousePosition.get().getX()-(newMap.x()- oldMap.x()), mousePosition.get().getY()-(newMap.y()- oldMap.y())));
        });
        pane.setOnMouseExited(event -> highlightPosition.set(Double.NaN)); // Si la souris quitte la carte (le pane), alors le cercle n'est plus visible.
        pane.setOnMouseMoved(event -> adjustHighlightPosition(new Point2D(event.getX(), event.getY())));
    }

    /**
     * Cette méthode ajuste la position du cercle.
     * Si la souris est proche de moins de 15 pixels de l'itinéraire, le cercle est donc visible sur le routePoint le plus proche de la souris.
     * Sinon il n'est pas visible.
     */

    private void adjustHighlightPosition(Point2D point2D) {
        mousePosition.set(point2D);
        PointCh point = mapViewParameters.get().pointAt(mousePosition.get().getX(), mousePosition.get().getY()).toPointCh();
        if (routeBean.route()!=null && point!=null) {
            PointWebMercator closestPoint = PointWebMercator.ofPointCh(routeBean.route().pointClosestTo(point).point());
            Point2D closestPoint2D = new Point2D(mapViewParameters.get().viewX(closestPoint), mapViewParameters.get().viewY(closestPoint));
            highlightPosition.set(mousePosition.get().distance(closestPoint2D) <= 15 ? routeBean.route().pointClosestTo(point).position() : Double.NaN);
        }
    }

    /**
     *
     * @return
     *        Retourne un BooleanBinding qui est vrai ssi la position du cercle (la highlightedPosition) est différente de NaN,
     *        donc quand la souris est dans la carte et est visible.
     */

    public BooleanBinding mouseInMapProperty () {
        return mouseInMap;
    }

    /**
     *
     * @return
     *        retourne le panneau contenant la carte annotée.
     */

    public Pane pane() {
        return pane;
    }

    /**
     *
     * @return
     *        retourne la propriété contenant la position du pointeur de la souris le long de l'itinéraire.
     */

    public DoubleProperty mousePositionOnRouteProperty() {
        return highlightPosition;
    }
}
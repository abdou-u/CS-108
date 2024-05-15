package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe gère l'affichage de l'itinéraire et l'interaction avec lui.
 */

public final class RouteManager {

    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    private final Circle hole = new Circle(5);
    private final Polyline itinerary = new Polyline();
    private final Pane pane = new Pane(itinerary, hole);
    private final List<Double> doubleList = new ArrayList<>();

    /**
     *
     * @param routeBean
     *                 représente le bean de l'itinéraire.
     * @param mapViewParameters
     *                 représente la propriété contenant les paramètres de la carte affichée.
     */

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters) {

        this.routeBean=routeBean;
        this.mapViewParameters=mapViewParameters;

        itinerary.setId("route");
        hole.setId("highlight");
        pane.setPickOnBounds(false);
        hole.setVisible(false);

        translationAndZoom();
        addWayPoint();
        routeBean.highlightedPositionProperty().addListener(observable -> adjustPositionAndVisibility()); // ajuste le cercle à chaque changement de position.
        routeBean.getWaypoints().addListener((ListChangeListener<? super Waypoint>) observable -> setItinerary()); // redessine l'itinéraire à chaque changement de la liste des points de passage.
    }

    /**
     * Cette méthode ajuste la position du cercle et sa visibilité chaque fois que la valeur de la position change.
     */

    private void adjustPositionAndVisibility () {

        if (!Double.isNaN(routeBean.highlightedPosition())) {
            PointWebMercator point = PointWebMercator.ofPointCh(routeBean.route().pointAt(routeBean.highlightedPosition()));
            hole.setLayoutX(mapViewParameters.get().viewX(point));
            hole.setLayoutY(mapViewParameters.get().viewY(point));
            hole.setVisible(true);
        } else hole.setVisible(false);
    }

    /**
     * Cette méthode dessine l'itinéraire en stockant dans une liste les coordonnés x et y de tous les points de la route.
     */

    private void setItinerary () {

        doubleList.clear();
        if (routeBean.route()!=null) {
            for (PointCh p : routeBean.route().points()) {
                doubleList.add(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(p)));
                doubleList.add(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(p)));
            }
            itinerary.getPoints().setAll(doubleList);
            itinerary.setLayoutX(0);
            itinerary.setLayoutY(0);
            itinerary.setVisible(true);
            adjustPositionAndVisibility();
        } else {
            itinerary.setVisible(false);
            hole.setVisible(false);
        }
    }

    /**
     * Cette méthode ajoute un point de passage en deux points lorsqu'on clique sur le cercle.
     */

    private void addWayPoint () {
        hole.setOnMouseClicked(event -> {
            Point2D positionInMap = hole.localToParent(new Point2D(mapViewParameters.get().x(), mapViewParameters.get().y()));
            PointWebMercator point = PointWebMercator.of(mapViewParameters.get().zoom(), positionInMap.getX(), positionInMap.getY());
            double position = routeBean.route().pointClosestTo(point.toPointCh()).position();
            int index = routeBean.indexOfNonEmptySegmentAt(position);
            routeBean.getWaypoints().add(index+1, new Waypoint(point.toPointCh(), routeBean.route().nodeClosestTo(position)));
        });
    }

    /**
     * Cette méthode gère les translations et le changement du niveau de zoom de la carte pour ajuster la position du cercle et de l'itinéraire.
     * Si on translate, on repositionne l'itinéraire et le cercle (1).
     * Par contre, si on zoome, on redessine l'itinéraire et on réajuste le cercle. (2)
     */

    private void translationAndZoom () {
        mapViewParameters.addListener((observable, oldMap, newMap) -> {
            if (routeBean.route()!=null) {
                if (oldMap.zoom() == newMap.zoom()) { // (1)
                    itinerary.setLayoutX(itinerary.getLayoutX() - (newMap.x() - oldMap.x()));
                    itinerary.setLayoutY(itinerary.getLayoutY() - (newMap.y() - oldMap.y()));
                    hole.setLayoutX(hole.getLayoutX() - (newMap.x() - oldMap.x()));
                    hole.setLayoutY(hole.getLayoutY() - (newMap.y() - oldMap.y()));
                } else setItinerary(); // (2)
            }
        });
    }

    /**
     *
     * @return
     *        retourne le panneau JavaFX contenant la ligne représentant l'itinéraire et le disque de mise en évidence.
     */

    public Pane pane () {
        return  pane;
    }
}
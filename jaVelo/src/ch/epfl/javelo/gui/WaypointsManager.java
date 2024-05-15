package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe gère l'affichage et l'interaction avec les points de passage.
 */

public final class WaypointsManager {

    private final Graph graph;
    private final ObservableList<Waypoint> wayPoints;
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final Consumer<String> stringConsumer;
    private final Pane pane = new Pane();
    private final List<Group> groups = new ArrayList<>();
    private final int SEARCH_DISTANCE = 500;

    /**
     *
     * @param graph
     *              représente le graph du réseau routier.
     * @param mapViewParameters
     *              représente une propriété JavaFX (ObjectProperty) contenant les paramètres de la carte affichée.
     * @param wayPoints
     *              représente la liste de tous les points de passage.
     * @param stringConsumer
     *              représente un objet (consommateur de valeurs) permettant de signaler les erreurs.
     */

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters, ObservableList<Waypoint> wayPoints, Consumer<String> stringConsumer) {

        this.graph = graph;
        this.wayPoints = wayPoints;
        this.mapViewParameters = mapViewParameters;
        this.stringConsumer = stringConsumer;
        pane.setPickOnBounds(false);

        stableAllCursors();
        dragOrDeleteCursor();
        wayPoints.addListener(this::updateGroups); //représente un auditeur détectant les changements de la liste contenant les points de passage.
    }

    /**
     *
     * @param observable
     *           Permet à cette méthode d'etre observée pour evaluation (appel dans le constructeur dès que la liste des points de passage change).
     *
     * Cette méthode recrée la totalité des marqueurs en fonction des points de passage.
     */

    private void updateGroups (Observable observable) {
            pane.getChildren().clear();
            groups.clear();
            for (int i = 0; i < wayPoints.size(); i++) {
                PointWebMercator point = PointWebMercator.ofPointCh(wayPoints.get(i).pointCh());
                createGroups(i,mapViewParameters.get().viewX(point), mapViewParameters.get().viewY(point));
            }
    }

    /**
     * Cette méthode représente un auditeur détectant les changements de la propriété contenant les paramètres du fond de carte.
     * Elle repositionne les marqueurs en cas de translation (1), ou de zoom (2).
     */

    private void stableAllCursors () {
        mapViewParameters.addListener((observable, lastMap, newMap) -> {
            for (Group group : groups) {
                if (lastMap.zoom() == newMap.zoom()) { // (1)
                    group.setLayoutX(group.getLayoutX() - (newMap.x() - lastMap.x()));
                    group.setLayoutY(group.getLayoutY() - (newMap.y() - lastMap.y()));
                } else { // (2)
                    PointWebMercator point = PointWebMercator.ofPointCh(wayPoints.get(groups.indexOf(group)).pointCh());
                    group.setLayoutX(newMap.viewX(point));
                    group.setLayoutY(newMap.viewY(point));
                }
            }
        });
    }

    /**
     * Cette méthode représente un auditeur détectant les événements sur les points de passage.
     * Si un point est appuyé (1), alors si la souris a changé de position (2), alors on appelle la méthode newWayPoint qui va
     * gérer le déplacement du point ou son impossibilité (3), sinon, si la souris n'a pas changé de position et elle n'est plus appuyée,
     * je supprime le point (4).
     */

    private void dragOrDeleteCursor () {
        pane.setOnMousePressed(event -> {
            ObjectProperty<Point2D> point = new SimpleObjectProperty<>(new Point2D(event.getX(), event.getY())); //Ce point est une propriété pour que je puisse l'utiliser dans la lambda.
            for (int i=0; i < groups.size(); i++) {
                Group group = groups.get(i);
                if (group.isPressed()) { // (1)
                    Point2D point2D = new Point2D(group.getLayoutX(), group.getLayoutY());
                        pane.setOnMouseDragged(event2 -> { // (2)
                            group.setLayoutX(group.getLayoutX() + (event2.getX()) - point.get().getX());
                            group.setLayoutY(group.getLayoutY() + (event2.getY()) - point.get().getY());
                            point.set(new Point2D(event2.getX(), event2.getY()));
                            pane.setOnMouseReleased(event3 -> checkWayPoint(groups.indexOf(group), point2D)); // (3)
                        });
                        pane.setOnMouseReleased(event4 -> wayPoints.remove(groups.indexOf(group))); // (4)
                        return; //Si on a appuyé sur un groupe pas la peine de voir si les autres groupes sont appuyés, car on ne peut pas appuyer sur plusieurs groupes à la fois.
                }
            }
        });
    }

    /**
     *
     * @param index
     *              représente l'index du point qu'on est en train de déplacer.
     * @param startPoint
     *              représente le point d'origine du point déplacé.
     *
     * Cette méthode gère le déplacement d'un point. Si la nouvelle position est invalide, alors,
     * le point revient à sa position initiale (startPoint), sinon le point de passage change de position.
     */

    private void checkWayPoint (int index, Point2D startPoint) {
        Group group = groups.get(index);
        PointWebMercator point = mapViewParameters.get().pointAt(group.getLayoutX(), group.getLayoutY());
        int nodeId = point.toPointCh()==null ? -1 : graph.nodeClosestTo(point.toPointCh(), SEARCH_DISTANCE);
        if (nodeId == -1) {
            group.setLayoutX(startPoint.getX());
            group.setLayoutY(startPoint.getY());
            stringConsumer.accept("Aucune route à proximité !");
        } else wayPoints.set(index, new Waypoint(point.toPointCh(), nodeId));
    }

    /**
     *
     * @param index
     *           représente l'index du groupe à créer.
     * @param x
     *           représente la coordonnée x du groupe par rapport à la carte.
     * @param y
     *           représente la coordonnée y du groupe par rapport à la carte.
     *
     * Cette méthode crée les groupes. Elle est appelée chaque fois qu'il y a un changement dans la liste des points de passage.
     * Chaque groupe est lié à un point de passage.
     */

    private void createGroups(int index, double x, double y) {

        String color;

        if(index==0) color = "first"; //Si c'est le premier point alors il est vert.
        else if(index==wayPoints.size()-1) color = "last"; //Si c'est le dernier point alors il est rouge.
        else color = "middle"; //Sinon, il est bleu.

        SVGPath outside = new SVGPath();
        outside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        outside.getStyleClass().add("pin_outside");

        SVGPath inside = new SVGPath();
        inside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        inside.getStyleClass().add("pin_inside");

        Group group = new Group(outside,inside);
        group.getStyleClass().addAll("pin",color);

        group.setLayoutX(x);
        group.setLayoutY(y);

        groups.add(group);
        pane.getChildren().add(group);
    }


    /**
     *
     * @return
     *        retourne le panneau contenant les points de passage.
     */

    public Pane pane() {
        return pane;
    }

    /**
     *
     * @param x
     *         représente la coordonnée x du point.
     * @param y
     *         représente la coordonnée x du point.
     * @throws IllegalArgumentException
     *         lève une exception si les coordonnées du point ne sont pas valides.
     *
     * Cette méthode ajoute un nouveau point de coordonnées x et y de passage au nœud du graphe qui en est le plus proche.
     * Si mon point "point" est nul alors la méthode sectorsInArea de nodeClosestTo va lever une exception donc je mets l'identité
     * du nœud manuellement en -1 (1) pour éviter cette exception et gérer l'erreur dans la prochaine ligne de code (2).
     */

    public void addWaypoint(double x, double y) {

        Preconditions.checkArgument(x>=0 && y>=0);

        PointWebMercator point = mapViewParameters.get().pointAt(x,y);
        int nodeId = point.toPointCh()==null ? -1 : graph.nodeClosestTo(point.toPointCh(), SEARCH_DISTANCE); // (1)
        if (nodeId == -1) stringConsumer.accept("Aucune route à proximité !"); // (2)
        else wayPoints.add(new Waypoint(point.toPointCh(), nodeId));
    }
}
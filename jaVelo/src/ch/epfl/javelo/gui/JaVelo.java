package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe représente la classe principale de l'application.
 */

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    /**
     *
     * @param primaryStage
     *                  représente la scène principale sur laquelle l'application peut être définie.
     * @throws IOException
     *                  cette méthode lève une exception si elle ne trouve pas le chemin du graphe.
     *
     * Cette méthode se charge de construire l'interface graphique finale en combinant
     * les parties gérées par les classes écrites précédemment et en y ajoutant le menu.
     */

    @Override
    public void start(Stage primaryStage) throws IOException {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError; // Toutes les erreurs doivent être gérées par la classe ErrorManager plus précisément par la méthode displayError.
        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);
        SplitPane splitPane = new SplitPane(mapManager.pane());
        TimeManager timeManager = new TimeManager(routeBean, errorConsumer);
        PlacesManager placesManager = new PlacesManager(errorConsumer, routeBean);
        splitPane.setOrientation(Orientation.VERTICAL);
        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);

        routeBean.elevationProfileProperty().addListener(observable -> { // Je gère ici l'affichage du pane du profil de l'élévation.
            splitPane.getItems().remove(elevationProfileManager.pane());
            if (routeBean.elevationProfile()!=null) splitPane.getItems().add(elevationProfileManager.pane());
        });

        splitPane.setOnMouseMoved(event -> { //je lie la position du cercle à la position de la ligne ou à celle du curseur (tout dépend de la position de la souris).
            if (routeBean.route()!=null) routeBean.highlightedPositionProperty().bind(Bindings
                        .when(mapManager.mouseInMapProperty())
                        .then(mapManager.mousePositionOnRouteProperty())
                        .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));
        });

        routeBean.routeProperty().addListener(observable -> {
            if (routeBean.route()==null && routeBean.getWaypoints().size()>1) errorConsumer.accept("Choisissez une autre route !");
        });

        primaryStage.showingProperty().addListener(observable -> {
            if (!primaryStage.isShowing()) {
                timeManager.getJFrameSpeed().dispose();
                timeManager.getJFrameTime().dispose();
            }
        });


        Menu menu = new Menu("Fichier");
        MenuItem export = new MenuItem("Exporter GPX");
        menu.getItems().add(export);

        Menu edit = new Menu("Edit");
        MenuItem deleteAllPoints = new MenuItem("Supprimer tous les points");
        MenuItem deleteWayPoints = new MenuItem("Supprimer les points de passages");
        MenuItem inverseItinerary = new MenuItem("Inverser l'itinéraire");
        edit.getItems().addAll(deleteAllPoints, deleteWayPoints, inverseItinerary);


        export.disableProperty().bind(Bindings.createBooleanBinding(() -> routeBean.route()==null, routeBean.routeProperty()));
        deleteAllPoints.disableProperty().bind(Bindings.createBooleanBinding(() -> routeBean.getWaypoints().isEmpty(), routeBean.getWaypoints()));
        inverseItinerary.disableProperty().bind(export.disableProperty());
        deleteWayPoints.disableProperty().bind(Bindings.createBooleanBinding(() -> routeBean.getWaypoints().size()<3, routeBean.getWaypoints()));


        export.setAccelerator(KeyCombination.valueOf("Ctrl+E"));
        deleteAllPoints.setAccelerator(KeyCombination.valueOf("Ctrl+X"));
        inverseItinerary.setAccelerator(KeyCombination.valueOf("Ctrl+I"));
        timeManager.getShowTime().setAccelerator(KeyCombination.valueOf("Ctrl+T"));


        export.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), routeBean.elevationProfile());
            } catch (IOException e) {
                throw new UncheckedIOException(e); //Les exceptions levées par la méthode writeGpx sont levées à nouveau sous la forme d'exceptions de type UncheckedIOException.
            }
        });
        inverseItinerary.setOnAction(event -> Collections.reverse(routeBean.getWaypoints()));
        deleteWayPoints.setOnAction(event -> routeBean.getWaypoints().remove(1, routeBean.getWaypoints().size()-1));
        deleteAllPoints.setOnAction(event -> routeBean.getWaypoints().clear());
        timeManager.getShowTime().setOnAction(event -> timeManager.start());
        placesManager.getLakeOfBienne().setOnAction(event -> placesManager.setWayPoint(placesManager.getLakeOfBienneWayPoint()));
        placesManager.getGalcier3000().setOnAction(event -> placesManager.setWayPoint(placesManager.getGlacier3000WayPoint()));
        placesManager.getGruyereCheeseHouse().setOnAction(event -> placesManager.setWayPoint(placesManager.getGruyereCheeseHouseWayPoint()));
        placesManager.getOlympicMuseum().setOnAction(event -> placesManager.setWayPoint(placesManager.getOlympicMuseumWayPoint()));

        MenuBar menuBar = new MenuBar(menu);
        menuBar.getMenus().addAll(edit, placesManager.getPlaceToVisit(), timeManager.getTime());
        new ImageManager(export, deleteAllPoints, deleteWayPoints, inverseItinerary, placesManager.getLakeOfBienne(), placesManager.getOlympicMuseum(), placesManager.getGalcier3000(), placesManager.getGruyereCheeseHouse());
        StackPane stackPane = new StackPane(splitPane, errorManager.pane());
        BorderPane borderPane = new BorderPane(stackPane, menuBar, null, null, null);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }
}

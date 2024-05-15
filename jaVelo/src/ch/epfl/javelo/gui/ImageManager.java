package ch.epfl.javelo.gui;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe représente les images ajoutées.
 */

public final class ImageManager {

    private final MenuItem lakeOfBienne;
    private final MenuItem deleteAllPoints;
    private final MenuItem deleteWayPoints;
    private final MenuItem inverseItinerary;
    private final MenuItem exportGPX;
    private final MenuItem gruyereCheeseHouse;
    private final MenuItem olympicMuseum;
    private final MenuItem galicer3000;

    public ImageManager (MenuItem exportGPX, MenuItem deleteAllPoints, MenuItem deleteWayPoints, MenuItem inverseItinerary, MenuItem lakeOfBienne, MenuItem olympicMuseum, MenuItem galicer3000, MenuItem gruyereCheeseHouse) {

        this.lakeOfBienne = lakeOfBienne;
        this.deleteAllPoints = deleteAllPoints;
        this.deleteWayPoints = deleteWayPoints;
        this.inverseItinerary = inverseItinerary;
        this.exportGPX = exportGPX;
        this.gruyereCheeseHouse = gruyereCheeseHouse;
        this.olympicMuseum = olympicMuseum;
        this.galicer3000 = galicer3000;

        exportGPXImage();
        deleteAllPointsImage();
        deleteWayPointsImage();
        inverseItineraryImage();
        lakeOfBienneImage();
        gruyereCheeseHouseImage();
        olympicMuseumImage();
        galicer3000Image();
    }

    private void galicer3000Image() {
        ImageView glacier = new ImageView("glacier3000.png");
        glacier.setFitWidth(25);
        glacier.setFitHeight(25);
        galicer3000.setGraphic(glacier);
    }

    private void olympicMuseumImage() {
        ImageView museum = new ImageView("olympicMuseum.png");
        museum.setFitWidth(25);
        museum.setFitHeight(25);
        olympicMuseum.setGraphic(museum);
    }

    private void gruyereCheeseHouseImage() {
        ImageView cheese = new ImageView("cheeseHouse.png");
        cheese.setFitWidth(25);
        cheese.setFitHeight(25);
        gruyereCheeseHouse.setGraphic(cheese);
    }

    private void lakeOfBienneImage () {
        ImageView lake = new ImageView("lakeOfBienne.png");
        lake.setFitWidth(25);
        lake.setFitHeight(25);
        lakeOfBienne.setGraphic(lake);
    }

    private void inverseItineraryImage () {
        ImageView inverse = new ImageView("inverseItinerary.png");
        inverse.setFitWidth(25);
        inverse.setFitHeight(25);
        inverseItinerary.setGraphic(inverse);
    }

    private void deleteWayPointsImage () {
        ImageView delete = new ImageView("deleteWayPoints.png");
        delete.setFitWidth(25);
        delete.setFitHeight(25);
        deleteWayPoints.setGraphic(delete);
    }

    private void exportGPXImage () {
        ImageView export = new ImageView("itinerary.png");
        export.setFitWidth(30);
        export.setFitHeight(30);
        exportGPX.setGraphic(export);
    }

    private void deleteAllPointsImage () {
        ImageView delete = new ImageView("deleteAllPoints.png");
        delete.setFitWidth(25);
        delete.setFitHeight(25);
        deleteAllPoints.setGraphic(delete);
    }
}

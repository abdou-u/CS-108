package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import java.util.function.Consumer;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe représente différents sites à visiter.
 */

public final class PlacesManager {

    private final RouteBean routeBean;
    private final Waypoint glacier3000WayPoint = new Waypoint(new PointCh(2581985.4949685256, 1130619.0344446485), 1020603);
    private final Waypoint gruyereCheeseHouseWayPoint = new Waypoint(new PointCh(2571964.084063792, 1159062.21171099), 1785801);
    private final Waypoint lakeOfBienneWayPoint = new Waypoint(new PointCh(2577820.5808820277,1213778.4164533645), 3171064);
    private final Waypoint olympicMuseumWayPoint = new Waypoint(new PointCh(2538247.643769931, 1151137.3132438143), 2042173);
    private final Consumer<String> consumer;
    private final Menu placeToVisit = new Menu("Sites à visiter");
    private final MenuItem lakeOfBienne = new MenuItem("Lac de Bienne");
    private final MenuItem gruyereCheeseHouse = new MenuItem("Maison du Gruyère");
    private final MenuItem olympicMuseum = new MenuItem("Musée Olympique");
    private final MenuItem galcier3000 = new MenuItem("Glacier 3000");

    public PlacesManager (Consumer<String> consumer, RouteBean routeBean) {

        this.routeBean = routeBean;
        this.consumer = consumer;

        placeToVisit.getItems().addAll(lakeOfBienne, galcier3000, gruyereCheeseHouse, olympicMuseum);
        lakeOfBienne.setDisable(false);
        galcier3000.setDisable(false);
        gruyereCheeseHouse.setDisable(false);
        olympicMuseum.setDisable(false);
    }

    /**
     *
     * @return
     *       retourne l'item qui représente le musée olympique.
     */

    public MenuItem getOlympicMuseum() {
        return olympicMuseum;
    }

    /**
     *
     * @return
     *       retourne le point de passage qui représente le musée olympique.
     */

    public Waypoint getOlympicMuseumWayPoint() {
        return olympicMuseumWayPoint;
    }

    /**
     *
     * @return
     *       retourne l'item qui représente le glacier.
     */

    public MenuItem getGalcier3000() {
        return galcier3000;
    }

    /**
     *
     * @return
     *       retourne le point de passage qui représente le lac de Bienne.
     */

    public Waypoint getLakeOfBienneWayPoint() {
        return lakeOfBienneWayPoint;
    }

    /**
     *
     * @return
     *       retourne le point de passage qui représente la maison du Gruyère.
     */

    public Waypoint getGruyereCheeseHouseWayPoint() {
        return gruyereCheeseHouseWayPoint;
    }

    /**
     *
     * @return
     *       retourne l'item qui représente la maison du Gruyère.
     */

    public MenuItem getGruyereCheeseHouse() {
        return gruyereCheeseHouse;
    }

    /**
     *
     * @return
     *       retourne le point de passage qui représente le glacier.
     */

    public Waypoint getGlacier3000WayPoint() {
        return glacier3000WayPoint;
    }

    /**
     *
     * @return
     *       retourne l'item qui représente le lac de Bienne.
     */

    public MenuItem getLakeOfBienne() {
        return lakeOfBienne;
    }

    /**
     *
     * @param placeToGo
     *               représente le site que vous voudrez visiter.
     *
     * Cette méthode ajoute un point de passage dans la place demandée.
     */

    public void setWayPoint (Waypoint placeToGo) {
        if (routeBean.getWaypoints().size()==0) consumer.accept("Ajoute ton point de départ !");
        else routeBean.getWaypoints().add(placeToGo);
    }

    /**
     *
     * @return
     *       retourne le Menu.
     */

    public Menu getPlaceToVisit () {
        return placeToVisit;
    }
}

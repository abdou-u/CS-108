package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import java.util.*;

/**
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe est un bean JavaFX regroupant les propriétés relatives aux points de passage et à l'itinéraire correspondant.
 */
public final class RouteBean {

    private final ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();
    private final ObjectProperty<Route> route = new SimpleObjectProperty<>();
    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty(Double.NaN);
    private final ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();
    private final Map<Pair<Integer, Integer>, Route> cacheMemoire = new LinkedHashMap<>(120, 75);

    /**
     *
     * @param routeComputer
     *                    représente le planificateur d'itinéraire qui va permettre de calculer à chaque fois les meilleurs itinéraires.
     *
     * Le constructeur calcule le meilleur itinéraire entre les points de passages.
     */

    public RouteBean (RouteComputer routeComputer) {

        waypoints.addListener((ListChangeListener<? super Waypoint>) observable -> {
            if (waypoints.size()<2) {
                route.set(null);
                elevationProfile.set(null);
            } else {
                List<Route> routeList = new ArrayList<>();

                for (int i=0; i < waypoints.size()-1; i++) {
                    Route newRoute;
                    Pair<Integer,Integer> id = new Pair<>(waypoints.get(i).nodeId(), waypoints.get(i+1).nodeId());
                    if (Objects.equals(id.getKey(), id.getValue())) continue;

                    if (cacheMemoire.containsKey(id)) {
                        newRoute = cacheMemoire.get(id);
                    } else {
                        newRoute = routeComputer.bestRouteBetween(id.getKey(), id.getValue());
                        if (cacheMemoire.size()==80) cacheMemoire.remove(cacheMemoire.keySet().iterator().next());
                        cacheMemoire.put(id, newRoute);
                    }
                    if (newRoute==null) {
                        routeList.clear();
                        route.set(null);
                        elevationProfile.set(null);
                        return;
                    }
                    routeList.add(newRoute);
                }

                if (!routeList.isEmpty()) {
                    route.set(new MultiRoute(routeList));
                    elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
                }
            }
        });
    }

    /**
     *
     * @param position
     *        représente la position mise en évidence.
     * @return
     *        retourne un segment qui passe par le point représenté par la position.
     */

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     *
     * @return
     *        retourne la liste des points de passage de l'itinéraire.
     */

    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     *
     * @return
     *        retourne la propriété contenant le meilleur itinéraire calculé.
     */

    public ReadOnlyObjectProperty<Route> routeProperty () {
        return route;
    }

    /**
     *
     * @return
     *        retourne le meilleur itinéraire calculé selon les points de passages existants.
     */

    public Route route() {
        return route.get();
    }

    /**
     *
     * @return
     *        retourne la propriété contenant la position du cercle.
     */

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     *
     * @return
     *        retourne la position du cercle.
     */

    public double highlightedPosition () {
        return highlightedPosition.get();
    }

    /**
     *
     * @return
     *        retourne la propriété contenant le profil en long de l'itinéraire.
     */

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     *
     * @return
     *        retourne le profil en long de l'itinéraire.
     */

    public ElevationProfile elevationProfile() {
        return elevationProfile.get();
    }
}
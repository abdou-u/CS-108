package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cet enregistrement représente les paramètres du fond de carte présenté dans l'interface graphique.
 * @param zoom  : Le niveau de zoom.
 * @param x  : La coordonnée x du coin haut-gauche de la portion de carte affichée.
 * @param y  : La coordonnée y du coin haut-gauche de la portion de carte affichée.
 */

public record MapViewParameters(int zoom, double x, double y) {

    /**
     *
     * @return
     *        retourne les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D.
     */

    public Point2D topLeft() {
        return new Point2D(x,y);
    }

    /**
     *
     * @param point2D
     *         représente un objet (point) utilisé.
     * @return
     *         retourne une nouvelle instance de MapViewParameters, identique au récepteur,
     *         dont les attributs x et y sont modifiés par ceux du point passé en argument.
     */

    public MapViewParameters withMinXY (Point2D point2D) {
        return new MapViewParameters(zoom, point2D.getX(), point2D.getY());
    }

    /**
     *
     * @param x
     *         représente la coordonnée x du point, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * @param y
     *         représente la coordonnée y du point, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * @return
     *         retourne un point sous la forma d'une instance de PointWebMercator.
     */

    public PointWebMercator pointAt (double x, double y) {
        return PointWebMercator.of(zoom, x+this.x, y+this.y);
    }

    /**
     *
     * @param pointWebMercator
     *        représente le point Web Mercator utilisé.
     * @return
     *        retourne la position x correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     * @throws IllegalArgumentException
     *         lève une exception si les coordonnées du pointWebMercator ne sont pas comprises entre 0 et 1.
     */

    public double viewX (PointWebMercator pointWebMercator) {
        return pointWebMercator.xAtZoomLevel(zoom) - x;
    }

    /**
     *
     * @param pointWebMercator
     *        représente le point Web Mercator utilisé.
     * @return
     *        retourne la position y correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     * @throws IllegalArgumentException
     *         lève une exception si les coordonnées du pointWebMercator ne sont pas comprises entre 0 et 1.
     */

    public double viewY (PointWebMercator pointWebMercator) {
        return pointWebMercator.yAtZoomLevel(zoom) - y;
    }
}
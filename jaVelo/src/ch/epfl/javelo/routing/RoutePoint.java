package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cet enregistrement représente le point d'un itinéraire le plus proche d'un point de référence donné, qui se trouve dans le voisinage de l'itinéraire.
 * Il possède les attributs suivants :
 * @param point, qui représente le point sur l'itinéraire.
 * @param position, qui représente la position du point le long de l'itinéraire, en mètres.
 * @param distanceToReference, qui représente la distance, en mètres, entre le point et la référence.
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint (null, Double.NaN, Double.POSITIVE_INFINITY); //une constante qui représente un point inexistant.
    
    /**
     * 
     * @param positionDifference
     *         représente la différence donnée et peut être positive ou négative.
     * @return
     *         retourne un point identique au récepteur (this) 
     *         mais dont la position est décalée de la différence donnée.
     */
    
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return positionDifference==0 ? this : new RoutePoint(this.point, position+positionDifference, distanceToReference);
    }
    
    /**
     * 
     * @param that
     *        représente une instance de RoutePoint.
     * @return
     *        retourne this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon.
     */
    
    public RoutePoint min(RoutePoint that) {
        return distanceToReference<=that.distanceToReference ? this : that;
    }
    
    /**
     * 
     * @param thatPoint
     *         représente le point sur l'itinéraire.
     * @param thatPosition
     *         représente la position du point le long de l'itinéraire, en mètres.
     * @param thatDistanceToReference
     *         représente la distance, en mètres, entre le point et la référence.
     * @return
     *         retourne this si sa distance à la référence est inférieure ou égale à thatDistanceToReference, 
     *         et une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min sinon.
     */
    
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return distanceToReference<=thatDistanceToReference ? this : new RoutePoint (thatPoint, thatPosition, thatDistanceToReference);
    }
}

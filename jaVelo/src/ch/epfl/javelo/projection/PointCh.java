package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * 
 * @author Ahmed Abdelmalek (344471
 * @author Youssef Neji (346960)
 * 
 * Cet enregistrement (PointCh) représente un point dans le système de coordonnées suisse. 
 * Il est doté des attributs e (coordonnée est du point) et n (coordonnée nord du point).
 */

public record PointCh(double e, double n) {
    
    /**
     * 
     * PointCh est un constructeur compact de l'enregistrement PointCh.
     * Il lève une exception (IllegalArgumentException) si les coordonnées fournies ne sont pas dans les limites de la Suisse définies par SwissBounds.
     */
    
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
        }
    
    /**
     * 
     * @param that 
     *         représente le point passé en argument.
     * @return
     *         retourne le carré de la distance en mètres séparant le récepteur (this) de l'argument that.
     */
    
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(this.e-that.e, this.n-that.n);
    }
    
    /**
     * 
     * @param that 
     *         représente le point passé en argument.
     * @return
     *         retourne la distance en mètres séparant le récepteur (this) de l'argument that.
     */
    
    public double distanceTo(PointCh that) {
        return Math.sqrt(squaredDistanceTo(that));
        }
    
    /**
     * 
     * @return
     *        retourne la longitude du point, dans le système WGS84, en radians.
     */
    
    public double lon() {
        return Ch1903.lon(e,n);
    }
    
    /**
     * 
     * @return
     *        retourne la latitude du point, dans le système WGS84, en radians.
     */
    
    public double lat() {
        return Ch1903.lat(e,n);
        }
    }

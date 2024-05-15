package ch.epfl.javelo.routing;

import java.util.List;
import ch.epfl.javelo.projection.PointCh;

/**
 * 
 * @author Ahmed Abdelmale (344471)
 * @author Youssef Neji (346960)
 *
 * Cette interface représente un itinéraire.
 */

public interface Route {

    /**
     * 
     * @param position
     *         représente la position donnée, en mètres.
     * @return
     *         retourne l'index du segment à la position donnée, en mètres.
     */
    
    int indexOfSegmentAt(double position);
    
    /**
     * 
     * @return
     *         retourne la longueur de l'itinéraire, en mètres.
     */
    
    double length();
    
    /**
     * 
     * @return
     *         retourne la totalité des arêtes de l'itinéraire.
     */
    
    List<Edge> edges();
    
    /**
     * 
     * @return
     *         retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     */
    
    List<PointCh> points();
    
    /**
     * 
     * @param position
     *         représente la position donnée, en mètres.
     * @return
     *         retourne le point se trouvant à la position donnée le long de l'itinéraire.
     */
    
    PointCh pointAt(double position);
    
    /**
     * 
     * @param position
     *         représente la position donnée, en mètres.
     * @return
     *         retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     */
    
    int nodeClosestTo(double position);
    
    /**
     * 
     * @param point
     *         représente le point de référence.
     * @return
     *         retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné.
     */
    
    RoutePoint pointClosestTo(PointCh point);
    
    /**
     * 
     * @param position
     *         représente la position donnée, en mètres.
     * @return
     *         retourne l'altitude à la position donnée le long de l'itinéraire.
     */
    
    double elevationAt(double position);
}

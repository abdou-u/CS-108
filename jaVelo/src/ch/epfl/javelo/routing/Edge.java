package ch.epfl.javelo.routing;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cet enregistrement représente une arête d'un itinéraire.
 * Le but de cet enregistrement est de collecter toutes les informations relatives à une arête d'itinéraire, 
 * qui pourraient être obtenues par des appels aux méthodes de Graph.
 * 
 * Il possède les attributs suivants :
 * @param fromNodeId qui représente l'identité du nœud de départ de l'arête.
 * @param toNodeId qui représente l'identité du nœud d'arrivée de l'arête.
 * @param fromPoint qui représente le point de départ de l'arête.
 * @param toPoint qui représente le point d'arrivée de l'arête.
 * @param length qui représente la longueur de l'arête, en mètres.
 * @param profile qui représente le profil en long de l'arête.
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     * 
     * @param graph
     *            représente le graph utilisé.
     * @param edgeId
     *            représente l'identité (index) de l'edge utilisé.
     * @param fromNodeId
     *            représente l'identité du nœud de départ de l'arête.
     * @param toNodeId
     *            représente l'identité du nœud d'arrivée de l'arête.
     * @return
     *         retourne une instance de Edge dont les attributs 
     *         fromNodeId et toNodeId sont ceux donnés, les autres 
     *         étant ceux de l'arête d'identité edgeId dans le graphe Graph.
     */
    
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }
    
    /**
     * 
     * @param point
     *         représente le point donné.
     * @return
     *         retourne la position le long de l'arête, en mètres, qui se trouve la plus proche du point donné.
     */
    
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }
    
    /**
     * 
     * @param position
     *         représente la position donnée, en mètres.
     * @return
     *         retourne le point se trouvant à la position donnée sur l'arête, exprimée en mètres.
     */
    
    public PointCh pointAt(double position) {
        if (length==0) return fromPoint;
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), position/length), Math2.interpolate(fromPoint.n(), toPoint.n(), position/length));
    }
    
    /**
     * 
     * @param position
     *         représente la position donnée, en mètres.
     * @return
     *         retourne l'altitude, en mètres, à la position donnée sur l'arête.
     */
    
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}

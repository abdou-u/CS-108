package ch.epfl.javelo.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

/**    
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe représente un itinéraire simple, reliant un point de départ à un point d'arrivée, sans point de passage intermédiaire. 
 * Elle implémente l'interface Route.
 */

public final class SingleRoute implements Route {

    private final List <Edge> edge;
    private final double [] tab;
    private final List<PointCh> pointChList;

    /**
     * Ce constructeur retourne l'itinéraire simple composé des arêtes données.
     * Il lève IllegalArgumentException si la liste d'arêtes est vide.
     * 
     * @param edges
     *           représente la liste des edges utilisées.
     */
    
    public SingleRoute(List<Edge> edges) {

        Preconditions.checkArgument(!edges.isEmpty());

        edge = List.copyOf(edges);
        tab = new double [edges.size()+1];
        tab [0] = 0;

        for (int i=1; i<=edges.size(); i++) {
            tab[i] = edges.get(i-1).length() + tab [i-1];
        }

        List<PointCh> list = new ArrayList<>();

        for (Edge P : edge) {
            list.add(P.pointAt(0));
        }
        Edge lastEdge = edge.get(edge.size()-1);
        list.add(lastEdge.pointAt(lastEdge.length())); //Dernier point de l'itinéraire

        pointChList=List.copyOf(list);
    }
    
    /**
     * {@inheritDoc}
     */
    
    @Override
    public double length() {
        return tab[points().size()-1];
    }
   
    /**
     * {@inheritDoc}
     * Cette méthode retourne toujours 0 dans le cas d'un itinéraire simple
     */
    
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<Edge> edges() {
        return edge;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<PointCh> points() {
        return pointChList;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public PointCh pointAt(double position) {
        
        position = Math2.clamp(0, position, length());
        
        int valeur = Arrays.binarySearch(tab, position); 
                
        if(valeur<0) {
            return edge.get(Math.abs(valeur+2)).pointAt(position-tab[Math.abs(valeur+2)]);
        }
        
        else {
            return pointChList.get(valeur);
        }
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public double elevationAt(double position) {
        
        position = Math2.clamp(0, position, length());
                
        int valeur = Arrays.binarySearch(tab, position);
        
        if (valeur<0) {
            return edge.get(Math.abs(valeur+2)).elevationAt(position-tab[Math.abs(valeur+2)]);
        }
        else {
            
            if (valeur == edge.size()) {//Cas extreme
                return edge.get(valeur-1).elevationAt(edge.get(valeur-1).length());
            }
            else {
                return edge.get(valeur).elevationAt(0); 
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    
    @Override
    public int nodeClosestTo(double position) {

        position = Math2.clamp(0, position, length());
        
        int valeur = Arrays.binarySearch(tab, position); 
        
        if (valeur<0) {

            if (position-tab[Math.abs(valeur+2)] <= (edge.get(Math.abs(valeur+2)).length())/2) {
                return edge.get(Math.abs(valeur+2)).fromNodeId();
            }
            else {
                return edge.get(Math.abs(valeur+2)).toNodeId();
            }
        }
        else {
            if (valeur == edge.size()) { //Cas extreme
                return edge.get(valeur-1).toNodeId();
            }
            else {
                return edge.get(valeur).fromNodeId();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        
        PointCh newPoint = point;
        double longer =0, dis=0;
        int j=0;
        
        for (int i=0; i<edge.size(); i++) {
            
            double pos = Math2.clamp(0, edge.get(i).positionClosestTo(point), edge.get(i).length());
            
            PointCh closestP = edge.get(i).pointAt(pos);
            
            if (i==0 || point.distanceTo(closestP)<dis) {
                
                longer = pos;
                dis = point.distanceTo(closestP);
                newPoint = closestP;
                j=i;
            }
        }
        return new RoutePoint(newPoint, tab[j]+ longer, point.distanceTo(newPoint));
    }
}

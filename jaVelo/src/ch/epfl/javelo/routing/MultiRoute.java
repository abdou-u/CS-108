package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe représente un itinéraire multiple, composé d'une séquence d'itinéraires contigus nommés segments. 
 * Elle implémente l'interface Route
 */

public final class MultiRoute implements Route {
    
    private final List <Route> segments;
    
    public MultiRoute(List<Route> segments) {

        Preconditions.checkArgument(segments.size()!=0); //lève une exception si la liste est vide.

        this.segments = List.copyOf(segments);
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public int indexOfSegmentAt(double position) {
        
        position = Math2.clamp(0, position, length());
        
        boolean bool = false;
        int i=0, index=0;  //i est un compteur d'itération, index est un compteur à retourner en résultat
        
        do {
            if(position>segments.get(i).length()) { //si la position est supérieure à la longueur de tout le segment, elle est forcément dans un segment suivant
                
                index+=segments.get(i).indexOfSegmentAt(segments.get(i).length())+1; //pour connaître le nombre de singleRoutes composant le segment en question
                position-=segments.get(i).length(); //on va estimer que la position qui suit le segment qu'on vient d'explorer est la nouvelle position 0
                i++;
            }
            else { //si la position est inferieure a la longueur du segment, elle y est incluse 
                index+=segments.get(i).indexOfSegmentAt(position)+1;
                bool =true; //ce test signale au programme que le segment en question a été trouvé
            }
        }
        while(!bool);
        
        return index-1; //l'index à la fin du travail représente le nombre de segments, puisque l'indice du premier est 0 et pas 1 on doit forcément enlever 1 au résultat
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public double length() {
        
        double length = 0;
        
        for (Route l : segments) {
            length += l.length();
        }
        
        return length;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<Edge> edges() {
        
        List<Edge> liste = new ArrayList<>();
        
        for(Route r : segments) {
            liste.addAll(r.edges());
        }
        
        return liste;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public List<PointCh> points() {
                
        List <PointCh> list = new ArrayList <>();

        for (Route segment : segments) {
            for (int z = 0; z < segment.points().size() - 1; z++) {
                list.add(segment.points().get(z));
            }
        }
        list.add(segments.get(segments.size()-1).pointAt(length()));

        return list;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public PointCh pointAt(double position) {
        
        position = Math2.clamp(0, position, length());
        
        int i=0;
        
        while(position>segments.get(i).length()) {
            
            position-=segments.get(i).length();
            i++;
        }
        return segments.get(i).pointAt(position);
    }
         
    /**
     * {@inheritDoc}
     */
    
    @Override
    public int nodeClosestTo(double position) {
        
        position = Math2.clamp(0, position, length());
        
        int i=0;
        
        while(position>segments.get(i).length()) {
            
            position-=segments.get(i).length();
            i++;
        }
        return segments.get(i).nodeClosestTo(position);
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        
        double p = segments.get(0).pointClosestTo(point).distanceToReference();
        int i=0, l=0;
        
        for (int j=1; j<segments.size(); j++) {
            
            if(p>segments.get(j).pointClosestTo(point).distanceToReference()) { //On compare la distance à la référence de chaque point pour avoir la plus courte distance à la référence.
                p=segments.get(j).pointClosestTo(point).distanceToReference();
                i=j; //i représente l'index du segments du point le plus proche.
            }
        }
                
        if (i!=0) {
    
            for (int z=0; z<i; z++) { //On calcule la longeur à ajouter.
                l+=segments.get(z).length();
            }
        }
        return segments.get(i).pointClosestTo(point).withPositionShiftedBy(l);
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public double elevationAt(double position) {
        
        position = Math2.clamp(0, position, length());
        
        int i=0;
        
        while(position>segments.get(i).length()) {
            
            position-=segments.get(i).length();
            i++;
        }
        return segments.get(i).elevationAt(position);
    }
}

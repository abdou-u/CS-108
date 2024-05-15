package ch.epfl.javelo.routing;

import java.util.DoubleSummaryStatistics;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe représente le profil en long d'un itinéraire simple ou multiple.
 */

public final class ElevationProfile {

    private final DoubleSummaryStatistics elevation = new DoubleSummaryStatistics() ;
    private final double length;
    private final float [] elevationSamples;
    private double totalAscent = 0;
    private double totalDescent = 0;
    
    /**
     * 
     * @param length
     *              représente la longeur du profil.
     * @param elevationSamples
     *              représente les échantillons d'altitude du profil.
     *              
     * Le constructeur de la classe ElevationProfile construit le profil en long d'un itinéraire de longueur length (en mètres) 
     * et dont les échantillons d'altitude, répartis uniformément le long de l'itinéraire, sont contenus dans elevationSamples.
     *
     * @throws IllegalArgumentException
     *               lève IllegalArgumentException si la longueur est négative ou nulle,
     *               ou si le tableau d'échantillons contient moins de 2 éléments.
     */
    
    public ElevationProfile(double length, float[] elevationSamples) {
        
        Preconditions.checkArgument(length>0 && elevationSamples.length>1);
        
        this.length = length;
        this.elevationSamples = elevationSamples.clone();

        for (float elevationSample : elevationSamples) {
            elevation.accept(elevationSample);
        }

        for (int i=0; i<elevationSamples.length-1; i++) {
            if ((elevationSamples[i+1] - elevationSamples[i])>0) totalAscent += elevationSamples[i+1] - elevationSamples[i];
            else totalDescent += elevationSamples[i+1] - elevationSamples[i];
        }
    }
    
    /**
     * 
     * @return
     *        retourne la longueur du profil, en mètres.
     */
    
    public double length() {
        return length;
    }
    
    /**
     * 
     * @return
     *        retourne l'altitude minimum du profil, en mètres.
     */
    
    public double minElevation() {
        return elevation.getMin();
    }
    
    /**
     * 
     * @return
     *        retourne l'altitude maximum du profil, en mètres.
     */
    
    public double maxElevation() {
        return elevation.getMax();
    }
    
    /**
     * 
     * @return
     *        retourne le dénivelé positif total du profil, en mètres.
     */
    
    public double totalAscent() {
        return totalAscent;
    }
    
    /**
     * 
     * @return
     *        retourne le dénivelé négatif total du profil, en mètres.
     */
    
    public double totalDescent() {
        return Math.abs(totalDescent);
    }
    
    
    /**
     * 
     * @param position
     *         représente la position voulue.
     * @return
     *         retourne l'altitude du profil à la position donnée. 
     *         Le premier échantillon est retourné lorsque la position est négative, 
     *         le dernier lorsqu'elle est supérieure à la longueur.
     */
    
    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    } 
}

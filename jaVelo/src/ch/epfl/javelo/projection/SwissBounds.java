package ch.epfl.javelo.projection;

/**
 * Cette classe représente la condition de contenance.
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class SwissBounds {
	
    /**
     * Ce constructeur privé permet à la classe SwissBounds d'etre non instanciable.
     */

    private SwissBounds() {}
    
    public final static double MIN_E = 2485000; //la plus petite coordonnée E de Suisse (2485000).
    public final static double MAX_E = 2834000; //la plus grande coordonnée E de Suisse (2834000).
    public final static double MIN_N = 1075000; //la plus petite coordonnée N de Suisse (1075000).
    public final static double MAX_N = 1296000; //la plus grande coordonnée N de Suisse (1296000).
    public final static double WIDTH = MAX_E - MIN_E; //la largeur de la Suisse en mètres, définie comme la différence entre MAX_E et MIN_E.
    public final static double HEIGHT = MAX_N - MIN_N; //la hauteur de la Suisse en mètres, définie comme la différence entre MAX_N et MIN_N.
    
    /**
     * @param e
     *      représente la coordonnée est dans le système de coordonnées utilisé actuellement en Suisse (CH1903+).
     *      
     * @param n
     *      représente la coordonnée nord dans le système de coordonnées utilisé actuellement en Suisse (CH1903+).
     *      
     * @return 
     *      retourne un boolean qui confirme que e et n sont dans les limites de la Suisse. 
     */
    
    public static boolean containsEN(double e, double n) {
        return (e<=MAX_E && e>=MIN_E && n<=MAX_N && n>=MIN_N);
        }
  }

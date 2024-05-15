package ch.epfl.javelo.projection;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Ch1903 {
	
    /**
     * Ce constructeur privé permet à la classe Ch1903 d'etre non instanciable.
     */
	
    private Ch1903() {}
    
    /**
     * 
     * @param lon
     *         représente la longitude du point.
     * @param lat
     *         représente la latitude du point.
     * @return
     *         retourne la coordonnée E (est) du point de longitude lon et latitude lat dans le système WGS84.
     */
    
    public static double e(double lon, double lat) {
        
        double lon1 = 1e-4 * (3600*Math.toDegrees(lon)-26782.5);
        double lat1 = 1e-4 * (3600*Math.toDegrees(lat)-169028.66);

        return 2600072.37 + 211455.93*lon1 - 10938.51*lon1*lat1 - 0.36*lon1*lat1*lat1 - 44.54*lon1*lon1*lon1;
    }
    
    /**
     * 
     * @param lon
     *         représente la longitude du point.
     * @param lat
     *         représente la latitude du point.
     * @return
     *         retourne la coordonnée N (nord) du point de longitude lon et latitude lat dans le système WGS84.
     */
    
    public static double n(double lon, double lat) {
        
        double lon1 = 1e-4 * (3600*Math.toDegrees(lon)-26782.5);
        double lat1 = 1e-4 * (3600*Math.toDegrees(lat)-169028.66);

        return 1200147.07 + 308807.95*lat1 + 3745.25*lon1*lon1 + 76.63*lat1*lat1 - 194.56*lon1*lon1*lat1 + 119.79*lat1*lat1*lat1;
    }
    
    /**
     * 
     * @param e
     *         représente la coordonnée est du point.
     * @param n
     *         représente la coordonnée nord du point.
     * @return
     *         retourne la longitude dans le système WGS84 du point dont les coordonnées sont e et n dans le système suisse.
     */
    
    public static double lon(double e, double n) {
        
        double x = 1e-6 * (e-2600000);
        double y = 1e-6 * (n-1200000);

        return Math.toRadians((2.6779094 + 4.728982*x + 0.791484*x*y + 0.1306*x*y*y - 0.0436*x*x*x) * (100/36d));
    }
    
    /**
     * 
     * @param e
     *         représente la coordonnée est du point.
     * @param n
     *         représente la coordonnée nord du point.
     * @return
     *         retourne la latitude dans le système WGS84 du point dont les coordonnées sont e et n dans le système suisse.
     */
    
    public static double lat(double e, double n) {
        
        double x = 1e-6 * (e-2600000);
        double y = 1e-6 * (n-1200000);

        return Math.toRadians((16.9023892 + 3.238272*y - 0.270978*x*x - 0.002528*y*y - 0.0447*x*x*y - 0.0140*y*y*y) * (100/36d));
    }
}


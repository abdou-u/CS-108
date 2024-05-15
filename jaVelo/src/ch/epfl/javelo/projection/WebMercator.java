package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class WebMercator {
	
	/**
	 * Ce constructeur privé permet à la classe WebMercator d'etre non instanciable.
	 */
	
	private WebMercator() {}

	/**
	 * 
	 * @param lon
	 *        la longitude du point.
	 * @return x
	 *        retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon.
	 */
	
	public static double x(double lon) {
		return (1/(2*Math.PI)) * (lon+Math.PI);
	}
	
	/**
	 * 
	 * @param lat
	 *        la latitude du point.
	 * @return y
	 *        retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat.
	 */
	
	public static double y(double lat) {
		return (1/(2*Math.PI)) * (Math.PI-Math2.asinh(Math.tan(lat)));
	}
	
	/**
	 * 
	 * @param x
	 *        la coordonnée Web Mercator x du point (x,y).
	 * @return lat
	 *        retourne la latitude d'un point dont la projection se trouve à la coordonnée y donnée.
	 */
	
	public static double lon(double x) {
		return 2*Math.PI*x-Math.PI;
	}
	/**
	 * 
	 * @param y
	 *        la coordonnée Web Mercator y du point (x,y).
	 * @return lon
	 *        retourne la longitude d'un point dont la projection se trouve à la coordonnée x donnée.
	 */
	
	public static double lat(double y) {
		return Math.atan(Math.sinh(Math.PI-2*Math.PI*y));
	}
}
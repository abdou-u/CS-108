package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Cet enregistrement représente un point dans le système Web Mercator. 
 * Il a comme attribut la coordonnée x et y du point.
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public record PointWebMercator(double x, double y) {

	private static final int MAGIC_VALUE = 8;
	
    /**
     * Le constructeur compact de PointWebMercator valide les coordonnées qu'il reçoit.
     * @throws IllegalArgumentException
	 *                   lève une exception (IllegalArgumentException) si l'une d'entre elles n'est pas comprise dans l'intervalle [0,1].
     */
    
	 public PointWebMercator { 
	        Preconditions.checkArgument(x>=0 && x<=1 && y>=0 && y<=1);
	        }
	 
	 /**
	  * 
	  * @param zoomLevel
	  *         représente le niveau de zoom en ce moment.
	  * @param x
	  *         représente la coordonnée x du point.
	  * @param y
      *         représente la coordonnée y du point.
	  * @return
	  *         retourne le point dont les coordonnées sont x et y au niveau de zoom zoomLevel.
	  */
	 
	 public static PointWebMercator of(int zoomLevel, double x, double y) {
		return new PointWebMercator(Math.scalb(x, -MAGIC_VALUE-zoomLevel), Math.scalb(y, -MAGIC_VALUE-zoomLevel));
	 }
	 
	 /**
	  * 
	  * @param pointCh
	  *         représente le point du système de coordonnées suisse.
	  * @return
	  *         retourne le point Web Mercator correspondant au point du système de coordonnées suisse donné.
	  */
	 
	 public static PointWebMercator ofPointCh(PointCh pointCh) {
		 return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
	 }
	 
	 /**
	  * 
	  * @param zoomLevel
      *         représente le niveau de zoom voulu.
	  * @return
	  *         retourne la coordonnée x au niveau de zoom donné.
	  */
	 
	 public double xAtZoomLevel(int zoomLevel) {
		 return Math.scalb(x, MAGIC_VALUE+zoomLevel);
	 }
	 
	 /**
      * 
      * @param zoomLevel
      *         représente le niveau de zoom voulu.
      * @return
      *         retourne la coordonnée y au niveau de zoom donné.
      */
	 
	 public double yAtZoomLevel(int zoomLevel) {
		 return Math.scalb(y, MAGIC_VALUE+zoomLevel);
	 }
	 
	 /**
	  * 
	  * @return
      *        retourne la longitude du point, en radians.
	  */
	 
	 public double lon() {
		 return 2*Math.PI*x-Math.PI;
	 }
	 
	 /**
	  * 
	  * @return
	  *        retourne la latitude du point, en radians.
	  */
	 
	 public double lat() {
		 return Math.atan(Math.sinh(Math.PI-2*Math.PI*y));
	 }
	 
	 /**
      * 
      * @return
      *        retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this) 
      *        ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds.
      */
	 
	 public PointCh toPointCh() {
	     
			double x=Ch1903.e(this.lon(),this.lat());
			double y=Ch1903.n(this.lon(),this.lat());
			
			if(SwissBounds.containsEN(x,y)) {
				return new PointCh(x,y);
				}
			else {
				return null;
			}
		}
	 }

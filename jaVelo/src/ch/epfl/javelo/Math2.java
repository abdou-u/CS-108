package ch.epfl.javelo;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Math2 {
    
	/**
	 * Ce constructeur privé permet à la classe Math2 d'etre non instanciable.
	 */
	
    private Math2() {}

    /**
     *
     * @param x
     *         Variable aléatoire.
     * @param y
     *         Variable aléatoire.
     * @return
     *         retourne la partie entière par excès de la division de x par y.
     *
     * @throws IllegalArgumentException
     *         lève une exception (IllegalArgumentException) si x est négatif ou si y est négatif ou nul.
     */
    
    public static int ceilDiv(int x, int y) {
        
            Preconditions.checkArgument(x>=0 && y>0);
           
            return (x+y-1)/y;
            }
        
    
    /**
     * 
     * @param y0
     *         représente l'odonnée au point x=0.
     * @param y1
     *         représente l'odonnée au point x=1.
     * @param x
     *         Variable aléatoire qui n'est pas forcément compris entre 0 et 1.
     * @return
     *         retourne la coordonnée y du point se trouvant sur la droite passant par (0,y0) et (1,y1) et de coordonnée x donnée.
     */
    
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1-y0, x, y0);
    }
    
    /**
     *
     * @param min
     *         représente la valeur minimale que peut etre V.
     * @param v
     *         Variable aléatoire.
     * @param max
     *         représente la valeur maximale que peut etre V.
     * @return
     *         retourne min si v est inférieure à min, max si v est supérieure à max, et v sinon.
     *
     * Cette méthode limite la valeur v à l'intervalle allant de min à max.
     *
     * @throws IllegalArgumentException
     *         lève une exception (IllegalArgumentException) si min est (strictement) supérieur à max.
     *
     * Cette méthode recoit et retourne un int.
     */
    
    public static int clamp(int min, int v, int max) {
        
        Preconditions.checkArgument(min<=max);
        
        if (v<min) {
            return min;
        }
        else {
            return Math.min(v, max);
        }
        }
    
    /**
     * 
     * @param min 
     *         représente la valeur minimale que peut etre V.
     * @param v
     *         Variable aléatoire.
     * @param max
     *         représente la valeur maximale que peut etre V.
     * @return
     *         retourne min si v est inférieure à min, max si v est supérieure à max, et v sinon.
     *         
     * Cette méthode limite la valeur v à l'intervalle allant de min à max.
     *
     * @throws IllegalArgumentException
     *         lève une exception (IllegalArgumentException) si min est (strictement) supérieur à max.
     * 
     * Cette méthode recoit et retourne un double.
     */
    
    public static double clamp(double min, double v, double max) {
        
        Preconditions.checkArgument(min<=max);
    
        if (v<min) {
            return min;
        }
        else {
            return Math.min(v, max);
        }
        }
    
    /**
     * 
     * @param x
     *        variable aléatoire qui représente l'argument passé à asinh.
     * @return
     *        retourne le sinus hyperbolique inverse de son argument x.
     */
    
    public static double asinh(double x) {
        return Math.log(x+Math.sqrt(1+x*x));
    }
    
    /**
     * 
     * @param uX
     *         représente la composante du vecteur u selon l'axe X.
     * @param uY
     *         représente la composante du vecteur u selon l'axe Y.
     * @param vX
     *         représente la composante du vecteur v selon l'axe X.
     * @param vY
     *         représente la composante du vecteur v selon l'axe Y.
     * @return
     *         retourne le produit scalaire entre le vecteur u et le vecteur v.
     */
    
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, uY*vY);
    }
    
    /**
     * 
     * @param uX
     *         représente la composante du vecteur u selon l'axe X.
     * @param uY
     *         représente la composante du vecteur u selon l'axe Y.
     * @return
     *         retourne le carré de la norme du vecteur u.
     */
    
    public static double squaredNorm(double uX, double uY) {
        return uX*uX+uY*uY;
    }
    
    /**
     * 
     * @param uX
     *         représente la composante du vecteur u selon l'axe X.
     * @param uY
     *         représente la composante du vecteur u selon l'axe Y.
     * @return
     *         retourne la norme du vecteur u.
     */
    
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX,uY));
    }
    
    /**
     * 
     * @param aX
     *         représente la composante du vecteur a selon l'axe X.
     * @param aY
     *         représente la composante du vecteur a selon l'axe Y.
     * @param bX
     *         représente la composante du vecteur b selon l'axe X.
     * @param bY
     *         représente la composante du vecteur b selon l'axe Y.
     * @param pX
     *         représente la composante du vecteur p selon l'axe X.
     * @param pY
     *         représente la composante du vecteur p selon l'axe Y.
     * @return
     *         retourne la longueur de la projection du vecteur allant du point A au point P 
     *         sur le vecteur allant du point A au point B.
     */
    
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        return dotProduct(aX-pX, aY-pY, aX-bX, aY-bY)/norm(aX-bX, aY-bY);
    }
}

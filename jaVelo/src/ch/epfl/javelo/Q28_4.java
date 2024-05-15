package ch.epfl.javelo;

/**
 * La classe Q28_4 permet de convertir des nombres entre la représentation Q28.4 et d'autres représentations.
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Q28_4 {
	
	/**
	 * Le constructeur privé permet à cette classe d'etre non instanciable.
	 */
	
    private Q28_4() {}
    
    /**
     * 
     * @param i
     *        représente un entier
     * @return i
     *        retourne la valeur Q28.4 qui correspond à l'entier i donné.
     */
    
    public static int ofInt(int i) {
    	return i*16;
    }
    
    /**
     * 
     * @param q28_4
     *          représente un double
     * @return q28_4
     *          retourne la valeur de type double qui est égale à la valeur Q28.4 donnée.
     */
    
    public static double asDouble(int q28_4) {
    	return Math.scalb((double)q28_4, -4);
    }
    
    /**
     * 
     * @param q28_4
     *         représente un float
     * @return q28_4
     *         retourne la valeur de type float qui correspond à la valeur Q28.4 donnée.
     */
    
    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -4);
    }
}

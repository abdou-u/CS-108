package ch.epfl.javelo;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Bits {
	
	/**
	 * Ce constructeur privé permet à la classe Bits d'etre non instanciable.
	 */
	
    private Bits() {}
    
    /**
     * 
     * @param value
     *        représente la valeur du vecteur extrait de 32 bits
     * @param start
     *        représente l'index de commencement  
     * @param length
     *        représente la longeur de la plage de bits
     * @return 
     *        extrait du vecteur de 32 bits la plage de length bits qui commence au bit d'index start,
     *        qu'elle interprete comme une valeur signée, 
     *        ou lève IllegalArgumentException si la plage de bits décrite n'est pas incluse dans l'intervalle allant de 0 a 31 inclus.
     */
    
    public static int extractSigned(int value, int start, int length) {
    	
    	Preconditions.checkArgument(start>=0 && length>=0 && start+length<=32);
    	
    	value = value << 32-(start+length);
    	
    	return value >> 32-length;
    }
    
    /**
     * 
     * @param value
     *        représente la valeur du vecteur extrait de 32 bits
     * @param start
     *        représente l'index de commencement  
     * @param length
     *        représente la longeur de la plage de bits
     * @return
     *        extrait du vecteur de 32 bits la plage de length bits qui commence au bit d'index start,
     *        qu'elle interprète comme une valeur non signée, 
     *        ou lève IllegalArgumentException si la plage de bits décrite n'est pas incluse dans l'intervalle allant de 0 à 31 inclus.
     */
    
    public static int extractUnsigned(int value, int start, int length) {
    	
    	Preconditions.checkArgument(start>=0 && length>=0 && start+length<=32 && length<32);
    	
    	value = value << 32-(start+length);
    	
    	return value >>> 32-length;
    }


}
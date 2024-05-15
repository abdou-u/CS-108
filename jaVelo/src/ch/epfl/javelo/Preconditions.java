package ch.epfl.javelo;

/**
 * La précondition.
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Preconditions {
	
	/**
	 * 
	 * Le constructeur privé (Preconditions) permet à cette classe d'etre non instanciable.
	 */
    
    private Preconditions() {}

    /**
     *
     * @param shouldBeTrue
     *                 est un boolean (qui représente la précondition qui doit etre vérifièe)
     *
     * @throws IllegalArgumentException
     *                  lance une exception si le boolean insérer est faux.
     *
     * L'appel à cette méthode est fait tout au long du projet ce qui rend le code plus compact.
     */
    
    public static void checkArgument(boolean shouldBeTrue) {

        if (!shouldBeTrue) {
            throw new IllegalArgumentException(); 
            }
    }
}

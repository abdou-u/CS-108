package ch.epfl.javelo.routing;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 * 
 * Cette interface représente une fonction de coût.
 */

public interface CostFunction {
    
    /**
     * 
     * @param nodeId
     *         représente l'identité du nœud.
     * @param edgeId
     *         représente l'identité de l'arête.
     * @return
     *         retourne le facteur (>=1) par lequel la longueur de l'arête d'identité edgeId, 
     *         partant du nœud d'identité nodeId, doit être multipliée.
     * 
     *  le facteur de coût peut être infini (Double.POSITIVE_INFINITY), ce qui exprime le fait que l'arête ne peut absolument pas être empruntée.
     */
    
    double costFactor(int nodeId, int edgeId);
}

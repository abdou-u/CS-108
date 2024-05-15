package ch.epfl.javelo.data;

import java.nio.IntBuffer;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cet enregistrement représente le tableau de tous les nœuds du graphe JaVelo. 
 * Il possède un attribut buffer de type IntBuffer, 
 */

public record GraphNodes(IntBuffer buffer) {
    
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    
    /**
     * 
     * @param buffer
     *            représente la mémoire tampon contenant la valeur des attributs de la totalité des nœuds du graphe.
     */
    
    public GraphNodes {}
    
    /**
     * 
     * @return
     *       retourne le nombre total de nœuds.
     */
    
    public int count() {
        return buffer.capacity()/NODE_INTS;
    }
    
    /**
     * 
     * @param nodeId
     *       représente l'identité du nœud.
     * @return
     *       retourne la coordonnée E du nœud d'identité donnée.
     */
    
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_E));
    }
    
    /**
     * 
     * @param nodeId
     *       représente l'identité du nœud.
     * @return
     *       retourne la coordonnée N du nœud d'identité donnée.
     */
    
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_N));
    }
    
    /**
     * 
     * @param nodeId
     *       représente l'identité du nœud.
     * @return
     *       retourne le nombre d'arêtes sortant du nœud d'identité donné.
     */
    
    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), 28, 4);
    }
    
    /**
     * 
     * @param nodeId
     *       représente l'identité du nœud.
     * @param edgeIndex
     *       représente l'index de l'arête.
     * @return
     *       retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId.
     */
    
    public int edgeId(int nodeId, int edgeIndex) {
        
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId); 
        
        return Bits.extractUnsigned(buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), 0, 28) + edgeIndex;
    }
}

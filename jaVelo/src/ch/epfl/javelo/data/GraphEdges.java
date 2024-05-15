package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cet enregistrement représente le tableau de toutes les arêtes du graphe JaVelo. 
 * Il possède les attributs edgesBuffer de type ByteBuffer, profileIds de type IntBuffer et elevations de type ShortBuffer.
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     * 
     * @param edgesBuffer
     *             représente la mémoire tampon contenant la valeur des attributs de la totalité des arêtes du graphe.
     * @param profileIds
     *             représente la mémoire tampon contenant la valeur des attributs de la totalité des arêtes du graphe.
     * @param elevations
     *             représente la mémoire tampon contenant la totalité des échantillons des profils.
     */
    
    public GraphEdges{}
    
    private final static int OFFSET_DIR_AND_ID = 0;
    private final static int OFFSET_DIR_AND_ID_END= OFFSET_DIR_AND_ID+Integer.BYTES-1;
    private final static int OFFSET_LENGTH = OFFSET_DIR_AND_ID_END+1;
    private final static int OFFSET_LENGTH_END=OFFSET_LENGTH+Short.BYTES-1;
    private final static int OFFSET_DENIV=OFFSET_LENGTH_END+1;
    private final static int OFFSET_DENIV_END=OFFSET_DENIV+Short.BYTES-1;
    private final static int OFFSET_OSM_ID=OFFSET_DENIV_END+1;
    private final static int EDGES_INTS=OFFSET_OSM_ID+Short.BYTES;

    /**
     * 
     * @param edgeId
     *        représente l'identité de l'arête.
     * @return
     *        retourne vrai ssi l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient.
     */
    
    public boolean isInverted(int edgeId) {
        return (edgesBuffer.getInt(edgeId*EDGES_INTS + OFFSET_DIR_AND_ID) < 0);
        }
    
    /**
     * 
     * @param edgeId
     *        représente l'identité des arêtes.
     * @return
     *        retourne l'identité du nœud destination de l'arête d'identité donnée.
     */
    
    public int targetNodeId(int edgeId) {
        return isInverted(edgeId) ? ~edgesBuffer.getInt(edgeId*EDGES_INTS + OFFSET_DIR_AND_ID) : edgesBuffer.getInt(edgeId*EDGES_INTS + OFFSET_DIR_AND_ID);
    }
    
    /**
     * 
     * @param edgeId
     *        représente l'identité des arêtes.
     * @return
     *        retourne la longueur, en mètres, de l'arête d'identité donnée.
     */
    
    public double length(int edgeId) { 
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_LENGTH)));
        }
    
    /**
     * 
     * @param edgeId
     *        représente l'identité des arêtes.
     * @return
     *        retourne le dénivelé positif, en mètres, de l'arête d'identité donnée.
     */
    
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_DENIV)));
        }
    
    /**
     * 
     * @param edgeId
     *        représente l'identité des arêtes.
     * @return
     *        retourne vrai ssi l'arête d'identité donnée possède un profil.
     */
    
    public boolean hasProfile(int edgeId) { 
        return Bits.extractUnsigned(profileIds.get(edgeId), 30, 2)!=0;
        }
    
    /**
     * 
     * @param edgeId
     *        représente l'identité des arêtes.
     * @return
     *        retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     *        qui est vide si l'arête ne possède pas de profil.
     */

    public float[] profileSamples(int edgeId) {
        
        if (!this.hasProfile(edgeId)) {
            return new float[0];
            }
        else {
            
            int profilType = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
            int nb = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_LENGTH)), Q28_4.ofInt(2));
            int firstSample= Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
            
            float [] Tab = new float[nb];
            
            if (profilType== 1) {
                
                for(int i=0;i<nb;i++) {
                    Tab[i]=Q28_4.asFloat(Short.toUnsignedInt(elevations.get(i+firstSample)));
                }   
            }
            else {
                
                Tab[0]=Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSample)));
                
                if(Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) == 2) {
                    
                    for (int i = 1; i < nb; i++) {
                        
                        if (i%2 == 1) {
                            Tab[i] = Tab[i - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSample + (i + 1)/2), 8, 8));
                        } 
                        else {
                            Tab[i] = Tab[i - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSample + i /2), 0, 8));
                        }
                    }
                } 
                else {
                    
                    for (int i = 1; i < nb; i++) {
                        
                        if (i%4 == 1) {
                            Tab[i] = Tab[i - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSample + (i + 3)/4), 12, 4));
                        } 
                        else if (i%4 == 2){
                            Tab[i] = Tab[i - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSample + (i + 2)/4), 8, 4));
                        } 
                        else if (i%4 == 3) {
                            Tab[i] = Tab[i - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSample + (i + 1)/4), 4, 4));
                        } 
                        else {
                            Tab[i] = Tab[i - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSample + i /4), 0, 4));
                        }
                    }
                }
            }
     
            if(isInverted(edgeId)) {
                
                int j=0;
                
                do {
                    float variable = Tab[j];
                    Tab[j] = Tab[Tab.length-1-j];
                    Tab[Tab.length-1-j] = variable;
                    j++;   
                    }
                while(j < (Tab.length)/2);
            }   
        return Tab;
        }
    }
    
    /**
     * 
     * @param edgeId
     *        représente l'identité des arêtes.
     * @return
     *        retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     */
    
    public int attributesIndex(int edgeId) { 
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_OSM_ID));
        }
}
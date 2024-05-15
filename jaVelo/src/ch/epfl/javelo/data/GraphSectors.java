package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cet enregistrement représente le tableau contenant les 16384 secteurs de JaVelo. 
 * Il possède l'attribut buffer de type ByteBuffer, qui représente la mémoire tampon contenant la valeur des attributs de la totalité des secteurs.
 */

public record GraphSectors(ByteBuffer buffer) {
    
    /**
     * 
     * GraphSectors possède un enregistrement imbriqué nommé Sector, représentant un secteur.
     * Il est doté des deux attributs startNodeId qui représente l'identité du premier nœud du secteur
     * et endNodeId qui représente l'identité du nœud situé juste après le dernier nœud du secteur.
     * 
     * Cet enregistrement permet de représenter une manière plus agréable pour utiliser les secteurs.
     */
    
    public record Sector (int startNodeId, int endNodeId) {}

    private static final int OFFSET_FIRSTIDSTART = 0;
    private static final int OFFSET_FIRSTIDEND = OFFSET_FIRSTIDSTART + Integer.BYTES-1;
    private static final int OFFSET_SIZESTART = OFFSET_FIRSTIDEND+1;
    private static final int SECTOR_INTS = OFFSET_SIZESTART+Short.BYTES;
    private final static double SECTOR_HEIGHT = SwissBounds.HEIGHT/128;
    private final static double SECTOR_WIDTH = SwissBounds.WIDTH/128;
    
    /**
     * 
     * @param center
     *        représente le centre du carré utilisé.
     * @param distance
     *        représente la moitié de la longeur du coté du carré utilisé.
     * @return
     *        retourne la liste de tous les secteurs ayant une intersection 
     *        avec le carré centré au point donné et de côté égal au double de la distance donnée.
     */
    
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        
        List<Sector> list = new ArrayList<>();
        
        int sectorID;
        int firstNodeID;
        short sectorSize;
        int unsignedSectorSize;
        
        int xMin = Math2.clamp(0,(int)((Math2.clamp(SwissBounds.MIN_E,center.e() - distance,SwissBounds.MAX_E)-SwissBounds.MIN_E)/SECTOR_WIDTH), 127);
        int xMax = Math2.clamp(0,(int)((Math2.clamp(SwissBounds.MIN_E,center.e() + distance,SwissBounds.MAX_E)-SwissBounds.MIN_E)/SECTOR_WIDTH),127);
        int yMin = Math2.clamp(0,(int)((Math2.clamp(SwissBounds.MIN_N,center.n() - distance,SwissBounds.MAX_N)-SwissBounds.MIN_N)/SECTOR_HEIGHT),127);
        int yMax = Math2.clamp(0,(int)((Math2.clamp(SwissBounds.MIN_N,center.n() + distance,SwissBounds.MAX_N)-SwissBounds.MIN_N)/SECTOR_HEIGHT),127);
 
        for(int y=yMin; y<=yMax; y++) {
            for(int x=xMin; x<=xMax; x++) {
                
                sectorID = y*128 + x;
                firstNodeID= buffer.getInt(SECTOR_INTS*sectorID + OFFSET_FIRSTIDSTART);
                sectorSize = buffer.getShort(SECTOR_INTS*sectorID + OFFSET_SIZESTART);
                unsignedSectorSize = Short.toUnsignedInt(sectorSize);
             
                    list.add(new Sector(firstNodeID, firstNodeID+unsignedSectorSize));
                } 
            }    
        return list;
    }
}
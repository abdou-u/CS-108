package ch.epfl.javelo.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.GraphSectors.Sector;
import ch.epfl.javelo.projection.PointCh;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Graph {
    
    private final GraphNodes nodes;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;
    private final GraphSectors sectors;
    
    /**
     * 
     * @param nodes
     *           représente tous les nœuds du graph qu'on utilise dans cette classe.
     * @param sectors
     *           représente tous les secteurs du graph qu'on utilise dans cette classe.
     * @param edges
     *           représente tous les edges du graph qu'on utilise dans cette classe.
     * @param attributeSets
     *           représente la liste de tous les attributs OSM qu'on utilise dans cette classe.
     *             
     * Ce constructeur publique retourne le graphe avec les nœuds, secteurs, arêtes et ensembles d'attributs donnés.
     */
    
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        
        this.nodes = nodes;
        this.edges = edges;
        this.sectors = sectors;
        
        this.attributeSets = List.copyOf(attributeSets);
    }
    
    /**
     * 
     * @param basePath
     *         représente le chemin d'accès au répertoire ou il y a les fichiers utilisés par la méthode LoadFrom.
     * @return
     *         retourne le graphe JaVelo obtenu à partir des fichiers 
     *         se trouvant dans le répertoire dont le chemin d'accès est basePath.
     * @throws IOException
     *         lève IOException en cas d'erreur d'entrée ou de sortie, par exemple si l'un des fichiers attendu n'existe pas.
     *         
     *  La méthode loadFrom doit déterminer les chemins des différents fichiers (sectors, edges, edlevations, nodes, profile_ids et attributes)
     *  à charger à partir du chemin de base (Path), afin de les passer à la méthode open de FileChannel en utilisant la méthode resolve.
     */
    
    public static Graph loadFrom(Path basePath) throws IOException {
                
        Path sectorsPath = basePath.resolve("sectors.bin");
        ByteBuffer sectorsBuffer;
        try (FileChannel ChannelS = FileChannel.open(sectorsPath)) {
            sectorsBuffer = ChannelS.map(FileChannel.MapMode.READ_ONLY, 0, ChannelS.size());
          }
        
        Path edgesPath = basePath.resolve("edges.bin");
        ByteBuffer edgesBuffer;
        try (FileChannel ChannelED = FileChannel.open(edgesPath)) {
            edgesBuffer = ChannelED.map(FileChannel.MapMode.READ_ONLY, 0, ChannelED.size());
          }
        
        Path elevationsPath = basePath.resolve("elevations.bin");
        ShortBuffer elevationsBuffer;
        try (FileChannel ChannelEL = FileChannel.open(elevationsPath)) {
            elevationsBuffer = ChannelEL.map(FileChannel.MapMode.READ_ONLY, 0, ChannelEL.size()).asShortBuffer();
          }
        
        Path nodesPath = basePath.resolve("nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel ChannelN = FileChannel.open(nodesPath)) {
            nodesBuffer = ChannelN.map(FileChannel.MapMode.READ_ONLY, 0, ChannelN.size()).asIntBuffer();
          }
        
        Path profile_idsPath = basePath.resolve("profile_ids.bin");
        IntBuffer profile_idsBuffer;
        try (FileChannel ChannelP = FileChannel.open(profile_idsPath)) {
            profile_idsBuffer = ChannelP.map(FileChannel.MapMode.READ_ONLY, 0, ChannelP.size()).asIntBuffer();
          }
        
        Path attributesPath = basePath.resolve("attributes.bin");
        LongBuffer attributesBuffer;
        try (FileChannel ChannelA = FileChannel.open(attributesPath)) {
            attributesBuffer = ChannelA.map(FileChannel.MapMode.READ_ONLY, 0, ChannelA.size()).asLongBuffer();
          }
              
        List <AttributeSet> attributes = new ArrayList <>(attributesBuffer.capacity());
        
        for (int i=0; i<attributesBuffer.capacity(); i++) {
            attributes.add(new AttributeSet(attributesBuffer.get(i)));
        }
        
        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer), new GraphEdges(edgesBuffer, profile_idsBuffer, elevationsBuffer), attributes);
    }
    
    /**
     * 
     * @return
     *         retourne le nombre total de nœuds dans le graphe.
     */
    
    public int nodeCount() {
        return nodes.count();
    }
    
    /**
     * 
     * @param nodeId
     *         représente l'identité du nœud.
     * @return
     *         retourne la position du nœud d'identité donnée.
     */
    
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }
    
    /**
     * 
     * @param nodeId
     *         représente l'identité du nœud.
     * @return
     *         retourne le nombre d'arêtes sortant du nœud d'identité donnée.
     */
    
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }
    
    /**
     * 
     * @param nodeId
     *         représente l'identité du nœud.
     * @param edgeIndex
     *         représente l'index de l'edge.
     * @return 
     *         retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId.
     */
    
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }
    
    /**
     * 
     * @param point
     *         représente le point donné.
     * @param searchDistance
     *         représente la distance donnée.
     * @return
     *         retourne l'identité du nœud se trouvant le plus proche du point donné, 
     *         à la distance maximale donnée en mètres, ou -1 si aucun nœud ne correspond à ces critères.
     *         
     * Cette méthode trouve le point le plus proche en comparant les carrés des distances ien utilisant la méthode squaredDistanceTo de PointCh.
     */
    
    public int nodeClosestTo(PointCh point, double searchDistance) {
        
        int nodeId = -1;
        double minDistance = searchDistance*searchDistance;
        
        for (Sector s : sectors.sectorsInArea(point, searchDistance)) {
            for (int i = s.startNodeId(); i<s.endNodeId(); i++) {
                
                double distance = point.squaredDistanceTo(nodePoint(i));
                
                if (distance <= minDistance) {
                    nodeId = i;
                    minDistance = distance;
                }
            }
        }
        return nodeId;
    }
    
    /**
     * 
     * @param edgeId
     *         représente l'identité (index) de l'edge utilisé.
     * @return
     *         retourne l'identité du nœud destination de l'arête d'identité donnée.
     */
    
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }
    
    /**
     * 
     * @param edgeId
     *         représente l'identité (index) de l'edge utilisé.
     * @return
     *         retourne vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient.
     */
    
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }
    
    /**
     * 
     * @param edgeId
     *         représente l'identité (index) de l'edge utilisé.
     * @return
     *         retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée.
     */
    
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }
    
    /**
     * 
     * @param edgeId
     *         représente l'identité (index) de l'edge utilisé.
     * @return
     *         retourne la longueur, en mètres, de l'arête d'identité donnée.
     */
    
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }
    
    /**
     * 
     * @param edgeId
     *         représente l'identité (index) de l'edge utilisé.
     * @return
     *        retourne le dénivelé positif total de l'arête d'identité donnée.
     */
    
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }
    
    /**
     * 
     * @param edgeId
     *         représente l'identité (index) de l'edge utilisé.
     * @return
     *         retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction; 
     *         si l'arête ne possède pas de profil, alors cette fonction retourne Double.NaN pour n'importe quel argument.
     */
    
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId)) : Functions.constant(Double.NaN);
    }
}

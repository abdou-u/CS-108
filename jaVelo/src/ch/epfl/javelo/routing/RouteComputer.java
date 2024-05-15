package ch.epfl.javelo.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe représente un planificateur d'itinéraire.
 */

public final class RouteComputer {
    
    private final Graph graph;
    private final CostFunction costFunction;
    
    /**
     * Le constructeur construit un planificateur d'itinéraire pour le graphe et la fonction de coût donnés.
     * 
     * @param graph
     *         représente le graph utilisé.
     * @param costFunction
     *         représente le facteur de cout.
     */
    
    public RouteComputer(Graph graph, CostFunction costFunction){

        this.graph = graph;
        this.costFunction = costFunction;
    }
    
    /**
     * 
     * @param startNodeId
     *         représente l'identité du nœud de départ.
     * @param endNodeId
     *         représente l'identité du nœud d'arrivé.
     * @return
     *         Retourne l'itinéraire de coût total minimal allant du nœud d'identité startNodeId
     *         au nœud d'identité endNodeId dans le graphe passé au constructeur, 
     *         ou null si aucun itinéraire n'existe. 
     * 
     * Si le nœud de départ et d'arrivée sont identiques, cette méthode lève IllegalArgumentException.
     */
    

    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        
        Preconditions.checkArgument(startNodeId!=endNodeId);
 
        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> { //attention à ne pas confondre distance de WeightedNode et les valeurs du tab distance, puisque dans WeightedNode il s'agit de la somme de la distance effectuée en plus du vol d'oiseau au point d'arrivée
      
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        
       List<Edge> edList= new ArrayList<>();
       float [] distance =new float[graph.nodeCount()];
       int [] pred = new int[graph.nodeCount()];

       Arrays.fill(distance, Float.POSITIVE_INFINITY);
       distance[startNodeId]=0;
      
       PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();
       
       enExploration.add(new WeightedNode(startNodeId,distance[startNodeId] + (float) graph.nodePoint(endNodeId).distanceTo(graph.nodePoint(startNodeId))));
       
       while(!enExploration.isEmpty()) {
         
             WeightedNode n = enExploration.remove();
             
             if(distance[n.nodeId]==Float.NEGATIVE_INFINITY) continue; //ici le point n'est simplement retiré de la liste et ignoré puisqu'on l'a marqué par une valeur qui signale au programme qu'il ne peut rien faire de mieux pour ce point

             if(n.nodeId==endNodeId) {  //on a fini d'explorer le nœud d'arrivée, on peut créer notre liste en partant de la fin
                 
                 int dest=endNodeId;  //dans cette boucle, dest représente le nœud auquel on souhaite voir notre arête arriver
                 int deb=pred[dest]; //deb représente le prédécesseur de dest selon la logique de l'algorithme
                 
                 do {
                     int i=-1;
                     do {
                         i++;   
                     }while(graph.edgeTargetNodeId(graph.nodeOutEdgeId(pred[dest], i))!=dest); //on itère sur l'ensemble des arêtes sortantes de deb, on veut obtenir celle qui arrive a dest
                     
                     edList.add(Edge.of(graph, graph.nodeOutEdgeId(deb, i), deb, dest));
                     
                     dest=deb; //puisqu'on recule, dest va à présent être notre nœud duquel sort l'arête, pour voir l'arête qui le vise à son tour
                     deb=pred[deb];
                     
                 }while(dest!=startNodeId); //l'étape n'est terminée que lorsque l'on parvient à atteindre le nœud de départ
                 
                 Collections.reverse(edList); //on doit inverser la liste qu'on a rempli en partant de la fin
                 
                 return new SingleRoute(edList);
             }
                 
             for(int i=0;i<graph.nodeOutDegree(n.nodeId);i++) {
                 
                 int n2 = graph.edgeTargetNodeId(graph.nodeOutEdgeId(n.nodeId, i));
                 float d=  distance[n.nodeId] + (float)(graph.edgeLength(graph.nodeOutEdgeId(n.nodeId, i)) * costFunction.costFactor(n.nodeId, graph.nodeOutEdgeId(n.nodeId, i)));
                
                 if(d<distance[n2]) {
                     
                     distance[n2]=d;
                     pred[n2]=n.nodeId;
                     
                     enExploration.add(new WeightedNode(n2,distance[n2]+(float) graph.nodePoint(endNodeId).distanceTo(graph.nodePoint(n2))));
                 }  
             }
             distance[n.nodeId]= Float.NEGATIVE_INFINITY; //on marque le point n qui a deja été exploré
       }
       return null; 
    }
}

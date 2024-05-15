package ch.epfl.javelo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import ch.epfl.javelo.Preconditions;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 * 
 * Cet enregistrement représente un ensemble d'attributs OpenStreetMap. 
 * Il possède un attribut bits de type long, qui représente le contenu de l'ensemble au moyen d'un bit par valeur possible
 */

public record AttributeSet(long bits) {

    /**
     * 
     * @param bits 
     *          représente le contenu de l'ensemble au moyen d'un bit par valeur possible;
     *          c'est-à-dire que le bit d'index b de cette valeur vaut 1 
     *          si et seulement si l'attribut b est contenu dans l'ensemble.
	 * @throws IllegalArgumentException
	 *          lève une exception si l'attribut n'est pas contenu dans l'ensemble.
     */
    
	public AttributeSet {
        Preconditions.checkArgument(bits>=0 && bits < (1L << Attribute.COUNT));
	}
	
	/**
	 * 
	 * @param attributes
	 *        représente l'ellipse contenant les attributs demandés.
	 * @return 
	 *        retourne un ensemble contenant uniquement les attributs donnés en argument.
	 */

	public static AttributeSet of(Attribute... attributes) {

		List<Integer> list = new ArrayList<>();
		long bits = 0;

		for (Attribute bit : attributes) {
			if(!list.contains(bit.ordinal())) {
				long b = 1L << bit.ordinal();
				bits += b;
				list.add(bit.ordinal());
			}
		}
		return new AttributeSet(bits);
	}




	/**
	 * 
	 * @param attribute 
	 *        l'attribut entré de la liste des attributs.
	 * @return
	 *        retourne vrai si et seulement si l'ensemble récepteur (this) contient l'attribut donné.
	 */
	
	public boolean contains(Attribute attribute){
	      
	    long mask = 1L << attribute.ordinal();
	       
	    return (this.bits & mask)!=0L;
	      }
	
	/**
	 * 
	 * @param other
	 *        l'attributeSet qu'on doit comparer avec le notre.
	 * @return
	 *        retourne vrai ssi l'intersection de l'ensemble récepteur (this) avec celui passé en argument (that) n'est pas vide.
	 */
	
	public boolean intersects(AttributeSet other){ 
	    return((this.bits & other.bits)!=0L);
	      }
	
	/**
	 * On redéfinit la méthode toString afin qu'elle retourne une chaîne composée
	 * de la représentation textuelle des éléments de l'ensemble entourés 
	 * d'accolades ({}) et séparés par des virgules.
	 */
	
	@Override
	public String toString() {
	    
	   StringJoiner j = new StringJoiner(",", "{", "}");
	    
	    for (Attribute a : Attribute.values()) {
	        
	        if (this.contains(a)) {
	            j.add(a.toString());
	            }
	        }
	    return j.toString();
    }
	}

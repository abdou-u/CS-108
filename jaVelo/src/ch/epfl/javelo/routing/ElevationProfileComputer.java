package ch.epfl.javelo.routing;

import java.util.Arrays;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe représente un calculateur de profil en long.
 */

public final class ElevationProfileComputer {
    
    /**
     * Ce constructeur privé permet à cette classe d'etre non instanciable.
     */
    
    private ElevationProfileComputer() {}
    
    /**
     * 
     * @param route
     *       représente l'itinéraire utilisé.
     * @param maxStepLength
     *       représente l'espacement entre les échantillons du profil.
     * @return
     *       Retourne le profil en long de l'itinéraire route, en garantissant que l'espacement entre les échantillons du profil est d'au maximum maxStepLength mètres.
     *
     * @throws IllegalArgumentException
     *       Lève IllegalArgumentException si cet espacement n'est pas strictement positif.
     */
    
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        
        Preconditions.checkArgument(maxStepLength>0);
        
        int echNumber = (int) Math.ceil(route.length() / maxStepLength) +1;
        double echSpace = route.length()/(echNumber-1);
        float[] echProfile = new float [echNumber];
        
        for (int i = 0; i<echNumber; i++) echProfile[i] = (float) route.elevationAt(echSpace*i);

        int j=-1;
        do {
            j++;
        } while(Float.isNaN(echProfile[j])&&(j<echProfile.length-1)); //on recherche l'index de la premiere valeur différente de NaN dans le tab

        if (j==echProfile.length-1) {
            Arrays.fill(echProfile, Float.isNaN(echProfile[j]) ? 0 : echProfile[j]);
            return new ElevationProfile(route.length(), echProfile);
        }
        else {
            if(j>0) Arrays.fill(echProfile, 0, j, echProfile[j]);

            int k=echProfile.length;
            do {
                k--;
            } while(Float.isNaN(echProfile[k])); //en partant de la fin, on recherche l'index de la première valeur qui n'est pas un NaN

            if (k<echProfile.length-1) Arrays.fill(echProfile, k, echProfile.length, echProfile[k]);

            int start=j;
            int end; //on va à présent itérer sur la partie du tab commençant en j et se terminant en k, en recherchant tous les groupes de NaN (peut être un groupe de 1) et en interpolant linéairement dessus

            do {
                int z=start;
                do {
                    z++;
                } while(!Float.isNaN(echProfile[z])&&(z<echProfile.length-1)&&(z<k)); //on recherche l'index du premier NaN sur notre chemin

                start=z-1; //start est l'élément qui précède le premier NaN
                end=z;

                if(end<k) {

                    do {
                        end++;
                    } while(Float.isNaN(echProfile[end])&&(end<k)&&(end<echProfile.length-1)); //on continue d'avancer jusqu'à tomber sur une valeur qui n'est pas un NaN
                }

                for (int x=start+1;x<end;x++)  //une fois qu'on a notre premier (start+1) et dernier NaN (end-1), on interpole linéairement tout ce groupe de NaN grâce à la valeur qui les précède (start) et celle qui les succède (end)
                    echProfile[x]=(float)Math2.interpolate(echProfile[start],echProfile[end], ((x-start-1)+1f)/((end-start-1)+1f));

                start=end;
            } while(end<k);
        }
        return new ElevationProfile(route.length(),echProfile);
    }
}
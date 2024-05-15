package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * 
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 */

public final class Functions {
    
    /**
     * 
     * @param y
     *        la constante retournée par la fonction.
     * @return
     *        la fonction f(x)=constante.
     */
    
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
      }
    
    /**
     * 
     * @param samples
     *        représente des échantillons espacés régulièrement
     * @param xMax
     *        représente la plus grande valeur possible de x
     * @return
     *        retourne une fonction par interpolation linéaire entre les extrémités de chaque échantillon
     *        
     * Cette méthode lève IllegalArgumentException si le tableau samples contient moins de deux éléments, ou si xMax est inférieur ou égal à 0.
     */
    
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        
        Preconditions.checkArgument((samples.length>=2) && (xMax>0)); 
        
        return new Sampled(samples,xMax);
    }

    /**
     * Cette classe redéfinit la méthode de l'interface DoubleUnaryOperator applyAsDouble
     * pour qu'elle retourne la meme valeur recue dans l'argument comme une fonction mathématique constante.
     *
     */

      private static final class Constant implements DoubleUnaryOperator {

          private final double c;

          public Constant(double c) {
              this.c=c;
          }

        @Override
        public double applyAsDouble(double x) {
            return c;
        }
      }

      /**
       * cette classe redéfinit la methode applyAsDouble de l'interface
       * pour donner une fonction obtenue par interpolation linéaire entre les échantillons samples,
       * espacés régulièrement et couvrant la plage allant de 0 à xMax.
       *
       */

      private static final class Sampled implements DoubleUnaryOperator {

          private final float[] samples;
          private final double xMax;

          public Sampled(float[] samples, double xMax) {

             this.samples=samples;
             this.xMax=xMax;
          }

          @Override
          public double applyAsDouble(double x) {

           if(x<=0) {
            return samples[0];
           }

           else if(x>=xMax) {
        	   return samples[samples.length-1];
           }

           else {

               double interval = xMax/(samples.length-1);
               double fraction = x/interval;

               return Math2.interpolate(samples[(int)fraction], samples[(int)fraction+1], fraction - (int)fraction);
          }
         }
     }
}



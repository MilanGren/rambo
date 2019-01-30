/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import java.util.Random;
import logger.Logger.*;
import static logger.Logger.*;

/**
 *
 * @author gre
 */
public class Utils {
    
    
    
    
    
    public static double sqrtform(double a, double b) {
        return Math.pow(Math.pow(a,2) + Math.pow(b,2) , 0.5) ;
    }
    
    public static double getRandom(double min, double max) {

            if (min >= max) {
                    throw new IllegalArgumentException("max must be greater than min");
            }

            Random r = new Random();
            
            return min + (max - min) * r.nextDouble();
  
    }

    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    public static double scalar2(double[] a,double[] b) {
        double sizeA = Math.pow((Math.pow(a[0],2) + Math.pow(a[1],2)),0.5) ;
        double sizeB = Math.pow((Math.pow(b[0],2) + Math.pow(b[1],2)),0.5) ;
        
        logUtils("a " + (a[0]/sizeA) + " " + (a[1]/sizeA)) ;
        logUtils("b " + (b[0]/sizeB) + " " + (b[1]/sizeB)) ;
        
        double cosAlpha = (a[0]*b[0]+a[1]*b[1])/sizeA/sizeB ;
        double direction ;
        if (a[0]*b[1] - a[1]*b[0] < 0) {
            direction = -1 ;
        } else {
            direction = 1 ;
        }
        return toDeg(direction*Math.acos(cosAlpha)) ;
    }
    
    public static double scalar(double[] a,double[] b) {
        double sizeA = Math.pow((Math.pow(a[0],2) + Math.pow(a[1],2)),0.5) ;
        double sizeB = Math.pow((Math.pow(b[0],2) + Math.pow(b[1],2)),0.5) ;
        
        double cosAlpha = (a[0]*b[0]+a[1]*b[1])/sizeA/sizeB ;
        double direction ;
        if (a[0]*b[1] - a[1]*b[0] < 0) {
            direction = -1 ;
        } else {
            direction = 1 ;
        }
        return toDeg(direction*Math.acos(cosAlpha)) ;
    }
    
    public static double angleDirection(double[] a,double[] b) {
        double direction ;
        if (a[0]*b[1] - a[1]*b[0] < 0) {
            direction = -1 ;
        } else {
            direction = 1 ;
        }
        return direction ;
    }
    
    public static double toRad(double x) {
        return x*Math.PI/180 ; 
    }
    
    public static double toDeg(double x) {
        return x*180/Math.PI ;
    }
    
}

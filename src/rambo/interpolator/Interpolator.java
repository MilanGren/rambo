/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo.interpolator;

/**
 *
 * @author gre
 */
public class Interpolator {
    
    private int ilast = 0 ;
    private final double[] inds, deps ;
    
    public Interpolator(double[] x, double[] y) {
        this.inds = x ;
        this.deps = y ;
    }
    
    public double linear(double x,double x1, double x2, double y1, double y2) {
        return (y2 - y1) / (x2 - x1) * (x - x1) + y1 ;
    }
    
    public double interpolate(double x) {
        int ileft = bracket(x) ;
        double x1 = inds[ileft] ;
        double x2 = inds[ileft+1] ;
        double y1 = deps[ileft] ;
        double y2 = deps[ileft+1] ;
        double ans = linear(x,x1,x2,y1,y2) ;
        return ans ;
    }
    
    public int bracket(double x) {

        if (x <= inds[0]) {
            ilast = 0 ;
        } else if (x >= inds[inds.length-2]) {
            ilast = inds.length-2 ;
        } else {
            int low = 0 ;
            int high = inds.length -1 ;
            
            while ( !(x >= inds[ilast] && x<inds[ilast+1]) ) {
                if (x > inds[ilast]) {
                    low =  ilast + 1 ;
                    ilast = (high - low)/2 + low ;
                } else {
                    high = ilast - 1 ;
                    ilast = high - (high - low)/2 ;
                }
            }
        }

        return ilast ;
        
    }
    
    public <T> void log(T t) {
        System.out.println("Interpolator " + t) ;
    }

    
}

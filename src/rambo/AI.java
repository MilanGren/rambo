/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import java.util.ArrayList;
import java.util.List;
import rambo.interpolator.Interpolator;
import robocode.Rules;

//import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
//import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

/**
 *
 * @author gre
 */
public class AI {
    
    public static final double WALLMARGIN = 160 ; //150
    public static final double FIRSTRINGRADIUS = 250 ;
    public static final double SECONDRINGRADIUS = 500 ;
    
    public List<Double> xVec = new ArrayList<>() ;
    public List<Double> yVec = new ArrayList<>() ;
    public List<Integer> hitVec = new ArrayList<>() ;
    
    public double dtime = 0 ;
    
    public boolean allowFire = true ;

    boolean useLog = true ;
    
    public double bulletSpeed ;
    
    public AI(boolean useLog) {
        this.useLog = useLog ;
    }
    
    
    
    public <T> void logAI(T t) {
        //System.out.println("Rambo AI " + t) ;
    }
    
    
    public double getFirepower(double distance)  {
        
        double x[] = {100,FIRSTRINGRADIUS,SECONDRINGRADIUS} ;
        //double y[] = {Rules.MAX_BULLET_POWER,Rules.MAX_BULLET_POWER/2,1.0} ;
        double y[] = {Rules.MAX_BULLET_POWER,Rules.MAX_BULLET_POWER*2/3,Rules.MAX_BULLET_POWER/2} ;
        
        Interpolator l = new Interpolator(x,y) ;
        
        double firepower = l.interpolate(distance) ;
        
        bulletSpeed = 20 - 3*firepower ;
        
        logAI("distance = " + distance + " => firepower " + firepower + " => bullet speed " + bulletSpeed) ;
    
        return firepower ;
        
    }
    
    
    
    
    public double getTotalDistance() {
        double tot = 0;
        for(int i=0; i<xVec.size()-1; i++){
            if (i == 0) {
                tot += 0 ;
            } else {
                tot += Math.pow(Math.pow(xVec.get(xVec.size()-1)-xVec.get(xVec.size()-2),2) + Math.pow(yVec.get(yVec.size()-1)-yVec.get(yVec.size()-2),2) , 0.5) ;
            }
        }
        return tot ;
    }
    
    /*
    
    public double getTotalTime() {
        double tot = 0 ;
        for(int i=0; i<timeVec.size()-1; i++){
            tot += timeVec.get(i) ;
        }
        return tot ;
        
    }
 
    */
    public double getAveHitDt() {
        double aveHitDt = 12 ;
        boolean doCalcul = false ;
        
        double discriminant = 2 ;
        
        //pokud discrimininat = 2, potom neuvazuju prvni dva zaznamy
        
        for (int i = 0; i < hitVec.size() - 1; i++) {
            if ( i < discriminant) {
                
            } else {
                doCalcul = true ;
                double hitDt = hitVec.get(i) - hitVec.get(i-1) ;
                aveHitDt += hitDt ;
           //     logAI(" dt " + hitDt) ;
            }
        }
        
        if (doCalcul) {
            aveHitDt = aveHitDt/(hitVec.size()-discriminant) ;
        }
        
        return aveHitDt ;
        
        
    }
    
    
}

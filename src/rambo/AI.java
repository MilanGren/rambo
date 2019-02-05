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
    public static final double FIRSTRINGRADIUS = 120 ;
    public static final double SECONDRINGRADIUS = 150 ;
    
    public List<Double> xVec = new ArrayList<>() ;
    public List<Double> yVec = new ArrayList<>() ;
    public List<Integer> hitVec = new ArrayList<>() ;
    
    public double dtime = 0 ;
    
    public boolean allowFire = true ;
    
    private double bulletSpeed, firePower ;
    

    
    public double getBulletSpeed() {
        return bulletSpeed ;
    }
    
    public double getFirePower() {
        return firePower ;
    }
    
    public <T> void logAI(T t) {
        //System.out.println("Rambo AI " + t) ;
    }
    
    
    public void setFirePower(double distance)  {
        
        double x[] = {100,FIRSTRINGRADIUS,SECONDRINGRADIUS,2000} ;
        //double y[] = {Rules.MAX_BULLET_POWER,Rules.MAX_BULLET_POWER/2,1.0} ;
        double y[] = {Rules.MAX_BULLET_POWER,Rules.MAX_BULLET_POWER*2/3,Rules.MAX_BULLET_POWER/2,1} ;
        
        Interpolator l = new Interpolator(x,y) ;
        
        firePower = l.interpolate(distance) ;
        
        bulletSpeed = 20 - 3*firePower ;
        
        logAI("distance = " + distance + " => firepower " + firePower + " => bullet speed " + bulletSpeed) ;
    
        
        
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
                logAI(" dt " + hitDt) ;
            }
        }
        
        if (doCalcul) {
            aveHitDt = aveHitDt/(hitVec.size()-discriminant) ;
        }
        
        return aveHitDt ;
        
        
    }
    
    
}

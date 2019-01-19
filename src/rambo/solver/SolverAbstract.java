/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo.solver;

import java.util.List;

/**
 *
 * @author gre
 */
public abstract class SolverAbstract {
    
    public double distanceOriginal, distanceActual, velocity, alpha, dA, dB, bulletVelocity, additionalAngle ;
    public List<Double> velVec ;
    
    int i = 0 ;
    
    
    public SolverAbstract(double distance, double velocity, List<Double> velVec, double alpha, double dA, double dB, double bulletVelocity) {
        
        this.distanceOriginal = distance ;
        this.distanceActual = distance ;
        this.velVec = velVec ;
        this.velocity = velocity ;
        this.alpha = alpha ;
        this.dA = dA ;
        this.dB = dB ;
        this.bulletVelocity = bulletVelocity ;

    }
    
    protected <T> void logSolver(T t) {
        System.out.println("Solver " + t) ;
    }
    
    protected double timeForBullet(double distance,double bulletVelocity) {
        double timeForBullet = distance/bulletVelocity ;
        //log("bullet velocity " + bulletVelocity) ;
        //log("distance " + distance) ;
        //log("enemy time for bullet " + timeForBullet) ;
        return timeForBullet ;
    }
    
    public abstract void solve() ;
    
    
    
}

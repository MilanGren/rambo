/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo.solver;

import java.util.List;
import rambo.Enemy;

/**
 *
 * @author gre
 */
public abstract class SolverAbstract {
    
    public double distanceOriginal, distanceActual, dA, dB, bulletVelocity, additionalAngle ;

    
    Enemy enemy ;
    
    int i = 0 ;
    
    
    //public SolverAbstract(double distance, double velocity, List<Double> velVec, double alpha, double dA, double dB, double bulletVelocity) {
    public SolverAbstract(Enemy enemy, double dA, double dB, double bulletVelocity) {    
        this.enemy = enemy ;
        this.distanceOriginal = enemy.distance ;
        this.distanceActual = enemy.distance ;
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

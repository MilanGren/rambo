/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import rambo.solver.SolverAbstract ;
import rambo.solver.SolverBasic ;
import rambo.solver.SolverAdvanced ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gre
 */
public class Enemy {
    
    boolean useLog = true ;
    
    public Enemy(boolean useLog) {
        this.useLog = useLog ;
    }
   
    public <T> void logEnemy(T t) {
        if (useLog) {
            //System.out.println("Enemy " + t) ;
        }
    }
    
    public double distance, velocity, angle, enemyHeading, alpha, additionalAngle ;
    
    private double dA, dB ;
    
    private double xC, yC, dx, dy, absAngle ; // udelat gettery???
    
    Map<Double, Double[]> dXYmap = new HashMap<>() ;
    
    public List<Double> dx_vec = new ArrayList<>() ;
    public List<Double> dy_vec = new ArrayList<>() ;
    public List<Double> xC_vec = new ArrayList<>() ;
    public List<Double> yC_vec = new ArrayList<>() ;
    public List<Integer> time_vec = new ArrayList<>() ;
    public List<Double> eneVec = new ArrayList<>() ;
    public List<Boolean> wasFired = new ArrayList<>() ;
    public List<Double> velVec = new ArrayList<>() ;
    //List<Double> velAveVec = new ArrayList<>() ;
    public List<Double> accelVec = new ArrayList<>() ;
    public List<Integer> accelDirVec = new ArrayList<>() ;
    
    
    public void set(double xC, double yC,double dx, double dy, double absAngle,double energy,long time) {
        
        this.xC = xC ;
        this.yC = yC ;
        this.dx = dx ;
        this.dy = dy ;
        this.absAngle = absAngle ;
        dx_vec.add(dx) ;
        dy_vec.add(dy) ;
        xC_vec.add(xC) ;
        yC_vec.add(yC) ;
        time_vec.add((int) time) ;
        eneVec.add(energy) ;
        
        double dds ;
        if (time_vec.size() == 1) {
            velVec.add(0.0) ;
            //velAveVec.add(0.0) ;
            accelVec.add(0.0) ;
            wasFired.add(false) ;
            accelDirVec.add(0) ;
            
        } else {
            dds = Math.pow( Math.pow(xC_vec.get(xC_vec.size()-1)-xC_vec.get(xC_vec.size()-2),2) + Math.pow(yC_vec.get(yC_vec.size()-1)-yC_vec.get(yC_vec.size()-2),2) , 0.5) ;
            double dtime = time_vec.get(time_vec.size()-1) - time_vec.get(time_vec.size()-2) ; 
            
            
            velVec.add(dds/dtime*velocity/Math.abs(velocity)); //zde je HACKem vyresen problem +/- rychlosti
            
            
            double v0 = velVec.get(velVec.size()-2) ; //musi byt ZA pridanim do velVec
            double vAve = (velVec.get(velVec.size()-1) + velVec.get(velVec.size()-2))/2 ;
            
            //velAveVec.add( vAve ) ;
                    
            double accel = 2*(vAve-v0)/dtime ;
            accelVec.add(accel) ;
            
            if (Math.abs(accel) < 0.00001) {
                logEnemy(" no speed change");
                accelDirVec.add(0) ;
            } else if (accel*vAve < 0) {
                logEnemy(" sloving down");
                accelDirVec.add(-1) ;
            } else {
                logEnemy(" accelerating");
                accelDirVec.add(1) ;
            }
            
            
           
            
            if ( Math.abs(eneVec.get(eneVec.size()-1)-eneVec.get(eneVec.size()-2) ) > 1) {
                wasFired.add(true) ;
            } else {
                wasFired.add(false) ;
            }
            
            logEnemy("AI enemy listing") ;
            int index = 0 ; 
            for (int t: time_vec) {
 
                logEnemy(" time " + t 
                         + " dx " + Utils.round(xC_vec.get(index),1) 
                         + " dy " + Utils.round(yC_vec.get(index),1)
                         + " vel " + Utils.round(velVec.get(index),2)
                         //+ " velAve " + Utils.round(velAveVec.get(index),2)
                         + " accel " + Utils.round(accelVec.get(index),2)
                         + " accelDir " + Utils.round(accelDirVec.get(index),2)
                         + " energy " + Utils.round(eneVec.get(index),2)
                         + " wasFired " + wasFired.get(index) ) ;
                index++ ;
            }
        }
    }
    
    public void setForFire(double distance, double velocity, double angle, double enemyHeading) {
        this.distance = distance ;
        this.velocity = velocity ;
        this.angle = angle ;     //absolute - odklon nepritele od Y souradnice
        this.enemyHeading = enemyHeading ;
        this.alpha =  this.angle - this.enemyHeading ; // uhel, pod kterym nepritel unika v ramci spojnice ja-nepritel
        logEnemy("velocity " + velocity) ;
        logEnemy("angle " + normalizeBearing(angle)) ;
        logEnemy("alpha " + normalizeBearing(alpha)) ;
        logEnemy("heading " + enemyHeading) ;
        logEnemy("heading relative to alpha " + normalizeBearing(alpha) ) ;
    }
   
    
    public double[] direction() {
        double[] vec = {dx,dy} ;
        return vec ;
    }
    
    public double getAdditionalAngle() {
        return this.additionalAngle ;
    }
    
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    
    private double timeForBullet(double distance,double bulletVelocity) {
        double timeForBullet = distance/bulletVelocity ;
        //log("bullet velocity " + bulletVelocity) ;
        //log("distance " + distance) ;
        //log("enemy time for bullet " + timeForBullet) ;
        return timeForBullet ;
    }
    

    public void fin(double bulletVelocity) {
        SolverAbstract solver = new SolverAdvanced(this, 0, 0, bulletVelocity) ;
        //SolverAbstract solver = new SolverBasic(this, 0, 0, bulletVelocity) ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ; 
        this.additionalAngle = solver.additionalAngle ;
    }
    
    
    
}

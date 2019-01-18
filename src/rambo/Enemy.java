/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

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
            System.out.println("Enemy" + t) ;
        }
    }
    
    private double distance, velocity, angle, t0, enemyHeading, alpha, additionalAngle ;
    
    private double dA, dB ;
    
    private double xC, yC, dx, dy, absAngle ; // udelat gettery???
    
    Map<Double, Double[]> dXYmap = new HashMap<>() ;
    
    List<Double> dx_vec = new ArrayList<>() ;
    List<Double> dy_vec = new ArrayList<>() ;
    List<Double> xC_vec = new ArrayList<>() ;
    List<Double> yC_vec = new ArrayList<>() ;
    List<Integer> time_vec = new ArrayList<>() ;
    List<Double> velocity_vec = new ArrayList<>() ;
    
    public void set(double xC, double yC,double dx, double dy, double absAngle,long time) {
        
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
        
        double dds ;
        if (time_vec.size() == 1) {
            dds = 0 ;
            velocity_vec.add(3.0) ;
            
        } else {
            dds = Math.pow( Math.pow(xC_vec.get(xC_vec.size()-1)-xC_vec.get(xC_vec.size()-2),2) + Math.pow(yC_vec.get(yC_vec.size()-1)-yC_vec.get(yC_vec.size()-2),2) , 0.5) ;
            double dtime = time_vec.get(time_vec.size()-1) - time_vec.get(time_vec.size()-2) ; 
            velocity_vec.add(dds/dtime) ;
            logEnemy(" AI " + dx_vec.size()) ;
            logEnemy(" AI " + time_vec.size()) ;
            logEnemy(" AI " + velocity_vec.size()) ;
            logEnemy(" AI listing time vec ") ;
            int index = 0 ;
            for (int t: time_vec) {
                //change of distance per unit time
                //logAI("AI enemy " + t + " dx " + xC_vec.get(index) + " dy " + xC_vec.get(index) + " velocity " + velocity_vec.get(index)) ;
                index++ ;
            }
        }
    }
    
    public void setForFire(double distance, double velocity, double angle, double enemyHeading, double t0) {
        this.distance = distance ;
        this.velocity = velocity ;
        this.angle = angle ;     //absolute - odklon nepritele od Y souradnice
        this.t0 = t0 ;
        this.enemyHeading = enemyHeading ;
        this.alpha =  this.angle - this.enemyHeading ; // uhel, pod kterym nepritel unika v ramci spojnice ja-nepritel
        logEnemy("enemy velocity " + velocity) ;
        logEnemy("enemy angle " + normalizeBearing(angle)) ;
        logEnemy("enemy alpha " + normalizeBearing(alpha)) ;
        logEnemy("enemy heading " + enemyHeading) ;
        logEnemy("enemy heading relative to alpha " + normalizeBearing(alpha) ) ;
    }
   
    
    public double[] direction() {
        double[] vec = {dx,dy} ;
        return vec ;
    }
    
    public double getAdditionalAngle() {
        return this.additionalAngle ;
    }
    
 
    
    double toRad(double x) {
        return x*Math.PI/180 ; 
    }
    
    double toDeg(double x) {
        return x*180/Math.PI ;
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
    
            
    
    void fin(double bulletVelocity) {
        Solver solver = new Solver(this.distance,this.velocity,this.velocity_vec, this.alpha, 0, 0, bulletVelocity) ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ; 
        this.additionalAngle = solver.additionalAngle ;
    }
    
    void finalizeOld(double t1,double bulletVelocity) {
        double additionalTime = (t1 - this.t0) ;

        double distance0 = this.distance ;
        logEnemy("\nOLD-- i0 -- distance " + distance0) ;
        double dt = additionalTime + timeForBullet(distance0,bulletVelocity) ;
        double ds = dt*this.velocity ;
        //logEnemy("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        logEnemy("                            " + alpha + ", " + ds + ", " + distance0) ;
        double dA = Math.cos(toRad(alpha))*ds ;
        double dB = Math.sin(toRad(alpha))*ds ;
        logEnemy("  => enemy dA " + dA) ;
        logEnemy("  => enemy dB " + dB) ;
        double projection = this.distance + dA ;
        this.additionalAngle = toDeg(-Math.atan(dB/projection)) ;
        logEnemy("  dB " + dB + "  projection " + (projection)) ;
        logEnemy("  dB/projection " + (dB/projection)) ;
        logEnemy("  fixing additional angle " + this.additionalAngle) ;
        
        
        double distance1 = Math.pow(Math.pow(this.distance + dA,2) + Math.pow(dB,2),0.5) ;
        logEnemy("\n-- i1 -- distance " + distance1) ;
        dt = additionalTime + timeForBullet(distance1,bulletVelocity) ;
        ds = dt*this.velocity ;
        //logEnemy("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        logEnemy("                            " + alpha + ", " + ds + ", " + distance1) ;
        dA = Math.cos(toRad(alpha))*ds ;
        dB = Math.sin(toRad(alpha))*ds ;
        logEnemy("  => enemy dA " + dA) ;
        logEnemy("  => enemy dB " + dB) ;
        projection = this.distance + dA ;
        this.additionalAngle = toDeg(-Math.atan(dB/projection)) ;
        logEnemy("  dB " + dB + "  projection " + (projection)) ;
        logEnemy("  dB/projection " + (dB/projection)) ;
        logEnemy("  fixing additional angle " + this.additionalAngle) ;
        logEnemy("\n") ;
        
        
    }
    
    
    
}

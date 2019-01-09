/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

/**
 *
 * @author gre
 */
public class Solver {
    
    public double distanceOriginal, distanceActual, velocity, alpha, dA, dB, bulletVelocity, additionalAngle ;
    
    int i = 0 ;
    
    
    public Solver(double distance, double velocity, double alpha, double dA, double dB, double bulletVelocity) {
        
        this.distanceOriginal = distance ;
        this.distanceActual = distance ;
        this.velocity = velocity ;
        this.alpha = alpha ;
        this.dA = dA ;
        this.dB = dB ;
        this.bulletVelocity = bulletVelocity ;

    }
    
    protected <T> void log(T t) {
        //System.out.println(t) ;
    }
    
    protected double toRad(double x) {
        return x*Math.PI/180 ; 
    }
    
    protected double toDeg(double x) {
        return x*180/Math.PI ;
    }
    
    protected double timeForBullet(double distance,double bulletVelocity) {
        double timeForBullet = distance/bulletVelocity ;
        //log("bullet velocity " + bulletVelocity) ;
        //log("distance " + distance) ;
        //log("enemy time for bullet " + timeForBullet) ;
        return timeForBullet ;
    }
    
    public synchronized void solve() {

        distanceActual = Math.pow(Math.pow(this.distanceOriginal + dA,2) + Math.pow(dB,2),0.5) ;
        
        log("\nNEW-- " + this.i + " -- distance " + this.distanceActual) ;
        double dt = timeForBullet(this.distanceActual,this.bulletVelocity) ;
        double ds = dt*this.velocity ;
        //log("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        log("                                                   " + alpha + ", " + ds + ", " + distanceActual) ;
        dA = Math.cos(toRad(alpha))*ds ;
        dB = Math.sin(toRad(alpha))*ds ;
        log("  => dA " + dA) ;
        log("  => dB " + dB) ;
        double projection = this.distanceOriginal + dA ;
        this.additionalAngle = toDeg(-Math.atan(dB/projection)) ;
        double xx = toDeg(-Math.atan(dB/projection)) ;
        
        log("  dB " + dB + "  projection " + (projection)) ;
        log("  dB/projection " + (dB/projection)) ;
        //log("fixing additional angle " + (dB/predicted)) ;
        log("  fixing additional angle " + this.additionalAngle) ; 
        this.i++ ;
        log("\n") ;
    }
    
}

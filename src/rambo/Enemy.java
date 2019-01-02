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
public class Enemy {
    
    private double distance, velocity, angle, t0, enemyHeading, alpha, additionalAngle ;
    
    
    
    
    public void set(double distance, double velocity, double angle, double enemyHeading, double t0) {
        this.distance = distance ;
        this.velocity = velocity ;
        this.angle = angle ;
        this.t0 = t0 ;
        this.enemyHeading = enemyHeading ;
        this.alpha =  this.angle - this.enemyHeading ;
        log("enemy angle " + angle) ;
        log("enemy alpha " + alpha) ;
        log("enemy heading " + enemyHeading) ;
        log("enemy heading relative to alpha " + normalizeBearing(alpha) ) ;
    }
    
    public double getAdditionalAngle() {
        return this.additionalAngle ;
    }
    
    public <T> void log(T t) {
        System.out.println(t) ;
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
    
    private double timeForBullet(double bulletVelocity) {
        double timeForBullet = this.distance/bulletVelocity ;
        log("bullet velocity " + bulletVelocity) ;
        log("distance " + this.distance) ;
        log("enemy time for bullet " + timeForBullet) ;
        return timeForBullet ;
    }
    
    void finalize(double t1,double bulletVelocity) {
        double additionalTime = (t1 - this.t0) ;
        double dt = additionalTime + timeForBullet(bulletVelocity) ;
        double ds = dt*this.velocity ;
        log("enemy dt " + dt + " enemy ds " + ds) ;
        
        double dA = Math.cos(toRad(alpha))*ds ;
        double dB = Math.sin(toRad(alpha))*ds ;
                        
        log("enemy dA " + dA) ;
        log("enemy dB " + dB) ;
        //log(Math.pow(Math.pow(dA,2) + Math.pow(dB,2),0.5)) ;
        
        double dsp = this.distance + dA ;
        this.additionalAngle = toDeg(-Math.atan(dB/dsp)) ;
        log("fixing additional angle " + this.additionalAngle) ;

    }
    
}

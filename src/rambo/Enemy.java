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
    
    private double dA, dB ;
    
    
    public void set(double distance, double velocity, double angle, double enemyHeading, double t0) {
        this.distance = distance ;
        this.velocity = velocity ;
        this.angle = angle ;     //absolute 
        this.t0 = t0 ;
        this.enemyHeading = enemyHeading ;
        this.alpha =  this.angle - this.enemyHeading ;
        log("enemy velocity " + velocity) ;
        log("enemy angle " + normalizeBearing(angle)) ;
        log("enemy alpha " + normalizeBearing(alpha)) ;
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
    
    private double timeForBullet(double distance,double bulletVelocity) {
        double timeForBullet = distance/bulletVelocity ;
        //log("bullet velocity " + bulletVelocity) ;
        //log("distance " + distance) ;
        //log("enemy time for bullet " + timeForBullet) ;
        return timeForBullet ;
    }
    
            
    
    void fin(double bulletVelocity) {
        Solver solver = new Solver(this.distance, this.velocity, this.alpha, 0, 0, bulletVelocity) ;
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
        log("\nOLD-- i0 -- distance " + distance0) ;
        double dt = additionalTime + timeForBullet(distance0,bulletVelocity) ;
        double ds = dt*this.velocity ;
        //log("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        log("                            " + alpha + ", " + ds + ", " + distance0) ;
        double dA = Math.cos(toRad(alpha))*ds ;
        double dB = Math.sin(toRad(alpha))*ds ;
        log("  => enemy dA " + dA) ;
        log("  => enemy dB " + dB) ;
        double projection = this.distance + dA ;
        this.additionalAngle = toDeg(-Math.atan(dB/projection)) ;
        log("  dB " + dB + "  projection " + (projection)) ;
        log("  dB/projection " + (dB/projection)) ;
        log("  fixing additional angle " + this.additionalAngle) ;
        
        
        double distance1 = Math.pow(Math.pow(this.distance + dA,2) + Math.pow(dB,2),0.5) ;
        log("\n-- i1 -- distance " + distance1) ;
        dt = additionalTime + timeForBullet(distance1,bulletVelocity) ;
        ds = dt*this.velocity ;
        //log("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        log("                            " + alpha + ", " + ds + ", " + distance1) ;
        dA = Math.cos(toRad(alpha))*ds ;
        dB = Math.sin(toRad(alpha))*ds ;
        log("  => enemy dA " + dA) ;
        log("  => enemy dB " + dB) ;
        projection = this.distance + dA ;
        this.additionalAngle = toDeg(-Math.atan(dB/projection)) ;
        log("  dB " + dB + "  projection " + (projection)) ;
        log("  dB/projection " + (dB/projection)) ;
        log("  fixing additional angle " + this.additionalAngle) ;
        log("\n") ;
        
        
    }
    
    
    
}

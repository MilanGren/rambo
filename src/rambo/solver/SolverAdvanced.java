
package rambo.solver;

import java.util.List;
import rambo.Utils;


public class SolverAdvanced extends SolverAbstract {
    
    public List<Double> accelVec ;
    
    public SolverAdvanced(double distance, double velocity, List<Double> velVec, List<Double> accelVec, double alpha, double dA, double dB, double bulletVelocity) {
        super(distance, velocity, velVec, alpha, dA, dB, bulletVelocity);
        this.accelVec = accelVec ;
    }
    
    @Override
    public void solve() {
        distanceActual = Math.pow(Math.pow(this.distanceOriginal + dA,2) + Math.pow(dB,2),0.5) ;
        
        logSolver("\nNEW-- " + this.i + " -- distance " + this.distanceActual) ;
        double dt = timeForBullet(this.distanceActual,this.bulletVelocity) ;
        double dsOld = dt*velocity ;//this.velVec.get(velVec.size()-1) ; //kolik nepritel ujede za cas dt
      
        
        double v0 = velVec.get(velVec.size()-1) ; //zde je rozdil oprovi v0 v Enemy - zde predikuji
        double accel = accelVec.get(accelVec.size()-1) ;
        
        
        
        double vMax = 8 ;
        double vMin = 0 ;
        
        double dtM ;
        double ds ;
        
        if (Math.abs(accel) < 0.00001) {
            logSolver(" no speed change");
        } else if (accel*v0 < 0) {
            logSolver(" sloving down");
        } else {
            logSolver(" accelerating");
        }
        
        if (Math.abs(accel) < 0.00001) {
            logSolver("0 accel " + accel) ;
            dtM = 0 ;
            ds = v0*dt ;
        } else if (accel <= 0) {
            logSolver("1 accel " + accel) ;
            dtM = (v0 - vMin)/accel ;
            if (dt <= dtM) {
                ds = 0.5*accel*dt*dt + v0*dt ; 
            } else {
                ds = 0.5*accel*dtM*dtM + v0*dtM ;
                ds += vMin*(dt-dtM) ;
            }   
        } else { 
            logSolver("2 accel " + accel) ;
            dtM = (vMax - v0)/accel ;
            if (dt <= dtM) {
                ds = 0.5*accel*dt*dt + v0*dt ; 
            } else {
                ds = 0.5*accel*dtM*dtM + v0*dtM ;
                ds += vMin*(dt-dtM) ;
            }
        }
        
        logSolver("dtM " + dtM) ;
        logSolver("dt " + dt + " a " + Utils.round(accel,2) + " ds " + Utils.round(ds,1) + " dsOld " + Utils.round(dsOld,1)) ;
        logSolver("velocity " + velocity + " velVec last" + velVec.get(velVec.size()-1)) ;
        
        
// this.velocity nahradit nejakou stredni hodnotou pres budoucnost
// this.velocity * <velocity>
// 

        //log("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        logSolver("                                                   " + alpha + ", " + ds + ", " + distanceActual) ;
        dA = Math.cos(Utils.toRad(alpha))*ds ; 
        dB = Math.sin(Utils.toRad(alpha))*ds ;
        logSolver("  => dA " + dA) ;
        logSolver("  => dB " + dB) ;
        double projection = this.distanceOriginal + dA ; 
        
        this.additionalAngle = Utils.toDeg(-Math.atan(dB/projection)) ;
        
        
        logSolver("  dB " + dB + "  projection " + (projection)) ;
        logSolver("  dB/projection " + (dB/projection)) ;
        //log("fixing additional angle " + (dB/predicted)) ;
        logSolver("  fixing additional angle " + this.additionalAngle) ; 
        this.i++ ;
        logSolver("\n") ;
    }


}

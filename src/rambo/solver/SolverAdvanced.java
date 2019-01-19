
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
        double dtMax = (vMax - v0)/accel ;
        
        
        double ds ;
        if (dt <= dtMax) {
            ds = 0.5*accel*dt*dt + v0*dt ;
        } else {
            ds = 0.5*accel*dtMax*dtMax + v0*dtMax ;
            ds += vMax*(dt-dtMax) ;
        }
        
        
        
        
        
        
        

        logSolver("dt " + dt + " a " + accel + " ds " + ds + " dsOld " + dsOld) ;
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

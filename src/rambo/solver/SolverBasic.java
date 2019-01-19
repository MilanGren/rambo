

package rambo.solver;

import java.util.List;
import rambo.Utils;


public class SolverBasic extends SolverAbstract {

    public SolverBasic(double distance, double velocity, List<Double> velVec, List<Double> accelVec, double alpha, double dA, double dB, double bulletVelocity) {
        super(distance, velocity, velVec, alpha, dA, dB, bulletVelocity);
    }
    
    @Override
    public void solve() {

        // vzdycky se aktualizuje uhel a tim i vzdalenost
        
        distanceActual = Math.pow(Math.pow(this.distanceOriginal + dA,2) + Math.pow(dB,2),0.5) ;
        
        logSolver("\nNEW-- " + this.i + " -- distance " + this.distanceActual) ;
        double dt = timeForBullet(this.distanceActual,this.bulletVelocity) ;
        double ds = dt*velocity ;//this.velVec.get(velVec.size()-1) ; //kolik nepritel ujede za cas dt

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

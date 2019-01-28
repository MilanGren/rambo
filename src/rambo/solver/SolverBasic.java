

package rambo.solver;

import java.util.List;
import rambo.Enemy;
import rambo.Utils;


public class SolverBasic extends SolverAbstract {

    public SolverBasic(Enemy enemy, double dA, double dB, double bulletVelocity) {
        super(enemy, dA, dB, bulletVelocity);
    }

    @Override
    public double solve() {

        // vzdycky se aktualizuje uhel a tim i vzdalenost
        
        distanceActual = Math.pow(Math.pow(this.distanceOriginal + dA,2) + Math.pow(dB,2),0.5) ;
        
        logSolver("\nNEW-- " + this.i + " -- distance " + this.distanceActual) ;
        double dt = timeForBullet(this.distanceActual,this.bulletVelocity) ;
        double ds = dt*enemy.velocity ;//this.velVec.get(velVec.size()-1) ; //kolik nepritel ujede za cas dt

        logSolver("                                                   " + enemy.alpha + ", " + ds + ", " + distanceActual) ;
        dA = Math.cos(Utils.toRad(enemy.alpha))*ds ; 
        dB = Math.sin(Utils.toRad(enemy.alpha))*ds ;
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
        
        return dt ;
    }


}
